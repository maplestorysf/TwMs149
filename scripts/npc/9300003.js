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
            content += "#L0##b#i5490001:# #t5490001##k#r(黃金楓葉 x60)#k#l\r\n";
            content += "#L1##b#i5490000:# #t5490000##k#r(黃金楓葉 x80)#k#l\r\n";
            content += "#k";
            cm.sendSimple(content);
            break;
        case 1:
            switch (selection) {
                case 0:
                    if (!cm.haveItem(4001168, 60) || !cm.canHold(5490001, 1)) {
                        cm.sendOk("請檢查#b黃金楓葉#k數量和道具欄位空間。");
                        cm.dispose();
                        return;
                    } else {
                        cm.gainItem(4001168, -60);
                    }
                    break;
                case 1:
                    if (!cm.haveItem(4001168, 80) || !cm.canHold(5490000, 1)) {
                        cm.sendOk("請檢查#b黃金楓葉#k數量和道具欄位空間。");
                        cm.dispose();
                        return;
                    } else {
                        cm.gainItem(4001168, -80);
                    }
                    break;
            }
            switch (selection) {
                case 0:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(5490001, 1);
                    break;
                case 1:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(5490000, 1);
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