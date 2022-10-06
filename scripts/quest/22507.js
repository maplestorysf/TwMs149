var status = -1;

function start(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		if (status == 14) {
			qm.sendNext("Uh, you're kidding me, right? Tell me your finger slipped! Go ahead and accept the quest.");
			qm.dispose();
			return;
		}
		status--;
	}
	if (status == 0) {
		qm.sendNext("確定的是我和主人果然是聯繫再一起！ 主人變強的話，我也會變強，可以使用力量的主人也會變得更強！這正是我們的契約！和主人締結契約真是太好了！");
	} else if (status == 1) {
		qm.sendNextPrevS("#b嘿嘿，原來如此…可是我們到底是怎麼締結契約的呢？", 2);
	} else if (status == 2) {
		qm.sendNextPrev("嗯…這個我也不太清楚。我已經不太記得在蛋裡面的時候了。我的記憶就像夢中發生的事一樣模糊不清。在濃霧瀰漫的森林當中，主人走向這裡。看到我之後嚇了一跳。我叫了主人。");
	} else if (status == 3) {
		qm.sendNextPrevS("#b#b(喔，那不是跟我的夢很像嗎？難道我們曾在夢中見過面？我在夢裡面看到的龐大的龍是#p1013000#？)", 2);
	} else if (status == 4) {
		qm.sendNextPrev("主人和我的靈魂的頻率很吻合。倘若是我的主人第一眼就能知道可以和我締結契約。所以我向主人說締結契約吧！主人支付契約的代價。");
	} else if (status == 5) {
		qm.sendNextPrevS("#b我說了些什麼呢？", 2);
	} else if (status == 6) {
		qm.sendNextPrev("你發現我的時候，不是摸了我的手嗎？你忘了嗎？這是我締結契約的條件。在發現我的時候，摸我的手的那一刻契約就成立了，我們靈魂也就聯繫在一起。");
	} else if (status == 7) {
		qm.sendNextPrevS("#b靈魂的…聯繫？", 2);
	} else if (status == 8) {
		qm.sendNextPrev("嗯！現在的主人和我是擁有兩個身體的同一個人。因此我變強的話， 主人也會變強，還有主人變強的話，我也會變強！很厲害吧？");
	} else if (status == 9) {
		qm.sendNextPrevS("嗯！現在的主人和我是擁有兩個身體的同一個人。因此我變強的話， 主人也會變強，還有主人變強的話，我也會變強！很厲害吧？", 2);
	} else if (status == 10) {
		qm.sendNextPrev("當然很厲害！從今起不用在擔心怪物了。主人你有我在。我會保護你不受怪物攻擊。 主人！好，現在可以測試看看！");
	} else if (status == 11) {
		qm.sendNextPrevS("#b可是…這個和平的農場沒有危險的怪物。", 2);
	} else if (status == 12) {
		qm.sendNextPrev("喔喔？真的嗎？那真無聊…主人不會去冒險嗎？為了人們和怪物戰鬥擊退魔王，你不做這些事嗎？");
	} else if (status == 13) {
		qm.sendNextPrevS("#b目前沒這個打算…", 2);
	} else if (status == 14) {
		qm.askAcceptDecline("嗯嗯…可是龍使者總不能一直過著平靜的生活吧！總有一天會有展現我的實力的機會！到時再和我一起去冒險吧！主人？\r\n\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 810 exp");
	} else if (status == 15) {
		qm.forceStartQuest(22507);
        qm.forceCompleteQuest(22507);
		qm.sendNextS("嘿嘿嘿，那麼今後還要請你多多關照。主人。", 1);
	} else if (status == 16) {
		qm.sendNextPrevS("#b(雖然不太清楚為什麼，不過已經變成龍魔導士要和寶貝龍一起共度。而且也不曉得何時會前去冒險。)", 3);
	} else if (status == 17) {
		qm.sendPrevS("#b#b(可是當務之急是跑腿的任務。爸爸好像有什麼話要說，先去找爸爸吧！)", 2);
		qm.dispose();
	}
}

function end(mode, type, selection) {}