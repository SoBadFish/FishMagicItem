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
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Sobadfish
 * @date 2022/10/26
 */
public class TagData extends BaseDataWriterGetter<TagItem> {

    public TagData(ArrayList<TagItem> dataList, File file) {
        super(dataList, file);
        initMap();
    }

    private final transient Map<String, TagItem> nameMap = new LinkedHashMap<>();

    private void initMap() {
        nameMap.clear();
        if (dataList != null) {
            for (TagItem item : dataList) {
                if (item != null) {
                    nameMap.put(item.name.toLowerCase(), item);
                }
            }
        }
    }

    /**
     * 缓存编译好的数据
     * */
    public Map<String,Item> cacheItem = new LinkedHashMap<>();


    public Item asItem(String tag){
        if(cacheItem.containsKey(tag)){
            return cacheItem.get(tag);
        }
        if(tag == null){
            return Item.get(0);
        }
        Item item;
        // Use nameMap for fast lookup instead of dataList.contains
        if(nameMap.containsKey(tag.toLowerCase())){
            item = nameMap.get(tag.toLowerCase()).asItem();
        }else {
            try {
                String[] t2 = tag.split(":");
                int count = 1;
                if(t2.length > 2){
                    count = Integer.parseInt(t2[2]);
                }
                item = Item.get(Integer.parseInt(t2[0]), Integer.parseInt(t2[1]), count);
            } catch (Exception e) {
                item = Item.get(0);
            }
        }
        if(item.getId() > 0){
            cacheItem.put(tag,item);
        }
        return item;
    }

    public boolean hasItem(String name){
        return nameMap.containsKey(name.toLowerCase());
    }


    public String itemToStr(Item item){
        if(item != null && item.getId() != 0){
            try {
                return Base64.getEncoder().encodeToString(NBTIO.write(NBTIO.putItemHelper(item)));
            } catch (IOException e) {
                MagicController.sendLogger("保存物品出现问题: " + e.getMessage());
            }

        }
        if(item != null) {
            return item.getId() + ":" + item.getDamage() + ":" + item.getCount();
        }
        return null;
    }

    public String getItemName(Item item){
        String str = itemToStr(item);
        if(str != null){
            // Optimizing lookup: Iterate values of map or check list if map isn't faster for values
            for(TagItem s1: dataList){
                if(s1 != null){
                    if(str.equalsIgnoreCase(s1.itemStr)){
                        return s1.name;
                    }
                }
            }
        }
        return null;
    }

    public TagItem getTagItemByName(String name){
        return nameMap.get(name.toLowerCase());
    }


    /**
     * 将物品写入Tag
     * */
    public void addItem(String name,Item item){
        TagItem tagItem;
        if(nameMap.containsKey(name.toLowerCase())){
            //更新Tag数据
            tagItem = nameMap.get(name.toLowerCase());
            tagItem.itemStr = itemToStr(item);
        }else{
            tagItem = TagItem.asNameTag(name);
            tagItem.itemStr = itemToStr(item);
            dataList.add(tagItem);
            nameMap.put(name.toLowerCase(), tagItem);
        }
        //清除该物品的缓存
        cacheItem.remove(name);
        save();

    }

    public String createItem(Item item){
        int size = 1;
        String nc = getItemName(item);
        if(nc == null){
            // 只要有 CompoundTag (NBT) 或者是特殊物品，就必须注册为 TagItem
            if(item.hasCompoundTag() || item.getNamespaceId() != null) {
                do {
                    nc = item.hasCustomName() ? item.getCustomName() + "-" + size : item.getName() + "-" + size;
                    size++;
                } while (hasItem(nc));
                addItem(nc, item);
            }else{
                //普通物品还有可能是自定义物品
                // 普通物品直接返回 id:meta:count
                return item.getId() + ":" + item.getDamage() + ":" + item.getCount();
            }
        }
        return nc;
    }
}
