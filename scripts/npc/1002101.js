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
        cm.sendYesNo("想要前往#b龍沉睡的島#k嗎？");
    } else if (status == 1) {
        cm.warp(914100000);
        cm.dispose();
    }
}