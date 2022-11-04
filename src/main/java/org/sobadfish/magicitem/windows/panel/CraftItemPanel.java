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
        if(canPlaceItem.size() > 0){
            //TODO 扔地上
            if(getInItem().size() > 0){
                backPlayer();
            }

        }
        clearAll();
        isInit = true;
        super.setPanel(panel);
    }

    public Map<Integer, Item> getOutItem(){
        return getIntegerItemMap(outPutItem);
    }

    private Map<Integer, Item> getIntegerItemMap(List<Integer> outPutItem) {
        Map<Integer, Item> itemMap = new LinkedHashMap<>();
        for (int i = 0; i < outPutItem.size(); i++) {
            Item it = this.getItem(outPutItem.get(i));
            if(it.getId() != 0){
                itemMap.put(i, this.getItem(outPutItem.get(i)));
            }
        }
        return itemMap;
    }

    public Map<Integer,Item> getInItem(){
        return getIntegerItemMap(canPlaceItem);
    }

    public void backPlayer(){
        Map<Integer, Item> i2 = getInItem();
        if(i2.size() > 0 && i2.get(0) != null) {
            for (Item it : i2.values()) {
                if (player.getInventory().canAddItem(it)) {
                    player.getInventory().addItem(it);
                } else {
                    player.getLevel().dropItem(player, it);
                }
            }
        }
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        super.onSlotChange(index, before, send);
        MagicItemMainClass.mainClass.getServer().getScheduler().scheduleTask(MagicItemMainClass.mainClass
                ,new PanelRunnable(this,index,before));


    }

    @Override
    public void onClose(Player who) {
        backPlayer();
        super.onClose(who);
    }

    public static class PanelRunnable implements Runnable{

        private final CraftItemPanel panel;
        private final int index;
        private final Item before;
        PanelRunnable(CraftItemPanel panel, int index, Item before){
            this.panel = panel;
            this.index = index;
            this.before = before;
        }
        @Override
        public void run() {
            if(!panel.isCraft) {
                //TODO 合成配方
                if (panel.isInit) {
                    if (panel.canPlaceItem.size() > 0) {
                        if(panel.getInItem().size() == 0){
                            panel.cacheOutPut = false;
                        }
                        Map<Integer, Item> outItemMap = panel.getOutItem();

                        if(outItemMap.size() == 0) {
                            Item[] out = MagicItemMainClass.mainClass.getMagicController().recipeController.craftItem
                                    (panel.getInItem(), MagicItemMainClass.mainClass.getMagicController());
                            if (out.length == 0 || out.length > 1 && out[0] == null) {
                                resetOut(out);
                                panel.cacheOutPut = false;
                                return;
                            }
                        }
                        if(outItemMap.size() > 0){
                            if(panel.canPlaceItem.contains(index)){
                                Item[] out = MagicItemMainClass.mainClass.getMagicController().recipeController.craftItem
                                        (panel.getInItem(), MagicItemMainClass.mainClass.getMagicController());
                                resetOut(out);
                            }
                            if(panel.outPutItem.contains(index)){
                                if(!panel.cacheOutPut){
                                    putInput();
                                    panel.cacheOutPut = true;

                                }

                            }
                            panel.sendContents(panel.getViewers());
                            return;
                        }else{
                            panel.cacheOutPut = false;
                        }

                        Map<Integer, Item> itemMap = panel.getInItem();
                        Item[] out = MagicItemMainClass.mainClass.getMagicController().recipeController.craftItem
                                (itemMap, MagicItemMainClass.mainClass.getMagicController());
                        if (out.length > 0 && out[0] != null) {
                            if (panel.outPutItem.contains(index) && before != null && before.getId() != 0) {
                                if(panel.getItem(index).getId()==  0){
                                    if(out.length == 1){
                                        putInput();
                                    }
                                    out = MagicItemMainClass.mainClass.getMagicController().recipeController.craftItem
                                            (panel.getInItem(), MagicItemMainClass.mainClass.getMagicController());
                                    resetOut(out);
                                    panel.sendContents(panel.getViewers());
                                }
                            } else {
                                for (int oi = 0; oi < panel.outPutItem.size(); oi++) {
                                    if(out.length > oi){
                                        panel.slots.put(panel.outPutItem.get(oi), out[oi]);
                                    }
                                }
                            }
                        } else {
                            for (Integer i : panel.outPutItem) {
                                panel.slots.put(i, Item.get(0));
                            }

                        }
                    }
                    panel.sendContents(panel.player);
                }
            }
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
            panel.sendContents(panel.player);
        }

        private void resetOut(Item[] out) {
            for (int oi = 0; oi < panel.outPutItem.size(); oi++) {
                if (out.length > oi) {
                    panel.slots.put(panel.outPutItem.get(oi), out[oi]);
                }else{
                    panel.slots.put(panel.outPutItem.get(oi),Item.get(0));
                }
            }
            panel.sendContents(panel.player);
        }
    }

}
