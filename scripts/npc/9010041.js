load('nashorn:mozilla_compat.js');
importPackage(Packages.client);

function start() {
    if (cm.getPlayer().getLevel() >= 30) {
        cm.sendSimple("你好。想要正當的努力的代價嗎? 關於打工的所有事情，我#b會計小姐#k都可以幫你。\r\n#b#e#L0# 打工獎勵取得。 #l");
    } else {
        cm.sendOk("未滿#b30級#k無法打工獲得獎勵。");
    }
}

function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
        return;
    }
    if (MapleCharacter.getPartTime(cm.getPlayer().getId()).getJob() > 0) {
        cm.sendNext("打工的成果總是甜美的。我希望能再次見到你。");
        //cm.partTimeReward();
    } else {
        cm.sendOk("嗯... 你真的有完成打工嗎? 目前沒有可取得的打工獎勵。");
    }
    cm.dispose();
}