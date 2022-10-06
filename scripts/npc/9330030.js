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
            content += "#L0##b#i2280003:# #t2280003##k#r(黃金楓葉 x120)#k#l\r\n";
            content += "#L1##b#i2280004:# #t2280004##k#r(黃金楓葉 x120)#k#l\r\n";
            content += "#L2##b#i2280005:# #t2280005##k#r(黃金楓葉 x120)#k#l\r\n";
            content += "#L3##b#i2280006:# #t2280006##k#r(黃金楓葉 x120)#k#l\r\n";
            content += "#L4##b#i2280007:# #t2280007##k#r(黃金楓葉 x120)#k#l\r\n";
            content += "#L5##b#i2280008:# #t2280008##k#r(黃金楓葉 x120)#k#l\r\n";
            content += "#L6##b#i2280009:# #t2280009##k#r(黃金楓葉 x120)#k#l\r\n";
            content += "#L7##b#i2280010:# #t2280010##k#r(黃金楓葉 x120)#k#l\r\n";
            content += "#L8##b#i2280012:# #t2280012##k#r(黃金楓葉 x120)#k#l\r\n";
            content += "#L9##b#i2280013:# #t2280013##k#r(黃金楓葉 x120)#k#l\r\n";
            content += "#L10##b#i2280014:# #t2280014##k#r(黃金楓葉 x120)#k#l\r\n";
            content += "#L11##b#i2280015:# #t2280015##k#r(黃金楓葉 x120)#k#l\r\n";
            content += "#L12##b#i2280016:# #t2280016##k#r(黃金楓葉 x120)#k#l\r\n";
            content += "#L13##b#i2280026:# #t2280026##k#r(黃金楓葉 x120)#k#l\r\n";
            content += "#L14##b#i2280027:# #t2280027##k#r(黃金楓葉 x120)#k#l\r\n";
            content += "#L15##b#i2280028:# #t2280028##k#r(黃金楓葉 x120)#k#l\r\n";
            content += "#L16##b#i2280029:# #t2280029##k#r(黃金楓葉 x120)#k#l\r\n";
            content += "#L17##b#i2280030:# #t2280030##k#r(黃金楓葉 x120)#k#l\r\n";
			content += "#L18##b#i2280031:# #t2280031##k#r(黃金楓葉 x120)#k#l\r\n";
            content += "#k";
            cm.sendSimple(content);
            break;
        case 1:
            if (!cm.haveItem(4001168, 120) || !cm.canHold(2280003, 1)) {
                cm.sendOk("請檢查#b黃金楓葉#k數量和道具欄位空間。");
                cm.dispose();
                return;
            } else {
                cm.gainItem(4001168, -120);
            }
            switch (selection) {
                case 0:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2280003, 1);
                    break;
                case 1:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2280004, 1);
                    break;
                case 2:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2280005, 1);
                    break;
                case 3:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2280006, 1);
                    break;
                case 4:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2280007, 1);
                    break;
                case 5:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2280008, 1);
                    break;
                case 6:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2280009, 1);
                    break;
                case 7:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2280010, 1);
                    break;
                case 8:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2280012, 1);
                    break;
                case 9:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2280013, 1);
                    break;
                case 10:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2280014, 1);
                    break;
                case 11:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2280015, 1);
                    break;
                case 12:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2280016, 1);
                    break;
                case 13:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2280026, 1);
                    break;
                case 14:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2280027, 1);
                    break;
                case 15:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2280028, 1);
                    break;
                case 16:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2280029, 1);
                    break;
                case 17:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2280030, 1);
                    break;
					                case 18:
                    cm.sendOk("兌換成功！");
                    cm.gainItem(2280031, 1);
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