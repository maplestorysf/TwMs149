load('nashorn:mozilla_compat.js');
importPackage(Packages.tools);
importPackage(Packages.client);

var status = -1;
var sel;
var minlv = 50;
var maxlv = 200;

function start() {
	if ((cm.getPlayer().getLevel() < minlv || cm.getPlayer().getLevel() > maxlv) && !cm.getPlayer().isGM()) {
		cm.sendNext("您此等級不符合參加納希沙漠競技場\r\n最低需求" + minlv + "等、最高" + maxlv + "等");
		cm.dispose();
		return;
	}
	if (cm.getPlayer().getMapId() % 10 == 1)
		cm.sendSimple("您想要我幫什麼忙?? \r\n#b#L0# 給我 #t2270002# 和 #t2100067#.#l\r\n#L1# 我應該做什麼?#l\r\n#L2# 我想離開這裡...#l");
	else
		cm.sendSimple(cm.getPlayer().getAriantRoomLeaderName(((cm.getPlayer().getMapId() / 100) % 10) - 1) == cm.getPlayer().getName() ? "您想要開始挑戰納希沙漠競技場了??#b\r\n#b#L3# 準備進入挑戰!!#l\r\n#L1# 我想踢除其他玩家#l\r\n#L2# 我想離開這裡...#l" : "您想要做什麼??#b\r\n#L2# 我想要離開這裡...#l");
}

function action(mode, type, selection) {
	status++;
	if (mode != 1) {
		if (mode == 0 && type == 0)
			status -= 2;
		else {
			cm.dispose();
			return;
		}
	}
	if (cm.haveItem(4031868)) {
		cm.getPlayer().removeAll(4031868);
	}
	if (cm.getPlayer().getMapId() % 10 == 1) {
		if (status == 0) {
			if (sel == undefined)
				sel = selection;
			if (sel == 0) {
				if (cm.haveItem(2270002))
					cm.sendNext("您已經有了 #b#t2270002##k.");
				else if (cm.canHold(2270002) && cm.canHold(2100067)) {
					if (cm.haveItem(2100067))
						cm.removeAll(2100067);
					cm.gainItem(2270002, 32);
					cm.gainItem(2100067, 5);
					cm.sendNext("已經給您了，開始挑戰吧!!!");
				} else
					cm.sendNext("請確認您的消耗欄位是否滿了。");
				cm.dispose();
			} else if (sel == 1) {
				status = 1;
				cm.sendNext("現在開始我將和你說明如何遊玩。");
			} else
				cm.sendYesNo("請問您想要離開了???"); //No GMS like.
		} else if (status == 1) {
			if (mode == 1) {
				cm.warp(980010020);
				cm.dispose();
				return;
			}
		} else if (status == 2)
			cm.sendNext("好我說完了。");
		else if (status == 3)
			cm.dispose();
	} else {
		var nextchar = cm.getMap(cm.getPlayer().getMapId()).getCharacters().iterator();
		if (status == 0) {
			if (sel == undefined)
				sel = selection;
			if (sel == 1)
				if (cm.getPlayerCount(cm.getPlayer().getMapId()) > 1) {
					var text = "請問您想把誰踢出房間??"; //Not GMS like text
					var name;
					for (var i = 0; nextchar.hasNext(); i++) {
						name = nextchar.next().getName();
						if (!cm.getPlayer().getAriantRoomLeaderName(((cm.getPlayer().getMapId() / 100) % 10) - 1).equals(name))
							text += "\r\n#b#L" + i + "#" + name + "#l";
					}
					cm.sendSimple(text);
				} else {
					cm.sendNext("這個地圖上沒有其它玩家可以踢除。");
					cm.dispose();
				}
			else if (sel == 2) {
				if (cm.getPlayer().getAriantRoomLeaderName(((cm.getPlayer().getMapId() / 100) % 10) - 1) == cm.getPlayer().getName())
					cm.sendYesNo("由於您是房長所以當您離開，這個房間將會關閉。\r\n請問您確定要離開這裡??");
				else
					cm.sendYesNo("請問您確定要離開這裡??"); //No GMS like.
			} else if (sel == 3)
				if (cm.getPlayerCount(cm.getPlayer().getMapId()) > 1)
					cm.sendYesNo("戰鬥房間我已經為您安排好了，是否要開始進行納希沙漠競技場??");
				else {
					cm.sendNext("您自有需要兩名以上的挑戰者，才可以開始納希沙漠競技場.");
					cm.dispose();
				}
		} else if (status == 1) {
			if (sel == 1) {
				for (var i = 0; nextchar.hasNext(); i++)
					if (i == selection) {
						nextchar.next().changeMap(980010000);
						break;
					} else
						nextchar.next();
				cm.sendNext("該玩家已經被您踢出了。"); //Not GMS like
				cm.dispose();
				return;
			} else if (sel == 2) {
				if (cm.getPlayer().getAriantRoomLeaderName(((cm.getPlayer().getMapId() / 100) % 10) - 1) != cm.getPlayer().getName())
					cm.warp(980010000);
				else {
					cm.getPlayer().removeAriantRoom((cm.getPlayer().getMapId() / 100) % 10);
					cm.mapMessage(5, "由於" + cm.getPlayer().getName() + " 已經離開了，因此這個房間將關閉。");
					cm.warpMap(980010000, 3);
				}
			} else {
				cm.startAriantPQ(cm.getPlayer().getMapId() + 1);
			}
			cm.dispose();
		}
	}
}
