/* guild emblem npc */
var status = 0;
var sel;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 && status == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1)
        status++;
    else
        status--;

    if (status == 0)
        cm.sendSimple("您好！我是管理#b公會徽章#k的#b蕾雅#k。\r\n#b#L0#我想登錄公會徽章#l#k");
    else if (status == 1) {
        sel = selection;
        if (selection == 0) {
            if (cm.getPlayerStat("GRANK") == 1)
                cm.sendYesNo("設定公會徽章需支付#b1,500,000 楓幣#k。所謂的徽章是各公會所能擁有的專屬標示，會出現在公會名稱左側的位置上。如何？想要製作公會徽章嗎？");
            else
                cm.sendOk("只有公會會長可以建立或變更公會徽章。");
        }

    } else if (status == 2) {
        if (sel == 0) {
            cm.genericGuildMessage(18);
            cm.dispose();
        }
    }
}