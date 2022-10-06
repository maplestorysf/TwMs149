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
public class MagicianBuff extends AbstractBuffClass {

    public MagicianBuff() {
        buffs = new int[]{
            2000007, // 自然力變弱
            2001002, //Magic Guard
            2001003, //Magic Armour
            2101001, //Meditation
            2101003, // 緩速術
            2120010, // 神秘狙擊
            2201001, //Meditation
            2201003, // 緩速術
            2300009, //Blessed Ensemble - passive but buff?
            2301004, //Bless    
            2301008, //Magic Booster
            2101008, //Magic Booster
            2201010, //Magic Booster
            2301003, //Invicible
            2111005, //Spell Booster
            2111007, //Teleport Mastery
            2111008, //Elemental Decrease
            2211005, //Spell Booster
            2211007, //Teleport Mastery
            2211008, //Elemental Decrease
            2220010, // 神秘狙擊
            2311011, //Holy Fountain
            2311012, //Divine Protection
            2211012, //Elemental Adaptation (Ice, Lightning)
            2111011, //Elemental Adaptation (Fire, Poison)
            2311002, //Mystic Door
            2311003, //Holy Symbol
            2311005, // 喚化術
            2311006, // 極速詠唱
            2311007, //Teleport Mastery
            2311009, // 聖十字魔法盾
            2121000, //Maple Warrior
            2121004, //Infinity
            2121009, //Buff Mastery
            2221000, //Maple Warrior
            2221004, //Infinity
            2121004, //Infinity
            2221001, // 核爆術
            2221004, //Infinity
            2221009, //Buff Mastery
            2320011, // 神秘狙擊
            2321000, //Maple Warrior
            2321004, //Infinity
            2321005, //Advanced Blessing
            2321010, //Buff Mastery
            2121053, //Epic Adventure
            2121054, //Inferno Aura
            2221053, //Epic Adventure
            2221054, //Absolute Zero Aura
            2321053, //Epic Adventure
            2321054, //Avenging Angel
        };
    }

    @Override
    public boolean containsJob(int job) {
        return GameConstants.isAdventurer(job) && job / 100 == 2;
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 2000007: // 自然力變弱
                eff.statups.put(MapleBuffStat.ELEMENT_WEAKEN, eff.x);
                break;
            case 2001002: // 魔心防禦
                eff.statups.put(MapleBuffStat.MAGIC_GUARD, eff.x);
                break;
            case 2101003: // 緩速術
            case 2201003: // 緩速術
                eff.monsterStatus.put(MonsterStatus.SPEED, eff.x);
                break;
            case 2120010: // 神秘狙擊
            case 2220010: // 神秘狙擊
            case 2320011: // 神秘狙擊
                eff.duration = 5000;
                eff.statups.put(MapleBuffStat.ARCANE_AIM, eff.x);
                eff.overTime = true;
                break;
            case 2121009: // 大師魔法
            case 2221009: // 大師魔法
            case 2321010: // 大師魔法
                eff.duration = 2100000000;
                eff.statups.put(MapleBuffStat.BUFF_MASTERY, eff.x);
                break;
            case 2301004: // 天使祝福  
                eff.statups.put(MapleBuffStat.BLESS, (int) eff.level);
                break;
            case 2301003: // 神聖之光
                eff.statups.put(MapleBuffStat.INVINCIBLE, eff.x);
                break;
            case 2311005: // 喚化術
                eff.monsterStatus.put(MonsterStatus.DOOM, 1);
                break;
            case 2311009: // 聖十字魔法盾
                eff.statups.put(MapleBuffStat.HOLY_MAGIC_SHELL, eff.x);
                eff.cooldown = eff.y;
                eff.hpR = eff.z / 100.0;
                break;
            case 2111008: // 自然力重置
            case 2211008: // 自然力重置
                eff.statups.put(MapleBuffStat.ELEMENT_RESET, eff.x);
                break;
            case 2111005: // 極速詠唱
            case 2211005: // 極速詠唱
            case 2311006: // 極速詠唱
                eff.statups.put(MapleBuffStat.BOOSTER, eff.x);
                break;
            case 2221001: // 核爆術
                eff.monsterStatus.put(MonsterStatus.FREEZE, 1);
                eff.duration *= 2; // freezing skills are a little strange
                break;
            case 2311002: // 時空門
                eff.statups.put(MapleBuffStat.SOULARROW, eff.x);
                break;
            case 2311003: // 神聖祈禱
                eff.statups.put(MapleBuffStat.HOLY_SYMBOL, eff.x);
                break;
            case 2111007: // 瞬間移動精通
            case 2211007: // 瞬間移動精通
            case 2311007: // 瞬間移動精通
                eff.mpCon = (short) eff.y;
                eff.duration = 2100000000;
                eff.statups.put(MapleBuffStat.TELEPORT_MASTERY, eff.x);
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case 2121004: // 魔力無限
            case 2221004: // 魔力無限
            case 2321004: // 魔力無限
                eff.hpR = eff.y / 100.0;
                eff.mpR = eff.y / 100.0;
                eff.statups.put(MapleBuffStat.INFINITY, eff.x);
                eff.statups.put(MapleBuffStat.Stance, (int) eff.prop);
                break;
            case 2321002: // 魔法反射
                eff.statups.put(MapleBuffStat.MANA_REFLECTION, 1);
                break;
            case 2321005: // 進階祝福
                eff.statups.put(MapleBuffStat.HOLY_SHIELD, (int) eff.level);
                eff.statups.put(MapleBuffStat.HP_BOOST, (int) eff.indieMhp);
                eff.statups.put(MapleBuffStat.MP_BOOST, (int) eff.indieMmp);
                break;
            case 2121000: // 楓葉祝福
            case 2221000: // 楓葉祝福
            case 2321000: // 楓葉祝福
                eff.statups.put(MapleBuffStat.MAPLE_WARRIOR, eff.x);
                break;
            default:
//                System.out.println("[法師]未處理技能: " + skill);
                break;
        }
    }
}
