package scripting;

import java.io.File;
import java.io.IOException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import client.MapleClient;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import javax.script.ScriptException;
import tools.FilePrinter;
import tools.StringUtil;

/**
 *
 * @author Matze
 */
public abstract class AbstractScriptManager {

    private static HashMap<String, ScriptEngine> npcs = new HashMap();
    private static final ScriptEngineManager sem = new ScriptEngineManager();

    protected Invocable getInvocable(String path, MapleClient c) {
        return getInvocable(path, c, false);
    }

    protected Invocable getInvocable(String path, MapleClient c, boolean npc) {
        path = "scripts/" + path;
        ScriptEngine engine = null;

        if (c != null) {
            engine = c.getScriptEngine(path);
        }
        if (npc && engine == null) {
            ScriptEngine tempEngine = npcs.get(path);
            if (tempEngine != null) {
                //engine = tempEngine;
            }
        }
        if (engine == null) {
            File scriptFile = new File(path);
            if (!scriptFile.exists()) {
                return null;
            }
            if (c != null && c.getPlayer() != null) {
                if (c.getPlayer().getDebugMessage()) {
                    c.getPlayer().dropMessage("getInvocable - Part1");
                }
            }
            engine = sem.getEngineByName("nashorn");
            if (c != null && c.getPlayer() != null) {
                if (c.getPlayer().getDebugMessage()) {
                    c.getPlayer().dropMessage("getInvocable - Part2");
                }
            }
            if (c != null) {
                c.setScriptEngine(path, engine);
                if (c != null && c.getPlayer() != null) {
                    if (c.getPlayer().getDebugMessage()) {
                        c.getPlayer().dropMessage("getInvocable - Part3");
                    }
                }
            }
            InputStreamReader fr = null;
            try {
                fr = new InputStreamReader(new FileInputStream(scriptFile), StringUtil.codeString(scriptFile));
                if (c != null && c.getPlayer() != null) {
                    if (c.getPlayer().getDebugMessage()) {
                        c.getPlayer().dropMessage("getInvocable - Part4");
                    }
                }

                engine.eval(fr);
                if (c != null && c.getPlayer() != null) {
                    if (c.getPlayer().getDebugMessage()) {
                        c.getPlayer().dropMessage("getInvocable - Part5");
                    }
                }
            } catch (ScriptException | IOException e) {
                FilePrinter.printError("AbstractScriptManager.txt", "Error executing script. Path: " + path + "\nException " + e);
                return null;
            } finally {
                try {
                    if (fr != null) {
                        fr.close();
                    }
                } catch (IOException ignore) {
                }
            }
        } else if (c != null && npc) {
            c.getPlayer().dropMessage(-1, "若不能攻擊或不能跟npc對話,請在對話框打 @ea 來解除異常狀態");
        }
        if (npc && !npcs.containsKey(path)) {
            npcs.put(path, engine);
        }
        return (Invocable) engine;
    }

    public static void cleanNpc(int id) {
        String path = "scripts/npc/" + id + ".js";
        if (npcs.containsKey(path)) {
            npcs.remove(path);
        }
    }

    public static void cleanNpcs() {
        npcs.clear();
    }
}
