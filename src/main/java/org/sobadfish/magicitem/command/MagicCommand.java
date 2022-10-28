package org.sobadfish.magicitem.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.magicitem.MagicItemMainClass;
import org.sobadfish.magicitem.controller.MagicController;
import org.sobadfish.magicitem.files.entity.CustomTagItem;

/**
 * @author Sobadfish
 * @date 2022/10/27
 */
public class MagicCommand extends Command {

    public MagicController magicController;

    public MagicCommand(String name) {
        super(name);
        magicController = MagicItemMainClass.mainClass.getMagicController();
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        MagicController.sendMessageToObject("&e当前仅为测试版，不代表最终品质 版本: v1.0.0",commandSender);
        if(strings.length > 0){
            switch (strings[0]){
                case "help":
                    MagicController.sendMessageToObject("/fmi i add [name] [itemid:damage/hand] 添加自定义物品",commandSender);
                    MagicController.sendMessageToObject("/fmi i give [name] [playerName] 给予玩家自定义物品",commandSender);
                    MagicController.sendMessageToObject("/fmi i save [name]  保存手持物品",commandSender);
                    MagicController.sendMessageToObject("/fmi c [name]  添加指令集",commandSender);
                    MagicController.sendMessageToObject("/fmi cx [name]  添加指令功能",commandSender);
                    break;
                case "i":
                    if(strings.length > 3) {
                        String name = strings[2];
                        if ("add".equalsIgnoreCase(strings[1])) {

                            if ("hand".equalsIgnoreCase(strings[3])) {
                                if (commandSender instanceof Player) {
                                    Item it = ((Player) commandSender).getInventory().getItemInHand();
                                    if (it.getId() > 0) {
                                        CustomTagItem customTagItem = magicController.tagController.createDefaultItemInHand(name, it);
                                        customTagItem.lore = new String[]{TextFormat.colorize('&'
                                                ,"&r初始自定义物品"),
                                                TextFormat.colorize('&'
                                                        ,"&r&e在配置文件修改lore显示")};
                                    } else {
                                        MagicController.sendMessageToObject("&c不要手持空气", commandSender);
                                        return true;
                                    }

                                }
                            }
                            Item i = Item.fromString(strings[3]);
                            if (i.getId() > 0) {
                                magicController.tagController.createDefaultItemInHand(name, i);
                            } else {
                                MagicController.sendMessageToObject("&c未知物品", commandSender);
                                return true;
                            }
                            MagicController.sendMessageToObject("&a创建成功", commandSender);

                        } else if("save".equalsIgnoreCase(strings[1])){
                            if (commandSender instanceof Player) {
                                if (magicController.tagController.getTagData().hasItem(name)) {
                                    MagicController.sendMessageToObject("&r" + name + " &c已存在", commandSender);
                                    return false;
                                }
                                magicController.tagController.getTagData().addItem(name,((Player) commandSender).getInventory().getItemInHand());
                                MagicController.sendMessageToObject("&a添加成功!", commandSender);

                            }else{
                                MagicController.sendMessageToObject("&c控制台无法执行此指令", commandSender);
                            }

                        }else{
                            if (commandSender instanceof Player) {
                                Item item = magicController.tagController.getCustomTagData().getItemByName(name, magicController.tagController);
                                if (item == null) {
                                    item = magicController.tagController.getTagData().asItem(name);
                                }
                                if (item.getId() > 0) {
                                    ((Player) commandSender).getInventory().addItem(item);
                                    MagicController.sendMessageToObject("&c给予成功", commandSender);
                                }
                            }else{
                                MagicController.sendMessageToObject("&c控制台无法执行此指令", commandSender);
                            }

                        }

                    } else{
                        MagicController.sendMessageToObject("/fmi help 查看帮助",commandSender);
                        return false;
                    }

                    break;
                case "c":
                    if(strings.length > 1){
                        String name = strings[1];
                        if(magicController.commandCollect.getCommandData().addCommandData(name)){
                            MagicController.sendMessageToObject("添加完成",commandSender);
                        }else{
                            MagicController.sendMessageToObject("添加失败",commandSender);
                        }
                    }else{
                        MagicController.sendMessageToObject("/fmi help 查看帮助",commandSender);
                        return false;
                    }

                    break;
                case "cx":
                    if(strings.length > 1){
                        String name = strings[1];
                        if(magicController.commandCollect.getCommandExData().addCommandExData(name)){
                            MagicController.sendMessageToObject("添加完成",commandSender);
                        }else{
                            MagicController.sendMessageToObject("添加失败",commandSender);
                        }
                    }else{
                        MagicController.sendMessageToObject("/fmi help 查看帮助",commandSender);
                        return false;
                    }
                    break;

                default:
                    MagicController.sendMessageToObject("/fmi help 查看帮助",commandSender);
            }
        }
        return false;
    }
}
