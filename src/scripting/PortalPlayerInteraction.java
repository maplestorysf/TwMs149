package scripting;

import client.MapleClient;
import server.MaplePortal;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.OverrideMonsterStats;

public class PortalPlayerInteraction extends AbstractPlayerInteraction {

    private final MaplePortal portal;

    public PortalPlayerInteraction(final MapleClient c, final MaplePortal portal) {
        super(c, null, portal.getId(), c.getPlayer().getMapId(), 0, (byte) -1);
        this.portal = portal;
    }

    public final MaplePortal getPortal() {
        return portal;
    }

    public final void inFreeMarket() {
        if (getMapId() != 910000000) {
            saveLocation("FREE_MARKET");
            playPortalSE();
            warp(910000000, "st00");
        }
    }

    public final void inArdentmill() {
        if (getMapId() != 910001000) {
            if (getPlayer().getLevel() >= 30) {
                saveLocation("ARDENTMILL");
                playPortalSE();
                warp(910001000, "st00");
            } else {
                playerMessage(5, "未滿 30 級無法進入梅斯特鎮。");
            }
        }
    }

    // summon one monster on reactor location
    @Override
    public void spawnMonster(int id) {
        spawnMonster(id, 1, portal.getPosition());
    }

    // summon monsters on reactor location
    @Override
    public void spawnMonster(int id, int qty) {
        spawnMonster(id, qty, portal.getPosition());
    }

    public void spawnMonster(int id, long hp, int mp, int qty, int exp) {
        MapleMonster monster = MapleLifeFactory.getMonster(id);
        OverrideMonsterStats stats = new OverrideMonsterStats();
        stats.setOHp(hp);
        stats.setOMp(mp);
        stats.setOExp(exp);
        monster.setOverrideStats(stats);
        for (int i = 0; i < qty; i++) {
            getMap().spawnMonsterOnGroundBelow(monster, portal.getPosition());
        }
    }
}
