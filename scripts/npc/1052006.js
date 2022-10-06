var status = -1;
var zones = 0;
var cost = 1000;

function start() {
	cm.sendNext("嗨~~ 我是地鐵站 服務員..");
	if (cm.getQuestStatus(2055)) {
		zones++;
	} else if (cm.getQuestStatus(2056)) {
		zones++;
	} else if (cm.getQuestStatus(2057)) {
		zones++;
	}
}

function action(mode, type, selection) {
	status++;
	if (mode != 1) {
		cm.dispose();
		return;
	}
	if (status == 0) {
		if (zones == 0) {
			cm.sendNext("找我有什麼事情嗎??");
			cm.dispose();
		} else {
			var selStr = "你想要買哪種票??#b";
			for (var i = 0; i < zones; i++)
				selStr += "\r\n#L" + i + "#工地 B" + (i + 1) + " (" + cost + " 楓幣)#l";
			cm.sendSimple(selStr);
		}
	} else if (status == 1) {
		if (cm.getMeso() < cost)
			cm.sendOk("看來你沒有足夠的楓幣...");
		else {
			cm.gainMeso(-cost);
			cm.gainItem(4031036 + selection, 1);
		}
		cm.dispose();
	}
}
