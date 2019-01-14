package me.rexlmanu.mobselector.mob.defaults;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public final class MobSelectorServer {

    private final String serverName;
    private final List<ServerMob> serverMobs;
}
