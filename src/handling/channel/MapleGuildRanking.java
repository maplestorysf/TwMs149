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
package handling.channel;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import database.DatabaseConnection;

public class MapleGuildRanking {

    private static MapleGuildRanking instance = new MapleGuildRanking();
    private List<GuildRankingInfo> ranks = new LinkedList<>();
    private List<fameRankingInfo> famerank = new LinkedList<>();
    private List<RebornRankingInfo> rebornrank = new LinkedList<>();

    public static MapleGuildRanking getInstance() {
        return instance;
    }

    public void load() {
        if (ranks.isEmpty()) {
            reload();
        }
    }

    public List<fameRankingInfo> getFameRank() {
        if (famerank.isEmpty()) {
            showFameRank();
        }
        return famerank;
    }

    public List<RebornRankingInfo> getRebornRank() {
        if (rebornrank.isEmpty()) {
            showRebornRank();
        }
        return rebornrank;
    }

    public List<GuildRankingInfo> getRank() {
        return ranks;
    }

    private void reload() {
        ranks.clear();
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM guilds where leader in (select id from characters where `gm` < 1) ORDER BY `GP` DESC LIMIT 50");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                final GuildRankingInfo rank = new GuildRankingInfo(
                        rs.getString("name"),
                        rs.getInt("GP"),
                        rs.getInt("logo"),
                        rs.getInt("logoColor"),
                        rs.getInt("logoBG"),
                        rs.getInt("logoBGColor"));

                ranks.add(rank);
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error handling guildRanking");
            e.printStackTrace();
        }
    }

    private void showFameRank() {
        famerank.clear();

        try {
            Connection con = DatabaseConnection.getConnection();
            ResultSet rs;
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM `characters` WHERE gm < 1 and fame > 0 ORDER BY fame DESC LIMIT 20")) {
                rs = ps.executeQuery();
                while (rs.next()) {
                    final fameRankingInfo rank2 = new fameRankingInfo(
                            rs.getString("name"),
                            rs.getInt("fame"),
                            rs.getInt("str"),
                            rs.getInt("dex"),
                            rs.getInt("int"),
                            rs.getInt("luk"));
                    famerank.add(rank2);
                }

                rs.close();
            } catch (SQLException e) {
                System.err.println("未能顯示名聲排行");
                System.err.println(e);
            }
        } catch (Exception e) {
            System.err.println("未能顯示名聲排行");
            System.err.println(e);
        }
    }

    private void showRebornRank() {
        rebornrank.clear();

        try {
            Connection con = DatabaseConnection.getConnection();

            ResultSet rs;

            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE gm < 1 and reborns > 0 ORDER BY `reborns` DESC LIMIT 20")) {
                rs = ps.executeQuery();
                while (rs.next()) {
                    final RebornRankingInfo rank2 = new RebornRankingInfo(
                            rs.getString("name"),
                            rs.getInt("reborns"),
                            rs.getInt("str"),
                            rs.getInt("dex"),
                            rs.getInt("int"),
                            rs.getInt("luk"));
                    rebornrank.add(rank2);
                }

                rs.close();
            } catch (SQLException e) {
                System.err.println("未能顯示轉生排行");
            }
        } catch (Exception e) {
            System.err.println("未能顯示轉生排行");
        }
    }

    public static class GuildRankingInfo {

        private String name;
        private int gp, logo, logocolor, logobg, logobgcolor;

        public GuildRankingInfo(String name, int gp, int logo, int logocolor, int logobg, int logobgcolor) {
            this.name = name;
            this.gp = gp;
            this.logo = logo;
            this.logocolor = logocolor;
            this.logobg = logobg;
            this.logobgcolor = logobgcolor;
        }

        public String getName() {
            return name;
        }

        public int getGP() {
            return gp;
        }

        public int getLogo() {
            return logo;
        }

        public int getLogoColor() {
            return logocolor;
        }

        public int getLogoBg() {
            return logobg;
        }

        public int getLogoBgColor() {
            return logobgcolor;
        }
    }

    public static class fameRankingInfo {

        private final String name;
        private final int fame;
        private final int str, dex, _int, luk;

        public fameRankingInfo(String name, int fame, int str, int dex, int intt, int luk) {
            this.name = name;
            this.fame = fame;
            this.str = str;
            this.dex = dex;
            this._int = intt;
            this.luk = luk;
        }

        public String getName() {
            return name;
        }

        public int getFame() {
            return fame;
        }

        public int getStr() {
            return str;
        }

        public int getDex() {
            return dex;
        }

        public int getInt() {
            return _int;
        }

        public int getLuk() {
            return luk;
        }
    }

    public static class RebornRankingInfo {

        private final String name;
        private final int str, dex, _int, luk, reborn;

        public RebornRankingInfo(String name, int reborn, int str, int dex, int intt, int luk) {
            this.name = name;
            this.str = str;
            this.dex = dex;
            this._int = intt;
            this.luk = luk;
            this.reborn = reborn;
        }

        public String getName() {
            return name;
        }

        public int getStr() {
            return str;
        }

        public int getDex() {
            return dex;
        }

        public int getInt() {
            return _int;
        }

        public int getLuk() {
            return luk;
        }

        public int getReborn() {
            return reborn;
        }
    }
}
