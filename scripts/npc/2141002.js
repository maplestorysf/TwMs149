var status = -1;

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		status--;
	}
	if (status == 0) {
		if (cm.getPlayer().getMapId() == 270050200) {
			cm.sendYesNo("您想要回去戰鬥嗎??");
			status = 2;
		} else {
			cm.sendYesNo("您想要出去了嗎??");
		}
	} else if (status == 1) {
		cm.warp(270050000, 0);
		cm.dispose();
	} else if (status == 3) {
		cm.warp(270050100, 0);
		cm.dispose();
	}
}
