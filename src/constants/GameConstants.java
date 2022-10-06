package constants;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleJob;
import client.PlayerStats;
import client.Skill;
import client.SkillFactory;
import client.inventory.MapleInventoryType;
import client.inventory.MapleWeaponType;
import client.status.MonsterStatus;

import java.awt.Point;
import java.util.List;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.Randomizer;
import server.ServerProperties;
import server.maps.MapleMapObjectType;
import tools.FileoutputUtil;
import tools.packet.CField;

public class GameConstants {

    public static final List<MapleMapObjectType> rangedMapobjectTypes = Collections.unmodifiableList(Arrays.asList(
            MapleMapObjectType.ITEM,
            MapleMapObjectType.MONSTER,
            MapleMapObjectType.DOOR,
            MapleMapObjectType.REACTOR,
            MapleMapObjectType.SUMMON,
            MapleMapObjectType.NPC,
            MapleMapObjectType.MIST,
            MapleMapObjectType.FAMILIAR,
            MapleMapObjectType.EXTRACTOR));
    private static final int[] exp = {
            0, 15, 34, 57, 92, 135, 372, 560, 840, 1242, 1242,
            1242, 1242, 1242, 1242, 1490, 1788, 2146, 2575, 3090, 3708,
            4450, 5340, 6408, 7690, 9228, 11074, 13289, 15947, 19136, 19136,
            19136, 19136, 19136, 19136, 22963, 27556, 33067, 39680, 47616, 51425,
            55539, 59982, 64781, 69963, 75560, 81605, 88133, 95184, 102799, 111023,
            119905, 129497, 139857, 151046, 163130, 176180, 190274, 205496, 221936, 239691,
            258866, 279575, 301941, 326096, 352184, 380359, 410788, 443651, 479143, 479143,
            479143, 479143, 479143, 479143, 512683, 548571, 586971, 628059, 672023, 719065,
            769400, 823258, 880886, 942548, 1008526, 1079123, 1154662, 1235488, 1321972, 1414510,
            1513526, 1619473, 1732836, 1854135, 1983924, 2122799, 2271395, 2430393, 2600521, 2782557,
            2977336, 3185750, 3408753, 3647366, 3902682, 4175870, 4468181, 4780954, 5115621, 5473714,
            5856874, 6266855, 6705535, 7174922, 7677167, 8214569, 8789589, 9404860, 10063200, 10063200,
            10063200, 10063200, 10063200, 10063200, 10767624, 11521358, 12327853, 13190803, 14114159, 15102150,
            16159301, 17290452, 18500784, 19795839, 21181548, 22664256, 24250754, 25948307, 27764688, 29708216,
            31787791, 34012936, 36393842, 38941411, 41667310, 44584022, 47704904, 51044247, 54617344, 58440558,
            62531397, 66908595, 71592197, 76603651, 81965907, 87703520, 93842766, 100411760, 107440583, 113887018,
            120720239, 127963453, 135641260, 143779736, 152406520, 161550911, 171243966, 181518604, 192409720, 203954303,
            216191561, 229163055, 242912838, 257487608, 272936864, 289313076, 306671861, 325072173, 344576503, 365251093,
            387166159, 410396129, 435019897, 461121091, 488788356, 518115657, 549202596, 582154752, 617084037, 654109079,
            693355624, 734956961, 779054379, 825797642, 875345501, 927866231, 983538205, 1042550497, 1105103527};
    private static final int[] closeness = {0, 1, 3, 6, 14, 31, 60, 108, 181, 287, 434, 632, 891, 1224, 1642, 2161, 2793,
            3557, 4467, 5542, 6801, 8263, 9950, 11882, 14084, 16578, 19391, 22547, 26074,
            30000};
    private static final int[] setScore = {0, 10, 100, 300, 600, 1000, 2000, 4000, 7000, 10000};
    private static final int[] cumulativeTraitExp = {0, 20, 46, 80, 124, 181, 255, 351, 476, 639, 851, 1084,
            1340, 1622, 1932, 2273, 2648, 3061, 3515, 4014, 4563, 5128,
            5710, 6309, 6926, 7562, 8217, 8892, 9587, 10303, 11040, 11788,
            12547, 13307, 14089, 14883, 15689, 16507, 17337, 18179, 19034, 19902,
            20783, 21677, 22584, 23505, 24440, 25399, 26362, 27339, 28331, 29338,
            30360, 31397, 32450, 33519, 34604, 35705, 36823, 37958, 39110, 40279,
            41466, 32671, 43894, 45135, 46395, 47674, 48972, 50289, 51626, 52967,
            54312, 55661, 57014, 58371, 59732, 61097, 62466, 63839, 65216, 66597,
            67982, 69371, 70764, 72161, 73562, 74967, 76376, 77789, 79206, 80627,
            82052, 83481, 84914, 86351, 87792, 89237, 90686, 92139, 93596, 96000};
    private static final int[] mobHpVal = {0, 15, 20, 25, 35, 50, 65, 80, 95, 110, 125, 150, 175, 200, 225, 250, 275, 300, 325, 350,
            375, 405, 435, 465, 495, 525, 580, 650, 720, 790, 900, 990, 1100, 1200, 1300, 1400, 1500, 1600, 1700, 1800,
            1900, 2000, 2100, 2200, 2300, 2400, 2520, 2640, 2760, 2880, 3000, 3200, 3400, 3600, 3800, 4000, 4300, 4600, 4900, 5200,
            5500, 5900, 6300, 6700, 7100, 7500, 8000, 8500, 9000, 9500, 10000, 11000, 12000, 13000, 14000, 15000, 17000, 19000, 21000, 23000,
            25000, 27000, 29000, 31000, 33000, 35000, 37000, 39000, 41000, 43000, 45000, 47000, 49000, 51000, 53000, 55000, 57000, 59000, 61000, 63000,
            65000, 67000, 69000, 71000, 73000, 75000, 77000, 79000, 81000, 83000, 85000, 89000, 91000, 93000, 95000, 97000, 99000, 101000, 103000,
            105000, 107000, 109000, 111000, 113000, 115000, 118000, 120000, 125000, 130000, 135000, 140000, 145000, 150000, 155000, 160000, 165000, 170000, 175000, 180000,
            185000, 190000, 195000, 200000, 205000, 210000, 215000, 220000, 225000, 230000, 235000, 240000, 250000, 260000, 270000, 280000, 290000, 300000, 310000, 320000,
            330000, 340000, 350000, 360000, 370000, 380000, 390000, 400000, 410000, 420000, 430000, 440000, 450000, 460000, 470000, 480000, 490000, 500000, 510000, 520000,
            530000, 550000, 570000, 590000, 610000, 630000, 650000, 670000, 690000, 710000, 730000, 750000, 770000, 790000, 810000, 830000, 850000, 870000, 890000, 910000};
    private static final int[] pvpExp = {0, 3000, 6000, 12000, 24000, 48000, 960000, 192000, 384000, 768000};
    private static final int[] guildexp = {0, 20000, 160000, 540000, 1280000, 2500000, 4320000, 6860000, 10240000, 14580000};
    private static final int[] mountexp = {0, 6, 25, 50, 105, 134, 196, 254, 263, 315, 367, 430, 543, 587, 679, 725, 897, 1146, 1394, 1701, 2247,
            2543, 2898, 3156, 3313, 3584, 3923, 4150, 4305, 4550};
    public static final int[] itemBlock = {4001168, 5220013, 3993003, 2340000, 2049100, 4001129, 2040037, 2040006, 2040007, 2040303, 2040403, 2040506, 2040507, 2040603, 2040709, 2040710, 2040711, 2040806, 2040903, 2041024, 2041025, 2043003, 2043103, 2043203, 2043303, 2043703, 2043803, 2044003, 2044103, 2044203, 2044303, 2044403, 2044503, 2044603, 2044908, 2044815, 2044019, 2044703};
    public static final int[] cashBlock = {5040005, 5330000, 5150064, 5150067, 5450004, 5450006, 5450007, 5152067, 5152071, 5152053, 5150043, 5151033, 5152050, 5153013, 5150042, 5151032, 5152049, 5030004, 5030005, 5030002, 5030003, 5030007, 5030008, 5030009, 5030010, 5030011, 5030013, 5030014, 5030015, 5220082, 5600000, 5600001, 5420008, 5530226, 5252014, 5501001, 5501002, 5521000, 5660000, 5660001, 5350003, 5690000, 5320000, 5533002, 5230000, 5190010/*5080001, 5080000, 5063000, 5064000, 5660000, 5660001, 5222027, 5530172, 5530173, 5530174, 5530175, 5530176, 5530177, 5251016, 5534000, 5152053, 5152058, 5150044, 5150040, 5220082, 5680021, 5150050, 5211091, 5211092, 5220087, 5220088, 5220089, 5220090, 5220085, 5220086, 5470000, 1002971, 1052202, 5060003, 5060004, 5680015, 5220082, 5530146, 5530147, 5530148, 5710000, 5500000, 5500001, 5500002, 5500002, 5500003, 5500004, 5500005, 5500006, 5050000, 5075000, 5075001, 5075002, 1122121, 5450000, 5190005, 5190007, 5600000, 5600001, 5350003, 2300002, 2300003, 5330000, 5062000, 5062001, 5211071, 5211072, 5211073, 5211074, 5211075, 5211076, 5211077, 5211078, 5211079, 5650000, 5431000, 5431001, 5432000, 5450000, 5550000, 5550001, 5640000, 5530013, 5150039, 5150040, 5150046, 5150054, 5150052, 5150053, 5151035, 5151036, 5152053, 5152056, 5152057, 5152058, 1812006, 5650000, 5222000, 5221001, 5220014, 5220015, 5420007, 5451000,
        5210000, 5210001, 5210002, 5210003, 5210004, 5210005, 5210006, 5210007, 5210008, 5210009, 5210010, 5210011, 5211000, 5211001, 5211002, 5211003, 5211004, 5211005, 5211006, 5211007, 5211008, 5211009, 5211010, 5211011, 5211012, 5211013, 5211014, 5211015, 5211016, 5211017, 5211018,
        5211019, 5211020, 5211021, 5211022, 5211023, 5211024, 5211025, 5211026, 5211027, 5211028, 5211029, 5211030, 5211031, 5211032, 5211033, 5211034, 5211035, 5211036, 5211037, 5211038, 5211039, 5211040, 5211041, 5211042, 5211043,
        5211044, 5211045, 5211046, 5211047, 5211048, 5211049, 5211050, 5211051, 5211052, 5211053, 5211054, 5211055, 5211056, 5211057, 5211058, 5211059, 5211060, 5211061,//2x exp
        5360000, 5360001, 5360002, 5360003, 5360004, 5360005, 5360006, 5360007, 5360008, 5360009, 5360010, 5360011, 5360012, 5360013, 5360014, 5360017, 5360050, 5211050, 5360042, 5360052, 5360053, 5360050, //2x drop
        1112810, 1112811, 5530013, 4001431, 4001432, 4032605,
        5140000, 5140001, 5140002, 5140003, 5140004, 5140007, //stores
        5270000, 5270001, 5270002, 5270003, 5270004, 5270005, 5270006, //2x meso
        9102328, 9102329, 9102330, 9102331, 9102332, 9102333*/
    }; //miracle cube and stuff
    public static final int[] rankC = {70000000, 70000001, 70000002, 70000003, 70000004, 70000005, 70000006, 70000007, 70000008, 70000009, 70000010, 70000011, 70000012, 70000013};
    public static final int[] rankB = {70000014, 70000015, 70000016, 70000017, 70000018, 70000021, 70000022, 70000023, 70000024, 70000025, 70000026};
    public static final int[] rankA = {70000027, 70000028, 70000029, 70000030, 70000031, 70000032, 70000033, 70000034, 70000035, 70000036, 70000039, 70000040, 70000041, 70000042};
    public static final int[] rankS = {70000043, 70000044, 70000045, 70000047, 70000048, 70000049, 70000050, 70000051, 70000052, 70000053, 70000054, 70000055, 70000056, 70000057, 70000058, 70000059, 70000060, 70000061, 70000062};
    public static final int[] circulators = {2702000, 2700000, 2700100, 2700200, 2700300, 2700400, 2700500, 2700600, 2700700, 2700800, 2700900, 2701000};
    public static final int MAX_BUFFSTAT = 8;
    public static final String[] RESERVED = {"Rental", "Donor", "MapleNews"};
    public static final String[] stats = {"tuc", "reqLevel", "reqJob", "reqSTR", "reqDEX", "reqINT", "reqLUK", "reqPOP", "cash", "cursed", "success", "setItemID", "equipTradeBlock", "durability", "randOption", "randStat", "masterLevel", "reqSkillLevel", "elemDefault", "incRMAS", "incRMAF", "incRMAI", "incRMAL", "canLevel", "skill", "charmEXP"};
    public static final int[] hyperTele = {310000000, 220000000, 100000000, 250000000, 240000000, 104000000, 103000000, 102000000, 101000000, 120000000, 260000000, 200000000, 230000000};
    public static List<Integer> blockedNpcs = new LinkedList<>();

    public static int getExpNeededForLevel(final int level) {
        if (level < 0 || level >= exp.length) {
            return Integer.MAX_VALUE;
        }
        return exp[level];
    }

    public static int getSkillLevel(int level) {
        if (level >= 70 && level < 120) {
            return 2;
        }
        if (level >= 120 && level < 200) {
            return 3;
        }
        if (level == 200) {
            return 4;
        }
        return 1;
    }

    public static int[] getInnerSkillbyRank(int rank) {
        switch (rank) {
            case 0:
                return rankC;
            case 1:
                return rankB;
            case 2:
                return rankA;
            case 3:
                return rankS;
            default:
                return null;
        }
    }

    public static int getGuildExpNeededForLevel(final int level) {
        if (level < 0 || level >= guildexp.length) {
            return Integer.MAX_VALUE;
        }
        return guildexp[level];
    }

    public static int getPVPExpNeededForLevel(final int level) {
        if (level < 0 || level >= pvpExp.length) {
            return Integer.MAX_VALUE;
        }
        return pvpExp[level];
    }

    public static int getClosenessNeededForLevel(final int level) {
        return closeness[level - 1];
    }

    public static int getMountExpNeededForLevel(final int level) {
        return mountexp[level - 1];
    }

    public static int getTraitExpNeededForLevel(final int level) {
        if (level < 0 || level >= cumulativeTraitExp.length) {
            return Integer.MAX_VALUE;
        }
        return cumulativeTraitExp[level];
    }

    public static int getSetExpNeededForLevel(final int level) {
        if (level < 0 || level >= setScore.length) {
            return Integer.MAX_VALUE;
        }
        return setScore[level];
    }

    public static int getMonsterHP(final int level) {
        if (level < 0 || level >= mobHpVal.length) {
            return Integer.MAX_VALUE;
        }
        return mobHpVal[level];
    }

    public static int getBookLevel(final int level) {
        return (int) ((5 * level) * (level + 1));
    }

    public static int getTimelessRequiredEXP(final int level) {
        return 70 + (level * 10);
    }

    public static int getReverseRequiredEXP(final int level) {
        return 60 + (level * 5);
    }

    public static int getProfessionEXP(final int level) {
        return ((100 * level * level) + (level * 400)) / 2;
    }

    public static boolean isHarvesting(final int itemId) {
        return itemId >= 1500000 && itemId < 1520000;
    }

    public static int maxViewRangeSq() {
        return Integer.MAX_VALUE;
    }

    public static int maxViewRangeSq_Half() {
        return Integer.MAX_VALUE;
    }

    public static boolean isJobFamily(final int baseJob, final int currentJob) {
        return currentJob >= baseJob && currentJob / 100 == baseJob / 100;
    }

    public static boolean isDB(final int job) {
        return job >= 430 && job <= 434;
    }

    public static boolean isKOC(final int job) {
        return job >= 1000 && job < 2000;
    }

    public static boolean isDawnWarrior(final int job) {
        return job == 1000 || (job >= 1100 && job <= 1112);
    }

