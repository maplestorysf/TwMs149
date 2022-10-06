package server;

import java.awt.Point;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import client.inventory.*;
import constants.GameConstants;
import client.PlayerStats;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleQuestStatus;
import client.MapleTrait.MapleTraitType;
import client.Skill;
import client.SkillEntry;
import client.SkillFactory;
import constants.MiMiConfig;
import server.maps.AramiaFireWorks;
import tools.packet.CField;
import tools.packet.MTSCSPacket;
import tools.StringUtil;
import client.inventory.EquipAdditions.RingSet;
import constants.ItemConstants;
import constants.ServerConstants;
import handling.world.World;
import java.util.HashMap;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.InfoPacket;
import tools.packet.CWvsContext.InventoryPacket;

public class MapleInventoryManipulator {

    public static void addRing(MapleCharacter chr, int itemId, int ringId, int sn, String partner) {
        CashItem csi = CashItemFactory.getInstance().getItem(sn);
        if (csi == null) {
            return;
        }
        Item ring = chr.getCashInventory().toItem(csi, ringId);
        if (ring == null || ring.getUniqueId() != ringId || ring.getUniqueId() <= 0 || ring.getItemId() != itemId) {
            return;
        }
        chr.getCashInventory().addToInventory(ring);
        chr.getClient().sendPacket(MTSCSPacket.sendBoughtRings(GameConstants.isCrushRing(itemId), ring, sn, chr.getClient().getAccID(), partner));
    }

    public static boolean addbyItem(final MapleClient c, final Item item) {
        return addbyItem(c, item, false) >= 0;
    }

    public static short addbyItem(final MapleClient c, final Item item, final boolean fromcs) {
        final MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
        final short newSlot = c.getPlayer().getInventory(type).addItem(item);
        if (newSlot == -1) {
            if (!fromcs) {
                c.sendPacket(InventoryPacket.getInventoryFull());
                c.sendPacket(InventoryPacket.getShowInventoryFull());
            }
            return newSlot;
        }
        if (item.hasSetOnlyId()) {
            item.setEquipOnlyId(MapleEquipOnlyId.getInstance().getNextEquipOnlyId());
        }
        if (GameConstants.isHarvesting(item.getItemId())) {
            c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
        }
        c.sendPacket(InventoryPacket.addInventorySlot(type, item));
        c.getPlayer().havePartyQuest(item.getItemId());
        return newSlot;
    }

    public static int getUniqueId(int itemId, MaplePet pet) {
        int uniqueid = -1;
        if (GameConstants.isPet(itemId)) {
            if (pet != null) {
                uniqueid = pet.getUniqueId();
            } else {
                uniqueid = MapleInventoryIdentifier.getInstance();
            }
        } else if (GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH || MapleItemInformationProvider.getInstance().isCash(itemId)) { //less work to do
            uniqueid = MapleInventoryIdentifier.getInstance(); //shouldnt be generated yet, so put it here
        }
        return uniqueid;
    }

    public static boolean addById(MapleClient c, int itemId, short quantity, String gmLog) {
        return addById(c, itemId, quantity, null, null, 0, gmLog);
    }

    public static boolean addById(MapleClient c, int itemId, short quantity, String owner, String gmLog) {
        return addById(c, itemId, quantity, owner, null, 0, gmLog);
    }

    public static byte addId(MapleClient c, int itemId, short quantity, String owner, String gmLog) {
        return addId(c, itemId, quantity, owner, null, 0, gmLog);
    }

    public static boolean addById(MapleClient c, int itemId, short quantity, String owner, MaplePet pet, String gmLog) {
        return addById(c, itemId, quantity, owner, pet, 0, gmLog);
    }

    public static boolean addById(MapleClient c, int itemId, short quantity, String owner, MaplePet pet, long period) {
        return addId(c, itemId, quantity, owner, pet, period, null) >= 0;
    }

    public static boolean addById(MapleClient c, int itemId, short quantity, String owner, MaplePet pet, long period, String gmLog) {
        return addId(c, itemId, quantity, owner, pet, period, gmLog) >= 0;
    }

