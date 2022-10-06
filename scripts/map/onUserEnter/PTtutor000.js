/* global ms */
var status = -1;

function action(mode, type, selection) {
    if (mode === 0 && status !== 0) {
        status--;
    } else {
        status++;
    }

    var i = -1;
    if (status <= i++) {
        ms.dispose();
    } else if (status === i++) {
        ms.getDirectionStatus(true);
        ms.lockUI(true);
        ms.disableOthers(true);
        ms.equip(1352104);
        ms.teachSkill(20031211, 1, 0);
        ms.teachSkill(20031212, 1, 0);
        ms.playMovie("phantom_memory.avi", true);
    } else if (status === i++) {
        ms.showScreenAutoLetterBox(false, "phantom/mapname1");
        ms.playerMoveLeft();
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.playerWaite();
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.playerMoveRight();
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.playerWaite();
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.playerMoveLeft();
        ms.exceTime(1000);
        ms.showWZEffectNew("Effect/OnUserEff.img/questEffect/phantom/tutorial");
    } else if (status === i++) {
        ms.playerWaite();
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.playerMoveRight();
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.playerWaite();
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.playerMoveLeft();
        ms.exceTime(500);
    } else if (status === i++) {
        ms.playerWaite();
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.sendNextS("終於到了決戰的時間了嗎。", 17, 0, 0);
    } else if (status === i++) {
        ms.sendNextPrevS("沒想到還真叫人緊張的呢？是因為太久沒進行活動的關係嗎？雖然也不是沒有自信。", 17, 0, 0);
    } else if (status === i++) {
        ms.sendNextPrevS("應該已經準備好了吧？若是再繼續拖拖拉拉導致錯失時機的話，一定會顏面掃地的，雖然有點趕，不過快點行動吧！", 17, 0, 0);
    } else if (status === i++) {
        ms.lockUI(false);
        ms.disableOthers(false);
        ms.dispose();
    } else {
        ms.dispose();
    }
}