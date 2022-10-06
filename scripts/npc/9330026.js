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
            content += "#L0#35級許願樹銀組合#l\r\n";
            content += "#L1#75級許願樹金組合#l\r\n";
            content += "#L2#115級許願樹傳說組合\r\n";
            content += "#k";
            cm.sendSimple(content);
            break;
        case 1:
            switch (selection) {
                case 0:
                    var content = "";
                    content += "請選擇兌換項目：\r\n";
                    content += "#b";
                    content += "#L0##i1032127:# #t1032127#(黃金楓葉 x30)#l\r\n";
                    content += "#L1##i1112686:# #t1112686#(黃金楓葉 x30)#l\r\n";
                    content += "#L2##i1122183:# #t1122183#(黃金楓葉 x30)#l\r\n";
                    content += "#L3##i1132133:# #t1132133#(黃金楓葉 x30)#l\r\n";
                    content += "#L4##i1152075:# #t1152075#(黃金楓葉 x30)#l\r\n";
                    content += "#k";
                    cm.sendSimple(content);
                    break;
                case 1:
                    var content = "";
                    content += "請選擇兌換項目：\r\n";
                    content += "#b";
                    content += "#L5##i1032128:# #t1032128#(黃金楓葉 x80)#l\r\n";
                    content += "#L6##i1112687:# #t1112687#(黃金楓葉 x80)#l\r\n";
                    content += "#L7##i1122184:# #t1122184#(黃金楓葉 x80)#l\r\n";
                    content += "#L8##i1132134:# #t1132134#(黃金楓葉 x80)#l\r\n";
                    content += "#L9##i1152076:# #t1152076#(黃金楓葉 x80)#l\r\n";
                    content += "#k";
                    cm.sendSimple(content);
                    break;
                case 2:
                    var content = "";
                    content += "請選擇兌換項目：\r\n";
                    content += "#b";
                    content += "#L10##i1032129:# #t1032129#(黃金楓葉 x120)#l\r\n";
                    content += "#L11##i1112688:# #t1112688#(黃金楓葉 x120)#l\r\n";
                    content += "#L12##i1122185:# #t1122185#(黃金楓葉 x120)#l\r\n";
                    content += "#L13##i1132135:# #t1132135#(黃金楓葉 x120)#l\r\n";
                    content += "#L14##i1152077:# #t1152077#(黃金楓葉 x120)#l\r\n";
                    content += "#k";
                    cm.sendSimple(content);
                    break;
                default:
                    cm.dispose();
                    break;
            }
            break;
        case 2:
            switch (selection) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                    if (!cm.haveItem(4001168, 30) || !cm.canHold(1032127, 1)) {
                        cm.sendOk("請檢查#b黃金楓葉#k數量和道具欄位空間。");
                        cm.dispose();
                        return;
                    } else {
                        cm.gainItem(4001168, -30);
                    }
                    break;
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    if (!cm.haveItem(4001168, 80) || !cm.canHold(1032128, 1)) {
                        cm.sendOk("請檢查#b黃金楓葉#k數量和道具欄位空間。");
                        cm.dispose();
                        return;
                    } else {
                        cm.gainItem(4001168, -80);
                    }
                    break;
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                    if (!cm.haveItem(4001168, 120) || !cm.canHold(1032129, 1)) {
                        cm.sendOk("請檢查#b黃金楓葉#k數量和道具欄位空間。");
                        cm.dispose();
                        return;
                    } else {
                        cm.gainItem(4001168, -120);
                    }
                    break;
            }
            switch (selection) {
                case 0:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(1032127, 1);
                    break;
                case 1:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(1112686, 1);
                    break;
                case 2:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(1122183, 1);
                    break;
                case 3:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(1132133, 1);
                    break;
                case 4:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(1152075, 1);
                    break;
                case 5:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(1032128, 1);
                    break;
                case 6:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(1112687, 1);
                    break;
                case 7:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(1122184, 1);
                    break;
                case 8:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(1132134, 1);
                    break;
                case 9:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(1152076, 1);
                    break;
                case 10:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(1032129, 1);
                    break;
                case 11:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(1112688, 1);
                    break;
                case 12:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(1122185, 1);
                    break;
                case 13:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(1132135, 1);
                    break;
                case 14:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(1152077, 1);
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