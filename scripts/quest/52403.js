/* global qm */
var status = -1;

function start() {
    qm.forceStartQuest();
    qm.dispose();
}

function end(mode, type, selection) {
    if (mode === 1) {
        status++;
    } else {
        status--;
    }

    var i = -1;
    if (status <= i++) {
        qm.dispose();
    } else if (status === i++) {
        qm.getDirectionFacialExpression(1, 10000);
        qm.say(0, 9330203, 16, "時間過還滿久了。", false, true);
    } else if (status === i++) {
        qm.say(0, 9330203, 16, "可是父親不會回來。耶願出去之後就沒有消息了。", true, true);
    } else if (status === i++) {
        qm.say(0, 9330203, 16, "總之我洪武團的一員。為了父親要更認真努力！", true, true);
    } else if (status === i++) {
        qm.say(0, 9330203, 16, "可是我有點睏。休息一下再繼續修煉。", true, true);
    } else if (status === i++) {
        qm.forceCompleteQuest();
        qm.dispose();
        qm.warp(743020101, 0);
    } else {
        qm.dispose();
    }
}
