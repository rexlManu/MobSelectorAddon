package me.rexlmanu.mobselector.utils;

import com.google.common.collect.Lists;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventorySideFiller {

    public static void fillSidesWithItem(final ItemStack itemStack, final Inventory inventory) {
        InventorySideFiller.getSideSlots(inventory).forEach(slot -> inventory.setItem(slot, itemStack));
    }

    private static List<Integer> getSideSlots(final Inventory inventory) {
        final List<Integer> sideSlots = Lists.newArrayList();
        final int size = inventory.getSize();
        final int rows = size / 9;
        if (rows >= 3) {
            for (int i = 0; i <= 8; i++) sideSlots.add(i);
            for (int s = 8; s < (inventory.getSize() - 9); s += 9) {
                final int lastSlot = s + 1;
                sideSlots.add(s);
                sideSlots.add(lastSlot);
            }
            for (int lr = (inventory.getSize() - 9); lr < inventory.getSize(); lr++) sideSlots.add(lr);
        }
        return sideSlots;
    }

    public static List<Integer> getNonSideSlots(final Inventory inventory) {
        return InventorySideFiller.getNonSideSlots(InventorySideFiller.getSideSlots(inventory), inventory);
    }

    private static List<Integer> getNonSideSlots(final List<Integer> sideSlots, final Inventory inventory) {
        final List<Integer> availableSlots = Lists.newArrayList();
        for (int i = 0; i < inventory.getSize(); i++) if (! sideSlots.contains(i)) availableSlots.add(i);
        return availableSlots;
    }

}
