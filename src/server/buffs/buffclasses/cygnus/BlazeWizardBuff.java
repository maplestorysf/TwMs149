package server.buffs.buffclasses.cygnus;

import client.MapleBuffStat;
import client.MapleJob;
import client.status.MonsterStatus;
import server.MapleStatEffect;
import server.buffs.AbstractBuffClass;

public class BlazeWizardBuff extends AbstractBuffClass {

    public BlazeWizardBuff() {
        buffs = new int[]{
            12000006, // 自然力變弱
            12001001, // 魔心防禦
            12001002, // 魔力之盾
            12101000, // 精神強力
            12101001, // 緩速術
            12101004, // 極速詠唱
            12101005, // 自然力重置
            12111007, // 瞬間移動精通
        };
    }

    @Override
    public boolean containsJob(int job) {
        return MapleJob.is烈焰巫師(job);
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 12000006: // 自然力變弱
                eff.statups.put(MapleBuffStat.ELEMENT_WEAKEN, eff.x);
                break;
            case 12001001: // 魔心防禦
                eff.statups.put(MapleBuffStat.MAGIC_GUARD, eff.x);
                break;
            case 12001002: // 魔力之盾
                eff.statups.put(MapleBuffStat.WDEF, (int) eff.pdd);
                eff.statups.put(MapleBuffStat.MDEF, (int) eff.mdd);
                break;
            case 12101000: // 精神強力
                eff.statups.put(MapleBuffStat.MATK, (int) eff.mad);
                break;
            case 12101001: // 緩速術
                eff.monsterStatus.put(MonsterStatus.SPEED, eff.x);
                break;
            case 12101004: // 極速詠唱
                eff.statups.put(MapleBuffStat.BOOSTER, eff.x);
                break;
            case 12101005: // 自然力重置
                eff.statups.put(MapleBuffStat.ELEMENT_RESET, eff.x);
                break;
            case 12111007: // 瞬間移動精通
                eff.mpCon = (short) eff.y;
                eff.duration = 2100000000;
                eff.statups.put(MapleBuffStat.TELEPORT_MASTERY, eff.x);
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            default:
                // System.out.println("Unhandled Buff: " + skill);
                break;
        }
    }
}
