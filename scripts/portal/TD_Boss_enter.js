function enter(pi) {
	if (pi.getPlayer().getParty() == null || !pi.isLeader()) {
		pi.playerMessage("請找隊長來和我談話。");
	} else {
		var party = pi.getPlayer().getParty().getMembers();
		var mapId = pi.getPlayer().getMapId();
		var next = true;
		var size = 0;
		var it = party.iterator();
		while (it.hasNext()) {
			var cPlayer = it.next();
			var ccPlayer = pi.getPlayer().getMap().getCharacterById(cPlayer.getId());
			if (ccPlayer == null) {
				next = false;
				break;
			}
			size += (ccPlayer.isGM() ? 4 : 1);
		}
		if (next && (pi.getPlayer().isGM() || size >= 2)) {
			for (var i = 0; i < 7; i++) {
				if (pi.getMap(pi.getMapId() + 1 + i) != null && pi.getMap(pi.getMapId() + 1 + i).getCharactersSize() == 0) {
					pi.warpParty(pi.getMapId() + 1 + i);
					return;
				}
			}
			pi.playerMessage("已經有人在挑戰了.");
		} else {
			pi.playerMessage("需要兩個以上的成員才能進入.");
		}
	}
}
