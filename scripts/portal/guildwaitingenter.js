function enter(pi) {
	if (pi.getEventInstance() == null) {
		pi.warp(101030104);
	} else {
		if (pi.getEventInstance().getProperty("canEnter") != null && pi.getEventInstance().getProperty("canEnter").equals("true")) {
			pi.warp(990000100);
		} else {
			pi.playerMessage("公會戰大門還沒有打開。");
			return false;
		}
	}
	return true;
}
