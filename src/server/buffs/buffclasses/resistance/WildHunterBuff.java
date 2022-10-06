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
public class WildHunterBuff extends AbstractBuffClass {

    public WildHunterBuff() {
        buffs = new int[]{
            33001003, // 快速之弩
            33101003, // 無形之箭
            33101005, // 吞食
            33111004, // 黑暗狙擊
            33111007, // 狂獸附體
            33121004, // 銳利之眼
            33121005, // 化學彈丸
            33121007, // 楓葉祝福
        };
    }

    @Override
    public boolean containsJob(int job) {
        return GameConstants.isWildHunter(job);
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 33001003: // 快速之弩
                eff.statups.put(MapleBuffStat.BOOSTER, eff.x);
                break;
            case 33101003: // 無形之箭
                eff.statups.put(MapleBuffStat.SOULARROW, eff.x);
                break;
//            case 33101005:// 吞食
//                eff.statups.put(MapleBuffStat.HowlingMaxMP, eff.info.get(MapleStatInfo.x));
//                eff.statups.put(MapleBuffStat.HowlingCritical, eff.info.get(MapleStatInfo.y));
//                eff.statups.put(MapleBuffStat.TORNADO, eff.info.get(MapleStatInfo.z));
////                eff.statups.put(MapleBuffStat.SATELLITESAFE_ABSORB, eff.info.get(MapleStatInfo.lt));
////                eff.statups.put(MapleBuffStat.SoulArrow, eff.info.get(MapleStatInfo.rb));                
//                break;
            case 33111004: // 黑暗狙擊
                eff.statups.put(MapleBuffStat.BLIND, eff.x);
                eff.monsterStatus.put(MonsterStatus.ACC, eff.x);
                break;
            case 33111007: // 狂獸附體
                eff.statups.put(MapleBuffStat.SPEED, eff.z);
                eff.statups.put(MapleBuffStat.ATTACK_BUFF, eff.y);
                eff.statups.put(MapleBuffStat.FELINE_BERSERK, eff.x);
                break;
            case 33121004: // 銳利之眼
                eff.statups.put(MapleBuffStat.SHARP_EYES, (eff.x << 8) + eff.y);
                break;
            case 33121005: // 化學彈丸
                eff.monsterStatus.put(MonsterStatus.SHOWDOWN, eff.x);
                eff.monsterStatus.put(MonsterStatus.MDEF, eff.x);
                eff.monsterStatus.put(MonsterStatus.WDEF, eff.x);
                break;
            case 33121007: // 楓葉祝福
                eff.statups.put(MapleBuffStat.MAPLE_WARRIOR, eff.x);
                break;
            default:
//                 System.out.println("Unhandled WildHunter Buff: " + skill);
                break;
        }
    }
}
