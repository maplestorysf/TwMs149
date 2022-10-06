var status = -1;
var time = 1;
var mode = false;

function action(mode, type, selection) {
    if (!mode) {
        cm.sendNext("目前尚未開放。");
        cm.dispose();
        return;
    }
    if (cm.getClient().getChannel() != 1) {
        cm.sendNext("請至#b第1頻道#k參加活動。");
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        cm.sendNext("你好！我是#p9330107#，你知道當所在伺服器收集香爐達一定數量後，就會在不夜城召喚出國慶遊行怪物嗎？");
    } else if (status == 1) {
        cm.sendSimple("當玩家收集一定數量的香爐的時候，就會在不夜城召喚出國慶遊行怪物哦。\n\r #b#L0# 嘿！我帶來香爐了#l#k \n\r #b#L1# 請告訴我目前香爐的收集狀態#l#k \n\r #b#L3# 我要索取仙女棒和煙火#l#k");
    } else if (status == 2) {
        if (selection == 1) {
            cm.sendNext("目前香爐收集狀態：\n\r #B" + cm.get香爐() + "# \n\r 當所在伺服器收集香爐達一定數量後，就會在不夜城召喚出國慶遊行怪物哦。");
            cm.safeDispose();
        } else if (selection == 2) {
            cm.sendNext("");
            cm.safeDispose();
        } else if (selection == 3) {
            //if (cm.getBossLog('time') < 1) {
            cm.sendNext("索取成功！");
            cm.gainItemPeriod(1472081, 1, 1);
            cm.gainItem(2070020, 1000);
            //	cm.setBossLog("time");
            cm.safeDispose();
            //} else {
            //	cm.sendNext("小兄弟，別這麼貪心嘛~一天只能領一次的啊！");
            //	cm.safeDispose();
            //}
        } else if (selection == 0) {
            cm.sendGetNumber("那...你要給我多少#b香爐#k呢？\n\r", 0, 0, 5000);
        }
    } else if (status == 3) {
        var num = selection;
        if (!cm.haveItem(4000516) || num == 0) {
            cm.sendOk("如果你收集到香爐，再來找我哦。");
        } else if (cm.haveItem(4000516, num)) {
            cm.gainItem(4000516, -num);
            cm.give香爐(num);
            cm.sendOk("謝謝你的香爐。");
        }
        cm.safeDispose();
    }
}