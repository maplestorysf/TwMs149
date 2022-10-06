load('nashorn:mozilla_compat.js');
importPackage(java.awt);

var status;
var curMap;
var playerStatus;
var chatState;
var questions = Array("請收集10張票券。",
		"請收集35張票券。",
		"請收集20張票券。",
		"請收集25張票券。",
		"請收集25張票券。",
		"請收集30張票券。");
var qanswers = Array(10, 35, 20, 25, 25, 30);
var party;
var preamble;
var stage2combos = Array(Array(0, 0, 1, 1), Array(1, 0, 0, 1), Array(1, 1, 0, 0), Array(1, 0, 1, 0), Array(0, 1, 0, 1), Array(0, 1, 1, 0));
var stage3combos = Array(Array(1, 1, 0, 0, 0), Array(1, 0, 1, 0, 0), Array(1, 0, 0, 1, 0), Array(1, 0, 0, 0, 1), Array(0, 1, 1, 0, 0), Array(0, 1, 0, 1, 0), Array(0, 1, 0, 0, 1), Array(0, 0, 1, 0, 1), Array(0, 0, 1, 1, 0), Array(0, 0, 0, 1, 1));
var prizeIdScroll = Array(2040502, 2040505, // Overall DEX and DEF
		2040802, // Gloves for DEX
		2040002, 2040402, 2040602); // Helmet, Topwear and Bottomwear for DEF
var prizeIdUse = Array(2000001, 2000002, 2000003, 2000006, // Orange, White and Blue Potions and Mana Elixir
		2000004, 2022000, 2022003); // Elixir, Pure Water and Unagi
var prizeQtyUse = Array(80, 80, 80, 50,
		5, 15, 15);
var prizeIdEquip = Array(1032004, 1032005, 1032009, // Level 20-25 Earrings
		1032006, 1032007, 1032010, // Level 30 Earrings
		1032002, // Level 35 Earring
		1002026, 1002089, 1002090); // Bamboo Hats
var prizeIdEtc = Array(4010000, 4010001, 4010002, 4010003, // Mineral Ores
		4010004, 4010005, 4010006, // Mineral Ores
		4020000, 4020001, 4020002, 4020003, // Jewel Ores
		4020004, 4020005, 4020006, // Jewel Ores
		4020007, 4020008, 4003000); // Diamond and Black Crystal Ores and Screws
var prizeQtyEtc = Array(15, 15, 15, 15,
		8, 8, 8,
		8, 8, 8, 8,
		8, 8, 8,
		3, 3, 30);

