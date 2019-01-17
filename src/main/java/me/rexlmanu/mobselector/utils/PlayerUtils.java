package me.rexlmanu.mobselector.utils;

import me.rexlmanu.mobselector.MobSelector;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerUtils {

    public static void sendPlayerToServer(final Player player, final String serverName) {
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            dataOutputStream.writeUTF("Connect");
            dataOutputStream.writeUTF(serverName);
            player.sendPluginMessage(MobSelector.getInstance(), "BungeeCord", byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
