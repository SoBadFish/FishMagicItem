package org.sobadfish.magicitem.windows.panel;

import cn.nukkit.Player;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import org.sobadfish.magicitem.MagicItemMainClass;
import org.sobadfish.magicitem.windows.items.BasePlayPanelItemInstance;
import org.sobadfish.magicitem.windows.lib.ChestInventoryPanel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sobadfish
 * @date 2022/11/1
 */
public class CraftItemPanel extends ChestInventoryPanel {

    //TODO 创建配方
    public boolean isCraft = false;

    public List<Integer> canPlaceItem = new ArrayList<>();

    public List<Integer> outPutItem = new ArrayList<>();

    public boolean cacheOutPut = false;

    public boolean isInit = false;

    public CraftItemPanel(Player player, InventoryHolder holder, String name) {
        super(player, holder, name);
    }

    @Override
    public void setPanel(Map<Integer, BasePlayPanelItemInstance> panel) {
        //如果是合成配方大于0
        if (canPlaceItem.size() > 0) {
            //TODO 扔地上
            if (getInItem().size() > 0) {
                backPlayer();
            }

        }
        clearAll();
        isInit = true;
        super.setPanel(panel);
    }

    public Map<Integer, Item> getOutItem() {
        return getIntegerItemMap(outPutItem);
    }

    private Map<Integer, Item> getIntegerItemMap(List<Integer> outPutItem) {
        Map<Integer, Item> itemMap = new LinkedHashMap<>();
        for (int i = 0; i < outPutItem.size(); i++) {
            Item it = this.getItem(outPutItem.get(i));
            if (it.getId() != 0) {
                itemMap.put(i, this.getItem(outPutItem.get(i)));
            }
        }
        return itemMap;
    }

    public Map<Integer, Item> getInItem() {
        return getIntegerItemMap(canPlaceItem);
    }
    
    /**
     * 获取原始的输入物品 Map (Key 是真实的槽位 ID，而不是 0-8 的索引)
     * 用于配方创建，需要知道真实的槽位分布来生成 3x3 矩阵
     */
    public Map<Integer, Item> getRawInItem() {
        for (int i: getContents().keySet()){
            System.out.println("当前可以获取的: slot: "+i+": "+getItem(i).getName());
        }
        Map<Integer, Item> itemMap = new LinkedHashMap<>();
        for (Integer slotId : canPlaceItem) {
            Item it = this.getItem(slotId);
            if (it != null && it.getId() != 0) {
                itemMap.put(slotId, it);
            }
        }
        return itemMap;
    }

    public void backPlayer(){
        // 改为直接遍历 canPlaceItem 列表获取真实槽位的物品
        for (Integer slot : canPlaceItem) {
            Item item = this.getItem(slot);
            if (item != null && item.getId() != 0) {
                if (player.getInventory().canAddItem(item)) {
                    player.getInventory().addItem(item);
                } else {
                    player.getLevel().dropItem(player, item);
                }
                // 清空槽位防止重复返还（虽然后面有关闭清理，但保险起见）
                this.slots.remove(slot);
            }
        }
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        super.onSlotChange(index, before, send);
        MagicItemMainClass.mainClass.getServer().getScheduler().scheduleTask(MagicItemMainClass.mainClass, 
                new PanelRunnable(this, index, before));

    }

    @Override
    public void onClose(Player who) {
        backPlayer();
        super.onClose(who);
    }

    public static class PanelRunnable implements Runnable {

        private final CraftItemPanel panel;
        private final int index;
        private final Item before;

        PanelRunnable(CraftItemPanel panel, int index, Item before) {
            this.panel = panel;
            this.index = index;
            this.before = before;
        }

        @Override
        public void run() {
            if (!panel.isCraft) {
                //TODO 合成配方
                if (panel.isInit) {
                    if (panel.canPlaceItem.size() > 0) {
                        // 输入发生变化，更新输出
                        if (panel.canPlaceItem.contains(index)) {
                            updateOutput();
                        }
                        // 输出发生变化（只能是取走物品，因为放入已被拦截）
                        if (panel.outPutItem.contains(index)) {
                            // 如果当前槽位为空，或者数量减少，说明被取走了
                            // 注意：before是变化前的物品，panel.getItem(index)是变化后的
                            Item current = panel.getItem(index);
                            boolean isTaken = false;
                            if (before != null && before.getId() != 0) {
                                if (current == null || current.getId() == 0 || current.getCount() < before.getCount()) {
                                    isTaken = true;
                                }
                            }

                            if (isTaken) {
                                consumeInput();
                                updateOutput();
                            } else {
                                // 如果不是取走（例如只是点击了一下，或者被系统重置），我们需要确保输出显示正确
                                // 这里可以强制刷新一下输出，防止显示错误
                                updateOutput();
                            }
                        }
                    }
                    panel.sendContents(panel.player);
                }
            }
        }

        private void updateOutput() {
            Map<Integer, Item> itemMap = panel.getInItem();
            Item[] out = MagicItemMainClass.mainClass.getMagicController().recipeController.craftItem(itemMap, MagicItemMainClass.mainClass.getMagicController());
            resetOut(out);
        }

        private void consumeInput() {
            putInput();
        }

        private void putInput() {
            for (Integer integer : panel.canPlaceItem) {
                Item ii = panel.getItem(integer);
                if (ii != null && ii.getId() > 0) {
                    if (ii.getCount() > 1) {
                        ii.setCount(ii.getCount() - 1);
                        panel.slots.put(integer, ii);
                    } else {
                        panel.slots.remove(integer);
                    }
                }
            }
            // 不需要 sendContents，因为是在 run() 最后统一发，或者 resetOut 里发
        }

        private void resetOut(Item[] out) {
            for (int oi = 0; oi < panel.outPutItem.size(); oi++) {
                if (out.length > oi) {
                    panel.slots.put(panel.outPutItem.get(oi), out[oi]);
                } else {
                    panel.slots.put(panel.outPutItem.get(oi), Item.get(0));
                }
            }
        }
    }

}
