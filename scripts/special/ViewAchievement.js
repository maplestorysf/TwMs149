/* 仿Ellinia/Khaini 成就查看系統 by Kodan*/
var achi = [
// 成就編號 成就稱號
[1, "獲得第一次點數"],
[2, "達到30級"],
[3, "達到70級"],
[4, "達到120級"],
[5, "達到200級"],
[7, "獲得50名聲"],
[9, "首次穿戴罕見系列裝備"],
[10, "首次穿戴永恆系列裝備"],
[40, "首次穿戴130級以上裝備"],
[41, "首次穿戴140級以上裝備"],
[11, "首次在遊戲說10句伺服器好話"],
[12, "擊敗大姐頭"],
[13, "擊敗拉圖斯"],
[14, "擊敗右海怒斯"],
[43, "擊敗左海怒斯"],
[15, "擊敗殘暴炎魔"],
[16, "擊敗闇黑龍王"],
[17, "擊敗皮卡啾"],
[23, "擊敗混沌殘暴炎魔"],
[24, "擊敗混沌闇黑龍王"],
[38, "擊敗凡雷恩"],
[39, "擊敗西格諾斯"],
[42, "擊敗阿卡伊農"],
[18, "擊敗任意BOSS"],
[22, "完成Boss任務高難度模式"],
[19, "在<選邊站>活動獲勝"],
[20, "在<障礙競走>活動獲勝"],
[21, "在<向上攀升>活動獲勝"],
[25, "在<生存挑戰>活動獲勝"],
[31, "首次持有超過 1,000,000 楓幣"],
[32, "首次持有超過 10,000,000 楓幣"],
[33, "首次持有超過 100,000,000 楓幣"],
[34, "首次持有超過 1,000,000,000 楓幣"],
[35, "成功建立公會"],
[36, "成功建立家族"],
[26, "攻擊傷害首次超過 10,000"],
[27, "攻擊傷害首次超過 50,000"],
[28, "攻擊傷害首次超過 100,000"],
[29, "攻擊傷害首次超過 500,000"],
[30, "攻擊傷害首次達到 999,999"]

];
var status = -1;
var msg = "";
var time = 0;
var alltime = 0;

function start() {
	action(1,0,0);
}

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		status--;
	}
	if (status == 0) {
		msg = "嗨我是成就查詢NPC!\r\n"
		for (var i = 0; i < achi.length; i++) {
			time = getCount(achi[i][0]);
			alltime++;
		}
		msg += "目前已經完成: "+time+"/"+alltime+"\r\n";
		for (var i = 0; i < achi.length; i++) {
			msg += (i+1) + ". " + achi[i][1] + "(#r" + getString(achi[i][0]) + "#k)#l\r\n";
		}
		cm.sendSimple(msg);
		cm.dispose();
	}
}

function getCount(achi) {
	if (cm.getPlayer().achievementFinished(achi) == 1) {
		time++;
	}
	return time;
}

function getString(achi) {
	return (cm.getPlayer().achievementFinished(achi) == 0 ? "#r未完成#k" : "#g已完成#k");
}