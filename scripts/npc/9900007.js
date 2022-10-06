/*  * Job Advancer
 * Creator: Aristocat
 * Updated by: Love
 */

load('nashorn:mozilla_compat.js'); 
importpackage(java.lang);
importpackage(Packages.client.inventory);
importpackage(Packages.constants);
importpackage(Packages.client);

var job = [
	/*冒險家*/
	[[100, "劍士"], [200, "法師"], [300, "弓箭手"], [400, "盜賊"], [500, "海盜"], [430, "影武者"], [501, "砲擊手"], [2300, "精靈遊俠"], [1000, "貴族"], [2000, "傳說"], [2200, "龍魔島"], [3000, "市民"], [3100, "惡魔殺手"]],
	/*貴族*/
	[[1100, "聖魂劍士"], [1200, "烈焰巫師"], [1300, "破風使者"], [1400, "暗夜行者"], [1500, "閃雷悍將"]],
	/*市民*/
	[[3200, "煉獄巫師"], [3300, "狂豹獵人"], [3500, "機甲戰神"]],
	/*二轉*/
	[[110, "狂戰士"], [120, "見習騎士"], [130, "槍騎兵"]],
	[[210, "火毒巫師"], [220, "冰雷巫師"], [230, "僧侶"]],
	[[310, "獵人"], [320, "弩弓手"]],
	[[410, "刺客"], [420, "俠盜"]],
	[[510, "打手"], [520, "槍手"]],
	[[3100, "惡魔殺手"]]];
