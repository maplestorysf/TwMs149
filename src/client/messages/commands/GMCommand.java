package client.messages.commands;

import client.LoginCrypto;
import client.inventory.*;
import client.messages.CommandExecute;
import client.MapleCharacter;
import constants.ServerConstants.PlayerGMRank;
import client.MapleClient;
import client.MapleStat;
import client.Skill;
import client.SkillFactory;
import client.messages.CommandProcessorUtil;
import constants.GameConstants;
import constants.ServerConstants;
import database.DatabaseConnection;
import handling.RecvPacketOpcode;
import handling.SendPacketOpcode;
import handling.channel.ChannelServer;
import handling.login.handler.AutoRegister;
import handling.world.World;
import handling.world.family.MapleFamily;
import handling.world.guild.MapleGuild;

import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import scripting.AbstractScriptManager;

import scripting.NPCScriptManager;
import scripting.PortalScriptManager;
import scripting.ReactorScriptManager;
import server.*;
import server.Timer.EventTimer;
import server.events.MapleOxQuizFactory;
import server.life.MapleLifeFactory;
import server.life.MapleMonsterInformationProvider;
import server.life.MapleNPC;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleReactor;
import server.maps.MapleReactorFactory;
import server.maps.MapleReactorStats;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.packet.CWvsContext;
import tools.StringUtil;
import tools.Triple;
import tools.packet.CField;
import tools.packet.CField.NPCPacket;
import tools.packet.CWvsContext.GuildPacket;

/**
 * @author Emilyx3
 */
