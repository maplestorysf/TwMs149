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
package handling.channel.handler;

import client.MapleClient;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.messages.CommandProcessor;
import constants.ServerConstants;
import constants.ServerConstants.CommandType;
import handling.channel.ChannelServer;
import handling.world.MapleMessenger;
import handling.world.MapleMessengerCharacter;
import handling.world.World;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import tools.FileoutputUtil;
import tools.packet.CField;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;

public class ChatHandler {

    public static final void GeneralChat(final String text, final byte unk, final MapleClient c, final MapleCharacter chr) {
        if (text.getBytes().length > 0 && chr != null && chr.getMap() != null && !CommandProcessor.processCommand(c, text, chr.getBattle() == null ? CommandType.NORMAL : CommandType.POKEMON)) {
            if (!chr.isIntern() && text.getBytes().length >= 80) {
                return;
            }
            if (chr.getCanTalk() || chr.isStaff()) {
                //Note: This patch is needed to prevent chat packet from being broadcast to people who might be packet sniffing.
                if (chr.isHidden()) {
                    if (chr.isIntern() && !chr.isGM() && unk == 0) {
                        chr.getMap().broadcastGMMessage(chr, CField.getChatText(chr.getId(), text, false, (byte) 1), true);
                        if (unk == 0) {
                            chr.getMap().broadcastGMMessage(chr, CWvsContext.serverNotice(2, chr.getName() + " : " + text), true);
                        }
                    } else {
                        chr.getMap().broadcastGMMessage(chr, CField.getChatText(chr.getId(), text, c.getPlayer().isGM(), unk), true);
                    }
                } else {
                    chr.getCheatTracker().checkMsg();
                    if (chr.isIntern() && !chr.isGM() && unk == 0) {
                        chr.getMap().broadcastMessage(CField.getChatText(chr.getId(), text, false, (byte) 1), c.getPlayer().getTruePosition());
                        if (unk == 0) {
                            chr.getMap().broadcastMessage(CWvsContext.serverNotice(2, chr.getName() + " : " + text), c.getPlayer().getTruePosition());
                        }
                    } else {
                        chr.getMap().broadcastMessage(CField.getChatText(chr.getId(), text, c.getPlayer().isGM(), unk), c.getPlayer().getTruePosition());
                    }
                }
                if (text.startsWith(c.getChannelServer().getServerName())) {
                    if (chr.getSayGood() < 10) {
                        chr.setSayGood(1);
                    } else if (chr.getSayGood() == 10) {
                        chr.finishAchievement(11);
                    }
                }
                if (chr.getGMLevel() == 0 && !chr.isHidden() || chr.isGod()) {
                    if (c.isMonitored()) {
                        World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, "[GM ??????] " + MapleCharacterUtil.makeMapleReadable(chr.getName())+ " ???????????????) " + text));
                    }
                    if (ServerConstants.log_chat) {
                        FileoutputUtil.logToFile("logs/??????/????????????.txt", "\r\n" + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " ???" + chr.getName() + "??? ?????????" + chr.getMapId() + "??????  " + text);
                    }
                    final StringBuilder sb = new StringBuilder("[GM ??????]???" + chr.getName() + "???(" + chr.getId() + ")?????????" + chr.getMapId() + "????????????  " + text);
                    try {
                        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                            for (MapleCharacter chr_ : cserv.getPlayerStorage().getAllCharactersThreadSafe()) {
                                if (chr_ == null) {
                                    break;
                                }
                                if (chr_.getmsg_Chat()) {
                                    chr_.dropMessage(sb.toString());
                                }
                            }
                        }
                    } catch (ConcurrentModificationException CME) {
                    }
                }
            } else {
                c.sendPacket(CWvsContext.serverNotice(6, "????????????????????????????????????"));
            }
        }
    }

    public static final void Others(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final int type = slea.readByte();
        final byte numRecipients = slea.readByte();
        if (numRecipients <= 0) {
            return;
        }
        int recipients[] = new int[numRecipients];

        for (byte i = 0; i < numRecipients; i++) {
            recipients[i] = slea.readInt();
        }
        final String chattext = slea.readMapleAsciiString();
        if (chr == null || !chr.getCanTalk()) {
            c.sendPacket(CWvsContext.serverNotice(6, "??????????????????????????????"));
            return;
        }

        if (c.isMonitored()) {
            String chattype = "??????";
            switch (type) {
                case 0:
                    chattype = "??????";
                    break;
                case 1:
                    chattype = "??????";
                    break;
                case 2:
                    chattype = "??????";
                    break;
                case 3:
                    chattype = "??????";
                    break;
                case 4:
                    chattype = "?????????";
                    break;
            }
            World.Broadcast.broadcastGMMessage(
                    CWvsContext.serverNotice(6, "[GM ??????] " + MapleCharacterUtil.makeMapleReadable(chr.getName())
                            + " ?????? (" + chattype + "): " + chattext));

        }
        if (chattext.getBytes().length <= 0 || CommandProcessor.processCommand(c, chattext, chr.getBattle() == null ? CommandType.NORMAL : CommandType.POKEMON)) {
            return;
        }
        chr.getCheatTracker().checkMsg();
        switch (type) {
            case 0:
                if (ServerConstants.log_chat) {
                    FileoutputUtil.logToFile("logs/??????/????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " ??????ID: " + Arrays.toString(recipients) + "??????: " + chr.getName() + " ?????? :" + chattext);
                }
                World.Buddy.buddyChat(recipients, chr.getId(), chr.getName(), chattext);
                break;
            case 1:
                if (chr.getParty() == null) {
                    break;
                }
                if (ServerConstants.log_chat) {
                    FileoutputUtil.logToFile("logs/??????/????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " ??????: " + chr.getParty().getId() + " ??????: " + chr.getName() + " ?????? :" + chattext);
                }
                World.Party.partyChat(chr.getParty().getId(), chattext, chr.getName());
                break;
            case 2:
                if (chr.getGuildId() <= 0) {
                    break;
                }
                if (ServerConstants.log_chat) {
                    FileoutputUtil.logToFile("logs/??????/????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " ??????: " + chr.getGuildId() + " ??????: " + chr.getName() + " ?????? :" + chattext);
                }
                World.Guild.guildChat(chr.getGuildId(), chr.getName(), chr.getId(), chattext);
                break;
            case 3:
                if (chr.getGuildId() <= 0) {
                    break;
                }
                if (ServerConstants.log_chat) {
                    FileoutputUtil.logToFile("logs/??????/????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " ??????: " + chr.getGuildId() + " ??????: " + chr.getName() + " ?????? :" + chattext);
                }
                World.Alliance.allianceChat(chr.getGuildId(), chr.getName(), chr.getId(), chattext);
                break;
            case 4:
                if (chr.getParty() == null || chr.getParty().getExpeditionId() <= 0) {
                    break;
                }
                if (ServerConstants.log_chat) {
                    FileoutputUtil.logToFile("logs/??????/???????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " ?????????: " + chr.getParty().getExpeditionId() + " ??????: " + chr.getName() + " ?????? :" + chattext);
                }
                World.Party.expedChat(chr.getParty().getExpeditionId(), chattext, chr.getName());
                break;
        }
    }

    public static final void Messenger(final LittleEndianAccessor slea, final MapleClient c) {
        String input;
        MapleMessenger messenger = c.getPlayer().getMessenger();

        switch (slea.readByte()) {
            case 0x00: // open
                if (messenger == null) {
                    int messengerid = slea.readInt();
                    if (messengerid == 0) { // create
                        c.getPlayer().setMessenger(World.Messenger.createMessenger(new MapleMessengerCharacter(c.getPlayer())));
                    } else { // join
                        messenger = World.Messenger.getMessenger(messengerid);
                        if (messenger != null) {
                            final int position = messenger.getLowestPosition();
                            if (position > -1 && position < 4) {
                                c.getPlayer().setMessenger(messenger);
                                World.Messenger.joinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()), c.getPlayer().getName(), c.getChannel());
                            }
                        }
                    }
                }
                break;
            case 0x02: // exit
                if (messenger != null) {
                    final MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(c.getPlayer());
                    World.Messenger.leaveMessenger(messenger.getId(), messengerplayer);
                    c.getPlayer().setMessenger(null);
                }
                break;
            case 0x03: // invite

                if (messenger != null) {
                    final int position = messenger.getLowestPosition();
                    if (position <= -1 || position >= 4) {
                        return;
                    }
                    input = slea.readMapleAsciiString();
                    final MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(input);

                    if (target != null) {
                        if (target.getMessenger() == null) {
                            if (!target.isIntern() || c.getPlayer().isIntern()) {
                                c.sendPacket(CField.messengerNote(input, 4, 1));
                                target.getClient().sendPacket(CField.messengerInvite(c.getPlayer().getName(), messenger.getId()));
                            } else {
                                c.sendPacket(CField.messengerNote(input, 4, 0));
                            }
                        } else {
                            c.sendPacket(CField.messengerChat(c.getPlayer().getName(), " : " + target.getName() + " is already using Maple Messenger."));
                        }
                    } else {
                        if (World.isConnected(input)) {
                            World.Messenger.messengerInvite(c.getPlayer().getName(), messenger.getId(), input, c.getChannel(), c.getPlayer().isIntern());
                        } else {
                            c.sendPacket(CField.messengerNote(input, 4, 0));
                        }
                    }
                }
                break;
            case 0x05: // decline
                final String targeted = slea.readMapleAsciiString();
                final MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(targeted);
                if (target != null) { // This channel
                    if (target.getMessenger() != null) {
                        target.getClient().sendPacket(CField.messengerNote(c.getPlayer().getName(), 5, 0));
                    }
                } else { // Other channel
                    if (!c.getPlayer().isIntern()) {
                        World.Messenger.declineChat(targeted, c.getPlayer().getName());
                    }
                }
                break;
            case 0x06: // message
                if (messenger != null) {
                    final String charname = slea.readMapleAsciiString();
                    final String text = slea.readMapleAsciiString();
                    final String chattext = charname + "" + text;
                    World.Messenger.messengerChat(messenger.getId(), charname, text, c.getPlayer().getName());
                    if (messenger.isMonitored() && text.getBytes().length > c.getPlayer().getName().length() + 3) { //name : NOT name0 or name1
                        World.Broadcast.broadcastGMMessage(
                                CWvsContext.serverNotice(
                                        6, "[GM Message] " + MapleCharacterUtil.makeMapleReadable(c.getPlayer().getName()) + "(Messenger: "
                                        + messenger.getMemberNamesDEBUG() + ") said: " + text));
                    }
                }
                break;
        }
    }

    public static final void Whisper_Find(final LittleEndianAccessor slea, final MapleClient c) {
        final byte mode = slea.readByte();
        slea.readInt(); //ticks
        switch (mode) {
            case 68: //buddy
            case 5: { // Find

                final String recipient = slea.readMapleAsciiString();
                MapleCharacter player = c.getChannelServer().getPlayerStorage().getCharacterByName(recipient);
                if (player != null) {
                    if (!player.isIntern() || c.getPlayer().isIntern() && player.isIntern()) {

                        c.sendPacket(CField.getFindReplyWithMap(player.getName(), player.getMap().getId(), mode == 68));
                    } else {
                        c.sendPacket(CField.getWhisperReply(recipient, (byte) 0));
                    }
                } else { // Not found
                    int ch = World.Find.findChannel(recipient);
                    if (ch > 0) {
                        player = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(recipient);
                        if (player == null) {
                            break;
                        }
                        if (player != null) {
                            if (!player.isIntern() || (c.getPlayer().isIntern() && player.isIntern())) {
                                c.sendPacket(CField.getFindReply(recipient, (byte) ch, mode == 68));
                            } else {
                                c.sendPacket(CField.getWhisperReply(recipient, (byte) 0));
                            }
                            return;
                        }
                    }
                    if (ch == -10) {
                        c.sendPacket(CField.getFindReplyWithCS(recipient, mode == 68));
                    } else if (ch == -20) {
                        c.getPlayer().dropMessage(5, "'" + recipient + "' is at the MTS."); //idfc
                    } else {
                        c.sendPacket(CField.getWhisperReply(recipient, (byte) 0));
                    }
                }
                break;
            }
            case 6: { // Whisper
                if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
                    return;
                }
                if (!c.getPlayer().getCanTalk()) {
                    c.sendPacket(CWvsContext.serverNotice(6, "You have been muted and are therefore unable to talk."));
                    return;
                }
                c.getPlayer().getCheatTracker().checkMsg();
                final String recipient = slea.readMapleAsciiString();
                final String text = slea.readMapleAsciiString();
                final int ch = World.Find.findChannel(recipient);
                if (ch > 0) {
                    MapleCharacter player = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(recipient);
                    if (player == null) {
                        break;
                    }
                    player.getClient().sendPacket(CField.getWhisper(c.getPlayer().getName(), c.getChannel(), text));
                    if (!c.getPlayer().isIntern() && player.isIntern()) {
                        c.sendPacket(CField.getWhisperReply(recipient, (byte) 0));
                    } else {
                        c.sendPacket(CField.getWhisperReply(recipient, (byte) 1));
                    }
                    if (c.isMonitored()) {
                        World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, c.getPlayer().getName() + " whispered " + recipient + " : " + text));
                    } else if (player.getClient().isMonitored()) {
                        World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, c.getPlayer().getName() + " whispered " + recipient + " : " + text));
                    }
                } else {
                    c.sendPacket(CField.getWhisperReply(recipient, (byte) 0));
                }
            }
            break;
        }
    }
}
