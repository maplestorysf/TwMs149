var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
        }
        status--;
    }
    if (status == 0) {
        cm.removeAll(4001163);
        cm.removeAll(4001169);
        cm.removeAll(2270004);
        cm.sendSimple("#b#L0#兌換亞泰爾耳環#l\r\n#L1#兌換藍色亞泰爾耳環#l\r\n#L3#兌換溫暖的亞泰爾耳環#l\r\n#L2#挑戰毒霧森林組隊任務#l#k");
    } else if (status == 1) {
        if (selection == 0) {
            if (!cm.haveItem(1032060) && cm.haveItem(4001198, 10)) {
                cm.gainItem(1032060, 1);
                cm.gainItem(4001198, -10);
            } else {
                cm.sendOk("請收集10個亞泰爾碎片。");
            }
        } else if (selection == 1) {
            if (cm.haveItem(1032060) && !cm.haveItem(1032061) && cm.haveItem(4001198, 10)) {
                cm.gainItem(1032060, -1);
                cm.gainItem(1032061, 1);
                cm.gainItem(4001198, -10);
            } else {
                cm.sendOk("請先兌換亞泰爾耳環，並收集10個亞泰爾碎片。");
            }
        } else if (selection == 3) {
            if (cm.haveItem(1032061) && !cm.haveItem(1032101) && cm.haveItem(4001198, 10)) {
                cm.gainItem(1032061, -1);
                cm.gainItem(1032101, 1);
                cm.gainItem(4001198, -10);
            } else {
                cm.sendOk("請先兌換藍色亞泰爾耳環，並收集10個亞泰爾碎片。");
            }
        } else if (selection == 2) {
            if (cm.getPlayer().getParty() == null || !cm.isLeader()) {
                cm.sendOk("請透過隊長來找我對話。");
            } else {
                var party = cm.getPlayer().getParty().getMembers();
                var mapId = cm.getPlayer().getMapId();
                var next = true;
                var size = 0;
                var it = party.iterator();
                while (it.hasNext()) {
                    var cPlayer = it.next();
                    var ccPlayer = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
                    if (ccPlayer == null || ccPlayer.getLevel() < 70 || ccPlayer.getLevel() > 119) {
                        next = false;
                        break;
                    }
                    size += (ccPlayer.isGM() ? 4 : 1);
                }
                if (next && size >= 2) {
                    var em = cm.getEventManager("Ellin");
                    if (em == null) {
                        cm.sendOk("請稍候再試。");
                    } else {
                        em.startInstance(cm.getPlayer().getParty(), cm.getPlayer().getMap(), 120);
                    }
                } else {
                    cm.sendOk("未滿70級，大於119級玩家無法入場，隊伍人數必須大於(含)2人。");
                }
            }
        }
        cm.dispose();
    }
}