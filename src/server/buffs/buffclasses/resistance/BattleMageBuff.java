package server.buffs.buffclasses.resistance;

import client.MapleBuffStat;
import client.MapleJob;
import client.status.MonsterStatus;
import server.MapleStatEffect;
import server.buffs.AbstractBuffClass;

/**
 *
 * @author Dismal
 */
public class BattleMageBuff extends AbstractBuffClass {

    public BattleMageBuff() {
        buffs = new int[]{
            32000012, // 少量消弱
            32101004, // 紅色吸血術
            32101005, // 長杖極速
            32111004, // 轉換
            32111005, // 超級體
            32111014, // 防禦姿態
            32111006, // 甦醒
            32120000, // 進階黑色繩索
            32121003, // 颶風
            32121007, // 楓葉祝福
            32111010, // 瞬間移動精通
        };
    }

    @Override
    public boolean containsJob(int job) {
        return MapleJob.is煉獄巫師(job);
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 32000012: // 少量消弱
                eff.statups.put(MapleBuffStat.ELEMENT_WEAKEN, eff.x);
                break;
            case 32111006: // 甦醒
                eff.statups.put(MapleBuffStat.REAPER, 1);
                break;
            case 32101004: // 紅色吸血術
                eff.statups.put(MapleBuffStat.COMBO_DRAIN, eff.x);
                break;
            case 32101005: // 長杖極速
                eff.statups.put(MapleBuffStat.BOOSTER, eff.x);
                break;
            case 32111004: // 轉換
                eff.statups.put(MapleBuffStat.CONVERSION, eff.x);
                break;
            case 32111005: // 超級體
                eff.duration = 60000;
                eff.statups.put(MapleBuffStat.BODY_BOOST, (int) eff.level);
                break;
            case 32111014: // 防禦姿態
                eff.statups.put(MapleBuffStat.Stance, (int) eff.prop);
                break;
            case 32111010: // 瞬間移動精通
                eff.mpCon = (short) eff.y;
                eff.duration = 2100000000;
                eff.statups.put(MapleBuffStat.TELEPORT_MASTERY, eff.x);
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case 32120000: // 進階黑色繩索
                eff.dot = eff.damage;
                eff.dotTime = 3;
                break;
            case 32121003: // 颶風
                eff.statups.put(MapleBuffStat.TORNADO, eff.x);
                break;
            case 32121007: // 楓葉祝福
                eff.statups.put(MapleBuffStat.MAPLE_WARRIOR, eff.x);
                break;
            default:
                System.out.println("未知的 煉獄巫師(3200) Buff: " + skill);
                break;
        }
    }
}
