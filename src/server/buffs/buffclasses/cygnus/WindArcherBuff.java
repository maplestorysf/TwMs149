package server.buffs.buffclasses.cygnus;

import client.MapleBuffStat;
import client.MapleJob;
import server.MapleStatEffect;
import server.buffs.AbstractBuffClass;

/**
 *
 * @author Maple
 */
public class WindArcherBuff extends AbstractBuffClass {

    public WindArcherBuff() {
        buffs = new int[]{
            13101001, // 快速之箭
            13101002, // 終極攻擊
            13101003, // 無形之箭
            13101006, // 風影漫步
            13111001, // 集中
        };
    }

    @Override
    public boolean containsJob(int job) {
        return MapleJob.is皇家騎士團(job) && (job / 100) % 10 == 3;
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 13101001: // 快速之箭
                eff.statups.put(MapleBuffStat.BOOSTER, eff.x);
                break;
            case 13101002: // 終極攻擊
                eff.statups.put(MapleBuffStat.FINALATTACK, eff.x);
                break;
            case 13101003: // 無形之箭
                eff.statups.put(MapleBuffStat.SOULARROW, eff.x);
                break;
            case 13101006: // 風影漫步
                eff.statups.put(MapleBuffStat.WIND_WALK, eff.x);
                break;
            case 13111001: // 集中
                eff.statups.put(MapleBuffStat.CONCENTRATE, eff.x);
                break;
            default:
                System.out.println("Unhandled Buff: " + skill);
                break;
        }
    }
}
