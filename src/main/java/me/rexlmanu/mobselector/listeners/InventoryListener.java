package me.rexlmanu.mobselector.listeners;

import me.rexlmanu.mobselector.MobSelector;
import me.rexlmanu.mobselector.inventory.InventoryManager;
import me.rexlmanu.mobselector.inventory.ServerInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Optional;

public final class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        final Optional<ServerInventory> optionalServerInventory = MobSelector.getInstance().getInventoryManager()
                .getServerInventories().stream().filter(serverInventory -> serverInventory.getInventory()
                        .equals(event.getClickedInventory())).findFirst();
        event.setCancelled(optionalServerInventory.isPresent());
        optionalServerInventory.ifPresent(serverInventory -> serverInventory.signalClick(event.getSlot()));
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        final InventoryManager inventoryManager = MobSelector.getInstance().getInventoryManager();
        final Optional<ServerInventory> optionalServerInventory = inventoryManager.getServerInventories().stream()
                .filter(serverInventory -> serverInventory.getInventory().equals(event.getInventory())).findFirst();
        optionalServerInventory.ifPresent(inventoryManager::unregister);
    }
}
