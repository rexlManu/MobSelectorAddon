package me.rexlmanu.mobselector.mob;

import com.google.common.collect.Lists;
import de.klarcloudservice.KlarCloudAPISpigot;
import de.klarcloudservice.meta.server.ServerGroup;
import me.rexlmanu.mobselector.MobSelector;
import me.rexlmanu.mobselector.configuration.ConfigManager;
import me.rexlmanu.mobselector.mob.defaults.MobSelectorServer;
import me.rexlmanu.mobselector.mob.defaults.ServerMob;
import me.rexlmanu.mobselector.utils.MobUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class MobManager {

    private final List<Entity> spawnedEntities;

    public MobManager() {
        this.spawnedEntities = Lists.newArrayList();
    }

    public void createMob(final ServerGroup serverGroup, final ServerMob serverMob) {
        final ConfigManager configManager = MobSelector.getInstance().getConfigManager();
        final Set<MobSelectorServer> mobSelectorServers = configManager.getMobSelectorServers();
        final MobSelectorServer selectorServer = mobSelectorServers
                .stream()
                .filter(mobSelectorServer -> mobSelectorServer.getServerName().equals(serverGroup.getName()))
                .findFirst().orElse(new MobSelectorServer(serverGroup.getName(), Lists.newArrayList()));
        if (!mobSelectorServers.contains(selectorServer))
            configManager.getMobSelectorServers().add(selectorServer);
        selectorServer.getServerMobs().add(serverMob);
        this.spawnMob(serverMob);
        configManager.save();
    }

    public void spawnMob(final ServerMob serverMob) {
        final Location spawnLocation = serverMob.getSpawnLocation().getAsLocation();
        final Entity spawnEntity = spawnLocation.getWorld().spawnEntity(spawnLocation, serverMob.getEntityType());
        spawnEntity.setCustomNameVisible(true);
        spawnEntity.setCustomName(serverMob.getDisplayName());
        spawnEntity.setMetadata("serverMob", new FixedMetadataValue(MobSelector.getInstance(), serverMob.hashCode()));
        this.spawnedEntities.add(spawnEntity);
        MobUtils.setAiEnabled(spawnEntity, false);
    }

    public ServerMob getServerMobByEntity(final Entity entity) {
        if (!entity.hasMetadata("serverMob"))
            return null;
        for (final MobSelectorServer mobSelectorServer : MobSelector.getInstance().getConfigManager().getMobSelectorServers()) {
            final Optional<ServerMob> optionalServerMob = mobSelectorServer.getServerMobs().stream().filter(serverMob ->
                    entity.getMetadata("serverMob").get(0).asInt() == serverMob.hashCode()).findFirst();
            if (optionalServerMob.isPresent())
                return optionalServerMob.get();
        }
        return null;
    }

    public void spawnAllMobs() {
        MobSelector.getInstance().getConfigManager().getMobSelectorServers().forEach(mobSelectorServer ->
                mobSelectorServer.getServerMobs().forEach(this::spawnMob));
    }

    public void despairAllMobs() {
        this.spawnedEntities.forEach(Entity::remove);
    }

    public void removeMob(final Entity entity) {
        this.spawnedEntities.remove(entity);
        entity.remove();
    }

}
