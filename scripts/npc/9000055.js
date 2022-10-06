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
            cm.sendNext("請至#b第1頻道#k參加本活動。");
            cm.dispose();
            return;
        }
        if (status == 0) {
            cm.sendNext("嗨，你好，你想要種植楓樹嗎？只要有#b溫暖陽光#k就可以讓楓樹日漸茁壯哦！溫暖的陽光可從怪物身上取得。");
        } else if (status == 1) {
            cm.sendSimple("你想要做什麼呢？\r\n#b#L0#我帶來了一些溫暖陽光#l#k\r\n#b#L1#請告訴我目前楓樹的成長狀態#l#k");
        } else if (status == 2) {
            if (selection == 0) {
                cm.sendGetNumber("請輸入你想給楓樹#b溫暖陽光#k的數量：", cm.itemQuantity(4001246), 0, cm.itemQuantity(4001246));
            } else {
                cm.sendOk("楓樹目前的成長狀態：\r\n#B" + cm.getSunshines() + "#");
                cm.dispose();
            }
        } else if (status == 3) {
            if (selection < 0 || selection > cm.itemQuantity(4001246)) {
                selection = cm.itemQuantity(4001246);
            }
            if (selection == 0) {
                cm.sendOk("當你有#b溫暖陽光#k的時候，再來找我哦。");
            } else {
                cm.addSunshines(selection);
                cm.gainItem(4001246, -selection);
                cm.sendOk("謝謝你的#b溫暖陽光#k！");
            }
            cm.dispose();
        }
    }
}