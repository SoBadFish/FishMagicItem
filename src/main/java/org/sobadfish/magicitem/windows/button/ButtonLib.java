package org.sobadfish.magicitem.windows.button;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.magicitem.controller.ChestPanelController;
import org.sobadfish.magicitem.windows.WindowFrom;
import org.sobadfish.magicitem.windows.items.BasePlayPanelItemInstance;
import org.sobadfish.magicitem.windows.lib.ChestInventoryPanel;
import org.sobadfish.magicitem.windows.panel.CraftItemPanel;

/**
 * @author Sobadfish
 * @date 2022/11/14
 */
public class ButtonLib extends BasePlayPanelItemInstance {

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Item getItem() {
        return Item.get(403);
    }

    @Override
    public void onClick(ChestInventoryPanel inventory, Player player) {
        // 先移除旧的缓存，强制刷新
        ChestPanelController.itemPage.remove(player.getName());
        if (inventory instanceof CraftItemPanel craftPanel) {
            // 关键修复：在切换到配方列表前，先退还玩家可能放在合成栏里的物品
            craftPanel.backPlayer();

            // 然后清除合成栏定义，防止在配方列表界面误触导致退还列表里的展示物品
            ChestPanelController.clearCraftPanelData(craftPanel);
        }
        inventory.setPanel(ChestPanelController.recipeListLib(player));
        inventory.sendContents(player);
    }

    @Override
    public void onClickButton(Player player, WindowFrom from) {

    }

    @Override
    public Item getPanelItem(Player info, int index) {
        Item item = defaultButtonTagItem(getItem(),index);
        item.setCustomName(TextFormat.colorize('&',"&r&e配方列表"));
        return item;
    }

    @Override
    public ElementButton getFromButton(Player info) {
        return null;
    }
}
