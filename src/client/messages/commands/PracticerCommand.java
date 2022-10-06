package client.messages.commands;

import client.messages.CommandExecute;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import client.messages.CommandProcessorUtil;
import constants.GameConstants;
import constants.ServerConstants;
import database.DatabaseConnection;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.world.World;

import java.sql.*;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.ShutdownServer;
import server.Timer;
import server.maps.MapleMap;
import tools.FilePrinter;
import tools.FileoutputUtil;
import tools.StringUtil;
import tools.packet.CField;
import tools.packet.CField.NPCPacket;
import tools.packet.CWvsContext;

/**
 *
 * @author Windyboy
 */
public class PracticerCommand {

    public static ServerConstants.PlayerGMRank getPlayerLevelRequired() {
        return ServerConstants.PlayerGMRank.新實習生;
    }

    public static class WarpT extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            List<MapleCharacter> chrs = new LinkedList<>();
            List<MapleCharacter> chrs_cs = new LinkedList<>();
            String input = splitted[1].toLowerCase();
            MapleCharacter smart_victim = null;
            StringBuilder sb = new StringBuilder();
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharactersThreadSafe()) {
                    String name = chr.getName().toLowerCase();
                    if (name.contains(input)) {
                        if (smart_victim == null) {
                            smart_victim = chr;
                        }
                        chrs.add(chr);
                    }
                }
            }

            for (MapleCharacter chr : CashShopServer.getPlayerStorage().getAllCharactersThreadSafe()) {
                String name = chr.getName().toLowerCase();
                if (name.contains(input)) {
                    if (smart_victim == null) {
                        smart_victim = chr;
                    }
                    chrs_cs.add(chr);
                }
            }

            if (chrs.size() > 1 || chrs_cs.size() > 1) {
                sb.append("尋找到的玩家共").append(chrs.size() + chrs_cs.size()).append("位 名單如下 : ");
                c.getPlayer().dropMessage(5, sb.toString());
                for (MapleCharacter list : chrs) {
                    c.getPlayer().dropMessage(5, "頻道" + list.getClient().getChannel() + ": " + list.getName() + "(" + list.getId() + ") -- " + list.getMapId() + "(" + list.getMap().getMapName() + ")");
                }
                for (MapleCharacter list : chrs_cs) {
                    c.getPlayer().dropMessage(5, "頻道-10: " + list.getName() + "(" + list.getId() + ") -- " + list.getMapId() + "(商城內)");
                }
                return true;
            } else if (chrs.isEmpty()) {
                c.getPlayer().dropMessage(6, "沒有搜尋到名稱含有 '" + input + "' 的角色");
            } else if (smart_victim != null) {
                c.getPlayer().changeMap(smart_victim.getMap(), smart_victim.getMap().findClosestSpawnpoint(smart_victim.getTruePosition()));
            } else {
                c.getPlayer().dropMessage(6, "角色不存在或是不在線上");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("WarpT [玩家名稱片段] - 移動到某個地圖或某個玩家所在的地方").toString();
        }
    }

    public static class Warp extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {

            if (splitted.length < 2) {
                return false;
            }
            String input = "";
            try {
                input = splitted[1];
            } catch (Exception ex) {
            }
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(input);

            if (victim != null) {
                if (splitted.length == 2) {
                    c.getPlayer().changeMap(victim.getMap(), victim.getMap().findClosestSpawnpoint(victim.getPosition()));
                } else {
                    MapleMap target = null;
                    try {
                        target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(Integer.parseInt(splitted[2]));
                    } catch (Exception ex) {

                    }
                    if (target == null) {
                        c.getPlayer().dropMessage(6, "地圖不存在");
                    } else {
                        victim.changeMap(target, target.getPortal(0));
                    }

                }
            } else {
                int ch = World.Find.findChannel(input);
                if (ch < 0) {
                    Integer map = null;
                    MapleMap target = null;
                    try {
                        map = Integer.parseInt(input);
                        target = c.getChannelServer().getMapFactory().getMap(map);
                    } catch (Exception ex) {
                    }
                    if (target == null) {
                        victim = CashShopServer.getPlayerStorage().getCharacterByName(input);
                        if (victim == null) {
                            if (map == null) {
                                c.getPlayer().dropMessage(6, "地圖不存在");
                            } else {
                                c.getPlayer().dropMessage(6, "玩家「" + input + "」不在線上");
                            }
                        } else {
                            c.getPlayer().dropMessage("玩家「" + input + "」目前位於商城");
                        }
                    } else {
                        c.getPlayer().changeMap(target, target.getPortal(0));
                    }
                } else {
                    victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(input);
                    if (victim != null) {
                        if (victim.getMapId() != c.getPlayer().getMapId()) {
                            final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(victim.getMapId());
                            c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                        }
                        c.getPlayer().dropMessage(6, "正在改變頻道請等待");
                        c.getPlayer().changeChannel(ch);
                    } else {
                        c.getPlayer().dropMessage("角色不存在");
                    }
                }

            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("warp [玩家名稱] <地圖ID> - 移動到某個地圖或某個玩家所在的地方").toString();
        }
    }

    public static class WarpID extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            int input = 0;
            try {
                input = Integer.parseInt(splitted[1]);
            } catch (Exception ex) {

            }
            int ch = World.Find.findChannel(input);
            if (ch < 1) {
                MapleCharacter victim = CashShopServer.getPlayerStorage().getCharacterById(input);
                if (victim == null) {
                    c.getPlayer().dropMessage(6, "玩家編號[" + input + "] 不在線上");
                } else {
                    c.getPlayer().dropMessage("玩家編號「" + input + "」目前位於商城");
                }
                return true;
            }
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterById(input);
            if (victim != null) {
                if (splitted.length == 2) {
                    c.getPlayer().changeMap(victim.getMap(), victim.getMap().findClosestSpawnpoint(victim.getPosition()));
                } else {
                    MapleMap target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(Integer.parseInt(splitted[2]));
                    if (target == null) {
                        c.getPlayer().dropMessage(6, "地圖不存在");
                    } else {
                        victim.changeMap(target, target.getPortal(0));
                    }
                }
            } else {
                try {
                    try {
                        victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(Integer.parseInt(splitted[1]));
                    } catch (Exception e) {
                        c.getPlayer().dropMessage(6, "出問題了 " + e.getMessage());
                    }
                    if (victim != null) {
                        if (victim.getMapId() != c.getPlayer().getMapId()) {
                            final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(victim.getMapId());
                            c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                        }
                        c.getPlayer().dropMessage(6, "正在改變頻道請等待");
                        c.getPlayer().changeChannel(ch);

                    } else {
                        c.getPlayer().dropMessage("角色不存在");
                    }

                } catch (Exception e) {
                    c.getPlayer().dropMessage(6, "出問題了 " + e.getMessage());
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("warpID [玩家編號] - 移動到某個玩家所在的地方").toString();
        }
    }

    public static class Ban extends CommandExecute {

        protected boolean hellban = false;

        private String getCommand() {
            return "Ban";
        }

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            StringBuilder sb = new StringBuilder(c.getPlayer().getName());
            sb.append(" 封鎖 ").append(splitted[1]).append(": ").append(StringUtil.joinStringFrom(splitted, 2));
            boolean offline = false;
            MapleCharacter target = null;
            String name = "";
            String input = "null";
            try {
                name = splitted[1];
                input = splitted[2];
            } catch (Exception ex) {

            }
            int ch = World.Find.findChannel(name);
            if (ch >= 1) {
                target = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            } else {
                target = CashShopServer.getPlayerStorage().getCharacterByName(name);
            }
            if (target == null) {
                if (c.getPlayer().OfflineBanByName(name, sb.toString())) {
                    c.getPlayer().dropMessage(6, "[" + getCommand() + "] 成功離線封鎖 " + splitted[1] + ".");
                    offline = true;
                } else {
                    c.getPlayer().dropMessage(6, "[" + getCommand() + "] 封鎖失敗 " + splitted[1]);
                    return true;
                }
            } else if (Ban(c, target, sb) != 1) {
                return true;
            }
            FileoutputUtil.logToFile("logs/hack/指令封鎖名單.txt", "\r\n " + FileoutputUtil.NowTime() + " " + c.getPlayer().getName() + " 封鎖了 " + splitted[1] + " 原因: " + sb.toString() + " 是否離線封鎖: " + offline);
            String reason = "null".equals(input) ? "使用違法程式練功" : StringUtil.joinStringFrom(splitted, 2);
            World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "[封鎖系統] " + splitted[1] + " 因為" + reason + "而被管理員永久停權。"));

            String msg = "[GM 密語] GM " + c.getPlayer().getName() + "  封鎖了 " + splitted[1] + " 是否離線封鎖 " + offline + " 原因：" + reason;
            World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, msg));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("ban <玩家> <原因> - 封鎖玩家").toString();
        }

        public int Ban(MapleClient c, MapleCharacter target, StringBuilder sb) {
            if (c.getPlayer().getGMLevel() >= target.getGMLevel()) {
                sb.append(" (IP: ").append(target.getClient().getSessionIPAddress()).append(")");
                if (target.ban(sb.toString(), c.getPlayer().hasGmLevel(5), false, hellban)) {
                    target.getClient().getSession().close();
                    target.getClient().disconnect(true, target.getClient().getChannel() == -10);
                    c.getPlayer().dropMessage(6, "[" + getCommand() + "] 成功封鎖 " + target.getName() + ".");
                    return 1;
                } else {
                    c.getPlayer().dropMessage(6, "[" + getCommand() + "] 封鎖失敗.");
                    return 0;
                }
            } else {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] 無法封鎖GMs...");
                return 0;
            }
        }
    }

    public static class BanID extends CommandExecute {

        protected boolean hellban = false;

        private String getCommand() {
            return "Ban";
        }

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            StringBuilder sb = new StringBuilder(c.getPlayer().getName());
            sb.append(" 封鎖 ").append(splitted[1]).append(": ").append(StringUtil.joinStringFrom(splitted, 2));
            boolean offline = false;
            boolean ban = false;
            MapleCharacter target;
            int id = 0;
            String input = "null";
            try {
                id = Integer.parseInt(splitted[1]);
                input = splitted[2];
            } catch (Exception ex) {

            }
            int ch = World.Find.findChannel(id);
            String name = c.getPlayer().getCharacterNameById(id);
            target = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(id);
            if (target == null) {
                target = CashShopServer.getPlayerStorage().getCharacterById(id);
                if (target == null) {
                    if (c.getPlayer().OfflineBanById(id, sb.toString())) {
                        c.getPlayer().dropMessage(6, "[" + getCommand() + "] 成功離線封鎖 " + name + ".");
                        offline = true;
                    } else {
                        c.getPlayer().dropMessage(6, "[" + getCommand() + "] 封鎖失敗 " + splitted[1]);
                        return true;
                    }
                } else if (Ban(c, target, sb) != 1) {
                    return true;
                }
            } else {
                if (Ban(c, target, sb) != 1) {
                    return true;
                }
                name = target.getName();
            }

            FileoutputUtil.logToFile("logs/Hack/指令封鎖名單.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " " + c.getPlayer().getName() + " 封鎖了 " + name + " 原因: " + sb.toString() + " 是否離線封鎖: " + offline);
            String reason = "null".equals(input) ? "使用違法程式練功" : StringUtil.joinStringFrom(splitted, 2);
            World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "[封鎖系統] " + name + " 因為" + reason + "而被管理員永久停權。"));

            String msg = "[GM 密語] GM " + c.getPlayer().getName() + "  封鎖了 " + name + " 是否離線封鎖 " + offline + " 原因：" + reason;
            World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, msg));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("BanID <玩家ID> <原因> - 封鎖玩家").toString();
        }

        public int Ban(MapleClient c, MapleCharacter target, StringBuilder sb) {
            if (c.getPlayer().getGMLevel() >= target.getGMLevel()) {
                sb.append(" (IP: ").append(target.getClient().getSessionIPAddress()).append(")");
                if (target.ban(sb.toString(), c.getPlayer().hasGmLevel(5), false, hellban)) {
                    target.getClient().getSession().close();
                    target.getClient().disconnect(true, target.getClient().getChannel() == -10);
                    c.getPlayer().dropMessage(6, "[" + getCommand() + "] 成功封鎖 " + target.getName() + ".");
                    return 1;
                } else {
                    c.getPlayer().dropMessage(6, "[" + getCommand() + "] 封鎖失敗.");
                    return 0;
                }
            } else {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] 無法封鎖GMs...");
                return 0;
            }
        }
    }

    public static class CnGM extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, "<GM聊天視窗>" + "頻道" + c.getPlayer().getClient().getChannel() + " [" + c.getPlayer().getName() + "](" + c.getPlayer().getId() + ") : " + StringUtil.joinStringFrom(splitted, 1)));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("cngm <訊息> - GM聊天").toString();
        }
    }

    public static class MobSize extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            int size = c.getPlayer().getMap().mobCount();
            c.getPlayer().dropMessage(5, "當前地圖怪物數量總共有" + size + "隻");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("MobSize 查看當前地圖總共的怪物數量").toString();
        }
    }

    public static class Hide extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            SkillFactory.getSkill(9001004).getEffect(1).applyTo(c.getPlayer());
            c.getPlayer().dropMessage(6, "管理員隱藏 = 開啟 \r\n 解除請輸入!unhide");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("hide - 隱藏").toString();
        }
    }

    public static class UnHide extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().dispelBuff(9001004);
            c.getPlayer().dropMessage(6, "管理員隱藏 = 關閉 \r\n 開啟請輸入!hide");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("unhide - 解除隱藏").toString();
        }
    }

    public static class 精靈商人訊息 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter p = c.getPlayer();
            boolean x = p.getmsg_HiredMerchant();
            if (x) {
                p.setmsg_HiredMerchant(false);
            } else {
                p.setmsg_HiredMerchant(true);
            }
            x = p.getmsg_HiredMerchant();
            p.dropMessage("目前精靈商人購買訊息狀態: " + (x ? "開啟 " : " 關閉 ") + "");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("精靈商人訊息 - 開啟精靈商人購買訊息顯示").toString();
        }
    }

    public static class 玩家私聊 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter p = c.getPlayer();
            boolean x = p.getmsg_Chat();
            if (x) {
                p.setmsg_Chat(false);
            } else {
                p.setmsg_Chat(true);
            }
            x = p.getmsg_Chat();
            p.dropMessage("目前玩家私聊狀態: " + (x ? "開啟 " : "關閉 ") + "");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("玩家私聊 - 開啟玩家訊息顯示").toString();
        }
    }
    
    public static class 玩家刪角紀錄 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter p = c.getPlayer();
            boolean x = p.getDelChrLog();
            if (x) {
                p.set_DelChrLog(false);
            } else {
                p.set_DelChrLog(true);
            }
            x = p.getDelChrLog();
            p.dropMessage("目前玩家刪角紀錄狀態: " + (x ? "開啟 " : "關閉 ") + "");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("玩家刪角紀錄 - 開啟玩家刪除顯示").toString();
        }
    }
    
    public static class online extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            Integer job = CommandProcessorUtil.getNamedIntArg(splitted, 1, "job");
            Integer job2 = CommandProcessorUtil.getNamedIntArg(splitted, 1, "job2");
            Integer level = CommandProcessorUtil.getNamedIntArg(splitted, 1, "lv");
            Integer level2 = CommandProcessorUtil.getNamedIntArg(splitted, 1, "lv2");
            Integer meso = CommandProcessorUtil.getNamedIntArg(splitted, 1, "meso");
            Integer meso2 = CommandProcessorUtil.getNamedIntArg(splitted, 1, "meso2");
            String name = CommandProcessorUtil.getNamedStringArg(splitted, 1, "name");
            boolean showAllChannel = false;
            boolean level_limit = false;
            boolean job_limit = false;
            boolean meso_limit = false;
            boolean name_limit = false;
            int total = 0;
            int curConnected = c.getChannelServer().getConnectedClients();

            if (job != null && job2 != null) {
                job_limit = true;
                showAllChannel = true;
            }
            if (level != null && level2 != null) {
                level_limit = true;
                showAllChannel = true;
            }
            if (meso != null && meso2 != null) {
                meso_limit = true;
                showAllChannel = true;
            }
            if (name != null) {
                name_limit = true;
                showAllChannel = true;
            }

            c.getPlayer().dropMessage(6, "-------------------------------------------------------------------------------------");
            c.getPlayer().dropMessage(6, new StringBuilder().append("頻道: ").append(c.getChannelServer().getChannel()).append(" 線上人數: ").append(curConnected).toString());
            total += curConnected;
            if (!showAllChannel) {
                for (MapleCharacter chr : c.getChannelServer().getPlayerStorage().getAllCharactersThreadSafe()) {
                    if (chr == null) {
                        continue;
                    } else if (c.getPlayer().getGMLevel() < chr.getGMLevel()) {
                        continue;
                    }
                    StringBuilder ret = new StringBuilder();
                    ret.append(" 角色暱稱 ");
                    ret.append(StringUtil.getRightPaddedStr(chr.getName(), ' ', 13));
                    ret.append(" ID: ");
                    ret.append(StringUtil.getRightPaddedStr(chr.getId() + "", ' ', 5));
                    ret.append(" 等級: ");
                    ret.append(StringUtil.getRightPaddedStr(String.valueOf(chr.getLevel()), ' ', 3));
                    ret.append(" 職業: ");
                    ret.append(StringUtil.getRightPaddedStr(String.valueOf(chr.getJob()), ' ', 4));
                    if (chr.getMap() != null) {
                        ret.append(" 地圖: ");
                        ret.append(chr.getMapId());
                        c.getPlayer().dropMessage(6, ret.toString());
                    }

                }
            } else {
                for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                    for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharactersThreadSafe()) {
                        if (chr == null) {
                            continue;
                        } else if (job_limit && (chr.getJob() < job || chr.getJob() > job2)) {
                            continue;
                        } else if (level_limit && (chr.getLevel() < level || chr.getLevel() > level2)) {
                            continue;
                        } else if (meso_limit && (chr.getMeso() < meso || chr.getMeso() > meso)) {
                            continue;
                        } else if (name_limit && name != null && (chr.getName().toLowerCase().contains(name.toLowerCase()))) {
                            continue;
                        }

                        StringBuilder ret = new StringBuilder();
                        ret.append("名稱 ");
                        ret.append(StringUtil.getRightPaddedStr(chr.getName(), ' ', 13));
                        ret.append(" ID: ");
                        ret.append(StringUtil.getRightPaddedStr(chr.getId() + "", ' ', 5));
                        ret.append(" 等級: ");
                        ret.append(StringUtil.getRightPaddedStr(String.valueOf(chr.getLevel()), ' ', 3));
                        ret.append(" 職業: ");
                        ret.append(StringUtil.getRightPaddedStr(String.valueOf(chr.getJob()), ' ', 4));
                        if (meso_limit) {
                            ret.append(" 楓幣: ");
                            ret.append(StringUtil.getRightPaddedStr(String.valueOf(chr.getMeso()), ' ', 10));
                            c.getPlayer().dropMessage(6, ret.toString());
                        } else if (chr.getMap() != null) {
                            ret.append(" 地圖: ");
                            ret.append(chr.getMapId());
                            c.getPlayer().dropMessage(6, ret.toString());
                        }
                    }
                }
            }
            c.getPlayer().dropMessage(6, new StringBuilder().append("當前頻道總計線上人數: ").append(total).toString());
            c.getPlayer().dropMessage(6, "-------------------------------------------------------------------------------------");

            int totalOnline = 0;
            /*伺服器總人數*/
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                totalOnline += cserv.getConnectedClients();
            }

            c.getPlayer().dropMessage(6, new StringBuilder().append("當前伺服器總計線上人數: ").append(totalOnline).append("個").toString());
            c.getPlayer().dropMessage(6, "-------------------------------------------------------------------------------------");

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("online - 查看線上人數").toString();
        }
    }

    public static class onlineGM extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            int channelOnline = 0;
            int totalOnline = 0;
            int GmInChannel = 0;
            List<MapleCharacter> chrs = new LinkedList<>();

            /*當前頻道總GM數*/
            for (MapleCharacter chr : c.getChannelServer().getPlayerStorage().getAllCharactersThreadSafe()) {
                if (chr.getGMLevel() > 0) {
                    channelOnline++;
                }
            }
            /*伺服器總GM數*/
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharactersThreadSafe()) {
                    if (chr != null && chr.getGMLevel() > 0) {
                        totalOnline++;
                    }
                }
            }
            c.getPlayer().dropMessage(6, "-------------------------------------------------------------------------------------");
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharactersThreadSafe()) {
                    if (chr != null && chr.getGMLevel() > 0) {
                        chrs.add(chr);
                    }
                }
                GmInChannel = chrs.size();
                if (GmInChannel > 0) {
                    c.getPlayer().dropMessage(6, new StringBuilder().append("頻道: ").append(cserv.getChannel()).append(" 線上GM人數: ").append(GmInChannel).toString());
                    for (MapleCharacter chr : chrs) {
                        if (chr != null) {
                            StringBuilder ret = new StringBuilder();
                            ret.append(" GM暱稱 ");
                            ret.append(StringUtil.getRightPaddedStr(chr.getName(), ' ', 13));
                            ret.append(" ID: ");
                            ret.append(StringUtil.getRightPaddedStr(chr.getId() + "", ' ', 5));
                            ret.append(" 權限: ");
                            ret.append(StringUtil.getRightPaddedStr(String.valueOf(chr.getGMLevel()), ' ', 3));
                            c.getPlayer().dropMessage(6, ret.toString());
                        }
                    }
                }
                chrs = new LinkedList<>();
            }
            c.getPlayer().dropMessage(6, new StringBuilder().append("當前頻道總計GM線上人數: ").append(channelOnline).toString());
            c.getPlayer().dropMessage(6, "-------------------------------------------------------------------------------------");

            c.getPlayer().dropMessage(6, new StringBuilder().append("當前伺服器GM總計線上人數: ").append(totalOnline).append("個").toString());
            c.getPlayer().dropMessage(6, "-------------------------------------------------------------------------------------");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("onlineGM - 查看線上人數GM").toString();
        }
    }

    public static class WarpHere extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null) {
                victim.changeMap(c.getPlayer().getMap(), c.getPlayer().getMap().findClosestSpawnpoint(c.getPlayer().getPosition()));
            } else {
                int ch = World.Find.findChannel(splitted[1]);
                if (ch < 0) {
                    c.getPlayer().dropMessage(5, "找不到");

                } else {
                    victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[1]);
                    c.getPlayer().dropMessage(5, "正在把玩家傳到這來");
                    victim.dropMessage(5, "正在傳送到GM那邊");
                    if (victim.getMapId() != c.getPlayer().getMapId()) {
                        final MapleMap mapp = victim.getClient().getChannelServer().getMapFactory().getMap(c.getPlayer().getMapId());
                        victim.changeMap(mapp, mapp.getPortal(0));
                    }
                    victim.changeChannel(c.getChannel());
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("warphere 把玩家傳送到這裡").toString();
        }
    }

    public static class Whoshere extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            StringBuilder builder = new StringBuilder("在地圖上的玩家 : ").append(c.getPlayer().getMap().getCharactersThreadsafe().size()).append(", ");
            for (MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (builder.length() > 150) { // wild guess :o
                    builder.setLength(builder.length() - 2);
                    c.getPlayer().dropMessage(6, builder.toString());
                    builder = new StringBuilder();
                }
                builder.append(MapleCharacterUtil.makeMapleReadable(chr.getName()));
                builder.append(", ");
            }
            builder.setLength(builder.length() - 2);
            c.getPlayer().dropMessage(6, builder.toString());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("Whoshere - 查地圖上玩家").toString();
        }
    }

    public static class UnHellBan extends UnBan {

        public UnHellBan() {
            hellban = true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("UnHellBan <玩家> - 解鎖玩家").toString();
        }
    }

    public static class UnBan extends CommandExecute {

        protected boolean hellban = false;

        private String getCommand() {
            return "UnBan";
        }

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            byte ret;
            if (hellban) {
                ret = MapleClient.unHellban(splitted[1]);
            } else {
                ret = MapleClient.unban(splitted[1]);
            }
            if (ret == -2) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] SQL 錯誤");
            } else if (ret == -1) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] 目標玩家不存在");
            } else {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] 成功解除鎖定");
            }
            byte ret_ = MapleClient.unbanIPMacs(splitted[1]);
            if (ret_ == -2) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] SQL 錯誤.");
            } else if (ret_ == -1) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] 角色不存在.");
            } else if (ret_ == 0) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] No IP or Mac with that character exists!");
            } else if (ret_ == 1) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] IP或Mac已解鎖其中一個.");
            } else if (ret_ == 2) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] IP以及Mac已成功解鎖.");
            }
            if (ret_ == 1 || ret_ == 2) {
                FileoutputUtil.logToFile("logs/Hack/ban/解除封鎖名單.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " " + c.getPlayer().getName() + " 解鎖了 " + splitted[1]
                );
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("unban <玩家> - 解鎖玩家").toString();
        }
    }

    public static class DCID extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter victim;
            int id = Integer.parseInt(splitted[1]);
            int ch = World.Find.findChannel(id);
            if (ch <= 0 && ch != -10) {
                c.getPlayer().dropMessage("該玩家不在線上");
                return true;
            }
            if (ch == -10) {
                victim = CashShopServer.getPlayerStorage().getCharacterById(id);
            } else {
                victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(id);
            }
            if (victim != null) {
                victim.getClient().disconnect(true, ch == -10);
                victim.getClient().getSession().close();
            } else {
                c.getPlayer().dropMessage("該玩家不在線上");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("DCID <玩家ID> - 讓玩家斷線").toString();
        }
    }

    public static class DC extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter victim;
            String name = splitted[1];
            int ch = World.Find.findChannel(name);
            if (ch <= 0 && ch != -10) {
                c.getPlayer().dropMessage("該玩家不在線上");
                return true;
            }
            if (ch == -10) {
                victim = CashShopServer.getPlayerStorage().getCharacterByName(name);
            } else {
                victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            }
            if (victim != null) {
                victim.getClient().disconnect(true, ch == -10);
                victim.getClient().getSession().close();
            } else {
                c.getPlayer().dropMessage("該玩家不在線上");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("dc <玩家> - 讓玩家斷線").toString();
        }
    }

    public static class DC2 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter victim;
            String name = splitted[1];
            int ch = World.Find.findChannel(name);
            if (ch <= 0 && ch != -10) {
                c.getPlayer().dropMessage("該玩家不在線上");
                return true;
            }
            if (ch == -10) {
                victim = CashShopServer.getPlayerStorage().getCharacterByName(name);
            } else {
                victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            }
            if (victim != null) {
                victim.getClient().sendPacket(NPCPacket.getNPCTalk(99, (byte) 0, "", "00 00", (byte) 0));
            } else {
                c.getPlayer().dropMessage("該玩家不在線上");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("dc2 <玩家> - 讓玩家-2147斷線").toString();
        }
    }

    public static class Job extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            int jobid = 0;
            try {
                jobid = Integer.parseInt(splitted[1]);
            } catch (Exception ex) {
                return false;
            }
            c.getPlayer().changeJob(jobid);
            c.getPlayer().dispelDebuffs();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("job <職業代碼> - 更換職業").toString();
        }
    }

    public static class WhereAmI extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().dropMessage(5, "目前地圖 " + c.getPlayer().getMap().getId() + "座標 (" + String.valueOf(c.getPlayer().getPosition().x) + " , " + String.valueOf(c.getPlayer().getPosition().y) + ")");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("whereami - 目前地圖").toString();
        }
    }

    public static class BanStatus extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            String name = splitted[1];
            String mac = "";
            String ip = "";
            int acid = 0, Macs = 0;
            boolean Systemban = false;
            boolean ACbanned = false;
            boolean IPbanned = false;
            boolean npc_show = false;
            String reason = null;

            if (splitted.length > 2) {
                if (splitted[2].equalsIgnoreCase("npc")) {
                    npc_show = true;
                }
            }
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps;
                ps = con.prepareStatement("select accountid from characters where name = ?");
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        acid = rs.getInt("accountid");
                    }
                }
                ps = con.prepareStatement("select banned, banreason, macs, Sessionip from accounts where id = ?");
                ps.setInt(1, acid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Systemban = rs.getInt("banned") == 2;
                        ACbanned = rs.getInt("banned") == 1 || rs.getInt("banned") == 2;
                        reason = rs.getString("banreason");
                        mac = rs.getString("macs");
                        ip = rs.getString("Sessionip");
                    }
                }
                ps.close();
            } catch (Exception e) {
            }
            if (reason == null || reason.isEmpty()) {
                reason = "無";
            }
            if (c.isBannedIP(ip)) {
                IPbanned = true;
            }

            String msg1 = "玩家[" + name + "] 帳號ID[" + acid + "]是否被封鎖: " + (ACbanned ? "是" : "否") + (Systemban ? "(系統自動封鎖)" : "") + ", 原因: " + reason;
            String msg2 = "目前IP: " + ip + " 是否在封鎖IP名單: " + (IPbanned ? "是" : "否");
            String msg3 = "";
            if (npc_show) {
                if (mac.contains(",")) {
                    for (String SingleMac : mac.split(", ")) {
                        msg3 += "MAC: " + SingleMac + " 在封鎖MAC名單: " + (c.isBannedMac(SingleMac) ? "是" : "否") + "\r\n";
                    }
                } else {
                    msg3 += "MAC: " + mac + " 在封鎖MAC名單: " + (c.isBannedMac(mac) ? "是" : "否") + "\r\n";
                }
                c.getPlayer().dropNPC(msg1 + "\r\n" + msg2 + "\r\n" + msg3);
            } else {
                c.getPlayer().dropMessage(msg1);
                c.getPlayer().dropMessage(msg2);
                if (mac.contains(",")) {
                    for (String SingleMac : mac.split(", ")) {
                        msg3 = "MAC: " + SingleMac + " 在封鎖MAC名單: " + (c.isBannedMac(SingleMac) ? "是" : "否");
                        c.getPlayer().dropMessage(msg3);
                    }
                } else {
                    msg3 = "MAC: " + mac + " 在封鎖MAC名單: " + (c.isBannedMac(mac) ? "是" : "否");
                    c.getPlayer().dropMessage(msg3);
                }
            }

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("BanStatus <玩家名稱> <npc> - 查看玩家是否被封鎖及原因").toString();
        }
    }

    public static class banMac extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            String mac = splitted[1];
            if (mac.equalsIgnoreCase("00-00-00-00-00-00") || mac.length() != 17) {
                c.getPlayer().dropMessage("封鎖MAC失敗，可能為格式錯誤或是長度錯誤 Ex: 00-00-00-00-00-00 ");
                return true;
            }
            c.getPlayer().dropMessage("封鎖MAC [" + mac + "] 成功");
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO macbans (mac) VALUES (?)")) {
                ps.setString(1, mac);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                System.err.println("Error banning MACs" + e);
                return true;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("BanMAC <MAC> - 封鎖MAC ").toString();
        }
    }

    public static class BanIP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            boolean error = false;
            String IP = splitted[1];
            if (!IP.contains("/") || !IP.contains(".")) {
                c.getPlayer().dropMessage("輸入IP必須包含 '/' 以及 '.' 例如: !banIP /127.0.0.1");
                return true;
            }
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps;
                ps = con.prepareStatement("INSERT INTO ipbans (ip) VALUES (?)");
                ps.setString(1, IP);
                ps.executeUpdate();
                ps.close();
            } catch (Exception ex) {
                error = true;
            }
            try {
                for (ChannelServer cs : ChannelServer.getAllInstances()) {
                    for (MapleCharacter chr : cs.getPlayerStorage().getAllCharactersThreadSafe()) {
                        if (chr.getClient().getSessionIPAddress().equals(IP)) {
                            if (!chr.getClient().isGm()) {
                                chr.getClient().disconnect(true, chr.getClient().getChannel() == -10);
                                chr.getClient().getSession().close();
                            }
                        }
                    }
                }
            } catch (Exception ex) {

            }
            c.getPlayer().dropMessage("封鎖IP [" + IP + "] " + (error ? "成功 " : "失敗"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("BanIP <IP> - 封鎖IP ").toString();
        }
    }

    public static class onlinedelay extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            List<MapleCharacter> allchrs = new LinkedList<>();
            boolean allch = false;

            if (splitted.length > 1) {
                if (splitted[1].equalsIgnoreCase("all")) {
                    allch = true;
                }
            }

            if (allch) {
                ChannelServer.getAllInstances().forEach((cserv) -> {
                    allchrs.addAll(cserv.getPlayerStorage().getAllCharactersThreadSafe());
                });
            } else {
                allchrs.addAll(c.getChannelServer().getPlayerStorage().getAllCharactersThreadSafe());
            }
            StringBuilder allret = new StringBuilder();
            for (MapleCharacter chr : allchrs) {
                if (chr.getClient().getLatency() == 0) {
                    continue;
                }
                StringBuilder ret = new StringBuilder();
                ret.append(" 名稱 ");
                ret.append(StringUtil.getRightPaddedStr(chr.getName(), ' ', 13));
                ret.append(" 等級: ");
                ret.append(StringUtil.getRightPaddedStr(String.valueOf(chr.getLevel()), ' ', 3));
                ret.append(" 延遲: ");
                ret.append(chr.getClient().getLatency());
                ret.append("ms");
                allret.append(FileoutputUtil.CurrentReadable_Time()).append(" ").append(ret).append("\r\n");
                c.getPlayer().dropMessage(6, ret.toString());
            }
            FileoutputUtil.logToFile("logs/data/Delay.txt", allret.toString());

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("onlinedelay (all) - 查看當前/全部頻道的玩家延遲").toString();
        }
    }

    public static class 發直播 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }

            String playername = splitted[1];
            int playerid = MapleCharacter.getCharacterIdByName(playername);
           // c.getPlayer().dropMessage("ＷＨＡＡＡＡ1");
            if (playerid == -1) {
                c.getPlayer().dropMessage("玩家[" + playername + "]不存在於資料庫內。");
                return true;
            }
            MapleCharacter victim = MapleCharacter.getCharacterById(playerid);
          //  c.getPlayer().dropMessage("ＷＨＡＡＡＡ2");
            if (victim == null){
                c.getPlayer().dropMessage("玩家[" + playername + "]null，ＷＴＦ？");
                return true;
            }
            int accountid = victim.getAccountID();
           // c.getPlayer().dropMessage("ＷＨＡＡＡＡ3");
            int id = createPlayer(accountid, playerid, playername);
           // c.getPlayer().dropMessage("ＷＨＡＡＡＡ4");

            if (id < 0) {
                switch (id) {
                    case -3:
                        c.getPlayer().dropMessage("帳號ID[" + accountid + "]已經在領取勳章清單內。");
                        break;
                    case -2:
                        c.getPlayer().dropMessage("玩家[" + playername + "]已經在領取勳章清單內。");
                        break;
                    case -1:
                        c.getPlayer().dropMessage("發生未知的錯誤。");
                        break;
                    default:
                        c.getPlayer().dropMessage("發生未知的未知的錯誤, id:" + id + " accountid: " + accountid + " playerid: " + playerid + " playername: " + playername);
                        break;
                }
                return true;
            } else {
                c.getPlayer().dropMessage("玩家[" + playername + "]已經成功添加到領取勳章清單內(編號" + id + ")。");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("發直播 <玩家名稱> - 將勳章領取名單添加玩家").toString();
        }

        public int createPlayer(int accountid, int id, String name) {
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT id FROM livemedals WHERE name = ?");
                ps.setString(1, name);
                ResultSet rs = ps.executeQuery();
                if (rs.first()) {// 角色已經存在於勳章表單
                    rs.close();
                    ps.close();
                    return -2;
                }
                ps.close();
                rs.close();

                ps = con.prepareStatement("SELECT accountid FROM livemedals WHERE accountid = ?");
                ps.setInt(1, accountid);
                rs = ps.executeQuery();
                if (rs.first()) {// 帳號已經存在於勳章表單
                    rs.close();
                    ps.close();
                    return -3;
                }
                ps.close();
                rs.close();

                ps = con.prepareStatement("INSERT INTO livemedals (`accountid`,`id`, `name`, `amount`) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, accountid);
                ps.setInt(2, id);
                ps.setString(3, name);
                ps.setInt(4, 1);
                ps.execute();
                rs = ps.getGeneratedKeys();
                int ret = 0;
                if (rs.next()) {
                    ret = rs.getInt(1);
                }
                rs.close();
                ps.close();
                return ret;
            } catch (Exception ex) {
                FilePrinter.printError("InternCommand.txt", ex, "createPlayer(" + name + ")");

            }
            return -1;
        }
    }
    public static class 發分享 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }

            String playername = splitted[1];
            int playerid = MapleCharacter.getCharacterIdByName(playername);
           // c.getPlayer().dropMessage("ＷＨＡＡＡＡ1");
            if (playerid == -1) {
                c.getPlayer().dropMessage("玩家[" + playername + "]不存在於資料庫內。");
                return true;
            }
            MapleCharacter victim = MapleCharacter.getCharacterById(playerid);
           // c.getPlayer().dropMessage("ＷＨＡＡＡＡ2");
            if (victim == null){
                c.getPlayer().dropMessage("玩家[" + playername + "]null，ＷＴＦ？");
                return true;
            }
            int accountid = victim.getAccountID();
           // c.getPlayer().dropMessage("ＷＨＡＡＡＡ3");
            int id = createPlayer(accountid, playerid, playername);
        //    c.getPlayer().dropMessage("ＷＨＡＡＡＡ4");

            if (id < 0) {
                switch (id) {
                    case -3:
                        c.getPlayer().dropMessage("帳號ID[" + accountid + "]已經在領取勳章清單內。");
                        break;
                    case -2:
                        c.getPlayer().dropMessage("玩家[" + playername + "]已經在領取勳章清單內。");
                        break;
                    case -1:
                        c.getPlayer().dropMessage("發生未知的錯誤。");
                        break;
                    default:
                        c.getPlayer().dropMessage("發生未知的未知的錯誤, id:" + id + " accountid: " + accountid + " playerid: " + playerid + " playername: " + playername);
                        break;
                }
                return true;
            } else {
                c.getPlayer().dropMessage("玩家[" + playername + "]已經成功添加到領取勳章清單內(編號" + id + ")。");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("發分享 <玩家名稱> - 將勳章領取名單添加玩家").toString();
        }

        public int createPlayer(int accountid, int id, String name) {
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT id FROM fbmedals WHERE name = ?");
                ps.setString(1, name);
                ResultSet rs = ps.executeQuery();
                if (rs.first()) {// 角色已經存在於勳章表單
                    rs.close();
                    ps.close();
                    return -2;
                }
                ps.close();
                rs.close();

                ps = con.prepareStatement("SELECT accountid FROM fbmedals WHERE accountid = ?");
                ps.setInt(1, accountid);
                rs = ps.executeQuery();
                if (rs.first()) {// 帳號已經存在於勳章表單
                    rs.close();
                    ps.close();
                    return -3;
                }
                ps.close();
                rs.close();

                ps = con.prepareStatement("INSERT INTO fbmedals (`accountid`,`id`, `name`, `amount`) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, accountid);
                ps.setInt(2, id);
                ps.setString(3, name);
                ps.setInt(4, 1);
                ps.execute();
                rs = ps.getGeneratedKeys();
                int ret = 0;
                if (rs.next()) {
                    ret = rs.getInt(1);
                }
                rs.close();
                ps.close();
                return ret;
            } catch (Exception ex) {
                FilePrinter.printError("InternCommand.txt", ex, "createPlayer(" + name + ")");

            }
            return -1;
        }
    }

    public static class 發推廣 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }

            String playername = splitted[1];
            int playerid = MapleCharacter.getCharacterIdByName(playername);
          //  c.getPlayer().dropMessage("ＷＨＡＡＡＡ1");
            if (playerid == -1) {
                c.getPlayer().dropMessage("玩家[" + playername + "]不存在於資料庫內。");
                return true;
            }
            MapleCharacter victim = MapleCharacter.getCharacterById(playerid);
           // c.getPlayer().dropMessage("ＷＨＡＡＡＡ2");
            if (victim == null){
                c.getPlayer().dropMessage("玩家[" + playername + "]null，ＷＴＦ？");
                return true;
            }
            int accountid = victim.getAccountID();
          //  c.getPlayer().dropMessage("ＷＨＡＡＡＡ3");
            int id = createPlayer(accountid, playerid, playername);
          //  c.getPlayer().dropMessage("ＷＨＡＡＡＡ4");

            if (id < 0) {
                switch (id) {
                    case -3:
                        c.getPlayer().dropMessage("帳號ID[" + accountid + "]已經在領取勳章清單內。");
                        break;
                    case -2:
                        c.getPlayer().dropMessage("玩家[" + playername + "]已經在領取勳章清單內。");
                        break;
                    case -1:
                        c.getPlayer().dropMessage("發生未知的錯誤。");
                        break;
                    default:
                        c.getPlayer().dropMessage("發生未知的未知的錯誤, id:" + id + " accountid: " + accountid + " playerid: " + playerid + " playername: " + playername);
                        break;
                }
                return true;
            } else {
                c.getPlayer().dropMessage("玩家[" + playername + "]已經成功添加到領取勳章清單內(編號" + id + ")。");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("發RC <玩家名稱> - 將勳章領取名單添加玩家").toString();
        }

        public int createPlayer(int accountid, int id, String name) {
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT id FROM rcmedals WHERE name = ?");
                ps.setString(1, name);
                ResultSet rs = ps.executeQuery();
                if (rs.first()) {// 角色已經存在於勳章表單
                    rs.close();
                    ps.close();
                    return -2;
                }
                ps.close();
                rs.close();

                ps = con.prepareStatement("SELECT accountid FROM rcmedals WHERE accountid = ?");
                ps.setInt(1, accountid);
                rs = ps.executeQuery();
                if (rs.first()) {// 帳號已經存在於勳章表單
                    rs.close();
                    ps.close();
                    return -3;
                }
                ps.close();
                rs.close();

                ps = con.prepareStatement("INSERT INTO rcmedals (`accountid`,`id`, `name`, `amount`) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, accountid);
                ps.setInt(2, id);
                ps.setString(3, name);
                ps.setInt(4, 1);
                ps.execute();
                rs = ps.getGeneratedKeys();
                int ret = 0;
                if (rs.next()) {
                    ret = rs.getInt(1);
                }
                rs.close();
                ps.close();
                return ret;
            } catch (Exception ex) {
                FilePrinter.printError("InternCommand.txt", ex, "createPlayer(" + name + ")");

            }
            return -1;
        }
    }

    public static class 發RC1000 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }

            String playername = splitted[1];
            int playerid = MapleCharacter.getCharacterIdByName(playername);
            c.getPlayer().dropMessage("ＷＨＡＡＡＡ1");
            if (playerid == -1) {
                c.getPlayer().dropMessage("玩家[" + playername + "]不存在於資料庫內。");
                return true;
            }
            MapleCharacter victim = MapleCharacter.getCharacterById(playerid);
            c.getPlayer().dropMessage("ＷＨＡＡＡＡ2");
            if (victim == null){
                c.getPlayer().dropMessage("玩家[" + playername + "]null，ＷＴＦ？");
                return true;
            }
            int accountid = victim.getAccountID();
            c.getPlayer().dropMessage("ＷＨＡＡＡＡ3");
            int id = createPlayer(accountid, playerid, playername);
            c.getPlayer().dropMessage("ＷＨＡＡＡＡ4");

            if (id < 0) {
                switch (id) {
                    case -3:
                        c.getPlayer().dropMessage("帳號ID[" + accountid + "]已經在領取勳章清單內。");
                        break;
                    case -2:
                        c.getPlayer().dropMessage("玩家[" + playername + "]已經在領取勳章清單內。");
                        break;
                    case -1:
                        c.getPlayer().dropMessage("發生未知的錯誤。");
                        break;
                    default:
                        c.getPlayer().dropMessage("發生未知的未知的錯誤, id:" + id + " accountid: " + accountid + " playerid: " + playerid + " playername: " + playername);
                        break;
                }
                return true;
            } else {
                c.getPlayer().dropMessage("玩家[" + playername + "]已經成功添加到領取勳章清單內(編號" + id + ")。");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("發RC <玩家名稱> - 將勳章領取名單添加玩家").toString();
        }

        public int createPlayer(int accountid, int id, String name) {
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT id FROM rcmedals1000 WHERE name = ?");
                ps.setString(1, name);
                ResultSet rs = ps.executeQuery();
                if (rs.first()) {// 角色已經存在於勳章表單
                    rs.close();
                    ps.close();
                    return -2;
                }
                ps.close();
                rs.close();

                ps = con.prepareStatement("SELECT accountid FROM rcmedals1000 WHERE accountid = ?");
                ps.setInt(1, accountid);
                rs = ps.executeQuery();
                if (rs.first()) {// 帳號已經存在於勳章表單
                    rs.close();
                    ps.close();
                    return -3;
                }
                ps.close();
                rs.close();

                ps = con.prepareStatement("INSERT INTO rcmedals1000 (`accountid`,`id`, `name`, `amount`) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, accountid);
                ps.setInt(2, id);
                ps.setString(3, name);
                ps.setInt(4, 1);
                ps.execute();
                rs = ps.getGeneratedKeys();
                int ret = 0;
                if (rs.next()) {
                    ret = rs.getInt(1);
                }
                rs.close();
                ps.close();
                return ret;
            } catch (Exception ex) {
                FilePrinter.printError("InternCommand.txt", ex, "createPlayer(" + name + ")");

            }
            return -1;
        }
    }

 public static class logindoor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            ServerConstants.isAdminOnly = !ServerConstants.isAdminOnly;
            c.getPlayer().dropMessage(0, "[logindoor] " + (ServerConstants.isAdminOnly ? "開啟" : "關閉"));
            System.out.println("[logindoor] " + (ServerConstants.isAdminOnly ? "開啟" : "關閉"));
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("logindoor  - 管理員登入模式開關").toString();
        }
    }
    public static class 禁止玩家使用 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            ServerConstants.isShutdown = !ServerConstants.isShutdown;
            c.getPlayer().dropMessage(6, "[禁止玩家使用] " + (ServerConstants.isShutdown ? "開啟" : "關閉"));
            System.out.println("[禁止玩家使用] " + (ServerConstants.isShutdown ? "開啟" : "關閉"));
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("禁止玩家使用  - 禁止玩家操作任何動作").toString();
        }
    }

    public static class 禁止使用商店 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            ServerConstants.Disable_Shop = !ServerConstants.Disable_Shop;
            c.getPlayer().dropMessage(6, "[禁止使用商店] " + (ServerConstants.Disable_Shop ? "開啟" : "關閉"));
            System.out.println("[禁止使用商店] " + (ServerConstants.Disable_Shop ? "開啟" : "關閉"));
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("禁止使用商店  - 禁止玩家操作任何商店").toString();
        }
    }

    public static class Speak extends CommandExecute {

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
                c.getPlayer().dropMessage(5, "找不到 '" + splitted[1]);
                return false;
            } else {
                victim.getMap().broadcastMessage(CField.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 2), victim.isGM(), 0));
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("speak <玩家名稱> <訊息> - 對某個玩家傳訊息").toString();
        }
    }

    public static class SpeakMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (MapleCharacter victim : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (victim.getId() != c.getPlayer().getId()) {
                    victim.getMap().broadcastMessage(CField.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), 0));
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("speakmap <訊息> - 對目前地圖進行傳送訊息").toString();
        }

    }

    public static class SpeakChannel extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (MapleCharacter victim : c.getChannelServer().getPlayerStorage().getAllCharactersThreadSafe()) {
                if (victim.getId() != c.getPlayer().getId()) {
                    victim.getMap().broadcastMessage(CField.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), 0));
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("speakchannel <訊息> - 對目前頻道進行傳送訊息").toString();
        }

    }

    public static class SpeakWorld extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter victim : cserv.getPlayerStorage().getAllCharactersThreadSafe()) {
                    if (victim.getId() != c.getPlayer().getId()) {
                        victim.getMap().broadcastMessage(CField.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), 0));
                    }
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("speakchannel <訊息> - 對目前伺服器進行傳送訊息").toString();
        }
    }

    public static class Say extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                sb.append(c.getPlayer().getName());
                sb.append("] ");
                sb.append(StringUtil.joinStringFrom(splitted, 1));
                World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, sb.toString()));
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("say 訊息 - 伺服器公告").toString();
        }
    }

    public static class Shutdown extends CommandExecute {

        private static Thread t = null;

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().dropMessage(6, "關閉伺服器中...");
            if (t == null || !t.isAlive()) {
                t = new Thread(server.ShutdownServer.getInstance());
                t.start();
            } else {
                c.getPlayer().dropMessage(6, "已在執行中...");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("shutdown - 關閉伺服器").toString();
        }
    }

    public static class ShutdownTime extends CommandExecute {

        private static ScheduledFuture<?> ts = null;
        private static Thread t = null;
        private int minutesLeft = 0;

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }

            minutesLeft = Integer.parseInt(splitted[1]);
            String stop = splitted[2];

            Timer.ShutdownTimer.getInstance().start();
            if (ts == null && (t == null || !t.isAlive()) || stop.equalsIgnoreCase("stop")) {
                t = new Thread(ShutdownServer.getInstance());
                ts = Timer.ShutdownTimer.getInstance().register(new Runnable() {

                    @Override
                    public void run() {
                        if (stop.equalsIgnoreCase("stop")) {
                            minutesLeft = 0;
                            World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "伺服器預定的倒數關機事件已取消，大家請安心遊玩!"));
                            System.out.println("伺服器將於已取消倒數關機線程..");
                            t.stop();
                            ts.cancel(false);
                            Timer.ShutdownTimer.getInstance().stop();
                            return;
                        }
                        if (!ServerConstants.Disable_Shop) {
                            ServerConstants.Disable_Shop = true;
                            if (c != null && c.getPlayer() != null) {
                                c.getPlayer().dropMessage(6, "已經限制玩家使用個人商店、精靈商人。");
                            }
                        }

                        if ((minutesLeft > 0 && minutesLeft <= 11) && !ServerConstants.isShutdown) {
                            ServerConstants.isShutdown = true;
                            if (c != null && c.getPlayer() != null) {
                                c.getPlayer().dropMessage(6, "已經限制玩家所有行動。");
                            }
                        }

                        if (minutesLeft == 0) {
                            ShutdownServer.getInstance().run();
                            t.start();
                            ts.cancel(false);
                            Timer.ShutdownTimer.getInstance().stop();
                            return;
                        }

                        StringBuilder message = new StringBuilder();
                        message.append("[楓之谷公告] 伺服器將在 ");
                        message.append(minutesLeft);
                        message.append(" 分鐘後關閉，請做好安全措施後並且盡快登出。");
                        World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, message.toString()));
                        System.out.println("伺服器將於 " + minutesLeft + " 分鐘後關機");
                        minutesLeft--;
                    }
                }, 60000);
            } else {
                c.getPlayer().dropMessage(6, new StringBuilder().append("伺服器關閉時間修改為 ").append(minutesLeft).append("分鐘後，請稍等伺服器關閉").toString());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("shutdowntime <分鐘數> <取消關機輸入 stop /沒有就隨便打>- 關閉伺服器").toString();
        }
    }

    public static class UnbanIP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            byte ret_ = MapleClient.unbanIP(splitted[1]);
            if (ret_ == -2) {
                c.getPlayer().dropMessage(6, "[unbanip] SQL 錯誤.");
            } else if (ret_ == -1) {
                c.getPlayer().dropMessage(6, "[unbanip] 角色不存在.");
            } else if (ret_ == 0) {
                c.getPlayer().dropMessage(6, "[unbanip] No IP or Mac with that character exists!");
            } else if (ret_ == 1) {
                c.getPlayer().dropMessage(6, "[unbanip] IP或Mac已解鎖其中一個.");
            } else if (ret_ == 2) {
                c.getPlayer().dropMessage(6, "[unbanip] IP以及Mac已成功解鎖.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("unbanip <玩家名稱> - 解鎖玩家").toString();
        }
    }

    public static class TempBan extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter victim;
            String name = splitted[1];
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                return false;
            }
            victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            final int reason = Integer.parseInt(splitted[2]);
            final int numDay = Integer.parseInt(splitted[3]);

            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, numDay);
            final DateFormat df = DateFormat.getInstance();

            if (victim == null) {
                c.getPlayer().dropMessage(6, "[tempban] 找不到目標角色");

            } else {
                victim.tempban("由" + c.getPlayer().getName() + "暫時鎖定了", cal, reason, true);
                c.getPlayer().dropMessage(6, "[tempban] " + splitted[1] + " 已成功被暫時鎖定至 " + df.format(cal.getTime()));
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("tempban <玩家名稱> - 暫時鎖定玩家").toString();
        }
    }

    public static class SaveAll extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            int p = 0;
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                List<MapleCharacter> chrs = cserv.getPlayerStorage().getAllCharactersThreadSafe();
                for (MapleCharacter chr : chrs) {
                    p++;
                    chr.saveToDB(false, false);
                }
            }
            if (c != null && c.getPlayer() != null) {
                c.getPlayer().dropMessage("[保存] " + p + "個玩家數據保存到數據中.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("saveall - 儲存所有角色資料").toString();
        }
    }

    public static class 角色訊息 extends spy {
    }

    public static class spy extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            } else {
                String name = splitted[1];
                MapleCharacter victim = MapleCharacter.getCharacterByName(name);
                int ch = World.Find.findChannel(name);
                if (victim != null) {
                    if (victim.getGMLevel() > c.getPlayer().getGMLevel()) {
                        c.getPlayer().dropMessage(5, "你不能查看比你高權限的人!");
                    } else {
                        int mesoInStorage = 0;
                        try {
                            if (victim.getStorage() != null) {
                                mesoInStorage = victim.getStorage().getMeso();
                            }
                        } catch (Exception ex) {
                        }
                        c.getPlayer().dropMessage(5, "此玩家狀態:");
                        c.getPlayer().dropMessage(5, "玩家名稱: " + victim.getName() + " 玩家編號: " + victim.getId() + " 帳號: " + victim.getClient().getAccountName() + " 帳號ID: " + victim.getAccountID());
                        c.getPlayer().dropMessage(5, "贊助金額: " + victim.getDonate() + " 玩家VIP: " + victim.getVip() + " 玩家權限: " + victim.getGMLevel() + " 等級: " + victim.getLevel() + " 職業: " + victim.getJob() + " 名聲: " + victim.getFame() + " 轉升: " + victim.getReborns());
                        c.getPlayer().dropMessage(5, "頭髮: " + victim.getHair() + " 臉型: " +victim.getFace());
                        c.getPlayer().dropMessage(5, "地圖: " + victim.getMapId() + " - " + victim.getMap().getMapName());
                        c.getPlayer().dropMessage(5, "目前HP: " + victim.getStat().getHp() + " 目前MP: " + victim.getStat().getMp());
                        c.getPlayer().dropMessage(5, "最大HP: " + victim.getStat().getMaxHp() + " 最大MP: " + victim.getStat().getMaxMp());
                        c.getPlayer().dropMessage(5, "力量: " + victim.getStat().getStr() + "  ||  敏捷: " + victim.getStat().getDex() + "  ||  智力: " + victim.getStat().getInt() + "  ||  幸運: " + victim.getStat().getLuk());
                        c.getPlayer().dropMessage(5, "物理攻擊: " + victim.getStat().getTotalWatk() + "  ||  魔法攻擊: " + victim.getStat().getTotalMagic());
                        c.getPlayer().dropMessage(5, "經驗倍率: " + victim.getStat().expBuff + " 金錢倍率: " + victim.getStat().mesoBuff + " 掉寶倍率: " + victim.getStat().dropBuff);
                        c.getPlayer().dropMessage(5, "GASH: " + victim.getCSPoints(1) + " 楓葉點數: " + victim.getCSPoints(2) + " 楓幣: " + victim.getMeso() + " 倉庫楓幣 " + mesoInStorage);
                        if (ch <= 0 && CashShopServer.getPlayerStorage().getCharacterByName(name) == null) {
                            c.getPlayer().dropMessage(5, "該角色為離線狀態");
                        } else {
                            if (ch <= 0) {
                                victim = CashShopServer.getPlayerStorage().getCharacterByName(name);
                            }
                            c.getPlayer().dropMessage(5, "IP:" + victim.getClient().getSessionIPAddress() + " 目前MAC:" + victim.getClient().getClientMac() + " 所有MAC:" + victim.getClient().getMacs());
                            c.getPlayer().dropMessage(5, "對伺服器延遲: " + victim.getClient().getLatency());
                        }
                    }
                } else {
                    c.getPlayer().dropMessage(5, "找不到此玩家.");
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("").append(getClass().getSimpleName().toLowerCase()).append(" <玩家名字> - 觀察玩家").toString();
        }
    }

    public static class spyID extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            } else {
                int id = 0;
                try {
                    id = Integer.parseInt(splitted[1]);
                } catch (Exception ex) {
                }
                MapleCharacter victim = MapleCharacter.getCharacterById(id);
                int ch = World.Find.findChannel(id);
                if (victim != null) {
                    if (victim.getGMLevel() > c.getPlayer().getGMLevel()) {
                        c.getPlayer().dropMessage(5, "你不能查看比你高權限的人!");
                    } else {
                        int mesoInStorage = 0;
                        try {
                            if (victim.getStorage() != null) {
                                mesoInStorage = victim.getStorage().getMeso();
                            }
                        } catch (Exception ex) {
                        }
                        c.getPlayer().dropMessage(5, "此玩家狀態:");
                        c.getPlayer().dropMessage(5, "玩家名稱: " + victim.getName() + " 玩家編號: " + victim.getId() + " 帳號: " + victim.getClient().getAccountName() + " 帳號ID: " + victim.getAccountID());
                        c.getPlayer().dropMessage(5, "贊助金額: " + victim.getDonate() + " 玩家VIP: " + victim.getVip() + " 玩家權限: " + victim.getGMLevel() + " 等級: " + victim.getLevel() + " 職業: " + victim.getJob() + " 名聲: " + victim.getFame() + " 轉升: " + victim.getReborns());
                        c.getPlayer().dropMessage(5, "頭髮: " + victim.getHair() + " 臉型: " +victim.getFace());
                        c.getPlayer().dropMessage(5, "地圖: " + victim.getMapId() + " - " + victim.getMap().getMapName());
                        c.getPlayer().dropMessage(5, "目前HP: " + victim.getStat().getHp() + " 目前MP: " + victim.getStat().getMp());
                        c.getPlayer().dropMessage(5, "最大HP: " + victim.getStat().getMaxHp() + " 最大MP: " + victim.getStat().getMaxMp());
                        c.getPlayer().dropMessage(5, "力量: " + victim.getStat().getStr() + "  ||  敏捷: " + victim.getStat().getDex() + "  ||  智力: " + victim.getStat().getInt() + "  ||  幸運: " + victim.getStat().getLuk());
                        c.getPlayer().dropMessage(5, "物理攻擊: " + victim.getStat().getTotalWatk() + "  ||  魔法攻擊: " + victim.getStat().getTotalMagic());
                        c.getPlayer().dropMessage(5, "經驗倍率: " + victim.getStat().expBuff + " 金錢倍率: " + victim.getStat().mesoBuff + " 掉寶倍率: " + victim.getStat().dropBuff);
                        c.getPlayer().dropMessage(5, "GASH: " + victim.getCSPoints(1) + " 楓葉點數: " + victim.getCSPoints(2) + " 楓幣: " + victim.getMeso() + " 倉庫楓幣 " + mesoInStorage);
                        if (ch <= 0 && CashShopServer.getPlayerStorage().getCharacterById(id) == null) {
                            c.getPlayer().dropMessage(5, "該角色為離線狀態");
                        } else {
                            if (ch <= 0) {
                                victim = CashShopServer.getPlayerStorage().getCharacterById(id);
                            }
                            c.getPlayer().dropMessage(5, "IP:" + victim.getClient().getSessionIPAddress() + " 所有MAC:" + victim.getClient().getMacs());
                            c.getPlayer().dropMessage(5, "對伺服器延遲: " + victim.getClient().getLatency());
                        }
                    }
                } else {
                    c.getPlayer().dropMessage(5, "找不到此玩家.");
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.新實習生.getCommandPrefix()).append("").append(getClass().getSimpleName().toLowerCase()).append(" <玩家名字> - 觀察玩家").toString();
        }
    }
}
