package client;

import constants.GameConstants;
import constants.MiMiConfig;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import tools.Triple;

import database.DatabaseConnection;
import java.util.regex.Pattern;
import tools.FileoutputUtil;
import tools.Pair;

public class MapleCharacterUtil {

    private static final Pattern namePattern = Pattern.compile("[a-zA-Z0-9_-]{3,12}");
    private static final Pattern petPattern = Pattern.compile("[a-zA-Z0-9_-]{4,12}");

    public static final boolean canCreateChar(final String name, final boolean gm) {
        if (getIdByName(name) != -1 || !isEligibleCharName(name, gm)) {
            return false;
        }
        return true;
    }

    public static final boolean canAndroidName(final String name) {
        if (getIdByName(name) != -1 || !isEligibleAndroidName(name)) {
            return false;
        }
        return true;
    }

    public static final boolean isEligibleCharName(final String name, final boolean gm) {
        if (name.getBytes().length > 12) {
            return false;
        }
        if (name.getBytes().length < 4) {
            return false;
        }
        if (gm) {
            return true;
        }
        for (String z : GameConstants.RESERVED) {
            if (name.contains(z)) {
                return false;
            }
        }

        if (!MiMiConfig.isCanTalkText(name)) {
            return false;
        }
        return true;
    }

    public static final boolean isEligibleAndroidName(final String name) {
        if (name.getBytes().length > 15) {
            return false;
        }
        if (name.getBytes().length < 4) {
            return false;
        }

        for (String z : GameConstants.RESERVED) {
            if (name.contains(z)) {
                return false;
            }
        }

        if (!MiMiConfig.isCanTalkText(name)) {
            return false;
        }
        return true;
    }

