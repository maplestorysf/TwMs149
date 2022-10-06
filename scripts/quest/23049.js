var status = -1;

function start(mode, type, selection) {
	qm.dispose();
}
function end(mode, type, selection) {
	status++;
	if (status == 0) {
		qm.sendNext("你跟我個性相同，我真想好好讚賞傑利麥勒你一番，但如果那些人帶著手下來，到時候就很難逃走了，我們趁現在快回到地下本部吧，去地下本部用基地返回卷軸，來，一…二…三！");
	} else {
		qm.warp(310010000);
		qm.forceCompleteQuest();
		qm.dispose();
	}
}
