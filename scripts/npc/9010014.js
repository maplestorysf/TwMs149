/* 	Aramia
 * 	Henesys fireworks NPC
 */
var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    if (cm.getClient().getChannel() != 1) {
        cm.sendNext("請至#b第1頻道#k參加活動。");
        cm.dispose();
        return;
    }
    if (status == 0) {
        cm.sendNext("嗨，你好，我是阿勒米！我知道該如何製作鞭炮，如果你可以收集火藥桶給我，我們就可以來放煙火。火藥桶可以從怪物身上收集到。");
    } else if (status == 1) {
        cm.sendSimple("當玩家們收集足夠的火藥桶時，我們就可以來放煙火！\n\r #b#L0# 我帶來火藥桶了#l#k \n\r #b#L1# 請告訴我目前火藥桶收集狀態#l#k");
    } else if (status == 2) {
        if (selection == 1) {
            cm.sendNext("火藥桶收集狀態：\n\r #B" + cm.getKegs() + "# \n\r 如果收集齊全，我們就可以開始放煙火...");
            cm.safeDispose();
        } else if (selection == 0) {
            cm.sendGetNumber("你帶來火藥桶了嗎？那麼給我你身上的#b火藥桶#k吧！我會製作出很漂亮的煙火唷！你想要給我幾個火藥桶呢？", cm.itemQuantity(4001128), 0, cm.itemQuantity(4001128));
        }
    } else if (status == 3) {
        var num = selection;
        if (num == 0) {
            cm.sendOk("如果你收集到火藥桶再來告訴我哦。");
        } else if (cm.haveItem(4001128, num)) {
            cm.gainItem(4001128, -num);
            cm.giveKegs(num);
            cm.sendOk("謝謝你的火藥桶，如果你收集到更多火藥桶，記得來找我哦。");
        }
        cm.safeDispose();
    }
}