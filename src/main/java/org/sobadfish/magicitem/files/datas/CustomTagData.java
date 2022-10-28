package org.sobadfish.magicitem.files.datas;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
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

    public CustomTagData(ArrayList<CustomTagItem> dataList, File file) {
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
    public boolean useItem(MagicController magicController,CustomTagItem customTagItem, CommandCollect.Trigger trigger, Item item, Entity player){
        if(item.hasCompoundTag() && item.getNamedTag().contains(TAG)) {
            //TODO 获取范围内玩家
            CommandCollect.TriggerType type = customTagItem.onUse(magicController, trigger, player);
            switch (type){
                case ERROR:
                    //TODO 使用失败
                    if(player instanceof Player) {
                        MagicController.sendMessageToObject(
                                magicController.languageController.echoToPlayer("display-error")
                                        .replace("{%e-msg}", magicController.languageController.echoToPlayer("e-msg-error"))
                                        .replace("{%name}",customTagItem.name), (Player) player);
                    }
                    break;
                case SUCCESS:
                    //TODO 使用失败
                    if(player instanceof Player) {
                        MagicController.sendMessageToObject(
                                magicController.languageController.echoToPlayer("display-success")
                                        .replace("{%e-msg}", magicController.languageController.echoToPlayer("e-msg-success"))
                                        .replace("{%name}",customTagItem.name), (Player) player);
                    }
                    //TODO 使用完成
                    ListTag<StringTag> tagListTag = item.getNamedTag().getList(TAG,StringTag.class);
                    if (tagListTag.size() > 1) {
                        if (customTagItem.canBeUse) {
                            tagListTag.remove(new StringTag(customTagItem.name));
                            return true;
                        }
                    }

                    break;
                case COOL:
                    //TODO 冷却..
                    if(player instanceof Player) {
                        MagicController.sendMessageToObject(
                                magicController.languageController.echoToPlayer("display-cool")
                                        .replace("{%e-msg}", magicController.languageController.echoToPlayer("e-msg-cool"))
                                        .replace("{%name}",customTagItem.name), (Player) player);
                    }
                    break;
                default:break;
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

    public Item resetItem(CustomTagItem customTagItem,Item item){
        if(customTagItem.nameTag == null){
            item.setCustomName(customTagItem.name);
        }else{
            item.setCustomName(customTagItem.nameTag);
        }
        //设置Lore
        item.setLore(customTagItem.lore);

        CompoundTag tag = item.getNamedTag();
        tag.remove("ench");
        item.setNamedTag(tag);
        for(String ench: customTagItem.enchant){
            String[] e = ench.split(":");
            Enchantment eh = Enchantment.getEnchantment(e[0]);
            if(e.length > 1){
                eh.setLevel(Integer.parseInt(e[0]));
            }
            item.addEnchantment(eh);
        }
        return item;
    }

    //重置物品
    public void resetTagItem(CustomTagItem customTagItem,Item item,TagController tagController){
        item = resetItem(customTagItem, item);
        tagController.getTagData().addItem(customTagItem.name, item);
    }

    public Item createTagItem(CustomTagItem customTagItem,Item item,TagController tagController){
        if(!item.hasCompoundTag() || !item.getNamedTag().contains(TAG)){
            item = resetItem(customTagItem, item);
            CompoundTag tag = item.getNamedTag();
            if(tag == null){
                tag = new CompoundTag();
            }
            ListTag<StringTag> tagListTag = new ListTag<>(TAG);
            tagListTag.add(new StringTag(customTagItem.name,customTagItem.name));
            tag.putList(tagListTag);
//            tag.putList(TAG,new ListTag<StringTag>(new StringTag(customTagItem.name)));
            item.setNamedTag(tag);
            //索引到Tag名下
            customTagItem.item = customTagItem.name;
            //TODO 存到Tag中
            tagController.getTagData().addItem(customTagItem.name,item);
        }else if(item.getNamedTag().contains(TAG)){
            //允许2合一
            item = resetItem(customTagItem, item);
            CompoundTag tag = item.getNamedTag();

            ListTag<StringTag> tagListTag = tag.getList(TAG,StringTag.class);
            for(StringTag tag1: tagListTag.getAll()){
                if(tag1.getName().equalsIgnoreCase(customTagItem.name)){
                    return item;
                }
            }
            tagListTag.add(new StringTag(customTagItem.name,customTagItem.name));
            item.setNamedTag(tag);
            tagController.getTagData().addItem(customTagItem.name, item);
        }

        return item;
    }
}
