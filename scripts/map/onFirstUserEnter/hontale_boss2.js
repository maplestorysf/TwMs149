/* global ms, java */

var nameList = ["HorntailBattle_Normal", "HorntailBattle_Chaos", "HorntailBattle_Easy"];

function start() {
    var nameIndex = ms.getMapId() % 10;
    var em = ms.getEventManager(nameList[nameIndex]);
    var eim = ms.getEventInstance();

    if (em !== null && eim !== null) {
        var prop = eim.getProperty("preheadCheck");

        if (prop !== null || prop.equals("1")) {
            ms.mapMessage(6, "從深洞窟裡有巨大的生物體靠近.");
            eim.setProperty("preheadCheck", "2");
            var mobId = 0;
            switch (nameIndex) {
                case 0:
                    mobId = 8810025;
                    break;
                case 1:
                    mobId = 8810129;
                    break;
                case 2:
                    mobId = 8810213;
                    break;
            }
            var mob = em.getMonster(mobId);
            var map = eim.setInstanceMap(ms.getMapId());
            if (mob !== null && map !== null) {
                map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(-354, 230));
            }
        }
    }
    ms.dispose();
}