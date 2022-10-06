var status = -1;

function start(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 1)
            status++;
        else
            status--;

        if (status == 0) {
			qm.askAcceptDecline("恭喜您阿，送您一個小禮物。");
		} else if (status == 1) {
			qm.gainItem(4330017, 1);
            qm.forceCompleteQuest();
            qm.dispose();
		}
	}
}

function end(mode, type, selection) {}