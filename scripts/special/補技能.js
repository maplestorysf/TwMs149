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
		cm.sendSimple("您好我是補技能npc如果您有缺失的話可以找我\r\n#b#L0#我要補技能#l");
	} else if (status == 1) {
		switch(cm.getPlayer().getJob()) {
			case 1510:
				cm.teachSkill(15101006, 1); //技能代碼,等級
			break;
			default:
				cm.sendNext("您的職業無法補償");
				cm.dispose();
			break;
		}
		cm.sendNext("已經補償了!");
		cm.dispose();
	}
}