var status = -1;
var times;

function start(mode, type, selection) {
	if (mode == -1) {
		qm.dispose();
	} else {
		status++;
		if (status == 0) {
			if (mode == 1) {
				var hairtimes = qm.getQuestRecord(29020);
				var time = parseInt(hairtimes.getCustomData());
				if (hairtimes.getCustomData() == null) {
					hairtimes.setCustomData("0");
				}
				if (time >= 50) {
					if (qm.canHold(1142122, 1)) {
						qm.gainItem(1142122, 1);
						qm.forceCompleteQuest();
						qm.worldMessage(6, "[勳章系統] " + qm.getPlayer().getName() + " 完成了多樣變髮師任務!");
					} else {
						qm.sendNext("請確認是否裝備欄位已滿。");
					}
				} else {
					times = hairtimes.getCustomData() == null ? "0" : hairtimes.getCustomData();
					qm.sendNext("您好像還沒去找弓箭手村的 #p1012103# 完成50次的呢!\r\n目前已經完成 "+ times + "/50 次");
				}
			}
			qm.dispose();
		} else {
			qm.dispose();
		}
	}
}

function end(mode, type, selection) {}
