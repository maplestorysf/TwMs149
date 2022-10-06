var shaoling = 5;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            cm.sendSimple("歡迎來到少林寺！你想要挑戰武陵妖僧嗎？\r\n#L0##b是，我想挑戰武陵妖僧#k#l");
        } else if (status == 1) {
            if (selection == 0) {
                var pt = cm.getPlayer().getParty();
                /*if (cm.getQuestStatus(8534) != 0 ) {
                    cm.sendOk("你似乎不夠資格挑戰武陵妖僧！");
                    cm.dispose();
                } else if (cm.getBossLog('shaoling') >= 5) {
                    cm.sendOk("每天只能打5次妖僧！");
                    cm.dispose();
                } else */
                if (cm.getParty() == null) {
                    cm.sendOk("你尚未加入組隊。");
                    cm.dispose();
                } else if (!cm.isLeader()) {
                    cm.sendOk("請透過組隊隊長來找我對話。");
                    cm.dispose();
                } else if (pt.getMembers().size() < 3) {
                    cm.sendOk("人數未滿三人無法挑戰武陵妖僧。");
                    cm.dispose();
                } else {
                    var party = cm.getParty().getMembers();
                    var mapId = cm.getMapId();
                    var next = true;
                    var levelValid = 0;
                    var inMap = 0;

                    var it = party.iterator();
                    while (it.hasNext()) {
                        var cPlayer = it.next();
                        if ((cPlayer.getLevel() >= 90 && cPlayer.getLevel() <= 100) || cPlayer.getJobId() == 900) {
                            levelValid += 1;
                        } else {
                            next = false;
                        }
                        if (cPlayer.getMapid() == mapId) {
                            inMap += (cPlayer.getJobId() == 900 ? 3 : 1);
                        }
                    }
                    if (inMap < 3) {
                        next = false;
                    }
                    if (next) {
                        var em = cm.getEventManager("Shaoling");
                        if (em == null) {
                            cm.sendOk("未知的錯誤。");
                        } else {
                            var prop = em.getProperty("state");
                            if (prop.equals("0") || prop == null) {
                                em.startInstance(cm.getParty(), cm.getMap());
                                //cm.setPartyBossLog("Shaoling");
                                cm.dispose();
                                return;
                            } else {
                                cm.sendOk("武陵妖僧挑戰中，請稍候再試。");
                            }
                        }
                    } else {
                        cm.sendOk("請檢查全員是否介於#r90級#k至#r100級#k間且全員位在此地圖。");
                    }
                }
                cm.dispose();
            }
        }
    }
}