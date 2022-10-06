var status = -1;
//this quest is DELIVER LETTER
function start(mode, type, selection) {
	if (qm.canHold(4032455,1)) {
		qm.sendNext("前往#b#m100000000##k把這封信交給長老斯坦。");
		qm.gainItem(4032455,1);
		qm.forceStartQuest();
	} else {
		qm.sendNext("請檢查背包空間是否足夠。");
	}
	qm.dispose();
}

function end(mode, type, selection) {
	if (qm.haveItem(4032455,1)) {
		qm.sendNext("謝謝。");
		qm.getPlayer().gainSP(1, 0);
		qm.gainExp(450);
		qm.gainItem(4032455, -1);
		qm.forceCompleteQuest();
	} else {
		qm.sendNext("請給我克里特的信件。");
	}
	qm.dispose();
}