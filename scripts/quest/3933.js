var status = -1;

function start(mode, type, selection) {
	qm.sendNext("感謝完成任務。");
	qm.forceCompleteQuest();
	qm.dispose();
}
function end(mode, type, selection) {}
