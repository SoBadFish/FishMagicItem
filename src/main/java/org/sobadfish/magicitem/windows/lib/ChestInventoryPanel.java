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
        Map<Integer, Item> itemMap = new LinkedHashMap<>();
        for (int i = 0; i < canPlaceItem.size(); i++) {
            Item it = this.getItem(canPlaceItem.get(i));
            if(it.getId() != 0){
                itemMap.put(i, this.getItem(canPlaceItem.get(i)));
            }
        }
        return itemMap;
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
        if(!isCraft) {
            //TODO 合成配方
            if (isInit) {
                if (canPlaceItem.size() > 0) {
                    Map<Integer, Item> itemMap = getInItem();
                    Item[] out = MagicItemMainClass.mainClass.getMagicController().recipeController.craftItem
                            (itemMap, MagicItemMainClass.mainClass.getMagicController());
                    if (out.length > 0 && out[0] != null) {

                        if (outPutItem.contains(index) && before != null && before.getId() != 0) {
                            for (Integer integer : canPlaceItem) {
                                Item ii = this.getItem(integer);
                                if (ii.getId() > 0) {
                                    if (ii.getCount() > 1) {
                                        ii.setCount(ii.getCount() - 1);
                                    } else {
                                        ii = Item.get(0);
                                    }
                                    this.setItem(integer, ii);
                                }
                            }

                        } else {
                            for (int oi = 0; oi < outPutItem.size(); oi++) {
                                if(out.length > oi){
                                    this.slots.put(outPutItem.get(oi), out[oi]);
                                }
                            }
                        }
                    } else {
                        for (Integer i : outPutItem) {
                            this.slots.put(i, Item.get(0));
                        }

                    }
                }
                //
                this.sendContents(player);
            }
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
