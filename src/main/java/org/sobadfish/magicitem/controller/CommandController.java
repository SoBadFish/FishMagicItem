package org.sobadfish.magicitem.controller;

import com.google.gson.reflect.TypeToken;
import org.sobadfish.magicitem.files.BaseDataWriterGetter;
import org.sobadfish.magicitem.files.datas.CommandData;
import org.sobadfish.magicitem.files.datas.CommandExData;
import org.sobadfish.magicitem.files.entity.CommandCollect;
import org.sobadfish.magicitem.files.entity.CommandEx;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Sobadfish
 * @date 2022/10/26
 */
public class CommandController {

    private CommandData commandData;

    private CommandExData commandExData;

    private CommandController(){}


    static CommandController initCommand(){
        CommandController controller = new CommandController();
        controller.commandData = (CommandData) BaseDataWriterGetter.asFile(new File(MagicController.getDataFolder()+"/command/cmd.json"),"cmd.json","command/cmd.json",new TypeToken<ArrayList<CommandCollect>>(){}.getType(),CommandData.class);
        controller.commandExData = (CommandExData) BaseDataWriterGetter.asFile(new File(MagicController.getDataFolder()+"/command/cmdex.json"),"cmdex.json","command/cmdex.json",new TypeToken<ArrayList<CommandEx>>(){}.getType(),CommandExData.class);
        return controller;
    }


    public CommandData getCommandData() {
        return commandData;
    }

    public CommandExData getCommandExData() {
        return commandExData;
    }

    public void save(){
        if(commandData != null) {
            commandData.save();
        }
        if(commandExData != null) {
            commandExData.save();
        }
    }
}
