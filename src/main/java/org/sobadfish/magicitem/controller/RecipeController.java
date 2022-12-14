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
 * @author Sobadfish
 * @date 2022/10/25
 */
public class RecipeController {

    private RecipeData recipeData;


    private RecipeController(){

    }

    static RecipeController initRecipe(MagicController magicController){
        long t1 = System.currentTimeMillis();
        RecipeController controller = new RecipeController();
        controller.recipeData = (RecipeData) BaseDataWriterGetter.asFile(new File(MagicController.getDataFolder()+"/recipe.json"),"recipe.json",Recipe[].class,RecipeData.class);
        controller.recipeData.setTagController(magicController.tagController);
        controller.recipeData.init(magicController.languageController);
        controller.recipeData.loadOutPutRecipe();
        controller.recipeData.buildOutRecipe();
        MagicController.sendLogger("&a加载完成 耗时: "+(System.currentTimeMillis() - t1)+" ms");
        return controller;
    }

    public RecipeData getRecipeData() {
        return recipeData;
    }

    public Item[] craftItem(Map<Integer, Item> input,MagicController controller){
        if(input.size() > 0) {

            Item i = null;
            for(Item it : input.values()){
                if(it.getId() != 0){
                    i = it;
                }
            }
            if(i == null){
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

    public List<Recipe> getRecipesByItem(Item item){
        if(recipeData.outPutRecipe.containsKey(item.getId())){
            return recipeData.outPutRecipe.get(item.getId());
        }
        return new ArrayList<>();

    }

    public List<Item> getRecipesItems(){
        return recipeData.getOutPutItems();

    }

    public void addCraft(Map<Integer,Item> input,Item[] output,TagController controller){
        StringBuilder str = new StringBuilder();
        LinkedHashMap<Character,Item> charItem = new LinkedHashMap<>();
        char a = 'A';
        int c = 0;
        for(int i = 0;i < 9;i++){
            if(input.containsKey(i)){
                c++;
                boolean has = false;
                for (Map.Entry<Character, Item> cE : charItem.entrySet()) {
                    if (cE.getValue().equals(input.get(i), true, true)) {
                        str.append(cE.getKey());
                        has = true;
                        break;
                    }
                }
                if(!has){
                    str.append(a);
                    charItem.put(a,input.get(i));
                    a++;
                }
            }else{
                if(c != input.size()){
                    str.append(" ");
                }
            }
        }
        LinkedHashMap<Character,String> charItemSa = new LinkedHashMap<>();
        for(Map.Entry<Character,Item> cE: charItem.entrySet()){
            charItemSa.put(cE.getKey(),controller.getTagData().createItem(cE.getValue()));
        }
        String[] s1 = new String[output.length];
        for(int i = 0;i < s1.length;i++){
            s1[i] = controller.getTagData().createItem(output[i]);
        }

        Recipe recipe = new Recipe();
        recipe.recipeIndex = str.toString().split("(?<=\\G.{3})");
        recipe.inputItem = charItemSa;
        recipe.outputItem = s1;
        if(!getRecipeData().dataList.contains(recipe)) {
            getRecipeData().dataList.add(recipe);
        }
        save();
    }


    public void save(){
        if(recipeData != null) {
            recipeData.save();
        }
    }
}
