/* global ms */

function start() {
    var eim = ms.getEventInstance();
    if (eim !== null) {
        var prop = eim.getProperty("spawnNpc");
        if (prop === null || prop === "0") {
            eim.setProperty("spawnNpc", "1");
            ms.spawnNpc(2144010, 310, -181);
        }
    }
    ms.dispose();
}