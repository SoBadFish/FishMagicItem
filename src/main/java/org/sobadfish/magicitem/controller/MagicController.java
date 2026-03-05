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
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.magicitem.MagicItemMainClass;
import org.sobadfish.magicitem.files.datas.CustomTagData;
import org.sobadfish.magicitem.files.entity.CommandCollect;
import org.sobadfish.magicitem.windows.items.BasePlayPanelItemInstance;
import org.sobadfish.magicitem.windows.lib.AbstractFakeInventory;
import org.sobadfish.magicitem.windows.lib.ChestInventoryPanel;
import org.sobadfish.magicitem.windows.panel.CraftItemPanel;
import org.sobadfish.magicitem.windows.panel.ItemListPanel;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 控制器
 *
 * @author Sobadfish
 * @date 2022/10/25
 */
public class MagicController implements Listener {

    /**
     * 配方管理
     *
     */
    public RecipeController recipeController;

    /**
     * 标签物品管理
     *
     */
    public TagController tagController;

    /**
     * 指令集管理
     *
     */
    public CommandController commandCollect;

    /**
     * 语言文件管理
     *
     */
    public LanguageController languageController;

    private final Plugin plugin;

    public Map<String, Long> coolTime = new LinkedHashMap<>();

    public MagicController(Plugin plugin) {
        this.plugin = plugin;
        checkServer();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        MagicController.sendLogger("&a加载指令集中...");
        File d = new File(getDataFolder() + "/command");
        if (!d.exists()) {
            if (!d.mkdirs()) {
                sendLogger("创建command文件夹失败");
            }
        }
        commandCollect = CommandController.initCommand();

        MagicController.sendLogger("&a加载物品中...");
        d = new File(getDataFolder() + "/items");
        if (!d.exists()) {
            if (!d.mkdirs()) {
                sendLogger("创建items文件夹失败");
            }
        }
        this.tagController = TagController.initTag();
        this.languageController = new LanguageController(this, plugin.getConfig());
        MagicController.sendLogger("&a加载配方中...");
        this.recipeController = RecipeController.initRecipe(this);

    }

    public Plugin getPlugin() {
        return plugin;
    }

    public static File getDataFolder() {
        return MagicItemMainClass.mainClass.getDataFolder();
    }

    public static String formatStr(String msg) {
        return TextFormat.colorize('&', msg);
    }

    public static void sendMessageToObject(String msg, CommandSender commandSender) {
        commandSender.sendMessage(formatStr(msg));
    }

    public static void saveResource(String filename, boolean replace) {
        MagicItemMainClass.mainClass.saveResource(filename, replace);
    }

    public static void saveResource(String filename, String outputFile, boolean replace) {
        MagicItemMainClass.mainClass.saveResource(filename, outputFile, replace);
    }

    public void save() {
        if (recipeController != null) {
            recipeController.save();
        }
        if (tagController != null) {
            tagController.save();
        }
        if (commandCollect != null) {
            commandCollect.save();
        }

    }

    public static void sendLogger(String msg) {
        MagicItemMainClass.mainClass.getLogger().info(formatStr(msg));
    }

