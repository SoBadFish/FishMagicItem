package org.sobadfish.magicitem.windows.button;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArrow;
import org.sobadfish.magicitem.controller.ChestPanelController;
import org.sobadfish.magicitem.windows.WindowFrom;
import org.sobadfish.magicitem.windows.items.BasePlayPanelItemInstance;
import org.sobadfish.magicitem.windows.lib.ChestInventoryPanel;

/**
 * @author Sobadfish
 * @date 2022/11/4
 */
public class ButtonPage2 extends BasePlayPanelItemInstance {

    public int count;

    public ButtonPage2(int page){
        this.count = page;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Item getItem() {
        return new ItemArrow();
    }

    @Override
    public void onClick(ChestInventoryPanel inventory, Player player) {
        inventory.clearAll();
        if(ChestPanelController.recipePage.containsKey(player)){
            ChestPanelController.PlayerRecipePage itemPage = ChestPanelController.recipePage.get(player);
            itemPage.page = this.count;
            inventory.setPanel(ChestPanelController.recipeLib(player,itemPage.item));
        }

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
