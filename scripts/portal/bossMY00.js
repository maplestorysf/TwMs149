function enter(pi) {
    if (!pi.haveItem(4032246)) {
        pi.playerMessage(5, "你沒有夢幻公園的意念，因而無法進入。");
    } else {
        pi.openNpc(9270047);
    }
}