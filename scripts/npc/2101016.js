var status = -1;
var item = 4031868;
var count = 0;

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	(mode == 1 ? status++ : status--);
	if (status == 0) {
		count = cm.getPlayer().getItemQuantity(item, false);
		if (count <= 0) {
			cm.dispose();
			return;
		}
		cm.sendNext("恭喜您完成納希沙漠競技場副本，我已經看見您有"+count+"寶珠接下來我要給您一點小獎勵~請收下");
	} else if (status == 1) {
		cm.removeAll(4031868);
		cm.gainExpR(92.7 * 100 * count);
		cm.dispose();
	}
}