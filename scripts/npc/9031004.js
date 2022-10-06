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
        cm.sendSimple("嗨！你想要做什麼？\r\n#b#L0#學習/解除飾品製作技能#l");
    } else if (status == 1) {
        if (cm.getPlayer().getProfessionLevel(92030000) > 0) {
            cm.sendYesNo("你確定要解除#b飾品製作#k技能嗎？經驗值和等級會被初始化哦！");
        } else if (cm.getPlayer().getProfessionLevel(92020000) > 0 || cm.getPlayer().getProfessionLevel(92040000) > 0 || cm.getPlayer().getProfessionLevel(92010000) <= 0) {
            cm.sendOk("請先學習#b採礦#k技能並解除#b裝備製作#k與#b煉金術#k技能。");
            cm.dispose();
        } else {
            cm.sendYesNo("你想要學習#b飾品製作#k技能嗎？");
        }
    } else if (status == 2) {
        if (cm.getPlayer().getProfessionLevel(92030000) > 0) {
            cm.sendOk("成功解除#b飾品製作#k技能。");
            cm.teachSkill(92030000, 0, 0);
        } else {
            cm.sendOk("成功學習#b飾品製作#k技能。");
            cm.teachSkill(92030000, 0x1000000, 0); //00 00 00 01
        }
        cm.dispose();
    }
}