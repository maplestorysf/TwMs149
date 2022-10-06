var status = -1;
function start(mode, type, selection) {
	end(mode, type, selection);
}

function end(mode, type, selection) {
	if (mode == 0) {
		if (status == 0) {
			qm.sendNext("這是很重要的決定，所以我必須再考慮一下!");
			qm.safeDispose();
			return;
		}
		status--;
	} else {
		status++;
	}
	if (status == 0) {
		qm.sendYesNo("您已經做出您的最後的決定成為精靈遊俠了??");
	} else if (status == 1) {
		qm.sendNext("我剛才已經將您轉成精靈遊俠了，若您希望變得更強大可以打開能力欄來提高能力，如果您不知道怎麼提升點擊#b自動配點#k那麼系統就能幫您分配了。");
		if (qm.canHold(1142337, 1)) {
			if (qm.getJob() == 2300) {
				qm.expandInventory(1, 4);
				qm.expandInventory(2, 4);
				qm.expandInventory(4, 4);
				qm.gainItem(1142337, 1);
				qm.changeJob(2310);
			}
			qm.forceCompleteQuest();
		} else {
			qm.sendNext("請確認背包是否滿了.");
			qm.dispose();
			return;
		}
	} else if (status == 2) {
		qm.sendNextPrev("我還順便擴充了您的道具欄位，您待會可以查看裝備欄位。");
	} else if (status == 3) {
		qm.sendNextPrev("現在....我希望您可以去展現一下您的能力!");
		qm.safeDispose();
	}
}
