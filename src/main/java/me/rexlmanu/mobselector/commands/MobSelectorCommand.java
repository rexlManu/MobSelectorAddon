package me.rexlmanu.mobselector.commands;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import de.klarcloudservice.KlarCloudAPISpigot;
import de.klarcloudservice.meta.server.ServerGroup;
import me.rexlmanu.mobselector.MobSelector;
import me.rexlmanu.mobselector.mob.defaults.MobLocation;
import me.rexlmanu.mobselector.mob.defaults.ServerMob;
import me.rexlmanu.mobselector.utils.MobUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
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
        pluginCommand.setUsage("Please use '/mobselector help' for the helptopic.");
        pluginCommand.setPermission("mobselector.command.mobselector");
        pluginCommand.setPermissionMessage(MobSelector.getInstance().getConfigManager()
                .getMobSelectorSettings().getPermissionMessage());
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command,
                             final String label, final String[] args) {
        final MobSelector instance = MobSelector.getInstance();
        if (! (sender instanceof Player)) {
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
                    sender.sendMessage(String.format(
                            "%s» %s/mobselector help %s| %sHelp overview",
                            ChatColor.DARK_GRAY, ChatColor.GREEN, ChatColor.DARK_GRAY, ChatColor.GRAY));
                    sender.sendMessage(String.format(
                            "%s» %s/mobselector create <ServerGroup> <EntityType> %s| %sCreate one servermob",
                            ChatColor.DARK_GRAY, ChatColor.GREEN, ChatColor.DARK_GRAY, ChatColor.GRAY));
                    sender.sendMessage(String.format(
                            "%s» %s/mobselector changetype <EntityType> %s| %sChange the type from a servermob",
                            ChatColor.DARK_GRAY, ChatColor.GREEN, ChatColor.DARK_GRAY, ChatColor.GRAY));
                    sender.sendMessage(String.format(
                            "%s» %s/mobselector changename <Displayname> %s| %sChange the name from a servermob",
                            ChatColor.DARK_GRAY, ChatColor.GREEN, ChatColor.DARK_GRAY, ChatColor.GRAY));
                    sender.sendMessage(String.format(
                            "%s» %s/mobselector remove %s| %sRemove a servermob",
                            ChatColor.DARK_GRAY, ChatColor.GREEN, ChatColor.DARK_GRAY, ChatColor.GRAY));
                } else if ("remove".equalsIgnoreCase(args[0])) {
                    final Entity lookingEntity = MobUtils.getLookingEntity(player);
                    if (lookingEntity == null) {
                        player.sendMessage(ChatColor.RED + "To edit an enity, you have to kook at one.");
                        return true;
                    }
                }
                break;
            case 2:
                final Entity lookingEntity = MobUtils.getLookingEntity(player);
                if (lookingEntity == null) {
                    player.sendMessage(ChatColor.RED + "To edit an enity, you have to kook at one.");
                    return true;
                }
                final ServerMob serverMob = instance.getMobManager().getServerMobByEntity(lookingEntity);
                if (serverMob == null) {
                    player.sendMessage(ChatColor.RED + "This entity is not a servermob.");
                    return true;
                }
                if ("changetype".equalsIgnoreCase(args[0])) {
                    final EntityType entityType = this.getEntityByName(args[1]);
                    if (entityType == null) {
                        sender.sendMessage(ChatColor.RED + "The entity type could not be found");
                        return true;
                    }
                    instance.getMobManager().removeMob(lookingEntity);
                    serverMob.setEntityTypeName(entityType.name());
                    instance.getMobManager().spawnMob(serverMob);
                    instance.getConfigManager().save();
                    sender.sendMessage(ChatColor.GREEN + String.format(
                            "You have successful changed the entity type to %s.", entityType.name()));
                } else if ("changename".equalsIgnoreCase(args[0])) {
                    instance.getMobManager().removeMob(lookingEntity);
                    final String displayName = ChatColor.translateAlternateColorCodes('&',
                            args[1].replace("_", " "));
                    serverMob.setDisplayName(displayName);
                    instance.getMobManager().spawnMob(serverMob);
                    instance.getConfigManager().save();
                    sender.sendMessage(ChatColor.GREEN + String.format(
                            "You have successful changed the display name to '%s'.", displayName));
                }
                break;
            case 3:
                if ("create".equalsIgnoreCase(args[0])) {
                    final String serverGroupName = args[1];
                    final String entityTypeName = args[2];
                    final Map<String, ServerGroup> serverGroups = KlarCloudAPISpigot.getInstance()
                            .getInternalCloudNetwork().getServerGroups();
                    if (! serverGroups.containsKey(serverGroupName)) {
                        sender.sendMessage(ChatColor.RED + "The server group could not be found");
                        return true;
                    }
                    final ServerGroup serverGroup = serverGroups.get(serverGroupName);
                    final EntityType entityType = this.getEntityByName(entityTypeName);
                    if (entityType == null) {
                        sender.sendMessage(ChatColor.RED + "The entity type could not be found");
                        return true;
                    }
                    instance.getMobManager().createMob(serverGroup, new ServerMob(
                            MobLocation.get(player.getLocation()),
                            ChatColor.DARK_GRAY + "» " + ChatColor.RED + serverGroupName,
                            entityType.name()));
                    sender.sendMessage(ChatColor.GREEN + "The server mob was created successfully");
                }
                break;
            default:
                sender.sendMessage("&aPlease execute 'mobselector help' for the help overview");
                break;
        }

        return true;
    }

    @SuppressWarnings("UnstableApiUsage")
    private EntityType getEntityByName(final String entityTypeName) {
        final Optional<EntityType> entityTypeOptional = Enums.getIfPresent(EntityType.class,
                entityTypeName.toUpperCase());
        return entityTypeOptional.isPresent() ? entityTypeOptional.get() : null;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command,
                                      final String alias, final String[] args) {
        return Lists.newArrayList();
    }
}
