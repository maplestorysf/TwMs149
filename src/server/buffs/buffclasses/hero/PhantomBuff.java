package server.buffs.buffclasses.hero;

import client.MapleBuffStat;
import client.MapleJob;
import server.MapleStatEffect;
import server.buffs.AbstractBuffClass;

public class PhantomBuff extends AbstractBuffClass {

    public PhantomBuff() {
        buffs = new int[]{
            20031211, // 鬼鬼祟祟的移動
            20031205, // 幻影斗蓬
            24101005, // 極速手杖
            24111002, // 幸運幻影
            24111003, // 幸運卡牌守護
            24111005, // 月光賜福
            24121003, // 最終的夕陽
            24121004, // 艾麗亞祝禱
            24121008, // 楓葉祝福
        };
    }

    @Override
    public boolean containsJob(int job) {
        return MapleJob.is幻影俠盜(job);
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 20031211: // 鬼鬼祟祟的移動
                eff.statups.put(MapleBuffStat.DARKSIGHT, eff.x);
                break;
            case 20031205: // 幻影斗蓬
                eff.statups.put(MapleBuffStat.SHROUD_WALK, eff.x);
                break;
            case 24101005: // 極速手杖
                eff.statups.put(MapleBuffStat.BOOSTER, eff.x);
                break;
            case 24111002: // 幸運幻影
                eff.duration = 2100000000;
                eff.statups.put(MapleBuffStat.FINAL_FEINT, eff.x);
                break;
            case 24111003: // 幸運卡牌守護
                eff.statups.put(MapleBuffStat.AsrR, eff.x);
                eff.statups.put(MapleBuffStat.TerR, eff.y);
                eff.statups.put(MapleBuffStat.HP_BOOST_PERCENT, (int) eff.indieMhpR);
                eff.statups.put(MapleBuffStat.MP_BOOST_PERCENT, (int) eff.indieMmpR);
                break;
            case 24111005: // 月光賜福
                eff.statups.clear();
                eff.statups.put(MapleBuffStat.ANGEL_ATK, (int) eff.indiePad);
                eff.statups.put(MapleBuffStat.ANGEL_ACC, (int) eff.indieAcc);
                break;
            case 24121003: // 最終的夕陽
//                eff.info.put(MapleStatInfo.damage, eff.info.get(MapleStatInfo.v));
//                eff.info.put(MapleStatInfo.attackCount, eff.info.get(MapleStatInfo.w));
//                eff.info.put(MapleStatInfo.mobCount, eff.info.get(MapleStatInfo.x));
                break;
            case 24121004: // 艾麗亞祝禱
                eff.statups.put(MapleBuffStat.DamR, (int) eff.damR);
                eff.statups.put(MapleBuffStat.IgnoreTargetDEF, eff.x);
                break;
            case 24121008:// 楓葉祝福
                eff.statups.put(MapleBuffStat.MAPLE_WARRIOR, eff.x);
                break;
            default:
//                System.out.println("未知的 幻影俠盜(2400) Buff: " + skill);
                break;
        }
    }
}
