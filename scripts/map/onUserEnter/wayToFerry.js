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
        ms.spawnNPCRequestController(9330204, 339, -7, 0, 8516447);
        ms.say(0, 9330204, 1, "現在有提起精神了嗎？", false, true);
        ms.getDirectionStatus(true);
    } else if (status === i++) {
        ms.say(0, 9330204, 3, "咻。耶願。這裡是哪裡呢？父親呢？", true, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 1, "幸好我的傷口沒有哪麼深，所以還算可以幫忙掩護你。", true, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 1, "已經說好將會在渡口見面，所以，趕緊去吧。", true, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 3, "是喔? 真的嗎? ", true, true);
    } else if (status === i++) {
        ms.exceTime(720);
    } else if (status === i++) {
        ms.say(0, 9330204, 1, "噓，稍等一下。", false, true);
    } else if (status === i++) {
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.say(0, 9330204, 1, "好像還有跟蹤我們的傢伙的樣子。", false, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 1, "我來除掉這些傢伙。得在此地分手了。請記住。要在渡口見面！", true, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 1, "我會除掉追逐你們的傢伙，可是前往的路上仍然不太順遂。我會在路上放上告知的標誌板，請多小心。", true, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 3, "恩，知道了!", true, true);
    } else if (status === i++) {
        ms.setNPCSpecialAction(8516447, "teleportation", 0, true);
        ms.exceTime(720);
    } else if (status === i++) {
        ms.removeNPCRequestController(8516447);
        ms.say(0, 9330204, 3, "父親真的會沒事嗎？", false, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 3, "如果回頭的話？不是的。渡口！先去渡口！耶願不可能說謊！在渡口見父親。", true, true);
    } else if (status === i++) {
        ms.exceTime(1500);
    } else if (status === i++) {
        ms.say(0, 9330204, 3, "好吧，就照耶願說的去見面吧。", false, true);
    } else if (status === i++) {
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.lockUI(0);
        ms.dispose();
    } else {
        ms.dispose();
    }
}
