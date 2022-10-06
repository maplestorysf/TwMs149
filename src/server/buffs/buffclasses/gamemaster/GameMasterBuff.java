package server.buffs.buffclasses.gamemaster;

import client.MapleBuffStat;
import client.MapleJob;
import server.MapleStatEffect;
import server.buffs.AbstractBuffClass;

public class GameMasterBuff extends AbstractBuffClass {

    public GameMasterBuff() {
        buffs = new int[]{
            9001001, // 終極輕功
            9001002, // 終極祈禱
            9001003, // 終極祝福
            9001004, // 終極隱藏
            9001008, // hyper body
        };
    }

    @Override
    public boolean containsJob(int job) {
        return MapleJob.is管理員(job);
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 9001001: // 終極輕功
                eff.statups.put(MapleBuffStat.SPEED, (int) eff.speed);
                eff.statups.put(MapleBuffStat.JUMP, (int) eff.jump);
                break;
            case 9001002: // 終極祈禱
                eff.statups.put(MapleBuffStat.HOLY_SYMBOL, eff.x);
                break;
            case 9001003: // 終極祝福
                eff.statups.put(MapleBuffStat.BLESS, (int) eff.level);
                break;
            case 9001004: // 終極隱藏
                eff.duration = 2100000000;
                eff.statups.put(MapleBuffStat.DARKSIGHT, eff.x);
                break;
            case 9001008: // hyper body
                eff.statups.put(MapleBuffStat.MAXHP, eff.x);
                eff.statups.put(MapleBuffStat.MAXMP, eff.x);
                break;
            default:
                break;
        }
    }
}