var status = 0;
var select;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 1)
		status++;
	else if (mode == -1)
		status--;
	else {
		cm.dispose();
		return;
	}
	if (status == 0) {
		if (cm.getPlayer().getLevel() >= 20 && cm.getPlayer().getJob() == 2200 ||
			cm.getPlayer().getLevel() >= 30 && cm.getPlayer().getJob() == 2210 ||
			cm.getPlayer().getLevel() >= 40 && cm.getPlayer().getJob() == 2211 ||
			cm.getPlayer().getLevel() >= 50 && cm.getPlayer().getJob() == 2212 ||
			cm.getPlayer().getLevel() >= 60 && cm.getPlayer().getJob() == 2213 ||
			cm.getPlayer().getLevel() >= 80 && cm.getPlayer().getJob() == 2214 ||
			cm.getPlayer().getLevel() >= 100 && cm.getPlayer().getJob() == 2215 ||
			cm.getPlayer().getLevel() >= 120 && cm.getPlayer().getJob() == 2216 ||
			cm.getPlayer().getLevel() >= 160 && cm.getPlayer().getJob() == 2217) {
			cm.sendYesNo("您龍魔島想轉職嗎?");
		} else if (cm.getPlayer().getLevel() >= 10 && (cm.getPlayer().getJob() % 1000 == 0 || cm.getPlayer().getJob() == 501 || cm.getPlayer().getJob() == 507 || cm.getPlayer().getJob() == 3001 || cm.getPlayer().getJob() == 6001 || cm.getPlayer().getJob() == 3002 || cm.getPlayer().getJob() == 4001 || cm.getPlayer().getJob() == 4002 || cm.getPlayer().getJob() >= 2001 && cm.getPlayer().getJob() <= 2004) || cm.getPlayer().getLevel() >= 30 && (cm.getPlayer().getJob() % 1000 > 0 && cm.getPlayer().getJob() % 100 == 0 || cm.getPlayer().getJob() == 508) || cm.getPlayer().getLevel() >= 70 && cm.getPlayer().getJob() % 10 == 0 && cm.getPlayer().getJob() % 100 != 0 || cm.getPlayer().getLevel() >= 120 && cm.getPlayer().getJob() % 10 == 1 || cm.getPlayer().getLevel() >= 30 && cm.getPlayer().getJob() == 430 || cm.getPlayer().getLevel() >= 55 && cm.getPlayer().getJob() == 431 || cm.getPlayer().getLevel() >= 70 && cm.getPlayer().getJob() == 432 || cm.getPlayer().getLevel() >= 120 && cm.getPlayer().getJob() == 433) {
			cm.sendYesNo("您想轉職嗎?");
		} else {
			cm.sendOk("您還沒到達轉職條件或是您已經轉職囉!");
			cm.dispose();
		}
	} else if (status == 1) {
		if (cm.getPlayer().getSubcategory() == 1 && cm.getPlayer().getJob() == 0) { //Dual Blade
			cm.getPlayer().changeJob(400);
			cm.getPlayer().reloadC();
			cm.dispose();
			return;
		}
		if (cm.getPlayer().getSubcategory() == 1 && cm.getPlayer().getJob() == 400) { //Dual Blade
			cm.getPlayer().changeJob(430);
			cm.getPlayer().reloadC();
			cm.dispose();
			return;
		}
		if (cm.getPlayer().getSubcategory() == 2 && cm.getPlayer().getJob() == 0) { //Cannoneer
			cm.getPlayer().changeJob(501);
			cm.getPlayer().reloadC();
			cm.dispose();
			return;
		}
		switch (cm.getPlayer().getJob()) {
			//Jobs with selections
		case 0: // Beginner
			jobSelection(0);
			break;
		case 1000: // Noblesse
			jobSelection(1);
			break;
			//Note: Heroes doesn't get job selection, the same goes about Nova.
		case 3000: // Citizen
			jobSelection(2);
			break;
		case 100: // Warrior
			jobSelection(3);
			break;
		case 200: // Magician
			jobSelection(4);
			break;
		case 300: // Bowman
			jobSelection(5);
			break;
		case 400: // Thief
			jobSelection(6);
			break;
		case 500: // Pirate
			jobSelection(7);
			break;
		case 3001: // Demon
			jobSelection(8);
			break;
			//Special Jobs
		case 501: // Pirate(Cannoneer)
			cm.getPlayer().changeJob(530);
			cm.dispose();
			break;
		case 2001: // 龍魔島剛創角
			cm.getPlayer().changeJob(2210);
			cm.dispose();
			break;
		case 2200:
			cm.getPlayer().changeJob(2210);
			cm.dispose();
			break;
		case 2210:
		case 2211:
		case 2212:
		case 2213:
		case 2214:
		case 2215:
		case 2216:
		case 2216:
		case 2217:
			cm.getPlayer().changeJob(cm.getPlayer().getJob() + 1);
			cm.dispose();
			break;
		case 2002: // Mercedes
			cm.getPlayer().changeJob(2300);
			cm.dispose();
			break;
		case 3001: // Demon Slayer
			cm.getPlayer().changeJob(3100);
			cm.dispose();
			break;
			// Dual Blader
		case 430: // Blade Reqruit
		case 431: // Blade Acolyte
		case 432: // Blade Specialist
		case 433: // Blade Lord
			cm.getPlayer().changeJob(cm.getPlayer().getJob() + 1);
			cm.dispose();
			break;
		case 2000: // Legend(Aran)
			cm.getPlayer().changeJob(2100);
			cm.dispose();
			break;
			//Nova coming soon....

			//1st Job
		case 1100: // Dawn Warrior
		case 1200: // Blaze Wizard
		case 1300: // Wind Archer
		case 1400: // Night Walker
		case 1500: // Thunder Breaker
		case 2100: // Aran
		case 2300: // Mercedes
		case 3100: // Demon Slayer
		case 3200: // Battle Mage
		case 3300: // Wild Hunter
		case 3500: // Mechanic
			cm.getPlayer().changeJob(cm.getPlayer().getJob() + 10);
			cm.dispose();
			break;

			//2nd Job
		case 110: // Fighter
		case 120: // Page
		case 130: // Spearman
		case 210: // Wizard(F/P)
		case 220: // Wizard(I/L)
		case 230: // Cleric
		case 310: // Hunter
		case 320: // Crossbow man
		case 410: // Assassin
		case 420: // Bandit
		case 510: // Brawler
		case 520: // Gunslinger
		case 530: // Cannoneer
		case 1110: // Dawn Warrior
		case 1210: // Blaze Wizard
		case 1310: // Wind Archer
		case 1410: // Night Walker
		case 1510: // Thunder Breaker
		case 2110: // Aran
		case 2310: // Mercedes
		case 3110: // Demon Slayer
		case 3210: // Battle Mage
		case 3310: // Wild Hunter
		case 3510: // Mechanic

			//3rd Job
		case 111: // Crusader
		case 121: // White Knight
		case 131: // Dragon Knight
		case 211: // Mage(F/P)
		case 221: // Mage(I/L)
		case 231: // Priest
		case 311: // Ranger
		case 321: // Sniper
		case 411: // Hermit
		case 421: // Chief Bandit
		case 511: // Marauder
		case 521: // Outlaw
		case 531: // Cannon Trooper
		case 1111: // Dawn Warrior
		case 1211: // Blaze Wizard
		case 1311: // Wind Archer
		case 1411: // Night Walker
		case 1511: // Thunder Breaker
		case 2111: // Aran
		case 2311: // Mercedes
		case 3111: // Demon Slayer
		case 3211: // Battle Mage
		case 3311: // Wild Hunter
		case 3511: // Mechanic
			cm.getPlayer().changeJob(cm.getPlayer().getJob() + 1);
			cm.dispose();
			break;
		default:
			cm.sendOk("發生錯誤\r\n請回報GM您的職業代碼\r\n您的職業代碼為: " + cm.getPlayer().getJob());
			cm.dispose();
		}
	} else if (status == 2) {
		select = selection;
		cm.sendYesNo("確定要轉職?");
	} else if (status == 3) {
		if (select >= 430 && select <= 434) { // 給予sub
			cm.getPlayer().setSubcategory(1);
		} else if (select >= 501 && select <= 532) {
			cm.getPlayer().setSubcategory(2);
		} else {
			cm.getPlayer().setSubcategory(0);
		}
		cm.getPlayer().changeJob(select);
		cm.getPlayer().reloadC();
		cm.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, cm.getPlayer().getRemainingAp());
		cm.dispose();
	}
}

function jobSelection(index) {
	var choose = "您可以轉的職業有"
		for (var i = 0; i < job[index].length; i++)
			choose += "\r\n#L" + job[index][i][0] + "#" + job[index][i][1] + "#l";
		cm.sendSimple(choose);
}
