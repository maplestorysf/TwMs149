/*
	Description: 	Quest - A Bite of Pork
*/

var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 2) {
	    qm.sendNext("How can you starve me like this. I'm just a baby. This is wrong!");
	    qm.dispose();
	    return;
	}
	status--;
    }
    if (status == 0) {
	qm.sendNext("不要這個，沒有更好吃的東西嗎？我不適合吃草，我需要更營養的東西， 主人！");
    } else if (status == 1) {
	qm.sendNextPrevS("#b嗯…你不喜歡吃素嗎？ 龍果然還是喜歡吃肉。那麼 #t4032453# 好嗎？", 2);
    } else if (status == 2) {
	qm.askAcceptDecline("我不曉得#t4032453#是什麼~ 只要是好吃的通通都好。快點拿給我~ 除了草以外！");
    } else if (status == 3) {
	qm.forceStartQuest();
	qm.sendOkS("#b#b(那麼，把#t4032453#交給#p1013000#吧。只要抓幾隻農場裡的#o1210100#就可以了。大概3個就夠了吧？)", 2);
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
	qm.sendOk("Oh, is this what you brought me to eat? So this is the Pork you were taking about? Let me try.");
    } else if (status == 1) {
	qm.gainExp(1850);
	qm.gainItem(4032453, -10);
	qm.sendNext("(Chomp, chomp, gulp...)");
	qm.forceCompleteQuest();
    } else if (status == 2) {
	qm.sendPrev("Uggh... This doesn't taste too bad but I don't think I can digest it. This isn't for me...");
	qm.dispose();
    }
}