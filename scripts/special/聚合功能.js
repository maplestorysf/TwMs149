/* global cm */
load('nashorn:mozilla_compat.js');

var status = -1;
var sel;

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		status--;
	}
	if (mode == 0) {
		cm.dispose();
		return;
	}
	if (status == 0) {
		cm.sendSimple(cm.getChannelServer().getServerName() + "管理員為您服務，請問你想做什麼呢？\r\n" +
			"\r\n-------玩家指令區-------\r\n" +
			"#L1#經驗歸零(修復經驗假死)#l\r\n" +
			"#L2#傳送訊息給GM#l\r\n" +
			"#L3#存檔#l\r\n" +
			"#L4#參加系統活動#l\r\n" +
			"#L5#補技能#l\r\n" +
			"#L6#成就查看#l\r\n" +
			"#L7#改名系統#l\r\n" +
			"#L8#test#l\r\n" +
			"#L9#阿斯旺介面#l\r\n");
	} else if (status == 1) {
		sel = selection;
		switch (sel) {
		case 1: {
				cm.dispose();
				cm.processCommand("@expfix");
				break;
			}
		case 2: {
				CGM(mode, type, selection);
				break;
			}
		case 3: {
				cm.dispose();
				cm.processCommand("@存檔");
				break;
			}
		case 4: {
				openNpc(9000000);
				break;
			}
		case 5: {
				openNpc(9000000, "補技能");
				break;
			}
		case 6: {
				openNpc(9000000, "ViewAchievement");
				break;
			}
		case 7: {
				openNpc(9000000, "改名");
				break;
			}
		case 8: {
				cm.sendLockUI(1);
				cm.sendMoveScreen(1000, 0, 1000);
				cm.sendDelay(5000);
				cm.sendLockUI(0);
				cm.sendResetScreen();
				cm.dispose();
				break;
			}
		case 9: {
				openNpc(9000000, "阿斯旺");
				break;
			}
		default: {
				cm.sendOk("此功能未完成");
				cm.dispose();
				break;
			}
		}
	}

}

function CGM(mode, type, selection) {
	if (mode === 1) {
		status++;
	} else if (mode === 0) {
		status--;
	}

	var i = -1;
	if (status <= i++) {
		cm.dispose();
	} else if (status === i++) {
		cm.sendGetText("請輸入你要對GM傳送的訊息");
	} else if (status === i++) {
		var text = cm.getText();
		if (text === null || text === "") {
			cm.sendOk("並未輸入任何內容.");
			cm.dispose();
			return;
		}
		cm.dispose();
		cm.processCommand("@CGM " + text);
	} else {
		cm.dispose();
	}
}

function openNpc(npcid) {
	openNpc(npcid, null);
}

function openNpc(npcid, script) {
	var mapid = cm.getMapId();
	cm.dispose();
	if (cm.getPlayerStat("LVL") < 10) {
		cm.sendOk("你的等級不能小於10等.");
	} else if (
		cm.hasSquadByMap() ||
		cm.hasEventInstance() ||
		cm.hasEMByMap() ||
		mapid >= 990000000 ||
		(mapid >= 680000210 && mapid <= 680000502) ||
		(mapid / 1000 === 980000 && mapid !== 980000000) ||
		mapid / 100 === 1030008 ||
		mapid / 100 === 922010 ||
		mapid / 10 === 13003000) {
		cm.sendOk("你不能在這裡使用這個功能.");
	} else {
		if (script == null) {
			cm.openNpc(npcid);
		} else {
			cm.openNpc(npcid, script);
		}
	}
}
