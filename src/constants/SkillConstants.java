package constants;

import constants.SkillType;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MapleWeaponType;
import client.status.MonsterStatus;
import static constants.GameConstants.isAdventurer;
import static constants.GameConstants.isEvan;
import static constants.GameConstants.isKOC;
import static constants.GameConstants.isResist;
import handling.channel.handler.AttackInfo;
import server.MapleStatEffect;

/**
 *
 * @author Windyboy
 */
public class SkillConstants {

    public static int getSkillForStat(MonsterStatus stat) {
        switch (stat) {
            case ACC:
                return 3221006;
            case SPEED:
                return 3121007;
        }
        return 0;
    }

    public static boolean is金字塔技能(final int skill) {
        return isBeginner(skill / 10000) && (skill == 1020 || skill % 10000 == 1020);
    }

    public static int getMPEaterForJob(final int job) {
        switch (job) {
            case 210:
            case 211:
            case 212:
                return 2100000;
            case 220:
            case 221:
            case 222:
                return 2200000;
            case 230:
            case 231:
            case 232:
                return 2300000;
        }
        return 2100000; // Default, in case GM
    }

    public static boolean is武陵道場技能(final int skill) {
        switch (skill) {
            case 1009:
            case 1010:
            case 1011:
                return true;
        }
        return isBeginner(skill / 10000) && (skill % 10000 == 1009 || skill % 10000 == 1010 || skill % 10000 == 1011);
    }

    public static boolean isBeginner(final int job) {
        return getJobGrade(job) == 0;
    }

    public static int getJobGrade(int jobz) {
        int job = (jobz % 1000);
        if (job / 10 == 0) {
            return 0; //beginner
        } else if (job / 10 % 10 == 0) {
            return 1;
        } else {
            return job % 10 + 2;
        }
    }

    public final boolean isRecovery(int skill) {
        switch (skill) {
            case 1001:
                return true;
        }
        return isBeginner(skill / 10000) && skill % 10000 == 1001;
    }

    public static boolean isRecoveryIncSkill(final int id) {
        switch (id) {
            case 1110000:
            case 2000000:
            case 1210000:
            case 11110000:
            case 4100002:
            case 4200001:
                return true;
        }
        return false;
    }

    public static boolean isLinkedAranSkill(final int id) {
        return getLinkedAranSkill(id) != id;
    }

    public static int getLinkedAranSkill(final int id) {
        switch (id) {
            case 21110007:
            case 21110008:
                return 21110002;
            case 21120009:
            case 21120010:
                return 21120002;
            case 4321001:
                return 4321000;
            case 33101006:
            case 33101007:
                return 33101005;
            case 33101008:
                return 33101004;
            case 35101009:
            case 35101010:
                return 35100008;
            case 35111009:
            case 35111010:
                return 35111001;
        }
        return id;
    }

    public static int getBOF_ForJob(final int job) {
        if (isAdventurer(job)) {
            return 12;
        } else if (isKOC(job)) {
            return 10000012;
        } else if (isResist(job)) {
            return 30000012;
        } else if (isEvan(job)) {
            return 20010012;
        }
        return 20000012;
    }

    public static boolean isElementAmp_Skill(final int skill) {
        switch (skill) {
            case 2110001:
            case 2210001:
            case 12110001:
            case 22150000:
                return true;
        }
        return false;
    }

    public static short getSummonAttackDelay(final int id) {
        switch (id) {
            case 15001004: // Lightning
            case 14001005: // Darkness
            case 13001004: // Storm
            case 12001004: // Flame
            case 11001004: // Soul
            case 3221005: // Freezer
            case 3211005: // Golden Eagle
            case 3121006: // Phoenix
            case 3111005: // Silver Hawk
            case 2321003: // Bahamut
            case 2311006: // Summon Dragon
            case 2221005: // Infrit
            case 2121005: // Elquines
                return 3030;
            case 5211001: // Octopus
            case 5211002: // Gaviota
            case 5211014: // Support Octopus
            case 5711001:
                return 1530;
            case 3211002: // Puppet
            case 3111002: // Puppet
            case 1321007: // Beholder
                return 0;
        }
        return 0;
    }

    public static short getAttackDelay(MapleCharacter chr, int id) {
        short delay = 0;
        Item weapon_item = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11);

        if (id != 0) {
            if (getAttackDelayBySkill(id) != 0) {
                delay = getAttackDelayBySkill(id);
                if (weapon_item != null) {
                    switch (id) {
                        case SkillType.劍士.魔天一擊:
                        case SkillType.聖魂劍士1.魔天一擊:
                            switch (weapon_item.getItemId()) {
                                case 1332010:
                                case 1322006:
                                case 1322004:
                                    delay = 660;
                                    break;
                                default:
                                    break;
                            }
                            break;
                    }
                }
            }
        } else {// 普通攻擊
            delay = getAttackDelayByWeapon(chr);
        }

        delay = handleAttackDelayBuff(chr, delay);

        if (chr.getBuffedValue(MapleBuffStat.BOOSTER) != null || (chr.getBuffedValue(MapleBuffStat.SPEED_INFUSION) != null)) {
            if (id != 0) {
                if (getAttackDelayBySkillAfterBuff(id) != 0) {
                    delay = getAttackDelayBySkillAfterBuff(id);
                }
            } else {
                // 普通攻擊
                if (getAttackDelayAfterBuff(chr) != 0) {
                    delay = getAttackDelayAfterBuff(chr);
                }
            }
        }
        // 強化連擊
        if (id == 21101003 || id == 5110001) {
            delay = 0;
        }

