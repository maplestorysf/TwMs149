/* global ms */

function start() {
    var eim = ms.getEventInstance();
    if (eim !== null) {
        var prop = eim.getProperty("spawnNpc");
        if (prop === null || prop === "0") {
            eim.setProperty("spawnNpc", "1");
            ms.spawnNpc(2161000, 0, -181);
        }
    }
    ms.dispose();
}