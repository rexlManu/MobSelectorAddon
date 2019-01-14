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

    private ItemStack itemStack;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public ItemBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
    }

    public ItemBuilder(Material material, int amount, short data) {
        this.itemStack = new ItemStack(material, amount, data);
    }

    public ItemBuilder setDisplayName(String name) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addEnchantments(HashMap<Enchantment, Integer> enchantments) {
        itemStack.addEnchantments(enchantments);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addEnchant(enchantment, level, true);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addLore(List<String> lore) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        return addLore(Arrays.asList(lore));
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.spigot().setUnbreakable(unbreakable);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder glow() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemStack build() {
        return itemStack;
    }

}
