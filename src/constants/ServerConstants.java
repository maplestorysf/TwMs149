package constants;

import server.ServerProperties;

public class ServerConstants {
    public static final byte[] Gateway_IP = new byte[]{(byte) 111, (byte) 242, (byte) 107, (byte) 224};
    // Inject a DLL that hooks SetupDiGetClassDevsExA and returns 0.

    /*
     * Specifics which job gives an additional EXP to party
     * returns the percentage of EXP to increase
     */
    public static final byte Class_Bonus_EXP(final int job) {
        switch (job) {
            case 501:
            case 530:
            case 531:
            case 532:
            case 2300:
            case 2310:
            case 2311:
            case 2312:
            case 3100:
            case 3110:
            case 3111:
            case 3112:
            case 800:
            case 900:
                return 10;
        }
        return 0;
    }
    public static final short MAPLE_VERSION = (short) 149;
    public static final String MAPLE_PATCH = "2";
    public static boolean Use_Fixed_IV = false; // true = disable sniffing, false = server can connect to itself
    public static boolean Use_Localhost = false; // true = packets are logged, false = others can connect to server
    public static boolean CUSTOM_ENCRYPTION = false;
    public static final int MIN_MTS = 100; //lowest amount an item can be, GMS = 110
    public static final int MTS_BASE = 0; //+amount to everything, GMS = 500, MSEA = 1000
    public static final int MTS_TAX = 5; //+% to everything, GMS = 10
    public static final int MTS_MESO = 10000; //mesos needed, GMS = 5000
    //master login is only used in GMS: fake account for localhost only
    //master and master2 is to bypass all accounts passwords only if you are under the IPs below

    public static boolean AUTO_REGISTER = false;
    public static boolean Can_GMItems = false;
    public static boolean isShutdown = false;
    public static boolean Disable_Shop = false;
    public static boolean isAdminOnly = false;

    public static boolean EnableHellCh = true;
    public static int[] HellCh = {2, 3};

    public static boolean MTS_FM = true;
    public static int MTS_NpcId = 9010000;
    public static String  MTS_NpcScript = "聚合型功能";

    public static boolean weekcash = true;
    public static int weekacash = 500;

    public static boolean mobdropMP = true;
    public static boolean mobdropautomp = false;
    public static int mobdropprop = 90;
    public static int mobdropminmp = 1;
    public static int mobdropmaxmp = 1;
    
    public static boolean leveljobshow = false;

    public static boolean log_chalkboard = false; //OK
    public static boolean log_mshop = true; //OK
    public static boolean log_merchant = true; //OK
    public static boolean log_csbuy = false; //OK
    public static boolean log_scroll = true; //OK
    public static boolean log_mega = false; //OK
    public static boolean log_chat = false; //OK
    public static boolean log_trade = true; //OK
    public static boolean log_drop = true; //OK
    public static boolean log_pickup = true; //OK
    public static boolean log_cube = true; //OK
    public static boolean log_seal = true; //OK
    public static boolean log_dc = true;
    public static boolean log_delchr = true; //OK
    public static boolean log_kite = true; //OK
    public static boolean log_storage = true; //OK

    public static int CashShopGiftsCount = 20;
    public static String GiftsMaxMessage = "對方的禮物盒已經超過"+CashShopGiftsCount+"件商品了，請對方接收完畢再贈禮。";

    public static boolean LOG_TRACE_GAINMESO = false;
    public static boolean LOG_TRACE_INVENTORY = false;

    public static int[] hot_sell = {10003162, 10200000, 10002111, 10002683, 10002112};

    public static boolean DisableMapCC = true;
    public static int[] MapNotCC = {910360000, 910360001, 910360100, 910360101};

    public static boolean DisableBlockSkills = true;
    public static int[] blockedSkills = {4341003};

    public static boolean EnableHM = false;
    public static boolean createMapInFM = false;
    public static boolean rebornexp = true;
    public static int JAIL = 180000002;
    public static long damagecap = 9999999999L;
    public static boolean LOG_UNHandle_PACKETS = false;
    public static boolean LOG_Handle_PACKETS = false;

    public static boolean EnablePeeSystem = true;
    public static int nopeeitem = 4000597;
	public static boolean loginMapInFM = false;

    public static enum PlayerGMRank {

        普通玩家(0),
        新實習生(1),
        老實習生(2),
        巡邏者(3),
        領導者(4),
        超級管理員(5),
        神(100);
        private final char commandPrefix;
        private final int level;

        PlayerGMRank(int level) {
            this.commandPrefix = level > 0 ? '!' : '@';
            this.level = level;
        }

        public char getCommandPrefix() {
            return commandPrefix;
        }

        public int getLevel() {
            return level;
        }
    }

    public static enum CommandType {

        NORMAL(0),
        POKEMON(2);
        private int level;

        CommandType(int level) {
            this.level = level;
        }

        public int getType() {
            return level;
        }
    }

