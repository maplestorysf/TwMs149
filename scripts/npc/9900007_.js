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
     //if (cm.getPlayer().getJob() != 0 && cm.getPlayer().getJob() != 2300 && cm.getPlayer().getJob() != 2310 && cm.getPlayer().getJob() != 2311) {
     //    cm.sendOk("非#r初心者#k無法使用#b轉職#k指令。");
     //    cm.dispose();
     //    return;
     //}
     if (cm.getPlayer().getMapId() == 10000 ||
         cm.getPlayer().getMapId() == 20000 ||
         cm.getPlayer().getMapId() == 30000 ||
         cm.getPlayer().getMapId() == 30001 ||
         cm.getPlayer().getMapId() == 40000 ||
         cm.getPlayer().getMapId() == 50000 ||
         cm.getPlayer().getMapId() == 1000000 ||
         cm.getPlayer().getMapId() == 1000001 ||
         cm.getPlayer().getMapId() == 1000002 ||
         cm.getPlayer().getMapId() == 1000003 ||
         cm.getPlayer().getMapId() == 1010000 ||
         cm.getPlayer().getMapId() == 1010100 ||
         cm.getPlayer().getMapId() == 1010200 ||
         cm.getPlayer().getMapId() == 1010300 ||
         cm.getPlayer().getMapId() == 1010400 ||
         cm.getPlayer().getMapId() == 1020000 ||
         cm.getPlayer().getMapId() == 2000000 ||
         cm.getPlayer().getMapId() == 2000001) {
         cm.sendOk("#r楓之島#k地圖無法使用#b@轉職#k指令。");
         cm.dispose();
         return;
     }
     switch (status) {
         case 0:
             var content = "請選擇職業：\r\n";
             content += "#b";
             content += "#L0#劍士(1轉)#l\r\n";
             content += "#L1#法師(1轉)#l\r\n";
             content += "#L2#弓箭手(1轉)#l\r\n";
             content += "#L3#盜賊(1轉)#l\r\n";
             content += "#L4#海盜(1轉)#l\r\n";
             content += "#L9#狂狼勇士(4轉)#l\r\n";
             content += "#L8#影武者(1轉、2轉、3轉、4轉、5轉、6轉)#l\r\n";
             content += "#L5#重砲指揮官(1轉、2轉、3轉、4轉)#l\r\n";
             content += "#L6#精靈遊俠(1轉、2轉、3轉、4轉)#l\r\n";
             content += "#L7#惡魔殺手(1轉、2轉、3轉、4轉)#l\r\n";
             content += "#k";
             cm.sendSimple(content);
             break;
         case 1:
             switch (selection) {
                 case 0:
                     if (cm.getJob() != 0) {
                         cm.sendOk("非#b初心者#k無法轉職。");
                         cm.dispose();
						 return;
                     }
                     cm.warp(102000003, 0);
                     cm.dispose();
                     break;
                 case 1:
					 if (cm.getJob() != 0) {
                         cm.sendOk("非#b初心者#k無法轉職。");
                         cm.dispose();
						 return;
                     }
                     cm.warp(101000003, 0);
                     cm.dispose();
                     break;
                 case 2:
					 if (cm.getJob() != 0) {
                         cm.sendOk("非#b初心者#k無法轉職。");
                         cm.dispose();
						 return;
                     }
                     cm.warp(100000201, 0);
                     cm.dispose();
                     break;
                 case 3:
					 if (cm.getJob() != 0) {
                         cm.sendOk("非#b初心者#k無法轉職。");
                         cm.dispose();
						 return;
                     }
                     cm.warp(103000003, 0);
                     cm.dispose();
                     break;
                 case 4:
					 if (cm.getJob() != 0) {
                         cm.sendOk("非#b初心者#k無法轉職。");
                         cm.dispose();
						 return;
                     }
                     cm.warp(120000101, 0);
                     cm.dispose();
                     break;
                 case 5: // 重砲指揮官
                     //cm.teachSkill(5011000, 20, 20);
                     //cm.teachSkill(5011001, 20, 20);
                     //cm.teachSkill(5011002, 15, 15);
                     switch (cm.getJob()) {
                         case 0:
                             if (cm.getPlayer().getLevel() >= 10) {
                                 cm.resetStats(4, 20, 4, 4);
                                 cm.expandInventory(1, 4);
                                 cm.expandInventory(4, 4);
                                 cm.gainItem(1532000, 1);
                                 cm.changeJob(501);
                             } else {
                                 cm.sendOk("未滿10級無法轉職。");
                             }
                             break;
                         case 501:
                             if (cm.getPlayer().getLevel() >= 30) {
                                 if (cm.canHold(1532004, 1)) {
                                     cm.gainItem(1532004, 1);
                                     cm.changeJob(530);
                                 } else {
                                     cm.sendOk("請空出一格裝備欄位。");
                                 }
                             } else {
                                 cm.sendOk("未滿30級無法轉職。");
                             }
                             break;
                         case 530:
                             if (cm.getPlayer().getLevel() >= 70) {
                                 if (cm.canHold(1532009, 1)) {
                                     cm.gainItem(1532009, 1);
                                     cm.changeJob(531);
                                 } else {
                                     cm.sendOk("請空出一格裝備欄位。");
                                 }
                             } else {
                                 cm.sendOk("未滿70級無法轉職。");
                             }
                             break;
                         case 531:
                             if (cm.getPlayer().getLevel() >= 120) {
                                 if (cm.canHold(1532016, 1)) {
                                     cm.gainItem(1532016, 1);
                                     cm.changeJob(532);
                                 } else {
                                     cm.sendOk("請空出一格裝備欄位。");
                                 }
                             } else {
                                 cm.sendOk("未滿120級無法轉職。");
                             }
                             break;
                         default:
                             cm.sendOk("非#b重砲指揮官#k無法進行轉職。");
                             break;
                     }
                     cm.dispose();
                     status = -1;
                     break;
                 case 6:
                     switch (cm.getJob()) {
                         case 0:
                             if (cm.getPlayer().getLevel() >= 10) {
                                 cm.resetStats(4, 25, 4, 4);
                                 cm.expandInventory(1, 4);
                                 cm.expandInventory(4, 4);
                                 cm.gainItem(1522000, 1);
                                 cm.gainItem(1352000, 1);
                                 cm.changeJob(2300);
                             } else {
                                 cm.sendOk("未滿10級無法轉職。");
                             }
                             break;
                         case 2300:
                             if (cm.getPlayer().getLevel() >= 30) {
                                 if (cm.canHold(1522004, 1) && cm.canHold(1352001, 1)) {
                                     cm.gainItem(1522004, 1);
                                     cm.gainItem(1352001, 1);
                                     cm.changeJob(2310);
                                 } else {
                                     cm.sendOk("請空出兩格裝備欄位。");
                                 }
                             } else {
                                 cm.sendOk("未滿30級無法轉職。");
                             }
                             break;
                         case 2310:
                             if (cm.getPlayer().getLevel() >= 70) {
                                 if (cm.canHold(1522009, 1) && cm.canHold(1352002, 1)) {
                                     cm.gainItem(1522009, 1);
                                     cm.gainItem(1352002, 1);
                                     cm.changeJob(2311);
                                 } else {
                                     cm.sendOk("請空出兩格裝備欄位。");
                                 }
                             } else {
                                 cm.sendOk("未滿70級無法轉職。");
                             }
                             break;
                         case 2311:
                             if (cm.getPlayer().getLevel() >= 120) {
                                 if (cm.canHold(1522016, 1) && cm.canHold(1352003, 1)) {
                                     cm.gainItem(1522016, 1);
                                     cm.gainItem(1352003, 1);
                                     cm.changeJob(2312);
                                 } else {
                                     cm.sendOk("請空出兩格裝備欄位。");
                                 }
                             } else {
                                 cm.sendOk("未滿120級無法轉職。");
                             }
                             break;
                         default:
                             cm.sendOk("非#b精靈遊俠#k無法進行轉職。");
                             break;
                     }
                     cm.dispose();
                     status = -1;
                     break;
                 case 7:
                     switch (cm.getJob()) {
                         case 0:
                             if (cm.getPlayer().getLevel() >= 10) {
                                 cm.resetStats(35, 4, 4, 4);
                                 cm.expandInventory(1, 4);
                                 cm.expandInventory(4, 4);
                                 cm.gainItem(1322122, 1);
                                 cm.changeJob(3100);
                             } else {
                                 cm.sendOk("未滿10級無法轉職。");
                             }
                             break;
                         case 3100:
                             if (cm.getPlayer().getLevel() >= 30) {
                                 if (cm.canHold(1322124, 1)) {
                                     cm.gainItem(1322124, 1);
                                     cm.changeJob(3110);
                                 } else {
                                     cm.sendOk("請空出一格裝備欄位。");
                                 }
                             } else {
                                 cm.sendOk("未滿30級無法轉職。");
                             }
                             break;
                         case 3110:
                             if (cm.getPlayer().getLevel() >= 70) {
                                 if (cm.canHold(1322126, 1)) {
                                     cm.gainItem(1322126, 1);
                                     cm.changeJob(3111);
                                 } else {
                                     cm.sendOk("請空出一格裝備欄位。");
                                 }
                             } else {
                                 cm.sendOk("未滿70級無法轉職。");
                             }
                             break;
                         case 3111:
                             if (cm.getPlayer().getLevel() >= 120) {
                                 if (cm.canHold(1322127, 1)) {
                                     cm.gainItem(1322127, 1);
                                     cm.changeJob(3112);
                                 } else {
                                     cm.sendOk("請空出一格裝備欄位。");
                                 }
                             } else {
                                 cm.sendOk("未滿120級無法轉職。");
                             }
                             break;
                         default:
                             cm.sendOk("非#b惡魔殺手#k無法進行轉職。");
                             break;
                     }
                     cm.dispose();
                     status = -1;
                     break;
                 case 8:
                     switch (cm.getJob()) {
                         case 0:
                             if (cm.getPlayer().getLevel() >= 10) {
                                 cm.resetStats(4, 25, 4, 4);
                                 cm.expandInventory(1, 4);
                                 cm.expandInventory(4, 4);
                                 cm.gainItem(1332063, 1);
                                 cm.changeJob(400);
                             } else {
                                 cm.sendOk("未滿10級無法轉職。");
                             }
                             break;
                         case 400:
                             if (cm.getPlayer().getLevel() >= 20) {
                                 if (cm.canHold(1342000, 1)) {
                                     cm.gainItem(1342000, 1);
                                     cm.changeJob(430);
                                 } else {
                                     cm.sendOk("請空出一格裝備欄位。");
                                 }
                             } else {
                                 cm.sendOk("未滿20級無法轉職。");
                             }
                             break;
                         case 430:
                             if (cm.getPlayer().getLevel() >= 30) {
                                 if (cm.canHold(1342001, 1)) {
                                     cm.gainItem(1342001, 1);
                                     cm.changeJob(431);
                                 } else {
                                     cm.sendOk("請空出一格裝備欄位。");
                                 }
                             } else {
                                 cm.sendOk("未滿30級無法轉職。");
                             }
                             break;
                         case 431:
                             if (cm.getPlayer().getLevel() >= 55) {
                                 if (cm.canHold(1342022, 1)) {
                                     cm.gainItem(1342022, 1);
                                     cm.changeJob(432);
                                 } else {
                                     cm.sendOk("請空出一格裝備欄位。");
                                 }
                             } else {
                                 cm.sendOk("未滿55級無法轉職。");
                             }
                             break;
                         case 432:
                             if (cm.getPlayer().getLevel() >= 70) {
                                 if (cm.canHold(1342005, 1)) {
                                     cm.gainItem(1342005, 1);
                                     cm.changeJob(433);
                                 } else {
                                     cm.sendOk("請空出一格裝備欄位。");
                                 }
                             } else {
                                 cm.sendOk("未滿70級無法轉職。");
                             }
                             break;
                         case 433:
                             if (cm.getPlayer().getLevel() >= 120) {
                                 if (cm.canHold(1342012, 1)) {
                                     cm.gainItem(1342012, 1);
                                     cm.changeJob(434);
                                 } else {
                                     cm.sendOk("請空出一格裝備欄位。");
                                 }
                             } else {
                                 cm.sendOk("未滿120級無法轉職。");
                             }
                             break;
                         default:
                             cm.sendOk("非#b影武者#k無法進行轉職。");
                             break;
                     }
                     cm.dispose();
                     status = -1;
                     break;
                 case 9:
					 //cm.teachSkill(21120005, 30, 30);
                     if (cm.getPlayer().getLevel() >= 120 && cm.getJob() == 2111) {
                         cm.changeJob(2112);
                         cm.sendOk("轉職成功！");
                     } else {
                         cm.sendOk("未滿120級或尚未三轉，因而無法進行轉職。");
                     }
                     cm.dispose();
                     break;
             }
             break;
         default:
             cm.dispose();
             break;
     }
 }