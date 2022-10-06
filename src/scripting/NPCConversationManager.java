/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package scripting;

import client.*;

import java.awt.*;
import java.lang.ref.WeakReference;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

import client.inventory.Equip;
import client.inventory.Item;
import client.messages.CommandProcessor;
import constants.GameConstants;
import client.inventory.ItemFlag;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.BattleConstants;
import constants.BattleConstants.MobExp;
import constants.BattleConstants.PokedexEntry;
import constants.ServerConstants;
import server.*;
import server.Timer;
import server.life.*;
import server.maps.*;
import server.quest.MapleQuest;
import tools.*;
import tools.packet.CField;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import database.DatabaseConnection;
import extensions.temporary.InGameDirectionEventOpcode;
import handling.channel.handler.HiredMerchantHandler;
import handling.channel.handler.PlayersHandler;
import handling.login.LoginInformationProvider;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.World;
import handling.world.exped.ExpeditionType;
import handling.world.guild.MapleGuild;

import handling.world.guild.MapleGuildAlliance;

import java.sql.ResultSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.script.Invocable;

import server.Gachapon.MapleGachapon;
import server.Gachapon.MapleGachaponItem;
import server.RankingWorker.PokebattleInformation;
import server.RankingWorker.PokedexInformation;
import server.RankingWorker.PokemonInformation;
import server.Timer.CloneTimer;
import tools.packet.CField.NPCPacket;
import tools.packet.CField.UIPacket;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.GuildPacket;
import tools.packet.CWvsContext.InfoPacket;

public class NPCConversationManager extends AbstractPlayerInteraction {

    public static final Map<Pair<Integer, MapleClient>, MapleNPC> npcRequestController = new HashMap<>();

    private String getText;
    private byte type; // -1 = NPC, 0 = start quest, 1 = end quest
    private byte lastMsg = -1;
    public boolean pendingDisposal = false;
    private Invocable iv;
    private int mode = -1;

    public NPCConversationManager(MapleClient c, Invocable iv, String script, int npc, int mode, int questid, byte type) {
        super(c, script, npc, mode, questid, type);
        this.mode = mode;
        this.type = type;
        this.iv = iv;
        if (c.getPlayer() != null) {
            c.getPlayer().setNpcNow(npc);
        }
    }

    public Invocable getIv() {
        return iv;
    }

    public int getNpc() {
        return id;
    }

    public int getQuest() {
        return quest;
    }

    public byte getType() {
        return type;
    }

    public void safeDispose() {
        pendingDisposal = true;
    }

    public void dispose() {
        NPCScriptManager.getInstance().dispose(c);
    }

    public void askMapSelection(final String sel) {
        if (lastMsg > -1) {
            return;
        }
        c.sendPacket(NPCPacket.getMapSelection(id, sel));
        lastMsg = (byte) 0x10;
    }

    public void askBuffSelection(final String text) {
        if (lastMsg > -1) {
            return;
        }
        c.sendPacket(NPCPacket.getBuffSelection(id, text));
        lastMsg = 16;
    }

    public void say(String sMsg) {
        say(sMsg, false, false);
    }

    public void say(String sMsg, boolean prev, boolean next) {
        say(0, sMsg, prev, next);
    }

    public void say(int bParam, String sMsg, boolean prev, boolean next) {
        say(id, bParam, sMsg, prev, next);
    }

    public void say(int nSpeakerTemplateID, int bParam, String sMsg, boolean prev, boolean next) {
        say(nSpeakerTemplateID, -1, bParam, sMsg, prev, next);
    }

    public void say(int nSpeakerTemplateID, int nAnotherSpeakerTemplateID, int bParam, String sMsg, boolean prev, boolean next) {
        say(nSpeakerTemplateID, nAnotherSpeakerTemplateID, -1, bParam, sMsg, prev, next);
    }

    public void say(int nSpeakerTemplateID, int nAnotherSpeakerTemplateID, int nOtherSpeakerTemplateID, int bParam, String sMsg, boolean prev, boolean next) {
        say(4, nSpeakerTemplateID, nAnotherSpeakerTemplateID, nOtherSpeakerTemplateID, bParam, 0, sMsg, prev, next, 0);
    }

    public void say(int nSpeakerTypeID, int nSpeakerTemplateID, int nAnotherSpeakerTemplateID, int nOtherSpeakerTemplateID, int bParam, int eColor, String sMsg, boolean prev, boolean next, int tWait) {
        if (sMsg.contains("#L")) {
            askMenu(nSpeakerTypeID, nSpeakerTemplateID, nAnotherSpeakerTemplateID, nOtherSpeakerTemplateID, bParam, eColor, sMsg);
            return;
        }
        lastMsg = 0;
        c.getSession().writeAndFlush(NPCPacket.OnScriptMessage(nSpeakerTypeID, nSpeakerTemplateID, nAnotherSpeakerTemplateID, nOtherSpeakerTemplateID, lastMsg, bParam, eColor, new String[]{sMsg}, new int[]{prev ? 1 : 0, next ? 1 : 0, tWait}, null, null));
    }

    public void askYesNo(String sMsg) {
        askYesNo(0, sMsg);
    }

    public void askYesNo(int bParam, String sMsg) {
        askYesNo(id, bParam, sMsg);
    }

    public void askYesNo(int nSpeakerTemplateID, int bParam, String sMsg) {
        askYesNo(nSpeakerTemplateID, -1, bParam, sMsg);
    }

    public void askYesNo(int nSpeakerTemplateID, int nAnotherSpeakerTemplateID, int bParam, String sMsg) {
        askYesNo(nSpeakerTemplateID, nAnotherSpeakerTemplateID, -1, bParam, sMsg);
    }

    public void askYesNo(int nSpeakerTemplateID, int nAnotherSpeakerTemplateID, int nOtherSpeakerTemplateID, int bParam, String sMsg) {
        askYesNo(4, nSpeakerTemplateID, nAnotherSpeakerTemplateID, nOtherSpeakerTemplateID, bParam, 0, sMsg);
    }

    public void askYesNo(int nSpeakerTypeID, int nSpeakerTemplateID, int nAnotherSpeakerTemplateID, int nOtherSpeakerTemplateID, int bParam, int eColor, String sMsg) {
        if (sMsg.contains("#L")) {
            askMenu(nSpeakerTypeID, nSpeakerTemplateID, nAnotherSpeakerTemplateID, nOtherSpeakerTemplateID, bParam, eColor, sMsg);
            return;
        }
        lastMsg = 2;
        c.sendPacket(NPCPacket.getNPCTalk(id, (byte) 2, sMsg, "", (byte) 0));
    }

    public void askMenu(String sMsg) {
        askMenu(0, sMsg);
    }

    public void askMenu(int bParam, String sMsg) {
        askMenu(id, bParam, sMsg);
    }

    public void askMenu(int nSpeakerTemplateID, int bParam, String sMsg) {
        askMenu(nSpeakerTemplateID, -1, bParam, sMsg);
    }

    public void askMenu(int nSpeakerTemplateID, int nAnotherSpeakerTemplateID, int bParam, String sMsg) {
        askMenu(nSpeakerTemplateID, nAnotherSpeakerTemplateID, -1, bParam, sMsg);
    }

    public void askMenu(int nSpeakerTemplateID, int nAnotherSpeakerTemplateID, int nOtherSpeakerTemplateID, int bParam, String sMsg) {
        askMenu(4, nSpeakerTemplateID, nAnotherSpeakerTemplateID, nOtherSpeakerTemplateID, bParam, 0, sMsg);
    }

    public void askMenu(int nSpeakerTypeID, int nSpeakerTemplateID, int nAnotherSpeakerTemplateID, int nOtherSpeakerTemplateID, int bParam, int eColor, String sMsg) {
        if (!sMsg.contains("#L")) {
            say(nSpeakerTypeID, nSpeakerTemplateID, nAnotherSpeakerTemplateID, nOtherSpeakerTemplateID, bParam, eColor, sMsg, false, false, 0);
            return;
        }
        lastMsg = 5;
        c.sendPacket(NPCPacket.getNPCTalk(id, (byte) 5, sMsg, "", (byte) 0));
    }

    public void sendNext(String text) {
        sendNext(text, id);
    }

