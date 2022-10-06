var status = 0;
var aircraft = [80001027, 80001028];
var msg = "";
var sel;

function start() {
	status = -1;
	boat = cm.getEventManager("Boats");
	action(1, 0, 0);
}

function action(mode, type, selection) {
	status++;
	if (mode == 0) {
		cm.sendNext("等你考慮好再來找我。");
		cm.dispose();
		return;
	}
	if (status == 0) {
		cm.sendSimple("如果擁有飛機的話，就可以用飛機飛到其他地方也不用等船，用飛機飛行如何呢？但如果用飛機飛行的話，要支付金額5000楓幣.\r\n\r\n#b#L0#用飛機飛行#k#r(5000楓幣)#l\r\n#b#L1#搭船#l");
	} else if (status == 1) {
		if (selection == 0) {
			status = 10;
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
		} else if (selection == 1) {
			if (boat == null) {
				cm.sendNext("找不到腳本請聯繫GM！");
				cm.dispose();
			} else if (boat.getProperty("entry").equals("true")) {
				cm.sendYesNo("你想要搭船？？");
			} else if (boat.getProperty("entry").equals("false") && boat.getProperty("docked").equals("true")) {
				cm.sendNext("很抱歉本班船準備開走,乘坐時間表可以通過售票展台查看.");
				cm.dispose();
			} else {
				cm.sendNext("很抱歉本班船已經走了,乘坐時間表可以通過售票展台查看.");
				cm.dispose();
			}
		}
	} else if (status == 2) {
		if (!cm.haveItem(4031047)) {
			cm.sendNext("不! 你沒有#b#t4031047##k 所以我不能放你走!");
		} else {
			cm.gainItem(4031047, -1);
			cm.warp(200000112, 0);
		}
		cm.dispose();
	} else if (status == 11) {
		sel = selection;
		var skillid = aircraft[sel];
		if (cm.getPlayer().getMeso() >= 5000) {
			cm.gainMeso(-5000);
			cm.useSkill(skillid, 1);
			switch (sel) {
			case 0:
				cm.warpBack(200110001, 104020110, 420);
				break;
			case 1:
				cm.warpBack(200110001, 104020110, 300);
				break;
			}
		} else {
			cm.sendNext("楓幣不夠？要搭飛機需要費用呢......");
		}
		cm.dispose();
		}
}
