var status = -1;

function action(mode, type, selection) {
	if (cm.isQuestActive(3900)) {
		cm.forceCompleteQuest(3900);
		cm.gainExp(300);
		cm.playerMessage(5, "你居然喝到了綠洲的淫水...");
	}
	cm.dispose();
}
