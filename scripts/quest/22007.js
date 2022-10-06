var status = -1;

function start(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0) {
            status -= 2;
        } else {
            qm.sendNext("不願意？那就算了。孵化器我就不能給你。");
            qm.dispose();
            return;
        }
    }
    if (status == 0) {
        qm.sendNext(" 嗯，好吧，我把孵化器給你，但你要幫我做一件事。媽媽讓我去收 雞蛋 ，我還沒去呢。啊~因為我覺的麻煩。如果你能幫我去收 雞蛋 ，我就把孵化器給你。怎麼樣？可以嗎？");
    } else if (status == 1) {
        qm.sendOk(" 好的，那你快到#b右邊的 雞蛋桶#k 去，把 雞蛋 拿回來。點擊雞蛋桶 ，就可以獲得 雞蛋 。拿太多的話，會不太方便，你只要拿1個回來就行。");
        qm.forceStartQuest();
        qm.dispose();
    }
}

function end(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0) {
            status -= 2;
        } else {
            qm.sendNext("嗯？奇怪。孵化器沒有設置好。重新嘗試一下吧。");
            qm.dispose();
            return;
        }
    }
    if (status == 0) {
        qm.sendNext("哦，雞蛋 拿來了嗎？快把蛋給我吧。我來幫你把它孵化。");
    } else if (status == 1) {
        qm.sendYesNo("來，拿著。不知道這到底可以用來幹什麼…… \r\n\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 360 exp");
    } else if (status == 2) {
        qm.forceCompleteQuest();
        qm.gainExp(360);
        if (qm.haveItem(4032451)) {
            qm.gainItem(4032451, -1);
        }
        qm.evanTutorial("UI/tutorial/evan/9/0", 1);
        qm.dispose();
    }
}