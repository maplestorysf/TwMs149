package handling.login.handler;

import client.LoginCrypto;
import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import tools.FileoutputUtil;

public class AutoRegister {

    private static final int ACCOUNTS_PER_MAC = 3;

    public static boolean isExistAndLimitMac(String mac) {
        int alreadyTimes = 0;
        String sql = "SELECT macs FROM accounts WHERE macs like ?";
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, mac);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        alreadyTimes++;
                    }
                }
            }
        } catch (SQLException ex) {
            FileoutputUtil.printError("AutoRegister.txt", "isExistAndLimitMac", ex, "MAC: " + mac);
            System.out.println("檢查MAC" + mac + "發生錯誤:" + ex);
        }
        return alreadyTimes >= ACCOUNTS_PER_MAC;
    }

    public static boolean getAccountExists(String login) {
        boolean accountExists = false;
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT name FROM accounts WHERE name = ?");
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                accountExists = true;
            }
        } catch (SQLException ex) {
            System.err.println("[getAccountExists]" + ex);
        }
        return accountExists;
    }

    public static boolean createAccount(String login, String pwd, String eip, String macData) {
        String sockAddr = eip;
        Connection con = DatabaseConnection.getConnection();
        boolean maclimit = !isExistAndLimitMac(macData);
        if (maclimit) {
            try {
                PreparedStatement ps = con.prepareStatement("INSERT INTO accounts (name, password, email, birthday, macs, SessionIP) VALUES (?, ?, ?, ?, ?, ?)");
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH) + 1;
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                ps.setString(1, login);
                ps.setString(2, LoginCrypto.hexSha1(pwd));
                ps.setString(3, "autoregister@mail.com");
                ps.setString(4, year + "-" + month + "-" + dayOfMonth);//Created day
                ps.setString(5, macData);
                ps.setString(6, "/" + sockAddr.substring(1, sockAddr.lastIndexOf(':')));
                ps.executeUpdate();
                return true;
            } catch (SQLException ex) {
                System.err.println("[createAccount]" + ex);
            }
        }
        return false;

    }
}
