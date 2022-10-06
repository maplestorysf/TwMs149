var status = -1;

function action(mode, type, selection) {
    switch (mode) {
        case 1:
            status++;
            break;
        default:
            status--;
            break;
    }
    
    switch (status) {
        case 0:
			var content = "想要前往精靈之地？啊，對了。發現新的精靈之地。皇家騎士庭院鑰匙說不定可以在這裡找到？為了和#b\r\n";
			content += "#L1#劍之地\r\n";
			content += "#L2#遊戲之地\r\n";
			content += "#L3#暴風之地\r\n";
			content += "#L4#黑暗之地\r\n";
			content += "#L5#閃電之地#k\r\n";
			cm.sendSimple(content);
            break;
        case 1:
            switch (selection) {
                case 1:
					cm.warp(271030201, 1);
					break;
				case 2:
					cm.warp(271030202, 1);
					break;
				case 3:
					cm.warp(271030203, 1);
					break;
				case 4:
					cm.warp(271030204, 1);
					break;
				case 5:
					cm.warp(271030205, 1);
					break;
            }
			cm.dispose();
            break;
        default:
            cm.dispose();
            break;
    }
}