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
    switch (status) {
        case 0:
            var content = "請選擇兌換獎勵項目：\r\n";
            content += "#b";
            content += "#L5##b10級獎勵(獵人的幸運)#l\r\n";
            content += "#L4##b50級獎勵(800楓葉點數)#l\r\n";
            content += "#L3##b70級獎勵(30張楓葉轉蛋券)#l\r\n";
            //content += "#L2##b贊助獎勵(300元)#l\r\n";
            //content += "#L0##b贊助獎勵#l\r\n";
            //content += "#L1##b贊助獎勵#l\r\n";
            content += "#k";
            cm.sendSimple(content);
            break;
        case 1:
            switch (selection) {
                case 0: // 贊助600元
                    if (cm.getPlayer().getClient().getVip() > 0) {
                        cm.sendOk("無法重複獲得獎勵。");
                        cm.dispose();
                        return;
                    }
                    if (!cm.canHold(2049700, 3) || !cm.canHold(2049300, 10) || !cm.canHold(1112127, 1)) {
                        cm.sendOk("請空出一格裝備欄位，兩格消耗欄位。");
                        cm.dispose();
                        return;
                    }
                    if (cm.getPlayer().getName() == "雪吉拉GM" ||
                        cm.getPlayer().getName() == "佐鳥") {
                        cm.getPlayer().getClient().setVip(1);
                        cm.sendOk("恭喜你#r符合#k領取資格！\r\n請收下#b2500楓葉點數#k、#b稀有潛在能力卷軸100%x3#k、#b高級裝備強化卷軸x10#k、#bWelcome Back戒指#k。");
                        cm.gainItem(2049700, 3);
                        cm.gainItem(2049300, 10);
                        cm.gainItem(1112127, 1);
                        cm.dispose();
                    } else {
                        cm.sendOk("領取失敗。");
                        cm.dispose();
                    }
                    break;
                case 1: // 贊助1000元
                    if (cm.getPlayer().getClient().getVip() > 0) {
                        cm.sendOk("無法重複獲得獎勵。");
                        cm.dispose();
                        return;
                    }
                    if (!cm.canHold(2049700, 3) || !cm.canHold(2049300, 10) || !cm.canHold(1112127, 1) || !cm.canHold(1142249, 1) || !cm.canHold(1182006, 1) || !cm.canHold(5210000, 1)) {
                        cm.sendOk("請空出三格裝備欄位，兩格消耗欄位，一格特殊欄位。");
                        cm.dispose();
                        return;
                    }
                    if (cm.getPlayer().getName() == "雪吉拉GM" ||
                        cm.getPlayer().getName() == "kods" ||
						cm.getPlayer().getName() == "泡泡谷毒瘤") {
                        cm.getPlayer().getClient().setVip(2);
                        cm.sendOk("恭喜你#r符合#k領取資格！\r\n請收下#b5000楓葉點數#k、#b稀有潛在能力卷軸100%x3#k、#b高級裝備強化卷軸x10#k、#bWelcome Back戒指#k、#b我是幸運兒#k、#b傳說中的勇士胸章#k、#b經驗值加倍全日券#k。");
                        cm.gainItem(2049700, 3);
                        cm.gainItem(2049300, 10);
                        cm.gainItem(1112127, 1);
                        cm.gainItem(1142249, 1);
                        cm.gainItem(1182006, 1);
                        cm.gainItem(5210000, 1);
                        cm.dispose();
                    } else {
                        cm.sendOk("領取失敗。");
                        cm.dispose();
                    }
                    break;
                case 2: // 贊助300元
                    if (cm.getPlayer().getRewardLevel() == 1000) {
                        cm.sendOk("無法重複獲得獎勵。");
                        cm.dispose();
                        return;
                    }
                    if (cm.getPlayer().getName() == "雪吉拉GM") {
                        cm.sendOk("恭喜你#r符合#k領取資格！\r\n請收下#b900楓葉點數#k和#b稀有潛在能力卷軸x3#k和#b高級裝備強化卷軸x10#k。額外加送#b加拿大浣熊#k您#b獵人的幸運x3#k和#b不滅的雙手武器攻擊卷軸100x7#k唷！");
                        if (cm.canHold(2049700, 3) && cm.canHold(2049306, 10) && cm.canHold(2450000, 10) && cm.canHold(2046134, 7)) {
                            cm.getPlayer().setRewardLevel(2);
                            cm.gainItem(2049700, 3);
                            cm.gainItem(2049306, 10);
                            cm.gainItem(2450000, 3);
                            cm.gainItem(2046134, 7);
                            cm.dispose();
                        } else {
                            cm.dispose();
                        }
                    } else {
                        cm.sendOk("領取失敗。");
                        cm.dispose();
                    }
                    break;
                case 3: // 70級楓葉轉蛋券獎勵
                    if (cm.getPlayer().getRewardLevel() >= 0) {
                        cm.sendOk("限#b2017/08/03#k後新建角色領取。");
                        cm.dispose();
                        return;
                    }
                    if (cm.getPlayer().getLevel() < 70) {
                        cm.sendOk("未滿70級無法領取獎勵。");
                        cm.dispose();
                        return;
                    }
                    if (cm.getPlayer().getRewardLevel() == -1 || cm.getPlayer().getRewardLevel() == -2) {
                        cm.sendOk("請先領取10級和50級獎勵。");
                        cm.dispose();
                        return;
                    }
                    if (cm.getPlayer().getRewardLevel() == -4) {
                        cm.sendOk("無法重複獲得獎勵。");
                        cm.dispose();
                        return;
                    }
                    if (cm.getPlayer().getRewardLevel() == -3) {
                        cm.getPlayer().setRewardLevel(-4);
                        cm.sendOk("恭喜獲得 #b30張#k #b#v5220040:##k。");
                        cm.gainItem(5220040, 30);
                        cm.dispose();
                    } else {
                        cm.sendOk("領取失敗。");
                        cm.dispose();
                    }
                    break;
                case 4: // 50級獎勵
                    if (cm.getPlayer().getRewardLevel() >= 0) {
                        cm.sendOk("限#b2017/08/03#k後新建角色領取。");
                        cm.dispose();
                        return;
                    }
                    if (cm.getPlayer().getLevel() < 50) {
                        cm.sendOk("未滿50級無法領取獎勵。");
                        cm.dispose();
                        return;
                    }
                    if (cm.getPlayer().getRewardLevel() == -1) {
                        cm.sendOk("請先領取10級獎勵。");
                        cm.dispose();
                        return;
                    }
                    if (cm.getPlayer().getRewardLevel() == -3) {
                        cm.sendOk("無法重複獲得獎勵。");
                        cm.dispose();
                        return;
                    }
                    if (cm.getPlayer().getRewardLevel() == -2) {
                        cm.getPlayer().setRewardLevel(-3);
                        cm.sendOk("恭喜獲得 #b800楓葉點數#k。");
                        cm.gainNX(800);
                        cm.dispose();
                    } else {
                        cm.sendOk("領取失敗。");
                        cm.dispose();
                    }
                    break;
                case 5: // 10級獎勵
                    if (cm.getPlayer().getRewardLevel() >= 0) {
                        cm.sendOk("限#b2017/08/03#k後新建角色領取。");
                        cm.dispose();
                        return;
                    }
                    if (cm.getPlayer().getLevel() < 10) {
                        cm.sendOk("未滿10級無法領取獎勵。");
                        cm.dispose();
                        return;
                    }
                    if (cm.getPlayer().getRewardLevel() == -2) {
                        cm.sendOk("無法重複獲得獎勵。");
                        cm.dispose();
                        return;
                    }
                    if (!cm.canHold(2450000, 10)) {
                        cm.sendOk("請空出10格消耗欄位。");
                        cm.dispose();
                        return;
                    }
                    if (cm.getPlayer().getRewardLevel() == -1) {
                        cm.getPlayer().setRewardLevel(-2);
                        cm.sendOk("恭喜獲得#b10張獵人的幸運#k。");
                        cm.gainItem(2450000, 10);
                        cm.dispose();
                    } else {
                        cm.sendOk("領取失敗。");
                        cm.dispose();
                    }
                    break;
                case 6: // 贊助5000元
                    if (cm.getPlayer().getClient().getVip() > 0) {
                        cm.sendOk("無法重複獲得獎勵。");
                        cm.dispose();
                        return;
                    }
                    if (!cm.canHold(2049700, 3) || 
						!cm.canHold(2049300, 10) || 
						!cm.canHold(1112127, 1) || 
						!cm.canHold(1142249, 1) || 
						!cm.canHold(1182006, 1) || 
						!cm.canHold(5210000, 1) ||
						!cm.canHold(1003174, 1) ||
						!cm.canHold(1102277, 1) ||
						!cm.canHold(1082297, 1) ||
						!cm.canHold(1052316, 1) ||
						!cm.canHold(1072487, 1) ||
						!cm.canHold(1522018, 1) ||
						!cm.canHold(1122036, 1)) {
                        cm.sendOk("請空出十格裝備欄位，兩格消耗欄位，一格特殊欄位。");
                        cm.dispose();
                        return;
                    }
                    if (cm.getPlayer().getName() == "雪吉拉GM" ||
                        cm.getPlayer().getName() == "戀o殤") {
                        cm.getPlayer().getClient().setVip(3);
                        cm.sendOk("恭喜你#r符合#k領取資格！\r\n請收下#b25000楓葉點數#k、#b稀有潛在能力卷軸100%x3#k、#b高級裝備強化卷軸x10#k、#bWelcome Back戒指#k、#b我是幸運兒#k、#b傳說中的勇士胸章#k、#b經驗值加倍全日券#k、#b女皇套裝#k、#b楓葉之心#k。");
                        cm.gainNX(25000);
                        cm.gainItem(2049700, 3);
                        cm.gainItem(2049300, 10);
                        cm.gainItem(1112127, 1);
                        cm.gainItem(1142249, 1);
                        cm.gainItem(1182006, 1);
                        cm.gainItem(5210000, 1);
						cm.gainItem(1003174, 1);
						cm.gainItem(1102277, 1);
						cm.gainItem(1082297, 1);
						cm.gainItem(1052316, 1);
						cm.gainItem(1072487, 1);
						cm.gainItem(1522018, 1);
						cm.gainItem(1122036, 1);
                        cm.dispose();
                    } else {
                        cm.sendOk("領取失敗。");
                        cm.dispose();
                    }
                    break;
					case 7: // 特別開單
                    if (cm.getPlayer().getClient().getVip() > 0) {
                        cm.sendOk("無法重複獲得獎勵。");
                        cm.dispose();
                        return;
                    }
                    if (!cm.canHold(1003174, 1) ||
						!cm.canHold(1102277, 1) ||
						!cm.canHold(1082297, 1) ||
						!cm.canHold(1052316, 1) ||
						!cm.canHold(1072487, 1) ||
						!cm.canHold(1522018, 1) ||
						!cm.canHold(1122036, 1)) {
                        cm.sendOk("請空出七格裝備欄位。");
                        cm.dispose();
                        return;
                    }
                    if (cm.getPlayer().getName() == "雪吉拉GM" ||
                        cm.getPlayer().getName() == "太陽下一攤水") {
                        cm.getPlayer().getClient().setVip(3);
                        cm.sendOk("恭喜你#r符合#k領取資格！\r\n請收下#b11500楓葉點數#k、#b女皇套裝#k、#b楓葉之心#k。");
                        cm.gainNX(11500);
                        //cm.gainItem(2049700, 3);
                        //cm.gainItem(2049300, 10);
                        //cm.gainItem(1112127, 1);
                        //cm.gainItem(1142249, 1);
                        //cm.gainItem(1182006, 1);
                        //cm.gainItem(5210000, 1);
						cm.gainItem(1003174, 1);
						cm.gainItem(1102277, 1);
						cm.gainItem(1082297, 1);
						cm.gainItem(1052316, 1);
						cm.gainItem(1072487, 1);
						cm.gainItem(1522018, 1);
						cm.gainItem(1122036, 1);
                        cm.dispose();
                    } else {
                        cm.sendOk("領取失敗。");
                        cm.dispose();
                    }
                    break;
            }
            break;
        default:
            cm.dispose();
            break;
    }
}