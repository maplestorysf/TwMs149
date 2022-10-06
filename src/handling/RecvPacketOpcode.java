package handling;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import tools.StringUtil;

public enum RecvPacketOpcode implements WritableIntValueHolder {
    // GENERIC

    PONG(false),
    INVALID_DECODING(false),
    CLIENT_HELLO(false),
    // LOGIN
    LOGIN_AUTH(false),
    LOGIN_PASSWORD(false),
    SEND_ENCRYPTED(false),
    CLIENT_ERROR(false),
    SERVERLIST_REQUEST,
    REDISPLAY_SERVERLIST,
    CHARLIST_REQUEST,
    SERVERSTATUS_REQUEST,
    CHECK_CHAR_NAME,
    UPDATE_ENV,
    CREATE_CHAR,
    DELETE_CHAR,
    STRANGE_DATA,
    CHAR_SELECT,
    AUTH_SECOND_PASSWORD,
    VIEW_ALL_CHAR,
    VIEW_REGISTER_PIC,
    ENABLE_SPECIAL_CREATION,
    CREATE_SPECIAL_CHAR,
    MONSTER_BOOK_DROPS,
    VIEW_SELECT_PIC,
    PICK_ALL_CHAR,
    TWIN_DRAGON_EGG,
    XMAS_SURPRISE,
    VICIOUS_HAMMER,
    USE_ALIEN_SOCKET,
    MAGIC_WHEEL,
    USE_ALIEN_SOCKET_RESPONSE,
    USE_NEBULITE_FUSION,
    CHAR_SELECT_NO_PIC,
    VIEW_SERVERLIST,
    RSA_KEY(false),
    CLIENT_START(false),
    PART_TIME_JOB,
    CHARACTER_CARD,
    CLIENT_FAILED(false),
    // CHANNEL
    PLAYER_LOGGEDIN(false),
    CHANGE_MAP,
    CHANGE_CHANNEL,
    CHANGE_ROOM_CHANNEL,
    ENTER_CASH_SHOP,
    MOVE_PLAYER,
    CANCEL_CHAIR,
    USE_TITLE,
    USE_CHAIR,
    CLOSE_RANGE_ATTACK,
    RANGED_ATTACK,
    MAGIC_ATTACK,
    PASSIVE_ENERGY,
    TAKE_DAMAGE,
    GENERAL_CHAT,
    CLOSE_CHALKBOARD,
    USE_NEBULITE,
    FACE_EXPRESSION,
    USE_ITEMEFFECT,
    WHEEL_OF_FORTUNE,
    NPC_TALK,
    NPC_TALK_MORE,
    NPC_SHOP,
    STORAGE,
    USE_HIRED_MERCHANT,
    MERCH_ITEM_STORE,
    DUEY_ACTION,
    ITEM_SORT,
    ITEM_GATHER,
    ITEM_MOVE,
    ITEM_MAKER,
    ITEM_UNLOCK,
    USE_ITEM,
    CANCEL_ITEM_EFFECT,
    //USE_FISHING, // Some unknown value sent by client after fishing for 30 sec, ignored
    USE_SUMMON_BAG,
    PET_FOOD,
    USE_MOUNT_FOOD,
    USE_SCRIPTED_NPC_ITEM,
    USE_CASH_ITEM,
    PET_LOOT_TOGGLE,
    USE_CATCH_ITEM,
    USE_SKILL_BOOK,
    USE_EXP_POTION,
    USE_RETURN_SCROLL,
    USE_UPGRADE_SCROLL,
    DISTRIBUTE_AP,
    AUTO_ASSIGN_AP,
    HEAL_OVER_TIME,
    TEACH_SKILL,
    DISTRIBUTE_SP,
    SPECIAL_MOVE,
    CANCEL_BUFF,
    SKILL_EFFECT,
    MESO_DROP,
    GIVE_FAME,
    CHAR_INFO_REQUEST,
    SPAWN_PET,
    PET_AUTO_BUFF,
    CANCEL_DEBUFF,
    CHANGE_MAP_SPECIAL,
    USE_INNER_PORTAL,
    TROCK_ADD_MAP,
    QUEST_ACTION,
    SKILL_MACRO,
    REWARD_ITEM,
    USE_TREASUER_CHEST,
    PARTYCHAT,
    WHISPER,
    MESSENGER,
    PLAYER_INTERACTION,
    PARTY_OPERATION,
    DENY_PARTY_REQUEST,
    GUILD_OPERATION,
    DENY_GUILD_REQUEST,
    BUDDYLIST_MODIFY,
    NOTE_ACTION,
    USE_DOOR,
    CHANGE_KEYMAP,
    ENTER_MTS,
    ALLIANCE_OPERATION,
    DENY_ALLIANCE_REQUEST,
    REQUEST_FAMILY,
    OPEN_FAMILY,
    FAMILY_OPERATION,
    DELETE_JUNIOR,
    DELETE_SENIOR,
    ACCEPT_FAMILY,
    USE_FAMILY,
    FAMILY_PRECEPT,
    FAMILY_SUMMON,
    CYGNUS_SUMMON,
    ARAN_COMBO,
    BBS_OPERATION,
    TRANSFORM_PLAYER,
    MOVE_PET,
    PET_CHAT,
    PET_COMMAND,
    PET_LOOT,
    PET_AUTO_POT,
    MOVE_SUMMON,
    SUMMON_ATTACK,
    DAMAGE_SUMMON,
    MOVE_LIFE,
    AUTO_AGGRO,
    FRIENDLY_DAMAGE,
    MONSTER_BOMB,
    HYPNOTIZE_DMG,
    NPC_ACTION,
    ITEM_PICKUP,
    DAMAGE_REACTOR,
    SNOWBALL,
    LEFT_KNOCK_BACK,
    COCONUT,
    MONSTER_CARNIVAL,
    SHIP_OBJECT,
    CS_UPDATE,
    BUY_CS_ITEM,
    COUPON_CODE,
    GIFT,
    MAPLETV,
    MOVE_DRAGON,
    REPAIR,
    REPAIR_ALL,
    TOUCHING_MTS,
    USE_MAGNIFY_GLASS,
    USE_POTENTIAL_SCROLL,
    USE_CARVED_SEAL,
    USE_EQUIP_SCROLL,
    GAME_POLL,
    OWL,
    OWL_WARP,
    //XMAS_SURPRISE, //header -> uniqueid(long) is entire structure
    USE_OWL_MINERVA,
    RPS_GAME,
    UPDATE_QUEST,
    //QUEST_ITEM, //header -> questid(int) -> 1/0(byte, open or close)
    USE_ITEM_QUEST,
    FOLLOW_REQUEST,
    FOLLOW_REPLY,
    MOB_NODE,
    DISPLAY_NODE,
    TOUCH_REACTOR,
    RING_ACTION,
    SOLOMON,
    GACH_EXP,
    EXPEDITION_OPERATION,
    EXPEDITION_LISTING,
    PARTY_SEARCH_START,
    PARTY_SEARCH_STOP,
    USE_TELE_ROCK,
    AZWAN_REVIVE,
    SUB_SUMMON,
    USE_MECH_DOOR,
    MECH_CANCEL,
    REMOVE_SUMMON,
    AUTO_FOLLOW_REPLY,
    REPORT,
    MOB_BOMB,
    CREATE_ULTIMATE,
    PAM_SONG,
    USE_POT,
    CLEAR_POT,
    FEED_POT,
    CURE_POT,
    CRAFT_MAKE,
    CRAFT_DONE,
    CRAFT_EFFECT,
    CHOOSE_SKILL,
    SKILL_SWIPE,
    VIEW_SKILLS,
    CANCEL_OUT_SWIPE,
    UPDATE_CORE,
    UPDATE_CORE_EXPIRE,
    CosmicDustShifter,
    UPDATE_RED_LEAF,
    STOP_HARVEST,
    START_HARVEST,
    MOVE_BAG,
    USE_BAG,
    CHANGE_SET,
    GET_BOOK_INFO,
    MOVE_ANDROID,
    FACE_ANDROID,
    REISSUE_MEDAL,
    CLICK_REACTOR,
    USE_RECIPE,
    USE_FAMILIAR,
    SPAWN_FAMILIAR,
    RENAME_FAMILIAR,
    MOVE_FAMILIAR,
    TOUCH_FAMILIAR,
    ATTACK_FAMILIAR,
    SIDEKICK_OPERATION,
    DENY_SIDEKICK_REQUEST,
    ALLOW_PARTY_INVITE,
    PROFESSION_INFO,
    QUICK_SLOT,
    STOLEN_TICK,
    MAKE_EXTRACTOR,
    USE_COSMETIC,
    INNER_CIRCULATOR,
    USE_FLAG_SCROLL,
    SWITCH_BAG,
    REWARD_POT,
    PVP_INFO,
    ENTER_PVP,
    ENTER_PVP_PARTY,
    LEAVE_PVP,
    ENTER_AZWAN,
    ENTER_AZWAN_EVENT,
    LEAVE_AZWAN,
    PVP_RESPAWN,
    PVP_ATTACK,
    PVP_SUMMON,
    PUBLIC_NPC,
    MTS_TAB,
    SET_SECOND_PASSWORD,
    CHRONOSPHERE,
    TICK;
    private short code = -2;

