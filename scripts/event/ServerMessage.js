/*
修改by宗達 20160106 06:52
 */

var setupTask;

function init() {
	scheduleNew();
}

function scheduleNew() {
	setupTask = em.schedule("start", 300000);
}

function cancelSchedule() {
	setupTask.cancel(false);
}

function start() {
	var Message = new Array(
			"當前頻道總人數:" + (em.getChannelOnline() * 2),
			"當前伺服器人數:" + (em.getTotalOnline() * 2));
	scheduleNew();
	em.broadcastYellowMsg("[" + em.getChannelServer().getServerName() + "公告]" + Message[Math.floor(Math.random() * Message.length)]);
}
