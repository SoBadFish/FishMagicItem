package org.sobadfish.magicitem.controller;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.item.Item;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.magicitem.MagicItemMainClass;
import org.sobadfish.magicitem.files.datas.CustomTagData;
import org.sobadfish.magicitem.files.entity.CommandCollect;
import org.sobadfish.magicitem.windows.items.BasePlayPanelItemInstance;
import org.sobadfish.magicitem.windows.lib.ChestInventoryPanel;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 控制器
 * @author Sobadfish
 * @date 2022/10/25
 */
public class MagicController implements Listener {


    /**
     * 配方管理
     * */
    public RecipeController recipeController;

    /**
     * 标签物品管理
     * */
    public TagController tagController;

    /**
     * 指令集管理
     * */
    public CommandController commandCollect;

    private final Plugin plugin;

    public Map<String,Long> coolTime = new LinkedHashMap<>();

    public MagicController(Plugin plugin){
        this.plugin = plugin;
        MagicController.sendLogger("&a加载指令集中...");
        commandCollect = CommandController.initCommand();
        MagicController.sendLogger("&a加载配方中...");
        this.recipeController = RecipeController.initRecipe();
        MagicController.sendLogger("&a加载物品中...");
        this.tagController = TagController.initTag();
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public static File getDataFolder() {
        return MagicItemMainClass.mainClass.getDataFolder();
    }

    public static String formatStr(String msg){
        return TextFormat.colorize('&',msg);
    }

    public static void sendMessageToObject(String msg, CommandSender commandSender){
        commandSender.sendMessage(formatStr(msg));
    }

    public static void saveResource(String filename, boolean replace){
        MagicItemMainClass.mainClass.saveResource(filename, replace);
    }

    public void save(){
        if(recipeController != null){
            recipeController.save();
        }
        if(tagController != null){
            tagController.save();
        }
    }

    public static void sendLogger(String msg){
        MagicItemMainClass.mainClass.getLogger().info(formatStr(msg));
    }

    @EventHandler
    public void onItemChange(InventoryTransactionEvent event) {
        ChestInventoryPanel chest = null;
        InventoryTransaction transaction = event.getTransaction();
        for (InventoryAction action : transaction.getActions()) {
            for (Inventory inventory : transaction.getInventories()) {
                if (inventory instanceof ChestInventoryPanel) {
                    chest = (ChestInventoryPanel) inventory;
                    Player player = ((ChestInventoryPanel) inventory).getPlayer();
                    Item i = action.getSourceItem();
                    if(i.hasCompoundTag()){
                        if(i.getNamedTag().contains("index") && i.getNamedTag().contains("button")){
                            event.setCancelled();
                            int index = i.getNamedTag().getInt("index");
                            BasePlayPanelItemInstance item = ((ChestInventoryPanel) inventory).getPanel().getOrDefault(index,null);

                            if(item != null){
                                ((ChestInventoryPanel) inventory).clickSolt = index;
                                item.onClick((ChestInventoryPanel) inventory,player);
                                ((ChestInventoryPanel) inventory).update();
                            }
                        }

                    }

                }
                if(inventory instanceof PlayerInventory){
                    EntityHuman player =((PlayerInventory) inventory).getHolder();
                    if(chest != null){
                        if(player instanceof Player) {
                            chest.onUpdate((Player) player);
                        }
                    }
                    if(tagController.lock.contains(player.getName())){
                        event.setCancelled();
                    }
                }
            }
        }
    }

    /**TODO 触发事件*/
    @EventHandler
    public void onDropEvent(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        Item item = event.getItem();
        if(tagController.lock.contains(player.getName())){
            event.setCancelled();
            return;
        }
        if(item.hasCompoundTag() && item.getNamedTag().contains(CustomTagData.TAG)){
            tagController.useItem(this,item, CommandCollect.Trigger.DROP,player);
        }

    }

}
