var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        if (cm.haveItem(5310000)) {
            cm.sendYesNo("你想要使用#b#t5310000##i5310000##k嗎？");
        } else {
            cm.sendOk("請至購物商城購買#b#t5310000##i5310000##k。");
            cm.safeDispose();
        }
    } else if (status == 1) {
        var item;
        if (Math.floor(Math.random() * 4) == 0) {
            var rareList = new Array(2022217/*, 2022221, 2022222*/, 2022223);
            item = cm.gainGachaponItem(rareList[Math.floor(Math.random() * rareList.length)], 1, "幸運御守");
        } else {
            var itemList = new Array(2022216, 2022218, 2022219, 2022220);
            item = cm.gainGachaponItem(itemList[Math.floor(Math.random() * itemList.length)], 1);
        }
        if (item != -1) {
            cm.gainItem(5310000, -1);
            cm.sendOk("恭喜獲得 #b#t" + item + "##k。");
        } else {
            cm.sendOk("請檢查道具欄位空間。");
        }
        cm.safeDispose();
    } else {
        cm.dispose();
    }
}