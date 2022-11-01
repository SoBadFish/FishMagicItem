package org.sobadfish.magicitem.files.datas;

import cn.nukkit.Server;
import cn.nukkit.inventory.CraftingManager;
import cn.nukkit.inventory.ShapedRecipe;
import cn.nukkit.item.Item;
import org.sobadfish.magicitem.controller.LanguageController;
import org.sobadfish.magicitem.controller.MagicController;
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
 * @author Sobadfish
 * @date 2022/10/26
 */
public class RecipeData extends BaseDataWriterGetter<Recipe> {

    private final LinkedHashMap<Integer, List<Recipe>> loadRecipeMap = new LinkedHashMap<>();

    private TagController tagController;

    public RecipeData(ArrayList<Recipe> dataList, File file) {
        super(dataList, file);

    }

    public void init(LanguageController languageController){
        loadRecipe(dataList);
        if(languageController.getConfig().getBoolean("import-craft",true)) {
            initDefault();
        }
        MagicController.sendLogger("&a加载完成 成功载入: "+loadRecipeMap.size()+" 个关联配方");

    }

    private void initDefault() {
        //载入原版合成
        ArrayList<Recipe> recipes = new ArrayList<>();
        CraftingManager craftingManager = Server.getInstance().getCraftingManager();

        for(cn.nukkit.inventory.Recipe recipe: craftingManager.recipes){
            if(recipe instanceof ShapedRecipe){
                Recipe recipe1 = new Recipe();
                recipe1.recipeIndex = ((ShapedRecipe) recipe).getShape();
                recipe1.inputItem = asChar(((ShapedRecipe) recipe).getIngredientsAggregate());
                recipe1.outputItem = asString(recipe.getResult());
                recipes.add(recipe1);


            }
        }

        loadRecipe(recipes);

    }

    /**
     * 加快配方查找效率
     * */
    public List<Recipe> getRecipeByInput(Item input){
        if(loadRecipeMap.containsKey(input.getId())){
            return loadRecipeMap.get(input.getId());
        }
        return new ArrayList<>();
    }

    public void setTagController(TagController tagController) {
        this.tagController = tagController;
    }

    private void loadRecipe(ArrayList<Recipe> dataList){
        for(Recipe recipe: dataList){
            List<Recipe> recipes;
            for(String istr: recipe.inputItem.values()){
                Item i = tagController.getTagData().asItem(istr);
                if(!loadRecipeMap.containsKey(i.getId())){
                   loadRecipeMap.put(i.getId(),new ArrayList<>());
                }
                recipes = loadRecipeMap.get(i.getId());
                recipes.add(recipe);
            }

        }


    }

    public String[] asString(Item in){
       return new String[]{in.getId()+":"+in.getDamage()+":"+in.getCount()};
    }

    public Map<Character,String> asChar(List<Item> in){
        char a = 'A';
        Map<Character, String> stringMap = new LinkedHashMap<>();
        for(Item i: in){
            stringMap.put(a,i.getId()+":"+i.getDamage()+":"+i.getCount());
            a++;
        }
        return stringMap;

    }
}
