package client.messages.commands;

import client.messages.CommandExecute;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.SkillFactory;
import constants.MiMiConfig;
import constants.ServerConstants;
import constants.ServerConstants.PlayerGMRank;

/**
 *
 * @author benq
 */
public class GodCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.神;
    }

    public static class Buff extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter player = c.getPlayer();
            SkillFactory.getSkill(9001002).getEffect(1).applyTo(player);
            SkillFactory.getSkill(9001003).getEffect(1).applyTo(player);
            SkillFactory.getSkill(9001008).getEffect(1).applyTo(player);
            SkillFactory.getSkill(9001001).getEffect(1).applyTo(player);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.神.getCommandPrefix()).append("Buff - 施放管理BUFF").toString();
        }
    }

    public static class MinStats extends CommandExecute {

        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            player.getStat().setHp(50, player);
            player.getStat().setMp(50, player);
            player.getStat().setMaxHp((short) 50, player);
            player.getStat().setMaxMp((short) 50, player);
            player.getStat().setStr((short) 4, player);
            player.getStat().setDex((short) 4, player);
            player.getStat().setInt((short) 4, player);
            player.getStat().setLuk((short) 4, player);
            player.setLevel((short) 10);
            player.updateSingleStat(MapleStat.HP, 50);
            player.updateSingleStat(MapleStat.MP, 50);
            player.updateSingleStat(MapleStat.MAXHP, 50);
            player.updateSingleStat(MapleStat.MAXMP, 50);
            player.updateSingleStat(MapleStat.STR, 4);
            player.updateSingleStat(MapleStat.DEX, 4);
            player.updateSingleStat(MapleStat.INT, 4);
            player.updateSingleStat(MapleStat.LUK, 4);
            player.updateSingleStat(MapleStat.LEVEL, 10);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.神.getCommandPrefix()).append("MinStats - 預設屬性").toString();
        }
    }

    public static class Maxstats extends CommandExecute {

        public boolean execute(MapleClient c, String splitted[]) {

            MapleCharacter player = c.getPlayer();
             player.getStat().setHp(99999, player);
            player.getStat().setMp(99999, player);
            player.getStat().setMaxHp((short) 99999, player);
            player.getStat().setMaxMp((short) 99999, player);
            player.getStat().setStr(Short.MAX_VALUE, player);
            player.getStat().setDex(Short.MAX_VALUE, player);
            player.getStat().setInt(Short.MAX_VALUE, player);
            player.getStat().setLuk(Short.MAX_VALUE, player);
            player.setLevel((short) 199);
            player.updateSingleStat(MapleStat.HP, 30000);
            player.updateSingleStat(MapleStat.MP, 30000);
            player.updateSingleStat(MapleStat.MAXHP, 30000);
            player.updateSingleStat(MapleStat.MAXMP, 30000);
            player.updateSingleStat(MapleStat.STR, Short.MAX_VALUE);
            player.updateSingleStat(MapleStat.DEX, Short.MAX_VALUE);
            player.updateSingleStat(MapleStat.INT, Short.MAX_VALUE);
            player.updateSingleStat(MapleStat.LUK, Short.MAX_VALUE);
            player.updateSingleStat(MapleStat.LEVEL, 199);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.神.getCommandPrefix()).append("Maxstats - 滿屬性").toString();
        }
    }

    public static class BanCommand extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MiMiConfig.setCommandLock(!MiMiConfig.getCommandLock());
            c.getPlayer().dropMessage("指令封鎖: " + MiMiConfig.getCommandLock());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.神.getCommandPrefix()).append("BanCommand - 封鎖指令").toString();
        }
    }

    public static class face extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            int id = 0;
            if (splitted.length < 2) {
                return false;
            }
            id = Integer.parseInt(splitted[1]);
            player.setFace(id);
            player.updateSingleStat(MapleStat.FACE, id);
            player.dropMessage(5, "您當前臉型的ＩＤ已被改為: " + id);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.神.getCommandPrefix()).append("Face <臉型代碼> - 修改臉型").toString();
        }
    }

    public static class hair extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            int id = 0;
            if (splitted.length < 2) {
                return false;
            }
            id = Integer.parseInt(splitted[1]);
            player.setHair(id);
            player.updateSingleStat(MapleStat.HAIR, id);
            player.dropMessage(5, "您當前髮型的ＩＤ已被改為: " + id);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.神.getCommandPrefix()).append("Hair <髮型代碼> - 修改髮型").toString();
        }
    }

    public static class Str extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            int id = 0;
            if (splitted.length < 2) {
                return false;
            }
            id = Integer.parseInt(splitted[1]);
            player.setStr(id);
            player.updateSingleStat(MapleStat.STR, id);
            player.dropMessage(5, "您當前力量已被改為: " + id);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.神.getCommandPrefix()).append("Str <能力值> - 修改能力值").toString();
        }
    }

    public static class Int extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            int id = 0;
            if (splitted.length < 2) {
                return false;
            }
            id = Integer.parseInt(splitted[1]);
            player.setInt(id);
            player.updateSingleStat(MapleStat.INT, id);
            player.dropMessage(5, "您當前智力已被改為: " + id);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.神.getCommandPrefix()).append("Int <能力值> - 修改能力值").toString();
        }
    }

    public static class Luk extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            int id = 0;
            if (splitted.length < 2) {
                return false;
            }
            id = Integer.parseInt(splitted[1]);
            player.setLuk(id);
            player.updateSingleStat(MapleStat.LUK, id);
            player.dropMessage(5, "您當前幸運已被改為: " + id);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.神.getCommandPrefix()).append("Luk <能力值> - 修改能力值").toString();
        }
    }

    public static class Dex extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            int id = 0;
            if (splitted.length < 2) {
                return false;
            }
            id = Integer.parseInt(splitted[1]);
            player.setDex(id);
            player.updateSingleStat(MapleStat.DEX, id);
            player.dropMessage(5, "您當前敏捷已被改為: " + id);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.神.getCommandPrefix()).append("Luk <能力值> - 修改能力值").toString();
        }
    }

    public static class HP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            int id = 0;
            if (splitted.length < 2) {
                return false;
            }
            id = Integer.parseInt(splitted[1]);
            player.setHp(id);
            player.setMaxHp(id);
            player.updateSingleStat(MapleStat.HP, id);
            player.updateSingleStat(MapleStat.MAXHP, id);
            player.dropMessage(5, "您當前HP已被改為: " + id);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.神.getCommandPrefix()).append("HP <能力值> - 修改能力值").toString();
        }
    }

    public static class MP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            int id = 0;
            if (splitted.length < 2) {
                return false;
            }
            id = Integer.parseInt(splitted[1]);
            player.setMp(id);
            player.setMaxMp(id);
            player.updateSingleStat(MapleStat.MP, id);
            player.updateSingleStat(MapleStat.MAXMP, id);
            player.dropMessage(5, "您當前MP已被改為: " + id);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(ServerConstants.PlayerGMRank.神.getCommandPrefix()).append("MP <能力值> - 修改能力值").toString();
        }
    }
}