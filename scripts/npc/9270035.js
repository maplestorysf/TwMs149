load('nashorn:mozilla_compat.js');
importpackage(Packages.constants);
var status = -1;
var selectionID;

function action(mode, type, selection) {
    switch (mode) {
        case 1:
            status++;
            break;
        default:
            status--;
            break;
    }
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
        cm.sendOk("#r楓之島#k地圖無法使用#b@npc#k指令。");
        cm.dispose();
        return;
    }
    var mapID;
    switch (status) {
        case 0:
            var content = "請選擇服務項目：\r\n";
            content += "#b#L0#地圖傳送#l#k\r\n";
            //content += "#L1#技能傳授#l\r\n";
            content += "#b#L2#兌換中心#l#k\r\n";
            //content += "#b#L3#地攤商人#l#k\r\n";
            content += "#b#L4#組隊任務#l#k\r\n";
            content += "#b#L6#領取獎勵#r(8月3日新建角色請使用此功能領取獎勵)#k#l#k\r\n";
            content += "#b#L5#惡魔殺手0轉技能#l#k\r\n";
            content += "#b#L8#怪物騎乘#r#k#l#k\r\n";
            //content += "#b#L10#雷克斯的土狼技能#l#k\r\n";
            content += "#b#L9#寵物達人#r#k#l#k\r\n";
            content += "#b#L7#贊助優惠#k#r(贊助優惠內容更新)#k#l\r\n";
			content += "#b#L11#兌換贊助點數#l#k\r\n";
			content += "#b#L12#GM專用#l#k\r\n";
            cm.sendSimple(content);
            break;
        case 1:
            switch (selection) {
                case 0:
                    selectionID = 0;
                    var content = "請選擇地圖：\r\n";
                    content += "#r";
                    content += "#L0#楓樹山丘(收集溫暖陽光)#l\r\n";
                    content += "#L18#白色聖誕節之丘(收集聖誕樹裝飾)#l\r\n";
                    content += "#L17#邱比特公園(收集火藥桶)#l\r\n";
                    content += "#L21#不夜城(收集香爐)#l\r\n";
                    content += "#k";
                    content += "#b";
                    content += "#L1#維多利亞港#l\r\n";
                    content += "#L2#弓箭手村#l\r\n";
                    content += "#L3#魔法森林#l\r\n";
                    content += "#L4#勇士之村#l\r\n"
                    content += "#L5#墮落城市#l\r\n";
                    content += "#L6#鯨魚號#l\r\n";
                    content += "#L19#耶雷弗#l\r\n";
                    content += "#L7#天空之城#l\r\n";
                    content += "#L8#冰原雪域#l\r\n";
                    content += "#L9#玩具城#l\r\n";
                    content += "#L20#地球防衛本部#l\r\n";
                    content += "#L10#神木村#l\r\n";
                    content += "#L11#時間神殿#l\r\n";
                    content += "#L12#納希沙漠#l\r\n";
                    content += "#L13#瑪迦提亞城#l\r\n";
                    content += "#L14#桃花仙境#l\r\n";
                    content += "#L15#靈藥幻境#l\r\n";
                    content += "#L16#埃德爾斯坦#l\r\n";
                    content += "#k";
                    cm.sendSimple(content);
                    break;
                case 1:
                    selectionID = 1;
                    cm.sendSimple("請選擇你想獲得的技能：\r\n#b#L15#怪物騎乘#l#k");
                    break;
                case 2:
                    selectionID = 2;
                    var content = "請選擇兌換項目：\r\n";
                    content += "#L0##b兌換楓葉武器#k#r(楓葉)#k#l\r\n";
                    content += "#L1##b兌換永恆武器#k#r(Boss Points)#k#l\r\n";
                    content += "#L2##b兌換許願樹組合#k#r(黃金楓葉)#k#l\r\n";
                    content += "#L3##b兌換母書#k#r(黃金楓葉)#k#l\r\n";
                    content += "#L4##b兌換技能書#k#r(黃金楓葉)#k#l\r\n";
                    content += "#L5##b兌換飛鏢#k#r(黃金楓葉)#k#l\r\n";
                    content += "#L6##b兌換鑰匙#k#r(黃金楓葉)#k#l\r\n";
                    content += "#L7##b兌換萬能療傷藥、魔法石、召喚石#k#r(黃金楓葉)#k#l\r\n";
                    content += "#L8##b放大鏡#k#r(1,000,000楓幣)(100個)#k#l\r\n";
                    content += "#L9##b獵人的幸運#k#r(300黃金楓葉)#k#l\r\n";
					content += "#L12##b獵人的幸運#k#r(3000楓葉)#k#l\r\n";
                    content += "#L10##b皇家美髮券#k#r(100楓葉點數)#k#l\r\n";
                    content += "#L11##b兌換奇幻方塊#k#r(60黃金楓葉)(1個)#k#l\r\n";
                    cm.sendSimple(content);
                    break;
                case 3:
                    cm.dispose();
                    cm.openShop(61);
                    break;
                case 4:
                    selectionID = 4;
                    var content = "請選擇組隊任務：\r\n";
                    content += "#L0##b月妙的年糕#k#r(20~69級)(2~6人)#k#l\r\n";
                    content += "#L1##b第一次同行#k#r(20~69級)(2~6人)#l\r\n";
                    content += "#L2##b時空的裂縫#k#r(20~69級)(3~6人)#k#l\r\n";
                    content += "#L3##b女神的痕跡#k#r(70~119級)(2~6人)#k#l\r\n";
                    content += "#L4##b金勾海賊王#k#r(70~119級)(2~6人)#k#l\r\n";
                    content += "#L5##b毒霧森林#k#r(70~119級)(2~6人)#k#l\r\n";
                    content += "#L6##b武林妖僧#k#r(90~100級)(3~6人)#k#l\r\n";
                    content += "#L7##bBoss組隊任務#k#r(熱門)(1~6人)#k#l\r\n";
                    cm.sendSimple(content);
                    break;
                case 5:
                    if (cm.getJob() == 3001 || cm.getJob() == 3100 || cm.getJob() == 3110 || cm.getJob() == 3111 || cm.getJob() == 3112) {
                        cm.teachSkill(30011000, 3, 3);
                        cm.teachSkill(30011001, 3, 3);
                        cm.teachSkill(30010002, 3, 3);
                    } else {
                        cm.sendOk("非惡魔殺手無法使用。");
                    }
                    break;
                case 6:
                    cm.dispose();
                    cm.openNpc(9330087);
                    break;
                case 7:
                    var list = "#r贊助採用累積制度。#k\r\n\r\n";
                    list += "贊助#r300#k獎勵：(#r1500楓葉點數#k)(#r10,000贊助點數#k)\r\n";
                    list += "#i2049700:# (#r三張#k)";
                    list += "#i2049300:# (#r十張#k)\r\n\r\n";
                    list += "贊助#r600#k獎勵：(#r3000楓葉點數#k)(#r20,000贊助點數#k)\r\n";
                    list += "#b250個黃金楓葉#k+#bVIP武器自選#k+#b80%戒指#k\r\n\r\n";
                    list += "贊助#r1000#k獎勵：(#r5000楓葉點數#k)(#r30,000贊助點數#k)\r\n";
                    list += "#b500個黃金楓葉#k+#b我是幸運兒#k+#b傳說中的勇士勳章#k+#b加倍卷#k+#b不滅武器卷7張#k\r\n\r\n"; // 300
                    list += "贊助#r3000#k獎勵：(#r15000楓葉點數#k)(#r80,000贊助點數#k)\r\n";
                    list += "#b1500個黃金楓葉#k、#b楓心(40攻)#k+#b不滅防具或飾品卷軸自選21張#k\r\n\r\n"; // 300
                    list += "贊助#r5000#k獎勵：(#r25000楓葉點數#k)(#r120,000贊助點數#k)\r\n";
                    list += "#b2500個黃金楓葉#k+#b女皇整套#k+#b30個傳說方塊#k\r\n\r\n"; // 300
                    list += "贊助#r10000#k獎勵：(#r50000楓葉點數#k)(#r180,000贊助點數#k)\r\n";
                    list += "#b任君挑選#k"; // 300
                    cm.sendOk(list);
                    status = -1;
                    break;
                case 8: // 怪物騎乘
                    cm.sendOk("技能索取成功。");
					var skillID = 1136;
                    var job = cm.getPlayer().getJob();
                    if (GameConstants.isKOC(job)) {
                        skillID += 10000000;
                    } else if (GameConstants.isAran(job)) {
                        skillID += 20000000;
                    } else if (GameConstants.isEvan(job)) {
                        skillID += 20010000;
                    } else if (GameConstants.isMercedes(job)) {
                        skillID += 20020000;
                    } else if (GameConstants.isDemon(job)) {
                        skillID += 30010000;
                    } else if (GameConstants.isResist(job)) {
                        skillID += 30000000;
                    }
                    cm.teachSkill(80001000, 1, 0);
                    cm.dispose();
                    break;
                case 9: // 寵物達人
                    cm.sendOk("技能索取成功。");
                    var skillID = 8;
                    var job = cm.getPlayer().getJob();
                    if (GameConstants.isKOC(job)) {
                        skillID += 10000000;
                    } else if (GameConstants.isAran(job)) {
                        skillID += 20000000;
                    } else if (GameConstants.isEvan(job)) {
                        skillID += 20010000;
                    } else if (GameConstants.isMercedes(job)) {
                        skillID += 20020000;
                    } else if (GameConstants.isDemon(job)) {
                        skillID = 30011024;
                    } else if (GameConstants.isResist(job)) {
                        skillID += 30000000;
                    }
                    cm.teachSkill(8, 1, 1);
                    cm.dispose();
                    break;
                case 10:
                    if (cm.getPlayer().getName() != "雪吉拉GM") {
                        cm.sendOk("尚未開放。");
                        cm.dispose();
                        return;
                    }
                    if (!cm.haveItem(2430335, 1)) {
                        cm.sendOk("尚未取得#b雷克斯的土狼交換券#k，無法交換。");
                        cm.dispose();
                        return;
                    }
                    cm.sendOk("技能索取成功。");
                    cm.teachSkill(80001118, 1, 1);
                    cm.dispose();
                    break;
				case 11:
					cm.dispose();
					cm.openNpc(9000039);
					break;
				case 12:
				    if (cm.getPlayer().getName() != "雪吉拉GM") {
                        cm.sendOk("非管理員無法使用。");
                        cm.dispose();
                        return;
                    }
					cm.dispose();
					cm.teachSkill(5011001, 20, 0);
					break;
            }
            break;
        case 2:
            if (selectionID == 0) {
                switch (selection) {
                    case 0:
                        mapID = 970010000;
                        break;
                    case 1:
                        mapID = 104000000;
                        break;
                    case 2:
                        mapID = 100000000;
                        break;
                    case 3:
                        mapID = 101000000;
                        break;
                    case 4:
                        mapID = 102000000;
                        break;
                    case 5:
                        mapID = 103000000;
                        break;
                    case 6:
                        mapID = 120000000;
                        break;
                    case 7:
                        mapID = 200000000;
                        break;
                    case 8:
                        mapID = 211000000;
                        break;
                    case 9:
                        mapID = 220000000;
                        break;
                    case 10:
                        mapID = 240000000;
                        break;
                    case 11:
                        mapID = 270000100;
                        break;
                    case 12:
                        mapID = 260000000;
                        break;
                    case 13:
                        mapID = 261000000;
                        break;
                    case 14:
                        mapID = 250000000;
                        break;
                    case 15:
                        mapID = 251000000;
                        break;
                    case 16:
                        mapID = 310000000;
                        break;
                    case 17:
                        mapID = 100000200;
                        break;
                    case 18:
                        cm.saveLocation("CHRISTMAS");
                        mapID = 555000000;
                        break;
                    case 19:
                        mapID = 130000000;
                        break;
                    case 20:
                        mapID = 221000000;
                        break;
                    case 21:
                        mapID = 741000000;
                        break;
                    default:
                        mapID = 100000000;
                        break;
                }
                cm.warp(mapID, 0);
                cm.dispose();
            } else if (selectionID == 2) {
                switch (selection) {
                    case 0:
                        cm.dispose();
                        cm.openNpc(9330082);
                        break;
                    case 1:
                        cm.dispose();
                        cm.openNpc(9001000);
                        break;
                    case 2:
                        cm.dispose();
                        cm.openNpc(9330026);
                        break;
                    case 3:
                        cm.dispose();
                        cm.openNpc(9330030);
                        break;
                    case 4:
                        cm.dispose();
                        cm.openNpc(9330073);
                        break;
                    case 5:
                        cm.dispose();
                        cm.openNpc(9330019);
                        break;
                    case 6:
                        cm.dispose();
                        cm.openNpc(9300003);
                        break;
                    case 7:
                        cm.dispose();
                        cm.openNpc(9300004);
                        break;
                    case 8:
                        if (cm.getMeso() < 1000000) {
                            cm.sendOk("請檢查楓幣數量。");
                            cm.dispose();
                            return;
                        }
                        cm.gainMeso(-1000000);
                        cm.gainItem(2460003, 100);
                        cm.dispose();
                        break;
                    case 9:
                        if (!cm.haveItem(4001168, 300) || !cm.canHold(2450000, 1)) {
                            cm.sendOk("請檢查黃金楓葉數量和道具欄位空間。");
                            cm.dispose();
                            return;
                        }
                        cm.gainItem(4001168, -300);
                        cm.gainItem(2450000, 1);
                        cm.dispose();
                        break;
                    case 10:
                        if ( /*cm.getPlayer().getCSPoints(0) < 100 ||*/ cm.getPlayer().getCSPoints(1) < 100) {
                            cm.sendOk("請檢查#樂豆點數#k或#b楓葉點數#k數量。");
                            return;
                        }
                        if (cm.canHold(5150040, 1)) {
                            cm.gainNX(-100);
                            cm.gainItem(5150040, 1);
                        } else {
                            cm.sendOk("請檢查道具欄位空間。");
                        }
                        cm.dispose();
                        break;
                    case 11:
                        if (!cm.haveItem(4001168, 60)) {
                            cm.sendOk("請檢查#黃金楓葉#k數量。");
                            cm.dispose();
                            return;
                        }
                        if (cm.canHold(5150040, 1)) {
                            cm.gainItem(4001168, -60);
                            cm.gainItem(5062000, 1);
                        } else {
                            cm.sendOk("請檢查道具欄位空間。");
                        }
                        cm.dispose();
                        break;
					case 12:
                        if (!cm.haveItem(4001126, 3000) || !cm.canHold(2450000, 1)) {
                            cm.sendOk("請檢查楓葉數量和道具欄位空間。");
                            cm.dispose();
                            return;
                        }
                        cm.gainItem(4001126, -3000);
                        cm.gainItem(2450000, 1);
                        cm.dispose();
                        break;
                    default:
                        cm.dispose();
                        break;
                }
            } else if (selectionID == 4) {
                switch (selection) {
                    case 0:
                        mapID = 910010500;
                        break;
                    case 1:
                        mapID = 910340700;
                        break;
                    case 2:
                        mapID = 221023300;
                        break;
                    case 3:
                        mapID = 200080101;
                        break;
                    case 4:
                        mapID = 251010404;
                        break;
                    case 5:
                        mapID = 300030100;
                        break;
                    case 6:
                        mapID = 702070400;
                        break;
                    case 7:
                        mapID = 980010000;
                        break;
                    default:
                        mapID = 100000000;
                        break;
                }
                cm.warp(mapID, 0);
                cm.dispose();
            } else {
                cm.dispose();
            }
            break;
        default:
            cm.dispose();
            break;
    }
}