var status = -1;

function start(mode, type, selection) {
	qm.sendNext("首先您必須先消滅巡邏機器人。");
    qm.forceStartQuest(23129, "1");
    qm.forceStartQuest(23110);
    qm.forceStartQuest();
    qm.forceCompleteQuest();
    qm.dispose();
}

function end(mode, type, selection) {}