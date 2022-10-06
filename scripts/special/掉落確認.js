function start() {
	if (cm.getMap().getAllMonstersThreadsafe().size() <= 0) {
		cm.sendOk("這地圖上沒有怪物。");
		cm.dispose();
		return;
	}
	var selStr = "請選擇要查看的怪物掉落。\r\n\r\n#b";
	var monsterIterator = cm.getMap().getAllUniqueMonsters().iterator();
	while (monsterIterator.hasNext()) {
		var nextMonster = monsterIterator.next();
		selStr += "#L" + nextMonster + "##o" + nextMonster + "##l\r\n";
	}
	cm.sendSimple(selStr);
}

function action(mode, type, selection) {
	if (mode == 0) {
		cm.dispose();
		return;
	}
	cm.sendOk(cm.checkDrop(selection));
	cm.dispose();
}
