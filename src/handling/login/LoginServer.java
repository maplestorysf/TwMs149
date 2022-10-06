/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package handling.login;

import client.MapleClient;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import handling.ServerType;
import java.util.Collection;
import java.util.HashSet;
import server.ServerProperties;
import server.netty.ServerConnection;
import tools.Quadra;

public class LoginServer {

    public static int PORT = 8484;
    private static InetSocketAddress InetSocketadd;
    //  private static IoAcceptor acceptor;
    private static Map<Integer, Integer> load = new HashMap<Integer, Integer>();
    private static String serverName, eventMessage;
    private static byte flag;
    private static int maxCharacters, userLimit, usersOn = 0;
    public static boolean finishedShutdown = true, adminOnly = false;
    private static final HashMap<Integer, Quadra<String, String, Integer, String>> loginAuth = new HashMap<>();
    private static HashSet<String> loginIPAuth = new HashSet<String>();
    private static AccountStorage clients;
    private static ServerConnection init;

    public static void putLoginAuth(int chrid, String ip, String tempIp, int channel, String mac) {
        loginAuth.put(chrid, new Quadra<>(ip, tempIp, channel, mac));
        loginIPAuth.add(ip);

    }

    public static Quadra<String, String, Integer, String> getLoginAuth(int chrid) {
        return loginAuth.remove(chrid);
    }

    public static boolean containsIPAuth(String ip) {
        return loginIPAuth.contains(ip);
    }

    public static void removeIPAuth(String ip) {
        loginIPAuth.remove(ip);
    }

    public static void addIPAuth(String ip) {
        loginIPAuth.add(ip);
    }

    public static final void addChannel(final int channel) {
        load.put(channel, 0);
    }

    public static final void removeChannel(final int channel) {
        load.remove(channel);
    }

    public static final void run_startup_configurations() {
        userLimit = Integer.parseInt(ServerProperties.getProperty("net.sf.odinms.login.userlimit", "100"));
        serverName = ServerProperties.getProperty("net.sf.odinms.login.serverName", "");
        eventMessage = ServerProperties.getProperty("net.sf.odinms.login.eventMessage", "");
        flag = Byte.parseByte(ServerProperties.getProperty("net.sf.odinms.login.flag", "0"));
        maxCharacters = Integer.parseInt(ServerProperties.getProperty("net.sf.odinms.login.maxCharacters", ""));
        PORT = Integer.parseInt(ServerProperties.getProperty("net.sf.odinms.login.port", "8484"));

//        ByteBuffer.setUseDirectBuffers(false);
//        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());
//
//        acceptor = new SocketAcceptor();
//        final SocketAcceptorConfig cfg = new SocketAcceptorConfig();
//        cfg.getSessionConfig().setTcpNoDelay(true);
//        cfg.setDisconnectOnUnbind(true);
//        cfg.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MapleCodecFactory()));
        String ip = ServerProperties.getProperty("net.sf.odinms.channel.net.interface", "127.0.0.1");
        init = new ServerConnection(ip, PORT, -1, -1, ServerType.登入伺服器);
        init.run();
        // System.out.println("綁定端口 " + PORT + "成功.");
    }

    public static final void shutdown() {
        if (finishedShutdown) {
            return;
        }
        System.out.println("Shutting down login...");
        init.close();
        finishedShutdown = true; //nothing. lol
    }

    public static final String getServerName() {
        return serverName;
    }

    public static final String getEventMessage() {
        return eventMessage;
    }

    public static final byte getFlag() {
        return flag;
    }

    public static final int getMaxCharacters() {
        return maxCharacters;
    }

    public static final Map<Integer, Integer> getLoad() {
        return load;
    }

    public static void setLoad(final Map<Integer, Integer> load_, final int usersOn_) {
        load = load_;
        usersOn = usersOn_;
    }

    public static final void setEventMessage(final String newMessage) {
        eventMessage = newMessage;
    }

    public static final void setFlag(final byte newflag) {
        flag = newflag;
    }

    public static final int getUserLimit() {
        return userLimit;
    }

    public static final int getUsersOn() {
        return usersOn;
    }

    public static final void setUserLimit(final int newLimit) {
        userLimit = newLimit;
    }

    public static final boolean isShutdown() {
        return finishedShutdown;
    }

    public static final void setOn() {
        finishedShutdown = false;
    }

    public static void forceRemoveClient(MapleClient client) {
        forceRemoveClient(client, true);
    }

    public static void forceRemoveClient(MapleClient client, boolean remove) {
        Collection<MapleClient> cls = getClientStorage().getAllClientsThreadSafe();
        for (MapleClient c : cls) {
            if (c == null) {
                continue;
            }
            if (c.getAccID() == client.getAccID() || c == client) {
                if (c != client) {
                    c.unLockDisconnect();
                }
                if (remove) {
                    removeClient(c);
                }
            }
        }
    }

    public static final void removeClient(final MapleClient c) {
        getClientStorage().deregisterAccount(c);
    }

    public static AccountStorage getClientStorage() {
        if (clients == null) {
            clients = new AccountStorage();
        }
        return clients;
    }

    public static boolean containClient(MapleClient client) {
        Collection<MapleClient> cls = getClientStorage().getAllClientsThreadSafe();
        for (MapleClient c : cls) {
            if (c == null) {
                continue;
            }
            if (client == c) {
                return true;
            }
        }
        return false;
    }
}
