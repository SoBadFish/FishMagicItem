package org.sobadfish.magicitem.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import org.sobadfish.magicitem.MagicItemMainClass;
import org.sobadfish.magicitem.controller.MagicController;

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
                    MagicController.sendMessageToObject("/fmi i add/give [name] [itemid:damage/hand] 添加自定义物品",commandSender);
                    MagicController.sendMessageToObject("/fmi c [name]  添加指令集",commandSender);
                    MagicController.sendMessageToObject("/fmi cx [name]  添加指令功能",commandSender);
                    break;
                case "i":

                    if("add".equalsIgnoreCase(strings[1])){
                        String name = strings[2];
                        if("hand".equalsIgnoreCase(strings[3])){
                            if(commandSender instanceof Player){
                                Item it = ((Player) commandSender).getInventory().getItemInHand();
                                if(it.getId() > 0){
                                    magicController.tagController.createDefaultItemInHand(name,it);
                                }else{
                                    MagicController.sendMessageToObject("&c不要手持空气",commandSender);
                                    return true;
                                }

                            }
                        }
                        Item i = Item.fromString(strings[2]);
                        if(i.getId() > 0){
                            magicController.tagController.createDefaultItemInHand(name,i);
                        }else{
                            MagicController.sendMessageToObject("&c未知物品",commandSender);
                            return true;
                        }
                        MagicController.sendMessageToObject("&a创建成功",commandSender);

                    }else{
//                        magicController.tagController.getTagData().asItem()
                    }


                    break;
                case "c":

                    break;
                case "cx":

                    break;
                default:
                    MagicController.sendMessageToObject("/fmi help 查看帮助",commandSender);
            }
        }
        return false;
    }
}
