var status = -1;
var 維修 = true;

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 1)
		status++;
	else
		status--;
	if (status == 0) {
		cm.sendSimple("您好我是 #p9110204# 我可以為您安排目前楓之谷最新的結婚\r\n#b#L0#我想了解銀婚#l\r\n#L1#我想預約一般銀式婚禮#l\r\n#L2#我想預約高級銀式婚禮#l");
	} else if (status == 1) {
		switch (selection) {
		case 0:
			cm.sendNext("#b銀婚#k:\r\n是目前新楓之谷新型的結婚系統\r\n它有分兩種一種是普通另一種則是高級，\r\n當然了獎勵也有所差別。");
			break;
		case 1:
			檢查(維修);
			break;
		case 2:
			檢查(維修);
			break;
		}
		cm.dispose();
	}
}

function 檢查(off) {
	if (off) {
		cm.sendNext("目前正在此功能尚未開放.....");
	}
	cm.dispose();
	return;
}
