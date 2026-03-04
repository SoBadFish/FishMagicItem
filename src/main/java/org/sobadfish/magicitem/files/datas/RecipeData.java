package org.sobadfish.magicitem.files.datas;

import cn.nukkit.Server;
import cn.nukkit.inventory.CraftingManager;
import cn.nukkit.inventory.ShapedRecipe;
import cn.nukkit.item.Item;
import org.sobadfish.magicitem.controller.LanguageController;
import org.sobadfish.magicitem.controller.TagController;
import org.sobadfish.magicitem.files.BaseDataWriterGetter;
import org.sobadfish.magicitem.files.entity.Recipe;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 配方数据
 *
 * @author Sobadfish
 * @date 2022/10/26
 */
public class RecipeData extends BaseDataWriterGetter<Recipe> {

    private final LinkedHashMap<Integer, List<Recipe>> loadRecipeMap = new LinkedHashMap<>();

    private TagController tagController;

    /**
     * 产物的配方表
     *
     */
    public LinkedHashMap<Integer, List<Recipe>> outPutRecipe = new LinkedHashMap<>();

    /**
     * 将配方编译为摆放
     *
     */
    public LinkedHashMap<Item, BuildRecipeOutPutItem> buildRecipe = new LinkedHashMap<>();

    public List<Item> outPutItems = new ArrayList<>();

    public RecipeData(ArrayList<Recipe> dataList, File file) {
        super(dataList, file);

    }

    public void init(LanguageController languageController) {
        loadRecipe(dataList);
        if (languageController.getConfig().getBoolean("import-craft", true)) {
            initDefault();
        }
    }

    private void initDefault() {
        //载入原版合成
        ArrayList<Recipe> recipes = new ArrayList<>();
        CraftingManager craftingManager = Server.getInstance().getCraftingManager();

        for (cn.nukkit.inventory.Recipe recipe : craftingManager.recipes) {
            if (recipe instanceof ShapedRecipe) {
                Recipe recipe1 = new Recipe();
                recipe1.recipeIndex = asShape(((ShapedRecipe) recipe).getShape());
                recipe1.inputItem = asChar(((ShapedRecipe) recipe).getIngredientsAggregate());
                recipe1.outputItem = asString(recipe.getResult());
                recipes.add(recipe1);
            }
        }

        loadRecipe(recipes);

    }

    public String[] asShape(String[] shape) {
        String[] ns = new String[shape.length];
        for (int index = 0; index < shape.length; index++) {
            if (shape[index].length() == 1) {
                ns[index] = " " + shape[index] + " ";
            } else if (shape[index].length() == 2) {
                ns[index] = shape[index] + " ";
            } else {
                ns[index] = shape[index];
            }
        }
        return ns;
    }

    /**
     * 加快配方查找效率
     *
     */
    public List<Recipe> getRecipeByInput(Item input) {
        if (loadRecipeMap.containsKey(input.getId())) {
            return loadRecipeMap.get(input.getId());
        }
        return new ArrayList<>();
    }

    public void setTagController(TagController tagController) {
        this.tagController = tagController;
    }

    public void loadOutPutRecipe() {
        //TODO 肯定是根据现有的加载
        List<Recipe> recipes1;
        for (List<Recipe> recipes : new ArrayList<>(loadRecipeMap.values())) {
            ArrayList<Recipe> recipeArrayList = new ArrayList<>(recipes);
            for (Recipe recipe : recipeArrayList) {
                for (String out : recipe.outputItem) {
                    Item i = tagController.getTagData().asItem(out);
                    if (!outPutItems.contains(i)) {
                        outPutItems.add(i);

                    }
                    if (outPutRecipe.containsKey(i.getId())) {
                        recipes1 = outPutRecipe.get(i.getId());
                        if (!recipes1.contains(recipe)) {
                            recipes1.add(recipe);
                        }
                        outPutRecipe.put(i.getId(), recipes1);
                    } else {
                        recipes1 = new ArrayList<>();
                        recipes1.add(recipe);
                        outPutRecipe.put(i.getId(), recipes1);
                    }
                }
            }
        }
    }

    public List<Item> getOutPutItems() {
        return outPutItems;
    }

    public void updateRecipeIndex(Recipe recipe) {
        for (Item i : recipe.getIngredientItems(tagController)) {
            if (!loadRecipeMap.containsKey(i.getId())) {
                loadRecipeMap.put(i.getId(), new ArrayList<>());
            }
            List<Recipe> recipes = loadRecipeMap.get(i.getId());
            if (!recipes.contains(recipe)) {
                recipes.add(recipe);
            }
        }

        // 更新输出列表和反向索引
        for (String out : recipe.outputItem) {
            Item i = tagController.getTagData().asItem(out);
            if (!outPutItems.contains(i)) {
                outPutItems.add(i);
            }
            List<Recipe> recipes1;
            if (outPutRecipe.containsKey(i.getId())) {
                recipes1 = outPutRecipe.get(i.getId());
                if (!recipes1.contains(recipe)) {
                    recipes1.add(recipe);
                }
            } else {
                recipes1 = new ArrayList<>();
                recipes1.add(recipe);
                outPutRecipe.put(i.getId(), recipes1);
            }
        }
        // 更新显示用的构建配方
        buildOutRecipe();
    }

