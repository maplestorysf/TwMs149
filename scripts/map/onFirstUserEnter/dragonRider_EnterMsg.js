function start() {
	var msg = "";
	var eim = ms.getEventInstance();
	if (eim != null) {
		switch(ms.getMapId()) {
			case 240080300:
				msg = "打倒夢幻龍族，進入天空的巢穴!";
			break;
			case 240080400:
				msg = "需在3分鐘內所有隊員通過障礙物，進入天空巢穴!";
			break;
			case 240080500:
				msg = "打倒欺負神木村的龍騎士!";
				ms.playerMessage(-1, "進入天空巢穴。");
			break;
			default:
				msg = "打倒所有飛翔的鷹和飛翔的老鷹!";
			break;
		}
		ms.getMap().startMapEffect(msg, 5120026);
	}
	ms.dispose();
}


function action(mode, type, selection) {}