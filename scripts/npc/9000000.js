var quantities = Array(10, 8, 6, 5, 4, 3, 2, 1, 1, 1);
var prize1 = Array(1442047, 2000000, 2000001, 2000002, 2000003, 2000004, 2000005, 2430036, 2430037, 2430038, 2430039, 2430040); //1 day
var prize2 = Array(1442047, 4080100, 4080001, 4080002, 4080003, 4080004, 4080005, 4080006, 4080007, 4080008, 4080009, 4080010, 4080011);
var prize3 = Array(1442047, 1442048, 2022070);
var prize4 = Array(1442048, 2430082, 2430072); //7 day
var prize5 = Array(1442048, 2430091, 2430092, 2430093, 2430101, 2430102); //10 day
var prize6 = Array(1442048, 1442050, 2430073, 2430074, 2430075, 2430076, 2430077); //15 day
var prize7 = Array(1442050, 3010183, 3010182, 3010053, 2430080); //20 day
var prize8 = Array(1442050, 3010178, 3010177, 3010075, 1442049, 2430053, 2430054, 2430055, 2430056, 2430103, 2430136); //30 day
var prize9 = Array(1442049, 3010123, 3010175, 3010170, 3010172, 3010173, 2430201, 2430228, 2430229); //60 day
var prize10 = Array(1442049, 3010172, 3010171, 3010169, 3010168, 3010161, 2430117, 2430118, 2430119, 2430120, 2430137); //1 year
var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (status >= 0 && mode == 0) {
			cm.dispose();
			return;
		}	
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {	
			cm.sendNext("哈囉，我是 #b#p" + cm.getNpc() + "##k，和弟弟一起來參加活動的，卻和弟弟走散了。如果有空的話你能不能和我一起去參加活動啊？");
		} else if (status == 1) {	
			cm.sendSimple("那個...如果不忙的話能不能和我一起去？弟弟應該是會跟別人一起來。\r\n#L0##e1.#n#b 什麼活動？#k#l\r\n#L1##e2.#n#b 說明一下活動的遊戲。#k#l\r\n#L2##e3.#n#b 是嗎！我們一起去吧。#k#l\r\n#L3##e4.#n#b 使用連勝證明書兌換獎勵#k#l");
		} else if (status == 2) {
			if (selection == 0) {
				cm.sendNext("本次活動是為了慶祝放假而舉行的活動。整天只是待在家裡是不是對健康不好呢？那就來參加愉快的活動吧~ 詳細活動日期請到網站上確認喔~在活動中獲勝會得到各種物品和楓幣呢。所有活動參加者都會得到紀念獎盃,獲勝者還會得到特殊的獎品呢,加油啊！");
				cm.dispose();
			} else if (selection == 1) {
				cm.sendSimple("正在準備舉行各種活動呢。事先瞭解清楚遊戲方法的話會很有好處的哦？那就挑選一下想要聽聽說明的遊戲吧~! #b\r\n#L0# 向上攀升#l\r\n#L1# 障礙競走#l\r\n#L2# 滾雪球#l\r\n#L3# 農夫的樂趣#l\r\n#L4# 選邊站#l\r\n#L5# 黃金傳說#l#k");
			} else if (selection == 2) {
				var marr = cm.getQuestRecord(160200);
				if (marr.getCustomData() == null) {
					marr.setCustomData("0");
				}
				var dat = parseInt(marr.getCustomData());
				if (dat + 3600000 >= cm.getCurrentTime()) {
					cm.sendNext("You've entered the event already in the past hour.");
				} else if (!cm.canHold()) {
					cm.sendNext("請確認道具欄位空間。");
				} else if (cm.getChannelServer().getEvent() > -1 && !cm.haveItem(4031019)) {
					cm.saveReturnLocation("EVENT");
					cm.getPlayer().setChalkboard(null);
					marr.setCustomData("" + cm.getCurrentTime());
					cm.warp(cm.getChannelServer().getEvent(), cm.getChannelServer().getEvent() == 109080000 || cm.getChannelServer().getEvent() == 109080010 ? 0 : "join00");
				} else {
					cm.sendNext("活動尚未開始,或者目前持有「#t4031019#」,或者名額已滿的情況,就不能參加活動。下次再一起玩吧~");
				}
				cm.dispose();
			} else if (selection == 3) {
				var selStr = "請選擇兌換項目：";
				for (var i = 0; i < quantities.length; i++) {
					selStr += "\r\n#b#L" + i + "##t" + (4031332 + i) + "# 兌換(" + quantities[i] + ")#l";
				}
				cm.sendSimple(selStr);
				status = 9;
			}
		} else if (status == 3) {
			if (selection == 0) {
				cm.sendNext("#b[向上攀升]是一種類似梯子遊戲#k。方法是,爬上梯子之後,在許多的入口之中,選擇一個,接著往下一階段移動。 \r\n\r\n總共有三個關卡。 #r遊戲時間為6分鐘#k。 \r\n[向上攀升] 遊戲裡頭, #b無法使用跳躍術、傳送術、加速術,以及能夠提高速度的藥水和道具等等#k。 其中有的入口,會使玩家移動到別處的陷阱入口,所以請多多留意。");
				cm.dispose();
			} else if (selection == 1) {
				cm.sendNext("#b[障礙競走] 遊戲是屬於障礙賽跑遊戲#k的一種。類似我們「楓之谷」裡的忍耐之林或歷恩森林。只要在時間限制之內,超越各式各樣的難關,抵達終點,就算獲勝。 \r\n\r\n遊戲一共分為四階段, #b時間限制為15分鐘#k。 [障礙競走] 遊戲裡頭,無法使用傳送術,加速術。");
				cm.dispose();
			} else if (selection == 2) {
				cm.sendNext("#b[滾雪球]#k分為「楓葉隊」和「傳說隊」兩隊 #b,滾出較多雪球的一方,贏得遊戲#k。要是在規定的時間內,無法分出勝負的話,就以滾較多雪球的一方為勝。 \r\n\r\n接近雪球後攻擊(Ctrl鍵),雪球就會慢慢開始滾動。遠距攻擊以及所有的技能攻擊都無效,只有 #r接近攻擊才有效果#k。 \r\n\r\n當玩家接觸到雪球的時候,就會自動退回到出發點的地方。如果攻擊在出發點的雪人,就能阻止對手雪球的前進。要攻擊雪球或是攻擊雪人,就要由小組間作戰策略而定了。");
				cm.dispose();
			} else if (selection == 3) {
				cm.sendNext("#b[農夫的樂趣]#k分為「楓葉隊」與「傳說隊」, #b在遊戲時間之內能獲得最多椰子的,就是優勝隊伍#k。遊戲的 #r限制時間是5分鐘#k。當第一場比賽為 [平手]時,將會加賽2分鐘。如果得分仍是一樣,就為不分勝負,以平手結束遊戲。 \r\n\r\n遠距攻擊以及技能攻擊都無法使用, #r只能使用接近攻擊#k。當沒有接近攻擊的武器時,可以透過在活動地圖內的NPC買到武器。無論角色等級、武器,以及附屬物,都能用來打擊。\r\n\r\n地圖中到處有障礙物以及陷阱。當玩家死掉的時候,就會被判出局。只有打出椰子掉落前最後一擊的隊伍,才會獲得分數。而只有讓椰子掉落才會得分,沒有掉落,或是裂開,都沒有分數。在地圖的下側有隱藏的入口,請加以運用。");
				cm.dispose();
			} else if (selection == 4) {
				cm.sendNext("#b[選邊站]#k是對提出的問題以OX猜出正確答案的遊戲。參加遊戲之後,按下鍵盤的M鍵打開小地圖確認O與X的位置。將全部問題都答對就能成為冠軍哦。 \r\n\r\n當出現問題之後通過梯子下到認為是正確答案的位置。要在限定時間之內選擇正確答案,若是不選擇正確答案或一直停在梯子上,在計算分數時會被自動脫落的。一定要等到畫面上[正確答案]消失之後再移動哦。");
				cm.dispose();
			} else if (selection == 5) {
				cm.sendNext("#b[黃金傳說]#k遊戲是將藏在各個原野的#b藏寶圖#k在#r限定時間10分鐘#k之內尋找出來的遊戲。到處都有隱藏的寶箱。打破這些寶箱之後會出現各種物品,而我們的目標就是其中的藏寶圖。 \r\n寶箱只能以#b普通攻擊#k才能打破,在出現的物品中找出藏寶圖拿給負責交換的NPC的話可以得到惡魔文件。尋寶地圖之內也有負責進行交換的NPC,也可拜託維多利亞港的#b[貝勤]#k。\r\n\r\n這個遊戲中存在隱蔽的門戶,隱蔽的瞬間移動場所。若想利用這類場所進行移動時,在特定場所按下#b方向鍵 ↑#k即可。遊戲中還有隱蔽的階梯和繩索,所以在看不到的地方也應該多跳躍幾次看看哦。當然,也有一些傳送到隱藏地點的寶箱,有些寶箱只有在這樣移動到的地方才能看到哦。\r\n\r\n在尋寶遊戲中,特定技能是#r不能#k使用的,一定要用普通攻擊砸碎寶箱。");
				cm.dispose();
			}
		} else if (status == 10) {
			if (selection < 0 || selection > quantities.length) {
				return;
			}
			var ite = 4031332 + selection;
			var quan = quantities[selection];
			var pri;
			switch(selection) {
				case 0:
					pri = prize1;
					break;
				case 1:
					pri = prize2;
					break;
				case 2:
					pri = prize3;
					break;
				case 3:
					pri = prize4;
					break;
				case 4:
					pri = prize5;
					break;
				case 5:
					pri = prize6;
					break;
				case 6:
					pri = prize7;
					break;
				case 7:
					pri = prize8;
					break;
				case 8:
					pri = prize9;
					break;
				case 9:
					pri = prize10;
					break;
				default:
					cm.dispose();
					return;
			}
			var rand = java.lang.Math.floor(java.lang.Math.random() * pri.length);
			if (!cm.haveItem(ite, quan)) {
				cm.sendOk("需要 #b" + quan + " #t" + ite + "##k 來兌換物品。");
			} else if (cm.getInventory(1).getNextFreeSlot() <= -1 || cm.getInventory(2).getNextFreeSlot() <= -1 || cm.getInventory(3).getNextFreeSlot() <= -1 || cm.getInventory(4).getNextFreeSlot() <= -1) {
				cm.sendOk("請確認道具欄位空間。");
			} else {
				cm.gainItem(pri[rand], 1);
				cm.gainItem(ite, -quan);
				cm.gainMeso(100000 * selection); //temporary prize lolol
			}
			cm.dispose();
		}
	}
}