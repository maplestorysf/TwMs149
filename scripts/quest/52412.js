var status = -1;

function start(mode, type, selection) {
	if (mode === 1) {
		status++;
	} else {
		status--;
	}

	var i = -1;
	if (status <= i++) {
		qm.dispose();
	} else if (status === i++) {
		qm.sendYesNo("好，要考慮看看嗎？很好~我收你為海盜的一員！準備好了嗎！");
	} else if (status === i++) {
		qm.sendNext("很好！從現在起你就是我們的同伴了！一開始要過流浪者的生活，可是過不久像天堂般的日子就會來臨了~");
	} else if (status === i++) {
		qm.sendNext("還有給你一把槍，這把槍是你來這裡時，和你在一起的朋友，說要轉交給你的槍。模樣特殊看起來是很珍貴的物品。");
	} else if (status === i++) {
		qm.sendNext("還有已經有人給你技能點數了。我不需要幫忙。開啟技能選單就能學習技能。可是沒辦法從一開始就全部放上。也有將其他技能熟悉到某種程度才能學習的技能。");
	} else if (status === i++) {
		qm.sendNext("還有一件是要注意。新手擁有職業的那一刻起，要小心不要死掉。如果死掉的話，就會減少這段期間以來累積的經驗值。辛辛苦苦取得的經驗值減少的話不是很冤枉嗎？");
	} else if (status === i++) {
		qm.sendNext("嗯！我可以教你的就是這個。也給你幾個你的水準可以用的武器，旅行時鍛鍊自己吧！如果你覺得沒什麼有趣的事，再來找我吧！那麼我就會告訴你更有趣的事。");
	} else if (status === i++) {
		qm.sendNext("啊…你的能力變得適合海盜了。在狀態屬性欄位（S鍵）上使用自動分配，就會變成更帥氣的海盜。對於海盜有什麼想知道的事，請盡管來問我。如果有什麼困難，也可以隨時來找我。那麼告辭了。");
	} else if (status === i++) {
		qm.forceCompleteQuest();
		qm.getPlayer().gainSP(5);
		qm.gainItem(3010408, 1); // 俠客的椅子
		qm.gainItem(1352820, 1); // 成長之拳
		qm.gainItem(1492144, 1); // 復仇者火槍
		qm.gainItem(1142727, 1); // 蒼龍的第一步
		qm.dispose();
	} else {
		qm.dispose();
	}
}
