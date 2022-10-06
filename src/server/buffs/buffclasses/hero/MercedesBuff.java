package server.buffs.buffclasses.hero;

import client.MapleBuffStat;
import client.MapleJob;
import client.status.MonsterStatus;
import server.MapleStatEffect;
import server.buffs.AbstractBuffClass;

public class MercedesBuff extends AbstractBuffClass {

    public MercedesBuff() {
        buffs = new int[]{
            23101002, // 雙弩槍推進器
            23101003, // 靈魂灌注
            23111002, // 獨角獸射擊
            23111004, // 依古尼斯咆哮
            23111005, // 水之盾
            23121000, // 伊修塔爾之環
            23121002, // 傳說之槍
            23121004, // 遠古意志
            23121005, // 楓葉祝福
        };
    }

    @Override
    public boolean containsJob(int job) {
        return MapleJob.is精靈遊俠(job);
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 23101002: // 雙弩槍推進器
                eff.statups.put(MapleBuffStat.BOOSTER, (int) eff.x);
                break;
            case 23101003: // 靈魂灌注
                eff.statups.put(MapleBuffStat.SPIRIT_SURGE, eff.x);
                eff.statups.put(MapleBuffStat.CRITICAL_INC, eff.x);
                break;
            case 23111002: // 獨角獸射擊
                eff.monsterStatus.put(MonsterStatus.IMPRINT, eff.x);
                break;
            case 23111004: // 依古尼斯咆哮
                eff.statups.put(MapleBuffStat.ANGEL_ATK, (int) eff.indiePad);
//                eff.statups.put(MapleBuffStat.AddAttackCount, 1);
                break;
            case 23111005: // 水之盾
                eff.statups.put(MapleBuffStat.AsrR, (int) eff.terR);
                eff.statups.put(MapleBuffStat.TerR, (int) eff.terR);
                eff.statups.put(MapleBuffStat.WATER_SHIELD, eff.x);
                break;
            case 23121000: // 伊修塔爾之環
//                eff.statups.put(MapleBuffStat.KeyDownMoving, 0);
                break;
            case 23121002: // 傳說之槍
                eff.monsterStatus.put(MonsterStatus.WDEF, -eff.x);
                break;
            case 23121004: // 遠古意志
                eff.statups.put(MapleBuffStat.DamR, (int) eff.damR);
                eff.statups.put(MapleBuffStat.ENHANCED_MAXHP, (int) eff.emhp);
                break;
            case 23121005: // 楓葉祝福
                eff.statups.put(MapleBuffStat.MAPLE_WARRIOR, eff.x);
                break;
            default:
                System.out.println("未知的 精靈遊俠(2300) Buff: " + skill);
                break;
        }
    }
}
