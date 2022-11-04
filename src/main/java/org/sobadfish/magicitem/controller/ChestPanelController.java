package org.sobadfish.magicitem.controller;

import cn.nukkit.Player;
import org.sobadfish.magicitem.files.entity.Recipe;
import org.sobadfish.magicitem.windows.button.ButtonCraft;
import org.sobadfish.magicitem.windows.button.ButtonWall1;
import org.sobadfish.magicitem.windows.button.ButtonWall2;
import org.sobadfish.magicitem.windows.items.BasePlayPanelItemInstance;
import org.sobadfish.magicitem.windows.lib.ChestInventoryPanel;
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


    public LinkedHashMap<Player,PlayerRecipePage> recipePage = new LinkedHashMap<>();

    public LinkedHashMap<Player,PlayerItemPage> itemPage = new LinkedHashMap<>();

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
    public static Map<Integer, BasePlayPanelItemInstance> recipeListLib(ChestInventoryPanel panel,Player player){
        //TODO 绘制配方列表界面

        return null;
    }

    /**
     * 配方库界面
     * */
    public static Map<Integer, BasePlayPanelItemInstance> recipeLib(ChestInventoryPanel panel, Player player, List<Recipe> recipeList){
        //TODO 绘制配方展示界面
        return null;
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

    public static class PlayerRecipePage{
        public int page;
        public int maxPage;

        public List<Recipe> recipes;
    }

    public static class PlayerItemPage{
        public int page;
        public int maxPage;

        public List<BasePlayPanelItemInstance> items;
    }
}
