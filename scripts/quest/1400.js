var status = -1;
var sel = -1;

function start(mode, type, selection) {
    switch (mode) {
        case 1:
            status++;
            break;
        default:
            status--;
            break;
    }
    if (status == 0) {
        qm.sendYesNo("嗯，你決定好你想轉職的職業了嗎？你可以成為一個擁有強大力量與體力的劍士或一個擁有魔法的法師或一個使用箭矢的弓箭手以及一個可快速攻擊的盜賊或者使用各種華麗連鎖技的海盜…有這麼多！");
    } else if (status == 1) {
        qm.sendSimple("只要去維多利亞島的話，就可以透過轉職官轉職為想要的職業。在那之前，若是提前告知想要的職業，我會寫推薦信給他們的。若是事先獲得推薦，就可以輕易進行轉職。你打算選哪一項職業呢？\r\n#b#L0#擁有強大力量與體力的劍士#l\r\n#b#L1#會利用各種魔法與敵人戰鬥的法師#l\r\n#b#L2#會利用迅速且正確的弓箭在遠處攻擊的弓箭手#l\r\n#b#L3#在黑暗中發揮能力的盜賊#l\r\n#b#L4#以各種連續技為傲的海盜#l");
    } else if (status == 2) {
        sel = selection;
        if (sel != -1) {
            qm.startQuest(1400);
        }
        if (sel == 0) {
            qm.sendNext("#b戰士#k是具有強大攻擊力和體力的職業，在戰場的最前線發揮作用。基本攻擊非常強大的職業，不斷學習高級技術，能夠發揮更強大的力量。");
        } else if (sel == 1) {
            qm.sendNext("#b法師#k擁有華麗效果的屬性魔法和組隊狩獵時非常實用的各種輔助魔法。而且2轉後能夠學習的屬性魔法可以對相反屬性的敵人賦予致命的傷害。");
        } else if (sel == 2) {
            qm.sendNext("#b弓箭手#k是具有敏捷性和力量的職業，在戰場的後方負責遠距離攻擊，利用地形的狩獵也非常強有力。");
        } else if (sel == 3) {
            qm.sendNext("#b盜賊#k是具有運氣和一定敏捷性和力量的職業，在戰場上經常使用突襲對方或藏身的特殊技能。具有相當高的機動性和回避率的盜賊，具有各種技術，操作起來非常有趣。");
        } else if (sel == 4) {
            qm.sendNext("#b海盜#k以出色的敏捷性和力量為基礎，向敵人發射百發百中的槍，或用體術將敵人瞬間消滅。槍手可以按照屬性選擇槍彈，有效地進行攻擊，或在船上進行更強大的攻擊，打手可由變身來發揮強有力的體術。");
        }
    } else if (status == 3) {
        if (sel == 0) {
            qm.sendNextPrev("#b10級#k時請到#b勇士之村#k找#b武術教練#k轉職哦！");
            //qm.forceStartQuest(1401);
            qm.forceCompleteQuest(1400);
            qm.dispose();
        } else if (sel == 1) {
            qm.sendNext("#b8級#k時請到#b魔法森林#k找#b漢斯#k轉職哦！");
            //qm.forceStartQuest(1402);
            qm.forceCompleteQuest(1400);
            qm.dispose();
        } else if (sel == 2) {
            qm.sendNext("#b10級#k時請到#b弓箭手村#k找#b赫麗娜#k轉職哦！");
            //qm.forceStartQuest(1403);
            qm.forceCompleteQuest(1400);
            qm.dispose();
        } else if (sel == 3) {
            qm.sendNext("#b10級#k時請到#b墮落城市#k找#b達克魯#k轉職哦！");
            //qm.forceStartQuest(1404);
            qm.forceCompleteQuest(1400);
            qm.dispose();
        } else if (sel == 4) {
            qm.sendNext("#b10級#k時請到#b鯨魚號#k找#b卡伊琳#k轉職哦！");
            //qm.forceStartQuest(1405);
            qm.forceCompleteQuest(1400);
            qm.dispose();
        }
        qm.dispose();
    } else {
        qm.dispose();
    }
}