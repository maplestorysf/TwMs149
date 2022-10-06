/*
Crysta; - Kamuma (Neo Tokyo Teleporter)
 */

function start() {
	cm.sendSimple("選擇一個想要去的地方\r\n#b#L0#奈歐市2021年#l \r #L1#奈歐市2099年#l \r #L2#奈歐市2215年#l \r #L3#奈歐市2216年#l \r #L4#奈歐市2230年#l \r #L5#奈歐市2503年#l");
}

function action(mode, type, selection) {
	var mapid = 0;

	switch (selection) {
	case 0:
		mapid = 240070100;
		break;
	case 1:
		mapid = 240070200;
		break;
	case 2:
		mapid = 240070300;
		break;
	case 3:
		mapid = 240070400;
		break;
	case 4:
		mapid = 240070500;
		break;
	case 5:
		mapid = 240070600;
		break;
	}
	if (mapid > 0) {
		cm.warp(mapid, 0);
	} else {
		cm.sendOk("想好了再告訴我。");
	}
	cm.dispose();
}
