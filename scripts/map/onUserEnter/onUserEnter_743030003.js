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
        ms.spawnNPCRequestController(9330204, 650, 3, 0, 8508813);
        ms.getEventEffect("Effect/Direction5.img/mercedesInIce/merBalloon/5", [500, 0, -100, 0, 0]);
        ms.exceTime(500);
        ms.getDirectionStatus(true);
    } else if (status === i++) {
        ms.exceTime(1500);
    } else if (status === i++) {
        ms.say(0, 9330204, 3, "耶願!!!!", false, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 1, "少爺", true, true);
    } else if (status === i++) {
        ms.exceTime(1500);
    } else if (status === i++) {
        ms.playerMoveRight();
        ms.setNPCSpecialAction(8508813, "move", 0, true);
        ms.updateNPCSpecialAction(8508813, -1, 250, 100);
        ms.exceTime(500);
    } else if (status === i++) {
        ms.say(0, 9330204, 3, "耶願!為什麼這麼晚了呢?父親在哪裡?", false, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 1, "吼吼，少爺.你必須要快點閃避。", true, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 3, "有什麼事呢！我做了惡夢心煩意亂的。這麼急迫的表情。汗…", true, true);
    } else if (status === i++) {
        ms.exceTime(500);
    } else if (status === i++) {
        ms.say(0, 9330204, 3, "難道是血？哪裡受傷了？父親，父親在哪裡？", false, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 1, "從現在起好好聽我說。快點在這裡躲好。", true, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 3, "不，父親。我要找父親。雖然不曉得有什麼問題，", true, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 1, "可是不是這樣的情況。現在不能從正門走。快點從後門…", true, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 3, "那麼父親應該會在大門!", true, true);
    } else if (status === i++) {
        ms.getDirectionFacialExpression(5, 10000);
        ms.playerMoveRight();
        ms.exceTime(500);
    } else if (status === i++) {
        ms.setNPCSpecialAction(8508813, "move", 0, true);
        ms.updateNPCSpecialAction(8508813, 1, 150, 100);
        ms.getDirectionStatus(true);
        ms.exceTime(500);
    } else if (status === i++) {
        ms.say(0, 9330204, 1, "不行的!!", false, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 3, "住手!我要先去找父親!!", true, true);
    } else if (status === i++) {
        ms.lockUI(0);
        ms.removeNPCRequestController(8508813);
        ms.dispose();
        ms.warp(743000100, 0);
    } else {
        ms.dispose();
    }
}
