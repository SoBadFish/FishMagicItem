package org.sobadfish.magicitem.files.entity;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import org.sobadfish.magicitem.MagicItemMainClass;
import org.sobadfish.magicitem.controller.CommandController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;

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
    public Trigger trigger = Trigger.RIGHT_CLICK;

    /**
     * 小数点概率
     * */
    public double round = 1;

    /**
     * 触发的指令
     * */
    public String[] command = new String[0];

    /**
     * 范围
     * */
    public int size = 1;


    /**
     * 响应的指令
     * */
    public String[] responseCommand = new String[0];

    public CommandCollect(){}

    public CommandCollect(String name){
        this.name = name;
    }

    public static CommandCollect asName(String name){
        return new CommandCollect(name);
    }


    /**
     * 触发响应
     * @param trigger 触发条件
     * @param player 触发的玩家  */
    public TriggerType activateCommand(CommandController collect, Trigger trigger, Entity player){
        if(trigger == this.trigger){
            //TODO 获取范围
            ArrayList<Entity> entities = new ArrayList<>();
            if(size > 0) {
                for (Entity entity : player.getLevel().getEntities()) {
                    if (entity instanceof EntityCreature) {
                        if (entity.distance(player) <= size){
                            entities.add(entity);
                        }
                    }
                }
            }

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
                   for(String cmd: command){
                       CommandEx cx = collect.getCommandExData().getDataByName(cmd);
                       if(cx != null){
                           cx.freeEntity(player);
                       }
                   }
                   if(entities.size() > 0) {
                       for (String commandEx : responseCommand) {
                           CommandEx cx = collect.getCommandExData().getDataByName(commandEx);
                           if(cx != null) {
                               for (Entity entity : entities) {
                                   cx.freeEntity(entity);
                               }
                           }
                       }
                   }
               });
               return TriggerType.SUCCESS;
            }else{
               return TriggerType.ERROR;
           }
        }
        return TriggerType.UNKNOWN;
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
        COOL,
        /**
         * 不符合触发条件
         * */
        UNKNOWN
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
        HAND,
        /**
         * 收到伤害触发
         * */
        DAMAGE
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof CommandCollect){
            CommandCollect that = (CommandCollect) o;
            return name.equals(that.name);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "CommandCollect{" +
                "name='" + name + '\'' +
                ", trigger=" + trigger +
                ", round=" + round +
                ", command=" + Arrays.toString(command) +
                ", size=" + size +
                ", responseCommand=" + Arrays.toString(responseCommand) +
                '}';
    }
}
