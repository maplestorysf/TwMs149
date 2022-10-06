var status = 0;
var job;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 && status == 2) {
        cm.sendOk("如果你想轉職成#r劍士#k再來找我。");
        cm.dispose();
        return;
    }
    if (mode == 1)
        status++;
    else
        status--;
    if (status == 0) {
        if (cm.getJob() == 0) {
            if (cm.getPlayerStat("LVL") >= 10 && cm.getJob() == 0) {
                cm.sendNext("你想要轉職成#r劍士#k嗎？");
            } else {
                cm.sendOk("未滿10級無法轉職成#r劍士#k。");
                cm.dispose();
            }
        } else {
            cm.sendOk("明智的選擇。");
            cm.dispose();
        }
    } else if (status == 1) {
        cm.sendNextPrev("這是很重要的抉擇，一旦轉職就不能反悔哦。");
    } else if (status == 2) {
        cm.sendYesNo("你確定要轉職成#r劍士#k嗎？");
    } else if (status == 3) {
        if (cm.getJob() == 0) {
            cm.resetStats(35, 4, 4, 4);
            cm.expandInventory(1, 4);
            cm.expandInventory(4, 4);
            cm.changeJob(100);
        }
        cm.gainItem(1402001, 1);
        cm.sendOk("轉職成功。");
        cm.dispose();
    }
}