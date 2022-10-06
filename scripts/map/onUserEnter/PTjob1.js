/* global ms */
var status = -1;

function action(mode, type, selection) {
    if (mode == 0) {
        status--;
    } else {
        status++;
    }

    if (ms.isQuestFinished(25000)) {
        ms.dispose();
        return;
    }

    if (status === 0) {
        ms.getDirectionStatus(true);
        ms.lockUI(1, 1);
        ms.getDirectionStatus(true);
        ms.exceTime(500);
    } else if (status == 1) {
        if (!ms.haveItem(1142375)) {
            ms.forceStartQuest(11620, "0");
            ms.updateInfoQuest(15710, "lasttime=17/12/13/21/50");
            ms.updateInfoQuest(25980, "normal=#");
            ms.updateInfoQuest(25980, "normal=#;hard=#");
            ms.teachSkill(20031203, 1, 1);
            ms.teachSkill(20031205, 1, 1);
            ms.teachSkill(20030206, 1, 1);
            ms.teachSkill(20031207, 1, 1);
            ms.teachSkill(20031208, 1, 1);
            ms.teachSkill(20031211, 0, -1);
            ms.teachSkill(20031212, 0, -1);
            ms.teachSkill(20030204, 1, 1);
            ms.gainItem(2000019, 50);
            ms.gainItem(1142375, 1);
        }
        ms.forceStartQuest(25001, "1");
        ms.forceStartQuest(11620, "0");
        ms.updateInfoQuest(15710, "lasttime=17/12/13/21/50");
        ms.updateInfoQuest(25980, "normal=#");
        ms.updateInfoQuest(25980, "normal=#;hard=#");
        while (ms.getLevel() < 10)
            ms.levelUp();
        ms.lockUI(false);
        ms.dispose();
    }
}