/* global ms */

function start() {
    var eim = ms.getEventInstance();
    if (eim !== null) {
        var prop = eim.getProperty("spawnNpc");
        if (prop === null || prop === "0") {
            eim.setProperty("spawnNpc", "1");
            ms.spawnNpc(9130090, -454, 123);
        }
    }
    ms.dispose();
}