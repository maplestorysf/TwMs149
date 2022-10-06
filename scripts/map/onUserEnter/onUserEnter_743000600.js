/* global ms */
var status = -1;

function action(mode, type, selection) {
    if (mode === 0) {
        status--;
    } else {
        status++;
    }
    var job = ms.getJob();
    if (job !== 0 || !ms.isQuestFinished(52403)) {
        ms.dispose();
        return;
    }
    if (ms.isQuestActive(52411) || ms.isQuestFinished(52411)) {
        ms.dispose();
        return;
    }

    var i = -1;
    if (status <= i++) {
        ms.dispose();
    } else if (status === i++) {
        ms.say(0, 9330215, 1, "起來了？身體還好嗎？", false, true);
    } else if (status === i++) {
        ms.say(0, 9330215, 3, "啊…還好。救我的人是你嗎？", true, true);
    } else if (status === i++) {
        ms.say(0, 9330215, 1, "真是厲害？沒錯。我開著很久沒開的鯨魚號前往異國的海洋，就發現你。", true, true);
    } else if (status === i++) {
        ms.say(0, 9330215, 3, "啊... 啊!耶願... 耶願現在他的狀況怎樣呢？", true, true);
    } else if (status === i++) {
        ms.say(0, 9330215, 1, "啊…你是說和你在一起的女人嗎？現在躺在那裡。傷勢嚴重，已經做了緊急處理。還要再觀察看看，可是已經度過了危險期。", true, true);
    } else if (status === i++) {
        ms.say(0, 9330215, 3, "啊... 真的太好了…太好了。", true, true);
    } else if (status === i++) {
        ms.say(0, 9330215, 1, "總之，請不要擔心！我們會好好照顧！", true, true);
    } else if (status === i++) {
        ms.say(0, 9330215, 1, "我的名字叫做卡伊琳！是這裡的船長，也是楓之谷海盜的領袖。那就麻煩你了~", true, false);
    } else if (status === i++) {
        ms.forceStartQuest(52411, 9330215);
        var level = 10 - ms.getLevel();
        for(var i = 0 ; i < level ; i++) {
            ms.levelUp();
        }
        if (job === 0) {
            ms.changeJob(508);
            ms.gainItem(1492144, 1); // 復仇者火槍
        }
        ms.dispose();
    } else {
        ms.dispose();
    }
}