    public static byte addId(MapleClient c, int itemId, short quantity, String owner, MaplePet pet, long period, String gmLog) {
        if (ServerConstants.LOG_TRACE_INVENTORY) {
            StringBuilder builder = new StringBuilder();
            builder.append("取得物品：").append(itemId).append(" 數量：").append(quantity).append(("\r\n"));
            builder.append("StackTraceing=>").append("\r\n");
            Throwable e = new Throwable();
            int nTrace = Math.min(e.getStackTrace().length, MiMiConfig.nStackTraceMax);
            for (int i = 0; i < nTrace; ++i) {
                StackTraceElement ste = e.getStackTrace()[i];
                builder.append("\t").append(ste.toString()).append("\r\n");
            }
            FileoutputUtil.print("/StackTrace/items/" + c.getPlayer().getName() + ".txt", builder.toString());
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (!ii.itemExists(itemId)) {
            FileoutputUtil.log("logs/except/不存在道具.txt", FileoutputUtil.CurrentReadable_Time() + "玩家: " + c.getPlayer().getName() + "道具:" + itemId + "\r\n");
            return -1;
        }
        if ((ii.isPickupRestricted(itemId) && c.getPlayer().haveItem(itemId, 1, true, false)) || (!ii.itemExists(itemId))) {
            c.sendPacket(InventoryPacket.getInventoryFull());
            c.sendPacket(InventoryPacket.showItemUnavailable());
            return -1;
        }
        final MapleInventoryType type = GameConstants.getInventoryType(itemId);
        int uniqueid = getUniqueId(itemId, pet);
        short newSlot = -1;
        if (!type.equals(MapleInventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(itemId);
            final List<Item> existing = c.getPlayer().getInventory(type).listById(itemId);
            if (!GameConstants.isRechargable(itemId)) {
                if (existing.size() > 0) { // first update all existing slots to slotMax
                    Iterator<Item> i = existing.iterator();
                    while (quantity > 0) {
                        if (i.hasNext()) {
                            Item eItem = (Item) i.next();
                            short oldQ = eItem.getQuantity();
                            if (oldQ < slotMax && (eItem.getOwner().equals(owner) || owner == null) && eItem.getExpiration() == -1) {
                                short newQ = (short) Math.min(oldQ + quantity, slotMax);
                                quantity -= (newQ - oldQ);
                                eItem.setQuantity(newQ);
                                c.sendPacket(InventoryPacket.updateInventorySlot(type, eItem, false));
                            }
                        } else {
                            break;
                        }
                    }
                }
                Item nItem;
                // add new slots if there is still something left
                while (quantity > 0) {
                    short newQ = (short) Math.min(quantity, slotMax);
                    if (newQ != 0) {
                        quantity -= newQ;
                        nItem = new Item(itemId, (byte) 0, newQ, (byte) 0, uniqueid);
                        newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                        if (newSlot == -1) {
                            c.sendPacket(InventoryPacket.getInventoryFull());
                            c.sendPacket(InventoryPacket.getShowInventoryFull());
                            return -1;
                        }
                        if (gmLog != null) {
                            nItem.setGMLog(gmLog);
                        }
                        if (owner != null) {
                            nItem.setOwner(owner);
                        }
                        if (period > 0) {
                            nItem.setExpiration(System.currentTimeMillis() + (period * 24 * 60 * 60 * 1000));
                        }
                        if (pet != null) {
                            nItem.setPet(pet);
                            pet.setInventoryPosition(newSlot);
                            c.getPlayer().addPet(pet);
                        }
                        c.sendPacket(InventoryPacket.addInventorySlot(type, nItem));
                        if (GameConstants.isRechargable(itemId) && quantity == 0) {
                            break;
                        }
                    } else {
                        c.getPlayer().havePartyQuest(itemId);
                        c.sendPacket(CWvsContext.enableActions());
                        return (byte) newSlot;
                    }
                }
            } else {
                // Throwing Stars and Bullets - Add all into one slot regardless of quantity.
                final Item nItem = new Item(itemId, (byte) 0, quantity, (byte) 0, uniqueid);
                newSlot = c.getPlayer().getInventory(type).addItem(nItem);

                if (newSlot == -1) {
                    c.sendPacket(InventoryPacket.getInventoryFull());
                    c.sendPacket(InventoryPacket.getShowInventoryFull());
                    return -1;
                }
                if (period > 0) {
                    nItem.setExpiration(System.currentTimeMillis() + (period * 24 * 60 * 60 * 1000));
                }
                if (gmLog != null) {
                    nItem.setGMLog(gmLog);
                }
                c.sendPacket(InventoryPacket.addInventorySlot(type, nItem));
                c.sendPacket(CWvsContext.enableActions());
            }
        } else {
            if (quantity == 1) {
                final Item nEquip = ii.getEquipById(itemId, uniqueid);
                if (owner != null) {
                    nEquip.setOwner(owner);
                }
                if (gmLog != null) {
                    nEquip.setGMLog(gmLog);
                }
                if (period > 0) {
                    nEquip.setExpiration(System.currentTimeMillis() + (period * 24 * 60 * 60 * 1000));
                }
                if (nEquip.hasSetOnlyId()) {
                    nEquip.setEquipOnlyId(MapleEquipOnlyId.getInstance().getNextEquipOnlyId());
                }
                newSlot = c.getPlayer().getInventory(type).addItem(nEquip);
                if (newSlot == -1) {
                    c.sendPacket(InventoryPacket.getInventoryFull());
                    c.sendPacket(InventoryPacket.getShowInventoryFull());
                    return -1;
                }
                c.sendPacket(InventoryPacket.addInventorySlot(type, nEquip));
                if (GameConstants.isHarvesting(itemId)) {
                    c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
                }
            } else {
                throw new InventoryException("Trying to create equip with non-one quantity");
            }
        }
        c.getPlayer().havePartyQuest(itemId);
        return (byte) newSlot;
    }

    public static Item addbyId_Gachapon(final MapleClient c, final int itemId, short quantity) {
        if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() == -1 || c.getPlayer().getInventory(MapleInventoryType.USE).getNextFreeSlot() == -1 || c.getPlayer().getInventory(MapleInventoryType.ETC).getNextFreeSlot() == -1 || c.getPlayer().getInventory(MapleInventoryType.SETUP).getNextFreeSlot() == -1) {
            return null;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if ((ii.isPickupRestricted(itemId) && c.getPlayer().haveItem(itemId, 1, true, false)) || (!ii.itemExists(itemId))) {
            c.sendPacket(InventoryPacket.getInventoryFull());
            c.sendPacket(InventoryPacket.showItemUnavailable());
            return null;
        }
        final MapleInventoryType type = GameConstants.getInventoryType(itemId);

        if (!type.equals(MapleInventoryType.EQUIP)) {
            short slotMax = ii.getSlotMax(itemId);
            final List<Item> existing = c.getPlayer().getInventory(type).listById(itemId);

            if (!GameConstants.isRechargable(itemId)) {
                Item nItem = null;
                boolean recieved = false;

                if (existing.size() > 0) { // first update all existing slots to slotMax
                    Iterator<Item> i = existing.iterator();
                    while (quantity > 0) {
                        if (i.hasNext()) {
                            nItem = (Item) i.next();
                            short oldQ = nItem.getQuantity();

                            if (oldQ < slotMax) {
                                recieved = true;

                                short newQ = (short) Math.min(oldQ + quantity, slotMax);
                                quantity -= (newQ - oldQ);
                                nItem.setQuantity(newQ);
                                c.sendPacket(InventoryPacket.updateInventorySlot(type, nItem, false));
                            }
                        } else {
                            break;
                        }
                    }
                }
                // add new slots if there is still something left
                while (quantity > 0) {
                    short newQ = (short) Math.min(quantity, slotMax);
                    if (newQ != 0) {
                        quantity -= newQ;
                        nItem = new Item(itemId, (byte) 0, newQ, (byte) 0);
                        final short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                        if (newSlot == -1 && recieved) {
                            return nItem;
                        } else if (newSlot == -1) {
                            return null;
                        }
                        recieved = true;
                        c.sendPacket(InventoryPacket.addInventorySlot(type, nItem));
                        if (GameConstants.isRechargable(itemId) && quantity == 0) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                if (recieved) {
                    c.getPlayer().havePartyQuest(nItem.getItemId());
                    return nItem;
                }
            } else {
                // Throwing Stars and Bullets - Add all into one slot regardless of quantity.
                final Item nItem = new Item(itemId, (byte) 0, quantity, (byte) 0);
                final short newSlot = c.getPlayer().getInventory(type).addItem(nItem);

                if (newSlot == -1) {
                    return null;
                }
                c.sendPacket(InventoryPacket.addInventorySlot(type, nItem));
                c.getPlayer().havePartyQuest(nItem.getItemId());
                return nItem;
            }
        } else {
            if (quantity == 1) {
                final Item item = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                final short newSlot = c.getPlayer().getInventory(type).addItem(item);

                if (newSlot == -1) {
                    return null;
                }
                if (item.hasSetOnlyId()) {
                    item.setEquipOnlyId(MapleEquipOnlyId.getInstance().getNextEquipOnlyId());
                }
                c.sendPacket(InventoryPacket.addInventorySlot(type, item, true));
                c.getPlayer().havePartyQuest(item.getItemId());
                return item;
            } else {
                throw new InventoryException("Trying to create equip with non-one quantity");
            }
        }
        return null;
    }

    public static boolean addFromDrop(final MapleClient c, final Item item, final boolean show) {
        return addFromDrop(c, item, show, false, false);
    }

    public static boolean addFromDrop(final MapleClient c, Item item, final boolean show, final boolean enhance, final boolean isPetPickup) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (!ii.itemExists(item.getItemId())) {
            FileoutputUtil.log("logs/except/不存在道具.txt", FileoutputUtil.CurrentReadable_Time() + "玩家: " + c.getPlayer().getName() + "道具:" + item.getItemId() + "\r\n");
            return false;
        }
        if (c.getPlayer() == null || (ii.isPickupRestricted(item.getItemId()) && c.getPlayer().haveItem(item.getItemId(), 1, true, false)) || (!ii.itemExists(item.getItemId()))) {
            c.sendPacket(InventoryPacket.getInventoryFull());
            c.sendPacket(InventoryPacket.showItemUnavailable());
            return false;
        }
        final int before = c.getPlayer().itemQuantity(item.getItemId());
        short quantity = item.getQuantity();
        final MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());

        if (!type.equals(MapleInventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(item.getItemId());
            final List<Item> existing = c.getPlayer().getInventory(type).listById(item.getItemId());
            if (!GameConstants.isRechargable(item.getItemId())) {
                if (quantity <= 0) { //wth
                    c.sendPacket(InventoryPacket.getInventoryFull());
                    c.sendPacket(InventoryPacket.showItemUnavailable());
                    return false;
                }
                if (existing.size() > 0) { // first update all existing slots to slotMax
                    Iterator<Item> i = existing.iterator();
                    while (quantity > 0) {
                        if (i.hasNext()) {
                            final Item eItem = (Item) i.next();
                            final short oldQ = eItem.getQuantity();
                            if (oldQ < slotMax && item.getOwner().equals(eItem.getOwner()) && item.getExpiration() == eItem.getExpiration()) {
                                final short newQ = (short) Math.min(oldQ + quantity, slotMax);
                                quantity -= (newQ - oldQ);
                                eItem.setQuantity(newQ);
                                if (isPetPickup) {
                                    c.sendPacket(InventoryPacket.updateInventorySlot(type, eItem, false));
                                } else {
                                    c.sendPacket(InventoryPacket.updateInventorySlot(type, eItem, true));
                                }
                            }
                        } else {
                            break;
                        }
                    }
                }
                // add new slots if there is still something left
                while (quantity > 0) {
                    final short newQ = (short) Math.min(quantity, slotMax);
                    quantity -= newQ;
                    final Item nItem = new Item(item.getItemId(), (byte) 0, newQ, item.getFlag());
                    nItem.setExpiration(item.getExpiration());
                    nItem.setOwner(item.getOwner());
                    nItem.setPet(item.getPet());
                    nItem.setGMLog(item.getGMLog());
                    short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                    if (newSlot == -1) {
                        c.sendPacket(InventoryPacket.getInventoryFull());
                        c.sendPacket(InventoryPacket.getShowInventoryFull());
                        item.setQuantity((short) (quantity + newQ));
                        return false;
                    }
                    if (isPetPickup) {
                        c.sendPacket(InventoryPacket.addInventorySlot(type, nItem, false));
                    } else {
                        c.sendPacket(InventoryPacket.addInventorySlot(type, nItem, true));
                    }
                }
            } else {
                // Throwing Stars and Bullets - Add all into one slot regardless of quantity.
                final Item nItem = new Item(item.getItemId(), (byte) 0, quantity, item.getFlag());
                nItem.setExpiration(item.getExpiration());
                nItem.setOwner(item.getOwner());
                nItem.setPet(item.getPet());
                nItem.setGMLog(item.getGMLog());
                final short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                if (newSlot == -1) {
                    c.sendPacket(InventoryPacket.getInventoryFull());
                    c.sendPacket(InventoryPacket.getShowInventoryFull());
                    return false;
                }
                c.sendPacket(InventoryPacket.addInventorySlot(type, nItem));
                c.sendPacket(CWvsContext.enableActions());
            }
        } else {
            if (quantity == 1) {
                if (item.hasSetOnlyId()) {
                    item.setEquipOnlyId(MapleEquipOnlyId.getInstance().getNextEquipOnlyId());
                }
                if (enhance) {
                    item = checkEnhanced(item, c.getPlayer());
                }
                final short newSlot = c.getPlayer().getInventory(type).addItem(item);

                if (newSlot == -1) {
                    c.sendPacket(InventoryPacket.getInventoryFull());
                    c.sendPacket(InventoryPacket.getShowInventoryFull());
                    return false;
                }
                c.sendPacket(InventoryPacket.addInventorySlot(type, item, true));
                if (GameConstants.isHarvesting(item.getItemId())) {
                    c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
                }
            } else {
                throw new RuntimeException("Trying to create equip with non-one quantity");
            }
        }
        if (item.getQuantity() >= 50 && item.getItemId() == 2340000) {
            c.setMonitored(true);
        }
        if (before == 0) {
            switch (item.getItemId()) {
                case AramiaFireWorks.KEG_ID:
                    //c.getPlayer().dropMessage(5, "收集火藥桶可至邱比特公園參加活動。");
                    break;
                case AramiaFireWorks.SUN_ID:
                    //c.getPlayer().dropMessage(5, "收集溫暖陽光可至楓樹山丘參加活動。");
                    break;
                case AramiaFireWorks.DEC_ID:
                    //c.getPlayer().dropMessage(5, "收集聖誕樹裝飾可至白色聖誕節之丘參加活動。");
                    break;
                case AramiaFireWorks.香爐_ID:
                    //c.getPlayer().dropMessage(5, "收集香爐可至不夜城參加活動。");
                    break;
            }
        }
        c.getPlayer().havePartyQuest(item.getItemId());
        if (show) {
            c.sendPacket(InfoPacket.getShowItemGain(item.getItemId(), item.getQuantity()));
        }
        return true;
    }

    private static Item checkEnhanced(final Item before, final MapleCharacter chr) {
        if (before instanceof Equip) {
            final Equip eq = (Equip) before;
            if (eq.getState() == 0 && (eq.getUpgradeSlots() >= 1 || eq.getLevel() >= 1) && GameConstants.canScroll(eq.getItemId()) && !GameConstants.isMechequip(eq.getItemId()) && Randomizer.nextInt(100) >= 80) { //20% chance of pot?
                eq.resetPotential();
            }
        }
        return before;
    }

    public static boolean checkSpace(final MapleClient c, final int itemid, int quantity, final String owner) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (c.getPlayer() == null || (ii.isPickupRestricted(itemid) && c.getPlayer().haveItem(itemid, 1, true, false)) || (!ii.itemExists(itemid))) {
            c.sendPacket(CWvsContext.enableActions());
            return false;
        }
        if (quantity <= 0 && !GameConstants.isRechargable(itemid)) {
            return false;
        }
        final MapleInventoryType type = GameConstants.getInventoryType(itemid);
        if (c == null || c.getPlayer() == null || c.getPlayer().getInventory(type) == null) { //wtf is causing this?
            return false;
        }
        if (!type.equals(MapleInventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(itemid);
            final List<Item> existing = c.getPlayer().getInventory(type).listById(itemid);
            if (!GameConstants.isRechargable(itemid)) {
                if (existing.size() > 0) { // first update all existing slots to slotMax
                    for (Item eItem : existing) {
                        final short oldQ = eItem.getQuantity();
                        if (oldQ < slotMax && owner != null && owner.equals(eItem.getOwner())) {
                            final short newQ = (short) Math.min(oldQ + quantity, slotMax);
                            quantity -= (newQ - oldQ);
                        }
                        if (quantity <= 0) {
                            break;
                        }
                    }
                }
            }
            // add new slots if there is still something left
            final int numSlotsNeeded;
            if (slotMax > 0 && !GameConstants.isRechargable(itemid)) {
                numSlotsNeeded = (int) (Math.ceil(((double) quantity) / slotMax));
            } else {
                numSlotsNeeded = 1;
            }
            return !c.getPlayer().getInventory(type).isFull(numSlotsNeeded - 1);
        } else {
            return !c.getPlayer().getInventory(type).isFull();
        }
    }

    public static boolean removeFromSlot(final MapleClient c, final MapleInventoryType type, final short slot, final short quantity, final boolean fromDrop) {
        return removeFromSlot(c, type, slot, quantity, fromDrop, false);
    }

    public static boolean removeFromSlot(final MapleClient c, final MapleInventoryType type, final short slot, short quantity, final boolean fromDrop, final boolean consume) {
        if (c.getPlayer() == null || c.getPlayer().getInventory(type) == null) {
            return false;
        }
        final Item item = c.getPlayer().getInventory(type).getItem(slot);
        if (item != null) {
            final boolean allowZero = consume && GameConstants.isRechargable(item.getItemId());
            c.getPlayer().getInventory(type).removeItem(slot, quantity, allowZero);
            if (GameConstants.isHarvesting(item.getItemId())) {
                c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
            }

            if (item.getQuantity() == 0 && !allowZero) {
                c.sendPacket(InventoryPacket.clearInventoryItem(type, item.getPosition(), fromDrop));
            } else {
                c.sendPacket(InventoryPacket.updateInventorySlot(type, (Item) item, fromDrop));
            }
            return true;
        }
        return false;
    }

    public static boolean removeById(final MapleClient c, final MapleInventoryType type, final int itemId, final int quantity, final boolean fromDrop, final boolean consume) {
        int remremove = quantity;
        if (c.getPlayer() == null || c.getPlayer().getInventory(type) == null) {
            return false;
        }
        for (Item item : c.getPlayer().getInventory(type).listById(itemId)) {
            int theQ = item.getQuantity();
            if (remremove <= theQ && removeFromSlot(c, type, item.getPosition(), (short) remremove, fromDrop, consume)) {
                remremove = 0;
                break;
            } else if (remremove > theQ && removeFromSlot(c, type, item.getPosition(), item.getQuantity(), fromDrop, consume)) {
                remremove -= theQ;
            }
        }
        return remremove <= 0;
    }

    public static boolean removeFromSlot_Lock(final MapleClient c, final MapleInventoryType type, final short slot, short quantity, final boolean fromDrop, final boolean consume) {
        if (c.getPlayer() == null || c.getPlayer().getInventory(type) == null) {
            return false;
        }
        final Item item = c.getPlayer().getInventory(type).getItem(slot);
        if (item != null) {
            if (ItemFlag.LOCK.check(item.getFlag()) || ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                return false;
            }
            return removeFromSlot(c, type, slot, quantity, fromDrop, consume);
        }
        return false;
    }

    public static boolean removeById_Lock(final MapleClient c, final MapleInventoryType type, final int itemId) {
        for (Item item : c.getPlayer().getInventory(type).listById(itemId)) {
            if (removeFromSlot_Lock(c, type, item.getPosition(), (short) 1, false, false)) {
                return true;
            }
        }
        return false;
    }

    public static void move(final MapleClient c, final MapleInventoryType type, final short src, final short dst) {
        if (src < 0 || dst < 0 || src == dst || type == MapleInventoryType.EQUIPPED) {
            return;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final Item source = c.getPlayer().getInventory(type).getItem(src);
        final Item initialTarget = c.getPlayer().getInventory(type).getItem(dst);
        if (source == null) {
            return;
        }
        boolean bag = false, switchSrcDst = false, bothBag = false;
        short eqIndicator = -1;
        if (dst > c.getPlayer().getInventory(type).getSlotLimit()) {
            if (type == MapleInventoryType.ETC && dst > 100 && dst % 100 != 0) {
                final int eSlot = c.getPlayer().getExtendedSlot((dst / 100) - 1);
                if (eSlot > 0) {
                    final MapleStatEffect ee = ii.getItemEffect(eSlot);
                    if (dst % 100 > ee.getSlotCount() || ee.getType() != ii.getBagType(source.getItemId()) || ee.getType() <= 0) {
                        c.getPlayer().dropMessage(1, "無法移動道具到背包.");
                        c.sendPacket(CWvsContext.enableActions());
                        return;
                    } else {
                        eqIndicator = 0;
                        bag = true;
                    }
                } else {
                    c.getPlayer().dropMessage(1, "背包已滿, 無法移動.");
                    c.sendPacket(CWvsContext.enableActions());
                    return;
                }
            } else {
                c.getPlayer().dropMessage(1, "無法移動到那裡.");
                c.sendPacket(CWvsContext.enableActions());
                return;
            }
        }
        if (src > c.getPlayer().getInventory(type).getSlotLimit() && type == MapleInventoryType.ETC && src > 100 && src % 100 != 0) {
            //source should be not null so not much checks are needed
            if (!bag) {
                switchSrcDst = true;
                eqIndicator = 0;
                bag = true;
            } else {
                bothBag = true;
            }
        }
        short olddstQ = -1;
        if (initialTarget != null) {
            olddstQ = initialTarget.getQuantity();
        }
        final short oldsrcQ = source.getQuantity();
        final short slotMax = ii.getSlotMax(source.getItemId());
        c.getPlayer().getInventory(type).move(src, dst, slotMax);
        if (GameConstants.isHarvesting(source.getItemId())) {
            c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
        }
        if (!type.equals(MapleInventoryType.EQUIP) && initialTarget != null
                && initialTarget.getItemId() == source.getItemId()
                && initialTarget.getOwner().equals(source.getOwner())
                && initialTarget.getExpiration() == source.getExpiration()
                && !GameConstants.isRechargable(source.getItemId())
                && !type.equals(MapleInventoryType.CASH)) {
            if (GameConstants.isHarvesting(initialTarget.getItemId())) {
                c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
            }
            if ((olddstQ + oldsrcQ) > slotMax) {
                c.sendPacket(InventoryPacket.moveAndMergeWithRestInventoryItem(type, src, dst, (short) ((olddstQ + oldsrcQ) - slotMax), slotMax, bag, switchSrcDst, bothBag));
            } else {
                c.sendPacket(InventoryPacket.moveAndMergeInventoryItem(type, src, dst, ((Item) c.getPlayer().getInventory(type).getItem(dst)).getQuantity(), bag, switchSrcDst, bothBag));
            }
        } else {
            c.sendPacket(InventoryPacket.moveInventoryItem(type, switchSrcDst ? dst : src, switchSrcDst ? src : dst, eqIndicator, bag, bothBag));
        }
    }

    public static void equip(final MapleClient c, final short src, short dst) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final MapleCharacter chr = c.getPlayer();
        if (chr == null || dst == -55) {
            return;
        }
        final PlayerStats statst = c.getPlayer().getStat();
        Equip source = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem(src);
        Equip target = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);

        if (source == null || source.getDurability() == 0 || GameConstants.isHarvesting(source.getItemId())) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }

        if (GameConstants.isGMEquip(source.getItemId()) && !c.getPlayer().isGM() && !ServerConstants.Can_GMItems) {
            c.getPlayer().dropMessage(1, "只有管理員能裝備這件道具。");
            c.getPlayer().removeAll(source.getItemId(), true);
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        chr.expirationTask(false, true);
        final Map<String, Integer> stats = ii.getEquipStats(source.getItemId());

        if (stats == null) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (dst > -1200 && dst < -999 && !GameConstants.isEvanDragonItem(source.getItemId()) && !GameConstants.isMechequip(source.getItemId())) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        } else if ((dst <= -5003 || (dst >= -999 && dst < -99)) && !stats.containsKey("cash")) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        } else if ((dst <= -1300) && (dst > -1306)) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (!ii.canEquip(stats, source.getItemId(), chr.getLevel(), chr.getJob(), chr.getFame(), statst.getTotalStr(), statst.getTotalDex(), statst.getTotalLuk(), statst.getTotalInt(), c.getPlayer().getStat().levelBonus)) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if ((ItemConstants.is透明短刀(source.getItemId()) && dst != -110) & GameConstants.isWeapon(source.getItemId()) && dst != -10 && dst != -11) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (dst == -18 && !GameConstants.isMountItemAvailable(source.getItemId(), c.getPlayer().getJob())) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (dst == -118 && source.getItemId() / 10000 != 190) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (dst > -5003 && dst <= -5000 && source.getItemId() / 10000 != 120) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (dst == -59) { //pendant
            MapleQuestStatus stat = c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
            if (stat == null || stat.getCustomData() == null || Long.parseLong(stat.getCustomData()) < System.currentTimeMillis()) {
                c.sendPacket(CWvsContext.enableActions());
                return;
            }
        }

        if (!ItemConstants.is透明短刀(source.getItemId()) && (GameConstants.isKatara(source.getItemId()) || source.getItemId() / 10000 == 135)) {
            dst = (byte) -10; //shield slot
        }

        if (GameConstants.isEvanDragonItem(source.getItemId()) && (chr.getJob() < 2200 || chr.getJob() > 2218)) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }

