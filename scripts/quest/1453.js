/* Gritto
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

        if (qm.getQuestStatus(1453) == 0) {
            qm.forceStartQuest(1453);
            qm.dispose();
            return;
        }

        if (status == 0) {
            qm.sendYesNo("幹得漂亮, 你確定要進行4轉了嗎?");
        } else if (status == 1) {
            if (qm.haveItem(4031511, 1) || qm.haveItem(4031512, 1)) {
                qm.removeAll(4031511); //英雄五角勳章
                qm.removeAll(4031512); //英雄星型墜飾
                //qm.gainItem(1142110, 1); //Master Adventure medal
                if (qm.getJob() == 211) {
                    qm.changeJob(212);
                } else if (qm.getJob() == 221) {
                    qm.changeJob(222);
                } else if (qm.getJob() == 231) {
                    qm.changeJob(232);
                } else {
                    qm.sendOk("出現未知錯誤");
                    qm.dispose();
                }
                qm.sendNext("你已經成功4轉了, 恭喜你！");
                //qm.gainSp(2);
                qm.forceCompleteQuest();
                qm.dispose();
            }
        } else {
            qm.sendOk("嗯, 考慮後再來找我吧。");
            qm.dispose();
        }
    }
}