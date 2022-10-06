var status = -1;

function action(mode, type, selection) {
    if (mode == 0 && status == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1)
        status++;
    else
        status--;
    if (status == 0) {
        cm.sendSimple("哈囉，我是興兒，我想要#b月妙的元宵#k...#b\r\n#L0#我帶月妙的元宵過來了#l\r\n#L1#我要在這裡做什麼？#l#k");
    } else if (status == 1) {
        if (selection == 0) {
            if (!cm.isLeader()) {
                cm.sendNext("只有組隊隊長可以帶月妙的年宵過來...");
            } else {
                if (cm.haveItem(4001101, 10)) {
                    cm.achievement(100);
                    cm.gainItem(4001101, -10);
                    cm.givePartyExp_PQ(70, 1.5);
                    cm.givePartyNX(1);
                    cm.addPartyTrait("will", 5);
                    cm.addPartyTrait("sense", 1);
                    cm.endPartyQuest(1200);
                    cm.warpParty(910010300);
                } else {
                    cm.sendNext("請收集10個月妙的元宵...");
                }
            }
        } else if (selection == 1) {
            cm.sendNext("這裡是迎月花山丘，月妙會在滿月的時候做出#b月妙的元宵#k。當種下6顆種子時，月亮會變成滿月，並召喚出月妙。你們必須保護月妙，如果任務失敗，我就會餓肚子，而且會很生氣...");

        }
        cm.dispose();
    }
}