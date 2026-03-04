package org.sobadfish.magicitem.windows.button;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.magicitem.MagicItemMainClass;
import org.sobadfish.magicitem.controller.ChestPanelController;
import org.sobadfish.magicitem.controller.MagicController;
import org.sobadfish.magicitem.windows.WindowFrom;
import org.sobadfish.magicitem.windows.items.BasePlayPanelItemInstance;
import org.sobadfish.magicitem.windows.lib.ChestInventoryPanel;
import org.sobadfish.magicitem.windows.panel.CraftItemPanel;

import java.util.Map;

public class ButtonRecipeBookSave extends BasePlayPanelItemInstance {

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Item getItem() {
        return Item.get(386);
    }

    @Override
    public void onClick(ChestInventoryPanel inventory, Player player) {
        if (inventory instanceof CraftItemPanel) {
            Map<Integer, Item> input = ((CraftItemPanel) inventory).getRawInItem();
            Item[] out = ((CraftItemPanel) inventory).getOutItem().values().toArray(new Item[0]);
            boolean isMobile = ChestPanelController.isMobile(player);
            MagicItemMainClass.mainClass.getMagicController().recipeController.addCraft(input, out, MagicItemMainClass.mainClass.getMagicController().tagController);
            player.level.addSound(player, Sound.RANDOM_ORB);
            ((CraftItemPanel) inventory).backPlayer();
            inventory.update();
            MagicController.sendMessageToObject("&a添加完成", player);
        }
    }

    @Override
    public void onClickButton(Player player, WindowFrom from) {
    }

    @Override
    public Item getPanelItem(Player info, int index) {
        Item item = defaultButtonTagItem(getItem(), index);
        item.setCustomName(TextFormat.colorize('&', "&r&e配方书&7(点击保存)"));
        return item;
    }

    @Override
    public ElementButton getFromButton(Player info) {
        return null;
    }
}

