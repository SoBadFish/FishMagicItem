package org.sobadfish.magicitem.files.entity;

import cn.nukkit.item.Item;
import org.sobadfish.magicitem.controller.TagController;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

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
    public String outputItem;

    /**AAA
     * BAB
     * AAA
     * 摆放
     * */
    public String[] recipeIndex;


    /**
     * 校验是否符合当前合成配方
     * */
    public Item math(Map<Integer, Item> integerStringMap, TagController controller){
       //TODO 固定只保留3个
        String str = recipeIndex[0].substring(0,3)
                +recipeIndex[1].substring(0,3)
                +recipeIndex[2].substring(0,3);
        int size = 0;
        for(Map.Entry<Integer,Item> itemEntry: integerStringMap.entrySet()){
            String itemStr = inputItem.get(str.charAt(itemEntry.getKey()));
            Item asItem =  controller.getTagData().asItem(itemStr);
            if(asItem.equals(itemEntry.getValue(),true,true)){
                size++;
            }
        }
        if(size == str.length()){
            return controller.getTagData().asItem(outputItem);
        }
        return Item.get(0);
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
        return inputItem.equals(recipe.inputItem) && outputItem.equals(recipe.outputItem) &&
                Arrays.equals(recipeIndex, recipe.recipeIndex);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(inputItem, outputItem);
        result = 31 * result + Arrays.hashCode(recipeIndex);
        return result;
    }
}
