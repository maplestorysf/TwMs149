function enter(pi) {
	if (pi.getPlayer().getParty() != null && pi.getMap().getAllMonstersThreadsafe().size() == 0 && pi.isLeader()) {
		pi.warpParty(pi.getPlayer().getMapId() + 100);
	} else {
		pi.playerMessage(-1, "要除掉場地內的所有怪物，才能移動到下個地圖。");
		pi.playerMessage(5, "先除掉場地內的所有怪物。");
	}
	pi.playPortalSE();
}
