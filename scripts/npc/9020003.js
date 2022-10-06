var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 0) {
	    cm.dispose();
	}
	status--;
    }
    if (status == 0) {
	cm.sendSimple("#b#L2#我要保護坎特#l\r\n#L3#想要坎特的水鏡(50個海怒斯的鱗片)#l\r\n#L4#隨機寵物卷軸(5個海怒斯的鱗片)#l#k");
    } else if (status == 1) {
	if (selection == 2) {
	    if (cm.getPlayer().getParty() == null || !cm.isLeader()) {
		cm.sendOk("請透過隊長來找我對話。");
	    } else {
		var party = cm.getPlayer().getParty().getMembers();
		var mapId = cm.getPlayer().getMapId();
		var next = true;
		var size = 0;
		var it = party.iterator();
		while (it.hasNext()) {
			var cPlayer = it.next();
			var ccPlayer = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
			if (ccPlayer == null || ccPlayer.getLevel() < 120) {
				next = false;
				break;
			}
			size += (ccPlayer.isGM() ? 4 : 1);
		}	
		if (next && size >= 2) {
			var em = cm.getEventManager("Kenta");
			if (em == null) {
				cm.sendOk("坎特腳本目前關閉中。");
			} else {
		    var prop = em.getProperty("state");
		    if (prop.equals("0") || prop == null) {
			em.startInstance(cm.getPlayer().getParty(), cm.getPlayer().getMap(), 200);
		    } else {
			cm.sendOk("其他隊伍正在挑戰中，請稍候再試。");
		    }
			}
		} else {
			cm.sendOk("隊伍人數必須大於(含)2人，且大於120級。");
		}
	    }
	} else if (selection == 3) {
		if (!cm.canHold(1022123,1)) {
			cm.sendOk("請檢查裝備欄位空間。");
		} else if (cm.haveItem(4001535,50)) { //TODO JUMP
			cm.gainItem(1022123, 1);
			cm.gainItem(4001535, -50);
		} else {
			cm.sendOk("請收集50個海怒斯的鱗片。");
		}
	} else if (selection == 4) {
		if (!cm.canHold(2048010,1) || !cm.canHold(2048011,1) || !cm.canHold(2048012,1) || !cm.canHold(2048013,1)) {
			cm.sendOk("請空出一格消耗欄位。");
		} else if (cm.haveItem(4001535,5)) { //TODO JUMP
			cm.gainItem(2048010 + java.lang.Math.floor(java.lang.Math.random() * 4) | 0, 1);
			cm.gainItem(4001535, -5);
		} else {
			cm.sendOk("請收集5個海怒斯的鱗片。");
		}
	}
	cm.dispose();
    }
}