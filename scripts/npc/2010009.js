var status;
var choice;
var guildName;
var partymembers;

function start() {
	//cm.sendOk("The Guild Alliance is currently under development.");
	//cm.dispose();
	partymembers = cm.getPartyMembers();
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		cm.dispose();
		return;
	}
	if (status == 0) {
		cm.sendSimple("您好~我叫#b蕾娜里歐#k。\r\n#b#L0#請告訴我什麼是公會聯盟#l\r\n#L1#若要成立公會聯盟，該怎麼辦呢？#l\r\n#L2#我想成立公會聯盟#l\r\n#L3#我想擴充公會聯盟的人數上限#l\r\n#L4#我想要解散公會聯盟#l");
	} else if (status == 1) {
		choice = selection;
		if (selection == 0) {
			cm.sendOk("公會聯盟就是讓兩方的公會成員可以聊天做一些有趣的事情。");
			cm.dispose();
		} else if (selection == 1) {
			cm.sendOk("若要成立公會聯盟，需有兩位公會會長組成隊伍，而這時候的隊長便會擔任公會聯盟盟主的職務。");
			cm.dispose();
		} else if (selection == 2) {
			if (cm.getPlayer().getParty() == null || partymembers == null || partymembers.size() != 2 || !cm.isLeader()) {
				cm.sendOk("若要成立公會聯盟，需有兩位公會會長組成隊伍，而這時候的隊長便會擔任公會聯盟盟主的職務。"); //Not real text
				cm.dispose();
			} else if (partymembers.get(0).getGuildId() <= 0 || partymembers.get(0).getGuildRank() > 1) {
				cm.sendOk("你不能創建一個公會聯盟，直到你有自己的公會。");
				cm.dispose();
			} else if (partymembers.get(1).getGuildId() <= 0 || partymembers.get(1).getGuildRank() > 1) {
				cm.sendOk("你的成員似乎沒有自己的工會。");
				cm.dispose();
			} else {
				var gs = cm.getGuild(cm.getPlayer().getGuildId());
				var gs2 = cm.getGuild(partymembers.get(1).getGuildId());
				if (gs.getAllianceId() > 0) {
					cm.sendOk("你不能再創建因為你已經和其他結為同盟了。");
					cm.dispose();
				} else if (gs2.getAllianceId() > 0) {
					cm.sendOk("你的成員已經和其他公會結為同盟了。");
					cm.dispose();
				} else if (cm.partyMembersInMap() < 2) {
					cm.sendOk("請確保其他成員在同張地圖上。");
					cm.dispose();
				} else 
					cm.sendYesNo("哦，你有興趣創建一個公會聯盟？");
			}
		} else if (selection == 3) {
			if (cm.getPlayer().getGuildRank() == 1 && cm.getPlayer().getAllianceRank() == 1 && cm.getPlayer().getMeso() >= 10000000) {
				cm.sendYesNo("為了增加礦大 需要支付 10,000,000 楓幣. 你確定要繼續嗎？");
			} else {
				cm.sendOk("只有公會聯盟長可以擴大聯盟\r\n或者沒有1000萬楓幣。");
				cm.dispose();
			}
		} else if (selection == 4) {
			if (cm.getPlayer().getGuildRank() == 1 && cm.getPlayer().getAllianceRank() == 1) {
				cm.sendYesNo("你真的想要解散公會聯盟？？");
			} else {
				cm.sendOk("只有公會聯盟長才可以解散。");
				cm.dispose();
			}
		}
	} else if (status == 2) {
		if (choice == 2) {
			cm.sendGetText("現在請輸入你想要的公會聯盟名稱 (最大字元限制. 12 個字)");
		} else if (choice == 3) {
			if (cm.getPlayer().getGuildId() <= 0) {
				cm.sendOk("你不能增加不存公會聯盟。");
				cm.dispose();
			} else {
				if (cm.addCapacityToAlliance()) {
					cm.sendOk("你成功增加了公會聯盟容量。");
				} else {
					cm.sendOk("很抱歉，由於你的公會聯盟容量已經滿了，所以不能再擴充。");
				}
				cm.dispose();
			}
		} else if (choice == 4) {
			if (cm.getPlayer().getGuildId() <= 0) {
				cm.sendOk("你不能解散不存在的公會聯盟。");
				cm.dispose();
			} else {
				if (cm.disbandAlliance()) {
					cm.sendOk("成功解散公會聯盟。");
				} else {
					cm.sendOk("解散公會聯盟時候發生錯誤。");
				}
				cm.dispose();
			}
		}
	} else if (status == 3) {
		guildName = cm.getText();
		cm.sendYesNo("這個 #b"+ guildName + "#k 是你想要的公會聯盟名字嗎？？");
	} else if (status == 4) {
		if (!cm.createAlliance(guildName)) {
			cm.sendNext("這個名字不能使用，請嘗試其他的。") //Not real text
			status = 1;
			choice = 2;
		} else
			cm.sendOk("成功的創建了公會聯盟！！");
		cm.dispose();
	}
}
