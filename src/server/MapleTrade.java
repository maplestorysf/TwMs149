package server;

import java.util.LinkedList;
import java.util.List;
import client.inventory.Item;
import client.inventory.ItemFlag;
import constants.GameConstants;
import client.MapleCharacter;
import client.MapleClient;
import client.inventory.MapleInventoryType;
import constants.ServerConstants;
import handling.channel.ChannelServer;
import handling.world.World;
import java.lang.ref.WeakReference;
import tools.FileoutputUtil;
import tools.packet.CField.InteractionPacket;
import tools.packet.CWvsContext;
import tools.packet.PlayerShopPacket;

public class MapleTrade {

    private final List<Item> items = new LinkedList<>();
    private final WeakReference<MapleCharacter> chr;
    private final byte tradingslot;
    private MapleTrade partner = null;
    private List<Item> exchangeItems;
    private int meso = 0, exchangeMeso = 0;
    private boolean locked = false, inTrade = false;
    private boolean canceling = false, completing = false;

    public MapleTrade(final byte tradingslot, final MapleCharacter chr) {
        this.tradingslot = tradingslot;
        this.chr = new WeakReference<>(chr);
    }

    public static boolean isAbonormalTradeStatus(MapleTrade local, MapleTrade partner) {
        if (local != null && partner != null) {
            return local.isCanceling() || partner.isCanceling() || local.isCompleting() || partner.isCompleting();
        } else if (local != null && partner == null) {
            return local.isCanceling() || local.isCompleting();
        } else if (local == null && partner != null) {
            return partner.isCanceling() || partner.isCompleting();
        } else if (local == null && partner == null) {
            return true;
        }
        return true;
    }

    public boolean isCompleting() {
        return completing;
    }

    public void setCompleting(boolean set) {
        canceling = set;
    }

    public boolean isCanceling() {
        return canceling;
    }

    public void setCanceling(boolean set) {
        canceling = set;
    }

