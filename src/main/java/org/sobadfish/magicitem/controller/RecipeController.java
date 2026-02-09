package org.sobadfish.magicitem.controller;

import cn.nukkit.item.Item;
import org.sobadfish.magicitem.files.BaseDataWriterGetter;
import org.sobadfish.magicitem.files.datas.RecipeData;
import org.sobadfish.magicitem.files.entity.Recipe;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 配方控制器
 *
 * @author Sobadfish
 * @date 2022/10/25
 */
public class RecipeController {

    private RecipeData recipeData;

    private RecipeController() {

    }

    static RecipeController initRecipe(MagicController magicController) {
        long t1 = System.currentTimeMillis();
        RecipeController controller = new RecipeController();
        controller.recipeData = (RecipeData) BaseDataWriterGetter.asFile(new File(MagicController.getDataFolder() + "/recipe.json"), "recipe.json", Recipe[].class, RecipeData.class);
        controller.recipeData.setTagController(magicController.tagController);
        controller.recipeData.init(magicController.languageController);
        controller.recipeData.loadOutPutRecipe();
        controller.recipeData.buildOutRecipe();
        MagicController.sendLogger("&a加载完成 耗时: " + (System.currentTimeMillis() - t1) + " ms");
        return controller;
    }

    public RecipeData getRecipeData() {
        return recipeData;
    }

    public Item[] craftItem(Map<Integer, Item> input, MagicController controller) {
        if (input.size() > 0) {

            Item i = null;
            for (Item it : input.values()) {
                if (it.getId() != 0) {
                    i = it;
                }
            }
            if (i == null) {
                return new Item[0];
            }

            for (Recipe recipe : recipeData.getRecipeByInput(i)) {
                Item[] is = recipe.math(input, controller.tagController);
                if (is.length > 0 && is[0] != null) {
                    return is;
                }
            }
        }

        return new Item[0];
    }

    public List<Recipe> getRecipesByItem(Item item) {
        if (recipeData.outPutRecipe.containsKey(item.getId())) {
            return recipeData.outPutRecipe.get(item.getId());
        }
        return new ArrayList<>();

    }

    public List<Item> getRecipesItems() {
        return recipeData.getOutPutItems();

    }

    public void addCraft(Map<Integer, Item> input, Item[] output, TagController controller) {
        StringBuilder str = new StringBuilder();
        LinkedHashMap<Character, Item> charItem = new LinkedHashMap<>();
        char a = 'A';
        int[] mobileSlots = new int[]{7, 8, 9, 13, 14, 15, 19, 20, 21};
        int[] pcSlots = new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30};

        int mobileMatch = 0;
        int pcMatch = 0;

        for (int slot : mobileSlots) {
            if (input.containsKey(slot) && input.get(slot) != null && input.get(slot).getId() != 0) {
                mobileMatch++;
            }
        }
        for (int slot : pcSlots) {
            if (input.containsKey(slot) && input.get(slot) != null && input.get(slot).getId() != 0) {
                pcMatch++;
            }
        }

        int[] inputSlots;
        // 智能选择：谁匹配的多用谁，如果都没匹配（空配方），默认 Mobile (为了响应用户需求)
        if (mobileMatch >= pcMatch) {
            inputSlots = mobileSlots;
            MagicController.sendLogger("调试: 检测到 Mobile 布局输入 (匹配数: " + mobileMatch + ")");
        } else {
            inputSlots = pcSlots;
            MagicController.sendLogger("调试: 检测到 PC 布局输入 (匹配数: " + pcMatch + ")");
        }

        // 2. 遍历 3x3 网格
        MagicController.sendLogger("调试: 创建配方中. 输入物品的槽位Key集合: " + input.keySet());
        for (int i = 0; i < 9; i++) {
            int slotIndex = inputSlots[i];

            // 调试输出
            if (input.containsKey(slotIndex)) {
                MagicController.sendLogger("调试: 槽位 " + slotIndex + " 包含物品: " + input.get(slotIndex));
            } else {
                MagicController.sendLogger("调试: 槽位 " + slotIndex + " 为空");
            }

            // 检查 Map 中是否包含该 key，且 value 不为 null，且物品 ID 不为 0
            // 过滤掉 ButtonWall (玻璃板)
            Item currentItem = input.get(slotIndex);
            boolean isWall = currentItem != null && currentItem.hasCompoundTag() && currentItem.getNamedTag().contains("button");
            // 如果是按钮，进一步检查是否是 Wall
            // 通常 ButtonWall 的 id 是 160 (Stained Glass Pane)
            // 或者我们可以检查 Item 名称或 ID。
            // 简单起见，如果它是一个 Button，我们就认为它不是有效配方材料。
            // 除非... 配方真的需要一个按钮？但这里的 Button 是 UI 组件。

            if (input.containsKey(slotIndex) && currentItem != null && currentItem.getId() != 0 && !isWall) {
                boolean has = false;
                for (Map.Entry<Character, Item> cE : charItem.entrySet()) {
                    // Use strict match to distinguish different items
                    if (cE.getValue().equals(currentItem, true, true)) {
                        str.append(cE.getKey());
                        has = true;
                        break;
                    }
                }
                if (!has) {
                    str.append(a);
                    charItem.put(a, currentItem);
                    a++;
                }
            }
//            else {
//                // Always append space for empty slots to maintain 3x3 structure
//                str.append(" ");
//            }
        }

        LinkedHashMap<Character, String> charItemSa = new LinkedHashMap<>();
        for (Map.Entry<Character, Item> cE : charItem.entrySet()) {
            String created = controller.getTagData().createItem(cE.getValue());
            charItemSa.put(cE.getKey(), created);
        }
        String[] s1 = new String[output.length];
        for (int i = 0; i < s1.length; i++) {
            s1[i] = controller.getTagData().createItem(output[i]);
        }

        Recipe recipe = new Recipe();
        recipe.recipeIndex = str.toString().split("(?<=\\G.{3})");
        recipe.inputItem = charItemSa;
        recipe.outputItem = s1;
        if (!getRecipeData().dataList.contains(recipe)) {
            getRecipeData().dataList.add(recipe);
            // 立即更新内存中的索引，确保无需重启即可使用
            getRecipeData().updateRecipeIndex(recipe);
        }
        save();
    }

    public void save() {
        if (recipeData != null) {
            recipeData.save();
        }
    }
}
