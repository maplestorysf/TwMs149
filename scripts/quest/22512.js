var status = -1;
//this quest is CALL DRAGON
function start(mode, type, selection) {
	qm.sendNext("去找弓箭手村的#b麗娜#k吧，她有事情想交代你。");
	qm.forceStartQuest();
	qm.dispose();
}

function end(mode, type, selection) {
	qm.gainExp(110);
	qm.forceCompleteQuest();
	qm.dispose();
}