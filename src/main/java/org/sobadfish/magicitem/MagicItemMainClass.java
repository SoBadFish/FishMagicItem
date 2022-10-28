package org.sobadfish.magicitem;

import cn.nukkit.plugin.PluginBase;
import org.sobadfish.magicitem.command.MagicCommand;
import org.sobadfish.magicitem.controller.MagicController;

/**
 * @author Sobadfish
 * @date 2022/10/25
 */
public class MagicItemMainClass extends PluginBase {

    private MagicController magicController;

    public static MagicItemMainClass mainClass;
    @Override
    public void onEnable() {
        mainClass = this;
        MagicController.sendLogger("正在加载鱼魔法物品!");
        magicController = new MagicController(this);
        this.getServer().getPluginManager().registerEvents(magicController,this);
        this.getServer().getCommandMap().register("fmagic",new MagicCommand("fmi"));
        MagicController.sendLogger("&e鱼魔法物品加载完成!");


    }

    public MagicController getMagicController() {
        return magicController;
    }

    @Override
    public void onDisable() {
        if(magicController != null){
            magicController.save();
        }
    }
}
