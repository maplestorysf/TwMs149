var status = -1;

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		if (status == 0) {
			cm.dispose();
		}
		status--;
	}
	if (status == 0) {
		if (cm.getPlayer().getMapId() == 262000300) {
			cm.dispose();
		} else {
			cm.sendYesNo("您想要前往#r#m262000300##k ??");
		}
	} else if (status == 1) {
		if (cm.getPlayer().getMapId() != 262000300) {
			cm.warp(262000300, 0);
			cm.dispose();
		}
	}
}
