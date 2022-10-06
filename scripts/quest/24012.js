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
		if (qm.getJob() == 2310) {
			qm.changeJob(2311);
		}
		qm.forceCompleteQuest();
	}
}
