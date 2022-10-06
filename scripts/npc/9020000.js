/*
	Lakelis - Victoria Road: Kerning City (103000000)
**/
function start() {
    cm.removeAll(4001007);
    cm.removeAll(4001008);
    if (cm.getPlayer().getMapId() != 910340700) {
        cm.sendYesNo("你想要到組隊任務地圖嗎？");
        return;
    }
    if (cm.getParty() == null) { // No Party
        cm.sendSimple("你和你的組員該如何共同完成任務？在這裡，你會發現很多障礙和問題，除非你們有良好的團隊默契，否則你們將無法完成它，如果你們想要挑戰#b第一次同行#k，請透過#b隊長#k找我說話。#b\r\n#L0#我想要軟趴趴鞋子#l");
    } else if (!cm.isLeader()) { // Not Party Leader
        cm.sendSimple("如果你想要挑戰組隊任務，請透過#b組隊隊長#k來找我對話。#b\r\n#L0#我想要軟趴趴鞋子#l");
    } else {
        // Check if all party members are within Levels 21-30
        var party = cm.getParty().getMembers();
        var mapId = cm.getMapId();
        var next = true;
        var levelValid = 0;
        var inMap = 0;

        var it = party.iterator();
        while (it.hasNext()) {
            var cPlayer = it.next();
            if ((cPlayer.getLevel() >= 20 && cPlayer.getLevel() <= 69) || cPlayer.getJobId() == 900) {
                levelValid += 1;
            } else {
                next = false;
            }
            if (cPlayer.getMapid() == mapId) {
                inMap += (cPlayer.getJobId() == 900 ? 4 : 1);
            }
        }
        if (party.size() > 6 || inMap < 2) {
            next = false;
        }
        if (next) {
            var em = cm.getEventManager("KerningPQ");
            if (em == null) {
                cm.sendSimple("組隊任務異常，請通報遊戲管理員。#b\r\n#L0#我想要軟趴趴鞋子#l");
            } else {
				//em.setProperty("state", "0");
                var prop = em.getProperty("state");
				//em.setProperty("state", "0");
				//cm.playerMessage(prop);
                if (prop == null || prop.equals("0")) {
                    em.startInstance(cm.getParty(), cm.getMap(), 70);
                    cm.dispose();
                } else {
                    cm.sendSimple("其他組隊正在挑戰#r第一次同行#k，請耐心等候。#b\r\n#L0#我想要軟趴趴鞋子#l");
                }
                cm.removeAll(4001008);
                cm.removeAll(4001007);
            }
        } else {
            cm.sendSimple("請檢查隊員數量。目前共 #b" + levelValid.toString() + "#k 名隊員符合等級限制，#b" + inMap.toString() + "#k 名隊員在此地圖裡。如果不正確，請#b重新登入#k或者重新組隊。#b\r\n#L0#我想要軟趴趴鞋子#l");
        }
    }

}

function action(mode, type, selection) {
    if (cm.getPlayer().getMapId() != 910340700) {
        cm.saveLocation("MULUNG_TC");
        cm.warp(910340700, 0);
    } else {
        if (!cm.canHold(1072533, 1)) {
            cm.sendOk("請檢查裝備欄位空間。");
        } else if (cm.haveItem(4001531, 10)) {
            cm.gainItem(4001531, -10); //should handle automatically for "have"
            cm.gainItem(1072533, 1);
        } else {
            cm.sendOk("請收集10個軟塌的液體。");
        }
    }
    cm.dispose();
}