/*
自動重置掉落物副本 每2小時1次 
可自定義 by Kodan
*/
var setupTask;
var time = 1000 * 60 * 120;
var timech = "兩";

function init() {
	waitstart();
}

function waitstart() {
	var cal = java.util.Calendar.getInstance();
	cal.set(java.util.Calendar.HOUR, 2);
	cal.set(java.util.Calendar.MINUTE, 0);
	cal.set(java.util.Calendar.SECOND, 0);
	var nextTime = cal.getTimeInMillis();
	while (nextTime <= java.lang.System.currentTimeMillis()) {
		nextTime += time; // 2 hour
	}
	setupTask = em.scheduleAtTimestamp("setup", nextTime); // 2 hour
}

function cancelSchedule() {
	setupTask.cancel(true);
}

function setup() {
	em.broadcastServerMsg(6, "[" + em.getChannelServer().getServerName() + "清潔公告] 每"+timech+"小時重整伺服器掉落物完成。", false);
	em.reloadDrops();
	waitstart();
}
