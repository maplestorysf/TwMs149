/* Author: Xterminator
	NPC Name: 		Regular Cab
	Map(s): 		Victoria Road : Henesys (100000000)
	Description: 		Henesys Cab
*/
var status = 0;
var maps = Array(100000000, 104000000, 102000000, 101000000, 103000000, 120000000, 105000000);
var show;
var sCost;
var selectedMap = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status == 1 && mode == 0) {
        cm.dispose();
        return;
    } else if (status >= 2 && mode == 0) {
        cm.sendNext("這村莊也有很多東西可以逛喔，若想要去其它村莊或白色波浪碼頭的話請隨時利用我們計程車喔~");
        cm.dispose();
        return;
    }
    if (mode == 1)
        status++;
    else
        status--;
    if (status == 0) {
        cm.sendNext("你好！我們是#p1012000#。想要迅速又安全的去其它村莊嗎？那就試試看顧客滿意度為最重要的我們#b#p1012000##k。以便宜的價錢親切的送客到目的地喔~");
    } else if (status == 1) {
        var job = cm.getJob();
        if (job == 0 || job == 1000 || job == 2000 || job == 3000 || job == 2001 || job == 2002 || job == 3001) {
            var selStr = "初心者可獲得一折優惠唷！請選擇目的地：#b";
            for (var i = 0; i < maps.length; i++) {
                if (maps[i] != cm.getMapId()) {
                    selStr += "\r\n#L" + i + "##m" + maps[i] + "# (100 楓幣)#l";
                }
            }
        } else {
            var selStr = "請選擇目的地：#b";
            for (var i = 0; i < maps.length; i++) {
                if (maps[i] != cm.getMapId()) {
                    selStr += "\r\n#L" + i + "##m" + maps[i] + "# (1000 楓幣)#l";
                }
            }
        }
        cm.sendSimple(selStr);
    } else if (status == 2) {
        var job = cm.getJob();
        if (job == 0 || job == 1000 || job == 2000 || job == 3000 || job == 2001 || job == 2002 || job == 3001) {
            sCost = 100;
            show = 100;
        } else {
            sCost = 1000;
            show = 1000;
        }
        cm.sendYesNo("這地方應該沒有什麼可以參觀的了。確定要移動到 #b#m" + maps[selection] + "##k 村子嗎? 價格 #b" + show + " 楓幣#k。");
        selectedMap = selection;
    } else if (status == 3) {
        if (cm.getMeso() < sCost) {
            cm.sendNext("沒有楓幣將無法乘坐我們的計程車哦！");
        } else {
            cm.gainMeso(-sCost);
            cm.warp(maps[selectedMap], 0);
        }
        cm.dispose();
    }
}