    public void sendNext(String text, int id) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { //sendNext will dc otherwise!
            sendSimple(text);
            return;
        }
        c.sendPacket(NPCPacket.getNPCTalk(id, (byte) 0, text, "00 01", (byte) 0));
        lastMsg = 0;
    }

    public void sendPlayerToNpc(String text) {
        sendNextS(text, (byte) 3, id);
    }

    public void sendNextNoESC(String text) {
        sendNextS(text, (byte) 1, id);
    }

    public void sendNextNoESC(String text, int id) {
        sendNextS(text, (byte) 1, id);
    }

    public void sendNextS(String text, byte type) {
        sendNextS(text, type, id);
    }

    public void sendNextS(String text, byte type, int idd) {
        sendNextS(text, type, idd, id);
    }

    public void sendNextS(String text, byte type, int idd, int npcid) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimpleS(text, type);
            return;
        }
        c.sendPacket(NPCPacket.getNPCTalk(id, (byte) 0, text, "00 01", type, idd));
        lastMsg = 0;
    }

    public void sendOthersTalk(String text, int npcid, boolean[] bottom) {
        sendOthersTalk(text, npcid, bottom, (byte) 1);
    }

    public void sendOthersTalk(String text, int npcid, boolean[] bottom, byte type) {
        String str = "";
        if (bottom.length >= 2) {
            for (int i = 0; i < 2; i++) {
                if (bottom[i]) {
                    str += "01";
                } else {
                    str += "00";
                }
                if (i < bottom.length - 1) {
                    str += " ";
                }
            }
        } else {
            str = "00 01";
        }
        if (text.contains("#L")) {
            lastMsg = 5;
            c.getSession().writeAndFlush(NPCPacket.getOthersTalk(id, lastMsg, npcid, text, "", type));
        } else {
            lastMsg = 0;
            c.getSession().writeAndFlush(NPCPacket.getOthersTalk(id, lastMsg, npcid, text, str, type));
        }
    }

    public void sendPrev(String text) {
        sendPrev(text, id);
    }

    public void sendPrev(String text, int id) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        c.sendPacket(NPCPacket.getNPCTalk(id, (byte) 0, text, "01 00", (byte) 0));
        lastMsg = 0;
    }

    public void getAdviceTalk(String[] wzinfo) {
        lastMsg = 1;
        c.getSession().writeAndFlush(NPCPacket.getAdviceTalk(wzinfo));
    }

    public void sendPrevS(String text, byte type) {
        sendPrevS(text, type, id);
    }

    public void sendPrevS(String text, byte type, int idd) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimpleS(text, type);
            return;
        }
        c.sendPacket(NPCPacket.getNPCTalk(id, (byte) 0, text, "01 00", type, idd));
        lastMsg = 0;
    }

    public void sendNextPrev(String text) {
        sendNextPrev(text, id);
    }

    public void sendNextPrev(String text, int id) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        c.sendPacket(NPCPacket.getNPCTalk(id, (byte) 0, text, "01 01", (byte) 0));
        lastMsg = 0;
    }

    public void PlayerToNpc(String text) {
        sendNextPrevS(text, (byte) 3);
    }

    public void sendNextPrevS(String text) {
        sendNextPrevS(text, (byte) 3);
    }

    public void sendNextPrevS(String text, byte type) {
        sendNextPrevS(text, type, id);
    }

    public void sendNextPrevS(String text, byte type, int idd) {
        sendNextPrevS(text, type, idd, id);
    }

    public void sendNextPrevS(String text, byte type, int idd, int npcid) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimpleS(text, type);
            return;
        }
        c.sendPacket(NPCPacket.getNPCTalk(id, (byte) 0, text, "01 01", type, idd));
        lastMsg = 0;
    }

    public void sendNextPrevNpcS(String text, byte type, int idd) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimpleS(text, type);
            return;
        }
        c.sendPacket(NPCPacket.getNPCTalk(idd, (byte) 0, text, "01 01", type, idd));
        lastMsg = 0;
    }

    public void sendOk(String text) {
        sendOk(text, id);
    }

    public void sendOk(String text, int id) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        c.sendPacket(NPCPacket.getNPCTalk(id, (byte) 0, text, "00 00", (byte) 0));
        lastMsg = 0;
    }

    public void sendOkS(String text, byte type) {
        sendOkS(text, type, id);
    }

    public void sendOkS(String text, byte type, int idd) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimpleS(text, type);
            return;
        }
        c.sendPacket(NPCPacket.getNPCTalk(id, (byte) 0, text, "00 00", type, idd));
        lastMsg = 0;
    }

    public void sendYesNo(String text) {
        sendYesNo(text, id);
    }

    public void sendYesNo(String text, int id) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        c.sendPacket(NPCPacket.getNPCTalk(id, (byte) 2, text, "", (byte) 0));
        lastMsg = 2;
    }

    public void sendYesNoS(String text, byte type) {
        sendYesNoS(text, type, id);
    }

    public void sendYesNoS(String text, byte type, int idd) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimpleS(text, type);
            return;
        }
        c.sendPacket(NPCPacket.getNPCTalk(id, (byte) 2, text, "", type, idd));
        lastMsg = 2;
    }

    public void sendAcceptDecline(String text) {
        askAcceptDecline(text);
    }

    public void sendAcceptDeclineNoESC(String text) {
        askAcceptDeclineNoESC(text);
    }

    public void askAcceptDecline(String text) {
        askAcceptDecline(text, id);
    }

    public void askAcceptDecline(String text, int id) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        lastMsg = (byte) 0xE;
        c.sendPacket(NPCPacket.getNPCTalk(id, (byte) lastMsg, text, "", (byte) 0));
    }

    public void askAcceptDeclineNoESC(String text) {
        askAcceptDeclineNoESC(text, id);
    }

    public void askAcceptDeclineNoESC(String text, int id) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        lastMsg = (byte) 0xE;
        c.sendPacket(NPCPacket.getNPCTalk(id, (byte) lastMsg, text, "", (byte) 1));
    }

    public void askAvatar(String text, int... args) {
        if (lastMsg > -1) {
            return;
        }
        c.sendPacket(NPCPacket.getNPCTalkStyle(id, text, args));
        lastMsg = 9;
    }

    public void sendSimple(String text) {
        sendSimple(text, id);
    }

    public void sendSimple(String text, int id) {
        if (lastMsg > -1) {
            return;
        }
        if (!text.contains("#L")) { //sendSimple will dc otherwise!
            sendNext(text);
            return;
        }
        c.sendPacket(NPCPacket.getNPCTalk(id, (byte) 5, text, "", (byte) 0));
        lastMsg = 5;
    }

    public void sendSimpleS(String text, byte type) {
        sendSimpleS(text, type, id);
    }

    public void sendSimpleS(String text, byte type, int idd) {
        if (lastMsg > -1) {
            return;
        }
        if (!text.contains("#L")) { //sendSimple will dc otherwise!
            sendNextS(text, type);
            return;
        }
        c.sendPacket(NPCPacket.getNPCTalk(id, (byte) 5, text, "", (byte) type, idd));
        lastMsg = 5;
    }

    public void sendStyle(String text, int styles[]) {
        if (lastMsg > -1) {
            return;
        }
        c.sendPacket(NPCPacket.getNPCTalkStyle(id, text, styles));
        lastMsg = 9;
    }

    public void sendAndroidStyle(String text, int styles[]) {
        if (lastMsg > -1) {
            return;
        }
        c.sendPacket(NPCPacket.getAndroidTalkStyle(id, text, styles));
        lastMsg = 10;
    }

    public void setAndroidHair(int hair) {
        getPlayer().getAndroid().setHair(hair);
        getPlayer().getAndroid().saveToDb();
        c.getPlayer().setAndroid(c.getPlayer().getAndroid());
    }

    public void setAndroidFace(int face) {
        getPlayer().getAndroid().setFace(face);
        getPlayer().getAndroid().saveToDb();
        c.getPlayer().setAndroid(c.getPlayer().getAndroid());
    }

    public void sendGetNumber(String text, int def, int min, int max) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        c.sendPacket(NPCPacket.getNPCTalkNum(id, text, def, min, max));
        lastMsg = 4;
    }

    public void sendGetText(String text) {
        sendGetText(text, id);
    }

    public void sendGetText(String text, int id) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        c.sendPacket(NPCPacket.getNPCTalkText(id, text));
        lastMsg = 3;
    }

    public void setGetText(String text) {
        this.getText = text;
    }

    public String getText() {
        return getText;
    }

    public void setHair(int hair) {
        getPlayer().setHair(hair);
        getPlayer().updateSingleStat(MapleStat.HAIR, hair);
        getPlayer().equipChanged();
    }

    public void setFace(int face) {
        getPlayer().setFace(face);
        getPlayer().updateSingleStat(MapleStat.FACE, face);
        getPlayer().equipChanged();
    }

    public void setSkin(int color) {
        getPlayer().setSkinColor((byte) color);
        getPlayer().updateSingleStat(MapleStat.SKIN, color);
        getPlayer().equipChanged();
    }

    public int setRandomAvatar(int ticket, int... args_all) {
        if (!haveItem(ticket)) {
            return -1;
        }
        gainItem(ticket, (short) -1);

        int args = args_all[Randomizer.nextInt(args_all.length)];
        if (args < 100) {
            c.getPlayer().setSkinColor((byte) args);
            c.getPlayer().updateSingleStat(MapleStat.SKIN, args);
        } else if (args < 30000) {
            c.getPlayer().setFace(args);
            c.getPlayer().updateSingleStat(MapleStat.FACE, args);
        } else {
            c.getPlayer().setHair(args);
            c.getPlayer().updateSingleStat(MapleStat.HAIR, args);
        }
        c.getPlayer().equipChanged();

        return 1;
    }

    public int setAvatar(int ticket, int args) {
        if (!haveItem(ticket)) {
            return -1;
        }
        gainItem(ticket, (short) -1);

        if (args < 100) {
            c.getPlayer().setSkinColor((byte) args);
            c.getPlayer().updateSingleStat(MapleStat.SKIN, args);
        } else if (args < 30000) {
            c.getPlayer().setFace(args);
            c.getPlayer().updateSingleStat(MapleStat.FACE, args);
        } else {
            c.getPlayer().setHair(args);
            c.getPlayer().updateSingleStat(MapleStat.HAIR, args);
        }
        c.getPlayer().equipChanged();

        return 1;
    }

    public void setMedalQuestHair(int hair) {
        getPlayer().setHair(hair);
        getPlayer().updateSingleStat(MapleStat.HAIR, hair);
        getPlayer().equipChanged();
    }

    public void sendStorage() {
        if (getPlayer().getMap() == null || getPlayer().getTrade() != null || !getPlayer().isAlive()) {
            c.getPlayer().dropMessage(1, "目前狀態無法執行本操作。");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (ServerConstants.isShutdown) {
            c.getPlayer().dropMessage(1, "伺服器即將關閉，無法執行本操作。");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        getPlayer().addStorageMsg(getPlayer().getName(), " 開啟倉庫\r\n");
        getPlayer().setConversation(4);
        getPlayer().setOperateStorage(true);
        getPlayer().getStorage().sendStorage(c, id);
    }

    public void openShop(int id) {
        MapleShopFactory.getInstance().getShop(id).sendShop(c);
    }

    public void openShopNPC(int id) {
        MapleShopFactory.getInstance().getShop(id).sendShop(c, this.id);
    }

    public int gainGachaponItem(int id, int quantity) {
        return gainGachaponItem(id, quantity, c.getPlayer().getMap().getStreetName());
    }

    public int gainGachaponItem(int id, int quantity, final String msg) {
        try {
            if (!MapleItemInformationProvider.getInstance().itemExists(id)) {
                return -1;
            }
            final Item item = MapleInventoryManipulator.addbyId_Gachapon(c, id, (short) quantity);

            if (item == null) {
                return -1;
            }
            final byte rareness = GameConstants.gachaponRareItem(item.getItemId());
            if (rareness > 0) {
                World.Broadcast.broadcastMessage(CWvsContext.getGachaponMega(c.getPlayer().getName(), item, false, c.getChannel()));
            }
            c.sendPacket(InfoPacket.getShowItemGain(item.getItemId(), (short) quantity, true));
            return item.getItemId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int useNebuliteGachapon() {
        try {
            if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1
                    || c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1
                    || c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < 1
                    || c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < 1
                    || c.getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 1) {
                return -1;
            }
            int grade = 0; // Default D
            final int chance = Randomizer.nextInt(100); // cannot gacha S, only from alien cube.
            if (chance < 1) { // Grade A
                grade = 3;
            } else if (chance < 5) { // Grade B
                grade = 2;
            } else if (chance < 35) { // Grade C
                grade = 1;
            } else { // grade == 0
                grade = Randomizer.nextInt(100) < 25 ? 5 : 0; // 25% again to get premium ticket piece
            }
            int newId = 0;
            if (grade == 5) {
                newId = 4420000;
            } else {
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final List<StructItemOption> pots = new LinkedList<>(ii.getAllSocketInfo(grade).values());
                while (newId == 0) {
                    StructItemOption pot = pots.get(Randomizer.nextInt(pots.size()));
                    if (pot != null) {
                        newId = pot.opID;
                    }
                }
            }
            final Item item = MapleInventoryManipulator.addbyId_Gachapon(c, newId, (short) 1);
            if (item == null) {
                return -1;
            }
            if (grade >= 2 && grade != 5) {
                World.Broadcast.broadcastMessage(CWvsContext.getGachaponMega(c.getPlayer().getName(), item, false, c.getChannel()));
            }
            c.sendPacket(InfoPacket.getShowItemGain(newId, (short) 1, true));
            gainItem(2430748, (short) 1);
            gainItemSilent(5220094, (short) -1);
            return item.getItemId();
        } catch (Exception e) {
            System.out.println("[Error] Failed to use Nebulite Gachapon. " + e);
        }
        return -1;
    }

    public void changeJob(int job) {
        c.getPlayer().changeJob(job);
    }

    public void startQuest(int idd) {
        MapleQuest.getInstance(idd).start(getPlayer(), id);
    }

    public void completeQuest(int idd) {
        MapleQuest.getInstance(idd).complete(getPlayer(), id);
    }

    public void forfeitQuest(int idd) {
        MapleQuest.getInstance(idd).forfeit(getPlayer());
    }

    public void forceStartQuest() {
        MapleQuest.getInstance(quest).forceStart(getPlayer(), getNpc(), null);
    }

    @Override
    public void forceStartQuest(int idd) {
        MapleQuest.getInstance(idd).forceStart(getPlayer(), getNpc(), null);
    }

    public void forceStartQuest(String customData) {
        MapleQuest.getInstance(quest).forceStart(getPlayer(), getNpc(), customData);
    }

    public void forceCompleteQuest() {
        MapleQuest.getInstance(quest).forceComplete(getPlayer(), getNpc());
    }

    @Override
    public void forceCompleteQuest(final int idd) {
        MapleQuest.getInstance(idd).forceComplete(getPlayer(), getNpc());
    }

    public String getQuestCustomData() {
        return c.getPlayer().getQuestNAdd(MapleQuest.getInstance(quest)).getCustomData();
    }

    public String getQuestCustomData(int quest) {
        return c.getPlayer().getQuestNAdd(MapleQuest.getInstance(quest)).getCustomData();
    }

    public void setQuestCustomData(String customData) {
        getPlayer().getQuestNAdd(MapleQuest.getInstance(quest)).setCustomData(customData);
    }

    public int getMeso() {
        return getPlayer().getMeso();
    }

    public void gainAp(final int amount) {
        c.getPlayer().gainAp((short) amount);
    }

    public void expandInventory(byte type, int amt) {
        c.getPlayer().expandInventory(type, amt);
    }

    public void unequipEverything() {
        MapleInventory equipped = getPlayer().getInventory(MapleInventoryType.EQUIPPED);
        MapleInventory equip = getPlayer().getInventory(MapleInventoryType.EQUIP);
        List<Short> ids = new LinkedList<>();
        for (Item item : equipped.newList()) {
            ids.add(item.getPosition());
        }
        for (short id : ids) {
            MapleInventoryManipulator.unequip(getC(), id, equip.getNextFreeSlot());
        }
    }

    public final void clearSkills() {
        getPlayer().clearSkills();
    }

    public boolean hasSkill(int skillid) {
        return c.getPlayer().hasSkill(skillid);
    }

    public void spawnNPCRequestController(int npcid, int x, int y) {
        spawnNPCRequestController(npcid, x, y, 0);
    }

    public void spawnNPCRequestController(int npcid, int x, int y, int f) {
        spawnNPCRequestController(npcid, x, y, f, npcid);
    }

    public void spawnNPCRequestController(int npcid, int x, int y, int f, int oid) {
        if (npcRequestController.containsKey(new Pair<>(oid, c))) {
            npcRequestController.remove(new Pair<>(oid, c));
        }
        MapleNPC npc;
        npc = c.getPlayer().getMap().getNPCById(npcid);
        if (npc == null) {
            npc = MapleLifeFactory.getNPC(npcid);
            if (npc == null) {
                return;
            }
            npc.setPosition(new Point(x, y));
            npc.setCy(y);
            npc.setRx0(x - 50);
            npc.setRx1(x + 50);
            npc.setF(f);
            MapleFoothold fh = c.getPlayer().getMap().getFootholds().findBelow(new Point(x, y), false);
            npc.setFh(fh == null ? 0 : fh.getId());
            npc.setCustom(true);
            npc.setObjectId(oid);
        }
        npcRequestController.put(new Pair<>(oid, c), npc);
        c.getSession().writeAndFlush(NPCPacket.spawnNPCRequestController(npc, true));// isMiniMap
        c.getSession().writeAndFlush(NPCPacket.setNPCSpecialAction(npc.getObjectId(), "summon", 0, false));
    }

    //    public void getNPCBubble(int npcid, String data, int exclamation, int bubbleType, int time, int directionTime) {
//        c.getSession().writeAndFlush(
//                CField.EffectPacket.showEffect(true, c.getPlayer(), UserEffectOpcode.UserEffect_SpeechBalloon,
//                        new int[]{exclamation, bubbleType, 0, time, 1, 0, 0, 0, 4, npcid}, new String[]{data},
//                        null, null));
//        if (directionTime > -1) {
//            exceTime(directionTime > 0 ? directionTime : time);
//        }
//    }
    public void setNPCSpecialAction(int npcid, String action) {
        setNPCSpecialAction(npcid, action, 0, false);
    }

    public void setNPCSpecialAction(int npcid, String action, int time, boolean unk) {
        setNPCSpecialAction(npcid, action, time, unk, -1);
    }

    public void setNPCSpecialAction(int npcid, String action, int time, boolean unk, int directionTime) {
        final MapleNPC npc;
        if (npcRequestController.containsKey(new Pair<>(npcid, c))) {
            npc = npcRequestController.get(new Pair<>(npcid, c));
        } else {
            return;
        }
        c.getSession().writeAndFlush(NPCPacket.setNPCSpecialAction(npc.getObjectId(), action, time, unk));
        if (directionTime > -1) {
            exceTime(directionTime > 0 ? directionTime : time);
        }
    }

    public void updateNPCSpecialAction(int oid, int value, int x, int y) {
        final MapleNPC npc;
        if (npcRequestController.containsKey(new Pair<>(oid, c))) {
            npc = npcRequestController.get(new Pair<>(oid, c));
        } else {
            return;
        }
        c.getSession().writeAndFlush(NPCPacket.NPCSpecialAction(npc.getObjectId(), value, x, y));
    }

    public void getNPCDirectionEffect(int npcid, String data, int value, int x, int y) {
        final MapleNPC npc;
        if (npcRequestController.containsKey(new Pair<>(npcid, c))) {
            npc = npcRequestController.get(new Pair<>(npcid, c));
        } else {
            return;
        }
        c.getSession().writeAndFlush(
                CField.UIPacket.getDirectionEffect(InGameDirectionEventOpcode.InGameDirectionEvent_EffectPlay, data,
                        new int[]{value, x, y, 1, 1, 0, npc.getObjectId(), 0}));
    }

    public void removeNPCRequestController(int oid) {
        final MapleNPC npc;
        if (npcRequestController.containsKey(new Pair<>(oid, c))) {
            npc = npcRequestController.get(new Pair<>(oid, c));
        } else {
            return;
        }
        c.getSession().writeAndFlush(NPCPacket.removeNPCController(npc.getObjectId()));
        c.getSession().writeAndFlush(NPCPacket.removeNPC(npc.getObjectId()));
        npcRequestController.remove(new Pair<>(oid, c));
    }

    //    public final void resetNPCController(final int npcId) {
//        final MapleNPC npc;
//        if (npcRequestController.containsKey(new Pair<>(npcId, c))) {
//            npc = npcRequestController.get(new Pair<>(npcId, c));
//        } else {
//            return;
//        }
//        c.getSession().writeAndFlush(NPCPacket.resetNPC(npc.getObjectId()));
//    }
    public void forcedAction(int[] values) {
        getDirectionEffect(InGameDirectionEventOpcode.InGameDirectionEvent_ForcedAction.getValue(), null, values);
    }

    public void exceTime(int time) {
        getDirectionEffect(InGameDirectionEventOpcode.InGameDirectionEvent_Delay.getValue(), null, new int[]{time});
    }

    public void getEventEffect(String data, int[] values) {
        getDirectionEffect(InGameDirectionEventOpcode.InGameDirectionEvent_EffectPlay.getValue(), data, values);
    }

    public void playerWaite() {
        getDirectionEffect(InGameDirectionEventOpcode.InGameDirectionEvent_ForcedInput.getValue(), null, new int[]{0});
    }

    public void playerMoveLeft() {
        getDirectionEffect(InGameDirectionEventOpcode.InGameDirectionEvent_ForcedInput.getValue(), null, new int[]{1});
    }

    public void playerMoveRight() {
        getDirectionEffect(InGameDirectionEventOpcode.InGameDirectionEvent_ForcedInput.getValue(), null, new int[]{2});
    }

    public void playerJump() {
        getDirectionEffect(InGameDirectionEventOpcode.InGameDirectionEvent_ForcedInput.getValue(), null, new int[]{3});
    }

    public void playerMoveDown() {
        getDirectionEffect(InGameDirectionEventOpcode.InGameDirectionEvent_ForcedInput.getValue(), null, new int[]{4});
    }

    public void forcedInput(int input) {
        getDirectionEffect(InGameDirectionEventOpcode.InGameDirectionEvent_ForcedInput.getValue(), null, new int[]{input});
    }

    public final void patternInput(String data, int[] values) {
        getDirectionEffect(InGameDirectionEventOpcode.InGameDirectionEvent_PatternInputRequest.getValue(), data, values);
    }

    public final void cameraMove(int[] values) {
        getDirectionEffect(InGameDirectionEventOpcode.InGameDirectionEvent_CameraMove.getValue(), null, values);
    }

    public final void cameraOnCharacter(int value) {
        getDirectionEffect(InGameDirectionEventOpcode.InGameDirectionEvent_CameraOnCharacter.getValue(), null, new int[]{value});
    }

    public final void cameraZoom(int[] values) {
        getDirectionEffect(InGameDirectionEventOpcode.InGameDirectionEvent_CameraZoom.getValue(), null, values);
    }

    public final void hidePlayer(boolean hide) {
        getDirectionEffect(InGameDirectionEventOpcode.InGameDirectionEvent_VansheeMode.getValue(), null, new int[]{hide ? 1 : 0});
    }

    public final void faceOff(int value) {
        getDirectionEffect(InGameDirectionEventOpcode.InGameDirectionEvent_FaceOff.getValue(), null, new int[]{value});
    }

    public void sendTellStory(String data, boolean lastLine) {
        getDirectionEffect(InGameDirectionEventOpcode.InGameDirectionEvent_Monologue.getValue(), data, new int[]{lastLine ? 1 : 0});
    }

    public void removeAdditionalEffect() {
        getDirectionEffect(InGameDirectionEventOpcode.InGameDirectionEvent_RemoveAdditionalEffect.getValue(), null, null);
    }

    public void forcedMove(int value, int value1) {
        getDirectionEffect(InGameDirectionEventOpcode.InGameDirectionEvent_ForcedMove.getValue(), null, new int[]{value, value1});
    }

    public void forcedFlip(int value) {
        getDirectionEffect(InGameDirectionEventOpcode.InGameDirectionEvent_ForcedFlip.getValue(), null, new int[]{value});
    }

    public void inputUI(int value) {
        getDirectionEffect(InGameDirectionEventOpcode.InGameDirectionEvent_InputUI.getValue(), null, new int[]{value});
    }

    public void getDirectionEffect(int mod, String data, int[] values) {
        InGameDirectionEventOpcode type = InGameDirectionEventOpcode.getType(mod);
        c.getSession().writeAndFlush(UIPacket.getDirectionEffect(type, data, values));
        if (lastMsg > -1) {
            return;
        }
        switch (type) {
            case InGameDirectionEvent_Delay:
            case InGameDirectionEvent_ForcedInput:
            case InGameDirectionEvent_PatternInputRequest:
            case InGameDirectionEvent_CameraMove:
            case InGameDirectionEvent_CameraZoom:
                lastMsg = 0x11;
                break;
//            case InGameDirectionEvent_Monologue:
//                lastMsg = ScriptMessageType.SM_MONOLOGUE;
//                break;
        }
    }

    public void getDirectionFacialExpression(int expression, int duration) {
        c.getSession().writeAndFlush(UIPacket.facialExpression2(expression, duration));
    }

    public void playMovie(String data) {
        playMovie(data, true);
    }

    @Override
    public void playMovie(String data, boolean show) {
        super.playMovie(data, show);
        lastMsg = 0x12;
    }

    public void showMapEffect(String effect) {
        c.sendPacket(CField.MapEff(effect));
    }

    @Override
    public void showEffect(boolean broadcast, String effect) {
        if (broadcast) {
            c.getPlayer().getMap().broadcastMessage(CField.showEffect(effect));
        } else {
            c.sendPacket(CField.showEffect(effect));
        }
    }

    @Override
    public void playSound(boolean broadcast, String sound) {
        if (broadcast) {
            c.getPlayer().getMap().broadcastMessage(CField.playSound(sound));
        } else {
            c.sendPacket(CField.playSound(sound));
        }
    }

    public void environmentChange(boolean broadcast, String env) {
        if (broadcast) {
            c.getPlayer().getMap().broadcastMessage(CField.environmentChange(env, 2));
        } else {
            c.sendPacket(CField.environmentChange(env, 2));
        }
    }

    public void updateBuddyCapacity(int capacity) {
        c.getPlayer().setBuddyCapacity((byte) capacity);
    }

    public int getBuddyCapacity() {
        return c.getPlayer().getBuddyCapacity();
    }

    public int partyMembersInMap() {
        int inMap = 0;
        if (getPlayer().getParty() == null) {
            return inMap;
        }
        for (MapleCharacter char2 : getPlayer().getMap().getCharactersThreadsafe()) {
            if (char2.getParty() != null && char2.getParty().getId() == getPlayer().getParty().getId()) {
                inMap++;
            }
        }
        return inMap;
    }

    public List<MapleCharacter> getPartyMembers() {
        if (getPlayer().getParty() == null) {
            return null;
        }
        List<MapleCharacter> chars = new LinkedList<>(); // creates an empty array full of shit..
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            for (ChannelServer channel : ChannelServer.getAllInstances()) {
                MapleCharacter ch = channel.getPlayerStorage().getCharacterById(chr.getId());
                if (ch != null) { // double check <3
                    chars.add(ch);
                }
            }
        }
        return chars;
    }

    public void warpPartyWithExp(int mapId, int exp) {
        if (getPlayer().getParty() == null) {
            warp(mapId, 0);
            gainExp(exp);
            return;
        }
        MapleMap target = getMap(mapId);
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
            if ((curChar.getEventInstance() == null && getPlayer().getEventInstance() == null) || curChar.getEventInstance() == getPlayer().getEventInstance()) {
                curChar.changeMap(target, target.getPortal(0));
                curChar.gainExp(exp, true, false, true);
            }
        }
    }

    public void warpPartyWithExpMeso(int mapId, int exp, int meso) {
        if (getPlayer().getParty() == null) {
            warp(mapId, 0);
            gainExp(exp);
            gainMeso(meso);
            return;
        }
        MapleMap target = getMap(mapId);
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
            if ((curChar.getEventInstance() == null && getPlayer().getEventInstance() == null) || curChar.getEventInstance() == getPlayer().getEventInstance()) {
                curChar.changeMap(target, target.getPortal(0));
                curChar.gainExp(exp, true, false, true);
                curChar.gainMeso(meso, true);
            }
        }
    }

    public MapleSquad getSquad(String type) {
        return c.getChannelServer().getMapleSquad(type);
    }

    public int getSquadAvailability(String type) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return -1;
        }
        return squad.getStatus();
    }

    public boolean registerSquad(String type, int minutes, String startText) {
        if (c.getChannelServer().getMapleSquad(type) == null) {
            final MapleSquad squad = new MapleSquad(c.getChannel(), type, c.getPlayer(), minutes * 60 * 1000, startText);
            final boolean ret = c.getChannelServer().addMapleSquad(squad, type);
            if (ret) {
                final MapleMap map = c.getPlayer().getMap();

                map.broadcastMessage(CField.getClock(minutes * 60));
                map.broadcastMessage(CWvsContext.serverNotice(6, c.getPlayer().getName() + startText));
            } else {
                squad.clear();
            }
            return ret;
        }
        return false;
    }

    public boolean getSquadList(String type, byte type_) {
        try {
            final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
            if (squad == null) {
                return false;
            }
            if (type_ == 0 || type_ == 3) { // Normal viewing
                sendNext(squad.getSquadMemberString(type_));
            } else if (type_ == 1) { // Squad Leader banning, Check out banned participant
                sendSimple(squad.getSquadMemberString(type_));
            } else if (type_ == 2) {
                if (squad.getBannedMemberSize() > 0) {
                    sendSimple(squad.getSquadMemberString(type_));
                } else {
                    sendNext(squad.getSquadMemberString(type_));
                }
            }
            return true;
        } catch (NullPointerException ex) {
            FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, ex);
            return false;
        }
    }

    public byte isSquadLeader(String type) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return -1;
        } else {
            if (squad.getLeader() != null && squad.getLeader().getId() == c.getPlayer().getId()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public boolean reAdd(String eim, String squad) {
        EventInstanceManager eimz = getDisconnected(eim);
        MapleSquad squadz = getSquad(squad);
        if (eimz != null && squadz != null) {
            squadz.reAddMember(getPlayer());
            eimz.registerPlayer(getPlayer());
            return true;
        }
        return false;
    }

    public void banMember(String type, int pos) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.banMember(pos);
        }
    }

    public void acceptMember(String type, int pos) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.acceptMember(pos);
        }
    }

    public int addMember(String type, boolean join) {
        try {
            final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
            if (squad != null) {
                return squad.addMember(c.getPlayer(), join);
            }
            return -1;
        } catch (NullPointerException ex) {
            FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, ex);
            return -1;
        }
    }

    public byte isSquadMember(String type) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return -1;
        } else {
            if (squad.getMembers().contains(c.getPlayer())) {
                return 1;
            } else if (squad.isBanned(c.getPlayer())) {
                return 2;
            } else {
                return 0;
            }
        }
    }

    public void resetReactors() {
        getPlayer().getMap().resetReactors();
    }

    public void genericGuildMessage(int code) {
        c.sendPacket(GuildPacket.genericGuildMessage((byte) code));
    }

    public void disbandGuild() {
        final int gid = c.getPlayer().getGuildId();
        if (gid <= 0 || c.getPlayer().getGuildRank() != 1) {
            return;
        }
        World.Guild.disbandGuild(gid);
    }

    public void increaseGuildCapacity(boolean trueMax) {
        if (c.getPlayer().getMeso() < 500000 && !trueMax) {
            c.sendPacket(CWvsContext.serverNotice(1, "您沒有足夠的楓幣。"));
            return;
        }
        final int gid = c.getPlayer().getGuildId();
        if (gid <= 0) {
            return;
        }
        if (World.Guild.increaseGuildCapacity(gid, trueMax)) {
            if (!trueMax) {
                c.getPlayer().gainMeso(-500000, true, true);
            } else {
                gainGP(-25000);
            }
            //sendNext("Your guild capacity has been raised...");
        } else if (!trueMax) {
            sendNext("請確認公會是否已經滿人. (最大限制: 100人)");
        } else {
            sendNext("請確認公會是否已經滿人，如果您需要再擴大公會人數必須扣除25000GP但這樣會讓公會等級降低 (最大限制: 200人)");
        }
    }

    public void displayGuildRanks() {
        c.sendPacket(GuildPacket.showGuildRanks(id, MapleGuildRanking.getInstance().getRank()));
    }

    public void showFm() {
        c.sendPacket(GuildPacket.showfameRanks(id, MapleGuildRanking.getInstance().getFameRank()));
    }

    public void showRb() {
        c.sendPacket(GuildPacket.showrebornRanks(id, MapleGuildRanking.getInstance().getRebornRank()));
    }

    public boolean removePlayerFromInstance() {
        if (c.getPlayer().getEventInstance() != null) {
            c.getPlayer().getEventInstance().removePlayer(c.getPlayer());
            return true;
        }
        return false;
    }

    public boolean isPlayerInstance() {
        if (c.getPlayer().getEventInstance() != null) {
            return true;
        }
        return false;
    }

    public void changeStat(byte slot, int type, int amount) {
        Equip sel = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slot);
        switch (type) {
            case 0:
                sel.setStr((short) amount);
                break;
            case 1:
                sel.setDex((short) amount);
                break;
            case 2:
                sel.setInt((short) amount);
                break;
            case 3:
                sel.setLuk((short) amount);
                break;
            case 4:
                sel.setHp((short) amount);
                break;
            case 5:
                sel.setMp((short) amount);
                break;
            case 6:
                sel.setWatk((short) amount);
                break;
            case 7:
                sel.setMatk((short) amount);
                break;
            case 8:
                sel.setWdef((short) amount);
                break;
            case 9:
                sel.setMdef((short) amount);
                break;
            case 10:
                sel.setAcc((short) amount);
                break;
            case 11:
                sel.setAvoid((short) amount);
                break;
            case 12:
                sel.setHands((short) amount);
                break;
            case 13:
                sel.setSpeed((short) amount);
                break;
            case 14:
                sel.setJump((short) amount);
                break;
            case 15:
                sel.setUpgradeSlots((byte) amount);
                break;
            case 16:
                sel.setViciousHammer((byte) amount);
                break;
            case 17:
                sel.setLevel((byte) amount);
                break;
            case 18:
                sel.setEnhance((byte) amount);
                break;
            case 19:
                sel.setPotential1(amount);
                break;
            case 20:
                sel.setPotential2(amount);
                break;
            case 21:
                sel.setPotential3(amount);
                break;
            case 22:
                sel.setPotential4(amount);
                break;
            case 23:
                sel.setPotential5(amount);
                break;
            case 24:
                sel.setOwner(getText());
                break;
            default:
                break;
        }
        c.getPlayer().equipChanged();
        c.getPlayer().fakeRelog();
    }

    public void openDuey() {
        c.getPlayer().setConversation(2);
        c.sendPacket(CField.sendDuey((byte) 9, null));
    }

    public void openMerchantItemStore() {
        c.getPlayer().setConversation(3);
        HiredMerchantHandler.displayMerch(c);
    }

    public void sendPVPWindow() {
        c.sendPacket(UIPacket.openUI(50, 0));
        c.sendPacket(CField.sendPVPMaps());
    }

    public void sendAzwanWindow() {
        c.sendPacket(UIPacket.openUI(70, 0));
    }

    public void sendFriendWindow() {
        int viewonly = c.getPlayer().getFriendShipToAdd();
        c.sendPacket(UIPacket.sendFriendWindow(viewonly));
    }

    public void sendDojoRanks() {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT `name`, `time` FROM dojo_ranks ORDER BY `time` ASC LIMIT 50");
            ResultSet rs = ps.executeQuery();
            c.sendPacket(CWvsContext.getMulungRanks(rs));
            ps.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println("Failed to load Mu Lung Ranking. " + e);
        }
    }

    public void sendRepairWindow() {
        c.sendPacket(UIPacket.sendRepairWindow(id));
    }

    public void sendProfessionWindow() {
        c.sendPacket(UIPacket.openUI(42, 0));
    }

    public void setDojoMode(int mode) {
        if (getParty() == null) {
            getPlayer().setDojoMode(getPlayer().getDojoMode(mode));
        } else {
            for (MaplePartyCharacter chr : getParty().getMembers()) {
                MapleCharacter ch = World.Find.findChr(chr.getName());
                if (ch != null) {
                    ch.setDojoMode(ch.getDojoMode(mode));
                }
            }
        }
    }

    public final int getDojoPoints() {
        return dojo_getPts();
    }

    public final int getDojoRecord() {
        return c.getPlayer().getIntNoRecord(GameConstants.DOJO_RECORD);
    }

    public void setDojoRecord(final boolean reset) {
        if (reset) {
            c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.DOJO_RECORD)).setCustomData("0");
            c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.DOJO)).setCustomData("0");
        } else {
            c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.DOJO_RECORD)).setCustomData(String.valueOf(c.getPlayer().getIntRecord(GameConstants.DOJO_RECORD) + 1));
        }
    }

    public boolean start_DojoAgent(final boolean dojo, final boolean party) {
        if (dojo) {
            return Event_DojoAgent.warpStartDojo(c.getPlayer(), party);
        }
        return Event_DojoAgent.warpStartAgent(c.getPlayer(), party);
    }

    public boolean start_PyramidSubway(final int pyramid) {
        if (pyramid >= 0) {
            return Event_PyramidSubway.warpStartPyramid(c.getPlayer(), pyramid);
        }
        return Event_PyramidSubway.warpStartSubway(c.getPlayer());
    }

    public boolean bonus_PyramidSubway(final int pyramid) {
        if (pyramid >= 0) {
            return Event_PyramidSubway.warpBonusPyramid(c.getPlayer(), pyramid);
        }
        return Event_PyramidSubway.warpBonusSubway(c.getPlayer());
    }

    public final short getKegs() {
        return c.getChannelServer().getFireWorks().getKegsPercentage();
    }

    public final short get香爐() {
        return c.getChannelServer().getFireWorks().get香爐Percentage();
    }

    public void giveKegs(final int kegs) {
        c.getChannelServer().getFireWorks().giveKegs(c.getPlayer(), kegs);
    }

    public void give香爐(final int 香爐) {
        c.getChannelServer().getFireWorks().give香爐(c.getPlayer(), 香爐);
    }

    public final short getSunshines() {
        return c.getChannelServer().getFireWorks().getSunsPercentage();
    }

    public void addSunshines(final int kegs) {
        c.getChannelServer().getFireWorks().giveSuns(c.getPlayer(), kegs);
    }

    public final short getDecorations() {
        return c.getChannelServer().getFireWorks().getDecsPercentage();
    }

    public void addDecorations(final int kegs) {
        try {
            c.getChannelServer().getFireWorks().giveDecs(c.getPlayer(), kegs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final MapleCarnivalParty getCarnivalParty() {
        return c.getPlayer().getCarnivalParty();
    }

    public final MapleCarnivalChallenge getNextCarnivalRequest() {
        return c.getPlayer().getNextCarnivalRequest();
    }

    public final MapleCarnivalChallenge getCarnivalChallenge(MapleCharacter chr) {
        return new MapleCarnivalChallenge(chr);
    }

    public void maxStats() {
        Map<MapleStat, Integer> statup = new EnumMap<>(MapleStat.class);
        c.getPlayer().getStat().str = (short) 32767;
        c.getPlayer().getStat().dex = (short) 32767;
        c.getPlayer().getStat().int_ = (short) 32767;
        c.getPlayer().getStat().luk = (short) 32767;

        int overrDemon = GameConstants.isDemon(c.getPlayer().getJob()) ? 10/*GameConstants.getMPByJob(c.getPlayer().getJob())*/ : 99999;
        c.getPlayer().getStat().maxhp = 99999;
        c.getPlayer().getStat().maxmp = overrDemon;
        c.getPlayer().getStat().setHp(99999, c.getPlayer());
        c.getPlayer().getStat().setMp(overrDemon, c.getPlayer());

        statup.put(MapleStat.STR, 32767);
        statup.put(MapleStat.DEX, 32767);
        statup.put(MapleStat.LUK, 32767);
        statup.put(MapleStat.INT, 32767);
        statup.put(MapleStat.HP, 99999);
        statup.put(MapleStat.MAXHP, 99999);
        statup.put(MapleStat.MP, overrDemon);
        statup.put(MapleStat.MAXMP, overrDemon);
        c.getPlayer().getStat().recalcLocalStats(c.getPlayer());
        c.sendPacket(CWvsContext.updatePlayerStats(statup, c.getPlayer()));
    }

    public Triple<String, Map<Integer, String>, Long> getSpeedRun(String typ) {
        final ExpeditionType type = ExpeditionType.valueOf(typ);
        if (SpeedRunner.getSpeedRunData(type) != null) {
            return SpeedRunner.getSpeedRunData(type);
        }
        return new Triple<>("", new HashMap<>(), 0L);
    }

    public boolean getSR(Triple<String, Map<Integer, String>, Long> ma, int sel) {
        if (ma.mid.get(sel) == null || ma.mid.get(sel).length() <= 0) {
            dispose();
            return false;
        }
        sendOk(ma.mid.get(sel));
        return true;
    }

    public Equip getEquip(int itemid) {
        return (Equip) MapleItemInformationProvider.getInstance().getEquipById(itemid);
    }

    public void setExpiration(Object statsSel, long expire) {
        if (statsSel instanceof Equip) {
            ((Equip) statsSel).setExpiration(System.currentTimeMillis() + (expire * 24 * 60 * 60 * 1000));
        }
    }

    public void setLock(Object statsSel) {
        if (statsSel instanceof Equip) {
            Equip eq = (Equip) statsSel;
            if (eq.getExpiration() == -1) {
                eq.setFlag((byte) (eq.getFlag() | ItemFlag.LOCK.getValue()));
            } else {
                eq.setFlag((byte) (eq.getFlag() | ItemFlag.UNTRADEABLE.getValue()));
            }
        }
    }

    public boolean addFromDrop(Object statsSel) {
        if (statsSel instanceof Item) {
            final Item it = (Item) statsSel;
            return MapleInventoryManipulator.checkSpace(getClient(), it.getItemId(), it.getQuantity(), it.getOwner()) && MapleInventoryManipulator.addFromDrop(getClient(), it, false);
        }
        return false;
    }

    public boolean replaceItem(int slot, int invType, Object statsSel, int offset, String type) {
        return replaceItem(slot, invType, statsSel, offset, type, false);
    }

    public boolean replaceItem(int slot, int invType, Object statsSel, int offset, String type, boolean takeSlot) {
        MapleInventoryType inv = MapleInventoryType.getByType((byte) invType);
        if (inv == null) {
            return false;
        }
        Item item = getPlayer().getInventory(inv).getItem((byte) slot);
        if (item == null || statsSel instanceof Item) {
            item = (Item) statsSel;
        }
        if (offset > 0) {
            if (inv != MapleInventoryType.EQUIP) {
                return false;
            }
            Equip eq = (Equip) item;
            if (takeSlot) {
                if (eq.getUpgradeSlots() < 1) {
                    return false;
                } else {
                    eq.setUpgradeSlots((byte) (eq.getUpgradeSlots() - 1));
                }
                if (eq.getExpiration() == -1) {
                    eq.setFlag((byte) (eq.getFlag() | ItemFlag.LOCK.getValue()));
                } else {
                    eq.setFlag((byte) (eq.getFlag() | ItemFlag.UNTRADEABLE.getValue()));
                }
            }
            if (type.equalsIgnoreCase("Slots")) {
                eq.setUpgradeSlots((byte) (eq.getUpgradeSlots() + offset));
                eq.setViciousHammer((byte) (eq.getViciousHammer() + offset));
            } else if (type.equalsIgnoreCase("Level")) {
                eq.setLevel((byte) (eq.getLevel() + offset));
            } else if (type.equalsIgnoreCase("Hammer")) {
                eq.setViciousHammer((byte) (eq.getViciousHammer() + offset));
            } else if (type.equalsIgnoreCase("STR")) {
                eq.setStr((short) (eq.getStr() + offset));
            } else if (type.equalsIgnoreCase("DEX")) {
                eq.setDex((short) (eq.getDex() + offset));
            } else if (type.equalsIgnoreCase("INT")) {
                eq.setInt((short) (eq.getInt() + offset));
            } else if (type.equalsIgnoreCase("LUK")) {
                eq.setLuk((short) (eq.getLuk() + offset));
            } else if (type.equalsIgnoreCase("HP")) {
                eq.setHp((short) (eq.getHp() + offset));
            } else if (type.equalsIgnoreCase("MP")) {
                eq.setMp((short) (eq.getMp() + offset));
            } else if (type.equalsIgnoreCase("WATK")) {
                eq.setWatk((short) (eq.getWatk() + offset));
            } else if (type.equalsIgnoreCase("MATK")) {
                eq.setMatk((short) (eq.getMatk() + offset));
            } else if (type.equalsIgnoreCase("WDEF")) {
                eq.setWdef((short) (eq.getWdef() + offset));
            } else if (type.equalsIgnoreCase("MDEF")) {
                eq.setMdef((short) (eq.getMdef() + offset));
            } else if (type.equalsIgnoreCase("ACC")) {
                eq.setAcc((short) (eq.getAcc() + offset));
            } else if (type.equalsIgnoreCase("Avoid")) {
                eq.setAvoid((short) (eq.getAvoid() + offset));
            } else if (type.equalsIgnoreCase("Hands")) {
                eq.setHands((short) (eq.getHands() + offset));
            } else if (type.equalsIgnoreCase("Speed")) {
                eq.setSpeed((short) (eq.getSpeed() + offset));
            } else if (type.equalsIgnoreCase("Jump")) {
                eq.setJump((short) (eq.getJump() + offset));
            } else if (type.equalsIgnoreCase("ItemEXP")) {
                eq.setItemEXP(eq.getItemEXP() + offset);
            } else if (type.equalsIgnoreCase("Expiration")) {
                eq.setExpiration((long) (eq.getExpiration() + offset));
            } else if (type.equalsIgnoreCase("Flag")) {
                eq.setFlag((byte) (eq.getFlag() + offset));
            }
            item = eq.copy();
        }
        MapleInventoryManipulator.removeFromSlot(getClient(), inv, (short) slot, item.getQuantity(), false);
        return MapleInventoryManipulator.addFromDrop(getClient(), item, false);
    }

    public boolean replaceItem(int slot, int invType, Object statsSel, int upgradeSlots) {
        return replaceItem(slot, invType, statsSel, upgradeSlots, "Slots");
    }

    public boolean isCash(final int itemId) {
        return MapleItemInformationProvider.getInstance().isCash(itemId);
    }

    public int getTotalStat(final int itemId) {
        return MapleItemInformationProvider.getInstance().getTotalStat((Equip) MapleItemInformationProvider.getInstance().getEquipById(itemId));
    }

    public int getReqLevel(final int itemId) {
        return MapleItemInformationProvider.getInstance().getReqLevel(itemId);
    }

    public MapleStatEffect getEffect(int buff) {
        return MapleItemInformationProvider.getInstance().getItemEffect(buff);
    }

    public void buffGuild(final int buff, final int duration, final String msg) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.getItemEffect(buff) != null && getPlayer().getGuildId() > 0) {
            final MapleStatEffect mse = ii.getItemEffect(buff);
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                    if (chr.getGuildId() == getPlayer().getGuildId()) {
                        mse.applyTo(chr, chr, true, null, duration);
                        chr.dropMessage(5, "Your guild has gotten a " + msg + " buff.");
                    }
                }
            }
        }
    }

    public boolean createAlliance(String alliancename) {
        MapleParty pt = c.getPlayer().getParty();
        MapleCharacter otherChar = c.getChannelServer().getPlayerStorage().getCharacterById(pt.getMemberByIndex(1).getId());
        if (otherChar == null || otherChar.getId() == c.getPlayer().getId()) {
            return false;
        }
        try {
            return World.Alliance.createAlliance(alliancename, c.getPlayer().getId(), otherChar.getId(), c.getPlayer().getGuildId(), otherChar.getGuildId());
        } catch (Exception re) {
            re.printStackTrace();
            return false;
        }
    }

    public boolean addCapacityToAlliance() {
        try {
            final MapleGuild gs = World.Guild.getGuild(c.getPlayer().getGuildId());
            if (gs != null && c.getPlayer().getGuildRank() == 1 && c.getPlayer().getAllianceRank() == 1) {
                if (World.Alliance.getAllianceLeader(gs.getAllianceId()) == c.getPlayer().getId() && World.Alliance.changeAllianceCapacity(gs.getAllianceId())) {
                    gainMeso(-MapleGuildAlliance.CHANGE_CAPACITY_COST);
                    return true;
                }
            }
        } catch (Exception re) {
            re.printStackTrace();
        }
        return false;
    }

    public boolean disbandAlliance() {
        try {
            final MapleGuild gs = World.Guild.getGuild(c.getPlayer().getGuildId());
            if (gs != null && c.getPlayer().getGuildRank() == 1 && c.getPlayer().getAllianceRank() == 1) {
                if (World.Alliance.getAllianceLeader(gs.getAllianceId()) == c.getPlayer().getId() && World.Alliance.disbandAlliance(gs.getAllianceId())) {
                    return true;
                }
            }
        } catch (Exception re) {
            re.printStackTrace();
        }
        return false;
    }

    public byte getLastMsg() {
        return lastMsg;
    }

    public final void setLastMsg(final byte last) {
        this.lastMsg = last;
    }

    public final void maxAllSkills() {
        HashMap<Skill, SkillEntry> sa = new HashMap<>();
        for (Skill skil : SkillFactory.getAllSkills()) {
            if (GameConstants.isApplicableSkill(skil.getId()) && skil.getId() < 90000000) { //no db/additionals/resistance skills
                sa.put(skil, new SkillEntry((byte) skil.getMaxLevel(), (byte) skil.getMaxLevel(), SkillFactory.getDefaultSExpiry(skil), -1));
            }
        }
        getPlayer().changeSkillsLevel(sa);
    }

    public final void maxSkillsByJob() {
        getPlayer().maxSkillsByJob();
    }

    public final void resetStats(int str, int dex, int z, int luk) {
        c.getPlayer().resetStats(str, dex, z, luk);
    }

    public final void levelUp() {
        c.getPlayer().levelUp();
    }

    public final boolean dropItem(int slot, int invType, int quantity) {
        MapleInventoryType inv = MapleInventoryType.getByType((byte) invType);
        if (inv == null) {
            return false;
        }
        return MapleInventoryManipulator.drop(c, inv, (short) slot, (short) quantity, true);
    }

    public final List<Integer> getAllPotentialInfo() {
        List<Integer> list = new ArrayList<>(MapleItemInformationProvider.getInstance().getAllPotentialInfo().keySet());
        Collections.sort(list);
        return list;
    }

    public final List<Integer> getAllPotentialInfoSearch(String content) {
        List<Integer> list = new ArrayList<>();
        for (Entry<Integer, List<StructItemOption>> i : MapleItemInformationProvider.getInstance().getAllPotentialInfo().entrySet()) {
            for (StructItemOption ii : i.getValue()) {
                if (ii.toString().contains(content)) {
                    list.add(i.getKey());
                }
            }
        }
        Collections.sort(list);
        return list;
    }

    public final String getPotentialInfo(final int id) {
        final List<StructItemOption> potInfo = MapleItemInformationProvider.getInstance().getPotentialInfo(id);
        final StringBuilder builder = new StringBuilder("#b#ePOTENTIAL INFO FOR ID: ");
        builder.append(id);
        builder.append("#n#k\r\n\r\n");
        int minLevel = 1, maxLevel = 10;
        for (StructItemOption item : potInfo) {
            builder.append("#eLevels ");
            builder.append(minLevel);
            builder.append("~");
            builder.append(maxLevel);
            builder.append(": #n");
            builder.append(item.toString());
            minLevel += 10;
            maxLevel += 10;
            builder.append("\r\n");
        }
        return builder.toString();
    }

    public final void sendRPS() {
        c.sendPacket(CField.getRPSMode((byte) 8, -1, -1, -1));
    }

    public final void setQuestRecord(Object ch, final int questid, final String data) {
        ((MapleCharacter) ch).getQuestNAdd(MapleQuest.getInstance(questid)).setCustomData(data);
    }

    public final void doWeddingEffect(final Object ch) {
        final MapleCharacter chr = (MapleCharacter) ch;
        final MapleCharacter player = getPlayer();
        getMap().broadcastMessage(CWvsContext.yellowChat(player.getName() + ", 妳願意承認 " + chr.getName() + " 做妳的丈夫，誠實遵照上帝的誡命，和他生活在一起，無論在什麼環境願順服他、愛惜他、安慰他、尊重他保護他，以致奉召歸主？？"));
        CloneTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (chr == null || player == null) {
                    warpMap(680000500, 0);
                } else {
                    chr.getMap().broadcastMessage(CWvsContext.yellowChat(chr.getName() + ", 你願意承認接納 " + player.getName() + " 做你的妻子，誠實遵照上帝的誡命，和她生活在一起，無論在什麼環境，願意終生養她、愛惜她、安慰她、尊重她、保護她，以至奉召歸主？？"));
                }
            }
        }, 10000);
        CloneTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (chr == null || player == null) {
                    if (player != null) {
                        setQuestRecord(player, 160001, "3");
                        setQuestRecord(player, 160002, "0");
                    } else if (chr != null) {
                        setQuestRecord(chr, 160001, "3");
                        setQuestRecord(chr, 160002, "0");
                    }
                    warpMap(680000500, 0);
                } else {
                    setQuestRecord(player, 160001, "2");
                    setQuestRecord(chr, 160001, "2");
                    sendNPCText(chr.getName() + " 和 " + getPlayer().getName() + "， 我希望你們兩個能在此時此刻永遠愛著對方！", 9201002);
                    getMap().startExtendedMapEffect("那麼現在請新郎親吻 " + getPlayer().getName() + "！", 5120006);
                    if (chr.getGuildId() > 0) {
                        World.Guild.guildPacket(chr.getGuildId(), CWvsContext.sendMarriage(false, chr.getName()));
                    }
                    if (chr.getFamilyId() > 0) {
                        World.Family.familyPacket(chr.getFamilyId(), CWvsContext.sendMarriage(true, chr.getName()), chr.getId());
                    }
                    if (player.getGuildId() > 0) {
                        World.Guild.guildPacket(player.getGuildId(), CWvsContext.sendMarriage(false, player.getName()));
                    }
                    if (player.getFamilyId() > 0) {
                        World.Family.familyPacket(player.getFamilyId(), CWvsContext.sendMarriage(true, chr.getName()), player.getId());
                    }
                }
            }
        }, 20000); //10 sec 10 sec

    }

    public void putKey(int key, int type, int action) {
        getPlayer().changeKeybinding(key, (byte) type, action);
        getClient().sendPacket(CField.getKeymap(getPlayer().getKeyLayout()));
    }

    public void logDonator(String log, int previous_points) {
        final StringBuilder logg = new StringBuilder();
        logg.append(MapleCharacterUtil.makeMapleReadable(getPlayer().getName()));
        logg.append(" [CID: ").append(getPlayer().getId()).append("] ");
        logg.append(" [Account: ").append(MapleCharacterUtil.makeMapleReadable(getClient().getAccountName())).append("] ");
        logg.append(log);
        logg.append(" [Previous: " + previous_points + "] [Now: " + getPlayer().getPoints() + "]");

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO donorlog VALUES(DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, MapleCharacterUtil.makeMapleReadable(getClient().getAccountName()));
            ps.setInt(2, getClient().getAccID());
            ps.setString(3, MapleCharacterUtil.makeMapleReadable(getPlayer().getName()));
            ps.setInt(4, getPlayer().getId());
            ps.setString(5, log);
            ps.setString(6, FileoutputUtil.CurrentReadable_Time());
            ps.setInt(7, previous_points);
            ps.setInt(8, getPlayer().getPoints());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        FileoutputUtil.log(FileoutputUtil.Donator_Log, logg.toString());
    }

    public void doRing(final String name, final int itemid) {
        PlayersHandler.DoRing(getClient(), name, itemid);
    }

    public int getNaturalStats(final int itemid, final String it) {
        Map<String, Integer> eqStats = MapleItemInformationProvider.getInstance().getEquipStats(itemid);
        if (eqStats != null && eqStats.containsKey(it)) {
            return eqStats.get(it);
        }
        return 0;
    }

    public boolean isEligibleName(String t) {
        return MapleCharacterUtil.canCreateChar(t, getPlayer().isGM()) && (!LoginInformationProvider.getInstance().isForbiddenName(t) || getPlayer().isGM());
    }

    public String checkDrop(int mobId) {
        final List<MonsterDropEntry> ranks = MapleMonsterInformationProvider.getInstance().retrieveDrop(mobId);
        if (ranks != null && ranks.size() > 0) {
            int num = 0, itemId = 0, ch = 0;
            MonsterDropEntry de;
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < ranks.size(); i++) {
                de = ranks.get(i);
                if (de.chance > 0 && (de.questid <= 0 || (de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0))) {
                    itemId = de.itemId;
                    if (num == 0) {
                        name.append("怪物名稱：#o").append(mobId).append("#\r\n");
                        name.append("--------------------------------------\r\n");
                    }
                    String namez = "#z" + itemId + "#";
                    if (itemId == 0) { //meso
                        itemId = 4031041; //display sack of cash
                        namez = (de.Minimum * getClient().getChannelServer().getMesoRate()) + " 至 " + (de.Maximum * getClient().getChannelServer().getMesoRate()) + " 楓幣";
                    }
                    ch = de.chance * getClient().getChannelServer().getDropRate();
                    name.append((num + 1) + ") #v" + itemId + "#" + namez + " - 機率 " + (Integer.valueOf(ch >= 999999 ? 1000000 : ch).doubleValue() / 10000.0) + "% " + (de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0 ? ("相關任務： " + MapleQuest.getInstance(de.questid).getName()) : "") + "\r\n");
                    num++;
                }
            }
            if (name.length() > 0) {
                return name.toString();
            }

        }
        return "查無掉寶資訊。";
    }

    public List<PokedexEntry> getAllPokedex() {
        return BattleConstants.getAllPokedex();
    }

    public String getLeftPadded(final String in, final char padchar, final int length) {
        return StringUtil.getLeftPaddedStr(in, padchar, length);
    }

    public void preparePokemonBattle(List<Integer> npcTeam, int restrictedLevel) {
        final int theId = MapleLifeFactory.getRandomNPC();
        final PokemonBattle wild = new PokemonBattle(getPlayer(), npcTeam, theId, restrictedLevel);
        getPlayer().changeMap(wild.getMap(), wild.getMap().getPortal(0));
        getPlayer().setBattle(wild);
        wild.initiate(getPlayer(), MapleLifeFactory.getNPC(theId));
    }

    public List<Integer> makeTeam(int lowRange, int highRange, int neededLevel, int restrictedLevel) { //easy = 10 lvls below you to your lvl, normal = 5 lvls below you to 5 lvls above, hard = your lvl to 10 lvls above, hell = bosses that are lower than you
        // easy/norm/hard = min lvl 10, hell = min lvl 100
        final List<Integer> ret = new ArrayList<>();
        int averageLevel = 0, numBattlers = 0;
        for (Battler b : getPlayer().getBattlers()) {
            if (b != null) {
                if (b.getLevel() > averageLevel) {
                    averageLevel = b.getLevel();
                }
                numBattlers++;
            }
        }
        final boolean hell = lowRange == highRange;
        if (numBattlers < 3 || averageLevel < neededLevel) {
            return null;
        }
        if (averageLevel > restrictedLevel) {
            averageLevel = restrictedLevel; //cap it
        }
        final List<PokedexEntry> pokeEntries = new ArrayList<>(getAllPokedex());
        Collections.shuffle(pokeEntries);
        while (ret.size() < numBattlers) {
            for (PokedexEntry d : pokeEntries) {
                if ((d.dummyBattler.getStats().isBoss() && hell) || (!d.dummyBattler.getStats().isBoss() && !hell)) {
                    if (!hell) {
                        if (d.dummyBattler.getLevel() <= (averageLevel + highRange) && d.dummyBattler.getLevel() >= (averageLevel + lowRange) && Randomizer.nextInt(numBattlers) == 0) {
                            ret.add(d.id);
                            if (ret.size() >= numBattlers) {
                                break;
                            }
                        }
                    } else if (d.dummyBattler.getFamily().type != MobExp.EASY && d.dummyBattler.getLevel() >= neededLevel && d.dummyBattler.getLevel() <= averageLevel && Randomizer.nextInt(numBattlers) == 0) {
                        ret.add(d.id);
                        if (ret.size() >= numBattlers) {
                            break;
                        }
                    }
                }
            }
        }
        return ret;
    }

    public BattleConstants.HoldItem[] getAllHoldItems() {
        return BattleConstants.HoldItem.values();
    }

    public void handleDivorce() {
        if (getPlayer().getMarriageId() <= 0) {
            sendNext("Please make sure you have a marriage.");
            return;
        }
        final int chz = World.Find.findChannel(getPlayer().getMarriageId());
        if (chz == -1) {
            //sql queries
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("UPDATE queststatus SET customData = ? WHERE characterid = ? AND (quest = ? OR quest = ?)");
                ps.setString(1, "0");
                ps.setInt(2, getPlayer().getMarriageId());
                ps.setInt(3, 160001);
                ps.setInt(4, 160002);
                ps.executeUpdate();
                ps.close();

                ps = con.prepareStatement("UPDATE characters SET marriageid = ? WHERE id = ?");
                ps.setInt(1, 0);
                ps.setInt(2, getPlayer().getMarriageId());
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                outputFileError(e);
                return;
            }
            setQuestRecord(getPlayer(), 160001, "0");
            setQuestRecord(getPlayer(), 160002, "0");
            getPlayer().setMarriageId(0);
            sendNext("You have been successfully divorced...");
            return;
        } else if (chz < -1) {
            sendNext("Please make sure your partner is logged on.");
            return;
        }
        MapleCharacter cPlayer = ChannelServer.getInstance(chz).getPlayerStorage().getCharacterById(getPlayer().getMarriageId());
        if (cPlayer != null) {
            cPlayer.dropMessage(1, "Your partner has divorced you.");
            cPlayer.setMarriageId(0);
            setQuestRecord(cPlayer, 160001, "0");
            setQuestRecord(getPlayer(), 160001, "0");
            setQuestRecord(cPlayer, 160002, "0");
            setQuestRecord(getPlayer(), 160002, "0");
            getPlayer().setMarriageId(0);
            sendNext("You have been successfully divorced...");
        } else {
            sendNext("An error occurred...");
        }
    }

    public String getReadableMillis(long startMillis, long endMillis) {
        return StringUtil.getReadableMillis(startMillis, endMillis);
    }

    public void sendUltimateExplorer() {
        getClient().sendPacket(CWvsContext.ultimateExplorer());
    }

    public void changeJobById(short job) {
        c.getPlayer().changeJob(job);
    }

    public int getJobId() {
        return getPlayer().getJob();
    }

    public int getLevel() {
        return getPlayer().getLevel();
    }

    public int getEquipId(byte slot) {
        MapleInventory equip = getPlayer().getInventory(MapleInventoryType.EQUIP);
        Equip eu = (Equip) equip.getItem(slot);
        return equip.getItem(slot).getItemId();
    }

    public int getUseId(byte slot) {
        MapleInventory use = getPlayer().getInventory(MapleInventoryType.USE);
        return use.getItem(slot).getItemId();
    }

    public int getSetupId(byte slot) {
        MapleInventory setup = getPlayer().getInventory(MapleInventoryType.SETUP);
        return setup.getItem(slot).getItemId();
    }

    public int getCashId(byte slot) {
        MapleInventory cash = getPlayer().getInventory(MapleInventoryType.CASH);
        return cash.getItem(slot).getItemId();
    }

    public int getETCId(byte slot) {
        MapleInventory etc = getPlayer().getInventory(MapleInventoryType.ETC);
        return etc.getItem(slot).getItemId();
    }

    public String getPokemonRanking() {
        StringBuilder sb = new StringBuilder();
        for (PokemonInformation pi : RankingWorker.getPokemonInfo()) {
            sb.append(pi.toString());
        }
        return sb.toString();
    }

    public String getPokemonRanking_Caught() {
        StringBuilder sb = new StringBuilder();
        for (PokedexInformation pi : RankingWorker.getPokemonCaught()) {
            sb.append(pi.toString());
        }
        return sb.toString();
    }

    public String getPokemonRanking_Ratio() {
        StringBuilder sb = new StringBuilder();
        for (PokebattleInformation pi : RankingWorker.getPokemonRatio()) {
            sb.append(pi.toString());
        }
        return sb.toString();
    }

    public void sendPendant(boolean b) {
        c.sendPacket(CWvsContext.pendantSlot(b));
    }

    public Triple<Integer, Integer, Integer> getCompensation() {
        Triple<Integer, Integer, Integer> ret = null;
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM compensationlog_confirmed WHERE chrname LIKE ?");
            ps.setString(1, getPlayer().getName());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ret = new Triple<>(rs.getInt("value"), rs.getInt("taken"), rs.getInt("donor"));
            }
            rs.close();
            ps.close();
            return ret;
        } catch (SQLException e) {
            FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, e);
            return ret;
        }
    }

    public boolean deleteCompensation(int taken) {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE compensationlog_confirmed SET taken = ? WHERE chrname LIKE ?");
            ps.setInt(1, taken);
            ps.setString(2, getPlayer().getName());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, e);
            return false;
        }
    }

    /*Start of Custom Features*/
    public void gainAPS(int gain) {
        getPlayer().gainAPS(gain);
    }

    /*End of Custom Features*/
    public boolean foundData(int type, String search) {
        return SearchGenerator.foundData(type, search);
    }

    public String searchData(int type, String search) {
        return SearchGenerator.searchData(type, search);
    }

    public int[] getSearchData(int type, String search) {
        Map<Integer, String> data = SearchGenerator.getSearchData(type, search);
        if (data.isEmpty()) {
            return null;
        }
        int[] searches = new int[data.size()];
        int i = 0;
        for (int key : data.keySet()) {
            searches[i] = key;
            i++;
        }
        return searches;
    }

    public int gachapon(int type) {
        MapleGachaponItem gitem = MapleGachapon.randomItem(type);
        if (gitem == null) {
            return -1;
        }
        int quantity = MapleGachapon.gainItem(gitem);
        final Item item = MapleInventoryManipulator.addbyId_Gachapon(c, gitem.getItemId(), (short) quantity);

        if (item == null) {
            return -1;
        }

        if (gitem.getSmegaType() > -1) {
            //World.Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega(c.getPlayer().getName() + " : x" + quantity + (gitem.getQuantity() > 0 ? gitem.getRemainingQuantity() == 0 ? "(已無剩餘)" : ("（剩餘" + gitem.getRemainingQuantity() + "個）") : "") + "，恭喜" + c.getPlayer().getName() + "從楓葉轉蛋機獲得。", item, c.getChannel()));
            World.Broadcast.broadcastMessage(CWvsContext.getGachaponMega(c.getPlayer().getName(), item, false, c.getChannel()));
        }
        return item.getItemId();
    }

    public boolean hasSubwpn() {
        Item toUse1 = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short) -10);
        Item toUse2 = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short) -110);
        if (toUse1 != null || toUse2 != null) {
            return false;
        }
        return true;
    }

    public boolean hasSquadByMap() {
        return getPlayer().getMap().getSquadByMap() != null;
    }

    public boolean hasEventInstance() {
        return getPlayer().getEventInstance() != null;
    }

    public boolean liveReceiveMedal() {
        int acid = getPlayer().getAccountID();
        int id = getPlayer().getId();
        String name = getPlayer().getName();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      /*  int item = 1142075;
        if (!getPlayer().canHold(item)) {
            return false;
        } else if (getPlayer().haveItem(item)) {
            return false;
        }*/

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT id FROM livemedals WHERE name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (!rs.first()) {// 角色不存在於勳章表單
                return false;
            }
            ps.close();

            ps = con.prepareStatement("SELECT id FROM livemedals WHERE accountid = ? and amount = ?");
            ps.setInt(1, acid);
            ps.setInt(2, 0);
            rs = ps.executeQuery();
            if (rs.next()) {// 帳號存在於勳章表單
                return false;
            }
            ps.close();
            rs.close();

            ps = con.prepareStatement("Update livemedals set amount = ? Where id = ?");
            ps.setInt(1, 0);
            ps.setInt(2, id);
            ps.execute();
            ps.close();
        } catch (Exception ex) {
            FilePrinter.printError("NPCConversationManager.txt", ex, "ReceiveMedal(" + name + ")");
        }
        //  Item toDrop = ii.randomizeStats((Equip) ii.getEquipById(item));;
        // toDrop.setGMLog(getPlayer().getName() + " 領取勳章");
        // MapleInventoryManipulator.addbyItem(c, toDrop);
        FileoutputUtil.logToFile("logs/data/直播.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 領取了RC勳章");
        return true;
    }

    public boolean ReceiveMedal() {
        int acid = getPlayer().getAccountID();
        int id = getPlayer().getId();
        String name = getPlayer().getName();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
       /* int item = 1672003;
        if (!getPlayer().canHold(item)) {
            return false;
        } else if (getPlayer().haveItem(item)) {
            return false;
        }*/

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT id FROM rcmedals WHERE name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (!rs.first()) {// 角色不存在於勳章表單
                return false;
            }
            ps.close();

            ps = con.prepareStatement("SELECT id FROM rcmedals WHERE accountid = ? and amount = ?");
            ps.setInt(1, acid);
            ps.setInt(2, 0);
            rs = ps.executeQuery();
            if (rs.next()) {// 帳號存在於勳章表單
                return false;
            }
            ps.close();
            rs.close();

            ps = con.prepareStatement("Update rcmedals set amount = ? Where id = ?");
            ps.setInt(1, 0);
            ps.setInt(2, id);
            ps.execute();
            ps.close();
        } catch (Exception ex) {
            FilePrinter.printError("NPCConversationManager.txt", ex, "ReceiveMedal(" + name + ")");
        }
