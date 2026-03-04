package org.sobadfish.magicitem.files.entity;

import cn.nukkit.item.Item;
import java.util.Map;

/**
 * Result of a crafting operation check.
 */
public class CraftingResult {
    /**
     * The resulting items of the recipe.
     */
    public Item[] output;

    /**
     * Map of input slot index to the number of items to consume from that slot.
     */
    public Map<Integer, Integer> consumption;

    /**
     * Whether the crafting match was successful.
     */
    public boolean success;

    public CraftingResult(boolean success, Item[] output, Map<Integer, Integer> consumption) {
        this.success = success;
        this.output = output;
        this.consumption = consumption;
    }

    public static CraftingResult failure() {
        return new CraftingResult(false, new Item[0], null);
    }
}
