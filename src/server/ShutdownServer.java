package server;

import constants.ServerConstants;
import database.DatabaseConnection;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.World;
import server.Timer.*;
import tools.packet.CWvsContext;

public class ShutdownServer implements ShutdownServerMBean {

    private static final ShutdownServer instance = new ShutdownServer();
    public static boolean running = false;

    public static ShutdownServer getInstance() {
        return instance;
    }

    @Override
    public void shutdown() {//can execute twice
        run();
    }

    @Override
    public void run() {
        synchronized (this) {
            if (running) { //Run once!
                return;
            }
            running = true;
        }
        ServerConstants.isShutdown = true;
        int ret = 0;
        World.Broadcast.broadcastMessage(CWvsContext.serverNotice(0, "伺服器即將關機，請盡快安全下線."));
		
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            System.out.println("頻道 " + cs.getChannel() + " 已經設置關閉正在儲存精靈商人中...");
            ret += cs.closeAllMerchant();
        }
		
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            cs.setShutdown();
        }
		
        try {
            System.err.println("正在儲存公會/聯盟/家族...");
            World.Guild.save();
            World.Alliance.save();
            World.Family.save();
        } catch (Exception ex) {
            System.err.println("公會、聯盟、家族關閉失敗");
        }
		
        System.out.println("資料儲存完成，精靈商人儲存共 " + ret + " 個.");
        World.Broadcast.broadcastMessage(CWvsContext.serverNotice(0, "伺服器將進行停機維護, 請安全的下線, 以免造成不必要的損失。"));
        
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            cs.shutdown();
        }
		
        try {
            WorldTimer.getInstance().stop();
            MapTimer.getInstance().stop();
            BuffTimer.getInstance().stop();
            CloneTimer.getInstance().stop();
            autoEventTimer.getInstance().stop();
            EventTimer.getInstance().stop();
            EtcTimer.getInstance().stop();
            PingTimer.getInstance().stop();
            MobTimer.getInstance().stop();
            System.out.println("計時器關閉完成");
        } catch (Exception e) {
            System.err.println("計時器關閉失敗");
        }

        try {
            CashShopServer.shutdown();
            System.out.println("購物商城伺服器關閉完成.");
        } catch (Exception e) {
            System.out.println("購物商城伺服器關閉失敗.");
        }
        
        try {
            LoginServer.shutdown();
            System.out.println("登入伺服器關閉完成.");
        } catch (Exception e) {
            System.out.println("登入伺服器關閉失敗.");
        }
        
        /*try {
            DatabaseConnection.closeAll();
            System.out.println("資料庫連線清除完成.");
        } catch (Exception e) {
            System.out.println("資料庫連線清除失敗.");
        }*/
        
        System.out.println("關閉伺服器完成\r\n");
    }
}
