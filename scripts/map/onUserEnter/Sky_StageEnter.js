function start() {
	var msg = "";
	var eim = ms.getEventInstance();
	if (eim != null) {
		switch(ms.getMapId()) {
			case 240080400:
				msg = "需在3分鐘內所有隊員通過障礙物，進入天空巢穴!";
			break;
		}
		ms.getMap().startMapEffect(msg, 5120026);
	}
	ms.dispose();
}


function action(mode, type, selection) {}