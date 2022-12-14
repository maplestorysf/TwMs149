package client;

import handling.Buffstat;
import java.io.Serializable;

public enum MapleBuffStat implements Serializable, Buffstat {

    // IDA順序, 不可移動!!!
    STR(0x10, 5),
    INT(0x20, 5),
    DEX(0x40, 5),
    LUK(0x80, 5),
    
    WATK(0x1, 1),
    WDEF(0x2, 1),
    MATK(0x4, 1),
    MDEF(0x8, 1),
    ACC(0x10, 1),
    EVA(0x20, 1),
    HANDS(0x40, 1),
    SPEED(0x80, 1),
    JUMP(0x100, 1),
    
    // IDA順序, 不可移動!!!
    ENHANCED_MAXHP(0x4000000, 3),
    ENHANCED_MAXMP(0x8000000, 3),
    ENHANCED_WATK(0x10000000, 3),
    ENHANCED_MATK(0x20000000, 3),
    ENHANCED_WDEF(0x40000000, 3),
    ENHANCED_MDEF(0x80000000, 3),
    
    MAGIC_GUARD(0x200, 1),
    DARKSIGHT(0x400, 1),
    BOOSTER(0x800, 1),
    POWERGUARD(0x1000, 1),
    
    // IDA順序, 不可移動!!!
    PERFECT_ARMOR(0x1, 4),
    SATELLITESAFE_PROC(0x2, 4),
    SATELLITESAFE_ABSORB(0x4, 4),
    
    MAXHP(0x2000, 1),
    MAXMP(0x4000, 1),
    INVINCIBLE(0x8000, 1),
    SOULARROW(0x10000, 1),
    IgnoreTargetDEF(0x2000000, 7),
    FINAL_FEINT(0x4000000, 7),
    SHROUD_WALK(0x8000000, 7),
    JUDGMENT_DRAW(0x40000000, 7),
    //2 - debuff
    //4 - debuff
    //8 - debuff

    //1 - debuff
    COMBO(0x200000, 1),
    SUMMON(0x200000, 1), //hack buffstat for summons ^.- (does/should not increase damage... hopefully <3)
    WK_CHARGE(0x400000, 1),
    DRAGONBLOOD(0x800000, 1),
    HOLY_SYMBOL(0x1000000, 1),
    MESOUP(0x2000000, 1),
    SHADOWPARTNER(0x4000000, 1),
    PICKPOCKET(0x8000000, 1),
    PUPPET(0x8000000, 1), // HACK - shares buffmask with pickpocket - odin special ^.-

    MESOGUARD(0x10000000, 1),
    HP_LOSS_GUARD(0x20000000, 1),
    //4 - debuff
    //8 - debuff

    //1 - debuff
    MORPH(0x2, 2),
    RECOVERY(0x4, 2),
    MAPLE_WARRIOR(0x8, 2),
    Stance(0x10, 2),
    SHARP_EYES(0x20, 2),
    MANA_REFLECTION(0x40, 2),
    //8 - debuff

    SPIRIT_CLAW(0x100, 2),
    INFINITY(0x200, 2),
    HOLY_SHIELD(0x400, 2), //advanced blessing after ascension
    HAMSTRING(0x800, 2),
    BLIND(0x1000, 2),
    CONCENTRATE(0x2000, 2),
    //4 - debuff
    ECHO_OF_HERO(0x8000, 2),
    MESO_RATE(0x10000, 2), //confirmed
    GHOST_MORPH(0x20000, 2),
    ARIANT_COSS_IMU(0x40000, 2), // The white ball around you
    //8 - debuff

    DROP_RATE(0x100000, 2), //confirmed
    //2 = unknown
    EXPRATE(0x400000, 2),
    ACASH_RATE(0x800000, 2),
    ILLUSION(0x1000000, 2), //hack buffstat
    //2 and 4 are unknown
    BERSERK_FURY(0x2000000, 2), // Check OK
    DIVINE_BODY(0x4000000, 2), // Check OK
    SPARK(0x8000000, 2),
    ARIANT_COSS_IMU2(0x10000000, 2), // no idea, seems the same
    FINALATTACK(0x20000000, 2),
    ELEMENT_RESET(0x80000000, 2),
    //4 = unknown

