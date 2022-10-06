load('nashorn:mozilla_compat.js');
importPackage(Packages.server);
/*
內容：個人排行榜
 */

var status = -1;
var limit = 100;

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else if (mode == 0) {
		status--;
	} else {
		cm.dispose();
		return;
	}

	if (status == 0) {
		var text = "#e請選擇要查詢的榜單：#r\r\n";
		text += "#d";
		text += "\t\t\t#r#L0#世界轉生排行榜#l\r\n";
		text += "#b";
		text += "\t\t\t#L2#世界名聲排行榜#l\r\n";
		text += "#d";
		text += "\t\t\t#L3#世界公會排行榜#l\r\n";
		cm.sendSimple(text);
	} else if (status == 1) {
		switch (selection) {
		case 0:
			cm.showRb();
			cm.dispose();
			break;
		case 2:
			cm.showFm();
			cm.dispose();
			break;
		case 3:
			cm.displayGuildRanks();
			cm.dispose();
			break;
		}
	} else {
		cm.dispose();
	}
}



/*
Pokemon Go Ranking System
var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 0) {
	    cm.dispose();
	}
	status--;
    }
    if (status == 0) {
	cm.sendSimple("#b#L0#Pokemon Rankings (by Wins)#l\r\n#L1#Pokemon Rankings (by Caught)#l\r\n#L2#Pokemon Rankings (by Ratio)#l\r\n");
    } else if (status == 1) {
	if (selection == 0) {
	    cm.sendNext(cm.getPokemonRanking());
	} else if (selection == 1) {
	    cm.sendNext(cm.getPokemonRanking_Caught());
	} else if (selection == 2) {
	    cm.sendNext(cm.getPokemonRanking_Ratio());
	}
	cm.dispose();
    }
}*/