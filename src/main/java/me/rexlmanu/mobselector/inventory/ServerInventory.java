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


    private final Player player;
    private MobSelectorServer mobSelectorServer;

    private final ConfigManager.MobSelectorSettings mobSelectorSettings;

    private final Inventory inventory;

    ServerInventory(final Player player, final MobSelectorServer mobSelectorServer) {
        this.player = player;
        this.mobSelectorServer = mobSelectorServer;
        this.mobSelectorSettings = MobSelector.getInstance().getConfigManager().getMobSelectorSettings();
        this.inventory = Bukkit.createInventory(null,
                this.mobSelectorSettings.getInventoryRows() * 9,
                ChatColor.translateAlternateColorCodes('&',
                        this.mobSelectorSettings.getInventoryDisplayName()));
    }

    void show() {
        InventorySideFiller.fillSidesWithItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 15).build(),
                this.inventory);
        this.update();
        this.player.openInventory(this.inventory);
    }

    void update() {
        InventorySideFiller.getNonSideSlots(this.inventory).forEach(slot -> this.inventory.setItem(slot, null));
        final ServerGroup serverGroup = KlarCloudAPISpigot.getInstance().getInternalCloudNetwork()
                .getServerGroups().get(this.mobSelectorServer.getServerName());
        if (serverGroup == null) return;
        if (serverGroup.isMaintenance()) return;
        final List<ServerInfo> serverInfos = KlarCloudAPISpigot.getInstance().getInternalCloudNetwork()
                .getServerProcessManager().getAllRegisteredServerGroupProcesses(this.mobSelectorServer.getServerName());
        if (serverInfos.isEmpty()) return;
        serverInfos.sort(Comparator.comparing(o -> o.getCloudProcess().getName()));
        serverInfos.forEach(serverInfo -> {
            final List<String> lore;
            final Material material;
            final int data;
            final int online = serverInfo.getOnlinePlayers().size();
            final int maxPlayers = serverInfo.getServerGroup().getAdvancedConfiguration().getMaxPlayers();

            if (serverInfo.getServerGroup().isMaintenance()) {
                lore = this.mobSelectorSettings.getLoreMaintenance();
                final ConfigManager.SimpleItemStack simpleItemStack =
                        this.mobSelectorSettings.getMaintenanceServerItem();
                material = simpleItemStack.getMaterial();
                data = simpleItemStack.getData();
            } else if (serverInfo.getOnlinePlayers().isEmpty()) {
                lore = this.mobSelectorSettings.getLoreEmpty();
                final ConfigManager.SimpleItemStack simpleItemStack = this.mobSelectorSettings.getEmptyServerItem();
                material = simpleItemStack.getMaterial();
                data = simpleItemStack.getData();
            } else if (online >= maxPlayers) {
                lore = this.mobSelectorSettings.getLoreFull();
                final ConfigManager.SimpleItemStack simpleItemStack = this.mobSelectorSettings.getFullServerItem();
                material = simpleItemStack.getMaterial();
                data = simpleItemStack.getData();
            } else {
                lore = this.mobSelectorSettings.getLoreOnline();
                final ConfigManager.SimpleItemStack simpleItemStack = this.mobSelectorSettings.getOnlineServerItem();
                material = simpleItemStack.getMaterial();
                data = simpleItemStack.getData();
            }
            for (int i = 0; i < lore.size(); i++)
                lore.set(i, lore.get(i).replace("%online_players%", String.valueOf(online))
                        .replace("%max_players%", String.valueOf(maxPlayers)));
            final ItemBuilder itemBuilder = new ItemBuilder(material, 1, data)
                    .addLore(lore)
                    .setDisplayName(this.mobSelectorSettings.getServerNamePrefix()
                            + serverInfo.getCloudProcess().getName());
            this.inventory.addItem(itemBuilder.build());
        });
    }

    public void signalClick(final int slot) {
        final ItemStack clickedItemStack = this.inventory.getItem(slot);
        if (clickedItemStack == null) return;
        if (! clickedItemStack.getItemMeta().hasDisplayName()) return;
        final String displayName = clickedItemStack.getItemMeta().getDisplayName();
        final String prefix = ChatColor.translateAlternateColorCodes('&',
                this.mobSelectorSettings.getServerNamePrefix());
        if (! displayName.startsWith(prefix)) return;
        final String serverName = displayName.replace(prefix, "");
        this.player.sendMessage(this.mobSelectorSettings.getConnectMessage().replace("%server%", serverName));
        PlayerUtils.sendPlayerToServer(this.player, serverName);
    }
}
