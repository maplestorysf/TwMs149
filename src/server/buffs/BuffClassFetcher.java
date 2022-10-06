package server.buffs;

import server.MapleStatEffect;
import server.buffs.buffclasses.adventurer.*;
import server.buffs.buffclasses.cygnus.*;
import server.buffs.buffclasses.gamemaster.*;
import server.buffs.buffclasses.hero.*;
import server.buffs.buffclasses.resistance.*;

/**
 *
 * @author Saint
 */
public class BuffClassFetcher {

    public static final Class<?>[] buffClasses = {
        WarriorBuff.class, 
        MagicianBuff.class,
        BowmanBuff.class, 
        ThiefBuff.class, 
        PirateBuff.class, 
        ChivalrousBuff.class, 
        GameMasterBuff.class,
        DawnWarriorBuff.class, 
        BlazeWizardBuff.class, 
        NightWalkerBuff.class, 
        WindArcherBuff.class,
        ThunderBreakerBuff.class, 
        AranBuff.class, 
        EvanBuff.class, 
        MercedesBuff.class, 
        PhantomBuff.class,
        DemonBuff.class, 
        BattleMageBuff.class, 
        WildHunterBuff.class,
        MechanicBuff.class, 
        MihileBuff.class
    };

    public static boolean getHandleMethod(MapleStatEffect eff, int skillid) {
        int jobid = skillid / 10000;
        for (Class<?> c : buffClasses) {
            try {
                if (!AbstractBuffClass.class.isAssignableFrom(c)) {
                    continue;
                }
                AbstractBuffClass cls = (AbstractBuffClass) c.newInstance();
                if (cls.containsJob(jobid)) {
                    if (!cls.containsSkill(skillid)) {
                        continue;
                    }
                    cls.handleBuff(eff, skillid);
                    return true;
                }
            } catch (InstantiationException | IllegalAccessException ex) {
                System.err.println("Error: handleBuff method was not found in " + c.getSimpleName() + ".class");
            } catch (Exception ex) {
            }
        }
        return false;
    }
}
