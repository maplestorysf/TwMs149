/* 轉生後120等直接學得四轉技能 By Kodan*/

var status = -1;
var skilltype = -1;
var job = -1;
var debug = false;
var msg = "";
var teachskill = [
	// 劍士
	[
		[1000006], [11000005], [1100009], [11100007], [1110009], [1120012]		
	],
	[
		[1220013], [1220006], [1220005], [1000006], [1210001], [1200009]
	],
	[
		[1310000], [1310009], [1320006], [13100008] ,[1000006],[1300009]
	],
	// 法師
	[
		[2100007], [2110001], [2110009]
	],
    [
        [2200007], [2210001], [2210009]
	],
	[
	    [2310010], [2300007]
	],
	// 弓箭手
	[
		[3100006], [31000003], [31100005], [3000001], [3101003], [3110007],[3120011], [3110007]
	],
	[
	    [3200006], [3201003], [3210007], [3220009], [3210007], [3000001]
	],
	// 盜賊
	[
	    [4110000], [4100006], [4120002], [4100006],  [4120002]
	],
	[
	    [4220002], [4200006], [4220002]
	],
	// 海盜
	[
		[5000000], [5100009] 
	],
	// 聖魂騎士
	[
		[1100009], [11100007], [12110001], [12110000]
	],
	// 烈焰巫師
	[
		[12100008], [12110001], [12110000] 
	],
	// 破風使者
	[
		[13000000], [13110008] 
	],
	// 暗夜行者
	[
		[14100010], [14110003]
	],
	// 閃雷悍將
	[
		[15000000], [15100007]
	],
	// 狂狼勇士
	[
		[21100008], [21110010], [21110000], [21120004]
	],
	// 重炮手
	[
		[5300008], [5310007]
	],
	// 影舞者
	[
	    [4310004], [4330007], [4341006], [4340010]
	],	
	// 精靈遊俠
	[
	    [23000003], [23000001], [23120010], [20020112]
	],
	// 煉獄巫師
	[
	    [32100007], [32100006], [32120009] 
	],
	// 狂豹獵人
	[
	    [33100010], [33120010]
	],
	// 機甲
	[
	    [35100011] 
	],
	// 惡魔殺手
	[
	    [31120009], [31000002], [31100006], [31110007], [31100005]
	],
];  

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	var lv = cm.getPlayer().getLevel();
	var gm = cm.getPlayer().isGM();
	if (mode == 1) {
		status++;
	} else if (mode == 0) {
		status--;
	} else {
		cm.dispose();
		return;
	}
	if (status == 0) {
		if (debug && !gm) {
			cm.sendNext("NPC維護中。");
			cm.dispose();
			return;
		}
		if (lv > 149) {
			cm.sendYesNo("是否要領取技能全滿??");
		} else {
			cm.sendNext("資格不符合，無法領取。");
			cm.dispose();
			return;
		}
	} else if (status == 1) {
		job = cm.getPlayer().getJob();
		skilltype = changeJob(job);
		if (skilltype == -1) {
			cm.sendNext("請回報管理員，錯誤的職業型態:" + job);
			cm.dispose();
			return;
		}
		if (debug) {
			for (var i = 0; i < teachskill[skilltype].length; i++) {
				msg += "\r\nskill: #s" + teachskill[skilltype][i][0] + "# #q" + teachskill[skilltype][i][0] + "# skilllevel:" + teachskill[skilltype][i][1] + "\r\n";
			}
			cm.sendSimple(msg);
		} else {
			for (var i = 0; i < teachskill[skilltype].length; i++) {
				var skillid = teachskill[skilltype][i][0];
				var skilllevel = Packages.client.SkillFactory.getSkill(skillid).getMaxLevel();
				cm.teachSkill(skillid, skilllevel);
			}
			cm.sendSimple("已經完成技能全滿");
		}
		cm.dispose();
	}
}

function changeJob(jobid) {
	switch (jobid) {
	case 112:
		return 0;
	case 122:
		return 1;
	case 132:
		return 2;
	case 212:
		return 3;
	case 222:
		return 4;
	case 232:
		return 5;
	case 312:
		return 6;
	case 322:
		return 7;
	case 412:
		return 8;
	case 422:
		return 9;
	case 512:
		return 10;
	case 522:
		return 11;
	case 1112:
		return 12;
	case 1212:
		return 13;
	case 1312:
		return 14;
	case 1412:
		return 15;
	case 1512:
		return 16;
	case 2100:
	case 2101:
	case 2111:
	case 2112:
		return 17;
	case 532:
	    return 18;
	case 434:
	    return 19;
	case 2312:
	    return 20;
	case 3212:
	    return 21;
	case 3312:
	    return 22;
	case 3512:
	    return 23;
	case 3112:
		return 24;
	    default:
		return -1;
	}
}
