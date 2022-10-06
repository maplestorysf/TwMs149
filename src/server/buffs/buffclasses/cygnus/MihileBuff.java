package server.buffs.buffclasses.cygnus;

import client.MapleBuffStat;
import client.MapleJob;
import client.status.MonsterStatus;
import server.MapleStatEffect;
import server.buffs.AbstractBuffClass;

public class MihileBuff extends AbstractBuffClass {

    public MihileBuff() {
        buffs = new int[]{
            50001075, // 女皇的祈禱
            51101003, // 快速之劍
            51101004, // 激勵
            51111003, // 閃耀激發
            51111004, // 靈魂抗性
            51111005, // 魔防消除
            51121004, // 格檔
            51121005, // 楓葉祝福
            51121006, // 靈魂之怒
        };
    }

    @Override
    public boolean containsJob(int job) {
        return MapleJob.is米哈逸(job);
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        // If this initial check and the corresponding arrays are removed, 
        // there should not be any impact (i.e., it will keep its functionality). 
        if (!containsSkill(skill)) {
            return;
        }

        switch (skill) {
            case 50001075: // 女皇的祈禱
                eff.statups.put(MapleBuffStat.ECHO_OF_HERO, eff.x);
                break;
            case 51101003: // 快速之劍
                eff.statups.put(MapleBuffStat.BOOSTER, eff.x);
                break;
            case 51101004: // 激勵
                eff.statups.put(MapleBuffStat.ANGEL_ATK, (int) eff.indiePad);
                break;
            case 51111003: // 閃耀激發
                eff.statups.put(MapleBuffStat.DAMAGE_BUFF, (int) eff.indieDamR);
                break;
            case 51111004: // 靈魂抗性
                eff.statups.put(MapleBuffStat.DEFENCE_BOOST_R, eff.x);
                eff.statups.put(MapleBuffStat.AsrR, eff.y);
                eff.statups.put(MapleBuffStat.TerR, eff.z);
                break;
            case 51111005: // 魔防消除
                eff.monsterStatus.put(MonsterStatus.MAGIC_CRASH, 1);
                break;
            case 51121004: // 格檔
                eff.statups.put(MapleBuffStat.Stance, (int) eff.prop);
                break;
            case 51121005: // 楓葉祝福
                eff.statups.put(MapleBuffStat.MAPLE_WARRIOR, eff.x);
                break;
            case 51121006: // 靈魂之怒
                eff.statups.put(MapleBuffStat.ENHANCED_WATK, eff.x);
                eff.statups.put(MapleBuffStat.CRITICAL_RATE_BUFF, eff.z);
                break;
            default:
//                System.out.println("[米哈逸]未處理技能: " + skill);
                break;
        }
    }
}
