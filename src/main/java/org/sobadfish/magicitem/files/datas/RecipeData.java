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
 * @author Sobadfish
 * @date 2022/10/26
 */
public class RecipeData extends BaseDataWriterGetter<Recipe> {

    private final LinkedHashMap<Integer, List<Recipe>> loadRecipeMap = new LinkedHashMap<>();

    private TagController tagController;

    /**
     * 产物的配方表
     * */
    public LinkedHashMap<Integer,List<Recipe>> outPutRecipe = new LinkedHashMap<>();

    /**
     * 将配方编译为摆放
     * */
    public LinkedHashMap<Item,BuildRecipeOutPutItem> buildRecipe = new LinkedHashMap<>();

    public List<Item> outPutItems = new ArrayList<>();

    public RecipeData(ArrayList<Recipe> dataList, File file) {
        super(dataList, file);

    }

    public void init(LanguageController languageController){
        loadRecipe(dataList);
        if(languageController.getConfig().getBoolean("import-craft",true)) {
            initDefault();
        }
    }

    private void initDefault() {
        //载入原版合成
        ArrayList<Recipe> recipes = new ArrayList<>();
        CraftingManager craftingManager = Server.getInstance().getCraftingManager();

        for(cn.nukkit.inventory.Recipe recipe: craftingManager.recipes){
            if(recipe instanceof ShapedRecipe){
                Recipe recipe1 = new Recipe();
                recipe1.recipeIndex = asShape(((ShapedRecipe) recipe).getShape());
                recipe1.inputItem = asChar(((ShapedRecipe) recipe).getIngredientsAggregate());
                recipe1.outputItem = asString(recipe.getResult());
                recipes.add(recipe1);
            }
        }

        loadRecipe(recipes);

    }

    public String[] asShape(String[] shape){
        String[] ns = new String[shape.length];
        for(int index = 0;index < shape.length;index++){
            if(shape[index].length() == 1){
                ns[index] = " "+shape[index]+" ";
            }else if(shape[index].length() == 2){
                ns[index] = shape[index] + " ";
            }else{
                ns[index] = shape[index];
            }
        }
        return ns;
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

    public void loadOutPutRecipe(){
        //TODO 肯定是根据现有的加载
        List<Recipe> recipes1;
        for(List<Recipe> recipes: new ArrayList<>(loadRecipeMap.values())){
            ArrayList<Recipe> recipeArrayList = new ArrayList<>(recipes);
            for(Recipe recipe: recipeArrayList){
                for(String out: recipe.outputItem){
                    Item i = tagController.getTagData().asItem(out);
                    if(!outPutItems.contains(i)){
                        outPutItems.add(i);

                    }
                    if(outPutRecipe.containsKey(i.getId())){
                        recipes1 = outPutRecipe.get(i.getId());
                        recipes1.add(recipe);
                        outPutRecipe.put(i.getId(),recipes1);
                    }else{
                        recipes1 = new ArrayList<>();
                        recipes1.add(recipe);
                        outPutRecipe.put(i.getId(),recipes1);
                    }
                }
            }
        }
    }

    public List<Item> getOutPutItems() {
        return outPutItems;
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

    /**
     * 编译输出配方
     * */
    public void buildOutRecipe(){
        for(Item item: outPutItems){
            if(!buildRecipe.containsKey(item)){
                buildRecipe.put(item,new BuildRecipeOutPutItem());
            }
            BuildRecipeOutPutItem bot = buildRecipe.get(item);
            List<LinkedHashMap<Integer,Item>> lrecipe = bot.build;
            List<Recipe> recipes = outPutRecipe.get(item.getId());
            for(Recipe recipe: recipes){
                LinkedHashMap<Integer,Item> craft = new LinkedHashMap<>();
                int index = 0;
                for(String str: recipe.recipeIndex){
                    if(str.length() == 3){
                        for(char a: str.toCharArray()){
                            if(a != ' '){
                                craft.put(index,tagController.getTagData().asItem(recipe.inputItem.get(a)));
                            }
                            index++;
                        }
                    }else if(str.length() == 2){
                        for(char a: str.toCharArray()) {
                            if (a != ' ') {
                                craft.put(index, tagController.getTagData().asItem(recipe.inputItem.get(a)));
                            }
                            index++;
                        }
                    }else if(str.length() == 1){
                        index++;
                        craft.put(index, tagController.getTagData().asItem(recipe.inputItem.get(str.charAt(0))));
                    }
                }
                lrecipe.add(craft);
                List<Item> output = new ArrayList<>();
                for(String ostr: recipe.outputItem){
                    output.add(tagController.getTagData().asItem(ostr));
                }
                bot.outPut = output;
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

    public static class BuildRecipeOutPutItem{
        public List<LinkedHashMap<Integer,Item>> build = new ArrayList<>();

        public List<Item> outPut = new ArrayList<>();

        @Override
        public String toString() {
            return "BuildRecipeOutPutItem{" +
                    "build=" + build +
                    ", outPut=" + outPut +
                    '}';
        }
    }
}
