/* 另類的技能全滿 by Kodan */

var status = -1;

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 0 && status >= 0) {
		cm.dispose();
		return;
	}
	mode == 1 ? status++ : status--;
	if (status == 0) {
		var menu = cm.getSkillMenu(cm.getPlayer().getJob());
		if (menu == "") {
			cm.sendOk("您沒有技能可以提升。");
			cm.dispose();
		} else {
			cm.sendSimple("你可以提升的技能清單如下." + menu);
		}
	} else if (status == 1) {
		var skilllevel = Packages.client.SkillFactory.getSkill(selection).getMaxLevel();
		cm.teachSkill(selection,skilllevel);
		cm.sendNext("已經幫您學習了#q" + selection + "##s" + selection + "#");
		status = -1;
	}
}
