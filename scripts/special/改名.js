var status = -1, donate = -1, req = 15000, maxtime = 11;
var bannedMsg = ["柚子", "管理", "GameMaster", "GM", "Fuck", "Shit", "Bitch", "雞掰", "幹", "肏", " ", "~", "～", "_", "-", "?", "*", "/", "\\", "=", "'", "(", ")", "%"]; // 大小寫沒差
var log = "改名";

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	var c = cm.getPlayer();
	donate = c.getPoints();
	if (mode == 1) {
		status++;
	} else {
		cm.dispose();
		return;
	}
	if (cm.getPlayer().getOneTimeLog(log) > 0) {
		if (req > 15000) {
			req = 15000;
		}
	}
	if (status == 0) {
		if (cm.getPlayer().getOneTimeLog(log) >= maxtime) {
			cm.sendNext("名字最多只能改"+maxtime+"次");
			cm.dispose();
			return;
		}
		if (donate >= req) {
			cm.sendYesNo("您擁有#b" + donate + "#k贊助點數，\r\n看起來您的贊助點數是足夠的，\r\n更改遊戲角色名稱需要#r" + req + " #k贊助點數，\r\n目前更名次數為#d" + cm.getPlayer().getOneTimeLog(log) + "#k次\r\n您要更改嗎？");
		} else {
			cm.sendNext("抱歉由於您的贊助點點數不足" + req + "點，無法更改角色名稱。");
			cm.dispose();
		}
	} else if (status == 1) {
		cm.sendGetText("請輸入您欲更改的角色暱稱（嚴格禁止包含以下內容：空白或底線、任何符號、不雅文字、注音文、任何誤導性字眼、與任何管理員相似之暱稱、與他人重複之暱稱)如有發現違規事宜，GM將不進行通知直接隨機改名，且不退回贊助點數以示懲戒。）");
	} else if (status == 2) {
		newusername = cm.getText();
		var next = true;

		for (var v = 0; v < bannedMsg.length; v++) {
			if (newusername.toLocaleUpperCase().indexOf(bannedMsg[v].toLocaleUpperCase()) != -1) {
				next = false;
				break;
			}
		}
		if (newusername.length >= 8) {
			next = false;
		}
		if (newusername.length <= 1) {
			next = false;
		}
		if (newusername.length == 0) {
			next = false;
		}
		if (!newusername.match("^[\u4e00-\u9fa5_a-zA-Z0-9]+$")) {
			next = false;
		}
		
		if(!filterStr(newusername).equals(newusername)){
			next = false;
		}
		
		
		if (!next) {
			cm.sendNext("您輸入的新名稱包含非法字元或是過長(超過7個字)或是過短(少於2個字)，請重新輸入。("+filterStr(newusername)+")");
			status = 0;
			return;
		} else if (Packages.client.MapleCharacterUtil.getIdByName(newusername) != -1) {
			cm.sendNext("您輸入的新名稱已經重複，請重新輸入。");
		} else {
			cm.getPlayer().setOneTimeLog(log);
			Packages.tools.FileoutputUtil.logToFile("logs/data/改名紀錄.txt", "\r\n " + Packages.tools.FileoutputUtil.NowTime() + " 玩家<" + cm.getPlayer().getName() + "> 編號<" + cm.getPlayer().getId() + "> 帳號<" + cm.getClient().getAccountName() + "> 等級<" + cm.getPlayer().getLevel() + "> 更名為 <" + newusername + ">");
			cm.getPlayer().setPoints((donate+-req));
			c.setName(newusername);
			c.fakeRelog();
			cm.showInstruction("已經將名字更改為:#r"+newusername,450,15);
		}
		cm.dispose();
	}
}

function filterStr(str) {  
	var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？%+_]");  
	var specialStr = "";  
	for (var i=0;i<str.length;i++) {  
		specialStr += str.substr(i, 1).replace(pattern, '');   
	}  
	return specialStr;  
}  
