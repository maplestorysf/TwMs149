var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        cm.sendNext("你好，我是#b必魯#k。");
    } else {
        cm.dispose();
    }
}