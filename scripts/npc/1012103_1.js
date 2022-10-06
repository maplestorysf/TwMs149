var status = 0;
var beauty = 0;
var mhair = Array(30030, 30020, 30000, 30310, 30330, 30060, 30150, 30410, 30210, 30140, 30120, 30200);
var fhair = Array(31050, 31040, 31000, 31150, 31310, 31300, 31160, 31100, 31410, 31030, 31080, 31070);
var hairnew = Array();

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode < 1) {
		cm.dispose();
	} else {
		status++;
		if (status == 0) {
			cm.sendSimple("您好，我是#p1012103#.\r\n看來您是想要完成多樣變髮師勳章任務了吧，找我就對了!! \r\n#L1#使用 #i5150042##t5150042# 來完成50次!#l");
		} else if (status == 1) {
			if (selection == 1) {
				beauty = 1;
				hairnew = Array();
				if (cm.getPlayer().getGender() == 0)
					for (var i = 0; i < mhair.length; i++)
						hairnew.push(mhair[i] + parseInt(cm.getPlayer().getHair() % 10));
				if (cm.getPlayer().getGender() == 1)
					for (var i = 0; i < fhair.length; i++)
						hairnew.push(fhair[i] + parseInt(cm.getPlayer().getHair() % 10));
				cm.sendStyle(beauty + "選擇一個想要的.", hairnew);
			}
		} else if (status == 2) {
			if (beauty == 1) {
				var hairtimes = cm.getQuestRecord(29020);
				var time = parseInt(hairtimes.getCustomData());
				if (cm.getPlayer().getHair() == hairnew[selection]) {
					cm.sendNext("您怎麼和剛才選擇的髮型一樣呢!?");
					cm.dispose();
					return;
				}
				if (time < 50) {
					if (cm.haveItem(5150042)) {
						cm.gainItem(5150042, -1);
						cm.setMedalQuestHair(hairnew[selection]);
						hairtimes.setCustomData(time+1);
						cm.sendOk("已經完成了"+(time+1)+"/50次");
					} else {
						cm.sendOk("您貌似沒有#b#t5150042##k..");
					}
				} else {
					cm.sendNext("已經滿50次了去完成任務吧!");
				}
			}
			cm.dispose();
		}
	}
}
