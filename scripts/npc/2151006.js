var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    cm.getPlayer().getStat().heal(cm.getPlayer());
    cm.sendNext("請繼續戰鬥！我會幫你治癒傷口。");
    cm.safeDispose();
}