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
        if (qm.getJob() == 3200) {
            qm.changeJob(3210);
        }
        qm.forceCompleteQuest();
		qm.dispose();
    }
}