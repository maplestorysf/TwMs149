function enter(pi) {
	if (!pi.haveMonster(9300216)) {
		pi.playerMessage("地圖上還有怪物。");
	} else {
		pi.dojo_getUp();
		pi.getMap().setReactorState();
	}
}
