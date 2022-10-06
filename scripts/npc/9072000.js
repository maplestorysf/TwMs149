/* 教官爾灣 租借飛機 NPC 對話抓台服封包 by Kodan*/
var status = -1;
var SkillIcon1 = "#s80001027##q80001027#";
var SkillIcon2 = "#s80001028##q80001028#";
var sel;
var aircraft = [
	[80001027, 10000, 1], // skillid meso day
	[80001027, 50000, 7],
	[80001028, 30000, 1],
	[80001028, 150000, 7]
];

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		status--;
	}
	if (mode != 1) {
		cm.dispose();
		return;
	}
	if (status == 0) {
		cm.sendSimple("使用飛機飛翔在天空看看.心情是不是很好呢?雖然年紀已經老了決定要隱退,不過教官爾灣告訴我飛翔在天空的方法.\r\n#b#L0# 想要租借飛機.#l\r\n#L1# 請說明關於飛機.#l\r\n#L2# 飛機有哪些種類?#l");
	} else if (status == 1) {
		sel = selection;
		switch (sel) {
		case 0:
			status = 10;
			cm.sendSimple("想要租借什麼類型的飛機?#r(注意:日期會蓋過去，請警慎選擇!)\r\n#b#L0#" + SkillIcon1 + "（1日）#r10000楓幣#k\r\n#b#L1#" + SkillIcon1 + "（7日）#r50000楓幣#k\r\n#b#L2#" + SkillIcon2 + "（1日）#r30000楓幣#k\r\n#b#L3#" + SkillIcon2 + "（7日）#r150000楓幣#k");
			break;
		case 1:
			cm.sendNext("飛機是什麼？飛機是新楓之谷世界新的交通工具，像其他騎寵一樣，坐上後就可以移動到各處，但是卻還能飛到其他大陸去。");
			break;
		case 2:
			cm.sendOk("可以借出的飛機,有性能還過的去,價格很便宜的#b木飛機#k,也有雖然有點貴,但可縮短陸地移動時間2分鐘的#b紅色飛機#k.選擇你自已想要的吧!");
			break;
		}
	} else if (status == 2) {
		if (sel == 1) {
			cm.sendOk("當然不是可以飛到所有大陸，在#b艾納斯島#k裡，可以飛到#b維多利亞島,耶雷弗，埃德爾斯坦，玩具城，納希綠洲城，桃花仙境，神木村#k，反之如是。還有在#b維多利亞島#k和#b埃德爾斯坦#k之間也可以使用飛機，至於其他地方由於使用飛機飛行非常危險，所以無法飛行，請務必注意。");
		}
		cm.dispose();
	} else if (status == 11) {
		sel = selection;
		var skillid = aircraft[sel][0];
		var meso = aircraft[sel][1];
		var day = aircraft[sel][2];
		if (cm.getPlayer().getMeso() >= meso) {
			cm.teachSkill(skillid, 1, 1, day);
			cm.gainMeso(-meso);
			cm.sendNext("快看看您的角色資訊裡面的騎寵技能吧!");
		} else {
			cm.sendNext("楓幣不夠？要搭飛機需要費用呢......");
		}
		cm.dispose();
	}
}
