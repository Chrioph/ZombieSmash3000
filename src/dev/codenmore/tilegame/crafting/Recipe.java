package dev.codenmore.tilegame.crafting;

import dev.codenmore.tilegame.inventory.Inventory;
import dev.codenmore.tilegame.items.Item;

/**
 * Crafting recipe: turns ingredients from the player's inventory into an item.
 */
public class Recipe {

    private final Item output;
    private final int outputCount;
    private final Item[] ingredients;
    private final int[] amounts;

    /**
     * @param ingredients pairs of ingredient item and amount, e.g. {@code Item.logItem, 2}
     */
    public Recipe(Item output, int outputCount, Object... ingredients) {
        this.output = output;
        this.outputCount = outputCount;
        this.ingredients = new Item[ingredients.length / 2];
        this.amounts = new int[ingredients.length / 2];
        for (int i = 0; i < this.ingredients.length; i++) {
            this.ingredients[i] = (Item) ingredients[2 * i];
            this.amounts[i] = (Integer) ingredients[2 * i + 1];
        }
    }

    public Item getOutput() {
        return output;
    }

    public int getOutputCount() {
        return outputCount;
    }

    public int getIngredientCount() {
        return ingredients.length;
    }

    public Item getIngredient(int i) {
        return ingredients[i];
    }

    public int getAmount(int i) {
        return amounts[i];
    }

    /** How often the recipe can be crafted with what is in the inventory. */
    public int timesCraftable(Inventory inventory) {
        int times = Integer.MAX_VALUE;
        for (int i = 0; i < ingredients.length; i++)
            times = Math.min(times, have(inventory, ingredients[i]) / amounts[i]);
        return times;
    }

    public static int have(Inventory inventory, Item item) {
        Item carried = inventory.getInventoryItemByID(item.getId());
        return carried == null ? 0 : carried.getCount();
    }

    /** Crafts the recipe up to {@code times} times and returns how often it was crafted. */
    public int craft(Inventory inventory, int times) {
        int n = Math.min(times, timesCraftable(inventory));
        if (n <= 0)
            return 0;
        for (int i = 0; i < ingredients.length; i++) {
            Item carried = inventory.getInventoryItemByID(ingredients[i].getId());
            carried.setCount(carried.getCount() - amounts[i] * n);
        }
        inventory.addItem(output.createNew(outputCount * n));
        return n;
    }
}
