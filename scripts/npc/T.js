var status = -1;

function action(mode, type, selection) {
    switch (mode) {
        case 1:
            status++;
            break;
        default:
            status--;
            break;
    }
    if (cm.getPlayer().getMapId() == 10000 ||
		cm.getPlayer().getMapId() == 20000 ||
		cm.getPlayer().getMapId() == 30000 ||
		cm.getPlayer().getMapId() == 30001 ||
		cm.getPlayer().getMapId() == 40000 ||
		cm.getPlayer().getMapId() == 50000 ||
		cm.getPlayer().getMapId() == 1000000 ||
		cm.getPlayer().getMapId() == 1000001 ||
		cm.getPlayer().getMapId() == 1000002 ||
		cm.getPlayer().getMapId() == 1000003 ||
		cm.getPlayer().getMapId() == 1010000 ||
		cm.getPlayer().getMapId() == 1010100 ||
		cm.getPlayer().getMapId() == 1010200 ||
		cm.getPlayer().getMapId() == 1010300 ||
		cm.getPlayer().getMapId() == 1010400 ||
		cm.getPlayer().getMapId() == 1020000 ||
		cm.getPlayer().getMapId() == 2000000 ||
		cm.getPlayer().getMapId() == 2000001) {
        cm.sendOk("#r楓之島#k地圖無法使用#b@npc#k指令。");
        cm.dispose();
        return;
    }
    switch (status) {
        case 0:
            var content = "請選擇服務項目：\r\n";
            content += "#b";
            content += "#L0#地圖傳送#l\r\n";
            content += "#L1#技能傳授#l\r\n";
            content += "#L2#兌換中心#l\r\n";
			//content += "#L2#楓葉兌換#l\r\n";
            content += "#k";
            cm.sendSimple(content);
            break;
        case 1:
            switch (selection) {
                case 0:
					var content = "請選擇地圖：\r\n";
					content += "#b";
					content += "#L0#維多利亞港#l\r\n";
					content += "#L1#弓箭手村#l\r\n";
					content += "#L2#魔法森林#l\r\n";
					content += "#L3#墮落城市#l\r\n";
					content += "#L4#鯨魚號#l\r\n";
					content += "#L5#天空之城#l\r\n";
					content += "#L6#冰原雪域#l\r\n";
					content += "#L7#玩具城#l\r\n";
					content += "#L8#神木村#l\r\n";
					content += "#L9#時間神殿#l\r\n";
					content += "#L10#納希沙漠#l\r\n";
					content += "#L11#瑪迦提亞城#l\r\n";
					content += "#L12#桃花仙境#l\r\n";
					content += "#L13#靈藥幻境#l\r\n";
					content += "#k";
                    cm.sendSimple(content);
                    break;
                case 1:
                    cm.sendSimple("請選擇你想獲得的技能：\r\n#L14怪物騎乘#l");
                    break;
                case 2:
                    cm.sendOk("目前尚未開放！");
                    cm.dispose();
                    break;
            }
            break;
		case 2:
			switch (selection) {
                case 0:
					cm.sendOk("0");
                    break;
                case 1:
                    cm.sendOk("1");
                    break;
                case 2:
                    cm.sendOk("2");
                    cm.dispose();
                    break;
            }
			break;
        default:
            cm.dispose();
            break;
    }
}