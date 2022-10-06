var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
    } else {
        status++;
        if (cm.getClient().getChannel() != 1) {
            cm.sendNext("請至#b第1頻道#k參加活動。");
            cm.dispose();
            return;
        }
        if (status == 0) {
            cm.sendNext("嗨，你好，這裡有一棵#b聖誕樹#k，你想要和我一起種植聖誕樹嗎？只要有#b聖誕樹裝飾#k就可以讓聖誕樹變大哦！聖誕樹裝飾可以從怪物身上取得...");
        } else if (status == 1) {
            cm.sendSimple("當玩家們收集足夠的聖誕樹裝飾時，聖誕樹就會長到最大！\r\n#b#L0# 我帶來了聖誕樹裝飾#l#k\r\n#b#L1# 請告訴我目前聖誕樹裝飾收集狀態#l#k");
        } else if (status == 2) {
            if (selection == 0) {
                cm.sendGetNumber("你帶來聖誕樹裝飾了嗎？那請把#b聖誕樹裝飾#k給我吧，你想要給我多少聖誕樹裝飾呢？", cm.itemQuantity(4001473), 0, cm.itemQuantity(4001473));
            } else {
                cm.sendOk("目前聖誕樹的成長狀態：\r\n#B" + cm.getDecorations() + "#\r\n如果收集齊全，聖誕樹就會長到最大。");
                cm.dispose();
            }
        } else if (status == 3) {
            if (selection < 0 || selection > cm.itemQuantity(4001473)) {
                selection = cm.itemQuantity(4001473);
            }
            if (selection == 0) {
                cm.sendOk("如果你有聖誕樹裝飾再來告訴我哦。");
            } else {
                cm.addDecorations(selection);
                cm.gainItem(4001473, -selection);
                cm.sendOk("謝謝你的聖誕樹裝飾，如果你收集到更多聖誕樹裝飾，記得來找我哦。");
            }
            cm.dispose();
        }
    }
}