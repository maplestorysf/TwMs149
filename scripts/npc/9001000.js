var points;
var status = -1;
var sel;

function start() {
	if (cm.getMapId() == 219000000) {
		cm.sendSimple("#b#L40##m219010000##l\r\n#L41##m219020000##l");
		cm.dispose();
		return;
	}
	var record = cm.getQuestRecord(150001);
	points = record.getCustomData() == null ? "0" : record.getCustomData();
	cm.sendSimple("你想要挑戰 Boss 嗎？\n\r\n\r #b#L3#調查我的BossPQ點數#l#k \n\r #b#L0#傳送至大廳#l#k \n\r #b#L36#使用1萬BossPQ點數兌換#v3993002##l#k  \n\r #b#L37#使用#v3993002#兌換1萬BossPQ點數#l#k \n\r #b#L4##i1492023:#(120,000BossPQ點數)#l#k \n\r #b#L5##i1472068:#(120,000BossPQ點數)#l#k \n\r #b#L6##i1462050:#(120,000BossPQ點數)#l#k \n\r #b#L7##i1452057:#(120,000BossPQ點數)#l#k \n\r #b#L8##i1432047:#(120,000BossPQ點數)#l#k \n\r #b#L9##i1382057:#(120,000BossPQ點數)#l#k \n\r #b#L10##i1372044:#(120,000BossPQ點數)#l#k \n\r #b#L11##i1332074:#(120,000BossPQ點數)#l#k \n\r #b#L12##i1332073:#(120,000BossPQ點數)#l#k \n\r #b#L13##i1482023:#(120,000BossPQ點數)#l#k \n\r #b#L14##i1442063:#(120,000BossPQ點數)#l#k \n\r #b#L15##i1422037:#(120,000BossPQ點數)#l#k \n\r #b#L16##i1412033:#(120,000BossPQ點數)#l#k \n\r #b#L17##i1402046:#(120,000BossPQ點數)#l#k \n\r #b#L18##i1322060:#(120,000BossPQ點數)#l#k \n\r #b#L19##i1312037:#(120,000BossPQ點數)#l#k \n\r #b#L20##i1302081:#(120,000BossPQ點數)#l#k \n\r #b#L31##i1342011:#(120,000BossPQ點數)#l#k \n\r #b#L34##i1532015:#(120,000BossPQ點數)#l#k \n\r #b#L42##i1522015:#(120,000BossPQ點數)#l#k \n\r #b#L22# #i1122017:#(30,000BossPQ點數)#l#k \n\r #b#L27##i2340000:#(75,000BossPQ點數)#l#k \n\r #b#L29##i5490001:#(15,000BossPQ點數)#l#k \n\r #b#L30##i5490000:#(30,000BossPQ點數)#l\n\r #b#L38##i2530000:#(75,000BossPQ點數)#l \n\r #b#L39##i2531000:#(150,000BossPQ點數)#l#k");

}

