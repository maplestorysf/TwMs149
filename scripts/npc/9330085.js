/* Yuan bao fo
 */
var fromYuanBaoMap;

function start() {
    if (cm.getMapId() == 749040000) {
        fromYuanBaoMap = true;
        cm.sendSimple("這個地方對你來說太詭異了嗎？\n\r #b#L0#是，帶我回到原本的地方。#l");
    } else if (cm.getMapId() == 749040001) {
        cm.warp(cm.getSavedLocation("DONGDONGCHIANG"), 0);
        cm.dispose();
    } else {
        fromYuanBaoMap = false;
        cm.sendSimple("新年快樂樂樂樂！哈哈哈！你需要仙女棒和鞭炮對吧？只要支出 #b10,000 楓幣#k，我就能送你仙女棒和鞭炮，並帶你到一個相當特別的地方。猜猜看那邊會有什麼等著你！\n\
\n\r #b#L0#我要到舞獅活動地圖#l \n\r #b#L1#使用 88 個紅包兌換 888,888 楓幣#l \n\r #b#L2#使用 488 個紅包兌換 銀鑰匙#l \n\r #b#L3#使用 888 個紅包兌換 金鑰匙#l");
    }
}

function action(mode, type, selection) {
    if (!fromYuanBaoMap) {
        if (mode == 1) {
            switch (selection) {
                case 0:
                    cm.saveLocation("DONGDONGCHIANG");
                    cm.warp(749040000, 0);

                    if (!cm.haveItem(1472081, 1, true, true)) {
                        cm.gainItem(1472081, 1);
                    }
                    if (!cm.haveItem(2070020)) {
                        cm.gainItem(2070020, 500);
                    }
                    break;
                case 1:
                    if (cm.haveItem(4000306, 88)) {
                        cm.gainMeso(888888);
                        cm.gainItem(4000306, -88);
                    }
                    break;
                case 2:
                    if (cm.haveItem(4000306, 488)) {
                        cm.gainItem(5490001, 1);
                        cm.gainItem(4000306, -488);
                    }
                    break;
                case 3:
                    if (cm.haveItem(4000306, 888)) {
                        cm.gainItem(5490000, 1);
                        cm.gainItem(4000306, -888);
                    }
                    break;
            }

        } else {
            cm.sendNext("嗯？對舞獅活動地圖沒有興趣嗎？那，希望這將是一個美好的一年，如果你改變主意，就隨時回來找我，好嗎？");
        }
    } else {
        if (mode == 1) {
            cm.warp(cm.getSavedLocation("DONGDONGCHIANG") < 0 ? 100000000 : cm.getSavedLocation("DONGDONGCHIANG"), 0);
            cm.clearSavedLocation("DONGDONGCHIANG");
        }
    }
    cm.dispose();
}