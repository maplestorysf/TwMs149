/* 天空之城 - 桃花仙境 - 靈藥幻境
鶴重寫作品 + 搭飛機
by:Kodan
 */
var menu = new Array("桃花仙境", "天空之城", "靈藥幻境", "桃花仙境");
var cost = new Array(1500, 1500, 1500, 1500);
var display = "";
var btwmsg;
var sel;
var aircraft = [80001027, 80001028];
var msg = "";

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 0 && status == 0) {
		cm.dispose();
		return;
	} else if (mode == 0) {
		cm.sendNext("改變想法隨時跟我搭話吧。");
		cm.dispose();
		return;
	}
	status++;
	if (status == 0) {
		for (var i = 0; i < menu.length; i++) {
			if (cm.getMapId() == 200000141 && i < 1) {
				display += "\r\n#L" + i + "##b" + menu[i] + "(" + cost[i] + " 楓幣)#k";
			} else if (cm.getMapId() == 250000100 && i > 0 && i < 3) {
				display += "\r\n#L" + i + "##b" + menu[i] + "(" + cost[i] + " 楓幣)#k";
			}
		}
		if (cm.getMapId() == 200000141 || cm.getMapId() == 251000000) {
			btwmsg = "#b天空之城#k 到 #b桃花仙境#k";
		} else if (cm.getMapId() == 250000100) {
			btwmsg = "#b桃花仙境#k 到 #b天空之城#k";
		}
		if (cm.getMapId() == 251000000) {
			cm.sendYesNo("怎麼樣？我從 " + btwmsg + " 再到現在。我的速度很快的吧，如果你想返回 #b" + menu[3] + "#k ，那麼我們就立刻出發，不過還是得給我一些辛苦錢，價格是 #b" + cost[3] + " 楓幣#k。");
		} else {
			cm.sendSimple("如果想從 " + btwmsg + " 去的話。給我些辛苦錢就送你。我送你比起你走著去快多了。怎麼樣？\r\n\r\n#L50##r搭飛機去#k\r\n" + display);
		}
	} else if (status == 1) {
		sel = selection;
		if (sel == 50) {
			status = 10;
			var x = 0;
			msg = "想用哪種飛機飛行呢?";
			for (var i = 0; i < aircraft.length; i++) {
				if (cm.hasSkill(aircraft[i])) {
					msg += "\r\n#L" + i + "# #b#q" + aircraft[i] + "#";
					x++;
				}
			}
			if (x == 0) {
				cm.sendNext("沒有飛機還敢來搭??");
				cm.dispose();
				return;
			}
			cm.sendNext(msg);
		} else if (sel == 2) {
			cm.sendYesNo("你確定要去 #b" + menu[2] + "#k ？ 那麼你要付給我 #b" + cost[2] + " 楓幣#k。");
		} else {
			if (cm.getMeso() < cost[selection]) {
				cm.sendNext("滾!你沒有足夠的楓幣");
				cm.dispose();
			} else {
				if (cm.getMapId() == 251000000) {
					cm.gainMeso( - cost[3]);
					cm.warp(250000100);
					cm.dispose();
				} else {
					if (cm.getMapId() == 200000141) {
						if (cm.getPlayer().getMeso() >= cost[selection]) {
							cm.gainMeso( - cost[2]);
							cm.warpBack(200090300, 250000100, 600);
							cm.dispose();
						} else {
							cm.sendOk("滾!你沒有足夠的楓幣");
							cm.dispose();
						}
					} else if (cm.getMapId() == 250000100) {
						if (cm.getPlayer().getMeso() >= cost[selection]) {
							cm.gainMeso( - cost[1]);
							cm.warpBack(200090310, 200000100, 600);
							cm.dispose();
						} else {
							cm.sendOk("滾!你沒有足夠的楓幣");
							cm.dispose();
						}
					}
				}
			}
		}
	} else if (status == 2) {
		if (cm.getMeso() < cost[2]) {
			cm.sendNext("滾!你沒有足夠的楓幣");
			cm.dispose();
		} else {
			cm.gainMeso( - cost[2]);
			cm.warp(251000000);
			cm.dispose();
		}
	} else if (status == 11) {
		sel = selection;
		var two = 0;
		var mapid = cm.getMapId();
		var skillid = aircraft[sel];
		if (cm.getPlayer().getMeso() >= 5000) {
			cm.gainMeso(-5000);
			cm.useSkill(skillid, 1);
			switch (mapid) {
			case 200000141:
				two = 1;
				break;
			case 250000100:
				two = 2;
				break;
			}
			if (two == 1) {
				switch (sel) {
				case 0:
					cm.warpBack(200110041, 250000100, 420);
					break;
				case 1:
					cm.warpBack(200110041, 250000100, 300);
					break;
				}
			} else if (two == 2) {
				switch (sel) {
				case 0:
					cm.warpBack(200110040, 200000141, 420);
					break;
				case 1:
					cm.warpBack(200110040, 200000141, 300);
					break;
				}
			}
		} else {
			cm.sendNext("楓幣不夠？要搭飛機需要費用呢......");
		}
		cm.dispose();
	}
}
