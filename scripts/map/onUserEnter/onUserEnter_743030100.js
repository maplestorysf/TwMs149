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
        ms.spawnNPCRequestController(9330205, 69, 3, 1, 8512017);
        ms.spawnNPCRequestController(9330202, 389, 3, 0, 8512018);
        ms.exceTime(500);
        ms.getDirectionStatus(true);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/balloonMsg10/0", [2000, 550, -120, 0, 0]);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/balloonMsg10/1", [2000, 250, -120, 0, 0]);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/balloonMsg10/2", [2000, 250, -120, 0, 0]);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/balloonMsg10/3", [2000, 550, -120, 0, 0]);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/balloonMsg10/4", [2000, 550, -120, 0, 0]);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/balloonMsg10/9", [2000, 550, -120, 0, 0]);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/balloonMsg10/5", [2000, 250, -120, 0, 0]);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/balloonMsg10/10", [2000, 550, -150, 0, 0]);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.setNPCSpecialAction(8512018, "attack0", 0, true);
        ms.exceTime(720);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/DaddysSkill0/0", [1000, 530, 0, 0, 0]);
        ms.playSound("chivalrousFighter/arkAttack0");
        ms.exceTime(240);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/BossHit/0", [600, 250, 0, 0, 0]);
        ms.setNPCSpecialAction(8512017, "hit1", 0, true);
        ms.setNPCSpecialAction(8512018, "hit0", 0, true);
        ms.exceTime(100);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/BossHit/0", [600, 235, -10, 0, 0]);
        ms.exceTime(100);
    } else if (status === i++) {
        ms.playSound("demonSlayer/arkAttack0");
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/BossHit/0", [600, 259, -20, 0, 0]);
        ms.exceTime(120);
    } else if (status === i++) {
        ms.setNPCSpecialAction(8512017, "hit1", 0, true);
        ms.playSound("demonSlayer/arkAttack1");
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/BossHit/0", [600, 235, -10, 0, 0]);
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/BossHit/0", [600, 259, -20, 0, 0]);
        ms.exceTime(120);
    } else if (status === i++) {
        ms.setNPCSpecialAction(8512017, "attack1", 0, true);
        ms.setNPCSpecialAction(8512018, "attack0", 0, true);
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/DaddysSkill0/1", [1000, 550, 0, 0, 0]);
        ms.playSound("chivalrousFighter/arkAttack1");
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/BossSkill10-1/3", [320, 250, 30, 0, 0]);
        ms.setNPCSpecialAction(8512018, "hit0", 0, true);
        ms.exceTime(290);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/BossHit/0", [600, 530, -30, 0, 0]);
        ms.playSound("demonSlayer/arkAttack1");
        ms.exceTime(100);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/BossHit/0", [600, 510, -20, 0, 0]);
        ms.playSound("demonSlayer/arkAttack0");
        ms.exceTime(120);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/BossHit/0", [600, 570, 0, 0, 0]);
        ms.exceTime(50);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/BossHit/0", [600, 540, 15, 0, 0]);
        ms.exceTime(120);
    } else if (status === i++) {
        ms.setNPCSpecialAction(8512017, "move", 0, true);
        ms.updateNPCSpecialAction(8512017, 1, 40, 100);
        ms.exceTime(800);
    } else if (status === i++) {
        ms.setNPCSpecialAction(8512017, "attack1", 0, true);
        ms.setNPCSpecialAction(8512018, "teleportation", 0, true);
        ms.exceTime(720);
    } else if (status === i++) {
        ms.removeNPCRequestController(8512018);
        ms.playSound("chivalrousFighter/dragonSkillUse");
        ms.exceTime(500);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/DaddysSkill0/2", [2000, 380, 0, 0, 0]);
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/BossSkill10-1/0", [3000, 510, 0, 0, 0]);
        ms.playSound("chivalrousFighter/dragonSkillBlast");
        ms.exceTime(1300);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/BossHit/0", [600, 290, -50, 0, 0]);
        ms.setNPCSpecialAction(8512017, "hit1", 0, true);
        ms.spawnNPCRequestController(9330202, 350, 3, 0, 8513690);
        ms.setNPCSpecialAction(8513690, "hit0", 0, true);
        ms.exceTime(1300);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/BossHit/0", [600, 550, 0, 0, 0]);
        ms.exceTime(720);
    } else if (status === i++) {
        ms.say(0, 9330205, 1, "比想像中更強　!!!", false, true);
    } else if (status === i++) {
        ms.say(0, 9330202, 1, "真的是皇帝的話，不會使用出這種邪惡的力量，你這假貨!!!!", true, true);
    } else if (status === i++) {
        ms.exceTime(500);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/balloonMsg10/6", [2000, 0, -100, 0, 0]);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.playerMoveRight();
        ms.exceTime(500);
    } else if (status === i++) {
        ms.playerWaite();
        ms.exceTime(500);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/balloonMsg10/7", [2000, 450, -130, 0, 0]);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/balloonMsg10/8", [2000, 200, -150, 0, 0]);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.setNPCSpecialAction(8512017, "move", 0, true);
        ms.updateNPCSpecialAction(8512017, -1, 10, 100);
        ms.exceTime(100);
    } else if (status === i++) {
        ms.say(0, 9330202, 1, "不行!!!!!!!!!!!", false, true);
    } else if (status === i++) {
        ms.setNPCSpecialAction(8512017, "attack1", 0, true);
        ms.exceTime(720);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/BossSkill10-1/4", [1600, 250, 30, 0, 0]);
        ms.playSound("chivalrousFighter/dragonSkillUse");
        ms.exceTime(180);
    } else if (status === i++) {
        ms.setNPCSpecialAction(8513690, "teleportation", 0, true);
        ms.exceTime(720);
    } else if (status === i++) {
        ms.removeNPCRequestController(8513690);
        ms.exceTime(50);
    } else if (status === i++) {
        ms.lockUI(0);
        ms.removeNPCRequestController(8512017);
        ms.dispose();
        ms.warp(743020300, 0);
    } else {
        ms.dispose();
    }
}
