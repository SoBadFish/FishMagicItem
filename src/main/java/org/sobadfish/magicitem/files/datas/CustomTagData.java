package org.sobadfish.magicitem.files.datas;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import org.sobadfish.magicitem.controller.MagicController;
import org.sobadfish.magicitem.controller.TagController;
import org.sobadfish.magicitem.files.BaseDataWriterGetter;
import org.sobadfish.magicitem.files.entity.CommandCollect;
import org.sobadfish.magicitem.files.entity.CustomTagItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义物品的数据存储
 * @author Sobadfish
 * @date 2022/10/26
 */
public class CustomTagData extends BaseDataWriterGetter<CustomTagItem> {

    public static final String TAG = "CTag";

    public CustomTagData(List<CustomTagItem> dataList, File file) {
        super(dataList, file);
    }

    public boolean hasItem(String name){
        return dataList.contains(CustomTagItem.asNameItem(name));
    }

    /**
     * 由于一个物品有多个性质，
     * */
    public CustomTagItem[] getCustomItemsByItem(Item item){
        List<CustomTagItem> customTagItems = new ArrayList<>();
        if(item.hasCompoundTag()){
            if(item.getNamedTag().contains(TAG)){

                ListTag<StringTag> s = item.getNamedTag().getList(TAG,StringTag.class);
                for(StringTag n: s.getAll()){
                    if(hasItem(n.getName())){
                        customTagItems.add(dataList.get(dataList.indexOf(CustomTagItem.asNameItem(n.getName()))));
                    }
                }
            }
        }
        return customTagItems.toArray(new CustomTagItem[0]);
    }

    //TODO 移除一个指令集
    public boolean useItem(MagicController magicController,CustomTagItem customTagItem, CommandCollect.Trigger trigger, Item item, Player player){
        if(item.hasCompoundTag() && item.getNamedTag().contains(TAG)) {
            ListTag<StringTag> tagListTag = item.getNamedTag().getList(TAG,StringTag.class);
            if (tagListTag.size() > 1) {
                if (customTagItem.onUse(magicController, trigger, player)) {
                    if(customTagItem.canBeUse){
                        tagListTag.remove(new StringTag(customTagItem.name));
                        return true;
                    }
                }

            } else {
                return customTagItem.onUse(magicController, trigger, player);
            }
        }
        return false;
    }

    public Item getItemByName(String name, TagController tagController){
        CustomTagItem tagItem = CustomTagItem.asNameItem(name);
        if(dataList.contains(tagItem)){
            tagItem = dataList.get(dataList.indexOf(tagItem));
            String item = tagItem.item;
            Item item1 = tagController.getTagData().asItem(item);
            //检验是否存在标签，如果没有标签，则证明这个物品不在tag中存储
            //没有被tag存储的物品不是好物品
            return createTagItem(tagItem,item1,tagController);
        }
        return null;
    }

    //重置物品
    public Item resetTagItem(CustomTagItem customTagItem,Item item,TagController tagController){
        tagController.getTagData().addItem(customTagItem.name, item);
        return item;
    }

    public Item createTagItem(CustomTagItem customTagItem,Item item,TagController tagController){
        if(!item.hasCompoundTag() || !item.getNamedTag().contains(TAG)){
            CompoundTag tag = item.getNamedTag();
            if(tag == null){
                tag = new CompoundTag();
            }
            ListTag<StringTag> tagListTag = new ListTag<>(TAG);
            tagListTag.add(new StringTag(customTagItem.name));
            tag.putList(tagListTag);
//            tag.putList(TAG,new ListTag<StringTag>(new StringTag(customTagItem.name)));
            item.setNamedTag(tag);
            if(customTagItem.nameTag == null){
                item.setCustomName(customTagItem.name);
            }else{
                item.setCustomName(customTagItem.nameTag);
            }
            //设置Lore
            item.setLore(customTagItem.lore);
            //索引到Tag名下
            customTagItem.item = customTagItem.name;
            //TODO 存到Tag中
            tagController.getTagData().addItem(customTagItem.name,item);
        }else if(item.getNamedTag().contains(TAG)){
            //允许2合一
            CompoundTag tag = item.getNamedTag();
            ListTag<StringTag> tagListTag = tag.getList(TAG,StringTag.class);
            for(StringTag tag1: tagListTag.getAll()){
                if(tag1.getName().equalsIgnoreCase(customTagItem.name)){
                    return item;
                }
            }
            tagListTag.add(new StringTag(customTagItem.name));
            item.setNamedTag(tag);
            tagController.getTagData().addItem(customTagItem.name, item);
        }
        save();
        return item;
    }
}
