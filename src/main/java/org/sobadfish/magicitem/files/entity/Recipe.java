package org.sobadfish.magicitem.files.entity;

import cn.nukkit.item.Item;
import org.sobadfish.magicitem.controller.TagController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * 合成的配方
 * @author Sobadfish
 * @date 2022/10/26
 */
public class Recipe {

    /**
     * 输入物品
     * */
    public Map<Character,String> inputItem;

    /**
     * 输出物品
     * */
    public String[] outputItem;

    /**A
     * 摆放
     * */
    public String[] recipeIndex;


    /**
     * 校验是否符合当前合成配方
     * */
    public Item[] math(Map<Integer, Item> integerStringMap, TagController controller){
       //TODO 固定只保留3个
        Item[] i9 = new Item[9];
        if(recipeIndex.length > 0){
            StringBuilder str = new StringBuilder();
            for(String ss: recipeIndex){
                str.append(ss.split("(?<=\\G.{3})")[0]);
            }
            str = new StringBuilder(str.toString().trim().split("(?<=\\G.{9})")[0]);
            int size = 0;
            //TODO 根据配方来
            int saveIndex = -1;
            for(char c: str.toString().toCharArray()){
                if(!inputItem.containsKey(c)){
                    continue;
                }
                for(Map.Entry<Integer,Item> itemEntry: integerStringMap.entrySet()){
                    if(c == ' '){
                        if(saveIndex == itemEntry.getKey() - 1){
                            if(itemEntry.getValue().getId() == 0) {
                                saveIndex = itemEntry.getKey();
                                size++;
                            }
                        }
                    }else{
                        TagItem tagItem = controller.getTagData().getTagItemByName(inputItem.get(c));
                        if(tagItem != null){
                            //忽视数量
                            if(itemEntry.getValue().equals(controller.getTagData().asItem(tagItem.name),true,true)){
                                if(saveIndex == -1){
                                    saveIndex = itemEntry.getKey();
                                    size++;
                                }else if(saveIndex == itemEntry.getKey() - 1){
                                    saveIndex = itemEntry.getKey();
                                    size++;
                                }
                            }
                        }

                    }

                }
            }

            if(size == str.length()){
                for(int i = 0;i < outputItem.length;i++){
                    if(i >= 9){
                        break;
                    }
                    i9[i] = controller.getTagData().asItem(outputItem[i]);
                }
                ArrayList<Item> ii9 = new ArrayList<>();
                for(Item i: i9){
                    if(i != null){
                        ii9.add(i);
                    }
                }
                return ii9.toArray(new Item[0]);

            }

        }
        return new Item[0];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Recipe)) {
            return false;
        }

        Recipe recipe = (Recipe) o;

        if (inputItem != null ? !inputItem.equals(recipe.inputItem) : recipe.inputItem != null) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(outputItem, recipe.outputItem)) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(recipeIndex, recipe.recipeIndex);
    }

    @Override
    public int hashCode() {
        int result = inputItem != null ? inputItem.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(outputItem);
        result = 31 * result + Arrays.hashCode(recipeIndex);
        return result;
    }
}