public class GMCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.領導者;
    }

    public static class 修改人氣商品 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            int input = 0, Change = 0, sn = 0;
            try {
                input = Integer.parseInt(splitted[1]);
                Change = (input - 1);
                sn = Integer.parseInt(splitted[2]);
            } catch (Exception ex) {
                return false;
            }
            if (input < 1 || input > 5) {
                c.getPlayer().dropMessage("數字只能輸入1~5之間唷");
                return true;
            }
            ServerConstants.hot_sell[Change] = sn;
            c.getPlayer().dropMessage("商城人氣商品第" + input + "個已經修改為SN是 " + sn + " 的道具");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("修改人氣商品 <第X個人氣商品> <新商品的SN> - 修改商城右邊人氣商品").toString();
        }
    }

    public static class LowHP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getStat().setHp((short) 1, c.getPlayer());
            c.getPlayer().getStat().setMp((short) 1, c.getPlayer());
            c.getPlayer().updateSingleStat(MapleStat.HP, 1);
            c.getPlayer().updateSingleStat(MapleStat.MP, 1);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("lowhp - 血魔歸ㄧ").toString();
        }
    }

    public static class MyPos extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            Point pos = c.getPlayer().getPosition();
            c.getPlayer().dropMessage(6, "X: " + pos.x + " | Y: " + pos.y + " | RX0: " + (pos.x + 50) + " | RX1: " + (pos.x - 50) + " | CY:" + pos.y);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("mypos - 我的位置").toString();
        }
    }

    public static class Notice extends CommandExecute {

        private static int getNoticeType(String typestring) {
            switch (typestring) {
                case "n":
                    return 0;
                case "p":
                    return 1;
                case "l":
                    return 2;
                case "nv":
                    return 5;
                case "v":
                    return 5;
                case "b":
                    return 6;
            }
            return -1;
        }

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            int joinmod = 1;
            int range = -1;
            if (splitted.length < 2) {
                return false;
            }
            switch (splitted[1]) {
                case "m":
                    range = 0;
                    break;
                case "c":
                    range = 1;
                    break;
                case "w":
                    range = 2;
                    break;
            }

            int tfrom = 2;
            if (range == -1) {
                range = 2;
                tfrom = 1;
            }
            if (splitted.length < tfrom + 1) {
                return false;
            }
            int type = getNoticeType(splitted[tfrom]);
            if (type == -1) {
                type = 0;
                joinmod = 0;
            }
            StringBuilder sb = new StringBuilder();
            if (splitted[tfrom].equals("nv")) {
                sb.append("[Notice]");
            } else {
                sb.append("");
            }
            joinmod += tfrom;
            if (splitted.length < joinmod + 1) {
                return false;
            }
            sb.append(StringUtil.joinStringFrom(splitted, joinmod));

            byte[] packet = CWvsContext.serverNotice(type, sb.toString());
            switch (range) {
                case 0:
                    c.getPlayer().getMap().broadcastMessage(packet);
                    break;
                case 1:
                    ChannelServer.getInstance(c.getChannel()).broadcastPacket(packet);
                    break;
                case 2:
                    World.Broadcast.broadcastMessage(packet);
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("notice <n|p|l|nv|v|b> <m|c|w> <message> - 公告").toString();
        }
    }

    public static class NoticeSpam extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            int count = Integer.parseInt(splitted[1]);
            StringBuilder sb = new StringBuilder();
            sb.append(StringUtil.joinStringFrom(splitted, 2));
            byte[] packet = CWvsContext.serverNotice(0, sb.toString());
            for (int i = 0; i < count; i++) {
                World.Broadcast.broadcastMessage(packet);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("NoticeSpam <count> <message> - 刷公告").toString();
        }
    }

    public static class Yellow extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            int range = -1;
            switch (splitted[1]) {
                case "m":
                    range = 0;
                    break;
                case "c":
                    range = 1;
                    break;
                case "w":
                    range = 2;
                    break;
            }
            if (range == -1) {
                range = 2;
            }
            byte[] packet = CWvsContext.yellowChat((splitted[0].equals("!y") ? ("[" + c.getPlayer().getName() + "] ") : "") + StringUtil.joinStringFrom(splitted, 2));
            switch (range) {
                case 0:
                    c.getPlayer().getMap().broadcastMessage(packet);
                    break;
                case 1:
                    ChannelServer.getInstance(c.getChannel()).broadcastPacket(packet);
                    break;
                case 2:
                    World.Broadcast.broadcastMessage(packet);
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("yellow <m|c|w> <message> - 黃色公告").toString();
        }
    }

    public static class Y extends Yellow {
    }

    public static class NpcNotice extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length <= 2) {
                return false;
            }
            int npcid = Integer.parseInt(splitted[1]);
            String msg = splitted[2];
            MapleNPC npc = MapleLifeFactory.getNPC(npcid);
            if (npc == null || !npc.getName().equals("MISSINGNO")) {
                c.getPlayer().dropMessage(6, "查無此 Npc ");
                return true;
            }
            World.Broadcast.broadcastMessage(NPCPacket.getNPCTalk(npcid, (byte) 0, msg, "00 00", (byte) 0));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("NpcNotice <npcid> <message> - 用NPC發訊息").toString();
        }
    }

    public static class opennpc extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            String script = null;
            int mode = 0;
            int npcid = 0;
            try {
                npcid = Integer.parseInt(splitted[1]);
                mode = Integer.parseInt(splitted[2]);
            } catch (Exception ex) {

            }
            if (splitted.length == 2 && npcid == 0) {
                script = splitted[1];
            }

            MapleNPC npc = MapleLifeFactory.getNPC(npcid);
            if (script != null) {
                NPCScriptManager.getInstance().start(c, 9900001, -1, script);
            } else if (npc != null && !npc.getName().equalsIgnoreCase("MISSINGNO")) {
                NPCScriptManager.getInstance().start(c, npcid, mode, script);
            } else {
                c.getPlayer().dropMessage(6, "未知NPC");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("openNpc <NPC代碼> - 開啟NPC").toString();
        }
    }

    public static class 改名字 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            String after = splitted[1];
            try {
                if (after.length() >= 4 || after.length() <= 12) {
                    c.getPlayer().setName(splitted[1]);
                    c.getPlayer().fakeRelog();
                }
            } catch (StringIndexOutOfBoundsException ex) {
                c.getPlayer().dropMessage("名字過長或者過短。");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("改名字 	新名字 - 改角色名字").toString();
        }
    }

    public static class 加入公會 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length != 2) {
                return false;
            }
            com.mysql.jdbc.Connection dcon = (com.mysql.jdbc.Connection) DatabaseConnection.getConnection();
            try {
                com.mysql.jdbc.PreparedStatement ps = (com.mysql.jdbc.PreparedStatement) dcon.prepareStatement("SELECT guildid FROM guilds WHERE name = ?");
                ps.setString(1, splitted[1]);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    if (c.getPlayer().getGuildId() > 0) {
                        try {
                            World.Guild.leaveGuild(c.getPlayer().getMGC());
                        } catch (Exception e) {
                            c.sendPacket(CWvsContext.serverNotice(6, "無法連接到世界伺服器，請稍後再嘗試。"));
                            return false;
                        }
                        c.sendPacket(GuildPacket.showGuildInfo(null));

                        c.getPlayer().setGuildId(0);
                        c.getPlayer().saveGuildStatus();
                    }
                    c.getPlayer().setGuildId(rs.getInt("guildid"));
                    c.getPlayer().setGuildRank((byte) 2); // 副會長
                    try {
                        World.Guild.addGuildMember(c.getPlayer().getMGC(), false);
                    } catch (Exception e) {
                    }
                    c.sendPacket(GuildPacket.showGuildInfo(c.getPlayer()));
                    c.getPlayer().getMap().broadcastMessage(c.getPlayer(), CField.removePlayerFromMap(c.getPlayer().getId()), false);
                    c.getPlayer().getMap().broadcastMessage(c.getPlayer(), CField.spawnPlayerMapobject(c.getPlayer()), false);
                    c.getPlayer().saveGuildStatus();
                } else {
                    c.getPlayer().dropMessage(6, "公會名稱不存在。");
                }
                rs.close();
                ps.close();
            } catch (SQLException e) {
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("加入公會 	公會名字 - 強制加入公會").toString();
        }
    }

    public static class 離婚 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter victim;
            String name = splitted[1];
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "玩家必須上線");
                return true;
            }
            victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                c.getPlayer().dropMessage(6, "玩家必須上線");
                return true;
            }
            victim.setMarriageId(0);
            victim.reloadC();
            victim.dropMessage(5, "離婚成功！");
            victim.saveToDB(false, false);
            c.getPlayer().dropMessage(6, victim.getName() + "離婚成功！");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("離婚 <玩家名稱> - 離婚").toString();
        }
    }

    public static class CancelBuffs extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().cancelAllBuffs();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("CancelBuffs - 取消所有BUFF").toString();
        }
    }

    public static class RemoveNPCs extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().resetNPCs();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("removenpcs - 刪除所有NPC").toString();
        }
    }

    public static class LookNPCs extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (MapleMapObject reactor1l : c.getPlayer().getMap().getAllNPCsThreadsafe()) {
                MapleNPC reactor2l = (MapleNPC) reactor1l;
                c.getPlayer().dropMessage(5, "NPC: oID: " + reactor2l.getObjectId() + " npcID: " + reactor2l.getId() + " Position: " + reactor2l.getPosition().toString() + " Name: " + reactor2l.getName());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("looknpcs - 查看所有NPC").toString();
        }
    }

    public static class LookReactors extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (MapleMapObject reactor1l : c.getPlayer().getMap().getAllReactorsThreadsafe()) {
                MapleReactor reactor2l = (MapleReactor) reactor1l;
                c.getPlayer().dropMessage(5, "Reactor: oID: " + reactor2l.getObjectId() + " reactorID: " + reactor2l.getReactorId() + " Position: " + reactor2l.getPosition().toString() + " State: " + reactor2l.getState() + " Name: " + reactor2l.getName());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("lookreactors - 查看所有反應堆").toString();
        }
    }

    public static class LookPortals extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (MaplePortal portal : c.getPlayer().getMap().getPortals()) {
                c.getPlayer().dropMessage(5, "Portal: ID: " + portal.getId() + " script: " + portal.getScriptName() + " name: " + portal.getName() + " pos: " + portal.getPosition().x + "," + portal.getPosition().y + " target: " + portal.getTargetMapId() + " / " + portal.getTarget());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("lookportals - 查看所有反應堆").toString();
        }
    }

    public static class GoTo extends CommandExecute {

        private static final HashMap<String, Integer> gotomaps = new HashMap<>();

        static {
            gotomaps.put("gmmap", 180000000);
            gotomaps.put("southperry", 2000000);
            gotomaps.put("amherst", 1010000);
            gotomaps.put("henesys", 100000000);
            gotomaps.put("ellinia", 101000000);
            gotomaps.put("perion", 102000000);
            gotomaps.put("kerning", 103000000);
            gotomaps.put("lithharbour", 104000000);
            gotomaps.put("sleepywood", 105040300);
            gotomaps.put("florina", 110000000);
            gotomaps.put("orbis", 200000000);
            gotomaps.put("happyville", 209000000);
            gotomaps.put("elnath", 211000000);
            gotomaps.put("ludibrium", 220000000);
            gotomaps.put("aquaroad", 230000000);
            gotomaps.put("leafre", 240000000);
            gotomaps.put("mulung", 250000000);
            gotomaps.put("herbtown", 251000000);
            gotomaps.put("omegasector", 221000000);
            gotomaps.put("koreanfolktown", 222000000);
            gotomaps.put("newleafcity", 600000000);
            gotomaps.put("sharenian", 990000000);
            gotomaps.put("pianus", 230040420);
            gotomaps.put("horntail", 240060200);
            gotomaps.put("chorntail", 240060201);
            gotomaps.put("mushmom", 100000005);
            gotomaps.put("griffey", 240020101);
            gotomaps.put("manon", 240020401);
            gotomaps.put("zakum", 280030000);
            gotomaps.put("czakum", 280030001);
            gotomaps.put("papulatus", 220080001);
            gotomaps.put("showatown", 801000000);
            gotomaps.put("zipangu", 800000000);
            gotomaps.put("ariant", 260000100);
            gotomaps.put("nautilus", 120000000);
            gotomaps.put("boatquay", 541000000);
            gotomaps.put("malaysia", 550000000);
            gotomaps.put("taiwan", 740000000);
            gotomaps.put("thailand", 500000000);
            gotomaps.put("erev", 130000000);
            gotomaps.put("ellinforest", 300000000);
            gotomaps.put("kampung", 551000000);
            gotomaps.put("singapore", 540000000);
            gotomaps.put("amoria", 680000000);
            gotomaps.put("timetemple", 270000000);
            gotomaps.put("pinkbean", 270050100);
            gotomaps.put("peachblossom", 700000000);
            gotomaps.put("fm", 910000000);
            gotomaps.put("freemarket", 910000000);
            gotomaps.put("oxquiz", 109020001);
            gotomaps.put("ola", 109030101);
            gotomaps.put("fitness", 109040000);
            gotomaps.put("snowball", 109060000);
            gotomaps.put("cashmap", 741010200);
            gotomaps.put("golden", 950100000);
            gotomaps.put("phantom", 610010000);
            gotomaps.put("cwk", 610030000);
            gotomaps.put("rien", 140000000);
        }

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "使用方法: !goto <地圖名稱>");
            } else if (gotomaps.containsKey(splitted[1])) {
                MapleMap target = c.getChannelServer().getMapFactory().getMap(gotomaps.get(splitted[1]));
                MaplePortal targetPortal = target.getPortal(0);
                c.getPlayer().changeMap(target, targetPortal);
            } else if (splitted[1].equals("目的地")) {
                c.getPlayer().dropMessage(6, "使用 !goto <目的地>. 目的地地圖如下:");
                StringBuilder sb = new StringBuilder();
                for (String s : gotomaps.keySet()) {
                    sb.append(s).append(", ");
                }
                c.getPlayer().dropMessage(6, sb.substring(0, sb.length() - 2));
            } else {
                c.getPlayer().dropMessage(6, "錯誤的指令規則 - 使用 !goto <目的地>. 來看目的地地圖清單, 接著使用 !goto 目的地地圖名稱.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("goto <名稱> - 到某個地圖").toString();

        }
    }

    public static class cleardrops extends RemoveDrops {

    }

    public static class RemoveDrops extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().dropMessage(5, "清除了 " + c.getPlayer().getMap().getNumItems() + " 個掉落物");
            c.getPlayer().getMap().removeDrops();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("removedrops - 移除地上的物品").toString();

        }
    }

    public static class NearestPortal extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MaplePortal portal = c.getPlayer().getMap().findClosestSpawnpoint(c.getPlayer().getPosition());
            c.getPlayer().dropMessage(6, portal.getName() + " id: " + portal.getId() + " script: " + portal.getScriptName());

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("nearestportal - 不知道啥").toString();

        }
    }

    public static class SpawnDebug extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().dropMessage(6, c.getPlayer().getMap().spawnDebug());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("spawndebug - debug怪物出生").toString();

        }
    }

    public static class copyAll extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            MapleCharacter victim;
            if (splitted.length < 2) {
                return false;
            }
            String name = splitted[1];
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "玩家必須上線");
                return true;
            }
            victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                player.dropMessage("找不到該玩家");
                return true;
            }
            MapleInventory equipped = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED);
            MapleInventory equip = c.getPlayer().getInventory(MapleInventoryType.EQUIP);
            List<Short> ids = new LinkedList<>();
            for (Item item : equipped.list()) {
                ids.add(item.getPosition());
            }
            for (short id : ids) {
                MapleInventoryManipulator.unequip(c, id, equip.getNextFreeSlot());
            }
            c.getPlayer().clearSkills();
            c.getPlayer().setStr(victim.getStat().getStr());
            c.getPlayer().setDex(victim.getStat().getDex());
            c.getPlayer().setInt(victim.getStat().getInt());
            c.getPlayer().setLuk(victim.getStat().getLuk());

            c.getPlayer().setMeso(victim.getMeso());
            c.getPlayer().setLevel((short) (victim.getLevel()));
            c.getPlayer().changeJob(victim.getJob());

            c.getPlayer().setHp(victim.getStat().getHp());
            c.getPlayer().setMp(victim.getStat().getMp());
            c.getPlayer().setMaxHp(victim.getStat().getMaxHp());
            c.getPlayer().setMaxMp(victim.getStat().getMaxMp());

            String normal = victim.getName();
            String after = (normal + "x2");
            if (after.length() <= 12) {
                c.getPlayer().setName(victim.getName() + "x2");
            }
            c.getPlayer().setRemainingAp(victim.getRemainingAp());
            c.getPlayer().setRemainingSp(victim.getRemainingSp());
            c.getPlayer().LearnSameSkill(victim);

            c.getPlayer().setFame(victim.getFame());
            c.getPlayer().setHair(victim.getHair());
            c.getPlayer().setFace(victim.getFace());

            c.getPlayer().setSkinColor(victim.getSkinColor() == 0 ? c.getPlayer().getSkinColor() : victim.getSkinColor());

            c.getPlayer().setGender(victim.getGender());

            for (Item ii : victim.getInventory(MapleInventoryType.EQUIPPED).list()) {
                Item eq = ii.copy();
                eq.setPosition(eq.getPosition());
                eq.setQuantity((short) 1);
                eq.setEquipOnlyId(MapleEquipOnlyId.getInstance().getNextEquipOnlyId());
                c.getPlayer().forceReAddItem_NoUpdate(eq, MapleInventoryType.EQUIPPED);
            }
            c.getPlayer().fakeRelog();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("copyall 玩家名稱 - 複製玩家").toString();
        }
    }

    public static class copyInv extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            MapleCharacter victim;
            int type = 1;
            if (splitted.length < 2) {
                return false;
            }

            String name = splitted[1];
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "玩家必須上線");
                return false;
            }
            victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                player.dropMessage("找不到該玩家");
                return true;
            }
            try {
                type = Integer.parseInt(splitted[2]);
            } catch (Exception ex) {
            }
            if (type == 0) {
                for (client.inventory.Item ii : victim.getInventory(MapleInventoryType.EQUIPPED).list()) {
                    Item n = ii.copy();
                    n.setEquipOnlyId(MapleEquipOnlyId.getInstance().getNextEquipOnlyId());
                    player.getInventory(MapleInventoryType.EQUIP).addItem(n);
                }
                player.fakeRelog();
            } else {
                MapleInventoryType types;
                if (type == 1) {
                    types = MapleInventoryType.EQUIP;
                } else if (type == 2) {
                    types = MapleInventoryType.USE;
                } else if (type == 3) {
                    types = MapleInventoryType.ETC;
                } else if (type == 4) {
                    types = MapleInventoryType.SETUP;
                } else if (type == 5) {
                    types = MapleInventoryType.CASH;
                } else {
                    types = null;
                }
                if (types == null) {
                    c.getPlayer().dropMessage("發生錯誤");
                    return true;
                }
                int[] equip = new int[97];
                for (int i = 1; i < 97; i++) {
                    if (victim.getInventory(types).getItem((short) i) != null) {
                        equip[i] = i;
                    }
                }
                for (int i = 0; i < equip.length; i++) {
                    if (equip[i] != 0) {
                        Item n = victim.getInventory(types).getItem((short) equip[i]).copy();
                        n.setEquipOnlyId(MapleEquipOnlyId.getInstance().getNextEquipOnlyId());
                        player.getInventory(types).addItem(n);
                        c.sendPacket(CWvsContext.InventoryPacket.addInventorySlot(types, n));
                    }
                }
            }
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append("!copyinv 玩家名稱 裝備欄位(0 = 裝備中 1=裝備欄 2=消耗欄 3=其他欄 4=裝飾欄 5=點數欄)(預設裝備欄) - 複製玩家道具").toString();
        }
    }

    public static class Clock extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            c.getPlayer().getMap().broadcastMessage(CField.getClock(CommandProcessorUtil.getOptionalIntArg(splitted, 1, 60)));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("clock <time> 時鐘").toString();
        }
    }

    public static class Song extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            c.getPlayer().getMap().broadcastMessage(CField.musicChange(splitted[1]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("song - 播放音樂").toString();
        }
    }

    public static class Kill extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter victim = null;
            for (int i = 1; i < splitted.length; i++) {
                String name = splitted[i];
                int ch = World.Find.findChannel(name);
                if (ch == -10) {
                    c.getPlayer().dropMessage(6, "玩家[" + name + "]在購物商城");
                    break;
                } else if (ch <= 0) {
                    c.getPlayer().dropMessage(6, "玩家[" + name + "]不在線上");
                    break;
                }
                victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
                if (victim != null) {
                    if (player.allowedToTarget(victim)) {
                        victim.getStat().setHp((short) 0, victim);
                        victim.getStat().setMp((short) 0, victim);
                        victim.updateSingleStat(MapleStat.HP, 0);
                        victim.updateSingleStat(MapleStat.MP, 0);
                    }
                } else {
                    c.getPlayer().dropMessage(6, "玩家 " + name + " 未上線.");
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("kill <玩家名稱> - 殺掉玩家").toString();
        }
    }

    public static class ReloadNpcs extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            int npc = -1;
            if (splitted.length > 1) {
                try {
                    npc = Integer.parseInt(splitted[1]);
                } catch (Exception ex) {

                }
            }
            if (npc == -1) {
                AbstractScriptManager.cleanNpcs();
                c.getPlayer().dropMessage(6, "Npc緩存已重置完成");
            } else {
                AbstractScriptManager.cleanNpc(npc);
                c.getPlayer().dropMessage(6, "Npc" + npc + "緩存已重置完成");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("ReloadNpcs <npc編號> - 重新載入Npc腳本，不輸入編號重載所有Npc腳本").toString();
        }
    }

    public static class ReloadOps extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            SendPacketOpcode.reloadValues();
            RecvPacketOpcode.reloadValues();
            c.getPlayer().dropMessage(6, "伺服器包頭已重置完成");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("reloadops - 重新載入OpCode").toString();
        }
    }

    public static class ReloadDrops extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleMonsterInformationProvider.getInstance().clearDrops();
            ReactorScriptManager.getInstance().clearDrops();
            c.getPlayer().dropMessage(6, "掉落相關道具已重置完成");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("reloaddrops - 重新載入掉寶").toString();
        }
    }

    public static class ReloadPortals extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            PortalScriptManager.getInstance().clearScripts();
            c.getPlayer().dropMessage(6, "傳送腳本已重置完成");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("reloadportals - 重新載入進入點").toString();
        }
    }

    public static class ReloadShops extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleShopFactory.getInstance().clear();
            c.getPlayer().dropMessage(6, "NPC商城已重置完成");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("reloadshops - 重新載入商店").toString();
        }
    }

    public static class ReloadQuests extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleQuest.clearQuests();
            c.getPlayer().dropMessage(6, "任務已重置完成");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("reloadquests - 重新載入任務").toString();
        }
    }

    public static class ReloadOX extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleOxQuizFactory.getInstance().reloadOX();
            c.getPlayer().dropMessage(6, "OX任務已重置完成");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("reloadox - 重新載入OX題目").toString();
        }
    }

    public static class ReloadCS extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            CashItemFactory.getInstance().initialize(true);
            c.getPlayer().dropMessage(6, "購物商城已重置完成");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("reloadCS - 重新載入購物商城").toString();
        }
    }

    public static class Reloadall extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            for (ChannelServer instance : ChannelServer.getAllInstances()) {
                instance.reloadEvents();
            }
            MapleShopFactory.getInstance().clear();
            PortalScriptManager.getInstance().clearScripts();
            MapleItemInformationProvider.getInstance().runEtc();
            MapleItemInformationProvider.getInstance().runItems();

            CashItemFactory.getInstance().initialize(true);
            MapleMonsterInformationProvider.getInstance().clearDrops();

            MapleGuild.loadAll(); //(this);
            MapleFamily.loadAll(); //(this);
            MapleLifeFactory.loadQuestCounts();
            MapleQuest.initQuests();
            MapleOxQuizFactory.getInstance();
            ReactorScriptManager.getInstance().clearDrops();
            SendPacketOpcode.reloadValues();
            RecvPacketOpcode.reloadValues();
            c.getPlayer().dropMessage(6, "全部已經重置完成");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("Reloadall - 重置全伺服器").toString();
        }
    }

    public static class skill extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            Skill skill = SkillFactory.getSkill(Integer.parseInt(splitted[1]));
            byte level = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);
            byte masterlevel = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1);
            if (level > skill.getMaxLevel()) {
                level = (byte) skill.getMaxLevel();
            }
            c.getPlayer().changeSkillLevel(skill, level, masterlevel);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!skill <技能ID> [技能等級] [技能最大等級] ...  - 學習技能").toString();
        }
    }

    public static class GiveSkill extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            Skill skill = SkillFactory.getSkill(Integer.parseInt(splitted[2]));
            byte level = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1);
            byte masterlevel = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 4, 1);
            if (skill == null) {
                c.getPlayer().dropMessage(6, "沒有這個技能。");
                return true;
            }
            if (level > skill.getMaxLevel()) {
                level = (byte) skill.getMaxLevel();
            }
            if (masterlevel > skill.getMaxLevel()) {
                masterlevel = (byte) skill.getMaxLevel();
            }
            victim.changeSingleSkillLevel(skill, level, masterlevel);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("giveskill <玩家名稱> <技能ID> [技能等級] [技能最大等級] - 給予技能").toString();
        }
    }

    public static class MaxSkillsByJob extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().maxSkillsByJob();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("MaxSkillsByJob - 職業技能全滿").toString();
        }
    }

    public static class MaxSkills extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().maxAllSkills();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("MaxSkills - 技能全滿").toString();
        }
    }

    public static class ClearSkills extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().clearSkills();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("ClearSkills - 技能全消").toString();
        }
    }

    public static class SP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            int lv = Integer.parseInt(splitted[1]);
            lv += c.getPlayer().getRemainingSp();
            c.getPlayer().setRemainingSp(lv);
            c.getPlayer().updateSingleStat(MapleStat.AVAILABLESP, lv);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("sp [數量] - 增加SP").toString();
        }
    }

    public static class AP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            short lv = Short.parseShort(splitted[1]);
            lv += c.getPlayer().getRemainingAp();
            c.getPlayer().setRemainingAp(lv);
            c.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, lv);

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("ap [數量] - 增加AP").toString();
        }
    }

    public static class Shop extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleShopFactory shop = MapleShopFactory.getInstance();
            int shopId = 0;
            try {
                shopId = Integer.parseInt(splitted[1]);
            } catch (Exception ex) {
            }
            if (shop.getShop(shopId) != null) {
                shop.getShop(shopId).sendShop(c);
            } else {
                c.getPlayer().dropMessage(5, "此商店ID[" + shopId + "]不存在");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("shop - 開啟商店").toString();
        }
    }

    public static class 關鍵時刻 extends CommandExecute {

        protected static ScheduledFuture<?> ts = null;

        @Override
        public boolean execute(final MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            if (ts != null) {
                ts.cancel(false);
                c.getPlayer().dropMessage(0, "原定的關鍵時刻已取消");
            }
            int minutesLeft;
            try {
                minutesLeft = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException ex) {
                return false;
            }
            if (minutesLeft > 0) {
                ts = EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                            for (MapleCharacter mch : cserv.getPlayerStorage().getAllCharactersThreadSafe()) {
                                if (mch.getLevel() >= 29 && !mch.isGM()) {
                                    NPCScriptManager.getInstance().start(mch.getClient(), 9010010, "CrucialTime");
                                }
                            }
                        }
                        World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "關鍵時刻開放囉，沒有30等以上的玩家是得不到的。"));
                        ts.cancel(false);
                        ts = null;
                    }
                }, minutesLeft * 60000); // 六十秒
                c.getPlayer().dropMessage(0, "關鍵時刻預定已完成");
            } else {
                c.getPlayer().dropMessage(0, "設定的時間必須 > 0。");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("關鍵時刻 <時間:分鐘> - 關鍵時刻").toString();
        }
    }

    public static class Letter extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "指令規則: ");
                return false;
            }
            int start, nstart;
            if (splitted[1].equalsIgnoreCase("green")) {
                start = 3991026;
                nstart = 3990019;
            } else if (splitted[1].equalsIgnoreCase("red")) {
                start = 3991000;
                nstart = 3990009;
            } else {
                c.getPlayer().dropMessage(6, "未知的顏色!");
                return true;
            }
            String splitString = StringUtil.joinStringFrom(splitted, 2);
            List<Integer> chars = new ArrayList<>();
            splitString = splitString.toUpperCase();
            // System.out.println(splitString);
            for (int i = 0; i < splitString.length(); i++) {
                char chr = splitString.charAt(i);
                if (chr == ' ') {
                    chars.add(-1);
                } else if ((int) (chr) >= (int) 'A' && (int) (chr) <= (int) 'Z') {
                    chars.add((int) (chr));
                } else if ((int) (chr) >= (int) '0' && (int) (chr) <= (int) ('9')) {
                    chars.add((int) (chr) + 200);
                }
            }
            final int w = 32;
            int dStart = c.getPlayer().getPosition().x - (splitString.length() / 2 * w);
            for (Integer i : chars) {
                if (i == -1) {
                    dStart += w;
                } else if (i < 200) {
                    int val = start + i - (int) ('A');
                    client.inventory.Item item = new client.inventory.Item(val, (byte) 0, (short) 1);
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), item, new Point(dStart, c.getPlayer().getPosition().y), false, false);
                    dStart += w;
                } else if (i >= 200 && i <= 300) {
                    int val = nstart + i - (int) ('0') - 200;
                    client.inventory.Item item = new client.inventory.Item(val, (byte) 0, (short) 1);
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), item, new Point(dStart, c.getPlayer().getPosition().y), false, false);
                    dStart += w;
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(" !letter <color (green/red)> <word> - 送信").toString();
        }

    }

    public static class Marry extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            int itemId = Integer.parseInt(splitted[2]);
            if (!GameConstants.isEffectRing(itemId)) {
                c.getPlayer().dropMessage(6, "錯誤的戒指ID.");
            } else {
                MapleCharacter fff;
                String name = splitted[1];
                int ch = World.Find.findChannel(name);
                if (ch <= 0) {
                    c.getPlayer().dropMessage(6, "玩家必須上線");
                    return false;
                }
                fff = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
                if (fff == null) {
                    c.getPlayer().dropMessage(6, "玩家必須上線");
                } else {
                    int[] ringID = {MapleInventoryIdentifier.getInstance(), MapleInventoryIdentifier.getInstance()};
                    try {
                        MapleCharacter[] chrz = {fff, c.getPlayer()};
                        for (int i = 0; i < chrz.length; i++) {
                            Equip eq = (Equip) MapleItemInformationProvider.getInstance().getEquipById(itemId);
                            if (eq == null) {
                                c.getPlayer().dropMessage(6, "錯誤的戒指ID.");
                                return true;
                            } else {
                                eq.setUniqueId(ringID[i]);
                                MapleInventoryManipulator.addbyItem(chrz[i].getClient(), eq.copy());
                                chrz[i].dropMessage(6, "成功與  " + chrz[i == 0 ? 1 : 0].getName() + " 結婚");
                            }
                        }
                        MapleRing.addToDB(itemId, c.getPlayer(), fff.getName(), fff.getId(), ringID);
                    } catch (SQLException e) {
                    }
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("marry <玩家名稱> <戒指代碼> - 結婚").toString();
        }
    }

    public static class KillID extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter victim;
            int id = 0;
            try {
                id = Integer.parseInt(splitted[1]);
            } catch (Exception ex) {

            }
            int ch = World.Find.findChannel(id);
            if (ch <= 0) {
                return false;
            }
            victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(id);
            if (victim == null) {
                c.getPlayer().dropMessage(6, "[kill] 玩家ID " + id + " 不存在.");
            } else if (player.allowedToTarget(victim)) {
                victim.getStat().setHp((short) 0, victim);
                victim.getStat().setMp((short) 0, victim);
                victim.updateSingleStat(MapleStat.HP, 0);
                victim.updateSingleStat(MapleStat.MP, 0);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("KillID <玩家ID> - 殺掉玩家").toString();
        }
    }

    public static class autoreg extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            ServerConstants.AUTO_REGISTER = !ServerConstants.AUTO_REGISTER;
            c.getPlayer().dropMessage(0, "[自動註冊] " + (ServerConstants.AUTO_REGISTER ? "開啟" : "關閉"));
            System.out.println("[自動註冊] " + (ServerConstants.AUTO_REGISTER ? "開啟" : "關閉"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("autoreg  - 自動註冊開關").toString();
        }
    }

    public static class LevelUp extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                c.getPlayer().levelUp();
            } else {
                int up = 0;
                try {
                    up = Integer.parseInt(splitted[1]);
                } catch (Exception ex) {

                }
                for (int i = 0; i < up; i++) {
                    c.getPlayer().levelUp();
                }
            }
            c.getPlayer().setExp(0);
            c.getPlayer().updateSingleStat(MapleStat.EXP, 0);
