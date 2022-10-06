package client;

import client.MapleTrait.MapleTraitType;
import constants.GameConstants;
import client.inventory.MapleInventoryType;
import client.inventory.MapleInventory;
import client.inventory.Item;
import client.inventory.ItemLoader;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleMount;
import client.inventory.MaplePet;
import client.inventory.ItemFlag;
import client.inventory.MapleRing;

import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Deque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.Serializable;

import constants.MiMiConfig;
import handling.login.LoginInformationProvider.JobType;
import client.anticheat.CheatTracker;
import client.anticheat.ReportType;
import client.inventory.Equip;
import client.inventory.MapleAndroid;
import client.inventory.MapleImp;
import client.inventory.MapleImp.ImpFlag;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.BattleConstants.PokemonNature;
import constants.BattleConstants.PokemonStat;
import constants.ItemConstants;
import constants.ServerConstants;
import database.DatabaseConnection;
import database.DatabaseException;

import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.CharacterTransfer;
import handling.world.MapleCharacterLook;
import handling.world.MapleMessenger;
import handling.world.MapleMessengerCharacter;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.PlayerBuffStorage;
import handling.world.PlayerBuffValueHolder;
import handling.world.World;
import handling.world.family.MapleFamily;
import handling.world.family.MapleFamilyBuff;
import handling.world.family.MapleFamilyCharacter;
import handling.world.guild.MapleGuild;
import handling.world.guild.MapleGuildCharacter;

import java.awt.Rectangle;
import java.lang.ref.WeakReference;
import java.sql.Statement;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import scripting.EventInstanceManager;
import scripting.NPCScriptManager;
import server.*;
import server.life.*;
import server.maps.AnimatedMapleMapObject;
import server.maps.MapleDoor;
import server.maps.MapleMap;
import server.maps.MapleMapFactory;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleSummon;
import server.maps.FieldLimitType;
import server.maps.SavedLocationType;
import server.quest.MapleQuest;
import server.shops.IMaplePlayerShop;
import tools.Pair;
import tools.packet.MTSCSPacket;
import tools.packet.MobPacket;
import tools.packet.PetPacket;
import tools.packet.MonsterCarnivalPacket;
import server.MapleStatEffect.CancelEffectAction;
import server.Timer.MapTimer;
import server.Timer.EtcTimer;
import server.maps.Event_PyramidSubway;
import server.maps.MapleDragon;
import server.maps.MapleExtractor;
import server.maps.MapleFoothold;
import server.maps.MechDoor;
import server.movement.LifeMovementFragment;
import tools.StringTool;
import tools.ConcurrentEnumMap;
import tools.FilePrinter;
import tools.FileoutputUtil;
import tools.MockIOSession;
import tools.StringUtil;
import tools.Triple;
import tools.packet.CField.EffectPacket;
import tools.packet.CField.NPCPacket;
import tools.packet.CField;
import tools.packet.CField.SummonPacket;
import tools.packet.CField.UIPacket;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.BuddylistPacket;
import tools.packet.CWvsContext.BuffPacket;
import tools.packet.CWvsContext.InfoPacket;
import tools.packet.CWvsContext.InventoryPacket;
import tools.packet.PlayerShopPacket;
import tools.packet.JobPacket.PhantomPacket;

public class MapleCharacter extends AnimatedMapleMapObject implements Serializable, MapleCharacterLook {

    private static final long serialVersionUID = 845748950829L;
    private static final ReentrantLock saveLock = new ReentrantLock();
    private final Map<String, String> TempValues = new HashMap<>();
    private String name, chalktext, BlessOfFairy_Origin, BlessOfEmpress_Origin, teleportname, chatmsg = "", storagemsg = "";
    private long lastCombo, lastfametime, keydown_skill, nextConsume, pqStartTime, lastDragonBloodTime,
            lastBerserkTime, lastRecoveryTime, lastSummonTime, mapChangeTime, lastFairyTime,
            lastHPTime, lastMPTime, lastFamiliarEffectTime, lastDOTTime, lastTradeTime, lastWarpingMap, lastUsingPortal;
    private byte gmLevel, gender, initialSpawnPoint, skinColor, guildrank = 5, allianceRank = 5,
            world, fairyExp, numClones, subcategory, cardStack, runningStack;
    private short level, mulung_energy, combo, force, availableCP, fatigue, totalCP, hpApUsed, job, remainingAp, scrolledPosition;
    private int accountid, id, meso, exp, hair, face, demonMarking, mapid, fame, pvpExp, pvpPoints, totalWins, totalLosses,
            guildid = 0, fallcounter, maplepoints, acash, chair, itemEffect, titleEffect, points, vpoints,
            rank = 1, rankMove = 0, jobRank = 1, jobRankMove = 0, marriageId, marriageItemId, dotHP,
            currentrep, totalrep, coconutteam, followid, battleshipHP, gachexp, challenge, guildContribution = 0, honourExp, honorLevel, chronosphere, cschronosphere, vip, reward,
            npcnow, pee, total_donate, mobVac, SayGood = 0 ,onlinetime = 0;
    private Point old;
    private MonsterFamiliar summonedFamiliar;
    private int[] wishlist, rocks, savedLocations, regrocks, hyperrocks, remainingSp = new int[10];
    private transient AtomicInteger inst, insd;
    private transient List<LifeMovementFragment> lastres;
    private List<Integer> lastmonthfameids, lastmonthbattleids, extendedSlots;
    private List<MapleDoor> doors;
    private List<MechDoor> mechDoors;
    private List<MaplePet> pets;
    private List<Item> rebuy;
    private MapleImp[] imps;
    private List<Pair<Integer, Boolean>> stolenSkills = new ArrayList<>();
    private transient WeakReference<MapleCharacter>[] clones;
    private transient Set<MapleMonster> controlled;
    private transient Set<MapleMapObject> visibleMapObjects;
    private transient ReentrantReadWriteLock visibleMapObjectsLock;
    private transient ReentrantReadWriteLock summonsLock;
    private transient ReentrantReadWriteLock controlledLock;
    private transient MapleAndroid android;
    private transient HashMap<Integer, Long> leftSkillTime = new HashMap<>();
    private Map<MapleQuest, MapleQuestStatus> quests;
    private Map<Integer, String> questinfo;
    private Map<Skill, SkillEntry> skills;
    private transient Map<MapleBuffStat, MapleBuffStatValueHolder> effects;
    private final Map<String, String> CustomValues = new HashMap<>();
    private transient List<MapleSummon> summons;
    private transient Map<Integer, MapleCoolDownValueHolder> coolDowns;
    private transient Map<MapleDisease, MapleDiseaseValueHolder> diseases;
    private Map<ReportType, Integer> reports;
    private CashShop cs;
    private transient Deque<MapleCarnivalChallenge> pendingCarnivalRequests;
    private transient MapleCarnivalParty carnivalParty;
    private BuddyList buddylist;
    private MonsterBook monsterbook;
    private transient CheatTracker anticheat;
    private MapleClient client;
    private transient MapleParty party;
    private PlayerStats stats;
    private final MapleCharacterCards characterCard;
    private transient MapleMap map;
    private transient MapleShop shop;
    private transient MapleDragon dragon;
    private transient MapleExtractor extractor;
    private transient RockPaperScissors rps;
    private Map<Integer, MonsterFamiliar> familiars;
    private MapleStorage storage;
    private transient MapleTrade trade;
    private MapleMount mount;
    private List<Integer> finishedAchievements;
    private MapleMessenger messenger;
    private byte[] petStore;
    private transient IMaplePlayerShop playerShop;
    private boolean invincible, canTalk, clone, followinitiator, followon, smega, hasSummon;
    private MapleGuildCharacter mgc;
    private MapleFamilyCharacter mfc;
    private transient EventInstanceManager eventInstance;
    private MapleInventory[] inventory;
    private SkillMacro[] skillMacros = new SkillMacro[5];
    private EnumMap<MapleTraitType, MapleTrait> traits;
    private Battler[] battlers = new Battler[6];
    private List<Battler> boxed;
    private MapleKeyLayout keylayout;
    private transient ScheduledFuture<?> mapTimeLimitTask, fishing;
    private transient Event_PyramidSubway pyramidSubway = null;
    private transient List<Integer> pendingExpiration = null;
    private transient Map<Skill, SkillEntry> pendingSkills = null;
    private transient Map<Integer, Integer> linkMobs;
    private List<InnerSkillValueHolder> innerSkills;
    public boolean keyvalue_changed = false, innerskill_changed = true;

    private transient PokemonBattle battle;
    private boolean tradeinviting = false, storageing = false, changed_wishlist, changed_trocklocations, changed_regrocklocations, changed_hyperrocklocations, changed_skillmacros, changed_achievements,
            changed_savedlocations, changed_pokemon, changed_questinfo, changed_skills, changed_reports, changed_extendedSlots, walkdebug = false, DebugMessage = false, HackMessage = false,
            attackdebug = false, check_msg_BuyMerChant = false, check_msg_Chat = false, flying = false, check_DelChrLog = false, check_FishingVip = false;
    private List<MapleSummon> linksummon = new ArrayList();
    private int luckyBarrelsStatus;
    /*Start of Custom Feature*/
    /*All custom shit declare here*/
    private int reborns, apstorage;
    private boolean useVipCharm = false;
    private boolean useFirmCharm = false;
    private transient PlayerRandomStream CRand;
    private static String[] ariantroomleader = new String[3];
    private static int[] ariantroomslot = new int[3];
    public int apprentice = 0;

    /*End of Custom Feature*/
    private int[] friendshippoints = new int[4];
    private int friendshiptoadd;
    private MapleCoreAura coreAura;

    public long dojoStartTime;
    public long dojoMapEndTime;
    public long dojoMapEndTimeTotal;

    private MapleCharacter(final boolean ChannelServer) {
        setStance(0);
        setPosition(new Point(0, 0));

        inventory = new MapleInventory[MapleInventoryType.values().length];
        for (MapleInventoryType type : MapleInventoryType.values()) {
            inventory[type.ordinal()] = new MapleInventory(type);
        }
        quests = new LinkedHashMap<>(); // Stupid erev quest.
        questinfo = new LinkedHashMap<>();
        skills = new LinkedHashMap<>(); //Stupid UAs.
        stats = new PlayerStats();
        innerSkills = new LinkedList<>();
        characterCard = new MapleCharacterCards();
        for (int i = 0; i < remainingSp.length; i++) {
            remainingSp[i] = 0;
        }
        traits = new EnumMap<>(MapleTraitType.class);
        for (MapleTraitType t : MapleTraitType.values()) {
            traits.put(t, new MapleTrait(t));
        }
        if (ChannelServer) {
            changed_reports = false;
            changed_skills = false;
            changed_achievements = false;
            changed_wishlist = false;
            changed_trocklocations = false;
            changed_regrocklocations = false;
            changed_hyperrocklocations = false;
            changed_skillmacros = false;
            changed_savedlocations = false;
            changed_pokemon = false;
            changed_extendedSlots = false;
            changed_questinfo = false;
            scrolledPosition = 0;
            lastCombo = 0;
            mulung_energy = 0;
            combo = 0;
            force = 0;
            keydown_skill = 0;
            nextConsume = 0;
            pqStartTime = 0;
            fairyExp = 0;
            cardStack = 0;
            runningStack = 1;
            mapChangeTime = 0;
            lastRecoveryTime = 0;
            lastDragonBloodTime = 0;
            lastBerserkTime = 0;
            lastFairyTime = 0;
            lastHPTime = 0;
            lastMPTime = 0;
            lastFamiliarEffectTime = 0;
            old = new Point(0, 0);
            coconutteam = 0;
            followid = 0;
            battleshipHP = 0;
            marriageItemId = 0;
            fallcounter = 0;
            challenge = 0;
            dotHP = 0;
            lastSummonTime = 0;
            hasSummon = false;
            invincible = false;
            canTalk = true;
            clone = false;
            followinitiator = false;
            followon = false;
            rebuy = new ArrayList<>();
            linkMobs = new HashMap<>();
            finishedAchievements = new ArrayList<>();
            reports = new EnumMap<>(ReportType.class);
            teleportname = "";
            smega = true;
            petStore = new byte[3];
            for (int i = 0; i < petStore.length; i++) {
                petStore[i] = (byte) -1;
            }
            wishlist = new int[10];
            rocks = new int[10];
            regrocks = new int[5];
            hyperrocks = new int[13];
            imps = new MapleImp[3];
            clones = new WeakReference[5]; //for now
            for (int i = 0; i < clones.length; i++) {
                clones[i] = new WeakReference<>(null);
            }
            boxed = new ArrayList<>();
            familiars = new LinkedHashMap<>();
            extendedSlots = new ArrayList<>();
            effects = new ConcurrentEnumMap<>(MapleBuffStat.class);
            coolDowns = new LinkedHashMap<>();
            diseases = new ConcurrentEnumMap<>(MapleDisease.class);
            inst = new AtomicInteger(0);// 1 = NPC/ Quest, 2 = Duey, 3 = Hired Merch store, 4 = Storage
            insd = new AtomicInteger(-1);
            keylayout = new MapleKeyLayout();
            doors = new ArrayList<>();
            mechDoors = new ArrayList<>();
            controlled = new LinkedHashSet<>();
            controlledLock = new ReentrantReadWriteLock();
            summons = new LinkedList<>();
            summonsLock = new ReentrantReadWriteLock();
            visibleMapObjects = new LinkedHashSet<>();
            visibleMapObjectsLock = new ReentrantReadWriteLock();
            pendingCarnivalRequests = new LinkedList<>();

            savedLocations = new int[SavedLocationType.values().length];
            for (int i = 0; i < SavedLocationType.values().length; i++) {
                savedLocations[i] = -1;
            }
            questinfo = new LinkedHashMap<>();
            pets = new ArrayList<>();
            friendshippoints = new int[4];
            coreAura = new MapleCoreAura(id, System.currentTimeMillis() + 24 * 60 * 60 * 1000);
            coreAura.resetCoreAura();
        }
    }

    public static MapleCharacter getDefault(final MapleClient client, final JobType type) {
        MapleCharacter ret = new MapleCharacter(false);
        ret.client = client;
        ret.map = null;
        ret.exp = 0;
        ret.gmLevel = 0;
        ret.job = (short) type.id;
        ret.meso = 0;
        ret.level = 1;
        ret.remainingAp = 0;
        ret.fame = 0;
        ret.accountid = client.getAccID();
        ret.buddylist = new BuddyList((byte) 20);

        ret.stats.str = 12;
        ret.stats.dex = 5;
        ret.stats.int_ = 4;
        ret.stats.luk = 4;
        ret.stats.maxhp = 50;
        ret.stats.hp = 50;
        ret.stats.maxmp = 50;
        ret.stats.mp = 50;
        ret.gachexp = 0;
        ret.friendshippoints = new int[]{0, 0, 0, 0};
        ret.friendshiptoadd = 0;

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps;
            ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
            ps.setInt(1, ret.accountid);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                ret.client.setAccountName(rs.getString("name"));
                ret.acash = rs.getInt("ACash");
                ret.maplepoints = rs.getInt("mPoints");
                ret.points = rs.getInt("points");
                ret.vpoints = rs.getInt("vpoints");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error getting character default" + e);
        }
        return ret;
    }

    public final static MapleCharacter ReconstructChr(final CharacterTransfer ct, final MapleClient client, final boolean isChannel) {
        final MapleCharacter ret = new MapleCharacter(true); // Always true, it's change channel
        ret.client = client;
        if (!isChannel) {
            ret.client.setChannel(ct.channel);
        }
        ret.onlinetime = ct.onlinetime;
        ret.id = ct.characterid;
        ret.name = ct.name;
        ret.level = ct.level;
        ret.fame = ct.fame;

        ret.CRand = new PlayerRandomStream();

        ret.stats.str = ct.str;
        ret.stats.dex = ct.dex;
        ret.stats.int_ = ct.int_;
        ret.stats.luk = ct.luk;
        ret.stats.maxhp = ct.maxhp;
        ret.stats.maxmp = ct.maxmp;
        ret.stats.hp = ct.hp;
        ret.stats.mp = ct.mp;

        ret.characterCard.setCards(ct.cardsInfo);

        ret.chalktext = ct.chalkboard;
        ret.gmLevel = ct.gmLevel;
        ret.exp = (ret.level >= 200 || (GameConstants.isKOC(ret.job) && ret.level >= 200)) && !ret.isIntern() ? 0 : ct.exp;
        ret.hpApUsed = ct.hpApUsed;
        ret.remainingSp = ct.remainingSp;
        ret.remainingAp = ct.remainingAp;
        ret.meso = ct.meso;
        ret.stolenSkills = ct.stolenSkills;
        ret.skinColor = ct.skinColor;
        ret.gender = ct.gender;
        ret.job = ct.job;
        ret.hair = ct.hair;
        ret.face = ct.face;
        ret.demonMarking = ct.demonMarking;
        ret.accountid = ct.accountid;
        ret.totalWins = ct.totalWins;
        ret.totalLosses = ct.totalLosses;
        client.setAccID(ct.accountid);
        ret.mapid = ct.mapid;
        ret.initialSpawnPoint = ct.initialSpawnPoint;
        ret.world = ct.world;
        ret.guildid = ct.guildid;
        ret.guildrank = ct.guildrank;
        ret.guildContribution = ct.guildContribution;
        ret.allianceRank = ct.alliancerank;
        ret.points = ct.points;
        ret.vpoints = ct.vpoints;
        ret.fairyExp = ct.fairyExp;
        ret.cardStack = ct.cardStack;
        ret.marriageId = ct.marriageId;
        ret.currentrep = ct.currentrep;
        ret.totalrep = ct.totalrep;
        ret.gachexp = ct.gachexp;
        ret.honourExp = ct.honourexp;
        ret.honorLevel = ct.honourlevel;
        ret.innerSkills = (LinkedList<InnerSkillValueHolder>) ct.innerSkills;
        ret.pvpExp = ct.pvpExp;
        ret.pvpPoints = ct.pvpPoints;
        /*Start of Custom Feature*/
        ret.reborns = ct.reborns;
        ret.apstorage = ct.apstorage;
        ret.pee = ct.pee;
        ret.chronosphere = ct.chronosphere;
        ret.cschronosphere = ct.cschronosphere;
        /*End of Custom Feature*/
        ret.makeMFC(ct.familyid, ct.seniorid, ct.junior1, ct.junior2);
        if (ret.guildid > 0) {
            ret.mgc = new MapleGuildCharacter(ret);
        }
        ret.fatigue = ct.fatigue;
        ret.buddylist = new BuddyList(ct.buddysize);
        ret.subcategory = ct.subcategory;

        if (isChannel) {
            final MapleMapFactory mapFactory = ChannelServer.getInstance(client.getChannel()).getMapFactory();
            ret.map = mapFactory.getMap(ret.mapid);
            if (ret.map == null) { //char is on a map that doesn't exist warp it to henesys
                ret.map = mapFactory.getMap(910000000);
            } else {
                if (ret.map.getForcedReturnId() != 999999999 && ret.map.getForcedReturnMap() != null) {
                    ret.map = ret.map.getForcedReturnMap();
                }
            }
            MaplePortal portal = ret.map.getPortal(ret.initialSpawnPoint);
            if (portal == null) {
                portal = ret.map.getPortal(0); // char is on a spawnpoint that doesn't exist - select the first spawnpoint instead
                ret.initialSpawnPoint = 0;
            }
            ret.setPosition(portal.getPosition());

            final int messengerid = ct.messengerid;
            if (messengerid > 0) {
                ret.messenger = World.Messenger.getMessenger(messengerid);
            }
        } else {

            ret.messenger = null;
        }
        int partyid = ct.partyid;
        if (partyid >= 0) {
            MapleParty party = World.Party.getParty(partyid);
            if (party != null && party.getMemberById(ret.id) != null) {
                ret.party = party;
            }
        }

        MapleQuestStatus queststatus_from;
        for (final Map.Entry<Integer, Object> qs : ct.Quest.entrySet()) {
            queststatus_from = (MapleQuestStatus) qs.getValue();
            queststatus_from.setQuest(qs.getKey());
            ret.quests.put(queststatus_from.getQuest(), queststatus_from);
        }
        for (final Map.Entry<Integer, SkillEntry> qs : ct.Skills.entrySet()) {
            ret.skills.put(SkillFactory.getSkill(qs.getKey()), qs.getValue());
        }
        for (final Integer zz : ct.finishedAchievements) {
            ret.finishedAchievements.add(zz);
        }
        for (final Object zz : ct.boxed) {
            Battler zzz = (Battler) zz;
            zzz.setStats();
            ret.boxed.add(zzz);
        }
        for (Entry<MapleTraitType, Integer> t : ct.traits.entrySet()) {
            ret.traits.get(t.getKey()).setExp(t.getValue());
        }
        for (final Map.Entry<Byte, Integer> qs : ct.reports.entrySet()) {
            ret.reports.put(ReportType.getById(qs.getKey()), qs.getValue());
        }
        ret.monsterbook = new MonsterBook(ct.mbook, ret);
        ret.inventory = (MapleInventory[]) ct.inventorys;
        ret.BlessOfFairy_Origin = ct.BlessOfFairy;
        ret.BlessOfEmpress_Origin = ct.BlessOfEmpress;
        ret.skillMacros = (SkillMacro[]) ct.skillmacro;
        ret.battlers = (Battler[]) ct.battlers;
        for (Battler b : ret.battlers) {
            if (b != null) {
                b.setStats();
            }
        }
        ret.petStore = ct.petStore;
        ret.keylayout = new MapleKeyLayout(ct.keymap);
        ret.questinfo = ct.InfoQuest;
        ret.familiars = ct.familiars;
        ret.savedLocations = ct.savedlocation;
        ret.wishlist = ct.wishlist;
        ret.rocks = ct.rocks;
        ret.regrocks = ct.regrocks;
        ret.hyperrocks = ct.hyperrocks;
        ret.buddylist.loadFromTransfer(ct.buddies);
        // ret.lastfametime
        // ret.lastmonthfameids
        ret.keydown_skill = 0; // Keydown skill can't be brought over
        ret.lastfametime = ct.lastfametime;
        ret.lastmonthfameids = ct.famedcharacters;
        ret.lastmonthbattleids = ct.battledaccs;
        ret.extendedSlots = ct.extendedSlots;
        ret.storage = (MapleStorage) ct.storage;
        ret.cs = (CashShop) ct.cs;
        client.setAccountName(ct.accountname);
        ret.acash = ct.ACash;
        ret.maplepoints = ct.MaplePoints;
        ret.numClones = ct.clonez;
        ret.imps = ct.imps;
        ret.anticheat = (CheatTracker) ct.anticheat;
        ret.anticheat.start(ret);
        ret.rebuy = ct.rebuy;
        ret.mount = new MapleMount(ret, ct.mount_itemid, ret.stats.getSkillByJob(1004, ret.job), ct.mount_Fatigue, ct.mount_level, ct.mount_exp);
        ret.expirationTask(false, false);
        ret.stats.recalcLocalStats(true, ret);
        client.setTempIP(ct.tempIP);

        return ret;
    }

    public static MapleCharacter loadCharFromDB(int charid, MapleClient client, boolean channelserver) {
        return loadCharFromDB(charid, client, channelserver, null);
    }

    public static MapleCharacter loadCharFromDB(int charid, MapleClient client, boolean channelserver, final Map<Integer, CardData> cads) {
        final MapleCharacter ret = new MapleCharacter(channelserver);
        ret.client = client;
        ret.id = charid;

        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        PreparedStatement pse = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement("SELECT * FROM characters WHERE id = ?");
            ps.setInt(1, charid);
            rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                throw new RuntimeException("Loading the Char Failed (char not found)");
            }
            ret.name = rs.getString("name");
            ret.level = rs.getShort("level");
            ret.fame = rs.getInt("fame");
            ret.stats.str = rs.getShort("str");
            ret.stats.dex = rs.getShort("dex");
            ret.stats.int_ = rs.getShort("int");
            ret.stats.luk = rs.getShort("luk");
            ret.stats.maxhp = rs.getInt("maxhp");
            ret.stats.maxmp = rs.getInt("maxmp");
            ret.stats.hp = rs.getInt("hp");
            ret.stats.mp = rs.getInt("mp");
            ret.job = rs.getShort("job");
            ret.gmLevel = rs.getByte("gm");
            ret.exp = rs.getInt("exp");
            ret.hpApUsed = rs.getShort("hpApUsed");
            final String[] sp = rs.getString("sp").split(",");
            for (int i = 0; i < ret.remainingSp.length; i++) {
                ret.remainingSp[i] = Integer.parseInt(sp[i]);
            }
            ret.remainingAp = rs.getShort("ap");
            ret.meso = rs.getInt("meso");
            ret.skinColor = rs.getByte("skincolor");
            ret.gender = rs.getByte("gender");

            ret.hair = rs.getInt("hair");
            ret.face = rs.getInt("face");
            ret.demonMarking = rs.getInt("demonMarking");
            ret.accountid = rs.getInt("accountid");
            client.setAccID(ret.accountid);
            ret.mapid = rs.getInt("map");
            ret.initialSpawnPoint = rs.getByte("spawnpoint");
            ret.world = rs.getByte("world");
            ret.guildid = rs.getInt("guildid");
            ret.guildrank = rs.getByte("guildrank");
            ret.allianceRank = rs.getByte("allianceRank");
            ret.guildContribution = rs.getInt("guildContribution");
            ret.totalWins = rs.getInt("totalWins");
            ret.totalLosses = rs.getInt("totalLosses");
            ret.currentrep = rs.getInt("currentrep");
            ret.totalrep = rs.getInt("totalrep");
            ret.makeMFC(rs.getInt("familyid"), rs.getInt("seniorid"), rs.getInt("junior1"), rs.getInt("junior2"));
            if (ret.guildid > 0) {
                ret.mgc = new MapleGuildCharacter(ret);
            }
            ret.gachexp = rs.getInt("gachexp");
            ret.buddylist = new BuddyList(rs.getByte("buddyCapacity"));
            ret.honourExp = rs.getInt("honourExp");
            ret.honorLevel = rs.getInt("honourLevel");
            ret.subcategory = rs.getByte("subcategory");
            ret.mount = new MapleMount(ret, 0, ret.stats.getSkillByJob(1004, ret.job), (byte) 0, (byte) 1, 0);
            ret.rank = rs.getInt("rank");
            ret.rankMove = rs.getInt("rankMove");
            ret.jobRank = rs.getInt("jobRank");
            ret.jobRankMove = rs.getInt("jobRankMove");
            ret.marriageId = rs.getInt("marriageId");
            ret.fatigue = rs.getShort("fatigue");
            ret.pvpExp = rs.getInt("pvpExp");
            ret.pvpPoints = rs.getInt("pvpPoints");
            ret.friendshiptoadd = rs.getInt("friendshiptoadd");
            /*Start of Custom Features*/
            ret.reborns = rs.getInt("reborns");
            ret.apstorage = rs.getInt("apstorage");
            ret.pee = rs.getInt("pee");
            ret.chronosphere = rs.getInt("chronosphere");
            ret.cschronosphere = rs.getInt("cschronosphere");
            /*End of Custom Features*/
            for (MapleTrait t : ret.traits.values()) {
                t.setExp(rs.getInt(t.getType().name()));
            }
            if (channelserver) {
                ret.CRand = new PlayerRandomStream();
                ret.anticheat = new CheatTracker(ret);
                MapleMapFactory mapFactory = ChannelServer.getInstance(client.getChannel()).getMapFactory();
                if (!GameConstants.isBeginnerJob(ret.job) && ret.level < 10) {
                    ret.map = mapFactory.getMap(ServerConstants.loginMapInFM ? 910000000 : ret.mapid);
                } else {
                    ret.map = mapFactory.getMap(ret.mapid);
                }
                if (ret.map == null) { //char is on a map that doesn't exist warp it to henesys
                    ret.map = mapFactory.getMap(910000000);
                }
                MaplePortal portal = ret.map.getPortal(ret.initialSpawnPoint);
                if (portal == null) {
                    portal = ret.map.getPortal(0); // char is on a spawnpoint that doesn't exist - select the first spawnpoint instead
                    ret.initialSpawnPoint = 0;
                }
                ret.setPosition(portal.getPosition());

                int partyid = rs.getInt("party");
                if (partyid >= 0) {
                    MapleParty party = World.Party.getParty(partyid);
                    if (party != null && party.getMemberById(ret.id) != null) {
                        ret.party = party;
                    }
                }
                final String[] pets = rs.getString("pets").split(",");
                for (int i = 0; i < ret.petStore.length; i++) {
                    ret.petStore[i] = Byte.parseByte(pets[i]);
                }
                String[] friendshippoints = rs.getString("friendshippoints").split(",");
                for (int i = 0; i < 4; i++) {
                    ret.friendshippoints[i] = Integer.parseInt(friendshippoints[i]);
                }
                rs.close();
                ps.close();
                ps = con.prepareStatement("SELECT * FROM achievements WHERE accountid = ?");
                ps.setInt(1, ret.accountid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ret.finishedAchievements.add(rs.getInt("achievementid"));
                }
                ps.close();
                rs.close();

                ps = con.prepareStatement("SELECT * FROM reports WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    if (ReportType.getById(rs.getByte("type")) != null) {
                        ret.reports.put(ReportType.getById(rs.getByte("type")), rs.getInt("count"));
                    }
                }

            }
            rs.close();
            ps.close();

            if (cads != null) { // so that we load only once.
                ret.characterCard.setCards(cads);
            } else { // load
                ret.characterCard.loadCards(client, channelserver);
            }

            ps = con.prepareStatement("SELECT * FROM queststatus WHERE characterid = ?");
            ps.setInt(1, charid);
            rs = ps.executeQuery();
            pse = con.prepareStatement("SELECT * FROM queststatusmobs WHERE queststatusid = ?");

            while (rs.next()) {
                final int id = rs.getInt("quest");
                final MapleQuest q = MapleQuest.getInstance(id);
                final byte stat = rs.getByte("status");
                if ((stat == 1 || stat == 2) && channelserver && (q == null || q.isBlocked())) { //bigbang
                    continue;
                }
                if (stat == 1 && channelserver && !q.canStart(ret, null)) { //bigbang
                    continue;
                }
                final MapleQuestStatus status = new MapleQuestStatus(q, stat);
                final long cTime = rs.getLong("time");
                if (cTime > -1) {
                    status.setCompletionTime(cTime * 1000);
                }
                status.setForfeited(rs.getInt("forfeited"));
                status.setCustomData(rs.getString("customData"));
                ret.quests.put(q, status);
                pse.setLong(1, rs.getLong("queststatusid"));
                final ResultSet rsMobs = pse.executeQuery();

                while (rsMobs.next()) {
                    status.setMobKills(rsMobs.getInt("mob"), rsMobs.getInt("count"));
                }
                rsMobs.close();
            }
            rs.close();
            ps.close();
            pse.close();

            if (channelserver) {
                ret.monsterbook = MonsterBook.loadCards(ret.accountid, ret);

                ps = con.prepareStatement("SELECT * FROM inventoryslot where characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();

                if (!rs.next()) {
                    rs.close();
                    ps.close();
                    throw new RuntimeException("No Inventory slot column found in SQL. [inventoryslot]");
                } else {
                    ret.getInventory(MapleInventoryType.EQUIP).setSlotLimit(rs.getByte("equip"));
                    ret.getInventory(MapleInventoryType.USE).setSlotLimit(rs.getByte("use"));
                    ret.getInventory(MapleInventoryType.SETUP).setSlotLimit(rs.getByte("setup"));
                    ret.getInventory(MapleInventoryType.ETC).setSlotLimit(rs.getByte("etc"));
                    ret.getInventory(MapleInventoryType.CASH).setSlotLimit(rs.getByte("cash"));
                }
                ps.close();
                rs.close();

                for (Pair<Item, MapleInventoryType> mit : ItemLoader.INVENTORY.loadItems(false, charid).values()) {
                    ret.getInventory(mit.getRight()).addFromDB(mit.getLeft());
                    if (mit.getLeft().getPet() != null) {
                        ret.pets.add(mit.getLeft().getPet());
                    }
                }

                ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
                ps.setInt(1, ret.accountid);
                rs = ps.executeQuery();
                if (rs.next()) {
                    ret.getClient().setAccountName(rs.getString("name"));
                    ret.acash = rs.getInt("ACash");
                    ret.maplepoints = rs.getInt("mPoints");
                    ret.points = rs.getInt("points");
                    ret.vpoints = rs.getInt("vpoints");

                    if (ServerConstants.weekcash) {
                        if (rs.getTimestamp("lastlogon") != null) {
                            final Calendar cal = Calendar.getInstance();
                            cal.setTimeInMillis(rs.getTimestamp("lastlogon").getTime());
                            if (cal.get(Calendar.DAY_OF_WEEK) + 1 == Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                                ret.acash += ServerConstants.weekacash;
                            }
                        }
                    }

                    if (rs.getInt("banned") > 0) {
                        rs.close();
                        ps.close();
                        ret.getClient().getSession().close();
                        throw new RuntimeException("Loading a banned character");
                    }
                    rs.close();
                    ps.close();

                    ps = con.prepareStatement("UPDATE accounts SET lastlogon = CURRENT_TIMESTAMP() WHERE id = ?");
                    ps.setInt(1, ret.accountid);
                    ps.executeUpdate();
                } else {
                    rs.close();
                }
                ps.close();

                ps = con.prepareStatement("SELECT * FROM questinfo WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();

                while (rs.next()) {
                    ret.questinfo.put(rs.getInt("quest"), rs.getString("customData"));
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT skillid, skilllevel, masterlevel, expiration, teachId FROM skills WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                Skill skil;
                while (rs.next()) {
                    final int skid = rs.getInt("skillid");
                    skil = SkillFactory.getSkill(skid);
                    int skl = rs.getInt("skilllevel");
                    byte msl = rs.getByte("masterlevel");
                    int teachId = rs.getInt("teachId");
                    if (skil != null && GameConstants.isApplicableSkill(skid)) {
                        if (skl > skil.getMaxLevel() && skid < 92000000) {
                            if (!skil.isBeginnerSkill() && skil.canBeLearnedBy(ret.job) && !skil.isSpecialSkill()) {
                                ret.remainingSp[GameConstants.getSkillBookForSkill(skid)] += (skl - skil.getMaxLevel());
                            }
                            skl = (byte) skil.getMaxLevel();
                        }
                        if (msl > skil.getMaxLevel()) {
                            msl = (byte) skil.getMaxLevel();
                        }
                        ret.skills.put(skil, new SkillEntry(skl, msl, rs.getLong("expiration"), teachId));
                    } else if (skil == null) { //doesnt. exist. e.g. bb
                        if (!GameConstants.isBeginnerJob(skid / 10000) && skid / 10000 != 900 && skid / 10000 != 800 && skid / 10000 != 9000) {
                            ret.remainingSp[GameConstants.getSkillBookForSkill(skid)] += skl;
                        }
                    }
                }
                rs.close();
                ps.close();

                ret.expirationTask(false, true); //do it now

                ps = con.prepareStatement("SELECT * FROM coreauras WHERE cid = ?");
                ps.setInt(1, ret.id);
                rs = ps.executeQuery();
                if (rs.next()) {
                    ret.coreAura = new MapleCoreAura(ret.id, rs.getLong("expire"));
                    ret.coreAura.setDelay(rs.getBoolean("delay"));
                    ret.coreAura.setStr(rs.getInt("str"));
                    ret.coreAura.setDex(rs.getInt("dex"));
                    ret.coreAura.setInt(rs.getInt("int"));
                    ret.coreAura.setLuk(rs.getInt("luk"));
                    ret.coreAura.setAtt(rs.getInt("att"));
                    ret.coreAura.setMagic(rs.getInt("magic"));
                    ret.coreAura.setTotal(rs.getInt("total"));
                } else {
                    ret.coreAura = new MapleCoreAura(ret.id, System.currentTimeMillis() + 24 * 60 * 60 * 1000);
                    ret.coreAura.resetCoreAura();
                }
                rs.close();
                ps.close();

                // Bless of Fairy handling
                ps = con.prepareStatement("SELECT * FROM characters WHERE accountid = ? ORDER BY level DESC");
                ps.setInt(1, ret.accountid);
                rs = ps.executeQuery();
                int maxlevel_ = 0, maxlevel_2 = 0;
                while (rs.next()) {
                    if (rs.getInt("id") != charid) { // Not this character
                        if (GameConstants.isKOC(rs.getShort("job"))) {
                            int maxlevel = (rs.getShort("level") / 5);

                            if (maxlevel > 24) {
                                maxlevel = 24;
                            }
                            if (maxlevel > maxlevel_2 || maxlevel_2 == 0) {
                                maxlevel_2 = maxlevel;
                                ret.BlessOfEmpress_Origin = rs.getString("name");
                            }
                        }
                        int maxlevel = (rs.getShort("level") / 10);

                        if (maxlevel > 20) {
                            maxlevel = 20;
                        }
                        if (maxlevel > maxlevel_ || maxlevel_ == 0) {
                            maxlevel_ = maxlevel;
                            ret.BlessOfFairy_Origin = rs.getString("name");
                        }

                    }
                }
                /*if (!compensate_previousSP) {
                for (Entry<Skill, SkillEntry> skill : ret.skills.entrySet()) {
                if (!skill.getKey().isBeginnerSkill() && !skill.getKey().isSpecialSkill()) {
                ret.remainingSp[GameConstants.getSkillBookForSkill(skill.getKey().getId())] += skill.getValue().skillevel;
                skill.getValue().skillevel = 0;
                }
                }
                ret.setQuestAdd(MapleQuest.getInstance(170000), (byte) 0, null); //set it so never again
                }*/
                if (ret.BlessOfFairy_Origin == null) {
                    ret.BlessOfFairy_Origin = ret.name;
                }
                ret.skills.put(SkillFactory.getSkill(GameConstants.getBOF_ForJob(ret.job)), new SkillEntry(maxlevel_, (byte) 0, -1, -1));
                if (SkillFactory.getSkill(GameConstants.getEmpress_ForJob(ret.job)) != null) {
                    if (ret.BlessOfEmpress_Origin == null) {
                        ret.BlessOfEmpress_Origin = ret.BlessOfFairy_Origin;
                    }
                    ret.skills.put(SkillFactory.getSkill(GameConstants.getEmpress_ForJob(ret.job)), new SkillEntry(maxlevel_2, (byte) 0, -1, -1));
                }
                ps.close();
                rs.close();
                // END

                ps = con.prepareStatement("SELECT skill_id, skill_level, max_level, rank, locked FROM inner_ability_skills WHERE player_id = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ret.innerSkills.add(new InnerSkillValueHolder(rs.getInt("skill_id"), rs.getByte("skill_level"), rs.getByte("max_level"), rs.getByte("rank"), rs.getBoolean("locked")));
                }

                ps = con.prepareStatement("SELECT * FROM skillmacros WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                int position;
                while (rs.next()) {
                    position = rs.getInt("position");
                    SkillMacro macro = new SkillMacro(rs.getInt("skill1"), rs.getInt("skill2"), rs.getInt("skill3"), rs.getString("name"), rs.getInt("shout"), position);
                    ret.skillMacros[position] = macro;
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT * FROM familiars WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    if (rs.getLong("expiry") <= System.currentTimeMillis()) {
                        continue;
                    }
                    ret.familiars.put(rs.getInt("familiar"), new MonsterFamiliar(charid, rs.getInt("id"), rs.getInt("familiar"), rs.getLong("expiry"), rs.getString("name"), rs.getInt("fatigue"), rs.getByte("vitality")));
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT * FROM pokemon WHERE characterid = ? OR (accountid = ? AND active = 0)");
                ps.setInt(1, charid);
                ps.setInt(2, ret.accountid);
                rs = ps.executeQuery();
                position = 0;
                while (rs.next()) {
                    Battler b = new Battler(rs.getInt("level"), rs.getInt("exp"), charid, rs.getInt("monsterid"), rs.getString("name"), PokemonNature.values()[rs.getInt("nature")], rs.getInt("itemid"), rs.getByte("gender"), rs.getByte("hpiv"), rs.getByte("atkiv"), rs.getByte("defiv"), rs.getByte("spatkiv"), rs.getByte("spdefiv"), rs.getByte("speediv"), rs.getByte("evaiv"), rs.getByte("acciv"), rs.getByte("ability"));
                    if (b.getFamily() == null) {
                        continue;
                    }
                    if (rs.getInt("active") > 0 && position < 6 && rs.getInt("characterid") == charid) {
                        ret.battlers[position] = b;
                        position++;
                    } else {
                        ret.boxed.add(b);
                    }
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT `key`,`type`,`action` FROM keymap WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();

                final Map<Integer, Pair<Byte, Integer>> keyb = ret.keylayout.Layout();
                while (rs.next()) {
                    keyb.put(rs.getInt("key"), new Pair<>(rs.getByte("type"), rs.getInt("action")));
                }
                rs.close();
                ps.close();
                ret.keylayout.unchanged();

                ps = con.prepareStatement("SELECT `locationtype`,`map` FROM savedlocations WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ret.savedLocations[rs.getInt("locationtype")] = rs.getInt("map");
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT `characterid_to`,`when` FROM famelog WHERE characterid = ? AND DATEDIFF(NOW(),`when`) < 30");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                ret.lastfametime = 0;
                ret.lastmonthfameids = new ArrayList<>(31);
                while (rs.next()) {
                    ret.lastfametime = Math.max(ret.lastfametime, rs.getTimestamp("when").getTime());
                    ret.lastmonthfameids.add(rs.getInt("characterid_to"));
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT `accid_to`,`when` FROM battlelog WHERE accid = ? AND DATEDIFF(NOW(),`when`) < 30");
                ps.setInt(1, ret.accountid);
                rs = ps.executeQuery();
                ret.lastmonthbattleids = new ArrayList<>();
                while (rs.next()) {
                    ret.lastmonthbattleids.add(rs.getInt("accid_to"));
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT `itemId` FROM extendedSlots WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ret.extendedSlots.add(rs.getInt("itemId"));
                }
                rs.close();
                ps.close();

                ret.buddylist.loadFromDb(charid);
                ret.storage = MapleStorage.loadStorage(ret.accountid);
                ret.cs = new CashShop(ret.accountid, charid, ret.getJob());

                ps = con.prepareStatement("SELECT sn FROM wishlist WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                int i = 0;
                while (rs.next()) {
                    ret.wishlist[i] = rs.getInt("sn");
                    i++;
                }
                while (i < 10) {
                    ret.wishlist[i] = 0;
                    i++;
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT mapid FROM trocklocations WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                int r = 0;
                while (rs.next()) {
                    ret.rocks[r] = rs.getInt("mapid");
                    r++;
                }
                while (r < 10) {
                    ret.rocks[r] = 999999999;
                    r++;
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT mapid FROM regrocklocations WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                r = 0;
                while (rs.next()) {
                    ret.regrocks[r] = rs.getInt("mapid");
                    r++;
                }
                while (r < 5) {
                    ret.regrocks[r] = 999999999;
                    r++;
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT mapid FROM hyperrocklocations WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                r = 0;
                while (rs.next()) {
                    ret.hyperrocks[r] = rs.getInt("mapid");
                    r++;
                }
                while (r < 13) {
                    ret.hyperrocks[r] = 999999999;
                    r++;
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT * from stolen WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ret.stolenSkills.add(new Pair<>(rs.getInt("skillid"), rs.getInt("chosen") > 0));
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT * FROM imps WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                r = 0;
                while (rs.next()) {
                    ret.imps[r] = new MapleImp(rs.getInt("itemid"));
                    ret.imps[r].setLevel(rs.getByte("level"));
                    ret.imps[r].setState(rs.getByte("state"));
                    ret.imps[r].setCloseness(rs.getShort("closeness"));
                    ret.imps[r].setFullness(rs.getShort("fullness"));
                    r++;
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT * FROM mountdata WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new RuntimeException("在數據庫沒有找到角色坐騎訊息...");
                }
                final Item mount = ret.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18);
                ret.mount = new MapleMount(ret, mount != null ? mount.getItemId() : 0, 80001000, rs.getByte("Fatigue"), rs.getByte("Level"), rs.getInt("Exp"));
                ps.close();
                rs.close();

                ret.stats.recalcLocalStats(true, ret);
            } else { // Not channel server
                for (Pair<Item, MapleInventoryType> mit : ItemLoader.INVENTORY.loadItems(true, charid).values()) {
                    ret.getInventory(mit.getRight()).addFromDB(mit.getLeft());
                }
                ret.stats.recalcPVPRank(ret);
            }
        } catch (SQLException ess) {
            ess.printStackTrace();
            System.out.println("Failed to load character..");
            FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, ess);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ignore) {
            }
        }
        return ret;
    }

    public static void saveNewCharToDB(final MapleCharacter chr, final JobType type, short db) {
        Connection con = DatabaseConnection.getConnection();

        PreparedStatement ps = null;
        PreparedStatement pse = null;
        ResultSet rs = null;
        try {
            con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            con.setAutoCommit(false);

            ps = con.prepareStatement("INSERT INTO characters (level, str, dex, luk, `int`, hp, mp, maxhp, maxmp, sp, ap, skincolor, gender, job, hair, face, demonMarking, map, meso, party, buddyCapacity, pets, subcategory, friendshippoints, pee, chronosphere, cschronosphere, accountid, name, world) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", DatabaseConnection.RETURN_GENERATED_KEYS);
            ps.setInt(1, chr.level); // Level
            final PlayerStats stat = chr.stats;
            ps.setShort(2, stat.getStr()); // Str
            ps.setShort(3, stat.getDex()); // Dex
            ps.setShort(4, stat.getInt()); // Int
            ps.setShort(5, stat.getLuk()); // Luk
            ps.setInt(6, stat.getHp()); // HP
            ps.setInt(7, stat.getMp());
            ps.setInt(8, stat.getMaxHp()); // MP
            ps.setInt(9, stat.getMaxMp());
            final StringBuilder sps = new StringBuilder();
            for (int i = 0; i < chr.remainingSp.length; i++) {
                sps.append(chr.remainingSp[i]);
                sps.append(",");
            }
            final String sp = sps.toString();
            ps.setString(10, sp.substring(0, sp.length() - 1));
            ps.setShort(11, (short) chr.remainingAp); // Remaining AP
            ps.setByte(12, chr.skinColor);
            ps.setByte(13, chr.gender);
            ps.setShort(14, chr.job);
            ps.setInt(15, chr.hair);
            ps.setInt(16, chr.face);
            ps.setInt(17, chr.demonMarking);
            if (db < 0 || db > 2) { //todo legend
                db = 0;
            }
            ps.setInt(18, ServerConstants.createMapInFM ? 910000000 : db == 2 ? 3000600 : type.map);
            ps.setInt(19, chr.meso); // Meso
            ps.setInt(20, -1); // Party
            ps.setByte(21, chr.buddylist.getCapacity()); // Buddylist
            ps.setString(22, "-1,-1,-1");
            ps.setInt(23, db); //for now
            ps.setString(24, chr.friendshippoints[0] + "," + chr.friendshippoints[1] + "," + chr.friendshippoints[2] + "," + chr.friendshippoints[3]);
            ps.setInt(25, chr.pee);
            ps.setInt(26, chr.chronosphere);
            ps.setInt(27, chr.cschronosphere);
            ps.setInt(28, chr.getAccountID());
            ps.setString(29, chr.name);
            ps.setByte(30, chr.world);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                chr.id = rs.getInt(1);
            } else {
                ps.close();
                rs.close();
                throw new DatabaseException("Inserting char failed.");
            }
            ps.close();
            rs.close();
            ps = con.prepareStatement("INSERT INTO queststatus (`queststatusid`, `characterid`, `quest`, `status`, `time`, `forfeited`, `customData`) VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)", DatabaseConnection.RETURN_GENERATED_KEYS);
            pse = con.prepareStatement("INSERT INTO queststatusmobs VALUES (DEFAULT, ?, ?, ?)");
            ps.setInt(1, chr.id);
            for (final MapleQuestStatus q : chr.quests.values()) {
                ps.setInt(2, q.getQuest().getId());
                ps.setInt(3, q.getStatus());
                ps.setInt(4, (int) (q.getCompletionTime() / 1000));
                ps.setInt(5, q.getForfeited());
                ps.setString(6, q.getCustomData());
                ps.execute();
                rs = ps.getGeneratedKeys();
                if (q.hasMobKills()) {
                    rs.next();
                    for (int mob : q.getMobKills().keySet()) {
                        pse.setInt(1, rs.getInt(1));
                        pse.setInt(2, mob);
                        pse.setInt(3, q.getMobKills(mob));
                        pse.execute();
                    }
                }
                rs.close();
            }
            ps.close();
            pse.close();

            ps = con.prepareStatement("INSERT INTO skills (characterid, skillid, skilllevel, masterlevel, expiration, teachId) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, chr.id);

            for (final Entry<Skill, SkillEntry> skill : chr.skills.entrySet()) {
                if (GameConstants.isApplicableSkill(skill.getKey().getId())) { //do not save additional skills
                    ps.setInt(2, skill.getKey().getId());
                    ps.setInt(3, skill.getValue().skillevel);
                    ps.setByte(4, skill.getValue().masterlevel);
                    ps.setLong(5, skill.getValue().expiration);
                    ps.setInt(6, skill.getValue().teachId);
                    ps.execute();
                }
            }
            ps.close();

            ps = con.prepareStatement("INSERT INTO coreauras (cid, str, dex, `int`, luk, att, magic, total, expire, delay) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, chr.id);
//            if (MapleJob.is蒼龍俠客(chr.job)) {
            ps.setInt(2, 5);
            ps.setInt(3, 5);
            ps.setInt(4, 5);
            ps.setInt(5, 5);
            ps.setInt(6, 0);
            ps.setInt(7, 0);
            ps.setInt(8, 5);
            ps.setLong(9, System.currentTimeMillis() + 24 * 60 * 60 * 1000);
            ps.setBoolean(10, false);
            ps.execute();
            ps.close();
//            }

            ps = con.prepareStatement("INSERT INTO inventoryslot (characterid, `equip`, `use`, `setup`, `etc`, `cash`) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, chr.id);
            ps.setByte(2, (byte) 24); // Eq
            ps.setByte(3, (byte) 24); // Use
            ps.setByte(4, (byte) 24); // Setup
            ps.setByte(5, (byte) 24); // ETC
            ps.setByte(6, (byte) 96); // Cash
            ps.execute();
            ps.close();

            ps = con.prepareStatement("INSERT INTO mountdata (characterid, `Level`, `Exp`, `Fatigue`) VALUES (?, ?, ?, ?)");
            ps.setInt(1, chr.id);
            ps.setByte(2, (byte) 1);
            ps.setInt(3, 0);
            ps.setByte(4, (byte) 0);
            ps.execute();
            ps.close();

            final int[] array1 = {47, 46, 41, 40, 43, 37, 39, 38, 33, 44, 45, 60, 61, 62, 63, 56, 57, 59, 48, 50, 35, 34, 31, 17, 7, 6, 5, 65, 4, 64, 3, 16, 19, 18, 29, 26, 27, 24, 25, 23, 20, 21, 2};
            final int[] array2 = {4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 6, 6, 6, 6, 5, 5, 6, 4, 4, 4, 4, 4, 4, 4, 4, 4, 6, 4, 6, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4, 4, 4};
            final int[] array3 = {31, 6, 22, 16, 9, 3, 26, 20, 25, 50, 51, 101, 102, 103, 104, 53, 54, 100, 29, 7, 11, 17, 2, 5, 28, 23, 18, 106, 13, 105, 12, 8, 4, 0, 52, 14, 15, 24, 19, 1, 27, 30, 10};

            ps = con.prepareStatement("INSERT INTO keymap (characterid, `key`, `type`, `action`) VALUES (?, ?, ?, ?)");
            ps.setInt(1, chr.id);
            for (int i = 0; i < array1.length; i++) {
                ps.setInt(2, array1[i]);
                ps.setInt(3, array2[i]);
                ps.setInt(4, array3[i]);
                ps.execute();
            }
            ps.close();

            List<Pair<Item, MapleInventoryType>> listing = new ArrayList<>();
            for (final MapleInventory iv : chr.inventory) {
                for (final Item item : iv.list()) {
                    listing.add(new Pair<>(item, iv.getType()));
                }
            }
            ItemLoader.INVENTORY.saveItems(listing, con, chr.id);

            con.commit();
        } catch (Exception e) {
            FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, e);
            e.printStackTrace();
            System.err.println("[charsave] Error saving character data");
            try {
                con.rollback();
            } catch (SQLException ex) {
                FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, ex);
                ex.printStackTrace();
                System.err.println("[charsave] Error Rolling Back");
            }
        } finally {
            try {
                if (pse != null) {
                    pse.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                con.setAutoCommit(true);
                con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            } catch (SQLException e) {
                FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, e);
                e.printStackTrace();
                System.err.println("[charsave] Error going back to autocommit mode");
            }
        }
    }

    public static void deleteWhereCharacterId(Connection con, String sql, int id) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
    }

    public static void deleteWhereCharacterId_NoLock(Connection con, String sql, int id) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ps.execute();
        ps.close();
    }

    public static int rands(int lbound, int ubound) {
        return (int) ((Math.random() * (ubound - lbound + 1)) + lbound);
    }

    public static boolean ban(String id, String reason, boolean accountId, int gmlevel, boolean hellban) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps;
            if (id.matches("/[0-9]{1,3}\\..*")) {
                ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
                ps.setString(1, id);
                ps.execute();
                ps.close();
                return true;
            }
            if (accountId) {
                ps = con.prepareStatement("SELECT id FROM accounts WHERE name = ?");
            } else {
                ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
            }
            boolean ret = false;
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int z = rs.getInt(1);
                PreparedStatement psb = con.prepareStatement("UPDATE accounts SET banned = 1, banreason = ? WHERE id = ? AND gm < ?");
                psb.setString(1, reason);
                psb.setInt(2, z);
                psb.setInt(3, gmlevel);
                psb.execute();
                psb.close();

                if (gmlevel > 100) { //admin ban
                    PreparedStatement psa = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
                    psa.setInt(1, z);
                    ResultSet rsa = psa.executeQuery();
                    if (rsa.next()) {
                        String sessionIP = rsa.getString("sessionIP");
                        if (sessionIP != null && sessionIP.matches("/[0-9]{1,3}\\..*")) {
                            PreparedStatement psz = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
                            psz.setString(1, sessionIP);
                            psz.execute();
                            psz.close();
                        }
                        if (rsa.getString("macs") != null) {
                            String[] macData = rsa.getString("macs").split(", ");
                            if (macData.length > 0) {
                                MapleClient.banMacs(macData);
                            }
                        }
                        if (hellban) {
                            PreparedStatement pss = con.prepareStatement("UPDATE accounts SET banned = 1, banreason = ? WHERE email = ?" + (sessionIP == null ? "" : " OR SessionIP = ?"));
                            pss.setString(1, reason);
                            pss.setString(2, rsa.getString("email"));
                            if (sessionIP != null) {
                                pss.setString(3, sessionIP);
                            }
                            pss.execute();
                            pss.close();
                        }
                    }
                    rsa.close();
                    psa.close();
                }
                ret = true;
            }
            rs.close();
            ps.close();
            return ret;
        } catch (SQLException ex) {
            System.err.println("Error while banning" + ex);
        }
        return false;
    }

    public static MapleCharacter getOnlineCharacterByName(int world, String name) {
        MapleCharacter chr = null;
        if (World.Find.findChannel(name) >= 1) {
            chr = ChannelServer.getInstance(World.Find.findChannel(name)).getPlayerStorage().getCharacterByName(name);
            if (chr != null) {
                return chr;
            }
        }
        return null;
    }

    public static int getCharacterIdByName(int world, String name) {
        int id = -1;
        MapleCharacter chr = getOnlineCharacterByName(world, name);
        if (chr != null) {
            return chr.getId();
        }
        try {
            PreparedStatement ps = null;
            Connection con = DatabaseConnection.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs;
            ps = con.prepareStatement("select id from characters where name = ?");
            ps.setString(1, name);
            rs = ps.executeQuery();
            while (rs.next()) {
                id = rs.getInt("id");
            }
            ps.close();
            rs.close();
        } catch (Exception ex) {
        }
        return id;
    }

    public static MapleCharacter getCharacterByName(String name) {
        MapleCharacter chr = getOnlineCharacterByName(name);
        if (chr != null) {
            return chr;
        }
        int cid = getCharacterIdByName(name);
        return cid == -1 ? null : MapleCharacter.loadCharFromDB(cid, new MapleClient(null, null, null), true);
    }

    public static MapleCharacter getOnlineCharacterByName(String name) {
        MapleCharacter chr = null;
        if (World.Find.findChannel(name) >= 1) {
            chr = ChannelServer.getInstance(World.Find.findChannel(name)).getPlayerStorage().getCharacterByName(name);
            if (chr != null) {
                return chr;
            }
        }

        return null;
    }

    public static int getCharacterIdByName(String name) {
        int id = -1;
        MapleCharacter chr = getOnlineCharacterByName(name);
        if (chr != null) {
            return chr.getId();
        }
        try {
            PreparedStatement ps = null;
            Connection con = DatabaseConnection.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs;
            ps = con.prepareStatement("select id from characters where name = ?");
            ps.setString(1, name);
            rs = ps.executeQuery();
            while (rs.next()) {
                id = rs.getInt("id");
            }
            ps.close();
            rs.close();
        } catch (Exception ex) {
        }
        return id;
    }

    public static MapleCharacter getCharacterById(int cid) {
        MapleCharacter chr = getOnlineCharacterById(cid);
        if (chr != null) {
            return chr;
        }
        String name = getCharacterNameById(cid);
        return name == null ? null : MapleCharacter.loadCharFromDB(cid, new MapleClient(null, null, null), true);
    }

    public static String getCharacterNameById(int id) {
        String name = null;
        MapleCharacter chr = getOnlineCharacterById(id);
        if (chr != null) {
            return chr.getName();
        }
        try {
            PreparedStatement ps = null;
            Connection con = DatabaseConnection.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs;
            ps = con.prepareStatement("select name from characters where id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                name = rs.getString("name");
            }
            ps.close();
            rs.close();
        } catch (Exception ex) {
        }
        return name;
    }

    public static MapleCharacter getOnlineCharacterById(int cid) {
        MapleCharacter chr = null;
        if (World.Find.findChannel(cid) >= 1) {
            chr = ChannelServer.getInstance(World.Find.findChannel(cid)).getPlayerStorage().getCharacterById(cid);
            if (chr != null) {
                return chr;
            }
        }

        return null;
    }

    public void ReconstructChr(final MapleCharacter player, final MapleClient client) {
        player.setClient(client);
        client.setTempIP(player.getOneTempValue("Transfer", "TempIP"));
        client.setAccountName(player.getOneTempValue("Transfer", "AccountName"));
        final MapleMapFactory mapFactory = ChannelServer.getInstance(client.getChannel()).getMapFactory();
        player.map = mapFactory.getMap(player.map.getId());
        if (player.map == null) { //char is on a map that doesn't exist warp it to spinel forest
            player.map = mapFactory.getMap(950000100);
        } else {
            if (player.map.getForcedReturnId() != 999999999 && player.map.getForcedReturnMap() != null) {
                player.map = player.map.getForcedReturnMap();
                if (player.map.getForcedReturnId() == 4000000) {
                    player.initialSpawnPoint = 0;
                }
            }
        }
        MaplePortal portal = player.map.getPortal(player.initialSpawnPoint);
        if (portal == null) {
            portal = player.map.getPortal(0); // char is on a spawnpoint that doesn't exist - select the first spawnpoint instead
            player.initialSpawnPoint = 0;
        }
        player.setPosition(portal.getPosition());
        //        final int messengerid = ct.messengerid;
        //        if (messengerid > 0) {
        //            player.messenger = World.Messenger.getMessenger(messengerid);
        //        }
    }

    public void saveToDB(boolean dc, boolean fromcs) {
//        if (getClient().getCloseSession()) {
//            return;
//        }
        if (isClone()) {
            return;
        }
        Connection con = DatabaseConnection.getConnection();

        PreparedStatement ps = null;
        PreparedStatement pse = null;
        ResultSet rs = null;

        try {
            World.addPlayerSaving(accountid);
            saveLock.lock();
            con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            con.setAutoCommit(false);

            ps = con.prepareStatement("UPDATE characters SET level = ?, fame = ?, str = ?, dex = ?, luk = ?, `int` = ?, exp = ?, hp = ?, mp = ?, maxhp = ?, maxmp = ?, sp = ?, ap = ?, gm = ?, skincolor = ?, gender = ?, job = ?, hair = ?, face = ?, demonMarking = ?, map = ?, meso = ?, hpApUsed = ?, spawnpoint = ?, party = ?, buddyCapacity = ?, pets = ?, subcategory = ?, marriageId = ?, currentrep = ?, totalrep = ?, gachexp = ?, fatigue = ?, charm = ?, charisma = ?, craft = ?, insight = ?, sense = ?, will = ?, totalwins = ?, totallosses = ?, pvpExp = ?, pvpPoints = ?, reborns = ?, apstorage = ?, honourExp = ?, honourLevel = ?, friendshippoints = ?, friendshiptoadd = ?, pee = ?, chronosphere = ?, cschronosphere = ?, name = ? WHERE id = ?", DatabaseConnection.RETURN_GENERATED_KEYS);
            ps.setInt(1, level);
            ps.setInt(2, fame);
            ps.setShort(3, stats.getStr());
            ps.setShort(4, stats.getDex());
            ps.setShort(5, stats.getLuk());
            ps.setShort(6, stats.getInt());
            ps.setInt(7, (level >= 200 || (GameConstants.isKOC(job) && level >= 200)) && !isIntern() ? 0 : exp);
            ps.setInt(8, stats.getHp() < 1 ? 50 : stats.getHp());
            ps.setInt(9, stats.getMp());
            ps.setInt(10, stats.getMaxHp());
            ps.setInt(11, stats.getMaxMp());
            final StringBuilder sps = new StringBuilder();
            for (int i = 0; i < remainingSp.length; i++) {
                sps.append(remainingSp[i]);
                sps.append(",");
            }
            final String sp = sps.toString();
            ps.setString(12, sp.substring(0, sp.length() - 1));
            ps.setShort(13, remainingAp);
            ps.setByte(14, gmLevel);
            ps.setByte(15, skinColor);
            ps.setByte(16, gender);
            ps.setShort(17, job);
            ps.setInt(18, hair);
            ps.setInt(19, face);
            ps.setInt(20, demonMarking);
            if (!fromcs && map != null) {
                if (map.getForcedReturnId() != 999999999 && map.getForcedReturnMap() != null) {
                    ps.setInt(21, map.getForcedReturnId());
                } else {
                    ps.setInt(21, stats.getHp() < 1 ? map.getReturnMapId() : map.getId());
                }
            } else {
                ps.setInt(21, mapid);
            }
            ps.setInt(22, meso);
            ps.setShort(23, hpApUsed);
            if (map == null) {
                ps.setByte(24, (byte) 0);
            } else {
                final MaplePortal closest = map.findClosestSpawnpoint(getTruePosition());
                ps.setByte(24, (byte) (closest != null ? closest.getId() : 0));
            }
            ps.setInt(25, party == null ? -1 : party.getId());
            ps.setShort(26, buddylist.getCapacity());
            final StringBuilder petz = new StringBuilder();
            int petLength = 0;
            for (final MaplePet pet : pets) {
                if (pet.getSummoned()) {
                    pet.saveToDb();
                    petz.append(pet.getInventoryPosition());
                    petz.append(",");
                    petLength++;
                }
            }
            while (petLength < 3) {
                petz.append("-1,");
                petLength++;
            }
            final String petstring = petz.toString();
            ps.setString(27, petstring.substring(0, petstring.length() - 1));
            ps.setByte(28, subcategory);
            ps.setInt(29, marriageId);
            ps.setInt(30, currentrep);
            ps.setInt(31, totalrep);
            ps.setInt(32, gachexp);
            ps.setShort(33, fatigue);
            ps.setInt(34, traits.get(MapleTraitType.charm).getTotalExp());
            ps.setInt(35, traits.get(MapleTraitType.charisma).getTotalExp());
            ps.setInt(36, traits.get(MapleTraitType.craft).getTotalExp());
            ps.setInt(37, traits.get(MapleTraitType.insight).getTotalExp());
            ps.setInt(38, traits.get(MapleTraitType.sense).getTotalExp());
            ps.setInt(39, traits.get(MapleTraitType.will).getTotalExp());
            ps.setInt(40, totalWins);
            ps.setInt(41, totalLosses);
            ps.setInt(42, pvpExp);
            ps.setInt(43, pvpPoints);
            /*Start of Custom Features*/
            ps.setInt(44, reborns);
            ps.setInt(45, apstorage);
            ps.setInt(46, honourExp);
            ps.setInt(47, honorLevel);
            ps.setString(48, friendshippoints[0] + "," + friendshippoints[1] + "," + friendshippoints[2] + "," + friendshippoints[3]);
            ps.setInt(49, friendshiptoadd);
            ps.setInt(50, pee);
            ps.setInt(51, chronosphere);
            ps.setInt(52, cschronosphere);
            /*End of Custom Features*/
            ps.setString(53, name);
            ps.setInt(54, id);
            if (ps.executeUpdate() < 1) {
                ps.close();
                throw new DatabaseException("Character not in database (" + id + ")");
            }
            ps.close();
            deleteWhereCharacterId(con, "DELETE FROM stolen WHERE characterid = ?");
            for (Pair<Integer, Boolean> st : stolenSkills) {
                ps = con.prepareStatement("INSERT INTO stolen (characterid, skillid, chosen) VALUES (?, ?, ?)");
                ps.setInt(1, id);
                ps.setInt(2, st.left);
                ps.setInt(3, st.right ? 1 : 0);
                ps.execute();
                ps.close();
            }

            if (changed_skillmacros) {
                deleteWhereCharacterId(con, "DELETE FROM skillmacros WHERE characterid = ?");
                for (int i = 0; i < 5; i++) {
                    final SkillMacro macro = skillMacros[i];
                    if (macro != null) {
                        ps = con.prepareStatement("INSERT INTO skillmacros (characterid, skill1, skill2, skill3, name, shout, position) VALUES (?, ?, ?, ?, ?, ?, ?)");
                        ps.setInt(1, id);
                        ps.setInt(2, macro.getSkill1());
                        ps.setInt(3, macro.getSkill2());
                        ps.setInt(4, macro.getSkill3());
                        ps.setString(5, macro.getName());
                        ps.setInt(6, macro.getShout());
                        ps.setInt(7, i);
                        ps.execute();
                        ps.close();
                    }
                }
            }
            if (changed_pokemon) {
                ps = con.prepareStatement("DELETE FROM pokemon WHERE characterid = ? OR (accountid = ? AND active = 0)");
                ps.setInt(1, id);
                ps.setInt(2, accountid);
                ps.execute();
                ps.close();
                ps = con.prepareStatement("INSERT INTO pokemon (characterid, level, exp, monsterid, name, nature, active, accountid, itemid, gender, hpiv, atkiv, defiv, spatkiv, spdefiv, speediv, evaiv, acciv, ability) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                for (int i = 0; i < battlers.length; i++) {
                    final Battler macro = battlers[i];
                    if (macro != null) {
                        ps.setInt(1, id);
                        ps.setInt(2, macro.getLevel());
                        ps.setInt(3, macro.getExp());
                        ps.setInt(4, macro.getMonsterId());
                        ps.setString(5, macro.getName());
                        ps.setInt(6, macro.getNature().ordinal());
                        ps.setInt(7, 1);
                        ps.setInt(8, accountid);
                        ps.setInt(9, macro.getItem() == null ? 0 : macro.getItem().id);
                        ps.setByte(10, macro.getGender());
                        ps.setByte(11, macro.getIV(PokemonStat.HP));
                        ps.setByte(12, macro.getIV(PokemonStat.ATK));
                        ps.setByte(13, macro.getIV(PokemonStat.DEF));
                        ps.setByte(14, macro.getIV(PokemonStat.SPATK));
                        ps.setByte(15, macro.getIV(PokemonStat.SPDEF));
                        ps.setByte(16, macro.getIV(PokemonStat.SPEED));
                        ps.setByte(17, macro.getIV(PokemonStat.EVA));
                        ps.setByte(18, macro.getIV(PokemonStat.ACC));
                        ps.setByte(19, macro.getAbilityIndex());
                        ps.execute();
                    }
                }
                for (Battler macro : boxed) {
                    ps.setInt(1, id);
                    ps.setInt(2, macro.getLevel());
                    ps.setInt(3, macro.getExp());
                    ps.setInt(4, macro.getMonsterId());
                    ps.setString(5, macro.getName());
                    ps.setInt(6, macro.getNature().ordinal());
                    ps.setInt(7, 0);
                    ps.setInt(8, accountid);
                    ps.setInt(9, macro.getItem() == null ? 0 : macro.getItem().id);
                    ps.setByte(10, macro.getGender());
                    ps.setByte(11, macro.getIV(PokemonStat.HP));
                    ps.setByte(12, macro.getIV(PokemonStat.ATK));
                    ps.setByte(13, macro.getIV(PokemonStat.DEF));
                    ps.setByte(14, macro.getIV(PokemonStat.SPATK));
                    ps.setByte(15, macro.getIV(PokemonStat.SPDEF));
                    ps.setByte(16, macro.getIV(PokemonStat.SPEED));
                    ps.setByte(17, macro.getIV(PokemonStat.EVA));
                    ps.setByte(18, macro.getIV(PokemonStat.ACC));
                    ps.setByte(19, macro.getAbilityIndex());
                    ps.execute();
                }
                ps.close();
            }

            deleteWhereCharacterId(con, "DELETE FROM inventoryslot WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO inventoryslot (characterid, `equip`, `use`, `setup`, `etc`, `cash`) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, id);
            ps.setByte(2, getInventory(MapleInventoryType.EQUIP).getSlotLimit());
            ps.setByte(3, getInventory(MapleInventoryType.USE).getSlotLimit());
            ps.setByte(4, getInventory(MapleInventoryType.SETUP).getSlotLimit());
            ps.setByte(5, getInventory(MapleInventoryType.ETC).getSlotLimit());
            ps.setByte(6, getInventory(MapleInventoryType.CASH).getSlotLimit());
            ps.execute();
            ps.close();
            if (getTrade() != null && dc) {
                MapleTrade.cancelTrade(getTrade(), client, this);
            }
            saveInventory(con);

            if (changed_questinfo) {
                deleteWhereCharacterId(con, "DELETE FROM questinfo WHERE characterid = ?");
                ps = con.prepareStatement("INSERT INTO questinfo (`characterid`, `quest`, `customData`) VALUES (?, ?, ?)");
                ps.setInt(1, id);
                for (final Entry<Integer, String> q : questinfo.entrySet()) {
                    ps.setInt(2, q.getKey());
                    ps.setString(3, q.getValue());
                    ps.execute();
                }
                ps.close();
            }

            deleteWhereCharacterId(con, "DELETE FROM queststatus WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO queststatus (`queststatusid`, `characterid`, `quest`, `status`, `time`, `forfeited`, `customData`) VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)", DatabaseConnection.RETURN_GENERATED_KEYS);
            pse = con.prepareStatement("INSERT INTO queststatusmobs VALUES (DEFAULT, ?, ?, ?)");
            ps.setInt(1, id);
            for (final MapleQuestStatus q : quests.values()) {
                ps.setInt(2, q.getQuest().getId());
                ps.setInt(3, q.getStatus());
                ps.setInt(4, (int) (q.getCompletionTime() / 1000));
                ps.setInt(5, q.getForfeited());
                ps.setString(6, q.getCustomData());
                ps.execute();
                rs = ps.getGeneratedKeys();
                if (q.hasMobKills()) {
                    rs.next();
                    for (int mob : q.getMobKills().keySet()) {
                        pse.setInt(1, rs.getInt(1));
                        pse.setInt(2, mob);
                        pse.setInt(3, q.getMobKills(mob));
                        pse.execute();
                    }
                }
                rs.close();
            }
            ps.close();
            pse.close();

            if (changed_skills) {
                deleteWhereCharacterId(con, "DELETE FROM skills WHERE characterid = ?");
                ps = con.prepareStatement("INSERT INTO skills (characterid, skillid, skilllevel, masterlevel, expiration, teachId) VALUES (?, ?, ?, ?, ?, ?)");
                ps.setInt(1, id);

                for (final Entry<Skill, SkillEntry> skill : skills.entrySet()) {
                    if (GameConstants.isApplicableSkill(skill.getKey().getId())) { //do not save additional skills
                        ps.setInt(2, skill.getKey().getId());
                        ps.setInt(3, skill.getValue().skillevel);
                        ps.setByte(4, skill.getValue().masterlevel);
                        ps.setLong(5, skill.getValue().expiration);
                        ps.setInt(6, skill.getValue().teachId);
                        ps.execute();
                    }
                }
                ps.close();
            }

            deleteWhereCharacterId(con, "DELETE FROM coreauras WHERE cid = ?");
            ps = con.prepareStatement("INSERT INTO coreauras (cid, str, dex, `int`, luk, att, magic, total, expire, delay) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, id);
            ps.setInt(2, getCoreAura().getStr());
            ps.setInt(3, getCoreAura().getDex());
            ps.setInt(4, getCoreAura().getInt());
            ps.setInt(5, getCoreAura().getLuk());
            ps.setInt(6, getCoreAura().getAtt());
            ps.setInt(7, getCoreAura().getMagic());
            ps.setInt(8, getCoreAura().getTotal());
            ps.setLong(9, getCoreAura().getExpire());
            ps.setBoolean(10, getCoreAura().getDelay());
            ps.execute();
            ps.close();

            if (innerskill_changed) {
                if (innerSkills != null) {
                    deleteWhereCharacterId(con, "DELETE FROM inner_ability_skills WHERE player_id = ?");
                    ps = con.prepareStatement("INSERT INTO inner_ability_skills (player_id, skill_id, skill_level, max_level, rank, locked) VALUES (?, ?, ?, ?, ?, ?)");
                    ps.setInt(1, id);

                    for (int i = 0; i < innerSkills.size(); ++i) {
                        ps.setInt(2, innerSkills.get(i).getSkillId());
                        ps.setInt(3, innerSkills.get(i).getSkillLevel());
                        ps.setInt(4, innerSkills.get(i).getMaxLevel());
                        ps.setInt(5, innerSkills.get(i).getRank());
                        ps.setBoolean(6, innerSkills.get(i).isLocked());
                        ps.executeUpdate();
                    }
                    ps.close();
                }
            }

            List<MapleCoolDownValueHolder> cd = getCooldowns();
            if (dc && cd.size() > 0) {
                ps = con.prepareStatement("INSERT INTO skills_cooldowns (charid, SkillID, StartTime, length) VALUES (?, ?, ?, ?)");
                ps.setInt(1, getId());
                for (final MapleCoolDownValueHolder cooling : cd) {
                    ps.setInt(2, cooling.skillId);
                    ps.setLong(3, cooling.startTime);
                    ps.setLong(4, cooling.length);
                    ps.execute();
                }
                ps.close();
            }

            if (changed_savedlocations) {
                deleteWhereCharacterId(con, "DELETE FROM savedlocations WHERE characterid = ?");
                ps = con.prepareStatement("INSERT INTO savedlocations (characterid, `locationtype`, `map`) VALUES (?, ?, ?)");
                ps.setInt(1, id);
                for (final SavedLocationType savedLocationType : SavedLocationType.values()) {
                    if (savedLocations[savedLocationType.getValue()] != -1) {
                        ps.setInt(2, savedLocationType.getValue());
                        ps.setInt(3, savedLocations[savedLocationType.getValue()]);
                        ps.execute();
                    }
                }
                ps.close();
            }

            if (changed_achievements) {
                ps = con.prepareStatement("DELETE FROM achievements WHERE accountid = ?");
                ps.setInt(1, accountid);
                ps.executeUpdate();
                ps.close();
                ps = con.prepareStatement("INSERT INTO achievements(charid, achievementid, accountid) VALUES(?, ?, ?)");
                for (Integer achid : finishedAchievements) {
                    ps.setInt(1, id);
                    ps.setInt(2, achid);
                    ps.setInt(3, accountid);
                    ps.execute();
                }
                ps.close();
            }

            if (changed_reports) {
                deleteWhereCharacterId(con, "DELETE FROM reports WHERE characterid = ?");
                ps = con.prepareStatement("INSERT INTO reports VALUES(DEFAULT, ?, ?, ?)");
                for (Entry<ReportType, Integer> achid : reports.entrySet()) {
                    ps.setInt(1, id);
                    ps.setByte(2, achid.getKey().i);
                    ps.setInt(3, achid.getValue());
                    ps.execute();
                }
                ps.close();
            }

            if (buddylist.changed()) {
                deleteWhereCharacterId(con, "DELETE FROM buddies WHERE characterid = ?");
                ps = con.prepareStatement("INSERT INTO buddies (characterid, `buddyid`, `pending`) VALUES (?, ?, ?)");
                ps.setInt(1, id);
                for (BuddylistEntry entry : buddylist.getBuddies()) {
                    ps.setInt(2, entry.getCharacterId());
                    ps.setInt(3, entry.isVisible() ? 0 : 1);
                    ps.execute();
                }
                ps.close();
                buddylist.setChanged(false);
            }

            ps = con.prepareStatement("UPDATE accounts SET `ACash` = ?, `mPoints` = ?, `vpoints` = ? WHERE id = ?");
            ps.setInt(1, acash);
            ps.setInt(2, maplepoints);
            ps.setInt(3, vpoints);
            ps.setInt(4, client.getAccID());
            ps.executeUpdate();
            ps.close();

            if (storage != null) {
                storage.saveToDB();
            }
            if (cs != null) {
                cs.save();
            }
            PlayerNPC.updateByCharId(this);
            keylayout.saveKeys(id);
            mount.saveMount(id);
            if (monsterbook != null) {
                monsterbook.saveCards(accountid);
            }
            deleteWhereCharacterId(con, "DELETE FROM familiars WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO familiars (characterid, expiry, name, fatigue, vitality, familiar) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, id);
            for (MonsterFamiliar f : familiars.values()) {
                ps.setLong(2, f.getExpiry());
                ps.setString(3, f.getName());
                ps.setInt(4, f.getFatigue());
                ps.setByte(5, f.getVitality());
                ps.setInt(6, f.getFamiliar());
                ps.executeUpdate();
            }
            ps.close();

            deleteWhereCharacterId(con, "DELETE FROM imps WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO imps (characterid, itemid, closeness, fullness, state, level) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, id);
            for (int i = 0; i < imps.length; i++) {
                if (imps[i] != null) {
                    ps.setInt(2, imps[i].getItemId());
                    ps.setShort(3, imps[i].getCloseness());
                    ps.setShort(4, imps[i].getFullness());
                    ps.setByte(5, imps[i].getState());
                    ps.setByte(6, imps[i].getLevel());
                    ps.executeUpdate();
                }
            }
            ps.close();
            if (changed_wishlist) {
                deleteWhereCharacterId(con, "DELETE FROM wishlist WHERE characterid = ?");
                for (int i = 0; i < getWishlistSize(); i++) {
                    ps = con.prepareStatement("INSERT INTO wishlist(characterid, sn) VALUES(?, ?) ");
                    ps.setInt(1, getId());
                    ps.setInt(2, wishlist[i]);
                    ps.execute();
                    ps.close();
                }
            }
            if (changed_trocklocations) {
                deleteWhereCharacterId(con, "DELETE FROM trocklocations WHERE characterid = ?");
                for (int i = 0; i < rocks.length; i++) {
                    if (rocks[i] != 999999999) {
                        ps = con.prepareStatement("INSERT INTO trocklocations(characterid, mapid) VALUES(?, ?) ");
                        ps.setInt(1, getId());
                        ps.setInt(2, rocks[i]);
                        ps.execute();
                        ps.close();
                    }
                }
            }

            if (changed_regrocklocations) {
                deleteWhereCharacterId(con, "DELETE FROM regrocklocations WHERE characterid = ?");
                for (int i = 0; i < regrocks.length; i++) {
                    if (regrocks[i] != 999999999) {
                        ps = con.prepareStatement("INSERT INTO regrocklocations(characterid, mapid) VALUES(?, ?) ");
                        ps.setInt(1, getId());
                        ps.setInt(2, regrocks[i]);
                        ps.execute();
                        ps.close();
                    }
                }
            }
            if (changed_hyperrocklocations) {
                deleteWhereCharacterId(con, "DELETE FROM hyperrocklocations WHERE characterid = ?");
                for (int i = 0; i < hyperrocks.length; i++) {
                    if (hyperrocks[i] != 999999999) {
                        ps = con.prepareStatement("INSERT INTO hyperrocklocations(characterid, mapid) VALUES(?, ?) ");
                        ps.setInt(1, getId());
                        ps.setInt(2, hyperrocks[i]);
                        ps.execute();
                        ps.close();
                    }
                }
            }
            if (changed_extendedSlots) {
                deleteWhereCharacterId(con, "DELETE FROM extendedSlots WHERE characterid = ?");
                for (int i : extendedSlots) {
                    if (getInventory(MapleInventoryType.ETC).findById(i) != null) { //just in case
                        ps = con.prepareStatement("INSERT INTO extendedSlots(characterid, itemId) VALUES(?, ?) ");
                        ps.setInt(1, getId());
                        ps.setInt(2, i);
                        ps.execute();
                        ps.close();
                    }
                }
            }
            changed_wishlist = false;
            changed_trocklocations = false;
            changed_regrocklocations = false;
            changed_hyperrocklocations = false;
            changed_skillmacros = false;
            changed_savedlocations = false;
            changed_pokemon = false;
            changed_questinfo = false;
            changed_achievements = false;
            changed_extendedSlots = false;
            changed_skills = false;
            changed_reports = false;
            con.commit();
        } catch (Exception e) {
            FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, e);
            e.printStackTrace();
            System.err.println(MapleClient.getLogMessage(this, "[charsave] Error saving character data") + e);
            try {
                con.rollback();
            } catch (SQLException ex) {
                FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, ex);
                System.err.println(MapleClient.getLogMessage(this, "[charsave] Error Rolling Back") + e);
            }
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (pse != null) {
                    pse.close();
                }
                if (rs != null) {
                    rs.close();
                }
                con.setAutoCommit(true);
                con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                World.removePlayerSaving(accountid);
                saveLock.unlock();
            } catch (SQLException e) {
                FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, e);
                System.err.println(MapleClient.getLogMessage(this, "[charsave] Error going back to autocommit mode") + e);
            }
        }
    }

    private void deleteWhereCharacterId(Connection con, String sql) throws SQLException {
        deleteWhereCharacterId(con, sql, id);
    }

    public void saveInventory(final Connection con) throws SQLException {
        List<Pair<Item, MapleInventoryType>> listing = new ArrayList<>();
        for (final MapleInventory iv : inventory) {
            for (final Item item : iv.list()) {
                listing.add(new Pair<>(item, iv.getType()));
            }
        }
        if (con != null) {
            ItemLoader.INVENTORY.saveItems(listing, con, id);
        } else {
            ItemLoader.INVENTORY.saveItems(listing, id);
        }
    }

    public final PlayerStats getStat() {
        return stats;
    }

    public final void QuestInfoPacket(final tools.data.MaplePacketLittleEndianWriter mplew) {
        mplew.writeShort(questinfo.size()); // // Party Quest data (quest needs to be added in the quests list)

        for (final Entry<Integer, String> q : questinfo.entrySet()) {
            mplew.writeShort(q.getKey());
            mplew.writeMapleAsciiString(q.getValue() == null ? "" : q.getValue());
        }
    }

    public final void updateInfoQuest(final int questid, final String data) {
        questinfo.put(questid, data);
        changed_questinfo = true;
        client.sendPacket(InfoPacket.updateInfoQuest(questid, data));
    }

    public final String getInfoQuest(final int questid) {
        if (questinfo.containsKey(questid)) {
            return questinfo.get(questid);
        }
        return "";
    }

    public final int getNumQuest() {
        int i = 0;
        for (final MapleQuestStatus q : quests.values()) {
            if (q.getStatus() == 2 && !(q.isCustom())) {
                i++;
            }
        }
        return i;
    }

    public final byte getQuestStatus(final int quest) {
        final MapleQuest qq = MapleQuest.getInstance(quest);
        if (getQuestNoAdd(qq) == null) {
            return 0;
        }
        return getQuestNoAdd(qq).getStatus();
    }

    public final MapleQuestStatus getQuest(final MapleQuest quest) {
        if (!quests.containsKey(quest)) {
            return new MapleQuestStatus(quest, (byte) 0);
        }
        return quests.get(quest);
    }

    public final void setQuestAdd(final MapleQuest quest, final byte status, final String customData) {
        if (!quests.containsKey(quest)) {
            final MapleQuestStatus stat = new MapleQuestStatus(quest, status);
            stat.setCustomData(customData);
            quests.put(quest, stat);
        }
    }

    public final MapleQuestStatus getQuestNAdd(final MapleQuest quest) {
        if (!quests.containsKey(quest)) {
            final MapleQuestStatus status = new MapleQuestStatus(quest, (byte) 0);
            quests.put(quest, status);
            return status;
        }
        return quests.get(quest);
    }

    public final MapleQuestStatus getQuestNoAdd(final MapleQuest quest) {
        return quests.get(quest);
    }

    public final MapleQuestStatus getQuestRemove(final MapleQuest quest) {
        return quests.remove(quest);
    }

    public final void updateQuest(final MapleQuestStatus quest) {
        updateQuest(quest, false);
    }

    public final void updateQuest(final MapleQuestStatus quest, final boolean update) {
        quests.put(quest.getQuest(), quest);
        if (!(quest.isCustom())) {
            client.sendPacket(InfoPacket.updateQuest(quest));
            if (quest.getStatus() == 1 && !update) {
                client.sendPacket(CField.updateQuestInfo(this, quest.getQuest().getId(), quest.getNpc(), (byte) 10));
            }
        }
    }

    public final Map<Integer, String> getInfoQuest_Map() {
        return questinfo;
    }

    public final Map<MapleQuest, MapleQuestStatus> getQuest_Map() {
        return quests;
    }

    public Integer getBuffedValue(MapleBuffStat effect) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        return mbsvh == null ? null : mbsvh.value;
    }

    public final Integer getBuffedSkill_X(final MapleBuffStat effect) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return null;
        }
        return mbsvh.effect.getX();
    }

    public final Integer getBuffedSkill_Y(final MapleBuffStat effect) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return null;
        }
        return mbsvh.effect.getY();
    }

    public boolean isBuffFrom(MapleBuffStat stat, Skill skill) {
        final MapleBuffStatValueHolder mbsvh = effects.get(stat);
        if (mbsvh == null || mbsvh.effect == null) {
            return false;
        }
        return mbsvh.effect.isSkill() && mbsvh.effect.getSourceId() == skill.getId();
    }

    public int getBuffSource(MapleBuffStat stat) {
        final MapleBuffStatValueHolder mbsvh = effects.get(stat);
        return mbsvh == null ? -1 : mbsvh.effect.getSourceId();
    }

    public int getTrueBuffSource(MapleBuffStat stat) {
        final MapleBuffStatValueHolder mbsvh = effects.get(stat);
        return mbsvh == null ? -1 : (mbsvh.effect.isSkill() ? mbsvh.effect.getSourceId() : -mbsvh.effect.getSourceId());
    }

    public int getItemQuantity(int itemid, boolean checkEquipped) {
        int possesed = inventory[GameConstants.getInventoryType(itemid).ordinal()].countById(itemid);
        if (checkEquipped) {
            possesed += inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemid);
        }
        return possesed;
    }

    public void setBuffedValue(MapleBuffStat effect, int value) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return;
        }
        mbsvh.value = value;
    }

    public void setSchedule(MapleBuffStat effect, ScheduledFuture<?> sched) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return;
        }
        mbsvh.schedule.cancel(false);
        mbsvh.schedule = sched;
    }

    public Long getBuffedStarttime(MapleBuffStat effect) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        return mbsvh == null ? null : mbsvh.startTime;
    }

    public MapleStatEffect getStatForBuff(MapleBuffStat effect) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        return mbsvh == null ? null : mbsvh.effect;
    }

    public void doDragonBlood() {
        final MapleStatEffect bloodEffect = getStatForBuff(MapleBuffStat.DRAGONBLOOD);
        if (bloodEffect == null) {
            lastDragonBloodTime = 0;
            return;
        }
        prepareDragonBlood();
        if (stats.getHp() - bloodEffect.getX() <= 1) {
            cancelBuffStats(MapleBuffStat.DRAGONBLOOD);
        } else {
            addHP(-bloodEffect.getX());
            client.sendPacket(EffectPacket.showOwnBuffEffect(bloodEffect.getSourceId(), 7, getLevel(), bloodEffect.getLevel()));
            map.broadcastMessage(MapleCharacter.this, EffectPacket.showBuffeffect(getId(), bloodEffect.getSourceId(), 7, getLevel(), bloodEffect.getLevel()), false);
        }
    }

    public final boolean canBlood(long now) {
        return lastDragonBloodTime > 0 && lastDragonBloodTime + 4000 < now;
    }

    private void prepareDragonBlood() {
        lastDragonBloodTime = System.currentTimeMillis();
    }

    public void doRecovery() {
        MapleStatEffect bloodEffect = getStatForBuff(MapleBuffStat.RECOVERY);
        if (bloodEffect == null) {
            bloodEffect = getStatForBuff(MapleBuffStat.MECH_CHANGE);
            if (bloodEffect == null) {
                lastRecoveryTime = 0;
                return;
            } else if (bloodEffect.getSourceId() == 35121005) {
                prepareRecovery();
                if (stats.getMp() < bloodEffect.getU()) {
                    cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
                    cancelEffectFromBuffStat(MapleBuffStat.MECH_CHANGE);
                } else {
                    addMP(-bloodEffect.getU());
                }
            }
        } else {
            prepareRecovery();
            if (stats.getHp() >= stats.getCurrentMaxHp()) {
                cancelEffectFromBuffStat(MapleBuffStat.RECOVERY);
            } else {
                healHP(bloodEffect.getX());
            }
        }
    }

    public final boolean canRecover(long now) {
        return lastRecoveryTime > 0 && lastRecoveryTime + 5000 < now;
    }

    private void prepareRecovery() {
        lastRecoveryTime = System.currentTimeMillis();
    }

    public void startMapTimeLimitTask(int time, final MapleMap to) {
        if (time <= 0) { //jail
            time = 1;
        }
        client.sendPacket(CField.getClock(time));
        final MapleMap ourMap = getMap();
        time *= 1000;
        mapTimeLimitTask = MapTimer.getInstance().register(new Runnable() {

            @Override
            public void run() {
                if (ourMap.getId() == ServerConstants.JAIL) {
                    getQuestNAdd(MapleQuest.getInstance(GameConstants.JAIL_TIME)).setCustomData(String.valueOf(System.currentTimeMillis()));
                    getQuestNAdd(MapleQuest.getInstance(GameConstants.JAIL_QUEST)).setCustomData("0"); //release them!
                }
                changeMap(to, to.getPortal(0));
            }
        }, time, time);
    }

    public boolean canDOT(long now) {
        return lastDOTTime > 0 && lastDOTTime + 8000 < now;
    }

    public boolean hasDOT() {
        return dotHP > 0;
    }

    public void doDOT() {
        addHP(-(dotHP * 4));
        dotHP = 0;
        lastDOTTime = 0;
    }

    public void setDOT(int d, int source, int sourceLevel) {
        this.dotHP = d;
        addHP(-(dotHP * 4));
        map.broadcastMessage(CField.getPVPMist(id, source, sourceLevel, d));
        lastDOTTime = System.currentTimeMillis();
    }

    public void startFishingTask(final boolean VIP) {
        try {
            if (ServerConstants.isShutdown) {
                dropMessage(1, "伺服器即將關閉或者正在準備，所以無法正常使用釣魚。");
                client.sendPacket(CWvsContext.enableActions());
                cancelFishingTask();
                return;
            }
            final int time = GameConstants.getFishingTime(VIP, isGM());
            cancelFishingTask();

            fishing = EtcTimer.getInstance().register(new Runnable() { //no real reason for clone.

                @Override
                public void run() {
                    final boolean noreMulti = haveItem(2300000, 1, false, true);
                    final boolean vipMulti = haveItem(2300001, 1, false, true);
                    if (getcheck_FishingVip() ? !vipMulti : !noreMulti) {
                        client.sendPacket(CWvsContext.serverNotice(1, "沒有魚餌了，不能釣魚了QQ."));
                        cancelFishingTask();
                        return;
                    }

                    MapleInventoryManipulator.removeById(client, MapleInventoryType.USE, getcheck_FishingVip() ? 2300001 : 2300000, 1, false, false);

                    final int rewardType = FishingRewardFactory.getInstance().getNextRewardType();

                    switch (rewardType) {
                        case 0: // Meso
                            final int money = Randomizer.rand(getcheck_FishingVip() ? 15 : 10, getcheck_FishingVip() ? 300 : 150);
                            if (client.getPlayer().getMeso() >= (client.getPlayer().getMeso() + money)) {
                                client.sendPacket(CWvsContext.serverNotice(1, "由於身上的楓幣超出異常，請先整理後再開始釣魚！"));
                                cancelFishingTask();
                                return;
                            }
                            gainMeso(money, true);
                            client.sendPacket(UIPacket.fishingUpdate((byte) 1, money));
                            break;
                        case 1: // EXP
                            final int experi = Randomizer.nextInt(Math.abs(GameConstants.getExpNeededForLevel(level) / 800) + 1);
                            gainExp(getcheck_FishingVip() ? (experi * 3 / 2) : experi, true, false, true);
                            client.sendPacket(UIPacket.fishingUpdate((byte) 2, experi));
                            break;
                        default:
                            final FishingRewardFactory.FishingReward item = FishingRewardFactory.getInstance().getNextRewardItemId();
                            if (item != null) {
                                if (!MapleInventoryManipulator.checkSpace(client, item.getItemId(), 1, getName())) {
                                    client.sendPacket(CWvsContext.serverNotice(1, "你的背包已經滿了！"));
                                    cancelFishingTask();
                                    return;
                                }
                                MapleInventoryManipulator.addById(client, item.getItemId(), (short) 1, ItemConstants.isChair(item.getItemId()) ? MapleCharacter.this.getName() : null, null, item.getExpiration());
                                client.sendPacket(UIPacket.fishingUpdate((byte) 0, item.getItemId()));
                            }
                            break;
                    }
                    map.broadcastMessage(UIPacket.fishingCaught(id));
                }
            }, time, time);
        } catch (RejectedExecutionException ex) {
            System.err.println("釣魚系統: Timer[EtcTimer] 出現異常: " + ex.getLocalizedMessage());
        }
    }

    public void cancelMapTimeLimitTask() {
        if (mapTimeLimitTask != null) {
            mapTimeLimitTask.cancel(false);
            mapTimeLimitTask = null;
        }
    }

    public int getNeededExp() {
        return GameConstants.getExpNeededForLevel(level);
    }

    public void cancelFishingTask() {
        if (fishing != null && !fishing.isCancelled()) {
            fishing.cancel(false);
        }
    }

    public void registerEffect(MapleStatEffect effect, long starttime, ScheduledFuture<?> schedule, int from) {
        registerEffect(effect, starttime, schedule, effect.getStatups(), false, effect.getDuration(), from);
    }

    public void registerEffect(MapleStatEffect effect, long starttime, ScheduledFuture<?> schedule, Map<MapleBuffStat, Integer> statups, boolean silent, final int localDuration, final int cid) {
        if (effect.isHide() && isStaff()) {
            map.broadcastNONGMMessage(this, CField.removePlayerFromMap(getId()), false);
        } else if (effect.isDragonBlood()) {
            prepareDragonBlood();
        } else if (effect.isRecovery()) {
            prepareRecovery();
        } else if (effect.isBerserk()) {
            checkBerserk();
        } else if (effect.isMonsterRiding_()) {
            getMount().startSchedule();
        }
        int clonez = 0;
        for (Entry<MapleBuffStat, Integer> statup : statups.entrySet()) {
            if (statup.getKey() == MapleBuffStat.ILLUSION) {
                clonez = statup.getValue();
            }
            int value = statup.getValue();
            if (statup.getKey() == MapleBuffStat.MONSTER_RIDING) {
                if (effect.getSourceId() == 5221006) {
                    battleshipHP = maxBattleshipHP(effect.getSourceId()); //copy this as well
                }
                removeFamiliar();
            }
            effects.put(statup.getKey(), new MapleBuffStatValueHolder(effect, starttime, schedule, value, localDuration, cid));
        }
        if (clonez > 0) {
            int cloneSize = Math.max(getNumClones(), getCloneSize());
            if (clonez > cloneSize) { //how many clones to summon
                for (int i = 0; i < clonez - cloneSize; i++) { //1-1=0
                    cloneLook();
                }
            }
        }
        if (!silent) {
            stats.recalcLocalStats(this);
        }
        if (this.getDebugMessage()) {
            dropMessage(6, new StringBuilder().append("註冊一般BUFF效果 - 當前BUFF總數： ").append(effects.size()).append(" 技能： ").append(effect.getSourceId()).toString());
        }
        //System.out.println("Effect registered. Effect: " + effect.getSourceId());
    }

    public List<MapleBuffStat> getBuffStats(final MapleStatEffect effect, final long startTime) {
        final List<MapleBuffStat> bstats = new ArrayList<>();
        final Map<MapleBuffStat, MapleBuffStatValueHolder> allBuffs = new EnumMap<>(effects);
        for (Entry<MapleBuffStat, MapleBuffStatValueHolder> stateffect : allBuffs.entrySet()) {
            final MapleBuffStatValueHolder mbsvh = stateffect.getValue();
            if (mbsvh.effect.sameSource(effect) && (startTime == -1 || startTime == mbsvh.startTime || stateffect.getKey().canStack())) {
                bstats.add(stateffect.getKey());
            }
        }
        return bstats;
    }

    private boolean deregisterBuffStats(List<MapleBuffStat> stats) {
        int effectSize = effects.size();
        boolean clonez = false;
        List<MapleBuffStatValueHolder> effectsToCancel = new ArrayList<>(stats.size());
        for (MapleBuffStat stat : stats) {
            final MapleBuffStatValueHolder mbsvh = effects.remove(stat);
            if (mbsvh != null) {
                boolean addMbsvh = true;
                for (MapleBuffStatValueHolder contained : effectsToCancel) {
                    if (mbsvh.startTime == contained.startTime && contained.effect == mbsvh.effect) {
                        addMbsvh = false;
                    }
                }
                if (addMbsvh) {
                    effectsToCancel.add(mbsvh);
                }
                if (stat == MapleBuffStat.SUMMON || stat == MapleBuffStat.PUPPET || stat == MapleBuffStat.REAPER || stat == MapleBuffStat.BEHOLDER || stat == MapleBuffStat.DAMAGE_BUFF || stat == MapleBuffStat.RAINING_MINES || stat == MapleBuffStat.ANGEL_ATK) {
                    final int summonId = mbsvh.effect.getSourceId();
                    final List<MapleSummon> toRemove = new ArrayList<>();
                    visibleMapObjectsLock.writeLock().lock(); //We need to lock this later on anyway so do it now to prevent deadlocks.
                    summonsLock.writeLock().lock();
                    try {
                        for (MapleSummon summon : summons) {
                            if (summon.getSkill() == summonId || (summon.getSkill() == summonId && summon.getSkill() == 5321004) || (stat == MapleBuffStat.RAINING_MINES && summonId == 33101008) || (summonId == 35121009 && summon.getSkill() == 35121011) || ((summonId == 86 || summonId == 88 || summonId == 91) && summon.getSkill() == summonId + 999) || ((summonId == 1085 || summonId == 1087 || summonId == 1090 || summonId == 1179) && summon.getSkill() == summonId - 999)) { //removes bots n tots
                                for (MapleSummon subsummon : summons) {
                                    map.broadcastMessage(SummonPacket.removeSummon(subsummon, true));
                                    map.removeMapObject(subsummon);
                                    visibleMapObjects.remove(subsummon);
                                    toRemove.add(subsummon);
                                }
                                map.broadcastMessage(SummonPacket.removeSummon(summon, true));
                                map.removeMapObject(summon);
                                visibleMapObjects.remove(summon);
                                toRemove.add(summon);
                            }
                        }
                        for (MapleSummon s : toRemove) {
                            summons.remove(s);
                        }
                    } finally {
                        summonsLock.writeLock().unlock();
                        visibleMapObjectsLock.writeLock().unlock(); //lolwut
                    }
                    if (summonId == 3111005 || summonId == 3211005) {
                        cancelEffectFromBuffStat(MapleBuffStat.SPIRIT_LINK);
                    }
                } else if (stat == MapleBuffStat.DRAGONBLOOD) {
                    lastDragonBloodTime = 0;
                } else if (stat == MapleBuffStat.RECOVERY || mbsvh.effect.getSourceId() == 35121005) {
                    lastRecoveryTime = 0;
                } else if (stat == MapleBuffStat.HOMING_BEACON || stat == MapleBuffStat.ARCANE_AIM) {
                    linkMobs.clear();
                } else if (stat == MapleBuffStat.ILLUSION) {
                    disposeClones();
                    clonez = true;
                }
            }
        }
        int toRemoveSize = effectsToCancel.size();
        for (MapleBuffStatValueHolder cancelEffectCancelTasks : effectsToCancel) {
            if (getBuffStats(cancelEffectCancelTasks.effect, cancelEffectCancelTasks.startTime).isEmpty()) {
                if (cancelEffectCancelTasks.schedule != null) {
                    cancelEffectCancelTasks.schedule.cancel(false);
                }
            }
        }
        effectsToCancel.clear();
        boolean ok = effectSize - effects.size() == toRemoveSize;
        if (getDebugMessage()) {
            dropMessage(5, new StringBuilder().append("取消使用的Buff效果 - 取消前Buff數量: ").append(effectSize).append(" 目前Buff數量 ").append(effects.size()).append(" 取消的Buff數量: ").append(toRemoveSize).append(" 是否相同: ").append(ok).toString());
        }
        return clonez;
    }

    /**
     * @param effect
     * @param overwrite when overwrite is set no data is sent and all the
     *                  Buffstats in the StatEffect are deregistered
     * @param startTime
     */
    public void cancelEffect(final MapleStatEffect effect, final boolean overwrite, final long startTime) {
        if (effect == null) {
            return;
        }
        cancelEffect(effect, overwrite, startTime, effect.getStatups());
    }

    public void cancelEffect(final MapleStatEffect effect, final boolean overwrite, final long startTime, Map<MapleBuffStat, Integer> statups) {
        if (effect == null) {
            return;
        }
        List<MapleBuffStat> buffstats;
        if (!overwrite) {
            buffstats = getBuffStats(effect, startTime);
        } else {
            buffstats = new ArrayList<>(statups.keySet());
        }
        if (buffstats.size() <= 0) {
            if (effect.getSourceId() == 35121013) {
                SkillFactory.getSkill(35121005).getEffect(getTotalSkillLevel(35121005)).applyTo(this);
            }
            return;
        }
        if (effect.isInfinity() && getBuffedValue(MapleBuffStat.INFINITY) != null) { //before
            int duration = Math.max(effect.getDuration(), effect.alchemistModifyVal(this, effect.getDuration(), false));
            final long start = getBuffedStarttime(MapleBuffStat.INFINITY);
            duration += (int) ((start - System.currentTimeMillis()));
            if (duration > 0) {
                final int neworbcount = getBuffedValue(MapleBuffStat.INFINITY) + effect.getDamage();
                final Map<MapleBuffStat, Integer> stat = new EnumMap<>(MapleBuffStat.class);
                stat.put(MapleBuffStat.INFINITY, neworbcount);
                setBuffedValue(MapleBuffStat.INFINITY, neworbcount);
                client.sendPacket(BuffPacket.giveBuff(effect.getSourceId(), duration, stat, effect));
                addHP((int) (effect.getHpR() * this.stats.getCurrentMaxHp()));
                addMP((int) (effect.getMpR() * this.stats.getCurrentMaxMp(this.getJob())));
                setSchedule(MapleBuffStat.INFINITY, Timer.BuffTimer.getInstance().schedule(new CancelEffectAction(this, effect, start, stat), effect.alchemistModifyVal(this, 4000, false)));
                return;
            }
        }
        final boolean clonez = deregisterBuffStats(buffstats);
        if (effect.isMagicDoor()) {
            // remove for all on maps
            if (!getDoors().isEmpty()) {
                removeDoor();
                silentPartyUpdate();
            }
        } else if (effect.isMechDoor()) {
            if (!getMechDoors().isEmpty()) {
                removeMechDoor();
            }
        } else if (effect.isMonsterRiding_()) {
            getMount().cancelSchedule();
        } else if (effect.isMonsterRiding()) {
            cancelEffectFromBuffStat(MapleBuffStat.SOARING);
            cancelEffectFromBuffStat(MapleBuffStat.MECH_CHANGE);
        } else if (effect.isAranCombo()) {
            combo = 0;
        }
        // check if we are still logged in o.o
        cancelPlayerBuffs(buffstats, overwrite);
        if (!overwrite) {
            if ((isGM() && effect.isHide()) && client.getChannelServer().getPlayerStorage().getCharacterById(this.getId()) != null) { //Wow this is so fking hacky...
                map.broadcastMessage(this, CField.spawnPlayerMapobject(this), false);

                for (final MaplePet pet : pets) {
                    if (pet.getSummoned()) {
                        map.broadcastMessage(this, PetPacket.showPet(this, pet, false, false), false);
                    }
                }
                for (final WeakReference<MapleCharacter> chr : clones) {
                    if (chr.get() != null) {
                        map.broadcastMessage(chr.get(), CField.spawnPlayerMapobject(chr.get()), false);
                    }
                }
            }
            if (effect.isDivineBody() && isInvincible()) {
                setInvincible(false);
                dropMessage(6, "無敵已經關閉");
            }
        }

//        if (effect.getSourceId() == 35121013 && !overwrite) { //when siege 2 deactivates, missile re-activates
//            SkillFactory.getSkill(35121005).getEffect(getTotalSkillLevel(35121005)).applyTo(this);
//        }
        if (!clonez) {
            for (WeakReference<MapleCharacter> chr : clones) {
                if (chr.get() != null) {
                    chr.get().cancelEffect(effect, overwrite, startTime);
                }
            }
        }

        //System.out.println("Effect deregistered. Effect: " + effect.getSourceId());
    }

    public void cancelBuffStats(MapleBuffStat... stat) {
        List<MapleBuffStat> buffStatList = Arrays.asList(stat);
        deregisterBuffStats(buffStatList);
        cancelPlayerBuffs(buffStatList, false);
    }

    public void cancelEffectFromBuffStat(MapleBuffStat stat) {
        if (effects.get(stat) != null) {
            cancelEffect(effects.get(stat).effect, false, -1);
        }
    }

    public void cancelEffectFromBuffStat(MapleBuffStat stat, int from) {
        if (effects.get(stat) != null && effects.get(stat).cid == from) {
            cancelEffect(effects.get(stat).effect, false, -1);
        }
    }

    private void cancelPlayerBuffs(List<MapleBuffStat> buffstats, boolean overwrite) {
        boolean write = client != null && client.getChannelServer() != null && client.getChannelServer().getPlayerStorage().getCharacterById(getId()) != null;
        if (buffstats.contains(MapleBuffStat.HOMING_BEACON)) {
            client.sendPacket(BuffPacket.cancelHoming());
        } else {
            if (overwrite) {
                List<MapleBuffStat> z = new ArrayList<>();
                for (MapleBuffStat s : buffstats) {
                    if (s.canStack()) {
                        z.add(s);
                    }
                }
                if (z.size() > 0) {
                    buffstats = z;
                } else {
                    return; //don't write anything
                }
            } else if (write) {
                stats.recalcLocalStats(this);
            }
            client.sendPacket(BuffPacket.cancelBuff(buffstats));
            map.broadcastMessage(this, BuffPacket.cancelForeignBuff(getId(), buffstats), false);
        }
    }

    public void dispel() {
        if (!isHidden()) {
            final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());
            for (MapleBuffStatValueHolder mbsvh : allBuffs) {
                if (mbsvh.effect.isSkill() && mbsvh.schedule != null && !mbsvh.effect.isMorph() && !mbsvh.effect.isGmBuff() && !mbsvh.effect.isMonsterRiding() && !mbsvh.effect.isMechChange() && !mbsvh.effect.isEnergyCharge() && !mbsvh.effect.isAranCombo()) {
                    cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                }
            }
        }
    }

    public void dispelSkill(int skillid) {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());

        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.isSkill() && mbsvh.effect.getSourceId() == skillid) {
                cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                break;
            }
        }
    }

    public void dispelSummons() {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());

        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.getSummonMovementType() != null) {
                cancelEffect(mbsvh.effect, false, mbsvh.startTime);
            }
        }
    }

    public void dispelBuff(int skillid) {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());

        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.getSourceId() == skillid) {
                cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                break;
            }
        }
    }

    public void cancelAllBuffs_() {
        effects.clear();
    }

    public void cancelAllBuffs() {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());

        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            cancelEffect(mbsvh.effect, false, mbsvh.startTime);
        }
    }

    public void cancelMorphs() {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());

        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            switch (mbsvh.effect.getSourceId()) {
                case 5101007:
                case 5111005:
                case 5121003:
                case 13111005:
                case 15111002:
                    return; // Since we can't have more than 1, save up on loops
                default:
                    if (mbsvh.effect.isMorph()) {
                        cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                        continue;
                    }
            }
        }
    }

    public int getMorphState() {
        LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.isMorph()) {
                return mbsvh.effect.getSourceId();
            }
        }
        return -1;
    }

    public void silentGiveBuffs(List<PlayerBuffValueHolder> buffs) {
        if (buffs == null || buffs.isEmpty()) {
            return;
        }
        for (PlayerBuffValueHolder mbsvh : buffs) {
            mbsvh.effect.silentApplyBuff(this, mbsvh.startTime, mbsvh.localDuration, mbsvh.statup, mbsvh.cid);
        }
    }

    public List<PlayerBuffValueHolder> getAllBuffs() {
        final List<PlayerBuffValueHolder> ret = new ArrayList<>();
        final Map<Pair<Integer, Byte>, Integer> alreadyDone = new HashMap<>();
        final LinkedList<Entry<MapleBuffStat, MapleBuffStatValueHolder>> allBuffs = new LinkedList<>(effects.entrySet());
        for (Entry<MapleBuffStat, MapleBuffStatValueHolder> mbsvh : allBuffs) {
            final Pair<Integer, Byte> key = new Pair<>(mbsvh.getValue().effect.getSourceId(), mbsvh.getValue().effect.getLevel());
            if (alreadyDone.containsKey(key)) {
                ret.get(alreadyDone.get(key)).statup.put(mbsvh.getKey(), mbsvh.getValue().value);
            } else {
                alreadyDone.put(key, ret.size());
                final EnumMap<MapleBuffStat, Integer> list = new EnumMap<>(MapleBuffStat.class);
                list.put(mbsvh.getKey(), mbsvh.getValue().value);
                ret.add(new PlayerBuffValueHolder(mbsvh.getValue().startTime, mbsvh.getValue().effect, list, mbsvh.getValue().localDuration, mbsvh.getValue().cid));
            }
        }
        return ret;
    }

    public void cancelMagicDoor() {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());

        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.isMagicDoor()) {
                cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                break;
            }
        }
    }

    public int getSkillLevel(int skillid) {
        return getSkillLevel(SkillFactory.getSkill(skillid));
    }

    public int getTotalSkillLevel(int skillid) {
        return getTotalSkillLevel(SkillFactory.getSkill(skillid));
    }

    public final void handleEnergyCharge(final int skillid, final int targets) {
        final Skill echskill = SkillFactory.getSkill(skillid);
        final int skilllevel = getTotalSkillLevel(echskill);
        if (skilllevel > 0) {
            final MapleStatEffect echeff = echskill.getEffect(skilllevel);
            if (targets > 0) {
                if (getBuffedValue(MapleBuffStat.ENERGY_CHARGE) == null) {
                    echeff.applyEnergyBuff(this, true); // Infinity time
                } else {
                    Integer energyLevel = getBuffedValue(MapleBuffStat.ENERGY_CHARGE);
                    //TODO: bar going down
                    if (energyLevel < 10000) {
                        energyLevel += (echeff.getX() * targets);

                        client.sendPacket(EffectPacket.showOwnBuffEffect(skillid, 2, getLevel(), skilllevel));
                        map.broadcastMessage(this, EffectPacket.showBuffeffect(id, skillid, 2, getLevel(), skilllevel), false);

                        if (energyLevel >= 10000) {
                            energyLevel = 10000;
                        }
                        client.sendPacket(BuffPacket.giveEnergyChargeTest(energyLevel, echeff.getDuration() / 1000));
                        setBuffedValue(MapleBuffStat.ENERGY_CHARGE, energyLevel);
                    } else if (energyLevel == 10000) {
                        echeff.applyEnergyBuff(this, false); // One with time
                        setBuffedValue(MapleBuffStat.ENERGY_CHARGE, 10001);
                    }
                }
            }
        }
    }

    public final void handleBattleshipHP(int damage) {
        final MapleStatEffect effect = getStatForBuff(MapleBuffStat.MONSTER_RIDING);
        if (effect != null && effect.getSourceId() == 5221006) {
            battleshipHP -= damage;
            client.sendPacket(CField.skillCooldown(5221999, battleshipHP / 10));
            if (battleshipHP <= 0) {
                battleshipHP = 0;
                client.sendPacket(CField.skillCooldown(5221006, effect.getCooldown(this)));
                addCooldown(5221006, System.currentTimeMillis(), effect.getCooldown(this) * 1000);
                cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
            }
        }
    }

    public final void handleOrbgain() {
        int orbcount = getBuffedValue(MapleBuffStat.COMBO);
        Skill combo;
        Skill advcombo;

        switch (getJob()) {
            case 1110:
            case 1111:
            case 1112:
                combo = SkillFactory.getSkill(11111001);
                advcombo = SkillFactory.getSkill(11110005);
                break;
            default:
                combo = SkillFactory.getSkill(1111002);
                advcombo = SkillFactory.getSkill(1120003);
                break;
        }

        MapleStatEffect ceffect = null;
        int advComboSkillLevel = getTotalSkillLevel(advcombo);
        if (advComboSkillLevel > 0) {
            ceffect = advcombo.getEffect(advComboSkillLevel);
        } else if (getSkillLevel(combo) > 0) {
            ceffect = combo.getEffect(getTotalSkillLevel(combo));
        } else {
            return;
        }

        if (orbcount < ceffect.getX() + 1) {
            int neworbcount = orbcount + 1;
            if (advComboSkillLevel > 0 && ceffect.makeChanceResult()) {
                if (neworbcount < ceffect.getX() + 1) {
                    neworbcount++;
                }
            }
            EnumMap<MapleBuffStat, Integer> stat = new EnumMap<>(MapleBuffStat.class);
            stat.put(MapleBuffStat.COMBO, neworbcount);
            setBuffedValue(MapleBuffStat.COMBO, neworbcount);
            int duration = ceffect.getDuration();
            duration += (int) ((getBuffedStarttime(MapleBuffStat.COMBO) - System.currentTimeMillis()));

            client.sendPacket(BuffPacket.giveBuff(combo.getId(), duration, stat, ceffect));
            map.broadcastMessage(this, BuffPacket.giveForeignBuff(getId(), stat, ceffect), false);
        }
    }

    public void handleOrbconsume(int howmany) {
        Skill combo;

        switch (getJob()) {
            case 1110:
            case 1111:
            case 1112:
                combo = SkillFactory.getSkill(11111001);
                break;
            default:
                combo = SkillFactory.getSkill(1111002);
                break;
        }
        if (getSkillLevel(combo) <= 0) {
            return;
        }
        MapleStatEffect ceffect = getStatForBuff(MapleBuffStat.COMBO);
        if (ceffect == null) {
            return;
        }
        EnumMap<MapleBuffStat, Integer> stat = new EnumMap<>(MapleBuffStat.class);
        stat.put(MapleBuffStat.COMBO, Math.max(1, getBuffedValue(MapleBuffStat.COMBO) - howmany));
        setBuffedValue(MapleBuffStat.COMBO, Math.max(1, getBuffedValue(MapleBuffStat.COMBO) - howmany));
        int duration = ceffect.getDuration();
        duration += (int) ((getBuffedStarttime(MapleBuffStat.COMBO) - System.currentTimeMillis()));

        client.sendPacket(BuffPacket.giveBuff(combo.getId(), duration, stat, ceffect));
        map.broadcastMessage(this, BuffPacket.giveForeignBuff(getId(), stat, ceffect), false);
    }

    public void silentEnforceMaxHpMp() {
        stats.setMp(stats.getMp(), this);
        stats.setHp(stats.getHp(), true, this);
    }

    public void enforceMaxHpMp() {
        Map<MapleStat, Integer> statups = new EnumMap<>(MapleStat.class);
        if (stats.getMp() > stats.getCurrentMaxMp(this.getJob())) {
            stats.setMp(stats.getMp(), this);
            statups.put(MapleStat.MP, stats.getMp());
        }
        if (stats.getHp() > stats.getCurrentMaxHp()) {
            stats.setHp(stats.getHp(), this);
            statups.put(MapleStat.HP, stats.getHp());
        }
        if (statups.size() > 0) {
            client.sendPacket(CWvsContext.updatePlayerStats(statups, this));
        }
    }

    public MapleMap getMap() {
        return map;
    }

    public void setMap(int PmapId) {
        this.mapid = PmapId;
    }

    public void setMap(MapleMap newmap) {
        this.map = newmap;
    }

    public MonsterBook getMonsterBook() {
        return monsterbook;
    }

    public int getBossLog(String bossid) {
        Connection con = DatabaseConnection.getConnection();
        try {
            int ret_count;
            PreparedStatement ps;
            ps = con.prepareStatement("select count(*) from bosslog where characterid = ? and bossid = ? and lastattempt >= subtime(current_timestamp, '1 0:0:0.0')");
            ps.setInt(1, id);
            ps.setString(2, bossid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ret_count = rs.getInt(1);
                } else {
                    ret_count = -1;
                }
            }
            ps.close();
            return ret_count;
        } catch (Exception Ex) {
            //log.error("Error while read bosslog.", Ex);
            return -1;
        }
    }

    public void setBossLog(String bossid) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps;
            ps = con.prepareStatement("insert into bosslog (characterid, bossid) values (?,?)");
            ps.setInt(1, id);
            ps.setString(2, bossid);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Ex) {
            //   log.error("Error while insert bosslog.", Ex);
        }
    }

    public int getOneTimeLog(String log) {
        Connection con = DatabaseConnection.getConnection();
        try {
            int ret_count = 0;
            PreparedStatement ps;
            ps = con.prepareStatement("select count(*) from onetimelog where characterid = ? and log = ?");
            ps.setInt(1, id);
            ps.setString(2, log);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ret_count = rs.getInt(1);
            } else {
                ret_count = -1;
            }
            rs.close();
            ps.close();
            return ret_count;
        } catch (Exception Wx) {
            return -1;
        }
    }

    public void setOneTimeLog(String log) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps;
            ps = con.prepareStatement("insert into onetimelog (characterid, log) values (?,?)");
            ps.setInt(1, id);
            ps.setString(2, log);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Wx) {
        }
    }

    public void setPrizeLog(String bossid) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps;
            ps = con.prepareStatement("insert into prizelog (accid, bossid) values (?,?)");
            ps.setInt(1, getClient().getAccID());
            ps.setString(2, bossid);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Wx) {
        }
    }

    public int getPrizeLog(String bossid) {
        Connection con = DatabaseConnection.getConnection();
        try {
            int ret_count = 0;
            PreparedStatement ps;
            ps = con.prepareStatement("select count(*) from prizelog where accid = ? and bossid = ?");
            ps.setInt(1, getClient().getAccID());
            ps.setString(2, bossid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ret_count = rs.getInt(1);
            } else {
                ret_count = -1;
            }
            rs.close();
            ps.close();
            return ret_count;
        } catch (Exception Wx) {
            return -1;
        }
    }

    public void setAcLog(String bossid) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps;
            ps = con.prepareStatement("insert into aclog (accid, bossid) values (?,?)");
            ps.setInt(1, getClient().getAccID());
            ps.setString(2, bossid);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Wx) {
        }
    }

    public int getAcLog(String bossid) {
        Connection con = DatabaseConnection.getConnection();
        try {
            int ret_count = 0;
            PreparedStatement ps;
            ps = con.prepareStatement("select count(*) from aclog where accid = ? and bossid = ? and lastattempt >= subtime(current_timestamp, '1 0:0:0.0')");
            ps.setInt(1, getClient().getAccID());
            ps.setString(2, bossid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ret_count = rs.getInt(1);
            } else {
                ret_count = -1;
            }
            rs.close();
            ps.close();
            return ret_count;
        } catch (Exception Wx) {
            return -1;
        }
    }

    public int getGiftLog(String bossid) {
        Connection con1 = DatabaseConnection.getConnection();
        try {
            int ret_count = 0;
            PreparedStatement ps;
            ps = con1.prepareStatement("select count(*) from bosslog where accountid = ? and bossid = ? and lastattempt >= subtime(current_timestamp, '1 0:0:0.0')");
            ps.setInt(1, accountid);
            ps.setString(2, bossid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ret_count = rs.getInt(1);
            } else {
                ret_count = -1;
            }
            rs.close();
            ps.close();
            return ret_count;
        } catch (Exception Ex) {
            return -1;
        }
    }

    public int getChronosphereLog(String chronoLog) {
        Connection con = DatabaseConnection.getConnection();
        try {
            int ret_count;
            PreparedStatement ps;
            ps = con.prepareStatement("select count(*) from chronolog where characterid = ? and chronid = ? and lastattempt >= subtime(current_timestamp, '30 0:0:0.0')");
            ps.setInt(1, id);
            ps.setString(2, chronoLog);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ret_count = rs.getInt(1);
                } else {
                    ret_count = -1;
                }
            }
            ps.close();
            return ret_count;
        } catch (Exception Ex) {
            //log.error("Error while read bosslog.", Ex);
            return -1;
        }
    }

    public void setChronosphereLog(String chronoLog) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps;
            ps = con.prepareStatement("insert into chronolog (characterid, chronid) values (?,?)");
            ps.setInt(1, id);
            ps.setString(2, chronoLog);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Ex) {
            //   log.error("Error while insert bosslog.", Ex);
        }
    }

    public int getPee() {
        return pee;
    }

    public void setPee(int pee) {
        this.pee = pee;
        peeEffects();
    }

    public void addPee() {
        if (getMapId() != 910000000 && getHp() != 0 && !GameConstants.isSquadMap(getMapId())) {
            int peez = rands(5, 15);
            pee += peez;
            peeEffects();
        }
        if ((getMapId() == 809000201 && getGender() == 1 && getPee() >= 30) || (getMapId() == 809000101 && getGender() == 0 && getPee() >= 30)) {
            dropMessage("(自動尿尿)[身體]: 哇,多謝! 感覺更好了!");
            setPee(0);
            World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, getName() + " 剛剛去尿尿了!"));
        }
    }

    public void peeEffects() {
        if (getPee() >= 100) {
            changeMap(getClient().getChannelServer().getMapFactory().getMap(910000000));
            setPee(0);
            setHp(0);
            updateSingleStat(MapleStat.HP, getHp());
            World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "[公告]: " + getName() + " 因為長時間沒尿尿掛掉了."));

        } else if (getPee() >= 90) {
            setHp((getHp() / 10));
            updateSingleStat(MapleStat.HP, getHp());
            client.getPlayer().dropMessage("[身體]: 大哥! 我已經很急了! 拜託去一下廁所!");
        } else if (getPee() >= 80) {
            setHp((getHp() / 5));
            updateSingleStat(MapleStat.HP, getHp());
            client.getPlayer().dropMessage("[身體]: 阿~, 好重的傷害. 快去尿尿吧?");
        } else if (getPee() >= 70) {
            setHp((getHp() / 2));
            updateSingleStat(MapleStat.HP, getHp());
            client.getPlayer().dropMessage("[身體]: 我開始感覺到尿意了,差不多可以去廁所囉~.");
        } else if (getPee() >= 60) {
            client.getPlayer().dropMessage("[身體]: 大約一半的感覺.");
        } else if (getPee() >= 40) {
            client.getPlayer().dropMessage("[身體]: 感覺真好! 那一定是你的媽媽.");
        } else if (getPee() >= 20) {
            client.getPlayer().dropMessage("[身體]: 你肯定知道如何保持膀胱是空的!");
        } else if (getPee() >= 1) {
            client.getPlayer().dropMessage("[身體]: 感覺良好又乾燥. 目前為止還不錯.");

        }
    }

    public int getMapId() {
        if (map != null) {
            return map.getId();
        }
        return mapid;
    }

    public byte getInitialSpawnpoint() {
        return initialSpawnPoint;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public final boolean canHold(final int itemid) {
        return getInventory(GameConstants.getInventoryType(itemid)).getNextFreeSlot() > -1;
    }

    public final String getBlessOfFairyOrigin() {
        return this.BlessOfFairy_Origin;
    }

    public final String getBlessOfEmpressOrigin() {
        return this.BlessOfEmpress_Origin;
    }

    public final short getLevel() {
        return level;
    }

    public void setLevel(final short level) {
        this.level = (short) (level - 1);
    }

    public final int getFame() {
        return fame;
    }

    public void setFame(int fame) {
        this.fame = fame;
    }

    public final int getFallCounter() {
        return fallcounter;
    }

    public void setFallCounter(int fallcounter) {
        this.fallcounter = fallcounter;
    }

    public final MapleClient getClient() {
        return client;
    }

    public final void setClient(final MapleClient client) {
        this.client = client;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public short getRemainingAp() {
        return remainingAp;
    }

    public void setRemainingAp(short remainingAp) {
        this.remainingAp = remainingAp;
    }

    public int getRemainingSp() {
        return remainingSp[GameConstants.getSkillBook(job)]; //default
    }

    public void setRemainingSp(int remainingSp) {
        this.remainingSp[GameConstants.getSkillBook(job)] = remainingSp; //default
    }

    public int getRemainingSp(final int skillbook) {
        return remainingSp[skillbook];
    }

    public int[] getRemainingSps() {
        return remainingSp;
    }

    public int getRemainingSpSize() {
        int ret = 0;
        for (int i = 0; i < remainingSp.length; i++) {
            if (remainingSp[i] > 0) {
                ret++;
            }
        }
        return ret;
    }

    public short getHpApUsed() {
        return hpApUsed;
    }

    public void setHpApUsed(short hpApUsed) {
        this.hpApUsed = hpApUsed;
    }

    public boolean isHidden() {
        return getBuffSource(MapleBuffStat.DARKSIGHT) / 1000000 == 9;
    }

    @Override
    public byte getSkinColor() {
        return skinColor;
    }

    public void setSkinColor(byte skinColor) {
        this.skinColor = skinColor;
    }

    @Override
    public short getJob() {
        return job;
    }

    public void setJob(int j) {
        this.job = (short) j;
    }

    @Override
    public byte getGender() {
        return gender;
    }

    public void setGender(byte gender) {
        this.gender = gender;
    }

    @Override
    public int getHair() {
        return hair;
    }

    public void setHair(int hair) {
        this.hair = hair;
    }

    @Override
    public int getFace() {
        return face;
    }

    public void setFace(int face) {
        this.face = face;
    }

    @Override
    public int getDemonMarking() {
        return demonMarking;
    }

    public void setDemonMarking(int mark) {
        this.demonMarking = mark;
    }

    public Point getOldPosition() {
        return old;
    }

    public void setOldPosition(Point x) {
        this.old = x;
    }

    public void setRemainingSp(int remainingSp, final int skillbook) {
        this.remainingSp[skillbook] = remainingSp;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public void setInvincible(boolean invinc) {
        invincible = invinc;
        if (invincible) {
            SkillFactory.getSkill(1010).getEffect(1).applyTo(this);
        } else {
            dispelBuff(1010);
        }
    }

    public CheatTracker getCheatTracker() {
        return anticheat;
    }

    public BuddyList getBuddylist() {
        return buddylist;
    }

    public void addFame(int famechange) {
        this.fame += famechange;
        getTrait(MapleTraitType.charm).addLocalExp(famechange);
        if (this.fame >= 50) {
            finishAchievement(7);
        }
    }

    public void updateFame() {
        updateSingleStat(MapleStat.FAME, this.fame);
    }

    public int getStr() {
        return getStat().getStr();
    }

    public void setStr(int str) {
        stats.str = (short) str;
        stats.recalcLocalStats(false, this);
    }

    public int getInt() {
        return getStat().getInt();
    }

    public void setInt(int int_) {
        stats.int_ = (short) int_;
        stats.recalcLocalStats(false, this);
    }

    public int getLuk() {
        return getStat().getLuk();
    }

    public void setLuk(int luk) {
        stats.luk = (short) luk;
        stats.recalcLocalStats(false, this);
    }

    public int getDex() {
        return getStat().getDex();
    }

    public void setDex(int dex) {
        stats.dex = (short) dex;
        stats.recalcLocalStats(false, this);
    }

    public int getHp() {
        return getStat().getHp();
    }

    public void setHp(int amount) {
        getStat().setHp(amount, this);
    }

    public int getMp() {
        return getStat().getMp();
    }

    public void setMp(int amount) {
        getStat().setMp(amount, this);
    }

    public int getMaxHp() {
        return getStat().getMaxHp();
    }

    public void setMaxHp(int amount) {
        getStat().setMaxHp(amount, this);
    }

    public int getMaxMp() {
        return getStat().getMaxMp();
    }

    public void setMaxMp(int amount) {
        getStat().setMaxMp(amount, this);
    }

    public void changeMapBanish(final int mapid, final String portal, final String msg) {
        dropMessage(5, msg);
        final MapleMap map = client.getChannelServer().getMapFactory().getMap(mapid);
        changeMap(map, map.getPortal(portal));
    }

    public void changeMap(int map, int portal) {
        MapleMap warpMap = client.getChannelServer().getMapFactory().getMap(map);
        changeMap(warpMap, warpMap.getPortal(portal));
    }

    public void changeMap(final MapleMap to, final Point pos) {
        changeMapInternal(to, pos, CField.getWarpToMap(to, 0x80, this), null);
    }

    public void changeMap(final MapleMap to) {
        changeMapInternal(to, to.getPortal(0).getPosition(), CField.getWarpToMap(to, 0, this), to.getPortal(0));
    }

    public void changeMap(final MapleMap to, final MaplePortal pto) {
        changeMapInternal(to, pto.getPosition(), CField.getWarpToMap(to, pto.getId(), this), null);
    }

    public void changeMap(final MapleMap to, final MaplePortal pto, boolean changeMP) {
        changeMapInternal(to, pto.getPosition(), CField.getWarpToMap(to, pto.getId(), this), null, changeMP);
    }

    public void changeMapPortal(final MapleMap to, final MaplePortal pto) {
        changeMapInternal(to, pto.getPosition(), CField.getWarpToMap(to, pto.getId(), this), pto);
    }

    private void changeMapInternal(final MapleMap to, final Point pos, byte[] warpPacket, final MaplePortal pto) {
        changeMapInternal(to, pos, warpPacket, pto, false);
    }

    private void changeMapInternal(final MapleMap to, final Point pos, byte[] warpPacket, final MaplePortal pto, final boolean changeMP) {
        if (to == null) {
            return;
        }
        final int nowmapid = map.getId();
        if (eventInstance != null) {
            eventInstance.changedMap(this, to.getId());
        }
        final boolean pyramid = pyramidSubway != null;
        if (map.getId() == nowmapid) {
            client.sendPacket(warpPacket);
            final boolean shouldChange = !isClone() && client.getChannelServer().getPlayerStorage().getCharacterById(getId()) != null;
            final boolean shouldState = map.getId() == to.getId();
            if (shouldChange && shouldState) {
                to.setCheckStates(false);
            }
            map.removePlayer(this);
            if (shouldChange) {
                map = to;
                setPosition(pos);
                to.addPlayer(this);
                stats.relocHeal(this);
                if (shouldState) {
                    to.setCheckStates(true);
                }
            }
        }
        if (pyramid && pyramidSubway != null) { //checks if they had pyramid before AND after changing
            pyramidSubway.onChangeMap(this, to.getId());
        }
        if (changeMP) {
            this.updateSingleStat(MapleStat.MP, getStat().getMp());
        }
    }

    public void cancelChallenge() {
        if (challenge != 0 && client.getChannelServer() != null) {
            final MapleCharacter chr = client.getChannelServer().getPlayerStorage().getCharacterById(challenge);
            if (chr != null) {
                chr.dropMessage(6, getName() + " has denied your request.");
                chr.setChallenge(0);
            }
            dropMessage(6, "Denied the challenge.");
            challenge = 0;
        }
    }

    public void leaveMap(MapleMap map) {
        controlledLock.writeLock().lock();
        visibleMapObjectsLock.writeLock().lock();
        try {
            for (MapleMonster mons : controlled) {
                if (mons != null) {
                    mons.setController(null);
                    mons.setControllerHasAggro(false);
                    map.updateMonsterController(mons);
                }
            }
            controlled.clear();
            visibleMapObjects.clear();
        } finally {
            controlledLock.writeLock().unlock();
            visibleMapObjectsLock.writeLock().unlock();
        }
        if (chair != 0) {
            chair = 0;
        }
        clearLinkMid();
        cancelFishingTask();
        cancelChallenge();
        if (getBattle() != null) {
            getBattle().forfeit(this, true);
        }
        if (!getMechDoors().isEmpty()) {
            removeMechDoor();
        }
        if (getTrade() != null) {
            MapleTrade.cancelTrade(getTrade(), client, this);
        }
        if (isOperateStorage()) {
            setOperateStorage(false);
        }
        cancelMapTimeLimitTask();
    }

    public void changeJob(int newJob) {
        if (getBuffedValue(MapleBuffStat.SHADOWPARTNER) != null || getBuffedValue(MapleBuffStat.WATER_SHIELD) != null) {
            dropMessage(5, "若要轉生或轉職 ， 請先關閉 影分身/水之盾。");
        } else {
            try {
                cancelEffectFromBuffStat(MapleBuffStat.SHADOWPARTNER);
                if (MapleJob.is影武者(newJob)) {
                    subcategory = 1;
                } else if (MapleJob.is重砲指揮官(newJob)) {
                    subcategory = 2;
                } else if (MapleJob.盜賊.getId() != newJob) {
                    subcategory = 0;
                }
                if (MapleJob.isBeginner(job)) {
                    resetStats(4, 4, 4, 4);
                }
                this.job = (short) newJob;
                updateSingleStat(MapleStat.JOB, newJob);
                if (!GameConstants.isBeginnerJob(newJob) && newJob != MapleJob.隱忍.getId()) {
                    if (GameConstants.isSeparatedSp(newJob)) {
                        if (MapleJob.is幻影俠盜(job)) {
                            client.sendPacket(PhantomPacket.updateCardStack(0));
                            resetRunningStack();
                        }
                        int changeSp = 5;
                        if (!MapleJob.is影武者(newJob) && MapleJob.getJobGrade(newJob) >= 4) {
                            changeSp = 3;
                        } else if (newJob == MapleJob.影武者.getId()) {
                            changeSp = 3;
                        } else if (MapleJob.is龍魔導士(newJob) && MapleJob.getJobGrade(newJob) == 2 && MapleJob.getJobGrade(newJob) == 3) {
                            changeSp = 2;
                        }
                        // 職業技能補滿
                        if (newJob == MapleJob.中忍.getId()) {
                            changeSp += 15;
                        } else if (newJob == MapleJob.上忍.getId()) {
                            changeSp += 5;
                        }
                        if (changeSp > 0) {
                            remainingSp[GameConstants.getSkillBookByJob(newJob)] += changeSp;
                            client.sendPacket(InfoPacket.getSPMsg((byte) changeSp, (short) newJob));
                        }
                    } else {
                        remainingSp[GameConstants.getSkillBookByJob(newJob)]++;
                        if (MapleJob.getJobGrade(newJob) >= 4) {
                            remainingSp[GameConstants.getSkillBookByJob(newJob)] += 2;
                        }
                    }
                    if (MapleJob.getJobGrade(newJob) == 1 && level >= 10 && MapleJob.is龍魔導士(newJob)) {
                        MapleQuest.getInstance(22100).forceStart(this, 0, null);
                        MapleQuest.getInstance(22100).forceComplete(this, 0);
                        client.sendPacket(NPCPacket.getEvanTutorial("UI/tutorial/evan/14/0"));
                        dropMessage(5, "感覺小龍有話要說，按下小龍後對話看看。");
                    }
                    if (newJob == 2218) {
                        final Skill skil1 = SkillFactory.getSkill(80001000);
                        final Skill skil2 = SkillFactory.getSkill(20010022);
                        if (skil1 != null && getSkillLevel(skil1) <= 0 || skil2 != null && getSkillLevel(skil2) <= 0) {
                            dropMessage(-1, "恭喜獲得龍坐騎。");
                            changeSingleSkillLevel(skil1, skil1.getMaxLevel(), (byte) skil1.getMaxLevel());
                            changeSingleSkillLevel(skil2, skil2.getMaxLevel(), (byte) skil2.getMaxLevel());
                        }
                    }
                    updateSingleStat(MapleStat.AVAILABLESP, 0); // we don't care the value here
                }

                if (MapleJob.getJobGrade(newJob) >= 3 && level >= 70) { // 3rd job or higher. lucky for evans who get 80,
                    // 100, 120, 160 ap...
                    remainingAp += 5;
                    updateSingleStat(MapleStat.AVAILABLEAP, remainingAp);
                }
                int maxhp = stats.getMaxHp(), maxmp = stats.getMaxMp();
                switch (job) {
                    case 0: // 初心者
                    case 1000:// 皇家騎士團
                    case 3000:// 末日反抗軍
                        break;
                    case 800: // 管理員
                    case 900: // GM
                    case 910: // 超級GM
                    case 10000:// 神之子-新手
                    case 10100:// 神之子
                    case 10110:// 神之子
                    case 10111:// 神之子
                    case 10112:// 神之子
                        break;

                    case 3101:// 惡魔復仇者
                        maxhp += Randomizer.rand(300, 350);
                        break;
                    case 3120:// 惡魔復仇者
                        maxhp += Randomizer.rand(400, 450);
                        break;
                    case 3121:// 惡魔復仇者
                        maxhp += Randomizer.rand(200, 250);
                        break;
                    case 3122:// 惡魔復仇者
                        maxhp += Randomizer.rand(100, 150);
                        break;

                    case 3200: // 煉獄巫師
                        maxhp += Randomizer.rand(125, 150);
                        maxmp += Randomizer.rand(250, 300);
                        break;
                    case 3210: // 煉獄巫師
                        maxhp += Randomizer.rand(175, 200);
                        maxmp += Randomizer.rand(350, 400);
                        break;
                    case 3211: // 煉獄巫師
                        maxhp += Randomizer.rand(125, 150);
                        maxmp += Randomizer.rand(250, 300);
                        break;
                    case 3212: // 煉獄巫師
                        maxhp += Randomizer.rand(75, 100);
                        maxmp += Randomizer.rand(150, 200);
                        break;

                    case 100: // 劍士
                    case 1100: // 聖魂劍士
                    case 2100: // 狂狼勇士
                    case 4001: // 劍豪
                    case 4100: // 劍豪
                    case 5000: // 米哈逸
                    case 5100: // 米哈逸
                    case 6000: // 龍之守護者凱薩
                    case 6100: // 龍之守護者凱薩
                    case 13000:// 皮卡啾
                    case 13100:// 皮卡啾
                        maxmp += Randomizer.rand(100, 175);
                    case 3001: // 惡魔殺手
                    case 3100: // 惡魔殺手
                        maxhp += Randomizer.rand(200, 250);
                        break;

                    case 110: // 狂戰士
                    case 120: // 見習騎士
                    case 130: // 槍騎兵
                    case 1110: // 聖魂劍士
                    case 2110: // 狂郎勇士
                    case 4110: // 劍豪
                    case 5110: // 米哈逸
                    case 6110: // 龍之守護者凱薩
                    case 13110:// 皮卡啾
                        maxmp += Randomizer.rand(150, 175);
                    case 3110: // 惡魔殺手
                        maxhp += Randomizer.rand(300, 350);
                        break;

                    case 111: // 狂戰士
                    case 121: // 見習騎士
                    case 131: // 槍騎兵
                    case 1111: // 聖魂劍士
                    case 2111: // 狂郎勇士
                    case 4111: // 劍豪
                    case 5111: // 米哈逸
                    case 6111: // 龍之守護者凱薩
                    case 13111:// 皮卡啾
                        maxmp += Randomizer.rand(100, 125);
                    case 3111: // 惡魔殺手
                        maxhp += Randomizer.rand(200, 250);
                        break;
                    case 112: // 狂戰士
                    case 122: // 見習騎士
                    case 132: // 槍騎兵
                    case 1112: // 聖魂劍士
                    case 2112: // 狂郎勇士
                    case 4112: // 劍豪
                    case 5112: // 米哈逸
                    case 6112: // 龍之守護者凱薩
                    case 13112:// 皮卡啾
                        maxmp += Randomizer.rand(50, 75);
                    case 3112: // 惡魔殺手
                        maxhp += Randomizer.rand(100, 150);
                        break;
                    case 200: // 魔法師
                    case 1200: // 烈焰巫師
                    case 2001: // 龍魔導士
                    case 2200: // 龍魔導士
                    case 2210: // 龍魔導士
                    case 2211: // 龍魔導士
                    case 2004: // 夜光
                    case 2700: // 夜光
                    case 11000:// 幻獸師
                    case 11200:// 幻獸師
                        maxmp += Randomizer.rand(75, 90);
                    case 4002: // 陰陽師
                    case 4200: // 陰陽師
                        maxhp += Randomizer.rand(150, 180);
                        break;
                    case 210: // 火毒
                    case 220: // 冰雷
                    case 230: // 主教
                    case 1210: // 烈焰巫師
                    case 2212: // 龍魔島士
                    case 2213: // 龍魔島士
                    case 2710: // 夜光
                    case 11210:// 幻獸師
                        maxmp += Randomizer.rand(400, 450);
                    case 4210: // 陰陽師
                        maxmp += Randomizer.rand(200, 225);
                        break;

                    case 211: // 火毒
                    case 221: // 冰雷
                    case 231: // 主教
                    case 1211: // 烈焰巫師
                    case 2214: // 龍魔島士
                    case 2215: // 龍魔島士
                    case 2711: // 夜光
                    case 11211:// 幻獸師
                        maxmp += Randomizer.rand(300, 350);
                    case 4211: // 陰陽師
                        maxmp += Randomizer.rand(150, 175);
                        break;
                    case 212: // 火毒
                    case 222: // 冰雷
                    case 232: // 主教
                    case 1212: // 烈焰巫師
                    case 2216: // 龍魔島士
                    case 2217: // 龍魔島士
                    case 2218: // 龍魔島士
                    case 2712: // 夜光
                    case 11212:// 幻獸師
                        maxmp += Randomizer.rand(200, 250);
                    case 4212: // 陰陽師
                        maxhp += Randomizer.rand(100, 125);
                        break;

                    case 300: // 弓箭手
                    case 400: // 盜賊
                    case 500: // 海盜
                    case 501: // 重砲指揮官
                    case 508: // JETT
                    case 1300:// 破風使者
                    case 1400:// 暗夜行者
                    case 1500:// 閃雷悍將
                    case 2002:// 精靈遊俠
                    case 2300:// 精靈遊俠
                    case 2003:// 幻影俠盜
                    case 2400:// 幻影俠盜
                    case 2005:// 隱月
                    case 2500:// 隱月
                    case 3300:// 狂豹獵人
                    case 3500:// 機甲戰神
                    case 3002:// 傑諾
                    case 3600:// 傑諾
                        maxmp += Randomizer.rand(100, 125);
                    case 6001:// 天使破壞者
                    case 6500:// 天使破壞者
                        maxhp += Randomizer.rand(200, 250);
                        break;
                    case 310: // 獵人
                    case 320: // 奴弓手
                    case 410: // 刺客
                    case 420: // 俠盜
                    case 430: // 下忍
                    case 510: // 打手
                    case 520: // 槍手
                    case 530: // 重砲指揮官
                    case 570: // JETT
                    case 1310:// 破風使者
                    case 1410:// 暗夜行者
                    case 1510:// 閃雷悍將
                    case 2310:// 精靈遊俠
                    case 2410:// 幻影俠盜
                    case 2510:// 隱月
                    case 3310:// 狂豹獵人
                    case 3510:// 機甲戰神
                    case 3610:// 傑諾
                        maxmp += Randomizer.rand(150, 175);
                    case 6510:// 天使破壞者
                        maxhp += Randomizer.rand(300, 350);
                        break;
                    case 311: // 獵人
                    case 321: // 奴弓手
                    case 411: // 刺客
                    case 421: // 俠盜
                    case 431: // 下忍
                    case 511: // 打手
                    case 521: // 槍手
                    case 531: // 重砲指揮官
                    case 571: // JETT
                    case 1311:// 破風使者
                    case 1411:// 暗夜行者
                    case 1511:// 閃雷悍將
                    case 2311:// 精靈遊俠
                    case 2411:// 幻影俠盜
                    case 2511:// 隱月
                    case 3311:// 狂豹獵人
                    case 3511:// 機甲戰神
                    case 3611:// 傑諾
                        maxmp += Randomizer.rand(100, 125);
                    case 6511:// 天使破壞者
                        maxhp += Randomizer.rand(200, 250);
                        break;
                    case 312: // 獵人
                    case 322: // 奴弓手
                    case 412: // 刺客
                    case 422: // 俠盜
                    case 432: // 下忍
                    case 433: // 隱忍
                    case 434: // 影武者
                    case 512: // 打手
                    case 522: // 槍手
                    case 532: // 重砲指揮官
                    case 572: // JETT
                    case 1312:// 破風使者
                    case 1412:// 暗夜行者
                    case 1512:// 閃雷悍將
                    case 2312:// 精靈遊俠
                    case 2412:// 幻影俠盜
                    case 2512:// 隱月
                    case 3312:// 狂豹獵人
                    case 3512:// 機甲戰神
                    case 3612:// 傑諾
                        maxmp += Randomizer.rand(50, 75);
                    case 6512:// 天使破壞者
                        maxhp += Randomizer.rand(100, 150);
                        break;
                    default:
                        System.err.println("職業 " + MapleJob.getById(job).name() + " 未處理轉職HPMP增加");
                }
                if (maxhp >= 99999) {
                    maxhp = 99999;
                }
                if (maxmp >= 99999) {
                    maxmp = 99999;
                }
                if (GameConstants.isDemon(job)) {
                    maxmp = 10/*GameConstants.getMPByJob(job)*/;
                }
                stats.setInfo(maxhp, maxmp, maxhp, maxmp);
                Map<MapleStat, Integer> statup = new EnumMap<>(MapleStat.class);
                statup.put(MapleStat.MAXHP, maxhp);
                statup.put(MapleStat.MAXMP, maxmp);
                statup.put(MapleStat.HP, maxhp);
                statup.put(MapleStat.MP, maxmp);
                characterCard.recalcLocalStats(this);
                stats.recalcLocalStats(this);
                client.sendPacket(CWvsContext.updatePlayerStats(statup, this));
                map.broadcastMessage(this, EffectPacket.showForeignEffect(getId(), 12), false);
                silentPartyUpdate();
                guildUpdate();
                familyUpdate();
                if (dragon != null) {
                    map.broadcastMessage(CField.removeDragon(this.id));
                    dragon = null;
                }
                baseSkills();
                if (newJob >= 2200 && newJob <= 2218) { //make new
                    if (getBuffedValue(MapleBuffStat.MONSTER_RIDING) != null) {
                        cancelBuffStats(MapleBuffStat.MONSTER_RIDING);
                    }
                    makeDragon();
                }
                checkForceShield();
            } catch (Exception e) {
                FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, e); //all jobs throw errors :(
            }
        }
    }

    public void baseSkills() {
        Map<Skill, SkillEntry> list = new HashMap<>();
        if (GameConstants.getJobNumber(job) >= 3) { //third job.
            List<Integer> skills = SkillFactory.getSkillsByJob(job);
            if (skills != null) {
                for (int i : skills) {
                    final Skill skil = SkillFactory.getSkill(i);
                    if (skil != null && !skil.isInvisible() && skil.isFourthJob() && getSkillLevel(skil) <= 0 && getMasterLevel(skil) <= 0 && skil.getMasterLevel() > 0) {
                        list.put(skil, new SkillEntry((byte) 0, (byte) skil.getMasterLevel(), SkillFactory.getDefaultSExpiry(skil), -1)); //usually 10 master
                    } else if (skil != null && skil.getName() != null && skil.getName().contains("Maple Warrior") && getSkillLevel(skil) <= 0 && getMasterLevel(skil) <= 0) {
                        list.put(skil, new SkillEntry((byte) 0, (byte) 10, SkillFactory.getDefaultSExpiry(skil), -1)); //hackish
                    }
                }

            }
        }
        Skill skil;
        if (job >= 2211 && job <= 2218) { // evan fix magic guard
            skil = SkillFactory.getSkill(22111001);
            if (skil != null) {
                if (getSkillLevel(skil) <= 0) { // no total
                    list.put(skil, new SkillEntry((byte) 0, (byte) 20, -1, -1));
                }
            }
        }
        if (GameConstants.isMercedes(job)) {
            final int[] ss = {20021000, 20021001, 20020002, 20020022, 20020109, 20021110, 20020111, 20020112};
            for (int i : ss) {
                skil = SkillFactory.getSkill(i);
                if (skil != null) {
                    if (getSkillLevel(skil) <= 0) { // no total
                        list.put(skil, new SkillEntry((byte) 1, (byte) 1, -1, -1));
                    }
                }
            }
            skil = SkillFactory.getSkill(20021181);
            if (skil != null) {
                if (getSkillLevel(skil) <= 0) { // no total
                    list.put(skil, new SkillEntry((byte) -1, (byte) 0, -1, -1));
                }
            }
            skil = SkillFactory.getSkill(20021166);
            if (skil != null) {
                if (getSkillLevel(skil) <= 0) { // no total
                    list.put(skil, new SkillEntry((byte) -1, (byte) 0, -1, -1));
                }
            }
        }
        if (GameConstants.isDemon(job)) {
            final int[] ss1 = {30011000, 30011001, 30010002, 30010185, 30010112, 30010111, 30010110, 30010022, 30011109};
            for (int i : ss1) {
                skil = SkillFactory.getSkill(i);
                if (skil != null) {
                    if (getSkillLevel(skil) <= 0) { // no total
                        list.put(skil, new SkillEntry((byte) 1, (byte) 1, -1, -1));
                    }
                }
            }
            final int[] ss2 = {30011170, 30011169, 30011168, 30011167, 30010166, 30010184, 30010183, 30010186};
            for (int i : ss2) {
                skil = SkillFactory.getSkill(i);
                if (skil != null) {
                    if (getSkillLevel(skil) <= 0) { // no total
                        list.put(skil, new SkillEntry((byte) -1, (byte) -1, -1, -1));
                    }
                }
            }
        }
        if (!list.isEmpty()) {
            changeSkillsLevel(list);
        }
        //redemption for completed quests. holy fk. ex
        /*List<MapleQuestStatus> cq = getCompletedQuests();
        for (MapleQuestStatus q : cq) {
        for (MapleQuestAction qs : q.getQuest().getCompleteActs()) {
        if (qs.getType() == MapleQuestActionType.skill) {
        for (Pair<Integer, Pair<Integer, Integer>> skill : qs.getSkills()) {
        final Skill skil = SkillFactory.getSkill(skill.left);
        if (skil != null && getSkillLevel(skil) <= skill.right.left && getMasterLevel(skil) <= skill.right.right) {
        changeSkillLevel(skil, (byte) (int)skill.right.left, (byte) (int)skill.right.right);
        }
        }
        } else if (qs.getType() == MapleQuestActionType.item) { //skillbooks
        for (MapleQuestAction.QuestItem item : qs.getItems()) {
        if (item.itemid / 10000 == 228 && !haveItem(item.itemid,1)) { //skillbook
        //check if we have the skill
        final Map<String, Integer> skilldata = MapleItemInformationProvider.getInstance().getSkillStats(item.itemid);
        if (skilldata != null) {
        byte i = 0;
        Skill finalSkill = null;
        Integer skillID = 0;
        while (finalSkill == null) {
        skillID = skilldata.get("skillid" + i);
        i++;
        if (skillID == null) {
        break;
        }
        final Skill CurrSkill = SkillFactory.getSkill(skillID);
        if (CurrSkill != null && CurrSkill.canBeLearnedBy(job) && getSkillLevel(CurrSkill) <= 0 && getMasterLevel(CurrSkill) <= 0) {
        finalSkill = CurrSkill;
        }
        }
        if (finalSkill != null) {
        //may as well give the skill
        changeSkillLevel(finalSkill, (byte) 0, (byte)10);
        //MapleInventoryManipulator.addById(client, item.itemid, item.count);
        }
        }
        }
        }
        }
        }
        }*/

    }

    public void makeDragon() {
        dragon = new MapleDragon(this);
        map.broadcastMessage(CField.spawnDragon(dragon));
    }

    public MapleDragon getDragon() {
        return dragon;
    }

    public void setDragon(MapleDragon d) {
        this.dragon = d;
    }

    public void gainAp(short ap) {
        this.remainingAp += ap;
        updateSingleStat(MapleStat.AVAILABLEAP, this.remainingAp);
    }

    public void gainSP(int sp) {
        this.remainingSp[GameConstants.getSkillBook(job)] += sp; //default
        updateSingleStat(MapleStat.AVAILABLESP, 0); // we don't care the value here
        client.sendPacket(InfoPacket.getSPMsg((byte) sp, (short) job));
    }

    public void gainSP(int sp, final int skillbook) {
        this.remainingSp[skillbook] += sp; //default
        updateSingleStat(MapleStat.AVAILABLESP, 0); // we don't care the value here
        client.sendPacket(InfoPacket.getSPMsg((byte) sp, (short) 0));
    }

    public void resetSP(int sp) {
        for (int i = 0; i < remainingSp.length; i++) {
            this.remainingSp[i] = sp;
        }
        updateSingleStat(MapleStat.AVAILABLESP, 0); // we don't care the value here
    }

    public void resetAPSP() {
        resetSP(0);
        gainAp((short) -this.remainingAp);
    }

    public List<Integer> getProfessions() {
        List<Integer> prof = new ArrayList<>();
        for (int i = 9200; i <= 9204; i++) {
            if (getProfessionLevel(i * 10000) > 0) {
                prof.add(i);
            }
        }
        return prof;
    }

    public byte getProfessionLevel(int id) {
        int ret = getSkillLevel(id);
        if (ret <= 0) {
            return 0;
        }
        return (byte) ((ret >>> 24) & 0xFF); //the last byte
    }

    public short getProfessionExp(int id) {
        int ret = getSkillLevel(id);
        if (ret <= 0) {
            return 0;
        }
        return (short) (ret & 0xFFFF); //the first two byte
    }

    public boolean addProfessionExp(int id, int expGain) {
        int ret = getProfessionLevel(id);
        if (ret <= 0 || ret >= 10) {
            return false;
        }
        int newExp = getProfessionExp(id) + expGain;
        if (newExp >= GameConstants.getProfessionEXP(ret)) {
            //gain level
            changeProfessionLevelExp(id, ret + 1, newExp - GameConstants.getProfessionEXP(ret));
            int traitGain = (int) Math.pow(2, ret + 1);
            switch (id) {
                case 92000000:
                    traits.get(MapleTraitType.sense).addExp(traitGain, this);
                    break;
                case 92010000:
                    traits.get(MapleTraitType.will).addExp(traitGain, this);
                    break;
                case 92020000:
                case 92030000:
                case 92040000:
                    traits.get(MapleTraitType.craft).addExp(traitGain, this);
                    break;
            }
            return true;
        } else {
            changeProfessionLevelExp(id, ret, newExp);
            return false;
        }
    }

    public void changeProfessionLevelExp(int id, int level, int exp) {
        changeSingleSkillLevel(SkillFactory.getSkill(id), ((level & 0xFF) << 24) + (exp & 0xFFFF), (byte) 10);
    }

    public void changeSingleSkillLevel(final Skill skill, int newLevel, byte newMasterlevel) { //1 month
        if (skill == null) {
            return;
        }
        changeSingleSkillLevel(skill, newLevel, newMasterlevel, SkillFactory.getDefaultSExpiry(skill));
    }

    public void changeSingleSkillLevel(final Skill skill, int newLevel, byte newMasterlevel, long expiration) {
        final Map<Skill, SkillEntry> list = new HashMap<>();
        boolean hasRecovery = false, recalculate = false;
        if (changeSkillData(skill, newLevel, newMasterlevel, expiration)) { // no loop, only 1
            list.put(skill, new SkillEntry(newLevel, newMasterlevel, expiration, -1));
            if (GameConstants.isRecoveryIncSkill(skill.getId())) {
                hasRecovery = true;
            }
            if (skill.getId() < 80000000) {
                recalculate = true;
            }
        }
        if (list.isEmpty()) { // nothing is changed
            return;
        }
        client.sendPacket(CWvsContext.updateSkills(list));
        reUpdateStat(hasRecovery, recalculate);
    }

    public void changeSkillsLevel(final Map<Skill, SkillEntry> ss) {
        if (ss.isEmpty()) {
            return;
        }
        final Map<Skill, SkillEntry> list = new HashMap<>();
        boolean hasRecovery = false, recalculate = false;
        for (final Entry<Skill, SkillEntry> data : ss.entrySet()) {
            if (changeSkillData(data.getKey(), data.getValue().skillevel, data.getValue().masterlevel, data.getValue().expiration)) {
                list.put(data.getKey(), data.getValue());
                if (GameConstants.isRecoveryIncSkill(data.getKey().getId())) {
                    hasRecovery = true;
                }
                if (data.getKey().getId() < 80000000) {
                    recalculate = true;
                }
            }
        }
        if (list.isEmpty()) { // nothing is changed
            return;
        }
        client.sendPacket(CWvsContext.updateSkills(list));
        reUpdateStat(hasRecovery, recalculate);
    }

    private void reUpdateStat(boolean hasRecovery, boolean recalculate) {
        changed_skills = true;
        if (hasRecovery) {
            stats.relocHeal(this);
        }
        if (recalculate) {
            stats.recalcLocalStats(this);
        }
    }

    public boolean changeSkillData(final Skill skill, int newLevel, byte newMasterlevel, long expiration) {
        if (skill == null || (!GameConstants.isApplicableSkill(skill.getId()) && !GameConstants.isApplicableSkill_(skill.getId()))) {
            return false;
        }
        if (newLevel == 0 && newMasterlevel == 0) {
            if (skills.containsKey(skill)) {
                skills.remove(skill);
            } else {
                return false; //nothing happen
            }
        } else {
            skills.put(skill, new SkillEntry(newLevel, newMasterlevel, expiration, -1));
        }
        return true;
    }

    public void changeSkillLevel_Skip(Skill skil, int skilLevel, byte masterLevel) {
        final Map<Skill, SkillEntry> enry = new HashMap<>(1);
        enry.put(skil, new SkillEntry(skilLevel, masterLevel, -1L, -1));
        changeSkillLevel_Skip(enry, true);
    }

    public void changeSkillLevel_Skip(final Map<Skill, SkillEntry> skill, final boolean write) { // only used for temporary skills (not saved into db)
        if (skill.isEmpty()) {
            return;
        }
        final Map<Skill, SkillEntry> newL = new HashMap<>();
        for (final Entry<Skill, SkillEntry> z : skill.entrySet()) {
            if (z.getKey() == null) {
                continue;
            }
            newL.put(z.getKey(), z.getValue());
            if (z.getValue().skillevel == 0 && z.getValue().masterlevel == 0) {
                if (skills.containsKey(z.getKey())) {
                    skills.remove(z.getKey());
                } else {
                    continue;
                }
            } else {
                skills.put(z.getKey(), z.getValue());
            }
        }
        if (write && !newL.isEmpty()) {
            client.sendPacket(CWvsContext.updateSkills(newL));
        }
    }

    public void playerDead() {
        final MapleStatEffect statss = getStatForBuff(MapleBuffStat.SOUL_STONE);
        if (statss != null) {
            dropMessage(5, "由於靈魂之石的效果發動，本次死亡經驗您的經驗值不會減少.");
            getStat().setHp(((getStat().getMaxHp() / 100) * statss.getX()), this);
            setStance(0);
            changeMap(getMap(), getMap().getPortal(0));
            return;
        }
        if (getEventInstance() != null) {
            getEventInstance().playerKilled(this);
        }
        cancelEffectFromBuffStat(MapleBuffStat.SHADOWPARTNER);
        cancelEffectFromBuffStat(MapleBuffStat.MORPH);
        cancelEffectFromBuffStat(MapleBuffStat.SOARING);
        cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
        cancelEffectFromBuffStat(MapleBuffStat.MECH_CHANGE);
        cancelEffectFromBuffStat(MapleBuffStat.RECOVERY);
        cancelEffectFromBuffStat(MapleBuffStat.HP_BOOST_PERCENT);
        cancelEffectFromBuffStat(MapleBuffStat.MP_BOOST_PERCENT);
        cancelEffectFromBuffStat(MapleBuffStat.HP_BOOST);
        cancelEffectFromBuffStat(MapleBuffStat.MP_BOOST);
        cancelEffectFromBuffStat(MapleBuffStat.ENHANCED_MAXHP);
        cancelEffectFromBuffStat(MapleBuffStat.ENHANCED_MAXMP);
        cancelEffectFromBuffStat(MapleBuffStat.MAXHP);
        cancelEffectFromBuffStat(MapleBuffStat.MAXMP);
        dispelSummons();
        checkFollow();
        dotHP = 0;
        lastDOTTime = 0;
        if (!GameConstants.isBeginnerJob(job) && !inPVP()) {
            int vipCharm = getItemQuantity(5131000, false);
            int firmCharm = getItemQuantity(5130002, false);
            int safetyCharm = getItemQuantity(5130000, false);
            if (vipCharm > 0) { // VIP護身符
                MapleInventoryManipulator.removeById(client, MapleInventoryType.CASH, 5131000, 1, true, false);
                vipCharm--;
                if (vipCharm > 0xFF) {
                    vipCharm = 0xFF;
                }
                useVipCharm = true;
                client.sendPacket(EffectPacket.useCharm((byte) vipCharm, (byte) 0, 0, 5131000));
            } else if (firmCharm > 0) { // 強效護身符
                MapleInventoryManipulator.removeById(client, MapleInventoryType.CASH, 5130002, 1, true, false);
                firmCharm--;
                if (firmCharm > 0xFF) {
                    firmCharm = 0xFF;
                }
                useFirmCharm = true;
                client.sendPacket(EffectPacket.useCharm((byte) firmCharm, (byte) 0, 0, 5130002));
            } else if (safetyCharm > 0) { // 護身符
                MapleInventoryManipulator.removeById(client, MapleInventoryType.CASH, 5130000, 1, true, false);
                safetyCharm--;
                if (safetyCharm > 0xFF) {
                    safetyCharm = 0xFF;
                }
                client.sendPacket(EffectPacket.useCharm((byte) safetyCharm, (byte) 0, 1, 0));
            } else {
                float diepercentage = 0.0f;
                int expforlevel = getNeededExp();
                if (map.isTown() || FieldLimitType.RegularExpLoss.check(map.getFieldLimit())) {
                    diepercentage = 0.01f;
                } else {
                    diepercentage = (float) (0.1f - ((traits.get(MapleTraitType.charisma).getLevel() / 20) / 100f));
                }
                int v10 = (int) (exp - (long) ((double) expforlevel * diepercentage));
                if (v10 < 0) {
                    v10 = 0;
                }
                this.exp = v10;
            }
            this.updateSingleStat(MapleStat.EXP, this.exp);
        }
        if (!stats.checkEquipDurabilitys(this, -100)) { //i guess this is how it works ?
            dropMessage(5, "An item has run out of durability but has no inventory room to go to.");
        } //lol
        if (pyramidSubway != null) {
            stats.setHp((short) 50, this);
            pyramidSubway.fail(this);
        }
    }

    public void updatePartyMemberHP() {
        if (party != null && client.getChannelServer() != null) {
            final int channel = client.getChannel();
            for (MaplePartyCharacter partychar : party.getMembers()) {
                if (partychar != null && partychar.getMapid() == getMapId() && partychar.getChannel() == channel) {
                    final MapleCharacter other = client.getChannelServer().getPlayerStorage().getCharacterByName(partychar.getName());
                    if (other != null) {
                        other.getClient().sendPacket(CField.updatePartyMemberHP(getId(), stats.getHp(), stats.getCurrentMaxHp()));
                    }
                }
            }
        }
    }

    public void receivePartyMemberHP() {
        if (party == null) {
            return;
        }
        int channel = client.getChannel();
        for (MaplePartyCharacter partychar : party.getMembers()) {
            if (partychar != null && partychar.getMapid() == getMapId() && partychar.getChannel() == channel) {
                MapleCharacter other = client.getChannelServer().getPlayerStorage().getCharacterByName(partychar.getName());
                if (other != null) {
                    client.sendPacket(CField.updatePartyMemberHP(other.getId(), other.getStat().getHp(), other.getStat().getCurrentMaxHp()));
                }
            }
        }
    }

    public void healHP(int delta) {
        addHP(delta);
        client.sendPacket(EffectPacket.showOwnHpHealed(delta));
        getMap().broadcastMessage(this, EffectPacket.showHpHealed(getId(), delta), false);
    }

    public void healMP(int delta) {
        addMP(delta);
        client.sendPacket(EffectPacket.showOwnHpHealed(delta));
        getMap().broadcastMessage(this, EffectPacket.showHpHealed(getId(), delta), false);
    }

    /**
     * Convenience function which adds the supplied parameter to the current hp
     * then directly does a updateSingleStat.
     *
     * @param delta
     * @see MapleCharacter#setHp(int)
     */
    public void addHP(int delta) {
        if (stats.setHp(stats.getHp() + delta, this)) {
            updateSingleStat(MapleStat.HP, stats.getHp());
        }
    }

    /**
     * Convenience function which adds the supplied parameter to the current mp
     * then directly does a updateSingleStat.
     *
     * @param delta
     * @see MapleCharacter#setMp(int)
     */
    public void addMP(int delta) {
        addMP(delta, false);
    }

    public void addMP(int delta, boolean ignore) {
        if ((delta < 0 && GameConstants.isDemon(getJob())) || !GameConstants.isDemon(getJob()) || ignore) {
            if (stats.setMp(stats.getMp() + delta, this)) {
                updateSingleStat(MapleStat.MP, stats.getMp());
            }
        }
    }

    public void addMPHP(int hpDiff, int mpDiff) {
        Map<MapleStat, Integer> statups = new EnumMap<>(MapleStat.class);

        if (stats.setHp(stats.getHp() + hpDiff, this)) {
            statups.put(MapleStat.HP, stats.getHp());
        }
        if ((mpDiff < 0 && GameConstants.isDemon(getJob())) || !GameConstants.isDemon(getJob())) {
            if (stats.setMp(stats.getMp() + mpDiff, this)) {
                statups.put(MapleStat.MP, stats.getMp());
            }
        }
        if (statups.size() > 0) {
            client.sendPacket(CWvsContext.updatePlayerStats(statups, this));
        }
    }

    public void updateSingleStat(MapleStat stat, int newval) {
        updateSingleStat(stat, newval, false);
    }

    /**
     * Updates a single stat of this MapleCharacter for the client. This method
     * only creates and sends an update packet, it does not update the stat
     * stored in this MapleCharacter instance.
     *
     * @param stat
     * @param newval
     * @param itemReaction
     */
    public void updateSingleStat(MapleStat stat, int newval, boolean itemReaction) {
        Map<MapleStat, Integer> statup = new EnumMap<>(MapleStat.class);
        statup.put(stat, newval);
        client.sendPacket(CWvsContext.updatePlayerStats(statup, itemReaction, this));
    }

    public void gainExp(final int total, final boolean show, final boolean inChat, final boolean white) {
        try {
            int prevexp = getExp();
            int needed = getNeededExp();
            if (total > 0) {
                stats.checkEquipLevels(this, total); //gms like
            }
            if ((level >= 200 || (GameConstants.isKOC(job) && level >= 200)) && !isIntern()) {
                setExp(0);
                //if (exp + total > needed) {
                //    setExp(needed);
                //} else {
                //    exp += total;
                //}
            } else {
                boolean leveled = false;
                long tot = exp + total;
                if (tot >= needed) {
                    exp += total;
                    levelUp();
                    leveled = true;
                    if ((level >= 200 || (GameConstants.isKOC(job) && level >= 200)) && !isIntern()) {
                        setExp(0);
                    } else {
                        needed = getNeededExp();
                        if (exp >= needed) {
                            setExp(needed - 1);
                        }
                    }
                } else {
                    exp += total;
                }
                if (total > 0) {
                    familyRep(prevexp, needed, leveled);
                }
            }
            if (total != 0) {
                if (exp < 0) { // After adding, and negative
                    if (total > 0) {
                        setExp(needed);
                    } else if (total < 0) {
                        setExp(0);
                    }
                }
                updateSingleStat(MapleStat.EXP, getExp());
                if (show) { // still show the expgain even if it's not there
                    client.sendPacket(InfoPacket.GainEXP_Others(total, inChat, white));
                }
            }
        } catch (Exception e) {
            FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, e); //all jobs throw errors :(
        }
    }

    public void familyRep(int prevexp, int needed, boolean leveled) {
        if (mfc != null) {
            int onepercent = needed / 100;
            if (onepercent <= 0) {
                return;
            }
            int percentrep = (getExp() / onepercent - prevexp / onepercent);
            if (leveled) {
                percentrep = 100 - percentrep + (level / 2);
            }
            if (percentrep > 0) {
                int sensen = World.Family.setRep(mfc.getFamilyId(), mfc.getSeniorId(), percentrep * 10, level, name);
                if (sensen > 0) {
                    World.Family.setRep(mfc.getFamilyId(), sensen, percentrep * 5, level, name); //and we stop here
                }
            }
        }
    }

    public void gainExpMonster(final int gain, final boolean show, final boolean white, final byte pty, int Class_Bonus_EXP, int Equipment_Bonus_EXP, int Premium_Bonus_EXP, boolean partyBonusMob, final int partyBonusRate) {
        int total = gain + Class_Bonus_EXP + Equipment_Bonus_EXP + Premium_Bonus_EXP;
        int partyinc = 0;
        int prevexp = getExp();
        if (pty > 1) {
            final double rate = (partyBonusRate > 0 ? (partyBonusRate / 100.0) : (map == null || !partyBonusMob || map.getPartyBonusRate() <= 0 ? 0.05 : (map.getPartyBonusRate() / 100.0)));
            partyinc = (int) (((float) (gain * rate)) * (pty + (rate > 0.05 ? -1 : 1)));
            total += partyinc;
        }
        expirationTask(false, true);
        if (gain > 0 && total < gain) { //just in case
            total = Integer.MAX_VALUE;
        }
        if (total > 0) {
            stats.checkEquipLevels(this, total); //gms like
        }
        int needed = getNeededExp();
        if ((level >= 200 || (GameConstants.isKOC(job) && level >= 200)) && !isIntern()) {
            setExp(0);
            //if (exp + total > needed) {
            //    setExp(needed);
            //} else {
            //    exp += total;
            //}
        } else {
            boolean leveled = false;
            if (exp + total >= needed || exp >= needed) {
                exp += total;
                levelUp();
                leveled = true;
                if ((level >= 200 || (GameConstants.isKOC(job) && level >= 200)) && !isIntern()) {
                    setExp(0);
                } else {
                    needed = getNeededExp();
                    if (exp >= needed) {
                        setExp(needed);
                    }
                }
            } else {
                exp += total;
            }
            if (total > 0) {
                familyRep(prevexp, needed, leveled);
            }
        }
        if (gain != 0) {
            if (exp < 0) { // After adding, and negative
                if (gain > 0) {
                    setExp(getNeededExp());
                } else if (gain < 0) {
                    setExp(0);
                }
            }
            updateSingleStat(MapleStat.EXP, getExp());
            if (show) { // still show the expgain even if it's not there
                client.sendPacket(InfoPacket.GainEXP_Monster(gain, white, partyinc, Class_Bonus_EXP, Equipment_Bonus_EXP, Premium_Bonus_EXP));
            }
        }
    }

    public void forceReAddItem_NoUpdate(Item item, MapleInventoryType type) {
        getInventory(type).removeSlot(item.getPosition());
        getInventory(type).addFromDB(item);
    }

    public void forceReAddItem(Item item, MapleInventoryType type) { //used for stuff like durability, item exp/level, probably owner?
        forceReAddItem_NoUpdate(item, type);
        if (type != MapleInventoryType.UNDEFINED) {
            client.sendPacket(InventoryPacket.updateSpecialItemUse(item, type == MapleInventoryType.EQUIPPED ? (byte) 1 : type.getType(), this));
        }
    }

    public void forceReAddItem_Flag(Item item, MapleInventoryType type) { //used for flags
        forceReAddItem_NoUpdate(item, type);
        if (type != MapleInventoryType.UNDEFINED) {
            client.sendPacket(InventoryPacket.updateSpecialItemUse_(item, type == MapleInventoryType.EQUIPPED ? (byte) 1 : type.getType(), this));
        }
    }

    public void forceReAddItem_Book(Item item, MapleInventoryType type) { //used for mbook
        forceReAddItem_NoUpdate(item, type);
        if (type != MapleInventoryType.UNDEFINED) {
            client.sendPacket(CWvsContext.upgradeBook(item, this));
        }
    }

    public void silentPartyUpdate() {
        if (party != null) {
            World.Party.updateParty(party.getId(), PartyOperation.SILENT_UPDATE, new MaplePartyCharacter(this));
        }
    }

    public boolean isIntern() {
        return gmLevel >= 1;
    }

    public boolean isGM() {
        return gmLevel > 0;
    }

    public boolean isGod() {
        return gmLevel >= 100;
    }

    public boolean isAdmin() {
        return gmLevel >= 5;
    }

    public int getGMLevel() {
        return gmLevel;
    }

    public boolean hasGmLevel(int level) {
        return gmLevel >= level;
    }

    public boolean isShowInfo() {
        return isAdmin();
    }

    public void showMessage(int type, String msg) {
        if (type >= 0 && type <= 44) {
            client.sendPacket(CField.getGameMessage(type, msg));
        } else {
            client.sendPacket(CWvsContext.serverNotice(5, msg));
        }
    }

    public final MapleInventory getInventory(MapleInventoryType type) {
        return inventory[type.ordinal()];
    }

    public final MapleInventory[] getInventorys() {
        return inventory;
    }

    public final void expirationTask(boolean pending, boolean firstLoad) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (pending) {
            if (pendingExpiration != null) {
                for (Integer z : pendingExpiration) {
                    client.sendPacket(InfoPacket.itemExpired(z));
                    if (!firstLoad) {
                        final Pair<Integer, String> replace = ii.replaceItemInfo(z);
                        if (replace != null && replace.left > 0 && replace.right.length() > 0) {
                            dropMessage(5, replace.right);
                        }
                    }
                }
            }
            pendingExpiration = null;
            if (pendingSkills != null) {
                client.sendPacket(CWvsContext.updateSkills(pendingSkills));
                for (Skill z : pendingSkills.keySet()) {
                    client.sendPacket(CWvsContext.serverNotice(5, "[" + SkillFactory.getSkillName(z.getId()) + "] 技能已經過期，系統自動從技能欄位移除。"));
                }
            } //not real msg
            pendingSkills = null;
            return;
        }
        final MapleQuestStatus stat = getQuestNoAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
        long expiration;
        final List<Integer> ret = new ArrayList<>();
        final long currenttime = System.currentTimeMillis();
        final List<Triple<MapleInventoryType, Item, Boolean>> toberemove = new ArrayList<>(); // This is here to prevent deadlock.
        final List<Item> tobeunlock = new ArrayList<>(); // This is here to prevent deadlock.

        for (final MapleInventoryType inv : MapleInventoryType.values()) {
            for (final Item item : getInventory(inv)) {
                expiration = item.getExpiration();

                if ((expiration != -1 && !GameConstants.isPet(item.getItemId()) && currenttime > expiration) || (firstLoad && ii.isLogoutExpire(item.getItemId()))) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        tobeunlock.add(item);
                    } else if (currenttime > expiration) {
                        toberemove.add(new Triple<>(inv, item, false));
                    }
                } else if (item.getItemId() == 5000054 && item.getPet() != null && item.getPet().getSecondsLeft() <= 0) {
                    toberemove.add(new Triple<>(inv, item, false));
                } else if (item.getPosition() == -59) {
                    if (stat == null || stat.getCustomData() == null || Long.parseLong(stat.getCustomData()) < currenttime) {
                        toberemove.add(new Triple<>(inv, item, true));
                    }
                }
            }
        }
        Item item;
        for (final Triple<MapleInventoryType, Item, Boolean> itemz : toberemove) {
            item = itemz.getMid();
            getInventory(itemz.getLeft()).removeItem(item.getPosition(), item.getQuantity(), false);
            if (itemz.getRight() && getInventory(GameConstants.getInventoryType(item.getItemId())).getNextFreeSlot() > -1) {
                item.setPosition(getInventory(GameConstants.getInventoryType(item.getItemId())).getNextFreeSlot());
                getInventory(GameConstants.getInventoryType(item.getItemId())).addFromDB(item);
            } else {
                ret.add(item.getItemId());
            }
            if (!firstLoad) {
                final Pair<Integer, String> replace = ii.replaceItemInfo(item.getItemId());
                if (replace != null && replace.left > 0) {
                    Item theNewItem = null;
                    if (GameConstants.getInventoryType(replace.left) == MapleInventoryType.EQUIP) {
                        theNewItem = ii.getEquipById(replace.left);
                        theNewItem.setPosition(item.getPosition());
                    } else {
                        theNewItem = new Item(replace.left, item.getPosition(), (short) 1, (byte) 0);
                    }
                    getInventory(itemz.getLeft()).addFromDB(theNewItem);
                }
            }
        }
        for (final Item itemz : tobeunlock) {
            itemz.setExpiration(-1);
            itemz.setFlag((byte) (itemz.getFlag() - ItemFlag.LOCK.getValue()));
        }
        this.pendingExpiration = ret;

        final Map<Skill, SkillEntry> skilz = new HashMap<>();
        final List<Skill> toberem = new ArrayList<>();
        for (Entry<Skill, SkillEntry> skil : skills.entrySet()) {
            if (skil.getValue().expiration != -1 && currenttime > skil.getValue().expiration) {
                toberem.add(skil.getKey());
            }
        }
        for (Skill skil : toberem) {
            skilz.put(skil, new SkillEntry(0, (byte) 0, -1, -1));
            this.skills.remove(skil);
            changed_skills = true;
        }
        this.pendingSkills = skilz;
        if (stat != null && stat.getCustomData() != null && Long.parseLong(stat.getCustomData()) < currenttime) { //expired bro
            quests.remove(MapleQuest.getInstance(7830));
            quests.remove(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
        }

        if (coreAura != null && currenttime > coreAura.getExpire()) {
            coreAura.resetCoreAura();
            coreAura.saveToDB();
            client.sendPacket(CWvsContext.updateCoreAura(this));
            stats.recalcLocalStats(this);
            dropMessage(5, "寶盒期限已到，能力值已初始化。");
        }
    }

    public MapleShop getShop() {
        return shop;
    }

    public void setShop(MapleShop shop) {
        this.shop = shop;
    }

    public int getMeso() {
        return meso;
    }

    public void setMeso(int mesos) {
        meso = mesos;
    }

    public final int[] getSavedLocations() {
        return savedLocations;
    }

    public int getSavedLocation(SavedLocationType type) {
        return savedLocations[type.getValue()];
    }

    public void saveLocation(SavedLocationType type) {
        savedLocations[type.getValue()] = getMapId();
        changed_savedlocations = true;
    }

    public void saveLocation(SavedLocationType type, int mapz) {
        savedLocations[type.getValue()] = mapz;
        changed_savedlocations = true;
    }

    public void clearSavedLocation(SavedLocationType type) {
        savedLocations[type.getValue()] = -1;
        changed_savedlocations = true;
    }

    public void gainMeso(int gain, boolean show) {
        gainMeso(gain, show, false);
    }

    public void gainMeso(int gain, boolean show, boolean inChat) {
        if (ServerConstants.LOG_TRACE_GAINMESO) {
            StringBuilder builder = new StringBuilder();
            builder.append("取得楓幣：").append(gain).append(" 目前楓幣：").append(this.meso).append(" 取得之後：").append(this.meso + gain).append(("\r\n"));
            builder.append("StackTraceing=>").append("\r\n");
            Throwable e = new Throwable();
            int nTrace = Math.min(e.getStackTrace().length, MiMiConfig.nStackTraceMax);
            for (int i = 0; i < nTrace; ++i) {
                StackTraceElement ste = e.getStackTrace()[i];
                builder.append("\t").append(ste.toString()).append("\r\n");
            }
            FileoutputUtil.print("/StackTrace/meso/" + this.getName() + ".txt", builder.toString());
        }
        if (meso + gain < 0) {
            client.sendPacket(CWvsContext.enableActions());
            return;
        }
        meso += gain;
        if (meso >= 1000000) {
            finishAchievement(31);
        }
        if (meso >= 10000000) {
            finishAchievement(32);
        }
        if (meso >= 100000000) {
            finishAchievement(33);
        }
        if (meso >= 1000000000) {
            finishAchievement(34);
        }
        updateSingleStat(MapleStat.MESO, meso, false);
        client.sendPacket(CWvsContext.enableActions());
        if (show) {
            client.sendPacket(InfoPacket.showMesoGain(gain, inChat));
        }
    }

    public void controlMonster(MapleMonster monster, boolean aggro) {
        if (clone || monster == null) {
            return;
        }
        monster.setController(this);
        controlledLock.writeLock().lock();
        try {
            controlled.add(monster);
        } finally {
            controlledLock.writeLock().unlock();
        }
        client.sendPacket(MobPacket.controlMonster(monster, false, aggro));
        monster.sendStatus(client);
    }

    public void stopControllingMonster(MapleMonster monster) {
        if (clone || monster == null) {
            return;
        }
        controlledLock.writeLock().lock();
        try {
            if (controlled.contains(monster)) {
                controlled.remove(monster);
            }
        } finally {
            controlledLock.writeLock().unlock();
        }
    }

    public void checkMonsterAggro(MapleMonster monster) {
        if (clone || monster == null) {
            return;
        }
        if (monster.getController() == this) {
            monster.setControllerHasAggro(true);
        } else {
            monster.switchController(this, true);
        }
    }

    public int getControlledSize() {
        return controlled.size();
    }

    public int getAccountID() {
        return accountid;
    }

    public void mobKilled(final int id, final int skillID) {
        for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus() != 1 || !q.hasMobKills()) {
                continue;
            }
            if (q.mobKilled(id, skillID)) {
                client.sendPacket(InfoPacket.updateQuestMobKills(q));
                if (q.getQuest().canComplete(this, null)) {
                    client.sendPacket(CWvsContext.getShowQuestCompletion(q.getQuest().getId()));
                }
            }
        }
    }

    public final List<MapleQuestStatus> getStartedQuests() {
        List<MapleQuestStatus> ret = new LinkedList<>();
        for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus() == 1 && !q.isCustom() && !q.getQuest().isBlocked()) {
                ret.add(q);
            }
        }
        return ret;
    }

    public final List<MapleQuestStatus> getCompletedQuests() {
        List<MapleQuestStatus> ret = new LinkedList<>();
        for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus() == 2 && !q.isCustom() && !q.getQuest().isBlocked()) {
                ret.add(q);
            }
        }
        return ret;
    }

    public final List<Pair<Integer, Long>> getCompletedMedals() {
        List<Pair<Integer, Long>> ret = new ArrayList<>();
        for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus() == 2 && !q.isCustom() && !q.getQuest().isBlocked() && q.getQuest().getMedalItem() > 0 && GameConstants.getInventoryType(q.getQuest().getMedalItem()) == MapleInventoryType.EQUIP) {
                ret.add(new Pair<>(q.getQuest().getId(), q.getCompletionTime()));
            }
        }
        return ret;
    }

    public Map<Skill, SkillEntry> getSkills() {
        return Collections.unmodifiableMap(skills);
    }

    public int getTotalSkillLevel(final Skill skill) {
        if (skill == null) {
            return 0;
        }
        final SkillEntry ret = skills.get(skill);
        if (ret == null || ret.skillevel <= 0) {
            return 0;
        }
        return Math.min(skill.getTrueMax(), ret.skillevel + (skill.isBeginnerSkill() ? 0 : (stats.combatOrders + (skill.getMaxLevel() > 10 ? stats.incAllskill : 0) + stats.getSkillIncrement(skill.getId()))));
    }

    public int getAllSkillLevels() {
        int rett = 0;
        for (Entry<Skill, SkillEntry> ret : skills.entrySet()) {
            if (!ret.getKey().isBeginnerSkill() && !ret.getKey().isSpecialSkill() && ret.getValue().skillevel > 0) {
                rett += ret.getValue().skillevel;
            }
        }
        return rett;
    }

    public long getSkillExpiry(final Skill skill) {
        if (skill == null) {
            return 0;
        }
        final SkillEntry ret = skills.get(skill);
        if (ret == null || ret.skillevel <= 0) {
            return 0;
        }
        return ret.expiration;
    }

    public int getSkillLevel(final Skill skill) {
        if (skill == null) {
            return 0;
        }
        final SkillEntry ret = skills.get(skill);
        if (ret == null || ret.skillevel <= 0) {
            return 0;
        }
        return ret.skillevel;
    }

    public byte getMasterLevel(final int skill) {
        return getMasterLevel(SkillFactory.getSkill(skill));
    }

    public byte getMasterLevel(final Skill skill) {
        final SkillEntry ret = skills.get(skill);
        if (ret == null) {
            return 0;
        }
        return ret.masterlevel;
    }

    public void levelUp() {
        if (GameConstants.isKOC(job)) {
            if (level <= 70) {
                remainingAp += 6;
            } else {
                remainingAp += 5;
            }
        } else {
            remainingAp += 5;
        }
        int maxhp = stats.getMaxHp();
        int maxmp = stats.getMaxMp();

        if (MapleJob.isBeginner(job)) {
            maxhp += Randomizer.rand(12, 16);
            maxmp += Randomizer.rand(10, 12);
        } else if (MapleJob.is劍士(job) && (MapleJob.is冒險家(job) || MapleJob.is皇家騎士團(job))) {
            maxhp += Randomizer.rand(48, 52);
            maxmp += Randomizer.rand(4, 6);
        } else if (MapleJob.is狂狼勇士(job)) {
            maxhp += Randomizer.rand(50, 52);
            maxmp += Randomizer.rand(4, 6);
        } else if (MapleJob.is惡魔殺手(job)) {
            maxhp += Randomizer.rand(48, 52);
        } else if (MapleJob.is米哈逸(job)) {
            maxhp += Randomizer.rand(48, 52);
            maxmp += Randomizer.rand(4, 6);
        } else if (MapleJob.is法師(job) && (MapleJob.is冒險家(job) || MapleJob.is皇家騎士團(job))) {
            maxhp += Randomizer.rand(10, 14);
            maxmp += Randomizer.rand(48, 52);
        } else if (MapleJob.is龍魔導士(job)) {
            maxhp += Randomizer.rand(12, 16);
            maxmp += Randomizer.rand(50, 52);
        } else if (MapleJob.is煉獄巫師(job)) {
            maxhp += Randomizer.rand(20, 24);
            maxmp += Randomizer.rand(42, 44);
        } else if (MapleJob.is弓箭手(job) || (MapleJob.is盜賊(job) && (MapleJob.is冒險家(job) || MapleJob.is皇家騎士團(job)))) {
            maxhp += Randomizer.rand(20, 24);
            maxmp += Randomizer.rand(14, 16);
        } else if (MapleJob.is幻影俠盜(job)) {
            maxhp += Randomizer.rand(56, 67);
            maxmp += Randomizer.rand(74, 100);
        } else if (MapleJob.is拳霸(job)) {
            maxhp += Randomizer.rand(37, 41);
            maxmp += Randomizer.rand(18, 22);
        } else if (MapleJob.is海盜(job) && MapleJob.is冒險家(job)) {
            maxhp += Randomizer.rand(20, 24);
            maxmp += Randomizer.rand(18, 22);
        } else if (MapleJob.is閃雷悍將(job) || MapleJob.is機甲戰神(job)) {
            maxhp += Randomizer.rand(56, 67);
            maxmp += Randomizer.rand(34, 47);
        } else {
            maxhp += Randomizer.rand(50, 100);
            maxmp += Randomizer.rand(50, 100);
        }
        maxmp += stats.getTotalInt() / 10;

        exp -= getNeededExp();
        if (exp < 0) {
            exp = 0;
        }
        level += 1;
        if (GameConstants.isKOC(job) && level < 200 && level > 10) {
            exp += getNeededExp() / 10;
        }

        if (level == 30) {
            finishAchievement(2);
        }
        if (level == 70) {
            finishAchievement(3);
        }
        if (level == 120) {
            finishAchievement(4);
        }
        if (level == 200) {
            finishAchievement(5);
        }
        LevelMsg();
        maxhp = Math.min(99999, Math.abs(maxhp));
        maxmp = Math.min(99999, Math.abs(maxmp));
        if (GameConstants.isDemon(job)) {
            maxmp = 10/*GameConstants.getMPByJob(job)*/;
        }
        final Map<MapleStat, Integer> statup = new EnumMap<>(MapleStat.class);

        statup.put(MapleStat.MAXHP, maxhp);
        statup.put(MapleStat.MAXMP, maxmp);
        statup.put(MapleStat.HP, maxhp);
        statup.put(MapleStat.MP, maxmp);
        statup.put(MapleStat.EXP, exp);
        statup.put(MapleStat.LEVEL, (int) level);
        updateSingleStat(MapleStat.AVAILABLESP, 0);

        if (level == 10 && MapleJob.is蒼龍俠客(job)) {
            resetStats(4, 4, 4, 4);
        } else if (level <= 10) {
            stats.str += remainingAp;
            remainingAp = 0;
            statup.put(MapleStat.STR, (int) stats.getStr());
        }
        if (level > 10 && level <= 200) {
            if (GameConstants.isResist(this.job) || GameConstants.isMercedes(this.job)) {
                remainingSp[GameConstants.getSkillBook(this.job, this.level)] += 3;
            } else {
                remainingSp[GameConstants.getSkillBook(this.job)] += 3;
            }
        }

        statup.put(MapleStat.AVAILABLEAP, (int) remainingAp);
        statup.put(MapleStat.AVAILABLESP, (int) remainingSp[GameConstants.getSkillBook(job, level)]);
        stats.setInfo(maxhp, maxmp, maxhp, maxmp);
        client.sendPacket(CWvsContext.updatePlayerStats(statup, this));
        map.broadcastMessage(this, EffectPacket.showForeignEffect(getId(), 0), false);
        characterCard.recalcLocalStats(this);
        stats.recalcLocalStats(this);
        silentPartyUpdate();
        guildUpdate();
        familyUpdate();
        autoJob();
        stats.heal(this);
    }

    public void autoJob() {
        if (MapleJob.is英雄(job)) {
            switch (getLevel()) {
                case 70:
                    changeJob((short) MapleJob.十字軍.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.英雄.getId());
                    break;
            }
        } else if (MapleJob.is聖騎士(job)) {
            switch (getLevel()) {
                case 70:
                    changeJob((short) MapleJob.騎士.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.聖騎士.getId());
                    break;
            }
        } else if (MapleJob.is黑騎士(job)) {
            switch (getLevel()) {
                case 70:
                    changeJob((short) MapleJob.嗜血狂騎.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.黑騎士.getId());
                    break;
            }
        } else if (MapleJob.is大魔導士_火毒(job)) {
            switch (getLevel()) {
                case 70:
                    changeJob((short) MapleJob.魔導士_火毒.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.大魔導士_火毒.getId());
                    break;
            }
        } else if (MapleJob.is大魔導士_冰雷(job)) {
            switch (getLevel()) {
                case 70:
                    changeJob((short) MapleJob.魔導士_冰雷.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.大魔導士_冰雷.getId());
                    break;
            }
        } else if (MapleJob.is主教(job)) {
            switch (getLevel()) {
                case 70:
                    changeJob((short) MapleJob.祭司.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.主教.getId());
                    break;
            }
        } else if (MapleJob.is箭神(job)) {
            switch (getLevel()) {
                case 70:
                    changeJob((short) MapleJob.遊俠.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.箭神.getId());
                    break;
            }
        } else if (MapleJob.is神射手(job)) {
            switch (getLevel()) {
                case 70:
                    changeJob((short) MapleJob.狙擊手.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.神射手.getId());
                    break;
            }
        } else if (MapleJob.is夜使者(job)) {
            switch (getLevel()) {
                case 70:
                    changeJob((short) MapleJob.暗殺者.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.夜使者.getId());
                    break;
            }
        } else if (MapleJob.is暗影神偷(job)) {
            switch (getLevel()) {
                case 70:
                    changeJob((short) MapleJob.神偷.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.暗影神偷.getId());
                    break;
            }
        } else if (MapleJob.is影武者(job) || (subcategory == 1 && job == 400)) {
            switch (getLevel()) {
                case 20:
                    changeJob((short) MapleJob.下忍.getId());
                    break;
                case 30:
                    changeJob((short) MapleJob.中忍.getId());
                    break;
                case 55:
                    changeJob((short) MapleJob.上忍.getId());
                    break;
                case 70:
                    changeJob((short) MapleJob.隱忍.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.影武者.getId());
                    break;
            }
        } else if (MapleJob.is拳霸(job)) {
            switch (getLevel()) {
                case 70:
                    changeJob((short) MapleJob.格鬥家.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.拳霸.getId());
                    break;
            }
        } else if (MapleJob.is槍神(job)) {
            switch (getLevel()) {
                case 70:
                    changeJob((short) MapleJob.神槍手.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.槍神.getId());
                    break;
            }
        } else if (MapleJob.is重砲指揮官(job) || (subcategory == 2 && job == 0)) {
            switch (getLevel()) {
                case 10:
                    gainItem(1532000, 1);
                    changeJob((short) MapleJob.砲手.getId());
                    break;
                case 30:
                    changeJob((short) MapleJob.重砲兵.getId());
                    break;
                case 70:
                    changeJob((short) MapleJob.重砲兵隊長.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.重砲指揮官.getId());
                    break;
            }
        } else if (MapleJob.is蒼龍俠客(job)) {
            switch (getLevel()) {
                case 30:
                    changeJob((short) MapleJob.蒼龍俠客2轉.getId());
                    break;
                case 70:
                    changeJob((short) MapleJob.蒼龍俠客3轉.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.蒼龍俠客4轉.getId());
                    break;
            }
        } else if (MapleJob.is聖魂劍士(job)) {
            switch (getLevel()) {
                case 30:
                    changeJob((short) MapleJob.聖魂劍士2轉.getId());
                    break;
                case 70:
                    changeJob((short) MapleJob.聖魂劍士3轉.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.聖魂劍士4轉.getId());
                    break;
            }
        } else if (MapleJob.is烈焰巫師(job)) {
            switch (getLevel()) {
                case 30:
                    changeJob((short) MapleJob.烈焰巫師2轉.getId());
                    break;
                case 70:
                    changeJob((short) MapleJob.烈焰巫師3轉.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.烈焰巫師4轉.getId());
                    break;
            }
        } else if (MapleJob.is破風使者(job)) {
            switch (getLevel()) {
                case 30:
                    changeJob((short) MapleJob.破風使者2轉.getId());
                    break;
                case 70:
                    changeJob((short) MapleJob.破風使者3轉.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.破風使者4轉.getId());
                    break;
            }
        } else if (MapleJob.is暗夜行者(job)) {
            switch (getLevel()) {
                case 30:
                    changeJob((short) MapleJob.暗夜行者2轉.getId());
                    break;
                case 70:
                    changeJob((short) MapleJob.暗夜行者3轉.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.暗夜行者4轉.getId());
                    break;
            }
        } else if (MapleJob.is閃雷悍將(job)) {
            switch (getLevel()) {
                case 30:
                    changeJob((short) MapleJob.閃雷悍將2轉.getId());
                    break;
                case 70:
                    changeJob((short) MapleJob.閃雷悍將3轉.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.閃雷悍將4轉.getId());
                    break;
            }
        } else if (MapleJob.is狂狼勇士(job)) {
            switch (getLevel()) {
//                case 10:
//                    changeJob((short) MapleJob.狂狼勇士1轉.getId());
//                    break;
                case 30:
                    changeJob((short) MapleJob.狂狼勇士2轉.getId());
                    break;
                case 70:
                    changeJob((short) MapleJob.狂狼勇士3轉.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.狂狼勇士4轉.getId());
                    break;
            }
        } else if (MapleJob.is龍魔導士(job)) {
            switch (getLevel()) {
                case 10:
                    changeJob((short) MapleJob.龍魔導士1轉.getId());
                    break;
                case 30:
                    changeJob((short) MapleJob.龍魔導士2轉.getId());
                    break;
                case 70:
                    changeJob((short) MapleJob.龍魔導士3轉.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.龍魔導士4轉.getId());
                    break;
            }
        } else if (MapleJob.is精靈遊俠(job)) {
            switch (getLevel()) {
                case 10:
                    changeJob((short) MapleJob.精靈遊俠1轉.getId());
                    break;
                case 30:
                    changeJob((short) MapleJob.精靈遊俠2轉.getId());
                    break;
                case 70:
                    changeJob((short) MapleJob.精靈遊俠3轉.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.精靈遊俠4轉.getId());
                    break;
            }
        } else if (MapleJob.is幻影俠盜(job)) {
            switch (getLevel()) {
                case 10:
                    changeJob((short) MapleJob.幻影俠盜1轉.getId());
                    break;
                case 30:
                    changeJob((short) MapleJob.幻影俠盜2轉.getId());
                    break;
                case 70:
                    changeJob((short) MapleJob.幻影俠盜3轉.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.幻影俠盜4轉.getId());
                    break;
            }
        } else if (MapleJob.is惡魔殺手(job)) {
            switch (getLevel()) {
                case 30:
                    changeJob((short) MapleJob.惡魔殺手2轉.getId());
                    break;
                case 70:
                    changeJob((short) MapleJob.惡魔殺手3轉.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.惡魔殺手4轉.getId());
                    break;
            }
        } else if (MapleJob.is煉獄巫師(job)) {
            switch (getLevel()) {
                case 30:
                    changeJob((short) MapleJob.煉獄巫師2轉.getId());
                    break;
                case 70:
                    changeJob((short) MapleJob.煉獄巫師3轉.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.煉獄巫師4轉.getId());
                    break;
            }
        } else if (MapleJob.is狂豹獵人(job)) {
            switch (getLevel()) {
                case 30:
                    changeJob((short) MapleJob.狂豹獵人2轉.getId());
                    break;
                case 70:
                    changeJob((short) MapleJob.狂豹獵人3轉.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.狂豹獵人4轉.getId());
                    break;
            }
        } else if (MapleJob.is機甲戰神(job)) {
            switch (getLevel()) {
                case 30:
                    changeJob((short) MapleJob.機甲戰神2轉.getId());
                    break;
                case 70:
                    changeJob((short) MapleJob.機甲戰神3轉.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.機甲戰神4轉.getId());
                    break;
            }
        } else if (MapleJob.is米哈逸(job)) {
            switch (getLevel()) {
//                case 10:
//                    changeJob((short) MapleJob.米哈逸1轉.getId());
//                    break;
                case 30:
                    changeJob((short) MapleJob.米哈逸2轉.getId());
                    break;
                case 70:
                    changeJob((short) MapleJob.米哈逸3轉.getId());
                    break;
                case 120:
                    changeJob((short) MapleJob.米哈逸4轉.getId());
                    break;
            }
        }
    }

    public void LevelMsg() {
        if (level == 200 && !isGM()) {
            final StringBuilder sb = new StringBuilder("[恭喜] ");
            final Item medal = getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -46);
            if (medal != null) { // Medal
                sb.append("<");
                sb.append(MapleItemInformationProvider.getInstance().getName(medal.getItemId()));
                sb.append("> ");
            }
            sb.append(getName());
            sb.append("達到了等級200級！請大家一起恭喜他！");
            World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, sb.toString()));
        }
        String noticemsg = "";
        if (GameConstants.isDB(job)) {
            switch (level) {
                case 10:
                case 20:
                case 30:
                case 55:
                case 70:
                case 120:
                    noticemsg = "現在可以轉職囉 轉職後記得去領技能。";
                    break;
            }
        } else if (GameConstants.isEvan(job)) {
            switch (level) {
                case 10:
                case 20:
                case 30:
                case 40:
                case 50:
                case 60:
                case 80:
                case 100:
                case 120:
                case 160:
                    noticemsg = "現在可以轉職囉 轉職後記得去領技能。";
                    break;
            }
        } else {
            switch (level) {
                case 10:
                case 30:
                case 70:
                case 120:
                    noticemsg = "現在可以轉職囉 轉職後記得去領技能。";
                    break;
            }
        }
        if (!noticemsg.equals("")) {
            getMap().startMapEffect(noticemsg, 5120000);
        }
    }

    public void changeKeybinding(int key, byte type, int action) {
        if (type != 0) {
            keylayout.Layout().put(key, new Pair(type, action));
        } else {
            keylayout.Layout().remove(key);
        }
    }

    public void sendMacros() {
        for (int i = 0; i < 5; i++) {
            if (skillMacros[i] != null) {
                client.sendPacket(CField.getMacros(skillMacros));
                break;
            }
        }
    }

    public void updateMacros(int position, SkillMacro updateMacro) {
        skillMacros[position] = updateMacro;
        changed_skillmacros = true;
    }

    public final SkillMacro[] getMacros() {
        return skillMacros;
    }

    public void tempban(String reason, Calendar duration, int greason, boolean IPMac) {
        if (IPMac) {
            client.banMacs();
        }
        client.sendPacket(CWvsContext.GMPoliceMessage(true));
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps;
            if (IPMac) {
                ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
                ps.setString(1, client.getSession().remoteAddress().toString().split(":")[0]);
                ps.execute();
                ps.close();
            }

            client.getSession().close();

            ps = con.prepareStatement("UPDATE accounts SET tempban = ?, banreason = ?, greason = ? WHERE id = ?");
            Timestamp TS = new Timestamp(duration.getTimeInMillis());
            ps.setTimestamp(1, TS);
            ps.setString(2, reason);
            ps.setInt(3, greason);
            ps.setInt(4, accountid);
            ps.execute();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("Error while tempbanning" + ex);
        }

    }

    public final boolean ban(String reason, boolean IPMac, boolean autoban, boolean hellban) {
        if (lastmonthfameids == null) {
            throw new RuntimeException("Trying to ban a non-loaded character (testhack)");
        }
        client.sendPacket(CWvsContext.GMPoliceMessage(true));
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET banned = ?, banreason = ? WHERE id = ?");
            ps.setInt(1, autoban ? 2 : 1);
            ps.setString(2, reason);
            ps.setInt(3, accountid);
            ps.execute();
            ps.close();

            if (IPMac) {
                client.banMacs();
                ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
                ps.setString(1, client.getSessionIPAddress());
                ps.execute();
                ps.close();

                if (hellban) {
                    PreparedStatement psa = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
                    psa.setInt(1, accountid);
                    ResultSet rsa = psa.executeQuery();
                    if (rsa.next()) {
                        PreparedStatement pss = con.prepareStatement("UPDATE accounts SET banned = ?, banreason = ? WHERE email = ? OR SessionIP = ?");
                        pss.setInt(1, autoban ? 2 : 1);
                        pss.setString(2, reason);
                        pss.setString(3, rsa.getString("email"));
                        pss.setString(4, client.getSessionIPAddress());
                        pss.execute();
                        pss.close();
                    }
                    rsa.close();
                    psa.close();
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error while banning" + ex);
            return false;
        }
        client.getSession().close();
        return true;
    }

    /**
     * Oid of players is always = the cid
     */
    @Override
    public int getObjectId() {
        return getId();
    }

    /**
     * Throws unsupported operation exception, oid of players is read only
     */
    @Override
    public void setObjectId(int id) {
        throw new UnsupportedOperationException();
    }

    public MapleStorage getStorage() {
        return storage;
    }

    public void addVisibleMapObject(MapleMapObject mo) {
        if (clone) {
            return;
        }
        visibleMapObjectsLock.writeLock().lock();
        try {
            visibleMapObjects.add(mo);
        } finally {
            visibleMapObjectsLock.writeLock().unlock();
        }
    }

    public void removeVisibleMapObject(MapleMapObject mo) {
        if (clone) {
            return;
        }
        visibleMapObjectsLock.writeLock().lock();
        try {
            visibleMapObjects.remove(mo);
        } finally {
            visibleMapObjectsLock.writeLock().unlock();
        }
    }

    public boolean isMapObjectVisible(MapleMapObject mo) {
        visibleMapObjectsLock.readLock().lock();
        try {
            return !clone && visibleMapObjects.contains(mo);
        } finally {
            visibleMapObjectsLock.readLock().unlock();
        }
    }

    public Collection<MapleMapObject> getAndWriteLockVisibleMapObjects() {
        visibleMapObjectsLock.writeLock().lock();
        return visibleMapObjects;
    }

    public void unlockWriteVisibleMapObjects() {
        visibleMapObjectsLock.writeLock().unlock();
    }

    public boolean isAlive() {
        return stats.getHp() > 0;
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        client.sendPacket(CField.removePlayerFromMap(this.getObjectId()));
        for (final WeakReference<MapleCharacter> chr : clones) {
            if (chr.get() != null) {
                chr.get().sendDestroyData(client);
            }
        }
        //don't need this, client takes care of it
        /*if (dragon != null) {
        client.sendPacket(CField.removeDragon(this.getId()));
        }
        if (android != null) {
        client.sendPacket(CField.deactivateAndroid(this.getId()));
        }
        if (summonedFamiliar != null) {
        client.sendPacket(CField.removeFamiliar(this.getId()));
        }*/
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        if (client.getPlayer().allowedToTarget(this)) {
            client.sendPacket(CField.spawnPlayerMapobject(this));
            if (getParty() != null && !isClone()) {
                updatePartyMemberHP();
                receivePartyMemberHP();
            }
            for (final MaplePet pet : client.getPlayer().getSummonedPets()) {
                client.sendPacket(PetPacket.updatePet(pet, getInventory(MapleInventoryType.CASH).getItem((short) (byte) pet.getInventoryPosition()), true));
                client.sendPacket(PetPacket.showPet(this, pet, false, false));
                client.sendPacket(PetPacket.showPetUpdate(this, pet.getUniqueId(), (byte) (pet.getSummonedValue() - 1)));
            }
            for (final WeakReference<MapleCharacter> chr : clones) {
                if (chr.get() != null) {
                    chr.get().sendSpawnData(client);
                }
            }
            if (dragon != null) {
                client.sendPacket(CField.spawnDragon(dragon));
            }
            if (android != null) {
                client.sendPacket(CField.spawnAndroid(this, android));
            }
            if (summonedFamiliar != null) {
                client.sendPacket(CField.spawnFamiliar(summonedFamiliar, true));
            }
            if (summons != null && summons.size() > 0) {
                summonsLock.readLock().lock();
                try {
                    for (final MapleSummon summon : summons) {
                        client.sendPacket(SummonPacket.spawnSummon(summon, false));
                    }
                } finally {
                    summonsLock.readLock().unlock();
                }
            }
            if (followid > 0 && followon) {
                client.sendPacket(CField.followEffect(followinitiator ? followid : id, followinitiator ? id : followid, null));
            }
        }
    }

    public final void equipChanged() {
        if (map == null) {
            return;
        }
        map.broadcastMessage(this, CField.updateCharLook(this), false);
        stats.recalcLocalStats(this);
        if (getMessenger() != null) {
            World.Messenger.updateMessenger(getMessenger().getId(), getName(), client.getChannel());
        }
    }

    public final List<MaplePet> getPets() {
        return pets;
    }

    public void addPet(final MaplePet pet) {
        if (pets.contains(pet)) {
            pets.remove(pet);
        }
        pets.add(pet);
    }

    public void removePet(MaplePet pet) {
        pet.setSummoned(0);
        pets.remove(pet);
    }

    public final List<MaplePet> getSummonedPets() {
        List<MaplePet> ret = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ret.add(null);
        }
        for (final MaplePet pet : pets) {
            if (pet != null && pet.getSummoned()) {
                int index = pet.getSummonedValue() - 1;
                ret.remove(index);
                ret.add(index, pet);
            }
        }
        List<Integer> nullArr = new ArrayList();
        nullArr.add(null);
        ret.removeAll(nullArr);
        return ret;
    }

    public final MaplePet getSummonedPet(final int index) {
        for (final MaplePet pet : getSummonedPets()) {
            if (pet.getSummonedValue() - 1 == index) {
                return pet;
            }
        }
        return null;
    }

    public final int getPetSlotNext() {
        List<MaplePet> petsz = getSummonedPets();
        int index = 0;
        if (petsz.size() >= 3) {
            unequipPet(getSummonedPet(0), false);
        } else {
            boolean[] indexBool = new boolean[]{false, false, false};
            for (int i = 0; i < 3; i++) {
                for (MaplePet p : petsz) {
                    if (p.getSummonedValue() == i + 1) {
                        indexBool[i] = true;
                    }
                }
            }
            for (boolean b : indexBool) {
                if (!b) {
                    break;
                }
                index++;
            }
            index = Math.min(index, 2);
            for (MaplePet p : petsz) {
                if (p.getSummonedValue() == index + 1) {
                    unequipPet(p, false);
                }
            }
        }
        return index;
    }

    public final byte getPetIndex(final MaplePet petz) {
        return (byte) Math.max(-1, petz.getSummonedValue() - 1);
    }

    public final byte getPetIndex(final int petId) {
        for (final MaplePet pet : getSummonedPets()) {
            if (pet.getUniqueId() == petId) {
                return (byte) Math.max(-1, pet.getSummonedValue() - 1);
            }
        }
        return -1;
    }

    public final byte getPetIndexById(final int petId) {
        for (final MaplePet pet : getSummonedPets()) {
            if (pet.getPetItemId() == petId) {
                return (byte) Math.max(-1, pet.getSummonedValue() - 1);
            }
        }
        return -1;
    }

    public final void unequipAllPets() {
        for (final MaplePet pet : getSummonedPets()) {
            unequipPet(pet, false);
        }
    }

    public void unequipPet(MaplePet pet, boolean hunger) {
        if (pet.getSummoned()) {
            pet.saveToDb();

            int index = pet.getSummonedValue() - 1;
            pet.setSummoned(0);
            client.sendPacket(PetPacket.updatePet(pet, getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()), true));
            if (map != null) {
                //map.broadcastMessage(this, PetPacket.showPet(this, pet, true, hunger), false);
                map.broadcastMessage(this, PetPacket.removePet(getId(), index), true);
            }
            //List<Pair<MapleStat, Integer>> stats = new ArrayList<Pair<MapleStat, Integer>>();
            //stats.put(MapleStat.PET, Integer.valueOf(0)));
            //showpetupdate isn't done here...
//            client.sendPacket(PetPacket.petStatUpdate(this));
            client.sendPacket(CWvsContext.enableActions());
        }
    }

    public final long getLastFameTime() {
        return lastfametime;
    }

    public final List<Integer> getFamedCharacters() {
        return lastmonthfameids;
    }

    public final List<Integer> getBattledCharacters() {
        return lastmonthbattleids;
    }

    public FameStatus canGiveFame(MapleCharacter from) {
        if (lastfametime >= System.currentTimeMillis() - 60 * 60 * 24 * 1000) {
            return FameStatus.NOT_TODAY;
        } else if (from == null || lastmonthfameids == null || lastmonthfameids.contains(from.getId())) {
            return FameStatus.NOT_THIS_MONTH;
        }
        return FameStatus.OK;
    }

    public void hasGivenFame(MapleCharacter to) {
        lastfametime = System.currentTimeMillis();
        lastmonthfameids.add(to.getId());
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO famelog (characterid, characterid_to) VALUES (?, ?)");
            ps.setInt(1, getId());
            ps.setInt(2, to.getId());
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            System.err.println("ERROR writing famelog for char " + getName() + " to " + to.getName() + e);
        }
    }

    public boolean canBattle(MapleCharacter to) {
        if (to == null || lastmonthbattleids == null || lastmonthbattleids.contains(to.getAccountID())) {
            return false;
        }
        return true;
    }

    public void hasBattled(MapleCharacter to) {
        lastmonthbattleids.add(to.getAccountID());
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO battlelog (accid, accid_to) VALUES (?, ?)");
            ps.setInt(1, getAccountID());
            ps.setInt(2, to.getAccountID());
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            System.err.println("ERROR writing battlelog for char " + getName() + " to " + to.getName() + e);
        }
    }

    public final MapleKeyLayout getKeyLayout() {
        return this.keylayout;
    }

    public MapleParty getParty() {
        if (party == null) {
            return null;
        } else if (party.isDisbanded()) {
            party = null;
        }
        return party;
    }

    public void setParty(MapleParty party) {
        this.party = party;
    }

    public byte getWorld() {
        return world;
    }

    public void setWorld(byte world) {
        this.world = world;
    }

    public MapleTrade getTrade() {
        return trade;
    }

    public void setTrade(MapleTrade trade) {
        this.trade = trade;
    }

    public EventInstanceManager getEventInstance() {
        return eventInstance;
    }

    public void setEventInstance(EventInstanceManager eventInstance) {
        this.eventInstance = eventInstance;
    }

    public void addDoor(MapleDoor door) {
        doors.add(door);
    }

    public void clearDoors() {
        doors.clear();
    }

    public List<MapleDoor> getDoors() {
        return new ArrayList<>(doors);
    }

    public void addMechDoor(MechDoor door) {
        mechDoors.add(door);
    }

    public void clearMechDoors() {
        mechDoors.clear();
    }

    public List<MechDoor> getMechDoors() {
        return new ArrayList<>(mechDoors);
    }

    public void setSmega() {
        if (smega) {
            smega = false;
            dropMessage(5, "You have set megaphone to disabled mode");
        } else {
            smega = true;
            dropMessage(5, "You have set megaphone to enabled mode");
        }
    }

    public boolean getSmega() {
        return smega;
    }

    public List<MapleSummon> getSummonsReadLock() {
        summonsLock.readLock().lock();
        return summons;
    }

    public int getSummonsSize() {
        return summons.size();
    }

    public void unlockSummonsReadLock() {
        summonsLock.readLock().unlock();
    }

    public void addSummon(MapleSummon s) {
        summonsLock.writeLock().lock();
        try {
            summons.add(s);
        } finally {
            summonsLock.writeLock().unlock();
        }
    }

    public void removeSummon(MapleSummon s) {
        summonsLock.writeLock().lock();
        try {
            summons.remove(s);
        } finally {
            summonsLock.writeLock().unlock();
        }
    }

    public int getChair() {
        return chair;
    }

    public void setChair(int chair) {
        this.chair = chair;
        stats.relocHeal(this);
    }

    public int getItemEffect() {
        return itemEffect;
    }

    public void setItemEffect(int itemEffect) {
        this.itemEffect = itemEffect;
    }

    public int getTitleEffect() {
        return titleEffect;
    }

    public void setTitleEffect(int titleEffect) {
        this.titleEffect = titleEffect;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.PLAYER;
    }

    public int getFamilyId() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getFamilyId();
    }

    public int getSeniorId() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getSeniorId();
    }

    public int getJunior1() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getJunior1();
    }

    public int getJunior2() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getJunior2();
    }

    public int getCurrentRep() {
        return currentrep;
    }

    public void setCurrentRep(int _rank) {
        currentrep = _rank;
        if (mfc != null) {
            mfc.setCurrentRep(_rank);
        }
    }

    public int getTotalRep() {
        return totalrep;
    }

    public void setTotalRep(int _rank) {
        totalrep = _rank;
        if (mfc != null) {
            mfc.setTotalRep(_rank);
        }
    }

    public int getTotalWins() {
        return totalWins;
    }

    public int getTotalLosses() {
        return totalLosses;
    }

    public void increaseTotalWins() {
        totalWins++;
    }

    public void increaseTotalLosses() {
        totalLosses++;
    }

    public int getGuildId() {
        return guildid;
    }

    public void setGuildId(int _id) {
        guildid = _id;
        if (guildid > 0) {
            if (mgc == null) {
                mgc = new MapleGuildCharacter(this);
            } else {
                mgc.setGuildId(guildid);
            }
        } else {
            mgc = null;
            guildContribution = 0;
        }
    }

    public byte getGuildRank() {
        return guildrank;
    }

    public void setGuildRank(byte _rank) {
        guildrank = _rank;
        if (mgc != null) {
            mgc.setGuildRank(_rank);
        }
    }

    public int getGuildContribution() {
        return guildContribution;
    }

    public void setGuildContribution(int _c) {
        this.guildContribution = _c;
        if (mgc != null) {
            mgc.setGuildContribution(_c);
        }
    }

    public MapleGuildCharacter getMGC() {
        return mgc;
    }

    public byte getAllianceRank() {
        return allianceRank;
    }

    public void setAllianceRank(byte rank) {
        allianceRank = rank;
        if (mgc != null) {
            mgc.setAllianceRank(rank);
        }
    }

    public MapleGuild getGuild() {
        if (getGuildId() <= 0) {
            return null;
        }
        return World.Guild.getGuild(getGuildId());
    }

    public void guildUpdate() {
        if (guildid <= 0) {
            return;
        }
        mgc.setLevel((short) level);
        mgc.setJobId(job);
        World.Guild.memberLevelJobUpdate(mgc);
    }

    public void saveGuildStatus() {
        MapleGuild.setOfflineGuildStatus(guildid, guildrank, guildContribution, allianceRank, id);
    }

    public void familyUpdate() {
        if (mfc == null) {
            return;
        }
        World.Family.memberFamilyUpdate(mfc, this);
    }

    public void saveFamilyStatus() {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE characters SET familyid = ?, seniorid = ?, junior1 = ?, junior2 = ? WHERE id = ?");
            if (mfc == null) {
                ps.setInt(1, 0);
                ps.setInt(2, 0);
                ps.setInt(3, 0);
                ps.setInt(4, 0);
            } else {
                ps.setInt(1, mfc.getFamilyId());
                ps.setInt(2, mfc.getSeniorId());
                ps.setInt(3, mfc.getJunior1());
                ps.setInt(4, mfc.getJunior2());
            }
            ps.setInt(5, id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException se) {
            System.out.println("SQLException: " + se.getLocalizedMessage());
            se.printStackTrace();
        }
        //MapleFamily.setOfflineFamilyStatus(familyid, seniorid, junior1, junior2, currentrep, totalrep, id);
    }

    public void modifyCSPoints(int type, int quantity) {
        modifyCSPoints(type, quantity, false);
    }

    public void modifyCSPoints(int type, int quantity, boolean show) {
        switch (type) {
            case 0:
                if (acash + quantity < 0) {
                    if (show) {
                        dropMessage(-1, "樂豆點數已達上限，無法再獲得。");
                    }
                    return;
                }
                acash += quantity;
                break;
            case 1:
                if (maplepoints + quantity < 0) {
                    if (show) {
                        dropMessage(-1, "楓葉點數已達上限，無法再獲得。");
                    }
                    return;
                }
                maplepoints += quantity;
                break;
            default:
                break;
        }
        if (show && quantity != 0) {
            dropMessage(-1, (quantity > 0 ? "獲得 " : "損失 ") + quantity + (type == 0 ? " 樂豆點數" : " 楓葉點數"));
            //client.sendPacket(EffectPacket.showForeignEffect(20));
        }
    }

    public int getCSPoints(int type) {
        switch (type) {
            case 0:
                return acash;
            case 1:
                return maplepoints;
            default:
                return 0;
        }
    }

    public void setCData(int questid, int points) {
        final MapleQuestStatus record = client.getPlayer().getQuestNAdd(MapleQuest.getInstance(questid));

        if (record.getCustomData() != null) {
            record.setCustomData(String.valueOf(points + Integer.parseInt(record.getCustomData())));
        } else {
            record.setCustomData(String.valueOf(points)); // First time
        }
    }

    public int getCData(MapleCharacter sai, int questid) {
        final MapleQuestStatus record = sai.getQuestNAdd(MapleQuest.getInstance(questid));
        if (record.getCustomData() != null) {
            return Integer.parseInt(record.getCustomData());
        }
        return 0;
    }

    public final boolean hasEquipped(int itemid) {
        return inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemid) >= 1;
    }

    public final boolean haveItem(int itemid, int quantity, boolean checkEquipped, boolean greaterOrEquals) {
        if (getNpcNow() != 0) {
            if (quantity <= 0) {
                FileoutputUtil.logToFile_NpcScript_Bug(this, " 含有 檢測道具[" + itemid + "]數量異常[" + quantity + "] 之漏洞\r\n");
                getClient().sendPacket(CWvsContext.enableActions());
                return false;
            }
            if (!MapleItemInformationProvider.getInstance().itemExists(itemid)) {
                FileoutputUtil.logToFile_NpcScript_Bug(this, " 含有 檢測道具代碼不存在" + "[" + itemid + "] 數量[" + quantity + "]之漏洞\r\n");
                getClient().sendPacket(CWvsContext.enableActions());
                return false;
            }
        }
        final MapleInventoryType type = GameConstants.getInventoryType(itemid);
        int possesed = inventory[type.ordinal()].countById(itemid);
        if (checkEquipped && type == MapleInventoryType.EQUIP) {
            possesed += inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemid);
        }
        if (greaterOrEquals) {
            return possesed >= quantity;
        } else {
            return possesed == quantity;
        }
    }

    public final boolean haveItem(int itemid, int quantity) {
        return haveItem(itemid, quantity, true, true);
    }

    public final boolean haveItem(int itemid) {
        return haveItem(itemid, 1, true, true);
    }

    public void dropNPC(String message) {
        client.sendPacket(NPCPacket.getNPCTalk(9010000, (byte) 0, message, "00 00", (byte) 0));
    }

    public void dropNPC(int npc, String message) {
        client.sendPacket(NPCPacket.getNPCTalk(npc, (byte) 0, message, "00 00", (byte) 0));
    }

    public void changeSkillLevel(Skill skill, byte newLevel, byte newMasterlevel) {
        final Map<Skill, SkillEntry> enry = new HashMap<Skill, SkillEntry>(1);
        enry.put(skill, new SkillEntry(newLevel, (byte) newMasterlevel, -1L, -1));
        changeSkillLevel_Skip(enry, true);
    }

    public boolean getDebugMessage() {
        return DebugMessage;
    }

    public void setDebugMessage(boolean control) {
        DebugMessage = control;
    }

    public boolean getHackMessage() {
        return HackMessage;
    }

    public void setHackMessage(boolean control) {
        HackMessage = control;
    }

    public boolean getWalkDebugMessage() {
        return walkdebug;
    }

    public void setWalkDebugMessage(boolean x) {
        walkdebug = x;
    }

    public boolean getAttackDebugMessage() {
        return attackdebug;
    }

    public void setAttackDebugMessage(boolean x) {
        attackdebug = x;
    }

    public boolean isCygnus() {
        return job >= 1000 && job <= 1512;
    }

    public boolean isKOC() {
        return job >= 1000 && job < 2000;
    }

    public boolean isAdventurer() {
        return job >= 0 && job < 1000;
    }

    public boolean isAran() {
        return job == 2000 || (job >= 2100 && job <= 2112);
    }

    public boolean isPirate() {
        return (job >= 500 && job <= 522) || (job >= 1500 && job <= 1512);
    }

    public boolean isThief() {
        return (job >= 400 && job <= 422) || (job >= 1400 && job <= 1412);
    }

    public boolean isBowman() {
        return (job >= 300 && job <= 322) || (job >= 1300 && job <= 1312) || (job >= 3300 && job <= 3312);
    }

    public boolean isMage() {
        return (job >= 200 && job <= 232) || (job >= 1200 && job <= 1212) || (job >= 3200 && job <= 3212) || (job / 100 == 22);
    }

    public boolean isWarrior() {
        return (job >= 100 && job <= 132) || (job >= 1100 && job <= 1112) || (job >= 2100 && job <= 2112);
    }

    public List<MapleSummon> getAllLinksummon() {
        return this.linksummon;
    }

    public void addLinksummon(MapleSummon x) {
        this.linksummon.add(x);
    }

    public void removeLinksummon(MapleSummon x) {
        if (this.linksummon.size() > 0) {
            this.linksummon.remove(x);
        }
    }

    public byte getBuddyCapacity() {
        return buddylist.getCapacity();
    }

    public void setBuddyCapacity(byte capacity) {
        buddylist.setCapacity(capacity);
        client.sendPacket(BuddylistPacket.updateBuddyCapacity(capacity));
    }

    public MapleMessenger getMessenger() {
        return messenger;
    }

    public void setMessenger(MapleMessenger messenger) {
        this.messenger = messenger;
    }

    public void addCooldown(int skillId, long startTime, long length) {
        coolDowns.put(skillId, new MapleCoolDownValueHolder(skillId, startTime, length));
    }

    public void removeCooldown(int skillId) {
        if (coolDowns.containsKey(skillId)) {
            coolDowns.remove(skillId);
        }
    }

    public boolean skillisCooling(int skillId) {
        return coolDowns.containsKey(skillId);
    }

    public void giveCoolDowns(final int skillid, long starttime, long length) {
        addCooldown(skillid, starttime, length);
    }

    public void giveCoolDowns(final List<MapleCoolDownValueHolder> cooldowns) {
        int time;
        if (cooldowns != null && !cooldowns.isEmpty()) {
            for (MapleCoolDownValueHolder cooldown : cooldowns) {
                coolDowns.put(cooldown.skillId, cooldown);
            }
        } else {
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT SkillID,StartTime,length FROM skills_cooldowns WHERE charid = ?");
                ps.setInt(1, getId());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    if (rs.getLong("length") + rs.getLong("StartTime") - System.currentTimeMillis() <= 0) {
                        continue;
                    }
                    giveCoolDowns(rs.getInt("SkillID"), rs.getLong("StartTime"), rs.getLong("length"));
                }
                ps.close();
                rs.close();
                deleteWhereCharacterId(con, "DELETE FROM skills_cooldowns WHERE charid = ?");

            } catch (SQLException e) {
                System.err.println("Error while retriving cooldown from SQL storage");
            }
        }
    }

    public int getCooldownSize() {
        return coolDowns.size();
    }

    public boolean getcheck_FishingVip() {
        return check_FishingVip;
    }

    public void setcheck_FishingVip(boolean x) {
        check_FishingVip = x;
    }

    public int getDiseaseSize() {
        return diseases.size();
    }

    public List<MapleCoolDownValueHolder> getCooldowns() {
        List<MapleCoolDownValueHolder> ret = new ArrayList<>();
        for (MapleCoolDownValueHolder mc : coolDowns.values()) {
            if (mc != null) {
                ret.add(mc);
            }
        }
        return ret;
    }

    public final List<MapleDiseaseValueHolder> getAllDiseases() {
        return new ArrayList<>(diseases.values());
    }

    public final boolean hasDisease(final MapleDisease dis) {
        return diseases.containsKey(dis);
    }

    public void giveDebuff(final MapleDisease disease, MobSkill skill) {
        giveDebuff(disease, skill.getX(), skill.getDuration(), skill.getSkillId(), skill.getSkillLevel());
    }

    public void giveDebuff(final MapleDisease disease, int x, long duration, int skillid, int level) {
        if (isInvincible()) {
            if (isShowInfo()) {
                showInfo("異常狀態", false, "無敵狀態消除異常狀態 - " + disease.name());
            }
            return;
        }
        if (map != null && !hasDisease(disease)) {
            if (!(disease == MapleDisease.SEDUCE || disease == MapleDisease.STUN || disease == MapleDisease.FLAG)) {
                if (getBuffedValue(MapleBuffStat.HOLY_SHIELD) != null) {
                    return;
                }
            }
            final int mC = getBuffSource(MapleBuffStat.MECH_CHANGE);
            if (mC > 0 && mC != 35121005) { //missile tank can have debuffs
                return; //flamethrower and siege can't
            }
            if (stats.ASR > 0 && Randomizer.nextInt(100) < stats.ASR) {
                return;
            }

            diseases.put(disease, new MapleDiseaseValueHolder(disease, System.currentTimeMillis(), duration - stats.decreaseDebuff));
            client.sendPacket(BuffPacket.giveDebuff(disease, x, skillid, level, (int) duration));
            map.broadcastMessage(this, BuffPacket.giveForeignDebuff(id, disease, skillid, level, x), false);

            if (x > 0 && disease == MapleDisease.POISON) { //poison, subtract all HP
                addHP((int) -(x * ((duration - stats.decreaseDebuff) / 1000)));
            }
        }
    }

    public final void giveSilentDebuff(final List<MapleDiseaseValueHolder> ld) {
        if (ld != null && !ld.isEmpty()) {
            for (final MapleDiseaseValueHolder disease : ld) {
                diseases.put(disease.disease, disease);
            }
        }
    }

    public void dispelDebuff(MapleDisease debuff) {
        if (hasDisease(debuff)) {
            client.sendPacket(BuffPacket.cancelDebuff(debuff));
            map.broadcastMessage(this, BuffPacket.cancelForeignDebuff(id, debuff), false);

            diseases.remove(debuff);
        }
    }

    public void dispelDebuffs() {
        List<MapleDisease> diseasess = new ArrayList<>(diseases.keySet());
        for (MapleDisease d : diseasess) {
            dispelDebuff(d);
        }
    }

    public void cancelAllDebuffs() {
        diseases.clear();
    }

    public void sendNote(String to, String msg) {
        sendNote(to, msg, 0);
    }

    public void sendNote(String to, String msg, int fame) {
        MapleCharacterUtil.sendNote(to, getName(), msg, fame);
    }

    public void showNote() {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM notes WHERE `to`=?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, getName());
            ResultSet rs = ps.executeQuery();
            rs.last();
            int count = rs.getRow();
            rs.first();
            client.sendPacket(MTSCSPacket.showNotes(rs, count));
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Unable to show note" + e);
        }
    }

    public void deleteNote(int id, int fame) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT gift FROM notes WHERE `id`=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt("gift") == fame && fame > 0) { //not exploited! hurray
                    addFame(fame);
                    updateSingleStat(MapleStat.FAME, getFame());
                    client.sendPacket(InfoPacket.getShowFameGain(fame));
                }
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("DELETE FROM notes WHERE `id`=?");
            ps.setInt(1, id);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Unable to delete note" + e);
        }
    }


    /* 武陵道場_處理 */
    public static enum DojoMode {
        簡單(0),
        普通(1),
        困難(2),
        排名(3),
        無(4);

        final int dojoMode;

        private DojoMode(int id) {
            dojoMode = id;
        }

        public int getDojoMode() {
            return dojoMode;
        }
    }

    public DojoMode dojoMode = DojoMode.無;

    public DojoMode getDojoMode() {
        return dojoMode;
    }

    public void setDojoMode(DojoMode mode) {
        dojoMode = mode;
    }

    public DojoMode getDojoMode(int mode) {
        for (DojoMode i : DojoMode.values()) {
            if (i.getDojoMode() == mode) {
                return i;
            }
        }
        return null;
    }

    public int getMulungEnergy() {
        return mulung_energy;
    }

    public void mulung_EnergyModify(boolean inc) {
        if (inc) {
            if (mulung_energy + 100 > 10000) {
                mulung_energy = 10000;
            } else {
                mulung_energy += 100;
            }
        } else {
            mulung_energy = 0;
        }
        client.sendPacket(CWvsContext.MulungEnergy(mulung_energy));
    }

    public void writeMulungEnergy() {
        client.sendPacket(CWvsContext.MulungEnergy(mulung_energy));
    }

    public void writeEnergy(String type, String inc) {
        client.sendPacket(CWvsContext.sendPyramidEnergy(type, inc));
    }

    public void writeStatus(String type, String inc) {
        client.sendPacket(CWvsContext.sendGhostStatus(type, inc));
    }

    public void writePoint(String type, String inc) {
        client.sendPacket(CWvsContext.sendGhostPoint(type, inc));
    }

    public final short getCombo() {
        return combo;
    }

    public void setCombo(final short combo) {
        this.combo = combo;
    }

    public final long getLastCombo() {
        return lastCombo;
    }

    public void setLastCombo(final long combo) {
        this.lastCombo = combo;
    }

    public final long getKeyDownSkill_Time() {
        return keydown_skill;
    }

    public void setKeyDownSkill_Time(final long keydown_skill) {
        this.keydown_skill = keydown_skill;
    }

    public void checkBerserk() { //berserk is special in that it doesn't use worldtimer :)
        if (job != 132 || lastBerserkTime < 0 || lastBerserkTime + 10000 > System.currentTimeMillis()) {
            return;
        }
        final Skill BerserkX = SkillFactory.getSkill(1320006);
        final int skilllevel = getTotalSkillLevel(BerserkX);
        if (skilllevel >= 1 && map != null) {
            lastBerserkTime = System.currentTimeMillis();
            final MapleStatEffect ampStat = BerserkX.getEffect(skilllevel);
            stats.Berserk = stats.getHp() * 100 / stats.getCurrentMaxHp() >= ampStat.getX();
            client.sendPacket(EffectPacket.showOwnBuffEffect(1320006, 1, getLevel(), skilllevel, (byte) (stats.Berserk ? 1 : 0)));
            map.broadcastMessage(this, EffectPacket.showBuffeffect(getId(), 1320006, 1, getLevel(), skilllevel, (byte) (stats.Berserk ? 1 : 0)), false);
        } else {
            lastBerserkTime = -1;
        }
    }

    public String getChalkboard() {
        return chalktext;
    }

    public void setChalkboard(String text) {
        this.chalktext = text;
        if (map != null) {
            if (ServerConstants.log_chalkboard) {
                try {
                    FileoutputUtil.logToFile("logs/聊天/使用黑板.txt", " " + FileoutputUtil.NowTime() + " IP: " + getClient().getSession().remoteAddress().toString().split(":")[0] + " 角色名字: " + getClient().getPlayer().getName() + " 在地圖「" + map.getId() + "-" + map.getMapName() + "」 黑板內容: " + text + "\r\n");
                } catch (Exception ex) {
                }
            }
            map.broadcastMessage(MTSCSPacket.useChalkboard(getId(), text));
        }
    }

    public MapleMount getMount() {
        return mount;
    }

    public int[] getWishlist() {
        return wishlist;
    }

    public void setWishlist(int[] wl) {
        this.wishlist = wl;
        changed_wishlist = true;
    }

    public void clearWishlist() {
        for (int i = 0; i < 10; i++) {
            wishlist[i] = 0;
        }
        changed_wishlist = true;
    }

    public int getWishlistSize() {
        int ret = 0;
        for (int i = 0; i < 10; i++) {
            if (wishlist[i] > 0) {
                ret++;
            }
        }
        return ret;
    }

    public int[] getRocks() {
        return rocks;
    }

    public int getRockSize() {
        int ret = 0;
        for (int i = 0; i < 10; i++) {
            if (rocks[i] != 999999999) {
                ret++;
            }
        }
        return ret;
    }

    public void deleteFromRocks(int map) {
        for (int i = 0; i < 10; i++) {
            if (rocks[i] == map) {
                rocks[i] = 999999999;
                changed_trocklocations = true;
                break;
            }
        }
    }

    public void addRockMap() {
        if (getRockSize() >= 10) {
            return;
        }
        rocks[getRockSize()] = getMapId();
        changed_trocklocations = true;
    }

    public boolean isRockMap(int id) {
        for (int i = 0; i < 10; i++) {
            if (rocks[i] == id) {
                return true;
            }
        }
        return false;
    }

    public int[] getRegRocks() {
        return regrocks;
    }

    public int getRegRockSize() {
        int ret = 0;
        for (int i = 0; i < 5; i++) {
            if (regrocks[i] != 999999999) {
                ret++;
            }
        }
        return ret;
    }

    public void deleteFromRegRocks(int map) {
        for (int i = 0; i < 5; i++) {
            if (regrocks[i] == map) {
                regrocks[i] = 999999999;
                changed_regrocklocations = true;
                break;
            }
        }
    }

    public void addRegRockMap() {
        if (getRegRockSize() >= 5) {
            return;
        }
        regrocks[getRegRockSize()] = getMapId();
        changed_regrocklocations = true;
    }

    public boolean isRegRockMap(int id) {
        for (int i = 0; i < 5; i++) {
            if (regrocks[i] == id) {
                return true;
            }
        }
        return false;
    }

    public int[] getHyperRocks() {
        return hyperrocks;
    }

    public int getHyperRockSize() {
        int ret = 0;
        for (int i = 0; i < 13; i++) {
            if (hyperrocks[i] != 999999999) {
                ret++;
            }
        }
        return ret;
    }

    public void deleteFromHyperRocks(int map) {
        for (int i = 0; i < 13; i++) {
            if (hyperrocks[i] == map) {
                hyperrocks[i] = 999999999;
                changed_hyperrocklocations = true;
                break;
            }
        }
    }

    public void addHyperRockMap() {
        if (getRegRockSize() >= 13) {
            return;
        }
        hyperrocks[getHyperRockSize()] = getMapId();
        changed_hyperrocklocations = true;
    }

    public boolean isHyperRockMap(int id) {
        for (int i = 0; i < 13; i++) {
            if (hyperrocks[i] == id) {
                return true;
            }
        }
        return false;
    }

    public List<LifeMovementFragment> getLastRes() {
        return lastres;
    }

    public void setLastRes(List<LifeMovementFragment> lastres) {
        this.lastres = lastres;
    }

    public void dropDebugMessage(String msg) {
        if (getDebugMessage()) {
            dropMessage(6, msg);
        }
    }

    public void dropMessage(String message) {
        dropMessage(6, message);
    }

    public void dropMessage(int type, String message) {
        switch (type) {
            case -1:
                client.sendPacket(CWvsContext.getTopMsg(message));
                break;
            case -2:
                client.sendPacket(PlayerShopPacket.shopChat(message, 0)); //0 or what
                break;
            case -3:
                client.sendPacket(CField.getChatText(getId(), message, isGM(), 0)); //1 = hide
                break;
            case -4:
                client.sendPacket(CField.getChatText(getId(), message, isGM(), 1)); //1 = hide
                break;
            case -5:
                client.sendPacket(CField.getGameMessage(message, false)); //pink
                break;
            case -6:
                client.sendPacket(CField.getGameMessage(message, true)); //white bg
                break;
            case -7:
                client.sendPacket(CWvsContext.getMidMsg(message, false, 0));
                break;
            case -8:
                client.sendPacket(CWvsContext.getMidMsg(message, true, 0));
                break;
            default:
                client.sendPacket(CWvsContext.serverNotice(type, message));
                break;
        }
    }

    public void showInfo(String caption, boolean pink, String msg) {
        short type = (short) (pink ? 11 : 6);
        if (caption != null && !caption.isEmpty()) {
            msg = "[" + caption + "] " + msg;
        }
        dropMessage(-1, msg);
    }

    public IMaplePlayerShop getPlayerShop() {
        return playerShop;
    }

    public void setPlayerShop(IMaplePlayerShop playerShop) {
        this.playerShop = playerShop;
    }

    public int getConversation() {
        return inst.get();
    }

    public void setConversation(int inst) {
        this.inst.set(inst);
    }

    public int getDirection() {
        return insd.get();
    }

    public void setDirection(int inst) {
        this.insd.set(inst);
    }

    public MapleCarnivalParty getCarnivalParty() {
        return carnivalParty;
    }

    public void setCarnivalParty(MapleCarnivalParty party) {
        carnivalParty = party;
    }

    public void addCP(int ammount) {
        totalCP += ammount;
        availableCP += ammount;
    }

    public void useCP(int ammount) {
        availableCP -= ammount;
    }

    public int getAvailableCP() {
        return availableCP;
    }

    public int getTotalCP() {
        return totalCP;
    }

    public void resetCP() {
        totalCP = 0;
        availableCP = 0;
    }

    public void reloadC() {
        client.sendPacket(CField.getCharInfo(client.getPlayer()));
        client.getPlayer().getMap().removePlayer(client.getPlayer());
        client.getPlayer().getMap().addPlayer(client.getPlayer());
    }

    public void addCarnivalRequest(MapleCarnivalChallenge request) {
        pendingCarnivalRequests.add(request);
    }

    public final MapleCarnivalChallenge getNextCarnivalRequest() {
        return pendingCarnivalRequests.pollLast();
    }

    public void clearCarnivalRequests() {
        pendingCarnivalRequests = new LinkedList<>();
    }

    public void startMonsterCarnival(final int enemyavailable, final int enemytotal) {
        client.sendPacket(MonsterCarnivalPacket.startMonsterCarnival(this, enemyavailable, enemytotal));
    }

    public void CPUpdate(final boolean party, final int available, final int total, final int team) {
        client.sendPacket(MonsterCarnivalPacket.CPUpdate(party, available, total, team));
    }

    public void playerDiedCPQ(final String name, final int lostCP, final int team) {
        client.sendPacket(MonsterCarnivalPacket.playerDiedMessage(name, lostCP, team));
    }

    public void setAchievementFinished(int id) {
        if (!finishedAchievements.contains(id)) {
            finishedAchievements.add(id);
            changed_achievements = true;
        }
    }

    public boolean achievementFinished(int achievementid) {
        return finishedAchievements.contains(achievementid);
    }

    public void finishAchievement(int id) {
        if (!achievementFinished(id)) {
            if (isAlive() && !isClone()) {
                MapleAchievements.getInstance().getById(id).finishAchievement(this);
            }
        }
    }

    public List<Integer> getFinishedAchievements() {
        return finishedAchievements;
    }

    public boolean getCanTalk() {
        return this.canTalk;
    }

    public void canTalk(boolean talk) {
        this.canTalk = talk;
    }

    public double getEXPMod() {
        return stats.expMod;
    }

    public int getDropMod() {
        return stats.dropMod;
    }

    public int getCashMod() {
        return stats.cashMod;
    }

    public int getPoints() {
        return getPoints(this);
    }

    public void setPoints(int p) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps;
            ps = con.prepareStatement("Update accounts set points = ? Where id = ?");
            ps.setInt(1, p);
            ps.setInt(2, getClient().getAccID());
            ps.execute();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("[Points]無法連接資料庫");
        } catch (Exception ex) {
            FilePrinter.printError("MapleCharacter.txt", ex, "setPoints");
            System.err.println("[setPoints]" + ex);
        }
    }

    public int getPoints(MapleCharacter chr) {
        int maxtimes = 10;
        int nowtime = 0;
        int delay = 500;
        boolean error = false;
        int x = 0;
        do {
            nowtime++;
            try {
                Connection con = DatabaseConnection.getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("Select points from Accounts Where id = " + chr.getClient().getAccID());
                while (rs.next()) {
                    int debug = -1;
                    try {
                        debug = rs.getInt("points");
                    } catch (Exception ex) {
                    }
                    if (debug != -1) {
                        x = rs.getInt("points");
                        error = false;
                    } else {
                        error = true;
                    }
                }
                rs.close();
            } catch (SQLException SQL) {
                System.err.println("[getPoints]無法連接資料庫");
            } catch (Exception ex) {
                FilePrinter.printError("MapleCharacter.txt", ex, "getPoints");
                System.err.println("[getPoints]" + ex);
            }
            if (error) {
                try {
                    Thread.sleep(delay);
                } catch (Exception ex) {
                }
            }
        } while (error && (nowtime < maxtimes));
        return x;
    }

    public int getVPoints() {
        return vpoints;
    }

    public void setVPoints(int p) {
        this.vpoints = p;
    }

    public CashShop getCashInventory() {
        return cs;
    }

    public void removeItem(int id, int quantity) {
        MapleInventoryManipulator.removeById(client, GameConstants.getInventoryType(id), id, -quantity, true, false);
        client.sendPacket(InfoPacket.getShowItemGain(id, (short) quantity, true));
    }

    public void removeAll(int id) {
        removeAll(id, false);
    }

    public void removeAll(int id, boolean show) {
        removeAll(id, false, false);
    }

    public void removeAll(int id, boolean show, boolean equip) {
        MapleInventoryType type = GameConstants.getInventoryType(id);
        int possessed = getInventory(type).countById(id);

        if (possessed > 0) {
            MapleInventoryManipulator.removeById(getClient(), type, id, possessed, true, false);
            if (show) {
                getClient().sendPacket(InfoPacket.getShowItemGain(id, (short) -possessed, true));
            }
        }
    }

    public Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> getRings(boolean equip) {
        MapleInventory iv = getInventory(MapleInventoryType.EQUIPPED);
        List<Item> equipped = iv.newList();
        Collections.sort(equipped);
        List<MapleRing> crings = new ArrayList<>(), frings = new ArrayList<>(), mrings = new ArrayList<>();
        MapleRing ring;
        for (Item ite : equipped) {
            Equip item = (Equip) ite;
            if (item.getRing() != null) {
                ring = item.getRing();
                ring.setEquipped(true);
                if (GameConstants.isEffectRing(item.getItemId())) {
                    if (equip) {
                        if (GameConstants.isCrushRing(item.getItemId())) {
                            crings.add(ring);
                        } else if (GameConstants.isFriendshipRing(item.getItemId())) {
                            frings.add(ring);
                        } else if (GameConstants.isMarriageRing(item.getItemId())) {
                            mrings.add(ring);
                        }
                    } else {
                        if (crings.size() == 0 && GameConstants.isCrushRing(item.getItemId())) {
                            crings.add(ring);
                        } else if (frings.size() == 0 && GameConstants.isFriendshipRing(item.getItemId())) {
                            frings.add(ring);
                        } else if (mrings.size() == 0 && GameConstants.isMarriageRing(item.getItemId())) {
                            mrings.add(ring);
                        } //for 3rd person the actual slot doesnt matter, so we'll use this to have both shirt/ring same?
                        //however there seems to be something else behind this, will have to sniff someone with shirt and ring, or more conveniently 3-4 of those
                    }
                }
            }
        }
        if (equip) {
            iv = getInventory(MapleInventoryType.EQUIP);
            for (Item ite : iv.list()) {
                Equip item = (Equip) ite;
                if (item.getRing() != null && GameConstants.isCrushRing(item.getItemId())) {
                    ring = item.getRing();
                    ring.setEquipped(false);
                    if (GameConstants.isFriendshipRing(item.getItemId())) {
                        frings.add(ring);
                    } else if (GameConstants.isCrushRing(item.getItemId())) {
                        crings.add(ring);
                    } else if (GameConstants.isMarriageRing(item.getItemId())) {
                        mrings.add(ring);
                    }
                }
            }
        }
        Collections.sort(frings, new MapleRing.RingComparator());
        Collections.sort(crings, new MapleRing.RingComparator());
        Collections.sort(mrings, new MapleRing.RingComparator());
        return new Triple<>(crings, frings, mrings);
    }

    public int getFH() {
        MapleFoothold fh = getMap().getFootholds().findBelow(getTruePosition());
        if (fh != null) {
            return fh.getId();
        }
        return 0;
    }

    public void startFairySchedule(boolean exp) {
        startFairySchedule(exp, false);
    }

    public void startFairySchedule(boolean exp, boolean equipped) {
        cancelFairySchedule(exp || stats.equippedFairy == 0);
        if (fairyExp <= 0) {
            fairyExp = (byte) stats.equippedFairy;
        }
        if (equipped && fairyExp < stats.equippedFairy * 3 && stats.equippedFairy > 0) {
            dropMessage(5, "您應裝備了精靈吊墜在1小時後經驗獲取將增加到 " + (fairyExp + stats.equippedFairy) + "%");
        }
        lastFairyTime = System.currentTimeMillis();
    }

    public final boolean canFairy(long now) {
        return lastFairyTime > 0 && lastFairyTime + (60 * 60 * 1000) < now;
    }

    public final boolean canHP(long now) {
        if (lastHPTime + 5000 < now) {
            lastHPTime = now;
            return true;
        }
        return false;
    }

    public final boolean canMP(long now) {
        if (lastMPTime + 5000 < now) {
            lastMPTime = now;
            return true;
        }
        return false;
    }

    public final boolean canHPRecover(long now) {
        if (stats.hpRecoverTime > 0 && lastHPTime + stats.hpRecoverTime < now) {
            lastHPTime = now;
            return true;
        }
        return false;
    }

    public final boolean canMPRecover(long now) {
        if (stats.mpRecoverTime > 0 && lastMPTime + stats.mpRecoverTime < now) {
            lastMPTime = now;
            return true;
        }
        return false;
    }

    public final boolean canTrade() {
        if (lastTradeTime + 500 > System.currentTimeMillis()) {
            //  return false;
        }
        lastTradeTime = System.currentTimeMillis();
        return true;
    }

    public void cancelFairySchedule(boolean exp) {
        lastFairyTime = 0;
        if (exp) {
            this.fairyExp = 0;
        }
    }

    public void doFairy() {
        if (fairyExp < stats.equippedFairy * 3 && stats.equippedFairy > 0) {
            fairyExp += stats.equippedFairy;
            dropMessage(5, "精靈吊墜經驗獲取增加到" + fairyExp + "%.");
        }
        if (getGuildId() > 0) {
            World.Guild.gainGP(getGuildId(), 20, id);
            client.sendPacket(InfoPacket.getGPContribution(20));
        }
        traits.get(MapleTraitType.will).addExp(5, this); //willpower every hour
        startFairySchedule(false, true);
    }

    public byte getFairyExp() {
        return fairyExp;
    }

    public int getTeam() {
        return coconutteam;
    }

    public void setTeam(int v) {
        this.coconutteam = v;
    }

    public void spawnPet(byte slot) {
        spawnPet(slot, false, true);
    }

    public void spawnPet(byte slot, boolean lead) {
        spawnPet(slot, lead, true);
    }

    public void spawnPet(byte slot, boolean lead, boolean broadcast) {
        final Item item = getInventory(MapleInventoryType.CASH).getItem(slot);
        if (item == null || item.getItemId() >= 5010000 || item.getItemId() < 5000000) {
            return;
        }
        switch (item.getItemId()) {
            case 5000047:
            case 5000028: {
                final MaplePet pet = MaplePet.createPet(item.getItemId() + 1, MapleInventoryIdentifier.getInstance());
                if (pet != null) {
                    MapleInventoryManipulator.addById(client, item.getItemId() + 1, (short) 1, item.getOwner(), pet, 45, "Evolved from pet " + item.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                    MapleInventoryManipulator.removeFromSlot(client, MapleInventoryType.CASH, slot, (short) 1, false);
                }
                break;
            }
            default: {
                final MaplePet pet = item.getPet();
                if (pet != null && (item.getItemId() != 5000054 || pet.getSecondsLeft() > 0) && (item.getExpiration() == -1 || item.getExpiration() > System.currentTimeMillis())) {
                    if (pet.getSummoned()) { // Already summoned, let's keep it
                        unequipPet(pet, false);
                    } else {
                        final Point pos = getPosition();
                        pet.setPos(pos);
                        try {
                            pet.setFh(getMap().getFootholds().findBelow(pos, true).getId());
                        } catch (NullPointerException e) {
                            pet.setFh(0); //lol, it can be fixed by movement
                        }
                        pet.setStance(0);
                        pet.setSummoned(getPetSlotNext() + 1);
                        addPet(pet);
                        if (broadcast && getMap() != null) {
                            client.sendPacket(PetPacket.updatePet(pet, getInventory(MapleInventoryType.CASH).getItem((short) (byte) pet.getInventoryPosition()), false));
                            getMap().broadcastMessage(this, PetPacket.showPet(this, pet, false, false), true);
                            client.sendPacket(PetPacket.showPetUpdate(this, pet.getUniqueId(), (byte) (pet.getSummonedValue() - 1)));
                            updatePetAuto();
//                            client.sendPacket(PetPacket.petStatUpdate(this));
                        }
                    }
                }
                break;
            }
        }
        client.sendPacket(CWvsContext.enableActions());
    }

    public void clearLinkMid() {
        linkMobs.clear();
        cancelEffectFromBuffStat(MapleBuffStat.HOMING_BEACON);
        cancelEffectFromBuffStat(MapleBuffStat.ARCANE_AIM);
    }

    public int getFirstLinkMid() {
        for (Integer lm : linkMobs.keySet()) {
            return lm;
        }
        return 0;
    }

    public Map<Integer, Integer> getAllLinkMid() {
        return linkMobs;
    }

    public void setLinkMid(int lm, int x) {
        linkMobs.put(lm, x);
    }

    public int getDamageIncrease(int lm) {
        if (linkMobs.containsKey(lm)) {
            return linkMobs.get(lm);
        }
        return 0;
    }

    public boolean isClone() {
        return clone;
    }

    public void setClone(boolean c) {
        this.clone = c;
    }

    public WeakReference<MapleCharacter>[] getClones() {
        return clones;
    }

    public MapleCharacter cloneLooks() {
        MapleClient cs = new MapleClient(null, null, new MockIOSession());

        final int minus = (getId() + Randomizer.nextInt(Integer.MAX_VALUE - getId())); // really randomize it, dont want it to fail

        MapleCharacter ret = new MapleCharacter(true);
        ret.id = minus;
        ret.client = cs;
        ret.exp = 0;
        ret.meso = 0;
        ret.remainingAp = 0;
        ret.fame = 0;
        ret.accountid = client.getAccID();
        ret.anticheat = anticheat;
        ret.name = name;
        ret.level = level;
        ret.fame = fame;
        ret.job = job;
        ret.hair = hair;
        ret.face = face;
        ret.demonMarking = demonMarking;
        ret.skinColor = skinColor;
        ret.monsterbook = monsterbook;
        ret.mount = mount;
        ret.CRand = new PlayerRandomStream();
        ret.gmLevel = gmLevel;
        ret.gender = gender;
        ret.mapid = map.getId();
        ret.map = map;
        ret.setStance(getStance());
        ret.chair = chair;
        ret.itemEffect = itemEffect;
        ret.guildid = guildid;
        ret.currentrep = currentrep;
        ret.totalrep = totalrep;
        ret.stats = stats;
        ret.effects.putAll(effects);
        ret.dispelSummons();
        ret.guildrank = guildrank;
        ret.guildContribution = guildContribution;
        ret.allianceRank = allianceRank;
        ret.setPosition(getTruePosition());
        for (Item equip : getInventory(MapleInventoryType.EQUIPPED).newList()) {
            ret.getInventory(MapleInventoryType.EQUIPPED).addFromDB(equip.copy());
        }
        ret.skillMacros = skillMacros;
        ret.keylayout = keylayout;
        ret.questinfo = questinfo;
        ret.savedLocations = savedLocations;
        ret.wishlist = wishlist;
        ret.buddylist = buddylist;
        ret.keydown_skill = 0;
        ret.lastmonthfameids = lastmonthfameids;
        ret.lastfametime = lastfametime;
        ret.storage = storage;
        ret.cs = this.cs;
        ret.client.setAccountName(client.getAccountName());
        ret.acash = acash;
        ret.maplepoints = maplepoints;
        ret.clone = true;
        ret.client.setChannel(this.client.getChannel());
        while (map.getCharacterById(ret.id) != null || client.getChannelServer().getPlayerStorage().getCharacterById(ret.id) != null) {
            ret.id++;
        }
        ret.client.setPlayer(ret);
        return ret;
    }

    public final void cloneLook() {
        if (clone || inPVP()) {
            return;
        }
        for (int i = 0; i < clones.length; i++) {
            if (clones[i].get() == null) {
                final MapleCharacter newp = cloneLooks();
                map.addPlayer(newp);
                map.broadcastMessage(CField.updateCharLook(newp));
                map.movePlayer(newp, getTruePosition());
                clones[i] = new WeakReference<>(newp);
                return;
            }
        }
    }

    public final void disposeClones() {
        numClones = 0;
        for (int i = 0; i < clones.length; i++) {
            if (clones[i].get() != null) {
                map.removePlayer(clones[i].get());
                if (clones[i].get().getClient() != null) {
                    clones[i].get().getClient().setPlayer(null);
                    clones[i].get().client = null;
                }
                clones[i] = new WeakReference<>(null);
                numClones++;
            }
        }
    }

    public final int getCloneSize() {
        int z = 0;
        for (int i = 0; i < clones.length; i++) {
            if (clones[i].get() != null) {
                z++;
            }
        }
        return z;
    }

    public void spawnClones() {
        if (!isGM()) { //removed tetris piece likely, expired or whatever
            numClones = (byte) (stats.hasClone ? 1 : 0);
        }
        for (int i = 0; i < numClones; i++) {
            cloneLook();
        }
        numClones = 0;
    }

    public byte getNumClones() {
        return numClones;
    }

    public MapleExtractor getExtractor() {
        return extractor;
    }

    public void setExtractor(MapleExtractor me) {
        removeExtractor();
        this.extractor = me;
    }

    public void removeExtractor() {
        if (extractor != null) {
            map.broadcastMessage(CField.removeExtractor(this.id));
            map.removeMapObject(extractor);
            extractor = null;
        }
    }

    public final void spawnSavedPets() {
        for (int i = 0; i < petStore.length; i++) {
            if (petStore[i] > -1) {
                spawnPet(petStore[i], false, false);
            }
        }
//        if (GameConstants.GMS) {
//            client.sendPacket(PetPacket.petStatUpdate(this));
//        }
        petStore = new byte[]{-1, -1, -1};
    }

    public final byte[] getPetStores() {
        return petStore;
    }

    public void resetStats(final int str, final int dex, final int int_, final int luk) {
        Map<MapleStat, Integer> stat = new EnumMap<>(MapleStat.class);
        int total = stats.getStr() + stats.getDex() + stats.getLuk() + stats.getInt() + getRemainingAp();

        total -= str;
        stats.str = (short) str;

        total -= dex;
        stats.dex = (short) dex;

        total -= int_;
        stats.int_ = (short) int_;

        total -= luk;
        stats.luk = (short) luk;

        setRemainingAp((short) total);
        stats.recalcLocalStats(this);
        stat.put(MapleStat.STR, str);
        stat.put(MapleStat.DEX, dex);
        stat.put(MapleStat.INT, int_);
        stat.put(MapleStat.LUK, luk);
        stat.put(MapleStat.AVAILABLEAP, total);

        client.sendPacket(CWvsContext.updatePlayerStats(stat, false, this));
    }

    public Event_PyramidSubway getPyramidSubway() {
        return pyramidSubway;
    }

    public void setPyramidSubway(Event_PyramidSubway ps) {
        this.pyramidSubway = ps;
    }

    public byte getSubcategory() {
        if (job >= 430 && job <= 434) {
            return 1; //dont set it
        }
        if (GameConstants.isCannon(job) || job == 1) {
            return 2;
        }
        if (job != 0 && job != 400) {
            return 0;
        }
        return subcategory;
    }

    public void setSubcategory(int z) {
        this.subcategory = (byte) z;
    }

    public int itemQuantity(final int itemid) {
        return getInventory(GameConstants.getInventoryType(itemid)).countById(itemid);
    }

    public RockPaperScissors getRPS() {
        return rps;
    }

    public void setRPS(RockPaperScissors rps) {
        this.rps = rps;
    }

    public long getNextConsume() {
        return nextConsume;
    }

    public void setNextConsume(long nc) {
        this.nextConsume = nc;
    }

    public int getRank() {
        return rank;
    }

    public int getRankMove() {
        return rankMove;
    }

    public int getJobRank() {
        return jobRank;
    }

    public int getJobRankMove() {
        return jobRankMove;
    }

    public void changeChannel(final int channel) {
        final ChannelServer toch = ChannelServer.getInstance(channel);

        if (channel == client.getChannel() || toch == null || toch.isShutdown()) {
            client.sendPacket(CField.serverBlocked(1));
            return;
        }

        try {
            this.saveToDB(false, false);
        } catch (Exception ex) {
        }

        changeRemoval();

        final ChannelServer ch = ChannelServer.getInstance(client.getChannel());
        if (getMessenger() != null) {
            World.Messenger.silentLeaveMessenger(getMessenger().getId(), new MapleMessengerCharacter(this));
        }
        PlayerBuffStorage.addBuffsToStorage(getId(), getAllBuffs());
        PlayerBuffStorage.addCooldownsToStorage(getId(), getCooldowns());
        PlayerBuffStorage.addDiseaseToStorage(getId(), getAllDiseases());
        World.ChannelChange_Data(client, this, channel);
        ch.removePlayer(this);
        client.updateLoginState(MapleClient.CHANGE_CHANNEL, client.getSessionIPAddress());
        final String s = client.getSessionIPAddress();
        LoginServer.addIPAuth(s.substring(s.indexOf('/') + 1, s.length()));
        client.sendPacket(CField.getChannelChange(client, toch.getSocket().split(":")[0], Integer.parseInt(toch.getSocket().split(":")[1])));
		
        getMap().removePlayer(this);

        client.setPlayer(null);
        client.setReceiving(false);
    }

    public void expandInventory(byte type, int amount) {
        final MapleInventory inv = getInventory(MapleInventoryType.getByType(type));
        inv.addSlot((byte) amount);
        client.sendPacket(InventoryPacket.getSlotUpdate(type, (byte) inv.getSlotLimit()));
    }

    public boolean allowedToTarget(MapleCharacter other) {
        return other != null && (!other.isHidden() || getGMLevel() >= other.getGMLevel());
    }

    public int getFollowId() {
        return followid;
    }

    public void setFollowId(int fi) {
        this.followid = fi;
        if (fi == 0) {
            this.followinitiator = false;
            this.followon = false;
        }
    }

    public boolean isFollowOn() {
        return followon;
    }

    public void setFollowOn(boolean fi) {
        this.followon = fi;
    }

    public boolean isFollowInitiator() {
        return followinitiator;
    }

    public void setFollowInitiator(boolean fi) {
        this.followinitiator = fi;
    }

    public void checkFollow() {
        if (followid <= 0) {
            return;
        }
        if (followon) {
            map.broadcastMessage(CField.followEffect(id, 0, null));
            map.broadcastMessage(CField.followEffect(followid, 0, null));
        }
        MapleCharacter tt = map.getCharacterById(followid);
        client.sendPacket(CField.getFollowMessage("跟隨取消。"));
        if (tt != null) {
            tt.setFollowId(0);
            tt.getClient().sendPacket(CField.getFollowMessage("跟隨取消。"));
        }
        setFollowId(0);
    }

    public int getMarriageId() {
        return marriageId;
    }

    public void setMarriageId(final int mi) {
        this.marriageId = mi;
    }

    public int getMarriageItemId() {
        return marriageItemId;
    }

    public void setMarriageItemId(final int mi) {
        this.marriageItemId = mi;
    }

    public boolean isStaff() {
        return this.gmLevel > ServerConstants.PlayerGMRank.普通玩家.getLevel();
    }

    // TODO: gvup, vic, lose, draw, VR
    public boolean startPartyQuest(final int questid) {
        boolean ret = false;
        MapleQuest q = MapleQuest.getInstance(questid);
        if (q == null || !q.isPartyQuest()) {
            return false;
        }
        if (!quests.containsKey(q) || !questinfo.containsKey(questid)) {
            final MapleQuestStatus status = getQuestNAdd(q);
            status.setStatus((byte) 1);
            updateQuest(status);
            switch (questid) {
                case 1300:
                case 1301:
                case 1302: //carnival, ariants.
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have=0;rank=F;try=0;cmp=0;CR=0;VR=0;gvup=0;vic=0;lose=0;draw=0");
                    break;
                case 1303: //ghost pq
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have=0;have1=0;rank=F;try=0;cmp=0;CR=0;VR=0;vic=0;lose=0");
                    break;
                case 1204: //herb town pq
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have0=0;have1=0;have2=0;have3=0;rank=F;try=0;cmp=0;CR=0;VR=0");
                    break;
                case 1206: //ellin pq
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have0=0;have1=0;rank=F;try=0;cmp=0;CR=0;VR=0");
                    break;
                default:
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have=0;rank=F;try=0;cmp=0;CR=0;VR=0");
                    break;
            }
            ret = true;
        } //started the quest.
        return ret;
    }

    public String getOneInfo(final int questid, final String key) {
        if (!questinfo.containsKey(questid) || key == null || MapleQuest.getInstance(questid) == null /*|| !MapleQuest.getInstance(questid).isPartyQuest()*/) {
            return null;
        }
        final String[] split = questinfo.get(questid).split(";");
        for (String x : split) {
            final String[] split2 = x.split("="); //should be only 2
            if (split2.length == 2 && split2[0].equals(key)) {
                return split2[1];
            }
        }
        return null;
    }

    public void updateOneInfo(final int questid, final String key, final String value) {
        if (!questinfo.containsKey(questid) || key == null || value == null || MapleQuest.getInstance(questid) == null || !MapleQuest.getInstance(questid).isPartyQuest()) {
            return;
        }
        final String[] split = questinfo.get(questid).split(";");
        boolean changed = false;
        final StringBuilder newQuest = new StringBuilder();
        for (String x : split) {
            final String[] split2 = x.split("="); //should be only 2
            if (split2.length != 2) {
                continue;
            }
            if (split2[0].equals(key)) {
                newQuest.append(key).append("=").append(value);
            } else {
                newQuest.append(x);
            }
            newQuest.append(";");
            changed = true;
        }

        updateInfoQuest(questid, changed ? newQuest.toString().substring(0, newQuest.toString().length() - 1) : newQuest.toString());
    }

    public void recalcPartyQuestRank(final int questid) {
        if (MapleQuest.getInstance(questid) == null || !MapleQuest.getInstance(questid).isPartyQuest()) {
            return;
        }
        if (!startPartyQuest(questid)) {
            final String oldRank = getOneInfo(questid, "rank");
            if (oldRank == null || oldRank.equals("S")) {
                return;
            }
            String newRank = null;
            if (oldRank.equals("A")) {
                newRank = "S";
            } else if (oldRank.equals("B")) {
                newRank = "A";
            } else if (oldRank.equals("C")) {
                newRank = "B";
            } else if (oldRank.equals("D")) {
                newRank = "C";
            } else if (oldRank.equals("F")) {
                newRank = "D";
            } else {
                return;
            }
            final List<Pair<String, Pair<String, Integer>>> questInfo = MapleQuest.getInstance(questid).getInfoByRank(newRank);
            if (questInfo == null) {
                return;
            }
            for (Pair<String, Pair<String, Integer>> q : questInfo) {
                boolean found = false;
                final String val = getOneInfo(questid, q.right.left);
                if (val == null) {
                    return;
                }
                int vall = 0;
                try {
                    vall = Integer.parseInt(val);
                } catch (NumberFormatException e) {
                    return;
                }
                if (q.left.equals("less")) {
                    found = vall < q.right.right;
                } else if (q.left.equals("more")) {
                    found = vall > q.right.right;
                } else if (q.left.equals("equal")) {
                    found = vall == q.right.right;
                }
                if (!found) {
                    return;
                }
            }
            //perfectly safe
            updateOneInfo(questid, "rank", newRank);
        }
    }

    public void tryPartyQuest(final int questid) {
        if (MapleQuest.getInstance(questid) == null || !MapleQuest.getInstance(questid).isPartyQuest()) {
            return;
        }
        try {
            startPartyQuest(questid);
            pqStartTime = System.currentTimeMillis();
            updateOneInfo(questid, "try", String.valueOf(Integer.parseInt(getOneInfo(questid, "try")) + 1));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("tryPartyQuest error");
        }
    }

    public void endPartyQuest(final int questid) {
        if (MapleQuest.getInstance(questid) == null || !MapleQuest.getInstance(questid).isPartyQuest()) {
            return;
        }
        try {
            startPartyQuest(questid);
            if (pqStartTime > 0) {
                final long changeTime = System.currentTimeMillis() - pqStartTime;
                final int mins = (int) (changeTime / 1000 / 60), secs = (int) (changeTime / 1000 % 60);
                final int mins2 = Integer.parseInt(getOneInfo(questid, "min"));
                if (mins2 <= 0 || mins < mins2) {
                    updateOneInfo(questid, "min", String.valueOf(mins));
                    updateOneInfo(questid, "sec", String.valueOf(secs));
                    updateOneInfo(questid, "date", FileoutputUtil.CurrentReadable_Date());
                }
                final int newCmp = Integer.parseInt(getOneInfo(questid, "cmp")) + 1;
                updateOneInfo(questid, "cmp", String.valueOf(newCmp));
                updateOneInfo(questid, "CR", String.valueOf((int) Math.ceil((newCmp * 100.0) / Integer.parseInt(getOneInfo(questid, "try")))));
                recalcPartyQuestRank(questid);
                pqStartTime = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("endPartyQuest error");
        }

    }

    public void havePartyQuest(final int itemId) {
        int questid = 0, index = -1;
        switch (itemId) {
            case 1002798:
                questid = 1200; //henesys
                break;
            case 1072369:
                questid = 1201; //kerning
                break;
            case 1022073:
                questid = 1202; //ludi
                break;
            case 1082232:
                questid = 1203; //orbis
                break;
            case 1002571:
            case 1002572:
            case 1002573:
            case 1002574:
                questid = 1204; //herbtown
                index = itemId - 1002571;
                break;
            case 1102226:
                questid = 1303; //ghost
                break;
            case 1102227:
                questid = 1303; //ghost
                index = 0;
                break;
            case 1122010:
                questid = 1205; //magatia
                break;
            case 1032061:
            case 1032060:
                questid = 1206; //ellin
                index = itemId - 1032060;
                break;
            case 3010018:
                questid = 1300; //ariant
                break;
            case 1122007:
                questid = 1301; //carnival
                break;
            case 1122058:
                questid = 1302; //carnival2
                break;
            default:
                return;
        }
        if (MapleQuest.getInstance(questid) == null || !MapleQuest.getInstance(questid).isPartyQuest()) {
            return;
        }
        startPartyQuest(questid);
        updateOneInfo(questid, "have" + (index == -1 ? "" : index), "1");
    }

    public void resetStatsByJob(boolean beginnerJob) {
        int baseJob = (beginnerJob ? (job % 1000) : (((job % 1000) / 100) * 100)); //1112 -> 112 -> 1 -> 100
        boolean UA = getQuestNoAdd(MapleQuest.getInstance(GameConstants.ULT_EXPLORER)) != null;
        switch (baseJob) {
            case 100:
                //first job = warrior
                resetStats(UA ? 4 : 35, 4, 4, 4);
                break;
            case 200:
            case 210:
            case 2200:
                resetStats(4, 4, UA ? 4 : 20, 4);
                break;
            case 300:
            case 400:
            case 430:
                resetStats(4, UA ? 4 : 25, 4, 4);
                break;
            case 500:
                resetStats(4, UA ? 4 : 20, 4, 4);
                break;
            case 0:
                resetStats(4, 4, 4, 4);
                break;
            default:
                break;
        }
    }

    public boolean hasSummon() {
        return hasSummon;
    }

    public void setHasSummon(boolean summ) {
        this.hasSummon = summ;
    }

    public void removeDoor() {
        final MapleDoor door = getDoors().iterator().next();
        for (final MapleCharacter chr : door.getTarget().getCharactersThreadsafe()) {
            door.sendDestroyData(chr.getClient());
        }
        for (final MapleCharacter chr : door.getTown().getCharactersThreadsafe()) {
            door.sendDestroyData(chr.getClient());
        }
        for (final MapleDoor destroyDoor : getDoors()) {
            door.getTarget().removeMapObject(destroyDoor);
            door.getTown().removeMapObject(destroyDoor);
        }
        clearDoors();
    }

    public void removeMechDoor() {
        for (final MechDoor destroyDoor : getMechDoors()) {
            for (final MapleCharacter chr : getMap().getCharactersThreadsafe()) {
                destroyDoor.sendDestroyData(chr.getClient());
            }
            getMap().removeMapObject(destroyDoor);
        }
        clearMechDoors();
    }

    public void changeRemoval() {
        changeRemoval(false);
    }

    public void changeRemoval(boolean dc) {
        if (getCheatTracker() != null && dc) {
            getCheatTracker().dispose();
        }
        removeFamiliar();
        dispelSummons();
        if (!dc) {
            cancelEffectFromBuffStat(MapleBuffStat.SOARING);
            cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
            cancelEffectFromBuffStat(MapleBuffStat.MECH_CHANGE);
            cancelEffectFromBuffStat(MapleBuffStat.RECOVERY);
        }
        if (getPyramidSubway() != null) {
            getPyramidSubway().dispose(this);
        }
        if (playerShop != null && !dc) {
            playerShop.removeVisitor(this);
            if (playerShop.isOwner(this)) {
                playerShop.setOpen(true);
            }
        }
        if (!getDoors().isEmpty()) {
            removeDoor();
        }
        if (!getMechDoors().isEmpty()) {
            removeMechDoor();
        }
        disposeClones();
        NPCScriptManager.getInstance().dispose(client);
        cancelFairySchedule(false);
    }

    public void updateTick(int newTick) {
        //anticheat.updateTick(newTick);
    }

    public boolean canUseFamilyBuff(MapleFamilyBuff buff) {
        final MapleQuestStatus stat = getQuestNoAdd(MapleQuest.getInstance(buff.questID));
        if (stat == null) {
            return true;
        }
        if (stat.getCustomData() == null) {
            stat.setCustomData("0");
        }
        return Long.parseLong(stat.getCustomData()) + (24 * 3600000) < System.currentTimeMillis();
    }

    public void useFamilyBuff(MapleFamilyBuff buff) {
        final MapleQuestStatus stat = getQuestNAdd(MapleQuest.getInstance(buff.questID));
        stat.setCustomData(String.valueOf(System.currentTimeMillis()));
    }

    public List<Integer> usedBuffs() {
        //assume count = 1
        List<Integer> used = new ArrayList<>();
        MapleFamilyBuff[] z = MapleFamilyBuff.values();
        for (int i = 0; i < z.length; i++) {
            if (!canUseFamilyBuff(z[i])) {
                used.add(i);
            }
        }
        return used;
    }

    public String getTeleportName() {
        return teleportname;
    }

    public void setTeleportName(final String tname) {
        teleportname = tname;
    }

    public int getNoJuniors() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getNoJuniors();
    }

    public MapleFamilyCharacter getMFC() {
        return mfc;
    }

    public void makeMFC(final int familyid, final int seniorid, final int junior1, final int junior2) {
        if (familyid > 0) {
            MapleFamily f = World.Family.getFamily(familyid);
            if (f == null) {
                mfc = null;
            } else {
                mfc = f.getMFC(id);
                if (mfc == null) {
                    mfc = f.addFamilyMemberInfo(this, seniorid, junior1, junior2);
                }
                if (mfc.getSeniorId() != seniorid) {
                    mfc.setSeniorId(seniorid);
                }
                if (mfc.getJunior1() != junior1) {
                    mfc.setJunior1(junior1);
                }
                if (mfc.getJunior2() != junior2) {
                    mfc.setJunior2(junior2);
                }
            }
        } else {
            mfc = null;
        }
    }

    public void setFamily(final int newf, final int news, final int newj1, final int newj2) {
        if (mfc == null || newf != mfc.getFamilyId() || news != mfc.getSeniorId() || newj1 != mfc.getJunior1() || newj2 != mfc.getJunior2()) {
            makeMFC(newf, news, newj1, newj2);
        }
    }

    public int maxBattleshipHP(int skillid) {
        return (getTotalSkillLevel(skillid) * 5000) + ((getLevel() - 120) * 3000);
    }

    public int currentBattleshipHP() {
        return battleshipHP;
    }

    public void setBattleshipHP(int v) {
        this.battleshipHP = v;
    }

    public void decreaseBattleshipHP() {
        this.battleshipHP--;
    }

    public int getGachExp() {
        return gachexp;
    }

    public void setGachExp(int ge) {
        this.gachexp = ge;
    }

    public boolean isInBlockedMap() {
        if (!isAlive() || getPyramidSubway() != null || getMap().getSquadByMap() != null || getEventInstance() != null || getMap().getEMByMap() != null) {
            return true;
        }
        if ((getMapId() >= 680000210 && getMapId() <= 680000502) || (getMapId() / 10000 == 92502 && getMapId() >= 925020100) || (getMapId() / 10000 == 92503) || getMapId() == ServerConstants.JAIL) {
            return true;
        }
        for (int i : GameConstants.blockedMaps) {
            if (getMapId() == i) {
                return true;
            }
        }
        return false;
    }

    public boolean isInTownMap() {
        if (hasBlockedInventory() || !getMap().isTown() || FieldLimitType.VipRock.check(getMap().getFieldLimit()) || getEventInstance() != null) {
            return false;
        }
        for (int i : GameConstants.blockedMaps) {
            if (getMapId() == i) {
                return false;
            }
        }
        return true;
    }

    public boolean hasBlockedInventory() {
        return !isAlive() || getTrade() != null || getConversation() > 0 || getDirection() >= 0 || getPlayerShop() != null || getBattle() != null || map == null;
    }

    public void startPartySearch(final List<Integer> jobs, final int maxLevel, final int minLevel, final int membersNeeded) {
        for (MapleCharacter chr : map.getCharacters()) {
            if (chr.getId() != id && chr.getParty() == null && chr.getLevel() >= minLevel && chr.getLevel() <= maxLevel && (jobs.isEmpty() || jobs.contains(Integer.valueOf(chr.getJob()))) && (isGM() || !chr.isGM())) {
                if (party != null && party.getMembers().size() < 6 && party.getMembers().size() < membersNeeded) {
                    chr.setParty(party);
                    World.Party.updateParty(party.getId(), PartyOperation.JOIN, new MaplePartyCharacter(chr));
                    chr.receivePartyMemberHP();
                    chr.updatePartyMemberHP();
                } else {
                    break;
                }
            }
        }
    }

    public Battler getBattler(int pos) {
        return battlers[pos];
    }

    public Battler[] getBattlers() {
        return battlers;
    }

    public List<Battler> getBoxed() {
        return boxed;
    }

    public PokemonBattle getBattle() {
        return battle;
    }

    public void setBattle(PokemonBattle b) {
        this.battle = b;
    }

    public int countBattlers() {
        int ret = 0;
        for (int i = 0; i < battlers.length; i++) {
            if (battlers[i] != null) {
                ret++;
            }
        }
        return ret;
    }

    public void changedBattler() {
        changed_pokemon = true;
    }

    public void makeBattler(int index, int monsterId) {
        final MapleMonsterStats mons = MapleLifeFactory.getMonsterStats(monsterId);
        this.battlers[index] = new Battler(mons);
        this.battlers[index].setCharacterId(id);
        changed_pokemon = true;
        getMonsterBook().monsterCaught(client, monsterId, mons.getName());
    }

    public boolean removeBattler(int ind) {
        if (countBattlers() <= 1) {
            return false;
        }
        if (ind == battlers.length) {
            this.battlers[ind] = null;
        } else {
            for (int i = ind; i < battlers.length; i++) {
                this.battlers[i] = ((i + 1) == battlers.length ? null : this.battlers[i + 1]);
            }
        }
        changed_pokemon = true;
        return true;
    }

    public int getChallenge() {
        return challenge;
    }

    public void setChallenge(int c) {
        this.challenge = c;
    }

    public short getFatigue() {
        return fatigue;
    }

    public void setFatigue(int j) {
        this.fatigue = (short) Math.max(0, j);
        updateSingleStat(MapleStat.FATIGUE, this.fatigue);
    }

    public void fakeRelog() {
        client.sendPacket(CField.getCharInfo(this));
        final MapleMap mapp = getMap();
        mapp.setCheckStates(false);
        mapp.removePlayer(this);
        mapp.addPlayer(this);
        mapp.setCheckStates(true);

        client.sendPacket(CWvsContext.getFamiliarInfo(this));
    }

    public boolean canSummon() {
        return canSummon(5000);
    }

    public boolean canSummon(int g) {
        if (lastSummonTime + g < System.currentTimeMillis()) {
            lastSummonTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public void SetIntNoRecord(int questID, String point) {
        final MapleQuestStatus stat = getQuestNoAdd(MapleQuest.getInstance(questID));
        if (stat == null || stat.getCustomData() == null) {
            stat.setCustomData("0");
        }
        stat.setCustomData(point);
    }

    public int getIntNoRecord(int questID) {
        final MapleQuestStatus stat = getQuestNoAdd(MapleQuest.getInstance(questID));
        if (stat == null || stat.getCustomData() == null) {
            return 0;
        }
        return Integer.parseInt(stat.getCustomData());
    }

    public int getIntRecord(int questID) {
        final MapleQuestStatus stat = getQuestNAdd(MapleQuest.getInstance(questID));
        if (stat.getCustomData() == null) {
            stat.setCustomData("0");
        }
        return Integer.parseInt(stat.getCustomData());
    }

    public void updatePetAuto() {
        if (getIntNoRecord(GameConstants.HP_ITEM) > 0) {
            client.sendPacket(CField.petAutoHP(getIntRecord(GameConstants.HP_ITEM)));
        }
        if (getIntNoRecord(GameConstants.MP_ITEM) > 0) {
            client.sendPacket(CField.petAutoMP(getIntRecord(GameConstants.MP_ITEM)));
        }
        if (getIntNoRecord(GameConstants.POT_ITEM) > 0) {
            client.sendPacket(CField.petAutoCure(getIntNoRecord(GameConstants.POT_ITEM)));
        }
    }

    public void sendEnglishQuiz(String msg) {
        //client.sendPacket(CField.englishQuizMsg(msg));
    }

    public void setChangeTime() {
        mapChangeTime = System.currentTimeMillis();
    }

    public long getChangeTime() {
        return mapChangeTime;
    }

    public Map<ReportType, Integer> getReports() {
        return reports;
    }

    public void addReport(ReportType type) {
        Integer value = reports.get(type);
        reports.put(type, value == null ? 1 : (value + 1));
        changed_reports = true;
    }

    public void clearReports(ReportType type) {
        reports.remove(type);
        changed_reports = true;
    }

    public void clearReports() {
        reports.clear();
        changed_reports = true;
    }

    public final int getReportPoints() {
        int ret = 0;
        for (Integer entry : reports.values()) {
            ret += entry;
        }
        return ret;
    }

    public final String getReportSummary() {
        final StringBuilder ret = new StringBuilder();
        final List<Pair<ReportType, Integer>> offenseList = new ArrayList<>();
        for (final Entry<ReportType, Integer> entry : reports.entrySet()) {
            offenseList.add(new Pair<>(entry.getKey(), entry.getValue()));
        }
        Collections.sort(offenseList, new Comparator<Pair<ReportType, Integer>>() {

            @Override
            public final int compare(final Pair<ReportType, Integer> o1, final Pair<ReportType, Integer> o2) {
                final int thisVal = o1.getRight();
                final int anotherVal = o2.getRight();
                return (thisVal < anotherVal ? 1 : (thisVal == anotherVal ? 0 : -1));
            }
        });
        for (int x = 0; x < offenseList.size(); x++) {
            ret.append(StringUtil.makeEnumHumanReadable(offenseList.get(x).left.name()));
            ret.append(": ");
            ret.append(offenseList.get(x).right);
            ret.append(" ");
        }
        return ret.toString();
    }

    public short getScrolledPosition() {
        return scrolledPosition;
    }

    public void setScrolledPosition(short s) {
        this.scrolledPosition = s;
    }

    public MapleTrait getTrait(MapleTraitType t) {
        return traits.get(t);
    }

    public void forceCompleteQuest(int id) {
        MapleQuest.getInstance(id).forceComplete(this, 9270035); //troll
    }

    public List<Integer> getExtendedSlots() {
        return extendedSlots;
    }

    public int getExtendedSlot(int index) {
        if (extendedSlots.size() <= index || index < 0) {
            return -1;
        }
        return extendedSlots.get(index);
    }

    public void changedExtended() {
        changed_extendedSlots = true;
    }

    public MapleAndroid getAndroid() {
        return android;
    }

    public void setAndroid(MapleAndroid a) {
        this.android = a;
        if (map != null && a != null) {
            map.broadcastMessage(CField.spawnAndroid(this, a));
            map.broadcastMessage(CField.showAndroidEmotion(this.getId(), Randomizer.nextInt(17) + 1));
        }
    }

    public void updateAndroid(short pos, int itemId) {
        if (map != null) {
            map.broadcastMessage(CField.updateAndroidLook(this.getId(), pos, itemId));
        }
    }

    public void removeAndroid() {
        if (map != null) {
            map.broadcastMessage(CField.deactivateAndroid(this.id));
        }
        android = null;
    }

    public List<Item> getRebuy() {
        return rebuy;
    }

    public void setRebuy(List<Item> rebuy) {
        this.rebuy = rebuy;
    }

    public void setClearRebuy() {
        if (this.rebuy != null) {
            this.rebuy.removeAll(this.rebuy);
        }
    }

    public Map<Integer, MonsterFamiliar> getFamiliars() {
        return familiars;
    }

    public MonsterFamiliar getSummonedFamiliar() {
        return summonedFamiliar;
    }

    public void removeFamiliar() {
        if (summonedFamiliar != null && map != null) {
            removeVisibleFamiliar();
        }
        summonedFamiliar = null;
    }

    public void removeVisibleFamiliar() {
        getMap().removeMapObject(summonedFamiliar);
        removeVisibleMapObject(summonedFamiliar);
        getMap().broadcastMessage(CField.removeFamiliar(this.getId()));
        anticheat.resetFamiliarAttack();
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        cancelEffect(ii.getItemEffect(ii.getFamiliar(summonedFamiliar.getFamiliar()).passive), false, System.currentTimeMillis());
    }

    public void spawnFamiliar(MonsterFamiliar mf) {
        summonedFamiliar = mf;

        mf.setStance(0);
        mf.setPosition(getPosition());
        mf.setFh(getFH());
        addVisibleMapObject(mf);
        getMap().spawnFamiliar(mf);

        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final MapleStatEffect eff = ii.getItemEffect(ii.getFamiliar(summonedFamiliar.getFamiliar()).passive);
        if (eff != null && eff.getInterval() <= 0 && eff.makeChanceResult()) { //i think this is actually done through a recv, which is ATTACK_FAMILIAR +1
            eff.applyTo(this);
        }
        lastFamiliarEffectTime = System.currentTimeMillis();
    }

    public final boolean canFamiliarEffect(long now, MapleStatEffect eff) {
        return lastFamiliarEffectTime > 0 && lastFamiliarEffectTime + eff.getInterval() < now;
    }

    public void doFamiliarSchedule(long now) {
        if (familiars == null) {
            return;
        }
        for (MonsterFamiliar mf : familiars.values()) {
            if (summonedFamiliar != null && summonedFamiliar.getId() == mf.getId()) {
                mf.addFatigue(this, 5);
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final MapleStatEffect eff = ii.getItemEffect(ii.getFamiliar(summonedFamiliar.getFamiliar()).passive);
                if (eff != null && eff.getInterval() > 0 && canFamiliarEffect(now, eff) && eff.makeChanceResult()) {
                    eff.applyTo(this);
                }
            } else if (mf.getFatigue() > 0) {
                mf.setFatigue(Math.max(0, mf.getFatigue() - 5));
            }
        }
    }

    public MapleImp[] getImps() {
        return imps;
    }

    public void sendImp() {
        for (int i = 0; i < imps.length; i++) {
            if (imps[i] != null) {
                client.sendPacket(CWvsContext.updateImp(imps[i], ImpFlag.SUMMONED.getValue(), i, true));
            }
        }
    }

    public int getBattlePoints() {
        return pvpPoints;
    }

    public void setBattlePoints(int p) {
        if (p != pvpPoints) {
            client.sendPacket(InfoPacket.getBPMsg(p - pvpPoints));
            updateSingleStat(MapleStat.BATTLE_POINTS, p);
        }
        this.pvpPoints = p;
    }

    public int getTotalBattleExp() {
        return pvpExp;
    }

    public void setTotalBattleExp(int p) {
        final int previous = pvpExp;
        this.pvpExp = p;
        if (p != previous) {
            stats.recalcPVPRank(this);

            updateSingleStat(MapleStat.BATTLE_EXP, stats.pvpExp);
            updateSingleStat(MapleStat.BATTLE_RANK, stats.pvpRank);
        }
    }

    public void changeTeam(int newTeam) {
        this.coconutteam = newTeam;

        if (inPVP()) {
            client.sendPacket(CField.getPVPTransform(newTeam + 1));
            map.broadcastMessage(CField.changeTeam(id, newTeam + 1));
        } else {
            client.sendPacket(CField.showEquipEffect(newTeam));
        }
    }

    public void disease(int type, int level) {
        if (MapleDisease.getBySkill(type) == null) {
            return;
        }
        chair = 0;
        client.sendPacket(CField.cancelChair(-1));
        map.broadcastMessage(this, CField.showChair(id, 0), false);
        giveDebuff(MapleDisease.getBySkill(type), MobSkillFactory.getMobSkill(type, level));
    }

    public boolean inPVP() {
        return eventInstance != null && eventInstance.getName().startsWith("PVP");
    }

    public boolean inAzwan() {
        return mapid >= 262020000 && mapid < 262023000;
    }

    public void clearAllCooldowns() {
        for (MapleCoolDownValueHolder m : getCooldowns()) {
            final int skil = m.skillId;
            removeCooldown(skil);
            client.sendPacket(CField.skillCooldown(skil, 0));
        }
    }

    public Pair<Double, Boolean> modifyDamageTaken(double damage, MapleMapObject attacke) {
        Pair<Double, Boolean> ret = new Pair<>(damage, false);
        if (damage <= 0) {
            return ret;
        }
        if (stats.ignoreDAMr > 0 && Randomizer.nextInt(100) < stats.ignoreDAMr_rate) {
            damage -= Math.floor((stats.ignoreDAMr * damage) / 100.0f);
        }
        if (stats.ignoreDAM > 0 && Randomizer.nextInt(100) < stats.ignoreDAM_rate) {
            damage -= stats.ignoreDAM;
        }
        final Integer div = getBuffedValue(MapleBuffStat.DIVINE_SHIELD);
        final Integer div2 = getBuffedValue(MapleBuffStat.HOLY_MAGIC_SHELL);
        if (div2 != null) {
            if (div2 <= 0) {
                cancelEffectFromBuffStat(MapleBuffStat.HOLY_MAGIC_SHELL);
            } else {
                setBuffedValue(MapleBuffStat.HOLY_MAGIC_SHELL, div2 - 1);
                damage = 0;
            }
        } else if (div != null) {
            if (div <= 0) {
                cancelEffectFromBuffStat(MapleBuffStat.DIVINE_SHIELD);
            } else {
                setBuffedValue(MapleBuffStat.DIVINE_SHIELD, div - 1);
                damage = 0;
            }
        }
        MapleStatEffect barrier = getStatForBuff(MapleBuffStat.COMBO_BARRIER);
        if (barrier != null) {
            damage = ((barrier.getX() / 1000.0) * damage);
        }
        barrier = getStatForBuff(MapleBuffStat.MAGIC_SHIELD);
        if (barrier != null) {
            damage = ((barrier.getX() / 1000.0) * damage);
        }
        barrier = getStatForBuff(MapleBuffStat.WATER_SHIELD);
        if (barrier != null) {
            damage = (((100 - barrier.getX()) / 100.0) * damage);
        }
        List<Integer> attack = attacke instanceof MapleMonster || attacke == null ? null : (new ArrayList<>());
        if (damage > 0) {
            if (getJob() == 122 && !skillisCooling(1220013)) {
                final Skill divine = SkillFactory.getSkill(1220013);
                if (getTotalSkillLevel(divine) > 0) {
                    final MapleStatEffect divineShield = divine.getEffect(getTotalSkillLevel(divine));
                    if (divineShield.makeChanceResult()) {
                        divineShield.applyTo(this);
                        client.sendPacket(CField.skillCooldown(1220013, divineShield.getCooldown(this)));
                        addCooldown(1220013, System.currentTimeMillis(), divineShield.getCooldown(this) * 1000);
                    }
                }
            } else if (getBuffedValue(MapleBuffStat.SATELLITESAFE_PROC) != null && getBuffedValue(MapleBuffStat.SATELLITESAFE_ABSORB) != null && getBuffedValue(MapleBuffStat.PUPPET) != null) {
                double buff = getBuffedValue(MapleBuffStat.SATELLITESAFE_PROC).doubleValue();
                double buffz = getBuffedValue(MapleBuffStat.SATELLITESAFE_ABSORB).doubleValue();
                if ((int) ((buff / 100.0) * getStat().getMaxHp()) <= damage) {
                    damage -= ((buffz / 100.0) * damage);
                    cancelEffectFromBuffStat(MapleBuffStat.PUPPET);
                }
            } else if (getJob() == 433 || getJob() == 434) {
                final Skill divine = SkillFactory.getSkill(4330001);
                if (getTotalSkillLevel(divine) > 0 && getBuffedValue(MapleBuffStat.DARKSIGHT) == null && !skillisCooling(divine.getId())) {
                    final MapleStatEffect divineShield = divine.getEffect(getTotalSkillLevel(divine));
                    if (Randomizer.nextInt(100) < divineShield.getX()) {
                        divineShield.applyTo(this);
                    }
                }
            } else if ((getJob() == 512 || getJob() == 522 || getJob() == 572) && getBuffedValue(MapleBuffStat.DamR) == null) {
                final Skill divine = SkillFactory.getSkill(getJob() == 512 ? 5120011 : getJob() == 572 ? 5720012 : 5220012);
                if (getTotalSkillLevel(divine) > 0 && !skillisCooling(divine.getId())) {
                    final MapleStatEffect divineShield = divine.getEffect(getTotalSkillLevel(divine));
                    if (divineShield.makeChanceResult()) {
                        divineShield.applyTo(this);
                        client.sendPacket(CField.skillCooldown(divine.getId(), divineShield.getCooldown(this)));
                        addCooldown(divine.getId(), System.currentTimeMillis(), divineShield.getCooldown(this) * 1000);
                    }
                }
            } else if (getJob() == 312 && attacke != null) {
                final Skill divine = SkillFactory.getSkill(3120010);
                if (getTotalSkillLevel(divine) > 0) {
                    final MapleStatEffect divineShield = divine.getEffect(getTotalSkillLevel(divine));
                    if (divineShield.makeChanceResult()) {
                        if (attacke instanceof MapleMonster) {
                            final Rectangle bounds = divineShield.calculateBoundingBox(getTruePosition(), isFacingLeft());
                            final List<MapleMapObject> affected = getMap().getMapObjectsInRect(bounds, Arrays.asList(attacke.getType()));
                            int i = 0;

                            for (final MapleMapObject mo : affected) {
                                MapleMonster mons = (MapleMonster) mo;
                                if (mons.getStats().isFriendly() || mons.isFake()) {
                                    continue;
                                }
                                mons.applyStatus(this, new MonsterStatusEffect(MonsterStatus.STUN, 1, divineShield.getSourceId(), null, false), false, divineShield.getDuration(), true, divineShield);
                                final int theDmg = (int) (divineShield.getDamage() * getStat().getCurrentMaxBaseDamage() / 100.0);
                                mons.damage(this, theDmg, true);
                                getMap().broadcastMessage(MobPacket.damageMonster(mons.getObjectId(), theDmg));
                                i++;
                                if (i >= divineShield.getMobCount()) {
                                    break;
                                }
                            }
                        } else {
                            MapleCharacter chr = (MapleCharacter) attacke;
                            chr.addHP(-divineShield.getDamage());
                            attack.add((int) divineShield.getDamage());
                        }
                    }
                }
            } else if ((getJob() == 531 || getJob() == 532) && attacke != null) {
                final Skill divine = SkillFactory.getSkill(5310009); //slea.readInt() = 5310009, then slea.readInt() = damage. (175000)
                if (getTotalSkillLevel(divine) > 0) {
                    final MapleStatEffect divineShield = divine.getEffect(getTotalSkillLevel(divine));
                    if (divineShield.makeChanceResult()) {
                        if (attacke instanceof MapleMonster) {
                            final MapleMonster attacker = (MapleMonster) attacke;
                            final int theDmg = (int) (divineShield.getDamage() * getStat().getCurrentMaxBaseDamage() / 100.0);
                            attacker.damage(this, theDmg, true);
                            getMap().broadcastMessage(MobPacket.damageMonster(attacker.getObjectId(), theDmg));
                        } else {
                            final MapleCharacter attacker = (MapleCharacter) attacke;
                            attacker.addHP(-divineShield.getDamage());
                            attack.add((int) divineShield.getDamage());
                        }
                    }
                }
            } else if (getJob() == 132 && attacke != null) {
                final Skill divine = SkillFactory.getSkill(1320011);
                if (getTotalSkillLevel(divine) > 0 && !skillisCooling(divine.getId()) && getBuffSource(MapleBuffStat.BEHOLDER) == 1321007) {
                    final MapleStatEffect divineShield = divine.getEffect(getTotalSkillLevel(divine));
                    if (divineShield.makeChanceResult()) {
                        client.sendPacket(CField.skillCooldown(divine.getId(), divineShield.getCooldown(this)));
                        addCooldown(divine.getId(), System.currentTimeMillis(), divineShield.getCooldown(this) * 1000);
                        if (attacke instanceof MapleMonster) {
                            final MapleMonster attacker = (MapleMonster) attacke;
                            final int theDmg = (int) (divineShield.getDamage() * getStat().getCurrentMaxBaseDamage() / 100.0);
                            attacker.damage(this, theDmg, true);
                            getMap().broadcastMessage(MobPacket.damageMonster(attacker.getObjectId(), theDmg));
                        } else {
                            final MapleCharacter attacker = (MapleCharacter) attacke;
                            attacker.addHP(-divineShield.getDamage());
                            attack.add((int) divineShield.getDamage());
                        }
                    }
                }
            }
            if (attacke != null) {
                final int damr = (Randomizer.nextInt(100) < getStat().DAMreflect_rate ? getStat().DAMreflect : 0) + (getBuffedValue(MapleBuffStat.POWERGUARD) != null ? getBuffedValue(MapleBuffStat.POWERGUARD) : 0);
                final int bouncedam_ = damr + (getBuffedValue(MapleBuffStat.PERFECT_ARMOR) != null ? getBuffedValue(MapleBuffStat.PERFECT_ARMOR) : 0);
                if (bouncedam_ > 0) {
                    long bouncedamage = (long) damage/*(long) (damage * bouncedam_ / 100)*/;
                    long bouncer = (long) (damage * damr / 100);
                    damage -= bouncer;
                    if (attacke instanceof MapleMonster) {
                        final MapleMonster attacker = (MapleMonster) attacke;
                        bouncedamage = Math.min(bouncedamage, attacker.getMobMaxHp() / 10);
                        attacker.damage(this, bouncedamage, true);
                        getMap().broadcastMessage(this, MobPacket.damageMonster(attacker.getObjectId(), bouncedamage), getTruePosition());
                        if (getBuffSource(MapleBuffStat.PERFECT_ARMOR) == 31101003) {
                            MapleStatEffect eff = this.getStatForBuff(MapleBuffStat.PERFECT_ARMOR);
                            if (eff.makeChanceResult()) {
                                attacker.applyStatus(this, new MonsterStatusEffect(MonsterStatus.STUN, 1, eff.getSourceId(), null, false), false, eff.getSubTime(), true, eff);
                            }
                        }
                    } else {
                        final MapleCharacter attacker = (MapleCharacter) attacke;
                        bouncedamage = Math.min(bouncedamage, attacker.getStat().getCurrentMaxHp() / 10);
                        attacker.addHP(-((int) bouncedamage));
                        attack.add((int) bouncedamage);
                        if (getBuffSource(MapleBuffStat.PERFECT_ARMOR) == 31101003) {
                            MapleStatEffect eff = this.getStatForBuff(MapleBuffStat.PERFECT_ARMOR);
                            if (eff.makeChanceResult()) {
                                attacker.disease(MapleDisease.STUN.getDisease(), 1);
                            }
                        }
                    }
                    ret.right = true;
                }
                if ((getJob() == 411 || getJob() == 412 || getJob() == 421 || getJob() == 422) && getBuffedValue(MapleBuffStat.SUMMON) != null && attacke != null) {
                    final List<MapleSummon> ss = getSummonsReadLock();
                    try {
                        for (MapleSummon sum : ss) {
                            if (sum.getTruePosition().distanceSq(getTruePosition()) < 400000.0 && (sum.getSkill() == 4111007 || sum.getSkill() == 4211007 || sum.getSkill() == 14111010)) {
                                final List<Pair<Integer, Integer>> allDamage = new ArrayList<>();
                                if (attacke instanceof MapleMonster) {
                                    final MapleMonster attacker = (MapleMonster) attacke;
                                    final int theDmg = (int) (SkillFactory.getSkill(sum.getSkill()).getEffect(sum.getSkillLevel()).getX() * damage / 100.0);
                                    allDamage.add(new Pair<>(attacker.getObjectId(), theDmg));
                                    getMap().broadcastMessage(SummonPacket.summonAttack(sum.getOwnerId(), sum.getObjectId(), (byte) 0x84, allDamage, getLevel(), true));
                                    attacker.damage(this, theDmg, true);
                                    checkMonsterAggro(attacker);
                                    if (!attacker.isAlive()) {
                                        getClient().sendPacket(MobPacket.killMonster(attacker.getObjectId(), 1));
                                    }
                                } else {
                                    final MapleCharacter chr = (MapleCharacter) attacke;
                                    final int dmg = SkillFactory.getSkill(sum.getSkill()).getEffect(sum.getSkillLevel()).getX();
                                    chr.addHP(-dmg);
                                    attack.add(dmg);
                                }
                            }
                        }
                    } finally {
                        unlockSummonsReadLock();
                    }
                }
            }
        }
        if (attack != null && attack.size() > 0 && attacke != null) {
            getMap().broadcastMessage(CField.pvpCool(attacke.getObjectId(), attack));
        }
        ret.left = damage;
        return ret;
    }

    public void onAttack(long maxhp, int maxmp, int skillid, int oid, long totDamage) {
        if (stats.hpRecoverProp > 0) {
            if (Randomizer.nextInt(100) <= stats.hpRecoverProp) {//i think its out of 100, anyway
                if (stats.hpRecover > 0) {
                    healHP(stats.hpRecover);
                }
                if (stats.hpRecoverPercent > 0) {
                    addHP(((int) Math.min(maxhp, Math.min(((int) ((double) totDamage * (double) stats.hpRecoverPercent / 100.0)), stats.getMaxHp() / 2))));
                }
            }
        }
        if (stats.mpRecoverProp > 0 && !GameConstants.isDemon(getJob())) {
            if (Randomizer.nextInt(100) <= stats.mpRecoverProp) {//i think its out of 100, anyway
                healMP(stats.mpRecover);
            }
        }
        if (getBuffedValue(MapleBuffStat.COMBO_DRAIN) != null) {
            addHP(((int) Math.min(maxhp, Math.min(((int) ((double) totDamage * (double) getStatForBuff(MapleBuffStat.COMBO_DRAIN).getX() / 100.0)), stats.getMaxHp() / 2))));
        }
        if (getBuffSource(MapleBuffStat.COMBO_DRAIN) == 23101003) {
            addMP(((int) Math.min(maxmp, Math.min(((int) ((double) totDamage * (double) getStatForBuff(MapleBuffStat.COMBO_DRAIN).getX() / 100.0)), stats.getMaxMp() / 2))));
        }
        if (getBuffedValue(MapleBuffStat.REAPER) != null && getBuffedValue(MapleBuffStat.SUMMON) == null && getSummonsSize() < 4 && canSummon()) {
            final MapleStatEffect eff = getStatForBuff(MapleBuffStat.REAPER);
            if (eff.makeChanceResult()) {
                eff.applyTo(this, this, false, null, eff.getDuration());
            }
        }
        if (getJob() == 212 || getJob() == 222 || getJob() == 232) {
            int[] skills = {2120010, 2220010, 2320011};
            for (int i : skills) {
                final Skill skill = SkillFactory.getSkill(i);
                if (getTotalSkillLevel(skill) > 0) {
                    final MapleStatEffect venomEffect = skill.getEffect(getTotalSkillLevel(skill));
                    if (venomEffect.makeChanceResult() && getAllLinkMid().size() < venomEffect.getY()) {
                        setLinkMid(oid, venomEffect.getX());
                        venomEffect.applyTo(this);
                    }
                    break;
                }
            }
        }
        // effects
        if (skillid > 0) {
            final Skill skil = SkillFactory.getSkill(skillid);
            final MapleStatEffect effect = skil.getEffect(getTotalSkillLevel(skil));
            switch (skillid) {
                case 15111001:
                case 3111008:
                case 1078:
                case 31111003:
                case 11078:
                case 14101006:
                case 33111006: //swipe
                case 4101005: //drain
                case 5111004: { // Energy Drain
                    addHP(((int) Math.min(maxhp, Math.min(((int) ((double) totDamage * (double) effect.getX() / 100.0)), stats.getMaxHp() / 2))));
                    break;
                }
                case 22151002: //killer wing
                case 5221015: {//homing
                    setLinkMid(oid, effect.getX());
                    break;
                }
                case 33101007: { //jaguar
                    clearLinkMid();
                    break;
                }
            }
        }
    }

    public void handleForceGain(int oid, int skillid) {
        handleForceGain(oid, skillid, 0);
    }

    public void handleForceGain(int oid, int skillid, int extraForce) {
        if (!GameConstants.isForceIncrease(skillid) && extraForce <= 0) {
            return;
        }
        int forceGain = 1;
        if (getLevel() >= 30 && getLevel() < 70) {
            forceGain = 2;
        } else if (getLevel() >= 70 && getLevel() < 120) {
            forceGain = 3;
        } else if (getLevel() >= 120) {
            forceGain = 4;
        }
        force++; // counter
        addMP(extraForce > 0 ? extraForce : forceGain, true);
        getClient().sendPacket(CField.gainForce(oid, force, forceGain));

        if (stats.mpRecoverProp > 0 && extraForce <= 0) {
            if (Randomizer.nextInt(100) <= stats.mpRecoverProp) {//i think its out of 100, anyway
                force++; // counter
                addMP(stats.mpRecover, true);
                getClient().sendPacket(CField.gainForce(oid, force, stats.mpRecover));
            }
        }
    }

    public void afterAttack(int mobCount, int attackCount, int skillid) {
        switch (getJob()) {
            case 511:
            case 512: {
                handleEnergyCharge(5110001, mobCount * attackCount);
                break;
            }
            case 1510:
            case 1511:
            case 1512: {
                handleEnergyCharge(15100004, mobCount * attackCount);
                break;
            }
            case 111:
            case 112:
            case 1111:
            case 1112:
            case 2411: // phantom combo
            case 2412:
                if (skillid != 1111008 & getBuffedValue(MapleBuffStat.COMBO) != null) { // shout should not give orbs
                    handleOrbgain();
                }
                break;
        }
        if (getBuffedValue(MapleBuffStat.OWL_SPIRIT) != null) {
            if (currentBattleshipHP() > 0) {
                decreaseBattleshipHP();
            }
            if (currentBattleshipHP() <= 0) {
                cancelEffectFromBuffStat(MapleBuffStat.OWL_SPIRIT);
            }
        }
        if (!isIntern()) {
            cancelEffectFromBuffStat(MapleBuffStat.WIND_WALK);
            cancelEffectFromBuffStat(MapleBuffStat.INFILTRATE);
            final MapleStatEffect ds = getStatForBuff(MapleBuffStat.DARKSIGHT);
            if (ds != null) {
                if (ds.getSourceId() != 4330001 || !ds.makeChanceResult()) {
                    cancelEffectFromBuffStat(MapleBuffStat.DARKSIGHT);
                }
            }
        }
    }

    public void applyIceGage(int x) {
        updateSingleStat(MapleStat.ICE_GAGE, x);
    }

    public Rectangle getBounds() {
        return new Rectangle(getTruePosition().x - 25, getTruePosition().y - 75, 50, 75);
    }

    @Override
    public final Map<Byte, Integer> getEquips() {
        final Map<Byte, Integer> eq = new HashMap<>();
        for (final Item item : inventory[MapleInventoryType.EQUIPPED.ordinal()].newList()) {
            eq.put((byte) item.getPosition(), item.getItemId());
        }
        return eq;
    }

    public final PlayerRandomStream CRand() {
        return CRand;
    }

    public final MapleCharacterCards getCharacterCard() {
        return characterCard;
    }

    /*Start of Custom Feature*/
    public int getReborns() {
        return reborns;
    }

    public void setReborns(int rb) {
        this.reborns += rb;
    }

    public int getAPS() {
        return apstorage;
    }

    public void gainAPS(int aps) {
        apstorage += aps;
    }

    public void doReborn() {
        Map<MapleStat, Integer> stat = new EnumMap<>(MapleStat.class);
        this.reborns += 1;
        setLevel((short) 12); // = 11
        setExp(0);
        setRemainingAp((short) 0);

        final int oriStats = stats.getStr() + stats.getDex() + stats.getLuk() + stats.getInt();

        final int str = Randomizer.rand(25, stats.getStr() < 25 ? 25 : stats.getStr());
        final int dex = Randomizer.rand(25, stats.getDex() < 25 ? 25 : stats.getDex());
        final int int_ = Randomizer.rand(25, stats.getInt() < 25 ? 25 : stats.getInt());
        final int luk = Randomizer.rand(25, stats.getLuk() < 25 ? 25 : stats.getLuk());

        final int afterStats = str + dex + int_ + luk;

        final int MAS = (oriStats - afterStats) + getRemainingAp();
        client.getPlayer().gainAPS(MAS);

        stats.recalcLocalStats(this);
        stats.setStr((short) str, client.getPlayer());
        stats.setDex((short) dex, client.getPlayer());
        stats.setInt((short) int_, client.getPlayer());
        stats.setLuk((short) luk, client.getPlayer());
        stat.put(MapleStat.STR, str);
        stat.put(MapleStat.DEX, dex);
        stat.put(MapleStat.INT, int_);
        stat.put(MapleStat.LUK, luk);
        stat.put(MapleStat.AVAILABLEAP, 0);
        updateSingleStat(MapleStat.LEVEL, 11);
        updateSingleStat(MapleStat.JOB, 0); // check in Command instead. @rb, @rbd, @rbc.. whatever..
        updateSingleStat(MapleStat.EXP, 0);
        client.sendPacket(CWvsContext.updatePlayerStats(stat, false, this));
    }

    /*End of Custom Feature*/
    public void checkForceShield() {
        final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();
        Equip equip;
        boolean potential = false;
        switch (job) {
            case 508:
                equip = (Equip) li.getEquipById(1352300);
                break;
            case 572:
                potential = true;
            case 570:
            case 571:
                equip = (Equip) li.getEquipById(1352301 + job % 10);
                break;
            case 3001:
                equip = (Equip) li.getEquipById(1099000);
                break;
            case 3112:
                potential = true;
            case 3100:
            case 3110:
            case 3111:
                equip = (Equip) li.getEquipById(1099001 + job % 10 + ((job % 100) / 10));
                break;
            case 3122:
                potential = true;
            case 3101:
            case 3120:
            case 3121:
                equip = (Equip) li.getEquipById(1099000 + job % 10 + ((job % 100) / 10));
                break;
            case 5112:
                potential = true;
            case 5100:
            case 5110:
            case 5111:
                equip = (Equip) li.getEquipById(1098000 + job % 10 + ((job % 100) / 10));
                break;
            case 3002:
                equip = (Equip) li.getEquipById(1353000);
                break;
            case 3612:
                potential = true;
            case 3600:
            case 3610:
            case 3611:
                equip = (Equip) li.getEquipById(1353001 + job % 10 + ((job % 100) / 10));
                break;
            default:
                equip = null;
        }
        if (equip != null) {
            if (potential) {
                equip.resetPotential();
            }
            equip.setPosition((short) -10);
            equip.setQuantity((short) 1);
            equip.setGMLog("轉職獲得盾牌, 時間 " + FileoutputUtil.CurrentReadable_Time());
            forceReAddItem_NoUpdate(equip, MapleInventoryType.EQUIPPED);
            client.sendPacket(InventoryPacket.updateEquippedItem(this, equip, (short) -10));
            equipChanged();
        }
    }

    @Override
    public Map<Byte, Integer> getTotems() {
        final Map<Byte, Integer> eq = new HashMap<>();
        for (final Item item : inventory[MapleInventoryType.EQUIPPED.ordinal()].newList()) {
            byte pos = (byte) ((item.getPosition() + 5000) * -1);
            if (pos < 0 || pos > 2) { //3 totem slots
                continue;
            }
            if (item.getItemId() < 1200000 || item.getItemId() >= 1210000) {
                continue;
            }
            eq.put(pos, item.getItemId());
        }
        return eq;
    }

    public void handleCardStack() {
        Skill noir = SkillFactory.getSkill(24120002);
        Skill blanc = SkillFactory.getSkill(24100003);
        MapleStatEffect ceffect = null;
        int advSkillLevel = getTotalSkillLevel(noir);
        boolean isAdv = false;
        if (advSkillLevel > 0) {
            ceffect = noir.getEffect(advSkillLevel);
            isAdv = true;
        } else if (getSkillLevel(blanc) > 0) {
            ceffect = blanc.getEffect(getTotalSkillLevel(blanc));
        } else {
            return;
        }
        if (getJob() == 2412 && getCardStack() == 40) {
            this.runningStack = 0;
            this.cardStack = (byte) (40);
        }
        if (getJob() == 2400 && getCardStack() == 20 || getJob() == 2410 && getCardStack() == 20 || getJob() == 2411 && getCardStack() == 20) {
            this.runningStack = 0;
            this.cardStack = (byte) (20);
        }
        if (ceffect.makeChanceResult()) {
            if (this.cardStack < (getJob() == 2412 ? 40 : 20)) {
                this.cardStack = (byte) (this.cardStack + 1);
            }
            this.runningStack += 1;
            this.client.sendPacket(PhantomPacket.gainCardStack(getId(), this.runningStack, isAdv ? 2 : 1, ceffect.getSourceId(), Randomizer.rand(100000, 500000), 1));
            this.client.sendPacket(PhantomPacket.updateCardStack(this.cardStack));
        }
    }

    public int getChronosphere() {
        return chronosphere;
    }

    public void setChronosphere(int count) {
        this.chronosphere = count;
    }

    public void setCSChronsphere(int count) {
        this.cschronosphere = count;
    }

    public int getCSChronosphere() {
        return cschronosphere;
    }

    public int getVip() {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE id = ?")) {
                ps.setInt(1, this.id);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    vip = rs.getInt("vip");
                }
            }
        } catch (SQLException ex) {
        }
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE characters SET vip = ? WHERE id = ?");
            ps.setInt(1, this.vip);
            ps.setInt(2, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Ex) {
        }
    }

    public int getDonate() {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?")) {
                ps.setInt(1, this.accountid);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    total_donate = rs.getInt("total_donate");
                }
            }
        } catch (SQLException ex) {
        }
        return total_donate;
    }

    public int getRewardLevel() {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE id = ?")) {
                ps.setInt(1, this.id);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    reward = rs.getInt("reward");
                }
            }
        } catch (SQLException ex) {
        }
        return reward;
    }

    public void setRewardLevel(int reward) {
        this.reward = reward;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE characters SET reward = ? WHERE id = ?");
            ps.setInt(1, this.reward);
            ps.setInt(2, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Ex) {
        }
    }

    public boolean changeFace(int color) {
        int f = 0;
        if (face % 1000 < 100) {
            f = face + color;
        } else if ((face % 1000 >= 100) && (face % 1000 < 200)) {
            f = face - 100 + color;
        } else if ((face % 1000 >= 200) && (face % 1000 < 300)) {
            f = face - 200 + color;
        } else if ((face % 1000 >= 300) && (face % 1000 < 400)) {
            f = face - 300 + color;
        } else if ((face % 1000 >= 400) && (face % 1000 < 500)) {
            f = face - 400 + color;
        } else if ((face % 1000 >= 500) && (face % 1000 < 600)) {
            f = face - 500 + color;
        } else if ((face % 1000 >= 600) && (face % 1000 < 700)) {
            f = face - 600 + color;
        } else if ((face % 1000 >= 700) && (face % 1000 < 800)) {
            f = face - 700 + color;
        }
        if (!MapleItemInformationProvider.getInstance().itemExists(f)) {
            return false;
        }
        face = f;
        updateSingleStat(MapleStat.FACE, face);
        equipChanged();
        return true;
    }

    public void gainItem(int code, int amount) {
        if (amount > 0) {
            MapleInventoryManipulator.addById(client, code, (short) amount, null);
        }
    }

    public void giftMedal(int id) {
        if (!this.getInventory(MapleInventoryType.EQUIP).isFull() && this.getInventory(MapleInventoryType.EQUIP).countById(id) == 0 && this.getInventory(MapleInventoryType.EQUIPPED).countById(id) == 0) {
            MapleInventoryManipulator.addById(client, id, (short) 1, null);
            World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "[恭喜]" + getName() + "剛才得到了 " + MapleItemInformationProvider.getInstance().getName(id) + "！"));
        }
    }

    public int getAveragePartyLevel() {
        int averageLevel = 0, size = 0;
        for (MaplePartyCharacter pl : getParty().getMembers()) {
            averageLevel += pl.getLevel();
            size++;
        }
        if (size <= 0) {
            return level;
        }
        averageLevel /= size;
        return averageLevel;
    }

    public int getAverageMapLevel() {
        int averageLevel = 0, size = 0;
        for (MapleCharacter pl : getMap().getCharacters()) {
            averageLevel += pl.getLevel();
            size++;
        }
        if (size <= 0) {
            return level;
        }
        averageLevel /= size;
        return averageLevel;
    }

    public int getNpcNow() {
        return npcnow;
    }

    public void setNpcNow(int id) {
        npcnow = id;
    }

    @Override
    public int getElf() {
        String elf = getOneInfo(GameConstants.精靈耳朵, "sw");
        if (elf == null) {
            return 0;
        } else {
            return Integer.valueOf(elf);
        }
    }

    public void setElf(int elf) {
        updateInfoQuest(GameConstants.精靈耳朵, "sw=" + elf);
        equipChanged();
    }

    public void setTempValue(String arg, String values) {
        if (TempValues.containsKey(arg)) {
            TempValues.remove(arg);
        }
        if (values == null) {
            return;
        }
        TempValues.put(arg, values);
    }

    public String getTempValue(String arg) {
        if (TempValues.containsKey(arg)) {
            return TempValues.get(arg);
        }
        return null;
    }

    public String getOneTempValue(String arg, String key) {
        if (!TempValues.containsKey(arg)) {
            return null;
        }
        return StringTool.getOneValue(TempValues.get(arg), key);
    }

    public void updateOneTempValue(final String arg, final String key, final String value) {
        if (key == null) {
            return;
        }
        String info = StringTool.updateOneValue(getTempValue(arg), key, value);
        if (info == null) {
            return;
        }
        if (info.isEmpty()) {
            setTempValue(arg, null);
        } else {
            setTempValue(arg, info);
        }
    }

    public final void maxAllSkills() {
        HashMap<Skill, SkillEntry> sa = new HashMap<>();
        for (Skill skil : SkillFactory.getAllSkills()) {
            if (GameConstants.isApplicableSkill(skil.getId()) && skil.getId() < 90000000) { //no db/additionals/resistance skills
                sa.put(skil, new SkillEntry((byte) skil.getMaxLevel(), (byte) skil.getMaxLevel(), SkillFactory.getDefaultSExpiry(skil), -1));
            }
        }
        changeSkillsLevel(sa);
    }

    public final void maxSkillsByJob() {
        boolean ultimate_explorer = getQuestNoAdd(MapleQuest.getInstance(GameConstants.ULT_EXPLORER)) != null;

        HashMap<Skill, SkillEntry> sa = new HashMap<>();
        for (Skill skil : SkillFactory.getAllSkills()) {
            if (GameConstants.isUselssSkill(skil.getId())) {
                if (!GameConstants.isUsefulUltimateSkill(job, skil.getId()) && !ultimate_explorer) {
                    sa.put(skil, new SkillEntry((byte) 0, (byte) -1, 0, -1));
                    continue;
                }
                if (!GameConstants.isUsefulUltimateLinkSkill(job, skil.getId()) && !ultimate_explorer) {
                    sa.put(skil, new SkillEntry((byte) 0, (byte) -1, 0, -1));
                    continue;
                }
            }
            if (GameConstants.isApplicableSkill(skil.getId()) && skil.canBeLearnedBy(getJob()) && !GameConstants.isProfessionSkill(skil.getId())) { //no db/additionals/resistance skills
                sa.put(skil, new SkillEntry((byte) skil.getMaxLevel(), (byte) skil.getMaxLevel(), SkillFactory.getDefaultSExpiry(skil), -1));
            }
        }
        changeSkillsLevel(sa);
    }

    public final void maxTeachSkills() {
        HashMap<Skill, SkillEntry> sa = new HashMap<>();
        //167772160 lv 10 exp 0
        for (Skill skil : SkillFactory.getAllSkills()) {
            if (skil.getId() >= 92000000 && skil.getId() <= 92040000) {
                if (hasSkill(skil.getId())) {
                    changeProfessionLevelExp(skil.getId(), 10, 0);
                }
            }
        }
    }

    public final void clearSkills() {
        final Map<Skill, SkillEntry> skills = new HashMap<>(getSkills());
        final Map<Skill, SkillEntry> newList = new HashMap<>();
        for (Entry<Skill, SkillEntry> skill : skills.entrySet()) {
            if (GameConstants.isProfessionSkill(skill.getKey().getId())) {
                continue;
            }
            newList.put(skill.getKey(), new SkillEntry((byte) 0, (byte) 0, -1, -1));
        }
        changeSkillsLevel(newList);
        newList.clear();
        skills.clear();
    }

    public final void LearnSameSkill(MapleCharacter victim) {

        // 清除技能
        Map<Skill, SkillEntry> skills = new HashMap<>(getSkills());
        Map<Skill, SkillEntry> newList = new HashMap<>();
        for (Entry<Skill, SkillEntry> skill : skills.entrySet()) {
            newList.put(skill.getKey(), new SkillEntry((byte) 0, (byte) 0, -1, -1));
        }
        changeSkillsLevel(newList);
        newList.clear();
        skills.clear();

        // 學習技能
        skills = new HashMap<>(victim.getSkills());
        newList = new HashMap<>();
        for (Entry<Skill, SkillEntry> skill : skills.entrySet()) {
            newList.put(skill.getKey(), new SkillEntry((byte) victim.getSkillLevel(skill.getKey().getId()), (byte) skill.getKey().getMasterLevel(), -1, -1));
        }
        changeSkillsLevel(newList);
        newList.clear();
        skills.clear();
    }

    public boolean OfflineBanByName(String name, String reason) {
        int id = 0;
        try {
            PreparedStatement ps = null;
            Connection con = DatabaseConnection.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs;
            ps = con.prepareStatement("select id from characters where name = ?");
            ps.setString(1, name);
            rs = ps.executeQuery();
            while (rs.next()) {
                id = rs.getInt("id");
            }
        } catch (Exception ex) {
        }
        if (id == 0) {
            return false;
        }
        return OfflineBanById(id, reason);
    }

    public boolean OfflineBanById(int id, String reason) {
        try {
            Connection con = DatabaseConnection.getConnection();
            Statement stmt = con.createStatement();
            PreparedStatement ps;
            ResultSet rs;
            int z = id;
            int acid = 0;
            String ip = "";
            String mac = "";
            rs = stmt.executeQuery("select accountid from characters where id = " + id);
            while (rs.next()) {
                acid = rs.getInt("accountid");
            }
            if (acid == 0) {
                return false;
            }
            try (PreparedStatement psb = con.prepareStatement("UPDATE accounts SET banned = 1, banreason = ? WHERE id = ?")) {
                psb.setString(1, reason);
                psb.setInt(2, acid);
                psb.execute();
                psb.close();
            }

            rs = stmt.executeQuery("select SessionIP, mac from accounts where id = " + acid);
            while (rs.next()) {
                ip = rs.getString("SessionIP");
                mac = rs.getString("mac");
            }

            FileoutputUtil.logToFile("logs/hack/Ban/MySql_input.txt", "\r\n[offlineBan] " + FileoutputUtil.NowTime() + " IP: " + ip + " MAC: " + mac + " 理由: " + reason, false, false);
            ps = con.prepareStatement("INSERT INTO ipbans (ip) VALUES (?)");
            ps.setString(1, ip);
            ps.executeUpdate();
            ps.close();
//            try {
//                for (ChannelServer cs : ChannelServer.getAllInstances()) {
//                    for (MapleCharacter chr : cs.getPlayerStorage().getAllCharactersThreadSafe()) {
//                        if (chr.getClient().getSessionIPAddress().equals(ip)) {
//                            if (!chr.getClient().isGm()) {
//                                chr.getClient().disconnect(true, false);
//                                chr.getClient().getSession().close();
//                            }
//                        }
//                    }
//                }
//
//                for (MapleCharacter chr : CashShopServer.getPlayerStorage().getAllCharactersThreadSafe()) {
//                    if (chr.getClient().getSessionIPAddress().equals(ip)) {
//                        if (!chr.getClient().isGm()) {
//                            chr.getClient().disconnect(true, false);
//                            chr.getClient().getSession().close();
//                        }
//                    }
//                }
//
//            } catch (Exception ex) {
//            }
            MapleClient.banSingleMacs(mac);
            rs.close();
            stmt.close();
            return true;
        } catch (Exception ex) {
            System.err.println("封鎖出現錯誤 " + ex);
        }
        return false;
    }

    public boolean isUseVipCharm() {
        return useVipCharm;
    }

    public void setUseVipCharm(boolean useVipCharm) {
        this.useVipCharm = useVipCharm;
    }

    public boolean isUseFirmCharm() {
        return useFirmCharm;
    }

    public void setUseFirmCharm(boolean useFirmCharm) {
        this.useFirmCharm = useFirmCharm;
    }

    public void updateAP() {
        // idk how i fucked this up when removing Auto Assign, but meh.
        updateSingleStat(MapleStat.STR, client.getPlayer().getStat().getStr());
        updateSingleStat(MapleStat.DEX, client.getPlayer().getStat().getDex());
        updateSingleStat(MapleStat.INT, client.getPlayer().getStat().getInt());
        updateSingleStat(MapleStat.LUK, client.getPlayer().getStat().getLuk());
    }

    public void setmsg_HiredMerchant(boolean control) {
        check_msg_BuyMerChant = control;
    }

    public void setmsg_Chat(boolean control) {
        check_msg_Chat = control;
    }

    public boolean getmsg_HiredMerchant() {
        return check_msg_BuyMerChant;
    }

    public boolean getmsg_Chat() {
        return check_msg_Chat;
    }

    public boolean getDelChrLog() {
        return check_DelChrLog;
    }

    public void set_DelChrLog(boolean control) {
        check_DelChrLog = control;
    }

    public int teachSkill(int skillId, int toCharId) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM skills WHERE skillid = ? AND teachId = ?");
            ps.setInt(1, skillId);
            ps.setInt(2, this.id);
            ps.executeUpdate();
            ps.close();
            ps = con.prepareStatement("SELECT * FROM skills WHERE skillid = ? AND characterid = ?");
            ps.setInt(1, skillId);
            ps.setInt(2, toCharId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                try (PreparedStatement psskills = con.prepareStatement("INSERT INTO skills (characterid, skillid, skilllevel, masterlevel, expiration, teachId) VALUES (?, ?, ?, ?, ?, ?)")) {
                    psskills.setInt(1, toCharId);
                    psskills.setInt(2, skillId);
                    psskills.setInt(3, 1);
                    psskills.setByte(4, (byte) 1);
                    psskills.setLong(5, -1L);
                    psskills.setInt(6, this.id);
                    psskills.executeUpdate();
                }
                return 1;
            }
            rs.close();
            ps.close();
            return -1;
        } catch (SQLException ex) {
            System.err.println("error while read teachSkill.");
        }
        return -1;
    }

    public void changeTeachSkill(int skillId, int toCharId) {
        Skill skill = SkillFactory.getSkill(skillId);
        if (skill == null) {
            return;
        }
        final Map<Skill, SkillEntry> list = new HashMap<>();
        list.put(skill, new SkillEntry(1, (byte) 1, -1L, toCharId));
        this.client.sendPacket(CWvsContext.updateSkills(list));
        this.skills.put(skill, new SkillEntry(1, (byte) 1, -1L, toCharId));
        this.changed_skills = true;
    }

    public void fly() {
        if (isFlying() == false) {
            SkillFactory.getSkill(80001069).getEffect(1).applyTo(this);
            if (getBuffedValue(MapleBuffStat.MONSTER_RIDING) != null) {
                setFlying(true);
                SkillFactory.getSkill(80001089).getEffect(1).applyTo(this);
                cancelBuffStats(MapleBuffStat.MONSTER_RIDING);
                cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
                if (isGM()) {
                    dropMessage(-1, "飛行模式 : 開起. 若要關閉, 請輸入 !fly.");
                    dropMessage(-1, "飛的方法 : 跳起來 用方向件控制方向");
                }
            }
        } else if (isFlying() == true) {
            cancelBuffStats(MapleBuffStat.SOARING);
            cancelEffectFromBuffStat(MapleBuffStat.SOARING);
            setFlying(false);
            if (isGM()) {
                dropMessage(-1, "飛行模式 : 關閉. 若要開起, 請輸入 !fly.");
            }
        }
    }

    public void fly1() {
        if (isFlying() == true) {
            SkillFactory.getSkill(80001069).getEffect(1).applyTo(this);
            if (getBuffedValue(MapleBuffStat.MONSTER_RIDING) != null) {
                setFlying(true);
                SkillFactory.getSkill(80001089).getEffect(1).applyTo(this);
                cancelBuffStats(MapleBuffStat.MONSTER_RIDING);
                cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
                if (isGM()) {
                    dropMessage(-1, "飛行模式 : 開起. 若要關閉, 請輸入 !fly.");
                }
            }
        }
    }

    public boolean isFlying() {
        return flying;
    }

    public void setFlying(boolean toggle) {
        flying = toggle;
    }

    public void addMobVac() {
        mobVac++;
    }

    public int getMobVac() {
        return mobVac;
    }

    public void updateWarpingMap(boolean set) {
        lastWarpingMap = set ? System.currentTimeMillis() : 0;
    }

    public boolean checkWarpingMap() {
        boolean started = lastWarpingMap > 0;
        boolean time = lastWarpingMap + 1500 < System.currentTimeMillis();
        return !started ? started : !(started && time);
    }

    public HashMap<Integer, Long> getSkillWorkings() {
        return leftSkillTime;
    }

    public void removeSkillWorking(int SkillID) {
        if (leftSkillTime.containsKey(SkillID)) {
            leftSkillTime.remove(SkillID);
        }
    }

    public void setSkillWorking(int SkillID, long SkillTime) {
        leftSkillTime.put(SkillID, SkillTime);
    }

    public boolean isSkillWorking(int SkillID) {
        return leftSkillTime.containsKey(SkillID);
    }

    public long getSkillWorking(int SkillID) {
        if (leftSkillTime.containsKey(SkillID)) {
            return leftSkillTime.get(SkillID);
        }
        return 0;
    }

    public void updateUsingPortal(boolean set) {
        lastUsingPortal = set ? System.currentTimeMillis() : 0;
    }

    public boolean checkUsingPortal() {
        boolean started = lastUsingPortal > 0;
        boolean time = lastUsingPortal + 1500 < System.currentTimeMillis();
        return !started ? started : !(started && time);
    }

    public void checkCopyItemsByID(int fromid, int itemid) {

        List<Long> inventoryItemIds = new ArrayList<>();
        List<Integer> ItemIds = new ArrayList<>();
        Map checkItems = new HashMap();

        for (Item item : getInventory(MapleInventoryType.USE).list()) {
            if (item.getItemId() != itemid) {
                continue;
            }
            int ItemId = item.getItemId();
            long invid = item.getInventoryitemId();
            long equipOnlyId = item.getEquipOnlyId();
            if (equipOnlyId > 0) {
                if (checkItems.containsKey(equipOnlyId)) {
                    if (((Integer) checkItems.get(equipOnlyId)) == item.getItemId()) {
                        inventoryItemIds.add(invid);
                        ItemIds.add(ItemId);
                    }
                } else {
                    Object put = checkItems.put(equipOnlyId, item.getItemId());
                }
            }
        }
        for (Item item : getInventory(MapleInventoryType.EQUIP).list()) {
            if (item.getItemId() != itemid) {
                continue;
            }
            int ItemId = item.getItemId();
            long invid = item.getInventoryitemId();
            long equipOnlyId = item.getEquipOnlyId();
            if (equipOnlyId > 0) {
                if (checkItems.containsKey(equipOnlyId)) {
                    if (((Integer) checkItems.get(equipOnlyId)) == item.getItemId()) {
                        inventoryItemIds.add(invid);
                        ItemIds.add(ItemId);
                    }
                } else {
                    Object put = checkItems.put(equipOnlyId, item.getItemId());
                }
            }
        }

        for (Item item : getInventory(MapleInventoryType.EQUIPPED).list()) {
            if (item.getItemId() != itemid) {
                continue;
            }
            int ItemId = item.getItemId();
            long invid = item.getInventoryitemId();
            long equipOnlyId = item.getEquipOnlyId();
            if (equipOnlyId > 0) {
                if (checkItems.containsKey(equipOnlyId)) {
                    if (((Integer) checkItems.get(equipOnlyId)) == item.getItemId()) {
                        inventoryItemIds.add(invid);
                        ItemIds.add(ItemId);
                    }
                } else {
                    Object put = checkItems.put(equipOnlyId, item.getItemId());
                }
            }
        }

        MapleCharacter victim = null;
        // 撿的人
        if (fromid != -1 && fromid != getId()) {
            victim = World.Find.findChr(fromid);
            if (victim != null) {

                for (Item item : victim.getInventory(MapleInventoryType.EQUIP).list()) {
                    if (item.getItemId() != itemid) {
                        continue;
                    }
                    int ItemId = item.getItemId();
                    long invid = item.getInventoryitemId();
                    long equipOnlyId = item.getEquipOnlyId();
                    if (equipOnlyId > 0) {
                        if (checkItems.containsKey(equipOnlyId)) {
                            if (((Integer) checkItems.get(equipOnlyId)) == item.getItemId()) {
                                inventoryItemIds.add(invid);
                                ItemIds.add(ItemId);
                            }
                        } else {
                            Object put = checkItems.put(equipOnlyId, item.getItemId());
                        }
                    }
                }

                for (Item item : victim.getInventory(MapleInventoryType.EQUIPPED).list()) {
                    if (item.getItemId() != itemid) {
                        continue;
                    }
                    int ItemId = item.getItemId();
                    long invid = item.getInventoryitemId();
                    long equipOnlyId = item.getEquipOnlyId();
                    if (equipOnlyId > 0) {
                        if (checkItems.containsKey(equipOnlyId)) {
                            if (((Integer) checkItems.get(equipOnlyId)) == item.getItemId()) {
                                inventoryItemIds.add(invid);
                                ItemIds.add(ItemId);
                            }
                        } else {
                            Object put = checkItems.put(equipOnlyId, item.getItemId());
                        }
                    }
                }
            }
        }

        boolean deleteitem = true; // 證據留著?
        boolean autoban = false;
        for (Long invid : inventoryItemIds) {
            if (deleteitem) {
                MapleInventoryManipulator.removeAllByInventoryId(this.client, invid);
                if (victim != null && victim.getClient() != null) {
                    MapleInventoryManipulator.removeAllByInventoryId(victim.getClient(), invid);
                }
            }
            autoban = true;
        }
        if (autoban) {
            AutobanManager.getInstance().autoban(this.client, "複製裝備.");
            if (victim != null && victim.getClient() != null) {
                AutobanManager.getInstance().autoban(victim.getClient(), "複製裝備.");
            }
        }
        checkItems.clear();
        inventoryItemIds.clear();

    }

    public int getLuckyBarrelsStatus() {
        return luckyBarrelsStatus;
    }

    public void setLuckyBarrelsStatus(int luckyBarrelsStatus) {
        this.luckyBarrelsStatus = luckyBarrelsStatus;
    }

    public void endStorageMsg(String name) {
        if (ServerConstants.log_storage) {
            FileoutputUtil.logToFile("logs/data/倉庫紀錄.txt", storagemsg);
        }
        storagemsg = "";
    }

    public void addEmptyStorageMsg(String msg) {
        storagemsg += msg;
    }

    public void addStorageMsg(String name, String msg) {
        storagemsg += FileoutputUtil.CurrentReadable_Time() + " " + name + " : " + msg;
    }

    public void endTradeMsg(String name) {
        if (ServerConstants.log_trade) {
            FileoutputUtil.logToFile("logs/data/交易紀錄.txt", chatmsg);
        }
        chatmsg = "";
    }

    public void addEmptyTradeMsg(String msg) {
        chatmsg += msg;
    }

    public void addTradeMsg(String name, String msg) {
        chatmsg += FileoutputUtil.CurrentReadable_Time() + " " + name + " : " + msg;
    }

    public boolean isOperateStorage() {
        return storageing;
    }

    public void setOperateStorage(boolean set) {
        storageing = set;
    }

    public boolean isInvited() {
        return tradeinviting;
    }

    public void setInvited(boolean set) {
        tradeinviting = set;
    }

    public int getSayGood() {
        return SayGood;
    }

    public void setSayGood(int time) {
        SayGood += time;
    }

    public int CashShopGiftCount(int recipientid) {
        int size = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT count(*) FROM gifts WHERE recipient = ?")) {
                ps.setInt(1, recipientid);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    size = rs.getInt(1);
                }
                rs.close();
                ps.close();
            }
        } catch (SQLException ex) {
        }
        return size;
    }

    public final void showInstruction(final String msg, final int width, final int height) {
        client.sendPacket(CField.sendHint(msg, width, height));
    }

    public void initMesoBank() {
        try {
            int accid = getAccountID();
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO mesobank (accid, money) VALUES (?, 0)")) {
                ps.setInt(1, accid);
                ps.executeUpdate();
            } catch (Exception ex) {
                FileoutputUtil.printError("MapleCharacter.txt", "initMesoBank", ex, "");
            }
        } catch (Exception ex) {
            FileoutputUtil.printError("MapleCharacter.txt", "initMesoBank", ex, "");
        }
    }

    public long getMesoFromBank() {
        long money = 0;
        try {
            int accid = getAccountID();
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT money FROM mesobank WHERE accid = ?")) {
                ps.setInt(1, accid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        money = rs.getLong("money");
                    } else {
                        initMesoBank();
                    }
                }
            } catch (Exception ex) {
                FileoutputUtil.printError("MapleCharacter.txt", "getMoney", ex, "");
            }
        } catch (Exception ex) {
            FileoutputUtil.printError("MapleCharacter.txt", "getMoney", ex, "");
        }
        return money;
    }

    public void setMoneytoBank(long money) {
        try {
            int accid = getAccountID();
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE mesobank SET money = ? WHERE accid = ?")) {
                ps.setLong(1, money);
                ps.setInt(2, accid);
                ps.executeUpdate();
            } catch (Exception ex) {
                FileoutputUtil.printError("MapleCharacter.txt", "setMoneytoBank", ex, " money: " + money);
            }
        } catch (Exception ex) {
            FileoutputUtil.printError("MapleCharacter.txt", "setMoneytoBank", ex, " money: " + money);
        }
    }

    public void incMoneytoBank(long money) {
        long oldMoney = getMesoFromBank();
        setMoneytoBank(oldMoney + money);
    }

    public void decMoneytoBank(long money) {
        long oldMoney = getMesoFromBank();
        setMoneytoBank(oldMoney - money);
    }

    public boolean hasSkill(int skillid) {
        Skill theSkill = SkillFactory.getSkill(skillid);
        if (theSkill != null) {
            return getSkillLevel(theSkill) > 0;
        }
        return false;
    }

    public String getAriantRoomLeaderName(int room) {
        return ariantroomleader[room];
    }

    public int getAriantSlotsRoom(int room) {
        return ariantroomslot[room];
    }

    public void removeAriantRoom(int room) {
        ariantroomleader[room] = "";
        ariantroomslot[room] = 0;
    }

    public void setAriantRoomLeader(int room, String charname) {
        ariantroomleader[room] = charname;
    }

    public void setAriantSlotRoom(int room, int slot) {
        ariantroomslot[room] = slot;
    }

    public void setApprentice(int app) {
        this.apprentice = app;
    }

    public boolean hasApprentice() {
        if (apprentice > 0) {
            return true;
        } else {
            return false;
        }
    }

    public int getApprentice() {
        return this.apprentice;
    }

    public int ariantScore = 0;

    public void addAriantScore() {
        ariantScore++;
    }

    public void resetAriantScore() {
        ariantScore = 0;
    }

    public int getAriantScore() { // TODO: code entire score system
        return ariantScore;
    }

    public void updateAriantScore() {
        this.getMap().broadcastMessage(CField.updateAriantScore(this.getName(), getAriantScore(), false));
    }

    public MapleCoreAura getCoreAura() {
        return coreAura;
    }

    public List<Pair<Integer, Boolean>> getStolenSkills() {
        return stolenSkills;
    }

    public void chooseStolenSkill(int skillID) {
        if (skillisCooling(20031208) || stolenSkills == null) {
            dropMessage(-6, "目前處於冷卻期間，請稍候再試。");
            return;
        }
        final Pair<Integer, Boolean> dummy = new Pair<>(skillID, false);
        if (stolenSkills.contains(dummy)) {
            unchooseStolenSkill(skillID);
            stolenSkills.get(stolenSkills.indexOf(dummy)).right = true;

            client.sendPacket(PhantomPacket.replaceStolenSkill(GameConstants.getStealSkill(GameConstants.getJobNumber(skillID / 10000)), skillID));
            //if (ServerConstants.CUSTOM_SKILL) {
            //    client.sendPacket(MaplePacketCreator.skillCooldown(20031208, 5));
            //    addCooldown(20031208, System.currentTimeMillis(), 5000);
            //}
        }
    }

    public void unchooseStolenSkill(int skillID) {
        if (skillisCooling(20031208) || stolenSkills == null) {
            dropMessage(-6, "目前處於冷卻期間，請稍候再試。");
            return;
        }
        final int stolenjob = GameConstants.getJobNumber(skillID / 10000);
        boolean changed = false;
        for (Pair<Integer, Boolean> sk : stolenSkills) {
            if (sk.right && GameConstants.getJobNumber(sk.left / 10000) == stolenjob) {
                cancelStolenSkill(sk.left);
                sk.right = false;
                changed = true;
            }
        }
        if (changed) {
            final Skill skil = SkillFactory.getSkill(skillID);
            changeSkillLevel_Skip(skil, getSkillLevel(skil), (byte) 0);
            client.sendPacket(PhantomPacket.replaceStolenSkill(GameConstants.getStealSkill(stolenjob), 0));
        }
    }

    public void addStolenSkill(int skillID, int skillLevel) {
        if (skillisCooling(20031208) || stolenSkills == null) {
            dropMessage(-6, "目前處於冷卻期間，請稍候再試。");
            return;
        }
        final Pair<Integer, Boolean> dummy = new Pair<>(skillID, true);
        final Skill skil = SkillFactory.getSkill(skillID);
        if (!stolenSkills.contains(dummy) && GameConstants.canSteal(skil)) {
            dummy.right = false;
            skillLevel = Math.min(skil.getMaxLevel(), skillLevel);
            final int jobid = GameConstants.getJobNumber(skillID / 10000);
            if (!stolenSkills.contains(dummy) && getSkillLevel(GameConstants.getStealSkill(jobid)) > 0) {
                int count = 0;
                skillLevel = Math.min(getSkillLevel(GameConstants.getStealSkill(jobid)), skillLevel);
                for (Pair<Integer, Boolean> sk : stolenSkills) {
                    if (GameConstants.getJobNumber(sk.left / 10000) == jobid) {
                        count++;
                    }
                }
                if (count < GameConstants.getNumSteal(jobid)) {
                    stolenSkills.add(dummy);
                    changed_skills = true;
                    changeSkillLevel_Skip(skil, skillLevel, (byte) skillLevel);
                    client.sendPacket(PhantomPacket.addStolenSkill(jobid, count, skillID, skillLevel));
                    //client.sendPacket(MaplePacketCreator.updateStolenSkills(this, jobid));
                } else {
                    client.getPlayer().dropMessage(5, "該偷取管理已經滿技能，或者您已經有這個技能。");
                    client.sendPacket(CWvsContext.enableActions());
                }
            }
        } else {
            client.getPlayer().dropMessage(5, "該偷取管理已經滿技能，或者您已經有這個技能。");
            client.sendPacket(CWvsContext.enableActions());
        }
    }

    public void removeStolenSkill(int skillID) {
        if (skillisCooling(20031208) || stolenSkills == null) {
            dropMessage(-6, "目前處於冷卻期間，請稍候再試。");
            return;
        }
        final int jobid = GameConstants.getJobNumber(skillID / 10000);
        final Pair<Integer, Boolean> dummy = new Pair<>(skillID, false);
        int count = -1, cc = 0;
        for (int i = 0; i < stolenSkills.size(); i++) {
            if (stolenSkills.get(i).left == skillID) {
                if (stolenSkills.get(i).right) {
                    unchooseStolenSkill(skillID);
                }
                count = cc;
                break;
            } else if (GameConstants.getJobNumber(stolenSkills.get(i).left / 10000) == jobid) {
                cc++;
            }
        }
        if (count >= 0) {
            cancelStolenSkill(skillID);
            stolenSkills.remove(dummy);
            dummy.right = true;
            stolenSkills.remove(dummy);
            changed_skills = true;
            changeSkillLevel_Skip(SkillFactory.getSkill(skillID), 0, (byte) 0);
            //hacky process begins here
            client.sendPacket(PhantomPacket.replaceStolenSkill(GameConstants.getStealSkill(jobid), 0));
            for (int i = 0; i < GameConstants.getNumSteal(jobid); i++) {
                client.sendPacket(PhantomPacket.removeStolenSkill(jobid, i));
            }
            count = 0;
            for (Pair<Integer, Boolean> sk : stolenSkills) {
                if (GameConstants.getJobNumber(sk.left / 10000) == jobid) {
                    client.sendPacket(PhantomPacket.addStolenSkill(jobid, count, sk.left, getSkillLevel(sk.left)));
                    if (sk.right) {
                        client.sendPacket(PhantomPacket.replaceStolenSkill(GameConstants.getStealSkill(jobid), sk.left));
                    }
                    count++;
                }
            }
            client.sendPacket(PhantomPacket.removeStolenSkill(jobid, count));
            //client.sendPacket(MaplePacketCreator.updateStolenSkills(this, jobid));
        }
    }

    public void cancelStolenSkill(int skillID) {
        final Skill skk = SkillFactory.getSkill(skillID);
        final MapleStatEffect eff = skk.getEffect(getTotalSkillLevel(skk));

        if (eff.isMonsterBuff() || (eff.getStatups().isEmpty() && !eff.getMonsterStati().isEmpty())) {
            for (MapleMonster mons : map.getAllMonstersThreadsafe()) {
                for (MonsterStatus b : eff.getMonsterStati().keySet()) {
                    if (mons.isBuffed(b) && mons.getBuff(b).getFromID() == this.id) {
                        mons.cancelStatus(b);
                    }
                }
            }
        } else if (eff.getDuration() > 0 && !eff.getStatups().isEmpty()) {
            for (MapleCharacter chr : map.getCharactersThreadsafe()) {
                chr.cancelEffect(eff, false, -1, eff.getStatups());

            }
        }
    }

    public List<InnerSkillValueHolder> getInnerSkills() {
        return innerSkills;
    }

    public final int[] getFriendShipPoints() {
        return friendshippoints;
    }

    public final void setFriendShipPoints(int joejoe, int hermoninny, int littledragon, int ika) {
        this.friendshippoints[0] = joejoe;
        this.friendshippoints[1] = hermoninny;
        this.friendshippoints[2] = littledragon;
        this.friendshippoints[3] = ika;
    }

    public final int getFriendShipToAdd() {
        return friendshiptoadd;
    }

    public final void setFriendShipToAdd(int points) {
        this.friendshiptoadd = points;
    }

    public final void addFriendShipToAdd(int points) {
        this.friendshiptoadd += points;
    }

    public void setHonourExp(int exp) {
        this.honourExp = exp;
    }

    public int getHonourExp() {
        return honourExp;
    }

    public void setHonorLevel(int level) {
        this.honorLevel = level;
    }

    public int getHonorLevel() {
        if (honorLevel == 0) {
            honorLevel++;
        }
        return honorLevel;
    }

    public void addHonorExp(int amount, boolean show) {
        if (getHonorLevel() == 0) {
            setHonorLevel(1);
        }
        if (getHonourExp() + amount >= getHonorLevel() * 500) {
            honorLevelUp();
            int leftamount = (getHonourExp() + amount) - ((getHonorLevel() - 1) * 500);
            leftamount = Math.min(leftamount, ((getHonorLevel()) * 500) - 1);
            setHonourExp(leftamount);
            return;
        }
        setHonourExp(getHonourExp() + amount);
        client.sendPacket(CWvsContext.updateAzwanFame(getHonorLevel(), getHonourExp(), true));
        client.sendPacket(CWvsContext.professionInfo("honorLeveling", 0, getHonorLevel(), getHonorNextExp()));
        if (show) {
            dropMessage(5, "您獲得了 " + amount + " 名譽經驗值.");
        }
    }

    public void honorLevelUp() {
        setHonorLevel(getHonorLevel() + 1);
        client.sendPacket(CWvsContext.updateAzwanFame(getHonorLevel(), getHonourExp(), true));
        if (getHonorLevel() == 2) {
            InnerSkillValueHolder diella = InnerAbillity.getInstance().renewSkill(0, -1, false);
            innerSkills.add(diella);
            changeSkillLevel(SkillFactory.getSkill(diella.getSkillId()), diella.getSkillLevel(), diella.getSkillLevel());
            client.sendPacket(CField.updateInnerPotential((byte) 1, diella.getSkillId(), diella.getSkillLevel(), diella.getRank()));
        } else if (getHonorLevel() == 30) {
            InnerSkillValueHolder is = InnerAbillity.getInstance().renewSkill(Randomizer.rand(0, 2), -1, false);
            innerSkills.add(is);
            changeSkillLevel(SkillFactory.getSkill(is.getSkillId()), is.getSkillLevel(), is.getSkillLevel());
            client.sendPacket(CField.updateInnerPotential((byte) 2, is.getSkillId(), is.getSkillLevel(), is.getRank()));
        } else if (getHonorLevel() == 70) {
            InnerSkillValueHolder beautiful = InnerAbillity.getInstance().renewSkill(Randomizer.rand(1, 3), -1, false);
            innerSkills.add(beautiful);
            changeSkillLevel(SkillFactory.getSkill(beautiful.getSkillId()), beautiful.getSkillLevel(), beautiful.getSkillLevel());
            client.sendPacket(CField.updateInnerPotential((byte) 3, beautiful.getSkillId(), beautiful.getSkillLevel(), beautiful.getRank()));
        }
    }

    public int getHonorNextExp() {
        if (getHonorLevel() == 0) {
            return 0;
        }
        return (getHonorLevel() + 1) * 500;
    }

    public void gainHonor(int honor, boolean show) {
        addHonorExp(honor, false);
        if (show) {
            client.sendPacket(InfoPacket.showInfo(honor + " Honor EXP obtained."));
        }
    }

    public void setKeyValue(String key, String values) {
        if (CustomValues.containsKey(key)) {
            CustomValues.remove(key);
        }
        CustomValues.put(key, values);
        keyvalue_changed = true;
    }

    public String getKeyValue(String key) {
        if (CustomValues.containsKey(key)) {
            return CustomValues.get(key);
        }
        return null;
    }

    public void resetRunningStack() {
        this.runningStack = 0;
    }

    public int getRunningStack() {
        return this.runningStack;
    }

    public void addRunningStack(int s) {
        runningStack += s;
    }

    public void setCardStack(byte amount) {
        this.cardStack = amount;
    }

    public byte getCardStack() {
        return this.cardStack;
    }

    public static void removePartTime(int cid) {
        Connection con = DatabaseConnection.getConnection();
        try {
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM parttime where cid = ?")) {
                ps.setInt(1, cid);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            System.out.println("Failed to remove part time job: " + ex);
        }
    }

    public static void addPartTime(PartTimeJob partTime) {
        if (partTime.getCharacterId() < 1) {
            return;
        }
        addPartTime(partTime.getCharacterId(), partTime.getJob(), partTime.getTime(), partTime.getReward());
    }

    public static void addPartTime(int cid, byte job, long time, int reward) {
        Connection con = DatabaseConnection.getConnection();
        try {
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO parttime (cid, job, time, reward) VALUES (?, ?, ?, ?)")) {
                ps.setInt(1, cid);
                ps.setByte(2, job);
                ps.setLong(3, time);
                ps.setInt(4, reward);
                ps.execute();
            }
        } catch (SQLException ex) {
            System.out.println("Failed to add part time job: " + ex);
        }
    }

    public static PartTimeJob getPartTime(int cid) {
        PartTimeJob partTime = new PartTimeJob(cid);
        Connection con = DatabaseConnection.getConnection();
        try {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM parttime WHERE cid = ?")) {
                ps.setInt(1, cid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        partTime.setJob(rs.getByte("job"));
                        partTime.setTime(rs.getLong("time"));
                        partTime.setReward(rs.getInt("reward"));
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Failed to retrieve part time job: " + ex);
        }
        return partTime;
    }

    public void equip(int itemId) {
        equip(itemId, false);
    }

    public void equip(int itemId, boolean replace) {
        equip(itemId, replace, true);
    }

    public void equip(int itemId, boolean replace, boolean add) {
        MapleInventory equipped = getInventory(MapleInventoryType.EQUIPPED);
        MapleInventory equip = getInventory(MapleInventoryType.EQUIP);
        Equip eqp = null;
        for (Item item : equip.newList()) {
            if (item.getItemId() == itemId) {
                eqp = (Equip) item;
            }
        }

        if (eqp == null) {
            if (add) {
                final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();
                Item item = li.getEquipById(itemId);
                item.setPosition(equip.getNextFreeSlot());
                item.setGMLog("從穿戴函數中獲得, 時間 " + FileoutputUtil.CurrentReadable_Time());
                MapleInventoryManipulator.addbyItem(client, item);
                eqp = (Equip) item;
            } else {
                return;
            }
        }

        short slot = 0;
        short[] slots = ItemConstants.getEquipedSlot(itemId);
        switch (slots.length) {
            case 0:
                if (isGM()) {
                    showInfo("穿戴函數", true, "未找到穿戴位置, 道具ID:" + itemId);
                }
                return;
            case 1:
                slot = slots[0];
                break;
            default:
                for (short i : slots) {
                    if (equipped.getItem(slot) == null) {
                        slot = i;
                        break;
                    }
                }
                if (slot == 0) {
                    slot = slots[0];
                }
                break;
        }
        if (slot == 0) {
            if (isGM()) {
                showInfo("穿戴函數", true, "未找到穿戴位置, 道具ID:" + itemId);
            }
            return;
        }

        if (replace && equipped.getItem(slot) != null) {
            equipped.removeSlot(slot);
            getClient().getSession().writeAndFlush(InventoryPacket.dropInventoryItem(MapleInventoryType.EQUIP, slot));
        }
        MapleInventoryManipulator.equip(client, eqp.getPosition(), slot);
    }

    public void unequip(int itemId) {
        unequip(itemId, false);
    }

    public void unequip(int itemId, boolean remove) {
        MapleInventory equipped = getInventory(MapleInventoryType.EQUIPPED);
        Equip eqp = null;
        if (itemId >= 0) {
            for (Item item : equipped.newList()) {
                if (item.getItemId() == itemId) {
                    eqp = (Equip) item;
                }
            }
        } else {
            eqp = (Equip) equipped.getItem((short) itemId);
        }

        if (eqp == null) {
            return;
        }

        if (remove) {
            equipped.removeSlot(eqp.getPosition());
            getClient().getSession().writeAndFlush(InventoryPacket.dropInventoryItem(MapleInventoryType.EQUIP, eqp.getPosition()));
        } else {
            MapleInventoryManipulator.unequip(client, eqp.getPosition(), getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
        }
    }

    public void dofkingputer() {
        final List<MapleSummon> toRemove = new ArrayList<>();
        visibleMapObjectsLock.writeLock().lock(); //We need to lock this later on anyway so do it now to prevent deadlocks.
        summonsLock.writeLock().lock();
        try {
            for (MapleSummon summon : summons) {
                if (getSummonsSize() > 1) {
                    for (MapleSummon subsummon : summons) {
                        if (summons.size() > 0) {
                            if (summons.indexOf(subsummon) == 0) {
                                map.broadcastMessage(SummonPacket.removeSummon(subsummon, true));
                                map.removeMapObject(subsummon);
                                visibleMapObjects.remove(subsummon);
                                toRemove.add(subsummon);
                            }
                        }
                    }
                    if (summons.size() > 0) {
                        if (summons.indexOf(summon) == 0) {
                            map.broadcastMessage(SummonPacket.removeSummon(summon, true));
                            map.removeMapObject(summon);
                            visibleMapObjects.remove(summon);
                            toRemove.add(summon);
                        }
                    }
                }
            }
            for (MapleSummon s : toRemove) {
                summons.remove(s);
            }
        } finally {
            summonsLock.writeLock().unlock();
            visibleMapObjectsLock.writeLock().unlock(); //lolwut
        }
    }

    public static enum FameStatus {

        OK, NOT_TODAY, NOT_THIS_MONTH
    }
  public int getOnlineTime() {
        return onlinetime;
    }

    public void addOnlineTime() {
        onlinetime++;
    }
}
