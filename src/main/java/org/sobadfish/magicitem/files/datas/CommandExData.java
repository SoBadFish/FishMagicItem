package org.sobadfish.magicitem.files.datas;

import org.sobadfish.magicitem.files.BaseDataWriterGetter;
import org.sobadfish.magicitem.files.entity.CommandEx;

import java.io.File;
import java.util.ArrayList;

/**
 * 指令集功能数据文件
 * @author Sobadfish
 * @date 2022/10/26
 */
public class CommandExData extends BaseDataWriterGetter<CommandEx> {

    public CommandExData(ArrayList<CommandEx> dataList, File file) {
        super(dataList, file);
    }

    public boolean addCommandExData(String name){
        CommandEx collect = CommandEx.asName(name);
        if(dataList.contains(collect)){
            return false;
        }
        boolean b = dataList.add(collect);
        save();
        return b;
    }

    public CommandEx getDataByName(String name){
        if(dataList.contains(CommandEx.asName(name))){
            return dataList.get(dataList.indexOf(CommandEx.asName(name)));
        }
        return null;
    }
}
