package org.sobadfish.magicitem.files.datas;

import org.sobadfish.magicitem.files.BaseDataWriterGetter;
import org.sobadfish.magicitem.files.entity.CommandCollect;

import java.io.File;
import java.util.List;

/**
 * 指令集数据文件
 * @author Sobadfish
 * @date 2022/10/26
 */
public class CommandData extends BaseDataWriterGetter<CommandCollect> {

    public CommandData(List<CommandCollect> dataList, File file) {
        super(dataList, file);
    }

}
