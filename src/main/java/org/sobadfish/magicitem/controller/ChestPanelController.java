package org.sobadfish.magicitem.controller;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import org.sobadfish.magicitem.MagicItemMainClass;
import org.sobadfish.magicitem.files.datas.RecipeData;
import org.sobadfish.magicitem.windows.button.*;
import org.sobadfish.magicitem.windows.items.BasePlayPanelItemInstance;
import org.sobadfish.magicitem.windows.panel.CraftItemPanel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 箱子界面控制器
 * 用于绘制界面
 * @author Sobadfish
 * @date 2022/10/26
 */
public class ChestPanelController {


    public static LinkedHashMap<Player,PlayerRecipePage> recipePage = new LinkedHashMap<>();

    public static LinkedHashMap<Player,PlayerItemPage> itemPage = new LinkedHashMap<>();

    /**
     * 主页面
     * */
    public static Map<Integer, BasePlayPanelItemInstance> createMenu(CraftItemPanel panel, Player player){
        Map<Integer,BasePlayPanelItemInstance> playPanelItemInstanceMap = new LinkedHashMap<>();

        panel.isCraft = false;
        if (player.getLoginChainData().getDeviceOS() == 7){
            panel.canPlaceItem = new ArrayList<Integer>(){
                {
                    add(10);add(11);add(12);
                    add(19);add(20);add(21);
                    add(28);add(29);add(30);
                }
            };
            panel.outPutItem = new ArrayList<Integer>(){
                {
                    add(14);add(15);add(16);
                    add(23);add(24);add(25);
                    add(32);add(33);add(34);
                }
            };
            for(int index = 0;index < panel.getInventory().getSize();index++){
                if(panel.outPutItem.contains(index) || panel.canPlaceItem.contains(index)){
                    continue;
                }
                if(index < panel.getInventory().getSize() - 9) {
                    playPanelItemInstanceMap.put(index, new ButtonWall1());
                }else{
                    playPanelItemInstanceMap.put(index, new ButtonWall2());
                }
            }
        }else{
            panel.canPlaceItem = new ArrayList<Integer>(){
                {
                    add(7);add(8);add(9);
                    add(13);add(14);add(15);
                    add(19);add(20);add(21);
                }
            };
            panel.outPutItem = new ArrayList<Integer>(){
                {
                    add(31);add(32);add(33);
                    add(37);add(38);add(39);
                    add(43);add(44);add(45);
                }
            };
            //TODO 手机用户
            for(int index = 0;index < panel.getInventory().getSize();index++){
                if((index + 1) % 6 == 0){
                    playPanelItemInstanceMap.put(index, new ButtonWall2());
                }else{
                    if(panel.outPutItem.contains(index) || panel.canPlaceItem.contains(index)){
                        continue;
                    }
                    playPanelItemInstanceMap.put(index, new ButtonWall1());
                }
            }

        }

        //TODO 绘制主页面
        return playPanelItemInstanceMap;
    }

    /**
     * 配方库列表界面
     * */
    public static Map<Integer, BasePlayPanelItemInstance> recipeListLib(Player player){
        Map<Integer,BasePlayPanelItemInstance> playPanelItemInstanceMap = new LinkedHashMap<>();
        //TODO 绘制配方列表界面
        PlayerItemPage playerItemPage;
        if(!itemPage.containsKey(player)){
            playerItemPage = new PlayerItemPage();
            playerItemPage.page = 1;
            RecipeController recipeController = MagicItemMainClass.mainClass.getMagicController().recipeController;
            playerItemPage.items = asPanelItem(recipeController.getRecipesItems());
            itemPage.put(player,playerItemPage);
        }
        playerItemPage = itemPage.get(player);
        for(int index = 0;index < 9;index++){
            playPanelItemInstanceMap.put(index,new ButtonWall3());
        }

        playPanelItemInstanceMap.put(4,new ButtonGoCraft());
        int index = 9;
        for(BasePlayPanelItemInstance bi: playerItemPage.getItemByPage()){
            playPanelItemInstanceMap.put(index++,bi);
        }
        index = 45;
        for(;index < 54;index++){
            playPanelItemInstanceMap.put(index,new ButtonWall3());
        }
        playPanelItemInstanceMap.put(49,new ButtonPage(playerItemPage.page));
        if(playerItemPage.getMaxPage() > playerItemPage.page){
            playPanelItemInstanceMap.put(51,new ButtonPage(playerItemPage.page + 1));
        }
        if(playerItemPage.page > 1){
            playPanelItemInstanceMap.put(47,new ButtonPage(playerItemPage.page - 1));
        }

        return playPanelItemInstanceMap;
    }

