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
        ms.disableOthers(true);
        ms.getPlayer().dropMessage(-1, "林伯特的雜貨商店");
        ms.getPlayer().dropMessage(-1, "楓之谷曆XXXX年 3月11日");
        ms.playerMoveRight();
        ms.exceTime(1000);
        ms.getDirectionStatus(true);
    } else if (status === i++) {
        ms.playerMoveLeft();
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction7.img/effect/tuto/step0/5", [2000, 0, -100, 1, 0, -100]);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.playerMoveRight();
        ms.exceTime(500);
    } else if (status === i++) {
        ms.playerWaite();
        ms.getEventEffect("Effect/Direction7.img/effect/tuto/step0/6", [2000, 0, -100, 1, 0, -100]);
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction7.img/effect/tuto/step0/4", [2000, 0, -100, 1, 0, -100]);
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction7.img/effect/tuto/step0/7", [2000, 0, -100, 1, 0, -100]);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction7.img/effect/tuto/step0/8", [2000, 0, -100, 1, 0, -100]);
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.getPlayer().dropMessage(-1, "後院好像有人。 去後院看一看。");
        ms.lockUI(0);
        ms.disableOthers(false);
        
        ms.dispose();
    } else {
        ms.dispose();
    }
}
