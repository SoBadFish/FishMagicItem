package org.sobadfish.magicitem.controller;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import org.sobadfish.magicitem.MagicItemMainClass;
import org.sobadfish.magicitem.files.datas.RecipeData;
import org.sobadfish.magicitem.windows.button.*;
import org.sobadfish.magicitem.windows.items.BasePlayPanelItemInstance;
import org.sobadfish.magicitem.windows.panel.CraftItemPanel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 箱子界面控制器 用于绘制界面
 *
 * @author Sobadfish
 * @date 2022/10/26
 */
public class ChestPanelController {

    public static LinkedHashMap<String, PlayerRecipePage> recipePage = new LinkedHashMap<>();

    public static LinkedHashMap<String, PlayerItemPage> itemPage = new LinkedHashMap<>();

    // 判断是否为移动端设备 (Android, iOS, FireOS)
    public static boolean isMobile(Player player) {
        int os = player.getLoginChainData().getDeviceOS();
        // 7 = Windows 10, 8 = Windows 32/Edu
        return os != 7 && os != 8;
    }

    /**
     * 主页面
     *
     */
    public static Map<Integer, BasePlayPanelItemInstance> createMenu(CraftItemPanel panel, Player player) {
        Map<Integer, BasePlayPanelItemInstance> playPanelItemInstanceMap = new LinkedHashMap<>();

        // Move ButtonLib placement to AFTER the loop to avoid overwriting
        // playPanelItemInstanceMap.put(0, new ButtonLib());
        panel.isCraft = false;

        boolean isMobile = isMobile(player);

        // 强制调试：当前设备类型
        MagicController.sendLogger("调试: createMenu 为玩家 " + player.getName() + " 创建菜单. 设备OS: " + player.getLoginChainData().getDeviceOS() + " (isMobile=" + isMobile + ")");

        panel.canPlaceItem = new ArrayList<Integer>();
        panel.inputItem = new ArrayList<Integer>();
        panel.outPutItem = new ArrayList<Integer>();

        if (isMobile) {
            int[] mobileInputs = {7, 8, 9, 13, 14, 15, 19, 20, 21};
            for (int i : mobileInputs) {
                if (!panel.canPlaceItem.contains(i)) {
                    panel.canPlaceItem.add(i);
                }
                if (!panel.inputItem.contains(i)) {
                    panel.inputItem.add(i);
                }
            }

            int[] mobileOutputs = {31, 32, 33, 37, 38, 39, 43, 44, 45};
            for (int i : mobileOutputs) {
                if (!panel.outPutItem.contains(i)) {
                    panel.outPutItem.add(i);
                }
            }
        } else {
            // PC Inputs (10, 11, 12...)
            int[] pcInputs = {10, 11, 12, 19, 20, 21, 28, 29, 30};
            for (int i : pcInputs) {
                if (!panel.canPlaceItem.contains(i)) {
                    panel.canPlaceItem.add(i);
                }
                if (!panel.inputItem.contains(i)) {
                    panel.inputItem.add(i);
                }
            }

            // PC Outputs
            int[] pcOutputs = {14, 15, 16, 23, 24, 25, 32, 33, 34};
            for (int i : pcOutputs) {
                if (!panel.outPutItem.contains(i)) {
                    panel.outPutItem.add(i);
                }
            }
        }

        int footerStart = 45;
        if (isMobile) {
            // For mobile, we fill everything that isn't input/output with glass/walls
            // The loop below handles "if not input/output, put wall"
            // So we just need to ensure the right wall type is used
            footerStart = 54; // Use ButtonWall1 for everything basically
        }

        for (int index = 0; index < panel.getInventory().getSize(); index++) {
        
            boolean skip = false;
            if (isMobile) {
                // Mobile Mode: Only skip Mobile slots
                // Mobile Inputs: 7, 8, 9, 13, 14, 15, 19, 20, 21
                // Mobile Outputs: 31, 32, 33, 37, 38, 39, 43, 44, 45
                int[] mobileSlots = {7, 8, 9, 13, 14, 15, 19, 20, 21, 31, 32, 33, 37, 38, 39, 43, 44, 45};
                for (int i : mobileSlots) {
                    if (index == i) {
                        skip = true;
                        break;
                    }
                }
            } else {
                // PC Mode: Only skip PC slots
                // PC Inputs: 10, 11, 12, 19, 20, 21, 28, 29, 30
                // PC Outputs: 14, 15, 16, 23, 24, 25, 32, 33, 34
                int[] pcSlots = {10, 11, 12, 19, 20, 21, 28, 29, 30, 14, 15, 16, 23, 24, 25, 32, 33, 34};
                for (int i : pcSlots) {
                    if (index == i) {
                        skip = true;
                        break;
                    }
                }
            }

            if (skip) {
                continue;
            }

            if (index < footerStart) {
                playPanelItemInstanceMap.put(index, new ButtonWall1());
            } else {
                playPanelItemInstanceMap.put(index, new ButtonWall2());
            }
        }

        // Special button placements
        if (isMobile) {
            // Recipe Book at top right (Row 0, Col 5 -> Index 5)
            playPanelItemInstanceMap.put(5, new ButtonLib());
            // Ensure Slot 0 is a wall (it was ButtonLib in Windows layout)
            playPanelItemInstanceMap.put(0, new ButtonWall1());
        } else {
            // Windows: ButtonLib at 0
            playPanelItemInstanceMap.put(0, new ButtonLib());
        }

        //TODO 绘制主页面
        return playPanelItemInstanceMap;
    }

