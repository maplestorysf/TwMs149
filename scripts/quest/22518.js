var status = -1;
//this quest is POWER B FORE
function start(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
    } else {
        status++;
        if (status == 0) {
            qm.sendNext("我不相信，哼哼！");
        } else if (status == 1) {
            qm.forceStartQuest();
            qm.sendNext("你自己進入修練場打倒 100 隻修練生斯普亞吧。");
            qm.dispose();
        }
    }
}

function end(mode, type, selection) {
    if (!qm.canHold(2000004, 20) || !qm.canHold(2000002, 20) || !qm.canHold(4032457, 1)) {
        qm.sendNext("請檢查背包是否有空間。");
        qm.dispose();
        return;
    }
    qm.gainItem(4032457, 1);
    qm.gainItem(2000004, 20);
    qm.gainItem(2000002, 20);
    qm.getPlayer().gainSP(1, 0);
    qm.gainExp(520);
    qm.forceCompleteQuest();
    qm.dispose();
}