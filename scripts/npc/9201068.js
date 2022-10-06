var sw;

function start() {
    status = -1;
    sw = cm.getEventManager("Subway");
    action(1, 0, 0);
}

function action(mode, type, selection) {
    status++;
    if (mode == 0) {
        cm.sendNext("你在這裡還有還沒完成的事情，對吧？");
        cm.dispose();
        return;
    }
    if (status == 0) {
        if (sw == null) {
            cm.dispose();
        } else if (sw.getProperty("entry").equals("true")) {
            cm.sendYesNo("這班地鐵看起來還有很多空間，怎麼樣？你想坐這班地鐵嗎？");
        } else if (sw.getProperty("entry").equals("false") && sw.getProperty("docked").equals("true")) {
            cm.sendNext("地鐵即將啟程，請搭下一班地鐵。");
            cm.dispose();
        } else {
            cm.sendNext("請耐心等待幾分鐘，請注意，地鐵按時啟程，我們在啟程前1分鐘停止接收車票，所以請務必準時到達。");
            cm.dispose();
        }
    } else if (status == 1) {
        if (!cm.haveItem(4031711)) {
            cm.sendNext("請購買新葉城地鐵票。");
        } else {
            cm.gainItem(4031711, -1);
            cm.warp(600010002);
        }
        cm.dispose();
    }
}