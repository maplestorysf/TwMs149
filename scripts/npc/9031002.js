var status = -1;
var sel = -1;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
        }
        status--;
    }
    if (status == 0) {
        cm.sendSimple("嗨！你想要做什麼？\r\n#b#L0#學習/解除採礦技能#l\r\n#L1#交換母礦的碎片#l");
    } else if (status == 1) {
        sel = selection;
        if (sel == 0) {
            if (cm.getPlayer().getProfessionLevel(92020000) > 0 || cm.getPlayer().getProfessionLevel(92030000) > 0 || cm.getPlayer().getProfessionLevel(92040000) > 0) {
                cm.sendOk("請先解除#b裝備製作#k、#b飾品製作#k、#b煉金術#k技能。");
                cm.dispose();
                return;
            }
            if (cm.getPlayer().getProfessionLevel(92010000) > 0) {
                cm.sendYesNo("你確定要解除#b採礦#k技能嗎？經驗值和等級會被初始化哦！");
            } else if (cm.getPlayer().getProfessionLevel(92000000) > 0) {
                cm.sendOk("想要學習採礦技能的話，請先解除#b採集藥草#k技能哦！");
                cm.dispose();
            } else {
                cm.sendYesNo("你想要學習#b採礦#k技能嗎？");
            }
        } else if (sel == 1) {
            if (!cm.haveItem(4011010, 100)) {
                cm.sendOk("需要100個母礦的碎片。");
            } else if (!cm.canHold(2028067, 1)) {
                cm.sendOk("請檢查消耗欄位空間。");
            } else {
                cm.sendOk("交換成功。");
                cm.gainItem(2028067, 1);
                cm.gainItem(4011010, -100);
            }
            cm.dispose();
        }
    } else if (status == 2) {
        if (sel == 0) {
            if (cm.getPlayer().getProfessionLevel(92010000) > 0) {
                cm.sendOk("成功解除#b採礦#k技能。");
                cm.teachSkill(92010000, 0, 0);
            } else {
                cm.sendOk("成功學習#b採礦#k技能。");
                cm.teachSkill(92010000, 0x1000000, 0); //00 00 00 01
                if (cm.canHold(1512000, 1)) {
                    cm.gainItem(1512000, 1);
                }
            }
            cm.dispose();
        }
    }
}