var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.sendNext("你還有沒完成的事嗎？");
        cm.safeDispose();
    }
    if (status == 0) {
        cm.sendYesNo("你要離開#b幸福村#k了嗎？");
    } else if (status == 1) {
        cm.warp(100000000, 0);
        cm.dispose();
    }
}