    public static final boolean canChangePetName(final String name) {
        if (petPattern.matcher(name).matches()) {
            for (String z : GameConstants.RESERVED) {
                if (name.contains(z)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static final String makeMapleReadable(final String in) {
        String wui = in.replace('I', 'i');
        wui = wui.replace('l', 'L');
        wui = wui.replace("rn", "Rn");
        wui = wui.replace("vv", "Vv");
        wui = wui.replace("VV", "Vv");
        return wui;
    }

    public static final int getIdByName(final String name) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT id FROM characters WHERE name = ?");
            ps.setString(1, name);
            final ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            final int id = rs.getInt("id");
            rs.close();
            ps.close();

            return id;
        } catch (SQLException e) {
            System.err.println("error 'getIdByName' " + e);
        }
        return -1;
    }

    public static final Pair<String, Integer> getNameById(int cid, int world) {
        try {
            Connection con = DatabaseConnection.getConnection();
            Pair id;
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE id = ? AND world = ?")) {
                ps.setInt(1, cid);
                ps.setInt(2, world);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        rs.close();
                        ps.close();
                        return null;
                    }
                    id = new Pair(rs.getString("name"), rs.getInt("accountid"));
                }
            }
            return id;
        } catch (SQLException e) {
            System.err.println("error 'getNameById' " + e);
        }
        return null;
    }

    public static final void updateCoreAura(MapleCharacter chr, int cid) {
        try {
            MapleCoreAura core = chr.getCoreAura();
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps;
            ps = con.prepareStatement("update coreauras set `str` = ?, `dex` = ?, `int` = ?, `luk` = ?, `att` = ?, `magic` = ?, `total` = ? where `cid` = ?");
            ps.setInt(1, core.getStr());
            ps.setInt(2, core.getDex());
            ps.setInt(3, core.getInt());
            ps.setInt(4, core.getLuk());
            ps.setInt(5, core.getAtt());
            ps.setInt(6, core.getMagic());
            ps.setInt(7, core.getTotal());
            ps.setInt(8, cid);
            ps.execute();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("[setCoreAura] 無法連接資料庫");
        } catch (Exception ex) {
            System.err.println("[setCoreAura] " + ex);
        }
    }

    // -2 = An unknown error occured
    // -1 = Account not found on database
    // 0 = You do not have a second password set currently.
    // 1 = The password you have input is wrong
    // 2 = Password Changed successfully
    public static final int Change_SecondPassword(final int accid, final String password, final String newpassword) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * from accounts where id = ?");
            ps.setInt(1, accid);
            final ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            String secondPassword = rs.getString("2ndpassword");
            final String salt2 = rs.getString("salt2");
            if (secondPassword != null && salt2 != null) {
                secondPassword = LoginCrypto.rand_r(secondPassword);
            } else if (secondPassword == null && salt2 == null) {
                rs.close();
                ps.close();
                return 0;
            }
            if (!check_ifPasswordEquals(secondPassword, password, salt2)) {
                rs.close();
                ps.close();
                return 1;
            }
            rs.close();
            ps.close();

            String SHA1hashedsecond;
            try {
                SHA1hashedsecond = LoginCryptoLegacy.encodeSHA1(newpassword);
            } catch (Exception e) {
                return -2;
            }
            ps = con.prepareStatement("UPDATE accounts set 2ndpassword = ?, salt2 = ? where id = ?");
            ps.setString(1, SHA1hashedsecond);
            ps.setString(2, null);
            ps.setInt(3, accid);

            if (!ps.execute()) {
                ps.close();
                return 2;
            }
            ps.close();
            return -2;
        } catch (SQLException e) {
            System.err.println("error 'getIdByName' " + e);
            return -2;
        }
    }

    private static boolean check_ifPasswordEquals(final String passhash, final String pwd, final String salt) {
        // Check if the passwords are correct here. :B
        if (LoginCryptoLegacy.isLegacyPassword(passhash) && LoginCryptoLegacy.checkPassword(pwd, passhash)) {
            // Check if a password upgrade is needed.
            return true;
        } else if (salt == null && LoginCrypto.checkSha1Hash(passhash, pwd)) {
            return true;
        } else if (LoginCrypto.checkSaltedSha512Hash(passhash, pwd, salt)) {
            return true;
        }
        return false;
    }

    //id accountid gender
    public static Triple<Integer, Integer, Integer> getInfoByName(String name, int world) {
        try {

            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE name = ? AND world = ?");
            ps.setString(1, name);
            ps.setInt(2, world);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return null;
            }
            Triple<Integer, Integer, Integer> id = new Triple<>(rs.getInt("id"), rs.getInt("accountid"), rs.getInt("gender"));
            rs.close();
            ps.close();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setNXCodeUsed(String name, String code) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("UPDATE nxcode SET `user` = ?, `valid` = 0 WHERE code = ?");
        ps.setString(1, name);
        ps.setString(2, code);
        ps.execute();
        ps.close();
    }

    public static void sendNote(String to, String name, String msg, int fame) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO notes (`to`, `from`, `message`, `timestamp`, `gift`) VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, to);
            ps.setString(2, name);
            ps.setString(3, msg);
            ps.setLong(4, System.currentTimeMillis());
            ps.setInt(5, fame);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Unable to send note" + e);
        }
    }

    public static Triple<Boolean, Integer, Integer> getNXCodeInfo(String code) throws SQLException {
        Triple<Boolean, Integer, Integer> ret = null;
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT `valid`, `type`, `item` FROM nxcode WHERE code LIKE ?");
        ps.setString(1, code);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            ret = new Triple<>(rs.getInt("valid") > 0, rs.getInt("type"), rs.getInt("item"));
        }
        rs.close();
        ps.close();
        return ret;
    }

    public static final boolean isExistCharacterInDataBase(final int id) {
        try {
            Connection con = DatabaseConnection.getConnection();

            final String name;
            try (PreparedStatement ps = con.prepareStatement("SELECT name FROM characters WHERE id = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        rs.close();
                        ps.close();
                        return false;
                    }
                    name = rs.getString("name");
                }
            }
        } catch (SQLException e) {
            System.err.println("error 'isExistCharacterInDataBase' " + e);
        }
        return true;
    }

    public static int getGiftAmount(int id) {
        int counts = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT count(*) FROM gifts WHERE recipient = ?")) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    counts = rs.getInt(1);
                }
            }
        } catch (Exception ex) {
            FileoutputUtil.printError("MapleCharacterUtil.txt", "getGiftCount", ex, "id: " + id);
        }
        return counts;
    }
}
