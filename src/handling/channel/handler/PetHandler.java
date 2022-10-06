package handling.channel.handler;

import java.util.List;

import client.inventory.*;
import client.MapleClient;
import client.MapleCharacter;
import client.MapleDisease;
import client.Skill;
import client.SkillFactory;
import constants.GameConstants;
import handling.world.MaplePartyCharacter;

import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;

import server.Randomizer;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.life.MapleMonster;
import server.movement.LifeMovementFragment;
import server.maps.FieldLimitType;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.packet.PetPacket;
import tools.data.LittleEndianAccessor;
import tools.packet.CField.EffectPacket;
import tools.packet.CWvsContext;

public class PetHandler {

    public static final void SpawnPet(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        chr.updateTick(slea.readInt());
        chr.spawnPet(slea.readByte(), slea.readByte() > 0);
    }

    public static final void Pet_AutoBuff(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        int petid = slea.readInt();
        MaplePet pet = chr.getSummonedPet(petid);
        if (chr == null || chr.getMap() == null || pet == null) {
            return;
        }
        int skillId = slea.readInt();
        Skill skill = SkillFactory.getSkill(skillId);
        if (chr.getSkillLevel(skill) > 0 || skillId == 0) {
            pet.setSkillId(skillId);
            pet.setChanged(true);
            for (final MaplePet p : chr.getSummonedPets()) {
                c.sendPacket(PetPacket.updatePet(p, chr.getInventory(MapleInventoryType.CASH).getItem((short) (byte) p.getInventoryPosition()), true));
                c.sendPacket(PetPacket.showPet(chr, p, false, false));
                c.sendPacket(PetPacket.showPetUpdate(chr, p.getUniqueId(), (byte) (p.getSummonedValue() - 1)));
//                client.getSession().writeAndFlush(PetPacket.petStatUpdate(this));
            }
        }
        c.sendPacket(CWvsContext.enableActions());
    }

