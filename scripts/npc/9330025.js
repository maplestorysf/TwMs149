var status = 0;
var skin = Array(0, 1, 2, 3, 4);
var price;

function start() {
    cm.sendSimple("歡迎來到不夜城護膚中心！如果你有#b#t5153004##k，我就能幫你護膚哦。\r\n\#L2##b使用 #t5153004##k#l");
}

function action(mode, type, selection) {
    if (mode < 1)
        cm.dispose();
    else {
        status++;
        if (status == 1)
            cm.sendStyle("請選擇你喜歡的皮膚顏色。", skin);
        else {
            if (cm.haveItem(5153004)) {
                cm.gainItem(5153004, -1);
                cm.setSkin(selection);
                cm.sendOk("護膚成功！");
            } else
                cm.sendOk("你沒有#b#t5153004##k，因而無法護膚。");
            cm.dispose();
        }
    }
}