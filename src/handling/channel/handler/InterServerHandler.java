package handling.channel.handler;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;

import client.MapleQuestStatus;
import client.SkillFactory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ServerConstants;
import handling.cashshop.CashShopServer;
import handling.cashshop.handler.CashShopOperation;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.login.handler.CharLoginHandler;
import handling.world.CharacterIdChannelPair;
import handling.world.MapleMessenger;
import handling.world.MapleMessengerCharacter;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.PlayerBuffStorage;
import handling.world.World;
import handling.world.exped.MapleExpedition;
import handling.world.guild.MapleGuild;
import java.util.List;

import scripting.NPCScriptManager;
import server.ServerProperties;
import server.life.MapleLifeFactory;
import server.life.MapleNPC;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import server.maps.SavedLocationType;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.Quadra;
import tools.packet.CField;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.BuddylistPacket;
import tools.packet.CWvsContext.ExpeditionPacket;
import tools.packet.CWvsContext.FamilyPacket;
import tools.packet.CWvsContext.GuildPacket;
import tools.packet.MTSCSPacket;

public class InterServerHandler {

    public static final void EnterCS(final MapleClient c, final MapleCharacter chr, final boolean mts) {
        if (c.getCloseSession()) {
            return;
        }
        if (!MapleCharacterUtil.isExistCharacterInDataBase(c.getPlayer().getId())) {
            FileoutputUtil.logToFile("logs/hack/角色複製.txt", FileoutputUtil.CurrentReadable_Time() + " <刪除角色複製CS> 玩家id: " + c.getPlayer().getId() + " 帳號id:" + c.getAccID());
            c.getSession().close();
            return;
        }
        if (chr.hasBlockedInventory() || chr.getMap() == null || chr.getEventInstance() != null || c.getChannelServer() == null) {
            c.sendPacket(CField.serverBlocked(2));
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
//        if (mts && chr.getLevel() < 50) {
//            chr.dropMessage(1, "You may not enter the Maple Trading System until level 50.");
//            c.sendPacket(CWvsContext.enableActions());
//            return;
//        }
        byte[] packet = MTSCSPacket.warpCS(c);
        if (packet.length >= 65535) {
            FileoutputUtil.logToFile("logs/except/商城無法進入.txt", FileoutputUtil.CurrentReadable_Time() + " 玩家:" + c.getPlayer().getName() + " 多餘封包數量: " + (packet.length - 65535) + " \r\n");
            c.getPlayer().dropMessage(5, "請將背包的東西減少後再進入商城(" + (packet.length - 65535) + ")");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (c.getLoginState() != MapleClient.LOGIN_LOGGEDIN) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (World.getPendingCharacterSize() >= 100) {
            chr.dropMessage(1, "伺服器忙碌中，請稍候再試。");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        //if (c.getChannel() == 1 && !c.getPlayer().isGM()) {
        //    c.getPlayer().dropMessage(5, "You may not enter on this channel. Please change channels and try again.");
        //    c.sendPacket(CWvsContext.enableActions());
        //    return;
        //}
        final ChannelServer ch = ChannelServer.getInstance(c.getChannel());

        if (World.isPlayerSaving(chr.getAccountID())) {
            FileoutputUtil.logToFile("logs/data/儲存中載入.txt", FileoutputUtil.CurrentReadable_Time() + " 角色: " + chr.getName() + "(" + chr.getId() + ") 帳號: " + c.getAccountName() + "(" + chr.getAccountID() + ") \r\n ");
            c.getSession().close();
            return;
        }
		
        try {
            chr.saveToDB(false, false);
        } catch (Exception ex) {
        }
		
        chr.changeRemoval();

        if (chr.getMessenger() != null) {
            MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(chr);
            World.Messenger.leaveMessenger(chr.getMessenger().getId(), messengerplayer);
        }
        PlayerBuffStorage.addBuffsToStorage(chr.getId(), chr.getAllBuffs());
        PlayerBuffStorage.addCooldownsToStorage(chr.getId(), chr.getCooldowns());
        PlayerBuffStorage.addDiseaseToStorage(chr.getId(), chr.getAllDiseases());
        World.ChannelChange_Data(c, chr, -10);
        ch.removePlayer(chr);
        c.updateLoginState(MapleClient.CASH_SHOP_TRANSITION, c.getSessionIPAddress());
        chr.getMap().removePlayer(chr);
        c.sendPacket(CField.getChannelChange(c, CashShopServer.getSocket().split(":")[0], Integer.parseInt(CashShopServer.getSocket().split(":")[1])));
        c.setPlayer(null);
        c.setReceiving(false);
    }

    public static final void EnterMTS(final MapleClient c, final MapleCharacter chr) {
        if (chr.hasBlockedInventory() || chr.getMap() == null || chr.getEventInstance() != null || c.getChannelServer() == null) {
            chr.dropMessage(1, "請稍候再試。");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (ServerConstants.MTS_FM) {
            if (chr.getLevel() < 10 && chr.getJob() != 200) {
                chr.dropMessage(5, "未達10級的玩家無法使用此功能。");
                c.sendPacket(CWvsContext.enableActions());
                return;
            }
            if (chr.getMapId() >= 910000000 && chr.getMapId() <= 910000022) {
                c.sendPacket(CWvsContext.enableActions());
                return;
            }
            chr.saveLocation(SavedLocationType.fromString("FREE_MARKET"));
            final MapleMap warpz = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(910000000);
            if (warpz != null) {
                chr.changeMap(warpz, warpz.getPortal("st00"));
            } else {
                chr.dropMessage(5, "請稍候再試。");
            }
        } else {
            int npcid = ServerConstants.MTS_NpcId;
            String scriptname = ServerConstants.MTS_NpcScript;
            MapleNPC npc = MapleLifeFactory.getNPC(npcid);
            if (npc != null && !npc.getName().equalsIgnoreCase("MISSINGNO")) {
                c.sendPacket(CWvsContext.enableActions());
                NPCScriptManager.getInstance().start(c, npcid, scriptname);
            } else {
                c.getPlayer().dropMessage(1, "發生異常，請回報給GM。");
            }
        }
        c.sendPacket(CWvsContext.enableActions());
    }

    public static final void Loggedin(final LittleEndianAccessor slea, final MapleClient c) {

        final int playerid = slea.readInt();

        if (c.getCloseSession()) {
            return;
        }
        c.loadNecessaryAccInfoByCharID(playerid);

        // 確認角色狀態
        if (checkCharacterState(c, playerid)) {
            return;
        }

        String macData = CharLoginHandler.readMacAddress(slea, c);

        MapleCharacter player = CashShopServer.getPlayerStorage() == null ? null : CashShopServer.getPlayerStorage().getPendingCharacter(playerid);
        if (player != null) {
            player.setClient(c);
            c.setTempIP(player.getOneTempValue("Transfer", "TempIP"));
            c.setAccountName(player.getOneTempValue("Transfer", "AccountName"));
            CashShopOperation.EnterCS(player, c);
            return;
        }
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            if (cserv == null || cserv.getPlayerStorage() == null) {
                continue;
            }
            player = cserv.getPlayerStorage().getPendingCharacter(playerid);
            if (player != null) {
                c.setChannel(cserv.getChannel());
                break;
            }
        }

        boolean transfer;
        if (player == null) { // Player isn't in storage, probably isn't CC
            Quadra<String, String, Integer, String> ip = LoginServer.getLoginAuth(playerid);
            String s = c.getSessionIPAddress();
            /*if (ip == null || (!s.substring(s.indexOf('/') + 1, s.length()).equals(ip.first) && (macData != null && !c.getClientMac().equals(macData)))) {
>>>>>>> bd6e59cc8fa9625578393991244eba834601d3f3
                if (ip != null) {
                    LoginServer.putLoginAuth(playerid, ip.first, ip.second, ip.third, ip.forth);
                }
                c.getSession().close();
                return;
            }*/
            c.setTempIP(ip.second);
            c.setChannel(ip.third);
            LoginServer.removeClient(c);
            player = MapleCharacter.loadCharFromDB(playerid, c, true);
            transfer = false;
        } else {
            player.ReconstructChr(player, c);
            transfer = true;
        }

        // 確認登入參數
        if (checkArgState(c, playerid, transfer)) {
            return;
        }

        player.updateOneTempValue("Transfer", "TempIP", null);
        player.updateOneTempValue("Transfer", "AccountName", null);
        player.updateOneTempValue("Transfer", "Channel", null);
        final ChannelServer channelServer = c.getChannelServer();
        c.setPlayer(player);
        c.setAccID(player.getAccountID());
        c.loadAccountData(c.getAccID());
        final int state = c.getLoginState();
        boolean allowLogin = false;

        // 確認雙燈
        if (checkDoubleLogin(c, playerid, state, transfer)) {
            return;
        }

//        if (World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()))) {
//            c.setPlayer(null);
//            c.getSession().close();
//            c.updateLoginState(MapleClient.LOGIN_NOTLOGGEDIN, c.getSessionIPAddress());
//            FileoutputUtil.logToFile("logs/data/DC.txt", "\r\n帳號[" + c.getAccountName() + "]伺服器主動斷開用戶端連接，調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
//            return;
//        }

        // 確認客戶端 IP
        if (!c.CheckIPAddress()) {
            c.getSession().close();
            return;
        }

        LoginServer.forceRemoveClient(c, false);
        ChannelServer.forceRemovePlayerByAccId(c, c.getAccID());

        // 儲存中的帳號不載入
        if (World.isPlayerSaving(c.getAccID())) {
            FileoutputUtil.logToFile("logs/data/儲存中載入.txt", FileoutputUtil.CurrentReadable_Time() + " 角色: " + player.getName() + "(" + player.getId() + ") 帳號: " + c.getAccountName() + "(" + player.getAccountID() + ") \r\n ");
            c.getSession().close();
            return;
        }

        c.updateLoginState(MapleClient.LOGIN_LOGGEDIN, c.getSessionIPAddress());
        channelServer.addPlayer(player);

        player.giveCoolDowns(PlayerBuffStorage.getCooldownsFromStorage(player.getId()));
        player.silentGiveBuffs(PlayerBuffStorage.getBuffsFromStorage(player.getId()));
        player.giveSilentDebuff(PlayerBuffStorage.getDiseaseFromStorage(player.getId()));

        c.sendPacket(CField.getCharInfo(player));
        c.sendPacket(MTSCSPacket.enableCSUse());

        // 伺服器管理員上線預設無敵
        if (player.isAdmin() && !player.isInvincible()) {
            player.dropMessage(6, "無敵已經開啟.");
            player.setInvincible(true);
        }
        //管理員上線預設隱藏
        if (player.isGM()) {
            SkillFactory.getSkill(9001004).getEffect(1).applyTo(player);
        }

        c.sendPacket(CWvsContext.temporaryStats_Reset()); // .
        player.getMap().addPlayer(player);

        try {
            // Start of buddylist
            final int buddyIds[] = player.getBuddylist().getBuddyIds();
            World.Buddy.loggedOn(player.getName(), player.getId(), c.getChannel(), buddyIds);
            if (player.getParty() != null) {
                final MapleParty party = player.getParty();
                World.Party.updateParty(party.getId(), PartyOperation.LOG_ONOFF, new MaplePartyCharacter(player));

                if (party != null && party.getExpeditionId() > 0) {
                    final MapleExpedition me = World.Party.getExped(party.getExpeditionId());
                    if (me != null) {
                        c.sendPacket(ExpeditionPacket.expeditionStatus(me, false));
                    }
                }
            }
            final CharacterIdChannelPair[] onlineBuddies = World.Find.multiBuddyFind(player.getId(), buddyIds);
            for (CharacterIdChannelPair onlineBuddy : onlineBuddies) {
                player.getBuddylist().get(onlineBuddy.getCharacterId()).setChannel(onlineBuddy.getChannel());
            }
            c.sendPacket(BuddylistPacket.updateBuddylist(player.getBuddylist().getBuddies()));

            // Start of Messenger
            final MapleMessenger messenger = player.getMessenger();
            if (messenger != null) {
                World.Messenger.silentJoinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()));
                World.Messenger.updateMessenger(messenger.getId(), c.getPlayer().getName(), c.getChannel());
            }

            // Start of Guild and alliance
            if (player.getGuildId() > 0) {
                World.Guild.setGuildMemberOnline(player.getMGC(), true, c.getChannel());
                c.sendPacket(GuildPacket.showGuildInfo(player));
                final MapleGuild gs = World.Guild.getGuild(player.getGuildId());
                if (gs != null) {
                    final List<byte[]> packetList = World.Alliance.getAllianceInfo(gs.getAllianceId(), true);
                    if (packetList != null) {
                        for (byte[] pack : packetList) {
                            if (pack != null) {
                                c.sendPacket(pack);
                            }
                        }
                    }
                } else { //guild not found, change guild id
                    player.setGuildId(0);
                    player.setGuildRank((byte) 5);
                    player.setAllianceRank((byte) 5);
                    player.saveGuildStatus();
                }
            }

            if (player.getFamilyId() > 0) {
                World.Family.setFamilyMemberOnline(player.getMFC(), true, c.getChannel());
            }
            c.sendPacket(FamilyPacket.getFamilyData());
            c.sendPacket(FamilyPacket.getFamilyInfo(player));
        } catch (Exception e) {
            FileoutputUtil.outputFileError(FileoutputUtil.Login_Error, e);
        }
        player.getClient().sendPacket(CWvsContext.serverMessage(channelServer.getServerMessage()));
        player.sendMacros();
        player.showNote();
        player.sendImp();
        player.updatePartyMemberHP();
        player.startFairySchedule(false);
        player.baseSkills(); //fix people who've lost skills.

        c.sendPacket(CField.getKeymap(player.getKeyLayout()));
        player.updatePetAuto();
        player.expirationTask(true, !transfer);
        if (player.getJob() == 132) { // DARKKNIGHT
            player.checkBerserk();
        }
        player.spawnClones();
        player.spawnSavedPets();
        if (player.getStat().equippedSummon > 0) {
            SkillFactory.getSkill(player.getStat().equippedSummon).getEffect(1).applyTo(player);
        }
        MapleQuestStatus stat = player.getQuestNoAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
        c.sendPacket(CWvsContext.pendantSlot(stat != null && stat.getCustomData() != null && Long.parseLong(stat.getCustomData()) > System.currentTimeMillis()));
        stat = player.getQuestNoAdd(MapleQuest.getInstance(GameConstants.QUICK_SLOT));
        c.sendPacket(CField.quickSlot(stat != null && stat.getCustomData() != null ? stat.getCustomData() : null));
        //c.sendPacket(CWvsContext.getFamiliarInfo(player));
        c.sendPacket(CField.ShowChronosphere(c.getPlayer().getChronosphere(), c.getPlayer().getCSChronosphere()));
        player.getInventory(MapleInventoryType.ETC).initBagItem();
        c.sendPacket(CWvsContext.InventoryPacket.getInventoryStatus());
        // 角色進入頻道顯示
        MapleCharacter ch = player;
        int Ch = c.getChannel();
        int Hellch = c.getChannelServer().HellChis();
        String pname = ch.getName();
        if (ServerConstants.EnableHellCh && Ch == Hellch) {
            String mg = "親愛的：" + pname + " 您好 \r\n本頻道為混沌頻道";
            ch.dropMessage(1, mg);
        }
    }

    public static final void ChangeChannel(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr, final boolean room) {
        if (c.getCloseSession()) {
            return;
        }
        if (chr == null || chr.hasBlockedInventory() || chr.getEventInstance() != null || chr.getMap() == null || chr.isInBlockedMap() || FieldLimitType.ChannelSwitch.check(chr.getMap().getFieldLimit())) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (c.getLoginState() != MapleClient.LOGIN_LOGGEDIN) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (World.getPendingCharacterSize() >= 100) {
            chr.dropMessage(1, "伺服器忙碌中，請稍候再試。");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (ServerConstants.DisableMapCC) {
            for (int i = 0; i < ServerConstants.MapNotCC.length; i++) {
                if (chr.getMapId() == ServerConstants.MapNotCC[i]) {
                    chr.dropMessage(5 , "該地圖無法切換頻道。");
                    c.sendPacket(CWvsContext.enableActions());
                    return;
                }
            }
        }
        final int chc = slea.readByte() + 1;
        int mapid = 0;
        if (room) {
            mapid = slea.readInt();
        }
        //chr.updateTick(slea.readInt());
        if (!World.isChannelAvailable(chc)) {
            chr.dropMessage(1, "此頻道人數已滿。");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (room && (mapid < 910000001 || mapid > 910000022)) {
            //chr.dropMessage(1, "The channel is full at the moment.");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (room) {
            if (chr.getMapId() == mapid) {
                if (c.getChannel() == chc) {
                    //chr.dropMessage(1, "You are already in " + chr.getMap().getMapName());
                    c.sendPacket(CWvsContext.enableActions());
                } else { // diff channel
                    chr.changeChannel(chc);
                }
            } else { // diff map
                if (c.getChannel() != chc) {
                    chr.changeChannel(chc);
                }
                final MapleMap warpz = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(mapid);
                if (warpz != null) {
                    chr.changeMap(warpz, warpz.getPortal("out00"));
                } else {
                    chr.dropMessage(1, "請稍候再試。");
                    c.sendPacket(CWvsContext.enableActions());
                }
            }
        } else {
            chr.changeChannel(chc);
        }
    }

    public static boolean checkCharacterState(MapleClient c, int playerid) {
        if (!MapleCharacterUtil.isExistCharacterInDataBase(playerid)) {
            FileoutputUtil.logToFile("logs/hack/角色複製.txt", FileoutputUtil.CurrentReadable_Time() + " <刪除角色複製Ch> 玩家id: " + playerid + " 帳號id:" + c.getAccID());
            c.getSession().close();
            return true;
        }
        return false;
    }

    public static boolean checkArgState(MapleClient c, int playerid, boolean transfer) {
        if (!transfer) {
            if (System.getProperty(String.valueOf(playerid)) == null || !System.getProperty(String.valueOf(playerid)).equals("1")) {
                c.getSession().close();
                return true;
            } else if (System.getProperty(c.getAccountName().toLowerCase()) == null || !System.getProperty(c.getAccountName().toLowerCase()).equals("1")) {
                c.getSession().close();
                return true;
            } else {
                System.setProperty(String.valueOf(playerid), String.valueOf(0));
                System.setProperty(String.valueOf(c.getAccountName().toLowerCase()), String.valueOf(0));
            }
        }
        return false;
    }

    public static boolean checkDoubleLogin(MapleClient c, int playerid, int state, boolean transfer) {
        if (state != MapleClient.LOGIN_SERVER_TRANSITION && !transfer) {
            c.setPlayer(null);
            c.getSession().close();
            return true;
        }

        if (state == MapleClient.LOGIN_SERVER_TRANSITION && transfer) {
            c.setPlayer(null);
            c.getSession().close();
            return true;
        }

        if (state != MapleClient.LOGIN_SERVER_TRANSITION && state != MapleClient.CHANGE_CHANNEL && state != MapleClient.CASH_SHOP_TRANSITION_LEAVE && state != MapleClient.MAPLE_TRADE_TRANSITION_LEAVE) {
            c.setPlayer(null);
            c.getSession().close();
            return true;
        }
        return false;
    }
}
