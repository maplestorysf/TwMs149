function enter(pi) {
	if (pi.getPlayer().getParty() != null && pi.getMap().getAllMonstersThreadsafe().size() == 0 && pi.isLeader()) {
		pi.warpParty(240080050);
		pi.playPortalSE();
	} else {
		pi.playerMessage(5,"請把地上的怪物消滅...");
	}
}