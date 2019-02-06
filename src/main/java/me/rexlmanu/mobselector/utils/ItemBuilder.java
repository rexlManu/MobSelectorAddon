package me.rexlmanu.mobselector.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public final class ItemBuilder {

    private final ItemStack itemStack;

    public ItemBuilder(final Material material) {
        this.itemStack = new ItemStack(material);
    }

    public ItemBuilder(final Material material, final int amount) {
        this.itemStack = new ItemStack(material, amount);
    }

    private ItemBuilder(final Material material, final int amount, final short data) {
        this.itemStack = new ItemStack(material, amount, data);
    }

    public ItemBuilder(final Material material, final int amount, final int data) {
        this(material, amount, (short) data);
    }

    public ItemBuilder setDisplayName(final String name) {
        final ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addEnchantments(final HashMap<Enchantment, Integer> enchantments) {
        this.itemStack.addEnchantments(enchantments);
        return this;
    }

    public ItemBuilder addEnchantment(final Enchantment enchantment, final int level) {
        final ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.addEnchant(enchantment, level, true);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addLore(final List<String> lore) {
        final ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setLore(lore);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addLore(final String... lore) {
        return this.addLore(Arrays.asList(lore));
    }

    public ItemBuilder setUnbreakable(final boolean unbreakable) {
        final ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.spigot().setUnbreakable(unbreakable);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder glow() {
        final ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemStack build() {
        return this.itemStack;
    }

}
