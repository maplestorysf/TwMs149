/*
 *  Scarf Snowman - Happy Ville NPC
 */
function start() {
    cm.sendYesNo("你有好好的裝飾聖誕樹嗎？這是一個很有趣的經驗，對吧？你想離開這裡了嗎？");
}

function action(mode, type, selection) {
    if (mode == 1) {
        cm.warp(209000000);
    } else {
        cm.sendNext("你還需要更多的時間來裝飾聖誕樹，對吧？如果你想離開這個地方，隨時都可以來找我哦。");
    }
    cm.dispose();
}