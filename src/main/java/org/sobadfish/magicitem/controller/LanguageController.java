package org.sobadfish.magicitem.controller;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

/**
 * @author Sobadfish
 * @date 2022/10/27
 */
public class LanguageController {

    public MagicController magicController;
    /**
     * 配置文件充当语言文件
     * */
    public Config config;

    public LanguageController(MagicController magicController,Config config){
        this.magicController = magicController;
        this.config = config;
    }

    public Config getConfig() {
        return config;
    }

    public MagicController getMagicController() {
        return magicController;
    }

    public String echoToPlayer(String key){
        return TextFormat.colorize('&',config.getString(key)
                .replace("{%title}",config.getString("plugin-title")));
    }
}
