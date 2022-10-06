/*
	Hikari - Showa Town(801000000)
*/

var status = -1;

function start() {
    action(1,0,0);
}

function action(mode, type, selection) {
    if (mode == 1)
	status++;
    else {
	cm.sendOk("如果你想進入屋內泡湯再來找我。");
	cm.dispose();
	return;
    }
    if (status == 0) {
	cm.sendYesNo("你想要進入屋內泡湯嗎？那麼要先付 "+300+" 楓幣。");
    } else if (status == 1) {
	if (cm.getMeso() < 300) {
	    cm.sendOk("請檢查你是否有"+300+"楓幣。");
	} else {
	    cm.gainMeso(-300);
	    if (cm.getPlayerStat("GENDER") == 0) {
		cm.warp(801000100);
	    } else {
		cm.warp(801000200);
	    }
	}
	cm.dispose();
    }
}
