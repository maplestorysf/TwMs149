package handling.world.exped;

public enum ExpeditionType {
    Normal_Balrog(15, 2000, 45, 200), // 巴洛古普通 - 正服改[簡單]
    Zakum(30, 2002, 50, 200), // 普通殘暴炎魔
    Horntail(30, 2003, 80, 200), // 普通闇黑龍王
    Pink_Bean(30, 2004, 120, 200), // 皮卡啾
    Chaos_Zakum(30, 2005, 100, 200), // 混沌殘暴炎魔
    ChaosHT(30, 2006, 110, 200), // 混沌闇黑龍王
    Von_Leon(30, 2007, 120, 200), // 凡雷恩
    Akyrum(30, 2009, 120, 200), // 阿卡伊農
    Hilla(30, 2010, 120, 200), // 希拉
    Cygnus(30, 2008, 170, 200); // 西格諾斯


    public int maxMembers, maxParty, exped, minLevel, maxLevel;

    private ExpeditionType(int maxMembers, int exped, int minLevel, int maxLevel) {
        this.maxMembers = maxMembers;
        this.exped = exped;
        this.maxParty = (maxMembers / 2) + (maxMembers % 2 > 0 ? 1 : 0);
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    public static ExpeditionType getById(int id) {
        for (ExpeditionType pst : ExpeditionType.values()) {
            if (pst.exped == id) {
                return pst;
            }
        }
        return null;
    }
}