    public static boolean isEvan(final int job) {
        return job == 2001 || (job >= 2200 && job <= 2218);
    }

    public static boolean isMercedes(final int job) {
        return job == 2002 || (job >= 2300 && job <= 2312);
    }

    public static boolean isWildHunter(final int job) {
        return job == 3000 || (job >= 3300 && job <= 3312);
    }

    public static boolean isDemon(final int job) {
        return job == 3001 || (job >= 3100 && job <= 3112);
    }

    public static boolean isAran(final int job) {
        return job == 2000 || (job >= 2100 && job <= 2112);
    }

    public static boolean isResist(final int job) {
        return job >= 3000 && job <= 3512;
    }

    public static boolean isAdventurer(final int job) {
        return job >= 0 && job < 1000;
    }

    public static boolean isCannon(final int job) {
        return job == 1 || job == 501 || (job >= 530 && job <= 532);
    }

    public static boolean isMihile(final int job) {
        return job == 5000 || (job >= 5100 && job <= 5112);
    }

    public static boolean isSeparatedSp(final int job) {
        return job / 1000 == 3 || (job / 100 == 22 || job == 2001) || (job / 100 == 23 || job == 2002) || (job / 100 == 24 || job == 2003) || (job / 100 == 51 || job == 5000);
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
            case 35121013:
                return 35111004;
            case 35121011:
                return 35121009;
            case 32001007:
            case 32001008:
            case 32001009:
            case 32001010:
            case 32001011:
                return 32001001;
            case 5300007:
                return 5301001;
            case 5320011:
                return 5321004;
            case 23101007:
                return 23101001;
            case 23111010:
            case 23111009:
                return 23111008;
            case 31001006:
            case 31001007:
            case 31001008:
                return 31000004;
            case 30010183:
            case 30010184:
            case 30010186:
                return 30010110;
        }
        return id;
    }

    public final static boolean isForceIncrease(int skillid) {
        switch (skillid) {
            case 31000004:
            case 31001006:
            case 31001007:
            case 31001008:

            case 30010166:
            case 30011167:
            case 30011168:
            case 30011169:
            case 30011170:
                return true;
        }
        return false;
    }

    public static int getBOF_ForJob(final int job) {
        return PlayerStats.getSkillByJob(12, job);
    }

    public static int getEmpress_ForJob(final int job) {
        return PlayerStats.getSkillByJob(73, job);
    }

    public static int getWOTA_ForJob(final int job) {
        return PlayerStats.getSkillByJob(190, job);
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

    public static int getJobShortValue(int job) {
        if (job >= 1000) {
            job -= (job / 1000) * 1000;
        }
        job /= 100;
        if (job == 4) { // For some reason dagger/ claw is 8.. IDK
            job *= 2;
        } else if (job == 3) {
            job += 1;
        } else if (job == 5) {
            job += 11; // 16
        }
        return job;
    }

    public static boolean isPyramidSkill(final int skill) {
        return isBeginnerJob(skill / 10000) && skill % 10000 == 1020;
    }

    public static boolean isInflationSkill(final int skill) {
        return isBeginnerJob(skill / 10000) && skill % 10000 == 1092;
    }

    public static boolean isBigAttackSkill(final int skill) {
        return isBeginnerJob(skill / 10000) && (skill % 10000 == 1094 || skill % 10000 == 1095);
    }

    public static boolean isMulungSkill(final int skill) {
        return isBeginnerJob(skill / 10000) && (skill % 10000 == 1009 || skill % 10000 == 1010 || skill % 10000 == 1011);
    }

    public static boolean isIceKnightSkill(final int skill) {
        return isBeginnerJob(skill / 10000) && (skill % 10000 == 1098 || skill % 10000 == 99 || skill % 10000 == 100 || skill % 10000 == 103 || skill % 10000 == 104 || skill % 10000 == 1105);
    }

    public static boolean isThrowingStar(final int itemId) {
        return itemId / 10000 == 207;
    }

    public static boolean isBullet(final int itemId) {
        return itemId / 10000 == 233;
    }

    public static boolean isRechargable(final int itemId) {
        return isThrowingStar(itemId) || isBullet(itemId);
    }

    public static boolean isOverall(final int itemId) {
        return itemId / 10000 == 105;
    }

    public static boolean isPet(final int itemId) {
        return itemId / 10000 == 500;
    }

    public static boolean isArrowForCrossBow(final int itemId) {
        return itemId >= 2061000 && itemId < 2062000;
    }

    public static boolean isArrowForBow(final int itemId) {
        return itemId >= 2060000 && itemId < 2061000;
    }

    public static boolean isMagicWeapon(final int itemId) {
        final int s = itemId / 10000;
        return s == 137 || s == 138;
    }

    public static boolean isWeapon(final int itemId) {
        return itemId >= 1300000 && itemId < 1600000;
    }

    public static MapleInventoryType getInventoryType(final int itemId) {
        MapleInventoryType type = MapleInventoryType.getByType((byte) (itemId / 1000000));
        if (type == MapleInventoryType.UNDEFINED || type == null) {
            final byte type2 = (byte) (itemId / 10000);
            if (type2 == 2) {
                type = MapleInventoryType.FACE;
            } else if (type2 == 3) {
                type = MapleInventoryType.HAIR;
            } else {
                type = MapleInventoryType.UNDEFINED;
            }
        }
        return type;
    }

    public static boolean isInBag(final int slot, final byte type) {
        return ((slot >= 101 && slot <= 512) && type == MapleInventoryType.ETC.getType());
    }

    public static MapleWeaponType getWeaponType(final int itemId) {
        int cat = itemId / 10000;
        cat = cat % 100;
        switch (cat) { // 39, 50, 51 ??
            case 30:
                return MapleWeaponType.單手劍;
            case 31:
                return MapleWeaponType.單手斧;
            case 32:
                return MapleWeaponType.單手棍;
            case 33:
                return MapleWeaponType.短劍;
            case 34:
                return MapleWeaponType.雙刀;
            case 35:
                return MapleWeaponType.魔法箭; // can be magic arrow or cards
            case 36:
                return MapleWeaponType.手杖;
            case 37:
                return MapleWeaponType.短杖;
            case 38:
                return MapleWeaponType.長杖;
            case 40:
                return MapleWeaponType.雙手劍;
            case 41:
                return MapleWeaponType.雙手斧;
            case 42:
                return MapleWeaponType.雙手棍;
            case 43:
                return MapleWeaponType.槍;
            case 44:
                return MapleWeaponType.矛;
            case 45:
                return MapleWeaponType.弓;
            case 46:
                return MapleWeaponType.弩;
            case 47:
                return MapleWeaponType.拳套;
            case 48:
                return MapleWeaponType.指虎;
            case 49:
                return MapleWeaponType.火槍;
            case 52:
                return MapleWeaponType.雙弩;
            case 53:
                return MapleWeaponType.火砲;
        }
        //System.out.println("Found new Weapon: " + cat + ", ItemId: " + itemId);
        return MapleWeaponType.沒有武器;
    }

    public static boolean isShield(final int itemId) {
        int cat = itemId / 10000;
        cat = cat % 100;
        return cat == 9;
    }

    public static boolean isEquip(final int itemId) {
        return itemId / 1000000 == 1;
    }

    public static boolean isCleanSlate(int itemId) {
        return itemId / 100 == 20490;
    }

    public static boolean isAccessoryScroll(int itemId) {
        return itemId / 100 == 20492;
    }

    public static boolean isInnocenceScroll(int itemId) {
        return itemId / 100 == 20496 || itemId == 5064200;
    }

    public static boolean isChaosScroll(int itemId) {
        if (itemId >= 2049105 && itemId <= 2049110) {
            return false;
        }
        return itemId / 100 == 20491 || itemId == 2040126;
    }

    public static int getChaosNumber(int itemId) {
        return itemId == 2049116 ? 10 : 5;
    }

    public static boolean isEquipScroll(int scrollId) {
        return scrollId / 100 == 20493;
    }

    public static boolean isPotentialScroll(int scrollId) {
        return scrollId / 100 == 20494 || scrollId / 100 == 20497 || scrollId == 5534000;
    }

    public static boolean isSpecialScroll(final int scrollId) {
        switch (scrollId) {
            case 2040727: // Spikes on show
            case 2041058: // Cape for Cold protection
            case 2530000:
            case 2530001:
            case 2531000:
            case 5063000:
            case 5064000:
            case 5064100:
                return true;
        }
        return false;
    }

    public static boolean isTwoHanded(final int itemId) {
        // 暫時修復雙弩槍(2017/07/25)
        int cat = itemId / 10000;
        cat = cat % 100;
        if (cat == 52) {
            return true;
        }
        MapleWeaponType type = getWeaponType(itemId);
        switch (type) {
            case 雙手斧:
            case 火槍:
            case 指虎:
            case 雙手棍:
            case 弓:
            case 拳套:
            case 弩:
            case 矛:
            case 槍:
            case 雙手劍:
            case 火砲:
            case 雙弩: //magic arrow
                return true;
            default:
                return false;
        }
    }

    public static boolean isTownScroll(final int id) {
        return id >= 2030000 && id < 2040000;
    }

    public static boolean isUpgradeScroll(final int id) {
        return id >= 2040000 && id < 2050000;
    }

    public static boolean isGun(final int id) {
        return id >= 1492000 && id < 1500000;
    }

    public static boolean isUse(final int id) {
        return id >= 2000000 && id < 3000000;
    }

    public static boolean isSummonSack(final int id) {
        return id / 10000 == 210;
    }

    public static boolean isMonsterCard(final int id) {
        return id / 10000 == 238;
    }

    public static boolean isSpecialCard(final int id) {
        return id / 1000 >= 2388;
    }

    public static int getCardShortId(final int id) {
        return id % 10000;
    }

    public static boolean isGem(final int id) {
        return id >= 4250000 && id <= 4251402;
    }

    public static boolean isOtherGem(final int id) {
        switch (id) {
            case 4001174:
            case 4001175:
            case 4001176:
            case 4001177:
            case 4001178:
            case 4001179:
            case 4001180:
            case 4001181:
            case 4001182:
            case 4001183:
            case 4001184:
            case 4001185:
            case 4001186:
            case 4031980:
            case 2041058:
            case 2040727:
            case 1032062:
            case 4032334:
            case 4032312:
            case 1142156:
            case 1142157:
                return true; //mostly quest items
        }
        return false;
    }

    public static boolean isCustomQuest(final int id) {
        return id > 99999;
    }

    public static int getTaxAmount(final int meso) {
        if (meso >= 100000000) {
            return (int) Math.round(0.06 * meso);
        } else if (meso >= 25000000) {
            return (int) Math.round(0.05 * meso);
        } else if (meso >= 10000000) {
            return (int) Math.round(0.04 * meso);
        } else if (meso >= 5000000) {
            return (int) Math.round(0.03 * meso);
        } else if (meso >= 1000000) {
            return (int) Math.round(0.018 * meso);
        } else if (meso >= 100000) {
            return (int) Math.round(0.008 * meso);
        }
        return 0;
    }

    public static int EntrustedStoreTax(final int meso) {
        if (meso >= 100000000) {
            return (int) Math.round(0.03 * meso);
        } else if (meso >= 25000000) {
            return (int) Math.round(0.025 * meso);
        } else if (meso >= 10000000) {
            return (int) Math.round(0.02 * meso);
        } else if (meso >= 5000000) {
            return (int) Math.round(0.015 * meso);
        } else if (meso >= 1000000) {
            return (int) Math.round(0.009 * meso);
        } else if (meso >= 100000) {
            return (int) Math.round(0.004 * meso);
        }
        return 0;
    }

    public static int getAttackDelay(final int id, final Skill skill) {
        switch (id) { // Assume it's faster(2)
            case 3121004: // Storm of Arrow
            case 23121000:
            case 33121009:
            case 13111002: // Storm of Arrow
            case 5221004: // Rapidfire
            case 5201006: // Recoil shot/ Back stab shot
            case 35121005:
            case 35111004:
            case 35121013:
                return 40; //reason being you can spam with final assaulter
            case 14111005:
            case 4121007:
            case 5221007:
                return 99; //skip duh chek
            case 0: // Normal Attack, TODO delay for each weapon type
                return 570;
        }
        if (skill != null && skill.getSkillType() == 3) {
            return 0; //final attack
        }
        if (skill != null && skill.getDelay() > 0 && !isNoDelaySkill(id)) {
            return skill.getDelay();
        }
        // TODO delay for final attack, weapon type, swing,stab etc
        return 330; // Default usually
    }

    public static byte gachaponRareItem(final int id) {
        switch (id) {
            case 1102041: // 粉紅冒險家披風
            case 1102042: // 紫色冒險家披風
                // 不速之客武器
            case 1302143: // 1st 不速之客 單手劍
            case 1302144: // 2nd 不速之客 單手劍
            case 1302145: // 3rd 不速之客 單手劍
            case 1302146: // Last 不速之客 單手劍
            case 1312058: // 1st 不速之客 單手斧
            case 1312059: // 2nd 不速之客 單手斧
            case 1312060: // 3rd 不速之客 單手斧
            case 1312061: // Last 不速之客 單手斧
            case 1322086: // 1st 不速之客 單手鈍器
            case 1322087: // 2nd 不速之客 單手鈍器
            case 1322088: // 3rd 不速之客 單手鈍器
            case 1322089: // Last 不速之客 單手鈍器
            case 1332116: // 1st 不速之客 短劍(LUK)
            case 1332117: // 2nd 不速之客 短劍(LUK)
            case 1332118: // 3rd 不速之客 短劍(LUK)
            case 1332119: // Last 不速之客 短劍(LUK)
            case 1332121: // 1st 不速之客 短劍(STR)
            case 1332122: // 2nd 不速之客 短劍(STR)
            case 1332123: // 3rd 不速之客 短劍(STR)
            case 1332124: // Last 不速之客 短劍(STR)
            case 1342029: // 1st 不速之客 短劍
            case 1342030: // 2nd 不速之客 短劍
            case 1342031: // 3rd 不速之客 短劍
            case 1342032: // Last 不速之客 短劍
            case 1372074: // 1st 不速之客 杖
            case 1372075: // 2nd 不速之客 杖
            case 1372076: // 3rd 不速之客 杖
            case 1372077: // Last 不速之客 杖
            case 1382095: // 1st 不速之客 長杖
            case 1382096: // 2nd 不速之客 長杖
            case 1382097: // 3rd 不速之客 長杖
            case 1382098: // Last 不速之客 長杖
            case 1402086: // 1st 不速之客 雙手劍
            case 1402087: // 2nd 不速之客 雙手劍
            case 1402088: // 3rd 不速之客 雙手劍
            case 1402089: // Last 不速之客 雙手劍
            case 1412058: // 1st 不速之客 雙手斧
            case 1412059: // 2nd 不速之客 雙手斧
            case 1412060: // 3rd 不速之客 雙手斧
            case 1412061: // Last 不速之客 雙手斧
            case 1422059: // 1st 不速之客 雙手鈍器
            case 1422060: // 2nd 不速之客 雙手鈍器
            case 1422061: // 3rd 不速之客 雙手鈍器
            case 1422062: // Last 不速之客 雙手鈍器
            case 1432077: // 1st 不速之客槍
            case 1432078: // 2nd 不速之客槍
            case 1432079: // 3rd 不速之客槍
            case 1432080: // Last 不速之客槍
            case 1442107: // 1st 不速之客 矛
            case 1442108: // 2nd 不速之客 矛
            case 1442109: // 3rd 不速之客 矛
            case 1442110: // Last 不速之客 矛
            case 1452102: // 1st 不速之客 弓
            case 1452103: // 2nd 不速之客 弓
            case 1452104: // 3rd 不速之客 弓
            case 1452105: // Last 不速之客 弓
            case 1462087: // 1st 不速之客 弩
            case 1462088: // 2nd 不速之客 弩
            case 1462089: // 3rd 不速之客 弩
            case 1462090: // Last 不速之客 弩
            case 1472113: // 1st 不速之客 拳套
            case 1472114: // 2nd 不速之客 拳套
            case 1472115: // 3rd 不速之客 拳套
            case 1472116: // Last 不速之客 拳套
            case 1482075: // 1st 不速之客 指虎
            case 1482076: // 2nd 不速之客 指虎
            case 1482077: // 3rd 不速之客 指虎
            case 1482078: // Last 不速之客 指虎
            case 1492075: // 1st 不速之客 火槍
            case 1492076: // 2nd 不速之客 火槍
            case 1492077: // 3rd 不速之客 火槍
            case 1492078: // Last 不速之客 火槍
                // The VIP 武器
            case 1302147: // The VIP 單手劍
            case 1312062: // The VIP 單手斧
            case 1322090: // The VIP 單手鈍器
            case 1332120: // The VIP 短劍(LUK)
            case 1332125: // The VIP 短劍(STR)
            case 1342033: // The VIP 短劍
            case 1372078: // The VIP 杖
            case 1382099: // The VIP 長杖
            case 1402090: // The VIP 雙手劍
            case 1412062: // The VIP 雙手斧
            case 1422063: // The VIP 雙手鈍器
            case 1432081: // The VIP槍
            case 1442111: // The VIP 矛
            case 1452106: // The VIP 弓
            case 1462091: // The VIP 弩
            case 1472117: // The VIP 拳套
            case 1482079: // The VIP 指虎
            case 1492079: // The VIP 火槍
                // 傳說卷軸
            case 2046028: // 傳說單手武器攻擊力卷軸20%
            case 2046029: // 傳說單手武器魔力卷軸 20%
            case 2046030: // 傳說單手武器攻擊力卷軸 40%
            case 2046031: // 傳說單手武器魔力卷軸 40%
            case 2046032: // 傳說單手武器攻擊力卷軸 70%
            case 2046033: // 傳說單手武器魔力卷軸 70%
            case 2046034: // 傳說單手武器攻擊力卷軸20%
            case 2046035: // 傳說單手武器魔力卷軸20%
            case 2046036: // 傳說單手武器攻擊力卷軸40%
            case 2046037: // 傳說單手武器魔力卷軸40%
            case 2046038: // 傳說單手武器攻擊力卷軸70%
            case 2046039: // 傳說單手武器魔力卷軸70%
            case 2046122: // 傳說雙手武器攻擊力卷軸20%
            case 2046123: // 傳說雙手武器攻擊力卷軸40%
            case 2046124: // 傳說雙手武器攻擊力卷軸70%
            case 2046125: // 傳說雙手武器攻擊力卷軸20%
            case 2046126: // 傳說雙手武器攻擊力卷軸40%
            case 2046127: // 傳說雙手武器攻擊力卷軸70%
            case 2046253: // 傳說防禦具力量卷軸20%
            case 2046254: // 傳說防具智力卷軸 20%
            case 2046255: // 傳說防具敏捷卷軸 20%
            case 2046256: // 傳說防具幸運卷軸 20%
            case 2046257: // 傳說防禦具力量卷軸40%
            case 2046258: // 傳說防禦具智力卷軸40%
            case 2046259: // 傳說防具敏捷卷軸 40%
            case 2046260: // 傳說防具幸運卷軸 40%
            case 2046261: // 傳說防具力量卷軸 70%
            case 2046262: // 傳說防具智力卷軸 70%
            case 2046263: // 傳說防具敏捷卷軸 70%
            case 2046264: // 傳說防具幸運卷軸 70%
            case 2046265: // 傳說防具力量卷軸20%
            case 2046266: // 傳說防禦具智力卷軸20%
            case 2046267: // 傳說防禦具敏捷卷軸20%
            case 2046268: // 傳說防具幸運卷軸 20%
            case 2046269: // 傳說防具力量卷軸40%
            case 2046270: // 傳說防具智力卷軸40%
            case 2046271: // 傳說防具敏捷卷軸40%
            case 2046272: // 傳說防具幸運卷軸 40%
            case 2046273: // 傳說防具幸運卷軸 70%
            case 2046274: // 傳說防具智力卷軸70%
            case 2046275: // 傳說防具敏捷卷軸 70%
            case 2046276: // 傳說防具幸運卷軸 70%
            case 2046342: // 傳說飾品力量卷軸20%
            case 2046343: // 傳說飾品智力卷軸20%
            case 2046344: // 傳說飾品敏捷卷軸20%
            case 2046345: // 傳說飾品幸運卷軸20%
            case 2046346: // 傳說飾品力量卷軸40%
            case 2046347: // 傳說飾品智力卷軸40%
            case 2046348: // 傳說飾品敏捷卷軸40%
            case 2046349: // 傳說飾品幸運卷軸40%
            case 2046350: // 傳說飾品力量卷軸70%
            case 2046351: // 傳說飾品智力卷軸70%
            case 2046352: // 傳說飾品敏捷卷軸70%
            case 2046353: // 傳說飾品幸運卷軸70%
            case 2046354: // 傳說飾品力量卷軸20%
            case 2046355: // 傳說飾品智力卷軸20%
            case 2046356: // 傳說飾品敏捷卷軸20%
            case 2046357: // 傳說飾品幸運卷軸20%
            case 2046358: // 傳說飾品力量卷軸40%
            case 2046359: // 傳說飾品智力卷軸40%
            case 2046360: // 傳說飾品敏捷卷軸40%
            case 2046361: // 傳說飾品幸運卷軸40%
            case 2046362: // 傳說飾品力量卷軸70%
            case 2046363: // 傳說飾品智力卷軸70%
            case 2046364: // 傳說飾品敏捷卷軸70%
            case 2046365: // 傳說飾品幸運卷軸70%
            case 2048032: // 傳說寵物裝備力量卷軸 60%
            case 2048033: // 傳說寵物裝備智力卷軸 60%
            case 2048034: // 傳說寵物裝備敏捷卷軸 60%
            case 2048035: // 傳說寵物裝備幸運卷軸 60%
            case 2048036: // 傳說寵物裝備移動速度卷軸 60%
            case 2048037: // 傳說寵物裝備跳躍力卷軸 60%
            case 2046048: // 傳說單手武器攻擊力卷軸 60％
            case 2046049: // 傳說單手武器魔力卷軸 60％
            case 2046050: // 傳說單手武器攻擊力卷軸 100%
            case 2046051: // 傳說單手武器魔力卷軸 100%
            case 2046052: // 不滅的傳說 單手武器攻擊力卷軸 100%
            case 2046053: // 不滅的傳說單手武器魔力卷軸 100%
            case 2046132: // 傳說雙手武器攻擊力卷軸 60％
            case 2046133: // 傳說雙手武器攻擊力卷軸 100%
            case 2046134: // 不滅的傳說雙手武器攻擊力卷軸 100%
            case 2046135: // 傳說雙手武器魔力卷軸 60％
            case 2046136: // 傳說雙手武器魔力卷軸 100%
            case 2046137: // 不滅的傳說 雙手武器魔力卷軸 100%
            case 2046295: // 傳說防具力量卷軸60％
            case 2046296: // 傳說防具智力卷軸 60％
            case 2046297: // 傳說防具敏捷卷軸60％
            case 2046298: // 傳說防具幸運卷軸60％
            case 2046299: // 傳說防具力量卷軸100%
            case 2046377: // 傳說裝飾品力量卷軸60％
            case 2046378: // 傳說裝飾品智力卷軸 60％
            case 2046379: // 傳說裝飾品敏捷卷軸60％
            case 2046380: // 傳說裝飾品幸運卷軸60％
            case 2046381: // 傳說裝飾品力量卷軸100%
            case 2046382: // 傳說裝飾品智力卷軸 100%
            case 2046383: // 傳說裝飾品敏捷卷軸100%
            case 2046384: // 傳說裝飾品幸運卷軸100%
            case 2046385: // 不滅的傳說裝飾品力量卷軸100%
            case 2046386: // 不滅的傳說裝飾品智力卷軸 100%
            case 2046387: // 不滅的傳說裝飾品敏捷卷軸100%
            case 2046388: // 不滅的傳說裝飾品幸運卷軸100%
            case 2046500: // 傳說防具智力卷軸 100%
            case 2046501: // 傳說防具敏捷卷軸100%
            case 2046502: // 傳說防具幸運卷軸100%
            case 2046503: // 不滅的傳說防具力量卷軸100%
            case 2046504: // 不滅的傳說防具智力卷軸 100%
            case 2046505: // 不滅的傳說防具敏捷卷軸100%
            case 2046506: // 不滅的傳說防具幸運卷軸100%
            case 2048039: // 傳說寵物裝備力量卷軸60％
            case 2048040: // 傳說寵物裝備智力卷軸 60％
            case 2048041: // 傳說寵物裝備敏捷卷軸 60％
            case 2048042: // 傳說寵物裝備幸運卷軸60％

            case 2511106: // 天使祝福製作法
            case 2511107: // 黑天使祝福製作法

            case 1182006: // 傳說中的勇士胸章
            case 1142249: // 我是幸運兒
            case 1512002:
            case 2070007:
            case 2049400:
            case 2049301:
            case 2049401:
            case 1122024:
            case 1122025:
            case 1122026:
            case 1122027:
            case 1122028:
            case 1122029:
            case 1122030:
            case 1122031:
            case 1122032:
            case 1122033:
            case 2340000: // White Scroll
            case 2049100: // Chaos Scroll
            case 2049000: // Reverse Scroll
            case 2049001: // Reverse Scroll
            case 2049002: // Reverse Scroll
            case 2040006: // Miracle
            case 2040007: // Miracle
            case 2040303: // Miracle
            case 2040403: // Miracle
            case 2040506: // Miracle
            case 2040507: // Miracle
            case 2040603: // Miracle
            case 2040709: // Miracle
            case 2040710: // Miracle
            case 2040711: // Miracle
            case 2040806: // Miracle
            case 2040903: // Miracle
            case 2041024: // Miracle
            case 2041025: // Miracle
            case 2043003: // Miracle
            case 2043103: // Miracle
            case 2043203: // Miracle
            case 2043303: // Miracle
            case 2043703: // Miracle
            case 2043803: // Miracle
            case 2044003: // Miracle
            case 2044103: // Miracle
            case 2044203: // Miracle
            case 2044303: // Miracle
            case 2044403: // Miracle
            case 2044503: // Miracle
            case 2044603: // Miracle
            case 2044908: // Miracle
            case 2044815: // Miracle
            case 2044019: // Miracle
            case 2044703: // Miracle
                return 2;
            //1 = wedding msg o.o
        }
        return 0;
    }

    public final static int[] goldrewards = {
            2511106, 1, // 天使祝福製作法
            2511107, 1, // 黑天使祝福製作法

            2049400, 1, // 高級潛在能力賦予卷軸
            2049401, 2, // 潛在能力賦予卷軸
            2340000, 1, // 祝福卷軸
            2070007, 2, // 月牙鏢

            2330007, 1, // 貫穿裝甲的特製子彈
            1512002, 1,
            //2070018, 1, // balance fury
            1402037, 1, // Rigbol Sword
            2040914, 1, // 盾牌攻擊卷軸60%

            1432011, 3, // Fair Frozen
            1442020, 3, // HellSlayer
            1382035, 3, // Blue Marine
            1372010, 3, // Dimon Wand
            1332027, 3, // Varkit
            1302056, 3, // Sparta
            1402005, 3, // Bezerker
            1472053, 3, // Red Craven
            1462018, 3, // Casa Crow
            1452017, 3, // Metus
            1422013, 3, // Lemonite
            1322029, 3, // Ruin Hammer
            1412010, 3, // Colonian Axe

            1472051, 1, // Green Dragon Sleeve
            1482013, 1, // Emperor's Claw
            1492013, 1, // Dragon fire Revlover

            1382049, 1,
            1382050, 1, // Blue Dragon Staff
            1382051, 1,
            1382052, 1,
            1382045, 1, // Fire Staff, Level 105
            1382047, 1, // Ice Staff, Level 105
            1382048, 1, // Thunder Staff
            1382046, 1, // Poison Staff

            1372035, 1,
            1372036, 1,
            1372037, 1,
            1372038, 1,
            1372039, 1,
            1372040, 1,
            1372041, 1,
            1372042, 1,
            1332032, 8, // Christmas Tree
            1482025, 7, // Flowery Tube

            4001011, 8, // Lupin Eraser
            4001010, 8, // Mushmom Eraser
            4001009, 8, // Stump Eraser

            2047000, 1, // 單手武器物理攻擊力提升卷
            2047001, 1, // 單手武器命中值提升卷
            2047002, 1, // 單手武器魔法攻擊力提升卷
            2047100, 1,
            2047101, 1,
            2047102, 1,
            2047200, 1,
            2047201, 1,
            2047202, 1,
            2047203, 1,
            2047204, 1,
            2047205, 1,
            2047206, 1,
            2047207, 1,
            2047208, 1,
            2047300, 1,
            2047301, 1,
            2047302, 1,
            2047303, 1,
            2047304, 1,
            2047305, 1,
            2047306, 1,
            2047307, 1,
            2047308, 1,
            2047309, 1,
            2046004, 1,
            2046005, 1,
            2046104, 1,
            2046105, 1,
            2046208, 1,
            2046209, 1,
            2046210, 1,
            2046211, 1,
            2046212, 1,
            1442018, 3, // Frozen Tuna
            2040900, 4, // Shield for DEF
            2049100, 10,
            2000005, 10, // Power Elixir
            2000004, 10, // Elixir
            4280000, 8,
            2430144, 10,
            2028061, 10,
            2028062, 10,
            2530000, 5,
            2531000, 5}; // Gold Box
    public final static int[] silverrewards = {
            2511106, 1, // 天使祝福製作法
            2511107, 1, // 黑天使祝福製作法

            2049401, 2, // 潛在能力賦予卷軸
            2049301, 2, // 裝備強化卷軸
            3010041, 1, // skull throne
            1002452, 6, // Starry Bandana
            1002455, 6, // Starry Bandana
            1102082, 1, // Black Raggdey Cape
            1302049, 1, // Glowing Whip
            2340000, 1, // White Scroll
            1102041, 1, // Pink Cape
            1452019, 2, // White Nisrock
            4001116, 3, // Hexagon Pend
            4001012, 3, // Wraith Eraser
            1022060, 2, // Foxy Racoon Eye
            2430144, 5,
            2028062, 5,
            2028061, 5,
            2530000, 1,
            2531000, 1,
            2041100, 1,
            2041101, 1,
            2041102, 1,
            2041103, 1,
            2041104, 1,
            2041105, 1,
            2041106, 1,
            2041107, 1,
            2041108, 1,
            2041109, 1,
            2041110, 1,
            2041111, 1,
            2041112, 1,
            2041113, 1,
            2041114, 1,
            2041115, 1,
            2041116, 1,
            2041117, 1,
            2041118, 1,
            2041119, 1,
            2041300, 1,
            2041301, 1,
            2041302, 1,
            2041303, 1,
            2041304, 1,
            2041305, 1,
            2041306, 1,
            2041307, 1,
            2041308, 1,
            2041309, 1,
            2041310, 1,
            2041311, 1,
            2041312, 1,
            2041313, 1,
            2041314, 1,
            2041315, 1,
            2041316, 1,
            2041317, 1,
            2041318, 1,
            2041319, 1,
            2049200, 1,
            2049201, 1,
            2049202, 1,
            2049203, 1,
            2049204, 1,
            2049205, 1,
            2049206, 1,
            2049207, 1,
            2049208, 1,
            2049209, 1,
            2049210, 1,
            2049211, 1,
            1432011, 3, // Fair Frozen
            1442020, 3, // HellSlayer
            1382035, 3, // Blue Marine
            1372010, 3, // Dimon Wand
            1332027, 3, // Varkit
            1302056, 3, // Sparta
            1402005, 3, // Bezerker
            1472053, 3, // Red Craven
            1462018, 3, // Casa Crow
            1452017, 3, // Metus
            1422013, 3, // Lemonite
            1322029, 3, // Ruin Hammer
            1412010, 3, // Colonian Axe

            1002587, 3, // Black Wisconsin
            1402044, 1, // Pumpkin lantern
            2101013, 4, // Summoning Showa boss
            1442046, 1, // Super Snowboard
            1422031, 1, // Blue Seal Cushion
            1332054, 3, // Lonzege Dagger
            1012056, 3, // Dog Nose
            1022047, 3, // Owl Mask
            3012002, 1, // Bathtub
            1442012, 3, // Sky snowboard
            1442018, 3, // Frozen Tuna
            1432010, 3, // Omega Spear
            //1432036, 1, // Fishing Pole
            2000005, 10, // Power Elixir
            2049100, 10,
            2000004, 10, // Elixir
            4280001, 8}; // Silver Box
    public final static int[] peanuts = {2430091, 200, 2430092, 200, 2430093, 200, 2430101, 200, 2430102, 200, 2430136, 200, 2430149, 200,//mounts 
            2340000, 1, //rares
            1152000, 5, 1152001, 5, 1152004, 5, 1152005, 5, 1152006, 5, 1152007, 5, 1152008, 5, //toenail only comes when db is out.
            1152064, 5, 1152065, 5, 1152066, 5, 1152067, 5, 1152070, 5, 1152071, 5, 1152072, 5, 1152073, 5,
            3010019, 2, //chairs
            1001060, 10, 1002391, 10, 1102004, 10, 1050039, 10, 1102040, 10, 1102041, 10, 1102042, 10, 1102043, 10, //equips
            1082145, 5, 1082146, 5, 1082147, 5, 1082148, 5, 1082149, 5, 1082150, 5, //wg
            2043704, 10, 2040904, 10, 2040409, 10, 2040307, 10, 2041030, 10, 2040015, 10, 2040109, 10, 2041035, 10, 2041036, 10, 2040009, 10, 2040511, 10, 2040408, 10, 2043804, 10, 2044105, 10, 2044903, 10, 2044804, 10, 2043009, 10, 2043305, 10, 2040610, 10, 2040716, 10, 2041037, 10, 2043005, 10, 2041032, 10, 2040305, 10, //scrolls
            2040211, 5, 2040212, 5, 1022097, 10, //dragon glasses
            2049000, 10, 2049001, 10, 2049002, 10, 2049003, 10, //clean slate
            1012058, 5, 1012059, 5, 1012060, 5, 1012061, 5,//pinocchio nose msea only.
            1332100, 10, 1382058, 10, 1402073, 10, 1432066, 10, 1442090, 10, 1452058, 10, 1462076, 10, 1472069, 10, 1482051, 10, 1492024, 10, 1342009, 10, //durability weapons level 105
            2049400, 1, 2049401, 2, 2049301, 2,
            2049100, 10,
            2430144, 10,
            2028062, 10,
            2028061, 10,
            2530000, 5,
            2531000, 5,
            1032080, 5,
            1032081, 4,
            1032082, 3,
            1032083, 2,
            1032084, 1,
            1112435, 5,
            1112436, 4,
            1112437, 3,
            1112438, 2,
            1112439, 1,
            1122081, 5,
            1122082, 4,
            1122083, 3,
            1122084, 2,
            1122085, 1,
            1132036, 5,
            1132037, 4,
            1132038, 3,
            1132039, 2,
            1132040, 1,
            //source
            1092070, 5,
            1092071, 4,
            1092072, 3,
            1092073, 2,
            1092074, 1,
            1092075, 5,
            1092076, 4,
            1092077, 3,
            1092078, 2,
            1092079, 1,
            1092080, 5,
            1092081, 4,
            1092082, 3,
            1092083, 2,
            1092084, 1,
            1092087, 1,
            1092088, 1,
            1092089, 1,
            1302143, 5,
            1302144, 4,
            1302145, 3,
            1302146, 2,
            1302147, 1,
            1312058, 5,
            1312059, 4,
            1312060, 3,
            1312061, 2,
            1312062, 1,
            1322086, 5,
            1322087, 4,
            1322088, 3,
            1322089, 2,
            1322090, 1,
            1332116, 5,
            1332117, 4,
            1332118, 3,
            1332119, 2,
            1332120, 1,
            1332121, 5,
            1332122, 4,
            1332123, 3,
            1332124, 2,
            1332125, 1,
            1342029, 5,
            1342030, 4,
            1342031, 3,
            1342032, 2,
            1342033, 1,
            1372074, 5,
            1372075, 4,
            1372076, 3,
            1372077, 2,
            1372078, 1,
            1382095, 5,
            1382096, 4,
            1382097, 3,
            1382098, 2,
            1392099, 1,
            1402086, 5,
            1402087, 4,
            1402088, 3,
            1402089, 2,
            1402090, 1,
            1412058, 5,
            1412059, 4,
            1412060, 3,
            1412061, 2,
            1412062, 1,
            1422059, 5,
            1422060, 4,
            1422061, 3,
            1422062, 2,
            1422063, 1,
            1432077, 5,
            1432078, 4,
            1432079, 3,
            1432080, 2,
            1432081, 1,
            1442107, 5,
            1442108, 4,
            1442109, 3,
            1442110, 2,
            1442111, 1,
            1452102, 5,
            1452103, 4,
            1452104, 3,
            1452105, 2,
            1452106, 1,
            1462087, 5,
            1462088, 4,
            1462089, 3,
            1462090, 2,
            1462091, 1,
            1472113, 5,
            1472114, 4,
            1472115, 3,
            1472116, 2,
            1472117, 1,
            1482075, 5,
            1482076, 4,
            1482077, 3,
            1482078, 2,
            1482079, 1,
            1492075, 5,
            1492076, 4,
            1492077, 3,
            1492078, 2,
            1492079, 1,
            1132012, 2,
            1132013, 1,
            1942002, 2,
            1952002, 2,
            1962002, 2,
            1972002, 2,
            1612004, 2,
            1622004, 2,
            1632004, 2,
            1642004, 2,
            1652004, 2,
            2047000, 1,
            2047001, 1,
            2047002, 1,
            2047100, 1,
            2047101, 1,
            2047102, 1,
            2047200, 1,
            2047201, 1,
            2047202, 1,
            2047203, 1,
            2047204, 1,
            2047205, 1,
            2047206, 1,
            2047207, 1,
            2047208, 1,
            2047300, 1,
            2047301, 1,
            2047302, 1,
            2047303, 1,
            2047304, 1,
            2047305, 1,
            2047306, 1,
            2047307, 1,
            2047308, 1,
            2047309, 1,
            2046004, 1,
            2046005, 1,
            2046104, 1,
            2046105, 1,
            2046208, 1,
            2046209, 1,
            2046210, 1,
            2046211, 1,
            2046212, 1,
            2049200, 1,
            2049201, 1,
            2049202, 1,
            2049203, 1,
            2049204, 1,
            2049205, 1,
            2049206, 1,
            2049207, 1,
            2049208, 1,
            2049209, 1,
            2049210, 1,
            2049211, 1,
            //ele wand
            1372035, 1,
            1372036, 1,
            1372037, 1,
            1372038, 1,
            //ele staff
            1382045, 1,
            1382046, 1,
            1382047, 1,
            1382048, 1,
            1382049, 1,
            1382050, 1, // Blue Dragon Staff
            1382051, 1,
            1382052, 1,
            1372039, 1,
            1372040, 1,
            1372041, 1,
            1372042, 1,
            2070016, 1,
            2070007, 2,
            2330007, 1,
            2070018, 1,
            2330008, 1,
            2070023, 1,
            2070024, 1,
            2028062, 5,
            2028061, 5};
    public static int[] eventCommonReward = {
            0, 10,
            1, 10,
            4, 5,
            5060004, 25,
            4170024, 25,
            4280000, 5,
            4280001, 6,
            5490000, 5,
            5490001, 6
    };
    public static int[] eventUncommonReward = {
            1, 4,
            2, 8,
            3, 8,
            2022179, 5,
            5062000, 20,
            2430082, 20,
            2430092, 20,
            2022459, 2,
            2022460, 1,
            2022462, 1,
            2430103, 2,
            2430117, 2,
            2430118, 2,
            2430201, 4,
            2430228, 4,
            2430229, 4,
            2430283, 4,
            2430136, 4,
            2430476, 4,
            2430511, 4,
            2430206, 4,
            2430199, 1,
            1032062, 5,
            5220000, 28,
            2022459, 5,
            2022460, 5,
            2022461, 5,
            2022462, 5,
            2022463, 5,
            5050000, 2,
            4080100, 10,
            4080000, 10,
            2049100, 10,
            2430144, 10,
            2028062, 10,
            2028061, 10,
            2530000, 5,
            2531000, 5,
            2041100, 1,
            2041101, 1,
            2041102, 1,
            2041103, 1,
            2041104, 1,
            2041105, 1,
            2041106, 1,
            2041107, 1,
            2041108, 1,
            2041109, 1,
            2041110, 1,
            2041111, 1,
            2041112, 1,
            2041113, 1,
            2041114, 1,
            2041115, 1,
            2041116, 1,
            2041117, 1,
            2041118, 1,
            2041119, 1,
            2041300, 1,
            2041301, 1,
            2041302, 1,
            2041303, 1,
            2041304, 1,
            2041305, 1,
            2041306, 1,
            2041307, 1,
            2041308, 1,
            2041309, 1,
            2041310, 1,
            2041311, 1,
            2041312, 1,
            2041313, 1,
            2041314, 1,
            2041315, 1,
            2041316, 1,
            2041317, 1,
            2041318, 1,
            2041319, 1,
            2049200, 1,
            2049201, 1,
            2049202, 1,
            2049203, 1,
            2049204, 1,
            2049205, 1,
            2049206, 1,
            2049207, 1,
            2049208, 1,
            2049209, 1,
            2049210, 1,
            2049211, 1
    };
    public static int[] eventRareReward = {
            2049100, 5,
            2430144, 5,
            2028062, 5,
            2028061, 5,
            2530000, 2,
            2531000, 2,
            2049116, 1,
            2049401, 10,
            2049301, 20,
            2049400, 3,
            2340000, 1,
            3010130, 5,
            3010131, 5,
            3010132, 5,
            3010133, 5,
            3010136, 5,
            3010116, 5,
            3010117, 5,
            3010118, 5,
            1112405, 1,
            1112445, 1,
            1022097, 1,
            2040211, 1,
            2040212, 1,
            2049000, 2,
            2049001, 2,
            2049002, 2,
            2049003, 2,
            1012058, 2,
            1012059, 2,
            1012060, 2,
            1012061, 2,
            2022460, 4,
            2022461, 3,
            2022462, 4,
            2022463, 3,
            2040041, 1,
            2040042, 1,
            2040334, 1,
            2040430, 1,
            2040538, 1,
            2040539, 1,
            2040630, 1,
            2040740, 1,
            2040741, 1,
            2040742, 1,
            2040829, 1,
            2040830, 1,
            2040936, 1,
            2041066, 1,
            2041067, 1,
            2043023, 1,
            2043117, 1,
            2043217, 1,
            2043312, 1,
            2043712, 1,
            2043812, 1,
            2044025, 1,
            2044117, 1,
            2044217, 1,
            2044317, 1,
            2044417, 1,
            2044512, 1,
            2044612, 1,
            2044712, 1,
            2046000, 1,
            2046001, 1,
            2046004, 1,
            2046005, 1,
            2046100, 1,
            2046101, 1,
            2046104, 1,
            2046105, 1,
            2046200, 1,
            2046201, 1,
            2046202, 1,
            2046203, 1,
            2046208, 1,
            2046209, 1,
            2046210, 1,
            2046211, 1,
            2046212, 1,
            2046300, 1,
            2046301, 1,
            2046302, 1,
            2046303, 1,
            2047000, 1,
            2047001, 1,
            2047002, 1,
            2047100, 1,
            2047101, 1,
            2047102, 1,
            2047200, 1,
            2047201, 1,
            2047202, 1,
            2047203, 1,
            2047204, 1,
            2047205, 1,
            2047206, 1,
            2047207, 1,
            2047208, 1,
            2047300, 1,
            2047301, 1,
            2047302, 1,
            2047303, 1,
            2047304, 1,
            2047305, 1,
            2047306, 1,
            2047307, 1,
            2047308, 1,
            2047309, 1,
            1112427, 5,
            1112428, 5,
            1112429, 5,
            1012240, 10,
            1022117, 10,
            1032095, 10,
            1112659, 10,
            2070007, 10,
            2330007, 5,
            2070016, 5,
            2070018, 5,
            1152038, 1,
            1152039, 1,
            1152040, 1,
            1152041, 1,
            1122090, 1,
            1122094, 1,
            1122098, 1,
            1122102, 1,
            1012213, 1,
            1012219, 1,
            1012225, 1,
            1012231, 1,
            1012237, 1,
            2070023, 5,
            2070024, 5,
            2330008, 5,
            2003516, 5,
            2003517, 1,
            1132052, 1,
            1132062, 1,
            1132072, 1,
            1132082, 1,
            1112585, 1,
            //walker
            1072502, 1,
            1072503, 1,
            1072504, 1,
            1072505, 1,
            1072506, 1,
            1052333, 1,
            1052334, 1,
            1052335, 1,
            1052336, 1,
            1052337, 1,
            1082305, 1,
            1082306, 1,
            1082307, 1,
            1082308, 1,
            1082309, 1,
            1003197, 1,
            1003198, 1,
            1003199, 1,
            1003200, 1,
            1003201, 1,
            1662000, 1,
            1662001, 1,
            1672000, 1,
            1672001, 1,
            1672002, 1,
            //crescent moon
            1112583, 1,
            1032092, 1,
            1132084, 1,
            //mounts, 90 day
            2430290, 1,
            2430292, 1,
            2430294, 1,
            2430296, 1,
            2430298, 1,
            2430300, 1,
            2430302, 1,
            2430304, 1,
            2430306, 1,
            2430308, 1,
            2430310, 1,
            2430312, 1,
            2430314, 1,
            2430316, 1,
            2430318, 1,
            2430320, 1,
            2430322, 1,
            2430324, 1,
            2430326, 1,
            2430328, 1,
            2430330, 1,
            2430332, 1,
            2430334, 1,
            2430336, 1,
            2430338, 1,
            2430340, 1,
            2430342, 1,
            2430344, 1,
            2430347, 1,
            2430349, 1,
            2430351, 1,
            2430353, 1,
            2430355, 1,
            2430357, 1,
            2430359, 1,
            2430361, 1,
            2430392, 1,
            2430512, 1,
            2430536, 1,
            2430477, 1,
            2430146, 1,
            2430148, 1,
            2430137, 1,};
    public static int[] eventSuperReward = {
            //4031307, 50,
            3010127, 10,
            3010128, 10,
            3010137, 10,
            3010157, 10,
            2049300, 10,
            2040758, 10,
            1442057, 10,
            2049402, 10,
            2049304, 1,
            2049305, 1,
            2040759, 7,
            2040760, 5,
            2040125, 10,
            2040126, 10,
            1012191, 5,
            1112514, 1, //untradable/tradable
            1112531, 1,
            1112629, 1,
            1112646, 1,
            1112515, 1, //untradable/tradable
            1112532, 1,
            1112630, 1,
            1112647, 1,
            1112516, 1, //untradable/tradable
            1112533, 1,
            1112631, 1,
            1112648, 1,
            2040045, 10,
            2040046, 10,
            2040333, 10,
            2040429, 10,
            2040542, 10,
            2040543, 10,
            2040629, 10,
            2040755, 10,
            2040756, 10,
            2040757, 10,
            2040833, 10,
            2040834, 10,
            2041068, 10,
            2041069, 10,
            2043022, 12,
            2043120, 12,
            2043220, 12,
            2043313, 12,
            2043713, 12,
            2043813, 12,
            2044028, 12,
            2044120, 12,
            2044220, 12,
            2044320, 12,
            2044520, 12,
            2044513, 12,
            2044613, 12,
            2044713, 12,
            2044817, 12,
            2044910, 12,
            2046002, 5,
            2046003, 5,
            2046102, 5,
            2046103, 5,
            2046204, 10,
            2046205, 10,
            2046206, 10,
            2046207, 10,
            2046304, 10,
            2046305, 10,
            2046306, 10,
            2046307, 10,
            2040006, 2,
            2040007, 2,
            2040303, 2,
            2040403, 2,
            2040506, 2,
            2040507, 2,
            2040603, 2,
            2040709, 2,
            2040710, 2,
            2040711, 2,
            2040806, 2,
            2040903, 2,
            2040913, 2,
            2041024, 2,
            2041025, 2,
            2044815, 2,
            2044908, 2,
            1152046, 1,
            1152047, 1,
            1152048, 1,
            1152049, 1,
            1122091, 1,
            1122095, 1,
            1122099, 1,
            1122103, 1,
            1012214, 1,
            1012220, 1,
            1012226, 1,
            1012232, 1,
            1012238, 1,
            1032088, 1,
            1032089, 1,
            1032090, 1,
            1032091, 1,
            1132053, 1,
            1132063, 1,
            1132073, 1,
            1132083, 1,
            1112586, 1,
            1112593, 1,
            1112597, 1,
            1662002, 1,
            1662003, 1,
            1672003, 1,
            1672004, 1,
            1672005, 1,
            //130, 140 weapons
            1092088, 1,
            1092089, 1,
            1092087, 1,
            1102275, 1,
            1102276, 1,
            1102277, 1,
            1102278, 1,
            1102279, 1,
            1102280, 1,
            1102281, 1,
            1102282, 1,
            1102283, 1,
            1102284, 1,
            1082295, 1,
            1082296, 1,
            1082297, 1,
            1082298, 1,
            1082299, 1,
            1082300, 1,
            1082301, 1,
            1082302, 1,
            1082303, 1,
            1082304, 1,
            1072485, 1,
            1072486, 1,
            1072487, 1,
            1072488, 1,
            1072489, 1,
            1072490, 1,
            1072491, 1,
            1072492, 1,
            1072493, 1,
            1072494, 1,
            1052314, 1,
            1052315, 1,
            1052316, 1,
            1052317, 1,
            1052318, 1,
            1052319, 1,
            1052329, 1,
            1052321, 1,
            1052322, 1,
            1052323, 1,
            1003172, 1,
            1003173, 1,
            1003174, 1,
            1003175, 1,
            1003176, 1,
            1003177, 1,
            1003178, 1,
            1003179, 1,
            1003180, 1,
            1003181, 1,
            1302152, 1,
            1302153, 1,
            1312065, 1,
            1312066, 1,
            1322096, 1,
            1322097, 1,
            1332130, 1,
            1332131, 1,
            1342035, 1,
            1342036, 1,
            1372084, 1,
            1372085, 1,
            1382104, 1,
            1382105, 1,
            1402095, 1,
            1402096, 1,
            1412065, 1,
            1412066, 1,
            1422066, 1,
            1422067, 1,
            1432086, 1,
            1432087, 1,
            1442116, 1,
            1442117, 1,
            1452111, 1,
            1452112, 1,
            1462099, 1,
            1462100, 1,
            1472122, 1,
            1472123, 1,
            1482084, 1,
            1482085, 1,
            1492085, 1,
            1492086, 1,
            1532017, 1,
            1532018, 1,
            //mounts
            2430291, 1,
            2430293, 1,
            2430295, 1,
            2430297, 1,
            2430299, 1,
            2430301, 1,
            2430303, 1,
            2430305, 1,
            2430307, 1,
            2430309, 1,
            2430311, 1,
            2430313, 1,
            2430315, 1,
            2430317, 1,
            2430319, 1,
            2430321, 1,
            2430323, 1,
            2430325, 1,
            2430327, 1,
            2430329, 1,
            2430331, 1,
            2430333, 1,
            2430335, 1,
            2430337, 1,
            2430339, 1,
            2430341, 1,
            2430343, 1,
            2430345, 1,
            2430348, 1,
            2430350, 1,
            2430352, 1,
            2430354, 1,
            2430356, 1,
            2430358, 1,
            2430360, 1,
            2430362, 1,
            //rising sun
            1012239, 1,
            1122104, 1,
            1112584, 1,
            1032093, 1,
            1132085, 1
    };
    public static int[] tenPercent = {
            //10% scrolls
            2040002,
            2040005,
            2040026,
            2040031,
            2040100,
            2040105,
            2040200,
            2040205,
            2040302,
            2040310,
            2040318,
            2040323,
            2040328,
            2040329,
            2040330,
            2040331,
            2040402,
            2040412,
            2040419,
            2040422,
            2040427,
            2040502,
            2040505,
            2040514,
            2040517,
            2040534,
            2040602,
            2040612,
            2040619,
            2040622,
            2040627,
            2040702,
            2040705,
            2040708,
            2040727,
            2040802,
            2040805,
            2040816,
            2040825,
            2040902,
            2040915,
            2040920,
            2040925,
            2040928,
            2040933,
            2041002,
            2041005,
            2041008,
            2041011,
            2041014,
            2041017,
            2041020,
            2041023,
            2041058,
            2041102,
            2041105,
            2041108,
            2041111,
            2041302,
            2041305,
            2041308,
            2041311,
            2043002,
            2043008,
            2043019,
            2043102,
            2043114,
            2043202,
            2043214,
            2043302,
            2043402,
            2043702,
            2043802,
            2044002,
            2044014,
            2044015,
            2044102,
            2044114,
            2044202,
            2044214,
            2044302,
            2044314,
            2044402,
            2044414,
            2044502,
            2044602,
            2044702,
            2044802,
            2044809,
            2044902,
            2045302,
            2048002,
            2048005
    };
    public static int[] fishingReward = {
            0, 100, // Meso
            1, 100, // EXP
            2022179, 1, // Onyx Apple
            1302021, 5, // Pico Pico Hammer
            1072238, 1, // Voilet Snowshoe
            1072239, 1, // Yellow Snowshoe
            2049100, 2, // Chaos Scroll
            2430144, 1,
            2028062, 1,
            2028061, 1,
            2049301, 1, // Equip Enhancer Scroll
            2049401, 1, // Potential Scroll
            1302000, 3, // Sword
            1442011, 1, // Surfboard
            4000517, 8, // Golden Fish
            4000518, 10, // Golden Fish Egg
            4031627, 2, // White Bait (3cm)
            4031628, 1, // Sailfish (120cm)
            4031630, 1, // Carp (30cm)
            4031631, 1, // Salmon(150cm)
            4031632, 1, // Shovel
            4031633, 2, // Whitebait (3.6cm)
            4031634, 1, // Whitebait (5cm)
            4031635, 1, // Whitebait (6.5cm)
            4031636, 1, // Whitebait (10cm)
            4031637, 2, // Carp (53cm)
            4031638, 2, // Carp (60cm)
            4031639, 1, // Carp (100cm)
            4031640, 1, // Carp (113cm)
            4031641, 2, // Sailfish (128cm)
            4031642, 2, // Sailfish (131cm)
            4031643, 1, // Sailfish (140cm)
            4031644, 1, // Sailfish (148cm)
            4031645, 2, // Salmon (166cm)
            4031646, 2, // Salmon (183cm)
            4031647, 1, // Salmon (227cm)
            4031648, 1, // Salmon (288cm)
            4001187, 20,
            4001188, 20,
            4001189, 20,
            4031629, 1 // Pot
    };

    public static int[] xmasReward = {
            20300223, 1,
            20300221, 1,
            20300275, 1
    };

    public static int[] surpriseStyleBoxReward = {
            20300223, 1,};

    public static boolean isReverseItem(int itemId) {
        switch (itemId) {
            case 1002790:
            case 1002791:
            case 1002792:
            case 1002793:
            case 1002794:
            case 1082239:
            case 1082240:
            case 1082241:
            case 1082242:
            case 1082243:
            case 1052160:
            case 1052161:
            case 1052162:
            case 1052163:
            case 1052164:
            case 1072361:
            case 1072362:
            case 1072363:
            case 1072364:
            case 1072365:

            case 1302086:
            case 1312038:
            case 1322061:
            case 1332075:
            case 1332076:
            case 1372045:
            case 1382059:
            case 1402047:
            case 1412034:
            case 1422038:
            case 1432049:
            case 1442067:
            case 1452059:
            case 1462051:
            case 1472071:
            case 1482024:
            case 1492025:

            case 1342012:
            case 1942002:
            case 1952002:
            case 1962002:
            case 1972002:
            case 1532016:
            case 1522017:
                return true;
            default:
                return false;
        }
    }

    public static boolean isTimelessItem(int itemId) {
        switch (itemId) {
            case 1032031: //shield earring, but technically
            case 1102172:
            case 1002776:
            case 1002777:
            case 1002778:
            case 1002779:
            case 1002780:
            case 1082234:
            case 1082235:
            case 1082236:
            case 1082237:
            case 1082238:
            case 1052155:
            case 1052156:
            case 1052157:
            case 1052158:
            case 1052159:
            case 1072355:
            case 1072356:
            case 1072357:
            case 1072358:
            case 1072359:
            case 1092057:
            case 1092058:
            case 1092059:

            case 1122011:
            case 1122012:

            case 1302081:
            case 1312037:
            case 1322060:
            case 1332073:
            case 1332074:
            case 1372044:
            case 1382057:
            case 1402046:
            case 1412033:
            case 1422037:
            case 1432047:
            case 1442063:
            case 1452057:
            case 1462050:
            case 1472068:
            case 1482023:
            case 1492023:
            case 1342011:
            case 1532015:
            case 1522016:
                //raven.
                return true;
            default:
                return false;
        }
    }

    public static boolean isRing(int itemId) {
        return itemId >= 1112000 && itemId < 1113000;
    }// 112xxxx - pendants, 113xxxx - belts

    //if only there was a way to find in wz files -.-
    public static boolean isEffectRing(int itemid) {
        return isFriendshipRing(itemid) || isCrushRing(itemid) || isMarriageRing(itemid);
    }

    public static boolean isMarriageRing(int itemId) {
        switch (itemId) {
            case 1112803:
            case 1112806:
            case 1112807:
            case 1112809:

                return true;
        }
        return false;
    }

    public static boolean isFriendshipRing(int itemId) {
        switch (itemId) {
            case 1112800:
            case 1112801:
            case 1112802:
            case 1112810: //new
            case 1112811: //new, doesnt work in friendship?
            case 1112812: //new, im ASSUMING it's friendship cuz of itemID, not sure.
            case 1112816: //new, i'm also assuming
            case 1112817:

            case 1049000:
                return true;
        }
        return false;
    }

    public static boolean isCrushRing(int itemId) {
        switch (itemId) {
            case 1112001:
            case 1112002:
            case 1112003:
            case 1112005: //new
            case 1112006: //new
            case 1112007:
            case 1112012:
            case 1112013:
            case 1112015: //new

            case 1048000:
            case 1048001:
            case 1048002:
                return true;
        }
        return false;
    }

    public static int[] Equipments_Bonus = {1122017};

    public static int Equipment_Bonus_EXP(final int itemid) { // TODO : Add Time for more exp increase
        switch (itemid) {
            case 1122017:
                return 10;
        }
        return 0;
    }

    public static int[] blockedMaps = {180000001, 180000002, 109050000, 280030000, 240060200, 280090000, 280030001, 240060201, 950101100, 950101010, 272030400};
    //If you can think of more maps that could be exploitable via npc,block nao pliz!

    public static int getExpForLevel(int i, int itemId) {
        if (isReverseItem(itemId)) {
            return getReverseRequiredEXP(i);
        } else if (getMaxLevel(itemId) > 0) {
            return getTimelessRequiredEXP(i);
        }
        return 0;
    }

    public static int getMaxLevel(final int itemId) {
        Map<Integer, Map<String, Integer>> inc = MapleItemInformationProvider.getInstance().getEquipIncrements(itemId);
        return inc != null ? (inc.size()) : 0;
    }

    public static int getStatChance() {
        return 25;
    }

    public static MonsterStatus getStatFromWeapon(final int itemid) {
        switch (itemid) {
            case 1302109:
            case 1312041:
            case 1322067:
            case 1332083:
            case 1372048:
            case 1382064:
            case 1402055:
            case 1412037:
            case 1422041:
            case 1432052:
            case 1442073:
            case 1452064:
            case 1462058:
            case 1472079:
            case 1482035:
                return MonsterStatus.DARKNESS;
            case 1302108:
            case 1312040:
            case 1322066:
            case 1332082:
            case 1372047:
            case 1382063:
            case 1402054:
            case 1412036:
            case 1422040:
            case 1432051:
            case 1442072:
            case 1452063:
            case 1462057:
            case 1472078:
            case 1482036:
                return MonsterStatus.SPEED;
        }
        return null;
    }

    public static int getXForStat(MonsterStatus stat) {
        switch (stat) {
            case DARKNESS:
                return -70;
            case SPEED:
                return -50;
        }
        return 0;
    }

    public static int getSkillForStat(MonsterStatus stat) {
        switch (stat) {
            case DARKNESS:
                return 1111003;
            case SPEED:
                return 3121007;
        }
        return 0;
    }

    public final static int[] normalDrops = {
            4001009, //real
            4001010,
            4001011,
            4001012,
            4001013,
            4001014, //real
            4001021,
            4001038, //fake
            4001039,
            4001040,
            4001041,
            4001042,
            4001043, //fake
            4001038, //fake
            4001039,
            4001040,
            4001041,
            4001042,
            4001043, //fake
            4001038, //fake
            4001039,
            4001040,
            4001041,
            4001042,
            4001043, //fake
            4000164, //start
            2000000,
            2000003,
            2000004,
            2000005,
            4000019,
            4000000,
            4000016,
            4000006,
            2100121,
            4000029,
            4000064,
            5110000,
            4000306,
            4032181,
            4006001,
            4006000,
            2050004,
            3994102,
            3994103,
            3994104,
            3994105,
            2430007, //end
            4000164, //start
            2000000,
            2000003,
            2000004,
            2000005,
            4000019,
            4000000,
            4000016,
            4000006,
            2100121,
            4000029,
            4000064,
            5110000,
            4000306,
            4032181,
            4006001,
            4006000,
            2050004,
            3994102,
            3994103,
            3994104,
            3994105,
            2430007, //end
            4000164, //start
            2000000,
            2000003,
            2000004,
            2000005,
            4000019,
            4000000,
            4000016,
            4000006,
            2100121,
            4000029,
            4000064,
            5110000,
            4000306,
            4032181,
            4006001,
            4006000,
            2050004,
            3994102,
            3994103,
            3994104,
            3994105,
            2430007}; //end
    public final static int[] rareDrops = {
            2022179,
            2049100,
            2049100,
            2430144,
            2028062,
            2028061,
            2049301,
            2049401,
            2022326,
            2022193,
            2049000,
            2049001,
            2049002};
    public final static int[] superDrops = {
            2040804,
            2049400,
            2028062,
            2028061,
            2430144,
            2430144,
            2430144,
            2430144,
            2049100,
            2049100,
            2049100,
            2049100};

    public static int getSkillBook(final int job) {
        if (job >= 2210 && job <= 2218) {
            return job - 2209;
        }
        switch (job) {
            case 2310:
            case 3110:
            case 3210:
            case 3310:
            case 3510:
            case 570:
            case 2410:
            case 5110:
                return 1;
            case 2311:
            case 3111:
            case 3211:
            case 3311:
            case 3511:
            case 571:
            case 2411:
            case 5111:
                return 2;
            case 2312:
            case 3112:
            case 3212:
            case 3312:
            case 3512:
            case 572:
            case 2412:
            case 5112:
                return 3;
        }
        return 0;
    }

    public static int getSkillBook(final int job, final int level) {
        if (job >= 2210 && job <= 2218) {
            return job - 2209;
        }
        switch (job) {
            case 2300:
            case 2310:
            case 2311:
            case 2312:
            case 3100:
            case 3200:
            case 3300:
            case 3500:
            case 3110:
            case 3210:
            case 3310:
            case 3510:
            case 3111:
            case 3211:
            case 3311:
            case 3511:
            case 3112:
            case 3212:
            case 3312:
            case 3512:
                return (level <= 30 ? 0 : (level >= 31 && level <= 70 ? 1 : (level >= 71 && level <= 120 ? 2 : (level >= 120 ? 3 : 0))));
        }
        return 0;
    }

    public static int getSkillBookForSkill(final int skillid) {
        return getSkillBook(skillid / 10000);
    }

    public static int getLinkedMountItem(final int sourceid) {
        switch (sourceid % 1000) {
            case 1:
            case 24:
            case 25:
                return 1018;
            case 2:
            case 26:
                return 1019;
            case 3:
                return 1025;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                return (sourceid % 1000) + 1023;
            case 9:
            case 10:
            case 11:
                return (sourceid % 1000) + 1024;
            case 12:
                return 1042;
            case 13:
                return 1044;
            case 14:
                return 1049;
            case 15:
            case 16:
            case 17:
                return (sourceid % 1000) + 1036;
            case 18:
            case 19:
                return (sourceid % 1000) + 1045;
            case 20:
                return 1072;
            case 21:
                return 1084;
            case 22:
                return 1089;
            case 23:
                return 1106;
            case 29:
                return 1151;
            case 30:
            case 50:
                return 1054;
            case 33: //33 = hot air
                return 1932057;
            case 37: //独角兽比约骑宠
                return 1932084;
            case 38: //38 = speedy chariot
                return 1932088;
            case 39: //飞马
                return 1932089;
            case 31:
            case 51:
                return 1069;
            case 32:
                return 1138;
            case 46:
                return 1932084;
            case 45:
            case 47:
            case 48:
            case 49:
                return (sourceid % 1000) + 1009;
            case 52:
                return 1070;
            case 53:
                return 1071;
            case 54:
                return 1096;
            case 44:
            case 55:
                return 1101;
            case 56:
                return 1102;
            case 57:
            case 78:
                return 1932083;
            case 58:
                return 1118;
            case 59:
                return 1121;
            case 60:
                return 1122;
            case 61:
                return 1932071;
            case 62:
                return 1139;
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
                return (sourceid % 1000) + 1080;
            case 82: //水牛骑宠
                return 1932093;
            case 83: //兔车骑宠
                return 1932094;
            case 84: //超级兔子骑宠
                return 1932095;
            case 85:
            case 86:
            case 87:
                return (sourceid % 1000) + 928;
            case 88:
                return 1065;
            case 90: //印第安猪
                return 1932096;
            case 27:
                return 1932049; //airplane
            case 28:
                return 1932050; //airplane
            case 111: //猫头鹰
                return 1932038;
            case 112: //熊猫
                return 1932097;
            case 113: //企鹅
                return 1932098;
            case 114: //GO兔冒险
                return 1932099;
            case 115: //无辜水牛
                return 1932065;
            case 116: //玩具坦克
                return 1932066;
            case 117: //打豆豆机器人
                return 1932072;
            case 118: //莱格斯的豺犬
                return 1932078;
            case 119: //跑车
                return 1932080;
            case 120: //国庆纪念热气球
                return 1992015;
            case 121: //赤兔马
                return 1932092;
            case 124: //猫猫海贼船
                return 1932105;
            case 142: //好朋友坐骑
                return 1932112;
            case 181: //藏獒骑宠
                return 1932091;
            case 194:
                return 1932137;
            case 195:
                return 1932138;
            case 196:
                return 1932139;
            case 198:
                return 1932140;
            case 199:
                return 1932141;
            case 220:
                return 1932143;
            case 221:
                return 1932144;
            case 228:
                return 1932148;
            case 237:
                return 1932153;
            case 240:
                return 1932154;
            case 243:
                return 1932156;
            case 245:
                return 1932158;
            case 330:
                return 1992030;
            case 410:
                return 1992033;
        }
        return 0;
    }

    public static int getMountItem(final int sourceid, final MapleCharacter chr) {
        switch (sourceid) {
            case 30011109:
                return 1932051;
            case 30011159:
                return 1932085;
            case 5221006:
                return 1932000;
            case 33001001: //temp.
                if (chr == null) {
                    return 1932015;
                }
                switch (chr.getIntNoRecord(JAGUAR)) {
                    case 20:
                        return 1932030;
                    case 30:
                        return 1932031;
                    case 40:
                        return 1932032;
                    case 50:
                        return 1932033;
                    case 60:
                        return 1932036;
                }
                return 1932015;
            case 35001002:
            case 35120000:
                return 1932016;

            case 20021160:
                return 1932086;
            case 20021161:
                return 1932087;
        }
        if (!isBeginnerJob(sourceid / 10000)) {
            if (sourceid / 10000 == 8000 && sourceid != 80001000) { //todoo clean up
                final Skill skil = SkillFactory.getSkill(sourceid);
                if (skil != null && skil.getTamingMob() > 0) {
                    return skil.getTamingMob();
                } else {
                    final int link = getLinkedMountItem(sourceid);
                    if (link > 0) {
                        if (link < 10000) {
                            return getMountItem(link, chr);
                        } else {
                            return link;
                        }
                    }
                }
            }
            return 0;
        }
        switch (sourceid % 10000) {
            case 1013:
            case 1046:
                return 1932001;
            case 1015:
            case 1048:
                return 1932002;
            case 1016:
            case 1017:
            case 1027:
                return 1932007;
            case 1018:
                return 1932003;
            case 1019:
                return 1932005;
            case 1025:
                return 1932006;
            case 1028:
                return 1932008;
            case 1029:
                return 1932009;
            case 1030:
                return 1932011;
            case 1031:
                return 1932010;
            case 1033:
                return 1932013;
            case 1034:
                return 1932014;
            case 1035:
                return 1932012;
            case 1036:
                return 1932017;
            case 1037:
                return 1932018;
            case 1038:
                return 1932019;
            case 1039:
                return 1932020;
            case 1040:
                return 1932021;
            case 1042:
                return 1932022;
            case 1044:
                return 1932023;
            //case 1045:
            //return 1932030; //wth? helicopter? i didnt see one, so we use hog
            case 1049:
                return 1932025;
            case 1050:
                return 1932004;
            case 1051:
                return 1932026;
            case 1052:
                return 1932027;
            case 1053:
                return 1932028;
            case 1054:
                return 1932029;
            case 1063:
                return 1932034;
            case 1064:
                return 1932035;
            case 1065:
                return 1932037;
            case 1069:
                return 1932038;
            case 1070:
                return 1932039;
            case 1071:
                return 1932040;
            case 1072:
                return 1932041;
            case 1084:
                return 1932043;
            case 1089:
                return 1932044;
            case 1096:
                return 1932045;
            case 1101:
                return 1932046;
            case 1102:
                return 1932061;
            case 1106:
                return 1932048;
            case 1118:
                return 1932060;
            case 1115:
                return 1932052;
            case 1121:
                return 1932063;
            case 1122:
                return 1932064;
            case 1123:
                return 1932065;
            case 1128:
                return 1932066;
            case 1130:
                return 1932072;
            case 1136:
                return 1932078;
            case 1138:
                return 1932080;
            case 1139:
                return 1932081;
            //FLYING
            case 1143:
            case 1144:
            case 1145:
            case 1146:
            case 1147:
            case 1148:
            case 1149:
            case 1150:
            case 1151:
            case 1152:
            case 1153:
            case 1154:
            case 1155:
            case 1156:
            case 1157:
                return 1992000 + (sourceid % 10000) - 1143;
            default:
                return 0;
        }
    }

    public static boolean isKatara(int itemId) {
        return itemId / 10000 == 134;
    }

    public static boolean isDagger(int itemId) {
        return itemId / 10000 == 133;
    }

    public static boolean isApplicableSkill(int skil) {
        return (skil < 60000000 && (skil % 10000 < 8000 || skil % 10000 > 8006) && !isAngel(skil)) || skil >= 92000000 || (skil >= 80000000 && skil < 80010000); //no additional/decent skills
    }

    public static boolean isApplicableSkill_(int skil) { //not applicable to saving but is more of temporary
        for (int i : PlayerStats.pvpSkills) {
            if (skil == i) {
                return true;
            }
        }
        return (skil >= 90000000 && skil < 92000000) || (skil % 10000 >= 8000 && skil % 10000 <= 8003) || isAngel(skil);
    }

    public static boolean isTablet(int itemId) {
        return itemId / 1000 == 2047;
    }

    public static boolean isGeneralScroll(int itemId) {
        return itemId / 1000 == 2046;
    }

    public static int getSuccessTablet(final int scrollId, final int level) {
        if (scrollId % 1000 / 100 == 2) { //2047_2_00 = armor, 2047_3_00 = accessory
            switch (level) {
                case 0:
                    return 70;
                case 1:
                    return 55;
                case 2:
                    return 43;
                case 3:
                    return 33;
                case 4:
                    return 26;
                case 5:
                    return 20;
                case 6:
                    return 16;
                case 7:
                    return 12;
                case 8:
                    return 10;
                default:
                    return 7;
            }
        } else if (scrollId % 1000 / 100 == 3) {
            switch (level) {
                case 0:
                    return 70;
                case 1:
                    return 35;
                case 2:
                    return 18;
                case 3:
                    return 12;
                default:
                    return 7;
            }
        } else {
            switch (level) {
                case 0:
                    return 70;
                case 1:
                    return 50; //-20
                case 2:
                    return 36; //-14
                case 3:
                    return 26; //-10
                case 4:
                    return 19; //-7
                case 5:
                    return 14; //-5
                case 6:
                    return 10; //-4
                default:
                    return 7;  //-3
            }
        }
    }

    public static int getCurseTablet(final int scrollId, final int level) {
        if (scrollId % 1000 / 100 == 2) { //2047_2_00 = armor, 2047_3_00 = accessory
            switch (level) {
                case 0:
                    return 10;
                case 1:
                    return 12;
                case 2:
                    return 16;
                case 3:
                    return 20;
                case 4:
                    return 26;
                case 5:
                    return 33;
                case 6:
                    return 43;
                case 7:
                    return 55;
                case 8:
                    return 70;
                default:
                    return 100;
            }
        } else if (scrollId % 1000 / 100 == 3) {
            switch (level) {
                case 0:
                    return 12;
                case 1:
                    return 18;
                case 2:
                    return 35;
                case 3:
                    return 70;
                default:
                    return 100;
            }
        } else {
            switch (level) {
                case 0:
                    return 10;
                case 1:
                    return 14; //+4
                case 2:
                    return 19; //+5
                case 3:
                    return 26; //+7
                case 4:
                    return 36; //+10
                case 5:
                    return 50; //+14
                case 6:
                    return 70; //+20
                default:
                    return 100;  //+30
            }
        }
    }

    public static boolean isAccessory(final int itemId) {
        return (itemId >= 1010000 && itemId < 1040000) || (itemId >= 1122000 && itemId < 1153000) || (itemId >= 1112000 && itemId < 1113186);
    }

    public static boolean potentialIDFits(final int potentialID, final int newstate, final int i) {
        //first line is always the best
        //but, sometimes it is possible to get second/third line as well
        //may seem like big chance, but it's not as it grabs random potential ID anyway
        if (newstate == 20) {
            return (i == 0 || Randomizer.nextInt(10) == 0 ? potentialID >= 40000 : potentialID >= 30000 && potentialID < 60004); // xml say so
        } else if (newstate == 19) {
            return (i == 0 || Randomizer.nextInt(10) == 0 ? potentialID >= 30000 : potentialID >= 20000 && potentialID < 30000);
        } else if (newstate == 18) {
            return (i == 0 || Randomizer.nextInt(10) == 0 ? potentialID >= 20000 && potentialID < 30000 : potentialID >= 10000 && potentialID < 20000);
        } else if (newstate == 17) {
            return (i == 0 || Randomizer.nextInt(10) == 0 ? potentialID >= 10000 && potentialID < 20000 : potentialID < 10000);
        } else {
            return false;
        }
    }

    public static boolean optionTypeFits(final int optionType, final int itemId) {
        switch (optionType) {
            case 10: // weapons
                return isWeapon(itemId);
            case 11: // all equipment except weapons
                return !isWeapon(itemId);
            case 20: // all armors
                return !isAccessory(itemId) && !isWeapon(itemId);
            case 40: // accessories
                return isAccessory(itemId);
            case 51: // hat
                return itemId / 10000 == 100;
            case 52: // top and overall
                return itemId / 10000 == 104 || itemId / 10000 == 105;
            case 53: // bottom and overall
                return itemId / 10000 == 106 || itemId / 10000 == 105;
            case 54: // glove
                return itemId / 10000 == 108;
            case 55: // shoe
                return itemId / 10000 == 107;
            default:
                return true;
        }
    }

    public static int getNebuliteGrade(final int id) {
        if (id / 10000 != 306) {
            return -1;
        }
        if (id >= 3060000 && id < 3061000) {
            return 0;
        } else if (id >= 3061000 && id < 3062000) {
            return 1;
        } else if (id >= 3062000 && id < 3063000) {
            return 2;
        } else if (id >= 3063000 && id < 3064000) {
            return 3;
        }
        return 4;
    }

    public static final boolean isMountItemAvailable(final int mountid, final int jobid) {
        if (jobid != 900 && mountid / 10000 == 190) {
            switch (mountid) {
                case 1902000:
                case 1902001:
                case 1902002:
                    return isAdventurer(jobid);
                case 1902005:
                case 1902006:
                case 1902007:
                    return isKOC(jobid);
                case 1902015:
                case 1902016:
                case 1902017:
                case 1902018:
                    return isAran(jobid);
                case 1902040:
                case 1902041:
                case 1902042:
                    return isEvan(jobid);
            }

            if (isResist(jobid)) {
                return false; //none lolol
            }
        }
        if (mountid / 10000 != 190) {
            return false;
        }
        return true;
    }

    public static boolean isEvanDragonItem(final int itemId) {
        return itemId >= 1940000 && itemId < 1980000; //194 = mask, 195 = pendant, 196 = wings, 197 = tail
    }

    public static boolean isHeart(final int itemId) {
        return itemId / 10000 == 167;
    }

    public static boolean isMechequip(final int itemId) {
        return (itemId / 10000 == 161 || itemId / 10000 == 162 || itemId / 10000 == 163 || itemId / 10000 == 164 || itemId / 10000 == 165);
    }

    public static boolean isDragonequip(final int itemId) {
        return (itemId / 10000 == 194 || itemId / 10000 == 195 || itemId / 10000 == 196 || itemId / 10000 == 197);
    }

    public static boolean isDSsub(final int itemId) {
        return itemId / 1000 == 1099;
    }

    public static boolean canScroll(final int itemId) {
        return (itemId / 100000 != 19 && itemId / 100000 != 16) || isHeart(itemId) || isMechequip(itemId) || isDragonequip(itemId); //no mech/taming/dragon
    }

    public static boolean canHammer(final int itemId) {
        switch (itemId) {
            case 1122000:
            case 1122076: //ht, chaos ht
                return false;
        }
        if (!canScroll(itemId)) {
            return false;
        }
        return true;
    }

    public static int[] owlItems = new int[]{
            1082002, // work gloves
            2070005,
            2070006,
            1022047,
            1102041,
            2044705,
            2340000, // white scroll
            2040017,
            1092030,
            2040804};

    public static boolean ExceptQuest(final int questid) {
        switch (questid) {
            case 29020:
                return true;
        }
        return false;
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

    public static int getExpRate_Below10(final int job) {
//        if (GameConstants.isEvan(job)) {
//            return 1;
//        } else if (GameConstants.isAran(job) || GameConstants.isKOC(job) || GameConstants.isResist(job)) {
//            return 5;
//        }
//        return 10;
        return 1;
    }

    public static int getExpRate_Quest(final int level) {
        return (level >= 30 ? (level >= 70 ? (level >= 120 ? 10 : 5) : 2) : 1);
    }

    public static String getCashBlockedMsg(final int id) {
        switch (id) {
            case 5062000:
            case 5050000:
            case 5062001:
                //cube
                return "目前無法購買此商品。";
        }
        return "目前無法購買此商品。";
    }

    public static int getCustomReactItem(final int rid, final int original) {
        if (rid == 2008006) { //orbis pq LOL
            return (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 4001055);
            //4001056 = sunday. 4001062 = saturday
        } else {
            return original;
        }
    }

    public static int getJobNumber(int jobz) {
        int job = (jobz % 1000);
        if (job / 100 == 0 || isBeginnerJob(jobz)) {
            return 0; //beginner
        } else if ((job / 10) % 10 == 0 || job == 501) {
            return 1;
        } else {
            return 2 + (job % 10);
        }
    }

    public static boolean isBeginnerJob(final int job) {
        return job == 0 || job == 1 || job == 1000 || job == 2000 || job == 2001 || job == 3000 || job == 3001 || job == 2002 || job == 2003 || job == 5000 || job == 2004 || job == 4001 || job == 4002 || job == 6000 || job == 6001 || job == 3002;
    }

    public static boolean isAzwanMap(int mapId) {
        return mapId >= 262020000 && mapId < 262023000;
    }

    public static boolean isForceRespawn(int mapid) {
        switch (mapid) {
            case 103000800: //kerning PQ crocs
            case 925100100: //crocs and stuff
                return true;
            default:
                return mapid / 100000 == 9800 && (mapid % 10 == 1 || mapid % 1000 == 100);
        }
    }

    public static int getFishingTime(boolean vip, boolean gm) {
        return gm ? 1000 : (vip ? 240000 : 360000);
    }

    public static int getCustomSpawnID(int summoner, int def) {
        switch (summoner) {
            case 9400589:
            case 9400748: //MV
                return 9400706; //jr
            default:
                return def;
        }
    }

    public static boolean canForfeit(int questid) {
        switch (questid) {
            case 20000:
            case 20010:
            case 20015: //cygnus quests
            case 20020:
                return false;
            default:
                return true;
        }
    }

    public static double getAttackRange(MapleStatEffect def, int rangeInc) {
        double defRange = ((400.0 + rangeInc) * (400.0 + rangeInc));
        if (def != null) {
            defRange += def.getMaxDistanceSq() + (def.getRange() * def.getRange());
        }
        //rangeInc adds to X
        //400 is approximate, screen is 600.. may be too much
        //200 for y is also too much
        //default 200000
        return defRange + 120000.0;
    }

    public static double getAttackRange(Point lt, Point rb) {
        double defRange = (400.0 * 400.0);
        final int maxX = Math.max(Math.abs(lt == null ? 0 : lt.x), Math.abs(rb == null ? 0 : rb.x));
        final int maxY = Math.max(Math.abs(lt == null ? 0 : lt.y), Math.abs(rb == null ? 0 : rb.y));
        defRange += (maxX * maxX) + (maxY * maxY);
        //rangeInc adds to X
        //400 is approximate, screen is 600.. may be too much
        //200 for y is also too much
        //default 200000
        return defRange + 120000.0;
    }

    public static int getLowestPrice(int itemId) {
        switch (itemId) {
            case 2340000: //ws
            case 2531000:
            case 2530000:
                return 50000000;
        }
        return -1;
    }

    public static boolean isNoDelaySkill(int skillId) {
        return skillId == 5110001 || skillId == 21101003 || skillId == 15100004 || skillId == 33101004 || skillId == 32111010 || skillId == 2111007 || skillId == 2211007 || skillId == 2311007 || skillId == 32121003 || skillId == 35121005 || skillId == 35111004 || skillId == 35121013 || skillId == 35121003 || skillId == 22150004 || skillId == 22181004 || skillId == 11101002 || skillId == 13101002 || skillId == 24121000 || skillId == 22161005 || skillId == 12111007 || skillId == 31121005;
    }

    public static boolean isNoSpawn(int mapID) {
        return mapID == 809040100 || mapID == 925020010 || mapID == 925020011 || mapID == 925020012 || mapID == 925020013 || mapID == 925020014 || mapID == 980010000 || mapID == 980010100 || mapID == 980010200 || mapID == 980010300 || mapID == 980010020;
    }

    public static int getExpRate(int job, int def) {
        return def;
    }

    public static int getModifier(int itemId, int up) {
        if (up <= 0) {
            return 0;
        }
        switch (itemId) {
            case 2022459:
            case 2860179:
            case 2860193:
            case 2860207:
                return 130;
            case 2022460:
            case 2022462:
            case 2022730:
                return 150;
            case 2860181:
            case 2860195:
            case 2860209:
                return 200;
        }
        if (itemId / 10000 == 286) { //familiars
            return 150;
        }
        return 200;
    }

    public static short getSlotMax(int itemId) {
        switch (itemId) {
            case 4030003:
            case 4030004:
            case 4030005:
            case 2046052:
            case 2046134:
            case 2046385:
            case 2046503:  
            case 4000597:
                return 1;
            case 2049402:
                return 100;
            case 4000516:
            case 4001168:
            case 4031306:
            case 4031307:
            case 3993000:
            case 3993002:
            case 3993003:
                return 1000;
            case 5220010:
            case 5220013:
                return 1000;
            case 5220020:
                return 2000;
        }
        return 0;
    }

    public static boolean isDropRestricted(int itemId) {
        return itemId == 3012000 || itemId == 4030004 || itemId == 1052098 || itemId == 1052202;
    }

    public static boolean isPickupRestricted(int itemId) {
        return itemId == 4030003 || itemId == 4030004;
    }

    public static short getStat(int itemId, int def) {
        switch (itemId) {
            case 1002419:
                return 5;
            case 1002959:
                return 25;
            case 1142002:
                return 10;
            case 1122121:
                return 7;
        }
        return (short) def;
    }

    public static short getHpMp(int itemId, int def) {
        switch (itemId) {
            case 1122121:
                return 500;
            case 1142002:
            case 1002959:
                return 1000;
        }
        return (short) def;
    }

    public static short getATK(int itemId, int def) {
        switch (itemId) {
            case 1122121:
                return 3;
            case 1002959:
                return 4;
            case 1142002:
                return 9;
        }
        return (short) def;
    }

    public static short getDEF(int itemId, int def) {
        switch (itemId) {
            case 1122121:
                return 250;
            case 1002959:
                return 500;
        }
        return (short) def;
    }

    public static boolean isDojo(int mapId) {
        return mapId >= 925020100 && mapId <= 925023814;
    }

    public static int getPartyPlayHP(int mobID) {
        switch (mobID) {
            case 4250000:
                return 836000;
            case 4250001:
                return 924000;
            case 5250000:
                return 1100000;
            case 5250001:
                return 1276000;
            case 5250002:
                return 1452000;

            case 9400661:
                return 15000000;
            case 9400660:
                return 30000000;
            case 9400659:
                return 45000000;
            case 9400658:
                return 20000000;
        }
        return 0;
    }

    public static int getPartyPlayEXP(int mobID) {
        switch (mobID) {
            case 4250000:
                return 5770;
            case 4250001:
                return 6160;
            case 5250000:
                return 7100;
            case 5250001:
                return 7975;
            case 5250002:
                return 8800;

            case 9400661:
                return 40000;
            case 9400660:
                return 70000;
            case 9400659:
                return 90000;
            case 9400658:
                return 50000;
        }
        return 0;
    }

    public static int getPartyPlay(int mapId) {
        switch (mapId) {
            case 300010000:
            case 300010100:
            case 300010200:
            case 300010300:
            case 300010400:
            case 300020000:
            case 300020100:
            case 300020200:
            case 300030000:

            case 683070400:
            case 683070401:
            case 683070402:
                return 25;
        }
        return 0;
    }

    public static int getPartyPlay(int mapId, int def) {
        int dd = getPartyPlay(mapId);
        if (dd > 0) {
            return dd;
        }
        return def / 2;
    }

    public static boolean isHyperTeleMap(int mapId) {
        for (int i : hyperTele) {
            if (i == mapId) {
                return true;
            }
        }
        return false;
    }

    public static int getCurrentDate() {
        final String time = FileoutputUtil.CurrentReadable_Time();
        return Integer.parseInt(new StringBuilder(time.substring(0, 4)).append(time.substring(5, 7)).append(time.substring(8, 10)).append(time.substring(11, 13)).toString());
    }

    public static int getCurrentDate_NoTime() {
        final String time = FileoutputUtil.CurrentReadable_Time();
        return Integer.parseInt(new StringBuilder(time.substring(0, 4)).append(time.substring(5, 7)).append(time.substring(8, 10)).toString());
    }

    public static void achievementRatio(MapleClient c) {
        //PQs not affected: Amoria, MV, CWK, English, Zakum, Horntail(?), Carnival, Ghost, Guild, LudiMaze, Elnath(?) 
        switch (c.getPlayer().getMapId()) {
            case 240080100:
            case 240080600:
            case 920010000:
            case 930000000:
            case 930000100:
            case 910010000:
            case 922010100:
            case 910340100:
            case 925100000:
            case 926100000:
            case 926110000:
            case 921120005:
            case 932000100:
            case 923040100:
            case 921160100:
                c.sendPacket(CField.achievementRatio(0));
                break;
            case 930000200:
            case 922010200:
            case 922010300:
            case 922010400:
            case 922010401:
            case 922010402:
            case 922010403:
            case 922010404:
            case 922010405:
            case 925100100:
            case 926100001:
            case 926110001:
            case 921160200:
                c.sendPacket(CField.achievementRatio(10));
                break;
            case 240080200:
            case 930000300:
            case 910340200:
            case 922010500:
            case 922010600:
            case 925100200:
            case 925100201:
            case 925100202:
            case 926100100:
            case 926110100:
            case 921120100:
            case 932000200:
            case 923040200:
            case 921160300:
            case 921160310:
            case 921160320:
            case 921160330:
            case 921160340:
            case 921160350:
                c.sendPacket(CField.achievementRatio(25));
                break;
            case 930000400:
            case 926100200:
            case 926110200:
            case 926100201:
            case 926110201:
            case 926100202:
            case 926110202:
            case 921160400:
                c.sendPacket(CField.achievementRatio(35));
                break;
            case 240080300:
            case 910340300:
            case 922010700:
            case 930000500:
            case 925100300:
            case 925100301:
            case 925100302:
            case 926100203:
            case 926110203:
            case 921120200:
            case 932000300:
            case 240080700:
            case 240080800:
            case 923040300:
            case 921160500:
                c.sendPacket(CField.achievementRatio(50));
                break;
            case 240080400:
            case 910340400:
            case 922010800:
            case 930000600:
            case 925100400:
            case 926100300:
            case 926110300:
            case 926100301:
            case 926110301:
            case 926100302:
            case 926110302:
            case 926100303:
            case 926110303:
            case 926100304:
            case 926110304:
            case 921120300:
            case 932000400:
            case 923040400:
            case 921160600:
                c.sendPacket(CField.achievementRatio(70));
                break;
            case 910340500:
            case 922010900:
            case 930000700:
            case 920010800:
            case 925100500:
            case 926100400:
            case 926110400:
            case 926100401:
            case 926110401:
            case 921120400:
            case 921160700:
                c.sendPacket(CField.achievementRatio(85));
                break;
            case 240080500:
            case 922011000:
            case 922011100:
            case 930000800:
            case 920011000:
            case 920011100:
            case 920011200:
            case 920011300:
            case 925100600:
            case 926100500:
            case 926110500:
            case 926100600:
            case 926110600:
            case 921120500:
            case 921120600:
                c.sendPacket(CField.achievementRatio(100));
                break;
        }
    }

    public static boolean isAngel(int sourceid) {
        return isBeginnerJob(sourceid / 10000) && (sourceid % 10000 == 1085 || sourceid % 10000 == 1087 || sourceid % 10000 == 1090 || sourceid % 10000 == 1179);
    }

    public static int getRewardPot(int itemid, int closeness) {
        switch (itemid) {
            case 2440000:
                switch (closeness / 10) {
                    case 0:
                    case 1:
                    case 2:
                        return 2028041 + (closeness / 10);
                    case 3:
                    case 4:
                    case 5:
                        return 2028046 + (closeness / 10);
                    case 6:
                    case 7:
                    case 8:
                        return 2028049 + (closeness / 10);
                }
                return 2028057;
            case 2440001:
                switch (closeness / 10) {
                    case 0:
                    case 1:
                    case 2:
                        return 2028044 + (closeness / 10);
                    case 3:
                    case 4:
                    case 5:
                        return 2028049 + (closeness / 10);
                    case 6:
                    case 7:
                    case 8:
                        return 2028052 + (closeness / 10);
                }
                return 2028060;
            case 2440002:
                return 2028069;
            case 2440003:
                return 2430278;
            case 2440004:
                return 2430381;
            case 2440005:
                return 2430393;
        }
        return 0;
    }

    public static boolean isEventMap(final int mapid) {
        return (mapid >= 109010000 && mapid < 109050000) || (mapid > 109050001 && mapid < 109090000) || (mapid >= 809040000 && mapid <= 809040100);
    }

    public static boolean isMagicChargeSkill(final int skillid) {
        switch (skillid) {
            case 2121001: // Big Bang
            case 2221001:
            case 2321001:
//            case 22121000: //breath
//            case 22151001:
                return true;
        }
        return false;
    }

    public static void addBlockedNpc(int id) {
        if (!blockedNpcs.contains(id)) {
            blockedNpcs.add(id);
        }
    }

    public static void removeBlockedNpc(int id) {
        if (blockedNpcs.contains(id)) {
            blockedNpcs.remove(blockedNpcs.indexOf(id));
        }
    }

    public static boolean isBlockedNpc(int id) {
        return blockedNpcs.contains(id);
    }

    public static boolean isTeamMap(final int mapid) {
        return mapid == 109080000 || mapid == 109080001 || mapid == 109080002 || mapid == 109080003 || mapid == 109080010 || mapid == 109080011 || mapid == 109080012 || mapid == 109090300 || mapid == 109090301 || mapid == 109090302 || mapid == 109090303 || mapid == 109090304 || mapid == 910040100 || mapid == 960020100 || mapid == 960020101 || mapid == 960020102 || mapid == 960020103 || mapid == 960030100 || mapid == 689000000 || mapid == 689000010;
    }

    public static int getStatDice(int stat) {
        switch (stat) {
            case 2:
                return 30;
            case 3:
                return 20;
            case 4:
                return 15;
            case 5:
                return 20;
            case 6:
                return 30;
        }
        return 0;
    }

    public static int getDiceStat(int buffid, int stat) {
        if (buffid == stat || buffid % 10 == stat || buffid / 10 == stat) {
            return getStatDice(stat);
        } else if (buffid == (stat * 100)) {
            return getStatDice(stat) + 10;
        }
        return 0;
    }

    public static int getMPByJob(int job) {
        switch (job) {
            case 3100:
                return 30;
            case 3110:
                return 60;
            case 3111:
                return 90;
            case 3112:
                return 120;
        }
        return 30; // beginner or 3100
    }

    public static int getJudgmentStat(int buffid, int stat) {
        switch (stat) {
            case 1:
                return buffid == 20031209 ? 5 : 10;
            case 2:
                return buffid == 20031209 ? 10 : 20;
            case 3:
                return 2020;
            case 4:
                return 100;
        }
        return 0;
    }

    public static int getBuffDelay(int skill) {
        switch (skill) {
            case 24111002:
            case 24111003:
            case 24111005:
            case 24121004:
            case 24121008:
                return 1000;
        }
        return 0;
    }

    public static boolean isHaveQuickMoveMap(int mapId) {
        switch (mapId) {
            case 100000000:
            case 101000000:
            case 102000000:
            case 103000000:
            case 104000000:
            case 105000000:
            case 120000100:
            case 130000000:
            case 140000000:
            case 200000000:
            case 220000000:
            case 221000000:
            case 222000000:
            case 230000000:
            case 240000000:
            case 250000000:
            case 251000000:
            case 260000000:
            case 261000000:
            case 310000000:
                return true;
            default:
                return false;
        }
    }

    public static final int[] publicNpcIds = {9070004, 9071003, 9010022, 9000087, 9000088};
    public static final String[] publicNpcs = {"#c戰鬥廣場#", "#c怪物公園#", "#c次元之鏡#", "#c自由市場#", "#c梅斯特鎮#"};
    //questID; FAMILY USES 19000x, MARRIAGE USES 16000x, EXPED USES 16010x
    //dojo = 150000, bpq = 150001, master monster portals: 122600
    //compensate evan = 170000, compensate sp = 170001
    public static final int EXP_POTION = 7985;
    public static final int 精靈耳朵 = 7784;
    public static final int OMOK_SCORE = 122200;
    public static final int MATCH_SCORE = 122210;
    public static final int HP_ITEM = 122221;
    public static final int MP_ITEM = 122223;
    public static final int POT_ITEM = 122224;
    public static final int JAIL_TIME = 123455;
    public static final int JAIL_QUEST = 123456;
    public static final int REPORT_QUEST = 123457;
    public static final int ULT_EXPLORER = 111111;
    //codex = -55 slot
    //crafting/gathering are designated as skills(short exp then byte 0 then byte level), same with recipes(integer.max_value skill level)
    public static final int POKEMON_WINS = 122400;
    public static final int ENERGY_DRINK = 122500;
    public static final int HARVEST_TIME = 122501;
    public static final int PENDANT_SLOT = 122700;
    public static final int CURRENT_SET = 122800;
    public static final int BOSS_PQ = 150001;
    public static final int JAGUAR = 111112;
    public static final int DOJO = 150100;
    public static final int DOJO_RECORD = 150101;
    public static final int PARTY_REQUEST = 122900;
    public static final int PARTY_INVITE = 122901;
    public static final int QUICK_SLOT = 123000;
    public static final int ITEM_TITLE = 124000;

    private static final List<Balloon> lBalloon = Arrays.asList(
            new Balloon("歡迎來到" + ServerProperties.getProperty("net.sf.odinms.login.serverName"), 236, 122),
            new Balloon("禁止開外掛", 0, 276),
            new Balloon("遊戲愉快", 196, 263));

    public static List<Balloon> getBalloons() {
        return lBalloon;
    }

    public static boolean isSquadMap(int id) {
        switch (id) {
            case 105100400:
            case 105100300:
            case 280030000:
            case 280030001:
            case 240060200:
            case 240060201:
            case 270050100:
            case 802000111:
            case 802000211:
            case 802000311:
            case 802000411:
            case 802000611:
            case 802000711:
            case 802000801:
            case 802000802:
            case 802000803:
            case 802000821:
            case 802000823:
            case 211070100:
            case 211070101:
            case 211070110:
            case 551030200:
            case 271040100:
            case 262030300:
            case 262031300:
            case 272030400:
                return true;
        }
        return false;
    }

    public static boolean isOpScroll(int scrollId) {
        switch (scrollId) {
            case 2340000:
                return true;
        }
        return false;
    }

    public static boolean isAllScroll(int scrollId) {
        return scrollId / 10000 == 204;
    }

    public static boolean accscriptquest(final int questid) {
        switch (questid) {
            case 26000:
            case 20527:
                return true;
        }
        return false;
    }

    public static class Balloon {

        public int x;
        public int y;
        public String msg;

        public Balloon(String sMessage, int nX, int nY) {
            this.msg = sMessage;
            this.x = nX;
            this.y = nY;
        }
    }

    public static boolean isGMEquip(final int itemId) {
        switch (itemId) {
            case 1002140://維澤特帽
            case 1042003://維澤特西裝
            case 1062007://維澤特西褲
            case 1322013://維澤特手提包
                return true;
        }
        return false;
    }

    public static boolean isProfessionSkill(int skill) {
        switch (skill) {
            case 92000000:
            case 92010000:
            case 92020000:
            case 92030000:
            case 92040000:
            case 92050000:
                return true;
        }
        return false;
    }

    public static boolean isUsefulUltimateSkill(int job, int skill) {
        int sk = -1;
        if (job >= 100 && job <= 132) {
            sk = 1075; // 米哈逸的靈魂突刺
        } else if (job >= 200 && job <= 232) {
            sk = 1076; // 奧茲的火牢術
        } else if (job >= 300 && job <= 322) {
            sk = 1077; // 伊麗娜的疾風光速神弩
        } else if (job >= 400 && job <= 422) {
            sk = 1078; // 伊卡勒特吸血
        } else if (job >= 500 && job <= 522) {
            sk = 1079; // 鷹眼的鯨噬
        }
        return sk == skill && sk != -1;
    }

    public static boolean isUsefulUltimateLinkSkill(int job, int skill1) {
        int sk1 = -1, sk2 = -1;
        if (job >= 100 && job <= 132) {
            sk1 = 80;
        } else if (job >= 200 && job <= 232) {
            sk1 = 80;
        } else if (job >= 300 && job <= 322) {
            sk1 = 80;
        } else if (job >= 400 && job <= 422) {
            sk1 = 80;
        } else if (job >= 500 && job <= 522) {
            sk1 = 80;
        }
        return sk1 == skill1 && sk1 != -1;
    }

    public static boolean isUselssSkill(int skill) {
        switch (skill) {
            case 74:
            case 93:
            case 109:
            case 10:
            case 111:
            case 112:
            case 1006:
            case 10001006:
            case 20011006:
            case 97:
            case 100:
            case 10000097:
            case 10000100:
            case 20000097:
            case 20000100:
            case 20010097:
            case 20010100:
            case 30000097:
            case 30000100:
            case 99:
            case 10000099:
            case 20000099:

            case 20010099:
            case 30000099:
            case 103:
            case 10000103:
            case 20000103:
            case 20010103:
            case 30000103:
            case 104:
            case 10000104:
            case 20000104:
            case 20010104:
            case 30000104:
            case 1009:
            case 10001009:
            case 20001009:
            case 20011009:
            case 20021009:
            case 30001009:
            case 30011009:
            case 1010:
            case 10001010:
            case 20001010:
            case 20011010:
            case 20021010:
            case 30001010:
            case 30011010:
            case 1011:
            case 10001011:
            case 20001011:
            case 20011011:
            case 20021011:
            case 30001011:
            case 30011011:
            case 1013:
            case 10001014:
            case 20001046:
            case 20011046:
            case 20021013:
            case 30001013:
            case 30011013:
            case 10001016:
            case 20021015:
            case 30001015:
            case 30011015:
            case 1016:
            case 10001017:
            case 20001041:
            case 1020:
            case 10001020:
            case 20001020:
            case 20011020:
            case 20021020:
            case 30001020:
            case 30011020:
            case 1075:
            case 1196:
            case 1077:
            case 1078:
            case 1199:
            case 1079:
            case 1200:
            case 1198:
            case 1076:
            case 1197:
            case 1188:
            case 1189:
            case 10001188:
            case 10001189:
            case 20001188:
            case 20001189:
            case 20011188:
            case 20011189:
            case 30001188:
            case 30001189:
            case 80001125:
            case 80001126:
            case 1113:
            case 1114:
            case 10001113:
            case 10001114:
            case 20001113:
            case 20001114:
            case 20011113:
            case 20011114:
            case 30001113:
            case 30001114:
            case 10001098:
            case 20001098:
            case 20011098:
            case 20021098:
            case 30011098:
            case 1105:
            case 10001105:
            case 20001105:
            case 20011105:
            case 20021105:
            case 30001105:
            case 30011105:
            case 4000010:
            case 23000004:
            case 1014:
            case 1015:
            case 20021014:
            case 30001014:
            case 30011014:
            case 20020097:
            case 30010097:
            case 20020099:
            case 30010099:
            case 20020100:
            case 30010100:
            case 20020103:
            case 30010103:
            case 20020104:
            case 30010104:
            case 20021161:
            case 30011170:
            case 30011169:
            case 30011168:
            case 30011167:
            case 30010166:
            case 30010184:
            case 30010183:
            case 30010186:
            case 1007:
            case 10001007:
            case 20001007:
            case 20011007:
            case 20021007:
            case 30001007:
            case 30011007:
                return true;
        }
        return false;
    }

    public static short getSummonAttackDelay(final int id) {
        switch (id) {
            case 1196:
            case 1197:
            case 1198:
            case 1199:
            case 1200:
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
            case 2221005: // 召喚火魔
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
            case 24121000:
                ret = true;
                break;
            default:
                break;
        }
        return ret;
    }

    public static boolean notdrop(int itemid) {
        switch (itemid) {
            case 2340000:
            case 2531000:
            case 3993002:
            case 4000038:
            case 4000516:
            case 4001126:
            case 4001168:
                return true;
        }
        return false;
    }

    public static boolean nottrade(int itemid) {
        switch (itemid) {
            case 4000516:
            case 4000038:
                return true;
        }
        return false;
    }

    public static int getMountSkill(int sourceid, MapleCharacter chr) {
        switch (sourceid) {
            case 33001007:
                switch (chr.getIntNoRecord(111112)) {
                    case 20:
                        return 33001007;
                    case 30:
                        return 33001008;
                    case 40:
                        return 33001009;
                    case 50:
                        return 33001010;
                    case 60:
                        return 33001011;
                    case 70:
                        return 33001012;
                    case 80:
                        return 33001013;
                    case 90:
                        return 33001014;
                    case 100:
                        return 33001015;
                }
        }
        return sourceid;
    }

    public static boolean exItemGather(int itemId) {
        return itemId >= 4330000 && itemId <= 4330019;
    }

    public static int getStealSkill(int job) {
        switch (job) {
            case 1:
                return 24001001;
            case 2:
                return 24101001;
            case 3:
                return 24111001;
            case 4:
                return 24121001;
        }
        return 0;
    }

    public static int getNumSteal(int jobNum) {
        switch (jobNum) {
            case 1:
            case 2:
                return 4;
            case 3:
                return 3;
            case 4:
                return 2;
        }
        return 0;
    }

    public static boolean canSteal(Skill skil) {
        return skil != null && !skil.isMovement() && !isLinkedAttackSkill(skil.getId()) && skil.getId() % 10000 >= 1000 && getJobNumber(skil.getId() / 10000) > 0 && !isDualBlade(skil.getId() / 10000) && !isCannon(skil.getId() / 10000) && !isJett(skil.getId() / 10000) && skil.getId() < 8000000 && skil.getEffect(1) != null && skil.getEffect(1).getSummonMovementType() == null && !skil.getEffect(1).isUnstealable();
    }

    public static boolean isLinkedAttackSkill(final int id) {
        return getLinkedAttackSkill(id) != id;
    }

    public static final int getLinkedAttackSkill(final int id) {
        switch (id) {
            case 36121013:
            case 36121014:
                return 36121002;
            case 21110015:
            case 21110007:
            case 21110008:
                return 21110002;
            case 21000006:
                return 21000002;
            case 21120015:
            case 21120009:
            case 21120010:
                return 21120002;
            case 4321001:
                return 4321000;
            case 33101008:
                return 33101004;
            case 35101009:
            case 35101010:
                return 35100008;
            case 35121013:
                return 35120013;
            case 35121011:
                return 35121009;
            case 35111009:
            case 35111010:
                return 35111001;
            case 35100004:
                return 35101004;
            case 32001007:
            case 32001008:
            case 32001009:
            case 32001010:
            case 32001011:
                return 32001001;
            case 5300007:
                return 5301001;
            case 5320011:
                return 5321004;
            case 23101007:
                return 23101001;
            case 23111010:
            case 23111009:
                return 23111008;
            case 31001006:
            case 31001007:
            case 31001008:
                return 31000004;
            case 27120211:
                return 27121201;
            case 61001004:
            case 61001005:
            case 61110212:
            case 61120219:
                return 61001000;
            case 61110211:
            case 61120007:
            case 61121217:
                return 61101002;
            case 61111215:
                return 61001101;
            case 61111217:
                return 61101101;
            case 61111216:
                return 61101100;
            case 61111219:
                return 61111101;
            case 61111113:
            case 61111218:
                return 61111100;
            case 61121201:
                return 61121100;
            case 61121203:
                return 61121102;
            case 61110009:
                return 61111003;
            case 61121116:
                return 61121104;
            case 61121223:
                return 61121221;
            case 61121221:
                return 61121104;
            case 65101006:
                return 65101106;
            case 65121007:
            case 65121008:
                return 65121101;
            case 61111220:
                return 61121105;
            case 65111007:
                return 65111100;
            case 4100012:
                return 4100011;
            case 24121010:
                return 24121003;
            case 24111008:
                return 24111006;
            case 5001008:
                return 5001005;
            case 61121053:
            case 61120008:
                return 61111008;
            case 51100006:
                return 51101006;
            case 31011004:
            case 31011005:
            case 31011006:
            case 31011007:
                return 31011000;
            case 31201007:
            case 31201008:
            case 31201009:
            case 31201010:
                return 31201000;
            case 31211007:
            case 31211008:
            case 31211009:
            case 31211010:
                return 31211000;
            case 31221009:
            case 31221010:
            case 31221011:
            case 31221012:
                return 31221000;
            case 31211011:
                return 31211002;
            case 31221014:
                return 31221001;
            case 25100010:
                return 25100009;
            case 25120115:
                return 25120110;
            case 36101008:
            case 36101009:
                return 36101000;
            case 36111010:
            case 36111009:
                return 36111000;
            case 36121011:
            case 36121012:
                return 36121001;
            case 35100009:
                return 35100009;
            case 2121055:
                return 2121052;
            case 11121055:
                return 11121052;
            case 1120017:
                return 1121008;
            case 25000003:
                return 25001002;
            case 25000001:
                return 25001000;
            case 25100001:
                return 25101000;
            case 25110001:
            case 25110002:
            case 25110003:
                return 25111000;
            case 25120001:
            case 25120002:
            case 25120003:
                return 25121000;
            case 95001000:
                return 3111013;
            case 4210014:
                return 4211006;
            case 101000102:
                return 101000101;
            case 14101021:
                return 14101020;
            case 14111021:
                return 14111020;
            case 14111023:
                return 14111022;
            case 14121002:
                return 14121001;
            case 12120011:
                return 12121001;
            case 12120012:
                return 12121003;
            case 101000202:
                return 101000201;
            case 101100202:
                return 101100201;
            case 101110201:
                return 101110200;
            case 101110204:
                return 101110203;
            case 101120101:
                return 101120100;
            case 101120103:
                return 101120102;
            case 101120105:
            case 101120106:
                return 101120104;
            case 101120203:
                return 101120202;
            case 101120205:
            case 101120206:
                return 101120204;
            case 101120200:
                return 101121200;
            case 41001005:
            case 41001004:
                return 41001000;
            case 41101009:
            case 41101008:
                return 41101000;
            case 41111012:
            case 41111011:
                return 41111000;
            case 41001000:
                return 41001002;
            case 41001002:
            case 41121012:
            case 41121011:
                return 41121000;
            case 42001006:
            case 42001005:
                return 42001000;
            case 42001007:
                return 42001002;
            case 42100010:
                return 42101001;
            case 33101006:
            case 33101007:
                return 33101005;
            case 35001001:
                return 35101009;
            case 42111011:
                return 42111000;
        }
        return id;
    }

    public static boolean isDualBlade(final int job) {
        return (job >= 430 && job <= 434);
    }

    public static boolean isJett(final int job) {
        return job == 508 || (job / 10 == 57);
    }

    public static boolean is特殊劇情地圖(final int mapid) {
        switch (mapid) {
            case 915000300:
                return true;
        }
        return false;
    }

    public static int getSkillBookBySkill(final int skillId) {
        return getSkillBookByJob(skillId / 10000, skillId);
    }

    public static int getSkillBookByJob(int job) {
        return getSkillBookByJob(job, 0);
    }

    public static int getSkillBookByJob(final int job, final int skillId) {
        if (MapleJob.isBeginner(job)) {
            return 0;
        }
        if (isSeparatedSp(job)) {
            return MapleJob.getJobGrade(job) - 1;
        }
        return 0;
    }
}
