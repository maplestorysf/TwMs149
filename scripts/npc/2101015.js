var status = -1;
var item = 4031868;
var count = 0;
var round = 200;

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	(mode == 1 ? status++ : status--);
	if (status == 0) {
		cm.sendNext("我是#p2101015# 您好，感謝您參加我舉辦的納西沙漠競技場活動，很好玩吧呵呵，如果您玩到了"+round+"場的話我將會給您一些小獎勵~");
	} else if (status == 1) {
		var cmp = cm.getPlayer().getOneInfo(1300, "cmp");
		if (cm.haveItem(3010018, 1)) {
			cm.sendOk("做好了#t3010018#.");
		} else if (!cm.canHold(3010018, 1)) {
			cm.sendOk("請空出一些裝飾欄空間。");
		} else if (cmp != null && parseInt(cmp) >= round) {
			if (cm.getPlayer().getOneInfo(1300, "have") == null || cm.getPlayer().getOneInfo(1300, "have").equals("0")) {
				cm.gainItem(3010018, 1, true);
			} else {
				cm.sendOk("你已經有#t3010018#了.");
			}
		} else {
			cm.sendOk("你還沒有做"+round+"次PQ 目前做了: " + (cmp == null ? "0" : cmp) + "次");
		}
		cm.dispose();
	}
}
