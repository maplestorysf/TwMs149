var maps = Array(954000000, 954010000, 954020000, 954030000, 954040000, 954050000);
var minLevel = Array(120, 125, 130, 140, 150, 165);
var maxLevel = Array(130, 135, 140, 150, 165, 200);

function start() {
    var selStr = "你要進去哪裡？\r\n";
    selStr += "#r(120級以上玩家可使用)#k\r\n#b";
    selStr += "#L0#廢棄的都市(Lv.120~130)\r\n";
    selStr += "#L1#死亡森林(Lv.125~135)\r\n";
    selStr += "#L2#監視之塔(Lv.130~140)\r\n";
    selStr += "#L3#龍之巢穴(Lv.140~150)\r\n";
    selStr += "#L4#忘卻的神殿(Lv.150~165)\r\n";
    selStr += "#L5#騎士團要塞(Lv.165~175)\r\n";
    cm.sendSimple(selStr);
}

function action(mode, type, selection) {
    if (mode == 1 && selection >= 0 && selection < maps.length) {
        if (cm.getParty() == null || !cm.isLeader()) {
            cm.sendOk("請藉由隊伍隊長找我對話。");
        } else {
            var party = cm.getParty().getMembers().iterator();
            var next = true;
            while (party.hasNext()) {
                var cPlayer = party.next();
                if (cPlayer.getLevel() < 120 || cPlayer.getMapid() != cm.getMapId()) {
                    next = false;
                }
            }
            if (!next) {
                cm.sendOk("請確認所有隊伍成員是否位在#b怪物公園#k且#b120級#k以上。");
            } else {
                var em = cm.getEventManager("MonsterPark");
                if (em == null || em.getInstance("MonsterPark" + maps[selection]) != null) {
                    cm.sendOk("此地圖正有人挑戰中，請稍候再試。");
                } else {
                    em.startInstance_Party("" + maps[selection], cm.getPlayer());
                }
            }
        }
    }
    cm.dispose();
}