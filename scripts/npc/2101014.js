var status = -1;
var visit = 0;
var easy = 0;
var med = 0;
var hard = 0;
var hell = 0;

function init() {
	var overflowTime = 100,
	overflow = 0;
	var min = 0,
	max = 5;
	easy = getRand(min, max);
	do {
		max += 100;
		overflow++;
		med = getRand(min, max);
	} while (med == easy && overflow < overflowTime);
	do {
		max += 100;
		overflow++;
		hard = getRand(min, max);
	} while ((hard == easy || hard == med) && overflow < overflowTime);
	do {
		max += 100;
		overflow++;
		hell = getRand(min, max);
	} while ((hell == easy || hell == med || hell == hard) && overflow < overflowTime);
	do {
		max += 100;
		overflow++;
		visit = getRand(min, max);
	} while ((visit == easy || visit == med || visit == hard || visit == hell) && overflow < overflowTime);
	//if (overflow >= overflowTime) {
	//cm.getPlayer().dropMessage("overflow: " + overflow);
	//}
	//cm.getPlayer().dropMessage(easy + " " + med + " " + hard + " " + " " + hell + " " + visit + " over:" + (overflow < overflowTime));
}

function start() {
	init();
	action(1, 0, 0);
}

function action(mode, type, selection) {
	var record = cm.getQuestRecord(150001);
	var points = record.getCustomData() == null ? "0" : record.getCustomData();
	if (mode == 1) {
		status++;
	} else if (mode == 0) {
		status--;
	} else {
		cm.dispose();
		return;
	}
	if (status == 0) {
		var easy_choice = "#b#L" + easy + "# #v03994115##l";
		var med_choice = "#b#L" + med + "# #v03994116##l";
		var hard_choice = "#b#L" + hard + "# #v03994117##l";
		var hell_choice = "#b#L" + hell + "# #v03994118##l";
		var choices = "";
		var arrays = new Array(3);
		var tmp_choice = [easy, med, hard, hell];
		
		for (var i = 0; i < tmp_choice.length; i++) {
			var rdm = 0;
			do {
				var exist = false;
				rdm = tmp_choice[Math.floor(Math.random() * tmp_choice.length)]
				if (arrays.indexOf(rdm) != -1) {
					exist = true;
				}
			} while (exist);
			arrays[i] = rdm;
		}

		for (var i = 0; i < arrays.length; i++) {
			if (arrays[i] == easy) {
				choices += easy_choice;
			} else if (arrays[i] == med) {
				choices += med_choice;
			} else if (arrays[i] == hard) {
				choices += hard_choice;
			} else if (arrays[i] == hell) {
				choices += hell_choice;
			}
		}

		var msg = "你要挑戰BOSS?\r\n" +
			" #b[提示 : BSPQ點數 殺死每隻BOSS皆會掉落!]#k \n\r" +
			" #b#L" + visit + "#調查我的Boss Points#l#k \n\r\n\r\n" +
			choices;

		cm.sendSimple(msg);
	} else if (status == 1) {
		var levelLimit = 0;
		var event = "";
		switch (selection) {
		case easy:
			levelLimit = 70;
			event = "BossQuestEASY";
			break;
		case med:
			levelLimit = 100;
			event = "BossQuestMed";
			break;
		case hard:
			levelLimit = 120;
			event = "BossQuestHARD";
			break;
		case hell:
			levelLimit = 160;
			event = "BossQuestHELL";
			break;
		case visit:
			cm.sendNext("#b我的 Boss Points : " + points);
			status = -1;
			return;
		}

		if (cm.getParty() != null) {
			if (cm.getDisconnected(event) != null) {
				cm.getDisconnected(event).registerPlayer(cm.getPlayer());
			} else if (cm.isLeader()) {
				var party = cm.getPlayer().getParty().getMembers();
				var mapId = cm.getPlayer().getMapId();
				var next = true;
				var it = party.iterator();
				while (it.hasNext()) {
					var cPlayer = it.next();
					var ccPlayer = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
					if (ccPlayer == null || ccPlayer.getLevel() < levelLimit) {
						next = false;
						break;
					}
				}
				if (next) {
					var q = cm.getEventManager(event);
					if (q == null) {
						cm.sendOk("未知的錯誤。");
					} else {
						q.startInstance(cm.getParty(), cm.getMap());
					}
				} else {
					cm.sendOk("請檢查是否全員已達到" + levelLimit + "級，而且全部位在此地圖。");
				}
			} else {
				cm.sendOk("請透過隊長來找我對話。");
			}
		} else {
			cm.sendOk("請組隊後再來找我對話。");
		}
		cm.dispose();
	} else {
		cm.dispose();
	}
}
function getRand(min, max) {
	return Math.floor(Math.random() * (max - min + 1)) + min;
}
