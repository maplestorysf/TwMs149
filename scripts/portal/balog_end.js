function enter(pi) {
    if (!pi.canHold(4001261, 1)) {
        pi.playerMessage(5, "請空出一個其他欄位。");
        return false;
    }
    pi.gainExpR(pi.getPlayer().getMapId() == 105100301 ? 130000 : 260000);
    pi.gainItem(4001261, 1);
    pi.warp(105100100, 0);
    pi.playPortalSE();
}