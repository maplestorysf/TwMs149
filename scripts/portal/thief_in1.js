function enter(pi) {
	if (pi.isQuestActive(3925)) {
		pi.forceCompleteQuest(3925);
		pi.playerMessage("任務完成...");
	}
	pi.warp(260010402, 0);
}
