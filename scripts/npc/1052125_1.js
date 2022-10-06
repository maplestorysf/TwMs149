var status = -1;
var sel;

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		if (mode == 1 && status == 0) {
			cm.sendNext("需要去搖滾的話再來找我!!");
			cm.dispose();
			return;
		}
		status--;
	}
	if (status == 0) {
		cm.sendSimple("您好，我是#p1052125# 有什麼需要我幫忙的嗎??\r\n#b#L0#我要去普通搖滾區#l\r\n#L1#我要去VIP搖滾區#l");
	} else if (status == 1) {
		sel = selection;
		switch (sel) {
		case 0:
			if (cm.getPlayerCount(103040430) <= 0) {
				cm.spawnMobOnMap(4300013, 1, -775, 86, 103040430);
			}
			cm.warp(103040410, 1);
			break;
		case 1:
			if (cm.getQuestStatus(2290) == 2) {
				if (cm.getPlayerCount(103040460) <= 0) {
					cm.spawnMobOnMap(4300013, 1, -775, 86, 103040460);
				}
				cm.warp(103040440, 1);
			} else {
				cm.sendNext("好像還沒準備好....");
			}
			break;
		default:
			cm.dispose();
			return;
		}
		cm.dispose();
	}
}
