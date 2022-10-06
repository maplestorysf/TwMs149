function enter(pi) {
    var blocked = true;
    for (var i = 913070050; i <= 913070069; i++)
        if (pi.getPlayerCount(i) == 0) {
            pi.warp(i, 0);
            blocked = false;
            return false;
        }
    if (blocked)
        pi.getPlayer().dropMessage(5, "有人在這張地圖了，請稍等。");
    return true;
}