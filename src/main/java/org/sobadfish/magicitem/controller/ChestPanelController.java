package org.sobadfish.magicitem.controller;

import cn.nukkit.Player;
import org.sobadfish.magicitem.windows.button.ButtonCraft;
import org.sobadfish.magicitem.windows.button.ButtonWall1;
import org.sobadfish.magicitem.windows.button.ButtonWall2;
import org.sobadfish.magicitem.windows.button.ButtonWall3;
import org.sobadfish.magicitem.windows.items.BasePlayPanelItemInstance;
import org.sobadfish.magicitem.windows.lib.ChestInventoryPanel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 箱子界面控制器
 * 用于绘制界面
 * @author Sobadfish
 * @date 2022/10/26
 */
public class ChestPanelController {



    /**
     * 主页面
     * */
    public static Map<Integer, BasePlayPanelItemInstance> createMenu(ChestInventoryPanel panel,Player player){
        Map<Integer,BasePlayPanelItemInstance> playPanelItemInstanceMap = new LinkedHashMap<>();
        panel.canPlaceItem = new ArrayList<Integer>(){
            {
                add(10);add(11);add(12);
                add(19);add(20);add(21);
                add(28);add(29);add(30);
            }
        };
        panel.outPutItem = new ArrayList<Integer>(){
            {
                add(15);add(16);add(17);
                add(24);add(25);add(26);
                add(33);add(34);add(35);
            }
        };
        int craft = 0;
        int chest = 0;
        for(int index = 0;index < panel.getInventory().getSize() - 9;index++){
            if(panel.outPutItem.contains(index)){
                chest++;
                continue;
            }
            if(chest > 3){
                chest = 0;
                craft = 0;
            }
            if(craft <= 4 ){
                if(!panel.canPlaceItem.contains(index)) {
                    playPanelItemInstanceMap.put(index, new ButtonWall1());
                }
                craft++;
            }else{
                if(panel.canPlaceItem.contains(index)){
                    chest = 0;
                    continue;
                }
                if(chest == 0){
                    playPanelItemInstanceMap.put(index, new ButtonWall3());
                    chest++;
                }else{
                    playPanelItemInstanceMap.put(index, new ButtonWall2());
                    chest++;
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
    public static Map<Integer, BasePlayPanelItemInstance> recipeLib(ChestInventoryPanel panel,Player player,BasePlayPanelItemInstance itemInstance){
        //TODO 绘制配方展示界面
        return null;
    }

    /**
     * 创建配方界面
     * */
    public static Map<Integer, BasePlayPanelItemInstance> createRecipeLib(ChestInventoryPanel panel,Player player){
        //TODO 绘制创建配方界面
        Map<Integer,BasePlayPanelItemInstance> playPanelItemInstanceMap = createMenu(panel, player);
        panel.isCraft = true;
        playPanelItemInstanceMap.put(49,new ButtonCraft());
        return playPanelItemInstanceMap;
    }
}
