var status = -1;
var items;
var itemsp = Array(3000, 3000, 3000, 3000, 1500, 1500, 1500, 1500, 1500, 1500, 1500, 1500, 1000, 1000, 1000, 15000, 15000, 15000, 25000, 25000, 1000 /*100%潛能*/);
var itemsu = Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0); // extra slots, not set.
var itemsq = Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
var itemse = Array(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1);
var extra_text = Array("", "", "", "", "", "", "", "", "", "");
var acash = 1000; //樂豆點
var acashp = 10; //贊助點
var sel = -1;
var itt = -1;
var previous_points;
var chairs;
var chairsp = Array(3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000);

var hairp = 2000;
var mhair;
var fhair;
var hairnew;

var keys = Array(16, 17, 18, 20, 21, 22, 36, 44, 45, 46, 47, 48);
var keynames = Array("Q", "W", "F", "T", "Y", "U", "J", "Z", "X", "C", "V", "B"); //just as reference
var skills;
var skillsnames = Array("終極祈禱", "終極輕功", "終極祝福", "終極瞬移", "神聖之火", "神龍擺尾");
var skillsp = Array(10000, 5000, 5000, 5000, 5000, 1500);
var allskillsp = 5000;

var resetp = 1000;

var pendantp = 5000;
var pendantp_perm = 10000;
var pendantp_perm_forever = 20000;

var namep = 1000;

var buddyp = 100;

var ep = 500;
var slot = Array();
var inv;

function start() {
	action(1, 0, 0);
	fhair = Array(34330, 34340, 34350, 34360, 34370, 34380, 34390, 34400, 34410, 34420, 34430, 34440, 34450, 34460, 34470, 34480, 34490, 34510, 34560, 34590, 34600, 34610, 34620, 34630, 34640, 34650, 34660, 34670, 34680, 34690, 34710, 34720, 34750, 34760, 34880, 34890, 34900, 34910);
	mhair = Array(33410, 33420, 33430, 33440, 33450, 33460, 33470, 33480, 33490, 33500, 33510, 33520, 33530, 33540, 33550, 33580, 33600, 33610, 33620, 33630, 33640, 33650, 33660, 33670, 33680, 33690, 33710, 33720, 33750, 33970, 33980, 33990, 36000);
	chairs = Array(3010045, 3010014, 3010068, 3010009, 3010022, 3010023, 3010041, 3010142, 3010069, 3010071, 3010107, 3010119, 3010151, 3010155, 3010139, 3010077, 3010173, 3010174, 3010175, 3010123, 3010168, 3010095, 3010099, 3010036, 3010112, 3010096, 3010131, 3010172, 3012010, 3012011, 3010180, 3010181, 3010188, 3010824, 3010825, 3010826, 3010827, 3010828, 3010829, 3010830, 3010831, 3010832);
	items = Array(
			2046052,
			2046053,
			2046137,
			2046134,
			2046385,
			2046386,
			2046387,
			2046388,
			2046503,
			2046504,
			2046505,
			2046506,
			2340000,
			2530002,
			2531000,
			1202089,
			1202090,
			1202091,
			1122017,
			1112127,
			2049406); //100%潛能 1000


	skills = Array(9001002, 9001001, 9001003, 9001007, 9001008, 80001130);
	inv = cm.getInventory(1);
	previous_points = cm.getPlayer().getPoints();
}

