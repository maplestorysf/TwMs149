function enter(pi) {
	if (pi.getPlayer().getParty() != null && pi.getMap().getAllMonstersThreadsafe().size() == 0 && pi.isLeader()) {
		pi.warpParty(921120400); //TODO JUMP
		pi.playPortalSE();
	} else {
		pi.playerMessage(5, "This portal is not available. Destroy Rex first.");
	}
}
