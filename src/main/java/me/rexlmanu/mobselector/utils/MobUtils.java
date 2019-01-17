package me.rexlmanu.mobselector.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.List;

public final class MobUtils {

    private static Method getHandle;
    private static Method getNBTTag;
    private static Class<?> nbtTagClass;
    private static Method c;
    private static Method setInt;
    private static Method f;

    static {
        String serverVersion = Bukkit.getServer().getClass().getName().split("\\.")[3];
        try {
            Class<?> craftEntity = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".entity.CraftEntity");
            MobUtils.getHandle = craftEntity.getDeclaredMethod("getHandle");
            MobUtils.getHandle.setAccessible(true);
            Class<?> nmsEntityClass = Class.forName("net.minecraft.server." + serverVersion + ".Entity");
            MobUtils.getNBTTag = nmsEntityClass.getDeclaredMethod("getNBTTag");
            MobUtils.getNBTTag.setAccessible(true);
            MobUtils.nbtTagClass = Class.forName("net.minecraft.server." + serverVersion + ".NBTTagCompound");
            MobUtils.c = nmsEntityClass.getDeclaredMethod("c", MobUtils.nbtTagClass);
            MobUtils.c.setAccessible(true);
            MobUtils.setInt = MobUtils.nbtTagClass.getDeclaredMethod("setInt", String.class, Integer.TYPE);
            MobUtils.setInt.setAccessible(true);
            MobUtils.f = nmsEntityClass.getDeclaredMethod("f", MobUtils.nbtTagClass);
            MobUtils.f.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static void setAiEnabled(final Entity entity, final boolean enabled) {
        try {
            Object nmsEntity = MobUtils.getHandle.invoke(entity);
            Object tag = MobUtils.getNBTTag.invoke(nmsEntity);
            if (tag == null) {
                tag = MobUtils.nbtTagClass.newInstance();
            }
            MobUtils.c.invoke(nmsEntity, tag);
            MobUtils.setInt.invoke(tag, "NoAI", enabled ? 0 : 1);
            MobUtils.f.invoke(nmsEntity, tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isLookingAt(final Player player, final Entity target) {
        final Location eyeLocation = player.getEyeLocation();
        double dot = target.getLocation().toVector().subtract(eyeLocation.toVector()).normalize().dot(eyeLocation.getDirection());
        player.sendMessage(String.format("Debug: dot=%s", dot));
        return dot > 0.99D;
    }

    public static Entity getLookingEntity(final Player player) {
        final List<Entity> nearbyEntities = player.getNearbyEntities(10, 10, 10);
        for (final Entity nearbyEntity : nearbyEntities) {
            if (MobUtils.isLookingAt(player, nearbyEntity))
                return nearbyEntity;
        }
        return null;
    }


}
