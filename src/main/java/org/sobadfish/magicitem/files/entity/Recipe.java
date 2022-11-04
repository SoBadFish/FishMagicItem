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
                String sp = ss.split("(?<=\\G.{3})")[0];
                if(sp.length() < 3){
                    sp = (sp + "   ").substring(0,3);
                }
                str.append(sp);
            }
            str = new StringBuilder(str.toString().split("(?<=\\G.{9})")[0]);
            int size = 0;
            int rsize = 0;
            //TODO 根据配方来
            int saveIndex = 0;
            for(char c: str.toString().toCharArray()) {
                if (c == ' ') {
                    if (!integerStringMap.containsKey(saveIndex)) {
                        size++;
                    }
                } else {
                    TagItem tagItem = controller.getTagData().getTagItemByName(inputItem.get(c));
                    if (tagItem != null) {
                        if (integerStringMap.containsKey(saveIndex)) {
                            if (integerStringMap.get(saveIndex).equals(controller.getTagData().asItem(tagItem.name), true, true)) {
                                size++;
                                rsize++;

                            }
                        }
                    }else{
                        if (integerStringMap.containsKey(saveIndex)) {
                            String[] t2 = inputItem.get(c).split(":");

                            Item item = Item.get(Integer.parseInt(t2[0]),Integer.parseInt(t2[1]),Integer.parseInt(t2[2]));
                            if (integerStringMap.get(saveIndex).equals(item, true, true)) {
                                size++;
                                rsize++;

                            }
                        }
                    }
                }
                saveIndex++;

            }
            if(size == str.length() && rsize == integerStringMap.size()){
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

    public boolean hasOutItem(Item item,TagController controller){
        for(String s: outputItem){
            if(controller.getTagData().asItem(s).equals(item,true,true)){
                return true;
            }
        }
        return false;
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


    @Override
    public String toString() {
        return "Recipe{" +
                "inputItem=" + inputItem +
                ", outputItem=" + Arrays.toString(outputItem) +
                ", recipeIndex=" + Arrays.toString(recipeIndex) +
                '}';
    }
}
