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
	//cm.playerMessage(status);
    switch (status) {
        case 0:
            var content = "本伺服器指令清單如下：\r\n";
            content += "#b";
			content += "#L0#@公告#l\r\n";
			content += "#L1#@轉職#l\r\n"
            content += "#L2#@資訊#l\r\n";
            content += "#L3#@異常#l\r\n";
            content += "#L4#@怪物#l\r\n";
            content += "#L5#@掉寶#l\r\n";
            content += "#L6#@npc#l\r\n";
			content += "#L7#@訊息#l\r\n";
            content += "#k";
            cm.sendSimple(content);
            break;
        case 1:
            switch (selection) {
				case 0:
                    cm.sendOk("#r@公告#k指令可查詢本伺服器最新公告。");
					status = -1;
                    break;
				case 1:
                    cm.sendOk("#r@轉職#k指令提供玩家選擇職業。\r\n#r10級#k開始可使用此指令。");
					status = -1;
                    break;
                case 2:
                    cm.sendOk("#r@資訊#k指令可顯示我的個人資訊。\r\n包含我的樂豆點數、楓葉點數、Boss組隊任務點數。");
					status = -1;
                    break;
				case 3:
					cm.sendOk("#r@異常#k指令可自行排除異常。例如：無法與npc對話。\r\n如使用此指令仍未排除異常，請嘗試重新登入。");
					status = -1;
					break;
				case 4:
					cm.sendOk("#r@怪物#k指令可查詢鄰近怪物的詳細資料。");
					status = -1;
					break;
				case 5:
					cm.sendOk("#r@掉寶#k指令可調查怪物的掉寶資料。");
					status = -1;
					break;
				case 6:
					cm.sendOk("#r@npc#k指令提供相當便利的功能。\r\n#r10級#k開始可使用此指令。");
					status = -1;
					break;
				case 7:
					cm.sendOk("#r@訊息 <內容>#k指令可向遊戲管理員傳送訊息，@訊息的後面要空一格哦！");
					status = -1;
					break;
				case 8:
					cm.gainMeso(100000000);
					cm.dispose();
					//status = -1;
					break;
				case 9:
					cm.dispose();
					//status = -1;
					break;
				case 10:
					cm.gainItem(2070006, 1000);
					cm.dispose();
					//status = -1;
					break;
				case 11:
					cm.gainItem(2050004, 300);
					cm.dispose();
					//status = -1;
					break;
				case 12:
					cm.gainItem(2049700, 1);
					cm.dispose();
					//status = -1;
					break;
				case 13:
					cm.gainItem(5062000, 100);
					cm.dispose();
					//status = -1;
					break;
				case 14:
					cm.gainItem(2460003, 100);
					cm.dispose();
					//status = -1;
					break;
				case 15:
					cm.gainItem(1112127, 1);
					cm.dispose();
					//status = -1;
					break;
				case 16:
					cm.gainItem(1122034, 1);
					cm.dispose();
					//status = -1;
					break;
				case 17:
					cm.gainItem(1122035, 1);
					cm.dispose();
					//status = -1;
					break;
				case 18:
					cm.gainItem(1122036, 1);
					cm.dispose();
					//status = -1;
					break;
				case 19:
					cm.gainItem(1122037, 1);
					cm.dispose();
					//status = -1;
					break;
				case 20:
					cm.gainItem(1122038, 1);
					cm.dispose();
					//status = -1;
					break;
				case 21:
					cm.gainItem(5490000, 100);
					cm.dispose();
					//status = -1;
					break;
				case 22:
					cm.gainItem(5490001, 100);
					cm.dispose();
					//status = -1;
					break;
				case 23:
                    if (cm.getJob() == 2112) {
                        cm.teachSkill(21120005, 30, 30);
                        cm.dispose();
                    } else {
                        cm.sendOk("非四轉狂狼勇士無法使用。");
                    }
                    break;
				case 24:
                    if (cm.getJob() == 2217 || cm.getJob() == 2218) {
                        cm.maxSkillsByJob();
                        cm.dispose();
                    } else {
                        cm.sendOk("非龍魔導士無法使用。");
                    }
                    break;
				case 25:
					cm.gainItem(5062002, 100);
					cm.dispose();
					break;
				case 26:
					cm.gainItem(1202022, 1);
					cm.dispose();
					break;
				case 27:
                    if (cm.getJob() == 2312) {
                        cm.maxSkillsByJob();
                        cm.dispose();
                    } else {
                        cm.sendOk("非精靈殺手無法使用。");
                    }
					break;
				case 28:
                    if (cm.getJob() == 3112) {
                        cm.maxSkillsByJob();
                        cm.dispose();
                    } else {
                        cm.sendOk("非惡魔殺手無法使用。");
                    }
					break;
				case 29:
                    if (cm.getJob() == 1311 || cm.getJob() == 1312) {
                        cm.maxSkillsByJob();
                        cm.dispose();
                    } else {
                        cm.sendOk("非破風使者無法使用。");
                    }
					break;
				case 30:
                    if (cm.getJob() == 2312) {
                        cm.teachSkill(10000018, 1, 1);
						cm.teachSkill(20000024, 1, 1);
						cm.teachSkill(20011024, 1, 1);
						cm.teachSkill(20021024, 1, 1);
						cm.teachSkill(30011024, 1, 1);
						cm.teachSkill(30001024, 1, 1);
						cm.teachSkill(30001024, 1, 1);
                        cm.dispose();
                    } else {
                        cm.sendOk("非四轉精靈遊俠無法使用。");
                    }
					break;
            }
            break;
		default:
			cm.dispose();
			break;
    }
}