package org.sobadfish.magicitem.files.entity;

import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import com.google.gson.annotations.SerializedName;
import org.sobadfish.magicitem.controller.TagController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 合成的配方
 * @author Sobadfish
 * @date 2022/10/26
 */
public class Recipe {

    /**
     * 输入物品
     * */
    public Map<Character,String> inputItem;

    /**
     * 输出物品
     * */
    public String[] outputItem;

    /**A
     * 摆放
     * */
    public String[] recipeIndex;

    @SerializedName("type")
    public int type = -1; // 0 = shapeless, 1 = shaped. Default -1 (legacy)

    @SerializedName("input")
    public Object inputRaw;

    @SerializedName("shape")
    public String[] shapeRaw;

    @SerializedName("output")
    public Object outputRaw;


    // 新增：预编译的配方网格缓存
    private transient Item[] cachedInputGrid;
    private transient List<Item> shapelessIngredients;

    // 新增：编译方法，将字符串配方转换为物品数组
    public void compile(TagController controller) {
        if (inputRaw != null) {
            compileNewFormat(controller);
        } else {
            compileLegacyFormat(controller);
        }
    }

    private void compileNewFormat(TagController controller) {
        // Parse Output
        if (outputRaw instanceof String) {
            outputItem = new String[]{(String) outputRaw};
        } else if (outputRaw instanceof List) {
            List<?> list = (List<?>) outputRaw;
            outputItem = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                outputItem[i] = list.get(i).toString();
            }
        }

