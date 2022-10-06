var status = -1;
var sel = 0;
var partycheck = false;
var target;
var text = "";
function start() {
	cm.sendSimple("武陵道場極限挑戰。請小心行事!#b\r\n#L0#我要挑戰武陵道場。#l\r\n#b#L1##b我想要兌換腰帶。#l\r\n#b#L3##b我想查看自己的道場點數。#l\r\n#L5#確認今天還等打幾次#l\r\n#L4#武陵道場是什麼？#l");
}

function action(mode, type, selection) {

	if (mode == 0) {
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
		switch (sel) {
		case 0:
			cm.sendSimple("歡迎挑戰武陵道場#b\r\r\n\n#L0#挑戰簡單模式(#r130等#b)#l\r\n#L1#挑戰普通模式(#r150等#b)#l\r\n#L2##b挑戰困難模式(#r160等#b)#l\r\n#L3##b挑戰排名模式(#r130等#b)#l");
			break;
		case 1:
			cm.sendSimple("如果有#b#i4001620# #t4001620##k, 就給你#b腰帶#k。不過，這個腰帶在 #r#e15天#k#n 之後會消失，如果還想要就再收集武功的證物吧。\r\n\r\n你想領取什麼腰帶呢？\r\n#b#L0##i1132112:# #t1132112# #r(需要武公的證物25個)#b#l\r\n#L1##i1132113:# #t1132113# #r(需要武公的證物50個)#b#l\r\n#L2##i1132114:# #t1132114# #r(需要武公的證物100個)#b#l\r\n#L3##i1132115:# #t1132115# #r(需要武公的證物125個)#b#l");
			break;
		case 2:
			cm.sendSimple("武陵道場裡,不管是哪個難度模式,只要收集到#t4001620#就能獲得#b武公的腰帶#k。如果你挑戰#b困難模式#k或#b排名模式#k，還可以獲得特殊獎勵。當然，能不能拿得到還得看你自己的實力。\r\n#b\r\n#L0#武公的腰帶都有什麼樣的？#l\r\n#L1#挑戰困難模式可以獲得什麼獎勵？#l\r\n#L2#怎麼做才能在排名模式中獲得獎勵？#l");
			break;
		case 3:
			cm.sendOk("您目前的道場點數為 #b" + cm.getDojoPoints() + "#k 點。請繼續努力.");
			cm.dispose();
			return;
		case 4:
			cm.sendNext("我們師傅是武陵最強的人。武陵道場是師父建造的地方。武陵道場非常高，有多種難度任你挑戰，當然沒有一定實力是不可能挑戰成功的!");
			cm.dispose();
			return;
		case 5:
			var ndojo = cm.getPlayer().getBossLog("武陵道場普");
			var rdojo = cm.getPlayer().getBossLog("武陵道場排");
			cm.sendNext("以下是您今天可以挑戰的次數:\r\n  " + (3 - ndojo) + "次[簡單、普通、困難]模式, " + (1 - rdojo) + "次排行模式");
			break;
		}

		break;
	case 1:
		normLimit = 100000;
		switch (sel) {
		case 0: //開始道場
			var reqLevel = selection == 0 ? 130 : selection == 1 ? 150 : 160;
			if (selection == 3) {
				if (cm.getParty() != null) {
					cm.sendNext("要挑戰排名模式前，請先退出組隊。");
					cm.dispose();
					return;
				}
				if (!cm.getPartyLog("武陵道場排", 1)) {
					cm.sendNext("今天已經挑戰過排行模式了。");
					cm.dispose();
					return;
				}
				partycheck = false;
			} else {
				if (cm.getParty() != null) {
					if (!cm.isLeader()) { // 不是隊長
						cm.sendOk("隊長必須在這裡，請隊長跟我對話。");
						cm.dispose();
						return;
					} else if (cm.getPlayer().getParty() == null || !cm.isLeader()) {
						cm.sendOk("隊長必須在這裡，請隊長跟我對話。");
						cm.dispose();
						return;
					} else if (!cm.isAllPartyMembersAllowedLevel(reqLevel, 255)) {
						cm.sendNext("組隊成員等級必須全 #r" + reqLevel + " 以上 #k才可以入場。");
						cm.dispose();
						return;
					} else if (!cm.allMembersHere()) {
						cm.sendOk("您的部分組員不在此地圖,請召集完畢後重新嘗試");
						cm.dispose();
						return;
					} else if (!cm.getPartyLog("武陵道場普", 3)) {
						cm.sendOk("您的部分組員已經到達上限次數。");
						cm.dispose();
						return;
					}
					partycheck = true;
				}
			}
			if (!cm.isQuestActive(7214)) {
				cm.forceStartQuest(7214);
			}
			switch (selection) {
				/*case 0:
				if (cm.getChar().getLevel() >= 130) {
				if (!cm.start_DojoAgent(selection, true, partycheck)) {
				cm.sendOk("當前頻道挑戰人數已滿,請換頻道後再試.");
				break;
				}
				cm.setDojoMode(0);
				cm.setPartyLog("武陵道場普");
				cm.start_DojoAgent(true, partycheck);
				cm.dispose();
				} else {
				cm.sendOk("要參與簡單難度須達 #b130 #k等.");
				}
				break;
				case 1:
				if (cm.getChar().getLevel() >= 150) {
				if (!cm.start_DojoAgent(selection, true, partycheck)) {
				cm.sendOk("當前頻道挑戰人數已滿,請換頻道後再試.");
				break;
				}
				cm.setDojoMode(1);
				cm.setPartyLog("武陵道場普");
				cm.start_DojoAgent(true, partycheck);
				cm.dispose()
				} else {
				cm.sendOk("要參與普通難度須達 #b150 #k等.");
				}
				break;
				case 2:
				if (cm.getChar().getLevel() >= 160) {
				if (!cm.start_DojoAgent(selection, true, partycheck)) {
				cm.sendOk("當前頻道挑戰人數已滿,請換頻道後再試.");
				break;
				}
				cm.setDojoMode(2);
				cm.setPartyLog("武陵道場普");
				cm.start_DojoAgent(true, partycheck);
				cm.dispose();
				} else {
				cm.sendOk("要參與困難難度須達 #b160 #k等.");
				}
				break;
				case 3:
				if (cm.getChar().getLevel() >= 130) {
				if (!cm.start_DojoAgent(selection, true, partycheck)) {
				cm.sendOk("當前頻道挑戰人數已滿,請換頻道後再試.");
				break;
				}
				cm.setDojoMode(3);
				cm.setPartyLog("武陵道場排");
				cm.start_DojoAgent(true, partycheck);
				cm.dispose();
				} else {
				cm.sendOk("要參與挑戰排名模式須達 #b130 #k等.");
				}
				break;*/
			default:
				cm.sendNext("目前模式不開放。");
				break;
			}
			cm.dispose();
			return;
		case 1:
			var cost,
			itemID = 1132112 + selection;
			switch (selection) {
			case 0:
				cost = 25;
				break;
			case 1:
				cost = 50;
				break;
			case 2:
				cost = 100;
				break;
			case 3:
				cost = 125;
				break;
			}
			if (cm.haveItem(4001620, cost)) {
				if (cm.canHold(itemID)) {
					cm.gainItem(4001620, -cost);
					cm.gainItem(itemID, 1);
					cm.sendOk("兌換好了，如果有能力，可以在130級之後參加 #b排名模式#k！");
				} else {
					cm.sendOk("背包空間不足,請整理下你的裝備欄空間!");
				}
			} else {
				cm.sendOk("你不要騙我了,你確定你有#r" + cost + "#k個#b#i4001620# #t4001620##k嗎??");
			}
			cm.dispose();
			break;
		case 2:
			switch (selection) {
			case 0:
				cm.sendOk("你只要在武陵道場中蒐集到#i4001620# #t4001620#就能獲得#b武公的腰帶#k。武陵道場中偶爾會掉落#b腰帶專用捲軸#k，用它可以給腰帶升級，你別忘了好好蒐集一些。 \r\n#e <武公的證物獎勵：有效期15日>#n#b\r\n#i1132112:# #t1132112# #r(需要#t4001620#25個)#b\r\n# i1132113:# #t1132113# #r(需要#t4001620#50個)#b\r\n#i1132114:# #t1132114# #r(需要#t4001620#100個)#b\r\n#i1132115:# # t1132115# #r(需要#t4001620#125個)#k");
				break;
			case 1:
				cm.sendOk("根據你挑戰困難模式的結果，可以獲得相應的點數。該點數在一周內會進行累積，按照你累積的點數，可以獲得相應的獎勵。只要你多加努力，就能獲得更好的獎勵，加油吧。 \r\n#e <困難模式獎勵> \r\n#b#eSS級：#i1022135:# #t1022135# #r(有效期：7日)#b\r\nS級：#i1022136:# #t1022136 # #r(有效期：7日)#b\r\nA級：#i2022957:# #t2022957#3個#r(有效期：7日)#b\r\nB級：#i2001505:# #t2001505#10個#r(有效期：7日)#b\r\n");
				break;
			case 2:
				cm.sendOk("挑戰排名模式，排名靠前，你就能獲得特別的獎勵。\r\n#e < 排名模式獎勵：有效期到下週一00點>#n#b\r\n#i1082392:# #t1082392# #r(全體排名第1名)\r\n#b#i1082393:# #t1082393# # r(除職業排名第1名，全體排名第1名)\r\n#b#i1082394:# #t1082394# #r(職業排名第2~3名)");
				break;
			}
			cm.dispose();
			break;
		}
		break;
	case 2:
		switch (sel) {
		default:
			cm.dispose();
			break;
		}
		break;
	case 3:
		cm.dispose();
		break;

	}
}