    /**
     * 配方库列表界面
     *
     */
    public static Map<Integer, BasePlayPanelItemInstance> recipeListLib(Player player) {
        //TODO
        recipePage.remove(player.getName());
        Map<Integer, BasePlayPanelItemInstance> playPanelItemInstanceMap = new LinkedHashMap<>();
        //TODO 绘制配方列表界面
        PlayerItemPage playerItemPage;
        boolean isMobile = isMobile(player);

        if (!itemPage.containsKey(player.getName())) {
            playerItemPage = new PlayerItemPage();
            if (isMobile) {
                playerItemPage.count = 27;
            }
            playerItemPage.page = 1;
            RecipeController recipeController = MagicItemMainClass.mainClass.getMagicController().recipeController;
            playerItemPage.items = asPanelItem(recipeController.getRecipesItems());
            itemPage.put(player.getName(), playerItemPage);
        }
        playerItemPage = itemPage.get(player.getName());
        // 更新count以防万一
        if (isMobile) {
            playerItemPage.count = 27;
        } else {
            playerItemPage.count = 36;
        }

        for (int index = 0; index < 9; index++) {
            playPanelItemInstanceMap.put(index, new ButtonWall3());
        }

        playPanelItemInstanceMap.put(4, new ButtonGoCraft());
        int index = 9;
        for (BasePlayPanelItemInstance bi : playerItemPage.getItemByPage()) {
            playPanelItemInstanceMap.put(index++, bi);
        }

        int btnStart = 45;
        int btnEnd = 54;
        int pageBtn = 49;
        int nextBtn = 51;
        int prevBtn = 47;

        if (isMobile) {
            btnStart = 36;
            btnEnd = 45;
            pageBtn = 40;
            nextBtn = 42;
            prevBtn = 38;
        }

        index = btnStart;
        for (; index < btnEnd; index++) {
            playPanelItemInstanceMap.put(index, new ButtonWall3());
        }
        playPanelItemInstanceMap.put(pageBtn, new ButtonPage(playerItemPage.page));
        if (playerItemPage.getMaxPage() > playerItemPage.page + 1) {
            playPanelItemInstanceMap.put(nextBtn, new ButtonPage(playerItemPage.page + 1));
        }
        if (playerItemPage.page > 1) {
            playPanelItemInstanceMap.put(prevBtn, new ButtonPage(playerItemPage.page - 1));
        }

        return playPanelItemInstanceMap;
    }

    // 添加一个新的辅助方法来处理面板清理
    public static void clearCraftPanelData(CraftItemPanel panel) {
        // panel.canPlaceItem.clear(); // 移除这行，保留槽位定义，这样返回时可以继续操作
        // panel.outPutItem.clear();   // 移除这行
        panel.isCraft = false;
        // 增加一个方法来清空物品内容，但不清空定义
        panel.getInventory().clearAll();
    }

