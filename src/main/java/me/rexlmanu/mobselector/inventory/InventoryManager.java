package me.rexlmanu.mobselector.inventory;

import com.google.common.collect.Sets;
import lombok.Getter;
import me.rexlmanu.mobselector.MobSelector;
import me.rexlmanu.mobselector.mob.defaults.MobSelectorServer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public final class InventoryManager extends BukkitRunnable {

    @Getter
    private final Set<ServerInventory> serverInventories;

    public InventoryManager() {
        this.serverInventories = Sets.newHashSet();
        this.runTaskTimer(MobSelector.getInstance(), 0, 1);
    }

    public void createServerInventory(final Player player, final MobSelectorServer mobSelectorServer) {
        final ServerInventory serverInventory = new ServerInventory(player, mobSelectorServer);
        serverInventory.show();
        this.serverInventories.add(serverInventory);
    }

    @Override
    public void run() {
        this.serverInventories.forEach(ServerInventory::update);
    }

    public void unregister(final ServerInventory serverInventory) {
        this.serverInventories.remove(serverInventory);
    }
}