    private void loadRecipe(ArrayList<Recipe> dataList) {
        for (Recipe recipe : dataList) {
            List<Recipe> recipes;
            for (Item i : recipe.getIngredientItems(tagController)) {
                if (!loadRecipeMap.containsKey(i.getId())) {
                    loadRecipeMap.put(i.getId(), new ArrayList<>());
                }
                recipes = loadRecipeMap.get(i.getId());
                recipes.add(recipe);
            }

        }

    }

    /**
     * 编译输出配方
     *
     */
    public void buildOutRecipe() {
        for (Item item : outPutItems) {
            if (!buildRecipe.containsKey(item)) {
                buildRecipe.put(item, new BuildRecipeOutPutItem());
            }
            BuildRecipeOutPutItem bot = buildRecipe.get(item);
            // 关键修复：清理旧数据，防止重复叠加
            bot.build.clear();
            bot.outPut.clear();
            bot.originRecipes.clear();
            
            List<LinkedHashMap<Integer, Item>> lrecipe = bot.build;
            List<Recipe> oRecipes = bot.originRecipes;
            List<Recipe> recipes = outPutRecipe.get(item.getId());
            
            if (recipes == null) continue;
            
            for (Recipe recipe : recipes) {
                // 关键修复：严格匹配物品（包括 NBT/Damage），防止 ID 相同但物品不同的配方混在一起
                boolean matches = false;
                for (String outStr : recipe.outputItem) {
                    Item outItem = tagController.getTagData().asItem(outStr);
                    if (outItem.equals(item, true, true)) {
                        matches = true;
                        break;
                    }
                }
                if (!matches) continue;

                LinkedHashMap<Integer, Item> craft = new LinkedHashMap<>();
                int index = 0;
                
                // 优先尝试获取缓存的 Grid (支持新格式)
                Map<Integer, Item> inputGrid = recipe.getInputGrid(tagController);
                if (inputGrid != null && !inputGrid.isEmpty()) {
                     for(Map.Entry<Integer, Item> entry : inputGrid.entrySet()) {
                         craft.put(entry.getKey(), entry.getValue());
                     }
                } else if (recipe.recipeIndex != null) {
                    // 旧格式兼容：强制归一到 3x3 索引 (0-8)，保证预览摆放与真实配方一致
                    for (int r = 0; r < recipe.recipeIndex.length && r < 3; r++) {
                        String row = recipe.recipeIndex[r] == null ? "" : recipe.recipeIndex[r];
                        if (row.length() == 1) {
                            row = " " + row + " ";
                        } else if (row.length() == 2) {
                            row = row + " ";
                        } else if (row.length() > 3) {
                            row = row.substring(0, 3);
                        }
                        for (int c = 0; c < 3; c++) {
                            char ch = row.length() > c ? row.charAt(c) : ' ';
                            if (ch != ' ') {
                                String inStr = recipe.inputItem == null ? null : recipe.inputItem.get(ch);
                                if (inStr != null) {
                                    craft.put(r * 3 + c, tagController.getTagData().asItem(inStr).clone());
                                }
                            }
                        }
                    }
                }
                
                if (!craft.isEmpty()) {
                    lrecipe.add(craft);
                    oRecipes.add(recipe);
                    List<Item> output = new ArrayList<>();
                    for (String ostr : recipe.outputItem) {
                        Item out = tagController.getTagData().asItem(ostr);
                        if (out != null && out.getId() != 0) {
                            output.add(out);
                        }
                    }
                    // Only update output if we found valid items, preventing overwrite with empty/null
                    if (!output.isEmpty()) {
                        bot.outPut = output;
                    }
                }
            }
        }
    }

    public String[] asString(Item in) {
        return new String[]{in.getId() + ":" + in.getDamage() + ":" + in.getCount()};
    }

    public Map<Character, String> asChar(List<Item> in) {
        char a = 'A';
        Map<Character, String> stringMap = new LinkedHashMap<>();
        for (Item i : in) {
            stringMap.put(a, i.getId() + ":" + i.getDamage() + ":" + i.getCount());
            a++;
        }
        return stringMap;

    }

    public static class BuildRecipeOutPutItem {

        public List<LinkedHashMap<Integer, Item>> build = new ArrayList<>();

        public List<Item> outPut = new ArrayList<>();
        
        // 新增：存储原始配方引用，确保 ButtonFillCraft 可以获取到干净的源数据
        public List<Recipe> originRecipes = new ArrayList<>();

        @Override
        public String toString() {
            return "BuildRecipeOutPutItem{"
                    + "build=" + build
                    + ", outPut=" + outPut
                    + '}';
        }
    }
}
