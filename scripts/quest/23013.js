var status = -1;

function start(mode, type, selection) {
    if (mode == 0) {
        if (status == 0) {
            qm.sendNext("這是一個重要的決定。");
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
            qm.gainItem(1492014, 1); //1492065 desert eagle
            qm.changeJob(3500);
            //30001061 = capture, 30001062 = call, 30001068 = mech dash
            qm.teachSkill(30001068, 1, 0);
        }
        qm.forceCompleteQuest();
        qm.dispose();
    }
}

function end(mode, type, selection) {
}