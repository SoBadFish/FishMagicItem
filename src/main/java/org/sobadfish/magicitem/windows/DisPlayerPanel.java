package org.sobadfish.magicitem.windows;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import org.sobadfish.magicitem.windows.items.BasePlayPanelItemInstance;
import org.sobadfish.magicitem.windows.lib.AbstractFakeInventory;
import org.sobadfish.magicitem.windows.lib.ChestInventoryPanel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * 发送窗口
 * @author SoBadFish
 * 2022/1/2
 */
public class DisPlayerPanel implements InventoryHolder {

    private AbstractFakeInventory inventory;

    public ChestInventoryPanel panel;

    private DisPlayerPanel(){
    }



    public static DisPlayerPanel getDisPlayPanel(Player player,String name,Class<? extends ChestInventoryPanel> tClass){
        try {
            DisPlayerPanel panel = new DisPlayerPanel();
            Constructor<?> tConstructor = tClass.getConstructor(Player.class,InventoryHolder.class,String.class);
            panel.panel = (ChestInventoryPanel) tConstructor.newInstance(player,panel,name);
            return panel;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
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
