package server.buffs.buffclasses.cygnus;

import client.MapleBuffStat;
import client.MapleJob;
import server.MapleStatEffect;
import server.buffs.AbstractBuffClass;

/**
 *
 * @author Fate
 */
public class ThunderBreakerBuff extends AbstractBuffClass {

    public ThunderBreakerBuff() {
        buffs = new int[]{
            15001003, // 衝鋒
            15100004, // 蓄能激發
            15101002, // 致命快打
            15101006, // 雷鳴
            15101008, // 全神貫注
            15111005, // 最終極速
            15111006, // 閃光擊
        };
    }

    @Override
    public boolean containsJob(int job) {
        return MapleJob.is閃雷悍將(job);
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 15001003: // 衝鋒
                eff.statups.put(MapleBuffStat.DASH_SPEED, eff.x);
                eff.statups.put(MapleBuffStat.DASH_JUMP, eff.y);
                break;
            case 15100004: // 蓄能激發
                eff.statups.put(MapleBuffStat.ENERGY_CHARGE, 0);
                break;
            case 15101002: // 致命快打
                eff.statups.put(MapleBuffStat.BOOSTER, eff.x);
                break;
            case 15101006: // 雷鳴
                eff.statups.put(MapleBuffStat.WK_CHARGE, eff.x);
//                eff.statups.put(MapleBuffStat.HowlingAttackDamage, eff.info.get(MapleStatInfo.z));
                break;
            case 15101008: // 全神貫注
                eff.statups.put(MapleBuffStat.ANGEL_ATK, (int) eff.indiePad);
                eff.statups.put(MapleBuffStat.ACC, (int) eff.acc);
                break;
            case 15111005: // 最終極速
                eff.statups.put(MapleBuffStat.SPEED_INFUSION, eff.x);
                break;
            case 15111006: // 閃光擊
                eff.statups.put(MapleBuffStat.SPARK, eff.x);
                break;
            default:
                // System.out.println("Hayato skill not coded: " + skill);
                break;
        }
    }
}
