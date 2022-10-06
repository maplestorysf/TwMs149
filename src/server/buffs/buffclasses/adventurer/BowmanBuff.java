package server.buffs.buffclasses.adventurer;

import client.MapleBuffStat;
import client.status.MonsterStatus;
import constants.GameConstants;
import server.MapleStatEffect;
import server.buffs.AbstractBuffClass;

/**
 *
 * @author Maple
 */
public class BowmanBuff extends AbstractBuffClass {

    public BowmanBuff() {
        buffs = new int[]{
            3101002, // 快速之弓
            3101004, // 無形之箭
            3111000, // 集中
            3201002, // 快速之弩
            3201004, // 無形之箭
            3211000, // 集中
            3121000, // 楓葉祝福
            3121002, // 會心之眼
            3120006, // 鳳凰附體
            3121007, // 爆發
            3220005, // 銀隼附體
            3221000, // 楓葉祝福
            3221002, // 會心之眼
            3221006, // 爆發
        };
    }

    @Override
    public boolean containsJob(int job) {
        return GameConstants.isAdventurer(job) && job / 100 == 3;
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 3101002: // 快速之弓
            case 3201002: // 快速之弩
                eff.statups.put(MapleBuffStat.BOOSTER, eff.x);
                break;
            case 3101004: // 無形之箭
            case 3201004: // 無形之箭
                eff.statups.put(MapleBuffStat.SOULARROW, eff.x);
                break;
            case 3111000: // 集中
            case 3211000: // 集中
                eff.statups.put(MapleBuffStat.CONCENTRATE, eff.x);
                break;
            case 3121000: // 楓葉祝福
            case 3221000: // 楓葉祝福
                eff.statups.put(MapleBuffStat.MAPLE_WARRIOR, eff.x);
                break;
            case 3121002: // 會心之眼
            case 3221002: // 會心之眼
                eff.statups.put(MapleBuffStat.SHARP_EYES, (eff.x << 8) + eff.y);
                break;
            case 3120006: // 鳳凰附體
            case 3220005: // 銀隼附體
                eff.statups.put(MapleBuffStat.TerR, (int) eff.terR);
                eff.statups.put(MapleBuffStat.SPIRIT_LINK, 1);
                break;
            case 3121007: // 爆發
                eff.statups.put(MapleBuffStat.HAMSTRING, eff.x);
                eff.monsterStatus.put(MonsterStatus.SPEED, eff.x);
                break;
            case 3221006: // 爆發
                eff.statups.put(MapleBuffStat.BLIND, eff.x);
                eff.monsterStatus.put(MonsterStatus.ACC, eff.x);
                break;
            default:
//                System.out.println("[弓箭手]未處理技能: " + skill);
                break;
        }
    }
}
