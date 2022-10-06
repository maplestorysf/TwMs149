/* global ms */
var status = -1;

function action(mode, type, selection) {
    if (mode === 0) {
        status--;
    } else {
        status++;
    }

    var i = -1;
    if (status <= i++) {
        ms.dispose();
    } else if (status === i++) {
        ms.getDirectionStatus(true);
        ms.lockUI(1, 1);
        ms.playerWaite();
        ms.exceTime(2000);
        ms.getDirectionStatus(true);
    } else if (status === i++) {
        ms.forcedAction([373, 0]);
        ms.getEventEffect("Effect/Direction100.img/effect/illustration/Sacrifice/0", [5000, 0, 0, 1, 1, 0, 0, 0]);
        ms.exceTime(5000);
    } else if (status === i++) {
        ms.lockUI(0);
        ms.dispose();
        ms.warp(743030101, 0);
    } else {
        ms.dispose();
    }
}
