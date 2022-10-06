var status = -1;

function end(mode, type, selection) {
    if (mode == 0) {
        if (status == 0) {
            qm.sendNext("This is an important decision to make.");
            qm.safeDispose();
            return;
        }
        status--;
    } else {
        status++;
    }
    if (status == 0) {
        if (qm.getJob() == 3000) {
            qm.expandInventory(1, 4);
            qm.expandInventory(2, 4);
            qm.expandInventory(4, 4);
            qm.gainItem(1382100, 1);
            qm.changeJob(3200);
        }
        qm.forceCompleteQuest();
		qm.dispose();
    }
}