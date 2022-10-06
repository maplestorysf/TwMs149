var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
        }
        status--;
    }
    if (status == 0) {
        cm.sendOk("目前戰鬥廣場關閉中。");
        cm.dispose();
        //cm.sendSimple((cm.getPlayer().getMapId() != 960000000 ? "\r\n#L5##b前往戰鬥廣場#k#l" : "\r\n#L5##b回到城鎮#k#l"));
    } else if (status == 1) {
        cm.warp(cm.getPlayer().getMapId() != 960000000 ? 960000000 : 100000000, 0);
        cm.dispose();
    }
}