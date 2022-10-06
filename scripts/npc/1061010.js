function start() {
    cm.sendYesNo("(可以藉由雪原聖地的神聖的石頭再進來，你確定要出去嗎?)");
}

function action(mode, type, selection) {
    if (mode == 1) {
        cm.warp(211040401, 0);
    }
    cm.dispose();
}
