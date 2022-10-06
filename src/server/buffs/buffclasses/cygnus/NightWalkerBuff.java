package server.buffs.buffclasses.cygnus;

import client.MapleBuffStat;
import client.MapleJob;
import client.status.MonsterStatus;
import server.MapleStatEffect;
import server.buffs.AbstractBuffClass;

/**
 *
 * @author Fate
 */
public class NightWalkerBuff extends AbstractBuffClass {

    public NightWalkerBuff() {
        buffs = new int[]{
            14001002, // 詛咒術
            14001003, // 隱身術
            14001007, // 速度激發
            14101002, // 極速暗殺
            14111000, // 影分身
            14111001, // 影網術
            14111007, // 無形鏢
        };
    }

    @Override
    public boolean containsJob(int job) {
        return MapleJob.is暗夜行者(job);
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 14001002: // 詛咒術
                eff.monsterStatus.put(MonsterStatus.WATK, eff.x);
                eff.monsterStatus.put(MonsterStatus.WDEF, eff.y);
                break;
            case 14001003: // 隱身術
                eff.statups.put(MapleBuffStat.DARKSIGHT, eff.x);
                break;
            case 14001007: // 速度激發
                eff.statups.put(MapleBuffStat.JUMP, (int) eff.jump);
                eff.statups.put(MapleBuffStat.SPEED, (int) eff.speed);
                break;
            case 14101002: // 極速暗殺
                eff.statups.put(MapleBuffStat.BOOSTER, eff.x);
                break;
            case 14111000: // 影分身
                eff.statups.put(MapleBuffStat.SHADOWPARTNER, eff.x);
                break;
            case 14111001: // 影網術
                eff.monsterStatus.put(MonsterStatus.SHADOW_WEB, 1);
                break;
            case 14111007: // 無形鏢
                eff.statups.put(MapleBuffStat.SPIRIT_CLAW, 0);
                break;
            default:
                // System.out.println("暗夜行者 skill not coded: " + skill);
                break;
        }
    }
}
