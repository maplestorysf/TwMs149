/* global ms */
var status = -1;

function action(mode, type, selection) {
    if (mode === 0) {
        status--;
    } else {
        status++;
    }

    var i = -1;
    if (status <= i++) {
        ms.dispose();
    } else if (status === i++) {
        ms.getDirectionStatus(true);
        ms.lockUI(1, 1);
        ms.playerWaite();
        ms.spawnNPCRequestController(9330204, 807, -100, 0, 8520701);
        ms.teachSkill(228, 1, 1);
        ms.say(0, 9330204, 3, "這是什麼？初次見到的物品？", false, true);
        ms.getDirectionStatus(true);
    } else if (status === i++) {
        ms.say(0, 9330204, 1, "現在沒有時間說明了。但是…", true, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 1, "請幫我打聽可以變得更強的…真相。", true, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 1, "快點坐上船！", true, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 1, "趕緊。", true, true);
    } else if (status === i++) {
        ms.setNPCSpecialAction(8520701, "move", 0, true);
        ms.updateNPCSpecialAction(8520701, -1, 837, 100);
        ms.exceTime(4000);
    } else if (status === i++) {
        ms.playerMoveLeft();
        ms.exceTime(3000);
    } else if (status === i++) {
        ms.forcedInput(5);
        ms.exceTime(100);
    } else if (status === i++) {
        ms.playerMoveLeft();
        ms.exceTime(100);
    } else if (status === i++) {
        ms.playerMoveRight();
        ms.exceTime(100);
    } else if (status === i++) {
        ms.playerWaite();
        ms.say(0, 9330204, 3, "耶願，這艘船有點奇怪？也沒有錨！真奇怪，慢慢的移動了！", false, true);
    } else if (status === i++) {
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.spawnNPCRequestController(9330212, 263, -31, 1, 8521438);
        ms.spawnNPCRequestController(9330212, 288, -31, 1, 8521439);
        ms.spawnNPCRequestController(9330212, 200, -31, 0, 8521440);
        ms.spawnNPCRequestController(9330212, 430, -31, 0, 8521441);
        ms.spawnNPCRequestController(9330212, 170, -31, 0, 8521442);
        ms.spawnNPCRequestController(9330212, 230, -31, 0, 8521443);
        ms.spawnNPCRequestController(9330213, 380, -31, 0, 8521444);
        ms.spawnNPCRequestController(9330212, 490, -31, 0, 8521445);
        ms.spawnNPCRequestController(9330212, 545, -31, 0, 8521446);
        ms.spawnNPCRequestController(9330212, 600, -31, 0, 8521447);
        ms.spawnNPCRequestController(9330212, 612, -31, 0, 8521448);
        ms.spawnNPCRequestController(9330213, 678, -31, 0, 8521449);
        ms.spawnNPCRequestController(9330212, 701, -31, 0, 8521450);
        ms.spawnNPCRequestController(9330212, 731, -31, 0, 8521451);
        ms.spawnNPCRequestController(9330212, 800, -31, 0, 8521452);
        ms.say(0, 9330204, 3, "耶願!剛剛看到的皇帝的部下！", false, true);
    } else if (status === i++) {
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.say(0, 9330204, 1, "快要出發了。", false, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 3, "父...父親呢? 父親說他應該會到渡口啊。", true, true);
    } else if (status === i++) {
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.say(0, 9330205, 1, "被至尊步控制住了!趕緊抓住那兩個傢伙! ", false, true);
    } else if (status === i++) {
        ms.spawnNPCRequestController(9330205, 490, -31, 1, 8521755);
        ms.updateNPCSpecialAction(8521755, -1, 300, 100);
        ms.say(0, 9330204, 3, "耶願，這是什麼意思呢！？父親？父親？現在不管父親就走了嗎？", true, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 3, "什麼，回答一下。趕快!", true, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 1, "洪武團長不會來。", true, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 3, "什麼?!!!", true, true);
    } else if (status === i++) {
        ms.say(0, 9330204, 1, "我說謊了。團長不會回來了。", true, true);
    } else if (status === i++) {
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.lockUI(0);
        ms.removeNPCRequestController(8520701);
        ms.removeNPCRequestController(8521438);
        ms.removeNPCRequestController(8521439);
        ms.removeNPCRequestController(8521440);
        ms.removeNPCRequestController(8521441);
        ms.removeNPCRequestController(8521442);
        ms.removeNPCRequestController(8521443);
        ms.removeNPCRequestController(8521444);
        ms.removeNPCRequestController(8521445);
        ms.removeNPCRequestController(8521446);
        ms.removeNPCRequestController(8521447);
        ms.removeNPCRequestController(8521448);
        ms.removeNPCRequestController(8521449);
        ms.removeNPCRequestController(8521450);
        ms.removeNPCRequestController(8521451);
        ms.removeNPCRequestController(8521452);
        ms.removeNPCRequestController(8521755);
        ms.dispose();
        ms.warp(743020400, 0);
    } else {
        ms.dispose();
    }
}
