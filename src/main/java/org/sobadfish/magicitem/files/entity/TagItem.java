package org.sobadfish.magicitem.files.entity;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import org.sobadfish.magicitem.controller.MagicController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * tag物品
 * @author Sobadfish
 * @date 2022/10/26
 */
public class TagItem {

    /**
     * 调用到的名字
     * */
    public String name = null;

    /**
     * 索引的标签内容
     * （可直接还原为物品）
     * */
    public String itemStr = null;

    public TagItem(){}

    public TagItem(String name){
        this.name = name;
    }

    public static TagItem asNameTag(String name){
        return new TagItem(name);
    }



    public Item asItem(){
        if(itemStr != null){
            try {
                CompoundTag tag = NBTIO.read(itemStr.getBytes(StandardCharsets.UTF_8));
                return NBTIO.getItemHelper(tag);
            } catch (IOException e) {
                MagicController.sendLogger("编译物品出现问题: "+e.getMessage());
            }
        }
        return Item.get(0);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }


    @Override
    public boolean equals(Object o) {
        if(o instanceof TagItem){
            TagItem that = (TagItem) o;
            return name.equalsIgnoreCase(that.name);
        }

        return false;
    }


}
