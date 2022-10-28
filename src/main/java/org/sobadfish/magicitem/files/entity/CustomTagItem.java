package org.sobadfish.magicitem.files.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import org.sobadfish.magicitem.controller.MagicController;
import org.sobadfish.magicitem.controller.TagController;

import java.util.Arrays;

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
     * 自定义
     * */
    public String[] enchant = new String[0];

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


    public CustomTagItem(){}

    public CustomTagItem(String name){
        this.name = name;
    }

    public static CustomTagItem asNameItem(String name){
        return new CustomTagItem(name);
    }



    public CommandCollect.TriggerType onUse(MagicController magicController, CommandCollect.Trigger trigger, Entity player){
        //是否在冷却
        if(magicController.coolTime.containsKey(name)){
            if(System.currentTimeMillis() - magicController.coolTime.get(name) < coolTime * 1000L){
                return CommandCollect.TriggerType.COOL;
            }
        }
        magicController.coolTime.put(name,System.currentTimeMillis());
        boolean success = false;
        for(CommandCollect collect: commandCollects){
            if(collect.activateCommand(trigger,player) == CommandCollect.TriggerType.SUCCESS){
                success = true;
            }
        }
        if(success){
            return CommandCollect.TriggerType.SUCCESS;
        }else{
            return CommandCollect.TriggerType.ERROR;
        }

    }

    /**
     * @deprecated 最好不要用这个功能
     * */
    @Deprecated
    public Item asItem(TagController controller){
        return controller.getTagData().asItem(item);
    }


    public CommandCollect[] getCommandCollects() {
        return commandCollects;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof CustomTagItem){
            return ((CustomTagItem) o).name.equalsIgnoreCase(name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "CustomTagItem{" +
                "name='" + name + '\'' +
                ", nameTag='" + nameTag + '\'' +
                ", lore=" + Arrays.toString(lore) +
                ", enchant=" + Arrays.toString(enchant) +
                ", item='" + item + '\'' +
                ", coolTime=" + coolTime +
                ", canBeUse=" + canBeUse +
                ", commandCollects=" + Arrays.toString(commandCollects) +
                '}';
    }






}
