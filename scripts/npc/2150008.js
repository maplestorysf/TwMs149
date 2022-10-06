/* 雷德拓重構 by Kodan*/
var menu = new Array("維多利亞港", "天空之城");
var cost = new Array(1500, 3000);
var display = "";
var sel;
var aircraft = [80001027, 80001028];
var msg = "";
var flysel;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 0 && status == 0) {
		cm.dispose();
		return;
	} else if (mode == 0) {
		cm.sendNext("改變想法隨時跟我搭話吧。");
		cm.dispose();
		return;
	}
	status++;
	
	if (cm.isQuestActive(23121)) {
		cm.sendOk("任務完成");
		cm.forceCompleteQuest(23121);
		cm.dispose();
		return;
	}
	if (status == 0) {
		for (var i = 0; i < menu.length; i++) {
			display += "\r\n#L" + i + "##b" + menu[i] + "(" + cost[i] + " 楓幣)#k";
		}
		cm.sendSimple("您好我是#p2150008#有什麼我能為您服務的呢?? \r\n#L100##r搭飛機#k#l"+display);
	} else if (status == 1) {
		sel = selection;
		if (sel == 100) {
			status = 10;
			display = "";
			for (var i = 0; i < menu.length; i++) {
				display += "\r\n#L" + i + "##b" + menu[i] + "#r(5000楓幣)#k";
			}
			cm.sendNext("那麼你想搭飛機去哪裡呢??"+display);
		} else {
			cm.sendYesNo("你確定要去 #b" + menu[sel] + "#k ？ 那麼你要付給我 #b" + cost[sel] + " 楓幣#k。");
		}
	} else if (status == 2) {
		if (cm.getPlayer().getMeso() >= cost[sel]) {
			cm.gainMeso(-cost[sel]);
			switch(sel) {
				case 0:
					cm.warpBack(200090710, 104020130, 600);
				break;
				case 1:
					cm.warpBack(200090610, 200000170, 600)
				break;
			}
		} else {
			cm.sendOk("您確定您有錢嗎.......??");
		}
		cm.dispose();
	} else if (status == 11) {
		sel = selection;
		var x = 0;
		msg = "想用哪種飛機飛行呢?";
		for (var i = 0; i < aircraft.length; i++) {
			if (cm.hasSkill(aircraft[i])) {
					msg += "\r\n#L" + i + "# #b#q" + aircraft[i] + "#";
					x++;
				}
			}
			if (x == 0) {
				cm.sendNext("沒有飛機還敢來搭??");
				cm.dispose();
				return;
			}
			cm.sendNext(msg);
	} else if (status == 12) {
		flysel = selection;
		var two = 0;
		var mapid = cm.getMapId();
		var skillid = aircraft[flysel];
		if (cm.getPlayer().getMeso() >= 5000) {
			cm.gainMeso(-5000);
			cm.useSkill(skillid, 1);
			if (sel == 0) {
				switch (flysel) {
				case 0:
					cm.warpBack(200110071, 104020130, 420);
					break;
				case 1:
					cm.warpBack(200110071, 104020130, 300);
					break;
				}
			} else if (sel == 1) {
				switch (flysel) {
				case 0:
					cm.warpBack(200110050, 200000170, 420);
					break;
				case 1:
					cm.warpBack(200110050, 200000170, 300);
					break;
				}
			}
		} else {
			cm.sendNext("楓幣不夠？要搭飛機需要費用呢......");
		}
		cm.dispose();
	}
}