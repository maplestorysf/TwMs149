var maps = Array(953000000, 953010000, 953020000, 953030000, 953040000, 953050000);
var minLevel = Array(70, 75, 85, 95, 100, 110);
var maxLevel = Array(80, 85, 95, 105, 110, 120);

function start() {
    var selStr = "你要進去哪裡？\r\n";
	selStr += "#r(70級以上，120級以下玩家可使用)#k\r\n#b";
	selStr += "#L0#烏山山坡(Lv.70~80)\r\n";
	selStr += "#L1#葛雷的隱身處(Lv.75~85)\r\n";
	selStr += "#L2#自動警衛區(Lv.85~95)\r\n";
	selStr += "#L3#苔蘚森林(Lv.95~105)\r\n";
	selStr += "#L4#天空森林修練場(Lv.100~110)\r\n";
	selStr += "#L5#禁忌的時間(Lv.110~120)\r\n";
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
                if (cPlayer.getLevel() < 70 || cPlayer.getLevel() > 120 || cPlayer.getMapid() != cm.getMapId()) {
                    next = false;
                }
            }
            if (!next) {
                cm.sendOk("請確認所有隊伍成員是否位在#b怪物公園#k且介於#b70級到120級#k間。");
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