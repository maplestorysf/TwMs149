package server.buffs.buffclasses.adventurer;

import client.MapleBuffStat;
import client.MapleJob;
import server.MapleStatEffect;
import server.buffs.AbstractBuffClass;

/**
 *
 * @author Administrator
 */
public class ChivalrousBuff extends AbstractBuffClass {

    public ChivalrousBuff() {
        buffs = new int[]{
                5701005, // 迅雷再起
                5701006, // 聚精會神
                5721000, // 楓葉祝福
                5721009, // 海盜風采
                5720012, // 反擊
        };
    }

    @Override
    public boolean containsJob(int job) {
        return MapleJob.is蒼龍俠客(job);
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 5701005: // 迅雷再起
                eff.statups.put(MapleBuffStat.BOOSTER, eff.x);
                break;
            case 5701006: // 聚精會神
                eff.statups.put(MapleBuffStat.ANGEL_ATK, (int) eff.indiePad);
                break;
            case 5721000: // 楓葉祝福
                eff.statups.put(MapleBuffStat.MAPLE_WARRIOR, eff.x);
                break;
            case 5721009: // 海盜風采
                eff.statups.put(MapleBuffStat.TerR, eff.x);
                eff.statups.put(MapleBuffStat.AsrR, eff.y);//or x?
                eff.statups.put(MapleBuffStat.DamR, (int) eff.damR);
                eff.statups.put(MapleBuffStat.Stance, eff.z);
                eff.statups.put(MapleBuffStat.EVA, (int) eff.eva);
                break;
            case 5720012:
                eff.cooldown = eff.x;
                eff.statups.put(MapleBuffStat.DamR, (int) eff.indieDamR);
                break;
            default:
                System.out.println("未知的 蒼龍俠客(572) Buff技能: " + skill);
                break;
        }
    }
}
