function action(mode, type, selection) {
    if (cm.getMapId() / 100 == 9211607) {
        if (cm.getMap().getAllMonstersThreadsafe().size() == 0) {
            if (!cm.canHold(4001534, 1)) {
                cm.sendOk("請空出一個其他欄空間");
                cm.dispose();
                return;
            }
            cm.gainExp_PQ(200, 1.5);
            cm.gainItem(4001534, 1);
            cm.warp(921160000, 0);
            cm.dispose();
        } else {
            cm.sendOk("請擊殺亞尼!");
            cm.safeDispose();
        }
    }
}