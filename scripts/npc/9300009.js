load('nashorn:mozilla_compat.js');
importPackage(java.lang);
importPackage(Packages.client.inventory);
importPackage(Packages.constants);
importPackage(Packages.client);

var status = -1;
var Editing = false; //true=顯示維修;false=正常運作

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	var quest = cm.getQuestRecord(150002); //Q點任務
	var qp = quest.getCustomData(); //Q點
	var nx = cm.getPlayer().getCSPoints(1); //偵測點數
	var sex = (cm.getPlayer().getGender() == 0 ? "他" : "她"); //判斷使用字彙
	var na = cm.getPlayer().getName(); //玩家名稱
	var gm = cm.getPlayer().isGM(); //偵測GM
	if (mode == 1) {
		status++;
	} else {
		if (status >= 2 || status == 0) {
			cm.dispose();
			return;
		}
		status--;
	}
	if (Editing && !gm) {
		cm.sendOk("轉身NPC維修中,造成不便請見諒");
		cm.dispose();
		return;
	}
	if (status == 0) {
		cm.sendYesNo("#b嗨 #d#h # ，#b轉生需要 1萬 楓點　且達到２００等，請問您確定要轉生嗎? 部分職業可能有異常 錯誤不補償 \r\n #dPS: 您目前有 " + nx + " 點的楓點 且 " + cm.getPlayer().getReborns() + "轉"); // 1千Ｑ點 和" + qp + " 點的Q點
	} else if (status == 1) {
		if (cm.getPlayer().getOneTimeLog("RebornNX") == 0 && cm.getPlayer().getReborns() > 4) {
			cm.getPlayer().modifyCSPoints(1, give, true);
			cm.getPlayer().setOneTimeLog("RebornNX");
			cm.sendOk("系統以補發 " + give + " 樂豆點 給您");
			cm.dispose();
			return;
		}

		if (cm.getPlayer().getLevel() < 200) {
			cm.sendOk("#d您的等級不到２００等呀~:P");
			cm.dispose();
			return;
		}
		if (cm.getPlayer().getCSPoints(1) < 10000) {
			cm.sendOk("#d您的楓點不夠 1萬 呀~:P");
			cm.dispose();
			return;
		}
		/*
		if (qp < 1000) {
		cm.sendOk("#d您的Ｑ點不夠 1千 呀~:P");
		cm.dispose();
		return;
		}*/
		if (cm.getPlayer().getReborns() > 500) {
			cm.sendOk("#d您的轉生已經到達最高啦~:P");
			cm.dispose();
			return;
		}
		/*
		if (cm.getPlayer().getJob() == 2412) {
		cm.sendOk("#d幻影配特無法轉生唷~:P");
		cm.dispose();
		return;
		}*/
		cm.sendYesNo("#b轉生之後就無法後悔了，確定要#r轉生#b嗎? \r\n #r PS: 每五轉系統將自動送出2萬楓點補助 \r\n #b根據您目前的轉生數來計算，轉生後#dＡＰ#b將為#r " +
			(((cm.getPlayer().getReborns() + 1) * 20) + 49)); //(((cm.getPlayer().getReborns() + 1) * 30) + 49 + 5));
	} else if (status == 2) {
		if (cm.hasSubwpn()) {
			cm.getPlayer().modifyCSPoints(1, -10000, true); //扣除1萬 樂豆點
			/* if (cm.getPlayer().getJob() == 2412) {
			cm.getPlayer().removeAllstolenSkills();
			} else {*/
			cm.clearSkills(); //清除技能
			//}
			cm.getPlayer().setJob(0); //職業設定為新手
			cm.getPlayer().setSubcategory(0); //設定Sub(影武者/重砲專用，轉職時使用)
			cm.getPlayer().setExp(0); //經驗設定為0
			cm.getPlayer().setLevel(10); //等級設定為10
			cm.getPlayer().setReborns(1); //增加一轉身
			//cm.getPlayer().maxSkillsByJob();//加滿技能
			//    quest.setCustomData((qp - 1000));

			cm.getPlayer().setStr(4); //設定力量4
			cm.getPlayer().setDex(4); //設定敏捷4
			cm.getPlayer().setInt(4); //設定智力4
			cm.getPlayer().setLuk(4); //設定幸運4

			var antiBug = cm.getPlayer().getReborns(); //聲明
			if (antiBug == 0) {
				antiBug = 1; //避免Bug
			}

			cm.getPlayer().setRemainingAp(((antiBug * 20) + 49)); //設定AP為轉生數*20 + 49(1~10等)+ cm.getPlayer().getRemainingAp()

			if (cm.getPlayer().getReborns() % 5 == 0) {
				cm.getPlayer().modifyCSPoints(1, 20000, true); //每五轉發回2萬點數
				if (cm.getPlayer().getOneTimeLog("RebornNX_Give") == 0) {
					cm.getPlayer().setOneTimeLog("RebornNX_Give");
					cm.getPlayer().setOneTimeLog("RebornNX");
				}
			}
			cm.getPlayer().saveToDB(false, false); //存檔
			cm.getPlayer().reloadC(); //重新載入角色
			cm.getPlayer().updateAP();
			antiBug = cm.getPlayer().getReborns(); //更新
			if (antiBug != 0) {
				cm.worldMessage(6, "[伺服器快報] 我們可愛的 " + na + " 已經通過了重重考驗 成功轉生 " + antiBug + " 次了，大家一起恭喜 " + sex + "吧");
			} else {
				cm.worldMessage(6, "[伺服器快報] 我們可愛的 " + na + " 已經通過了重重考驗 完成了" + sex + "的第一次轉生");
			}
			cm.getPlayer().levelUp();
			cm.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, cm.getPlayer().getRemainingAp());
			cm.dispose();
			cm.openNpc(9900007); //開起轉職NPC
		} else {
			cm.sendOk("請把當前的副武器取下才可以轉生");
			cm.dispose();
		}
	}
}
