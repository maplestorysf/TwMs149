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
public class ThiefBuff extends AbstractBuffClass {

    public ThiefBuff() {
        buffs = new int[]{
            4001002, // 詛咒術
            4001003, // 隱身術
            4001005, // 速度激發
            4101003, // 極速暗殺
            4121003, // 挑釁
            4121004, // 忍影瞬殺
            4201002, // 快速之刀
            4201009, // 輪迴
            4201011, // 楓幣護盾
            4111002, // 影分身
            4111003, // 影網術
            4111009, // 無形鏢
            4211003, // 勇者掠奪術
            4211008, // 影分身
            4121000, // 楓葉祝福   
            4121014, // 黑暗能量
            4211005, // 楓幣護盾
            4221000, // 楓葉祝福 
            4221003, // 挑釁
            4221004, // 忍影瞬殺
            4221013, // 暗影神偷本能
            4301002, // 神速雙刀
            4301003, // 自我速度激發
            4311005, // 輪迴
            4311009, // 神速雙刀
            4321000, // 雙刃旋
            4321002, // 閃光彈
            4330001, // 進階隱身術
            4331002, // 替身術
            4331003, // 貓頭鷹召喚
            4341000, // 楓葉祝福
            4341002, // 絕殺刃
            4341003, // 怪物大爆炸
            4341007, // 荊棘特效
        };
    }

    @Override
    public boolean containsJob(int job) {
        return GameConstants.isAdventurer(job) && job / 100 == 4;
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 4001002: // 詛咒術
                eff.monsterStatus.put(MonsterStatus.WATK, eff.x);
                eff.monsterStatus.put(MonsterStatus.WDEF, eff.y);
                break;
            case 4001005: // 速度激發
            case 4301003: // 自我速度激發
                eff.statups.put(MapleBuffStat.JUMP, (int) eff.jump);
                eff.statups.put(MapleBuffStat.SPEED, (int) eff.speed);
                break;
            case 4001003: // 隱身術
                eff.statups.put(MapleBuffStat.DARKSIGHT, eff.x);
                break;
            case 4101003: // 極速暗殺
            case 4201002: // 快速之刀
            case 4311009: // 神速雙刀
                eff.statups.put(MapleBuffStat.BOOSTER, eff.x);
                break;
            case 4111001: // 幸運術
                eff.statups.put(MapleBuffStat.MESOUP, eff.x);
                break;
            case 4111003: // 影網術
                eff.monsterStatus.put(MonsterStatus.SHADOW_WEB, 1);
                break;
            case 4111009: // 無形鏢
                eff.statups.put(MapleBuffStat.SPIRIT_CLAW, 0);
                break;
            case 4121003: // 挑釁
            case 4221003: // 挑釁
                eff.monsterStatus.put(MonsterStatus.SHOWDOWN, eff.x);
                eff.monsterStatus.put(MonsterStatus.MDEF, eff.x);
                eff.monsterStatus.put(MonsterStatus.WDEF, eff.x);
                break;
            case 4121004: // 忍影瞬殺
            case 4221004: // 忍影瞬殺
                eff.monsterStatus.put(MonsterStatus.NINJA_AMBUSH, (int) eff.damage);
                break;
            case 4201009: // 輪迴
            case 4311005: // 輪迴
                eff.statups.put(MapleBuffStat.WATK, (int) eff.pad);
                break;
            case 4201011: // 楓幣護盾
            case 4211005: // 楓幣護盾
                eff.statups.put(MapleBuffStat.MESOGUARD, eff.x);
                break;
            case 4211003: // 勇者掠奪術
                eff.duration = 2100000000;
                eff.statups.put(MapleBuffStat.PICKPOCKET, eff.x);
                break;
            case 4111002: // 影分身
            case 4211008: // 影分身
            case 4331002: // 替身術
                eff.statups.put(MapleBuffStat.SHADOWPARTNER, eff.x);
                break;
            case 4121014: // 黑暗能量
                eff.statups.put(MapleBuffStat.ANGEL_ATK, (int) eff.indiePad);//test - works without
                break;
            case 4301002: // 神速雙刀
                eff.statups.put(MapleBuffStat.BOOSTER, eff.x);
                break;
            case 4321000: //tornado spin uses same buffstats
                eff.duration = 1000;
                eff.statups.put(MapleBuffStat.DASH_SPEED, 100 + eff.x);
                eff.statups.put(MapleBuffStat.DASH_JUMP, eff.y); //always 0 but its there
                break;
            case 4321002: // 閃光彈
                eff.monsterStatus.put(MonsterStatus.DARKNESS, eff.x);
                break;
            case 4330001: // 進階隱身術
                eff.statups.put(MapleBuffStat.DARKSIGHT, (int) eff.level);
                break;
            case 4331003: // 貓頭鷹召喚
                eff.duration = 2100000000;
                eff.statups.put(MapleBuffStat.OWL_SPIRIT, eff.y);
                break;
            case 4341003: // 怪物大爆炸
                eff.monsterStatus.put(MonsterStatus.MONSTER_BOMB, (int) eff.damage);
                break;
            case 4341007: // 荊棘特效
                eff.statups.put(MapleBuffStat.Stance, (int) eff.prop);
                eff.statups.put(MapleBuffStat.ENHANCED_WATK, (int) eff.epad);
                break;
//            case 4221013: // 暗影神偷本能
//                break;
            case 4121000: // 楓葉祝福
            case 4221000: // 楓葉祝福
            case 4341000: // 楓葉祝福
                eff.statups.put(MapleBuffStat.MAPLE_WARRIOR, eff.x);
                break;
            case 4341002: // 絕殺刃
                eff.duration = 60 * 1000;
                eff.hpR = -eff.x / 100.0;
                eff.statups.put(MapleBuffStat.FINAL_CUT, eff.y);
                break;
            default:
//                System.out.println("[盜賊]未處理技能: " + skill);
                break;
        }
    }
}
