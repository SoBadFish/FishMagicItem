package org.sobadfish.magicitem.files.datas;

import cn.nukkit.item.Item;
import org.sobadfish.magicitem.controller.TagController;
import org.sobadfish.magicitem.files.BaseDataWriterGetter;
import org.sobadfish.magicitem.files.entity.Recipe;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 配方数据
 * @author Sobadfish
 * @date 2022/10/26
 */
public class RecipeData extends BaseDataWriterGetter<Recipe> {

    private LinkedHashMap<Item, List<Recipe>> loadRecipeMap = new LinkedHashMap<>();

    private TagController tagController;

    public RecipeData(ArrayList<Recipe> dataList, File file) {
        super(dataList, file);
        loadRecipe();
    }

    /**
     * 加快配方查找效率
     * */
    public List<Recipe> getRecipeByInput(Item input){
        if(loadRecipeMap.containsKey(input)){
            return loadRecipeMap.get(input);
        }
        return new ArrayList<>();
    }

    public void setTagController(TagController tagController) {
        this.tagController = tagController;
    }

    private void loadRecipe(){
        for(Recipe recipe: dataList){
            List<Recipe> recipes;
            for(String istr: recipe.inputItem.values()){
                Item i = tagController.getTagData().asItem(istr);
                if(!loadRecipeMap.containsKey(i)){
                   loadRecipeMap.put(i,new ArrayList<>());
                }
                recipes = loadRecipeMap.get(i);
                recipes.add(recipe);
            }

        }

    }
}
