package org.sobadfish.magicitem.windows.lib;

import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import org.sobadfish.magicitem.MagicItemMainClass;
import org.sobadfish.magicitem.windows.items.BasePlayPanelItemInstance;

import java.util.*;

/**
 * @author BadFish
 */
public class ChestInventoryPanel extends DoubleChestFakeInventory implements InventoryHolder {

    public long id;

    private final Player player;

    //TODO 创建配方
    public boolean isCraft = false;

    public List<Integer> canPlaceItem = new ArrayList<>();

    public List<Integer> outPutItem = new ArrayList<>();

    public int clickSolt;

    public boolean cacheOutPut = false;

    public boolean isInit = false;

    private Map<Integer, BasePlayPanelItemInstance> panel = new LinkedHashMap<>();

    public ChestInventoryPanel(Player player, InventoryHolder holder, String name) {
        super(holder);
        this.player = player;
        this.setName(name);
    }

    public void setPanel(Map<Integer, BasePlayPanelItemInstance> panel){
        //如果是合成配方大于0
        if(canPlaceItem.size() > 0){
           //TODO 扔地上
            if(getInItem().size() > 0){
                backPlayer();
            }

        }
        clearAll();
        Map<Integer, BasePlayPanelItemInstance> m = new LinkedHashMap<>();
        for(Map.Entry<Integer,BasePlayPanelItemInstance> entry : panel.entrySet()){
            Item value = entry.getValue().getPanelItem(getPlayer(),entry.getKey()).clone();
            m.put(entry.getKey(),entry.getValue());
            setItem(entry.getKey(),value);
        }

        isInit = true;
        this.panel = m;
    }

    public void update(){
        setPanel(panel);
    }


    public Player getPlayer(){
        return player;
    }


    public Map<Integer, BasePlayPanelItemInstance> getPanel() {
        return panel;
    }

    public Map<Integer,Item> getOutItem(){
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
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        super.onSlotChange(index, before, send);
        MagicItemMainClass.mainClass.getServer().getScheduler().scheduleTask(MagicItemMainClass.mainClass
                ,new PanelRunnable(this,index,before));


    }

    public static class PanelRunnable implements Runnable{

        private ChestInventoryPanel panel;
        private int index;
        private Item before;
        PanelRunnable(ChestInventoryPanel panel, int index, Item before){
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


    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        ContainerOpenPacket pk = new ContainerOpenPacket();
        pk.windowId = who.getWindowId(this);
        pk.entityId = id;
        pk.type = InventoryType.DOUBLE_CHEST.getNetworkType();
        who.dataPacket(pk);
    }

    @Override
    public void onClose(Player who) {
        RemoveEntityPacket pk = new RemoveEntityPacket();
        pk.eid = id;
        who.dataPacket(pk);
        super.onClose(who);

    }

    @Override
    public Inventory getInventory() {
        return this;
    }

    public void onUpdate(Player player){
        //TODO 界面刷新

    }



}
