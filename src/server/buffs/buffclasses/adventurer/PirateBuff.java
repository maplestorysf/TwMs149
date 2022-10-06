package server.buffs.buffclasses.adventurer;

import client.MapleBuffStat;
import client.MapleJob;
import client.status.MonsterStatus;
import server.MapleStatEffect;
import server.buffs.AbstractBuffClass;

/**
 *
 * @author Maple
 */
public class PirateBuff extends AbstractBuffClass {

    public PirateBuff() {
        buffs = new int[]{
            5001005, // 衝鋒
            5011002, // 迴避砲擊
            5101011, // Dark Clarity
            5701006, // Dark Clarity
            5101006, // Knuckle Booster
            5111007, // Roll Of The Dice
            5211007, // Roll Of The Dice
            5111010, // 雲體風身
            5120011, // 反擊姿態
            5121015, // 拳霸大師
            5121010, // Time Leap
            5121009, // Speed Infusion
            5121000, // Maple Warrior
            5221000, // Maple Warrior
            5321005, // Maple Warrior
            5721000, // Maple Warrior
            5110001, // 蓄能激發
            5121054, // Stimulating Conversation
            5121053, // Epic Adventure
            5221053, // Epic Adventure
            5321053, // Epic Adventure
            5721053, // Epic Adventure
            5201012, // Scurvy Summons
            5201003, // Gun Booster
            5201008, // Infinity Blast
            5211006, // 指定攻擊
            5211009, // 魔法彈丸
            5220011, // 精準砲擊
            5220012, // 反擊
            5221009, // 心靈控制
            5221018, // Jolly Roger
            5221021, // Quickdraw
            5221054, // Whaler's Potion
            5301002, // Cannon Booster
            5301003, // Monkey Magic
            5311004, // Barrel Roulette
            5311005, // Luck of the Die
            5321010, // Pirate's Spirit
            5321054, // Buckshot
            5701005, // Gun Booster
            5711011, // Roll of the Dice
            5721054, // Bionic Maximizer
            5721052,};
    }

    @Override
    public boolean containsJob(int job) {
        return MapleJob.is冒險家(job) && MapleJob.is海盜(job) && !MapleJob.is蒼龍俠客(job);
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 5001005: // 衝鋒
                eff.statups.put(MapleBuffStat.DASH_SPEED, eff.x);
                eff.statups.put(MapleBuffStat.DASH_JUMP, eff.y);
                break;
            case 5011002: // 迴避砲擊
                eff.monsterStatus.put(MonsterStatus.SPEED, eff.z);
                break;
            case 5101011: // 全神貫注
                eff.statups.put(MapleBuffStat.ANGEL_ATK, (int) eff.indiePad);
                eff.statups.put(MapleBuffStat.ANGEL_ACC, (int) eff.acc);
                break;
            case 5101006: // 致命快打
            case 5201003: // 迅雷再起
            case 5301002: // 加農砲推進器
                eff.statups.put(MapleBuffStat.BOOSTER, eff.x);
                break;
//            case 5111007: // Roll Of The Dice
//            case 5211007: // Roll Of The Dice
//            case 5311005: // Luck of the Die
//            case 5711011: // Roll of the Dice
//                eff.statups.put(MapleBuffStat.Dice, 0);
//                break;
            case 5110001: // 蓄能激發
                eff.statups.put(MapleBuffStat.ENERGY_CHARGE, 0);
                break;
            case 5111010: // 雲體風身
                eff.statups.put(MapleBuffStat.WATER_SHIELD, eff.x);
                break;
            case 5121015: // 拳霸大師
                eff.statups.put(MapleBuffStat.DamR, eff.x);
//                eff.statups.put(MapleBuffStat.AsrR, (int) eff.asrR);
//                eff.statups.put(MapleBuffStat.TerR, (int) eff.terR);
                break;
            case 5120011: // 反擊姿態
            case 5220012: // 反擊
                eff.cooldown = eff.x;
                eff.statups.put(MapleBuffStat.DamR, (int) eff.indieDamR);
                break;
            case 5121009: // 最終極速
                eff.statups.put(MapleBuffStat.SPEED_INFUSION, eff.x);
                break;
            case 5211006: // 指定攻擊
            case 5220011: // 精準砲擊
                eff.duration = 2100000000;
                eff.statups.put(MapleBuffStat.HOMING_BEACON, eff.x);
                break;
            case 5221009: // 心靈控制
                eff.monsterStatus.put(MonsterStatus.HYPNOTIZE, 1);
                break;
            case 5221018: // 海盜風采
                eff.statups.put(MapleBuffStat.TerR, eff.x);
                eff.statups.put(MapleBuffStat.AsrR, eff.y);//or x?
                eff.statups.put(MapleBuffStat.DamR, (int) eff.damR);
                eff.statups.put(MapleBuffStat.Stance, eff.z);
                eff.statups.put(MapleBuffStat.EVA, (int) eff.eva);
                break;
            case 5201008: // 無形彈藥
                eff.statups.put(MapleBuffStat.SPIRIT_CLAW, 0);
                break;
            case 5211009: // 魔法彈丸
                eff.duration = 180000;
                eff.statups.put(MapleBuffStat.WATK, eff.y);
                break;
//            case 5311004: // 幸運木桶
//                eff.statups.put(MapleBuffStat.RepeatEffect, 0);
//                break;
            case 5321010: // 百烈精神
                eff.statups.put(MapleBuffStat.Stance, (int) eff.prop);
                break;
            case 5121000: // 楓葉祝福
            case 5221000: // 楓葉祝福
            case 5321005: // 楓葉祝福
                eff.statups.put(MapleBuffStat.MAPLE_WARRIOR, eff.x);
                break;
            default:
//                System.out.println("[海盜]未處理技能: " + skill);
                break;
        }
    }
}
