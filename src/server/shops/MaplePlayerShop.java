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
package server.shops;

import java.util.ArrayList;
import java.util.List;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.MapleCharacter;
import client.MapleClient;
import constants.GameConstants;
import constants.ServerConstants;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import tools.FileoutputUtil;
import tools.packet.CWvsContext;
import tools.packet.PlayerShopPacket;

public class MaplePlayerShop extends AbstractPlayerStore {

    private int boughtnumber = 0;
    private List<String> bannedList = new ArrayList<>();

    public MaplePlayerShop(MapleCharacter owner, int itemId, String desc) {
        super(owner, itemId, desc, "", 3);
    }

    @Override
    public void buy(MapleClient c, int item, short quantity) {
        MaplePlayerShopItem pItem = items.get(item);
        if (pItem.bundles > 0) {
            Item newItem = pItem.item.copy();
            newItem.setQuantity((short) (quantity * newItem.getQuantity()));
            short flag = newItem.getFlag();

            if (ItemFlag.KARMA_EQ.check(flag)) {
                newItem.setFlag((short) (flag - ItemFlag.KARMA_EQ.getValue()));
            } else if (ItemFlag.KARMA_USE.check(flag)) {
                newItem.setFlag((short) (flag - ItemFlag.KARMA_USE.getValue()));
            }
            final int gainmeso = pItem.price * quantity;
            if (c.getPlayer().getMeso() >= gainmeso) {
                if (!c.getPlayer().canHold(newItem.getItemId())) {
                    c.getPlayer().dropMessage(1, "您的背包滿了.");
                    c.sendPacket(CWvsContext.enableActions());
                    return;
                }
                if ((getMCOwner().getMeso() + gainmeso) < 0) {
                    c.getPlayer().dropMessage(1, "目前賣家身上的楓幣，\r\n已經超過了21億因此無法購買此道具。");
                    c.sendPacket(CWvsContext.enableActions());
                    return;
                }
                if (getMCOwner().getMeso() + gainmeso > 0 && MapleInventoryManipulator.checkSpace(c, newItem.getItemId(), newItem.getQuantity(), newItem.getOwner()) && MapleInventoryManipulator.addFromDrop(c, newItem, false)) {
                    pItem.bundles -= quantity;
                    bought.add(new BoughtItem(newItem.getItemId(), quantity, gainmeso, c.getPlayer().getName()));
                    c.getPlayer().gainMeso(-gainmeso, false);
                    getMCOwner().gainMeso(gainmeso - GameConstants.EntrustedStoreTax(gainmeso), false);
                    if (ServerConstants.log_mshop) {
                        FileoutputUtil.logToFile("logs/data/個人商店.txt", "\r\n 時間　[" + FileoutputUtil.NowTime() + "] IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 玩家 " + c.getPlayer().getName() + " 從  " + getOwnerName() + " 的個人商店購買了" + MapleItemInformationProvider.getInstance().getName(newItem.getItemId()) + " (" + newItem.getItemId() + ") x" + quantity + " 單個價錢為 : " + pItem.price + " 道具唯一ID : " + newItem.getEquipOnlyId());
                    }
                    if (pItem.bundles <= 0) {
                        boughtnumber++;
                        if (boughtnumber == items.size()) {
                            closeShop(false, true);
                            return;
                        }
                    }
                } else {
                    c.getPlayer().dropMessage(1, "請確認道具欄位是否已經滿了。");
                    c.sendPacket(CWvsContext.enableActions());
                }
            } else {
                c.getPlayer().dropMessage(1, "您的楓幣不足，無法購買。");
                c.sendPacket(CWvsContext.enableActions());
            }
            getMCOwner().getClient().sendPacket(PlayerShopPacket.shopItemUpdate(this));
        }
    }

    @Override
    public byte getShopType() {
        return IMaplePlayerShop.PLAYER_SHOP;
    }

    @Override
    public void closeShop(boolean saveItems, boolean remove) {
        MapleCharacter owner = getMCOwner();
        removeAllVisitors(3, 1);
        getMap().removeMapObject(this);

        for (MaplePlayerShopItem items : getItems()) {
            if (items.bundles > 0) {
                Item newItem = items.item.copy();
                newItem.setQuantity((short) (items.bundles * newItem.getQuantity()));
                if (MapleInventoryManipulator.addFromDrop(owner.getClient(), newItem, false)) {
                    items.bundles = 0;
                } else {
                    saveItems(); //O_o
                    break;
                }
            }
        }
        owner.setPlayerShop(null);
        update();
//        getMCOwner().getClient().sendPacket(PlayerShopPacket.shopErrorMessage(10, 1));
    }

    public void banPlayer(String name) {
        if (!bannedList.contains(name)) {
            bannedList.add(name);
        }
        for (int i = 0; i < 3; i++) {
            MapleCharacter chr = getVisitor(i);
            if (chr.getName().equals(name)) {
                chr.getClient().sendPacket(PlayerShopPacket.shopErrorMessage(5, 1));
                chr.setPlayerShop(null);
                removeVisitor(chr);
            }
        }
    }

    public boolean isBanned(String name) {
        if (bannedList.contains(name)) {
            return true;
        }
        return false;
    }
}