function start() {
	status = -1;
	mapId = cm.getMapId();
	if (mapId == 910340100)
		curMap = 1;
	else if (mapId == 910340200)
		curMap = 2;
	else if (mapId == 910340300)
		curMap = 3;
	else if (mapId == 910340400)
		curMap = 4;
	else if (mapId == 910340500)
		curMap = 5;
	playerStatus = cm.isLeader();
	preamble = null;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 0 && status == 0) {
		cm.dispose();
		return;
	}
	if (mode == 1)
		status++;
	else
		status--;

	if (curMap == 1) { // First Stage.
		if (playerStatus) { // Check if player is leader
			if (status == 0) {
				var eim = cm.getEventInstance();
				party = eim.getPlayers();
				preamble = eim.getProperty("leader1stpreamble");

				if (preamble == null) {
					cm.sendNext("大家好。歡迎來到第一個關卡。在這周圍，大家一定都看到鱷魚怪在走來走去。打倒鱷魚怪的話，就一定會有一張#b票券#k掉下來。除了隊長之外，隊伍所有的成員，要各自對我所提出的問題作答，方法就是，把和答案數量一樣的，從鱷魚怪所掉下來的#b票券#k收集過來才行。\r\n和正確解答數量一樣的#b票券#k收集過來後，我就會把#b通行券#k給該名隊員。當除了隊長以外的所有隊員都得到#b通行證#之後，把它交給隊長，最後隊長再把所有的#b通行券#k交給我，這就算是通過關卡了。請加快你的速度，盡量解決，就能挑戰更多的關卡。那麼，祝你們好運了。");
					eim.setProperty("leader1stpreamble", "done");
					cm.dispose();
				} else { // Check how many they have compared to number of party members
					// Check for stage completed
					var complete = eim.getProperty(curMap.toString() + "stageclear");
					if (complete != null) {
						cm.sendNext("請加快腳步！下一關的傳送門已經開啟。");
						cm.dispose();
					} else {
						var numpasses = party.size() - 1;
						var strpasses = "#b" + numpasses.toString() + " 張通行證#k";
						if (!cm.haveItem(4001008, numpasses)) {
							cm.sendNext("請收集 " + strpasses + " ，即可完成這道關卡。告訴你的隊員如何取得通行證，並且收集起來。");
							cm.dispose();
						} else {
							cm.sendNext("成功收集 " + strpasses + "！恭喜你完成這道關卡，我已經開啟下一階段的傳送門了，請注意有時間限制，所以請加快腳步，祝你好運！");
							clear(1, eim, cm);
							cm.givePartyExp(100, party);
							cm.gainItem(4001008, -numpasses);
							cm.dispose();
							// TODO: Make the shiny thing flash
						}
					}
				}
			}
		} else { // Not leader
			var eim = cm.getChar().getEventInstance();
			pstring = "member1stpreamble" + cm.getChar().getId().toString();
			preamble = eim.getProperty(pstring);
			if (status == 0 && preamble == null) {
				var qstring = "member1st" + cm.getChar().getId().toString();
				var question = eim.getProperty(qstring);
				if (question == null) {
					// Select a random question to ask the player.
					var questionNum = Math.floor(Math.random() * questions.length);
					eim.setProperty(qstring, questionNum.toString());
				}
				cm.sendNext("在這裡，你必須消滅怪物來收集#b票券#k，並且收集#b正確數量#k的票券來兌換通行證。");
			} else if (status == 0) { // Otherwise, check for stage completed
				var complete = eim.getProperty(curMap.toString() + "stageclear");
				if (complete != null) {
					cm.sendNext("請加快腳步！下一階段的傳送門已經開啟。");
					cm.dispose();
				} else {
					// Reply to player correct/incorrect response to the question they have been asked
					var qstring = "member1st" + cm.getChar().getId().toString();
					var numcoupons = qanswers[parseInt(eim.getProperty(qstring))];
					var qcorr = cm.haveItem(4001007, (numcoupons + 1));
					var enough = false;
					if (!qcorr) { // Not too many
						qcorr = cm.haveItem(4001007, numcoupons);
						if (qcorr) { // Just right
							cm.sendNext("正確答案！成功兌換#b通行證#k！請交給你的隊長。");
							cm.gainItem(4001007, -numcoupons);
							cm.gainItem(4001008, 1);
							enough = true;
						}
					}
					if (!enough) {
						cm.sendNext("錯誤答案！請收集正確數量的票券給我。");
					}
					cm.dispose();
				}
			} else if (status == 1) {
				if (preamble == null) {
					var qstring = "member1st" + cm.getChar().getId().toString();
					var question = parseInt(eim.getProperty(qstring));
					cm.sendNextPrev(questions[question]);
				} else { // Shouldn't happen, if it does then just dispose
					cm.dispose();
				}
			} else if (status == 2) { // Preamble completed
				eim.setProperty(pstring, "done");
				cm.dispose();
			} else { // Shouldn't happen, but still...
				eim.setProperty(pstring, "done"); // Just to be sure
				cm.dispose();
			}
		} // End first map scripts
	} else if (2 <= curMap && 3 >= curMap) {
		rectanglestages(cm);
	} else if (curMap == 4) {
		var eim = cm.getChar().getEventInstance();
		var stage5done = eim.getProperty("4stageclear");
		if (stage5done == null) {
			if (playerStatus) { // Leader
				var passes = cm.getMap().getAllMonstersThreadsafe().size() == 0;
				if (passes) {
					// Clear stage
					cm.sendNext("下一階段的傳送門已經開啟！");
					party = eim.getPlayers();
					clear(4, eim, cm);
					cm.givePartyExp(700, party);
					cm.dispose();
				} else { // Not done yet
					cm.sendNext("你好，歡迎來到第四關卡。請消滅怪物，並收集通行證。");
				}
				cm.dispose();
			} else { // Members
				cm.sendNext("你好，歡迎來到第四關卡。請消滅怪物，並收集通行證交給隊長。");
				cm.dispose();
			}
		} else { // Give rewards and warp to bonus
			cm.sendNext("下一階段的傳送門已經開啟！");
			cm.dispose();
		}
	} else if (curMap == 5) { // Final stage
		var eim = cm.getChar().getEventInstance();
		if (eim == null) {
			cm.dispose();
			return;
		}
		var stage5done = eim.getProperty("5stageclear");
		if (stage5done == null) {
			if (playerStatus) { // Leader
				var passes = cm.haveItem(4001008, 1);
				if (passes) {
					// Clear stage
					cm.sendNext("恭喜你們完成所有關卡！");
					party = eim.getPlayers();
					cm.gainItem(4001008, -1);
					clear(5, eim, cm);
					cm.addPartyTrait("will", 8);
					cm.dispose();
				} else { // Not done yet
					cm.sendNext("你好，歡迎來到第五關卡，也是最後一個關卡，請消滅超級綠水靈，收集通行證，請注意這隻怪物可能比你們想像中的更強大一些，所以請小心！祝你們好運。");
				}
				cm.dispose();
			} else { // Members
				cm.sendNext("歡迎來到第五關卡，也是最後一個關卡，請消滅超級綠水靈，並且收集通行證交給隊長。請注意這隻怪物可能比你們想像中的更強大一些，所以請小心！祝你們好運。");
				cm.dispose();
			}
		} else { // Give rewards and warp to bonus
			if (status == 0) {
				cm.sendNext("難以置信！你們已經完成所有關卡，我想要送你一個小小的禮物，在你接受前，請確認道具欄位是否有剩餘空間可使用。\r\n#b如果道具欄位沒有空間，就無法獲得獎勵。#k");
			}
			if (status == 1) {
				getPrize(eim, cm);
				cm.dispose();
			}
		}
	} else { // No map found
		cm.sendNext("Invalid map, this means the stage is incomplete.");
		cm.dispose();
	}
}

