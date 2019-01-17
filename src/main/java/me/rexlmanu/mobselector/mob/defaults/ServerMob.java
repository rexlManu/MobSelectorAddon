package me.rexlmanu.mobselector.mob.defaults;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import org.bukkit.entity.EntityType;

@Data
@AllArgsConstructor
@Setter
public final class ServerMob {

    private MobLocation spawnLocation;
    private String displayName;
    private String entityTypeName;

    @SuppressWarnings("UnstableApiUsage")
    public EntityType getEntityType() {
        final Optional<EntityType> entityTypeOptional = Enums.getIfPresent(EntityType.class, this.entityTypeName.toUpperCase());
        return entityTypeOptional.isPresent() ? entityTypeOptional.get() : EntityType.values()[0];
    }
}
