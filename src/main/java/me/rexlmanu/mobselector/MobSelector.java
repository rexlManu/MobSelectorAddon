package me.rexlmanu.mobselector;

import lombok.Getter;
import me.rexlmanu.mobselector.commands.MobSelectorCommand;
import me.rexlmanu.mobselector.configuration.ConfigManager;
import me.rexlmanu.mobselector.inventory.InventoryManager;
import me.rexlmanu.mobselector.listeners.InventoryListener;
import me.rexlmanu.mobselector.listeners.MobListener;
import me.rexlmanu.mobselector.mob.MobManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

@Getter
public final class MobSelector extends JavaPlugin {

    @Getter
    private static MobSelector instance;

    private ConfigManager configManager;
    private MobManager mobManager;
    private InventoryManager inventoryManager;

    private MobSelectorCommand mobSelectorCommand;

    @Override
    public void onEnable() {
        instance = this;

        if (this.getDataFolder().mkdir()) getLogger().log(Level.INFO, "The folder for the config has been created.");

        this.configManager = new ConfigManager();
        this.mobManager = new MobManager();
        this.inventoryManager = new InventoryManager();

        this.mobSelectorCommand = new MobSelectorCommand();

        this.registerListeners();

        this.mobManager.spawnAllMobs();
    }

    private void registerListeners() {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new InventoryListener(), this);
        pluginManager.registerEvents(new MobListener(), this);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public void onDisable() {
        this.mobManager.despairAllMobs();
        instance = null;
    }
}
