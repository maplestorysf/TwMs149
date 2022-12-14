/* global qm */
var status = -1;

function start(mode, type, selection) {
	qm.forceStartQuest();
	qm.dispose();
}

function end(mode, type, selection) {
	if (mode === 1) {
		status++;
	} else {
		status--;
	}

	var i = -1;
	if (status <= i++) {
		qm.dispose();
	} else if (status === i++) {
		qm.askMenu(1106002, 3, "雞蛋拿來了嗎？沒打破吧？你做了什麼，怎麼這副狼狽樣？ \r\n#b\r\n#L0# 那個…狼突然攻擊…還有那個…狼逃走了。#l");
	} else if (status === i++) {
		qm.say(0, 1106002, 1, "你說什麼？狼逃走了？ 你這個辦事不力的傢伙！今天沒飯可吃！找不到狼的話，你就走吧？", false, true);
	} else if (status === i++) {
		qm.forceCompleteQuest();
		qm.gainItem(2001503, 30);
		qm.dispose();
		qm.warp(913070004, 0);
	} else {
		qm.dispose();
	}
}