    public static final void Pet_AutoPotion(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        slea.skip(1);
        slea.skip(4);
        //chr.updateTick(slea.readInt());
        final short slot = slea.readShort();
        if (chr == null || !chr.isAlive() || chr.getMapId() == 749040100 || chr.getMap() == null || chr.hasDisease(MapleDisease.POTION)) {
            return;
        }
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != slea.readInt()) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        final long time = System.currentTimeMillis();
        if (chr.getNextConsume() > time) {
            chr.dropMessage(5, "您不可以使用此道具。");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (!FieldLimitType.PotionUse.check(chr.getMap().getFieldLimit())) { //cwk quick hack
            if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyTo(chr)) {
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
                if (chr.getMap().getConsumeItemCoolTime() > 0) {
                    chr.setNextConsume(time + (chr.getMap().getConsumeItemCoolTime() * 1000));
                }
            }
        } else {
            c.sendPacket(CWvsContext.enableActions());
        }
    }

    public static final void PetChat(final int petid, final short command, final String text, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null || chr.getSummonedPet(petid) == null) {
            return;
        }
        chr.getMap().broadcastMessage(chr, PetPacket.petChat(chr.getId(), command, text, (byte) petid), true);
    }

    public static final void PetCommand(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        int petId = slea.readInt();
        MaplePet pet = null;
        pet = chr.getSummonedPet((byte) petId);
        slea.readByte(); //always 0?
        if (pet == null) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        byte command = slea.readByte();
        PetCommand petCommand = PetDataFactory.getPetCommand(pet.getPetItemId(), command);
        if (petCommand == null) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        byte petIndex = chr.getPetIndex(pet);
        boolean success = false;
        if (Randomizer.nextInt(99) <= petCommand.getProbability()) {
            success = true;
            if (pet.getCloseness() < 30000) {
                int newCloseness = pet.getCloseness() + (petCommand.getIncrease() * c.getChannelServer().getTraitRate());
                if (newCloseness > 30000) {
                    newCloseness = 30000;
                }
                pet.setCloseness(newCloseness);
                if (newCloseness >= GameConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                    pet.setLevel(pet.getLevel() + 1);
                    c.sendPacket(EffectPacket.showOwnPetLevelUp(petIndex));
                    chr.getMap().broadcastMessage(PetPacket.showPetLevelUp(chr, petIndex));
                }
                c.sendPacket(PetPacket.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()), false));
            }
        }
        chr.getMap().broadcastMessage(PetPacket.commandResponse(chr.getId(), (byte) petCommand.getSkillId(), petIndex, success, false));
    }

    public static final void PetFood(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        int previousFullness = 100;
        MaplePet pet = null;
        if (chr == null) {
            return;
        }
        for (final MaplePet pets : chr.getPets()) {
            if (pets.getSummoned()) {
                if (pets.getFullness() < previousFullness) {
                    previousFullness = pets.getFullness();
                    pet = pets;
                }
            }
        }
        if (pet == null) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        //c.getPlayer().updateTick(slea.readInt());
        slea.skip(4);
        short slot = slea.readShort();
        final int itemId = slea.readInt();
        Item petFood = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
        if (petFood == null || petFood.getItemId() != itemId || petFood.getQuantity() <= 0 || itemId / 10000 != 212) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        boolean gainCloseness = false;

        if (Randomizer.nextInt(99) <= 50) {
            gainCloseness = true;
        }
        if (pet.getFullness() < 100) {
            int newFullness = pet.getFullness() + 30;
            if (newFullness > 100) {
                newFullness = 100;
            }
            pet.setFullness(newFullness);
            final byte index = chr.getPetIndex(pet);

            if (gainCloseness && pet.getCloseness() < 30000) {
                int newCloseness = pet.getCloseness() + 1;
                if (newCloseness > 30000) {
                    newCloseness = 30000;
                }
                pet.setCloseness(newCloseness);
                if (newCloseness >= GameConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                    pet.setLevel(pet.getLevel() + 1);

                    c.sendPacket(EffectPacket.showOwnPetLevelUp(index));
                    chr.getMap().broadcastMessage(PetPacket.showPetLevelUp(chr, index));
                }
            }
            c.sendPacket(PetPacket.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()), false));
            chr.getMap().broadcastMessage(c.getPlayer(), PetPacket.commandResponse(chr.getId(), (byte) 1, index, true, true), true);
        } else {
            if (gainCloseness) {
                int newCloseness = pet.getCloseness() - 1;
                if (newCloseness < 0) {
                    newCloseness = 0;
                }
                pet.setCloseness(newCloseness);
                if (newCloseness < GameConstants.getClosenessNeededForLevel(pet.getLevel())) {
                    pet.setLevel(pet.getLevel() - 1);
                }
            }
            c.sendPacket(PetPacket.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()), false));
            chr.getMap().broadcastMessage(chr, PetPacket.commandResponse(chr.getId(), (byte) 1, chr.getPetIndex(pet), false, true), true);
        }
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, true, false);
        c.sendPacket(CWvsContext.enableActions());
    }

    public static final void MovePet(final LittleEndianAccessor slea, final MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        final int index = slea.readInt();
        slea.skip(9); // int(pos), int
        final List<LifeMovementFragment> res = MovementParse.parseMovement(slea, 3);
        if (res != null && !res.isEmpty() && chr.getMap() != null) { // map crash hack
            final MaplePet pet = chr.getSummonedPet(index);
            if (pet == null) {
                return;
            }
            pet.updatePosition(res);
            Point p = pet.getPos();
            chr.getMap().broadcastMessage(chr, PetPacket.movePet(chr.getId(), index, (byte) index, p, res), false);
            if (chr.hasBlockedInventory() || chr.getStat().pickupRange <= 0.0 || chr.inPVP()) {
                return;
            }
            chr.setScrolledPosition((short) 0);
            List<MapleMapObject> objects = chr.getMap().getMapObjectsInRange(chr.getTruePosition(), chr.getRange(), Arrays.asList(MapleMapObjectType.ITEM));
            for (LifeMovementFragment move : res) {
                final Point pp = move.getPosition();
                boolean foundItem = false;
                for (MapleMapObject mapitemz : objects) {
                    if (mapitemz instanceof MapleMapItem && (Math.abs(pp.x - mapitemz.getTruePosition().x) <= chr.getStat().pickupRange || Math.abs(mapitemz.getTruePosition().x - pp.x) <= chr.getStat().pickupRange) && (Math.abs(pp.y - mapitemz.getTruePosition().y) <= chr.getStat().pickupRange || Math.abs(mapitemz.getTruePosition().y - pp.y) <= chr.getStat().pickupRange)) {
                        final MapleMapItem mapitem = (MapleMapItem) mapitemz;
                        final Lock lock = mapitem.getLock();
                        lock.lock();
                        try {
                            if (mapitem.isPickedUp()) {
                                continue;
                            }
                            if (mapitem.getQuest() > 0 && chr.getQuestStatus(mapitem.getQuest()) != 1) {
                                continue;
                            }
                            if (mapitem.getOwner() != chr.getId() && mapitem.isPlayerDrop()) {
                                continue;
                            }
                            if (mapitem.getOwner() != chr.getId() && ((!mapitem.isPlayerDrop() && mapitem.getDropType() == 0) || (mapitem.isPlayerDrop() && chr.getMap().getEverlast()))) {
                                continue;
                            }
                            if (!mapitem.isPlayerDrop() && (mapitem.getDropType() == 1 || mapitem.getDropType() == 3) && mapitem.getOwner() != chr.getId()) {
                                continue;
                            }
                            if (mapitem.getDropType() == 2 && mapitem.getOwner() != chr.getId()) {
                                continue;
                            }
                            if (mapitem.getMeso() > 0) {
                                if (chr.getParty() != null && mapitem.getOwner() != chr.getId()) {
                                    final List<MapleCharacter> toGive = new LinkedList<>();
                                    final int splitMeso = mapitem.getMeso() * 40 / 100;
                                    for (MaplePartyCharacter z : chr.getParty().getMembers()) {
                                        MapleCharacter m = chr.getMap().getCharacterById(z.getId());
                                        if (m != null && m.getId() != chr.getId()) {
                                            toGive.add(m);
                                        }
                                    }
                                    for (final MapleCharacter m : toGive) {
                                        m.gainMeso(splitMeso / toGive.size() + (m.getStat().hasPartyBonus ? (int) (mapitem.getMeso() / 20.0) : 0), true, true);
                                    }
                                    chr.gainMeso(mapitem.getMeso() - splitMeso, true, false);
                                } else {
                                    chr.gainMeso(mapitem.getMeso(), true, false);
                                }
                                InventoryHandler.removeItem_Pet(chr, mapitem, index);
                                foundItem = true;
                            } else if (!MapleItemInformationProvider.getInstance().isPickupBlocked(mapitem.getItem().getItemId()) && mapitem.getItem().getItemId() / 10000 != 291) {
                                if (InventoryHandler.useItem(chr.getClient(), mapitem.getItemId())) {
                                    InventoryHandler.removeItem_Pet(chr, mapitem, index);
                                } else if (MapleInventoryManipulator.checkSpace(chr.getClient(), mapitem.getItem().getItemId(), mapitem.getItem().getQuantity(), mapitem.getItem().getOwner())) {
                                    if (mapitem.getItem().getQuantity() >= 50 && mapitem.getItem().getItemId() == 2340000) {
                                        chr.getClient().setMonitored(true); //hack check
                                    }
                                    if (MapleInventoryManipulator.addFromDrop(chr.getClient(), mapitem.getItem(), true, mapitem.getDropper() instanceof MapleMonster, false)) {
                                        InventoryHandler.removeItem_Pet(chr, mapitem, index);
                                        foundItem = true;
                                    }
                                }
                            }
                        } finally {
                            lock.unlock();
                        }
                    }
                }
                if (foundItem) {
                    return;
                }
            }
        }
    }

    public static void Pet_LootToggle(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        //System.out.println(slea);
        if (chr == null || chr.getMap() == null) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }

        slea.skip(4);
        short data = slea.readShort();
        
        for (final MaplePet p : chr.getSummonedPets()) {
            p.setCanPickup((data > 0 ? 0 : (short) 1));
            p.setChanged(true);
            c.sendPacket(PetPacket.updatePet(p, chr.getInventory(MapleInventoryType.CASH).getItem((short) (byte) p.getInventoryPosition()), true));
            c.sendPacket(PetPacket.showPet(chr, p, false, false));
            c.sendPacket(PetPacket.showPetUpdate(chr, p.getUniqueId(), (byte) (p.getSummonedValue() - 1)));
        }
        chr.dropMessage(5, "寵物撿取道具技能已經" + (data > 0 ? "開啟了" : "關閉了") + "。");
    }
}
