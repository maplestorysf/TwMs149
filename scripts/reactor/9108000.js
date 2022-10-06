
function act() {
	rm.mapMessage(6, "其中一顆種子種植成功。");
	var em = rm.getEventManager("HenesysPQ");
	if (em != null) {
		var react = rm.getMap().getReactorByName("fullmoon");
		em.setProperty("stage", parseInt(em.getProperty("stage")) + 1);
		react.forceHitReactor(react.getState() + 1);
		if (em.getProperty("stage").equals("6")) {
			rm.mapMessage(6, "保護月妙!!!");
			rm.getMap().setSpawns(true);
			rm.getMap().respawn(true);
			rm.getMap().spawnMonsterOnGroundBelow(em.getMonster(9300061), new java.awt.Point(-183, -433));
			rm.achievement(50);
		}
	}
}