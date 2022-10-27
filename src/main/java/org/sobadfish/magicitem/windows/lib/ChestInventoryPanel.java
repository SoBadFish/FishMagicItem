package org.sobadfish.magicitem.windows.lib;

import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import org.sobadfish.magicitem.windows.items.BasePlayPanelItemInstance;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BadFish
 */
public class ChestInventoryPanel extends DoubleChestFakeInventory implements InventoryHolder {

    public long id;

    private final Player player;

    public List<Integer> canPlaceItem = new ArrayList<>();

    public int clickSolt;

    private Map<Integer, BasePlayPanelItemInstance> panel = new LinkedHashMap<>();

    public ChestInventoryPanel(Player player, InventoryHolder holder, String name) {
        super(holder);
        this.player = player;
        this.setName(name);
    }

    public void setPanel(Map<Integer, BasePlayPanelItemInstance> panel){
        Map<Integer, BasePlayPanelItemInstance> m = new LinkedHashMap<>();
        LinkedHashMap<Integer, Item> map = new LinkedHashMap<>();
        for(Map.Entry<Integer,BasePlayPanelItemInstance> entry : panel.entrySet()){
            Item value = entry.getValue().getPanelItem(getPlayer(),entry.getKey()).clone();
            map.put(entry.getKey(),value);
            m.put(entry.getKey(),entry.getValue());
        }
        setContents(map);
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

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        super.onSlotChange(index, before, send);
        //TODO 合成配方
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
