/**
	Konpei - Near the Hideout(801040000)
*/

function start() {
    cm.sendYesNo("什麼？你想要回到 #m801000000# 嗎？");
}

function action(mode, type, selection) {
    if (mode == 0) {
	cm.sendOk("如果你想要回到 #m801000000#，再告訴我哦。");
    } else {
	cm.warp(801000000,0);
    }
    cm.dispose();
}