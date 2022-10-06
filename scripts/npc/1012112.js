var status = -1;
var minLevel = 20; // 35
var maxLevel = 69; // 65

var minPartySize = 2;
var maxPartySize = 6;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
            return;
        }
        status--;
    }
    if (cm.getPlayer().getMapId() != 910010500) {
        if (status == 0) {
            cm.sendYesNo("你想要移動到組隊任務地圖嗎？");
        } else {
            cm.saveLocation("MULUNG_TC");
            cm.warp(910010500, 0);
            cm.dispose();
        }
        return;
    }
    if (status == 0) {
        if (cm.getParty() == null) { // No Party
            cm.sendSimple("你和你的組員該如何共同完成任務？在這裡，你會發現很多障礙和問題，除非你們有良好的團隊默契，否則你們將無法完成它，如果你們想要挑戰#b月妙的年糕#k，請透過#b隊長#k找我說話。\r\n\r\n#r限制人數: " + minPartySize + "人(含)以上，組員必須介於 " + minLevel + " 至 " + maxLevel + " 級。#b\r\n#L0#我想要年糕頭飾#l");
        } else if (!cm.isLeader()) { // Not Party Leader
            cm.sendSimple("請透過#b隊長#k找我對話。#b\r\n#L0#我想要年糕頭飾#l");
        } else {
            // Check if all party members are within PQ levels
            var party = cm.getParty().getMembers();
            var mapId = cm.getMapId();
            var next = true;
            var levelValid = 0;
            var inMap = 0;
            var it = party.iterator();

            while (it.hasNext()) {
                var cPlayer = it.next();
                if ((cPlayer.getLevel() >= minLevel) && (cPlayer.getLevel() <= maxLevel)) {
                    levelValid += 1;
                } else {
                    next = false;
                }
                if (cPlayer.getMapid() == mapId) {
                    inMap += (cPlayer.getJobId() == 900 ? 6 : 1);
                }
            }
            if (party.size() > maxPartySize || inMap < minPartySize) {
                next = false;
            }
            if (next) {
                var em = cm.getEventManager("HenesysPQ");
                if (em == null) {
                    cm.sendSimple("組隊任務異常，請通報遊戲管理員。#b\r\n#L0#我想要年糕頭飾#l");
                } else {
                    var prop = em.getProperty("state");
                    if (prop.equals("0") || prop == null) {
                        em.startInstance(cm.getParty(), cm.getMap(), 70);
                        cm.removeAll(4001101);
                        cm.dispose();
                        return;
                    } else {
                        cm.sendSimple("其他隊伍正在挑戰 #r月妙的年糕#k，請耐心等候。#b\r\n#L0#我想要年糕頭飾#");
                    }
                }
            } else {
                cm.sendSimple("組隊人數或組員等級不正確。\r\n\r\n#r人數限制: " + minPartySize + "人(含)以上，組員必須介於 " + minLevel + " 至 " + maxLevel + " 級。#b\r\n#L0#我想要年糕頭飾#l");
            }
        }
    } else { //broken glass
        if (cm.haveItem(1002798, 1)) {
            if (!cm.canHold(1003266, 1)) {
                cm.sendOk("請檢查裝備欄位空間。");
            } else if (cm.haveItem(4001101, 20)) { //TODO JUMP
                cm.gainItem(1003266, 1);
                cm.gainItem(4001101, -20);
            } else {
                cm.sendOk("請收集20個月妙的年宵。");
            }
        } else if (!cm.canHold(1002798, 1)) {
            cm.sendOk("請檢查裝備欄位空間。");
        } else if (cm.haveItem(4001101, 10)) {
            cm.gainItem(4001101, -10); //should handle automatically for "have"
            cm.gainItem(1002798, 1);
        } else {
            cm.sendOk("請收集10個月妙的年宵。");
        }
        cm.dispose();

    }
}