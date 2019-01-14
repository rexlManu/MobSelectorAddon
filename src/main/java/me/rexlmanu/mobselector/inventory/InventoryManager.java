package me.rexlmanu.mobselector.inventory;

import com.google.common.collect.Sets;
import lombok.Getter;

import java.util.Set;

public final class InventoryManager {

    @Getter
    private final Set<ServerInventory> serverInventories;

    public InventoryManager() {
        this.serverInventories = Sets.newHashSet();
    }
}
