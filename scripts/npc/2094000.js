function action(mode, type, selection) {
    cm.removeAll(4001117);
    cm.removeAll(4001120);
    cm.removeAll(4001121);
    cm.removeAll(4001122);
    if (cm.getPlayer().getParty() == null || !cm.isLeader()) {
        cm.sendOk("請透過隊長來找我對話。");
    } else {
        var party = cm.getPlayer().getParty().getMembers();
        var mapId = cm.getPlayer().getMapId();
        var next = true;
        var size = 0;
        var it = party.iterator();
        while (it.hasNext()) {
            var cPlayer = it.next();
            var ccPlayer = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
            if (ccPlayer == null || ccPlayer.getLevel() < 70 || ccPlayer.getLevel() > 119) {
                next = false;
                break;
            }
            size += (ccPlayer.isGM() ? 4 : 1);
        }
        if (next && size >= 2) {
            var em = cm.getEventManager("Pirate");
            if (em == null) {
                cm.sendOk("請稍候再試。");
            } else {
                var prop = em.getProperty("state");
                if (prop.equals("0") || prop == null) {
                    em.startInstance(cm.getPlayer().getParty(), cm.getPlayer().getMap(), 120);
                } else {
                    cm.sendOk("其他隊伍正在挑戰中，請稍候再試。");
                }
            }
        } else {
            cm.sendOk("未滿70級，大於119級玩家無法挑戰，隊伍人數必須大於(含)2人。");
        }
    }
    cm.dispose();
}