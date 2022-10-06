var status = -1;

function start(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0) {
            status -= 2;
        } else {
            qm.sendNext("嗯？什麼？害怕 陰險的狐狸 ？沒想到我弟弟這麼膽小。");
            qm.dispose();
            return;
        }
    }
    if (status == 0) {
        qm.sendAcceptDecline("你不覺得奇怪嗎？最近的雞怎麼和以前不一樣了？以前它們會下很多 雞蛋 ，但現在越來越少了。是不是因為狐狸增多了呢？那樣的話，必須趕緊想辦法才行。你說對不對？");
    } else if (status == 1) {
        qm.forceStartQuest();
        qm.sendNext("好吧，讓我們去消滅狐狸吧。你先去 #b後院#k 消滅#r10隻 陰險的狐狸#k 。\r\n我會負責剩下的事情的。\r\n好了，你快到後院去砸雞蛋吧~");
    } else if (status == 2) {
        qm.evanTutorial("UI/tutorial/evan/10/0", 1);
        qm.dispose();
    }
}

function end(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0) {
            status -= 2;
        } else {
            qm.dispose();
            return;
        }
    }
    if (status == 0) {
        qm.sendNext("陰險的狐狸，消滅掉了嗎？");
    } else if (status == 1) {
        qm.PlayerToNpc("#b你說要去收拾剩下的狐狸的，怎麼回事？");
    } else if (status == 2) {
        qm.sendNextPrev("啊，那個嘛？我後來是去了，但走錯了路，怕被 #o9300385# 抓去做人質，所以就回來了。");
    } else if (status == 3) {
        qm.PlayerToNpc("#b該不會是害怕狐狸而躲起來了吧？");
    } else if (status == 4) {
        qm.sendNextPrev("你在胡說什麼啊？！我為什麼會害怕狐狸？！我一點都不害怕狐狸！");
    } else if (status == 5) {
        qm.PlayerToNpc("#b……啊，有一隻 #o9300385# !");
    } else if (status == 6) {
        qm.sendNextPrev("啊！快躲起來！");
    } else if (status == 7) {
        qm.PlayerToNpc("#b……");
    } else if (status == 8) {
        qm.sendNextPrev("......");
    } else if (status == 9) {
        qm.sendNextPrev("……你這傢伙。別嚇哥哥我！哥哥我的心臟不好，不能受驚嚇！");
    } else if (status == 10) {
        qm.PlayerToNpc("#b(所以叫哥哥才不願意去，叫我去。)");
    } else if (status == 11) {
        qm.sendNextPrev("哼哼，不管怎樣，陰險的狐狸 消滅掉了。辛苦你了。我把一個路過的冒險家送我的東西送給你，作為給你的報酬。來，拿著。 \r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0# \r\n#i1372043# 1個 #t1372043# \r\n#i2022621# 25個 #t2022621# \r\n#i2022622# 25個 #t2022622# \r\n\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 910 exp");
    } else if (status == 12) {
        qm.forceCompleteQuest();
        qm.gainItem(1372043, 1);
        qm.gainItem(2022621, 25);
        qm.gainItem(2022622, 25);
        qm.gainExp(910);
        qm.sendNextPrev("是#b魔法師的攻擊武器短杖。#k 雖然你也可能沒什麼用，但拿在手裡到處走，還是很帥的，哈哈哈。");
    } else if (status == 13) {
        qm.sendPrev("狐狸的數量確實增加了，對吧？奇怪。狐狸的數量為什麼會增加呢？看來必須調查一下。");
        qm.dispose();
    }
}