    WIND_WALK(0x1, 3), // Check OK
    ARAN_COMBO(0x4, 3),
    COMBO_DRAIN(0x8, 3),
    COMBO_BARRIER(0x10, 3),
    BODY_PRESSURE(0x20, 3),
    SMART_KNOCKBACK(0x40, 3),
    PYRAMID_PQ(0x80, 3),
    // 4 ?
    //8 - debuff

    //1 - debuff
    //2 - debuff
    // 4 - debuff Check OK
    SLOW(0x1000, 3),
    MAGIC_SHIELD(0x2000, 3),
    MAGIC_RESISTANCE(0x4000, 3),
    SOUL_STONE(0x8000, 3),
    SOARING(0x10000, 3), // Check OK
    //8 - debuff

    LIGHTNING_CHARGE(0x40000, 3),
    ENRAGE(0x80000, 3),
    OWL_SPIRIT(0x100000, 3),
    //0x200000 debuff, shiny yellow
    FINAL_CUT(0x400000, 3),
    DAMAGE_BUFF(0x800000, 3),
    ATTACK_BUFF(0x1000000, 3), //attack %? feline berserk
    RAINING_MINES(0x2000000, 3),
    TORNADO(0x8, 4),
    CRITICAL_RATE_BUFF(0x10, 4),
    MP_BUFF(0x20, 4),
    DAMAGE_TAKEN_BUFF(0x40, 4),
    DODGE_CHANGE_BUFF(0x80, 4),
    CONVERSION(0x100, 4),
    REAPER(0x200, 4),
    INFILTRATE(0x400, 4),
    MECH_CHANGE(0x800, 4), // 合金盔甲: 導彈罐
    AURA(0x1000, 4), // 繩索
    DARK_AURA(0x2000, 4), // 黑色繩索
    BLUE_AURA(0x4000, 4), // 藍色繩索
    YELLOW_AURA(0x8000, 4), // 黃色繩索
    BODY_BOOST(0x10000, 4),
    FELINE_BERSERK(0x20000, 4),
    DICE_ROLL(0x40000, 4),
    DIVINE_SHIELD(0x80000, 4),
    DamR(0x100000, 4),
    TELEPORT_MASTERY(0x200000, 4),
    COMBAT_ORDERS(0x400000, 4),
    BEHOLDER(0x800000, 4),
    //4 = debuff
    GIANT_POTION(0x2000000, 4),
    ONYX_SHROUD(0x4000000, 4),
    ONYX_WILL(0x8000000, 4),
    //4 = debuff
    BLESS(0x20000000, 4),
    //1 //blue star + debuff
    //2 debuff	 but idk
    THREATEN_PVP(0x1, 5),
    ICE_KNIGHT(0x2, 5),
    //1 debuff idk.
    //2 unknown
    //4 unknown
    //8 unknown tornado debuff? - hp

    ANGEL_ATK(0x400, 5, true),
    ANGEL_MATK(0x800, 5, true),
    HP_BOOST(0x1000, 5, true), //indie hp
    MP_BOOST(0x2000, 5, true),
    ANGEL_ACC(0x4000, 5, true),
    ANGEL_AVOID(0x8000, 5, true),
    ANGEL_JUMP(0x10000, 5, true),
    ANGEL_SPEED(0x20000, 5, true),
    ANGEL_STAT(0x40000, 5, true),
    PVP_DAMAGE(0x80000, 5),
    PVP_ATTACK(0x100000, 5), //skills
    INVINCIBILITY(0x200000, 5),
    HIDDEN_POTENTIAL(0x400000, 5),
    ELEMENT_WEAKEN(0x40000, 5),
    SNATCH(0x80000, 5), //however skillid is 90002000, 1500 duration
    FROZEN(0x100000, 5),
    //1, unknown
    ICE_SKILL(0x400000, 5),
    //4 - debuff
    BOUNDLESS_RAGE(0x20000000, 5), // 無限力量
    // 1 unknown
    //2 = debuff
    //4 unknown
    //8 unknown

