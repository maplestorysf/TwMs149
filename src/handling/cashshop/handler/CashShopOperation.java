package handling.cashshop.handler;

import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;

import constants.GameConstants;
import client.MapleClient;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.inventory.MapleInventoryType;
import client.inventory.MapleRing;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.Item;
import client.inventory.MapleInventory;
import constants.ServerConstants;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.World;

import java.util.List;

import server.CashItemFactory;
import server.CashItem;
import server.CashModItem;
import server.CashShop;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.RandomRewards;
import tools.FileoutputUtil;
import tools.packet.CField;
import tools.packet.MTSCSPacket;
import tools.Triple;
import tools.data.LittleEndianAccessor;

public class CashShopOperation {

    public static void LeaveCS(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null || c == null) {
            return;
        }
        int loginStatus = c.getLoginState();
        if (loginStatus != MapleClient.LOGIN_CS_LOGGEDIN) {
            c.getSession().close();
            return;
        }
        World.ChannelChange_Data(c, chr);
        ChannelServer toch = ChannelServer.getInstance(c.getChannel());
        if (toch == null) {
            c.getSession().close();
            return;
        }
        CashShopServer.getPlayerStorage().deregisterPlayer(chr);
        c.updateLoginState(MapleClient.CASH_SHOP_TRANSITION_LEAVE, c.getSessionIPAddress());

