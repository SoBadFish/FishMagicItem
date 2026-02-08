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


    // 新增：预编译的配方网格缓存
    private transient Item[] cachedInputGrid;

    // 新增：编译方法，将字符串配方转换为物品数组
    public void compile(TagController controller) {
        cachedInputGrid = new Item[9];
        StringBuilder str = new StringBuilder();
        // 还原配方字符串逻辑只执行一次
        for(String ss: recipeIndex){
            String sp = ss.split("(?<=\\G.{3})")[0];
            if(sp.length() < 3) sp = (sp + "   ").substring(0,3);
            str.append(sp);
        }
        String pattern = str.toString();
        // 补齐或截断到9位
        if (pattern.length() > 9) pattern = pattern.substring(0, 9);
        while (pattern.length() < 9) pattern += " ";

        char[] chars = pattern.toCharArray();
        for (int i = 0; i < 9; i++) {
            char c = chars[i];
            if (c != ' ') {
                String tagStr = inputItem.get(c);
                if (tagStr != null) {
                    cachedInputGrid[i] = controller.getTagData().asItem(tagStr);
                }
            }
        }
    }

    /**
     * 校验是否符合当前合成配方
     * */
    public Item[] math(Map<Integer, Item> input, TagController controller){
        if (cachedInputGrid == null) {
            compile(controller);
        }

        int matches = 0;
        int inputCount = 0;
        // 统计实际输入的物品数量
        for(Item it : input.values()) {
            if(it != null && it.getId() != 0) inputCount++;
        }

        for (int i = 0; i < 9; i++) {
            Item expected = cachedInputGrid[i];
            Item actual = input.get(i);
            
            boolean expectedEmpty = (expected == null || expected.getId() == 0);
            boolean actualEmpty = (actual == null || actual.getId() == 0);

            if (expectedEmpty) {
                if (!actualEmpty) return new Item[0]; // 期望空但实际有物品 -> 失败
            } else {
                if (actualEmpty) return new Item[0]; // 期望有物品但实际空 -> 失败
                
                // 比较逻辑：如果是自定义物品(有NBT)，严格匹配；否则宽松匹配
                boolean checkTag = expected.hasCompoundTag();
                if (!expected.equals(actual, true, checkTag)) {
                     return new Item[0];
                }
                matches++;
            }
        }
        
        // 确保没有多余的物品（例如在 3x3 之外的物品，虽然通常不会有）
        if (matches != inputCount) return new Item[0];

        // 构建输出 (注意：必须 clone 以防止修改缓存)
        ArrayList<Item> output = new ArrayList<>();
        for (String outStr : outputItem) {
            output.add(controller.getTagData().asItem(outStr).clone());
        }
        return output.toArray(new Item[0]);
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
