package org.sobadfish.magicitem.controller;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
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

    /**
     * 语言文件管理
     * */
    public LanguageController languageController;

    private final Plugin plugin;

    public Map<String,Long> coolTime = new LinkedHashMap<>();

    public MagicController(Plugin plugin){
        this.plugin = plugin;

        MagicController.sendLogger("&a加载指令集中...");
        File d = new File(getDataFolder()+"/command");
        if(!d.exists()){
            if(!d.mkdirs()){
                sendLogger("创建command文件夹失败");
            }
        }
        commandCollect = CommandController.initCommand();
        MagicController.sendLogger("&a加载配方中...");
        this.recipeController = RecipeController.initRecipe();
        MagicController.sendLogger("&a加载物品中...");
        d = new File(getDataFolder()+"/items");
        if(!d.exists()){
            if(!d.mkdirs()){
                sendLogger("创建items文件夹失败");
            }
        }
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

    public static void saveResource(String filename,String outputFile, boolean replace){
        MagicItemMainClass.mainClass.saveResource(filename,outputFile, replace);
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


    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Item item = player.getInventory().getItemInHand();
        if(item != null) {
            if (item.hasCompoundTag() && item.getNamedTag().contains(CustomTagData.TAG)) {
                if (tagController.lock.contains(player.getName())) {
                    event.setCancelled();
                    return;
                }
                CommandCollect.Trigger trigger = null;
                if(event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_AIR ||
                        event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK){
                    trigger = CommandCollect.Trigger.LEFT_CLICK;
                }
                if(event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR ||
                        event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK){
                    trigger = CommandCollect.Trigger.RIGHT_CLICK;
                }
                if(trigger != null){
                    tagController.onUseItemInventory(this,player.getInventory().getHeldItemIndex(), item, trigger, player);
                }
                for(Map.Entry<Integer,Item> entry: ((EntityHuman) player).getOffhandInventory().slots.entrySet()){
                    Item item2 = entry.getValue();
                    if(item2.hasCompoundTag() && item2.getNamedTag().contains(CustomTagData.TAG)){
                        tagController.onUseItemOffhand(this,entry.getKey(),item2, trigger,player);
                    }
                }
            }
        }

    }
    /**TODO 触发事件*/
    @EventHandler
    public void onDamage(EntityDamageEvent event){
        Entity entity = event.getEntity();
        if(event instanceof EntityDamageByEntityEvent){
            Entity entity1 = ((EntityDamageByEntityEvent) event).getDamager();
            onUseEntity(entity1);
            return;
        }
        onUseEntity(entity);

    }

    private void onUseEntity(Entity entity){
        //TODO 盔甲都可以触发
        LinkedHashMap<Integer,Item> items = new LinkedHashMap<>();
        if(entity instanceof EntityHuman){
            int index = 0;
            for(Item armor: ((EntityHuman) entity).getInventory().getArmorContents()){
                if(armor.getId() != 0){
                    items.put(((EntityHuman) entity).getInventory().getSize() + index,armor);
                }
                index++;
            }
            items.put(((EntityHuman) entity).getInventory().getHeldItemIndex(),((EntityHuman) entity).getInventory().getItemInHand());
            for(Map.Entry<Integer,Item> entry: items.entrySet()){
                Item item = entry.getValue();
                if(item.hasCompoundTag() && item.getNamedTag().contains(CustomTagData.TAG)){
                    tagController.onUseItemInventory(this,entry.getKey(),item, CommandCollect.Trigger.DAMAGE,entity);
                }
            }
            for(Map.Entry<Integer,Item> entry: ((EntityHuman) entity).getOffhandInventory().slots.entrySet()){
                Item item = entry.getValue();
                if(item.hasCompoundTag() && item.getNamedTag().contains(CustomTagData.TAG)){
                    tagController.onUseItemOffhand(this,entry.getKey(),item, CommandCollect.Trigger.DAMAGE,entity);
                }
            }

        }

    }


}
