package org.sobadfish.magicitem.windows.button;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.item.Item;
import org.sobadfish.magicitem.windows.WindowFrom;
import org.sobadfish.magicitem.windows.items.BasePlayPanelItemInstance;
import org.sobadfish.magicitem.windows.lib.ChestInventoryPanel;

/**
 * @author Sobadfish
 * @date 2022/10/29
 */
public class ButtonWall1 extends BasePlayPanelItemInstance {
    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Item getItem() {
        return Item.get(95);
    }

    @Override
    public void onClick(ChestInventoryPanel inventory, Player player) {

    }

    @Override
    public void onClickButton(Player player, WindowFrom from) {

    }

    @Override
    public Item getPanelItem(Player info, int index) {
        return defaultButtonTagItem(getItem(),index);
    }

    @Override
    public ElementButton getFromButton(Player info) {
        return null;
    }
}
