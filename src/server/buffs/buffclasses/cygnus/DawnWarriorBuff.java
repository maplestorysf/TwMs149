package server.buffs.buffclasses.cygnus;

import client.MapleBuffStat;
import client.MapleJob;
import client.status.MonsterStatus;
import server.MapleStatEffect;
import server.buffs.AbstractBuffClass;

public class DawnWarriorBuff extends AbstractBuffClass {

    public DawnWarriorBuff() {
        buffs = new int[]{
            11001001, // 自身強化
            11101001, // 快速之劍
            11101002, // 終極攻擊
            11101003, // 憤怒
            11101006, // 反射之盾
            11111002, // 黑暗之劍
            11111003, // 昏迷之劍
            11111007, // 靈魂屬性
            11111008, // 魔防消除
        };
    }

    @Override
    public boolean containsJob(int job) {
        return MapleJob.is聖魂劍士(job);
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 11001001: // 自身強化
                eff.statups.put(MapleBuffStat.WDEF, (int) eff.pdd);
                break;
            case 11101001: // 快速之劍
                eff.statups.put(MapleBuffStat.BOOSTER, eff.x);
                break;
            case 11101002: // 終極攻擊
                eff.statups.put(MapleBuffStat.FINALATTACK, eff.x);
                break;
            case 11101003: // 憤怒
                eff.statups.put(MapleBuffStat.WATK, (int) eff.pad);
                break;
            case 11101006: // 反射之盾
                eff.statups.put(MapleBuffStat.POWERGUARD, eff.x);
                break;
            case 11111002: // 黑暗之劍
                eff.monsterStatus.put(MonsterStatus.DARKNESS, eff.x);
                break;
            case 11111003: // 昏迷之劍
                eff.monsterStatus.put(MonsterStatus.STUN, eff.x);
                break;
            case 11111007: // 靈魂屬性
                eff.statups.put(MapleBuffStat.WK_CHARGE, eff.x);
//                eff.statups.put(MapleBuffStat.DamR, eff.info.get(MapleStatInfo.z));
                break;
            case 11111008: // 魔防消除
                eff.monsterStatus.put(MonsterStatus.MAGIC_CRASH, 1);
//                eff.statups.put(MapleBuffStat.DamR, eff.info.get(MapleStatInfo.z));
                break;
            default:
                break;
        }
    }
}
