var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
        }
        status--;
    }
    if (status == 0) {
        cm.sendYesNo("想要前往#b維多利亞島#k嗎？");
    } else if (status == 1) {
        cm.warp(104000000);
        cm.dispose();
    }
}