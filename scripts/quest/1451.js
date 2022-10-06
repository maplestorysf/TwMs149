/* Harmonia
 Leafre : Forest of the Priest (240010501)
 4th Job Advancer/Quests.
 Made by TheGM
 */
var status = -1;

function start(mode, type, selection) {}

function end(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 1)
            status++;
        else
            status--;

        if (qm.getQuestStatus(1451) == 0) {
            qm.forceStartQuest(1451);
            qm.dispose();
            return;
        }

        if (status == 0) {
            qm.sendYesNo("幹得漂亮, 你確定要進行4轉了嗎?");
        } else if (status == 1) {
            if (qm.haveItem(4031343, 1) || qm.haveItem(4031344, 1)) {
                qm.removeAll(4031343); //英雄五角勳章
                qm.removeAll(4031344); //英雄星型墜飾
                //qm.gainItem(1142110, 1); //Master Adventure medal
                if (qm.getJob() == 111) {
                    qm.changeJob(112);
                } else if (qm.getJob() == 121) {
                    qm.changeJob(122);
                } else if (qm.getJob() == 131) {
                    qm.changeJob(132);
                } else {
                    qm.sendOk("出現未知錯誤");
                    qm.dispose();
                }
                qm.sendNext("你已經成功4轉了, 恭喜你！");
                //qm.gainSp(2);//1 is auto so it only gets 3 i think?
                qm.forceCompleteQuest();
                qm.dispose();
            }
        } else {
            qm.sendOk("嗯, 考慮後再來找我吧。");
            qm.dispose();
        }
    }
}