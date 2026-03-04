package org.sobadfish.magicitem.windows.button;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.magicitem.MagicItemMainClass;
import org.sobadfish.magicitem.controller.ChestPanelController;
import org.sobadfish.magicitem.files.datas.RecipeData;
import org.sobadfish.magicitem.files.entity.Recipe;
import org.sobadfish.magicitem.windows.WindowFrom;
import org.sobadfish.magicitem.windows.items.BasePlayPanelItemInstance;
import org.sobadfish.magicitem.windows.lib.ChestInventoryPanel;
import org.sobadfish.magicitem.windows.panel.CraftItemPanel;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 自动填充并合成按钮
 */
public class ButtonFillCraft extends BasePlayPanelItemInstance {

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Item getItem() {
        Item item = Item.get(58);
        item.setCustomName(TextFormat.GREEN + "返回合成台");
        return item;
    }

    @Override
    public void onClick(ChestInventoryPanel inventory, Player player) {
        if (!(inventory instanceof CraftItemPanel)) {
            return;
        }
        CraftItemPanel panel = (CraftItemPanel) inventory;

        ChestPanelController.PlayerRecipePage page = ChestPanelController.recipePage.get(player.getName());
        if (page == null) {
            return;
        }


        if (page.recipe.originRecipes == null || page.recipe.originRecipes.size() < page.page) {
             return;
        }
        
        Recipe cleanRecipe = page.recipe.originRecipes.get(page.page - 1);
        Map<Integer, Item> requiredGrid = cleanRecipe.getInputGrid(MagicItemMainClass.mainClass.getMagicController().tagController);

        if (requiredGrid == null || requiredGrid.isEmpty()) {
            return;
        }

        Inventory playerInv = player.getInventory();
        Map<Integer, Item> toMove = new HashMap<>();

        boolean hasAll = true;

        Map<String, Integer> totalNeeded = new HashMap<>();
        Map<String, Item> prototypeItems = new HashMap<>();

        for (Map.Entry<Integer, Item> entry : requiredGrid.entrySet()) {
            Item displayItem = entry.getValue();
            // 直接使用配方定义的物品
            Item realItem = displayItem.clone();

            Item item = realItem;
            String key = getItemKey(item);
            totalNeeded.put(key, totalNeeded.getOrDefault(key, 0) + item.getCount());
            prototypeItems.putIfAbsent(key, item);
        }

        Map<String, Integer> totalAvailable = new HashMap<>();
        for (Item item : playerInv.getContents().values()) {
            for (String key : prototypeItems.keySet()) {
                Item prototype = prototypeItems.get(key);
                boolean checkTag = prototype.hasCompoundTag();
                if (checkTag && prototype.getNamedTag().isEmpty()) checkTag = false;

                if (prototype.equals(item, true, checkTag)) {
                    totalAvailable.put(key, totalAvailable.getOrDefault(key, 0) + item.getCount());
                    break;
                }
            }
        }

        int minFactor = Integer.MAX_VALUE;
        for (String key : totalNeeded.keySet()) {
            int needed = totalNeeded.get(key);
            int available = totalAvailable.getOrDefault(key, 0);
            if (available < needed) {
                hasAll = false;
            }
            if (needed > 0) {
                int factor = available / needed;
                if (factor < minFactor) minFactor = factor;
            }
        }

        if (minFactor == Integer.MAX_VALUE) minFactor = 0;
        if (minFactor > 64) minFactor = 64;

        int fillFactor = (minFactor > 0) ? minFactor : 1;

        for (Map.Entry<Integer, Item> entry : requiredGrid.entrySet()) {
            int gridIndex = entry.getKey();
            Item requiredItem = entry.getValue();

            // 直接使用配方定义的物品
            Item realItem = requiredItem.clone();
            
            int amountToPlace = realItem.getCount() * fillFactor;

            if (amountToPlace > 64) amountToPlace = 64;

            Item itemToPlace = realItem.clone();
            itemToPlace.setCount(amountToPlace);
            toMove.put(gridIndex, itemToPlace);
        }

        if (!hasAll) {
            player.sendMessage(TextFormat.RED + "材料不足，已尽可能填充！");
        }

        ChestPanelController.clearCraftPanelData(panel);
        panel.setPanel(ChestPanelController.createMenu(panel, player));

        boolean isMobile = ChestPanelController.isMobile(player);
        int[] targetSlots;
        if (isMobile) {
            targetSlots = new int[]{7, 8, 9, 13, 14, 15, 19, 20, 21};
        } else {
            targetSlots = new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30};
        }

        for (Map.Entry<Integer, Item> entry : toMove.entrySet()) {
            int gridIndex = entry.getKey();
            Item itemToPlace = entry.getValue();
            
            // 不需要再做任何 Tag 清理，因为从 originRecipes 获取的物品本身就是干净的
            // 且已经在 RecipeData 中通过 .clone() 隔离了缓存
            
            int needed = itemToPlace.getCount();
            Inventory inv = player.getInventory();

            boolean checkTag = itemToPlace.hasCompoundTag();
            if (checkTag && itemToPlace.getNamedTag().isEmpty()) {
                checkTag = false;
            }

            int removed = 0;
            for (Map.Entry<Integer, Item> invEntry : inv.getContents().entrySet()) {
                if (removed >= needed) break;
                Item i = invEntry.getValue();
                if (itemToPlace.equals(i, true, checkTag)) {
                    int take = Math.min(i.getCount(), needed - removed);
                    i.setCount(i.getCount() - take);
                    if (i.getCount() <= 0) {
                        inv.clear(invEntry.getKey());
                    } else {
                        inv.setItem(invEntry.getKey(), i);
                    }
                    removed += take;
                }
            }

            if (removed > 0) {
                itemToPlace.setCount(removed);
                if (gridIndex >= 0 && gridIndex < targetSlots.length) {
                    panel.getInventory().setItem(targetSlots[gridIndex], itemToPlace);
                }
            }
        }

        panel.checkRecipe();

        inventory.sendContents(player);
        if (hasAll) {
            player.sendMessage(TextFormat.GREEN + "材料已填充！");
        }
    }

    private String getItemKey(Item item) {
        boolean hasTag = item.hasCompoundTag();
        if (hasTag && item.getNamedTag().isEmpty()) hasTag = false;
        return item.getId() + ":" + item.getDamage() + ":" + (hasTag ? item.getNamedTag().toString() : "");
    }

    @Override
    public void onClickButton(Player player, WindowFrom from) {

    }

    @Override
    public Item getPanelItem(Player info, int index) {
        return defaultButtonTagItem(getItem(), index);
    }

    @Override
    public ElementButton getFromButton(Player info) {
        return null;
    }
}
