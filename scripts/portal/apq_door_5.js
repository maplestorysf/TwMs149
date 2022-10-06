load('nashorn:mozilla_compat.js');
importpackage(org.rise.server.maps);
importpackage(org.rise.net.channel);
importpackage(org.rise.tools);

/*
Amoria: 2nd stage to 3rd stage portal
 */

function enter(pi) {
	var nextMap = 670010600;
	var eim = pi.getPlayer().getEventInstance();
	var target = eim.getMapInstance(nextMap);
	var targetPortal = target.getPortal("st00");
	// only let people through if the eim is ready
	var avail = eim.getProperty("4stageclear");
	if (avail == null) {
		// do nothing; send message to player
		pi.getClient().getSession().write(MaplePacketCreator.serverNotice(6, "開傳點尚未開放。"));
		return false;
	} else {
		pi.getPlayer().changeMap(target, targetPortal);
		return true;
	}
}
