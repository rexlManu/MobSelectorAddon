package me.rexlmanu.mobselector;

import lombok.Getter;
import me.rexlmanu.mobselector.commands.MobSelectorCommand;
import me.rexlmanu.mobselector.configuration.ConfigManager;
import me.rexlmanu.mobselector.listeners.InventoryListener;
import me.rexlmanu.mobselector.listeners.MobListener;
import me.rexlmanu.mobselector.mob.MobManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class MobSelector extends JavaPlugin {

    @Getter
    private static MobSelector instance;

    private ConfigManager configManager;
    private MobManager mobManager;

    private MobSelectorCommand mobSelectorCommand;

    @Override
    public void onEnable() {
        instance = this;

        this.configManager = new ConfigManager();
        this.mobManager = new MobManager();

        this.mobSelectorCommand = new MobSelectorCommand();

        this.registerListeners();
    }

    private void registerListeners() {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new InventoryListener(), this);
        pluginManager.registerEvents(new MobListener(), this);
    }

    @Override
    public void onDisable() {
        instance = null;
    }
}
