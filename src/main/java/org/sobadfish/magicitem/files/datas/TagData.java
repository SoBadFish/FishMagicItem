package org.sobadfish.magicitem.files.datas;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.NBTIO;
import org.sobadfish.magicitem.controller.MagicController;
import org.sobadfish.magicitem.files.BaseDataWriterGetter;
import org.sobadfish.magicitem.files.entity.TagItem;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sobadfish
 * @date 2022/10/26
 */
public class TagData extends BaseDataWriterGetter<TagItem> {

    public TagData(List<TagItem> dataList, File file) {
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
            item = Item.fromString(tag);
        }
        cacheItem.put(tag,item);
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

    }
}
