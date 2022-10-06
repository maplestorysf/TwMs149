/* global qm */
var status = -1;

function start(mode, type, selection) {
    if (mode === 1) {
        status++;
    } else {
        status--;
    }

    var i = -1;
    if (status <= i++) {
        qm.dispose();
    } else if (status === i++) {
        qm.teachSkill(20010294, 0, -1);
        qm.teachSkill(20011293, 0, -1);
        qm.teachSkill(20011293, 1, 0);
        qm.getTopMsg("學會回歸技能。");
        qm.forceStartQuest(22130);
        qm.forceCompleteQuest(22130);
    } else if (status === i++) {
        qm.forceStartQuest(22010);
        qm.dispose();
    } else {
        qm.dispose();
    }
}
