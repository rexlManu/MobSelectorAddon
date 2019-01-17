package me.rexlmanu.mobselector.inventory;

import de.klarcloudservice.KlarCloudAPISpigot;
import de.klarcloudservice.meta.info.ServerInfo;
import de.klarcloudservice.meta.server.ServerGroup;
import lombok.Data;
import me.rexlmanu.mobselector.MobSelector;
import me.rexlmanu.mobselector.configuration.ConfigManager;
import me.rexlmanu.mobselector.mob.defaults.MobSelectorServer;
import me.rexlmanu.mobselector.utils.InventorySideFiller;
import me.rexlmanu.mobselector.utils.ItemBuilder;
import me.rexlmanu.mobselector.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;

@Data
public final class ServerInventory {

    private final ConfigManager.MobSelectorSettings MOB_SELECTOR_SETTINGS;

    private final Player player;
    private MobSelectorServer mobSelectorServer;
    private final Inventory inventory;

    ServerInventory(final Player player, final MobSelectorServer mobSelectorServer) {
        this.MOB_SELECTOR_SETTINGS = MobSelector.getInstance().getConfigManager().getMobSelectorSettings();
        this.player = player;
        this.mobSelectorServer = mobSelectorServer;
        this.inventory = Bukkit.createInventory(null,
                MOB_SELECTOR_SETTINGS.getInventoryRows() * 9,
                ChatColor.translateAlternateColorCodes('&',
                        MOB_SELECTOR_SETTINGS.getInventoryDisplayName()));
    }

    void show() {
        InventorySideFiller.fillSidesWithItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 15).build(), inventory);
        this.update();
        this.player.openInventory(this.inventory);
    }

    void update() {
        InventorySideFiller.getNonSideSlots(inventory).forEach(slot -> this.inventory.setItem(slot, null));
        final ServerGroup serverGroup = KlarCloudAPISpigot.getInstance().getInternalCloudNetwork().getServerGroups().get(this.mobSelectorServer.getServerName());
        if (serverGroup == null) return;
        if (serverGroup.isMaintenance()) return;
        final List<ServerInfo> serverInfos = KlarCloudAPISpigot.getInstance().getInternalCloudNetwork().getServerProcessManager().getAllRegisteredServerGroupProcesses(this.mobSelectorServer.getServerName());
        if (serverInfos.isEmpty()) return;
        serverInfos.sort(Comparator.comparing(o -> o.getCloudProcess().getName()));
        serverInfos.forEach(serverInfo -> {
            List<String> lore;
            Material material;
            int data;
            int online = serverInfo.getOnlinePlayers().size();
            int maxPlayers = serverInfo.getServerGroup().getAdvancedConfiguration().getMaxPlayers();

            if (serverInfo.getServerGroup().isMaintenance()) {
                lore = MOB_SELECTOR_SETTINGS.getLoreMaintenance();
                ConfigManager.SimpleItemStack simpleItemStack = MOB_SELECTOR_SETTINGS.getMaintenanceServerItem();
                material = simpleItemStack.getMaterial();
                data = simpleItemStack.getData();
            } else if (serverInfo.getOnlinePlayers().isEmpty()) {
                lore = MOB_SELECTOR_SETTINGS.getLoreEmpty();
                ConfigManager.SimpleItemStack simpleItemStack = MOB_SELECTOR_SETTINGS.getEmptyServerItem();
                material = simpleItemStack.getMaterial();
                data = simpleItemStack.getData();
            } else if (online >= maxPlayers) {
                lore = MOB_SELECTOR_SETTINGS.getLoreFull();
                ConfigManager.SimpleItemStack simpleItemStack = MOB_SELECTOR_SETTINGS.getFullServerItem();
                material = simpleItemStack.getMaterial();
                data = simpleItemStack.getData();
            } else {
                lore = MOB_SELECTOR_SETTINGS.getLoreOnline();
                ConfigManager.SimpleItemStack simpleItemStack = MOB_SELECTOR_SETTINGS.getOnlineServerItem();
                material = simpleItemStack.getMaterial();
                data = simpleItemStack.getData();
            }
            for (int i = 0; i < lore.size(); i++)
                lore.set(i, lore.get(i).replace("%online_players%", String.valueOf(online)).replace("%max_players%", String.valueOf(maxPlayers)));
            ItemBuilder itemBuilder = new ItemBuilder(material, 1, data).addLore(lore).setDisplayName(MOB_SELECTOR_SETTINGS.getServerNamePrefix() + serverInfo.getCloudProcess().getName());
            this.inventory.addItem(itemBuilder.build());
        });
    }

    void close() {}

    public void signalClick(final int slot) {
        final ItemStack clickedItemStack = this.inventory.getItem(slot);
        if (clickedItemStack == null) return;
        if (!clickedItemStack.getItemMeta().hasDisplayName()) return;
        final String displayName = clickedItemStack.getItemMeta().getDisplayName();
        String prefix = ChatColor.translateAlternateColorCodes('&', MOB_SELECTOR_SETTINGS.getServerNamePrefix());
        if (!displayName.startsWith(prefix)) return;
        final String serverName = displayName.replace(prefix, "");
        player.sendMessage(MOB_SELECTOR_SETTINGS.getConnectMessage().replace("%server%", serverName));
        PlayerUtils.sendPlayerToServer(player, serverName);
    }
}