    HOLY_MAGIC_SHELL(0x1, 6), //max amount of attacks absorbed
    //2 unknown a debuff
    ARCANE_AIM(0x4, 6, true),
    BUFF_MASTERY(0x8, 6), //buff duration increase
    AsrR(0x10, 6), // %
    TerR(0x20, 6), // %
    
    // IDA順序(不可移動!!!)
    DARK_METAMORPHOSIS(0x80, 6), // mob count
    
    WATER_SHIELD(0x40, 6),
    LUCKY_BARRELS(0x100, 6), // 幸運木桶
    VIRTUE_EFFECT(0x1000, 6), // 金槍戰鬥機的祝福
    
    // IDA順序(不可移動!!!)
    SPIRIT_SURGE(0x200, 6), // 靈魂灌注
    SPIRIT_LINK(0x200, 6), // 銀隼附體
    
    CRITICAL_INC(0x8000, 6), // 靈魂灌注
    //8 unknown
    //2, 4, 8 unknown

    NO_SLIP(0x100000, 6),
    FAMILIAR_SHADOW(0x200000, 6),
    // 4
    // 8

    //CRITICAL_RATE(0x1000000, 6),
    //0x2000000 unknown
    //0x4000000 unknown DEBUFF?
    //0x8000000 unknown DEBUFF?

    // 1 unknown	
    // 2 unknown
    ABSORB_DAMAGE_HP(0x2000000, 6),
    DEFENCE_BOOST_R(0x4000000, 6), // 靈魂抗性
    // 8 unknown

    // 0x1
    // 0x2
    // 0x4
    // 0x8 got somekind of effect when buff ends...

    // 0x10
    // 0x20 dc, should be overrride
    // 0x40 add attack, 425 wd, 425 md, 260 for acc, and avoid
    // 0x80

    // 0x100 // 0x4
    HP_BOOST_PERCENT(0x8, 7, true),
    MP_BOOST_PERCENT(0x80000, 7, true),
    //WEAPON_ATTACK(0x800, 7), // 0x20

    // 0x1000, 7, true + 5003 wd // 0x40
    // 0x2000, // 0x80
    // 0x4000, true // 0x100
    // 0x8000 // 0x200

    // WEAPON ATTACK 0x10000, true // 0x400
    // 0x20000, true // 0x800
    // 0x40000, true /// 0x1000
    // 0x80000, true // 0x2000

    // 0x100000  true // 0x4000
    // 0x200000 idk // 0x8000
    // 0x400000  true // 0x10000
    // 0x800000 idk

    ENERGY_CHARGE(0x1000000, 8),
    DASH_SPEED(0x2000000, 8),
    DASH_JUMP(0x4000000, 8),
    MONSTER_RIDING(0x8000000, 8),
    SPEED_INFUSION(0x10000000, 8),
    HOMING_BEACON(0x20000000, 8),
    DEFAULT_BUFFSTAT(0x40000000, 8),
    DEFAULT_BUFFSTAT2(0x80000000, 8);
    private static final long serialVersionUID = 0L;
    private final int buffstat;
    private final int first;
    private boolean stacked = false;
    // [8] [7] [6] [5] [4] [3] [2] [1]
    // [0] [1] [2] [3] [4] [5] [6] [7]

    private MapleBuffStat(int buffstat, int first) {
        this.buffstat = buffstat;
        this.first = first;
    }

    private MapleBuffStat(int buffstat, int first, boolean stacked) {
        this.buffstat = buffstat;
        this.first = first;
        this.stacked = stacked;
    }

    @Override
    public final int getPosition() {
        return getPosition(false);
    }

    public final int getPosition(boolean fromZero) {
        if (!fromZero) {
            return first; // normal one
        }
        switch (first) {
            case 8:
                return 0;
            case 7:
                return 1;
            case 6:
                return 2;
            case 5:
                return 3;
            case 4:
                return 4;
            case 3:
                return 5;
            case 2:
                return 6;
            case 1:
                return 7;
        }
        return 0; // none
    }

    @Override
    public final int getValue() {
        return buffstat;
    }

    public final boolean canStack() {
        return stacked;
    }
}
