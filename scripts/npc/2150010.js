var status = -1;
var aircraft = [80001027, 80001028];
var msg = "";
var sel;

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 0) {
		cm.sendOk("等你考慮好再來找我吧!");
		cm.dispose();
	}
	if (mode == 1) {
		status++;
	} else {
		status--;
	}
	if (status == 0) {
		cm.sendSimple("如果擁有飛機的話，就可以用飛機飛到其他地方也不用等船，用飛機飛行如何呢？但如果用飛機飛行的話，要支付金額5000楓幣.\r\n\r\n#b#L0#用飛機飛行#k#r(5000楓幣)#l\r\n#b#L1#搭船#r(3000楓幣)#l");
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
			if (cm.getPlayer().getMeso() >= 3000) {
				cm.gainMeso(-3000);
				cm.warpBack(200090600,310000010,600);
			} else {
				cm.sendNext("您的錢不夠阿...");
			}
			cm.dispose();
		}
	} else if (status == 11) {
		sel = selection;
		var skillid = aircraft[sel];
		if (cm.getPlayer().getMeso() >= 5000) {
			cm.gainMeso(-5000);
			cm.useSkill(skillid, 1);
			switch (sel) {
			case 0:
				cm.warpBack(200110070, 310000010, 420);
				break;
			case 1:
				cm.warpBack(200110070, 310000010, 300);
				break;
			}
		} else {
			cm.sendNext("楓幣不夠？要搭飛機需要費用呢......");
		}
		cm.dispose();
	}
}
