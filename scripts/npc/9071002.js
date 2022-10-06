var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    cm.sendOk("祝你在怪物公園玩得愉快！");
    cm.dispose();
}