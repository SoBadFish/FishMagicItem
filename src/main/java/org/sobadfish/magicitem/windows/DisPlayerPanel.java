package org.sobadfish.magicitem.windows;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import org.sobadfish.magicitem.windows.items.BasePlayPanelItemInstance;
import org.sobadfish.magicitem.windows.lib.AbstractFakeInventory;
import org.sobadfish.magicitem.windows.lib.ChestInventoryPanel;

import java.util.Map;

/**
 * 发送窗口
 * @author SoBadFish
 * 2022/1/2
 */
public class DisPlayerPanel implements InventoryHolder {

    private AbstractFakeInventory inventory;

    public ChestInventoryPanel panel;

    //TODO 手机 6 * 9
    //Win10 9 * 6
    public DisPlayerPanel(Player player,String name){
        panel = new ChestInventoryPanel(player,this,name);
    }


    public void displayPlayer(Map<Integer, BasePlayPanelItemInstance> itemMap){
        panel.setPanel(itemMap);
        panel.id = ++Entity.entityCount;
        inventory = panel;
        panel.getPlayer().addWindow(panel);

    }



    @Override
    public Inventory getInventory() {
        return inventory;
    }


}
