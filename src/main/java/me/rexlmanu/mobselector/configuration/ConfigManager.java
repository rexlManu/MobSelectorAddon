package me.rexlmanu.mobselector.configuration;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.klarcloudservice.KlarCloudLibrary;
import de.klarcloudservice.utility.files.FileUtils;
import lombok.Data;
import lombok.Getter;
import me.rexlmanu.mobselector.MobSelector;
import me.rexlmanu.mobselector.mob.defaults.MobSelectorServer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public final class ConfigManager {

    private final File file;
    private JsonObject jsonObject;
    @Getter
    private final Set<MobSelectorServer> mobSelectorServers;
    @Getter
    private MobSelectorSettings mobSelectorSettings;

    @SuppressWarnings("UnstableApiUsage")
    public ConfigManager() {
        this.file = new File(MobSelector.getInstance().getDataFolder(), "config.json");
        this.mobSelectorServers = Sets.newHashSet();
        this.jsonObject = new JsonObject();
        this.mobSelectorSettings = new MobSelectorSettings();

        if (! this.file.exists())
            try {
                if (this.file.createNewFile())
                    MobSelector.getInstance().getLogger().log(Level.INFO,
                            "The configuration file was created successfully\n");
                this.save();
            } catch (final IOException e) {
                MobSelector.getInstance().getLogger().log(Level.SEVERE,
                        "An error occurred while creating the configuration: ".concat(e.getMessage()));
            }

        try {
            this.jsonObject = KlarCloudLibrary.PARSER.parse(
                    Files.toString(this.file, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (final IOException e) {
            MobSelector.getInstance().getLogger().log(Level.SEVERE,
                    "An error occurred while loading the configuration: ".concat(e.getMessage()));
        }

        this.loadMobSelectorServers();
        this.loadMobSelectorSettings();
    }

    public void save() {
        this.jsonObject.add("mobSelectorServers", this.convertObjectToJsonElement(this.mobSelectorServers));
        this.jsonObject.add("mobSelectorSettings", this.convertObjectToJsonElement(this.mobSelectorSettings));
        FileUtils.writeToFile(this.file.toPath(), KlarCloudLibrary.GSON.toJson(this.jsonObject));
    }

    private JsonElement convertObjectToJsonElement(final Object object) {
        return KlarCloudLibrary.PARSER.parse(KlarCloudLibrary.GSON.toJson(object));
    }

    private void loadMobSelectorSettings() {
        this.mobSelectorSettings = KlarCloudLibrary.GSON.fromJson(ChatColor.translateAlternateColorCodes('&',
                this.jsonObject.get("mobSelectorSettings").toString()), MobSelectorSettings.class);
    }

    private void loadMobSelectorServers() {
        this.jsonObject.getAsJsonArray("mobSelectorServers").forEach(jsonElement ->
                this.mobSelectorServers.add(KlarCloudLibrary.GSON.fromJson(jsonElement, MobSelectorServer.class)));
    }

    @Data
    public class MobSelectorSettings {

        private final String permissionMessage =
                "&cI'm sorry but you do not have permission to perform this command." +
                        " Please contact the server administrator if you believe that this is in error.",
                consoleSender = "&cPlease execute this command as a player.",
                inventoryDisplayName = "&8» &4Mobselector",
                serverNamePrefix = "&8» &a",
                connectMessage = "&aYou will be sent to the server %server%.";

        private final List<String> loreOnline = Arrays.asList("",
                "&7Players online&8: &a%online_players%&8/&a%max_players%&r", ""),
                loreFull = Arrays.asList("", "&cThis server is already full.", ""),
                loreEmpty = Arrays.asList("", "&aFeel free to join this server.", ""),
                loreMaintenance = Arrays.asList("", "&bThis server is in maintenance mode.", "");

        private final int inventoryRows = 5;

        private final SimpleItemStack onlineServerItem = new SimpleItemStack(Material.STAINED_CLAY, 5),
                fullServerItem = new SimpleItemStack(Material.STAINED_CLAY, 4),
                emptyServerItem = new SimpleItemStack(Material.STAINED_CLAY, 9),
                maintenanceServerItem = new SimpleItemStack(Material.STAINED_CLAY, 3);
    }

    @Data
    public class SimpleItemStack {

        private final String materialName;
        private final int data;

        SimpleItemStack(final Material material, final int data) {
            this.materialName = material.name();
            this.data = data;
        }

        @SuppressWarnings("UnstableApiUsage")
        public Material getMaterial() {
            final Optional<Material> entityTypeOptional = Enums.getIfPresent(Material.class,
                    this.materialName.toUpperCase());
            return entityTypeOptional.isPresent() ? entityTypeOptional.get() : Material.values()[0];
        }
    }
}