        // Parse Input
        if (type == 0 && inputRaw instanceof List) {
            // Shapeless
            shapelessIngredients = new ArrayList<>();
            for (Object o : (List<?>) inputRaw) {
                if (o instanceof String) {
                    shapelessIngredients.add(controller.getTagData().asItem((String) o));
                }
            }
        } else if (type == 1 && inputRaw instanceof Map) {
            // Shaped
            cachedInputGrid = new Item[9];
            Map<?, ?> inputMap = (Map<?, ?>) inputRaw;
            String[] shape = shapeRaw != null ? shapeRaw : recipeIndex;
            
            if (shape == null) return; // Error

            StringBuilder str = new StringBuilder();
            for(String ss: shape){
                String sp = ss.split("(?<=\\G.{3})")[0];
                if(sp.length() < 3) sp = (sp + "   ").substring(0,3);
                str.append(sp);
            }
            String pattern = str.toString();
            if (pattern.length() > 9) pattern = pattern.substring(0, 9);
            while (pattern.length() < 9) pattern += " ";

            char[] chars = pattern.toCharArray();
            for (int i = 0; i < 9; i++) {
                char c = chars[i];
                if (c != ' ') {
                    Object tagObj = inputMap.get(String.valueOf(c)); // Gson Map uses String keys
                    if (tagObj == null) tagObj = inputMap.get(c); // Try char just in case?
                    
                    if (tagObj instanceof String) {
                        cachedInputGrid[i] = controller.getTagData().asItem((String) tagObj).clone();
                    }
                }
            }
        }
    }

    private void compileLegacyFormat(TagController controller) {
        // Legacy Shaped Logic
        type = 1; // Assume shaped for legacy
        cachedInputGrid = new Item[9];
        StringBuilder str = new StringBuilder();
        // 还原配方字符串逻辑只执行一次
        for(String ss: recipeIndex){
            String sp = ss.split("(?<=\\G.{3})")[0];
            if(sp.length() < 3) sp = (sp + "   ").substring(0,3);
            str.append(sp);
        }
        String pattern = str.toString();
        // 补齐或截断到9位
        if (pattern.length() > 9) pattern = pattern.substring(0, 9);
        while (pattern.length() < 9) pattern += " ";

        char[] chars = pattern.toCharArray();
        for (int i = 0; i < 9; i++) {
            char c = chars[i];
            if (c != ' ') {
                String tagStr = inputItem.get(c);
                if (tagStr != null) {
                    cachedInputGrid[i] = controller.getTagData().asItem(tagStr).clone();
                }
            }
        }
    }

    public List<Item> getIngredientItems(TagController controller) {
        if (cachedInputGrid == null && shapelessIngredients == null) {
            compile(controller);
        }
        List<Item> ingredients = new ArrayList<>();
        if (type == 0 && shapelessIngredients != null) {
            ingredients.addAll(shapelessIngredients);
        } else if (cachedInputGrid != null) {
            for (Item item : cachedInputGrid) {
                if (item != null && item.getId() != 0) {
                    ingredients.add(item.clone());
                }
            }
        } else if (inputItem != null) {
            // Fallback for legacy if compile failed or didn't run properly?
            for (String s : inputItem.values()) {
                 ingredients.add(controller.getTagData().asItem(s).clone());
            }
        }
        return ingredients;
    }

    public Map<Integer, Item> getInputGrid(TagController controller) {
        if (cachedInputGrid == null && shapelessIngredients == null) {
            compile(controller);
        }
        Map<Integer, Item> grid = new java.util.HashMap<>();
        if (type == 0 && shapelessIngredients != null) {
            for (int i = 0; i < shapelessIngredients.size(); i++) {
                grid.put(i, shapelessIngredients.get(i).clone());
            }
        } else if (cachedInputGrid != null) {
            for (int i = 0; i < cachedInputGrid.length; i++) {
                if (cachedInputGrid[i] != null && cachedInputGrid[i].getId() != 0) {
                    grid.put(i, cachedInputGrid[i].clone());
                }
            }
        }
        return grid;
    }

    public Map<Integer, Item> getPreviewInputGrid(TagController controller) {
        if (cachedInputGrid == null && shapelessIngredients == null) {
            compile(controller);
        }
        Map<Integer, Item> grid = new java.util.HashMap<>();
        if (type == 0 && shapelessIngredients != null) {
            for (int i = 0; i < shapelessIngredients.size() && i < 9; i++) {
                Item it = shapelessIngredients.get(i);
                if (it != null && it.getId() != 0) {
                    grid.put(i, it.clone());
                }
            }
            return grid;
        }
        if (cachedInputGrid == null) {
            return grid;
        }

        int minR = 3, maxR = -1, minC = 3, maxC = -1;
        for (int i = 0; i < 9; i++) {
            Item it = cachedInputGrid[i];
            if (it != null && it.getId() != 0) {
                int r = i / 3;
                int c = i % 3;
                if (r < minR) minR = r;
                if (r > maxR) maxR = r;
                if (c < minC) minC = c;
                if (c > maxC) maxC = c;
            }
        }
        if (maxR == -1) {
            return grid;
        }
        for (int r = minR; r <= maxR; r++) {
            for (int c = minC; c <= maxC; c++) {
                int from = r * 3 + c;
                Item it = cachedInputGrid[from];
                if (it != null && it.getId() != 0) {
                    int to = (r - minR) * 3 + (c - minC);
                    grid.put(to, it.clone());
                }
            }
        }
        return grid;
    }

    /**
     * 校验是否符合当前合成配方
     * */
    public CraftingResult match(Map<Integer, Item> input, TagController controller){
        if (cachedInputGrid == null && shapelessIngredients == null) {
            compile(controller);
        }

        if (type == 1) {
            // Shaped
            if (cachedInputGrid != null) {
                Map<Integer, Integer> consumption = matchShaped(input);
                if (consumption != null) {
                    return new CraftingResult(true, createOutput(controller), consumption);
                }
            }
        } else if (type == 0) {
            // Shapeless
            if (shapelessIngredients != null) {
                Map<Integer, Integer> consumption = matchShapelessList(input);
                if (consumption != null) {
                    return new CraftingResult(true, createOutput(controller), consumption);
                }
            }
        } else {
            // Legacy Fallback (try both?)
            Map<Integer, Integer> consumption = matchShaped(input);
            if (consumption != null) {
                return new CraftingResult(true, createOutput(controller), consumption);
            }
            // For legacy, maybe shapeless logic too? But legacy data only populates cachedInputGrid
            // So if type is -1, and we populated cachedInputGrid, it's effectively shaped.
        }

        return CraftingResult.failure();
    }

    /**
     * 保留旧方法以兼容 (Deprecated)
     */
    public Item[] math(Map<Integer, Item> input, TagController controller) {
        CraftingResult result = match(input, controller);
        if (result.success) {
            return result.output;
        }
        return new Item[0];
    }

    private Item[] createOutput(TagController controller) {
        ArrayList<Item> output = new ArrayList<>();
        for (String outStr : outputItem) {
            Item out = controller.getTagData().asItem(outStr).clone();
            if (out.hasCompoundTag()) {
                CompoundTag tag = out.getNamedTag();
                if (tag != null && (tag.contains("button") || tag.contains("index"))) {
                    tag.remove("button");
                    tag.remove("index");
                    out.setNamedTag(tag);
                }
            }
            output.add(out);
        }
        return output.toArray(new Item[0]);
    }

    public Item[] getOutputItems(TagController controller) {
        return createOutput(controller);
    }

    private Map<Integer, Integer> matchShaped(Map<Integer, Item> input) {
        java.util.LinkedHashMap<Integer, Integer> consumption = new java.util.LinkedHashMap<>();
        int inputCount = 0;
        for(Item it : input.values()) {
            if(it != null && it.getId() != 0) inputCount++;
        }

        int minR_rec = 3, maxR_rec = -1, minC_rec = 3, maxC_rec = -1;
        for (int i = 0; i < 9; i++) {
            if (cachedInputGrid[i] != null && cachedInputGrid[i].getId() != 0) {
                int r = i / 3;
                int c = i % 3;
                if (r < minR_rec) minR_rec = r;
                if (r > maxR_rec) maxR_rec = r;
                if (c < minC_rec) minC_rec = c;
                if (c > maxC_rec) maxC_rec = c;
            }
        }
        
        if (maxR_rec == -1) return null; 
        
        int h_rec = maxR_rec - minR_rec + 1;
        int w_rec = maxC_rec - minC_rec + 1;
        
        int minR_in = 3, maxR_in = -1, minC_in = 3, maxC_in = -1;
        for (int i = 0; i < 9; i++) {
            Item it = input.get(i);
            if (it != null && it.getId() != 0) {
                int r = i / 3;
                int c = i % 3;
                if (r < minR_in) minR_in = r;
                if (r > maxR_in) maxR_in = r;
                if (c < minC_in) minC_in = c;
                if (c > maxC_in) maxC_in = c;
            }
        }
        
        if (maxR_in == -1) {
            return null; 
        }
        
        int h_in = maxR_in - minR_in + 1;
        int w_in = maxC_in - minC_in + 1;
        
        if (h_rec != h_in || w_rec != w_in) {
            return null;
        }
        
        for (int r = 0; r < h_rec; r++) {
            for (int c = 0; c < w_rec; c++) {
                int recipeIdx = (minR_rec + r) * 3 + (minC_rec + c);
                int inputIdx = (minR_in + r) * 3 + (minC_in + c);
                
                Item expected = cachedInputGrid[recipeIdx];
                Item actual = input.get(inputIdx);
                
                boolean expectedEmpty = (expected == null || expected.getId() == 0);
                boolean actualEmpty = (actual == null || actual.getId() == 0);
                
                if (expectedEmpty != actualEmpty) return null;
                
                if (!expectedEmpty) {
                    boolean checkTag = expected.hasCompoundTag();
                    if (!isItemEqual(expected, actual, true, checkTag)) {
                        return null;
                    }
                    if (actual.getCount() < expected.getCount()) {
                        return null;
                    }
                    consumption.put(inputIdx, expected.getCount());
                }
            }
        }
        
        if (consumption.size() != inputCount) return null;

        return consumption;
    }

    private Map<Integer, Integer> matchShapelessList(Map<Integer, Item> input) {
        // 1. Required items from list
        List<Item> requiredItems = new ArrayList<>(shapelessIngredients);

        // 2. Collect available items from input
        Map<Integer, Item> availableItems = new java.util.HashMap<>(input);
        Map<Integer, Integer> consumption = new java.util.LinkedHashMap<>();

        // 3. Match
        for (Item required : requiredItems) {
            boolean found = false;
            for (Map.Entry<Integer, Item> entry : availableItems.entrySet()) {
                int slot = entry.getKey();
                Item available = entry.getValue();

                if (available == null || available.getId() == 0) continue;

                boolean checkTag = required.hasCompoundTag();
                // Check if items match (ignoring damage if required is wildcard)
                // Assuming standard Nukkit behavior where -1 meta in required matches any meta in available
                // If not, we might need manual check: 
                // boolean damageMatch = required.getDamage() == -1 || required.getDamage() == available.getDamage();
                // But Item.equals usually handles this if checkDamage is true.
                
                if (isItemEqual(required, available, true, checkTag)) {
                    if (available.getCount() >= required.getCount()) {
                        consumption.put(slot, required.getCount());
                        availableItems.remove(slot);
                        found = true;
                        break;
                    }
                }
            }
            if (!found) return null;
        }

        // 4. Check for extra items
        for (Item item : availableItems.values()) {
            if (item != null && item.getId() != 0) return null;
        }

        return consumption;
    }

    private Map<Integer, Integer> matchShapeless(Map<Integer, Item> input) {
        // 1. Collect required items (ignoring empty slots in grid)
        ArrayList<Item> requiredItems = new ArrayList<>();
        for (Item item : cachedInputGrid) {
            if (item != null && item.getId() != 0) {
                requiredItems.add(item);
            }
        }

        // 2. Collect available items from input
        Map<Integer, Item> availableItems = new java.util.HashMap<>(input);
        Map<Integer, Integer> consumption = new java.util.LinkedHashMap<>();

        // 3. Match
        for (Item required : requiredItems) {
            boolean found = false;
            // Find a matching item in available inputs
            for (Map.Entry<Integer, Item> entry : availableItems.entrySet()) {
                int slot = entry.getKey();
                Item available = entry.getValue();

                if (available == null || available.getId() == 0) continue;

                // Skip if this slot is already fully consumed (not possible with current logic as we remove/decrement, 
                // but here we just want to find ONE slot that satisfies ONE requirement)
                // Wait, if we have 2 requirements for Stone, and 1 slot with 2 Stone.
                // Can we satisfy both?
                // Standard Minecraft shapeless: 1 slot = 1 ingredient.
                // So if recipe needs 2 Stone, you need 2 slots with Stone.
                // BUT, custom recipes might allow stack consumption.
                // Let's assume 1 requirement maps to 1 input slot for now to be safe and consistent with Minecraft.
                // UNLESS the required count > 1.
                
                boolean checkTag = required.hasCompoundTag();
                if (required.equals(available, true, checkTag)) {
                    // Check count
                    if (available.getCount() >= required.getCount()) {
                        // Match found!
                        consumption.put(slot, required.getCount());
                        // Remove from available so it's not used again
                        availableItems.remove(slot); 
                        found = true;
                        break;
                    }
                }
            }
            if (!found) return null;
        }

        // 4. Check for extra items
        // availableItems should be empty or only contain air
        for (Item item : availableItems.values()) {
            if (item != null && item.getId() != 0) return null;
        }

        return consumption;
    }


    private boolean isItemEqual(Item item1, Item item2, boolean checkDamage, boolean checkCompoundTag) {
        if (item1.getId() != item2.getId()) {
            return false;
        }
        if (checkDamage && item1.getDamage() != item2.getDamage() && item1.getDamage() != -1) {
            return false;
        }
        if (checkCompoundTag) {
            CompoundTag tag1 = item1.hasCompoundTag() ? item1.getNamedTag() : null;
            CompoundTag tag2 = item2.hasCompoundTag() ? item2.getNamedTag() : null;
            
            // Treat empty tag as null
            if (tag1 != null && tag1.isEmpty()) tag1 = null;
            if (tag2 != null && tag2.isEmpty()) tag2 = null;

            if (tag1 == null && tag2 == null) return true;
            if (tag1 == null || tag2 == null) {
                return false;
            }
            
            boolean equals = tag1.equals(tag2);
            return equals;
        }
        return true;
    }

    public boolean hasOutItem(Item item,TagController controller){
        for(String s: outputItem){
            if(controller.getTagData().asItem(s).equals(item,true,true)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Recipe)) {
            return false;
        }

        Recipe recipe = (Recipe) o;

        if (inputItem != null ? !inputItem.equals(recipe.inputItem) : recipe.inputItem != null) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(outputItem, recipe.outputItem)) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(recipeIndex, recipe.recipeIndex);
    }

    @Override
    public int hashCode() {
        int result = inputItem != null ? inputItem.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(outputItem);
        result = 31 * result + Arrays.hashCode(recipeIndex);
        return result;
    }


    @Override
    public String toString() {
        return "Recipe{" +
                "inputItem=" + inputItem +
                ", outputItem=" + Arrays.toString(outputItem) +
                ", recipeIndex=" + Arrays.toString(recipeIndex) +
                '}';
    }
}
