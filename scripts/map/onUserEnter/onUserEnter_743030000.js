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
        ms.playerWaite();
        ms.spawnNPCRequestController(9330201, 488, 27, 0, 8476388);
        ms.exceTime(2000);
        ms.getDirectionStatus(true);
    } else if (status === i++) {
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.say(0, 9330201, 1, "修煉進行的怎樣呢？", false, true);
    } else if (status === i++) {
        ms.say(0, 9330201, 3, "每天每天，都有在進步中，父親。", true, true);
    } else if (status === i++) {
        ms.say(0, 9330201, 1, "聽起來耶願講的話，好像很認真的做下去的樣子。", true, true);
    } else if (status === i++) {
        ms.say(0, 9330201, 3, "不管多麼努力，都不願意肯定耶願。可是你跟父親說過這些話嗎？", true, true);
    } else if (status === i++) {
        ms.say(0, 9330201, 1, "哈哈，那麼是代表不努力嗎？", true, true);
    } else if (status === i++) {
        ms.say(0, 9330201, 3, "不，才不是！我也想要快點認真修煉，成為像父親這樣的洪武團的一員！", true, true);
    } else if (status === i++) {
        ms.say(0, 9330201, 1, "好的，我會更認真。啊！可以過來一下嗎？", true, true);
    } else if (status === i++) {
        ms.exceTime(100);
    } else if (status === i++) {
        ms.say(0, 9330201, 3, "好　父親...!", false, true);
    } else if (status === i++) {
        ms.exceTime(500);
        ms.playerMoveRight();
    } else if (status === i++) {
        ms.say(0, 9330201, 1, "你的生日是明天嗎？", false, true);
    } else if (status === i++) {
        ms.say(0, 9330201, 3, "(哇，父親沒有忘記耶!)", true, true);
    } else if (status === i++) {
        ms.say(0, 9330201, 3, "是啊。父親，明天是我的生日。可是好像太認真修煉了，連生日都忘了", true, true);
    } else if (status === i++) {
        ms.say(0, 9330201, 1, "是嗎？想送什麼禮物給你，如果不送的話也沒有關係嗎？", true, true);
    } else if (status === i++) {
        ms.say(0, 9330201, 3, "不，不是，不是這樣的….", true, true);
    } else if (status === i++) {
        ms.spawnNPCRequestController(9330203, 647, 20, 0, 8484926);
        ms.exceTime(2500);
    } else if (status === i++) {
        ms.setNPCSpecialAction(8484926, "move", 0, false);
        ms.updateNPCSpecialAction(8484926, -1, 97, 100);
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.say(0, 9330203, 9, "洪武團長", false, true);
    } else if (status === i++) {
        ms.exceTime(300);
    } else if (status === i++) {
        ms.say(0, 9330203, 9, "我有話要跟你說。", false, true);
    } else if (status === i++) {
        ms.exceTime(500);
    } else if (status === i++) {
        ms.say(0, 9330203, 3, "耶願，你去過什麼地方嗎？", false, true);
    } else if (status === i++) {
        ms.exceTime(300);
    } else if (status === i++) {
        ms.say(0, 9330203, 9, "是少爺有事外出回來了。雖然很冒昧，我可以和團長談一下話嗎？", false, true);
    } else if (status === i++) {
        ms.say(0, 9330201, 1, "#h0#。我去一下，所以，在這期間你好好進行練習吧。", true, true);
    } else if (status === i++) {
        ms.exceTime(300);
    } else if (status === i++) {
        ms.setNPCSpecialAction(8476388, "move", 0, true);
        ms.updateNPCSpecialAction(8476388, 1, 158, 100);
        ms.exceTime(1500);
    } else if (status === i++) {
        ms.setNPCSpecialAction(8484926, "move", 0, true);
        ms.updateNPCSpecialAction(8484926, 1, 80, 100);
        ms.exceTime(720);
    } else if (status === i++) {
        ms.removeNPCRequestController(8476388);
        ms.exceTime(780);
    } else if (status === i++) {
        ms.exceTime(720);
    } else if (status === i++) {
        ms.removeNPCRequestController(8484926);
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.playerMoveRight();
        ms.getDirectionStatus(true);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.say(0, 9330201, 3, "耶願的表情看起來心情不好。是什麼事呢？", false, true);
    } else if (status === i++) {
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/balloonMsg2/0", [2000, 290, -100, 0, 0]);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/balloonMsg2/2", [1500, 0, -100, 0, 0]);
        ms.exceTime(1500);
    } else if (status === i++) {
        ms.playerMoveRight();
        ms.getDirectionStatus(true);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.playerWaite();
        ms.exceTime(100);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/balloonMsg2/1", [2000, 120, -100, 0, 0]);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction100.img/effect/tuto/balloonMsg2/3", [2000, 120, -100, 0, 0]);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.say(0, 9330201, 3, "是不是發生什麼事了呢？我要跟她去看看嗎？", false, true);
    } else if (status === i++) {
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.spawnNPCRequestController(9330203, 573, 3, 0, 8486759);
        ms.exceTime(1200);
    } else if (status === i++) {
        ms.say(0, 9330203, 3, "吼，耶願，不要這麼突然間出現", false, true);
    } else if (status === i++) {
        ms.exceTime(300);
    } else if (status === i++) {
        ms.say(0, 9330203, 1, "少爺不管怎樣，偷聽的習慣是不好的。", false, true);
    } else if (status === i++) {
        ms.exceTime(300);
    } else if (status === i++) {
        ms.getDirectionFacialExpression(3, 5000);
        ms.say(0, 9330203, 3, "偷，偷…。我沒有偷聽!", false, true);
    } else if (status === i++) {
        ms.say(0, 9330203, 1, "洪武團長突然間有事情，所以，離開了。少爺 的修練，請在跟我委託之後進行", true, true);
    } else if (status === i++) {
        ms.exceTime(300);
    } else if (status === i++) {
        ms.getDirectionStatus(true);
        ms.removeNPCRequestController(8486759);
        ms.dispose();
        ms.warp(743000000, 0);
    } else {
        ms.dispose();
    }
}