        String s = c.getSessionIPAddress();
        LoginServer.addIPAuth(s.substring(s.indexOf('/') + 1, s.length()));
        c.sendPacket(CField.getChannelChange(c, ChannelServer.getInstance(c.getChannel()).getSocket().split(":")[0], Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getSocket().split(":")[1])));
        //     chr.saveToDB(false, true);
        c.disconnect(true, true);

        c.setPlayer(null);
        c.setReceiving(false);
    }

    public static void EnterCS(final MapleCharacter chr, final MapleClient c) {
        if (chr == null) {
            c.getSession().close();
            return;
        }
        c.setPlayer(chr);
        c.setAccID(chr.getAccountID());
        c.loadAccountData(chr.getAccountID());
        if (!c.CheckIPAddress()) { // Remote hack
            c.getSession().close();
            return;
        }
        final int state = c.getLoginState();

        if (state != MapleClient.CASH_SHOP_TRANSITION) {
            c.setPlayer(null);
            c.getSession().close();
            c.updateLoginState(MapleClient.LOGIN_NOTLOGGEDIN, c.getSessionIPAddress());
            return;
        }
        /*else {
            if (World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()))) {
                c.setPlayer(null);
                c.getSession().close();
                c.updateLoginState(MapleClient.LOGIN_NOTLOGGEDIN, c.getSessionIPAddress());
                return;
            }
        }*/
        c.updateLoginState(MapleClient.LOGIN_CS_LOGGEDIN, c.getSessionIPAddress());
        CashShopServer.getPlayerStorage().registerPlayer(chr);
        c.sendPacket(MTSCSPacket.warpCS(c));
        c.sendPacket(MTSCSPacket.getCSInfo(c));
        c.sendPacket(MTSCSPacket.getCSAccountName(c));
        CSUpdate(c);
    }

    public static void CSUpdate(final MapleClient c) {
        c.sendPacket(MTSCSPacket.getCSGifts(c));
        doCSPackets(c);
        c.sendPacket(MTSCSPacket.sendWishList(c.getPlayer(), false));
    }

    public static void CouponCode(final String code, final MapleClient c) {
        if (code.length() <= 0) {
            return;
        }
        Triple<Boolean, Integer, Integer> info = null;
        try {
            info = MapleCharacterUtil.getNXCodeInfo(code);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (info != null && info.left) {
            int type = info.mid, item = info.right;
            try {
                MapleCharacterUtil.setNXCodeUsed(c.getPlayer().getName(), code);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            /*
             * Explanation of type!
             * Basically, this makes coupon codes do
             * different things!
             *
             * Type 1: A-Cash,
             * Type 2: Maple Points
             * Type 3: Item.. use SN
             * Type 4: Mesos
             */
            Map<Integer, Item> itemz = new HashMap<>();
            int maplePoints = 0, mesos = 0;
            switch (type) {
                case 0:
                case 1:
                    c.getPlayer().modifyCSPoints(type, item, false);
                    maplePoints = item;
                    break;
                case 3:
                    CashItem itez = CashItemFactory.getInstance().getItem(item);
                    if (itez == null) {
                        c.sendPacket(MTSCSPacket.sendCSFail(0));
                        return;
                    }
                    byte slot = MapleInventoryManipulator.addId(c, itez.getId(), (short) 1, "", "Cash shop: coupon code" + " on " + FileoutputUtil.CurrentReadable_Date());
                    if (slot <= -1) {
                        c.sendPacket(MTSCSPacket.sendCSFail(0));
                        return;
                    } else {
                        itemz.put(item, c.getPlayer().getInventory(GameConstants.getInventoryType(item)).getItem(slot));
                    }
                    break;
                case 4:
                    c.getPlayer().gainMeso(item, false);
                    mesos = item;
                    break;
            }
            c.sendPacket(MTSCSPacket.showCouponRedeemedItem(itemz, mesos, maplePoints, c));
        } else {
            //c.sendPacket(MTSCSPacket.sendCSFail(info == null ? 0xA7 : 0xA5)); //A1, 9F
            c.getPlayer().dropMessage(1, "使用優待券失敗。");
        }
    }

    public static final void BuyCashItem(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final int action = slea.readByte();
        CashItemFactory cif = CashItemFactory.getInstance();
        if (c.getLoginState() != MapleClient.LOGIN_CS_LOGGEDIN) {
            FileoutputUtil.logToFile("日誌/外掛/非商城狀態操作商城.txt", FileoutputUtil.CurrentReadable_Time() + " 玩家: " + chr.getName() + " 動作: " + action);
            return;
        }
        switch (action) {
            case 0:
                slea.skip(2);
                CouponCode(slea.readMapleAsciiString(), c);
                break;
            case 3: {
                final int toCharge = slea.readByte();
                CashModItem item = cif.getModItem(slea.readInt());
                if (item != null && chr.getCSPoints(toCharge) >= item.getPrice()) {
                    if (!item.genderEquals(c.getPlayer().getGender())) {
                        c.sendPacket(MTSCSPacket.sendCSFail(0xA6));
                        doCSPackets(c);
                        return;
                    } else if (c.getPlayer().getCashInventory().getItemsSize() >= 100) {
                        c.sendPacket(MTSCSPacket.sendCSFail(0xB1));
                        doCSPackets(c);
                        return;
                    }
                    for (int i : GameConstants.cashBlock) {
                        if (item.getId() == i) {
                            c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item.getId()));
                            doCSPackets(c);
                            return;
                        }
                    }
                    chr.modifyCSPoints(toCharge, -item.getPrice(), false);
                    Item itemz = chr.getCashInventory().toItem(item);
                    if (itemz != null && itemz.getUniqueId() > 0 && itemz.getItemId() == item.getId() && itemz.getQuantity() == item.getCount()) {
                        chr.getCashInventory().addToInventory(itemz);
                        //c.sendPacket(MTSCSPacket.confirmToCSInventory(itemz, c.getAccID(), item.getSN()));
                        c.sendPacket(MTSCSPacket.showBoughtCSItem(itemz, item.getSN(), c.getAccID()));
                        if (ServerConstants.log_csbuy) {
                            FileoutputUtil.logToFile("logs/data/商城購買.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 使用了" + (toCharge == 0 ? "樂豆點" : "楓葉點數") + item.getPrice() + "點 來購買" + item.getId() + "x" + item.getCount());
                        }
                    } else {
                        c.sendPacket(MTSCSPacket.sendCSFail(0));
                    }
                } else {
                    c.sendPacket(MTSCSPacket.sendCSFail(0));
                }
                break;
            }
            case 4:
            case 34: { //gift, package
                slea.readMapleAsciiString(); // pic
                final CashModItem item = cif.getModItem(slea.readInt());
                if (action == 4) {
                    slea.skip(1);
                }
                String partnerName = slea.readMapleAsciiString();
                String msg = slea.readMapleAsciiString();
                if (item == null || c.getPlayer().getCSPoints(0) < item.getPrice() || msg.getBytes().length > 74 || msg.getBytes().length < 1) { //dont want packet editors gifting random stuff =P
                    c.sendPacket(MTSCSPacket.sendCSFail(0));
                    doCSPackets(c);
                    return;
                }

                Triple<Integer, Integer, Integer> info = MapleCharacterUtil.getInfoByName(partnerName, c.getPlayer().getWorld());
                if (info == null || info.getLeft() <= 0 || info.getLeft() == c.getPlayer().getId() || info.getMid() == c.getAccID()) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0xA2)); //9E v75
                    doCSPackets(c);
                    return;
                } else if (!item.genderEquals(info.getRight())) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0xA3));
                    doCSPackets(c);
                    return;
                } else if (MapleCharacterUtil.getGiftAmount(info.getLeft()) >= ServerConstants.CashShopGiftsCount) {
                    c.getPlayer().dropMessage(1, ServerConstants.GiftsMaxMessage);
                    doCSPackets(c);
                    return;
                } else {
                    for (int i : GameConstants.cashBlock) {
                        if (item.getId() == i) {
                            c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item.getId()));
                            doCSPackets(c);
                            return;
                        }
                    }
                    if (ServerConstants.log_csbuy) {
                        FileoutputUtil.logToFile("logs/data/商城送禮.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 使用了樂豆點" + item.getPrice() + "點 來購買" + item.getId() + "x" + item.getCount() + "送禮給: " + partnerName + " 送禮訊息: " + msg + "\r\n");
                    }
                    c.getPlayer().getCashInventory().gift(info.getLeft(), c.getPlayer().getName(), msg, item.getSN(), MapleInventoryIdentifier.getInstance());
                    c.getPlayer().modifyCSPoints(0, -item.getPrice(), false);
                    c.sendPacket(MTSCSPacket.sendGift(item.getPrice(), item.getId(), item.getCount(), partnerName, action == 34));
                }
                break;
            }
            case 5: // Wishlist
                chr.clearWishlist();
                if (slea.available() < 40) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0));
                    doCSPackets(c);
                    return;
                }
                int[] wishlist = new int[10];
                for (int i = 0; i < 10; i++) {
                    wishlist[i] = slea.readInt();
                }
                chr.setWishlist(wishlist);
                c.sendPacket(MTSCSPacket.sendWishList(chr, true));
                break;
            case 6: { // 擴充欄位
                final int toCharge = slea.readByte();
                final boolean coupon = slea.readByte() > 0;
                if (coupon) {
                    int id = slea.readInt();
                    final MapleInventoryType type = getInventoryType(id);
                    int slot = 4;
                    switch (id) {
                        case 50300083:
                        case 50300084:
                        case 50300085:
                            slot = 4;
                            break;
                        case 50300137:
                        case 50300138:
                        case 50300139:
                            slot = 8;
                            break;
                    }
                    if (chr.getCSPoints(toCharge) >= 100 && chr.getInventory(type).getSlotLimit() < (slot == 4 ? 93 : 89)) {
                        chr.modifyCSPoints(toCharge, -100, false);
                        chr.getInventory(type).addSlot((byte) slot);
                        chr.dropMessage(1, "欄位已擴充至 " + chr.getInventory(type).getSlotLimit() + " 格。");
                    } else {
                        //c.sendPacket(MTSCSPacket.sendCSFail(0xA4));
                        chr.dropMessage(1, "目前欄位空間：" + chr.getInventory(type).getSlotLimit() + "格。\r\n欄位空間上限：96格。\r\n無法購買" + slot + "格擴充券。");
                    }
                } else {
                    final MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());
                    if (chr.getCSPoints(toCharge) >= 100 && chr.getInventory(type).getSlotLimit() < 93) {
                        chr.modifyCSPoints(toCharge, -100, false);
                        chr.getInventory(type).addSlot((byte) 4);
                        chr.dropMessage(1, "欄位已擴充至 " + chr.getInventory(type).getSlotLimit() + " 格。");
                    } else {
                        //c.sendPacket(MTSCSPacket.sendCSFail(0xA4));
                        chr.dropMessage(1, "目前欄位空間：" + chr.getInventory(type).getSlotLimit() + "格。\r\n欄位空間上限：96格。\r\n無法購買4格擴充券。");
                    }
                }
                break;
            }
            case 7: { // 擴充倉庫欄位
                final int toCharge = slea.readByte();
                final int coupon = slea.readByte() > 0 ? 2 : 1;
                if (chr.getCSPoints(toCharge) >= 100 * coupon && chr.getStorage().getSlots() < (49 - (4 * coupon))) {
                    chr.modifyCSPoints(toCharge, -100 * coupon, false);
                    chr.getStorage().increaseSlots((byte) (4 * coupon));
                    chr.getStorage().saveToDB();
                    chr.dropMessage(1, "倉庫已擴充至 " + chr.getStorage().getSlots() + " 格。");
                } else {
                    //c.sendPacket(MTSCSPacket.sendCSFail(0xA4));
                    chr.dropMessage(1, "倉庫欄位空間已達到上限，無法再擴充。");
                }
                break;
            }
            case 8: { // 擴充角色欄位
                final int toCharge = slea.readByte();
                CashModItem item = cif.getModItem(slea.readInt());
                int slots = c.getCharacterSlots();
                if (item == null || c.getPlayer().getCSPoints(toCharge) < item.getPrice() || slots > 15 || item.getId() != 5430000) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0));
                    doCSPackets(c);
                    return;
                }
                if (c.gainCharacterSlot()) {
                    c.getPlayer().modifyCSPoints(toCharge, -item.getPrice(), false);
                    chr.dropMessage(1, "角色欄位已擴充至 " + (slots + 1) + " 格。");
                } else {
                    c.sendPacket(MTSCSPacket.sendCSFail(0));
                }
                /*} else if (action == 9) { //...9 = pendant slot expansion
                    slea.readByte();
                    final int sn = slea.readInt();
                    CashItemInfo item = CashItemFactory.getInstance().getItem(sn);
                    int slots = c.getCharacterSlots();
                    if (item == null || c.getPlayer().getCSPoints(1) < item.getPrice() || item.getId() / 10000 != 555) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0));
                    doCSPackets(c);
                    return;
                    }
                    MapleQuestStatus marr = c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
                    if (marr != null && marr.getCustomData() != null && Long.parseLong(marr.getCustomData()) >= System.currentTimeMillis()) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0));
                    } else {
                    c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT)).setCustomData(String.valueOf(System.currentTimeMillis() + ((long)item.getPeriod() * 24 * 60 * 60000)));
                    c.getPlayer().modifyCSPoints(1, -item.getPrice(), false);
                    chr.dropMessage(1, "Additional pendant slot gained.");
                    }*/
                break;
            }
            case 14: { // 購物商城 -> 道具欄位
                //uniqueid, 00 01 01 00, type->position(short)
                Item item = c.getPlayer().getCashInventory().findByCashId((int) slea.readLong());
                if (item != null && item.getQuantity() > 0 && MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                    Item item_ = item.copy();
                    short pos = MapleInventoryManipulator.addbyItem(c, item_, true);
                    if (pos >= 0) {
                        if (item_.getPet() != null) {
                            item_.getPet().setInventoryPosition(pos);
                            c.getPlayer().addPet(item_.getPet());
                        }
                        c.getPlayer().getCashInventory().removeFromInventory(item);
                        c.sendPacket(MTSCSPacket.confirmFromCSInventory(item_, pos));
                    } else {
                        c.sendPacket(MTSCSPacket.sendCSFail(0xB1));
                    }
                } else {
                    c.sendPacket(MTSCSPacket.sendCSFail(0xB1));
                }
                break;
            }
            case 15: // 道具欄位 -> 購物商城
                CashShop cs = chr.getCashInventory();
                int cashId = (int) slea.readLong();
                byte type = slea.readByte();
                MapleInventory mi = chr.getInventory(MapleInventoryType.getByType(type));
                Item item1 = mi.findByUniqueId(cashId);
                if (item1 == null) {
                    c.sendPacket(MTSCSPacket.showNXMapleTokens(chr));
                    return;
                }
                if (cs.getItemsSize() < 100) {
                    int sn = CashItemFactory.getInstance().getItemSN(item1.getItemId());
                    cs.addToInventory(item1);
                    mi.removeSlot(item1.getPosition());
                    c.sendPacket(MTSCSPacket.confirmToCSInventory(item1, c.getAccID(), sn));
                } else {
                    chr.dropMessage(1, "移動失敗。");
                }
                break;
            case 29: { // 販售道具
                slea.readMapleAsciiString();
                chr.dropMessage(1, "目前尚未開放賣回功能。");
                break;
            }
            case 32:
            case 38: { //38 = friendship, 32 = crush
                //c.sendPacket(MTSCSPacket.sendCSFail(0));
                slea.readMapleAsciiString(); // as13
                final int toCharge = 0;
                final CashModItem item = cif.getModItem(slea.readInt());
                final String partnerName = slea.readMapleAsciiString();
                final String msg = slea.readMapleAsciiString();

                if (item == null || !GameConstants.isEffectRing(item.getId()) || c.getPlayer().getCSPoints(toCharge) < item.getPrice() || msg.getBytes().length > 74 || msg.getBytes().length < 1) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0));
                    doCSPackets(c);
                    return;
                } else if (!item.genderEquals(c.getPlayer().getGender())) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0xA6));
                    doCSPackets(c);
                    return;
                } else if (c.getPlayer().getCashInventory().getItemsSize() >= 100) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0xB1));
                    doCSPackets(c);
                    return;
                }
                for (int i : GameConstants.cashBlock) { //just incase hacker
                    if (item.getId() == i) {
                        c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item.getId()));
                        doCSPackets(c);
                        return;
                    }
                }
                Triple<Integer, Integer, Integer> info = MapleCharacterUtil.getInfoByName(partnerName, c.getPlayer().getWorld());
                if (info == null || info.getLeft() <= 0 || info.getLeft() == c.getPlayer().getId()) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0xB4)); //9E v75
                    doCSPackets(c);
                    return;
                } else {
                    if (info.getRight() == c.getPlayer().getGender() && action == 30) {
                        c.sendPacket(MTSCSPacket.sendCSFail(0xA1)); //9B v75
                        doCSPackets(c);
                        return;
                    } else if (MapleCharacterUtil.getGiftAmount(info.getLeft()) >= ServerConstants.CashShopGiftsCount) {
                        c.getPlayer().dropMessage(1, ServerConstants.GiftsMaxMessage);
                        doCSPackets(c);
                        return;
                    }

                    int err = MapleRing.createRing(item.getId(), c.getPlayer(), partnerName, msg, info.getLeft(), item.getSN());

                    if (err != 1) {
                        c.sendPacket(MTSCSPacket.sendCSFail(0)); //9E v75
                        doCSPackets(c);
                        return;
                    }
                    if (ServerConstants.log_csbuy) {
                        FileoutputUtil.logToFile("logs/data/商城送禮.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 使用了樂豆點" + item.getPrice() + "點 來購買" + item.getId() + "x" + item.getCount() + "送禮給: " + partnerName + " 送禮訊息: " + msg + "\r\n");
                    }
                    c.getPlayer().modifyCSPoints(toCharge, -item.getPrice(), false);
                }
                break;
            }
            case 33: {
                final int toCharge = slea.readByte();
                final CashModItem item = cif.getModItem(slea.readInt());
                List<Integer> ccc = null;
                if (item != null) {
                    ccc = CashItemFactory.getInstance().getPackageItems(item.getId());
                }
                if (item == null || ccc == null || c.getPlayer().getCSPoints(toCharge) < item.getPrice()) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0));
                    doCSPackets(c);
                    return;
                } else if (!item.genderEquals(c.getPlayer().getGender())) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0xA6));
                    doCSPackets(c);
                    return;
                } else if (c.getPlayer().getCashInventory().getItemsSize() >= (100 - ccc.size())) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0xB1));
                    doCSPackets(c);
                    return;
                }
                for (int iz : GameConstants.cashBlock) {
                    if (item.getId() == iz) {
                        c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item.getId()));
                        doCSPackets(c);
                        return;
                    }
                }
                Map<Integer, Item> ccz = new HashMap<>();
                for (int i : ccc) {
                    final CashItem cii = CashItemFactory.getInstance().getSimpleItem(i);
                    if (cii == null) {
                        continue;
                    }
                    Item itemz = c.getPlayer().getCashInventory().toItem(cii);
                    if (itemz == null || itemz.getUniqueId() <= 0) {
                        continue;
                    }
                    for (int iz : GameConstants.cashBlock) {
                        if (itemz.getItemId() == iz) {
                            continue;
                        }
                    }
                    ccz.put(i, itemz);
                    c.getPlayer().getCashInventory().addToInventory(itemz);
                }
                if (ServerConstants.log_csbuy) {
                    FileoutputUtil.logToFile("logs/data/商城購買.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 使用了" + (toCharge == 0 ? "樂豆點" : "楓葉點數") + item.getPrice() + "點 來購買套裝" + item.getId() + "x" + item.getCount());
                }
                chr.modifyCSPoints(toCharge, -item.getPrice(), false);
                c.sendPacket(MTSCSPacket.showBoughtCSPackage(ccz, c.getAccID()));
                break;
            }
            case 35: {
                final CashModItem item = cif.getModItem(slea.readInt());
                if (item == null || !MapleItemInformationProvider.getInstance().isQuestItem(item.getId())) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0));
                    doCSPackets(c);
                    return;
                } else if (c.getPlayer().getMeso() < item.getPrice()) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0xB8));
                    doCSPackets(c);
                    return;
                } else if (c.getPlayer().getInventory(GameConstants.getInventoryType(item.getId())).getNextFreeSlot() < 0) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0xB1));
                    doCSPackets(c);
                    return;
                }
                for (int iz : GameConstants.cashBlock) {
                    if (item.getId() == iz) {
                        c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item.getId()));
                        doCSPackets(c);
                        return;
                    }
                }
                byte pos = MapleInventoryManipulator.addId(c, item.getId(), (short) item.getCount(), null, "Cash shop: quest item" + " on " + FileoutputUtil.CurrentReadable_Date());
                if (pos < 0) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0xB1));
                    doCSPackets(c);
                    return;
                }
                chr.gainMeso(-item.getPrice(), false);
                c.sendPacket(MTSCSPacket.showBoughtCSQuestItem(item.getPrice(), (short) item.getCount(), pos, item.getId()));
                break;
            }
            case 46:
                c.sendPacket(MTSCSPacket.updatePurchaseRecord(slea.readInt()));
                break;
            case 61:
                break;
            case 91:
                // Open random box.
                final int uniqueid = (int) slea.readLong();
                //c.sendPacket(MTSCSPacket.sendRandomBox(uniqueid, new Item(1302000, (short) 1, (short) 1, (short) 0, 10), (short) 0));
                break;
            default:
                System.out.println("New Action: " + action + " Remaining: " + slea.toString());
                c.sendPacket(MTSCSPacket.sendCSFail(0));
                break;
        }
        doCSPackets(c);
    }

    private static MapleInventoryType getInventoryType(final int id) {
        switch (id) {
            case 50300083:
            case 50300137:
                return MapleInventoryType.EQUIP;
            case 50300084:
            case 50300138:
                return MapleInventoryType.USE;
            case 50200197:
                return MapleInventoryType.SETUP;
            case 50300085:
            case 50300139:
                return MapleInventoryType.ETC;
            default:
                return MapleInventoryType.UNDEFINED;
        }
    }

    public static final void doCSPackets(MapleClient c) {
        c.sendPacket(MTSCSPacket.getCSInventory(c));
        c.sendPacket(MTSCSPacket.showNXMapleTokens(c.getPlayer()));
        c.sendPacket(MTSCSPacket.enableCSUse());
        c.getPlayer().getCashInventory().checkExpire(c);
    }

    public static final void sendGift(final LittleEndianAccessor slea, final MapleClient c) {
        CashItemFactory cif = CashItemFactory.getInstance();
        String secondPassowrd = slea.readMapleAsciiString();
        final CashModItem item = cif.getModItem(slea.readInt());
        String partnerName = slea.readMapleAsciiString();
        String msg = slea.readMapleAsciiString();
        MapleCharacter ChrName = MapleCharacter.getCharacterByName(partnerName);
        if (item == null || c.getPlayer().getCSPoints(0) < item.getPrice() || msg.getBytes().length > 74 || msg.getBytes().length < 1) { //dont want packet editors gifting random stuff =P
            c.sendPacket(MTSCSPacket.sendCSFail(0));
            doCSPackets(c);
            return;
        }
        if (secondPassowrd != null) {
            if (!c.CheckSecondPassword(secondPassowrd)) {
                c.getPlayer().dropMessage(6, "第二組密碼錯誤。");
                doCSPackets(c);
                return;
            }
        } else {
            c.getPlayer().dropMessage(6, "請輸入第二組密碼。");
            doCSPackets(c);
            return;
        }
        Triple<Integer, Integer, Integer> info = MapleCharacterUtil.getInfoByName(partnerName, c.getPlayer().getWorld());
        if (info == null || info.getLeft() <= 0 || info.getLeft() == c.getPlayer().getId() || info.getMid() == c.getAccID()) {
            c.sendPacket(MTSCSPacket.sendCSFail(0xA2)); //9E v75
            doCSPackets(c);
            return;
        } else if (!item.genderEquals(info.getRight())) {
            c.sendPacket(MTSCSPacket.sendCSFail(0xA3));
            doCSPackets(c);
            return;
        } else if (ChrName.CashShopGiftCount(ChrName.getId()) > ServerConstants.CashShopGiftsCount) {
            c.getPlayer().dropMessage(1, ServerConstants.GiftsMaxMessage);
            doCSPackets(c);
            return;
        } else {
            for (int i : GameConstants.cashBlock) {
                if (item.getId() == i) {
                    c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item.getId()));
                    doCSPackets(c);
                    return;
                }
            }
            if (ServerConstants.log_csbuy) {
                FileoutputUtil.logToFile("logs/data/商城送禮.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 使用了樂豆點" + item.getPrice() + "點 來購買" + item.getId() + "x" + item.getCount() + "送禮給: " + partnerName + " 送禮訊息: " + msg + "\r\n");
            }
            c.getPlayer().getCashInventory().gift(info.getLeft(), c.getPlayer().getName(), msg, item.getSN(), MapleInventoryIdentifier.getInstance());
            c.getPlayer().modifyCSPoints(0, -item.getPrice(), false);
            c.sendPacket(MTSCSPacket.sendGift(item.getPrice(), item.getId(), item.getCount(), partnerName, true));
            doCSPackets(c);
        }
    }

    public static final void XmasSurprise(final LittleEndianAccessor slea, final MapleClient c) {
        int cashId = (int) slea.readLong();
        Item item = c.getPlayer().getCashInventory().findByCashId(cashId);
        if (item.getItemId() != 5222000 && item.getItemId() != 5222006) {
            c.getPlayer().dropMessage(1, "目前無法使用，請按 Esc 鍵 離開介面。");
            return;
        }
        if (item != null && item.getQuantity() > 0 && MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
            int rewardItemId = 0;
            switch (item.getItemId()) {
                case 5222000:
                    rewardItemId = RandomRewards.getXmasReward();
                    break;
                case 5222006:
                    rewardItemId = RandomRewards.getSurpriseStyleBoxReward();
                    break;
            }
            final CashItem rewardItem = CashItemFactory.getInstance().getItem(rewardItemId);
            if (rewardItem == null) {
                c.sendPacket(MTSCSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            for (int i : GameConstants.cashBlock) {
                if (rewardItem.getId() == i) {
                    c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(rewardItem.getId()));
                    doCSPackets(c);
                    return;
                }
            }
            Item itemz = c.getPlayer().getCashInventory().toItem(rewardItem);
            if (itemz != null) {
                if (c.getPlayer().getCashInventory().getItemsSize() >= 100) {
                    c.sendPacket(MTSCSPacket.showXmasSurprise(true, cashId, itemz, c.getAccID()));
                    doCSPackets(c);
                    return;
                }
                c.getPlayer().getCashInventory().addToInventory(itemz);
                c.sendPacket(MTSCSPacket.showXmasSurprise(false, cashId, itemz, c.getAccID()));
                if (item.getQuantity() <= 1) {
                    c.getPlayer().getCashInventory().removeFromInventory(item);
                } else {
                    item.setQuantity((short) (item.getQuantity() - 1));
                    doCSPackets(c);
                }
            } else {
                c.sendPacket(MTSCSPacket.sendCSFail(0));
            }
        }
    }
}