function action(mode, type, selection) {
	if (cm.isQuestActive(3929)) {
		var mainquest = cm.getQuest(3929);
		mainquest.setCustomData("3333");
		cm.getPlayer().updateQuest(mainquest, true);
		cm.removeAll(4031580);
	} else if (cm.isQuestActive(3926)) {
		var mainquest = cm.getQuest(3926);
		mainquest.setCustomData("3333");
		cm.getPlayer().updateQuest(mainquest, true);
		cm.removeAll(4031579);
	}
	cm.dispose();
}
