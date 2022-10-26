package org.sobadfish.magicitem.files.entity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.potion.Effect;


/**
 * 拓展响应
 * @author Sobadfish
 * @date 2022/10/26
 */
public class CommandEx {

    /**
     * 名称
     * */
    public String name;

    /**
     * 指令
     * */
    public String[] commands;

    /**
     * 药水
     * */
    public String[] effects;




    public void freeEntity(Entity entity){
        for(String cmd: commands){
            if(entity instanceof Player){
                Server.getInstance().getCommandMap().dispatch(new ConsoleCommandSender()
                ,cmd.replace("@p","'"+entity.getName()+"'"));
            }
        }
        for(String effect: effects){
            String[] e = effect.split(":");
            Effect effect1 = Effect.getEffect(Integer.parseInt(e[0]));
            effect1.setDuration(60);
            if(e.length > 1){
              effect1.setAmplifier(Integer.parseInt(e[1]));
            }
            if(e.length > 2){
                effect1.setDuration(Integer.parseInt(e[2]));
            }
            entity.addEffect(effect1);

        }
    }


    @Override
    public String toString() {
        return name;
    }
}
