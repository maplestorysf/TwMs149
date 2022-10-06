/* global ms */
var status = -1;

function action(mode, type, selection) {
    if (mode === 0) {
        status--;
    } else {
        status++;
    }

    if (ms.isQuestActive(20032) || ms.isQuestFinished(20032)) {
        ms.dispose();
        return;
    }

    var i = -1;
    if (status <= i++) {
        ms.dispose();
    } else if (status === i++) {
        ms.lockUI(0);
        ms.disableOthers(false);
        ms.getPlayer().dropMessage(-1, "林伯特的雜貨商店");
        ms.getPlayer().dropMessage(-1, "楓之谷曆XXXX年 3月8日");
        ms.dispose();
    } else {
        ms.dispose();
    }
}
