var status = -1;

function start(mode, type, selection) {}

function end(mode, type, selection) {
    if (qm.getQuestStatus(20746) == 0) {
        qm.forceStartQuest();
    } else if (qm.getQuestStatus(20746) == 1) {
        qm.forceCompleteQuest();
    }
	qm.dispose();
}