    @EventHandler
    public void onItemChange(InventoryTransactionEvent event) {
        ChestInventoryPanel chest = null;
        InventoryTransaction transaction = event.getTransaction();

        // First pass: Find the chest and player involved
        for (Inventory inventory : transaction.getInventories()) {
            if (inventory instanceof ChestInventoryPanel) {
                chest = (ChestInventoryPanel) inventory;
            }
        }

        for (InventoryAction action : transaction.getActions()) {
            if (action instanceof SlotChangeAction slotAction) {

                // Only apply logic if the action is ON the chest inventory
                if (slotAction.getInventory() instanceof ChestInventoryPanel) {
                    ChestInventoryPanel currentChest = (ChestInventoryPanel) slotAction.getInventory();

                    // 1. Output Slot Protection (Prevent placing items into output)
                    if (currentChest instanceof CraftItemPanel) {
                        CraftItemPanel panel = (CraftItemPanel) currentChest;
                        if (!panel.isCraft && panel.lockInput && panel.inputItem.contains(slotAction.getSlot())) {
                            event.setCancelled();
                            panel.sendContents(panel.getPlayer());
                            return;
                        }
                        // Only block output slot interaction if we are NOT in creation mode
                        if (!panel.isCraft) {
                            int slot = slotAction.getSlot();
                            if (panel.outPutItem.contains(slot)) {
                                event.setCancelled();
                                MagicItemMainClass.mainClass.getServer().getScheduler().scheduleTask(MagicItemMainClass.mainClass, () -> {
                                    Player player = panel.getPlayer();
                                    Item now = panel.getItem(slot);
                                    if (now == null || now.getId() == 0) {
                                        panel.sendContents(player);
                                        return;
                                    }

                                    Item give = now.clone();
                                    if (give.hasCompoundTag()) {
                                        CompoundTag tag = give.getNamedTag();
                                        if (tag != null) {
                                            tag.remove("button");
                                            tag.remove("index");
                                            give.setNamedTag(tag);
                                        }
                                    }

                                    cn.nukkit.inventory.PlayerCursorInventory cursorInv = player.getCursorInventory();
                                    Item cursor = cursorInv.getItem(0);
                                    if (cursor == null) cursor = Item.get(0);
                                    boolean moved = false;
                                    if (cursor.getId() == 0) {
                                        cursorInv.setItem(0, give);
                                        cursorInv.sendContents(player);
                                        moved = true;
                                    } else if (cursor.equals(give, true, true)) {
                                        int max = cursor.getMaxStackSize();
                                        if (cursor.getCount() + give.getCount() <= max) {
                                            cursor.setCount(cursor.getCount() + give.getCount());
                                            cursorInv.setItem(0, cursor);
                                            cursorInv.sendContents(player);
                                            moved = true;
                                        }
                                    }
                                    if (!moved) {
                                        if (player.getInventory().canAddItem(give)) {
                                            player.getInventory().addItem(give);
                                            moved = true;
                                        } else {
                                            player.getLevel().dropItem(player, give);
                                            moved = true;
                                        }
                                    }

                                    panel.setItem(slot, Item.get(0));
                                    panel.hasTakenOutput = true;
                                    panel.lockInput = true;

                                    if (!panel.outputConsumed) {
                                        panel.outputConsumed = true;
                                        panel.consumeInput();
                                    }

                                    boolean anyOut = false;
                                    for (Integer s : panel.outPutItem) {
                                        Item it = panel.getItem(s);
                                        if (it != null && it.getId() != 0) {
                                            anyOut = true;
                                            break;
                                        }
                                    }
                                    if (!anyOut) {
                                        panel.lockInput = false;
                                        panel.outputConsumed = false;
                                        panel.hasTakenOutput = false;
                                        panel.checkRecipe();
                                    }

                                    panel.sendContents(player);
                                });
                                return;
                            }
                        }
                    }

                    // 2. Button Protection (Prevent taking buttons & Handle Clicks)
                    Item source = action.getSourceItem();
                    // 只有当源物品是按钮时才拦截。
                    // 现在的逻辑：如果玩家拿起一个非按钮物品（比如放入原材料），source 没有 button tag，不会被拦截。
                    // 如果玩家点击一个按钮（source 有 button tag），则被拦截。
                    if (source.hasCompoundTag() && source.getNamedTag().contains("index") && source.getNamedTag().contains("button")) {
                        event.setCancelled();
                        currentChest.sendContents(currentChest.getPlayer());

                        // Handle Click
                        int index = source.getNamedTag().getInt("index");
                        BasePlayPanelItemInstance item = currentChest.getPanel().getOrDefault(index, null);
                        if (item != null) {
                            currentChest.clickSolt = index;
                            item.onClick(currentChest, currentChest.getPlayer());
                        }
                        return;
                    }

                    if (!(currentChest instanceof CraftItemPanel)) {
                        if (event.isCancelled()) {
                            currentChest.sendContents(currentChest.getPlayer());
                        }
                    }
                }
                if(slotAction.getInventory() instanceof ItemListPanel){
                    //TODO 防止玩家往里面拖动物品
                    event.setCancelled();
                    return;
                }
            }

            // Legacy logic for Player Inventory updates and locks
            for (Inventory inventory : transaction.getInventories()) {
                if (inventory instanceof PlayerInventory) {
                    EntityHuman player = ((PlayerInventory) inventory).getHolder();
                    if (chest != null) {
                        if (player instanceof Player) {
                            chest.onUpdate((Player) player);
                        }
                    }
                    if (tagController.lock.contains(player.getName())) {
                        event.setCancelled();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Item item = player.getInventory().getItemInHand();

        if (tagController.lock.contains(player.getName())) {
            event.setCancelled();
            return;
        }
        CommandCollect.Trigger trigger = null;
        if (event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_AIR
                || event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            trigger = CommandCollect.Trigger.LEFT_CLICK;
        }
        if (event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR
                || event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            trigger = CommandCollect.Trigger.RIGHT_CLICK;
        }

        if (trigger != null) {
            if (item.hasCompoundTag() && item.getNamedTag().contains(CustomTagData.TAG)) {
                tagController.onUseItemInventory(this, player.getInventory().getHeldItemIndex(), item, trigger, player);
            }
            for (Map.Entry<Integer, Item> entry : player.getOffhandInventory().getContents().entrySet()) {
                Item item2 = entry.getValue();
                if (item2.hasCompoundTag() && item2.getNamedTag().contains(CustomTagData.TAG)) {
                    tagController.onUseItemOffhand(this, entry.getKey(), item2, trigger, player);
                }
            }
        }

    }

    /**
     * TODO 触发事件
     */
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (event instanceof EntityDamageByEntityEvent) {
            Entity entity1 = ((EntityDamageByEntityEvent) event).getDamager();
            onUseEntity(entity1);
            return;
        }
        onUseEntity(entity);

    }

    private void onUseEntity(Entity entity) {
        //TODO 盔甲都可以触发
        LinkedHashMap<Integer, Item> items = new LinkedHashMap<>();
        if (entity instanceof EntityHuman) {
            int index = 0;
            for (Item armor : ((EntityHuman) entity).getInventory().getArmorContents()) {
                if (armor.getId() != 0) {
                    items.put(((EntityHuman) entity).getInventory().getSize() + index, armor);
                }
                index++;
            }
            items.put(((EntityHuman) entity).getInventory().getHeldItemIndex(), ((EntityHuman) entity).getInventory().getItemInHand());
            for (Map.Entry<Integer, Item> entry : items.entrySet()) {
                Item item = entry.getValue();
                if (item.hasCompoundTag() && item.getNamedTag().contains(CustomTagData.TAG)) {
                    tagController.onUseItemInventory(this, entry.getKey(), item, CommandCollect.Trigger.DAMAGE, entity);
                }
            }
            for (Map.Entry<Integer, Item> entry : ((EntityHuman) entity).getOffhandInventory().slots.entrySet()) {
                Item item = entry.getValue();
                if (item.hasCompoundTag() && item.getNamedTag().contains(CustomTagData.TAG)) {
                    tagController.onUseItemOffhand(this, entry.getKey(), item, CommandCollect.Trigger.DAMAGE, entity);
                }
            }

        }

    }

    private void checkServer() {
        boolean ver = false;
        //双核心兼容
        try {
            Class<?> c = Class.forName("cn.nukkit.Nukkit");
            c.getField("NUKKIT_PM1E");
            ver = true;

        } catch (ClassNotFoundException | NoSuchFieldException ignore) {
        }
        try {
            Class<?> c = Class.forName("cn.nukkit.Nukkit");
            c.getField("NUKKIT").get(c).toString().equalsIgnoreCase("Nukkit PetteriM1 Edition");
            ver = true;
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignore) {
        }

        AbstractFakeInventory.IS_PM1E = ver;
        if (ver) {
            sendLogger("&e当前核心为 Nukkit PM1E");
        } else {
            sendLogger("&e当前核心为 Nukkit");
        }
    }

}
