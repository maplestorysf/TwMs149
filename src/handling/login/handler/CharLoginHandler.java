package handling.login.handler;

import java.util.List;
import java.util.Calendar;

import client.inventory.Item;
import client.MapleClient;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.PartTimeJob;
import client.Skill;
import client.SkillEntry;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import client.SkillFactory;
import constants.JobConstants;
import constants.ServerConstants;
import handling.channel.ChannelServer;
import handling.login.LoginInformationProvider;
import handling.login.LoginInformationProvider.JobType;
import handling.login.LoginServer;
import handling.login.LoginWorker;
import handling.world.World;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import server.MapleItemInformationProvider;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.StringUtil;
import tools.packet.CField;
import tools.packet.LoginPacket;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;
import tools.packet.PacketHelper;

public class CharLoginHandler {

    private static boolean isLoginFailMaxCount(MapleClient c) {
        return c.loginAttempt > 5;
    }

    private static void addLoginFailCount(MapleClient c) {
        c.loginAttempt++;
    }

    private static int getLoginFailCount(MapleClient c) {
        return c.loginAttempt;
    }

    public static final void loginAuthRequest(final LittleEndianAccessor slea, final MapleClient c) {
        //String[] background = {"MapLogin", "MapLogin0", "MapLogin1", "MapLogin2"};
        //String bg = background[(int) (Math.random() * 5)];
        //c.sendPacket(LoginPacket.getLoginAUTH(bg));
    }

    public static final void set2ndPasswordRequest(final LittleEndianAccessor slea, final MapleClient c) {
        String username = slea.readMapleAsciiString();
        String password = slea.readMapleAsciiString();
        byte gender = slea.readByte();
        if (username == null || password == null || gender > 1 || gender < 0) {
            c.getSession().close();
            return;
        }

        if (c.getAccountName().equals(username) && c.getSecondPassword() == null) {
            c.setGender(gender);
            c.setSecondPassword(password);
            c.updateGender();
            c.updateSecondPassword();
            c.sendPacket(LoginPacket.getGenderChanged(c));
            c.updateLoginState(MapleClient.LOGIN_NOTLOGGEDIN, c.getSessionIPAddress());
            FileoutputUtil.logToFile("logs/data/註冊第二組.txt", "\r\n 時間　[" + FileoutputUtil.NowTime() + "] 帳號：　" + username + " 第二組密碼：" + password + " IP：/" + c.getSessionIPAddress() + " MAC： " + c.getClientMac(), false, false);
        } else {
            c.getSession().close();
        }
    }

