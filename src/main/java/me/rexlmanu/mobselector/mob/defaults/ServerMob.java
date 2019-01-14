package me.rexlmanu.mobselector.mob.defaults;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

@Data
@AllArgsConstructor
public final class ServerMob {

    private final Location spawnLocation;
    private final String displayName;
    private final EntityType entityType;
}
