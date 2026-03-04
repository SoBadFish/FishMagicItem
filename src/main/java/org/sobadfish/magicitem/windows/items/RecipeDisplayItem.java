package org.sobadfish.magicitem.windows.items;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.item.Item;
import org.sobadfish.magicitem.MagicItemMainClass;
import org.sobadfish.magicitem.files.entity.Recipe;
import org.sobadfish.magicitem.windows.WindowFrom;
import org.sobadfish.magicitem.windows.lib.ChestInventoryPanel;

/**
 * 配方展示物品
 */
public class RecipeDisplayItem extends BasePlayPanelItemInstance {

    private final Recipe recipe;

    public RecipeDisplayItem(Recipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Item getItem() {
        if (recipe.outputItem != null && recipe.outputItem.length > 0) {
            String out = recipe.outputItem[0];
            // 简单的解析，或者使用 TagController 解析
            // 假设格式为 "id:damage:count" 或 "id:damage"
            return MagicItemMainClass.mainClass.getMagicController().tagController
                    .getTagData().asItem(out).clone();
//            String[] split = out.split(":");
//            int id = Integer.parseInt(split[0]);
//            int damage = 0;
//            int count = 1;
//            if (split.length > 1) {
//                damage = Integer.parseInt(split[1]);
//            }
//            if (split.length > 2) {
//                count = Integer.parseInt(split[2]);
//            }
//            return Item.get(id, damage, count);
        }
        return Item.get(Item.AIR);
    }

    public Recipe getRecipe() {
        return recipe;
    }
    @Override
    public void onClick(ChestInventoryPanel inventory, Player player) {
        // 点击配方，可以显示详情，或者尝试自动填充
        // 目前暂不实现复杂逻辑，只是防止被拿取
    }

    @Override
    public void onClickButton(Player player, WindowFrom from) {
        // GUI 按钮逻辑，暂不需要
    }

    @Override
    public Item getPanelItem(Player info, int index) {
        Item item = getItem();
        item.setCustomName("§r§e" + item.getName());
        
        java.util.List<String> lore = new java.util.ArrayList<>();
        lore.add("§r§7材料:");
        if (recipe.inputItem != null) {
            for (String input : recipe.inputItem.values()) {
                Item inputItem = MagicItemMainClass.mainClass.getMagicController().tagController.getTagData().asItem(input);
                if (inputItem != null && inputItem.getId() != 0) {
                     lore.add("§r§f- " + inputItem.getName() + " x" + inputItem.getCount());
                } else {
                     lore.add("§r§f- " + input);
                }
            }
        }
        lore.add("§r§7产出:");
        if (recipe.outputItem != null) {
            for (String output : recipe.outputItem) {
                 Item outputItem = MagicItemMainClass.mainClass.getMagicController().tagController.getTagData().asItem(output);
                 if (outputItem != null && outputItem.getId() != 0) {
                     lore.add("§r§f- " + outputItem.getName() + " x" + outputItem.getCount());
                 } else {
                     lore.add("§r§f- " + output);
                 }
            }
        }
        item.setLore(lore.toArray(new String[0]));
        
        return defaultButtonTagItem(item, index);
    }

    @Override
    public ElementButton getFromButton(Player info) {
        return new ElementButton(getItem().getName());
    }
}
