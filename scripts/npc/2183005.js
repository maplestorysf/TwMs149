function start() {
    cm.sendYesNoS("真的要離開嗎？", 4);
}

function action(mode, type, selection) {
    if (mode == 1) {
        cm.warp(262010000,0);
    }
    cm.dispose();
}