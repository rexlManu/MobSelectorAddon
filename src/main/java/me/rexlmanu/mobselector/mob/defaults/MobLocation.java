package me.rexlmanu.mobselector.mob.defaults;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@Data
@AllArgsConstructor
public class MobLocation {

    public static MobLocation get(final Location location) {
        return new MobLocation(
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getPitch(),
                location.getYaw(),
                location.getWorld().getName());
    }

    private double x, y, z;
    private float pitch, yaw;

    private String world;

    public Location getAsLocation() {
        return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z, this.yaw, this.pitch);
    }
}
