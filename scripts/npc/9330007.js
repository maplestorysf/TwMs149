var status = 0;
var cost = 2000;

function start() {
    cm.sendYesNo("你好，我是捷運服務員，你想到#b西門町#k嗎？金額2000楓幣。");
}

function action(mode, type, selection) {
    if (mode != 1) {
        if (mode == 0)
            cm.sendOk("如果你想要到#b西門町#k，再告訴我哦。");
        cm.dispose();
        return;
    }
    status++;
    if (status == 1) {
        if (cm.getMeso() < cost) {
            cm.sendOk("請檢查楓幣數量。");
            cm.dispose();
        } else {
            cm.gainMeso(-cost);
            cm.warp(740000100, 0);
            cm.dispose();
        }
    }
}