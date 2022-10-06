/*
	NPC Name: 		Cygnus
	Description: 		Quest - Encounter with the Young Queen
*/
var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        qm.dispose();
        return;
    }
    if (status == 0) {
        qm.sendNext("你好，騎士團團長。現在新楓之谷處於非常危險的處境，為了從黑魔法師手中守護新楓之谷，我們需要更多的兵力，並且為了培養更強大的戰力的方法，我聚集冒險家和長老們的力量，培養比冒險家更強韌的終極冒險家。");
    } else if (status == 1) {
        qm.sendYesNo("如果以終極冒險家出生，不但擁有50等級還會給予特殊的技能。如何？現在想變為終極冒險家嗎？");
    } else if (status == 2) {
        if (!qm.getClient().canMakeCharacter(qm.getPlayer().getWorld())) {
            qm.sendOk("請檢查角色欄位數量。");
        } else {
            qm.sendUltimateExplorer();
        }
        qm.dispose();
    }
}

function end(mode, type, selection) {}