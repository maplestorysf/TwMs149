var status = -1;

function start(mode, type, selection) {
    qm.sendNext("你想要巨大的矛？哈哈！你看起來還不夠強壯呢！如果你想要巨大的矛，狩獵西邊的 #r#o9001012#s#k，並找到30個 #b#t4032311##k!");
    qm.forceStartQuest();
    qm.dispose();
}

function end(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        qm.sendNext("喔~資格的象徵全部都拿來了嗎？你...比我想像中的還要強。");
    } else if (status == 1) {
        if (qm.getPlayerStat("RSP") > (qm.getPlayerStat("LVL") - 30) * 3) {
            qm.sendNext("你有#b技能點數#k過多，請使用完後再找我對話。");
            qm.dispose();
            return;
        }
        qm.sendNextS("我的記憶正在回來...", 2);
        qm.changeJob(2110);
        qm.gainItem(4032311, -30);
        qm.forceCompleteQuest(21201);
		qm.forceCompleteQuest(29925); // TODO: 檢查道具欄位空間
		if (qm.canHold(1142130, 1)) {
			qm.gainItem(1142130, 1);
		}
        qm.forceCompleteQuest();
    } else if (status == 2) {
        qm.sendOk("哈哈！你拿到你想要的了，對吧？現在離開吧！");
        qm.dispose();
    }
}