var status = 0;

function start() {
    if (cm.getMapId() == 951000000) {
        cm.sendYesNo("親愛的顧客，想要回到城鎮嗎？");
    } else {
        cm.sendYesNo("親愛的顧客，想要移動至充滿歡樂的休菲凱曼之怪物公園嗎？");
    }
}

function action(mode, type, selection) {
    if (mode == 1) {
        if (cm.getMapId() == 951000000) {
            cm.warp(cm.getSavedLocation("MONSTER_PARK"), 0);
            cm.clearSavedLocation("MONSTER_PARK");
        } else {
            cm.saveLocation("MONSTER_PARK");
            cm.warp(951000000, 0);
        }
    }
    cm.dispose();
}