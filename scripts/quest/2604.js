/* Cygnus revamp
 Dualblade tutorial
 Ryden
 Made by Daenerys
 */

var status = -1;

function start(mode, type, selection) {
    if (mode == 1)
        status++;
    else
        status--;
    if (status == 0) {
        qm.sendNext("呵呵呵… 這次來了有趣的新生？西巴嫌你動作慢你連眼睛都不眨一下，不會心情不好嗎？實際上西巴只是想測試你。你有成為影武者的才華。");
    } else if (status == 1) {
        qm.sendNextPrev("除了影武者的才華之外，還有別的才華... 這樣不能只做平凡的修練！#b有特殊才能的人要給予特別任務#k！這是我們影武者的方式。");
    } else if (status == 2) {
        qm.sendNextPrev("什麼任務呢？ 這個… 還不能說明。 把你正式介紹給#b雪姬#k。雪姬喜歡你的話，就得取得特別任務，不喜歡的話… 就做平凡的修練。努力讓雪姬欣賞你吧！");
    } else if (status == 3) {
        qm.sendYesNo("答應之後就送你去找雪姬。");
    } else if (status == 4) {
        qm.warp(103050101);
        qm.removeNpc(103050910, 1057001);
        qm.forceStartQuest();
        qm.dispose();
        qm.warp(103050101, 1);
    }
}
function end(mode, type, selection) {
    qm.dispose();
}