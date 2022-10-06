function enter(pi) {
	if (pi.getMap().getReactorByName("ghostgate").getState() == 1 || (pi.getPlayer().getEventInstance() != null && pi.getPlayer().getEventInstance().getProperty("stage4clear") != null && pi.getPlayer().getEventInstance().getProperty("stage4clear").equals("true"))) {
		pi.warp(990000800, 0);
		return true;
	} else {
		pi.playerMessage("這個大門的鎖尚未解除。");
		return false;
	}
}
