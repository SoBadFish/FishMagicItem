package org.sobadfish.magicitem.files.datas;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.NBTIO;
import org.sobadfish.magicitem.controller.MagicController;
import org.sobadfish.magicitem.files.BaseDataWriterGetter;
import org.sobadfish.magicitem.files.entity.TagItem;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Sobadfish
 * @date 2022/10/26
 */
public class TagData extends BaseDataWriterGetter<TagItem> {

    public TagData(ArrayList<TagItem> dataList, File file) {
        super(dataList, file);
    }

    /**
     * 缓存编译好的数据
     * */
    public Map<String,Item> cacheItem = new LinkedHashMap<>();


    public Item asItem(String tag){
        if(cacheItem.containsKey(tag)){
            return cacheItem.get(tag);
        }
        Item item;
        if(dataList.contains(TagItem.asNameTag(tag))){
            item = dataList.get(dataList.indexOf(TagItem.asNameTag(tag))).asItem();
        }else {
            String[] t2 = tag.split(":");

            item = Item.get(Integer.parseInt(t2[0]),Integer.parseInt(t2[1]),Integer.parseInt(t2[2]));

        }
        if(item.getId() > 0){
            cacheItem.put(tag,item);
        }
        return item;
    }

    public boolean hasItem(String name){
        return dataList.contains(TagItem.asNameTag(name));
    }


    public String itemToStr(Item item){
        if(item != null && item.getId() != 0){
            try {
                return new String(NBTIO.write(NBTIO.putItemHelper(item)), StandardCharsets.UTF_8);
            } catch (IOException e) {
                MagicController.sendLogger("保存物品出现问题: "+e.getMessage());
            }
        }
        if(item != null) {
            return item.getId() + ":" + item.getDamage() + ":" + item.getCount();
        }
        return null;
    }

    public String getItemName(Item item){
        for(TagItem s1: dataList){
            if(s1 != null){
                String str = itemToStr(item);
                if(str != null){
                    if(str.equalsIgnoreCase(s1.itemStr)){
                        return s1.name;
                    }
                }
            }else{
                dataList.remove(s1);
            }


        }
        return null;
    }

    public TagItem getTagItemByName(String name){
        for(TagItem s1: dataList){
            if(s1 != null) {
                if (s1.name.equalsIgnoreCase(name)) {
                    return s1;
                }
            }else{
                dataList.remove(s1);
            }
        }
        return null;
    }


    /**
     * 将物品写入Tag
     * */
    public void addItem(String name,Item item){
        TagItem tagItem;
        if(dataList.contains(TagItem.asNameTag(name))){
            //更新Tag数据
            tagItem = dataList.get(dataList.indexOf(TagItem.asNameTag(name)));
            tagItem.itemStr = itemToStr(item);
        }else{
            tagItem = TagItem.asNameTag(name);
            tagItem.itemStr = itemToStr(item);
            dataList.add(tagItem);
        }
        //清除该物品的缓存
        cacheItem.remove(name);
        save();

    }

    public String createItem(Item item){
        int size = 1;
        String nc = getItemName(item);
        if(nc == null){
            do {
                nc = item.hasCustomName()?item.getCustomName()+"-"+size:item.getName()+"-"+size;
                size++;
            }while (hasItem(nc));
            addItem(nc,item);
        }
        return nc;
    }
}
