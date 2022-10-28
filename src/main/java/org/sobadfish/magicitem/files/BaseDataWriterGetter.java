package org.sobadfish.magicitem.files;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.sobadfish.magicitem.controller.MagicController;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * JSON数据文件读写类
  @author Sobadfish
 */
public class BaseDataWriterGetter<T>{

    public List<T> dataList;

    public File file;

    public BaseDataWriterGetter(List<T> dataList, File file){
        this.dataList = dataList;
        this.file = file;
    }

    public static <T> BaseDataWriterGetter<?> asFile(File file, String fileName,String outputFile, Class<? extends BaseDataWriterGetter<?>> baseClass){
        Gson gson = new Gson();
        InputStreamReader reader = null;
        try {
            if(!file.exists()){
                if(outputFile != null){
                    MagicController.saveResource(fileName,outputFile,false);
                }else{
                    MagicController.saveResource(fileName,false);
                }

            }
            reader = new InputStreamReader(new FileInputStream(file));
            List<T> data = gson.fromJson(reader, new TypeToken<List<T>>() {}.getType());
            Constructor<?> constructor = baseClass.getConstructor(List.class,File.class);
            if(data != null){
                return (BaseDataWriterGetter<?>) constructor.newInstance(data,file);
            }else{
                return (BaseDataWriterGetter<?>) constructor.newInstance(new ArrayList<>(),file);
            }


        } catch (IOException  e) {
            MagicController.sendLogger("&c无法读取 "+file.getName()+" 配置文件");
            e.printStackTrace();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            if(reader !=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    MagicController.sendLogger("&c"+ e.getLocalizedMessage());
                }
            }
        }
        return null;
    }

    public static <T> BaseDataWriterGetter<?> asFile(File file, String fileName, Class<? extends BaseDataWriterGetter<?>> baseClass){
        return asFile(file, fileName,null, baseClass);
    }

    public void save(){
        Gson gson = new Gson();
        if(!file.exists()){
            try {
                if(!file.createNewFile()){
                    MagicController.sendLogger("&c创建文件失败");
                }
            } catch (IOException e) {
                MagicController.sendLogger("未知错误 无法保存数据");
            }
        }
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
            String json = gson.toJson(dataList);
            writer.write(json,0,json.length());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