//        Item toDrop = ii.randomizeStats((Equip) ii.getEquipById(item));;
//        toDrop.setGMLog(getPlayer().getName() + " 領取勳章");
//        MapleInventoryManipulator.addbyItem(c, toDrop);
        FileoutputUtil.logToFile("logs/data/RC.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 領取了RC勳章");
        return true;
    }

    public boolean ReceiveMedal1000() {
        int acid = getPlayer().getAccountID();
        int id = getPlayer().getId();
        String name = getPlayer().getName();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      /*  int item = 1032024;
        if (!getPlayer().canHold(item)) {
            return false;
        } else if (getPlayer().haveItem(item)) {
            return false;
        }*/

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT id FROM rcmedals1000 WHERE name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (!rs.first()) {// 角色不存在於勳章表單
                return false;
            }
            ps.close();

            ps = con.prepareStatement("SELECT id FROM rcmedals1000 WHERE accountid = ? and amount = ?");
            ps.setInt(1, acid);
            ps.setInt(2, 0);
            rs = ps.executeQuery();
            if (rs.next()) {// 帳號存在於勳章表單
                return false;
            }
            ps.close();
            rs.close();

            ps = con.prepareStatement("Update rcmedals1000 set amount = ? Where id = ?");
            ps.setInt(1, 0);
            ps.setInt(2, id);
            ps.execute();
            ps.close();
        } catch (Exception ex) {
            FilePrinter.printError("NPCConversationManager.txt", ex, "ReceiveMedal(" + name + ")");
        }
