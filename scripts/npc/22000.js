/* 
	NPC Name: 		Shanks
	Map(s): 		Maple Road : Southperry (60000)
	Description: 		Brings you to Victoria Island
*/
var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status >= 0 && mode == 0) {
        cm.sendOk("嗯... 你在這邊還有還沒完成的事嗎？");
        cm.dispose();
        return;
    }
    if (mode == 1)
        status++;
    else
        status--;

    if (status == 0) {
        cm.sendYesNo("乘坐這艘船，可以到達更加寬廣的地方！只要支付#e150 楓幣#n，我就把你送往#b維多利亞島#k吧！不過只要離開這裡，你就不能再回來囉！怎麼樣？想要離開楓之島了嗎？");
    } else if (status == 1) {
        if (cm.haveItem(4031801)) {
            cm.sendNext("好，那給我150楓幣吧...嘿，那是什麼？路卡斯的推薦函？你應該要早一點告訴我！由於你被路卡斯所推薦，而你作為有著巨大潛力的冒險家，因此我不會收取你任何費用。");
        } else {
            cm.sendNext("這裡待膩了吧？來吧…先讓我我先收下#e150 楓幣#n…");
        }
    } else if (status == 2) {
        if (cm.haveItem(4031801)) {
            cm.sendNextPrev("那我們航向航向維多利亞港吧！抓緊了！");
        } else {
            if (cm.getPlayerStat("LVL") >= 7) {
                if (cm.getMeso() < 150) {
                    cm.sendOk("什麼！沒錢還敢說要去？奇怪的傢伙！");
                    cm.dispose();
                } else {
                    cm.sendNext("真棒！#e150#n 楓幣我收下了！航向維多利亞港吧！");
                }
            } else {
                cm.sendOk("讓我看看...嗯...我認為你還不夠強壯。你至少要#b7級#k才能去維多利亞港。");
                cm.dispose();
            }
        }
    } else if (status == 3) {
        if (cm.haveItem(4031801)) {
            cm.gainItem(4031801, -1);
            cm.warp(2010000, 0);
            cm.dispose();
        } else {
            cm.gainMeso(-150);
            cm.warp(2010000, 0);
            cm.dispose();
        }
    }
}