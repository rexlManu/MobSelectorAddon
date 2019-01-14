package me.rexlmanu.mobselector.configuration;

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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.logging.Level;

public final class ConfigManager {

    private final File file;
    private JsonObject jsonObject;
    @Getter
    private final Set<MobSelectorServer> mobSelectorServers;
    @Getter
    private MobSelectorSettings mobSelectorSettings;

    public ConfigManager() {
        this.file = new File(MobSelector.getInstance().getDataFolder(), "config.json");
        this.mobSelectorServers = Sets.newHashSet();
        this.jsonObject = new JsonObject();
        this.mobSelectorSettings = new MobSelectorSettings();

        if (!file.exists())
            try {
                file.createNewFile();
                this.jsonObject.add("mobSelectorServers", this.convertObjectToJsonElement(this.mobSelectorServers));
                this.jsonObject.add("mobSelectorSettings", this.convertObjectToJsonElement(this.mobSelectorSettings));
                FileUtils.writeToFile(this.file.toPath(), KlarCloudLibrary.GSON.toJson(this.jsonObject));
            } catch (IOException e) {
                MobSelector.getInstance().getLogger().log(Level.SEVERE, "An error occurred while creating the configuration: ".concat(e.getMessage()));
            }

        try {
            this.jsonObject = KlarCloudLibrary.PARSER.parse(Files.toString(this.file, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (IOException e) {
            MobSelector.getInstance().getLogger().log(Level.SEVERE, "An error occurred while loading the configuration: ".concat(e.getMessage()));
        }

        loadMobSelectorServers();
        loadMobSelectorSettings();
    }

    private JsonElement convertObjectToJsonElement(Object object) {
        return KlarCloudLibrary.PARSER.parse(KlarCloudLibrary.GSON.toJson(object));
    }

    private void loadMobSelectorSettings() {
        this.mobSelectorSettings = KlarCloudLibrary.GSON.fromJson(ChatColor.translateAlternateColorCodes('&',
                this.jsonObject.get("mobSelectorSettings").toString()), MobSelectorSettings.class);
    }

    private void loadMobSelectorServers() {
        jsonObject.getAsJsonArray("mobSelectorServers").forEach(jsonElement ->
                this.mobSelectorServers.add(KlarCloudLibrary.GSON.fromJson(jsonElement, MobSelectorServer.class)));
    }

    @Data
    public class MobSelectorSettings {
        private String permissionMessage = "&cI'm sorry but you do not have permission to perform this command." +
                " Please contact the server administrator if you believe that this is in error.",
                consoleSender = "&cPlease execute this command as a player.",
                inventoryDisplayName = "&8» &4Mobselector";
    }
}
