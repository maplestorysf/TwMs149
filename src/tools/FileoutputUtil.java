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
package tools;

import client.MapleCharacter;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.TimeZone;

public class FileoutputUtil {

    // Logging output file
    public static final String Acc_Stuck = "Log_AccountStuck.rtf",
            Login_Error = "Log_Login_Error.rtf",
    // IP_Log = "Log_AccountIP.rtf",
    //GMCommand_Log = "Log_GMCommand.rtf",
    // Zakum_Log = "Log_Zakum.rtf",
    //Horntail_Log = "Log_Horntail.rtf",
    Script_Bug = "logs/Except/腳本漏洞.txt",
            Pinkbean_Log = "Log_Pinkbean.rtf",
            ScriptEx_Log = "Log_Script_Except.rtf",
            PacketEx_Log = "Log_Packet_Except.rtf", // I cba looking for every error, adding this back in.
            Donator_Log = "Log_Donator.rtf",
            Hacker_Log = "Log_Hacker.rtf",
            Movement_Log = "Log_Movement.rtf",
            CommandEx_Log = "Log_Command_Except.rtf", //PQ_Log = "Log_PQ.rtf"
            UnknownPacket_Log = "數據包_未知.txt",
            KnownPacket_Log = "數據包_已知.txt";
    // End
    private static final SimpleDateFormat sdfT = new SimpleDateFormat("yyyy年MM月dd日HH時mm分ss秒");
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat sdfGMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat sdf_ = new SimpleDateFormat("yyyy-MM-dd");
    private static final String FILE_PATH = "logs/";
    private static final String ERROR = "error/";

    static {
        sdfGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public static void log(final String file, final String msg, boolean size) {
        logToFile(file, "\r\n------------------------ " + CurrentReadable_Time() + " ------------------------\r\n" + msg, false, size);
    }

    public static void log(final String file, final String msg) {
        logToFile(file, "\r\n------------------------ " + CurrentReadable_Time() + " ------------------------\r\n" + msg);
    }

    public static void logToFile(final String file, final String[] msgs) {
        for (int i = 0; i < msgs.length; i++) {
            logToFile(file, msgs[i], false);
            if (i < msgs.length - 1) {
                logToFile(file, "\r\n", false);
            }
        }
    }

    public static void logToFile(final String file, final String msg) {
        logToFile(file, msg, false);
    }

    public static void logToFileIfNotExists(final String file, final String msg) {
        logToFile(file, msg, true);
    }

    public static void logToFile(final String file, final String msg, boolean notExists) {
        logToFile(file, msg, notExists, true);
    }

    /**
     * @param file      - 檔案名稱(包含目錄)
     * @param oldmsg    - 要記錄的訊息
     * @param notExists - 檔案是否存在
     * @param size      - 是否單文件限制大小
     */
    public static void logToFile(final String file, final String oldmsg, boolean notExists, boolean size) {
        String msg = oldmsg;
        if (!oldmsg.contains("\r\n")) {
            msg = "\r\n" + oldmsg;
        }
        FileOutputStream out = null;
        try {
            File outputFile = new File(file);
            if (outputFile.exists() && outputFile.isFile() && outputFile.length() >= 1024000 && size) {
                String sub = file.substring(0, file.indexOf('/', file.indexOf("/") + 1) + 1) + "old/" + file.substring(file.indexOf('/', file.indexOf("/") + 1) + 1, file.length() - 4);
                String time = sdfT.format(Calendar.getInstance().getTime());
                String sub2 = file.substring(file.length() - 4, file.length());
                String output = sub + "_" + time + sub2;
                if (new File(output).getParentFile() != null) {
                    new File(output).getParentFile().mkdirs();
                }
                outputFile.renameTo(new File(output));
                outputFile = new File(file);
            }
            if (outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }
            out = new FileOutputStream(file, true);
            if (!out.toString().contains(msg) || !notExists) {
                OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
                osw.write(msg);
                osw.flush();
            }
        } catch (IOException ess) {
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ignore) {
            }
        }
    }

    public static void outputFileError(final String file, final Throwable t) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file, true);
            out.write(("\n------------------------ " + CurrentReadable_Time() + " ------------------------\n").getBytes());
            out.write(getString(t).getBytes());
        } catch (IOException ess) {
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ignore) {
            }
        }
    }

    public static String getChineseData() {
        return sdfT.format(Calendar.getInstance().getTime());
    }

    public static String CurrentReadable_Date() {
        return sdf_.format(Calendar.getInstance().getTime());
    }

    public static String CurrentReadable_Time() {
        return sdf.format(Calendar.getInstance().getTime());
    }

    public static String CurrentReadable_TimeGMT() {
        return sdfGMT.format(new Date());
    }

    public static String NowTime() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式    
        String hehe = dateFormat.format(now);
        return hehe;
    }

    public static String getString(final Throwable e) {
        String retValue = null;
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            retValue = sw.toString();
        } finally {
            try {
                if (pw != null) {
                    pw.close();
                }
                if (sw != null) {
                    sw.close();
                }
            } catch (IOException ignore) {
            }
        }
        return retValue;
    }

    public static void logToFile_NpcScript_Bug(final MapleCharacter chr, final String msg) {
        logToFile(Script_Bug, CurrentReadable_Time() + " 玩家[" + chr.getName() + "] NPC腳本[" + chr.getNpcNow() + "]" + msg, false);
    }

    public static void print(final String name, final String s) {
        print(name, s, true);
    }

    public static void print(final String name, final String s, boolean line) {
        logToFile(FILE_PATH + name, s + (line ? "\r\n---------------------------------\r\n" : null));
    }

    public static void printError(final String name, final Throwable t, final String info) {
        printError(name, info + "\r\n" + getString(t));
    }

    public static void printError(final String file, String function, final Throwable t, String msg) {
        printError(file, "[" + function + "] " + msg + "\r\n " + getString(t));
    }

    public static void printError(final String name, final String s) {
        logToFile(FILE_PATH + ERROR + sdf_.format(Calendar.getInstance().getTime()) + "/" + name, s + "\r\n---------------------------------\r\n");
    }
}
