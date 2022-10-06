var status = -1;

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
        cm.sendYesNo("你好，我是負責支援公會業務的蕾雅。為了業務方便，可以幫你移動到英雄之殿。為了要處理公會業務需要移動到英雄之殿嗎？");
    } else if (status == 1) {
		cm.saveLocation("GUILD");
        cm.warp(200000301);
        cm.dispose();
    }
}