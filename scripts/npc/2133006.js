var status = -1;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0) {
			cm.sendOk("等您想要再來吧。");
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;

		if (status == 0) {
			if (cm.getQuestStatus(31229) != 0) {
				cm.sendYesNo("裡面有一個洞穴....\r\n是否要進去看看??");
			} else {
				cm.getPlayer().dropMessage(5, "由於裡面太強大...無法進去洞穴探查...");
				cm.dispose();
			}
		} else if (status == 1) {
			cm.dispose();
			cm.warp(300030310, 0);
			cm.killAllMob();
			cm.spawnMob(5250007, 58, 150);
		}
	}
}
