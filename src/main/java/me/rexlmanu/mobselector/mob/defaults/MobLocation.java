package me.rexlmanu.mobselector.mob.defaults;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@Data
@AllArgsConstructor
public class MobLocation {

    public static MobLocation get(Location location) {
        return new MobLocation(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw(), location.getWorld().getName());
    }

    private double x, y, z;
    private float pitch, yaw;

    private String world;

    public Location getAsLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }
}
