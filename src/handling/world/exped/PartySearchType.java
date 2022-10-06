package handling.world.exped;

public enum PartySearchType {
    Dojo(25, 200, 1013, false), // 武陵道場
    Balrog_Normal(50, 200, 2000, true), //  巴洛古普通 - 正服改[簡單]
    Zakum(50, 200, 2002, true), // 殘暴炎魔
    Horntail(80, 200, 2003, true), // 闇黑龍王
    PinkBean(120, 200, 2004, true), // 皮卡啾
    ChaosZakum(100, 200, 2005, true), // 混沌殘暴炎魔
    ChaosHT(110, 200, 2006, true), // 混沌闇黑龍王
    VonLeon(120, 200, 2007, true), // 凡雷恩
    DarkEreb(170, 200, 2008, true), // 西格諾斯
    Hilla(120, 200, 2010, true), // 希拉
    Akayrum(120, 200, 2009, true); // 阿卡伊農

    public int id, minLevel, maxLevel, timeLimit;
    public boolean exped;

    private PartySearchType(int minLevel, int maxLevel, int value, boolean exped) {
        this.id = value;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.exped = exped;
        this.timeLimit = exped ? 20 : 5;
    }

    public static PartySearchType getById(int id) {
        for (PartySearchType pst : PartySearchType.values()) {
            if (pst.id == id) {
                return pst;
            }
        }
        return null;
    }
}