    public static final void login(final LittleEndianAccessor slea, final MapleClient c) {
        String macData = readMacAddress(slea, c);
        slea.skip(17);
        String login = slea.readMapleAsciiString();
        String pwd = slea.readMapleAsciiString();
        c.setMacs(macData);
        final boolean ipBan = c.hasBannedIP();
        final boolean macBan = c.hasBannedMac();
        final boolean banned = (ipBan || macBan);
        if (ServerConstants.AUTO_REGISTER) {
            if (!AutoRegister.getAccountExists(login)) {
                if (c.hasBannedIP() || c.hasBannedMac()) {
                    c.sendPacket(LoginPacket.getLoginFailed(1));
                    c.sendPacket(CWvsContext.serverNotice(1, "由於有使用不法程式的紀錄，無法註冊新帳號"));
                    return;
                }
                if (login.length() > 13) {
                    c.sendPacket(LoginPacket.getLoginFailed(1));
                    c.sendPacket(CWvsContext.serverNotice(1, "帳號太長，請重新輸入。"));
                    return;
                }
                if (login.contains(" ")) {
                    c.sendPacket(LoginPacket.getLoginFailed(1));
                    c.sendPacket(CWvsContext.serverNotice(1, "您的帳號包含空白！\r\n請重新輸入帳號。"));
                    return;
                }
                if (AutoRegister.createAccount(login, pwd, c.getSession().remoteAddress().toString(), macData)) {
                    c.sendPacket(LoginPacket.getLoginFailed(1));
                    c.sendPacket(CWvsContext.serverNotice(1, "帳號註冊成功！\r\n請重新登入遊戲。\r\n帳號：" + login + "\r\n密碼：" + pwd));
                    return;
                } else {
                    c.sendPacket(LoginPacket.getLoginFailed(1));
                    c.sendPacket(CWvsContext.serverNotice(1, "同一台電腦只能建立三組帳號。"));
                    return;
                }
            }
        }
        int loginok = c.login(login, pwd, banned);
        final Calendar tempbannedTill = c.getTempBanCalendar();

        if (loginok == 0 && World.isPlayerSaving(c.getAccID())) {
            c.sendPacket(CWvsContext.serverNotice(1, "您登入的速度過快!\r\n請重新輸入."));
            c.sendPacket(LoginPacket.getLoginFailed(1));
            return;
        }

        if (loginok == 0 && banned && !c.isGm()) {
            FileoutputUtil.logToFile("logs/data/" + (macBan ? "MAC" : "IP") + "封鎖_登入帳號.txt", "\r\n 時間　[" + FileoutputUtil.NowTime() + "]  目前MAC位址:" + macData + " IP地址: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號：　" + login + " 密碼：" + pwd);
            c.sendPacket(LoginPacket.getLoginFailed(3));
            return;
        }

        if (loginok != 0) {
            if (!isLoginFailMaxCount(c)) {
                addLoginFailCount(c);
                c.sendPacket(LoginPacket.getLoginFailed(loginok));
            } else {
                c.getSession().close();
            }
        } else if (tempbannedTill.getTimeInMillis() != 0) {
            if (!isLoginFailMaxCount(c)) {
                addLoginFailCount(c);
                c.clearInformation();
                c.sendPacket(LoginPacket.getTempBan(PacketHelper.getTime(tempbannedTill.getTimeInMillis()), c.getBanReason()));
            } else {
                c.getSession().close();
            }
        } else {
            c.updateMacs(macData);
            c.loginAttempt = 0;
            LoginWorker.registerClient(c);
            FileoutputUtil.logToFile("logs/data/登入帳號.txt", "\r\n 時間　[" + FileoutputUtil.NowTime() + "]  MAC 地址 : " + c.getClientMac() + " IP 地址 : " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號：　" + login + " 密碼：" + pwd);
        }
    }

    public static final void ServerListRequest(final MapleClient c, boolean frompacket) {
        if (frompacket) {
            int state = c.getLoginState();
            if (state != MapleClient.LOGIN_CHARLIST) {
                c.getSession().close();
                return;
            }
            c.updateLoginState(MapleClient.LOGIN_SERVERLIST, c.getSessionIPAddress());

        }
        LoginServer.forceRemoveClient(c, false);
        ChannelServer.forceRemovePlayerByCharNameFromDataBase(c, MapleClient.loadCharacterNamesByAccId(c.getAccID()));

        c.sendPacket(LoginPacket.getLoginWelcome());
        c.sendPacket(LoginPacket.getServerList(0, LoginServer.getLoad()));
        //c.sendPacket(CField.getServerList(1, "Scania", LoginServer.getInstance().getChannels(), 1200));
        //c.sendPacket(CField.getServerList(2, "Scania", LoginServer.getInstance().getChannels(), 1200));
        //c.sendPacket(CField.getServerList(3, "Scania", LoginServer.getInstance().getChannels(), 1200));
        c.sendPacket(LoginPacket.getEndOfServerList());
        //c.sendPacket(LoginPacket.enableRecommended());
        //c.sendPacket(LoginPacket.sendRecommended(0, LoginServer.getEventMessage()));
    }

    public static final void ServerStatusRequest(final MapleClient c) {
        LoginServer.forceRemoveClient(c, false);
        ChannelServer.forceRemovePlayerByCharNameFromDataBase(c, MapleClient.loadCharacterNamesByAccId(c.getAccID()));

        // 0 = Select world normally
        // 1 = "Since there are many users, you may encounter some..."
        // 2 = "The concurrent users in this world have reached the max"
        final int numPlayer = LoginServer.getUsersOn();
        final int userLimit = LoginServer.getUserLimit();
        if (numPlayer >= userLimit) {
            c.sendPacket(LoginPacket.getServerStatus(2));
        } else if (numPlayer * 2 >= userLimit) {
            c.sendPacket(LoginPacket.getServerStatus(1));
        } else {
            c.sendPacket(LoginPacket.getServerStatus(0));
        }
    }

    public static final void CharlistRequest(final LittleEndianAccessor slea, final MapleClient c) {
//        if (c.getCloseSession()) {
//            if (ServerConfig.LOG_DC) {
//            FileoutputUtil.logToFile("logs/data/DC.txt", "\r\n伺服器主動斷開客戶端連接，調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
//            }
//            return;
//        }
        LoginServer.forceRemoveClient(c, false);
        ChannelServer.forceRemovePlayerByCharNameFromDataBase(c, MapleClient.loadCharacterNamesByAccId(c.getAccID()));
        if (!c.isLoggedIn() && !c.isLoggedIn_beforeInGame()) {
            c.getSession().close();
            return;
        }
        if (c.getLoginState() != MapleClient.LOGIN_SERVERLIST && c.getLoginState() != MapleClient.LOGIN_CHARLIST) {
            c.getSession().close();
            return;
        }
        c.updateLoginState(MapleClient.LOGIN_CHARLIST, c.getSessionIPAddress());

        slea.readByte();
        final int server = slea.readByte();
        final int channel = slea.readByte() + 1;
        if (!World.isChannelAvailable(channel) || server != 0) { //TODOO: MULTI WORLDS
            c.sendPacket(LoginPacket.getLoginFailed(10)); //cannot process so many
            return;
        }

        //System.out.println("Client " + c.getSession().remoteAddress().toString().split(":")[0] + " is connecting to server " + server + " channel " + channel + "");
        final List<MapleCharacter> chars = c.loadCharacters(server);
        if (chars != null && ChannelServer.getInstance(channel) != null) {
            c.setWorld(server);
            c.setChannel(channel);
            c.sendPacket(LoginPacket.getCharList(c.getSecondPassword(), chars, c.getCharacterSlots()));
        } else {
            c.getSession().close();
        }
    }

    public static void updateCCards(final LittleEndianAccessor slea, final MapleClient c) {
        if (slea.available() != 24 || !c.isLoggedIn()) {
            c.getSession().close();
            return;
        }
        final Map<Integer, Integer> cids = new LinkedHashMap<>();
        for (int i = 1; i <= 6; i++) { // 6 chars
            final int charId = slea.readInt();
            if ((!c.login_Auth(charId) && charId != 0) || ChannelServer.getInstance(c.getChannel()) == null) {
                c.getSession().close();
                return;
            }
            cids.put(i, charId);
        }
        c.updateCharacterCards(cids);
    }

    public static final void CheckCharName(final String name, final MapleClient c) {
        c.sendPacket(LoginPacket.charNameResponse(name, !(MapleCharacterUtil.canCreateChar(name, c.isGm()) && (!LoginInformationProvider.getInstance().isForbiddenName(name) || c.isGm()))));
    }

    public static final void CreateChar(final LittleEndianAccessor slea, final MapleClient c) {
        if (!c.isLoggedIn() && !c.isLoggedIn_beforeInGame()) {
            c.getSession().close();
            return;
        }
        final String name = slea.readMapleAsciiString();
        LoginInformationProvider lli = LoginInformationProvider.getInstance();
        if (!MapleCharacterUtil.canCreateChar(name, !c.isGm()) || (lli.isForbiddenName(name))) {
            System.out.println("異常角色名稱: " + name);
            return;
        }
        final int job_type = slea.readInt(); // BIGBANG: 0 = Resistance, 1 = Adventurer, 2 = Cygnus, 3 = Aran, 4 = Evan, 5 = mercedes
        JobType job = JobType.getByType(job_type);
        if (job == null) {
            System.out.println("未知職業類型: " + job_type);
            return;
        }
        for (JobConstants.JobType j : JobConstants.JobType.values()) {
            if (j.getJobType() == job_type) {
                if (!j.enableCreate()) {
                    c.sendPacket(CWvsContext.serverNotice(1, "目前無法選擇本職業。"));
                    c.sendPacket(LoginPacket.getLoginFailed(1));
                    return;
                }
            }
        }
        final short subcategory = slea.readShort(); //whether dual blade = 1 or adventurer = 0
        final byte gender = slea.readByte(); //??idk corresponds with the thing in addCharStats
        byte skinColor = slea.readByte(); // 01
        int hairColor = 0;
        final byte unk2 = slea.readByte(); // 08
        final boolean resistance = (job == JobType.末日反抗軍);
        final boolean mercedes = (job == JobType.精靈遊俠);
        final boolean demon = (job == JobType.惡魔);
        final boolean phantom = (job == JobType.幻影俠盜);
        final int face = slea.readInt();
        final int hair = slea.readInt();
        final int demonMark = demon ? slea.readInt() : 0;
        final int top = slea.readInt();
        final int bottom = (resistance || mercedes || demon || phantom) ? 0 : slea.readInt();
        final int cape = phantom ? slea.readInt() : 0;
        final int shoes = slea.readInt();
        final int weapon = slea.readInt();
        final int shield = demon ? slea.readInt() : (mercedes ? 1352000 : 0);
//        switch (job) {
//            case 惡魔:
//                if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, job.type, face) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 1, job.type, hair)
//                        || !LoginInformationProvider.getInstance().isEligibleItem(gender, 2, job.type, demonMark) || (skinColor != 0 && skinColor != 13)
//                        || !LoginInformationProvider.getInstance().isEligibleItem(gender, 3, job.type, top) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 4, job.type, shoes)
//                        || !LoginInformationProvider.getInstance().isEligibleItem(gender, 5, job.type, weapon) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 6, job.type, shield)) {
//                    return;
//                }
//                break;
//            case 精靈遊俠:
//                if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, job.type, face) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 1, job.type, hair)
//                        || !LoginInformationProvider.getInstance().isEligibleItem(gender, 2, job.type, top) || (skinColor != 0 && skinColor != 12)
//                        || !LoginInformationProvider.getInstance().isEligibleItem(gender, 3, job.type, shoes) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 4, job.type, weapon)) {
//                    return;
//                }
//                break;
//            default:
//                if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, job.type, face) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 1, job.type, hair)
//                        || !LoginInformationProvider.getInstance().isEligibleItem(gender, 2, job.type, hairColor) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 3, job.type, skinColor)
//                        || !LoginInformationProvider.getInstance().isEligibleItem(gender, 4, job.type, top) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 5, job.type, bottom)
//                        || !LoginInformationProvider.getInstance().isEligibleItem(gender, 6, job.type, shoes) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 7, job.type, weapon)) {
//                    return;
//                }
//                break;
//        }
        MapleCharacter newchar = MapleCharacter.getDefault(c, job);
        newchar.setWorld((byte) c.getWorld());
        newchar.setFace(face);
        newchar.setHair(hair + hairColor);
        newchar.setGender(gender);
        newchar.setName(name);
        newchar.setSkinColor(skinColor);
        newchar.setDemonMarking(demonMark);

        final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();
        final MapleInventory equip = newchar.getInventory(MapleInventoryType.EQUIPPED);

        Item item = li.getEquipById(top);
        item.setPosition((byte) -5);
        equip.addFromDB(item);

        if (bottom > 0) { //resistance have overall
            item = li.getEquipById(bottom);
            item.setPosition((byte) -6);
            equip.addFromDB(item);
        }

        if (cape > 0) {
            item = li.getEquipById(cape);
            item.setPosition((byte) -9);
            equip.addFromDB(item);
        }

        item = li.getEquipById(shoes);
        item.setPosition((byte) -7);
        equip.addFromDB(item);

        item = li.getEquipById(weapon);
        item.setPosition((byte) -11);
        equip.addFromDB(item);

        if (shield > 0) {
            item = li.getEquipById(shield);
            item.setPosition((byte) -10);
            equip.addFromDB(item);
        }

        newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000013, (byte) 0, (short) 100, (byte) 0));
        newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000014, (byte) 0, (short) 100, (byte) 0));
        //blue/red pots
        switch (job) {
            case 末日反抗軍: // Resistance
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161001, (byte) 0, (short) 1, (byte) 0));
                break;
            case 冒險家: // Adventurer
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161001, (byte) 0, (short) 1, (byte) 0));
                break;
            case 皇家騎士團: // Cygnus
                newchar.setQuestAdd(MapleQuest.getInstance(20022), (byte) 1, "1");
                newchar.setQuestAdd(MapleQuest.getInstance(20010), (byte) 1, null); //>_>_>_> ugh
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161047, (byte) 0, (short) 1, (byte) 0));
                break;
            case 狂狼勇士: // Aran
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161048, (byte) 0, (short) 1, (byte) 0));
                break;
            case 龍魔導士: //Evan
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161052, (byte) 0, (short) 1, (byte) 0));
                break;
            case 精靈遊俠: // Mercedes
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161079, (byte) 0, (short) 1, (byte) 0));
                final Map<Skill, SkillEntry> ss = new HashMap<>();
                ss.put(SkillFactory.getSkill(20021000), new SkillEntry((byte) 0, (byte) 0, -1, -1));
                ss.put(SkillFactory.getSkill(20021001), new SkillEntry((byte) 0, (byte) 0, -1, -1));
                ss.put(SkillFactory.getSkill(20020002), new SkillEntry((byte) 0, (byte) 0, -1, -1));
                ss.put(SkillFactory.getSkill(20020022), new SkillEntry((byte) 1, (byte) 1, -1, -1));
                ss.put(SkillFactory.getSkill(20020109), new SkillEntry((byte) 1, (byte) 1, -1, -1));
                ss.put(SkillFactory.getSkill(20021110), new SkillEntry((byte) 1, (byte) 1, -1, -1));
                ss.put(SkillFactory.getSkill(20020111), new SkillEntry((byte) 1, (byte) 1, -1, -1));
                ss.put(SkillFactory.getSkill(20020112), new SkillEntry((byte) 1, (byte) 1, -1, -1));
                ss.put(SkillFactory.getSkill(20021181), new SkillEntry((byte) -1, (byte) 0, -1, -1));
                ss.put(SkillFactory.getSkill(20021166), new SkillEntry((byte) -1, (byte) 0, -1, -1));
                newchar.changeSkillLevel_Skip(ss, false);
                break;
            case 惡魔: //Demon
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161054, (byte) 0, (short) 1, (byte) 0));
                final Map<Skill, SkillEntry> ss2 = new HashMap<>();
                ss2.put(SkillFactory.getSkill(30011000), new SkillEntry((byte) 0, (byte) 0, -1, -1));
                ss2.put(SkillFactory.getSkill(30011001), new SkillEntry((byte) 0, (byte) 0, -1, -1));
                ss2.put(SkillFactory.getSkill(30010002), new SkillEntry((byte) 0, (byte) 0, -1, -1));
                ss2.put(SkillFactory.getSkill(30010185), new SkillEntry((byte) 1, (byte) 1, -1, -1));
                ss2.put(SkillFactory.getSkill(30010112), new SkillEntry((byte) 1, (byte) 1, -1, -1));
                ss2.put(SkillFactory.getSkill(30010111), new SkillEntry((byte) 1, (byte) 1, -1, -1));
                ss2.put(SkillFactory.getSkill(30010110), new SkillEntry((byte) 1, (byte) 1, -1, -1));
                ss2.put(SkillFactory.getSkill(30010022), new SkillEntry((byte) 1, (byte) 1, -1, -1));
                ss2.put(SkillFactory.getSkill(30011109), new SkillEntry((byte) 1, (byte) 1, -1, -1));
                ss2.put(SkillFactory.getSkill(30011170), new SkillEntry((byte) 1, (byte) 1, -1, -1));
                ss2.put(SkillFactory.getSkill(30011169), new SkillEntry((byte) 1, (byte) 1, -1, -1));
                ss2.put(SkillFactory.getSkill(30011168), new SkillEntry((byte) 1, (byte) 1, -1, -1));
                ss2.put(SkillFactory.getSkill(30011167), new SkillEntry((byte) 1, (byte) 1, -1, -1));
                ss2.put(SkillFactory.getSkill(30010166), new SkillEntry((byte) 1, (byte) 1, -1, -1));
                newchar.changeSkillLevel_Skip(ss2, false);
                break;
            case 幻影俠盜:
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161080, (byte) 0, (short) 1, (byte) 0));
                break;
        }

        if (MapleCharacterUtil.canCreateChar(name, c.isGm()) && (!LoginInformationProvider.getInstance().isForbiddenName(name) || c.isGm()) && (c.isGm() || c.canMakeCharacter(c.getWorld()))) {
            MapleCharacter.saveNewCharToDB(newchar, job, subcategory);
            c.sendPacket(LoginPacket.addNewCharEntry(newchar, true));
            c.createdChar(newchar.getId());
        } else {
            c.sendPacket(LoginPacket.addNewCharEntry(newchar, false));
        }
    }

    public static final void CreateUltimate(final LittleEndianAccessor slea, final MapleClient c) {
        if (!c.isLoggedIn() && !c.isLoggedIn_beforeInGame()) {
            c.sendPacket(CField.createUltimate(2));
            return;
        }
        if (c.getPlayer() == null || c.getPlayer().getLevel() < 120 || c.getPlayer().getMapId() != 130000000 || c.getPlayer().getQuestStatus(20734) != 0 || c.getPlayer().getQuestStatus(20616) != 2 || !GameConstants.isKOC(c.getPlayer().getJob()) || !c.canMakeCharacter(c.getPlayer().getWorld())) {
            //c.getPlayer().dropMessage(1, "請檢查角色欄位空間。");
            c.sendPacket(CField.createUltimate(2));
            return;
        }
        //System.out.println(slea.toString());
        final String name = slea.readMapleAsciiString();

        final int job = slea.readInt(); //job ID
        if (job < 110 || job > 520 || job % 10 > 0 || (job % 100 != 10 && job % 100 != 20 && job % 100 != 30) || job == 430) {
            c.getPlayer().dropMessage(1, "An error has occurred.");
            c.sendPacket(CField.createUltimate(0));
            return;
        }
        final int face = slea.readInt();
        final int hair = slea.readInt();

        final int hat = slea.readInt();
        final int top = slea.readInt();
        final int glove = slea.readInt();
        final int shoes = slea.readInt();
        final int weapon = slea.readInt();

        final byte gender = c.getPlayer().getGender();
        JobType jobType = JobType.冒險家;
        /*if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, jobType.type, face)) {
            c.getPlayer().dropMessage(1, "An error occured.");
            c.sendPacket(CField.createUltimate(0));
            return;
        }*/
        jobType = JobType.終極冒險家;
        /*if (!LoginInformationProvider.getInstance().isEligibleItem(-1, job, jobType.type, hat) || !LoginInformationProvider.getInstance().isEligibleItem(-1, job, jobType.type, top)
                || !LoginInformationProvider.getInstance().isEligibleItem(-1, job, jobType.type, glove) || !LoginInformationProvider.getInstance().isEligibleItem(-1, job, jobType.type, shoes)
                || !LoginInformationProvider.getInstance().isEligibleItem(-1, job, jobType.type, weapon)) {
            c.getPlayer().dropMessage(1, "An error occured.");
            c.sendPacket(CField.createUltimate(0));
            return;
        }*/

        MapleCharacter newchar = MapleCharacter.getDefault(c, jobType);
        newchar.setJob(job);
        newchar.setWorld((byte) c.getPlayer().getWorld());
        newchar.setFace(face);
        newchar.setHair(hair);
        newchar.setGender(gender);
        newchar.setName(name);
        newchar.setSkinColor((byte) 3); //troll
        newchar.setLevel((short) 51);
        newchar.getStat().str = (short) 4;
        newchar.getStat().dex = (short) 4;
        newchar.getStat().int_ = (short) 4;
        newchar.getStat().luk = (short) 4;
        newchar.setRemainingAp((short) 254); //49*5 + 25 - 16
        newchar.setRemainingSp(job / 100 == 2 ? 128 : 122); //2 from job advancements. 120 from leveling. (mages get +6)
        newchar.getStat().maxhp += 150; //Beginner 10 levels
        newchar.getStat().maxmp += 125;
        switch (job) {
            case 110:
            case 120:
            case 130:
                newchar.getStat().maxhp += 600; //Job Advancement
                newchar.getStat().maxhp += 2000; //Levelup 40 times
                newchar.getStat().maxmp += 200;
                break;
            case 210:
            case 220:
            case 230:
                newchar.getStat().maxmp += 600;
                newchar.getStat().maxhp += 500; //Levelup 40 times
                newchar.getStat().maxmp += 2000;
                break;
            case 310:
            case 320:
            case 410:
            case 420:
            case 520:
                newchar.getStat().maxhp += 500;
                newchar.getStat().maxmp += 250;
                newchar.getStat().maxhp += 900; //Levelup 40 times
                newchar.getStat().maxmp += 600;
                break;
            case 510:
                newchar.getStat().maxhp += 500;
                newchar.getStat().maxmp += 250;
                newchar.getStat().maxhp += 450; //Levelup 20 times
                newchar.getStat().maxmp += 300;
                newchar.getStat().maxhp += 800; //Levelup 20 times
                newchar.getStat().maxmp += 400;
                break;
            default:
                return;
        }
        for (int i = 2490; i < 2507; i++) {
            newchar.setQuestAdd(MapleQuest.getInstance(i), (byte) 2, null);
        }
        newchar.setQuestAdd(MapleQuest.getInstance(29947), (byte) 2, null);
        newchar.setQuestAdd(MapleQuest.getInstance(GameConstants.ULT_EXPLORER), (byte) 0, c.getPlayer().getName());

        final Map<Skill, SkillEntry> ss = new HashMap<>();
        ss.put(SkillFactory.getSkill(1074 + (job / 100)), new SkillEntry((byte) 5, (byte) 5, -1, -1));
        ss.put(SkillFactory.getSkill(80), new SkillEntry((byte) 1, (byte) 1, -1, -1));
        newchar.changeSkillLevel_Skip(ss, false);
        final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();

        int[] items = new int[]{1142257, hat, top, shoes, glove, weapon, hat + 1, top + 1, shoes + 1, glove + 1, weapon + 1}; //brilliant = fine+1
        for (byte i = 0; i < items.length; i++) {
            Item item = li.getEquipById(items[i]);
            item.setPosition((byte) (i + 1));
            newchar.getInventory(MapleInventoryType.EQUIP).addFromDB(item);
        }
        newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000004, (byte) 0, (short) 200, (byte) 0));
        //newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000004, (byte) 0, (short) 100, (byte) 0));
        c.getPlayer().fakeRelog();
        if (MapleCharacterUtil.canCreateChar(name, c.isGm()) && (!LoginInformationProvider.getInstance().isForbiddenName(name) || c.isGm())) {
            MapleCharacter.saveNewCharToDB(newchar, jobType, (short) 0);
            MapleQuest.getInstance(20734).forceComplete(c.getPlayer(), 1101000);
            c.sendPacket(CField.createUltimate(0));
        } else {
            System.out.println("異常角色名稱: " + name);
            c.getPlayer().dropMessage(1, "遊戲名稱中包含禁止字元或太長或太短");
            c.sendPacket(CField.createUltimate(2));
        }
    }

    public static final void DeleteChar(final LittleEndianAccessor slea, final MapleClient c) {
        String Secondpw_Client = slea.readMapleAsciiString();
        final int Character_ID = slea.readInt();
        if (!c.isLoggedIn() && !c.isLoggedIn_beforeInGame()) {
            c.getSession().close();
            return;
        }
        if (!c.login_Auth(Character_ID) || isLoginFailMaxCount(c)) {
            c.getSession().close();
            return; // Attempting to delete other character
        }
        byte state = 0;
        if (c.getLoginState() != MapleClient.LOGIN_CHARLIST) {
            c.getSession().close();
            return;
        }
        if (c.getSecondPassword() != null) { // On the server, there's a second password
            if (Secondpw_Client == null) { // Client's hacking
                c.getSession().close();
                return;
            } else {
                if (!c.CheckSecondPassword(Secondpw_Client)) { // Wrong Password
                    state = 20;
                }
            }
        }

        if (state == 0) {
            state = (byte) c.deleteCharacter(Character_ID);
        }
        c.sendPacket(LoginPacket.deleteCharResponse(Character_ID, state));
    }

    public static final void Character_WithoutSecondPassword(final LittleEndianAccessor slea, final MapleClient c, final boolean haspic, final boolean view) {
        if (!LoginServer.containClient(c)) {
            return;
        }
        if (c.getCloseSession()) {
            return;
        }
        final int charId = slea.readInt();
        if (!c.isLoggedIn() && !c.isLoggedIn_beforeInGame()) {
            c.getSession().close();
            return;
        }
        if (isLoginFailMaxCount(c) || c.getSecondPassword() == null || !c.login_Auth(charId) || ChannelServer.getInstance(c.getChannel()) == null || c.getWorld() != 0) { // TODOO: MULTI WORLDS
            c.getSession().close();
            return;
        }
        if (c.getLoginState() != MapleClient.LOGIN_CHARLIST) {
            c.getSession().close();
            return;
        }
        if (c.getIdleTask() != null) {
            c.getIdleTask().cancel(true);
        }
        final String s = c.getSessionIPAddress();
        World.clearChannelChangeDataByAccountId(c.getAccID());
        LoginServer.putLoginAuth(charId, s.substring(s.indexOf('/') + 1, s.length()), c.getTempIP(), c.getChannel(), c.getClientMac());
        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, s);
        byte[] ip = {127, 0, 0, 1};
        try {
            ip = InetAddress.getByName(ChannelServer.getInstance(c.getChannel()).getSocket().split(":")[0]).getAddress();
        } catch (UnknownHostException ex) {
        }
        c.sendPacket(CField.getServerIP(c, ip, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getSocket().split(":")[1]), charId));
        System.setProperty(String.valueOf(c.getAccountName().toLowerCase()), "1");
        System.setProperty(String.valueOf(charId), "1");
    }

    public static final void Character_WithSecondPassword(final LittleEndianAccessor slea, final MapleClient c, final boolean view) {
//        final String password = slea.readMapleAsciiString();
//        final int charId = slea.readInt();
//        if (view) {
//            c.setChannel(1);
//            c.setWorld(slea.readInt());
//        }
//        if (!LoginServer.containClient(c)) {
//            return;
//        }
//        if (!c.isLoggedIn() || loginFailCount(c) || c.getSecondPassword() == null || !c.login_Auth(charId) || ChannelServer.getInstance(c.getChannel()) == null || c.getWorld() != 0) { // TODOO: MULTI WORLDS
//            c.getSession().close();
//            return;
//        }
//        c.updateMacs(slea.readMapleAsciiString());
//        if (c.CheckSecondPassword(password) && password.length() >= 6 && password.length() <= 16) {
//            if (c.getIdleTask() != null) {
//                c.getIdleTask().cancel(true);
//            }
//            final String s = c.getSessionIPAddress();
//            LoginServer.putLoginAuth(charId, s.substring(s.indexOf('/') + 1, s.length()), c.getTempIP(), c.getChannel(), c.getClientMac());
//            c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, s);
//            byte[] ip = {127, 0, 0, 1};
//            try {
//                ip = InetAddress.getByName(ChannelServer.getInstance(c.getChannel()).getSocket().split(":")[0]).getAddress();
//            } catch (UnknownHostException ex) {
//            }
//            c.sendPacket(CField.getServerIP(c, ip, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getSocket().split(":")[1]), charId));
//        } else {
//            c.sendPacket(LoginPacket.secondPwError((byte) 0x14));
//        }
    }

    public static void PartJob(LittleEndianAccessor slea, MapleClient c) {
        if (c.getPlayer() != null || !c.isLoggedIn()) {
            c.getSession().close();
            return;
        }
        final byte mode = slea.readByte();
        final int cid = slea.readInt();
        if (mode == 1) {
            final PartTimeJob partTime = MapleCharacter.getPartTime(cid);
            final byte job = slea.readByte();
            if (/*chr.getLevel() < 30 || */job < 0 || job > 5 || partTime.getReward() > 0
                    || (partTime.getJob() > 0 && partTime.getJob() <= 5)) {
                c.getSession().close();
                return;
            }
            partTime.setTime(System.currentTimeMillis());
            partTime.setJob(job);
            c.sendPacket(LoginPacket.updatePartTimeJob(partTime));
            MapleCharacter.removePartTime(cid);
            MapleCharacter.addPartTime(partTime);
        } else if (mode == 2) {
            final PartTimeJob partTime = MapleCharacter.getPartTime(cid);
            if (/*chr.getLevel() < 30 || */partTime.getReward() > 0
                    || partTime.getJob() < 0 || partTime.getJob() > 5) {
                c.getSession().close();
                return;
            }
            final long distance = (System.currentTimeMillis() - partTime.getTime()) / (60 * 60 * 1000L);
            if (distance > 1) {
                partTime.setReward((int) (((partTime.getJob() + 1) * 1000L) + distance));
            } else {
                partTime.setJob((byte) 0);
                partTime.setReward(0);
            }
            partTime.setTime(System.currentTimeMillis());
            MapleCharacter.removePartTime(cid);
            MapleCharacter.addPartTime(partTime);
            c.sendPacket(LoginPacket.updatePartTimeJob(partTime));
        }
    }

    public static String readMacAddress(final LittleEndianAccessor slea, final MapleClient c) {
        int[] bytes = new int[6];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = slea.readByteAsInt();
        }
        StringBuilder sps = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sps.append(StringUtil.getLeftPaddedStr(Integer.toHexString(bytes[i]).toUpperCase(), '0', 2));
            sps.append("-");
        }
        return sps.toString().substring(0, sps.toString().length() - 1);
    }
}
