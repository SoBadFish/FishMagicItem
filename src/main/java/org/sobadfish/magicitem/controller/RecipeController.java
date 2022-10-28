package org.sobadfish.magicitem.controller;

import org.sobadfish.magicitem.files.BaseDataWriterGetter;
import org.sobadfish.magicitem.files.datas.RecipeData;
import org.sobadfish.magicitem.files.entity.Recipe;

import java.io.File;

/**
 * 配方控制器
 * @author Sobadfish
 * @date 2022/10/25
 */
public class RecipeController {

    private RecipeData recipeData;


    private RecipeController(){}


    static RecipeController initRecipe(){
        RecipeController controller = new RecipeController();
        controller.recipeData = (RecipeData) BaseDataWriterGetter.asFile(new File(MagicController.getDataFolder()+"/recipe.json"),"recipe.json",RecipeData.class);
        return controller;
    }

    public RecipeData getRecipeData() {
        return recipeData;
    }


    public void save(){
        if(recipeData != null) {
            recipeData.save();
        }
    }
}
