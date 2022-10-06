var status = -1;

function start(mode, type, selection) {
    //qm.sendOk("Go to #bChief Tatamo#k in Leafre and bring back a Ancient Dragon Wing Scale.");
    qm.forceStartQuest();
    qm.dispose();
}

function end(mode, type, selection) {
    status++;
    if (status == 0) {
        if (qm.haveItem(4032969, 1)) {
            qm.sendNext("非常棒！請等我把這些成分混合在一起...");
        } else {
            //qm.sendOk("Please go to #bChief Tatamo#k of Leafre and bring back an Ancient Dragon Wing Scale.");
            qm.forceStartQuest();
            qm.dispose();
        }
    } else {
        qm.teachSkill(80001089, 1, 0); // Maker
        qm.removeAll(4032969);
        qm.sendOk("恭喜你學會飛天騎乘技能。");
        qm.forceCompleteQuest();
        qm.dispose();
    }
}