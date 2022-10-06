function action(mode, type, selection) {
	if (cm.getNpc() >= 9901000) {
		cm.sendNext("嗨 #h0#, 我200級了唷！");
	} else {
		cm.sendNext("我的編號：" + cm.getNpc() + "。");
	}
	cm.safeDispose();
}