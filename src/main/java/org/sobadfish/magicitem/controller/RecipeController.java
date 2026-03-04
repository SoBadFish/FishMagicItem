package org.sobadfish.magicitem.controller;

import cn.nukkit.item.Item;
import org.sobadfish.magicitem.files.BaseDataWriterGetter;
import org.sobadfish.magicitem.files.datas.RecipeData;
import org.sobadfish.magicitem.files.entity.CraftingResult;
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

    public org.sobadfish.magicitem.files.entity.CraftingResult craftItemResult(Map<Integer, Item> input, MagicController controller) {
        if (!input.isEmpty()) {

            Item i = null;
            for (Item it : input.values()) {
                if (it.getId() != 0) {
                    i = it;
                }
            }
            if (i == null) {
                return CraftingResult.failure();
            }

            for (Recipe recipe : recipeData.getRecipeByInput(i)) {
                CraftingResult result = recipe.match(input, controller.tagController);
                if (result.success) {
                    return result;
                }
            }
        }

        return org.sobadfish.magicitem.files.entity.CraftingResult.failure();
    }

    public Item[] craftItem(Map<Integer, Item> input, MagicController controller) {
        org.sobadfish.magicitem.files.entity.CraftingResult result = craftItemResult(input, controller);
        if (result.success) {
            return result.output;
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

    public void addCraft(Map<Integer, Item> input, Item[] output, TagController controller, boolean isMobile) {
        StringBuilder str = new StringBuilder();
        LinkedHashMap<Character, Item> charItem = new LinkedHashMap<>();
        char a = 'A';
        int[] customUiSlots = new int[]{0, 1, 2, 9, 10, 11, 18, 19, 20};
        int[] mobileSlots = new int[]{7, 8, 9, 13, 14, 15, 19, 20, 21};
        int[] pcSlots = new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30};

        int[] inputSlots;
        boolean useCustomUi = input.containsKey(0) || input.containsKey(1) || input.containsKey(2) || input.containsKey(18);
        // Use the layout based on the player's device
        if (useCustomUi) {
            inputSlots = customUiSlots;
            MagicController.sendLogger("调试: 使用 CustomUI 布局输入");
        } else if (isMobile) {
            inputSlots = mobileSlots;
            MagicController.sendLogger("调试: 使用 Mobile 布局输入");
        } else {
            inputSlots = pcSlots;
            MagicController.sendLogger("调试: 使用 PC 布局输入");
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
            boolean isWall = false;
            if (currentItem != null && currentItem.hasCompoundTag() && currentItem.getNamedTag().contains("button")) {
                isWall = true;
            }

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
            } else {
                // Always append space for empty slots to maintain 3x3 structure
                str.append(" ");
            }
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
