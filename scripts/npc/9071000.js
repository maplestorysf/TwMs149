var items = Array(1182000, 1182002, 1182004, 1012270, 1162008);
var coins = Array(10, 30, 50, 30, 50);
var status = 0;

function start() {
    var selStr = "歡迎來到怪物公園！請選擇兌換項目：\r\n#b";
    for (var i = 0; i < items.length; i++) {
        selStr += "#L" + i + "##i" + items[i] + "#(永久)：" + coins[i] + "個怪物公園紀念貨幣#l\r\n";
    }
    cm.sendSimple(selStr);
}

function action(mode, type, selection) {
    if (mode == 1 && selection >= 0 && selection < items.length) {
        if (!cm.canHold(items[selection])) {
            cm.sendOk("兌換失敗，請檢查道具欄位空間大小。");
        } else if (!cm.haveItem(4310020, coins[selection])) {
            cm.sendOk("兌換失敗，請檢查#b怪物公園紀念貨幣#k數量。");
        } else {
            cm.gainItem(4310020, -coins[selection]);
            cm.gainItem(items[selection], 1);
        }
    }
    cm.dispose();
}