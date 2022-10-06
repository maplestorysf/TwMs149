package client;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Itzik
 */
public class MapleCoreAura {

    private final int id;

    private int str, dex, int_, luk, att, magic, total;
    private long expire = 0;
    private boolean delay;

    public MapleCoreAura(int id, long expire) {
        this.id = id;
        this.expire = expire;
    }

    public int getId() {
        return id;
    }

    public void setStr(int str) {
        this.str = str;
    }

    public int getStr() {
        return str;
    }

    public void setDex(int dex) {
        this.dex = dex;
    }

    public int getDex() {
        return dex;
    }

    public void setInt(int int_) {
        this.int_ = int_;
    }

    public int getInt() {
        return int_;
    }

    public void setLuk(int luk) {
        this.luk = luk;
    }

    public int getLuk() {
        return luk;
    }

    public void setAtt(int att) {
        this.att = att;
    }

    public int getAtt() {
        return att;
    }

    public void setMagic(int magic) {
        this.magic = magic;
    }

    public int getMagic() {
        return magic;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal() {
        return total;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public long getExpire() {
        return expire;
    }

    public void setDelay(boolean delay) {
        this.delay = delay;
    }

    public boolean getDelay() {
        return delay;
    }

    public void updateCoreAura(int level) {
        int coreLevel = getCoreLevel(level);
        int min = getCoreMinByLevel(coreLevel);
        int max = getCoreMaxByLevel(coreLevel);
        int str = (int) (Math.random() * (max - min + 1)) + min;
        int dex = (int) (Math.random() * (max - min + 1)) + min;
        int int_ = (int) (Math.random() * (max - min + 1)) + min;
        int luk = (int) (Math.random() * (max - min + 1)) + min;
        int attack = (int) (Math.random() * (max - min + 1)) + min;
        int magic = (int) (Math.random() * (max - min + 1)) + min;
        this.str = str;
        this.dex = dex;
        this.int_ = int_;
        this.luk = luk;
        this.att = attack;
        this.magic = magic;
        this.total = max;
        this.expire = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
        this.delay = getDelay();
    }

    public void resetCoreAura() {
        this.str = 5;
        this.dex = 5;
        this.int_ = 5;
        this.luk = 5;
        this.att = 0;
        this.magic = 0;
        this.total = 5;
        this.expire = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
        this.delay = false;
    }

    public void saveToDB() {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE `coreauras` SET `str` = ?, `dex` = ?, `int` = ?, `luk` = ?, `att` = ?, `magic` = ?, `total` = ?, `expire` = ?, `delay` = ? WHERE `cid` = ?")) {
                ps.setInt(1, str);
                ps.setInt(2, dex);
                ps.setInt(3, int_);
                ps.setInt(4, luk);
                ps.setInt(5, att);
                ps.setInt(6, magic);
                ps.setInt(7, total);
                ps.setLong(8, expire);
                ps.setBoolean(9, delay);
                ps.setInt(10, id);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            System.err.println("更新寶盒的保佑異常 " + ex);
        }
    }

    public static int getCoreLevel(int level) {
        if (level >= 30 && level < 40) {
            return 1;
        } else if (level >= 40 && level < 50) {
            return 2;
        } else if (level >= 50 && level < 60) {
            return 3;
        } else if (level >= 60 && level < 70) {
            return 4;
        } else if (level >= 70 && level < 80) {
            return 5;
        } else if (level >= 80 && level < 90) {
            return 6;
        } else if (level >= 90 && level < 100) {
            return 7;
        } else if (level >= 100 && level < 110) {
            return 8;
        } else if (level >= 110 && level < 120) {
            return 9;
        } else if (level >= 120) {
            return (int) (Math.random() * (15 - 10 + 1)) + 10;
        }
        return 1;
    }

    public static int getCoreMinByLevel(int level) {
        switch (level) {
            case 1:
                return 0;
            case 2:
                return 1;
            case 3:
                return 1;
            case 4:
                return 2;
            case 5:
                return 3;
            case 6:
                return 3;
            case 7:
                return 4;
            case 8:
                return 4;
            case 9:
                return 5;
            case 10:
                return 6;
            case 11:
                return 6;
            case 12:
                return 7;
            case 13:
                return 7;
            case 14:
                return 8;
            case 15:
                return 9;
            default:
                return 0;
        }
    }

    public static int getCoreMaxByLevel(int level) {
        switch (level) {
            case 1:
                return 2;
            case 2:
                return 4;
            case 3:
                return 6;
            case 4:
                return 8;
            case 5:
                return 10;
            case 6:
                return 12;
            case 7:
                return 14;
            case 8:
                return 16;
            case 9:
                return 18;
            case 10:
                return 20;
            case 11:
                return 23;
            case 12:
                return 28;
            case 13:
                return 35;
            case 14:
                return 44;
            case 15:
                return 55;
            default:
                return 2;
        }
    }
}
