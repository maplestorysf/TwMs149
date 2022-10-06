/* Hellin
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

        if (qm.getQuestStatus(1457) == 0) {
            qm.forceStartQuest(1457);
            qm.dispose();
            return;
        }

        if (status == 0) {
            qm.sendYesNo("幹得漂亮, 你確定要進行4轉了嗎?");
        } else if (status == 1) {
            if (qm.haveItem(4031517, 1) || qm.haveItem(4031518, 1)) {
                qm.removeAll(4031517); //英雄五角勳章
                qm.removeAll(4031518); //英雄星型墜飾
                //qm.gainItem(1142110, 1); //Master Adventure medal
                if (qm.getJob() == 411) {
                    qm.changeJob(412);
                } else if (qm.getJob() == 421) {
                    qm.changeJob(422);
                } else if (qm.getJob() == 433) { //Need to test? Weird job..
                    qm.changeJob(434);
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