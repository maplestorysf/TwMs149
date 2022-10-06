/*
 *  Cliff - Happy Ville NPC
 */
var status = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status > 0) {
            status--;
        } else {
            cm.dispose();
            return;
        }
    }
    if (status == 0) {
        cm.sendNext("你有看到站在那裡的一群雪人嗎？和他們其中一人談談，他們會帶你一起去裝飾聖誕樹哦。聖誕樹可以用各種裝飾品進行裝飾。你怎麼看？聽起來很有趣，對吧？");
    } else if (status == 1) {
        cm.sendNextPrev("你提供聖誕樹的裝飾品只有你可以撿起來，所以不用擔心你的裝飾品會被其他人撿走喔！");
    } else if (status == 2) {
        cm.sendNextPrev("當然，聖誕樹的裝飾品永遠都不會消失，而當你離開地圖，裝飾品會全部回到你的身上，聽起來很酷，對吧？");
    } else if (status == 3) {
        cm.sendPrev("那麼，去找#b#p2002001##k購買聖誕裝飾並裝飾聖誕樹吧！哦，對了，最美麗的裝飾品已經被怪物帶走了，所以不能從他那裡購買，很可惜，對吧？");
        cm.dispose();
    }
}