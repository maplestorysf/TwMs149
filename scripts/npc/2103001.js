function action(mode, type, selection) {
    if (cm.isQuestActive(3927)) {
		var mainquest = cm.getQuest(3927);
		mainquest.setCustomData("1");
		cm.getPlayer().updateQuest(mainquest, true);
    }
    cm.dispose();
}