    /**
     * 配方库界面
     *
     */
    public static Map<Integer, BasePlayPanelItemInstance> recipeLib(Player player, Item item) {
        Map<Integer, BasePlayPanelItemInstance> playPanelItemInstanceMap = new LinkedHashMap<>();
        boolean isMobile = isMobile(player);
        int[] inputLocation;
        int[] outPutLocation;

        if (isMobile) {
            // Mobile: 6 cols x 9 rows
            // Input: Top Center 3x3
            inputLocation = new int[]{
                7, 8, 9,
                13, 14, 15,
                19, 20, 21
            };
            // Output: Bottom Center 3x3
            outPutLocation = new int[]{
                31, 32, 33,
                37, 38, 39,
                43, 44, 45
            };
        } else {
            // Windows: 9 cols x 6 rows
            // Input: Left 3x3
            inputLocation = new int[]{
                10, 11, 12,
                19, 20, 21,
                28, 29, 30
            };
            // Output: Right 3x3
            outPutLocation = new int[]{
                14, 15, 16,
                23, 24, 25,
                32, 33, 34
            };
        }

        playPanelItemInstanceMap.put(0, new ButtonBackLib());

        // Always fill the background first
        int footerStart = 45;
        if (isMobile) {
            footerStart = 54;
        }
        
        // Calculate inventory size based on platform
        int inventorySize = 54; // Actually DoubleChestFakeInventory is always 54 slots (double chest)

        for (int index = 0; index < inventorySize; index++) {
            boolean skip = false;
            // Don't overwrite the back button
            if (index == 0) {
                continue;
            }
            
            // Check if slot is an input or output slot
            boolean isInput = false;
            for(int i : inputLocation) {
                if(i == index) { isInput = true; break; }
            }
            
            boolean isOutput = false;
            for(int i : outPutLocation) {
                if(i == index) { isOutput = true; break; }
            }
            
            if (isInput || isOutput) {
                continue;
            }

            if (index < footerStart) {
                playPanelItemInstanceMap.put(index, new ButtonWall1());
            } else {
                playPanelItemInstanceMap.put(index, new ButtonWall2());
            }
        }
        
        RecipeController recipeController = MagicItemMainClass.mainClass.getMagicController().recipeController;
        if (recipeController.getRecipeData().buildRecipe.containsKey(item)) {
            PlayerRecipePage playerRecipePage;
            if (!recipePage.containsKey(player.getName())) {
                playerRecipePage = new PlayerRecipePage();
                playerRecipePage.item = item;
                playerRecipePage.page = 1;
                playerRecipePage.recipe = recipeController.getRecipeData().buildRecipe.get(item);
                recipePage.put(player.getName(), playerRecipePage);

            }
            playerRecipePage = recipePage.get(player.getName());
            LinkedHashMap<Integer, Item> itemLinkedHashMap = playerRecipePage.getRecipeByPage();
            //TODO 绘制展示
            for (Map.Entry<Integer, Item> itemEntry : itemLinkedHashMap.entrySet()) {
                playPanelItemInstanceMap.put(inputLocation[itemEntry.getKey()], new ButtoRecipeButton(itemEntry.getValue()));
            }
            List<Item> output = playerRecipePage.recipe.outPut;

            for (int i = 0; i < output.size(); i++) {
                if (i < outPutLocation.length) {
                    playPanelItemInstanceMap.put(outPutLocation[i], new ButtoRecipeButton(output.get(i)));
                }
            }

            int btnStart = 45;
            int btnEnd = 54;
            int pageBtn = 49;
            int nextBtn = 51;
            int prevBtn = 47;
            int fillBtn = 22; // Default for PC (Center arrow position)

            if (isMobile) {
                btnStart = 36;
                btnEnd = 45;
                pageBtn = 40; // Row 6 (36-41). 40 is col 4.
                nextBtn = 42; // Row 7 (42-47). 42 is col 0.
                prevBtn = 38; // Row 6 (36-41). 38 is col 2. (Wait, 37,38,39 are Output). 
                // Fix Mobile Button Positions to avoid overlap with Output (31-33, 37-39, 43-45)
                
                // Output is in center columns (1,2,3 of 6-col grid).
                // Left col (0) and Right cols (4,5) are free.
                // Row 5 (30-35): 30(L), 34,35(R) free.
                // Row 6 (36-41): 36(L), 40,41(R) free.
                // Row 7 (42-47): 42(L), 46,47(R) free.
                
                // Let's put Page controls on the Right side?
                pageBtn = 40; // Row 6 Right
                nextBtn = 46; // Row 7 Right
                prevBtn = 34; // Row 5 Right? Or 41 (Row 6 Right+1)?
                
                // Let's use Row 4 (24-29) - The gap row - for controls!
                // 24, 25, 26, 27, 28, 29.
                // Center is 26, 27.
                fillBtn = 26; 
                
                // Paging buttons can go to Row 8 (48-53) - Bottom row.
                pageBtn = 50;
                prevBtn = 49;
                nextBtn = 51;
            } else {
                 // PC
                 fillBtn = 22; // Center
                 // Paging at bottom
            }

            // Place Fill Button
            playPanelItemInstanceMap.put(fillBtn, new ButtonFillCraft());

            playPanelItemInstanceMap.put(pageBtn, new ButtonPage2(playerRecipePage.page));
            if (playerRecipePage.getMaxPage() > playerRecipePage.page + 1) {
                playPanelItemInstanceMap.put(nextBtn, new ButtonPage2(playerRecipePage.page + 1));
            }
            if (playerRecipePage.page > 1) {
                playPanelItemInstanceMap.put(prevBtn, new ButtonPage2(playerRecipePage.page - 1));
            }

        }
        return playPanelItemInstanceMap;
    }

