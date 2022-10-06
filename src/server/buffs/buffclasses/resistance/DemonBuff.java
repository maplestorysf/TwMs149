package server.buffs.buffclasses.resistance;

import client.MapleBuffStat;
import client.status.MonsterStatus;
import constants.GameConstants;
import server.MapleStatEffect;
import server.buffs.AbstractBuffClass;

/**
 *
 * @author Sunny
 */
public class DemonBuff extends AbstractBuffClass {

    public DemonBuff() {
        buffs = new int[]{
            31001001, // 惡魔推進器
            31101003, // 黑暗復仇
            31111004, // 黑暗耐力
            31121007, // 無限力量
            31121004, // 楓葉祝福
            31121005, // 變形
            31121002, // 吸血鬼之觸
            31121003, // 魔力吶喊
        };
    }

    @Override
    public boolean containsJob(int job) {
        return GameConstants.isDemon(job);
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 31001001: // 惡魔推進器
                eff.statups.put(MapleBuffStat.BOOSTER, eff.x);
                break;
            case 31101003: // 黑暗復仇
                eff.statups.put(MapleBuffStat.PERFECT_ARMOR, eff.y);
                break;
            case 31111004: // 黑暗耐力
                eff.statups.put(MapleBuffStat.AsrR, eff.y);
                eff.statups.put(MapleBuffStat.TerR, eff.z);
                eff.statups.put(MapleBuffStat.DEFENCE_BOOST_R, eff.x);
                break;
            case 31121002: // 吸血鬼之觸
                eff.statups.put(MapleBuffStat.COMBO_DRAIN, eff.x);
                break;
            case 31121003: // 魔力吶喊
                eff.monsterStatus.put(MonsterStatus.SHOWDOWN, eff.w);
                eff.monsterStatus.put(MonsterStatus.MDEF, eff.x);
                eff.monsterStatus.put(MonsterStatus.WDEF, eff.x);
                eff.monsterStatus.put(MonsterStatus.MATK, eff.x);
                eff.monsterStatus.put(MonsterStatus.WATK, eff.x);
                eff.monsterStatus.put(MonsterStatus.ACC, eff.x);
                break;
            case 31121004: // 楓葉祝福
                eff.statups.put(MapleBuffStat.MAPLE_WARRIOR, eff.x);
                break;
            case 31121005: // 變形
                eff.statups.put(MapleBuffStat.DamR, (int) eff.damR);
                eff.statups.put(MapleBuffStat.DARK_METAMORPHOSIS, 6); // mob count
                break;
            case 31121007: // 無限力量
                eff.statups.put(MapleBuffStat.BOUNDLESS_RAGE, 1); // for now
                break;
            default:
//                System.out.println("[惡魔殺手]未處理技能: " + skill);
                break;
        }
    }
}
