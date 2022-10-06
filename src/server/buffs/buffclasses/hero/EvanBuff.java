package server.buffs.buffclasses.hero;

import client.MapleBuffStat;
import client.MapleJob;
import client.status.MonsterStatus;
import server.MapleStatEffect;
import server.buffs.AbstractBuffClass;

/**
 *
 * @author Charmander
 */
public class EvanBuff extends AbstractBuffClass {

    public EvanBuff() {
        buffs = new int[]{
            22000002, // 自然力變弱
            22111001, // 魔心防禦
            22131001, // 守護之力
            22131002, // 自然力重置
            22141002, // 極速詠唱
            22151002, // 襲殺翼
            22151003, // 魔法抵抗．改
            22161002, // 鬼神詛咒
            22161004, // 龍神的庇護
            22161005, // 瞬間移動精通
            22171000, // 楓葉祝福
            22181000, // 龍神的祝福
            22181003, // 靈魂之石
            22181004, // 歐尼斯的意志
        };
    }

    @Override
    public boolean containsJob(int job) {
        return MapleJob.is龍魔導士(job);
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 22000002: // 自然力變弱
                eff.statups.put(MapleBuffStat.ELEMENT_WEAKEN, eff.x);
                break;
            case 22111001: // 魔心防禦
                eff.statups.put(MapleBuffStat.MAGIC_GUARD, eff.x);
                break;
            case 22131001: // 守護之力
                eff.statups.put(MapleBuffStat.MAGIC_SHIELD, eff.x);
                break;
            case 22131002: // 自然力重置
                eff.statups.put(MapleBuffStat.ELEMENT_RESET, eff.x);
                break;
            case 22141002: // 極速詠唱
                eff.statups.put(MapleBuffStat.BOOSTER, eff.x);
                break;
            case 22151002: // 襲殺翼
                eff.duration = 2100000000;
                eff.statups.put(MapleBuffStat.HOMING_BEACON, eff.x);
                break;
            case 22151003: // 魔法抵抗．改
                eff.statups.put(MapleBuffStat.MAGIC_RESISTANCE, eff.x);
                break;
            case 22161002: // 鬼神詛咒
                eff.monsterStatus.put(MonsterStatus.IMPRINT, eff.x);
                break;
            case 22161004: // 龍神的庇護
                eff.statups.put(MapleBuffStat.ONYX_SHROUD, eff.x);
                break;
            case 22161005: // 瞬間移動精通
                eff.mpCon = (short) eff.y;
                eff.duration = 2100000000;
                eff.statups.put(MapleBuffStat.TELEPORT_MASTERY, eff.x);
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case 22171000: // 楓葉祝福
                eff.statups.put(MapleBuffStat.MAPLE_WARRIOR, eff.x);
                break;
            case 22181000: // 龍神的祝福
                eff.statups.put(MapleBuffStat.ENHANCED_WDEF, (int) eff.epdd); // epdd
                eff.statups.put(MapleBuffStat.ENHANCED_MDEF, (int) eff.emdd); // emdd
                eff.statups.put(MapleBuffStat.ENHANCED_MATK, (int) eff.emad); // emad
                break;
            case 22181003: // 靈魂之石
                eff.statups.put(MapleBuffStat.SOUL_STONE, 1);
                break;
            case 22181004: // 歐尼斯的意志
                eff.statups.put(MapleBuffStat.ONYX_WILL, (int) eff.damage); //is this the right order
                eff.statups.put(MapleBuffStat.Stance, (int) eff.prop);
                break;
            default:
//                System.out.println("[龍魔導士]未處理技能: " + skill);
                break;
        }
    }
}
