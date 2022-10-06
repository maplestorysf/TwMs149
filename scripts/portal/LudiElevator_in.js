var em;

function enter(pi) {
	if (pi.getMapId() == 222020100) {
		pi.playPortalSE();
		pi.warpBack(222020110, 222020200, 60);
	} else { // 222020200
		pi.playPortalSE();
		pi.warpBack(222020210, 222020100, 60);
	}
}
