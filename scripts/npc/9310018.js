/* Romi
	Orbis Skin Change.
*/
var status = -1;
var skin = [0, 1, 2, 3, 4];

function action(mode, type, selection) {
    if (mode == 0) {
        cm.dispose();
        return;
    } else {
        status++;
    }

    if (status == 0) {
        cm.sendNext("嗨，我是#p9310018#。如果你有 #b#t5153003##k，我就可以幫你美容皮膚！");
    } else if (status == 1) {
        cm.askAvatar("請選擇你喜歡的皮膚。", skin);
    } else if (status == 2) {
        if (cm.setAvatar(5153003, skin[selection]) == 1) {
            cm.sendOk("護膚成功！");
        } else {
            cm.sendOk("您沒有#b#t5153003##k，無法護膚。");
        }
        cm.safeDispose();
    }
}