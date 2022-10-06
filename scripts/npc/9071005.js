var status = 0;

function start() {
    if (cm.getMapId() == 951000000) {
        cm.dispose();
        return;
    }
    cm.sendYesNo("你要離開嗎？");
}

function action(mode, type, selection) {
    if (mode == 1) {
        cm.warp(951000000);
    }
    cm.dispose();
}