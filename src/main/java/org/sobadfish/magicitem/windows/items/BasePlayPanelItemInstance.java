package org.sobadfish.magicitem.windows.items;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import org.sobadfish.magicitem.windows.WindowFrom;
import org.sobadfish.magicitem.windows.lib.ChestInventoryPanel;


/**
 * @author Sobadfish
 * @date 2022/9/9
 */
public abstract class BasePlayPanelItemInstance {

    /**
     * 消费数量
     * @return 数量
     * */
    public abstract int getCount();
    /**
     * 游戏内物品
     * @return 物品
     * */
    public abstract Item getItem();
    /**
     * 当玩家触发
     *
     * @param inventory 商店
     * @param player 玩家
     *
     * */
    public abstract void onClick(ChestInventoryPanel inventory, Player player);
    /**
     * 当玩家触发GUI的button
     *
     * @param player 玩家
     * @param from 按键GUI
     *
     * */
    public abstract void onClickButton(Player player, WindowFrom from);

    /**
     * 箱子菜单展示物品
     * @param index 位置
     * @param info 玩家信息
     * @return 物品
     *
     * */
    public abstract Item getPanelItem(Player info, int index);

    /**
     * GUI按键button
     * @param info 玩家信息
     * @return 物品
     *
     * */
    public abstract ElementButton getFromButton(Player info);


    @Override
    public String toString() {
        return getItem()+" count: "+getCount();
    }

    /**
     * 生成默认物品
     * */
    public Item defaultButtonTagItem(Item item,int index){

        CompoundTag tag = item.getNamedTag();
        if(tag == null){
            tag = new CompoundTag();
        }
        tag.putInt("index",index);
        tag.putBoolean("button",true);
        item.setNamedTag(tag);
        item.setCount(getCount());
        return item;
    }


}
