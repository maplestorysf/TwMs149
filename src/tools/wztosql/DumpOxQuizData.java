/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.wztosql;

import database.DatabaseConnection;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;

public class DumpOxQuizData {

    private final Connection con = DatabaseConnection.getConnection();
    static CharsetEncoder asciiEncoder = Charset.forName("UTF-8").newEncoder();

    public static void main(String args[]) throws FileNotFoundException, IOException, SQLException {
        if (System.getProperty("net.sf.odinms.wzpath") == null) {
            System.setProperty("net.sf.odinms.wzpath", "wz");
        }
        System.out.println("OXQuiz.img 讀取中 ...");
        DumpOxQuizData dump = new DumpOxQuizData();
        dump.dumpOxData();
        System.out.println("Ox quiz 讀取資料完成。");
    }

    public void dumpOxData() throws SQLException {

        MapleDataProvider oz = MapleDataProviderFactory.getDataProvider("/Etc.wz");
        MapleData ox = oz.getData("OXQuiz.img");

        PreparedStatement ps = con.prepareStatement("DELETE FROM `wz_oxdata`");
        ps.execute();
        ps.close();
        for (MapleData child1 : ox.getChildren()) {
            for (MapleData child2 : child1.getChildren()) {
                MapleData q = child2.getChildByPath("q");
                MapleData d = child2.getChildByPath("d");
                int a = MapleDataTool.getInt(child2.getChildByPath("a"));
                String qs = "";
                String ds = "";
                String as;
                if (a == 0) {
                    as = "x";
                } else {
                    as = "o";
                }
                if (q != null) {
                    qs = (String) q.getData();
                }
                if (d != null) {
                    ds = (String) d.getData();
                }
                if (!asciiEncoder.canEncode(child1.getName()) || !asciiEncoder.canEncode(child2.getName())
                        || !asciiEncoder.canEncode(qs) || !asciiEncoder.canEncode(ds)
                        || !asciiEncoder.canEncode(as)) {
                    continue;
                }
                ps = con.prepareStatement("INSERT INTO `wz_oxdata`"
                        + " (`questionset`, `questionid`, `question`, `display`, `answer`)"
                        + " VALUES (?, ?, ?, ?, ?)");
                ps.setString(1, child1.getName());
                ps.setString(2, child2.getName());
                ps.setString(3, qs);
                ps.setString(4, ds);
                ps.setString(5, as);
                ps.execute();
                ps.close();
            }
        }
    }
}
