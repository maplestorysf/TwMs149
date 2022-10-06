var status = -1;
//this quest is DRAGON TRAINING
function start(mode, type, selection) {
	qm.sendNext("想要變強的話，那就加強修練吧！去找#b長老斯坦#k。");
	qm.forceCompleteQuest();
	qm.dispose();
}

function end(mode, type, selection) {
	qm.gainExp(100);
	qm.forceCompleteQuest();
	qm.dispose();
}