    /**
     * 创建配方界面
     *
     */
    public static Map<Integer, BasePlayPanelItemInstance> createRecipeLib(CraftItemPanel panel, Player player) {
        //TODO 绘制创建配方界面
        Map<Integer, BasePlayPanelItemInstance> playPanelItemInstanceMap = createMenu(panel, player);
        panel.isCraft = true;
        
        // 在创建模式下，将输出槽位添加到 canPlaceItem 列表中，允许玩家放置物品
        for (Integer slot : panel.outPutItem) {
            if (!panel.canPlaceItem.contains(slot)) {
                panel.canPlaceItem.add(slot);
            }
        }
        
        // 修复按钮位置，移动端放在第4行中间(40)，PC端放在第5行中间(49)
        if (isMobile(player)) {
            playPanelItemInstanceMap.put(40, new ButtonCraft());
        } else {
            playPanelItemInstanceMap.put(49, new ButtonCraft());
        }
        return playPanelItemInstanceMap;
    }

    private static List<BasePlayPanelItemInstance> asPanelItem(List<Item> items) {
        List<BasePlayPanelItemInstance> itemInstances = new ArrayList<>();
        for (Item item : items) {
            itemInstances.add(new ButtoRecipeButton(item));
        }
        return itemInstances;
    }

    public static class PlayerRecipePage {

        public int page = 0;

        public Item item;

        public RecipeData.BuildRecipeOutPutItem recipe;

        public LinkedHashMap<Integer, Item> getRecipeByPage() {
            if (recipe.build.size() > page - 1) {
                return recipe.build.get(page - 1);
            }
            return null;
        }

        public int getMaxPage() {
            return recipe.build.size() - 1;
        }

        @Override
        public String toString() {
            return "PlayerRecipePage{"
                    + "page=" + page
                    + ", item=" + item
                    + ", recipe=" + recipe
                    + '}';
        }
    }

    public static class PlayerItemPage {

        public int page = 1;

        public int count = 36;

        public List<BasePlayPanelItemInstance> items = new ArrayList<>();

        public List<BasePlayPanelItemInstance> getItemByPage() {
            List<BasePlayPanelItemInstance> itemInstances = new ArrayList<>();
            for (int i = (page - 1) * count; i < count + ((page - 1) * count); i++) {
                if (items.size() > i) {
                    itemInstances.add(items.get(i));
                }
            }
            return itemInstances;

        }

        public int getMaxPage() {
            if (items.size() == 0) {
                return 1;
            }
            return (int) Math.ceil(items.size() / (double) count);
        }

    }
}
