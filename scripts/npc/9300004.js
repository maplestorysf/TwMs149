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
            content += "#L0##b#i2050004:# #t2050004##k#b(100罐)#k#r(黃金楓葉 x12)#k#l\r\n";
            content += "#L1##b#i4006000:# #t4006000##k#r(黃金楓葉 x1)#k#l\r\n";
			content += "#L2##b#i4006001:# #t4006001##k#r(黃金楓葉 x1)#k#l\r\n";
            content += "#k";
            cm.sendSimple(content);
            break;
        case 1:
            switch (selection) {
                case 0:
                    if (!cm.haveItem(4001168, 12) || !cm.canHold(2050004, 100)) {
                        cm.sendOk("請檢查#b黃金楓葉#k數量和道具欄位空間。");
                        cm.dispose();
                        return;
                    } else {
                        cm.gainItem(4001168, -12);
                    }
                    break;
                case 1:
                    if (!cm.haveItem(4001168, 1) || !cm.canHold(4006000, 1)) {
                        cm.sendOk("請檢查#b黃金楓葉#k數量和道具欄位空間。");
                        cm.dispose();
                        return;
                    } else {
                        cm.gainItem(4001168, -1);
                    }
                    break;
				case 2:
                    if (!cm.haveItem(4001168, 1) || !cm.canHold(4006001, 1)) {
                        cm.sendOk("請檢查#b黃金楓葉#k數量和道具欄位空間。");
                        cm.dispose();
                        return;
                    } else {
                        cm.gainItem(4001168, -1);
                    }
                    break;
            }
            switch (selection) {
                case 0:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2050004, 100);
                    break;
                case 1:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(4006000, 1);
                    break;
				case 2:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(4006001, 1);
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