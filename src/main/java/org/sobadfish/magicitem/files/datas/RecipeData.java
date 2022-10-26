package org.sobadfish.magicitem.files.datas;

import org.sobadfish.magicitem.files.BaseDataWriterGetter;
import org.sobadfish.magicitem.files.entity.Recipe;

import java.io.File;
import java.util.List;

/**
 * 配方数据
 * @author Sobadfish
 * @date 2022/10/26
 */
public class RecipeData extends BaseDataWriterGetter<Recipe> {

    public RecipeData(List<Recipe> dataList, File file) {
        super(dataList, file);
    }
}