        return delay;
    }

    public static short getAttackDelayBySkill(int id) {
        switch (id) {
            case SkillType.遊俠.致命箭:
            case SkillType.槍手.炸彈投擲:
                return 60;
            case SkillType.箭神.暴風神射:
            case SkillType.暗影神偷.致命暗殺:
            case SkillType.槍神.瞬迅雷:
            case SkillType.槍手.脫離戰場:
            case SkillType.破風使者3.暴風神射:
                return 120;
            case SkillType.破風使者2.暴風射擊:
                return 360;
            case SkillType.隱忍.躍空斬:
                return 420;
            case SkillType.海盜.雙子星攻擊:
                return 450;
            case SkillType.海盜.衝擊拳:
            case SkillType.閃雷悍將1.衝擊拳:
                return 510;
            case SkillType.閃雷悍將3.損人利己:
            case SkillType.英雄.究極突刺:
            case SkillType.聖騎士.究極突刺:
            case SkillType.黑騎士.究極突刺:
            case SkillType.格鬥家.能量暴擊:
            case SkillType.閃雷悍將2.能量暴擊:
            case SkillType.聖魂劍士1.魔天一擊:
            case SkillType.聖魂劍士1.劍氣縱橫:
                return 570;
            case SkillType.烈焰巫師2.火焰箭:
            case SkillType.破風使者3.箭雨:
            case SkillType.暗殺者.楓幣攻擊:
            case SkillType.盜賊.劈空斬:
            case SkillType.暗夜行者3.風魔手裏劍:
            case SkillType.閃雷悍將2.狂暴衝擊:
            case SkillType.聖魂劍士3.雙連斬:
            case SkillType.僧侶.群體治癒:
                return 600;
            case SkillType.劍士.劍氣蹤橫:
            case SkillType.夜使者.三飛閃:
            case SkillType.暗夜行者3.三飛閃:
            case SkillType.海盜.旋風斬:
            case SkillType.閃雷悍將1.旋風斬:
            case SkillType.暗夜行者1.雙飛斬:
            case SkillType.盜賊.雙飛斬:
            case SkillType.刺客.吸血術:
            case SkillType.俠盜.妙手術:
            case SkillType.神偷.分身術:
                return 660;
            case SkillType.暗殺者.風魔手裏劍:
            case SkillType.中忍.狂刃風暴:
            case SkillType.槍神.海盜加農炮:
            case SkillType.槍手.散射:
                return 690;
            case SkillType.劍士.魔天一擊:
            case SkillType.騎士.屬性攻擊:
            case SkillType.龍騎士.龍之獻祭:
            case SkillType.盜賊.詛咒術:
                return 720;
            case SkillType.聖騎士.騎士衝擊波:
            case SkillType.龍騎士.無雙矛:
            case SkillType.龍騎士.無雙槍:
            case SkillType.弓箭手.二連箭:
            case SkillType.弓箭手.斷魂箭:
            case SkillType.獵人.炸彈箭:
            case SkillType.獵人.強弓:
            case SkillType.弩弓手.強弩:
            case SkillType.遊俠.四連箭:
            case SkillType.遊俠.烈火箭:
            case SkillType.遊俠.箭雨:
            case SkillType.狙擊手.四連箭:
            case SkillType.神槍手.三連發:
            case SkillType.神射手.必殺狙擊:
            case SkillType.箭神.龍魂之箭:
            case SkillType.槍手.偽裝射擊:
            case SkillType.破風使者1.二連箭:
            case SkillType.破風使者3.四連箭:
            case SkillType.英雄.無雙劍舞:
            case SkillType.烈焰巫師1.魔力爪:
            case SkillType.烈焰巫師3.火風暴:
            case SkillType.閃雷悍將3.閃光擊:
                return 750;
            case SkillType.冰雷大魔導士.閃電連擊:
            case SkillType.弩弓手.穿透之箭:
            case SkillType.狙擊手.寒冰箭:
            case SkillType.狙擊手.升龍弩:
            case SkillType.俠盜.迴旋斬:
            case SkillType.隱忍.血雨暴風狂斬:
            case SkillType.下忍.三重斬:
            case SkillType.槍神.精準砲擊:
                return 780;
            case SkillType.冒險之技.嫩寶丟擲術:
            case SkillType.貴族.嫩寶丟擲術:
            case SkillType.傳說.嫩寶丟擲術:
            case SkillType.法師.魔力爪:
            case SkillType.法師.魔靈彈:
            case SkillType.火毒巫師.毒霧:
            case SkillType.火毒巫師.火焰箭:
            case SkillType.冰雷巫師.冰錐術:
            case SkillType.冰雷巫師.電閃雷鳴:
            case SkillType.冰雷大魔導士.寒冰地獄:
            case SkillType.火毒大魔導士.炎靈地獄:
            case SkillType.火毒大魔導士.劇毒麻痺:
            case SkillType.狙擊手.致命箭:
            case SkillType.閃雷悍將3.鯨噬:
            case SkillType.僧侶.神聖之箭:
            case SkillType.主教.天使之箭:
            case SkillType.拳霸.閃連殺:
            case SkillType.龍魔島1.魔法飛彈:
            case SkillType.龍魔島7.火焰殺:
                return 810;
            case SkillType.龍魔島4.雪嵐陣:
            case SkillType.神偷.楓幣炸彈:
                return 840;
            case SkillType.冰雷大魔導士.核爆術:
            case SkillType.火毒大魔導士.核爆術:
            case SkillType.主教.核爆術:
            case SkillType.槍神.海盜魚雷:
            case SkillType.聖魂劍士2.靈魂之刃:
                return 870;
            case SkillType.龍騎士.槍連擊:
            case SkillType.龍騎士.矛連擊:
            case SkillType.冰雷魔導士.冰雷合擊:
            case SkillType.火毒魔導士.火毒合擊:
            case SkillType.打手.迴旋肘擊:
            case SkillType.神槍手.指定攻擊:
            case SkillType.破風使者3.疾風光速神弩:
            case SkillType.神射手.光速神弩:
                return 900;
            case SkillType.影武者.絕殺刃:
            case SkillType.拳霸.鬥神降世:
            case SkillType.龍魔島2.火圈:
                return 930;
            case SkillType.破風使者3.疾風掃射:
                return 960;
            case SkillType.狂狼勇士1.雙重攻擊:
                return 990;

            case SkillType.暗夜行者2.吸血:
            case SkillType.夜使者.挑釁:
            case SkillType.暗影神偷.挑釁:
            case SkillType.閃雷悍將2.蓄能激發:
                return 1020;
            case SkillType.冰雷魔導士.冰風暴:
            case SkillType.祭司.聖光:
            case SkillType.神槍手.火焰噴射:
            case SkillType.神槍手.寒霜噴射:
            case SkillType.神偷.落葉斬:
            case SkillType.烈焰巫師2.火柱:
                return 1050;
            case SkillType.狂狼勇士3.雙重攻擊:
                return 1131;
            case SkillType.拳霸.元氣彈:
            case SkillType.打手.昇龍拳:
            case SkillType.GM.終極龍咆哮:
                return 1140;
            case SkillType.格鬥家.損人利己:
            case SkillType.閃雷悍將3.閃連殺:
                return 1170;
            case SkillType.上忍.二段跳:
                return 1020;
            case SkillType.龍魔島3.閃電球:
            case SkillType.聖魂劍士3.靈魂突刺:
                return 1230;
            case SkillType.烈焰巫師3.火牢術屏障:
            case SkillType.狂狼勇士3.伺機攻擊:
            case SkillType.狂狼勇士4.三重攻擊:
                return 1260;
            //case SkillType.狂狼勇士4.雙重攻擊:
            case SkillType.隱忍.貓頭鷹召喚:
                return 1260;
            case SkillType.十字軍.虎咆哮:
            case SkillType.冰雷魔導士.落雷凝聚:
            case SkillType.拳霸.閃索命:
            case SkillType.龍魔島8.鬼神詛咒:
            case SkillType.龍魔島10.烈焰爆擊:

                return 1320;
            case SkillType.狂狼勇士2.猛擲之矛:
                return 1350;
            case SkillType.中忍.分身斬:
            case SkillType.龍騎士.龍咆哮:
            case SkillType.龍魔島6.龍神閃:
            case SkillType.龍魔島8.大地震盪:

                return 1410;
            case SkillType.暗影神偷.瞬步連擊:
            case SkillType.夜使者.忍術風影:
                return 1440;
            case SkillType.龍魔島9.火輪舞:
                return 1470;
            case SkillType.十字軍.黑暗之斧:
                return 1530;
            case SkillType.火毒魔導士.致命毒霧:
            case SkillType.格鬥家.蓄能激發:
            case SkillType.狂狼勇士2.強化連擊:
            case SkillType.閃雷悍將3.衝擊波:
                return 1500;
            case SkillType.十字軍.氣絕劍:

                return 1530;
            case SkillType.狂狼勇士2.三重攻擊:
            case SkillType.打手.狂暴衝擊:
            case SkillType.龍魔島5.魔光殺:
                return 1560;
            case SkillType.十字軍.氣絕斧:
                return 1620;
            case SkillType.十字軍.黑暗之劍:
            case SkillType.火毒魔導士.末日烈焰:
            case SkillType.龍魔島9.四連殺:
                return 1650;
            case SkillType.狂狼勇士3.挑怪:
                return 1710;
            case SkillType.拳霸.魔龍降臨:
            case SkillType.格鬥家.衝擊波:
                return 1860;
            case SkillType.狂狼勇士4.終極之矛:
                return 1890;
            case SkillType.影武者.隱鎖煉地域:
                return 2190;
            case SkillType.狂狼勇士2.突刺之矛:
                return 2250;
            case SkillType.影武者.穢土轉生:
                return 2370;
            case SkillType.主教.天怒:
                return 2700;
            case SkillType.狂狼勇士4.極冰暴風:
                return 2820;
            case SkillType.聖騎士.鬼神之擊:
                return 2910;
            case SkillType.槍神.海鷗特戰隊:
            case SkillType.拳霸.閃爆破:
                return 2940;
            case SkillType.冒險之技.地火天爆:
            case SkillType.貴族.地火天爆:
            case SkillType.傳說.地火天爆:
            case SkillType.龍魔島0.地火天爆:
            case SkillType.烈焰巫師3.火流星:
            case SkillType.龍魔島10.龍神之怒:
                return 3060;
            case SkillType.冰雷大魔導士.暴風雪:
            case SkillType.火毒大魔導士.火流星:
                return 3480;
            case SkillType.冒險之技.竹竿天擊:
            case SkillType.貴族.竹竿天擊:
            case SkillType.傳說.竹竿天擊:
            case SkillType.龍魔島0.竹竿千擊:
                return 3900;
        }
        return 0; // 預設值
    }

    private static short getAttackDelayAfterBuff(MapleCharacter chr) {
        // TODO : 普通攻擊因為處理BUFF後Delay值異常重新調整
        short AtkDelay = 0;
        Item weapon_item = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11);
        MapleWeaponType weapon = weapon_item == null ? MapleWeaponType.沒有武器 : GameConstants.getWeaponType(weapon_item.getItemId());
        String name = weapon.name();
        switch (name) {
            case "指虎":
            case "火槍":
            case "弩":
            case "弓":
                AtkDelay = 570;
                break;
            default:
                break;
        }
        if (weapon_item != null && weapon_item.getItemId() == 1492037) {
            AtkDelay = 540;
        }
        return AtkDelay;
    }

    public static short getAttackDelayBySkillAfterBuff(int id) {
        // BUFF後Delay值異常的技能重新調整
        switch (id) {
            case SkillType.箭神.暴風神射:
            case SkillType.槍神.瞬迅雷:
            case SkillType.破風使者3.暴風神射:
                return 120;
            case SkillType.海盜.雙子星攻擊:
                return 390;
            case SkillType.英雄.究極突刺:
            case SkillType.聖騎士.究極突刺:
            case SkillType.黑騎士.究極突刺:
            case SkillType.格鬥家.損人利己:
                return 450;
            case SkillType.遊俠.四連箭:
            case SkillType.槍神.海盜加農炮:
            case SkillType.夜使者.三飛閃:
            case SkillType.暗夜行者3.三飛閃:
            case SkillType.暗夜行者1.雙飛斬:
            case SkillType.盜賊.雙飛斬:
                return 600;
            case SkillType.狙擊手.四連箭:
            case SkillType.狙擊手.升龍弩:
            case SkillType.英雄.無雙劍舞:
                return 630;
            case SkillType.弓箭手.二連箭:
            case SkillType.海盜.旋風斬:
                return 660;
            case SkillType.神槍手.指定攻擊:
                return 780;
            case SkillType.狂狼勇士2.猛擲之矛:
                return 990;
            case SkillType.冰雷魔導士.落雷凝聚:
                return 1140;
            case SkillType.狂狼勇士2.強化連擊:
            case SkillType.格鬥家.蓄能激發:
                return 1500;
        }
        return 0; // 預設值
    }

    private static short getAttackDelayByWeapon(MapleCharacter chr) {
        // 取各類武器中最快的測試取最低值
        short AtkDelay;
        Item weapon_item = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11);
        MapleWeaponType weapon = weapon_item == null ? MapleWeaponType.沒有武器 : GameConstants.getWeaponType(weapon_item.getItemId());
        String name = weapon.name();
        switch (name) {
            case "拳套":
                AtkDelay = 540;
                break;
            case "雙手劍":
            case "單手劍":
            case "單手棍":
            case "長杖":
            case "短劍":
                AtkDelay = 570;
                break;
            case "單手斧":
            case "短杖":
                AtkDelay = 630;
                break;
            case "矛":
            case "火槍":
            case "弓":
            case "雙手棍":
            case "指虎":
            case "槍":
            case "弩":
            case "沒有武器":
                AtkDelay = 660;
                break;
            case "雙手斧":
                AtkDelay = 720;
                break;
            default:
                AtkDelay = 690;
                break;
        }
        if (weapon_item != null) {
            switch (weapon_item.getItemId()) {
                case 1322013:
                    AtkDelay = 540;
                    break;
                case 1492037:
                    AtkDelay = 630;
                    break;
                default:
                    break;
            }
        }
        return AtkDelay;
    }

    public static short handleAttackDelayBuff(MapleCharacter chr, short AtkDelay) {
        boolean booster = false;
        // 由於Timer異常，Buff可能判斷錯誤
        if (chr.getBuffedValue(MapleBuffStat.BODY_PRESSURE) != null) {
            AtkDelay /= 6;// 使用這Buff之後 tickcount - lastAttackTickCount 可以為0...
        }
        // 攻擊加速
        if (chr.getBuffedValue(MapleBuffStat.BOOSTER) != null) {
            if (chr.isWarrior()) {// 720 -> 600 (1001004)
                AtkDelay /= 1.21;
            } else if (chr.isMage()) {// 810 -> 720 (2001004)
                AtkDelay /= 1.14;
            } else if (chr.isBowman()) {// 810 -> 720 (3101005)
                AtkDelay /= 1.14;
            } else if (chr.isThief()) {//720 -> 600 (4001344)
                AtkDelay /= 1.21;
            } else if (chr.isPirate()) {// 570 - > 510 (5001001)
                AtkDelay /= 1.15;
            } else {
                AtkDelay /= 1.21;
            }
            booster = true;
        }
        // 最終極速
        if (chr.getBuffedValue(MapleBuffStat.SPEED_INFUSION) != null) {
            if (chr.isWarrior()) {// 720 -> 600 (1001004)
                if (!booster) {
                    AtkDelay /= 1.21;
                }
            } else if (chr.isMage()) {// 900 -> 810 (2111006)
                if (!booster) {
                    AtkDelay /= 1.14;
                }
            } else if (chr.isBowman()) {// 810 -> 720 (3101005)
                if (!booster) {
                    AtkDelay /= 1.14;
                }
            } else if (chr.isThief()) {//720 -> 600 (4001344)
                if (!booster) {
                    AtkDelay /= 1.21;
                }
            } else if (chr.isPirate()) {// 570 - > 510 (5001001)
                AtkDelay /= 1.15;
            } else {
                AtkDelay /= 1.21;
            }
        }
        return AtkDelay;
    }

    public static boolean isApplicableSkill(int skil) {
        return skil < 40000000 && (skil % 10000 < 8000 || skil % 10000 > 8003); //no additional/decent skills
    }

    public static boolean isApplicableSkill_(int skil) { //not applicable to saving but is more of temporary
        return skil >= 90000000 || (skil % 10000 >= 8000 && skil % 10000 <= 8003);
    }

    public static int getMasterySkill(final int job) {
        if (job >= 1410 && job <= 1412) {
            return 14100000;
        } else if (job >= 410 && job <= 412) {
            return 4100000;
        } else if (job >= 520 && job <= 522) {
            return 5200000;
        }
        return 0;
    }

    public static int getMaxDamage_T(final MapleCharacter chr, int skillid) {
        int max = 200000;
        int level = chr.getLevel();
        if (level < 9) {
            max = 150;
        } else if (level < 10) {
            max = 250;
        } else if (level <= 15) {
            max = 600;
        } else if (level <= 20) {
            max = 1000;
        } else if (level <= 25) {
            max = 1500;
        } else if (level <= 30) {
            max = 2500;
        } else if (level <= 40) {
            max = 4000;
        } else if (level <= 50) {
            max = 7000;
        } else if (level <= 60) {
            max = 8000;
        }
        switch (skillid) {
            case SkillType.劍士.魔天一擊:
            case SkillType.聖魂劍士1.魔天一擊:
                max *= 1.2;
                break;
            default:
                break;
        }
        return max;
    }

    public static int getMaxDamage(final MapleCharacter chr, int skillid) {
        int max = 200000;
        int level = chr.getLevel();
        if (level < 4) {
            max = 80;
        } else if (level < 9) {
            max = 150;
        } else if (level < 10) {
            max = 250;
        } else if (level <= 15) {
            max = 600;
        } else if (level <= 20) {
            max = 1000;
        } else if (level <= 25) {
            max = 1500;
        } else if (level <= 30) {
            max = 2200;
        } else if (level <= 35) {
            max = 3200;
        } else if (level <= 40) {
            max = 4000;
        } else if (level <= 50) {
            max = 7000;
        } else if (level <= 60) {
            max = 8000;
        } else if (level <= 70) {
            max = 12000;
        } else if (level <= 80) {
            max = 15000;
        } else if (level <= 90) {
            max = 18000;
        } else if (level <= 100) {
            max = 25000;
        }
        if (chr.isCygnus() && level >= 70) {
            max += 1000;
        }
        switch (skillid) {
            case SkillType.劍士.魔天一擊:
            case SkillType.聖魂劍士1.魔天一擊:
                max *= 1.2;
                break;
            case SkillType.閃雷悍將3.閃連殺:
                if (chr.getBuffedValue(MapleBuffStat.WK_CHARGE) != null && chr.isPirate()) {
                    max *= 2;
                }
                max *= 3;
                break;
            default:
                break;
        }

        return max;
    }

    public static boolean isElseSkill(int id) {
        switch (id) {
            case 10001009:
            case 20001009:
            case 1009:   // 武陵道場技能
            case 1020:   // 金字塔技能
            case 10001020:
            case 20001020:
            case 3221001:// 光速神弩
            case 4211006:// 楓幣炸彈
                return true;
        }
        return false;
    }

    public static boolean is嫩寶投擲術(int skill) {
        switch (skill) {
            case 1000://新手 蝸牛殼
            case 10001000://新手 蝸牛殼
            case 20001000://狂郎  蝸牛殼
            case 20011000:
                return true;
        }
        return false;
    }

    public static double getAttackRange(MapleCharacter chr, MapleStatEffect def, AttackInfo attack) {
        int rangeInc = chr.getStat().defRange;// 處理遠程職業
        double base = 450.0;// 基礎
        double defRange = ((base + rangeInc) * (base + rangeInc));// 基礎範圍
        if (def != null) {
            // 計算範圍((maxX * maxX) + (maxY * maxY)) + (技能範圍 * 技能範圍))
            defRange += def.getMaxDistanceSq() + (def.getRange() * def.getRange());
            if (getAttackRangeBySkill(attack) != 0) {// 直接指定技能範圍
                defRange = getAttackRangeBySkill(attack);
            }
        } else {// 普通攻擊
            defRange = getAttackRangeByWeapon(chr);// 從武器獲取範圍
        }
        return defRange;
    }

    private static double getAttackRangeBySkill(AttackInfo attack) {
        double defRange = 0;
        switch (attack.skill) {
            case 21120006: // 極冰暴風
                defRange = 800000.0;
                break;
            case 2121007: // 火流星
            case 2221007: // 暴風雪
            case 2321008: // 天怒
                defRange = 750000.0;
                break;
            case 2221006: // 閃電連擊
            case 3101005: // 炸彈箭
            case 21101003:// 強化連擊
                defRange = 600000.0;
                break;
            case 15111006:// 閃光擊
                defRange = 500000.0;
                break;
            case 12111006:// 火風暴
            case 2111003: // 致命毒
                defRange = 400000.0;
                break;
            case 5221004:// 迅雷
            case 4001344: // 雙飛斬
            case 2101004: // 火焰箭 
            case 1121008: // 無雙劍舞
                defRange = 350000.0;
                break;
            case 2211002: // 冰風暴
                defRange = 300000.0;
                break;
            case 5110001: // 蓄能激發
            case 2311004: // 聖光
            case 2211003: // 落雷凝聚
            case 2001005: // 魔力爪
                defRange = 250000.0;
                break;
            case 2321007: // 天使之箭
                defRange = 200000.0;
                break;
            default:
                break;
        }
        if (is嫩寶投擲術(attack.skill)) {
            defRange = 180000.0;

        }
        return defRange;
    }

    private static double getAttackRangeByWeapon(MapleCharacter chr) {
        Item weapon_item = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11);
        MapleWeaponType weapon = weapon_item == null ? MapleWeaponType.沒有武器 : GameConstants.getWeaponType(weapon_item.getItemId());
        switch (weapon) {
            case 槍:       // 矛
                return 200000;
            case 拳套:     // 拳套
                return 250000;
            case 火槍:     // 火槍
            case 弩:       // 弩
            case 弓:       // 弓
                return 220000;
            case 矛:
                return 180000;
            default:
                return 100000;
        }
    }

    public static boolean isMoveSkill(int id) {
//        if (is二段跳(id)) {
//            return true;
//        }
//        if (is究極突刺(id)) {
//            return true;
//        }
        switch (id) {
            case SkillType.槍手.脫離戰場:
            case SkillType.火毒巫師.瞬間移動:
            case SkillType.冰雷巫師.瞬間移動:
            case SkillType.僧侶.瞬間移動:
            case SkillType.烈焰巫師2.瞬間移動:
            case SkillType.管理者.瞬間移動:
            case SkillType.聖魂劍士2.靈魂迅移:
//            case SkillType.神偷.落葉斬:
//                case 4331000:
//                case 4311003:
                // case SkillType.狂狼勇士1.戰鬥衝刺:
                return true;
        }

        return false;
    }

    public static boolean is二段跳(int id) {
        boolean ret = false;
        switch (id) {
            case SkillType.暗殺者.二段跳:
            case SkillType.暗夜行者2.二段跳:
            case 4321003:
                ret = true;
                break;
            default:
                break;
        }
        return ret;
    }

    public static boolean isFastAttack(int id) {
        boolean ret = false;
        switch (id) {
            case SkillType.火毒大魔導士.火流星:
            case SkillType.冰雷大魔導士.暴風雪:
            case SkillType.僧侶.群體治癒:
            case SkillType.主教.天怒:
            case SkillType.箭神.暴風神射:
            case SkillType.槍神.瞬迅雷:
            case SkillType.破風使者3.暴風神射:
            case SkillType.狂狼勇士2.強化連擊:
                ret = true;
                break;
            default:
                break;
        }
        return ret;
    }

    public static boolean is終極攻擊(int id) {
        boolean skill = false;
        switch (id) {
            case SkillType.狂戰士.終極之劍:
            case SkillType.狂戰士.終極之斧:
            case SkillType.見習騎士.終極之劍:
            case SkillType.見習騎士.終極之棍:
            case SkillType.槍騎兵.終極之槍:
            case SkillType.槍騎兵.終極之矛:
            case SkillType.獵人.終極之弓:
            case SkillType.弩弓手.終極之弩:
            case SkillType.聖魂劍士2.終極攻擊:
            case SkillType.破風使者2.終極攻擊:
            case SkillType.狂狼勇士3.三重攻擊:
            case SkillType.狂狼勇士4.終極攻擊:
            case SkillType.狂狼勇士4.終極之矛:
            case SkillType.狂狼勇士4.雙重攻擊:
            case SkillType.狂狼勇士4.三重攻擊:
                skill = true;
                break;
            default:
                break;
        }
        return skill;
    }

    public static boolean is瞬間移動(int id) {
        boolean ret = false;
        switch (id) {
            case SkillType.火毒巫師.瞬間移動:
            case SkillType.冰雷巫師.瞬間移動:
            case SkillType.僧侶.瞬間移動:
            case SkillType.管理者.瞬間移動:
            case SkillType.烈焰巫師2.瞬間移動:
            case SkillType.龍魔島2.瞬間移動:
                ret = true;
                break;
            default:
                break;
        }
        return ret;
    }

    public static int getSkillBook(final int job) {
        if (job >= 2210 && job <= 2218) {
            return job - 2209;
        }
        return 0;
    }

    public static int getSkillBookForSkill(final int skillid) {
        return getSkillBook(skillid / 10000);
    }

    public static boolean is破風使者技能(int skillid) {
        switch (skillid) {
            case 0:
            case 13000000:
            case 13000001:
            case 13001002:
            case 13001003:
            case 13001004:
            case 13100000:
            case 13101001:
            case 13101002:
            case 13101003:
            case 13101005:
            case 13101006:
            case 13111000:
            case 13111001:
            case 13111002:
            case 13111004:
            case 13111005:
            case 13111006:
            case 13111007:
                return true;
            default:
                break;
        }
        return false;
    }

    public static boolean isMagicAttack(int id) {
        boolean ret = false;
        switch (id) {
            case 1000:
            case 2001004:
            case 2001005:
            case 2101004:
            case 2101005:
            case 2111002:
            case 2111003:
            case 2111006:
            case 2121001:
            case 2121003:
            case 2121006:
            case 2121007:
            case 2201004:
            case 2201005:
            case 2211002:
            case 2211003:
            case 2211006:
            case 2221001:
            case 2221003:
            case 2221006:
            case 2221007:
            case 2301002:
            case 2301005:
            case 2311004:
            case 2321001:
            case 2321007:
            case 2321008:
            case 10001000:
            case 12101002:
            case 12001003:
            case 12101006:
            case 12111003:
            case 12111005:
            case 12111006:
            case 20001000:
            case 20011000:
            case 22001001:
            case 22101000:
            case 22111000:
            case 22121000:
            case 22131000:
            case 22141001:
            case 22151001:
            case 22151002:
            case 22161001:
            case 22161002:
            case 22171002:
            case 22171003:
            case 22181001:
            case 22181002:

                ret = true;
            default:
                break;
        }
        return ret;
    }

    public static boolean isRangedAttack(int id) {
        boolean ret = false;
        switch (id) {
            case 0:
            case 3001004:
            case 3001005:
            case 3100001:
            case 3101005:
            case 3110001:
            case 3111003:
            case 3111004:
            case 3111006:
            case 3121003:
            case 3121004:
            case 3200001:
            case 3201005:
            case 3210001:
            case 3211003:
            case 3211004:
            case 3211006:
            case 3221001:
            case 3221003:
            case 3221007:
            case 4001344:
            case 4101005:
            case 4111004:
            case 4111005:
            case 4121003:
            case 4121007:
            case 4221003:
            case 5001003:
            case 5121002:
            case 5201001:
            case 5201006:
            case 5210000:
            case 5211004:
            case 5211005:
            case 5211006:
            case 5220011:
            case 5221004:
            case 5221007:
            case 5221008:
            case 5221009:
            case 11101004:
            case 13001003:
            case 13101002:
            case 13101005:
            case 13111000:
            case 13111001:
            case 13111002:
            case 13111006:
            case 13111007:
            case 14001004:
            case 14101006:
            case 14111002:
            case 14111005:
            case 15111006:
            case 15111007:
            case 21100004:
            case 21110004:
            case 21120006:
                ret = true;
                break;
            default:
                break;
        }
        return ret;
    }

    public static boolean isCloseRangedAttack(int id) {
        boolean ret = false;
        switch (id) {
            case 0:
            case 1009:
            case 1020:
            case 1001004:
            case 1001005:
            case 1100002:
            case 1100003:
            case 1111003:
            case 1111004:
            case 1111005:
            case 1111006:
            case 1111008:
            case 1121006:
            case 1121008:
            case 1200002:
            case 1200003:
            case 1211002:
            case 1221007:
            case 1221009:
            case 1221011:
            case 1300002:
            case 1300003:
            case 1311001:
            case 1311002:
            case 1311003:
            case 1311004:
            case 1311005:
            case 1311006:
            case 1321003:
            case 3101003:
            case 3201003:
            case 4001002:
            case 4001334:
            case 4121008:
            case 4201004:
            case 4201005:
            case 4211002:
            case 4211004:
            case 4211006:
            case 4221001:
            case 4221007:
            // 影武者
            case 4301001:
            case 4311002:
            case 4311003:
            case 4321001:
            case 4321002:
            case 4331000:
            case 4341002:
            case 4331003:
            case 4331004:
            case 4331005:
            case 4341004:
            case 4341005:
            case 5001001:
            case 5001002:
            case 5101002:
            case 5101003:
            case 5101004:
            case 5110001:
            case 5111002:
            case 5111004:
            case 5111006:
            case 5121001:
            case 5121004:
            case 5121005:
            case 5121007:
            case 5201002:
            case 5201004:
            case 5221003:
            case 9001006:
            case 10001009:
            case 10001020:
            case 11001002:
            case 11001003:
            case 11101002:
            case 11111003:
            case 11111002:
            case 11111004:
            case 11111006:
            case 14001002:
            case 14111006:
            case 15001001:
            case 15001002:
            case 15100004:
            case 15101003:
            case 15101004:
            case 15101005:
            case 15111001:
            case 15111003:
            case 15111004:
            case 20000014:
            case 20000015:
            case 20000016:
            case 20001009:
            case 20001020:
            case 20011020:
            case 21000002:
            case 21100001:
            case 21100002:
            case 21101003:
            case 21110003:
            case 21110006:
            case 21110007:
            case 21110008:
            case 21120005:
            case 21120009:
            case 21120010:
                ret = true;
                break;
            default:
                break;
        }

        return ret;
    }

    public static boolean isSpecialMove(int id) {
        boolean ret = false;
        switch (id) {
            case 1001:
            case 1002:
            case 1004:
            case 1005:
            case 1010:
            case 1011:
            case 1013:
            case 1014:
            case 1015:
            case 1016:
            case 1017:
            case 1026:
            case 1029:
            case 1030:
            case 1069:

            case 1001003:
            case 1101005:
            case 1101004:
            case 1101006:
            case 1101007:
            case 1111002:
            case 1111007:
            case 1121000:
            case 1121001:
            case 1121002:
            case 1121010:
            case 1121011:
            case 1201004:
            case 1201005:
            case 1201006:
            case 1201007:
            case 1211003:
            case 1211004:
            case 1211005:
            case 1211006:
            case 1211007:
            case 1211008:
            case 1211009:
            case 1221000:
            case 1221001:
            case 1221002:
            case 1221003:
            case 1301004:
            case 1301005:
            case 1301006:
            case 1301007:
            case 1311007:
            case 1311008:
            case 1321000:
            case 1321001:
            case 1321002:
            case 1321007:
            case 1321010:

            case 2001002:
            case 2001003:
            case 2101001:
            case 2101002:
            case 2101003:
            case 2111004:
            case 2111005:
            case 2121000:
            case 2121002:
            case 2121004:
            case 2121005:
            case 2121008:
            case 2201001:
            case 2201003:
            case 2201002:
            case 2211004:
            case 2211005:
            case 2221000:
            case 2221002:
            case 2221004:
            case 2221005:
            case 2221008:
            case 2301001:
            case 2301002:
            case 2301003:
            case 2301004:
            case 2311001:
            case 2311002:
            case 2311003:
            case 2311005:
            case 2311006:
            case 2321000:
            case 2321003:
            case 2321002:
            case 2321004:
            case 2321005:
            case 2321006:
            case 2321009:

            case 3001003:
            case 3101002:
            case 3101003:
            case 3101004:
            case 3111002:
            case 3111005:
            case 3121000:
            case 3121002:
            case 3121006:
            case 3121007:
            case 3121008:
            case 3121009:
            case 3201002:
            case 3201004:
            case 3211002:
            case 3211005:
            case 3221000:
            case 3221002:
            case 3221005:
            case 3221006:
            case 3221008:

            case 4001003:
            case 4101003:
            case 4101004:
            case 4111002:
            case 4111003:
            case 4111001:
            case 4111006:
            case 4121000:
            case 4121004:
            case 4121006:
            case 4121009:
            case 4201002:
            case 4201003:
            case 4211001:
            case 4211003:
            case 4211005:
            case 4221000:
            case 4221004:
            case 4221006:
            case 4221008:
            case 4301002:
            case 4321000:
            case 4321003:
            case 4331002:
            case 4341000:
            case 4341003:
            case 4341005:
            case 4341006:
            case 4341007:
            case 4341008:
            case 5001005:
            case 5101005:
            case 5101006:
            case 5101007:
            case 5111005:
            case 5121000:
            case 5121003:
            case 5121008:
            case 5121009:
            case 5121010:
            case 5201003:
            case 5201005:
            case 5211001:
            case 5211002:
            case 5221000:
            case 5211014:
            case 5711001:
            case 5221006:
            case 5221010:

            case 9001000:
            case 9001001:
            case 9001002:
            case 9001003:
            case 9001004:
            case 9001005:
            case 9001007:
            case 9001008:

            case 10001001:
            case 10001002:
            case 10001004:
            case 10001010:
            case 10001011:

            case 11001001:
            case 11001004:
            case 11101001:
            case 11101002:
            case 11101003:
            case 11101005:
            case 11111001:
            case 11111007:

            case 12001001:
            case 12001002:
            case 12001004:
            case 12101000:
            case 12101001:
            case 12101003:
            case 12101004:
            case 12101005:
            case 12111002:
            case 12111004:

            case 13001002:
            case 13001004:
            case 13101001:
            case 13101002:
            case 13101003:
            case 13101006:
            case 13111004:
            case 13111005:

            case 14001003:
            case 14001005:
            case 14101002:
            case 14101003:
            case 14101004:
            case 14111000:
            case 14111001:

            case 15001003:
            case 15001004:
            case 15101002:
            case 15101006:
            case 15111002:
            case 15111005:
            case 15111006:

            case 20001001:
            case 20001002:
            case 20001004:
            case 20001010:
            case 20001011:
            case 21001001:
            case 21001003:
            case 21100005:
            case 21101003:
            case 21111001:
            case 21111005:
            case 21120007:
            case 21121000:
            case 21121003:
            case 21121008:
            case 22101001:
            case 22121001:
            case 22131001:
            case 22141002:
            case 22141003:
            case 22151003:
            case 22161003:
            case 22171000:
                ret = true;
                break;
            default:
                break;
        }

        return ret;
    }

    public static boolean is究極突刺(int id) {
        switch (id) {
            case SkillType.英雄.究極突刺:
            case SkillType.聖騎士.究極突刺:
            case SkillType.黑騎士.究極突刺:
                return true;
            default:
                break;
        }
        return false;
    }

    public static boolean is飛毒殺(int id) {
        switch (id) {
            case SkillType.暗夜行者3.飛毒殺:
            case SkillType.夜使者.飛毒殺:
            case SkillType.暗影神偷.飛毒殺:
                return true;
            default:
                break;
        }
        return false;
    }

}
