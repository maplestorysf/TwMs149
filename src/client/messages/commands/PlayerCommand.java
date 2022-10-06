package client.messages.commands;

import client.*;
import client.inventory.MaplePet;
import client.messages.CommandExecute;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ItemConstants;
import constants.MiMiConfig;
import constants.ServerConstants;
import constants.ServerConstants.PlayerGMRank;
import scripting.NPCConversationManager;
import scripting.NPCScriptManager;
import server.MapleShop;
import server.ServerProperties;
import server.life.MapleMonster;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;

import java.util.*;

import tools.StringUtil;
import handling.world.World;

import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.maps.MapleMap;
import tools.FilePrinter;
import tools.FileoutputUtil;
import tools.packet.CField;
import tools.packet.CWvsContext;

/**
 * @author Emilyx3
 */
public class PlayerCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return ServerConstants.PlayerGMRank.普通玩家;
    }

    public abstract static class OpenNPCCommand extends CommandExecute {

        private static int[] npcs = { //Ish yur job to make sure these are in order and correct ;(
                9270035,
                9010017,
                9000000,
                9000030,
                9010000,
                9000085,
                9000018,
                9900003,
                9900002,
                9900007,
                9300009,
                2012000,
                9310051//12
        };
        
        protected int npc = -1;

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (npc != 8 && npc != 7 && npc != 6 && npc != 5 && npc != 4 && npc != 3 && npc != 1 && c.getPlayer().getMapId() != 910000000) { //drpcash can use anywhere
                if (c.getPlayer().getLevel() < 10 && c.getPlayer().getJob() != 200) {
                    c.getPlayer().dropMessage(5, "未達10級的玩家無法使用此指令。");
                    return true;
                }
                if (c.getPlayer().isInBlockedMap()) {
                    c.getPlayer().dropMessage(5, "此地圖無法使用指令。");
                    return true;
                }
            } else if (npc == 1) {
                if (c.getPlayer().getLevel() < 70) {
                    c.getPlayer().dropMessage(5, "未達70級的玩家無法使用此指令。");
                    return true;
                }
            }
            if (c.getPlayer().hasBlockedInventory()) {
                c.getPlayer().dropMessage(5, "遇到NPC異常請使用 @EA 指令排除。");
                return true;
            }
            NPCScriptManager.getInstance().start(c, npcs[npc]);
            return true;
        }
    }

    public static class Npc extends OpenNPCCommand {

        public Npc() {
            npc = 0;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("Npc - 呼叫萬能npc").toString();
        }
    }

    public static class 活動 extends OpenNPCCommand {

        public 活動() {
            npc = 2;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("活動 - 呼叫活動npc").toString();
        }
    }

    public static class 掉寶 extends OpenNPCCommand {

        public 掉寶() {
            npc = 4;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("掉寶 - 呼叫掉寶npc").toString();
        }
    }

    public static class 幫助 extends OpenNPCCommand {

        public 幫助() {
            npc = 7;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("幫助 - 呼叫幫助npc").toString();
        }
    }

    public static class 公告 extends OpenNPCCommand {

        public 公告() {
            npc = 8;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("公告 - 呼叫公告npc").toString();
        }
    }
     public static class 尿尿 extends OpenNPCCommand {

        public 尿尿() {
            npc = 12;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("尿尿- 傳送去澡堂").toString();
        }
    }

    public static class 轉職 extends OpenNPCCommand {

        public 轉職() {
            npc = 9;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("轉職 - 呼叫轉職npc").toString();
        }
    }
     public static class go extends CommandExecute {

        private static final HashMap<String, Integer> gotomaps = new HashMap<>();

        static {
           gotomaps.put("艾靈森林", 300000000);
            gotomaps.put("埃德爾", 310000000);
            gotomaps.put("新葉城", 600000000); 
            gotomaps.put("fm", 910000000); 
            gotomaps.put("武陵道場", 925020000); 
            gotomaps.put("怪物公園", 951000000);
            gotomaps.put("古代神社", 800000000);
            gotomaps.put("昭和村", 801000000); 
            gotomaps.put("楓葉村", 1000000);
   	    gotomaps.put("雄獅", 551030100);
            gotomaps.put("楓之港", 2000000);
            gotomaps.put("天空", 200000000);
            gotomaps.put("冰原", 211000000);
            gotomaps.put("城牆1", 211060100);
            gotomaps.put("城牆2", 211060300);
            gotomaps.put("城牆3", 211060500);
            gotomaps.put("城牆4", 211060700); 
            gotomaps.put("城牆5", 211060900); 
            gotomaps.put("玩具", 220000000); 
            gotomaps.put("地球", 221000000);
            gotomaps.put("童話", 222000000); 
            gotomaps.put("水世界", 230000000);
            gotomaps.put("神木", 240000000); 
            gotomaps.put("桃花", 250000000);
            gotomaps.put("靈藥", 251000000);
            gotomaps.put("鯨魚號", 251010404);
            gotomaps.put("沙漠", 260000200); 
            gotomaps.put("瑪迦", 261000000); 
            gotomaps.put("茱麗葉", 261000021); 
            gotomaps.put("天上", 200100000); 
            gotomaps.put("三扇門", 270000000); 
            gotomaps.put("皮卡", 270050100); 
            gotomaps.put("泰拉", 240070000);  
            gotomaps.put("新加坡", 540000000);
            gotomaps.put("馬來", 550000000);
	    gotomaps.put("龍王", 240050400 );
 	    gotomaps.put("炎魔", 211042400);
	    gotomaps.put("女皇", 271040000);
            gotomaps.put("弓手", 100000000); 
            gotomaps.put("魔法", 101000000); 
            gotomaps.put("櫻花處", 101050000);
            gotomaps.put("勇士", 102000000); 
            gotomaps.put("墮落", 103000000); 
            gotomaps.put("維多", 104000000); 
            gotomaps.put("六岔", 104020000); 
            gotomaps.put("奇幻", 105000000);
            gotomaps.put("鯨魚", 120000000); 
            gotomaps.put("黃金", 120030000); 
            gotomaps.put("耶雷", 130000000); 
            gotomaps.put("菇菇", 106020000); 
            gotomaps.put("瑞恩", 140000000); 
            gotomaps.put("101", 103040000); 
            gotomaps.put("結婚", 680000000); 
            gotomaps.put("boss", 689010000); 
            gotomaps.put("鄉村", 551000000);
            gotomaps.put("碼頭", 541000000); 
            gotomaps.put("公會", 200000301);
            gotomaps.put("鐘王", 220080001);
            gotomaps.put("海怒斯", 230040410);
            gotomaps.put("大姐頭", 801040003);
            gotomaps.put("長老", 801040100);
            gotomaps.put("puma", 931000500);
            gotomaps.put("金勾", 251010404);
            gotomaps.put("坎特", 923040000);
            gotomaps.put("技術", 910001000);
        }

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "使用方法: @go <地圖名稱>");
            } else if (gotomaps.containsKey(splitted[1])) {
                MapleMap target = c.getChannelServer().getMapFactory().getMap(gotomaps.get(splitted[1]));
                MaplePortal targetPortal = target.getPortal(0);
                c.getPlayer().changeMap(target, targetPortal);
            } else if (splitted[1].equals("目的地")) {
                c.getPlayer().dropMessage(6, "使用 @go <目的地>. 目的地地圖如下:");
                StringBuilder sb = new StringBuilder();
                for (String s : gotomaps.keySet()) {
                    sb.append(s).append(", ");
                }
                c.getPlayer().dropMessage(6, sb.substring(0, sb.length() - 2));
            } else {
                c.getPlayer().dropMessage(6, "錯誤的指令規則 - 使用 @go <目的地>. 來看目的地地圖清單, 接著使用 @go 目的地地圖名稱.");
            }
            return true;
        }

        @Override
        public String getMessage() {
              return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("goto <名稱> - 到某個地圖").toString();

        }
    }

    public static class 轉生 extends OpenNPCCommand {

        public 轉生() {
            npc = 10;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("轉生 - 200級轉生").toString();
        }
    }
 public static class 技能滿 extends OpenNPCCommand {

        public 技能滿() {
            npc = 11;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("技能滿 - 技能全滿").toString();
        }
    }
    public static class expfix extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().setExp(0);
            c.getPlayer().updateSingleStat(MapleStat.EXP, c.getPlayer().getExp());
            c.getPlayer().dropMessage(5, "經驗修復完成");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("expfix - 經驗歸零").toString();
        }
    }

    public static class TSmega extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().setSmega();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("TSmega - 開/關閉廣播").toString();
        }
    }

    public static class ea extends CommandExecute {

        public static String getDayOfWeek() {
            int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
            String dd = String.valueOf(dayOfWeek);
            switch (dayOfWeek) {
                case 0:
                    dd = "日";
                    break;
                case 1:
                    dd = "一";
                    break;
                case 2:
                    dd = "二";
                    break;
                case 3:
                    dd = "三";
                    break;
                case 4:
                    dd = "四";
                    break;
                case 5:
                    dd = "五";
                    break;
                case 6:
                    dd = "六";
                    break;
            }
            return dd;
        }

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.removeClickedNPC();
            NPCScriptManager.getInstance().dispose(c);
            c.sendPacket(CWvsContext.enableActions());
            StringBuilder sc = new StringBuilder();
            sc.append("解卡完畢..\r\n#b當前系統時間:#r").append(FilePrinter.getAreaDateString()).append(" #k#g星期").append(getDayOfWeek()).append("#k");
            sc.append("\r\n經驗值倍率 ").append(((Math.round(c.getPlayer().getEXPMod()) * 100) * Math.round(c.getPlayer().getStat().expBuff / 100.0))).append("%, 掉寶倍率 ").append(Math.round(c.getPlayer().getDropMod() * (c.getPlayer().getStat().dropBuff / 100.0) * 100)).append("%, 楓幣倍率 ").append(Math.round((c.getPlayer().getStat().mesoBuff / 100.0) * 100));
            if (c.getChannelServer().getExExpRate() > 1 || c.getChannelServer().getExDropRate() > 1 || c.getChannelServer().getExMesoRate() > 1) {
                sc.append("\r\n額外經驗值倍率 ").append(c.getChannelServer().getExExpRate()).append("倍, 掉寶倍率 ").append(c.getChannelServer().getExDropRate()).append("倍, 楓幣倍率 ").append(c.getChannelServer().getExMesoRate()).append("倍");
            }
            sc.append("\r\n目前轉升數: ").append(c.getPlayer().getReborns()).append(" 目前膀胱量: ").append(c.getPlayer().getPee() + "%");
            sc.append("\r\n目前剩餘 ").append(c.getPlayer().getCSPoints(0)).append(" 樂豆點, ").append(c.getPlayer().getCSPoints(1)).append(" 楓葉點數, ").append(c.getPlayer().getPoints()).append(" 贊助點數, \r\n").append(c.getPlayer().getIntNoRecord(GameConstants.BOSS_PQ)).append(" Boss組隊任務點數。");
            sc.append("\r\n目前銀行存款:#d").append(c.getPlayer().getMesoFromBank());
            sc.append("\r\n要查看角色能力訊息請使用 @charinfo");

            c.getPlayer().showInstruction(sc.toString(), 450, 30);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("ea - 解卡").toString();
        }
    }

    public static class charinfo extends CommandExecute {
        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            StringBuilder sb = new StringBuilder();
            sb.append("-----------以下是角色屬性(不含技能加層的)-----------");
            sb.append("\r\n力量:").append(c.getPlayer().getStat().getStr()).append("\t\t總力量:").append(c.getPlayer().getStat().getTotalStr());
            sb.append("\r\n敏捷:").append(c.getPlayer().getStat().getDex()).append("\t\t總敏捷:").append(c.getPlayer().getStat().getTotalDex());
            sb.append("\r\n智力:").append(c.getPlayer().getStat().getInt()).append("\t\t總智力:").append(c.getPlayer().getStat().getTotalInt());
            sb.append("\r\n幸運:").append(c.getPlayer().getStat().getLuk()).append("\t\t總幸運:").append(c.getPlayer().getStat().getTotalLuk());
            sb.append("\r\n-----------以下是個人角色資訊-----------");
            sb.append("\r\n血量:").append(c.getPlayer().getStat().getHp()).append("/").append(c.getPlayer().getStat().getCurrentMaxHp());
            sb.append("\r\n魔量:").append(c.getPlayer().getStat().getMp()).append("/").append(c.getPlayer().getStat().getCurrentMaxMp(c.getPlayer().getJob()));
            sb.append("\r\n經驗值:").append(c.getPlayer().getExp());
            sb.append("\r\n爆擊率").append(c.getPlayer().getStat().passive_sharpeye_rate()).append("%");
            sb.append("\r\n物理攻擊力:").append(c.getPlayer().getStat().getTotalWatk());
            sb.append("\r\n魔法攻擊力:").append(c.getPlayer().getStat().getTotalMagic());
            sb.append("\r\n最高攻擊:").append(c.getPlayer().getStat().getCurrentMaxBaseDamage());
            sb.append("\r\n總傷害:").append((int) Math.ceil(c.getPlayer().getStat().dam_r - 100)).append("%");
            sb.append("\r\nBOSS攻擊力:").append((int) Math.ceil(c.getPlayer().getStat().bossdam_r - 100)).append("%");
            sb.append("\r\n無視防禦率:").append((float) Math.ceil(c.getPlayer().getStat().ignoreTargetDEF)).append("%");
            c.getPlayer().dropNPC(sb.toString());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(PlayerGMRank.普通玩家.getCommandPrefix()).append("charinfo - 查看本身自己的角色訊息").toString();
        }
    }

    public static class mob extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            StringBuilder sc = new StringBuilder();
            MapleMonster monster = null;
            for (final MapleMapObject monstermo : c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 100000, Arrays.asList(MapleMapObjectType.MONSTER))) {
                monster = (MapleMonster) monstermo;
                if (monster.isAlive()) {
                    sc.append(monster.toString());
                    sc.append("\r\n\r\n");
                }
            }
            if (monster == null) {
                c.getPlayer().dropNPC("找不到怪物。");
                return true;
            }
            c.getPlayer().dropNPC(sc.toString());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("mob - 查看怪物狀態").toString();
        }
    }

    public static class CGM extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            boolean autoReply = false;

            if (splitted.length < 2) {
                return false;
            }
            String talk = StringUtil.joinStringFrom(splitted, 1);
            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage(6, "因為你自己是GM所以無法使用此指令,可以嘗試!cngm <訊息> 來建立GM聊天頻道~");
            } else {
                if (!c.getPlayer().getCheatTracker().GMSpam(100000, 1)) { // 1 minutes.
                    boolean fake = false;
                    boolean showmsg = true;

                    // 管理員收不到，玩家有顯示傳送成功
                    if (MiMiConfig.getBlackList().containsKey(c.getAccID())) {
                        fake = true;
                    }

                    // 管理員收不到，玩家沒顯示傳送成功
                    if (talk.contains("搶") && talk.contains("圖")) {
                        c.getPlayer().dropMessage(1, "搶圖自行解決！！");
                        fake = true;
                        showmsg = false;
                    } else if ((talk.contains("被") && talk.contains("騙")) || (talk.contains("點") && talk.contains("騙"))) {
                        c.getPlayer().dropMessage(1, "被騙請自行解決");
                        fake = true;
                        showmsg = false;
                    } else if ((talk.contains("被") && talk.contains("盜"))) {
                        c.getPlayer().dropMessage(1, "被盜請自行解決");
                        fake = true;
                        showmsg = false;
                    } else if (talk.contains("刪") && ((talk.contains("角") || talk.contains("腳")) && talk.contains("錯"))) {
                        c.getPlayer().dropMessage(1, "刪錯角色請自行解決");
                        fake = true;
                        showmsg = false;
                    } else if (talk.contains("亂") && (talk.contains("名") && talk.contains("聲"))) {
                        c.getPlayer().dropMessage(1, "請自行解決");
                        fake = true;
                        showmsg = false;
                    } else if (talk.contains("改") && talk.contains("密") && talk.contains("碼")) {
                        c.getPlayer().dropMessage(1, "目前第二組密碼及密碼無法查詢及更改,");
                        fake = true;
                        showmsg = false;
                    }

                    // 管理員收的到，自動回復
                    if (((talk.contains("商人") || talk.contains("精靈")) && talk.contains("吃")) || (talk.contains("商店") && talk.contains("補償"))) {
                        c.getPlayer().dropMessage(1, "目前精靈商人裝備和楓幣有機率被吃\r\n如被吃了請務必將當時的情況完整描述給管理員\r\n\r\nPS: 不會補償任何物品");
                        autoReply = true;
                    } else if (talk.contains("檔") && talk.contains("案") && talk.contains("受") && talk.contains("損")) {
                        c.getPlayer().dropMessage(1, "檔案受損請重新解壓縮主程式唷");
                        autoReply = true;
                    }

                    if (showmsg) {
                        c.getPlayer().dropMessage(6, "訊息已經寄送給GM了!");
                    }

                    if (!fake) {
                        World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, "[管理員幫幫忙]頻道 " + c.getPlayer().getClient().getChannel() + " 玩家 [" + c.getPlayer().getName() + "] (" + c.getPlayer().getId() + "): " + talk + (autoReply ? " -- (系統已自動回復)" : "")));
                    }

                    FileoutputUtil.logToFile("logs/data/管理員幫幫忙.txt", "\r\n " + FileoutputUtil.NowTime() + " 玩家[" + c.getPlayer().getName() + "] 帳號[" + c.getAccountName() + "]: " + talk + (autoReply ? " -- (系統已自動回復)" : "") + "\r\n");

                } else {
                    c.getPlayer().dropMessage(6, "為了防止對GM刷屏所以每1分鐘只能發一次.");
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("cgm - 跟GM回報").toString();
        }
    }

    public static class 清除道具 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            if (splitted.length < 4 || player.hasBlockedInventory()) {
                return false;
            }
            MapleInventory inv;
            MapleInventoryType type;
            String Column = "null";
            int start = -1, end = -1;
            try {
                Column = splitted[1];
                start = Integer.parseInt(splitted[2]);
                end = Integer.parseInt(splitted[3]);
            } catch (Exception ex) {
            }
            if (start == -1 || end == -1) {
                return false;
            }
            if (start < 1) {
                start = 1;
            }
            if (start > 96) {
                start = 96;
            }
            if (end < 1) {
                end = 1;
            }
            if (end > 96) {
                end = 96;
            }
            switch (Column) {
                case "裝備欄":
                    type = MapleInventoryType.EQUIP;
                    break;
                case "消耗欄":
                    type = MapleInventoryType.USE;
                    break;
                case "裝飾欄":
                    type = MapleInventoryType.SETUP;
                    break;
                case "其他欄":
                    type = MapleInventoryType.ETC;
                    break;
                case "特殊欄":
                    type = MapleInventoryType.CASH;
                    break;
                default:
                    type = null;
                    break;
            }
            if (type == null) {
                return false;
            }
            inv = c.getPlayer().getInventory(type);

            boolean haveSummonedPet = false;
            for (MaplePet pet : c.getPlayer().getPets()) {
                if (pet.getSummoned()) {
                    haveSummonedPet = true;
                    break;
                }
            }
            if (type == MapleInventoryType.CASH && haveSummonedPet) {
                player.dropMessage("請先收起身上的寵物在使用清除道具指令!");
                return true;
            }
            if (type == MapleInventoryType.ETC && c.getPlayer().haveItem(4031454, 1)) {
                player.dropMessage("身上有獎杯唷，請存到倉庫後在使用本命令");
                return true;
            }

            int totalMesosGained = 0;
            for (byte i = (byte) start; i <= end; i++) {
                if (inv.getItem((short) i) != null) {
                    MapleItemInformationProvider iii = MapleItemInformationProvider.getInstance();
                    int itemPrice = (int) iii.getPrice(inv.getItem((short) i).getItemId());
                    int amount = inv.getItem((short) i).getQuantity();
                    int itemQ = (amount > 1 ? amount : 1);
                    totalMesosGained += (itemPrice * itemQ);
                    itemPrice = 0;
                    MapleInventoryManipulator.removeFromSlot(c, type, i, inv.getItem(i).getQuantity(), true);
                }
            }
            player.gainMeso(totalMesosGained < 0 ? 0 : (totalMesosGained / 2), true);
            FileoutputUtil.logToFile("logs/data/玩家指令.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 使用了指令 " + StringUtil.joinStringFrom(splitted, 0) + " 得到了: " + totalMesosGained);
            c.getPlayer().dropMessage("你的欄位位子從 " + start + " 賣到 " + end + ", 得到了 " + (totalMesosGained / 2) + " 元.");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("清除道具 <裝備欄/消耗欄/裝飾欄/其他欄/特殊欄> <開始格數> <結束格數>").toString();
        }
    }

    public static class 存檔 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (!c.getPlayer().getCheatTracker().SaveSpam(200000, 1)) {
                if (!World.isPlayerSaving(c.getAccID())) {
                    c.getPlayer().saveToDB(false, false);
                }
                c.getPlayer().dropMessage(6, "[訊息] 保存成功!");
            } else {
                c.getPlayer().dropMessage(6, "存檔冷卻中請兩分鐘後再使用.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("save - 存檔").toString();
        }
    }

    public static class pee extends CommandExecute { //尿尿系統

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();

            if (player.getPee() <= 29) {
                player.dropMessage("[身體]: 我不是真的有心情去撒尿。請稍後再試.");
            } else if (player.getMapId() != 809000201 && player.getMapId() != 809000101) {
                player.dropMessage("[身體]: 你瘋了嗎! 我才不要在大庭廣眾下尿尿. 到廁所去尿!");
            } else {
                player.dropMessage("[身體]: 哇,多謝! 感覺更好了!");
                player.setPee(0);
                if (!player.isGM()) {
                    //World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, player.getName() + " 剛剛去尿尿了!"));
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("pee - 尿尿").toString();
        }
    }

    // 快速點能力指令
    public static class STR extends DistributeStatCommands {

        public STR() {
            stat = MapleStat.STR;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("STR <數量> - 快速點力量").toString();
        }
    }

    public static class DEX extends DistributeStatCommands {

        public DEX() {
            stat = MapleStat.DEX;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("DEX <數量> - 快速點敏捷").toString();
        }
    }

    public static class INT extends DistributeStatCommands {

        public INT() {
            stat = MapleStat.INT;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("INT <數量> - 快速點智力").toString();
        }
    }

    public static class LUK extends DistributeStatCommands {

        public LUK() {
            stat = MapleStat.LUK;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("LUK <數量> - 快速點幸運").toString();
        }
    }

    public abstract static class DistributeStatCommands extends CommandExecute {

        private static int statLim = 32767;
        private static int statLow = 4;
        private static int LOW = 0;
        protected MapleStat stat = null;

        private void setStat(MapleCharacter chr, int amount) {
            switch (stat) {
                case STR:
                    chr.getStat().setStr((short) amount, chr);
                    chr.updateSingleStat(MapleStat.STR, chr.getStat().getStr());
                    break;
                case DEX:
                    chr.getStat().setDex((short) amount, chr);
                    chr.updateSingleStat(MapleStat.DEX, chr.getStat().getDex());
                    break;
                case INT:
                    chr.getStat().setInt((short) amount, chr);
                    chr.updateSingleStat(MapleStat.INT, chr.getStat().getInt());
                    break;
                case LUK:
                    chr.getStat().setLuk((short) amount, chr);
                    chr.updateSingleStat(MapleStat.LUK, chr.getStat().getLuk());
                    break;
            }
        }

        private int getStat(MapleCharacter chr) {
            switch (stat) {
                case STR:
                    return chr.getStat().getStr();
                case DEX:
                    return chr.getStat().getDex();
                case INT:
                    return chr.getStat().getInt();
                case LUK:
                    return chr.getStat().getLuk();
                default:
                    throw new RuntimeException(); //Will never happen.
            }
        }

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            int change = 0;
            try {
                change = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException nfe) {
                return false;
            }
            if (change <= 0) {
                c.getPlayer().dropMessage(6, "您必需輸入大於0的數字.");
                return true;
            }
            if (LOW == 1 && c.getPlayer().getRemainingAp() != 0 && change < 0) {
                c.getPlayer().dropMessage("您的能力值尚未重製完，還剩下" + c.getPlayer().getRemainingAp() + "點沒分配");
                return true;
            } else {
                LOW = 0;
            }
            if (c.getPlayer().getRemainingAp() < change) {
                c.getPlayer().dropMessage(6, "您的AP不足.");
                return true;
            }
            if (getStat(c.getPlayer()) + change > statLim) {
                c.getPlayer().dropMessage(6, "能力值不能高於 " + statLim + ".");
                return true;
            }
            if (getStat(c.getPlayer()) + change < statLow) {
                c.getPlayer().dropMessage("能力值不能低於 " + statLow + ".");
                return true;
            }
            setStat(c.getPlayer(), getStat(c.getPlayer()) + change);
            c.getPlayer().setRemainingAp((short) (c.getPlayer().getRemainingAp() - change));
            c.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, c.getPlayer().getRemainingAp());
            int a = change;
            if (change < 0) {
                c.getPlayer().dropMessage("重製AP完成，現在有" + c.getPlayer().getRemainingAp() + "點可以分配");
                LOW = 1;
            }
            int b = Math.abs(a);
            c.getPlayer().dropMessage((change >= 0 ? "增加" : "減少") + stat.name() + b + "點");
            FileoutputUtil.logToFile("logs/data/玩家快速點能力指令.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 使用了指令 " + StringUtil.joinStringFrom(splitted, 0) + (change >= 0 ? "增加" : "減少") + stat.name() + b + "點");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("STR/DEX/INT/LUK <數量> 快速點能力值").toString();
        }
    }

    public static class 穿副武器 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getLevel() < 20) {
                c.getPlayer().dropMessage(5, "等級必須大於20等.");
                return true;
            }
            if (GameConstants.isDB(c.getPlayer().getJob())) {
                short src = (short) 1;
                short ssrc = (short) -110;
                Item toUse = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(src);
                Item toEUse = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(ssrc);
                if (toUse == null || toUse.getQuantity() < 1 || !ItemConstants.is透明短刀(toUse.getItemId())) {
                    c.getPlayer().dropMessage(6, "穿戴錯誤，裝備欄第 " + src + " 個是空的，或者該道具不是透明短刀。");
                    return true;
                } else if (toEUse != null) {
                    c.getPlayer().dropMessage(6, "穿戴錯誤，已經有穿戴相同透明短刀了。");
                    return true;
                }
                MapleInventoryManipulator.equip(c, src, (short) -110);
                c.getPlayer().dropMessage(6, "[副武器] 穿戴成功!");
                return true;
            } else if (GameConstants.isJett(c.getPlayer().getJob())) {
                short src = (short) 1;
                short ssrc = (short) -10;
                Item toUse = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(src);
                Item toEUse = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(ssrc);
                if (toUse == null || toUse.getQuantity() < 1 || !ItemConstants.is寶盒(toUse.getItemId())) {
                    c.getPlayer().dropMessage(6, "穿戴錯誤，裝備欄第 " + src + " 個是空的，或者該道具不是寶盒。");
                    return true;
                } else if (toEUse != null) {
                    c.getPlayer().dropMessage(6, "穿戴錯誤，已經有穿戴相同寶盒了。");
                    return true;
                }
                MapleInventoryManipulator.equip(c, src, (short) -10);
                c.getPlayer().dropMessage(6, "[副武器] 穿戴成功!");
                return true;
            } else {
                return false;
            }

        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("穿副武器 - 讓影武者穿透明雙刀").toString();
        }
    }

    public static class 取下副武器 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (GameConstants.isDB(c.getPlayer().getJob())) {
                Item toUse = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short) -110);
                if (toUse == null || c.getPlayer().getInventory(MapleInventoryType.EQUIP).isFull()) {
                    c.getPlayer().dropMessage(6, "取下副武器錯誤，副武器位置當前空的道具，或者裝備欄已滿。");
                    return true;
                }

                MapleInventoryManipulator.unequip(c, (byte) -110, c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                c.getPlayer().dropMessage(6, "[副武器] 取下成功!");
                return true;
            } else {
                Item toUse = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short) -10);
                if (toUse == null || c.getPlayer().getInventory(MapleInventoryType.EQUIP).isFull()) {
                    c.getPlayer().dropMessage(6, "取下副武器錯誤，副武器位置當前空的道具，或者裝備欄已滿。");
                    return true;
                }
                MapleInventoryManipulator.unequip(c, (byte) -10, c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                c.getPlayer().dropMessage(6, "[副武器] 取下成功!");
                return true;
            }
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("取下副武器 - 針對職業取下副武器問題").toString();
        }
    }

    public static class 脫身上騎寵道具 extends CommandExecute {
        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            Item toUse1 = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short) -18);
            Item toUse2 = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short) -19);
            Item toUse3 = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short) -118);
            Item toUse4 = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short) -119);
            if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() <= 4) {
                c.getPlayer().dropMessage(6, "脫身上騎寵道具錯誤，請確認裝備欄是否有4格空位。");
                return true;
            }
            if (toUse1 != null) {
                MapleInventoryManipulator.unequip(c, (byte) -18, c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
            }
            if (toUse2 != null) {
                MapleInventoryManipulator.unequip(c, (byte) -19, c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
            }
            if (toUse3 != null) {
                MapleInventoryManipulator.unequip(c, (byte) -118, c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
            }
            if (toUse4 != null) {
                MapleInventoryManipulator.unequip(c, (byte) -119, c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
            }
            c.getPlayer().dropMessage(6, "[騎寵道具] 取下成功!");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("脫身上騎寵道具 - 針對職業脫身上騎寵道具問題").toString();
        }
    }

    public static class 清除重新購買 extends CommandExecute {
        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().setClearRebuy();
            c.getPlayer().dropMessage(6, "成功清除商店重新購買清單");
            FileoutputUtil.logToFile("logs/data/指令清除重新購買.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 職業: " + MapleJob.getById(c.getPlayer().getJob()) + " 地圖: " + c.getPlayer().getMapId() + " 使用了指令 " + StringUtil.joinStringFrom(splitted, 0));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(PlayerGMRank.普通玩家.getCommandPrefix()).append("清除所有重新購買的清單").toString();
        }
    }

    public static class 提錢 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            String input = null;
            long money = 0;

            try {
                input = splitted[1];
                money = Long.parseLong(input);
            } catch (Exception ex) {
                return false;
            }

            if (money <= 0) {
                c.getPlayer().dropMessage(-1, "[銀行系統] 不能給負數或是0");
            } else if (c.getPlayer().getMesoFromBank() < money) {
                c.getPlayer().dropMessage(-1, "[銀行系統] 銀行內只有" + money + "錢");
            } else if (money > 2100000000) {
                c.getPlayer().dropMessage(-1, "[銀行系統] 一次只能提21E以內的錢");
            } else if (money + c.getPlayer().getMeso() > 2100000000 || money + c.getPlayer().getMeso() < 0) {
                c.getPlayer().dropMessage(-1, "[銀行系統] 領的錢+身上的錢無法超過21E");
            } else {
                // 身上給錢
                c.getPlayer().gainMeso((int) (money), true);
                // 銀行扣錢 
                c.getPlayer().decMoneytoBank(money);
                c.getPlayer().dropMessage(-1, "[銀行系統] 您已經提出 " + money + "錢");
                FileoutputUtil.logToFile("logs/openlog/銀行取出.txt", "取出時間:" + FileoutputUtil.NowTime() + "帳號編號:" + c.getPlayer().getAccountID() + " 角色名稱:" + c.getPlayer().getName() + " 取出金額: " + money + "銀行剩餘:" + c.getPlayer().getMesoFromBank() + "\r\n");
            }

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("存錢  <錢> - 存錢到自己銀行").toString();
        }
    }

    public static class 存錢 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            String input = null;
            long money = 0;

            try {
                input = splitted[1];
                money = Long.parseLong(input);
            } catch (Exception ex) {
                return false;
            }

            if (money <= 0) {
                c.getPlayer().dropMessage(-1, "[銀行系統] 不能給負數或是0");
            } else if (money > 2100000000) {
                c.getPlayer().dropMessage(-1, "[銀行系統] 一次只能存21E以內的錢");
            } else if (c.getPlayer().getMeso() < money) {
                c.getPlayer().dropMessage(-1, "[銀行系統] 目前您的身上裡沒有" + money + "錢");
            } else {
                // 身上扣錢
                c.getPlayer().gainMeso((int) (-money), true);
                // 銀行存錢 
                c.getPlayer().incMoneytoBank(money);
                c.getPlayer().dropMessage(-1, "[銀行系統] 您已經存入 " + money + "錢");
                FileoutputUtil.logToFile("logs/openlog/銀行存入.txt", "存入時間:" + FileoutputUtil.NowTime() + "帳號編號:" + c.getPlayer().getAccountID() + " 角色名稱:" + c.getPlayer().getName() + " 存入金額: " + money + "銀行餘額:" + c.getPlayer().getMesoFromBank() + "\r\n");
            }

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("存錢  <錢> - 存錢到自己銀行").toString();
        }
    }

    public static class 轉帳 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                return false;
            }
            MapleCharacter victim = null;
            String input = null;
            long money = 0;
            int ch = -1;

            try {
                input = splitted[1];
                money = Long.parseLong(splitted[2]);
                ch = World.Find.findChannel(input);
                victim = World.Find.findChr(input);
            } catch (Exception ex) {
                return false;
            }

            if (money <= 0) {
                c.getPlayer().dropMessage(-1, "[銀行系統] 不能給負數或是0");
            } else if (victim == null) {
                c.getPlayer().dropMessage(-1, "[銀行系統] 對方(" + input + ")不在線上");
            } else if (ch == -10) {
                c.getPlayer().dropMessage(-1, "[銀行系統] 對方目前在商城內");
            } else if (victim.getAccountID() == c.getAccID()) {
                c.getPlayer().dropMessage(-1, "[銀行系統] 無法給予自己錢。");
            } else if (victim.getGMLevel() > c.getPlayer().getGMLevel()) {
                c.getPlayer().dropMessage(-1, "[銀行系統] 無法給予" + victim.getName() + "錢。");
            } else if (c.getPlayer().getMesoFromBank() < money) {
                c.getPlayer().dropMessage(-1, "[銀行系統] 目前您的銀行裡沒有" + money + "錢");
            } else {
                // 從自己銀行扣錢
                c.getPlayer().decMoneytoBank(money);
                // 到對方銀行給錢
                victim.incMoneytoBank(money);
                c.getPlayer().dropMessage(-1, "[銀行系統] 您已經給予" + victim.getName() + ": " + money);
                victim.dropMessage(-1, "[銀行系統] " + c.getPlayer().getName() + "給予您 " + money);
                FileoutputUtil.logToFile("logs/openlog/銀行系統.txt", "\r\n" + FileoutputUtil.NowTime() + " 帳號編號: (" + c.getAccID() + ")" + c.getPlayer().getName() + " 給了 帳號編號: (" + victim.getAccountID() + ")" + victim.getName() + " " + money + "錢\r\n" + "總結:" + c.getPlayer().getName() + " 原本:" + (c.getPlayer().getMesoFromBank() + money) + " 剩餘:" + c.getPlayer().getMesoFromBank() + " " + victim.getName() + " 原本:" + (victim.getMesoFromBank() - money) + " 獲得後:" + victim.getMesoFromBank());
            }

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.普通玩家.getCommandPrefix()).append("轉帳 <名字> <錢> - 給對方自己銀行的錢").toString();
        }
    }

    public static class 生活滿 extends CommandExecute {
        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().maxTeachSkills();
            c.getPlayer().dropMessage(6, "生活技能已經滿等。");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuffer().append(PlayerGMRank.普通玩家.getCommandPrefix()).append("生技滿 - 把生活技能全滿").toString();
        }
    }
}