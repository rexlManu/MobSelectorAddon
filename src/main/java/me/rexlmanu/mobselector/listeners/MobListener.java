package me.rexlmanu.mobselector.listeners;

import me.rexlmanu.mobselector.MobSelector;
import me.rexlmanu.mobselector.mob.defaults.MobSelectorServer;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public final class MobListener implements Listener {

    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (! entity.isCustomNameVisible()) return;
        for (MobSelectorServer mobSelectorServer : MobSelector.getInstance().getConfigManager().getMobSelectorServers()) {
            if (mobSelectorServer.getServerMobs().stream().anyMatch(serverMob -> entity.getCustomName().equals(serverMob.getDisplayName()) && entity.getType().equals(serverMob.getEntityType()))) {
                MobSelector.getInstance().getInventoryManager().createServerInventory(event.getPlayer(), mobSelectorServer);
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (! entity.isCustomNameVisible()) return;
        MobSelector.getInstance().getConfigManager().getMobSelectorServers().forEach(mobSelectorServer -> {
            if (mobSelectorServer.getServerMobs().stream().anyMatch(serverMob -> entity.getCustomName().equals(serverMob.getDisplayName())
                    && entity.getType().equals(serverMob.getEntityType())))
                event.setCancelled(true);
        });
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        event.setCancelled(MobSelector.getInstance().getMobManager().getServerMobByEntity(event.getEntity()) != null);
    }

}
