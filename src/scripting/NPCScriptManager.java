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
package scripting;

import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;

import client.MapleClient;
import constants.GameConstants;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import javax.script.ScriptException;

import server.life.MapleLifeFactory;
import server.life.MapleNPC;
import server.quest.MapleQuest;
import tools.FileoutputUtil;

public class NPCScriptManager extends AbstractScriptManager {

    private final Map<MapleClient, NPCConversationManager> cms = new WeakHashMap<>();
    private static final NPCScriptManager instance = new NPCScriptManager();

    public static final NPCScriptManager getInstance() {
        return instance;
    }

    public final void start(final MapleClient c, final int npc) {
        start(c, npc, null);
    }

    public final void start(final MapleClient c, final int npc, final int mode) {
        start(c, npc, mode, null);
    }

    public final void start(final MapleClient c, final int npc, String script) {

        start(c, npc, 0, script);
    }

    public final void start(final MapleClient c, final int npc, final int mode, String script) {
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            MapleNPC CheckNpc = MapleLifeFactory.getNPC(npc);
            if (CheckNpc == null || CheckNpc.getName().equalsIgnoreCase("MISSINGNO")) {
                if (c.getPlayer().isGM()) {
                    c.getPlayer().dropMessage("NPC " + npc + " 不存在");
                }
                dispose(c);
                return;
            } else if (GameConstants.isBlockedNpc(npc)) {
                c.getPlayer().dropMessage(1, "本NPC目前維護中...");
                dispose(c);
                return;
            }
            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage(-1, "[系統提示]您已經建立與NPC:" + npc + (script == null ? "" : ("(" + script + ")")) + (mode == 0 ? "" : "型號: " + mode) + "的對話。");
            }
            if (!cms.containsKey(c) && c.canClickNPC()) {
                Invocable iv;
                if (script == null) {
                    if (mode != 0) {
                        iv = getInvocable("npc/" + npc + "_" + mode + ".js", c, true); //safe disposal
                    } else {
                        iv = getInvocable("npc/" + npc + ".js", c, true); //safe disposal
                    }
                } else {
                    iv = getInvocable("special/" + script + ".js", c, true); //safe disposal
                }
                if (iv == null) {
                    iv = getInvocable("special/notcoded.js", c, true); //safe disposal
                    if (iv == null) {
                        dispose(c);
                        return;
                    }
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, iv, script, npc, mode, -1, (byte) -1);
                cms.put(c, cm);
                scriptengine.put("cm", cm);

                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                //System.out.println("NPCID started: " + npc);
                try {
                    iv.invokeFunction("start"); // Temporary until I've removed all of start
                } catch (NoSuchMethodException nsme) {
                    iv.invokeFunction("action", (byte) 1, (byte) 0, 0);
                }
            }
        } catch (final Exception e) {
            System.err.println("Error executing NPC script, NPC ID : " + npc + "." + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing NPC script, NPC ID : " + npc + "." + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void action(final MapleClient c, final byte mode, final byte type, final int selection) {
        if (mode != -1) {
            final NPCConversationManager cm = cms.get(c);
            if (cm == null || cm.getLastMsg() > -1) {
                return;
            }
            final Lock lock = c.getNPCLock();
            lock.lock();
            try {

                if (cm.pendingDisposal) {
                    dispose(c);
                } else {
                    c.setClickedNPC();
                    cm.getIv().invokeFunction("action", mode, type, selection);
                }
            } catch (final Exception e) {
                if (c.getPlayer() != null) {
                    if (c.getPlayer().isGM()) {
                        c.getPlayer().dropMessage("[系統提示] NPC " + cm.getNpc() + "腳本錯誤 " + e + "");
                    }
                }
                System.err.println("Error executing NPC script. NPC ID : " + cm.getNpc() + ":" + e);
                dispose(c);
                FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing NPC script, NPC ID : " + cm.getNpc() + "." + e);
            } finally {
                lock.unlock();
            }
        }
    }

    public final void startQuest(final MapleClient c, final int npc, final int quest) {
        if (!MapleQuest.getInstance(quest).canStart(c.getPlayer(), null) && !GameConstants.accscriptquest(quest)) {
            return;
        }
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!cms.containsKey(c) && c.canClickNPC()) {
                final Invocable iv = getInvocable("quest/" + quest + ".js", c, true);
                if (iv == null) {
                    c.getPlayer().dropMessage(1, "此任務尚未建置，請通知管理員。\r\n任務編號: " + quest);
                    dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, iv, null, npc, -1, quest, (byte) 0);
                cms.put(c, cm);
                scriptengine.put("qm", cm);

                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                if (c.getPlayer().isGM()) {
                    c.getPlayer().dropMessage("[系統提示]您已經建立與任務腳本:" + quest + "的往來。");
                }
                //System.out.println("NPCID started: " + npc + " startquest " + quest);
                iv.invokeFunction("start", (byte) 1, (byte) 0, 0); // start it off as something
            }
        } catch (final Exception e) {
            System.err.println("Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void startQuest(final MapleClient c, final byte mode, final byte type, final int selection) {
        final Lock lock = c.getNPCLock();
        final NPCConversationManager cm = cms.get(c);
        if (cm == null || cm.getLastMsg() > -1) {
            return;
        }
        if (cm == null) {
            c.getPlayer().dropMessage(1, "此任務尚未建置，請通知管理員。\r\n任務編號: " + cm.getQuest());
            dispose(c);
            return;
        }
        lock.lock();
        try {
            if (cm.pendingDisposal) {
                dispose(c);
            } else {
                c.setClickedNPC();
                if (c.getPlayer().isGM()) {
                    c.getPlayer().dropMessage("[系統提示]您已經建立與任務腳本:" + cm.getQuest() + "的往來。");
                }
                cm.getIv().invokeFunction("start", mode, type, selection);
            }
        } catch (Exception e) {
            System.err.println("Error executing Quest script. (" + cm.getQuest() + ")...NPC: " + cm.getNpc() + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing Quest script. (" + cm.getQuest() + ")..NPCID: " + cm.getNpc() + ":" + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void endQuest(final MapleClient c, int npc, final int quest, final boolean customEnd) {
        if (!customEnd && !MapleQuest.getInstance(quest).canComplete(c.getPlayer(), null)) {
            return;
        }
        if (quest == 52403) {
            npc = 9330203;
        }
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!cms.containsKey(c) && c.canClickNPC()) {
                final Invocable iv = getInvocable("quest/" + quest + ".js", c, true);
                if (iv == null) {
                    dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, iv, null, npc, -1, quest, (byte) 1);
                cms.put(c, cm);
                scriptengine.put("qm", cm);

                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                if (c.getPlayer().isGM()) {
                    c.getPlayer().dropMessage("[系統提示]您已經建立與任務End腳本:" + cm.getQuest() + "的往來。");
                }
                //System.out.println("NPCID started: " + npc + " endquest " + quest);
                iv.invokeFunction("end", (byte) 1, (byte) 0, 0); // start it off as something
            }
        } catch (Exception e) {
            System.err.println("Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void endQuest(final MapleClient c, final byte mode, final byte type, final int selection) {
        final Lock lock = c.getNPCLock();
        final NPCConversationManager cm = cms.get(c);
        if (cm == null || cm.getLastMsg() > -1) {
            return;
        }
        lock.lock();
        try {
            if (cm.pendingDisposal) {
                dispose(c);
            } else {
                c.setClickedNPC();
                if (c.getPlayer().isGM()) {
                    c.getPlayer().dropMessage("[系統提示]您已經建立與任務End腳本:" + cm.getQuest() + "的往來。");
                }
                cm.getIv().invokeFunction("end", mode, type, selection);
            }
        } catch (Exception e) {
            System.err.println("Error executing Quest script. (" + cm.getQuest() + ")...NPC: " + cm.getNpc() + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing Quest script. (" + cm.getQuest() + ")..NPCID: " + cm.getNpc() + ":" + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void onUserEnter(final MapleClient c, final String script) {
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (c.getPlayer().isShowInfo()) {
                c.getPlayer().showInfo("onUserEnter腳本", false, "開始onUserEnter腳本：" + script);
            }
            if (!cms.containsKey(c)) {
                Invocable iv = getInvocable("map/onUserEnter/" + script + ".js", c, true);
                if (iv == null) {
                    if (c.getPlayer().isGM()) {
                        c.getPlayer().showInfo("onUserEnter腳本", true, "找不到onUserEnter腳本:" + script);
                    }
                    //System.out.println("\r\n找不到onUserEnter腳本:" + script + "\r\n");
                    iv = getInvocable("map/onUserEnter/notcoded.js", c, true); // safe disposal
                    if (iv == null) {
                        dispose(c);
                        return;
                    }
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, iv, script, 0, -1, -1, (byte) 2);
                cms.put(c, cm);
                scriptengine.put("ms", cm);
                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                try {
                    iv.invokeFunction("start");
                } catch (NoSuchMethodException nsme) {
                    iv.invokeFunction("action", (byte) 1, (byte) 0, 0);
                }
            } else if (c.getPlayer().isGM()) {
                c.getPlayer().showInfo("onUserEnter腳本", true, "無法執行腳本:已有腳本執行-" + cms.containsKey(c));
            }
        } catch (final ScriptException | NoSuchMethodException e) {
            if (c.getPlayer().getDebugMessage()) {
                c.getPlayer().dropMessage(6, "執行onUserEnter腳本出錯 : " + script + ". " + e);
            }
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "執行onUserEnter腳本出錯 : " + script + ". " + e);
            dispose(c);
//            notice(c, 0, script, ScriptType.ON_USER_ENTER);
        } finally {
            lock.unlock();
        }
    }

    public final void onFirstUserEnter(final MapleClient c, final String script) {
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (c.getPlayer().isShowInfo()) {
                c.getPlayer().showInfo("onFirstUserEnter腳本", false, "開始onFirstUserEnter腳本：" + script + c.getPlayer().getMap());
            }
            if (!cms.containsKey(c)) {
                Invocable iv = getInvocable("map/onFirstUserEnter/" + script + ".js", c, true);
                if (iv == null) {
                    if (c.getPlayer().getDebugMessage()) {
                        c.getPlayer().showInfo("onFirstUserEnter腳本", true, "找不到onFirstUserEnter腳本:" + script + c.getPlayer().getMap());
                    }
                    System.out.println("\r\n找不到onFirstUserEnter腳本:" + script + c.getPlayer().getMap() + "\r\n");
                    iv = getInvocable("map/onFirstUserEnter/notcoded.js", c, true); // safe disposal
                    if (iv == null) {
                        dispose(c);
                        return;
                    }
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, iv, script, 0, -1, -1, (byte) 3);
                cms.put(c, cm);
                scriptengine.put("ms", cm);
                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                try {
                    iv.invokeFunction("start");
                } catch (NoSuchMethodException nsme) {
                    iv.invokeFunction("action", (byte) 1, (byte) 0, 0);
                }
            } else if (c.getPlayer().getDebugMessage()) {
                c.getPlayer().showInfo("onFirstUserEnter腳本", true, "無法執行腳本:已有腳本執行-" + cms.containsKey(c));
            }
        } catch (final ScriptException | NoSuchMethodException e) {
            if (c.getPlayer().getDebugMessage()) {
                c.getPlayer().dropMessage(6, "執行地圖onFirstUserEnter腳本出錯 : " + script + ". " + e);
            }
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "執行地圖onFirstUserEnter腳本出錯 : " + script + ". " + e);
            dispose(c);
//            notice(c, 0, script, ScriptType.ON_FIRST_USER_ENTER);
        } finally {
            lock.unlock();
        }
    }

    public final void dispose(final MapleClient c) {
        final NPCConversationManager npccm = cms.get(c);
        if (npccm != null) {
            cms.remove(c);
            switch (npccm.getType()) {
                case -1:
                    c.removeScriptEngine("scripts/npc/" + npccm.getNpc() + ".js");
                    if (npccm.getMode() != 0) {
                        c.removeScriptEngine("scripts/npc/" + npccm.getNpc() + "_" + npccm.getMode() + ".js");
                    }
                    c.removeScriptEngine("scripts/special/" + npccm.getScript() + ".js");
                    c.removeScriptEngine("scripts/special/notcoded.js");
                    break;
                case 0:
                case 1:
                    c.removeScriptEngine("scripts/quest/" + npccm.getQuest() + ".js");
                    break;
                case 2:
                    c.removeScriptEngine("scripts/map/onUserEnter/" + npccm.getScript() + ".js");
                    c.removeScriptEngine("scripts/map/onUserEnter/notcoded.js");
                    break;
                case 3:
                    c.removeScriptEngine("scripts/map/onFirstUserEnter/" + npccm.getScript() + ".js");
                    c.removeScriptEngine("scripts/map/onFirstUserEnter/notcoded.js");
                    break;
                default:
                    break;
            }
        }
        if (c.getPlayer() != null && c.getPlayer().getConversation() == 1) {
            c.getPlayer().setConversation(0);
        }
    }

    public final NPCConversationManager getCM(final MapleClient c) {
        return cms.get(c);
    }
}
