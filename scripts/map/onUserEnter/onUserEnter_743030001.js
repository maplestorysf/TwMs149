/* global ms */
var status = -1;

function action(mode, type, selection) {
    if (mode === 0) {
        status--;
    } else {
        status++;
    }

    var i = -1;
    if (status <= i++) {
        ms.dispose();
    } else if (status === i++) {
        ms.getDirectionStatus(true);
        ms.lockUI(1, 1);
        ms.exceTime(500);
        ms.getDirectionStatus(true);
    } else if (status === i++) {
        ms.say(0, 9330203, 1, "試穿看看", false, true);
    } else if (status === i++) {
        if (ms.getOneInfo(52410, "equip") !== "1") {
            ms.gainItem(2000004, 5);
            ms.gainItem(2000003, 10);
            if (ms.getPlayerStat("GENDER") === 0) {
                ms.equip(1050236); // 洪武團禮服
            } else {
                ms.equip(1051286); // 洪武團禮服
            }
            ms.equip(1072665); // 洪武團靴子
            ms.equip(1102422); // 洪武團披風
            ms.updateOneInfo(52410, "equip", "1");
        }
        ms.getDirectionFacialExpression(2, 10000);
        ms.exceTime(500);
    } else if (status === i++) {
        ms.say(0, 9330203, 1, "怎樣?嚇到了嗎?", false, true);
    } else if (status === i++) {
        ms.exceTime(300);
    } else if (status === i++) {
        ms.say(0, 9330203, 3, "太，太開心了，所以，無話可說。", false, true);
    } else if (status === i++) {
        ms.exceTime(300);
    } else if (status === i++) {
        ms.exceTime(300);
    } else if (status === i++) {
        ms.say(0, 9330203, 3, "好!我從現在開始也是洪武團的一份子!", false, true);
    } else if (status === i++) {
        ms.say(0, 9330203, 3, "如果想像父親一樣成為優秀的俠客,就要更認真的修煉了!", true, true);
    } else if (status === i++) {
        ms.say(0, 9330203, 1, "少爺你開心，連我的心情也變好。對了我要去幫團長，要走了。就算沒有我，你也不能疏於修煉！", true, true);
    } else if (status === i++) {
        ms.say(0, 9330203, 3, "恩，知道了!耶願!謝謝!!", true, true);
    } else if (status === i++) {
        ms.lockUI(0);
        ms.dispose();
        ms.warp(743000000, 0);
    } else {
        ms.dispose();
    }
}
