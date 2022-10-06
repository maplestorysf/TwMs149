function enter(pi) {
	if (pi.isQuestActive(3935)) {
		if (!pi.haveItem(4031574)) {
			pi.gainItem(4031574, 1);
			pi.playerMessage("拿到了青空之晶...趕緊回報任務...");
		}
	}
}
