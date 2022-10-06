/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import database.DatabaseConnection;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.CashItemFactory;

/**
 *
 * @author Flower
 */
public class CashShopDumper {

    private static final MapleDataProvider data = MapleDataProviderFactory.getDataProvider("/Etc.wz");

    public static void main(String[] args) {
        CashItemFactory.getInstance().initialize(false);
        Connection con = DatabaseConnection.getConnection();
        List<Pair<Integer, Integer>> qq = new ArrayList<>();
        for (MapleData field : data.getData("Commodity.img").getChildren()) {
            try {
                final int itemId = MapleDataTool.getIntConvert("ItemId", field, 0);
                final int sn = MapleDataTool.getIntConvert("SN", field, 0);
                final int count = MapleDataTool.getIntConvert("Count", field, 0);
                final int price = MapleDataTool.getIntConvert("Price", field, 0);
                final int priority = MapleDataTool.getIntConvert("Priority", field, 0);
                final int period = MapleDataTool.getIntConvert("Period", field, 0);
                final int gender = MapleDataTool.getIntConvert("Gender", field, -1);
                final int meso = MapleDataTool.getIntConvert("Meso", field, 0);
                final int bonus = MapleDataTool.getIntConvert("Bonus", field, 0);
                int onSale = 1;
                if (sn < 20000000 || sn > 80000000) {
                    continue;
                }
                if (itemId == 0 || price < 5) {
                    continue;
                }
                if (meso > 0) {
                    continue;
                }
                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                    if (period > 0) {
                        continue;
                    }
                }
                boolean needContinue = false;
                for (Pair<Integer, Integer> p : qq) {
                    if (p.getLeft() == itemId && p.getRight() == count) {
                        needContinue = true;
                        break;
                    }
                }
                if (needContinue) {
                    continue;
                }
                if (itemId >= 5010000 && itemId <= 5021024 && period > 0) {
                    continue;
                }
                if (itemId == 2070007 || itemId == 2048001) {
                    continue;
                }
                if (bonus > 0) {
                    continue;
                }
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO cashshop_modified_items (serial, showup,itemid,priority,period,gender,count,meso,discount_price,mark, unk_1, unk_2, unk_3) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                    ps.setInt(1, sn);
                    ps.setInt(2, onSale);
                    ps.setInt(3, itemId);
                    ps.setInt(4, -1);
                    ps.setInt(5, period);
                    ps.setInt(6, gender);
                    ps.setInt(7, count);
                    ps.setInt(8, meso);
                    ps.setInt(9, price);
                    qq.add(new Pair<>(itemId, count));
                    ps.setInt(10, -2);
                    ps.setInt(11, 0);
                    ps.setInt(12, 0);
                    ps.setInt(13, 0);
                    ps.executeUpdate();
                }
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }
}
