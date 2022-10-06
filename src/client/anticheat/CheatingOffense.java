package client.anticheat;

public enum CheatingOffense {

    吸怪(1, 7000, 5, 1),
    吸怪_飛(1, 7000, 5, 2),
    FAST_SUMMON_ATTACK(5, 6000, 50, 1),
    FASTATTACK(5, 6000, 200, 2),
    FASTATTACK2(5, 10000, 20, 2),
    FASTATTACK3(5, 30000, 50, 2),
    //ATTACK_TYPE_ERROR(5, 60000, 10, 1),
    ATTACKMOB_MP_HACK(1, 60000, 100, 1),
    ATTACK_COUNT_HACK(1, 600000, 7, 1),
    ATTACKMOB_COUNT_HACK(1, 600000, 7, 1),
    MOVE_MONSTERS(5, 30000, 500, 2),
    FAST_HP_MP_REGEN(5, 20000, 100, 2),
    SAME_DAMAGE(5, 180000),
    ATTACK_WITHOUT_GETTING_HIT(1, 30000, 1200, 0),
    HIGH_DAMAGE_MAGIC(5, 30000),
    HIGH_DAMAGE_MAGIC_2(10, 180000),
    HIGH_DAMAGE(5, 30000),
    HIGH_DAMAGE_2(10, 180000),
    EXCEED_DAMAGE_CAP(5, 60000, 800, 0),
    ATTACK_FARAWAY_MONSTER(5, 180000), // NEEDS A SPECIAL FORMULAR!
    ATTACK_FARAWAY_MONSTER_SUMMON(5, 180000, 200, 2),
    REGEN_HIGH_HP(10, 30000, 1000, 2),
    REGEN_HIGH_MP(10, 30000, 1000, 2),
    ITEMVAC_CLIENT(3, 10000, 100),
    ITEMVAC_SERVER(2, 10000, 50, 2),
    PET_ITEMVAC_CLIENT(3, 10000, 100),
    PET_ITEMVAC_SERVER(2, 10000, 100, 2),
    USING_FARAWAY_PORTAL(1, 60000, 100, 0),
    FAST_TAKE_DAMAGE(1, 60000, 100),
    HIGH_AVOID(5, 180000, 100),
    //FAST_MOVE(1, 60000),
    HIGH_JUMP(1, 60000),
    MISMATCHING_BULLETCOUNT(1, 300000),
    ETC_EXPLOSION(1, 300000),
    ATTACKING_WHILE_DEAD(1, 300000),
    USING_UNAVAILABLE_ITEM(1, 300000),
    FAMING_SELF(1, 300000), // purely for marker reasons (appears in the database)
    FAMING_UNDER_15(1, 300000),
    EXPLODING_NONEXISTANT(1, 300000),
    SUMMON_HACK(1, 300000),
    SUMMON_HACK_MOBS(1, 300000),
    ARAN_COMBO_HACK(1, 600000, 50, 2),
    HEAL_ATTACKING_UNDEAD(20, 30000, 100);
    private final int points;
    private final long validityDuration;
    private final int autobancount;
    private int bantype = 0; // 0 = Disabled, 1 = Enabled, 2 = DC

    public final int getPoints() {
        return points;
    }

    public final long getValidityDuration() {
        return validityDuration;
    }

    public final boolean shouldAutoban(final int count) {
        if (autobancount < 0) {
            return false;
        }
        return count >= autobancount;
    }

    public final int getBanType() {
        return bantype;
    }

    public final void setEnabled(final boolean enabled) {
        bantype = (enabled ? 1 : 0);
    }

    public final boolean isEnabled() {
        return bantype >= 1;
    }

    private CheatingOffense(final int points, final long validityDuration) {
        this(points, validityDuration, -1, 2);
    }

    private CheatingOffense(final int points, final long validityDuration, final int autobancount) {
        this(points, validityDuration, autobancount, 1);
    }

    private CheatingOffense(final int points, final long validityDuration, final int autobancount, final int bantype) {
        this.points = points;
        this.validityDuration = validityDuration;
        this.autobancount = autobancount;
        this.bantype = bantype;
    }
}
