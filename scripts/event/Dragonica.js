var minPlayers = 3;
var number;

function init() {
	em.setProperty("state", "0");
	em.setProperty("leader", "true");
	em.setProperty("clear", "0");
}

function setup(level, leaderid) {
	em.setProperty("state", "1");
	em.setProperty("leader", "true");
	var eim = em.newInstance("Dragonica" + leaderid);

	eim.setInstanceMap(240080100).resetPQ(level);
	eim.setInstanceMap(240080200).resetPQ(level);
	var map3 = eim.setInstanceMap(240080300);
	map3.resetPQ(level);
	var mob3 = em.getMonster(8300006);
	mob3.changeLevel(level);
	eim.registerMonster(mob3);
	map3.spawnMonsterOnGroundBelow(mob3, new java.awt.Point(1535, 146));
	
	eim.setInstanceMap(240080400);
	var map4 = eim.setInstanceMap(240080500);
	map4.resetPQ(level);
	var mob4 = em.getMonster(8300007);
	mob4.changeLevel(level);
	eim.registerMonster(mob4);
	map4.spawnMonsterOnGroundBelow(mob4, new java.awt.Point(700, -10));

	eim.startEventTimer(1200000); //20 分鐘
	return eim;
}

function playerEntry(eim, player) {
	var map = eim.getMapInstance(0);
	player.changeMap(map, map.getPortal(0));
	number = eim.getMapInstance(0).getAllMonstersThreadsafe().size();
}

function playerRevive(eim, player) {
	var map = eim.getMapInstance(0);
	player.addHP(50);
	player.changeMap(map, map.getPortal(0));
	return true;
}

function scheduledTimeout(eim) {
	end(eim);
}

function changedMap(eim, player, mapid) {
	if (mapid == 240080200) {
		number = eim.getMapInstance(1).getAllMonstersThreadsafe().size();
	}
	if (mapid == 240080400) {
		eim.restartEventTimer(3 * 60 * 1000);
	}
	if (mapid == 240080500) {
		eim.restartEventTimer(20 * 60 * 1000);
	}
	if (mapid < 240080100 || mapid > 240080500) {
		eim.unregisterPlayer(player);
		var map = em.getChannelServer().getMapFactory().getMap(240080050);
		player.changeMap(map, map.getPortal(0));

		if (eim.disposeIfPlayerBelow(0, 0)) {
			em.setProperty("state", "0");
			em.setProperty("leader", "true");
		}
	}
}

function playerDisconnected(eim, player) {
	return 0;
}

function monsterValue(eim, mobId) {
	if (mobId != 8300006 || mobId != 8300007) {
		number-=1;
		eim.broadcastPlayerMsg(-1, "怪物還剩下" +number + "隻。");
	}
	if (eim.getMapInstance(0).getAllMonstersThreadsafe().size() == 0 && em.getProperty("clear") == 0) {
		eim.PQClearEffect();
		em.setProperty("clear", "1");
	}
	if (eim.getMapInstance(1).getAllMonstersThreadsafe().size() == 0 && em.getProperty("clear") == 1) {
		eim.PQClearEffect();
		em.setProperty("clear", "2");
	}
	if (eim.getMapInstance(2).getAllMonstersThreadsafe().size() == 0 && em.getProperty("clear") == 2) {
		eim.PQClearEffect();
		em.setProperty("clear", "3");
	}
	return 1;
}

function playerExit(eim, player) {
	eim.unregisterPlayer(player);

	if (eim.disposeIfPlayerBelow(0, 0)) {
		em.setProperty("state", "0");
		em.setProperty("leader", "true");
	}
}

function end(eim) {
	eim.disposeIfPlayerBelow(100, 240080050);
	em.setProperty("state", "0");
	em.setProperty("leader", "true");
}

function clearPQ(eim) {
	end(eim);
}

function allMonstersDead(eim) {
	eim.PQClearEffect();
	if (eim.getMapInstance(4).getAllMonstersThreadsafe().size() == 0) {
		eim.getMapInstance(4).spawnNpc(2085003, new java.awt.Point(-420, -10));
		eim.broadcastPlayerMsg(-1, "請移動到入口的傳送點。");
	}
}

function leftParty(eim, player) {
	// If only 2 players are left, uncompletable:
	end(eim);
}
function disbandParty(eim) {
	end(eim);
}
function playerDead(eim, player) {}
function cancelSchedule() {}
