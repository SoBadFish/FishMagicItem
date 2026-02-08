package org.sobadfish.magicitem.windows.button;

import cn.nukkit.Player;
import cn.nukkit.block.BlockCraftingTable;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.magicitem.windows.WindowFrom;
import org.sobadfish.magicitem.windows.items.BasePlayPanelItemInstance;
import org.sobadfish.magicitem.controller.ChestPanelController;
import org.sobadfish.magicitem.windows.lib.ChestInventoryPanel;
import org.sobadfish.magicitem.windows.panel.CraftItemPanel;

/**
 * @author Sobadfish
 * @date 2022/10/29
 */
public class ButtonGoCraft extends BasePlayPanelItemInstance {

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Item getItem() {
        return new BlockCraftingTable().toItem();
    }

    @Override
    public void onClick(ChestInventoryPanel inventory, Player player) {
        // 切换到合成台界面
        if (inventory instanceof CraftItemPanel craftPanel) {
            craftPanel.setPanel(ChestPanelController.createMenu(craftPanel, player));
            craftPanel.sendContents(player);
        }
    }

    @Override
    public void onClickButton(Player player, WindowFrom from) {

    }

    @Override
    public Item getPanelItem(Player info, int index) {
        Item item = defaultButtonTagItem(getItem(), index);
        item.setCustomName(TextFormat.colorize('&', "&r&e合成台"));
        return item;
    }

    @Override
    public ElementButton getFromButton(Player info) {
        return null;
    }
}
