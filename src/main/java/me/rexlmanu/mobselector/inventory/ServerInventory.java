package me.rexlmanu.mobselector.inventory;

import lombok.Data;
import me.rexlmanu.mobselector.mob.defaults.MobSelectorServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@Data
public final class ServerInventory {

    private final Player player;
    private MobSelectorServer mobSelectorServer;
    private final Inventory inventory;

    public ServerInventory(final Player player, final MobSelectorServer mobSelectorServer) {
        this.player = player;
        this.mobSelectorServer = mobSelectorServer;
        this.inventory = Bukkit.createInventory(null, 6 * 9, "");
    }

    public void show() {

    }

    public void update() {

    }

    public void close() {

    }
}
