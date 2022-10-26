package org.sobadfish.magicitem.windows;

import cn.nukkit.Player;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.magicitem.windows.items.BasePlayPanelItemInstance;


import java.util.ArrayList;
import java.util.List;

/**
 * GUI菜单
 * @author SoBadFish
 * 2022/1/12
 */
public class WindowFrom {

    private final int id;


    private List<BasePlayPanelItemInstance> baseButtons = new ArrayList<>();

    private final String title;

    private final String context;
    public WindowFrom(String title, String context, int id){
        this.title = title;
        this.context = context;
        this.id = id;
    }

    public List<BasePlayPanelItemInstance> getBaseButtons() {
        return baseButtons;
    }

    public void setBaseButtons(List<BasePlayPanelItemInstance> baseButtons) {
        this.baseButtons = baseButtons;
    }

    public int getId() {
        return id;
    }


    public void add(BasePlayPanelItemInstance baseButton){
        baseButtons.add(baseButton);
    }

    public void disPlay(Player player){
        FormWindowSimple simple = new FormWindowSimple(TextFormat.colorize('&',title), TextFormat.colorize('&', context));
        for(BasePlayPanelItemInstance baseButton : baseButtons){
            simple.addButton(baseButton.getFromButton(player));
        }
        player.showFormWindow(simple, getId());
    }

    @Override
    public String toString() {
        return id+" -> "+ baseButtons;
    }
}