    /**
     * 配方库界面
     * */
    public static Map<Integer, BasePlayPanelItemInstance> recipeLib(Player player,Item item){
        Map<Integer,BasePlayPanelItemInstance> playPanelItemInstanceMap = new LinkedHashMap<>();
        int[] inputLocation = new int[]{
                10,11,12,
                19,20,21,
                28,29,30
        };
        int[] outPutLocation = new int[]{
                14,15,16,
                23,24,25,
                32,33,34
        };


        RecipeController recipeController = MagicItemMainClass.mainClass.getMagicController().recipeController;
        if(recipeController.getRecipeData().buildRecipe.containsKey(item)){
            PlayerRecipePage playerRecipePage;
            if(!recipePage.containsKey(player)){
                playerRecipePage = new PlayerRecipePage();
                playerRecipePage.recipe = recipeController.getRecipeData().buildRecipe.get(item);
                recipePage.put(player,playerRecipePage);

            }
            playerRecipePage = recipePage.get(player);
            LinkedHashMap<Integer,Item> itemLinkedHashMap = playerRecipePage.getRecipeByPage();
            //TODO 绘制展示
            for(Map.Entry<Integer,Item> itemEntry: itemLinkedHashMap.entrySet()){
                playPanelItemInstanceMap.put(inputLocation[itemEntry.getKey()],new ButtonDisplayItem(itemEntry.getValue()));
            }
            List<Item> output = playerRecipePage.recipe.outPut;

            for(int i = 0;i < output.size();i++){
                playPanelItemInstanceMap.put(outPutLocation[i],new ButtonDisplayItem(output.get(i)));
            }

        }


        return playPanelItemInstanceMap;
    }

    /**
     * 创建配方界面
     * */
    public static Map<Integer, BasePlayPanelItemInstance> createRecipeLib(CraftItemPanel panel,Player player){
        //TODO 绘制创建配方界面
        Map<Integer,BasePlayPanelItemInstance> playPanelItemInstanceMap = createMenu(panel, player);
        panel.isCraft = true;
        if (player.getLoginChainData().getDeviceOS() == 7){
            playPanelItemInstanceMap.put(6 * 5 - 1, new ButtonCraft());
        }else {
            playPanelItemInstanceMap.put(9 * 5 + 4, new ButtonCraft());
        }
        return playPanelItemInstanceMap;
    }

    private static List<BasePlayPanelItemInstance> asPanelItem(List<Item> items){
        List<BasePlayPanelItemInstance> itemInstances = new ArrayList<>();
        for(Item item: items){
            itemInstances.add(new ButtonDisplayItem(item));
        }
        return itemInstances;
    }

    public static class PlayerRecipePage{
        public int page = 0;


        public RecipeData.BuildRecipeOutPutItem recipe;

        public LinkedHashMap<Integer,Item> getRecipeByPage(){
            if(recipe.build.size() > page){
                return recipe.build.get(page);
            }
            return null;
        }
    }

    public static class PlayerItemPage{
        public int page = 1;

        public int count = 36;

        public List<BasePlayPanelItemInstance> items = new ArrayList<>();

        public List<BasePlayPanelItemInstance> getItemByPage(){
            List<BasePlayPanelItemInstance> itemInstances = new ArrayList<>();
            for(int i = (page - 1) * count; i < count + ((page - 1) * count);i++){
                if(items.size() > i){
                    itemInstances.add(items.get(i));
                }
            }
            return itemInstances;

        }

        public int getMaxPage(){
            if(items.size() == 0){
                return 1;
            }
            return (int) Math.ceil(items.size() / (double)count);
        }

    }
}
