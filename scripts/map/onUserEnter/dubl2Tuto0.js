/* global ms */

var status = -1;

function action(mode, type, selection) {
    if (mode === 1) {
        status++;
    } else {
        status--;
    }

    var i = -1;
    if (status <= i++) {
        ms.dispose();
    } else if (status === i++) {
        ms.getDirectionStatus(true);
        ms.lockUI(1, 1);
        ms.EarnTitleMsg("秘密花園深處");
        ms.showDarkEffect(false);
        ms.showDarkEffect(true);
		ms.exceTime(120);
    } else if (status === i++) {
        ms.EarnTitleMsg("下雨的某一天");
		ms.exceTime(120);
    } else if (status === i++) {
        ms.playerWaite();
        ms.exceTime(3000);
    } else if (status === i++) {
        ms.playerMoveRight();
        ms.exceTime(5000);
    } else if (status === i++) {
        ms.lockUI(0);
        ms.disableOthers(false);
        ms.lockUI(false);
        ms.showDarkEffect(false);
        ms.dispose();
    } else {
        ms.dispose();
    }
}