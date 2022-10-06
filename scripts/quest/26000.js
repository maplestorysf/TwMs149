var status = -1;

function start(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		qm.dispose();
		return;
	}
	if (qm.getGuild().getLevel() < 1 || !qm.getGuild().hasSkill(91000006)) {
		qm.dispose();
		return;
	}
	if (status == 0) {
		qm.sendYesNo("來囉~公會補給品來支援了，當您公會等級上升的時候，能在獲得更多的!!");
	} else {
		if (!qm.canHold(2002037, qm.getGuild().getLevel() * 20)) {
			qm.sendOk("請空出一些消耗欄位。");
		} else {
			qm.gainItemPeriod(2002037, qm.getGuild().getLevel() * 20, 7);
			qm.forceCompleteQuest();
		}
		qm.dispose();
	}
}
function end(mode, type, selection) {
}