        if (GameConstants.isMechequip(source.getItemId()) && (chr.getJob() < 3500 || chr.getJob() > 3512)) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }

        if (source.getItemId() / 1000 == 1112) { //ring
            for (RingSet s : RingSet.values()) {
                if (s.id.contains(source.getItemId())) {
                    List<Integer> theList = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).listIds();
                    for (Integer i : s.id) {
                        if (theList.contains(i)) {
                            c.getPlayer().dropMessage(1, "You may not equip this item because you already have a " + (StringUtil.makeEnumHumanReadable(s.name())) + " equipped.");
                            c.sendPacket(CWvsContext.enableActions());
                            return;
                        }
                    }
                }
            }
        }
        if (c.getPlayer().getDebugMessage()) {
            c.getPlayer().dropMessage("穿裝備: src : " + src + " dst : " + dst + " 代碼：" + source.getItemId() + " 唯一ID: " + source.getEquipOnlyId());
        }

        switch (dst) {
            case -6: { // Top
                final Item top = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -5);
                if (top != null && GameConstants.isOverall(top.getItemId())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                        c.sendPacket(InventoryPacket.getInventoryFull());
                        c.sendPacket(InventoryPacket.getShowInventoryFull());
                        return;
                    }
                    unequip(c, (byte) -5, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                }
                break;
            }
            case -5: {
                final Item top = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -5);
                final Item bottom = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -6);
                if (top != null && GameConstants.isOverall(source.getItemId())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull(bottom != null && GameConstants.isOverall(source.getItemId()) ? 1 : 0)) {
                        c.sendPacket(InventoryPacket.getInventoryFull());
                        c.sendPacket(InventoryPacket.getShowInventoryFull());
                        return;
                    }
                    unequip(c, (byte) -5, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                }
                if (bottom != null && GameConstants.isOverall(source.getItemId())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                        c.sendPacket(InventoryPacket.getInventoryFull());
                        c.sendPacket(InventoryPacket.getShowInventoryFull());
                        return;
                    }
                    unequip(c, (byte) -6, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                }
                break;
            }
            case -10: { // Shield
                Item weapon = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11);
                if (GameConstants.isKatara(source.getItemId())) {
                    if ((chr.getJob() != 900 && (chr.getJob() < 430 || chr.getJob() > 434)) || weapon == null || !GameConstants.isDagger(weapon.getItemId())) {
                        c.sendPacket(InventoryPacket.getInventoryFull());
                        c.sendPacket(InventoryPacket.getShowInventoryFull());
                        return;
                    }
                    // 雙弩槍 fix
                } else if (weapon != null && GameConstants.isTwoHanded(weapon.getItemId()) && !GameConstants.isMercedes(chr.getJob())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                        c.sendPacket(InventoryPacket.getInventoryFull());
                        c.sendPacket(InventoryPacket.getShowInventoryFull());
                        return;
                    }
                    if (ItemConstants.武器類型(weapon.getItemId()) != MapleWeaponType.單手棍) {
                        unequip(c, (byte) -11, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                    }
                }
                break;
            }
            case -11: { // Weapon
                Item shield = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -10);
                if (shield != null && GameConstants.isTwoHanded(source.getItemId()) && !GameConstants.isMercedes(chr.getJob())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                        c.sendPacket(InventoryPacket.getInventoryFull());
                        c.sendPacket(InventoryPacket.getShowInventoryFull());
                        return;
                    }
                    if (shield.getItemId() / 1000 != 1352) {
                        if (shield.getItemId() / 1000 != 1099 && GameConstants.isDemon(chr.getJob())) {
                            unequip(c, (byte) -10, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                        } else if (ItemConstants.武器類型(source.getItemId()) != MapleWeaponType.單手棍) {
                            unequip(c, (byte) -10, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                        }
                    }
                }
                break;
            }
        }
        source = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem(src); // Equip
        target = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst); // Currently equipping
        if (source == null) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        short flag = source.getFlag();
        if (stats.get("equipTradeBlock") != null || source.getItemId() / 10000 == 167) { // Block trade when equipped.
            if (!ItemFlag.UNTRADEABLE.check(flag)) {
                flag |= ItemFlag.UNTRADEABLE.getValue();
                source.setFlag(flag);
                c.sendPacket(InventoryPacket.updateSpecialItemUse_(source, MapleInventoryType.EQUIP.getType(), c.getPlayer()));
            }
        }
        if (source.getItemId() / 10000 == 166) {
            if (source.getAndroid() == null) {
                final int uid = MapleInventoryIdentifier.getInstance();
                source.setUniqueId(uid);
                source.setAndroid(MapleAndroid.create(source.getItemId(), uid));
                //flag |= ItemFlag.LOCK.getValue();
                flag |= ItemFlag.UNTRADEABLE.getValue();
                flag |= ItemFlag.ANDROID_ACTIVATED.getValue();
                source.setFlag(flag);
                c.sendPacket(InventoryPacket.updateSpecialItemUse_(source, MapleInventoryType.EQUIP.getType(), c.getPlayer()));
            }
            final Equip heart = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -33);
            if (heart != null) {
                chr.removeAndroid();
                chr.setAndroid(source.getAndroid());
            }
        } else if (chr.getAndroid() != null) {
            if (dst <= -1300) {
                chr.setAndroid(chr.getAndroid()); //respawn it
            } else if (dst <= -1200) {
                chr.updateAndroid(dst, source.getItemId());
            }
        }
        if (source.getCharmEXP() > 0 && !ItemFlag.CHARM_EQUIPPED.check(flag)) {
            chr.getTrait(MapleTraitType.charm).addExp(source.getCharmEXP(), chr);
            source.setCharmEXP((short) 0);
            flag |= ItemFlag.CHARM_EQUIPPED.getValue();
            source.setFlag(flag);
            c.sendPacket(InventoryPacket.updateSpecialItemUse_(source, GameConstants.getInventoryType(source.getItemId()).getType(), c.getPlayer()));
        }

        chr.getInventory(MapleInventoryType.EQUIP).removeSlot(src);
        if (target != null) {
            chr.getInventory(MapleInventoryType.EQUIPPED).removeSlot(dst);
        }
        source.setPosition(dst);
        chr.getInventory(MapleInventoryType.EQUIPPED).addFromDB(source);
        if (target != null) {
            target.setPosition(src);
            chr.getInventory(MapleInventoryType.EQUIP).addFromDB(target);
        }
        if (GameConstants.isWeapon(source.getItemId())) {
            c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.BOOSTER);
            c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.SPIRIT_CLAW);
            c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.SOULARROW);
            c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.WK_CHARGE);
            c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.LIGHTNING_CHARGE);
        }
        if (source.getItemId() / 10000 == 190 || source.getItemId() / 10000 == 191) {
            c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
            c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.MECH_CHANGE);
        } else if (GameConstants.isReverseItem(source.getItemId())) {
            chr.finishAchievement(9);
        } else if (GameConstants.isTimelessItem(source.getItemId())) {
            chr.finishAchievement(10);
        } else if (stats.containsKey("reqLevel") && stats.get("reqLevel") >= 140) {
            chr.finishAchievement(41);
        } else if (stats.containsKey("reqLevel") && stats.get("reqLevel") >= 130) {
            chr.finishAchievement(40);
        } else if (source.getItemId() == 1122017) {
            chr.startFairySchedule(true, true);
        }
        if (source.getState() >= 17) {
            final Map<Skill, SkillEntry> ss = new HashMap<>();
            int[] potentials = {source.getPotential1(), source.getPotential2(), source.getPotential3()};
            for (int i : potentials) {
                if (i > 0) {
                    StructItemOption pot;
                    if (ItemConstants.is例外裝備潛能(source.getItemId())) {
                        pot = ii.getPotentialInfo(i).get(ii.getReqLevel(source.getItemId()) / 12);
                    } else {
                        pot = ii.getPotentialInfo(i).get(ii.getReqLevel(source.getItemId()) / 10);
                    }
                    if (pot != null && pot.get("skillID") > 0) {
                        ss.put(SkillFactory.getSkill(PlayerStats.getSkillByJob(pot.get("skillID"), c.getPlayer().getJob())), new SkillEntry((byte) 1, (byte) 0, -1, -1));
                    }
                }
            }
            c.getPlayer().changeSkillLevel_Skip(ss, true);
        }
        if (source.getSocketState() > 15) {
            final Map<Skill, SkillEntry> ss = new HashMap<>();
            int[] sockets = {source.getSocket1(), source.getSocket2(), source.getSocket3()};
            for (int i : sockets) {
                if (i > 0) {
                    StructItemOption soc = ii.getSocketInfo(i);
                    if (soc != null && soc.get("skillID") > 0) {
                        ss.put(SkillFactory.getSkill(PlayerStats.getSkillByJob(soc.get("skillID"), c.getPlayer().getJob())), new SkillEntry((byte) 1, (byte) 0, -1, -1));
                    }
                }
            }
            c.getPlayer().changeSkillLevel_Skip(ss, true);
        }
        c.sendPacket(InventoryPacket.moveInventoryItem(MapleInventoryType.EQUIP, src, dst, (byte) 2, false, false));
        boolean ultimate_explorer = c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(GameConstants.ULT_EXPLORER)) != null;
        int reqlv = MapleItemInformationProvider.getInstance().getReqLevel(source.getItemId());
        if (reqlv > c.getPlayer().getLevel() && !c.getPlayer().isGM() && !ultimate_explorer) {
            FileoutputUtil.logToFile("logs/hack/Ban/修改封包.txt", "\r\n" + FileoutputUtil.NowTime() + " 玩家：" + c.getPlayer().getName() + "(" + c.getPlayer().getId() + ") <等級: " + c.getPlayer().getLevel() + " > 修改裝備(" + source.getItemId() + ")封包，穿上裝備時封鎖。 該裝備需求等級: " + reqlv);
            //World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6,"[封鎖系統] " + c.getPlayer().getName() + " 因為修改封包而被管理員永久停權。"));
            World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, "[GM密語]  " + c.getPlayer().getName() + "(" + c.getPlayer().getId() + ") <等級: " + c.getPlayer().getLevel() + " > 修改裝備(" + source.getItemId() + ")封包，穿上裝備時封鎖。 該裝備需求等級: " + reqlv));
            //c.getPlayer().ban("修改封包", true, true, false);
            //c.getSession().close();
        }
        chr.equipChanged();
    }

    public static void unequip(final MapleClient c, final short src, final short dst) {
        Equip source = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(src);
        Equip target = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(dst);

        if (dst < 0 || source == null || src == -55) {
            return;
        }
        c.getPlayer().expirationTask(false, true);
        if (target != null && src <= 0) { // do not allow switching with equip
            c.sendPacket(InventoryPacket.getInventoryFull());
            return;
        }
        if (c.getPlayer().getDebugMessage()) {
            c.getPlayer().dropMessage("脫裝備: src : " + src + " dst : " + dst + " 代碼：" + source.getItemId() + " 唯一ID: " + source.getEquipOnlyId());
        }
        c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).removeSlot(src);
        if (target != null) {
            c.getPlayer().getInventory(MapleInventoryType.EQUIP).removeSlot(dst);
        }
        source.setPosition(dst);
        c.getPlayer().getInventory(MapleInventoryType.EQUIP).addFromDB(source);
        if (target != null) {
            target.setPosition(src);
            c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).addFromDB(target);
        }

        if (GameConstants.isWeapon(source.getItemId())) {
            c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.BOOSTER);
            c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.SPIRIT_CLAW);
            c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.SOULARROW);
            c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.WK_CHARGE);
            c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.LIGHTNING_CHARGE);
        } else if (source.getItemId() / 10000 == 190 || source.getItemId() / 10000 == 191) {
            c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
            c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.MECH_CHANGE);
        } else if (source.getItemId() / 10000 == 166 || src == -33) {
            c.getPlayer().removeAndroid();
            c.sendPacket(CField.removeAndroidHeart());
        } else if (c.getPlayer().getAndroid() != null) {
            if (src <= -1300) {
                c.getPlayer().setAndroid(c.getPlayer().getAndroid());
            } else if (src <= -1200) {
                c.getPlayer().updateAndroid(src, 0);
            }
        } else if (source.getItemId() == 1122017) {
            c.getPlayer().cancelFairySchedule(true);
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (source.getState() >= 17) {
            final Map<Skill, SkillEntry> ss = new HashMap<>();
            int[] potentials = {source.getPotential1(), source.getPotential2(), source.getPotential3()};
            for (int i : potentials) {
                if (i > 0) {
                    StructItemOption pot;
                    if (ItemConstants.is例外裝備潛能(source.getItemId())) {
                        pot = ii.getPotentialInfo(i).get(ii.getReqLevel(source.getItemId()) / 12);
                    } else {
                        pot = ii.getPotentialInfo(i).get(ii.getReqLevel(source.getItemId()) / 10);
                    }
                    if (pot != null && pot.get("skillID") > 0) {
                        ss.put(SkillFactory.getSkill(PlayerStats.getSkillByJob(pot.get("skillID"), c.getPlayer().getJob())), new SkillEntry((byte) -1, (byte) -1, -1, -1));
                    }
                }
            }
            c.getPlayer().changeSkillLevel_Skip(ss, true);
        }
        if (source.getSocketState() > 15) {
            final Map<Skill, SkillEntry> ss = new HashMap<>();
            int[] sockets = {source.getSocket1(), source.getSocket2(), source.getSocket3()};
            for (int i : sockets) {
                if (i > 0) {
                    StructItemOption soc = ii.getSocketInfo(i);
                    if (soc != null && soc.get("skillID") > 0) {
                        ss.put(SkillFactory.getSkill(PlayerStats.getSkillByJob(soc.get("skillID"), c.getPlayer().getJob())), new SkillEntry((byte) 1, (byte) 0, -1, -1));
                    }
                }
            }
            c.getPlayer().changeSkillLevel_Skip(ss, true);
        }
        c.sendPacket(InventoryPacket.moveInventoryItem(MapleInventoryType.EQUIP, src, dst, (byte) 1, false, false));
        /*int reqlv = MapleItemInformationProvider.getInstance().getReqLevel(source.getItemId());
        if (reqlv > c.getPlayer().getLevel() && !c.getPlayer().isGM()) {
            FileoutputUtil.logToFile("logs/hack/Ban/修改封包.txt", "\r\n" + FileoutputUtil.NowTime() + " 玩家：" + c.getPlayer().getName() + " <等級: " + c.getPlayer().getLevel() + " > 修改裝備(" + source.getItemId() + ")封包，脫除裝備時封鎖。 該裝備需求等級: " + reqlv);
            //World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6,"[封鎖系統] " + c.getPlayer().getName() + " 因為修改封包而被管理員永久停權。"));
            World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6,"[GM密語]  " + c.getPlayer().getName() + "(" + c.getPlayer().getId() + ") <等級: " + c.getPlayer().getLevel() + " > 修改裝備(" + source.getItemId() + ")封包，脫除裝備時封鎖。 該裝備需求等級: " + reqlv));
            //c.getPlayer().ban("修改封包", true, true, false);
            //c.getSession().close();
        }*/
        c.getPlayer().equipChanged();
    }

    public static boolean drop(final MapleClient c, MapleInventoryType type, final short src, final short quantity) {
        return drop(c, type, src, quantity, false);
    }

    public static boolean drop(final MapleClient c, MapleInventoryType type, final short src, short quantity, final boolean npcInduced) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ServerConstants.isShutdown) {
            c.getPlayer().dropMessage(1, "目前無法丟落物品。");
            c.sendPacket(CWvsContext.enableActions());
            return false;
        }
        if (src < 0) {
            type = MapleInventoryType.EQUIPPED;
        }
        if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
            return false;
        }
        final Item source = c.getPlayer().getInventory(type).getItem(src);
        if (quantity < 0 || source == null || src == -55 || (!npcInduced && GameConstants.isPet(source.getItemId())) || (quantity == 0 && !GameConstants.isRechargable(source.getItemId())) || c.getPlayer().inPVP()) {
            c.sendPacket(CWvsContext.enableActions());
            return false;
        }

        if (GameConstants.notdrop(source.getItemId()) || ("Donor".equals(source.getOwner())) || ("贊助專屬".equals(source.getOwner()))) {
            c.getPlayer().dropMessage(1, "該東西無法丟棄。");
            c.sendPacket(CWvsContext.enableActions());
            return false;
        }

        final short flag = source.getFlag();
        if (quantity > source.getQuantity() && !GameConstants.isRechargable(source.getItemId())) {
            c.sendPacket(CWvsContext.enableActions());
            return false;
        }
        if (ItemFlag.LOCK.check(flag) || (quantity != 1 && type == MapleInventoryType.EQUIP)) { // hack
            c.sendPacket(CWvsContext.enableActions());
            return false;
        }
        if (c.getPlayer().getDebugMessage()) {
            c.getPlayer().dropMessage("丟棄物品 欄位:" + type + " 物品代碼：" + source.getItemId() + " 物品數量: " + quantity + " 唯一ID: " + source.getEquipOnlyId());
        }
        final Point dropPos = new Point(c.getPlayer().getPosition());
