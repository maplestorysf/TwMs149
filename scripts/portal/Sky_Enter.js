function enter(pi) {
    var eim = pi.getDisconnected("Dragonica");
    if (eim != null && pi.getPlayer().getParty() != null) { //only skip if not null
        eim.registerPlayer(pi.getPlayer());
        return true;
    }
    if (pi.getPlayer().getParty() == null || !pi.isLeader()) {
        pi.playerMessage(5, "請透過隊長來找我對話。");
        return false;
    }
    var party = pi.getPlayer().getParty().getMembers();
    var next = true;
    var size = 0;
    var it = party.iterator();
    while (it.hasNext()) {
        var cPlayer = it.next();
        var ccPlayer = pi.getPlayer().getMap().getCharacterById(cPlayer.getId());
        if (ccPlayer == null || ccPlayer.getLevel() < 120 /*|| (ccPlayer.getSkillLevel(ccPlayer.getStat().getSkillByJob(1026, ccPlayer.getJob())) <= 0)*/) {
            next = false;
            break;
        } else if (ccPlayer.isGM()) {
            size += 4;
        } else {
            size++;
        }
    }
    if (next && size >= 2) {
        var em = pi.getEventManager("Dragonica");
        if (em == null) {
            pi.playerMessage(5, "請稍候再試。");
        } else {
            var prop = em.getProperty("state");
            if (prop == null || prop.equals("0")) {
                em.startInstance(pi.getParty(), pi.getMap(), 200);
            } else {
                pi.playerMessage(5, "其他隊伍正在挑戰中，請稍候再試。");
            }
        }
    } else {
        pi.playerMessage(5, "未滿120級無法挑戰，隊伍人數必須大於2人。");
        return false;
    }
    return true;
}