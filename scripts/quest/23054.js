var status = -1;

function start(mode, type, selection) {
    if (mode == 0) {
        if (status == 0) {
            qm.sendNext("決定好再來找我。");
            qm.safeDispose();
            return;
        }
        status--;
    } else {
        status++;
    }
    if (status == 0) {
        qm.sendYesNo("你做出轉職的決定了嗎？請仔細考慮清楚。");
    } else if (status == 1) {
        qm.sendNext("我剛剛塑造了你的身體，使其完美。如果你想變得更強大，請使用能力值視窗(S)來分配你的能力值，如果你不知道要提高哪些能力值，請使用#b自動分配#k。");
        if (qm.getJob() == 3511) {
            qm.changeJob(3512);
            qm.forceCompleteQuest();
        }
    } else if (status == 2) {
        qm.sendNextPrev("去告訴大家我們的末日反抗軍有多麼強大吧！");
        qm.safeDispose();
    }
}

function end(mode, type, selection) {
    if (mode == 0) {
        if (status == 0) {
            qm.sendNext("決定好再來找我。");
            qm.safeDispose();
            return;
        }
        status--;
    } else {
        status++;
    }
    if (status == 0) {
        qm.sendYesNo("你做出轉職的決定了嗎？請仔細考慮清楚。");
    } else if (status == 1) {
        qm.sendNext("我剛剛塑造了你的身體，使其完美。如果你想變得更強大，請使用能力值視窗(S)來分配你的能力值，如果你不知道要提高哪些能力值，請使用#b自動分配#k。");
        if (qm.getJob() == 3511) {
            qm.changeJob(3512);
            qm.forceCompleteQuest();
        }
    } else if (status == 2) {
        qm.sendNextPrev("去告訴大家我們的末日反抗軍有多麼強大吧！");
        qm.safeDispose();
    }
}