function enter(pi) {
	if (pi.isQuestActive(25000) || pi.isQuestFinished(25000)) {
		pi.warp(915000200, "in00");
	} else {
		pi.getPlayer().dropMessage(5, "請和管家對話。");
	}
}
