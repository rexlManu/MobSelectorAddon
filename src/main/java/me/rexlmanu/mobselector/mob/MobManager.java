package me.rexlmanu.mobselector.mob;

import com.google.common.collect.Lists;
import de.klarcloudservice.meta.server.ServerGroup;
import me.rexlmanu.mobselector.MobSelector;
import me.rexlmanu.mobselector.mob.defaults.MobSelectorServer;
import me.rexlmanu.mobselector.mob.defaults.ServerMob;
import me.rexlmanu.mobselector.utils.MobUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.Set;

public final class MobManager {

    public void spawnMob(ServerGroup serverGroup, ServerMob serverMob) {
        final Set<MobSelectorServer> mobSelectorServers = MobSelector.getInstance().getConfigManager().getMobSelectorServers();
        final MobSelectorServer selectorServer = mobSelectorServers
                .stream()
                .filter(mobSelectorServer -> mobSelectorServer.getServerName().equals(serverGroup.getName()))
                .findFirst().orElse(new MobSelectorServer(serverGroup.getName(), Lists.newArrayList()));
        mobSelectorServers.add(selectorServer);
        selectorServer.getServerMobs().add(serverMob);
        final Location spawnLocation = serverMob.getSpawnLocation();
        final Entity spawnEntity = spawnLocation.getWorld().spawnEntity(spawnLocation, serverMob.getEntityType());
        spawnEntity.setCustomNameVisible(true);
        spawnEntity.setCustomName(serverMob.getDisplayName());
        MobUtils.setAiEnabled(spawnEntity, true);
    }
}
