/*
	Description: 	Quest - Tasty Milk 1
*/

var status = -1;

function start(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		if (status == 2) {
			qm.sendNext("No use trying to find an answer to this on my own. I'd better look for #bsomeone older and wiser than master!#k");
			qm.dispose();
			return;
		}
		status--;
	}
	if (status == 0) {
		qm.sendNext("嗚嗚，不要啦！我需要別的。有沒有不是草也不是肉的其他東西 …呢？主人的年紀比我大，應該什麼都知道。");
	} else if (status == 1) {
		qm.sendNextPrevS("#b就算你這樣說，我也不太清楚.…年紀比較大不代表什麼都知道…", 2);
	} else if (status == 2) {
		qm.askAcceptDecline("可是年紀愈大，在這個世上的經驗愈多，也就當然會有更豐富的知識。啊，可能可以找比主人年紀還要大的人問問看。");
	} else if (status == 3) {
		qm.forceStartQuest();
		qm.sendOkS("#b#b(已經向爸爸請教過一次了…我再去問爸爸看看好了。)", 2);
		qm.dispose();
	}
}

function end(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		status--;
	}
	if (status == 0) {
		qm.sendOk("What? You're still trying to feed that lizard? Eh, so it won't eat the Handful of Hay or the Pork? Picky little fellow. Oh? The lizard is still a baby?");
	} else if (status == 1) {
		qm.gainExp(260);
		qm.forceCompleteQuest();
		qm.dispose();
	}
}