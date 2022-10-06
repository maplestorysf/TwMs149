var status = -1;

function action(mode, type, selection) {
    switch (mode) {
        case 1:
            status++;
            break;
        default:
            status--;
            break;
    }
    switch (status) {
        case 0:
            var content = "請選擇兌換項目：\r\n";
            content += "#b";
            content += "#L0##b#i2070001:# #t2070001##k#r(黃金楓葉 x3)#k#l\r\n";
            content += "#L1##b#i2070002:# #t2070002##k#r(黃金楓葉 x5)#k#l\r\n";
            content += "#L2##b#i2070003:# #t2070003##k#r(黃金楓葉 x8)#k#l\r\n";
            content += "#L3##b#i2070004:# #t2070004##k#r(黃金楓葉 x10)#k#l\r\n";
            content += "#L4##b#i2070005:# #t2070005##k#r(黃金楓葉 x12)#k#l\r\n";
            content += "#L5##b#i2070006:# #t2070006##k#r(黃金楓葉 x18)#k#l\r\n";
            content += "#L6##b#i2330002:# #t2330002##k#r(黃金楓葉 x3)#k#l\r\n";
            content += "#L7##b#i2330003:# #t2330003##k#r(黃金楓葉 x5)#k#l\r\n";
            content += "#L8##b#i2330004:# #t2330004##k#r(黃金楓葉 x8)#k#l\r\n";
            content += "#L9##b#i2330005:# #t2330005##k#r(黃金楓葉 x10)#k#l\r\n";
            content += "#k";
            cm.sendSimple(content);
            break;
        case 1:
            switch (selection) {
                case 0:
                    if (!cm.haveItem(4001168, 3) || !cm.canHold(2070001, 1)) {
                        cm.sendOk("請檢查#b黃金楓葉#k數量和道具欄位空間。");
                        cm.dispose();
                        return;
                    } else {
                        cm.gainItem(4001168, -3);
                    }
                    break;
                case 1:
                    if (!cm.haveItem(4001168, 5) || !cm.canHold(2070002, 1)) {
                        cm.sendOk("請檢查#b黃金楓葉#k數量和道具欄位空間。");
                        cm.dispose();
                        return;
                    } else {
                        cm.gainItem(4001168, -5);
                    }
                    break;
                case 2:
                    if (!cm.haveItem(4001168, 8) || !cm.canHold(2070003, 1)) {
                        cm.sendOk("請檢查#b黃金楓葉#k數量和道具欄位空間。");
                        cm.dispose();
                        return;
                    } else {
                        cm.gainItem(4001168, -8);
                    }
                    break;
                case 3:
                    if (!cm.haveItem(4001168, 10) || !cm.canHold(2070004, 1)) {
                        cm.sendOk("請檢查#b黃金楓葉#k數量和道具欄位空間。");
                        cm.dispose();
                        return;
                    } else {
                        cm.gainItem(4001168, -10);
                    }
                    break;
                case 4:
                    if (!cm.haveItem(4001168, 12) || !cm.canHold(2070005, 1)) {
                        cm.sendOk("請檢查#b黃金楓葉#k數量和道具欄位空間。");
                        cm.dispose();
                        return;
                    } else {
                        cm.gainItem(4001168, -12);
                    }
                    break;
                case 5:
                    if (!cm.haveItem(4001168, 18) || !cm.canHold(2070006, 1)) {
                        cm.sendOk("請檢查#b黃金楓葉#k數量和道具欄位空間。");
                        cm.dispose();
                        return;
                    } else {
                        cm.gainItem(4001168, -18);
                    }
                    break;
                case 6:
                    if (!cm.haveItem(4001168, 3) || !cm.canHold(2330002, 1)) {
                        cm.sendOk("請檢查#b黃金楓葉#k數量和道具欄位空間。");
                        cm.dispose();
                        return;
                    } else {
                        cm.gainItem(4001168, -3);
                    }
                    break;
                case 7:
                    if (!cm.haveItem(4001168, 5) || !cm.canHold(2330003, 1)) {
                        cm.sendOk("請檢查#b黃金楓葉#k數量和道具欄位空間。");
                        cm.dispose();
                        return;
                    } else {
                        cm.gainItem(4001168, -5);
                    }
                    break;
                case 8:
                    if (!cm.haveItem(4001168, 8) || !cm.canHold(2330004, 1)) {
                        cm.sendOk("請檢查#b黃金楓葉#k數量和道具欄位空間。");
                        cm.dispose();
                        return;
                    } else {
                        cm.gainItem(4001168, -8);
                    }
                    break;
                case 9:
                    if (!cm.haveItem(4001168, 10) || !cm.canHold(2330005, 1)) {
                        cm.sendOk("請檢查#b黃金楓葉#k數量和道具欄位空間。");
                        cm.dispose();
                        return;
                    } else {
                        cm.gainItem(4001168, -10);
                    }
                    break;
            }
            switch (selection) {
                case 0:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2070001, 1);
                    break;
                case 1:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2070002, 1);
                    break;
                case 2:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2070003, 1);
                    break;
                case 3:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2070004, 1);
                    break;
                case 4:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2070005, 1);
                    break;
                case 5:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2070006, 1);
                    break;
				case 6:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2330002, 1);
                    break;
                case 7:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2330003, 1);
                    break;
                case 8:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2330004, 1);
                    break;
                case 9:
				    cm.sendOk("兌換成功！");
                    cm.gainItem(2330005, 1);
                    break;
                default:
                    cm.dispose();
                    break;
            }
            break;
        default:
            cm.dispose();
            break;
    }
}