//        Item toDrop = ii.randomizeStats((Equip) ii.getEquipById(item));;
//        toDrop.setGMLog(getPlayer().getName() + " 領取勳章");
//        MapleInventoryManipulator.addbyItem(c, toDrop);
        FileoutputUtil.logToFile("logs/data/RC.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 領取了RC勳章");
        return true;
    }

    public boolean fbReceiveMedal() {
        int acid = getPlayer().getAccountID();
        int id = getPlayer().getId();
        String name = getPlayer().getName();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      /*  int item = 1202118;
        if (!getPlayer().canHold(item)) {
            return false;
        } else if (getPlayer().haveItem(item)) {
            return false;
        }*/

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT id FROM fbmedals WHERE name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (!rs.first()) {// 角色不存在於勳章表單
                return false;
            }
            ps.close();

            ps = con.prepareStatement("SELECT id FROM fbmedals WHERE accountid = ? and amount = ?");
            ps.setInt(1, acid);
            ps.setInt(2, 0);
            rs = ps.executeQuery();
            if (rs.next()) {// 帳號存在於勳章表單
                return false;
            }
            ps.close();
            rs.close();

            ps = con.prepareStatement("Update fbmedals set amount = ? Where id = ?");
            ps.setInt(1, 0);
            ps.setInt(2, id);
            ps.execute();
            ps.close();
        } catch (Exception ex) {
            FilePrinter.printError("NPCConversationManager.txt", ex, "ReceiveMedal(" + name + ")");
        }
//        Item toDrop = ii.randomizeStats((Equip) ii.getEquipById(item));;
//        toDrop.setGMLog(getPlayer().getName() + " 領取勳章");
//        MapleInventoryManipulator.addbyItem(c, toDrop);
        FileoutputUtil.logToFile("logs/data/fb分享.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 領取了RC勳章");
        return true;
    }

    public boolean hasEMByMap() {
        return getPlayer().getMap().getEMByMap() != null;
    }

    public void processCommand(String line) {
        CommandProcessor.processCommand(getClient(), line, ServerConstants.CommandType.NORMAL);
    }

    public String getSkillMenu(int job) {
        String menu = "";
        for (Skill ret : SkillFactory.getAllSkills()) {
            int skillJob = ret.getId() / 10000;
            if (MapleJob.getBeginner((short) job) == job) {
                return "";
            }
            if (GameConstants.isProfessionSkill(ret.getId())) {
                continue;
            }
            if (skillJob != job) {
                continue;
            }
            if (c.getPlayer().getTotalSkillLevel(ret.getId()) >= ret.getMaxLevel()) {
                continue;
            }
            menu += "\r\n#L" + ret.getId() + "##s" + ret.getId() + "##q" + ret.getId() + "#";

        }
        return menu;
    }
}
