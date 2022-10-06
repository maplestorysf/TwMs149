/* 	Sejan
Ariant
 */

function start() {
	if (cm.isQuestActive(3929)) {
		if (cm.getPlayer().getOneTimeLog("包裝食物1") == 1 && cm.getPlayer().getOneTimeLog("包裝食物2") == 1 && 
		cm.getPlayer().getOneTimeLog("包裝食物3") == 1 && cm.getPlayer().getOneTimeLog("包裝食物4") == 1) {
			cm.forceCompleteQuest(3929);
			cm.dispose();
		} else {
			cm.sendNext("您還沒把包裝好的食物送完阿...");
		}
	} else {
		cm.sendNext("光明...黑暗....兩者之間互不隨行..");
	}
}

function action() {
	cm.dispose()
}
