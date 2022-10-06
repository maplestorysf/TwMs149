/**
 * @npc: 杰薩勒
 * @func: 沙漠競技場NPC
 */

var status = 0;
var sel;
var empty = [false, false, false];
var closed = false;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	(mode == 1 ? status++ : status--);
	if (status == 0) {
		if (cm.haveItem(4031868)) {
			cm.getPlayer().removeAll(4031868);
		}
		cm.sendSimple("#e<挑戰:納希沙漠競技場>#n\r\n歡迎來到納希沙漠競技場，是時候該展現下自己的技術了!#b\r\n#L0#申請挑戰[納希沙漠競技場].\r\n#L1#說明[納希沙漠競技場] 玩法\r\n#L3#確認今天可以挑戰的次數\r\n#L4#領取納希沙漠競技場獎勵");
	} else if (status == 1) {
		if (selection == 0) {
			if (closed || (cm.getPlayer().getLevel() < 50 && !cm.getPlayer().isGM())) {
				cm.sendOk(closed ? "納西沙漠競技場目前正在維修請稍後再嘗試." : "您的等級小於50級無法參加納希沙漠競技場.");
				cm.dispose();
				return;
			}
			var text = "您想要做什麼呢??#b";
			for (var i = 0; i < 3; i += 1)
				if (cm.getPlayerCount(980010100 + (i * 100)) > 0)
					if (cm.getPlayerCount(980010101 + (i * 100)) > 0)
						continue;
					else
						text += "\r\n#L" + i + "# 競技房間 " + (i + 1) + " (" + cm.getPlayerCount(980010100 + (i * 100)) + "/" + cm.getPlayer().getAriantSlotsRoom(i) + " 人數. 室長: " + cm.getPlayer().getAriantRoomLeaderName(i) + ")#l";
				else {
					empty[i] = true;
					text += "\r\n#L" + i + "# 競技房間 " + (i + 1) + " (空房間)#l";
					if (cm.getPlayer().getAriantRoomLeaderName(i) != "")
						cm.getPlayer().removeAriantRoom(i);
				}
			cm.sendSimple(text);
		} else if (selection == 1) {
			cm.sendNext("納西沙漠競技場是一個真正的戰爭遊戲，如果你是一個懦夫應該也不會看上這個副本，只要把眼前的敵人也擊敗，您就是勝利的贏家，很簡單的規則吧~\r\n - #e等級#n : #r(需求 : 50 - 200 )#k\r\n - #e時間限制#n : 8 分鐘\r\n - #e遊玩人數#n : 2-6\r\n - #e獎勵道具#n :\r\n#i1113048:##t1113048#");
			cm.dispose();
		} else if (selection == 3) {
			var ariant = cm.getQuestRecord(150139);
			var data = ariant.getCustomData();
			if (data == null) {
				ariant.setCustomData("10");
				data = "10";
			}
			cm.sendNext("#r#h ##k, 您今天還剩餘 #b" + parseInt(data) + "#k 次.");
			cm.dispose();
		} else if (selection == 4) {
			status = 4;
			cm.sendNext("向您展示納西沙漠競技場的獎品 #i1113048:# #b#t1113048##k.\r\n這是真正冠軍的象徵。");
		}
	} else if (status == 2) {
		var sel = selection;
		if (cm.getPlayer().getAriantRoomLeaderName(sel) != "" && empty[sel])
			empty[sel] = false;
		else if (cm.getPlayer().getAriantRoomLeaderName(sel) != "") {
			cm.warp(980010100 + (sel * 100));
			cm.dispose();
			return;
		}
		if (!empty[sel]) {
			cm.sendNext("已經有人創建了該房間，我建議您加入向他挑戰或者在建立一個新房間!");
			cm.dispose();
			return;
		}
		cm.getPlayer().setApprentice(sel);
		cm.sendGetNumber("設置該房間挑戰人數 (2~6 人)", 0, 2, 6);
	} else if (status == 3) {
		var sel = cm.getPlayer().getApprentice();
		if (cm.getPlayer().getAriantRoomLeaderName(sel) != "" && empty[sel])
			empty[sel] = false;
		if (!empty[sel]) {
			cm.sendNext("已經有人創建了該房間，我建議您加入向他挑戰或者在建立一個新房間!");
			cm.dispose();
			return;
		}
		cm.getPlayer().setAriantRoomLeader(sel, cm.getPlayer().getName());
		cm.getPlayer().setAriantSlotRoom(sel, selection);
		cm.warp(980010100 + (sel * 100));
		cm.getPlayer().setApprentice(0);
		cm.dispose();
	} else if (status == 5) {
		cm.sendNextPrev("加油吧!");
	} else if (status == 6) {
		cm.dispose();
	}
}
