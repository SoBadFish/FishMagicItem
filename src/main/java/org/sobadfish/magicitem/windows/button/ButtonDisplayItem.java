package org.sobadfish.magicitem.windows.button;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.item.Item;
import org.sobadfish.magicitem.MagicItemMainClass;
import org.sobadfish.magicitem.controller.MagicController;
import org.sobadfish.magicitem.files.entity.Recipe;
import org.sobadfish.magicitem.windows.WindowFrom;
import org.sobadfish.magicitem.windows.items.BasePlayPanelItemInstance;
import org.sobadfish.magicitem.windows.lib.ChestInventoryPanel;

import java.util.List;

/**
 * @author Sobadfish
 * @date 2022/11/4
 */
public class ButtonDisplayItem extends BasePlayPanelItemInstance {

    private final Item item;

    public ButtonDisplayItem(Item item){
        this.item = item;
    }

    @Override
    public int getCount() {
        return item.getCount();
    }

    @Override
    public Item getItem() {
        return item;
    }

    @Override
    public void onClick(ChestInventoryPanel inventory, Player player) {
        MagicController controller = MagicItemMainClass.mainClass.getMagicController();
        List<Recipe> recipes = controller.recipeController.getRecipesByItem(item);
        //TODO 跳转配方界面
        //TODO 没有就不跳转

    }

    @Override
    public void onClickButton(Player player, WindowFrom from) {

    }

    @Override
    public Item getPanelItem(Player info, int index) {
        return defaultButtonTagItem(item,index);
    }

    @Override
    public ElementButton getFromButton(Player info) {
        return null;
    }
}
