function start(mode, type, selection) {
	qm.forceStartQuest();
	qm.dispose();
}

function end(mode, type, selection) {
	if (qm.getPlayer().getQuestStatus(2124) == 0) {
		qm.forceStartQuest();
	} else if (qm.getPlayer().getQuestStatus(2124) == 1) {
		qm.forceCompleteQuest();
	}
	qm.dispose();
}