    public static void loadSetting() {
        AUTO_REGISTER = ServerProperties.getProperty("AutoRegister", AUTO_REGISTER);
        Can_GMItems = ServerProperties.getProperty("CanGMItems", Can_GMItems);
        isShutdown = ServerProperties.getProperty("isShutdown", isShutdown);
        Disable_Shop = ServerProperties.getProperty("Disable_Shop", Disable_Shop);
        isAdminOnly = ServerProperties.getProperty("net.sf.odinms.world.admin", isAdminOnly);
        CUSTOM_ENCRYPTION = ServerProperties.getProperty("server.crypt", CUSTOM_ENCRYPTION);

        mobdropMP = ServerProperties.getProperty("mobdropMP", mobdropMP);
        mobdropautomp = ServerProperties.getProperty("mobdropautomp", mobdropautomp);
        mobdropprop = ServerProperties.getProperty("mobdropprop", mobdropprop);
        mobdropminmp = ServerProperties.getProperty("mobdropminmp", mobdropminmp);
        mobdropmaxmp = ServerProperties.getProperty("mobdropmaxmp", mobdropmaxmp);

        LOG_TRACE_GAINMESO = ServerProperties.getProperty("GainMesoLog", LOG_TRACE_GAINMESO);
        LOG_TRACE_INVENTORY = ServerProperties.getProperty("GainItemLog", LOG_TRACE_INVENTORY);

        MTS_FM = ServerProperties.getProperty("MTS_FM", MTS_FM);
        MTS_NpcId = ServerProperties.getProperty("MTS_NpcId", MTS_NpcId);
        MTS_NpcScript = ServerProperties.getProperty("MTS_NpcScript", MTS_NpcScript);

        weekcash = ServerProperties.getProperty("weekcash", weekcash);
        weekacash = ServerProperties.getProperty("weekacash", weekacash);

        log_chalkboard = ServerProperties.getProperty("log_chalkboard", log_chalkboard);
        log_mshop = ServerProperties.getProperty("log_mshop", log_mshop);
        log_merchant = ServerProperties.getProperty("log_merchant", log_merchant);
        log_csbuy = ServerProperties.getProperty("log_csbuy", log_csbuy);
        log_scroll = ServerProperties.getProperty("log_scroll", log_scroll);
        log_mega = ServerProperties.getProperty("log_mega", log_mega);
        log_chat = ServerProperties.getProperty("log_chat", log_chat);
        log_trade = ServerProperties.getProperty("log_trade", log_trade);
        log_drop = ServerProperties.getProperty("log_drop", log_drop);
        log_pickup = ServerProperties.getProperty("log_pickup", log_pickup);
        log_cube = ServerProperties.getProperty("log_cube", log_cube);
        log_seal = ServerProperties.getProperty("log_seal", log_seal);
        log_dc = ServerProperties.getProperty("log_dc", log_dc);
        log_delchr = ServerProperties.getProperty("log_delchr", log_delchr);
        log_kite = ServerProperties.getProperty("log_kite", log_kite);
        log_storage = ServerProperties.getProperty("log_storage", log_storage);

        CashShopGiftsCount = ServerProperties.getProperty("CashShopGiftsCount", CashShopGiftsCount);
        GiftsMaxMessage = ServerProperties.getProperty("GiftsMaxMessage", GiftsMaxMessage);

        DisableBlockSkills = ServerProperties.getProperty("DisableBlockSkills", DisableBlockSkills);
        String[] xxx = ServerProperties.getProperty("blockedSkills", "4341003").split(",");
        int[] yyy = new int[xxx.length];
        for (int i = 0; i <xxx.length; i++) {
            yyy[i] = Integer.parseInt(xxx[i].replace(" ", ""));
        }
        blockedSkills = yyy;

        DisableMapCC = ServerProperties.getProperty("DisableMapCC", DisableMapCC);

        String[] xx = ServerProperties.getProperty("MapNotCC", "910360000, 910360001, 910360100, 910360101").split(",");
        int[] yy = new int[xx.length];
        for (int i = 0; i <xx.length; i++) {
            yy[i] = Integer.parseInt(xx[i].replace(" ", ""));
        }
        MapNotCC = yy;

        String[] x = ServerProperties.getProperty("CSHotSell", "10003162, 10200000, 10002111, 10002683, 10002112").split(",");
        int[] y = new int[x.length];
        for (int i = 0; i < x.length; i++) {
            y[i] = Integer.parseInt(x[i].replace(" ", ""));
        }
        hot_sell = y;

        EnableHellCh = ServerProperties.getProperty("EnableHellCh", EnableHellCh);
        String[] xxxx = ServerProperties.getProperty("HellCh", "2, 3").split(",");
        int[] yyyy = new int[xxxx.length];
        for (int i = 0; i <xxxx.length; i++) {
            yyyy[i] = Integer.parseInt(xxxx[i].replace(" ", ""));
        }
        HellCh = yyyy;

        EnableHM = ServerProperties.getProperty("EnableHM", EnableHM);

        JAIL = ServerProperties.getProperty("JailMap", JAIL);
        damagecap = ServerProperties.getProperty("DamageCap", damagecap);
        LOG_UNHandle_PACKETS = ServerProperties.getProperty("packetUnHandleLog", LOG_UNHandle_PACKETS);
        LOG_Handle_PACKETS = ServerProperties.getProperty("packetHandleLog", LOG_Handle_PACKETS);
        createMapInFM = ServerProperties.getProperty("createMapInFM", createMapInFM);
        EnablePeeSystem = ServerProperties.getProperty("EnablePeeSystem", EnablePeeSystem);
        nopeeitem = ServerProperties.getProperty("nopeeitem", nopeeitem);
        loginMapInFM = ServerProperties.getProperty("loginMapInFM", loginMapInFM);
    }

    static {
        loadSetting();
    }
}
