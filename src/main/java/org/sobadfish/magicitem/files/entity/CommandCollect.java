package org.sobadfish.magicitem.files.entity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import org.sobadfish.magicitem.MagicItemMainClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 指令集
 * @author Sobadfish
 * @date 2022/10/26
 */
public class CommandCollect {

    /**
     * 指令集名称
     * */
    public String name;

    /**
     * 指令集响应类型
     * */
    public Trigger trigger;

    /**
     * 小数点概率
     * */
    public double round = 1;

    /**
     * 触发的指令
     * */
    public CommandEx[] command = new CommandEx[0];

    /**
     * 范围
     * */
    public int size = 1;


    /**
     * 响应的指令
     * */
    public CommandEx[] responseCommand;


    /**
     * 触发响应
     * @param trigger 触发条件
     * @param player 触发的玩家
     * @param target 响应实体
     *
     * @return 触发类型
     * */
    public void activateCommand(Trigger trigger, Player player, Entity... target){
        if(trigger == this.trigger){
            //最低5位
            boolean key = false;
            if(this.round >= 1){
                key = true;
            }else{
                double r = new BigDecimal(this.round).setScale(5, RoundingMode.DOWN).doubleValue();
                int i = pow(r);
                int radix = 10;
                if(random(Math.pow(radix,i)) <= r * (Math.pow(radix,i))) {
                    key = true;
                }

            }
           if(key){
               //放在线程后台执行
               Server.getInstance().getScheduler().scheduleTask(MagicItemMainClass.mainClass, () -> {
                   //TODO 成功执行
                   for(CommandEx cmd: command){
                       cmd.freeEntity(player);
                   }
                   if(target.length > 0) {
                       for (CommandEx commandEx : responseCommand) {
                           for (Entity entity : target) {
                               commandEx.freeEntity(entity);
                           }
                       }
                   }
               });
            }
        }
    }

    public enum TriggerType{
        /**
         * 成功触发
         * */
        SUCCESS,
        /**
         * 触发失败,没随机到
         * */
        ERROR,
        /**
         * 冷却中
         * */
        COOL
    }

    private int random(double max){
        return (int) (Math.random() * max);
    }

    private int pow(double d){
        int i = 0;
        while (d < 1){
            d *= 10;
            i++;
        }
        return i;
    }

    /**
     * 触发方式
     * */
    public enum Trigger{
        /**
         * 丢弃触发
         * */
        DROP,
        /**
         * 右键触发
         * */
        RIGHT_CLICK,
        /**
         * 左键触发
         * */
        LEFT_CLICK,
        /**
         * 攻击生物触发
         * */
        ATTACK_ENTITY,
        /**
         * 手持触发
         * */
        HAND
    }
}
