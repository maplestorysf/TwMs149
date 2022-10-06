package server.buffs.buffclasses.adventurer;

import client.MapleBuffStat;
import client.status.MonsterStatus;
import constants.GameConstants;
import server.MapleStatEffect;
import server.buffs.AbstractBuffClass;

/**
 *
 * @author Itzik
 */
public class WarriorBuff extends AbstractBuffClass {

    public WarriorBuff() {
        buffs = new int[]{
            1001003, // 自身強化
            1101004, // 極速武器
            1101006, // 激勵
            1101007, // 反射之盾
            1111003, // 黑暗之劍
            1111007, // 防禦消除
            1121002, // 格擋
            1201004, // 極速武器
            1201006, // 降魔咒
            1201007, // 反射之盾
            1301004, // 極速武器
            1301006, // 禦魔陣
            1301007, // 神聖之火
            1211004, // 烈焰之劍
            1211006, // 寒冰之劍
            1211008, // 雷鳴之劍
            1211009, // 魔防消除
            1211011, // 戰鬥命令
            1121000, // 楓葉祝福
            1121010, // 鬥氣爆發
            1221000, // 楓葉祝福
            1221002, // 格擋
            1211010, // 復原
            1211011, // 戰鬥命令
            1221004, // 聖靈之劍
            1220013, // 祝福護甲
            1311005, // 龍之獻祭
            1311006, // 龍咆哮
            1311007, // 魔防消除
            1311008, // 龍之力量
            1321000, // 楓葉祝福
            1321002, // 格擋
            1321007, // 暗之靈魂
            1320008, // 闇靈治癒
            1320009, // 黑暗守護
            1320011, // 暗之靈魂的復仇
        };
    }

    @Override
    public boolean containsJob(int job) {
        return GameConstants.isAdventurer(job) && job / 100 == 1;
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 1001003: // 自身強化
                eff.statups.put(MapleBuffStat.WDEF, (int) eff.pdd);
                break;
            case 1101004: // 極速武器
            case 1201004: // 極速武器
            case 1301004: // 極速武器
                eff.statups.put(MapleBuffStat.BOOSTER, eff.x);
                break;
            case 1101006: // 激勵
                eff.statups.put(MapleBuffStat.WATK, (int) eff.pad);
                break;
            case 1101007: // 反射之盾
            case 1201007: // 反射之盾
                eff.statups.put(MapleBuffStat.POWERGUARD, eff.x);
                break;
            case 1111003: // 黑暗之劍
                eff.monsterStatus.put(MonsterStatus.DARKNESS, eff.x);
                break;
            case 1111007: // 防禦消除
            case 1211009: // 魔防消除
            case 1311007: // 魔防消除
                eff.monsterStatus.put(MonsterStatus.MAGIC_CRASH, 1);
                break;
            case 1121002: // 格擋
            case 1221002: // 格擋
            case 1321002: // 格擋
                eff.statups.put(MapleBuffStat.Stance, (int) eff.prop);
                break;
            case 1121010: // 鬥氣爆發
                eff.statups.put(MapleBuffStat.ENRAGE, eff.x * 100 + eff.mobCount);
                eff.statups.put(MapleBuffStat.CRITICAL_INC, eff.z);
                break;
            case 1201006: // 降魔咒
                eff.monsterStatus.put(MonsterStatus.WATK, eff.x);
                eff.monsterStatus.put(MonsterStatus.WDEF, eff.x);
                eff.monsterStatus.put(MonsterStatus.DARKNESS, eff.z);
                break;
            case 1211004: // 烈焰之劍
            case 1211006: // 寒冰之劍
            case 1211008: // 雷鳴之劍
            case 1221004: // 聖靈之劍
                eff.statups.put(MapleBuffStat.WK_CHARGE, eff.x);
                break;
            case 1211010: // 復原
                eff.hpR = eff.x / 100.0;
                break;
            case 1211011: // 戰鬥命令
                eff.statups.put(MapleBuffStat.COMBAT_ORDERS, eff.x);
                break;
            case 1220013: // 祝福護甲
                eff.statups.put(MapleBuffStat.DIVINE_SHIELD, eff.x + 1);
                break;
            case 1301006: // 禦魔陣
                eff.statups.put(MapleBuffStat.MDEF, (int) eff.mdd);
                eff.statups.put(MapleBuffStat.WDEF, (int) eff.pdd);
                break;
            case 1301007: // 神聖之火
                eff.statups.put(MapleBuffStat.MAXHP, eff.x);
                eff.statups.put(MapleBuffStat.MAXMP, eff.x);
                break;
            case 1311005: // 龍之獻祭
            case 1311006: // 龍咆哮
                eff.hpR = -eff.x / 100.0;
                break;
            case 1311008: // 龍之力量
                eff.statups.put(MapleBuffStat.DRAGONBLOOD, eff.x);
                break;
            case 1321007: // 暗之靈魂
                eff.statups.put(MapleBuffStat.BEHOLDER, (int) eff.level);
                break;
            case 1121000: // 楓葉祝福
            case 1221000: // 楓葉祝福
            case 1321000: // 楓葉祝福
                eff.statups.put(MapleBuffStat.MAPLE_WARRIOR, eff.x);
                break;
            default:
//                System.out.println("[劍士]未處理技能: " + skill);
                break;
        }
    }
}
