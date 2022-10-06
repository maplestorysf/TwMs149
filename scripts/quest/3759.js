load('nashorn:mozilla_compat.js'); 
importPackage(Packages.constants);
var status = -1;

function start(mode, type, selection) {
    //qm.sendOk("Go to #bChief Tatamo#k in Leafre and bring back a Dragon Moss Extract.");
    qm.forceStartQuest();
    qm.dispose();
}

function end(mode, type, selection) {
    status++;
    if (status == 0) {
        if (qm.haveItem(4032531, 1)) {
            qm.sendNext("非常棒！請等我把這些成分混合在一起...");
        } else {
            //qm.sendOk("Please go to #bChief Tatamo#k of Leafre and bring back a Dragon Moss Extract.");
            qm.forceStartQuest();
            qm.dispose();
        }
    } else {
        var skillID = 1026;
        var job = qm.getPlayer().getJob();
        if (GameConstants.isKOC(job)) {
            skillID += 10000000;
        } else if (GameConstants.isAran(job)) {
            skillID += 20000000;
        } else if (GameConstants.isEvan(job)) {
            skillID += 20010000;
        } else if (GameConstants.isMercedes(job)) {
            skillID += 20020000;
        } else if (GameConstants.isDemon(job)) {
            skillID += 30010000;
        } else if (GameConstants.isResist(job)) {
            skillID += 30000000;
        }
        qm.teachSkill(skillID, 1, 0); // Maker
        qm.gainExp(11000);
        qm.removeAll(4032531);
        qm.sendOk("恭喜你學會飛翔技能。");
        qm.forceCompleteQuest();
        qm.dispose();
    }
}