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
        ms.spawnNPCRequestController(9330204, 807, -100, 0, 8519856);
        ms.say(0, 9330204, 1, "你到了!!!", false, true);
        ms.getDirectionStatus(true);
    } else if (status === i++) {
        ms.say(0, 9330204, 3, "父親，父親還沒有到達嗎？", true, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 1, "好，先坐上船做好出發準備。我觀察一下四週", true, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 3, "好，知道了!", true, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 1, "然，然後，請領取這個。", true, true);
    } else if (status === i++) {
        ms.lockUI(0);
        ms.removeNPCRequestController(8519856);
        ms.dispose();
        ms.warp(743020401, 0);
    } else {
        ms.dispose();
    }
}
