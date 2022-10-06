var maps = Array(952000000, 952010000, 952020000, 952030000, 952040000);
var minLevel = Array(20, 45, 50, 55, 60);
var maxLevel = Array(30, 55, 60, 65, 70);

function start() {
    var selStr = "你要進去哪裡？\r\n";
	selStr += "#r(13級以上，70級以下玩家可使用)#k\r\n#b";
	selStr += "#L0#石巨人寺院(Lv.20~30)\r\n";
	selStr += "#L1#墮落廣場(Lv.45~55)\r\n";
	selStr += "#L2#魔女的雪原(Lv.50~60)\r\n";
	selStr += "#L3#寧靜的海(Lv.55~65)\r\n";
	selStr += "#L4#黑暗的神殿(Lv.60~70)\r\n";
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
                if (cPlayer.getLevel() < 13 || cPlayer.getLevel() > 70 || cPlayer.getMapid() != cm.getMapId()) {
                    next = false;
                }
            }
            if (!next) {
                cm.sendOk("請確認所有隊伍成員是否位在#b怪物公園#k且介於#b13級到70級#k間。");
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