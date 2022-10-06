/* guild creation npc */
var status = -1;
var sel;

function start() {
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
        cm.sendSimple("你想要做什麼？\r\n#b#L0#建立公會#l\r\n#L1#解散公會#l\r\n#L2#擴增公會人數上限(最大可擴充至100人)(使用楓幣)#l\r\n#L3#擴增公會人數上限(最大可擴充至200人)(使用公會GP)#l#k");
    else if (status == 1) {
        sel = selection;
        if (selection == 0) {
            if (cm.getPlayerStat("GID") > 0) {
                cm.sendOk("無法同時加入兩個公會。");
                cm.dispose();
            } else
                cm.sendYesNo("建立公會需要 #b500,000 楓幣#k，你真的要建立公會嗎？");
        } else if (selection == 1) {
            if (cm.getPlayerStat("GID") <= 0 || cm.getPlayerStat("GRANK") != 1) {
                cm.sendOk("只有公會會長可以解散公會。");
                cm.dispose();
            } else
                cm.sendYesNo("你確定要解散公會嗎？#bGP值#k會完全初始化，無法恢復哦！");
        } else if (selection == 2) {
            if (cm.getPlayerStat("GID") <= 0 || cm.getPlayerStat("GRANK") != 1) {
                cm.sendOk("只有公會會長可以擴增公會人數上限。");
                cm.dispose();
            } else
                cm.sendYesNo("擴增公會人數上限 #b5人#k 需要 #b500,000 楓幣#k，你真的要擴增公會人數上限嗎？");
        } else if (selection == 3) {
            if (cm.getPlayerStat("GID") <= 0 || cm.getPlayerStat("GRANK") != 1) {
                cm.sendOk("只有公會會長可以擴增公會人數上限。");
                cm.dispose();
            } else
                cm.sendYesNo("擴增公會人數上限 #b5人#k 需要 #b25,000 GP#k，你真的要擴增公會人數上限嗎？");
        }
    } else if (status == 2) {
        if (sel == 0 && cm.getPlayerStat("GID") <= 0) {
            cm.genericGuildMessage(1);
            cm.dispose();
        } else if (sel == 1 && cm.getPlayerStat("GID") > 0 && cm.getPlayerStat("GRANK") == 1) {
            cm.disbandGuild();
            cm.dispose();
        } else if (sel == 2 && cm.getPlayerStat("GID") > 0 && cm.getPlayerStat("GRANK") == 1) {
            cm.increaseGuildCapacity(false);
            cm.dispose();
        } else if (sel == 3 && cm.getPlayerStat("GID") > 0 && cm.getPlayerStat("GRANK") == 1) {
            cm.increaseGuildCapacity(true);
            cm.dispose();
        }
    }
}