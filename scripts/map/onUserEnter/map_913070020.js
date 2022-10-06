/* global ms */
var status = -1;

function action(mode, type, selection) {
	if (mode === 0) {
		status--;
	} else {
		status++;
	}

	var i = -1;
	if (status <= i++) {
		ms.dispose();
	} else if (status === i++) {
		ms.getPlayer().dropMessage(-1, "雜貨商店後院");
		var mainquest = rm.getQuest(20033);
		mainquest.setCustomData("1");
		ms.dispose();
	} else {
		ms.dispose();
	}
}
