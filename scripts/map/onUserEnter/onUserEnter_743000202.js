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
        ms.exceTime(1500);
        ms.getDirectionStatus(true);
    } else if (status === i++) {
        ms.say(0, 9330204, 3, "這次是那邊嗎？", false, true);
    } else if (status === i++) {
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.lockUI(0);
        ms.dispose();
    } else {
        ms.dispose();
    }
}
