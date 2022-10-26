package org.sobadfish.magicitem.files.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import org.sobadfish.magicitem.controller.MagicController;

import java.util.Objects;

/**
 * 自定义物品
 * @author Sobadfish
 * @date 2022/10/26
 */
public class CustomTagItem {

    /**
     * 物品名称
     * */
    public String name;
    /**
     * 显示的名字
     * */
    public String nameTag = null;

    /**
     * 自定义显示的Lore
     * */
    public String[] lore = new String[0];

    /**
     * 物品外观
     * */
    public String item = "0";
    /**
     * 冷却时长
     * */
    public int coolTime = 0;

    /**
     * 是否被消耗
     * */
    public boolean canBeUse;

    /**
     * 执行的指令集
     * */
    public CommandCollect[] commandCollects = new CommandCollect[0];



    public static CustomTagItem asNameItem(String name){
        CustomTagItem customTagItem = new CustomTagItem();
        customTagItem.name = name;
        return customTagItem;
    }

    public boolean onUse(MagicController magicController, CommandCollect.Trigger trigger, Player player, Entity... entities){
        //是否在冷却
        if(magicController.coolTime.containsKey(name)){
            if(System.currentTimeMillis() - magicController.coolTime.get(name) < coolTime * 1000L){
                return false;
            }
        }
        magicController.coolTime.put(name,System.currentTimeMillis());
        for(CommandCollect collect: commandCollects){
            collect.activateCommand(trigger,player,entities);
        }
        return true;
    }


    public CommandCollect[] getCommandCollects() {
        return commandCollects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomTagItem)) {
            return false;
        }
        CustomTagItem that = (CustomTagItem) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
