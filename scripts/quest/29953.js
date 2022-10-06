var status = -1;
function start(mode, type, selection) {
	end(mode, type, selection);
}

function end(mode, type, selection) {
	if (mode == 0) {
		status--;
	} else {
		status++;
	}
	if (status == 0) {
		qm.forceStartQuest();
	}
}
