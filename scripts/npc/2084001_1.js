/* 一些函數設置區 */
var status = -1;
var requiredItem = 0;
var requiredItemNum = 0;
var reward;
var itemSet;
var lastSelection = -1;


/* 獎勵道具設置區 */
var eQuestChoices = Array(1003172, 1003174, 1003173, 1003175, 1003176);
var eQuestPrizes = Array();
/* 卷軸設置區:有機率獲得*/
var Allscroll = Array(2040315, 2040912, 2043013, 2043108, 2043208, 2043308, 2043708, 2043808, 2044008, 2044108, 2044208, 2044308, 2044408, 2044508, 2044608, 2044708);

/* 需求道具設置區 */
var requiredItemArr = [
	[1003172, 4000003, 4000038], //頭盔開始
	[1003174, 4000001, 4000038],
	[1003173, 4000031, 4000038],
	[1003175, 4000035, 4000038],
	[1003176, 4000042, 4000038]];
var requiredItemNumArr = [
	[1, 3000, 500], //頭盔
	[1, 3000, 500],
	[1, 3000, 500],
	[1, 3000, 500],
	[1, 3000, 500]];

/* 楓幣設置區 */
var usemeso = true; // 是否需要啟用扣楓幣
var requiredMoneyArr = Array(1000, 2000, 3000, 4000, 5000);
var remoney = 0;


function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
        status--;
        cm.dispose();
        return;
	}
	
	if (status == 2 && mode != 1) {
		cm.sendOk("好好慶祝吧!有問題歡迎隨時找我");
		cm.safeDispose();
		return;
	} else if (status == 1 && mode != 1) {
		cm.safeDispose();
		return;
	}
	
	if (status == 0) {
		var eQuestChoice = makeChoices(eQuestChoices);
		cm.sendSimple(eQuestChoice);
	} else if (status == 1) {

		requiredItem = requiredItemArr[selection];
		requiredItemNum = requiredItemNumArr[selection];
		reward = eQuestChoices[selection];
		remoney = requiredMoneyArr[selection];
		var eRequired = makeRequire(requiredItem, requiredItemNum, reward, remoney);
		cm.sendSimple(eRequired);

		lastSelection = selection;
	} else if (status == 2) {
		cm.sendYesNo("你確定你要製作#b#v" + reward + "##t" + reward + "##k嗎?\r\n");
	} else if (status == 3) {
		if (lastSelection == 23) {
			itemSet = (Math.floor(Math.random() * Allscroll.length));
			reward = Allscroll[itemSet];
			if (!cm.haveItem(4001126, 300)) {
				cm.sendOk("你的楓葉不夠\r\n");
				cm.dispose();
				return;
			}
			if (!cm.canHold(reward)) {
				cm.sendOk("你的物品欄已經滿了！\r\n");
				cm.dispose();
				return;
			}
			cm.gainItem(4001126, -300);

			cm.gainItem(reward, 1);
			cm.sendOk("希望您開心！\r\n");
			cm.dispose();

		} else {
			for (var i = 0; i < requiredItem.length; i++) {
				if (!cm.haveItem(requiredItem[i], requiredItemNum[i])) {
					cm.sendOk("還沒收集完成嗎？\r\n");
					cm.dispose();
					return;
				}
			}
			if (cm.getMeso() < remoney && usemeso) {
				cm.sendOk("你的錢不夠！\r\n");
				cm.dispose();
				return;
			}
			if (!cm.canHold(reward)) {
				cm.sendOk("你的物品欄已經滿了！\r\n");
				cm.dispose();
				return;
			}
			if (usemeso) {
				cm.gainMeso(-remoney);
			}
			for (var i = 0; i < requiredItem.length; i++) {
				cm.gainItem(requiredItem[i], -requiredItemNum[i]);
			}
			//cm.makeStatsEquip(1302000, 500, 500, 500, 500, 0);
			cm.makeStatsEquip2(reward, 75, 0, 0, 0, 0);
			//	cm.gainItem(reward,1,true);
			cm.sendOk("完成囉！繼續欣賞美麗的楓葉吧！\r\n");
			cm.dispose();
		}
	}
}

function makeChoices(a) {
	var result = "收集完材料了嗎?確定後就可以換囉!換錯不負責喔!\r\n";
	for (var x = 0; x < a.length; x++) {
		result += " #L" + x + "##v" + a[x] + "##t" + a[x] + "##l\r\n";
	}
	//result += "#L23##b我想兌換週年慶卷軸...#k#l\r\n";
	return result;
}

function makeRequire(a, b, re, m) {
	var result = "注意！做出來的物品#b素質都是隨機#k的唷~\r\n製作#b#v" + re + "##t" + re + "##k需要以下物品：\r\n\r\n";
	for (var x = 0; x < a.length; x++) {
		result += "#v" + a[x] + "##t" + a[x] + "# " + b[x] + "個#l\r\n";
	}
	if (usemeso) {
		result += "#fUI/UIWindow.img/QuestIcon/7/0##b" + m + "#k\r\n";
	}
	return result;
}
