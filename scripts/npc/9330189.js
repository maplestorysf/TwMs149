function start() {
	cm.sendSimple("準備好上學了嗎??\r\n#L0#上學去#l\r\n#L1#查看我的好友度#l");
}

function action(mode, type, selection) {
	if (mode != 1) {
		cm.dispose();
		return;
	}
	switch (selection) {
	case 0:
		cm.saveLocation("MAPLE_HIGH_SCHOOL");
		cm.warp(744000000, 0);
		break;
	case 1:
		cm.sendFriendWindow();
		break;
	}
	cm.dispose();
}
