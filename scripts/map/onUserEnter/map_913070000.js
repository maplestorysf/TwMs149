/* global ms */
var status = -1;
var select = -1;

function action(mode, type, selection) {
    if (mode === 0) {
        status--;
    } else {
        status++;
    }

    var i = -1;
    if (status <= i++) {
        ms.dispose();
    } else if (status === i++) {
        ms.getDirectionStatus(true);
        ms.lockUI(1, 1);
        ms.disableOthers(true);
        ms.sendSimpleS("你好, 請問你的性別是?\r\n#L0#我是一名#b男生#k, 你看不到嗎?!#l\r\n#L1#好吧, 我當然是一名#r女生#k啦!#l", 9010000);
    } else if (status === i++) {
        if (select === -1) {
            select = selection;
            ms.unequip(-6, true); // 褲子
            if (select === 1) {
                ms.setGender(1);
                ms.setFace(21158); // 青色乖女孩臉型
                ms.setHair(34773); // 黃色艾麗亞造型
            } else {
                ms.setGender(0);
                ms.setFace(20169); // 青色米哈逸臉型
                ms.setHair(36033); // 黃色米哈逸造型
            }
        }
        ms.sendNextS("啊, 原來是一名可愛的" + (select === 0 ? "男生" : "女生") + "啊, 好的, 祝你遊戲愉快!", 9010000);
    } else if (status === i++) {
        ms.getPlayer().dropMessage(-1, "林伯特的雜貨商店");
        ms.exceTime(500);
    } else if (status === i++) {
        ms.getPlayer().dropMessage(-1, "楓之谷曆 XXXX年 3月4日… ");
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction7.img/effect/tuto/step0/0", [2000, 0, -100, 1, 0, -100]);
        ms.getDirectionFacialExpression(6, 10000);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction7.img/effect/tuto/step0/1", [2000, 0, -100, 1, 0, -100]);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction7.img/effect/tuto/step0/2", [3000, 0, -100, 1, 0, -100]);
        ms.getDirectionFacialExpression(4, 8000);
        ms.exceTime(3000);
    } else if (status === i++) {
        ms.playerMoveLeft();
        ms.getEventEffect("Effect/Direction7.img/effect/tuto/step0/3", [3000, 0, -100, 1, 0, -100]);
        ms.getDirectionFacialExpression(6, 2000);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.playerMoveLeft();
		ms.exceTime(1500);
		ms.playerWaite();
        ms.say(0, 1106000, 3, "有…有話跟我說嗎？", false, true);
    } else if (status === i++) {
        ms.say(0, 1106000, 1, "你叫什麼名字？", true, true);
    } else if (status === i++) {
        ms.say(0, 1106000, 3, "我…沒有名字。叫我 #b「小毛頭」#k。林伯特大叔也這樣叫。你找什麼物品呢？", true, true);
    } else if (status === i++) {
        ms.say(0, 1106000, 1, "家人…沒有家人嗎？", true, true);
    } else if (status === i++) {
        ms.say(0, 1106000, 3, "我沒有家人…#b\r\n（這個人是什麼，為什麼問我這些？ ）#k\r\n如果沒有需要的物品…我就先…", true, true);
    } else if (status === i++) {
        ms.say(0, 1106000, 1, "你知道聖殿騎士雷神之錘嗎？", true, true);
    } else if (status === i++) {
        ms.say(0, 1106000, 3, "雷神之錘嗎？ 這個嘛...我不太...#b\r\n（雷神之錘是誰？ 這個人為什麼問我這些？）", true, true);
    } else if (status === i++) {
        ms.say(0, 1106000, 1106002, 5, "#e小毛頭！\r\n叫你把箱子清一清，你還在跟誰聊天？", true, true);
    } else if (status === i++) {
        ms.say(0, 1106000, 3, "是…是！ 林伯特大叔！ 我要整理了！\r\n那個，那麼…我…先告辭…", true, true);
    } else if (status === i++) {
        ms.forceCompleteQuest(20030);
        ms.getEventEffect("Effect/Direction7.img/effect/tuto/step0/4", [2000, 0, -100, 1, 0, -100]);
        ms.getDirectionFacialExpression(6, 2000);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.say(0, 1106000, 3, "去…哪裡呢？ 這個人？\r\n真是的！ 在林伯特大叔教訓我之前要快點把箱子清乾淨！", false, true);
    } else if (status === i++) {
        ms.playerMoveRight();
        ms.exceTime(800);
    } else if (status === i++) {
        ms.lockUI(0);
        ms.disableOthers(false);
        ms.dispose();
        ms.warp(913070001, 0);
    } else {
        ms.dispose();
    }
}