//        c.getPlayer().getCheatTracker().checkDrop();
        if (quantity < source.getQuantity() && !GameConstants.isRechargable(source.getItemId())) {
            final Item target = source.copy();
            target.setQuantity(quantity);
            source.setQuantity((short) (source.getQuantity() - quantity));
            c.sendPacket(InventoryPacket.dropInventoryItemUpdate(type, source));

            if (ii.isDropRestricted(target.getItemId()) || ii.isAccountShared(target.getItemId())) {
                if (ItemFlag.KARMA_EQ.check(flag)) {
                    target.setFlag((byte) (flag - ItemFlag.KARMA_EQ.getValue()));
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true);
                } else if (ItemFlag.KARMA_USE.check(flag)) {
                    target.setFlag((byte) (flag - ItemFlag.KARMA_USE.getValue()));
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true);
                } else {
                    c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos);
                }
            } else {
                if (GameConstants.isPet(source.getItemId()) || ItemFlag.UNTRADEABLE.check(flag)) {
                    c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos);
                } else {
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true);
                }
            }
        } else {
            c.getPlayer().getInventory(type).removeSlot(src);
            if (GameConstants.isHarvesting(source.getItemId())) {
                c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
            }
            c.sendPacket(InventoryPacket.dropInventoryItem((src < 0 ? MapleInventoryType.EQUIP : type), src));
            if (src < 0) {
                c.getPlayer().equipChanged();
            }
            if (ii.isDropRestricted(source.getItemId()) || ii.isAccountShared(source.getItemId())) {
                if (ItemFlag.KARMA_EQ.check(flag)) {
                    source.setFlag((byte) (flag - ItemFlag.KARMA_EQ.getValue()));
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true);
                } else if (ItemFlag.KARMA_USE.check(flag)) {
                    source.setFlag((byte) (flag - ItemFlag.KARMA_USE.getValue()));
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true);
                } else {
                    c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos);
                }
            } else {
                if (GameConstants.isPet(source.getItemId()) || ItemFlag.UNTRADEABLE.check(flag)) {
                    c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos);
                } else {
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true);
                }
            }
        }
        if (ServerConstants.log_drop && (source.getEquipOnlyId() > 0 || (GameConstants.isAllScroll(source.getItemId()) || GameConstants.isOpScroll(source.getItemId())))) {
            FileoutputUtil.logToFile("logs/data/丟東西.txt", FileoutputUtil.NowTime() + "角色名字:" + c.getPlayer().getName() + " 從身上丟出了: " + MapleItemInformationProvider.getInstance().getName(source.getItemId()) + "(" + source.getItemId() + ") 數量:" + quantity + " 道具唯一ID: " + source.getEquipOnlyId() + " \r\n");
        }
        return true;
    }

    public static void removeAllByInventoryId(MapleClient c, long inventoryitemid) {
        if (c.getPlayer() == null) {
            return;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Item copyEquipItems = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItemByInventoryItemId(inventoryitemid);
        if (copyEquipItems != null) {
            removeFromSlot(c, MapleInventoryType.EQUIP, copyEquipItems.getPosition(), copyEquipItems.getQuantity(), true, false);
            String msgtext = "玩家" + c.getPlayer().getName() + " ID: " + c.getPlayer().getId() + " (等級" + c.getPlayer().getLevel() + ") 地圖: " + c.getPlayer().getMapId() + " 在玩家背包中發現複製裝備[" + ii.getName(copyEquipItems.getItemId()) + "]已經將其刪除。";
            World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, "[GM密語] " + msgtext));
            FileoutputUtil.logToFile("logs/hack/複製裝備_已刪除.txt", FileoutputUtil.CurrentReadable_Time() + " " + msgtext + " 道具唯一ID: " + copyEquipItems.getEquipOnlyId() + "\r\n");
        }
        Item copyEquipedItems = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItemByInventoryItemId(inventoryitemid);
        if (copyEquipedItems != null) {
            removeFromSlot(c, MapleInventoryType.EQUIPPED, copyEquipedItems.getPosition(), copyEquipedItems.getQuantity(), true, false);
            String msgtext = "玩家" + c.getPlayer().getName() + " ID: " + c.getPlayer().getId() + " (等級" + c.getPlayer().getLevel() + ") 地圖: " + c.getPlayer().getMapId() + " 在玩家穿戴中發現複製裝備[" + ii.getName(copyEquipedItems.getItemId()) + "]已經將其刪除。";
            World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, "[GM密語] " + msgtext));
            FileoutputUtil.logToFile("logs/hack/複製裝備_已刪除.txt", FileoutputUtil.CurrentReadable_Time() + " " + msgtext + " 道具唯一ID: " + copyEquipedItems.getEquipOnlyId() + "\r\n");
        }
        for (Item copyStorageItem : c.getPlayer().getStorage().getItems()) {
            if (copyStorageItem != null) {
                if (c.getPlayer().getStorage().removeItemByInventoryItemId(inventoryitemid)) {
                    String msgtext = "玩家" + c.getPlayer().getName() + " ID: " + c.getPlayer().getId() + " (等級" + c.getPlayer().getLevel() + ") 地圖: " + c.getPlayer().getMapId() + " 在玩家倉庫中發現複製裝備[" + ii.getName(copyStorageItem.getItemId()) + "]已經將其刪除。";
                    World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, "[GM密語] " + msgtext));
                    FileoutputUtil.logToFile("logs/hack/複製裝備_已刪除.txt", FileoutputUtil.CurrentReadable_Time() + " " + msgtext + " 道具唯一ID: " + copyStorageItem.getEquipOnlyId() + "\r\n");
                }
            }
        }
    }
}
