package org.sobadfish.magicitem.windows;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.scheduler.PluginTask;
import org.sobadfish.magicitem.MagicItemMainClass;
import org.sobadfish.magicitem.windows.items.BasePlayPanelItemInstance;
import org.sobadfish.magicitem.windows.lib.AbstractFakeInventory;
import org.sobadfish.magicitem.windows.lib.ChestInventoryPanel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 发送窗口
 * @author SoBadFish
 * 2022/1/2
 */
public class DisPlayerPanel implements InventoryHolder {

    private AbstractFakeInventory inventory;

    public ChestInventoryPanel panel;

    public static LinkedHashMap<Player,DisPlayerPanel> panelLib = new LinkedHashMap<>();

    private DisPlayerPanel(){
    }



    public static DisPlayerPanel getDisPlayPanel(Player player,String name,Class<? extends ChestInventoryPanel> tClass){
        try {
            DisPlayerPanel panel;
            if(!panelLib.containsKey(player)){
                panelLib.put(player,new DisPlayerPanel());
            }
            panel = panelLib.get(player);

            Constructor<?> tConstructor = tClass.getConstructor(Player.class,InventoryHolder.class,String.class);
            panel.panel = (ChestInventoryPanel) tConstructor.newInstance(player,panel,name);
            return panel;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setPanel(ChestInventoryPanel panel) {
        this.panel = panel;
    }

    public void displayPlayer(Map<Integer, BasePlayPanelItemInstance> itemMap){
        panel.setPanel(itemMap);
        panel.id = ++Entity.entityCount;
        inventory = panel;
        Server.getInstance().getScheduler().scheduleDelayedTask(MagicItemMainClass.mainClass, new PluginTask<>(MagicItemMainClass.mainClass) {
            @Override
            public void onRun(int i) {
                panel.getPlayer().addWindow(panel);
            }
        },5);


    }



    @Override
    public Inventory getInventory() {
        return inventory;
    }


}