    public final static void completeTrade(final MapleCharacter c) {
        final MapleTrade local = c.getTrade();
        final MapleTrade partner = local.getPartner();

        if (partner == null || local.locked) {
            return;
        }
        if (!isAbonormalTradeStatus(local, partner)) {
            if (c.canTrade() && partner.getChr().canTrade()) {
                local.locked = true; // Locking the trade
                partner.getChr().getClient().sendPacket(InteractionPacket.getTradeConfirmation());

                partner.exchangeItems = new LinkedList<>(local.items); // Copy this to partner's trade since it's alreadt accepted
                partner.exchangeMeso = local.meso; // Copy this to partner's trade since it's alreadt accepted

                if (partner.isLocked()) { // Both locked
                    int lz = local.check(), lz2 = partner.check();

                    if (lz == 0 && lz2 == 0) {
                        local.setCompleting(true);
                        partner.setCompleting(true);
                        local.CompleteTrade();
                        partner.CompleteTrade();
                    } else {
                        local.setCanceling(true);
                        partner.setCanceling(true);
                        partner.cancel(partner.getChr().getClient(), partner.getChr(), lz == 0 ? lz2 : lz);
                        local.cancel(c.getClient(), c, lz == 0 ? lz2 : lz);
                    }
                    local.getChr().addTradeMsg(local.getChr().getName(), " ???????????? ???" + partner.getChr().getName() + "(" + partner.getChr().getClient().getSessionIPAddress() + ")????????? ??????: " + ((lz + lz2 == 0) ? "??????" : "??????") + "\r\n");
                    local.getChr().addEmptyTradeMsg("========================================\r\n");
                    partner.getChr().addTradeMsg(partner.getChr().getName(), " ???????????? ???" + local.getChr().getName() + "(" + local.getChr().getClient().getSessionIPAddress() + ")????????? ??????: " + ((lz + lz2 == 0) ? "??????" : "??????") + "\r\n");
                    partner.getChr().addEmptyTradeMsg("========================================\r\n");
                    local.getChr().endTradeMsg(local.getChr().getName());
                    partner.getChr().endTradeMsg(partner.getChr().getName());
                    partner.getChr().setTrade(null);
                    c.setTrade(null);
                    local.setCanceling(false);
                    partner.setCanceling(false);
                    local.setCompleting(false);
                    partner.setCompleting(false);
                    if (local.getChr().getClient().getAccID() == partner.getChr().getClient().getAccID()) {
                        local.getChr().ban("??????????????? - ?????????????????????", true, true, false);
                        partner.getChr().ban("??????????????? - ?????????????????????", true, true, false);
                        World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "[????????????]" + local.getChr().getName() + " ?????????????????????????????????????????????"));
                        World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "[????????????]" + partner.getChr().getName() + " ?????????????????????????????????????????????"));
                        World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, "[GM??????]" + partner.getChr().getName() + "???" + local.getChr().getName() + " ???????????????????????????????????????"));
                        FileoutputUtil.logToFile("logs/hack/ban/????????????.txt", "??????: " + FileoutputUtil.NowTime() + " IP: " + local.getChr().getClient().getSessionIPAddress() + " " + local.getChr().getName() + " ??? " + partner.getChr().getName() + " ???????????????????????????????????????\r\n");
                        local.getChr().getClient().getSession().close();
                        partner.getChr().getClient().getSession().close();
                        return;
                    }
                }
            } else {
                //  FileoutputUtil.logToFile("logs/hack/????????????.txt", "??????: " + FileoutputUtil.NowTime() + " ??????[" + local.getChr().getName() + "](??????" + local.getChr().getId() + ")<??????" + local.getChr().getAccountID() + "> ??? [" + partner.getChr().getName() + "](??????" + partner.getChr().getId() + ")<??????" + partner.getChr().getAccountID() + "> ????????????????????? ??????:" + local.getChr().getMapId() + "\r\n ");
            }
        }
    }

    public static final void cancelTrade(final MapleTrade Localtrade, final MapleClient c, final MapleCharacter chr) {
        final MapleTrade partner = Localtrade.getPartner();

        if (!isAbonormalTradeStatus(Localtrade, partner)) {
            Localtrade.setCanceling(true);
            if (partner != null) {
                partner.setCanceling(true);
            }
            Localtrade.cancel(c, chr);

            if (partner != null && partner.getChr() != null) {
                Localtrade.getChr().addTradeMsg(Localtrade.getChr().getName(), " ???????????? ???" + partner.getChr().getName() + "(" + partner.getChr().getClient().getSessionIPAddress() + ")????????? ??????: ??????\r\n");
                Localtrade.getChr().addEmptyTradeMsg("========================================\r\n");
                partner.getChr().addTradeMsg(partner.getChr().getName(), " ???????????? ???" + Localtrade.getChr().getName() + "(" + Localtrade.getChr().getClient().getSessionIPAddress() + ")????????? ??????: ??????\r\n");
                partner.getChr().addEmptyTradeMsg("========================================\r\n");
                Localtrade.getChr().endTradeMsg(Localtrade.getChr().getName());
                partner.getChr().endTradeMsg(partner.getChr().getName());

                partner.cancel(partner.getChr().getClient(), partner.getChr());
                partner.getChr().setTrade(null);
            }
            Localtrade.setCanceling(false);
            if (Localtrade.chr.get() != null) {
                Localtrade.chr.get().setTrade(null);
            }
        }
    }

    public static final void startTrade(final MapleCharacter c) {
        if (c.getTrade() == null) {
            c.setTrade(new MapleTrade((byte) 0, c));
            c.getClient().sendPacket(InteractionPacket.getTradeStart(c.getClient(), c.getTrade(), (byte) 0));
        } else {
            c.getClient().sendPacket(CWvsContext.serverNotice(5, "???????????????????????????"));
        }
    }

    public static final void inviteTrade(final MapleCharacter c1, final MapleCharacter c2) {
        if (c1 == null || c1.getTrade() == null || c2 == null) {
            return;
        }
        if (ServerConstants.isShutdown) {
            c1.getTrade().cancelTrade(c1.getTrade(), c1.getClient(), c1);
            c1.setTrade(null);
            c1.getClient().sendPacket(CWvsContext.serverNotice(1, "?????????????????????"));
            return;
        }
        if (c1.getTrade().getPartner() != null) {
            c1.dropMessage(1, "??????????????????????????????");
            return;
        }
        if (c2.getTrade() == null && c1.getTrade().getPartner() == null) {
            c1.addTradeMsg(c1.getName(), " ???" + c2.getName() + "????????????\r\n");
            c2.addTradeMsg(c2.getName(), " ???" + c1.getName() + "????????????\r\n");
            c2.setTrade(new MapleTrade((byte) 1, c2));
            c2.getTrade().setPartner(c1.getTrade());
            c1.getTrade().setPartner(c2.getTrade());
            c2.getClient().sendPacket(InteractionPacket.getTradeInvite(c1));
        } else {
            c1.getClient().sendPacket(CWvsContext.serverNotice(5, "????????????????????????"));
            cancelTrade(c1.getTrade(), c1.getClient(), c1);
        }
    }

    public static final void visitTrade(final MapleCharacter c1, final MapleCharacter c2) {
        if (c2 != null && c1.getTrade() != null && c1.getTrade().getPartner() == c2.getTrade() && c2.getTrade() != null && c2.getTrade().getPartner() == c1.getTrade()) {
            // We don't need to check for map here as the user is found via MapleMap.getCharacterById()
            c1.getTrade().inTrade = true;
            c2.getClient().sendPacket(PlayerShopPacket.shopVisitorAdd(c1, 1));
            c1.getClient().sendPacket(InteractionPacket.getTradeStart(c1.getClient(), c1.getTrade(), (byte) 1));
            //c1.dropMessage(-2, "System : Use @tradehelp to see the list of trading commands");
            //c2.dropMessage(-2, "System : Use @tradehelp to see the list of trading commands");
        } else {
            c1.getClient().sendPacket(CWvsContext.serverNotice(5, "??????????????????????????????"));
        }
    }

    public static final void declineTrade(final MapleCharacter c) {
        final MapleTrade trade = c.getTrade();
        if (trade != null) {
            if (trade.getPartner() != null) {
                MapleCharacter other = trade.getPartner().getChr();
                if (other != null && other.getTrade() != null) {
                    other.getTrade().cancel(other.getClient(), other);
                    other.setTrade(null);
                    other.dropMessage(5, c.getName() + "??????????????????????????????");
                }
            }
            trade.cancel(c.getClient(), c);
            c.setTrade(null);
        }
    }

    public final void CompleteTrade() {
        final int mesos = exchangeMeso;
        String Items = "";
        if (exchangeItems != null) { // just to be on the safe side...
            List<Item> itemz = new LinkedList<>(exchangeItems);
            for (final Item item : itemz) {
                short flag = item.getFlag();

                if (ItemFlag.KARMA_EQ.check(flag)) {
                    item.setFlag((short) (flag - ItemFlag.KARMA_EQ.getValue()));
                } else if (ItemFlag.KARMA_USE.check(flag)) {
                    item.setFlag((short) (flag - ItemFlag.KARMA_USE.getValue()));
                }
                Items += item.getItemId() + "(" + item.getItemName() + ")x" + item.getQuantity() + " ??????ID:" + item.getEquipOnlyId() + ", ";
                MapleInventoryManipulator.addFromDrop(chr.get().getClient(), item, false);
            }
            exchangeItems.clear();
        }
        if (exchangeMeso > 0) {
            chr.get().gainMeso(exchangeMeso - GameConstants.getTaxAmount(exchangeMeso), false, false);
        }
        exchangeMeso = 0;
        chr.get().getClient().sendPacket(InteractionPacket.TradeMessage(tradingslot, (byte) 0x08));
        chr.get().addTradeMsg(chr.get().getName(), " ????????????,??????: " + Items + "\r\n");
        chr.get().addTradeMsg(chr.get().getName(), " ????????????,?????? " + mesos + "\r\n");
        chr.get().addTradeMsg(chr.get().getName(), " ????????????,??????: ????????????\r\n");
    }

    public final void cancel(final MapleClient c, final MapleCharacter chr) {
        cancel(c, chr, 0);
    }

    public final void cancel(final MapleClient c, final MapleCharacter chr, final int unsuccessful) {
        final int mesos = meso;
        String Items = "";
        if (items != null) { // just to be on the safe side...
            List<Item> itemz = new LinkedList<>(items);
            for (final Item item : itemz) {
                Items += item.getItemId() + "(" + item.getItemName() + ")x" + item.getQuantity() + " ??????ID:" + item.getEquipOnlyId() + ", ";
                MapleInventoryManipulator.addFromDrop(c, item, false);
            }
            items.clear();
        }
        if (meso > 0) {
            chr.gainMeso(meso, false, false);
        }
        meso = 0;

        if (c != null) {
            c.sendPacket(InteractionPacket.getTradeCancel(tradingslot, unsuccessful));
        }
        if (c != null && c.getPlayer() != null) {
            chr.addTradeMsg(chr.getName(), " ????????????,??????: " + Items + "\r\n");
            chr.addTradeMsg(chr.getName(), " ????????????,?????? " + mesos + "\r\n");
            chr.addTradeMsg(chr.getName(), " ????????????,??????: ????????????\r\n");
        }
    }

    public final boolean isLocked() {
        return locked;
    }

    public final void setMeso(final int meso) {
        if (locked || partner == null || meso <= 0 || this.meso + meso <= 0) {
            return;
        }
        if (chr.get().getMeso() >= meso) {
            chr.get().addTradeMsg(chr.get().getName(), " ????????????[" + meso + "]\r\n");
            chr.get().gainMeso(-meso, false, false);
            this.meso += meso;
            chr.get().getClient().sendPacket(InteractionPacket.getTradeMesoSet((byte) 0, this.meso));
            if (partner != null) {
                partner.getChr().getClient().sendPacket(InteractionPacket.getTradeMesoSet((byte) 1, this.meso));
            }
        }
    }

    public final void addItem(final Item item) {
        if (locked || partner == null) {
            return;
        }
        chr.get().addTradeMsg(chr.get().getName(), " ????????????[" + item.getItemId() + "]??????[" + item.getQuantity() + "]\r\n");
        items.add(item);
        chr.get().getClient().sendPacket(InteractionPacket.getTradeItemAdd((byte) 0, item));
        if (partner != null) {
            partner.getChr().getClient().sendPacket(InteractionPacket.getTradeItemAdd((byte) 1, item));
        }
    }

    public final void chat(final String message) {
        if (partner == null) {
            return;
        }
        if (partner != null) {
            partner.getChr().getClient().sendPacket(PlayerShopPacket.shopChat(chr.get().getName() + " : " + message, 1));
        }
        if (chr.get().getCanTalk()) {
            chr.get().dropMessage(-2, chr.get().getName() + " : " + message);
        }
        if (ServerConstants.log_chat) {
            try {
                FileoutputUtil.logToFile("logs/??????/????????????.txt", " " + FileoutputUtil.NowTime() + " IP: " + chr.get().getClient().getSession().remoteAddress().toString().split(":")[0] + " ???" + chr.get().getName() + "?????????" + partner.getChr().getName() + "?????????????????????  " + message + "\r\n");
            } catch (Exception ex) {

            }
        }
        final StringBuilder sb = new StringBuilder("[GM ??????] ???" + chr.get().getName() + "?????????" + partner.getChr().getName() + "?????????????????????  " + message);
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (MapleCharacter chr_ : cserv.getPlayerStorage().getAllCharactersThreadSafe()) {
                if (chr_.getmsg_Chat()) {
                    chr_.dropMessage(sb.toString());
                }
            }
        }
        if (chr.get().getClient().isMonitored()) { //Broadcast info even if it was a command.
            World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, chr.get().getName() + " ??? " + partner.getChr().getName() + " ???????????????: " + message));
        } else if (partner != null && partner.getChr() != null && partner.getChr().getClient().isMonitored()) {
            World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, chr.get().getName() + " ??? " + partner.getChr().getName() + " ???????????????: " + message));
        }
    }

    public final void chatAuto(final String message) {
        chr.get().dropMessage(-2, message);
        if (partner != null) {
            partner.getChr().getClient().sendPacket(PlayerShopPacket.shopChat(message, 1));
        }
        if (chr.get().getClient().isMonitored()) { //Broadcast info even if it was a command.
            World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, chr.get().getName() + " said in trade [Automated] with " + partner.getChr().getName() + ": " + message));
        } else if (partner != null && partner.getChr() != null && partner.getChr().getClient().isMonitored()) {
            World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, chr.get().getName() + " said in trade [Automated] with " + partner.getChr().getName() + ": " + message));
        }
    }

    public final MapleTrade getPartner() {
        return partner;
    }

    public final void setPartner(final MapleTrade partner) {
        if (locked) {
            return;
        }
        if (this.items != null) {
            this.items.clear();
        }
        if (this.exchangeItems != null) {
            this.exchangeItems.clear();
        }
        this.meso = 0;
        this.exchangeMeso = 0;
        this.partner = partner;
    }

    public final MapleCharacter getChr() {
        return chr.get();
    }

    public final int getNextTargetSlot() {
        if (items.size() >= 9) {
            return -1;
        }
        int ret = 1; //first slot
        for (Item item : items) {
            if (item.getPosition() == ret) {
                ret++;
            }
        }
        return ret;
    }

    public boolean inTrade() {
        return inTrade;
    }

    public final boolean setItems(final MapleClient c, final Item item, byte targetSlot, final int quantity) {
        int target = getNextTargetSlot();
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (partner == null || target == -1 || GameConstants.isPet(item.getItemId()) || isLocked() || (GameConstants.getInventoryType(item.getItemId()) == MapleInventoryType.EQUIP && quantity != 1)) {
            return false;
        }
        final short flag = item.getFlag();
        if (ItemFlag.UNTRADEABLE.check(flag) || ItemFlag.LOCK.check(flag)) {
            c.sendPacket(CWvsContext.enableActions());
            return false;
        }
        if (ii.isDropRestricted(item.getItemId()) || ii.isAccountShared(item.getItemId())) {
            if (!(ItemFlag.KARMA_EQ.check(flag) || ItemFlag.KARMA_USE.check(flag))) {
                c.sendPacket(CWvsContext.enableActions());
                return false;
            }
        }
        if (GameConstants.nottrade(item.getItemId()) /*|| ("Donor".equals(item.getOwner()))*/) {
            c.getPlayer().dropMessage(-2, "?????????" + MapleItemInformationProvider.getInstance().getName(item.getItemId()) + "???????????????");
            c.sendPacket(CWvsContext.enableActions());
            return false;
        }
        Item tradeItem = item.copy();
        if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
            tradeItem.setQuantity(item.getQuantity());
            MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(item.getItemId()), item.getPosition(), item.getQuantity(), true);
        } else {
            tradeItem.setQuantity((short) quantity);
            MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(item.getItemId()), item.getPosition(), (short) quantity, true);
        }
        if (targetSlot < 0) {
            targetSlot = (byte) target;
        } else {
            for (Item itemz : items) {
                if (itemz.getPosition() == targetSlot) {
                    targetSlot = (byte) target;
                    break;
                }
            }
        }
        tradeItem.setPosition(targetSlot);
        addItem(tradeItem);
        return true;
    }

    private int check() { //0 = fine, 1 = invent space not, 2 = pickupRestricted
        if (chr.get() == null) {
            return 2;
        }
        if ((chr.get().getMeso() + exchangeMeso) < 0 || (chr.get().getMeso() + exchangeMeso) > 2147483647) {
            return 1;
        }

        if (exchangeItems != null) {
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            byte eq = 0, use = 0, setup = 0, etc = 0, cash = 0;
            for (final Item item : exchangeItems) {
                switch (GameConstants.getInventoryType(item.getItemId())) {
                    case EQUIP:
                        eq++;
                        break;
                    case USE:
                        use++;
                        break;
                    case SETUP:
                        setup++;
                        break;
                    case ETC:
                        etc++;
                        break;
                    case CASH: // Not allowed, probably hacking
                        cash++;
                        break;
                }
                if (ii.isPickupRestricted(item.getItemId()) && chr.get().getInventory(GameConstants.getInventoryType(item.getItemId())).findById(item.getItemId()) != null) {
                    return 2;
                } else if (ii.isPickupRestricted(item.getItemId()) && chr.get().haveItem(item.getItemId(), 1, true, true)) {
                    return 2;
                }
            }
            if (chr.get().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < eq || chr.get().getInventory(MapleInventoryType.USE).getNumFreeSlot() < use || chr.get().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < setup || chr.get().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < etc || chr.get().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < cash) {
                return 1;
            }
        }

        return 0;
    }
}
