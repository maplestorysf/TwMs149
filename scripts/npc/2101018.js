/* 納希沙漠競技場 入口NPC */

var status = -1;
var minlv = 50;
var maxlv = 200;

function start() {
	if ((cm.getPlayer().getLevel() < minlv || cm.getPlayer().getLevel() > maxlv) && !cm.getPlayer().isGM()) {
		cm.sendNext("您此等級不符合參加納希沙漠競技場\r\n最低需求" + minlv + "等、最高" + maxlv+ "等");
		cm.dispose();
		return;
	}
	action(1, 0, 0);
}

function action(mode, type, selection) {
	status++;
	if (status == 2) {
		cm.saveLocation("ARIANT");
		cm.warp(980010000, 3);
		cm.dispose();
	}
	if (mode != 1) {
		if (mode == 0 && type == 0)
			status -= 2;
		else {
			cm.dispose();
			return;
		}
	}
	if (status == 0)
		cm.sendYesNo("我是納希沙漠的親衛隊-隊長，目前楓之谷內正在舉辦#b納希沙漠的競技場#k是否有興趣參加??");
	else if (status == 1)
		cm.sendNext("好的，現在我將把您傳送到戰場等候室，祝您好運！");
}
