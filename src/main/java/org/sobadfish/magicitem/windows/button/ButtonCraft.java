package org.sobadfish.magicitem.windows.button;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.magicitem.MagicItemMainClass;
import org.sobadfish.magicitem.controller.MagicController;
import org.sobadfish.magicitem.controller.ChestPanelController;
import org.sobadfish.magicitem.windows.WindowFrom;
import org.sobadfish.magicitem.windows.items.BasePlayPanelItemInstance;
import org.sobadfish.magicitem.windows.lib.ChestInventoryPanel;
import org.sobadfish.magicitem.windows.panel.CraftItemPanel;

import java.util.Map;

/**
 * @author Sobadfish
 * @date 2022/10/29
 */
public class ButtonCraft extends BasePlayPanelItemInstance {

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
        if (inventory instanceof CraftItemPanel) {
            // Fix: 使用 getRawInItem() 获取真实的槽位 ID 映射，而不是标准化的 0-8 映射
            // 因为 RecipeController.addCraft 需要真实的槽位 ID (如 10, 11...) 来推断是 PC 还是 Mobile 布局，并正确生成 3x3 网格
            Map<Integer, Item> input = ((CraftItemPanel) inventory).getRawInItem();
            Item[] out = ((CraftItemPanel) inventory).getOutItem().values().toArray(new Item[0]);
            boolean isMobile = ChestPanelController.isMobile(player);
            MagicItemMainClass.mainClass.getMagicController().recipeController.addCraft(input, out, MagicItemMainClass.mainClass.getMagicController().tagController, isMobile);
            player.level.addSound(player, Sound.RANDOM_ORB);

            // 保存完成后，手动清空面板并退还玩家（因为我们取消了全局的自动退还）
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
        item.setCustomName(TextFormat.colorize('&', "&r&e点击保存配方"));
        return item;
    }

    @Override
    public ElementButton getFromButton(Player info) {
        return null;
    }
}
