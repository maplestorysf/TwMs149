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
        ms.getDirectionFacialExpression(1, 10000);
        ms.say(0, 9330201, 3, "這樣跑出來了，可是父親在哪裡？", false, true);
        ms.getDirectionStatus(true);
    } else if (status === i++) {
        ms.say(0, 9330201, 3, "好奇怪喔！那是什麼？怎麼會有這麼醜陋的東西出現？", true, true);
    } else if (status === i++) {
        ms.exceTime(500);
    } else if (status === i++) {
		ms.sendMoveScreen(2000, 0, 500);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/balloonMsg9/1", [2000, 2000, -200, 0, 0]);
        ms.exceTime(2000);
    } else if (status === i++) {
		ms.sendMoveScreen(0, 0, 500);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.say(0, 9330201, 3, "那個聲音是!父親，是父親的聲音!", false, true);
    } else if (status === i++) {
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.lockUI(0);
        ms.dispose();
    } else {
        ms.dispose();
    }
}
