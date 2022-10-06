package server.events;

import client.MapleCharacter;
import constants.GameConstants;

import handling.channel.ChannelServer;
import handling.world.World;
import server.MapleInventoryManipulator;
import server.RandomRewards;
import server.Randomizer;
import server.Timer.*;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import server.maps.SavedLocationType;
import tools.FileoutputUtil;
import tools.packet.CField;
import tools.StringUtil;
import tools.packet.CWvsContext;

public abstract class MapleEvent {

    protected MapleEventType type;
    protected int channel, playerCount = 0;
    protected boolean isRunning = false;

    public MapleEvent(final int channel, final MapleEventType type) {
        this.channel = channel;
        this.type = type;
    }

    public void incrementPlayerCount() {
        playerCount++;
        if (playerCount == 250) {
            setEvent(ChannelServer.getInstance(channel), true);
        }
    }

    public MapleEventType getType() {
        return type;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public MapleMap getMap(final int i) {
        return getChannelServer().getMapFactory().getMap(type.mapids[i]);
    }

    public ChannelServer getChannelServer() {
        return ChannelServer.getInstance(channel);
    }

    public void broadcast(final byte[] packet) {
        for (int i = 0; i < type.mapids.length; i++) {
            getMap(i).broadcastMessage(packet);
        }
    }

    public static void givePrize(final MapleCharacter chr) {
        final int reward = RandomRewards.getEventReward();
        switch (reward) {
            case 0:
                final int mes = Randomizer.nextInt(1000000) + 100000;
                chr.gainMeso(mes, true, false);
                chr.dropMessage(5, "參加活動獲得 " + mes + " 楓幣獎勵。");
                break;
            case 1:
                final int acash = Randomizer.nextInt(30) + 10;
                chr.modifyCSPoints(0, acash, true);
                chr.dropMessage(5, "參加活動獲得 " + acash + " 樂豆點數獎勵。");
                break;
            case 2:
                final int maplePoint = Randomizer.nextInt(30) + 10;
                chr.modifyCSPoints(1, maplePoint, true);
                chr.dropMessage(5, "參加活動獲得 " + maplePoint + " 楓葉點數獎勵。");
                break;
            case 3:
                chr.addFame(3);
                chr.dropMessage(5, "參加活動獲得 3 名聲獎勵。");
                break;
            case 4:
                chr.dropMessage(5, "參加活動獲得銘謝惠顧獎勵。");
                break;
            default:
                int max_quantity = 1;
                switch (reward) {
                    case 5062000:
                        max_quantity = 3;
                        break;
                    case 5220040:
                        max_quantity = 20;
                        break;
                    case 4031307:
                    case 5050000:
                        max_quantity = 5;
                        break;
                }
                final int quantity = (max_quantity > 1 ? Randomizer.nextInt(max_quantity) : 0) + 1;
                if (MapleInventoryManipulator.checkSpace(chr.getClient(), reward, quantity, "")) {
                    MapleInventoryManipulator.addById(chr.getClient(), reward, (short) quantity, "Event prize on " + FileoutputUtil.CurrentReadable_Date());
                } else {
                    givePrize(chr); //do again until they get
                }
                //5062000 = 1-3
                //5220000 = 1-25
                //5050000 = 1-5
                //4031307 = 1-5
                break;
        }
    }

    public abstract void finished(MapleCharacter chr); //most dont do shit here

    public abstract void startEvent();

    public void onMapLoad(MapleCharacter chr) { //most dont do shit here
        if (GameConstants.isEventMap(chr.getMapId()) && FieldLimitType.Event.check(chr.getMap().getFieldLimit()) && FieldLimitType.Event2.check(chr.getMap().getFieldLimit())) {
            chr.getClient().sendPacket(CField.showEventInstructions());
        }
    }

    public void warpBack(MapleCharacter chr) {
        int map = chr.getSavedLocation(SavedLocationType.EVENT);
        if (map <= -1) {
            map = 104000000;
        }
        final MapleMap mapp = chr.getClient().getChannelServer().getMapFactory().getMap(map);
        chr.changeMap(mapp, mapp.getPortal(0));
    }

    public void reset() {
        isRunning = true;
        playerCount = 0;
    }

    public void unreset() {
        isRunning = false;
        playerCount = 0;
    }

    public static final void setEvent(final ChannelServer cserv, final boolean auto) {
        if (auto && cserv.getEvent() > -1) {
            for (MapleEventType t : MapleEventType.values()) {
                final MapleEvent e = cserv.getEvent(t);
                if (e.isRunning) {
                    for (int i : e.type.mapids) {
                        if (cserv.getEvent() == i) {
                            World.Broadcast.broadcastMessage(CWvsContext.serverNotice(0, "活動入口已關閉！"));
                            e.broadcast(CWvsContext.serverNotice(0, "30秒後活動開始！"));
                            e.broadcast(CField.getClock(30));
                            autoEventTimer.getInstance().schedule(new Runnable() {

                                @Override
                                public void run() {
                                    e.startEvent();
                                }
                            }, 30000);
                            break;
                        }
                    }
                }
            }
        }
        cserv.setEvent(-1);
    }

    public static final void mapLoad(final MapleCharacter chr, final int channel) {
        if (chr == null) {
            return;
        } //o_o
        for (MapleEventType t : MapleEventType.values()) {
            final MapleEvent e = ChannelServer.getInstance(channel).getEvent(t);
            if (e.isRunning) {
                if (chr.getMapId() == 109050000) { //finished map
                    e.finished(chr);
                }
                for (int i = 0; i < e.type.mapids.length; i++) {
                    if (chr.getMapId() == e.type.mapids[i]) {
                        e.onMapLoad(chr);
                        if (i == 0) { //first map
                            e.incrementPlayerCount();
                        }
                    }
                }
            }
        }
    }

    public static final void onStartEvent(final MapleCharacter chr) {
        for (MapleEventType t : MapleEventType.values()) {
            final MapleEvent e = chr.getClient().getChannelServer().getEvent(t);
            if (e.isRunning) {
                for (int i : e.type.mapids) {
                    if (chr.getMapId() == i) {
                        e.startEvent();
                        setEvent(chr.getClient().getChannelServer(), false);
                        chr.dropMessage(5, String.valueOf(t) + " has been started.");
                    }
                }
            }
        }
    }

    public static final String scheduleEvent(final MapleEventType event, final ChannelServer cserv) {
        if (cserv.getEvent() != -1 || cserv.getEvent(event) == null) {
            return "The event must not have been already scheduled.";
        }
        for (int i : cserv.getEvent(event).type.mapids) {
            if (cserv.getMapFactory().getMap(i).getCharactersSize() > 0) {
                return "活動正在進行中。";
            }
        }
        cserv.setEvent(cserv.getEvent(event).type.mapids[0]);
        cserv.getEvent(event).reset();
        World.Broadcast.broadcastMessage(CWvsContext.serverNotice(0, StringUtil.makeEnumHumanReadable("第 " + cserv.getChannel() + " 頻道即將舉辦 " + event.name()) + " 活動，變更到第 " + cserv.getChannel() + " 頻道使用 @活動 指令 入場吧！"));
        return "";
    }
}