    @Override
    public void setValue(short code) {
        this.code = code;
    }

    @Override
    public final short getValue() {
        return code;
    }

    private boolean CheckState;

    private RecvPacketOpcode() {
        this.CheckState = true;
    }

    private RecvPacketOpcode(final boolean CheckState) {
        this.CheckState = CheckState;
    }

    public final boolean NeedsChecking() {
        return CheckState;
    }

    public static String nameOf(short value) {
        for (RecvPacketOpcode header : RecvPacketOpcode.values()) {
            if (header.getValue() == value) {
                return header.name();
            }
        }
        return "UNKNOWN";
    }

    public static boolean isSpamHeader(RecvPacketOpcode header) {
        switch (header.name()) {
            case "PONG":
            case "NPC_ACTION":
//            case "ENTER"":
//            case "CRASH_INFO":
//            case "AUTH_REQUEST":
//            case "SPECIAL_MOVE":
            case "MOVE_LIFE":
            case "MOVE_PLAYER":
            case "MOVE_ANDROID":
//            case "MOVE_DRAGON":
            case "TICK":
            case "MOVE_SUMMON":
//            case "MOVE_FAMILIAR":
            case "MOVE_PET":
            case "AUTO_AGGRO":
//            case "CLOSE_RANGE_ATTACK":
//            case "QUEST_ACTION":
            case "HEAL_OVER_TIME":
//            case "CHANGE_KEYMAP":
//            case "USE_INNER_PORTAL":
//            case "MOVE_HAKU":
//            case "FRIENDLY_DAMAGE":
//            case "CLOSE_RANGE_ATTACK":
//            case "RANGED_ATTACK":
//            case "ARAN_COMBO":
//            case "SPECIAL_STAT":
//            case "UPDATE_HYPER":
//            case "RESET_HYPER":
//            case "ANGELIC_CHANGE":
//            case "DRESSUP_TIME":
            case "BUTTON_PRESSED":
            case "STRANGE_DATA":
            case "SYSTEM_PROCESS_LIST":
            case "PINKBEAN_YOYO_REQUEST":
            case "CANCEL_DEBUFF":
                return true;
            default:
                return false;
        }
    }

    public static final void reloadValues() {
        String fileName = "recv.properties";
        Properties props = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(fileName); BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream, StringUtil.codeString(fileName)))) {
            props.load(br);
        } catch (IOException ex) {
            InputStream in = RecvPacketOpcode.class.getClassLoader().getResourceAsStream("properties/" + fileName);
            if (in == null) {
                System.err.println("錯誤: 未加載 " + fileName + " 檔案");
                return;
            }
            try {
                props.load(in);
                in.close();
            } catch (IOException e) {
                throw new RuntimeException("加載 " + fileName + " 檔案出錯", e);
            }
        }
        ExternalCodeTableGetter.populateValues(props, values());
    }

    static {
        reloadValues();
    }
}
