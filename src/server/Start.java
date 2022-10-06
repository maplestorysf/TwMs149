package server;

import client.SkillFactory;
import client.inventory.MapleEquipIdOnly;
import client.inventory.MapleInventoryIdentifier;
import constants.ServerConstants;
import handling.MapleServerHandler;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import handling.login.LoginServer;
import handling.cashshop.CashShopServer;
import handling.login.LoginInformationProvider;
import handling.world.World;
import java.sql.SQLException;
import database.DatabaseConnection;
import handling.world.SkillCollector;
import handling.world.family.MapleFamily;
import handling.world.guild.MapleGuild;
import java.sql.PreparedStatement;
import server.Timer.*;
import server.events.MapleOxQuizFactory;
import server.life.MapleLifeFactory;
import server.life.MapleMonsterInformationProvider;
import server.life.MobSkillFactory;
import server.life.PlayerNPC;
import server.quest.MapleQuest;
import java.util.concurrent.atomic.AtomicInteger;

public class Start {

    public static long startTime = System.currentTimeMillis();
    public static final Start instance = new Start();
    public static AtomicInteger CompletedLoadingThreads = new AtomicInteger(0);
    private static Thread CopyServer = null;

    public void run() throws InterruptedException {
        if (System.getProperty("net.sf.odinms.wzpath") == null) {
            System.setProperty("net.sf.odinms.wzpath", "wz");
        }
        if (ServerConstants.isAdminOnly) {
            System.out.println("【管理員模式】開啟");
        } else {
            System.out.println("【管理員模式】關閉");
        }

        try {
            final PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE accounts SET loggedin = 0");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new RuntimeException("[警告] 請確認SQL伺服器連接.");
        }

        try {
            final PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE characters SET map = 910000000");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new RuntimeException("[警告] 請確認SQL伺服器連接.");
        }

        if (ServerConstants.AUTO_REGISTER) {
            System.out.println("【自動註冊】開啟");
        } else {
            System.out.println("【自動註冊】關閉");
        }
        if (ServerConstants.Can_GMItems) {
            System.out.println("【允許玩家使用管理員物品】開啟");
        } else {
            System.out.println("【允許玩家使用管理員物品】關閉");
        }

        System.out.println("伺服器名稱: " + ServerProperties.getProperty("net.sf.odinms.login.serverName"));
        System.out.println("伺服器版本: " + ServerConstants.MAPLE_VERSION + "." + ServerConstants.MAPLE_PATCH);
        World.init();
        WorldTimer.getInstance().start();
        EtcTimer.getInstance().start();
        MapTimer.getInstance().start();
        MobTimer.getInstance().start();
        CloneTimer.getInstance().start();
        autoEventTimer.getInstance().start();
        EventTimer.getInstance().start();
        BuffTimer.getInstance().start();
        PingTimer.getInstance().start();

        System.out.println("讀取Timers系統完成...");
        MapleGuildRanking.getInstance().load();
        MapleGuild.loadAll(); //(this); 
        System.out.println("讀取公會相關完成...");
        MapleFamily.loadAll(); //(this); 
        System.out.println("讀取家族相關完成...");
        MapleLifeFactory.loadQuestCounts();
        MapleQuest.initQuests();
        System.out.println("讀取任務相關完成...");
        FishingRewardFactory.getInstance();
        System.out.println("讀取釣魚系統完成...");
        MapleItemInformationProvider.getInstance().runEtc();
        MapleMonsterInformationProvider.getInstance().load();
        //BattleConstants.init(); 
        MapleItemInformationProvider.getInstance().runItems();
        System.out.println("讀取物品相關完成...");
        SkillFactory.load();
        System.out.println("讀取技能相關完成..");
        LoginInformationProvider.getInstance();
        RandomRewards.load();
        MapleOxQuizFactory.getInstance();
        MapleCarnivalFactory.getInstance();
        CharacterCardFactory.getInstance().initialize();
        SkillCollector.getInstance().init();
        MobSkillFactory.getInstance();
        SpeedRunner.loadSpeedRuns();
        MTSStorage.load();
        System.out.println("讀取其他相關完成...");
        MapleInventoryIdentifier.getInstance();
        CashItemFactory.getInstance().initialize(false);

        MapleServerHandler.initiate();

        ChannelServer.startChannel_Main();
        System.out.println("[讀取頻道伺服器完成]");

        CashShopServer.run_startup_configurations();
        System.out.println("[讀取購物商城伺服器完成]");

        CheatTimer.getInstance().register(AutobanManager.getInstance(), 60000);
        Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
        World.registerRespawn();

        // 自由1頻隨機丟道具
        //ChannelServer.getInstance(1).getMapFactory().getMap(910000000).spawnRandDrop();
        //ServerConstants.registerMBean();
        PlayerNPC.loadAll();// touch - so we see database problems early...
        MapleMonsterInformationProvider.getInstance().addExtra();
        LoginServer.setOn(); //now or later
        if (ServerConstants.EnablePeeSystem) {
            World.尿尿活動();
        }

        if (ServerConstants.EnableHellCh) {
            System.out.print("[渾沌頻道系統] 成功註冊頻道：");
            for (int i : ServerConstants.HellCh) {
                System.out.print(i+"\t");
            }
            System.out.print("\r\n");
        }


        if (ServerConstants.isShutdown) {
            System.out.println("【禁止玩家使用:啟動 如果要開放請GM上線打:!禁止玩家使用】");
        }

        if (ServerConstants.Disable_Shop) {
            System.out.println("【禁止使用個人商店、精靈商人:啟動 如果要開放請GM上線打:!禁止使用商店】");
        }

//        RankingWorker.run();
        /* 唯一道具 */
        CopyServer = new Thread() {
            @Override
            public void run() {
                Timer.EventTimer.getInstance().schedule(MapleEquipIdOnly.getInstance(), 30 * 1000);
            }
        };
        CopyServer.start();

        LoginServer.run_startup_configurations();
        System.out.println("[登入伺服器啟動]");
        System.out.println("[已完成開服花費 " + ((System.currentTimeMillis() - startTime) / 1000) + " 秒]");
    }

    public static class Shutdown implements Runnable {

        @Override
        public void run() {
            ShutdownServer.getInstance().run();
            ShutdownServer.getInstance().run();
        }
    }

    public static void main(final String args[]) throws InterruptedException {
        instance.run();
    }
}
