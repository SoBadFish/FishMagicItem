package org.sobadfish.magicitem.windows.panel;

import cn.nukkit.Player;
import cn.nukkit.inventory.InventoryHolder;
import org.sobadfish.magicitem.windows.lib.ChestInventoryPanel;

/**
 * @author Sobadfish
 * @date 2022/11/4
 */
public class ItemListPanel extends ChestInventoryPanel {

    public ItemListPanel(Player player, InventoryHolder holder, String name) {
        super(player, holder, name);
    }
}
