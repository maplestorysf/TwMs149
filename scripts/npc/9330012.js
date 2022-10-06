var points;
var status = -1;
var sel;

function start() {
	if (cm.getMapId() == 219000000) {
		cm.sendSimple("#b#L40##m219010000##l\r\n#L41##m219020000##l");
		cm.dispose();
		return;
	}
    //var record = cm.getCData("bossQuest");
    var record = cm.getQuestRecord(150001);
	//points = record == null ? "0" : record;
	points = record.getCustomData() == null ? "0" : record.getCustomData();

    cm.sendSimple("你想要#r挑戰 Boss #k或#r 兌換武器 #k嗎？\n\r #b#L3#調查我的 Boss Points#l#k \n\r #b#L0#我要到 Boss 組隊任務地圖#l \n\r #b#L4##i1492023:#(120,000 Boss Points)#l#k \n\r #b#L5##i1472068:#(120,000 Boss Points)#l#k \n\r #b#L6##i1462050:#(120,000 Boss Points)#l#k \n\r #b#L7##i1452057:#(120,000 Boss Points)#l#k \n\r #b#L8##i1432047:#(120,000 Boss Points)#l#k \n\r #b#L9##i1382057:#(120,000 Boss Points)#l#k \n\r #b#L10##i1372044:#(120,000 Boss Points)#l#k \n\r #b#L11##i1332074:#(120,000 Boss Points)#l#k \n\r #b#L12##i1332073:#(120,000 Boss Points)#l#k \n\r #b#L13##i1482023:#(120,000 Boss Points)#l#k \n\r #b#L14##i1442063:#(120,000 Boss Points)#l#k \n\r #b#L15##i1422037:#(120,000 Boss Points)#l#k \n\r #b#L16##i1412033:#(120,000 Boss Points)#l#k \n\r #b#L17##i1402046:#(120,000 Boss Points)#l#k \n\r #b#L18##i1322060:#(120,000 Boss Points)#l#k \n\r #b#L19##i1312037:#(120,000 Boss Points)#l#k \n\r #b#L20##i1302081:#(120,000 Boss Points)#l#k \n\r #b#L31##i1342011:#(120,000 Boss Points)#l#k \n\r #b#L34##i1532015:#(120,000 Boss Points)#l#k \n\r #b#L22# #i1122017:#(30,000 Boss Points)#l#k \n\r #b#L27##i2340000:#(75,000 Boss Points)#l#k \n\r #b#L29##i5490001:#(15,000 Boss Points)#l#k \n\r #b#L30##i5490000:#(30,000 Boss Points)#l\n\r #b#L38##i2530000:#(75,000 Boss Points)#l \n\r #b#L39##i2531000:#(150,000 Boss Points)#l#k");

}

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
	if (status == 0) {
	sel = selection;
	switch (selection) {
	    case 0:
	    case 1:
	    case 2:
	    case 28:
		cm.warp(980010000,0);
		break;
	    case 3:
		cm.sendOk("#b我的 Boss Points : " + points);
		break;
	    case 4: // Timeless Blindness
		var intPoints = parseInt(points);

		if (intPoints >= 120000) {
		    if (cm.canHold(1492023)) {
			intPoints -= 120000;
			cm.setCData("bossQuest", ""+intPoints+"");
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
		var intPoints = parseInt(points);

		if (intPoints >= 120000) {
		    if (cm.canHold(1472068)) {
			intPoints -= 120000;
			cm.setCData("bossQuest", ""+intPoints+"");
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
		var intPoints = parseInt(points);

		if (intPoints >= 120000) {
		    if (cm.canHold(1462050)) {
			intPoints -= 120000;
			cm.setCData("bossQuest", ""+intPoints+"");
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
		var intPoints = parseInt(points);

		if (intPoints >= 120000) {
		    if (cm.canHold(1452057)) {
			intPoints -= 120000;
			cm.setCData("bossQuest", ""+intPoints+"");
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
		var intPoints = parseInt(points);

		if (intPoints >= 120000) {
		    if (cm.canHold(1432047)) {
			intPoints -= 120000;
			cm.setCData("bossQuest", ""+intPoints+"");
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
		var intPoints = parseInt(points);

		if (intPoints >= 120000) {
		    if (cm.canHold(1382057)) {
			intPoints -= 120000;
			cm.setCData("bossQuest", ""+intPoints+"");
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
		var intPoints = parseInt(points);

		if (intPoints >= 120000) {
		    if (cm.canHold(1372044)) {
			intPoints -= 120000;
			cm.setCData("bossQuest", ""+intPoints+"");
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
		var intPoints = parseInt(points);

		if (intPoints >= 120000) {
		    if (cm.canHold(1332074)) {
			intPoints -= 120000;
			cm.setCData("bossQuest", ""+intPoints+"");
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
		var intPoints = parseInt(points);

		if (intPoints >= 120000) {
		    if (cm.canHold(1332073)) {
			intPoints -= 120000;
			cm.setCData("bossQuest", ""+intPoints+"");
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
		var intPoints = parseInt(points);

		if (intPoints >= 120000) {
		    if (cm.canHold(1482023)) {
			intPoints -= 120000;
			cm.setCData("bossQuest", ""+intPoints+"");
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
		var intPoints = parseInt(points);

		if (intPoints >= 120000) {
		    if (cm.canHold(1442063)) {
			intPoints -= 120000;
			cm.setCData("bossQuest", ""+intPoints+"");
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
		var intPoints = parseInt(points);

		if (intPoints >= 120000) {
		    if (cm.canHold(1422037)) {
			intPoints -= 120000;
			cm.setCData("bossQuest", ""+intPoints+"");
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
		var intPoints = parseInt(points);

		if (intPoints >= 120000) {
		    if (cm.canHold(1412033)) {
			intPoints -= 120000;
			cm.setCData("bossQuest", ""+intPoints+"");
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
		var intPoints = parseInt(points);

		if (intPoints >= 120000) {
		    if (cm.canHold(1402046)) {
			intPoints -= 120000;
			cm.setCData("bossQuest", ""+intPoints+"");
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
		var intPoints = parseInt(points);

		if (intPoints >= 120000) {
		    if (cm.canHold(1322060)) {
			intPoints -= 120000;
			cm.setCData("bossQuest", ""+intPoints+"");
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
		var intPoints = parseInt(points);

		if (intPoints >= 120000) {
		    if (cm.canHold(1312037)) {
			intPoints -= 1312037;
			cm.setCData("bossQuest", ""+intPoints+"");
			cm.gainItem(2049100, 1);
			cm.sendOk("兌換成功。");
		    } else {
			cm.sendOk("請檢查道具欄位空間。")
		    }
		} else {
		    cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
		}
		break;
	    case 20: // Timeless Executioners
		var intPoints = parseInt(points);

		if (intPoints >= 120000) {
		    if (cm.canHold(1302081)) {
			intPoints -= 120000;
			cm.setCData("bossQuest", ""+intPoints+"");
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
		var intPoints = parseInt(points);

		if (intPoints >= 125000) {
		    if (cm.canHold(2070018)) {
			intPoints -= 125000;
			cm.setCData("bossQuest", ""+intPoints+"");
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
		var intPoints = parseInt(points);

		if (intPoints >= 30000) {
		    if (cm.canHold(1122017)) {
			intPoints -= 30000;
			cm.setCData("bossQuest", ""+intPoints+"");
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
		var intPoints = parseInt(points);

		if (intPoints >= 75000) {
		    if (cm.canHold(2340000)) {
			intPoints -= 75000;
			cm.setCData("bossQuest", ""+intPoints+"");
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
		var intPoints = parseInt(points);

		if (intPoints >= 15000) {
		    if (cm.canHold(5490001)) {
			intPoints -= 15000;
			cm.setCData("bossQuest", ""+intPoints+"");
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
		var intPoints = parseInt(points);

		if (intPoints >= 30000) {
		    if (cm.canHold(5490000)) {
			intPoints -= 30000;
			cm.setCData("bossQuest", ""+intPoints+"");
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
		var intPoints = parseInt(points);

		if (intPoints >= 120000) {
		    if (cm.canHold(1342011)) {
			intPoints -= 120000;
			cm.setCData("bossQuest", ""+intPoints+"");
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
		var intPoints = parseInt(points);

		if (intPoints >= 120000) {
		    if (cm.canHold(1532015)) {
			intPoints -= 120000;
			cm.setCData("bossQuest", ""+intPoints+"");
			cm.gainItem(1532015, 1);
			cm.sendOk("兌換成功。");
		    } else {
			cm.sendOk("請檢查道具欄位空間。")
		    }
		} else {
		    cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
		}
		break;	
	    case 32: //Enchanted Scroll
		var intPoints = parseInt(points);
	        if (intPoints < 1000) {
		    cm.sendOk("You need at least 1000 points for an Enchanted Scroll.");
		    cm.dispose();
	        } else {
		    cm.sendGetNumber("How many would you like? (1 Enchanted Scroll = 1000 points) (Current Points: " + intPoints + ") (Current Scrolls: " + cm.getPlayer().itemQuantity(5221001) + ")", intPoints / 1000, 1, intPoints / 1000);
	        }
		break;
	    case 33: //points
		var intPoints = parseInt(points);
	        if (!cm.getPlayer().haveItem(5221001)) {
		    cm.sendOk("You need at least 1 Enchanted Scroll.");
		    cm.dispose();
	        } else {
		    cm.sendGetNumber("How many would you like to redeem? (1 Enchanted Scroll = 1000 points) (Current Scrolls: " + cm.getPlayer().itemQuantity(5221001) + ") (Current Points: " + intPoints + ")", cm.getPlayer().itemQuantity(5221001), 1, cm.getPlayer().itemQuantity(5221001));
	        }
		break;
	    case 36: //item
		var intPoints = parseInt(points);
	        if (intPoints < 10000) {
		    cm.sendOk("You need at least 10000 points for a Bamboo Luck Sack.");
		    cm.dispose();
	        } else {
		    cm.sendGetNumber("How many would you like? (1 Bamboo Luck Sack = 10000 points) (Current Points: " + intPoints + ") (Current:  " + cm.getPlayer().itemQuantity(3993002) + ")", intPoints / 10000, 1, intPoints / 10000);
	        }
		break;
	    case 37: //points
		var intPoints = parseInt(points);
	        if (!cm.getPlayer().haveItem(3993002)) {
		    cm.sendOk("You need at least 1 Bamboo Luck Sack.");
		    cm.dispose();
	        } else {
		    cm.sendGetNumber("How many would you like to redeem? (1 Bamboo Luck Sack = 10000 points) (Current: " + cm.getPlayer().itemQuantity(3993002) + ") (Current Points: " + intPoints + ")", cm.getPlayer().itemQuantity(3993002), 1, cm.getPlayer().itemQuantity(3993002));
	        }
		break;
	    case 38:
		var intPoints = parseInt(points);

		if (intPoints >= 75000) {
		    if (cm.canHold(2530000)) {
			intPoints -= 75000;
			cm.setCData("bossQuest", ""+intPoints+"");
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
		var intPoints = parseInt(points);

		if (intPoints >= 150000) {
		    if (cm.canHold(2531000)) {
			intPoints -= 150000;
			cm.setCData("bossQuest", ""+intPoints+"");
			cm.gainItem(2531000, 1);
			cm.sendOk("兌換成功。");
		    } else {
			cm.sendOk("請檢查道具欄位空間。")
		    }
		} else {
		    cm.sendOk("請檢查Boss點數數量，#b目前Boss點數 : " + points);
		}
		break;	
	    case 35: // Crystal Ilbi
		var intPoints = parseInt(points);

		if (intPoints >= 75000) {
		    if (cm.canHold(2070016)) {
			intPoints -= 75000;
			cm.setCData("bossQuest", ""+intPoints+"");
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
			cm.warp(219010000,0);
			break;
		case 41:
			cm.warp(219020000,0);
			break;
	}
	} else {
	    var intPoints = parseInt(points);
	    if (sel == 32) {
		if (selection >= 1 && selection <= (intPoints / 1000)) {
			if (selection > (intPoints / 1000)) {
				cm.sendOk("You can only get max " + (intPoints / 1000) + " Scrolls. 1 Scroll = 1000 points.");
			} else if (!cm.canHold(5221001, selection)) {
				cm.sendOk("Please make space in CASH tab.");
			} else {
				cm.gainItem(5221001, selection);
				intPoints -= selection * 1000;
				cm.setCData("bossQuest", ""+intPoints+"");
				cm.sendOk("You have gained " + selection + " Scrolls and lost " + (selection * 1000) + " points. Current Points: " + intPoints);
			}
		}
	    } else if (sel == 33) {
		if (selection >= 1 && selection <= cm.getPlayer().itemQuantity(5221001)) {
			if (intPoints > (2147483647 - (selection * 1000))) {
				cm.sendOk("You have too many points.");
			} else {
				cm.gainItem(5221001, -selection);
				intPoints += selection * 1000;
				cm.setCData("bossQuest", ""+intPoints+"");
				cm.sendOk("You have lost " + selection + " Scrolls and gained " + (selection * 1000) + " points. Current Points: " + intPoints);
			}
		} 
	    } else if (sel == 36) {
		if (selection >= 1 && selection <= (intPoints / 10000)) {
			if (selection > (intPoints / 10000)) {
				cm.sendOk("You can only get max " + (intPoints / 10000) + ". 1 Item = 10000 points.");
			} else if (!cm.canHold(3993002, selection)) {
				cm.sendOk("Please make space in SETUP tab.");
			} else {
				cm.gainItem(3993002, selection);
				intPoints -= selection * 10000;
				cm.setCData("bossQuest", ""+intPoints+"");
				cm.sendOk("You have gained " + selection + " and lost " + (selection * 10000) + " points. Current Points: " + intPoints);
			}
		}
	    } else if (sel == 37) {
		if (selection >= 1 && selection <= cm.getPlayer().itemQuantity(3993002)) {
			if (intPoints > (2147483647 - (selection *10000))) {
				cm.sendOk("You have too many points.");
			} else {
				cm.gainItem(3993002, -selection);
				intPoints += selection * 10000;
				cm.setCData("bossQuest", ""+intPoints+"");
				cm.sendOk("You have lost " + selection + " and gained " + (selection * 10000) + " points. Current Points: " + intPoints);
			}
		} 
	    }
	    cm.dispose();
	}
    }
    if (selection != 32 && selection != 33 && selection != 36 && selection != 37) {
        cm.dispose();
    }
}