package org.sobadfish.magicitem.controller;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.item.Item;
import org.sobadfish.magicitem.files.BaseDataWriterGetter;
import org.sobadfish.magicitem.files.datas.CustomTagData;
import org.sobadfish.magicitem.files.datas.TagData;
import org.sobadfish.magicitem.files.entity.CommandCollect;
import org.sobadfish.magicitem.files.entity.CustomTagItem;
import org.sobadfish.magicitem.files.entity.TagItem;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * tag物品控制类
 * @author Sobadfish
 * @date 2022/10/26
 */
public class TagController {

    private TagData tagData;

    private CustomTagData customTagData;

    public CopyOnWriteArrayList<String> lock = new CopyOnWriteArrayList<>();

    private TagController(){}


    static TagController initTag(){
        TagController controller = new TagController();
        MagicController.sendLogger("载入数据中...");
        controller.tagData = (TagData) BaseDataWriterGetter.asFile(new File(MagicController.getDataFolder()+"/items/tag.json"),"tag.json", TagItem.class, TagData.class);
        controller.customTagData = (CustomTagData) BaseDataWriterGetter.asFile(new File(MagicController.getDataFolder()+"/items/custom_item.json"),"custom_item.json", CustomTagItem.class, CustomTagData.class);
        MagicController.sendLogger("载入完成");
        return controller;
    }

    public TagData getTagData() {
        return tagData;
    }

    public CustomTagData getCustomTagData() {
        return customTagData;
    }

    public void save(){
        if(tagData != null) {
            tagData.save();
        }
        if(customTagData != null) {
            customTagData.save();
        }
    }


    /**
     * 创建一个自定义物品
     * */
    public CustomTagItem createDefaultItem(String name,String item){
        CustomTagItem customTagItem = createCustomItem(name,Item.fromString(item));


        return customTagItem;
    }

    /**
     * 通过手持物品创建一个自定义物品
     * */
    public CustomTagItem createDefaultItemInHand(String name, Item item){
        CustomTagItem customTagItem = createCustomItem(name,item);


        return customTagItem;

    }

    public CustomTagItem createCustomItem(String name,Item item){
        //TODO 手持物品创建
        CustomTagItem customTagItem = CustomTagItem.asNameItem(name);
        if(customTagData.hasItem(name)){
            customTagItem = customTagData.dataList.get(customTagData.dataList.indexOf(customTagItem));
        }else{
            customTagItem.item = tagData.itemToStr(item);
            customTagData.dataList.add(customTagItem);
            getTagData().addItem(customTagItem.name,item);
        }
        save();
        customTagData.createTagItem(customTagItem,item,this);
        return customTagItem;
    }

    public Item getItemByName(String name){
        Item i = tagData.asItem(name);
        if(i.getId() > 0){
            return i;
        }
        return null;
    }

    public void useOffhandItem(MagicController magicController,int index,Item item, CommandCollect.Trigger trigger, Entity player){
        //LOCK锁一下
        lock.add(player.getName());
        CustomTagItem[] customTagItem = getCustomTagData().getCustomItemsByItem(item);
        if(customTagItem.length > 0){
            CustomTagItem c0 = customTagItem[0];
            boolean onUse;
            onUse = getCustomTagData().useItem(magicController,c0, trigger,item,player);
            if(onUse){
                for(int i = 1;i < customTagItem.length;i++){
                    getCustomTagData().useItem(magicController,customTagItem[i],trigger,item,player);
                }
            }
            item = getCustomTagData().resetTagItem(c0,item,this);
            if(c0.canBeUse && onUse){
                Item c1 = item.clone();
                c1.setCount(1);
                if(player instanceof EntityHuman){
                    ((EntityHuman) player).getOffhandInventory().removeItem(c1);
                }
            }else{
                if(player instanceof EntityHuman){
                    ((EntityHuman) player).getOffhandInventory().setItem(index,item);
                }
            }
        }
        lock.remove(player.getName());
    }

    public void useItem(MagicController magicController,int index,Item item, CommandCollect.Trigger trigger, Entity player){
        //LOCK锁一下
        lock.add(player.getName());
        CustomTagItem[] customTagItem = getCustomTagData().getCustomItemsByItem(item);
        if(customTagItem.length > 0){
            CustomTagItem c0 = customTagItem[0];
            boolean onUse;
            onUse = getCustomTagData().useItem(magicController,c0, trigger,item,player);
            if(onUse){
                for(int i = 1;i < customTagItem.length;i++){
                    getCustomTagData().useItem(magicController,customTagItem[i],trigger,item,player);
                }
            }
            item = getCustomTagData().resetTagItem(c0,item,this);
            if(c0.canBeUse && onUse){
                Item c1 = item.clone();
                c1.setCount(1);
                if(player instanceof EntityHuman){
                    ((EntityHuman) player).getInventory().removeItem(c1);
                }
            }else{
                if(player instanceof EntityHuman){
                    ((EntityHuman) player).getInventory().setItem(index,item);
                }
            }
        }
        lock.remove(player.getName());
    }

    /**
     * 更新物品标签
     * */
    public Item notifyChange(CustomTagItem tagData,Item item){
        item.setLore(tagData.lore);
        item.setCustomName(tagData.nameTag != null? tagData.nameTag : tagData.name);
        getTagData().addItem(tagData.name, item);
        return item;
    }
}
