var status = -1;
function start(mode, type, selection) {
	qm.forceStartQuest();
	qm.dispose();
}

function end(mode, type, selection) {
	if (qm.canHold(1142340, 1)) {
		qm.gainItem(1142340,1);
		qm.teachSkill(20021005, 1, 1);
		qm.sendNext("您已經學得#s20021005#、#t1142340##i1142340#");
		qm.forceCompleteQuest();
	} else {
		qm.sendNext("請檢察背包空間。");
	}
	qm.dispose();
}
