package org.doodieman.posemaster.util;

import com.google.common.collect.MultimapBuilder;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.*;

public class ItemBuilder {

    private final ItemStack itemStack;

    @Getter
    private ItemMeta itemMeta;

    public ItemBuilder(ItemBuilder itemBuilder) {
        this(itemBuilder.itemStack, itemBuilder.itemMeta);
    }

    public ItemBuilder(String material) {
        this(Material.valueOf(material));
    }

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        this(new ItemStack(material, amount));
    }

    public ItemBuilder(Material material, String name, String... lore) {
        this(material, 1, (short) 0, name, lore);
    }

    public ItemBuilder(Material material, int amount, short durability) {
        this(new ItemStack(material, amount, durability));
    }

    public ItemBuilder(Material material, int amount, short durability, String name, String... lore) {
        this(new ItemStack(material, amount, durability));
        this.name(name);
        this.lore(lore);
    }

    public ItemBuilder(ItemStack itemStack) {
        this(itemStack, itemStack.getItemMeta());
    }

    public ItemBuilder(ItemStack itemStack, ItemMeta itemMeta) {
        if (itemStack.getType() == Material.AIR) itemStack.setType(Material.DIRT);
        this.itemStack = itemStack;
        this.itemMeta = itemMeta;
    }

    @Override
    public ItemBuilder clone() {
        return new ItemBuilder(this.build());
    }

    public ItemBuilder material(String material) {
        this.itemStack.setType(Material.valueOf(material));
        return this;
    }

    public ItemBuilder material(Material material) {
        this.itemStack.setType(material);
        return this;
    }

    public ItemBuilder setDurability(short durability) {
        this.itemStack.setDurability(durability);
        return this;
    }

    public ItemBuilder name(String name) {
        this.itemMeta.setDisplayName(name);
        return this;
    }

    public ItemBuilder addUnsafeEnchantment(Enchantment enchantment, int level) {
        this.itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder addUnsafeEnchantments(Map<Enchantment, Integer> enchantments) {
        enchantments.forEach((enchantment, level) -> this.itemMeta.addEnchant(enchantment, level, true));
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        this.itemMeta.addEnchant(enchantment, level, false);
        return this;
    }

    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        enchantments.forEach((enchantment, level) -> this.itemMeta.addEnchant(enchantment, level, false));
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        this.itemMeta.removeEnchant(enchantment);
        return this;
    }

    public ItemBuilder removeEnchantments(Collection<Enchantment> enchantments) {
        enchantments.forEach(this.itemMeta::removeEnchant);
        return this;
    }

    public ItemBuilder glow(boolean value) {
        if (value)
            return this.makeGlowing();
        return this;
    }

    public ItemBuilder makeGlowing() {
        if (!this.itemMeta.hasEnchants()) this.itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
        return this.addItemFlag(ItemFlag.HIDE_ENCHANTS);
    }

    public ItemBuilder colorizeAll() {
        this.colorizeName();
        return this.colorizeLore();
    }

    public ItemBuilder colorizeLore() {
        if (this.itemMeta.hasLore()) {
            List<String> lore = this.getItemMeta().getLore();
            lore.replaceAll(text -> ChatColor.translateAlternateColorCodes('&', text));
            return this.lore(lore);
        }
        return this;
    }

    public ItemBuilder colorizeName() {
        if (this.itemMeta.hasDisplayName()) this.name(ChatColor.translateAlternateColorCodes('&', this.getItemMeta().getDisplayName()));
        return this;
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        this.itemMeta.setUnbreakable(unbreakable);
        return this;
    }

    public List<String> getLore() {
        return this.itemMeta.getLore();
    }

    public ItemBuilder lore(String... lore) {
        return this.lore(Arrays.asList(lore));
    }

    public ItemBuilder lore(List<String> lore) {
        this.itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder removeLoreLine(String line) {
        List<String> lore = new ArrayList<>(this.itemMeta.getLore());
        lore.remove(line);
        this.itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder removeLoreLine(int index) {
        if (index < 0) return this;
        List<String> lore = new ArrayList<>(this.itemMeta.getLore());
        if (index > lore.size()) return this;
        lore.remove(index);
        this.itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        return this.addLore(Arrays.asList(lore));
    }

    public ItemBuilder addLore(List<String> lore) {
        lore.forEach(this::addLoreLine);
        return this;
    }

    public ItemBuilder addLoreLine(String line) {
        List<String> lore = new ArrayList<>();
        if (this.itemMeta.hasLore()) lore = new ArrayList<>(this.itemMeta.getLore());
        lore.add(line);
        this.itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder addLoreLine(String line, int pos) {
        List<String> lore = new ArrayList<>(this.itemMeta.getLore());
        if (pos >= lore.size()) return this;
        lore.set(pos, line);
        this.itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder addItemFlags(List<String> itemFlags) {
        for (String itemFlagStr : itemFlags)
            try {
                ItemFlag itemFlag = ItemFlag.valueOf(itemFlagStr);
                this.addItemFlag(itemFlag);
            } catch (IllegalArgumentException ignored) {
            }
        return this;
    }

    public ItemBuilder itemFlags(List<ItemFlag> itemFlags) {
        this.itemMeta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag itemFlag) {
        this.itemMeta.addItemFlags(itemFlag);
        return this;
    }

    public ItemBuilder hideAttributes() {
        this.itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        this.itemMeta.setAttributeModifiers(MultimapBuilder.hashKeys().hashSetValues().build());
        return this;
    }

    public ItemBuilder setLeatherArmorColor(Color color) {
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) this.itemMeta;
        if (Bukkit.getItemFactory().isApplicable(leatherArmorMeta, this.itemStack)) {
            leatherArmorMeta.setColor(color);
            this.itemMeta = leatherArmorMeta;
        }
        return this;
    }

    public ItemStack build() {
        this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack;
    }

}