function clear(stage, eim, cm) {
	eim.setProperty(stage.toString() + "stageclear", "true");

	cm.showEffect(true, "quest/party/clear");
	cm.playSound(true, "Party1/Clear");
	cm.environmentChange(true, "gate");

	var mf = eim.getMapFactory();
	map = mf.getMap(910340100 + (stage * 100));
	var nextStage = eim.getMapFactory().getMap(910340100 + (stage * 100));
	var portal = nextStage.getPortal("next00");
	if (portal != null) {
		portal.setScriptName("kpq" + (stage + 1).toString());
	}
}

function failstage(eim, cm) {
	cm.showEffect(true, "quest/party/wrong_kor");
	cm.playSound(true, "Party1/Failed");
}

function rectanglestages(cm) {
	// Debug makes these stages clear without being correct
	var eim = cm.getChar().getEventInstance();
	if (curMap == 2) {
		var nthtext = "2nd";
		var nthobj = "ropes";
		var nthverb = "hang";
		var nthpos = "hang on the ropes too low";
		var curcombo = stage2combos;
		var objset = [0, 0, 0, 0];
	} else if (curMap == 3) {
		var nthtext = "3rd";
		var nthobj = "platforms";
		var nthverb = "stand";
		var nthpos = "stand too close to the edges";
		var curcombo = stage3combos;
		var objset = [0, 0, 0, 0, 0];
	}
	if (playerStatus) { // Check if player is leader
		if (status == 0) {
			// Check for preamble
			party = eim.getPlayers();
			preamble = eim.getProperty("leader" + nthtext + "preamble");
			if (preamble == null) {
				if (curMap == 2) {
					cm.sendNext("現在說明第二關卡的內容。應該可以看到，我的身邊有很多的繩子。這其中#b有2個#k，是通往下一關卡的入口。隊伍成員之中的#b2人要去找出正確解答，並且抓住繩子#k 就可以。\r\n只不過，要是抓在繩子太過下面的地方，不會被認定為正確解答，所以一定要儘量抓在繩子夠高的地方才行。而且，一定要2個人一起抓住繩子才可以，當隊伍成員都抓住繩子之後，隊長一定要#b在我的身上用滑鼠點兩下，確認是否為正確解答#k才可以。那麼，請多多加油！");
				} else {
					cm.sendNext("現在說明第三關卡的內容。應該可以看到，我的身邊有很多館子，裡面有貓。這些管子之中，#b有2個是通往下一關卡的入口#k。隊伍成員之中的#b2人要去找正確解答，並且站在管子上#k就可以。\r\n請注意！管子會搖搖晃晃的，所以一定要站在管子的中間，才會被認定為正確解答的。而且，一定要2個人都站在管子上才可以。當隊伍成員都站在管子上之後，隊長一定要#b在我的身上用滑鼠點兩下，確認是否為正確解答#k才可以。那麼，請多多加油！");
				}
				eim.setProperty("leader" + nthtext + "preamble", "done");
				var sequenceNum = Math.floor(Math.random() * curcombo.length);
				eim.setProperty("stage" + nthtext + "combo", sequenceNum.toString());
				cm.dispose();
			} else {
				// Otherwise, check for stage completed
				var complete = eim.getProperty(curMap.toString() + "stageclear");
				if (complete != null) {
					var mapClear = curMap.toString() + "stageclear";
					eim.setProperty(mapClear, "true"); // Just to be sure
					cm.sendNext("請加快腳步！下一關的傳送門已經開啟。");
				} else { // Check for people on ropes and their positions
					var totplayers = 0;
					for (i = 0; i < objset.length; i++) {
						var present = cm.getMap().getNumPlayersItemsInArea(i);
						if (present != 0) {
							objset[i] = objset[i] + 1;
							totplayers = totplayers + 1;
						}
					}
					// Compare to correct positions
					// First, are there 3 players on the correct positions?
					if (totplayers == 2) {
						var combo = curcombo[parseInt(eim.getProperty("stage" + nthtext + "combo"))];
						// Debug
						// Combo = curtestcombo;
						var testcombo = true;
						for (i = 0; i < objset.length; i++) {
							if (combo[i] != objset[i])
								testcombo = false;
						}
						if (testcombo) {
							// Do clear
							clear(curMap, eim, cm);
							var exp = (Math.pow(2, curMap) * 50);
							cm.givePartyExp(exp, party);
							cm.dispose();
						} else { // Wrong
							// Do wrong
							failstage(eim, cm);
							cm.dispose();
						}
					} else {
						cm.sendNext("It looks like you haven't found the 2 " + nthobj + " just yet. Please think of a different combination of " + nthobj + ". Only 2 are allowed to " + nthverb + " on " + nthobj + ", and if you " + nthpos + " it may not count as an answer, so please keep that in mind. Keep going!");
						cm.dispose();
					}
				}
			}
		} else {
			var complete = eim.getProperty(curMap.toString() + "stageclear");
			if (complete != null) {
				var target = eim.getMapInstance(910340100 + (curMap * 100));
				var targetPortal = target.getPortal("st00");
				cm.getChar().changeMap(target, targetPortal);
			}
			cm.dispose();
		}
	} else { // Not leader
		if (status == 0) {
			var complete = eim.getProperty(curMap.toString() + "stageclear");
			if (complete != null) {
				cm.sendNext("請加快腳步！下一關的傳送門已經開啟。");
			} else {
				cm.sendNext("請透過隊長來找我對話。");
				cm.dispose();
			}
		} else {
			var complete = eim.getProperty(curMap.toString() + "stageclear");
			if (complete != null) {
				var target = eim.getMapInstance(910340100 + (curMap * 100));
				var targetPortal = target.getPortal("st00");
				cm.getChar().changeMap(target, targetPortal);
			}
			cm.dispose();
		}
	}
}

function getPrize(eim, cm) {
	var itemSetSel = Math.random();
	var itemSet;
	var itemSetQty;
	var hasQty = false;
	if (itemSetSel < 0.3)
		itemSet = prizeIdScroll;
	else if (itemSetSel < 0.6)
		itemSet = prizeIdEquip;
	else if (itemSetSel < 0.9) {
		itemSet = prizeIdUse;
		itemSetQty = prizeQtyUse;
		hasQty = true;
	} else {
		itemSet = prizeIdEtc;
		itemSetQty = prizeQtyEtc;
		hasQty = true;
	}
	var sel = Math.floor(Math.random() * itemSet.length);
	var qty = 1;
	if (hasQty) {
		qty = itemSetQty[sel];
		cm.gainItem(itemSet[sel], qty);
		cm.gainItem(4001531, 1);
		cm.gainExp_PQ(70, 1.5);
		cm.removeAll(4001007);
		cm.removeAll(4001008);
		cm.getPlayer().endPartyQuest(1201);
		cm.warp(910340600, 0);
	} else {
		cm.sendNext("異常...");
	}
}
