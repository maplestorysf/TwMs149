function enter(pi) {
	if (pi.isQuestActive(20033)) {
		var blocked = true;
		for (var i = 913070020; i <= 913070039; i++)
			if (pi.getPlayerCount(i) == 0) {
				if (pi.itemQuantity(4033196) >= 1)
					pi.gainItem(4033196, -pi.itemQuantity(4033196));
				pi.resetMap(i);
				pi.warp(i, 0);
				blocked = false;
				return true;
				break;
			}
	} else {
		pi.getPlayer().dropMessage(-1, "林伯特想見您!");
		blocked = false;
		return false;
	}
	if (blocked)
		pi.getPlayer().dropMessage(5, "已經有人在偷雞蛋了，請稍後。");
	return true;
}
