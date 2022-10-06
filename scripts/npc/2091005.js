var sel;
var mapid;
var selectedBuff;

﻿var status = -1;

function start() {
	mapid = cm.getMapId();
	var dojostarttime = cm.getPlayer().dojoStartTime;
	var dojomapendtime = cm.getPlayer().dojoMapEndTimeTotal;
	var dojoTime = Packages.tools.StringUtil.getReadableMillis(dojostarttime, dojomapendtime);
	if (isRestingSpot(mapid)) {
		cm.sendSimple("沒想到你能平安來到這裡，真讓人吃驚。但是接下去可沒那麼容易。怎麼樣？你想繼續挑戰嗎?\r\n#r[目前為止消耗時間：" + dojoTime + (cm.getPlayer().getInfoQuest(7218).equals("1") ? "#b\r\n#L4#想要獲得恢復、增益效果。#l" : "") + "#b\r\n#L0#要繼續挑戰。#l\r\n#L1#離開。#l");
	} else {
		cm.sendYesNo("你要放棄?現在只要通過下一階段就能大功告成了.你真的要放棄並離開嗎?");
	}
}

function action(mode, type, selection) {

	if (mode == 0) {
		if (!isRestingSpot(mapid)) {
			cm.sendOk("你怎麼這麼優柔寡斷!相信要不了多久你就又巴望著要回來了.");
		}
		cm.dispose();
		return;
	} else if (mode == 1) {
		status++;
	} else {
		status--;
	}

	switch (status) {
	case 0:
		sel = selection;
		if (!isRestingSpot(mapid)) {
			exit();
		} else {
			switch (sel) {
			case 4:
				var text = "";
				potions = [[2022855, "HP恢復50%"], [2022856, "HP恢復100%"], [2022857, "最大HP + 10000 (持續時間: 10分鐘)"], [2022858, "攻擊力/魔法攻擊力+30 (持續時間: 10分鐘)"], [2022859, "攻擊力/魔法攻擊力+60 (持續時間: 10分鐘)"], [2022860, "物理/魔法防禦力+2500 (持續時間: 10分鐘)"], [2022861, "物理/魔法防禦力+4000 (持續時間: 10分鐘)"], [2022862, "物理/魔法防禦力 + 2000 (持續時間: 10分鐘)"], [2022863, "移動速度/跳躍力最大 (持續時間: 10分鐘)"], [2022864, "攻擊速度+1 (持續時間: 10 分鐘)"]];
				for (var i = 0; i < potions.length; text += "#" + i + "# " + potions[i][1] + "", i++);
				cm.askBuffSelection(text);
				break;
			case 0:
				if (cm.getParty() == null || cm.isLeader()) {
					cm.dojoAgent_NextMap(true, true);
					cm.getPlayer().updateInfoQuest(7218, "1");
				} else {
					cm.sendOk("請组隊長跟我對話.");
				}
				cm.dispose();
				break;
			case 1:
				cm.askAcceptDecline("最後還是要放棄了嗎?真的想退出嗎?");
				break;
			}
		}
		break;
	case 1:
		switch (sel) {
		case 4:
			selectedBuff = 2022856 - 1 + selection;
			cm.sendYesNo("#i" + selectedBuff + "# #t" + selectedBuff + "#你要使用嗎？每個休息階段只能選擇一次，要慎重考慮！");
			break;
		case 1:
			exit();
			cm.dispose();
			break;
		}
		break;
	case 2:
		if (sel == 4) {
			cm.getPlayer().updateInfoQuest(7218, "0");
			cm.useItem(selectedBuff);
		}
		cm.dispose();
		break;
	}
}

function exit() {
	if (cm.isLeader()) {
		cm.warpParty(925020002);
	} else {
		cm.warp(925020002);
	}
	cm.getPlayer().updateInfoQuest(7218, "1");
}

function isRestingSpot(id) {
	return (id % 10000 / 100 % 6) == 0;
}
