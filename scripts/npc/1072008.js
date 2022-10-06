var status;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status == -1) {
        if (cm.getMapId() == 912040000) {
            if (!(cm.haveItem(4031856, 15))) {
                cm.sendNext("走吧, 去狩獵15個#b強大力量結晶#k！");
                cm.dispose();
            } else {
                status = 2;
                cm.sendNext("哇, 這麼快就狩獵到15顆#b強大力量結晶#k了! 恭喜你, 我現在就把你傳送出去吧。");
            }
        } else if (cm.getMapId() == 912040005) {
            if (!(cm.haveItem(4031013, 30))) {
                cm.sendNext("走吧, 去狩獵30顆#b黑珠#k！");
                cm.dispose();
            } else {
                status = 2;
                cm.sendNext("哇, 這麼快就狩獵到30顆#b黑珠#k了! 恭喜你, 我現在就把你傳送出去吧。");
            }
        } else {
            cm.sendNext("腳本異常。");
            cm.dispose();
        }
    } else if (status == 2) {
        cm.warp(120000101, 0);
        cm.dispose();
    }
}