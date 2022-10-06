function act() {
	var mainquest = rm.getQuest(20033);
	if (mainquest.getCustomData() == null) {
		mainquest.setCustomData("1");
	}
	if (mainquest.getCustomData().equals("1111111111")) {
		rm.mapMessage("狼人項圈斷掉了。");
		rm.spawnMob(9001051, 229, 65);
		mainquest.setCustomData("1");
	}
	var dd = (mainquest.getCustomData()+1);
	mainquest.setCustomData("" + dd + "");
	rm.getPlayer().dropMessage(6, "" + dd + "");
	rm.dropSingleItem(4033196);
}
