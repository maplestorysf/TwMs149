var status = 0;
var cost = 5000;

function start() {
    if (cm.getMapId() == 103020000) {
        cm.sendYesNo("你好，我是貝爾，你想到#b新葉城#k嗎？金額5000楓幣。");
    } else {
        cm.sendYesNo("你好，我是貝爾，你想回到#b墮落城市#k嗎？金額5000楓幣。");
    }
}

function action(mode, type, selection) {
    if (mode != 1) {
        if (mode == 0) {
            if (cm.getMapId() == 103020000) {
                cm.sendOk("如果你想要到#b新葉城#k，再告訴我哦。");
            } else {
                cm.sendOk("如果你想要回到#b墮落城市#k，再告訴我哦。");
            }
        }
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
            if (cm.getMapId() == 103020000) {
                cm.warp(600010001, 1);
            } else {
                cm.warp(103020000, 1);
            }
            cm.dispose();
        }
    }
}