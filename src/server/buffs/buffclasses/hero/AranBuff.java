package server.buffs.buffclasses.hero;

import client.MapleBuffStat;
import client.MapleJob;
import server.MapleStatEffect;
import server.buffs.AbstractBuffClass;

/**
 *
 * @author Charmander
 */
public class AranBuff extends AbstractBuffClass {

    public AranBuff() {
        buffs = new int[]{
            21000000, // 矛之鬥氣
            21001003, // 神速之矛
            21100005, // 連環吸血
            21101003, // 強化連擊
            21101006, // 寒冰屬性
            21111001, // 釘錘
            21111009, // 鬥氣填充
            21111012, // 瑪哈祝福
            21120007, // 宙斯之盾
            21121000, // 楓葉祝福
            21121003, // 靈魂戰鬥
        };
    }

    @Override
    public boolean containsJob(int job) {
        return MapleJob.is狂狼勇士(job);
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 21000000: // 矛之鬥氣
                eff.statups.put(MapleBuffStat.ARAN_COMBO, 100);
                break;
            case 21001003: // 神速之矛
                eff.statups.put(MapleBuffStat.BOOSTER, -eff.y);
                break;
            case 21100005: // 連環吸血
                eff.statups.put(MapleBuffStat.COMBO_DRAIN, eff.x);
                break;
            case 21101003: // 強化連擊
                eff.statups.put(MapleBuffStat.BODY_PRESSURE, eff.x);
                break;
            case 21101006: // 寒冰屬性
                eff.statups.put(MapleBuffStat.WK_CHARGE, eff.x);
                break;
            case 21111001: // 釘錘
                eff.statups.put(MapleBuffStat.SMART_KNOCKBACK, eff.x);
                eff.statups.put(MapleBuffStat.ENHANCED_WATK, (int) eff.epad);
                eff.statups.put(MapleBuffStat.ENHANCED_WDEF, (int) eff.epdd);
                eff.statups.put(MapleBuffStat.ENHANCED_MDEF, (int) eff.emdd);
                break;
            case 21111009: // 鬥氣填充
                eff.hpR = -eff.x / 100.0;
                break;
            case 21111012: // 瑪哈祝福
                eff.statups.put(MapleBuffStat.WATK, (int) eff.pad);
                eff.statups.put(MapleBuffStat.MATK, (int) eff.mad);
                break;
            case 21120007: // 宙斯之盾
                eff.statups.put(MapleBuffStat.COMBO_BARRIER, eff.x);
                break;
            case 21121000: // 楓葉祝福
                eff.statups.put(MapleBuffStat.MAPLE_WARRIOR, eff.x);
                break;
            case 21121003: // 靈魂戰鬥
                eff.statups.put(MapleBuffStat.Stance, (int) eff.prop);
                break;
            default:
//                System.out.println("[狂狼勇士]未處理技能: " + skill);
                break;
        }
    }
}
