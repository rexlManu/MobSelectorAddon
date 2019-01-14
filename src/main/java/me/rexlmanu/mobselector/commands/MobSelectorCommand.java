package me.rexlmanu.mobselector.commands;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import de.klarcloudservice.KlarCloudAPISpigot;
import de.klarcloudservice.meta.server.ServerGroup;
import me.rexlmanu.mobselector.MobSelector;
import me.rexlmanu.mobselector.mob.defaults.ServerMob;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public final class MobSelectorCommand implements CommandExecutor, TabCompleter {

    public MobSelectorCommand() {
        final PluginCommand pluginCommand = MobSelector.getInstance().getCommand("mobselector");
        pluginCommand.setTabCompleter(this);
        pluginCommand.setExecutor(this);
        pluginCommand.setDescription("The main command from plugin.");
        pluginCommand.setUsage("Please use 'mobselector help' for the helptopic.");
        pluginCommand.setPermission("mobselector.command.mobselector");
        pluginCommand.setPermissionMessage(MobSelector.getInstance().getConfigManager().getMobSelectorSettings().getPermissionMessage());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final MobSelector instance = MobSelector.getInstance();
        if (!(sender instanceof Player)) {
            sender.sendMessage(instance.getConfigManager().getMobSelectorSettings().getConsoleSender());
            return true;
        }
        final Player player = (Player) sender;
        switch (args.length) {
            case 0:
                sender.sendMessage(ChatColor.GREEN + "Please execute 'mobselector help' for the help overview");
                break;
            case 1:
                if ("help".equalsIgnoreCase(args[0])) {
                    sender.sendMessage(ChatColor.GREEN + "Help overview for mobselector");
                    sender.sendMessage(String.format("%s» %s/mobselector help %s| %sHelp overview", ChatColor.DARK_GRAY, ChatColor.GREEN, ChatColor.DARK_GRAY, ChatColor.GRAY));
                    sender.sendMessage(String.format("%s» %s/mobselector create <ServerGroup> <EntityType> %s| %sCreate one servermob", ChatColor.DARK_GRAY, ChatColor.GREEN, ChatColor.DARK_GRAY, ChatColor.GRAY));
                }
                break;
            case 3:
                if ("create".equalsIgnoreCase(args[0])) {
                    final String serverGroupName = args[1];
                    final String entityTypeName = args[2];
                    final Map<String, ServerGroup> serverGroups = KlarCloudAPISpigot.getInstance().getInternalCloudNetwork().getServerGroups();
                    if (!serverGroups.containsKey(serverGroupName)) {
                        sender.sendMessage(ChatColor.GREEN + "The server group could not be found");
                        return true;
                    }
                    final ServerGroup serverGroup = serverGroups.get(serverGroupName);
                    final Optional<EntityType> entityTypeOptional = Enums.getIfPresent(EntityType.class, entityTypeName.toUpperCase());
                    if (!entityTypeOptional.isPresent()) {
                        sender.sendMessage(ChatColor.GREEN + "The entity type could not be found");
                        return true;
                    }
                    final EntityType entityType = entityTypeOptional.get();
                    instance.getMobManager().spawnMob(serverGroup, new ServerMob(player.getLocation(), ChatColor.DARK_GRAY + "» " + ChatColor.RED + serverGroupName, entityType));
                    sender.sendMessage(ChatColor.GREEN + "The server mob was created successfully");
                }
                break;
            default:
                sender.sendMessage("&aPlease execute 'mobselector help' for the help overview");
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Lists.newArrayList();
    }
}
