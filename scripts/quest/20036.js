/* global qm */
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
		qm.sendNextPrev("你看。 那因哈特。 這個人繼承了聖殿的血統，已經證明了具有資格。");
	} else if (status === i++) {
		qm.sendNextPrevS("是啊。 女皇的話永遠都是對的。 雖然還有許多不足之處，但是少年有延續聖殿騎士血統的資格。", 4, 1106003);
	} else if (status === i++) {
		qm.sendNextPrevS("我的父親是聖殿騎士嗎？我會成為聖殿騎士嗎？我是個平凡的少年。連名字都沒有...", 2);
	} else if (status === i++) {
		qm.askAcceptDecline("要由你來選擇。可是最好不要違背自己的命運。為了你、還有楓之谷。\r\n和我一起走吧？");
	} else if (status === i++) {
		qm.sendNextPrevS("你需要名字。「從光誕生的人」的含意 #b#e「米哈逸」#k#n怎麼樣呢？真的很適合你。現在跟我一起去耶雷弗吧！ 盡情散發重生的光芒，沒有比那裡更適合的地方了。", 1);
	} else if (status === i++) {
		qm.gainItem(1142390, 1);
		qm.gainItem(1302077, 1);
		qm.gainItem(1052444, 1);
		qm.forceCompleteQuest();
		var level = 10 - qm.getLevel();
		for (var i = 0; i < level; i++) {
			qm.levelUp();
		}
		if (qm.getPlayer().getJob() == 5000) {
			qm.changeJob(5100);
		}
		qm.dispose();
		qm.warp(913070071, 0);
	} else {
		qm.dispose();
	}
}