function action(mode, type, selection) {
	if (mode != 1) {
		cm.dispose();
		return;
	}
	status++;
	if (status == 0) {
		cm.sendSimple("嗨，#r#h ##k你好！\r\n我是#r幹員 W#k。我的工作是負責提供#b贊助服務#k。\r\n你想要什麼服務呢？\r\n#b#L0#什麼是贊助點數？#l\r\n#b#L1#兌換物品#l \r\n#L9#兌換技能#l \r\n#L18#擴充墜飾欄位#l \r\n#b#L4#兌換樂豆點#l\r\n#b#L5278#兌換傳說方塊#l\r\n#b#L6#兌換更多的物品請點我#l\r\n#L5##r#我有多少贊助點數？#l");
	} else if (status == 1) {
		sel = selection;
		if (selection == 0) {
			cm.sendNext("贊助點數請透過#b贊助我們#k取得。它可被用來兌換相當豐富的獎勵，例如卷軸、椅子或現金道具。");
			status = -1;
		} else if (selection == 1) {
			var selStr = "請選擇兌換項目：#b\r\n\r\n";
			for (var i = 0; i < items.length; i++) {
				selStr += "#L" + i + "##i" + items[i] + ":##t" + items[i] + ":#" /*+ extra_text[i]*/ + (itemsu[i] > 0 ? "(with " + itemsu[i] + " extra slots)" : "") + " x " + itemsq[i] + " \r\n  (#e" + itemsp[i] + "#n 贊助點數)#n" + (itemse[i] > 0 ? (" ...lasts #r#e" + itemse[i] + "#n#bdays") : "") + "#l\r\n";
			}
			cm.sendSimple(selStr + "#k");
		} else if (selection == 3) {
			var bbb = false;
			var selStr = "Alright. I can #eonly give a slot to equipments that have 0 upgrade slots and have been hammered twice. You can only give a slot up to 10 times to a certain item. It will cost #b" + ep + "#k points, and #b" + (ep * 2) + "#k points for items above 5 slots upgraded.#n Select the equipment you have below(equipped items are not included):\r\n\r\n#b";
			for (var i = 0; i <= inv.getSlotLimit(); i++) {
				slot.push(i);
				var it = inv.getItem(i);
				if (it == null || it.getUpgradeSlots() > 0 || it.getViciousHammer() < 2 || it.getViciousHammer() > 6) {
					continue;
				}
				var itemid = it.getItemId();
				//bwg - 7, with hammer is 9.
				//therefore, we should make the max slots (natural+7)
				if (cm.getNaturalStats(itemid, "tuc") <= 0 || itemid == 1122080 || cm.isCash(itemid)) {
					continue;
				}
				bbb = true;
				selStr += "#L" + i + "##v" + itemid + "##t" + itemid + "##l\r\n";
			}
			if (!bbb) {
				cm.sendOk("我需要#b0可使用卷軸次數#k的物品，並使用過兩次黃金鐵鎚的物品。");
				cm.dispose();
				return;
			}
			cm.sendSimple(selStr + "#k");
		} else if (selection == 4) {
			cm.sendYesNo("想要換成樂豆點? #r#e" + acashp + "贊助點換  " + acash + " 樂豆點.#n#k \r\n你接受這個價格嗎?");
		} else if (selection == 5) {
			cm.sendOk("你目前共有 #e" + cm.getPlayer().getPoints() + "#n 贊助點數。");
			cm.dispose();
		} else if (selection == 6) {
			cm.dispose();
			cm.openNpc(1300009);
			/* var selStr = "請選擇你喜歡的椅子：#b\r\n\r\n";
			for (var i = 0; i < chairs.length; i++) {
			selStr += "#L" + i + "##v" + chairs[i] + "##t" + chairs[i] + "# (#e" + chairsp[i] + "#n 贊助點數)#n#l\r\n";
			}
			cm.sendSimple(selStr + "#k");*/
		} else if (selection == 7) {
			hairnew = Array();
			if (cm.getPlayerStat("GENDER") == 0) {
				for (var i = 0; i < mhair.length; i++) {
					if (mhair[i] == 30010 || mhair[i] == 30070 || mhair[i] == 30080 || mhair[i] == 30090 || mhair[i] == 33140 || mhair[i] == 33240 || mhair[i] == 33180) {
						hairnew.push(mhair[i]);
					} else {
						hairnew.push(mhair[i] + parseInt(cm.getPlayerStat("HAIR") % 10));
					}
				}
			} else {
				for (var i = 0; i < fhair.length; i++) {
					if (fhair[i] == 34160) {
						hairnew.push(fhair[i]);
					} else {
						hairnew.push(fhair[i] + parseInt(cm.getPlayerStat("HAIR") % 10));
					}
				}
			}
			cm.sendStyle("選擇一個您喜歡的髮型", hairnew);
		} else if (selection == 8) {
			cm.sendYesNo("重置技能點數總共要消耗#b1,000贊助點數#k，而且一旦重置技能點數，就無法復原，你真的要#b重置技能點數#k嗎？");
		} else if (selection == 9) {
			var selStr = "請選擇你想要購買的技能：#n#b\r\n\r\n";
			for (var i = 0; i < skills.length; i++) {
				selStr += "#L" + i + "##s" + skills[i] + "#" + skillsnames[i] + " (#e" + skillsp[i] + "#n 贊助點數)#n#l\r\n";
			}
			//selStr += "#L" + skills.length + "##rALL skills above#b for #e" + allskillsp + "#n points#l\r\n";
			cm.sendSimple(selStr + "#k");
		} else if (selection == 10) {
			cm.sendGetText("Please enter the name you wish to change to.");
		} else if (selection == 11) {
			if (cm.getBuddyCapacity() < 120 && cm.getPlayer().getPoints() >= buddyp) {
				cm.getPlayer().setPoints(cm.getPlayer().getPoints() - buddyp);
				cm.logDonator(" has bought buddy capacity 120 costing " + buddyp + ".", previous_points);
				cm.updateBuddyCapacity(120);
			} else {
				cm.sendOk("You either have 120 capacity or you don't have enough points.");
			}
			cm.dispose();
		} else if (selection == 12) {
			if (!cm.getPlayer().haveItem(5220013)) {
				cm.sendOk("You need at least 1 M Coin.");
				cm.dispose();
			} else {
				cm.sendGetNumber("How many M Coins would you like to redeem? (1 M Coin = 100 points) (Current M Coins: " + cm.getPlayer().itemQuantity(5220013) + ") (Current Points: " + cm.getPlayer().getPoints() + ")", cm.getPlayer().itemQuantity(5220013), 1, cm.getPlayer().itemQuantity(5220013));
			}
		} else if (selection == 13) {
			if (cm.getPlayer().getPoints() < 100) {
				cm.sendOk("You need at least 100 points for an M Coin.");
				cm.dispose();
			} else {
				cm.sendGetNumber("How many M Coins would you like? (1 M Coin = 100 points) (Current Points: " + cm.getPlayer().getPoints() + ") (Current M Coins: " + cm.getPlayer().itemQuantity(5220013) + ")", cm.getPlayer().getPoints() / 100, 1, cm.getPlayer().getPoints() / 100);
			}
		} else if (selection == 16) {
			if (!cm.getPlayer().haveItem(3993003)) {
				cm.sendOk("You need at least 1 Red Luck Sack.");
				cm.dispose();
			} else {
				cm.sendGetNumber("How many Red Luck Sacks would you like to redeem? (1 Red Luck Sack = 1000 points) (Current: " + cm.getPlayer().itemQuantity(3993003) + ") (Current Points: " + cm.getPlayer().getPoints() + ")", cm.getPlayer().itemQuantity(3993003), 1, cm.getPlayer().itemQuantity(3993003));
			}
		} else if (selection == 17) {
			if (cm.getPlayer().getPoints() < 1000) {
				cm.sendOk("You need at least 1000 points for a Red Luck Sack.");
				cm.dispose();
			} else {
				cm.sendGetNumber("How many Red Luck Sacks would you like? (1 Red Luck Sack = 1000 points) (Current Points: " + cm.getPlayer().getPoints() + ") (Current Red Luck Sacks: " + cm.getPlayer().itemQuantity(3993003) + ")", cm.getPlayer().getPoints() / 1000, 1, cm.getPlayer().getPoints() / 1000);
			}
		} else if (selection == 5278) {
			cm.dispose();
			cm.openNpc(9000039, 1);

		} else if (selection == 5279) {
			if (cm.getPlayer().getPoints() > 100) {
				cm.getPlayer().setPoints(cm.getPlayer().getPoints() - 100);
				cm.gainItem(5062002, 10);
				cm.sendOk("用100點買了10顆傳說方塊.");
				cm.dispose();
			} else {
				cm.sendOk("很抱歉你贊助點不夠!.");
			}
		} else if (selection == 5280) {
			if (cm.getPlayer().getPoints() > 1000) {
				cm.getPlayer().setPoints(cm.getPlayer().getPoints() - 1000);
				cm.gainItem(5062002, 100);
				cm.sendOk("用1000點買了100顆傳說方塊.");
				cm.dispose();
			} else {
				cm.sendOk("很抱歉你贊助點不夠!.");
			}
		} else if (selection == 5281) {
			if (cm.getPlayer().getPoints() > 10000) {
				cm.getPlayer().setPoints(cm.getPlayer().getPoints() - 10000);
				cm.gainItem(5062002, 1000);
				cm.sendOk("用10000點買了1000顆傳說方塊.");
				cm.dispose();
			} else {
				cm.sendOk("很抱歉你贊助點不夠!.");
			}
		} else if (selection == 18) {
			cm.sendSimple("請選擇購買天數：\r\n#b#L0#30 天 - " + pendantp + " 贊助點數#l\r\n#L1#90 天 - " + pendantp_perm + " 贊助點數#l\r\n#L2#永久 - " + pendantp_perm_forever + " 贊助點數");
		}
	} else if (status == 2) {
		if (sel == 1) {
			var it = items[selection];
			var ip = itemsp[selection];
			var iu = itemsu[selection];
			var iq = itemsq[selection];
			var ie = itemse[selection];
			if (cm.getPlayer().getPoints() < ip) {
				cm.sendOk("贊助點數不夠。\r\n你目前共有 #b" + cm.getPlayer().getPoints() + " 贊助點數#k，我總共要 #b" + ip + " 贊助點數#k。");
			} else if (!cm.canHold(it, iq)) {
				cm.sendOk("請檢查道具欄位空間。");
			} else {
				if (iu > 0) {
					cm.gainItem(it, iq, false, ie, iu, "Donor");
				} else {
					cm.gainItemPeriod(it, iq, ie, "Donor");
				}
				cm.getPlayer().setPoints(cm.getPlayer().getPoints() - ip);
				cm.sendOk("謝謝光臨。");
				cm.logDonator(" has bought item [" + it + "] x " + iq + " costing " + ip + ". [Expiry: " + ie + "] [Extra Slot: " + iu + "] ", previous_points);
			}
			cm.dispose();
		} else if (sel == 3) {
			var statsSel = inv.getItem(slot[selection]);
			if (statsSel == null || statsSel.getUpgradeSlots() > 0 || statsSel.getViciousHammer() < 2) {
				cm.dispose();
				return;
			}
			var itemid = statsSel.getItemId();
			//bwg - 7, with hammer is 9.
			//therefore, we should make the max slots(natural+7)
			if (statsSel.getViciousHammer() > 6 || cm.getNaturalStats(itemid, "tuc") <= 0 || itemid == 1122080) {
				cm.dispose();
				return;
			}
			if (cm.isCash(itemid)) {
				cm.dispose();
				return;
			}
			var pointsToUse = ep;
			if (statsSel.getViciousHammer() >= 4) { //2 slots with normal, 3 slots afterwards with doubled price
				pointsToUse = ep * 2;
			}
			if (cm.getPlayer().getPoints() < pointsToUse) {
				cm.sendOk("贊助點數不夠。\r\n你目前共有 #b" + cm.getPlayer().getPoints() + " 贊助點數#k，我總共要 #b" + pointsToUse + " 贊助點數#k。");
			} else {
				cm.replaceItem(selection, 1, statsSel, 1);
				cm.getPlayer().setPoints(cm.getPlayer().getPoints() - pointsToUse);
				cm.sendOk("謝謝光臨。");
				cm.logDonator(" has enhanced +1 slot on item [" + statsSel.getItemId() + "] costing " + pointsToUse + ". [Used slots: " + statsSel.getViciousHammer() + "]", previous_points);
			}
			cm.dispose();
		} else if (sel == 4) {
			if (cm.getPlayer().getPoints() < acashp) {
				cm.sendOk("贊助點數不夠。\r\n你目前共有 #b" + cm.getPlayer().getPoints() + " 贊助點數#k，我總共要 #b" + acashp + " 贊助點數#k。");
			} else if (cm.getPlayer().getCSPoints(0) > (java.lang.Integer.MAX_VALUE - acash)) {
				cm.sendOk("You have too much Cash.");
			} else {
				cm.getPlayer().setPoints(cm.getPlayer().getPoints() - acashp);
				cm.getPlayer().modifyCSPoints(0, acash, true);
				cm.sendOk("謝謝你的那些贊助點，我給了你樂豆點。再來呦〜");
				cm.logDonator(" has bought Cash [" + acash + "] costing " + acashp + ".", previous_points);
			}
			cm.dispose();
		} else if (sel == 6) {
			var it = chairs[selection];
			var cp = chairsp[selection];
			if (cm.getPlayer().getPoints() < cp) {
				cm.sendOk("贊助點數不夠。\r\n你目前共有 #b" + cm.getPlayer().getPoints() + " 贊助點數#k，我總共要 #b" + cp + " 贊助點數#k。");
			} else if (!cm.canHold(it)) {
				cm.sendOk("請檢查道具欄位空間。");
			} else {
				cm.gainItem(it, 1);
				cm.getPlayer().setPoints(cm.getPlayer().getPoints() - cp);
				cm.sendOk("謝謝光臨。");
				cm.logDonator(" has bought chair [" + it + "] costing " + cp + ".", previous_points);
			}
			cm.dispose();
		} else if (sel == 7) {
			if (cm.getPlayer().getPoints() < hairp) {
				cm.sendOk("贊助點數不夠。\r\n你只有 #b" + cm.getPlayer().getPoints() + " 贊助點數#k。");
			} else if (cm.getPlayer().getOneTimeLog("贊助皇家美髮") >= 1) {
				cm.setHair(hairnew[selection]);
				cm.sendOk("謝謝光臨。");
				cm.logDonator(" has bought hair [" + hairnew[selection] + "] costing " + hairp + ".", previous_points);
			} else {
				cm.setHair(hairnew[selection]);
				cm.getPlayer().setOneTimeLog("贊助皇家美髮");
				cm.getPlayer().setPoints(cm.getPlayer().getPoints() - hairp);
				cm.sendOk("謝謝光臨。");
				cm.logDonator(" has bought hair [" + hairnew[selection] + "] costing " + hairp + ".", previous_points);
			}
			cm.dispose();
		} else if (sel == 8) {
			if (cm.getPlayer().getPoints() < resetp) {
				cm.sendOk("贊助點數不夠。\r\n你只有 #b" + cm.getPlayer().getPoints() + " 贊助點數#k。");
			} else {
				cm.getPlayer().resetStatsByJob(false);
				cm.getPlayer().setPoints(cm.getPlayer().getPoints() - resetp);
				cm.sendOk("謝謝光臨。");
				cm.logDonator(" has bought full AP reset costing " + resetp + ".", previous_points);
			}
			cm.dispose();
		} else if (sel == 9) {
			if (selection == skills.length) {
				if (cm.getPlayer().getPoints() < allskillsp) {
					cm.sendOk("贊助點數不夠。\r\n你只有 #b" + cm.getPlayer().getPoints() + " 贊助點數#k。");
				} else {
					for (var i = 0; i < skills.length; i++) {
						cm.teachSkill(skills[i], 1, 0);
					}
					cm.getPlayer().setPoints(cm.getPlayer().getPoints() - allskillsp);
					cm.sendOk("謝謝光臨。");
					cm.logDonator(" has bought all skills costing " + allskillsp + ".", previous_points);
				}
				cm.dispose();
				return;
			}
			itt = selection;
			var selStr = "請選擇熱鍵位置：#b\r\n\r\n";
			for (var i = 0; i < keys.length; i++) {
				selStr += "#L" + i + "#" + keynames[i] + "#l\r\n";
			}
			cm.sendSimple(selStr + "#k");
		} else if (sel == 10) {
			if (cm.getPlayer().getPoints() >= namep && cm.isEligibleName(cm.getText())) {
				cm.getPlayer().setPoints(cm.getPlayer().getPoints() - namep);
				cm.logDonator(" has bought name change from " + cm.getPlayer().getName() + " to " + cm.getText() + " costing " + namep + ".", previous_points);
				cm.getClient().getChannelServer().removePlayer(cm.getPlayer().getId(), cm.getPlayer().getName());
				cm.getPlayer().setName(cm.getText());
				cm.getClient().getSession().close();
			} else {
				cm.sendOk("You either don't have enough points or " + cm.getText() + " is not an eligible name");
			}
			cm.dispose();
		} else if (sel == 12) {
			if (selection >= 1 && selection <= cm.getPlayer().itemQuantity(5220013)) {
				if (cm.getPlayer().getPoints() > (2147483647 - (selection * 100))) {
					cm.sendOk("You have too many points.");
				} else {
					cm.gainItem(5220013, -selection);
					cm.getPlayer().setPoints(cm.getPlayer().getPoints() + (selection * 100));
					cm.sendOk("You have lost " + selection + " M Coins and gained " + (selection * 100) + " points. Current Points: " + cm.getPlayer().getPoints());
					cm.logDonator(" has redeemed " + selection + " M Coin(s) gaining " + (selection * 100) + ".", previous_points);
				}
			}
			cm.dispose();
		} else if (sel == 13) {
			if (selection >= 1 && selection <= 100) {
				if (selection > (cm.getPlayer().getPoints() / 100)) {
					cm.sendOk("You can only get max " + (cm.getPlayer().getPoints() / 100) + " M Coins. 1 M Coin = 100 points.");
				} else if (!cm.canHold(5220013, selection)) {
					cm.sendOk("Please make space in CASH tab.");
				} else {
					cm.gainItem(5220013, selection);
					cm.getPlayer().setPoints(cm.getPlayer().getPoints() - (selection * 100));
					cm.sendOk("You have gained " + selection + " M Coins and lost " + (selection * 100) + " points. Current Points: " + cm.getPlayer().getPoints());
					cm.logDonator(" has gained " + selection + " M Coin(s) costing " + (selection * 100) + ".", previous_points);
				}
			}
			cm.dispose();
		} else if (sel == 16) {
			if (selection >= 1 && selection <= cm.getPlayer().itemQuantity(3993003)) {
				if (cm.getPlayer().getPoints() > (2147483647 - (selection * 1000))) {
					cm.sendOk("You have too many points.");
				} else {
					cm.gainItem(3993003, -selection);
					cm.getPlayer().setPoints(cm.getPlayer().getPoints() + (selection * 1000));
					cm.sendOk("You have lost " + selection + " and gained " + (selection * 1000) + " points. Current Points: " + cm.getPlayer().getPoints());
					cm.logDonator(" has redeemed " + selection + " Red Luck Sack(s) gaining " + (selection * 1000) + ".", previous_points);
				}
			}
			cm.dispose();
		} else if (sel == 17) {
			if (selection >= 1) {
				if (selection > (cm.getPlayer().getPoints() / 1000)) {
					cm.sendOk("You can only get max " + (cm.getPlayer().getPoints() / 1000) + ". 1 Item = 1000 points.");
				} else if (!cm.canHold(3993003, selection)) {
					cm.sendOk("Please make space in SETUP tab.");
				} else {
					cm.gainItem(3993003, selection);
					cm.getPlayer().setPoints(cm.getPlayer().getPoints() - (selection * 1000));
					cm.sendOk("You have gained " + selection + " and lost " + (selection * 1000) + " points. Current Points: " + cm.getPlayer().getPoints());
					cm.logDonator(" has gained " + selection + " Red Luck Sack(s) costing " + (selection * 1000) + ".", previous_points);
				}
			}
			cm.dispose();
		} else if (sel == 18) {
			if (selection == 0) {
				if (cm.getPlayer().getPoints() < pendantp) {
					cm.sendOk("You do not have enough points.");
				} else {
					//var marr = cm.getCData("pendant");
					var marr = cm.getQuestNoRecord(122700);
					if (marr != null && parseInt(marr) > cm.getCurrentTime()) {
						cm.sendOk("無法重複擴充墜飾欄位。");
					} else {
						//cm.setCData("pendant", "" + (cm.getCurrentTime() + (30 * 24 * 60 * 60 * 1000)));
						cm.getQuestRecord(122700).setCustomData("" + (cm.getCurrentTime() + (30 * 24 * 60 * 60 * 1000)));
						cm.forceStartQuest(7830, "1");
						cm.getPlayer().setPoints(cm.getPlayer().getPoints() - pendantp);
						cm.sendOk("恭喜你獲得#b30天#k墜飾欄位擴充。");
						cm.sendPendant(true);
						cm.getPlayer().fakeRelog();
						cm.logDonator(" has gained Additional Pendant Slot (30 Day) costing " + (pendantp) + ".", previous_points);
					}
				}
			} else if (selection == 1) {
				if (cm.getPlayer().getPoints() < pendantp_perm) {
					cm.sendOk("贊助點數不夠。");
				} else {
					//var marr = cm.getCData("pendant");
					var marr = cm.getQuestNoRecord(122700);
					if (marr != null && parseInt(marr) > cm.getCurrentTime()) {
						cm.sendOk("無法重複擴充墜飾欄位。");
					} else {
						//cm.setCData("pendant", "" + (cm.getCurrentTime() + (90 * 24 * 60 * 60 * 1000)));
						cm.getQuestRecord(122700).setCustomData("" + (cm.getCurrentTime() + (90 * 24 * 60 * 60 * 1000)));
						cm.forceStartQuest(7830, "1");
						cm.getPlayer().setPoints(cm.getPlayer().getPoints() - pendantp_perm);
						cm.sendOk("恭喜你獲得#b90天#k墜飾欄位擴充。");
						cm.sendPendant(true);
						cm.getPlayer().fakeRelog();
						cm.logDonator(" has gained Additional Pendant Slot (90 Day) costing " + (pendantp_perm) + ".", previous_points);
					}
				}
			} else {
				if (cm.getPlayer().getPoints() < pendantp_perm_forever) {
					cm.sendOk("贊助點數不夠。");
				} else {
					//var marr = cm.getCData("pendant");
					var marr = cm.getQuestNoRecord(122700);
					if (marr != null && parseInt(marr) > cm.getCurrentTime()) {
						cm.sendOk("無法重複擴充墜飾欄位。");
					} else {
						//cm.setCData("pendant", "" + (cm.getCurrentTime() + (90 * 24 * 60 * 60 * 1000)));
						cm.getQuestRecord(122700).setCustomData("" + (cm.getCurrentTime() + (99999 * 24 * 60 * 60 * 1000)));
						cm.forceStartQuest(7830, "1");
						cm.getPlayer().setPoints(cm.getPlayer().getPoints() - pendantp_perm_forever);
						cm.sendOk("恭喜你獲得#b永久#k墜飾欄位擴充。");
						cm.sendPendant(true);
						cm.getPlayer().fakeRelog();
						cm.logDonator(" has gained Additional Pendant Slot (90 Day) costing " + (pendantp_perm_forever) + ".", previous_points);
					}
				}
			}
			cm.dispose();
		}
	} else if (status == 3) {
		if (sel == 9) {

			var skip = false;
			var hasSkill = true;

			// 技能等級為0以及從來沒學過
			if (cm.getPlayer().getSkillLevel(skills[itt]) <= 0 && cm.getPlayer().getOneTimeLog("donateSkill" + skills[itt]) <= 0) {
				hasSkill = false;
			}

			// 沒有技能且點數不足(學習技能後不判斷)
			if (cm.getPlayer().getPoints() < skillsp[itt] && !hasSkill) {
				cm.sendOk("贊助點數不夠. 你身上只有 " + cm.getPlayer().getPoints() + " 可是我要 " + skillsp[itt] + ".");
				cm.dispose();
				return;
			}

			// 沒有學技能才扣點和給技能
			if (!hasSkill) {
				cm.getPlayer().setPoints(cm.getPlayer().getPoints() - skillsp[itt]);
			}
			cm.teachSkill(skills[itt], 1, 0);

			cm.getPlayer().setOneTimeLog("donateSkill" + skills[itt]);
			cm.putKey(keys[selection], 1, skills[itt]);
			cm.sendOk("感謝購買技能，我已經給您了贊助點數技能 請查看~");
			cm.logDonator(" has bought skill [" + skills[itt] + "] costing " + skillsp[itt] + " on key " + keynames[selection] + " (" + keys[selection] + "). [HasSkill: " + hasSkill + "] ", previous_points);
		}
		cm.dispose();

	}
}

function openNpc(npcid) {
	openNpc(npcid, null);
}

function openNpc(npcid, script) {
	var mapid = cm.getMapId();
	cm.dispose();
	if (cm.getPlayerStat("LVL") < 10) {
		cm.sendOk("你的等級不能小於10等.");
	} else if (
		cm.hasSquadByMap() ||
		cm.hasEventInstance() ||
		cm.hasEMByMap() ||
		mapid >= 990000000 ||
		(mapid >= 680000210 && mapid <= 680000502) ||
		(mapid / 1000 === 980000 && mapid !== 980000000) ||
		mapid / 100 === 1030008 ||
		mapid / 100 === 922010 ||
		mapid / 10 === 13003000) {
		cm.sendOk("你不能在這裡使用這個功能.");
	} else {
		if (script == null) {
			cm.openNpc(npcid);
		} else {
			cm.openNpc(npcid, script);
		}
	}
}