//            if (c.getPlayer().getLevel() < 200) {
//                c.getPlayer().gainExp(GameConstants.getExpNeededForLevel(c.getPlayer().getLevel()) + 1, true, false, true);
//            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("levelup - 等級上升").toString();
        }
    }

    public static class FakeRelog extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter player = c.getPlayer();
            c.sendPacket(CField.getCharInfo(player));
            player.getMap().removePlayer(player);
            player.getMap().addPlayer(player);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("fakerelog - 假登出再登入").toString();

        }
    }

    public static class SpawnReactor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleReactorStats reactorSt = MapleReactorFactory.getReactor(Integer.parseInt(splitted[1]));
            MapleReactor reactor = new MapleReactor(reactorSt, Integer.parseInt(splitted[1]));
            reactor.setDelay(-1);
            reactor.setPosition(c.getPlayer().getPosition());
            c.getPlayer().getMap().spawnReactor(reactor);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("spawnreactor - 設立Reactor").toString();

        }
    }

    public static class HReactor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            c.getPlayer().getMap().getReactorByOid(Integer.parseInt(splitted[1])).hitReactor(c);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("hitreactor - 觸碰Reactor").toString();

        }
    }

    public static class DestroyReactor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleMap map = c.getPlayer().getMap();
            List<MapleMapObject> reactors = map.getMapObjectsInRange(c.getPlayer().getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.REACTOR));
            if (splitted[1].equals("all")) {
                for (MapleMapObject reactorL : reactors) {
                    MapleReactor reactor2l = (MapleReactor) reactorL;
                    c.getPlayer().getMap().destroyReactor(reactor2l.getObjectId());
                }
            } else {
                c.getPlayer().getMap().destroyReactor(Integer.parseInt(splitted[1]));
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("drstroyreactor - 移除Reactor").toString();

        }
    }

    public static class ResetReactors extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().resetReactors();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("resetreactors - 重置此地圖所有的Reactor").toString();

        }
    }

    public static class SetReactor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            c.getPlayer().getMap().setReactorState(Byte.parseByte(splitted[1]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("hitreactor - 觸碰Reactor").toString();

        }
    }

    public static class ResetQuest extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).forfeit(c.getPlayer());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("resetquest <任務ID> - 重置任務").toString();

        }
    }

    public static class StartQuest extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).start(c.getPlayer(), 9900000);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("startquest <任務ID> - 開始任務").toString();

        }
    }

    public static class CompleteQuest extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).complete(c.getPlayer(), Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("completequest <任務ID> - 完成任務").toString();

        }
    }

    public static class FStartQuest extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).forceStart(c.getPlayer(), Integer.parseInt(splitted[2]), splitted.length >= 4 ? splitted[3] : null);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("fstartquest <任務ID> - 強制開始任務").toString();

        }
    }

    public static class FCompleteQuest extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            try {
                MapleQuest quest = MapleQuest.getInstance(Integer.parseInt(splitted[1]));
                int npc = 22000;

                if (splitted.length > 2) {
                    npc = Integer.parseInt(splitted[2]);
                }
                MapleQuest.getInstance(Integer.parseInt(splitted[1])).forceComplete(c.getPlayer(), Integer.parseInt(splitted[2]));
            } catch (ArrayIndexOutOfBoundsException ex) {
                c.getPlayer().dropMessage(6, "參數長度錯誤。");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("fcompletequest <任務ID> - 強制完成任務").toString();

        }
    }

    public static class FStartOther extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {

            MapleQuest.getInstance(Integer.parseInt(splitted[2])).forceStart(c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]), Integer.parseInt(splitted[3]), splitted.length >= 4 ? splitted[4] : null);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("fstartother - 不知道啥").toString();

        }
    }

    public static class FCompleteOther extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleQuest.getInstance(Integer.parseInt(splitted[2])).forceComplete(c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]), Integer.parseInt(splitted[3]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("fcompleteother - 不知道啥").toString();

        }
    }

    public static class log extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            boolean next = false;
            boolean Action = false;
            String LogType = null;
            String[] Log = {"黑板", "個人商店", "精靈商人", "商城", "卷軸", "廣播", "聊天", "交易", "倉庫", "丟東西", "撿東西", "洗方塊", "印章", "斷線"};
            StringBuilder show_log = new StringBuilder();
            for (String s : Log) {
                show_log.append(s);
                show_log.append(" / ");
            }
            if (splitted.length < 3) {
                c.getPlayer().dropMessage("目前Log種類: " + show_log.toString());
                return false;
            }
            if (!splitted[1].contains("開") && !splitted[1].contains("關")) {
                return false;
            }
            if (splitted[1].contains("開") && splitted[1].contains("關")) {
                c.getPlayer().dropMessage("請問這位管理員到底是要開還是關呢?");
                return true;
            }

            for (int i = 0; i < Log.length; i++) {
                if (splitted[2].contains(Log[i])) {
                    next = true;
                    LogType = Log[i];
                    break;
                }
            }
            Action = splitted[1].contains("開");
            if (!next) {
                c.getPlayer().dropMessage("目前Log種類: " + show_log.toString());
                return true;
            }
            switch (LogType) {
                case "黑板":
                    ServerConstants.log_chalkboard = Action;
                    break;
                case "個人商店":
                    ServerConstants.log_mshop = Action;
                    break;
                case "精靈商人":
                    ServerConstants.log_merchant = Action;
                    break;
                case "商城":
                    ServerConstants.log_csbuy = Action;
                    break;
                case "卷軸":
                    ServerConstants.log_scroll = Action;
                    break;
                case "廣播":
                    ServerConstants.log_mega = Action;
                    break;
                case "聊天":
                    ServerConstants.log_chat = Action;
                    break;
                case "交易":
                    ServerConstants.log_trade = Action;
                    break;
                case "倉庫":
                    ServerConstants.log_storage = Action;
                    break;
                case "丟東西":
                    ServerConstants.log_drop = Action;
                    break;
                case "洗方塊":
                    ServerConstants.log_cube = Action;
                    break;
                case "印章":
                    ServerConstants.log_seal = Action;
                    break;
                case "斷線":
                    ServerConstants.log_dc = Action;
                    break;
            }
            String msg = "[GM 密語] 管理員[" + c.getPlayer().getName() + "] " + splitted[1] + "了" + LogType + "的Log";
            World.Broadcast.broadcastGMMessage(CWvsContext.serverMessage(msg));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!log 開/關 Log種類名稱").toString();
        }
    }

    public static class RemoveItem extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            MapleCharacter chr;
            String name = splitted[1];
            int id = Integer.parseInt(splitted[2]);
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "玩家必須上線");
                return true;
            }
            chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);

            if (chr == null) {
                c.getPlayer().dropMessage(6, "此玩家並不存在");
            } else {
                chr.removeAll(id, false, true);
                c.getPlayer().dropMessage(6, "所有ID為 " + id + " 的道具已經從 " + name + " 身上被移除了");
            }
            return true;

        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("RemoveItem <角色名稱> <物品ID> - 移除玩家身上的道具").toString();
        }
    }

    public static class RemoveItemOff extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            try {
                Connection con = DatabaseConnection.getConnection();
                int item = Integer.parseInt(splitted[1]);
                String name = splitted[2];
                int id = 0, quantity = 0;
                List<Long> inventoryitemid = new LinkedList();
                boolean isEquip = GameConstants.isEquip(item);

                if (MapleCharacter.getCharacterIdByName(c.getWorld(), name) == -1) {
                    c.getPlayer().dropMessage(5, "角色不存在資料庫。");
                    return true;
                } else {
                    id = MapleCharacter.getCharacterIdByName(c.getWorld(), name);
                }

                PreparedStatement ps = con.prepareStatement("select inventoryitemid, quantity from inventoryitems WHERE itemid = ? and characterid = ?");
                ps.setInt(1, item);
                ps.setInt(2, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        if (isEquip) {
                            long Equipid = rs.getLong("inventoryitemid");
                            if (Equipid != 0) {
                                inventoryitemid.add(Equipid);
                            }
                            quantity++;
                        } else {
                            quantity += rs.getInt("quantity");
                        }
                    }
                }
                if (quantity == 0) {
                    c.getPlayer().dropMessage(5, "玩家[" + name + "]沒有物品[" + item + "]在背包。");
                    return true;
                }

                if (isEquip) {
                    StringBuilder Sql = new StringBuilder();
                    Sql.append("Delete from inventoryequipment WHERE inventoryitemid = ");
                    for (int i = 0; i < inventoryitemid.size(); i++) {
                        Sql.append(inventoryitemid.get(i));
                        if (i < (inventoryitemid.size() - 1)) {
                            Sql.append(" OR inventoryitemid = ");
                        }
                    }
                    ps = con.prepareStatement(Sql.toString());
                    ps.executeUpdate();
                }

                ps = con.prepareStatement("Delete from inventoryitems WHERE itemid = ? and characterid = ?");
                ps.setInt(1, item);
                ps.setInt(2, id);
                ps.executeUpdate();
                ps.close();

                c.getPlayer().dropMessage(6, "已經從 " + name + " 身上被移除了道具 ID[" + item + "] 數量x" + quantity);
                return true;
            } catch (SQLException e) {
                return true;
            }
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("RemoveItemOff <物品ID> <角色名稱> - 移除玩家身上的道具").toString();
        }
    }

    public static class 查詢洗道具 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleEquipIdOnly Only = MapleEquipIdOnly.getInstance();
            if (Only.isDoing()) {
                c.getPlayer().dropMessage("目前系統忙碌中, 請稍候在試");
                return true;
            }
            c.getPlayer().dropMessage("正在查詢複製中....");
            Only.StartChecking();
            c.getPlayer().dropMessage("複製查詢完成");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("查詢洗道具 - 查詢洗道具").toString();
        }
    }

    public static class 處理洗道具 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleEquipIdOnly Only = MapleEquipIdOnly.getInstance();
            List<Triple<Integer, Long, Long>> MapleEquipIdOnlyList = Only.getData();
            if (MapleEquipIdOnlyList.isEmpty()) {
                c.getPlayer().dropMessage("目前沒有複製裝備的資料, 請輸入 !查詢洗道具 ");
                return true;
            } else if (Only.isDoing()) {
                c.getPlayer().dropMessage("目前系統忙碌中, 請稍候在試");
                return true;
            }
            c.getPlayer().dropMessage(6, "正在處理複製...");
            Only.StartCleaning();
            c.getPlayer().dropMessage(6, "複製裝備處理完畢");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("處理洗道具 - 查詢洗道具").toString();
        }

        public void HandleOffline(MapleClient c, int chr, long inventoryitemid, long equiponlyid) {
            try {
                String itemname = "null";
                Connection con = DatabaseConnection.getConnection();

                try (PreparedStatement ps = con.prepareStatement("select itemid from inventoryitems WHERE inventoryitemid = ?")) {
                    ps.setLong(1, inventoryitemid);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int itemid = rs.getInt("itemid");
                            itemname = MapleItemInformationProvider.getInstance().getName(itemid);
                        } else {
                            c.getPlayer().dropMessage("發生錯誤: 流水號無法指向道具代碼");
                        }
                    }
                }

                try (PreparedStatement ps = con.prepareStatement("Delete from inventoryequipment WHERE inventoryitemid = " + inventoryitemid)) {
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement("Delete from inventoryitems WHERE inventoryitemid = ?")) {
                    ps.setLong(1, inventoryitemid);
                    ps.executeUpdate();
                }

                String msgtext = "玩家ID: " + chr + " 在玩家道具中發現複製裝備[" + itemname + "]已經將其刪除。";
                World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, "[GM密語] " + msgtext));
                FileoutputUtil.logToFile("logs/hack/複製裝備_已刪除.txt", FileoutputUtil.CurrentReadable_Time() + " " + msgtext + " 道具唯一ID: " + equiponlyid + "\r\n");

            } catch (Exception ex) {
                String output = FileoutputUtil.NowTime();
                FileoutputUtil.outputFileError(FileoutputUtil.CommandEx_Log, ex);
                FileoutputUtil.logToFile(FileoutputUtil.CommandEx_Log, output + " \r\n");
            }
        }
    }

    public static class fly extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter m = c.getPlayer();
            int i = m.getMapId();
            if (i != 0) {
                m.fly();
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("fly - 讓自己角色飛起來").toString();
        }
    }

    public static class ReloadEvents extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (ChannelServer instance : ChannelServer.getAllInstances()) {
                instance.reloadEvents();
            }
            c.getPlayer().dropMessage(6, "副本已重置完成");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("reloadevents - 重新載入活動腳本").toString();
        }
    }

    public static class ReloadFishing extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            FishingRewardFactory.getInstance().reloadItems();
            c.getPlayer().dropMessage(6, "釣魚已重置完成");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("reloadFishing - 重新載入釣魚獎勵").toString();
        }
    }

    public static class 尿液修改 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            } else {
                String name = splitted[1];
                int pee = Integer.parseInt(splitted[2]);
                MapleCharacter victim = MapleCharacter.getCharacterByName(name);
                int ch = World.Find.findChannel(name);
                if (victim != null) {
                    victim.setPee(pee);
                    c.getPlayer().dropMessage(6, "已將" + name + "尿液修改為" + pee);
                } else {
                    c.getPlayer().dropMessage(6, "找不到此玩家。");
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(PlayerGMRank.領導者.getCommandPrefix()).append("尿液修改 <玩家名字> <膀胱量> - 控制此人物膀胱量").toString();
        }
    }

    public static class CItem extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }

            int itemId = 0;
            try {
                itemId = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException ex) {

            }
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

            if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " - 物品不存在");
            } else if (!ii.isCash(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " - 物品無法叫出");
            } else if (GameConstants.isEquip(itemId) && ii.isCash(itemId)) {
                Item item = null;
                byte flag = 0;
                flag |= ItemFlag.LOCK.getValue();
                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                    item = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                    item.setFlag(flag);
                }
                if (item != null) {
                    item.setOwner(c.getPlayer().getName());
                    item.setGMLog(c.getPlayer().getName());
                    MapleInventoryManipulator.addbyItem(c, item);
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("Citem <道具ID> - 取得點數裝備").toString();
        }
    }

    public static class 改密碼 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            String username = "";
            String password = "";
            try {
                username = splitted[1];
                password = splitted[2];
            } catch (Exception ex) {
                return false;
            }
            if (username == null || password == null) {
                c.getPlayer().dropMessage("帳號或密碼輸入異常");
                return true;
            }
            if (!AutoRegister.getAccountExists(username)) {
                c.getPlayer().dropMessage("該帳號不存在");
                return true;
            }
            if (username.length() >= 12) {
                c.getPlayer().dropMessage("該帳號長度過長");
                return true;
            }
            try {
                Connection con = DatabaseConnection.getConnection();
                try (java.sql.PreparedStatement ps = con.prepareStatement("Update accounts set password = ?, salt = null Where name = ?")) {
                    ps.setString(1, LoginCrypto.hexSha1(password));
                    ps.setString(2, username);
                    ps.execute();
                } catch (SQLException ex) {
                    return false;
                }
            } catch (Exception ex) {
                return false;
            }
            c.getPlayer().dropMessage("[修改帳密]帳號: " + username + " 密碼: " + password);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("改密碼 <帳號> <新密碼> - 修改遊戲密碼").toString();
        }
    }

    public static class 改第二組密碼 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            String username = "";
            String password = "";
            try {
                username = splitted[1];
                password = splitted[2];
            } catch (Exception ex) {
                return false;
            }
            if (username == null || password == null) {
                c.getPlayer().dropMessage("帳號或密碼輸入異常");
                return true;
            }
            if (!AutoRegister.getAccountExists(username)) {
                c.getPlayer().dropMessage("該帳號不存在");
                return true;
            }
            if (username.length() >= 12) {
                c.getPlayer().dropMessage("該帳號長度過長");
                return true;
            }
            try {
                Connection con = DatabaseConnection.getConnection();
                try (java.sql.PreparedStatement ps = con.prepareStatement("UPDATE accounts set 2ndpassword = ?, salt2 = null where name = ?")) {
                    ps.setString(1, LoginCrypto.hexSha1(password));
                    ps.setString(2, username);
                    ps.execute();
                } catch (SQLException ex) {
                    return false;
                }
            } catch (Exception ex) {
                return false;
            }
            c.getPlayer().dropMessage("[修改第二組密碼]帳號: " + username + " 第二組密碼: " + password);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.領導者.getCommandPrefix()).append("改密碼 <帳號> <新第二組密碼> - 修改遊戲第二組密碼").toString();
        }
    }
}
