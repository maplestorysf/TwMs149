var debug = false;
var status = -1;
var item = 4031217;
var d = new Date();
var gift = ["Day7", "Day1", "Day2", "Day3", "Day4", "Day5", "Day6"];
var month = d.getMonth() + 1;
var date = d.getDate();
var day = d.getDay();

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
	
    if(debug && !cm.getPlayer().isAdmin()){
		cm.sendNext("本NPC目前維修中。");
		cm.dispose();
		return;
	}
	
	if (mode == 1) {
        status++;
    } else if (mode == 0) {
		status--;
    } else {
		cm.dispose();
		return;
	}
	
    if (status == 0) {
        cm.sendSimple("#b每日獎勵NPC 可以領取到1~500楓點\r\n" +
				//"#b贊助滿額禮請至粉絲團看物品!\r\n" +
				"#b未空出欄位物品遭吃不補償!\r\n" +
                "#rPS:當前時間: " + month + "/" + date + " 星期" + day + "\r\n" +
                "#L0##d簽到\r\n" +
                "#L1##d我不想簽到\r\n"+
				"#L2##d查看贊助金額\r\n"+
				"#L3##d領取10等獎勵\r\n"+
				"#L4##d領取30等獎勵\r\n"+
				"#L5##d領取70等獎勵\r\n"+
				"#L6##d領取120等獎勵\r\n"
				/*"#L7##d領取贊助1000滿額禮\r\n"+
				"#L8##d領取贊助3000滿額禮\r\n"+
				"#L9##d領取贊助5000滿額禮\r\n"+
				//"#L10##d補償\r\n*/
				)
    } else if (status == 1) {
         if (selection == 0) {
			if(cm.getPlayer().getLevel() < 9){
				cm.sendOk("10等才能使用簽到唷");
				cm.dispose();
				return;
			}
			var mprandom = Math.floor(Math.random() * 500 + 1);
            if (cm.getPlayer().getAcLog(gift[day]) < 1) {
                cm.getPlayer().setAcLog(gift[day]);
				cm.getPlayer().modifyCSPoints(2,mprandom, false);
				cm.sendOk("恭喜已簽到領取"+mprandom+"楓點");
                cm.worldMessage(6, "[轉蛋屋發出簽到訊息] " + " 玩家 " + cm.getChar().getName() + " 今天簽到領到 "+mprandom+" 楓點囉~還沒領的玩家快去!");
			//	FileoutputUtil.logToFile("logs/Data/每日點數.txt", "領取時間:" + FileoutputUtil.NowTime() + "角色名稱:" + cm.getPlayer().getName() + "角色等級: " + cm.getPlayer().getLevel() + "今天簽到領到 "+mprandom+" 楓點");
               // cm.gainItem(item, 1);
			   cm.dispose();
            } else {
                cm.sendOk("一天只能找我簽到一次喔");
				cm.dispose();
            }
        } else if (selection == 1) {
            cm.sendYesNo("#請問您真的不想簽到嗎?");
        } else if (selection == 2) {
			cm.sendOk("您已贊助"+ cm.getPlayer().getDonate() +"元");
			cm.dispose();
						return;
		} else if (selection == 3) {
			if (cm.getPlayer().getPrizeLog('10等') < 1 && cm.getPlayer().getLevel() >= 10) {
				cm.getPlayer().modifyCSPoints(2, 1688, false);
				cm.gainMeso(500000);
				cm.gainPet(5000007, "黑色小豬", 1, 0, 100, 0, 45);
				//cm.gainItem(1122017, 1);
				cm.getPlayer().setPrizeLog('10等');
				cm.sendOk("已領取10等獎勵!");
				cm.worldMessage(6, "[轉蛋屋發出等級獎勵] " + " 玩家 " + cm.getChar().getName() + " 領了10等獎勵!");
			} else {
				cm.sendOk("#d你已經領過囉或者沒有10等以上!!");
			}
			cm.dispose();
		}else if (selection == 4) {
			if (cm.getPlayer().getPrizeLog('30等') < 1 && cm.getPlayer().getLevel() >= 30) {
				cm.getPlayer().modifyCSPoints(2, 888, false);
				//cm.gainItem(3010172, 1);
				//cm.gainItem(5220040, 10); 
				cm.getPlayer().setPrizeLog('30等');
				cm.sendOk("已領取30等獎勵!");
				cm.worldMessage(6, "[轉蛋屋發出等級獎勵] " + " 玩家 " + cm.getChar().getName() + " 領了30等獎勵!");
			} else {
				cm.sendOk("#d你已經領過囉或者沒有30等以上!!");
			}
			cm.dispose();
		}else if (selection == 5) {
			if (cm.getPlayer().getPrizeLog('70等') < 1 && cm.getPlayer().getLevel() >= 70) {
				cm.getPlayer().modifyCSPoints(2, 888, false);
				//cm.gainItem(1902036, 1);
				//cm.gainItem(1912029, 1);
				//cm.gainItem(1112434, 1);
				cm.getPlayer().setPrizeLog('70等');
				cm.sendOk("已領取70等獎勵!");
				cm.worldMessage(6, "[轉蛋屋發出等級獎勵] " + " 玩家 " + cm.getChar().getName() + " 領了70等獎勵!");
			} else {
				cm.sendOk("#d你已經領過囉或者沒有70等以上!!");
			}
			cm.dispose();
		}else if (selection == 6) {
			if (cm.getPlayer().getPrizeLog('120等') < 1 && cm.getPlayer().getLevel() >= 120) {
				cm.getPlayer().modifyCSPoints(2, 1688, false);
				//cm.gainItem(1142636, 1);
				cm.getPlayer().setPrizeLog('120等');
				cm.sendOk("已領取120等獎勵!");
				cm.worldMessage(6, "[轉蛋屋發出等級獎勵] " + " 玩家 " + cm.getChar().getName() + " 領了120等獎勵!");
			} else {
				cm.sendOk("#d你已經領過囉或者沒有120等以上!!");
			}
			cm.dispose();
		}else if (selection == 7) {
			if (cm.getPlayer().getPrizeLog('1000滿額') < 1 && cm.getPlayer().getDonate() >=1000) {
				cm.gainItem(1112918, 1);
				cm.getPlayer().setPrizeLog('1000滿額');
				cm.sendOk("已領取1000滿額禮!");
				cm.worldMessage(6, "[轉蛋屋發出贊助滿額] " + " 玩家 " + cm.getChar().getName() + " 領了1000贊助滿額禮!");
			} else {
				cm.sendOk("#d你已經領過囉或者沒有達到斗內金額!!");
			}
			cm.dispose();
		}else if (selection == 8) {
			if (cm.getPlayer().getPrizeLog('3000滿額') < 1 && cm.getPlayer().getDonate() >=3000) {
				cm.gainItem(1032146, 1);
				cm.getPlayer().setPrizeLog('3000滿額');
				cm.sendOk("已領取3000滿額禮!");
				cm.worldMessage(6, "[轉蛋屋發出贊助滿額] " + " 玩家 " + cm.getChar().getName() + " 領了3000贊助滿額禮!");
			} else {
				cm.sendOk("#d你已經領過囉或者沒有達到斗內金額!!");
			}
			cm.dispose();
		}else if (selection == 9) {
			if (cm.getPlayer().getPrizeLog('5000滿額') < 1 && cm.getPlayer().getDonate() >=5000) {
				
				cm.getPlayer().setPrizeLog('5000滿額');
				cm.sendOk("已領取5000滿額禮!");
				cm.worldMessage(6, "[轉蛋屋發出贊助滿額] " + " 玩家 " + cm.getChar().getName() + " 領了5000贊助滿額禮!");
			} else {
				cm.sendOk("#d你已經領過囉或者沒有達到斗內金額!!");
			}
			cm.dispose();
			}else if (selection == 10) {
			if  (cm.getPlayer().getPrizeLog('第一次補償') < 1 && cm.getPlayer().getLevel() >= 10) {
				cm.getPlayer().setPrizeLog('第一次補償');
				cm.getPlayer().modifyCSPoints(2, 500, false);
				cm.gainItem (2450000,4);
				cm.sendOk("已領取");
			} else {
				cm.sendOk("#d你已領過!");
			}
		cm.dispose();
		}
	} else if (status >= 2) {
		var times = (status - 1);
		var repeatmsg = "真的";
		for(var i = 0; i < times; i++){
			repeatmsg += repeatmsg;
		}
		var show = "您"+repeatmsg+"不想簽到嗎?";
		
		cm.sendYesNo(show);
	}
	}
	