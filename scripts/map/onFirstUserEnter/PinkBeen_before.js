/* global ms */

function start() {
    var eim = ms.getEventInstance();
    if (eim !== null) {
        var prop = eim.getProperty("spawnNpc");
        if (prop === null || prop === "0") {
            eim.setProperty("spawnNpc", "1");
            ms.spawnNpc(2141000, -190, -42);
        }
    }
    ms.dispose();
}