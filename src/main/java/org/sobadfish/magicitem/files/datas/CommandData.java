package org.sobadfish.magicitem.files.datas;

import org.sobadfish.magicitem.files.BaseDataWriterGetter;
import org.sobadfish.magicitem.files.entity.CommandCollect;

import java.io.File;
import java.util.ArrayList;

/**
 * 指令集数据文件
 * @author Sobadfish
 * @date 2022/10/26
 */
public class CommandData extends BaseDataWriterGetter<CommandCollect> {

    public CommandData(ArrayList<CommandCollect> dataList, File file) {
        super(dataList, file);
    }



    public boolean addCommandData(String name){
        CommandCollect collect = CommandCollect.asName(name);
        if(dataList.contains(collect)){
            return false;
        }
        boolean b = dataList.add(collect);
        save();
        return b;
    }

    public CommandCollect getDataByName(String name){
        if(dataList.contains(CommandCollect.asName(name))){
            return dataList.get(dataList.indexOf(CommandCollect.asName(name)));
        }
        return null;
    }

}
