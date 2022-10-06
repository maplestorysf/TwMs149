load('nashorn:mozilla_compat.js');
importPackage(Packages.client);

function enter(pi) {
	switch (pi.getMapId()) {
	case 955000300:
		if (pi.getMap().getAllMonstersThreadsafe().size() == 0) {
			pi.getPlayer().gainExp(30000, true, true, true);
			pi.getPlayer().addHonorExp(100 * pi.getPlayer().getHonorLevel(), false);
			pi.getPlayer().dropMessage(5, "您獲得了 " + 100 * pi.getPlayer().getHonorLevel() + " 聲望。");
			pi.warp(262010000, 0);
		} else {
			pi.playerMessage(5, "請先把所有的怪物消滅。");
		}
		break;
	}
}
