var menu;
var status;

function start() {
    status = 0;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 && status == 1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.sendNext("想好在告訴我..");
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        if (status == 1) {
            menu = "歡迎使用剪票口，你想要使用哪種票？\r\n";
            menu += "#L0##b墮落地鐵#k#l\r\n#L1##b墮落廣場#k#l\r\n";
            cm.sendSimple(menu);
        }
        if (status == 2) {
            section = selection;
			switch(section) {
				case 0:
					cm.warp(103020100, 0);
				break;
				case 1:
					cm.warpBack(103020010, 103020020, 60);
				break;
			}
			cm.dispose();
		}
	}
}