function action(mode, type, selection) {
    cm.sendNext("這是雷克斯，正在睡覺。");
    if (cm.isQuestActive(3122)) {
	cm.forceStartQuest(3122, "1");
    }
    cm.dispose();
}