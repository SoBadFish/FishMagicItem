package org.sobadfish.magicitem.controller;

import cn.nukkit.Player;
import org.sobadfish.magicitem.windows.items.BasePlayPanelItemInstance;

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
    public static Map<Integer, BasePlayPanelItemInstance> createMenu(Player player){
        //TODO 绘制主页面
        return null;
    }

    /**
     * 配方库列表界面
     * */
    public static Map<Integer, BasePlayPanelItemInstance> recipeListLib(Player player){
        //TODO 绘制配方列表界面
        return null;
    }

    /**
     * 配方库界面
     * */
    public static Map<Integer, BasePlayPanelItemInstance> recipeLib(Player player,BasePlayPanelItemInstance itemInstance){
        //TODO 绘制配方展示界面
        return null;
    }

    /**
     * 创建配方界面
     * */
    public static Map<Integer, BasePlayPanelItemInstance> createRecipeLib(Player player){
        //TODO 绘制创建配方界面
        return null;
    }
}
