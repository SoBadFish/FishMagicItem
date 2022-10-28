package org.sobadfish.magicitem.files;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.sobadfish.magicitem.controller.MagicController;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * JSON数据文件读写类
  @author Sobadfish
 */
public class BaseDataWriterGetter<T>{

    public ArrayList<T> dataList;

    public File file;

    public BaseDataWriterGetter(ArrayList<T> dataList, File file){
        this.dataList = dataList;
        this.file = file;
    }

    public static <T> BaseDataWriterGetter<?> asFile(File file, String fileName, String outputFile, Class<T[]> tClass, Class<? extends BaseDataWriterGetter<?>> baseClass){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        InputStreamReader reader = null;
        try {
            if(!file.exists()){
                if(outputFile != null){
                    MagicController.saveResource(fileName,outputFile,false);
                }else{
                    MagicController.saveResource(fileName,false);
                }

            }
            reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            T[] data =  gson.fromJson(reader, tClass);
            Constructor<?> constructor = baseClass.getConstructor(ArrayList.class,File.class);
            if(data != null){
                return (BaseDataWriterGetter<?>) constructor.newInstance(new ArrayList<>(Arrays.asList(data)),file);
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

    public static <T> BaseDataWriterGetter<?> asFile(File file, String fileName,Class<T[]> tClass, Class<? extends BaseDataWriterGetter<?>> baseClass){
        return asFile(file, fileName,null,tClass, baseClass);
    }

    public void save(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
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
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            String json = gson.toJson(dataList);
            writer.write(json,0,json.length());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
