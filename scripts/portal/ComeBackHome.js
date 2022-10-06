function enter(pi) {
	var returnMap = pi.getSavedLocation("MAPLE_HIGH_SCHOOL");
	pi.clearSavedLocation("MAPLE_HIGH_SCHOOL");

	if (returnMap < 0) {
		returnMap = 100000000;
	}
	var target = pi.getMap(returnMap);
	var portal;

	if (portal == null) {
		portal = target.getPortal(0);
	}
	if (pi.getMapId() != target) {
		pi.getPlayer().changeMap(target, portal);
	}
}