function action(mode, type, selection) {
	var record = cm.getQuestRecord(150001);
	var intPoints = parseInt(points);
	if (mode == 1) {
		status++;
		if (status == 0) {
			sel = selection;

			switch (selection) {
			case 0:
			case 1:
			case 2:
			case 28:
				cm.warp(980010000, 0);
				break;
			case 3:
				cm.sendOk("#b我的BossPQ點數 : " + points);
				break;
			case 4: // Timeless Blindness
				if (intPoints >= 120000) {
					if (cm.canHold(1492023)) {
						intPoints -= 120000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(1492023, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 5: // Timeless Lampion
				if (intPoints >= 120000) {
					if (cm.canHold(1472068)) {
						intPoints -= 120000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(1472068, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 6: // Timeless Black Beauty
				if (intPoints >= 120000) {
					if (cm.canHold(1462050)) {
						intPoints -= 120000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(1462050, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 7: // Timeless Engaw
				if (intPoints >= 120000) {
					if (cm.canHold(1452057)) {
						intPoints -= 120000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(1452057, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 8: // Timeless Alchupiz
				if (intPoints >= 120000) {
					if (cm.canHold(1432047)) {
						intPoints -= 120000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(1432047, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 9: // Timeless Aeas Hand
				if (intPoints >= 120000) {
					if (cm.canHold(1382057)) {
						intPoints -= 120000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(1382057, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 10: // Timeless Enreal Tear
				if (intPoints >= 120000) {
					if (cm.canHold(1372044)) {
						intPoints -= 120000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(1372044, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 11: // Timeless Killic
				if (intPoints >= 120000) {
					if (cm.canHold(1332074)) {
						intPoints -= 120000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(1332074, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 12: // Timeless Pescas
				if (intPoints >= 120000) {
					if (cm.canHold(1332073)) {
						intPoints -= 120000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(1332073, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 13: // Timeless Equinox
				if (intPoints >= 120000) {
					if (cm.canHold(1482023)) {
						intPoints -= 120000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(1482023, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 14: // Timeless Diesra
				if (intPoints >= 120000) {
					if (cm.canHold(1442063)) {
						intPoints -= 120000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(1442063, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 15: // Timeless Bellocce
				if (intPoints >= 120000) {
					if (cm.canHold(1422037)) {
						intPoints -= 120000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(1422037, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 16: // Timeless Tabarzin
				if (intPoints >= 120000) {
					if (cm.canHold(1412033)) {
						intPoints -= 120000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(1412033, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 17: // Timeless Nibleheim
				if (intPoints >= 120000) {
					if (cm.canHold(1402046)) {
						intPoints -= 120000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(1402046, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 18: // Timeless Allargando
				if (intPoints >= 120000) {
					if (cm.canHold(1322060)) {
						intPoints -= 120000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(1322060, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 19: // Timeless Bardiche
				if (intPoints >= 120000) {
					if (cm.canHold(1312037)) {
						intPoints -= 120000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(1312037, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 20: // Timeless Executioners
				if (intPoints >= 120000) {
					if (cm.canHold(1302081)) {
						intPoints -= 120000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(1302081, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 21: // Balanced Fury
				if (intPoints >= 125000) {
					if (cm.canHold(2070018)) {
						intPoints -= 125000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(2070018, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 22: // Fairy Pendant
				if (intPoints >= 30000) {
					if (cm.canHold(1122017)) {
						intPoints -= 30000;
						record.setCustomData("" + intPoints + "");
						cm.gainItemPeriod(1122017, 1, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 27:
				if (intPoints >= 75000) {
					if (cm.canHold(2340000)) {
						intPoints -= 75000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(2340000, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 29:
				if (intPoints >= 15000) {
					if (cm.canHold(5490001)) {
						intPoints -= 15000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(5490001, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 30:
				if (intPoints >= 30000) {
					if (cm.canHold(5490000)) {
						intPoints -= 30000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(5490000, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 31: // Timeless Katara
				if (intPoints >= 120000) {
					if (cm.canHold(1342011)) {
						intPoints -= 120000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(1342011, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 34: // Obliterator
				if (intPoints >= 120000) {
					if (cm.canHold(1532015)) {
						intPoints -= 120000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(1532015, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 36: //item
				if (intPoints < 10000) {
					cm.sendOk("您沒有BossPQ點數.");
					cm.dispose();
				} else {
					cm.sendGetNumber("您藥用多少的BossPQ點數兌換#i3993002#\r\n現在比值 1萬BossPQ點數 : 1個#v3993002# " + "\r\n您目前有 " + cm.getPlayer().itemQuantity(3993002) + "個#v3993002#  和 " + intPoints + "BossPQ點數", 1, 1, 1000);
				}
				break;
			case 37: //points
				if (!cm.getPlayer().haveItem(3993002)) {
					cm.sendOk("您沒有任何的#v3993002#");
					cm.dispose();
				} else {
					cm.sendGetNumber("您要用多少的#i3993002#兌換BossPQ點數  \r\n現在比值 1 個#v3993002# : 1萬BossPQ點數\r\n您目前有 " + cm.getPlayer().itemQuantity(3993002) + "個#v3993002#  和 " + intPoints + "BossPQ點數", 1, 1, 1000);
				}
				break;
			case 38:
				if (intPoints >= 75000) {
					if (cm.canHold(2530000)) {
						intPoints -= 75000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(2530000, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 39:
				if (intPoints >= 150000) {
					if (cm.canHold(2531000)) {
						intPoints -= 150000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(2531000, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 42: // Obliterator
				if (intPoints >= 120000) {
					if (cm.canHold(1522015)) {
						intPoints -= 120000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(1522015, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 35: // Crystal Ilbi
				if (intPoints >= 75000) {
					if (cm.canHold(2070016)) {
						intPoints -= 75000;
						record.setCustomData("" + intPoints + "");
						cm.gainItem(2070016, 1);
						cm.sendOk("兌換成功。");
					} else {
						cm.sendOk("請檢查道具欄位空間。")
					}
				} else {
					cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
				}
				break;
			case 40:
				cm.warp(219010000, 0);
				break;
			case 41:
				cm.warp(219020000, 0);
				break;
			}
		} else {
			number = selection;
			if (sel == 36) {
				if (number >= 1 && number <= (intPoints / 10000)) {
					if (number > (intPoints / 10000)) {
						cm.sendOk("您最多只能拿到 " + (intPoints / 10000));
					} else if (!cm.canHold(3993002, number)) {
						cm.sendOk("請空出一些裝飾欄空間");
					} else {
						cm.gainItem(3993002, number);
						intPoints -= (number * 10000);
						record.setCustomData("" + intPoints + "");
						cm.sendOk("完成! 請查看你的點數。\r\n您目前有 " + cm.getPlayer().itemQuantity(3993002) + "個#v3993002#  和 " + intPoints + "BossPQ點數");
					}
				} else {
					cm.sendNext("發生未知的錯誤...");
				}
			} else if (sel == 37) {
				if (number >= 1 && number <= cm.getPlayer().itemQuantity(3993002)) {
					if (intPoints > (2100000000 - (number * 10000)) || intPoints >= 2100000000) {
						cm.sendOk("您的點數過多...");
					} else {
						cm.gainItem(3993002, -number);
						intPoints += (number * 10000);
						record.setCustomData("" + intPoints + "");
						cm.sendOk("完成! 請查看你的點數。\r\n您目前有 " + cm.getPlayer().itemQuantity(3993002) + "個#v3993002#  和 " + intPoints + "BossPQ點數");
					}
				} else {
					cm.sendNext("發生未知的錯誤...");
				}
			}
			cm.dispose();
		}
	}
	if (selection != 32 && selection != 33 && selection != 36 && selection != 37) {
		cm.dispose();
	}
}
