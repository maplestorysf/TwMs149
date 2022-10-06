var status = -1;
function start() {
	if (cm.getPlayer().getMapId() == 272030400) {
		cm.sendYesNo("請問你是否要離開這裡 ?");
		status = 1;
		return;
	}
	if (cm.getPlayer().getLevel() < 120) {
		cm.sendOk("等級 120 以上才能參加阿卡伊農遠征隊");
		cm.dispose();
		return;
	}
	var em = cm.getEventManager("ArkariumBattle");
	if (em == null) {
		cm.sendOk("請聯繫 GM 開啟阿卡伊農遠征隊");
		cm.dispose();
		return;
	}
	var eim_status = em.getProperty("state");
	var marr = cm.getQuestRecord(160111);
	var data = marr.getCustomData();
	if (data == null) {
		marr.setCustomData("0");
		data = "0";
	}
	var time = parseInt(data);
	if (eim_status == null || eim_status.equals("0")) {
		var squadAvailability = cm.getSquadAvailability("Arkarium");
		if (squadAvailability == -1) {
			status = 0;
			cm.sendYesNo("請問你是否要成為阿卡伊農遠征隊隊長 ?");
		} else if (squadAvailability == 1) {
			var type = cm.isSquadLeader("Arkarium");
			if (type == -1) {
				cm.sendOk("遠征隊已結束 請重新登錄");
				cm.dispose();
			} else if (type == 0) {
				var memberType = cm.isSquadMember("Arkarium");
				if (memberType == 2) {
					cm.sendOk("你已被遠征隊長封鎖");
					cm.dispose();
				} else if (memberType == 1) {
					status = 5;
					cm.sendSimple("請選擇功能：\r\n#b#L0#加入遠征隊#l \r\n#b#L1#離開遠征隊#l \r\n#b#L2#查看遠征隊隊員列表#l");
				} else if (memberType == -1) {
					cm.sendOk("遠征隊已結束 請重新登錄");
					cm.dispose();
				} else {
					status = 5;
					cm.sendSimple("請選擇功能：\r\n#b#L0#加入遠征隊#l \r\n#b#L1#離開遠征隊#l \r\n#b#L2#查看遠征隊隊員列表#l");
				}
			} else {
				status = 10;
				cm.sendSimple("遠征隊長管理：\r\n#b#L0#查看遠征隊隊員列表#l \r\n#b#L1#剔除遠征隊員#l \r\n#b#L2#解除遠征隊員封鎖#l \r\n#r#L3#開始遠征隊任務#l");
			}
		} else {
			var eim = cm.getDisconnected("ArkariumBattle");
			if (eim == null) {
				var squd = cm.getSquad("Arkarium");
				if (squd != null) {
					cm.sendYesNo("已經有遠征隊在裡面進行任務了 ~\r\n" + squd.getNextPlayer());
					status = 3;
				} else {
					cm.sendOk("已經有遠征隊在裡面進行任務了 ~");
					cm.safeDispose();
				}
			} else {
				cm.sendYesNo("歡迎回歸 請問你是否要再次加入遠征隊 ?");
				status = 2;
			}
		}
	} else {
		var eim = cm.getDisconnected("ArkariumBattle");
		if (eim == null) {
			var squd = cm.getSquad("Arkarium");
			if (squd != null) {
				cm.sendYesNo("已經有遠征隊在裡面進行任務了 ~\r\n" + squd.getNextPlayer());
				status = 3;
			} else {
				cm.sendOk("已經有遠征隊在裡面進行任務了 ~");
				cm.safeDispose();
			}
		} else {
			cm.sendYesNo("歡迎回歸 請問你是否要再次加入遠征隊 ?");
			status = 2;
		}
	}
}
function action(mode, type, selection) {
	switch (status) {
	case 0:
		if (mode == 1) {
			if (cm.registerSquad("Arkarium", 5, " 已成為阿卡伊農遠征隊隊長 若要加入阿卡伊農遠征隊請向隊長申請 !")) {
				cm.sendOk("你已成為遠征隊隊長 您可以在 5 分鐘內籌組遠征隊伍開始任務");
			} else {
				cm.sendOk("發生錯誤 !");
			}
		}
		cm.dispose();
		break;
	case 1:
		if (mode == 1) {
			cm.warp(272020110, 0);
		}
		cm.dispose();
		break;
	case 2:
		if (!cm.reAdd("ArkariumBattle", "Arkarium")) {
			cm.sendOk("發生錯誤 !");
		}
		cm.safeDispose();
		break;
	case 3:
		if (mode == 1) {
			var squd = cm.getSquad("Arkarium");
			if (squd != null && !squd.getAllNextPlayer().contains(cm.getPlayer().getName())) {
				squd.setNextPlayer(cm.getPlayer().getName());
				cm.sendOk("你已成功保留遠征隊位置");
			}
		}
		cm.dispose();
		break;
	case 5:
		if (selection == 0) {
			var ba = cm.addMember("Arkarium", true);
			if (ba == 2) {
				cm.sendOk("遠征隊隊伍以滿 請稍後再試");
			} else if (ba == 1) {
				cm.sendOk("你已成功加入遠征隊");
			} else {
				cm.sendOk("你已經是遠征隊隊員了");
			}
		} else if (selection == 1) {
			var baa = cm.addMember("Arkarium", false);
			if (baa == 1) {
				cm.sendOk("你已離開遠征隊 ...");
			} else {
				cm.sendOk("你還不是遠征隊隊員 ~");
			}
		} else if (selection == 2) {
			if (!cm.getSquadList("Arkarium", 0)) {
				cm.sendOk("發生錯誤 !");
			}
		}
		cm.dispose();
		break;
	case 10:
		if (mode == 1) {
			if (selection == 0) {
				if (!cm.getSquadList("Arkarium", 0)) {
					cm.sendOk("發生錯誤 !");
				}
				cm.dispose();
			} else if (selection == 1) {
				status = 11;
				if (!cm.getSquadList("Arkarium", 1)) {
					cm.sendOk("發生錯誤 !");
					cm.dispose();
				}
			} else if (selection == 2) {
				status = 12;
				if (!cm.getSquadList("Arkarium", 2)) {
					cm.sendOk("發生錯誤 !");
					cm.dispose();
				}
			} else if (selection == 3) {
				if (cm.getSquad("Arkarium") != null) {
					var dd = cm.getEventManager("ArkariumBattle");
					dd.startInstance(cm.getSquad("Arkarium"), cm.getMap(), 160111);
				} else {
					cm.sendOk("發生錯誤 !");
				}
				cm.dispose();
			}
		} else {
			cm.dispose();
		}
		break;
	case 11:
		cm.banMember("Arkarium", selection);
		cm.dispose();
		break;
	case 12:
		if (selection != -1) {
			cm.acceptMember("Arkarium", selection);
		}
		cm.dispose();
		break;
	}
}
