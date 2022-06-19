package com.github.zamponimarco.enchantdrink.enchant;

import com.github.zamponimarco.cubescocktail.CubesCocktail;
import com.github.zamponimarco.cubescocktail.libs.annotation.Serializable;
import com.github.zamponimarco.cubescocktail.libs.model.Model;
import com.github.zamponimarco.cubescocktail.libs.model.wrapper.ModelWrapper;
import com.github.zamponimarco.cubescocktail.libs.util.MessageUtils;
import com.github.zamponimarco.enchantdrink.utils.RomanUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.papermc.paper.enchantments.EnchantmentRarity;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.Translator;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class EnchantmentWrapper extends Enchantment implements Model {

    private static final String CONDITIONS_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjNhYzM0MTg1YmNlZTYyNmRkOWJjOTY2YmU2NDk4NDM4ZmJmYTc1NDFjODYyYWM3MTZmZmVmOWZkMTg1In19fQ==";

    @Serializable
    private String name;
    @Serializable(stringValue = true)
    private UUID id;
    @Serializable(headTexture = CONDITIONS_HEAD, description = "gui.condition.or.conditions")
    @Serializable.Number(minValue = 1, scale = 1)
    private int maxLevel;
    @Serializable(headTexture = CONDITIONS_HEAD, description = "gui.condition.or.conditions")
    @Serializable.Number(minValue = 1, scale = 1)
    private int startLevel;
    @Serializable(headTexture = CONDITIONS_HEAD, description = "gui.condition.or.conditions", stringValue = true)
    private EnchantmentTarget itemTarget;
    @Serializable(headTexture = CONDITIONS_HEAD, description = "gui.condition.or.conditions")
    private boolean treasure;
    @Serializable(headTexture = CONDITIONS_HEAD, description = "gui.condition.or.conditions")
    private boolean cursed;
    @Serializable(headTexture = CONDITIONS_HEAD, description = "gui.condition.or.conditions")
    private List<Enchantment> conflicts;
    @Serializable(headTexture = CONDITIONS_HEAD, description = "gui.condition.or.conditions")
    private boolean tradeable;
    @Serializable(headTexture = CONDITIONS_HEAD, description = "gui.condition.or.conditions")
    private boolean discoverable;
    @Serializable(headTexture = CONDITIONS_HEAD, description = "gui.condition.or.conditions", stringValue = true)
    private EnchantmentRarity rarity;

    public EnchantmentWrapper(String name, UUID id) {
        this(id, name, 3, 1, EnchantmentTarget.BREAKABLE, false, false, Lists.newArrayList(),
                false, false, EnchantmentRarity.COMMON);
    }

    public EnchantmentWrapper(UUID id, String name, int maxLevel, int startLevel,
                              EnchantmentTarget itemTarget, boolean treasure, boolean cursed,
                              List<Enchantment> conflicts, boolean tradeable, boolean discoverable,
                              EnchantmentRarity rarity) {
        super(new NamespacedKey(CubesCocktail.getInstance(), id.toString()));
        this.id = id;
        this.name = name;
        this.maxLevel = maxLevel;
        this.startLevel = startLevel;
        this.itemTarget = itemTarget;
        this.treasure = treasure;
        this.cursed = cursed;
        this.conflicts = conflicts;
        this.tradeable = tradeable;
        this.discoverable = discoverable;
        this.rarity = rarity;
    }

    public EnchantmentWrapper(Map<String, Object> map) {
        super(new NamespacedKey(CubesCocktail.getInstance(), (String) map.get("id")));
        this.id = UUID.fromString((String) map.get("id"));
        this.name = (String) map.get("name");
        this.maxLevel = (int) map.get("maxLevel");
        this.startLevel = (int) map.get("startLevel");
        this.itemTarget = EnchantmentTarget.valueOf((String) map.get("itemTarget"));
        this.treasure = (boolean) map.get("treasure");
        this.cursed = (boolean) map.get("cursed");
        this.conflicts = (List<Enchantment>) map.get("conflicts");
        this.tradeable = (boolean) map.get("tradeable");
        this.discoverable = (boolean) map.get("discoverable");
        this.rarity = EnchantmentRarity.valueOf((String) map.get("rarity"));
    }

    @Override
    public boolean conflictsWith(@NotNull Enchantment enchantment) {
        return conflicts.contains(enchantment);
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack itemStack) {
        return itemTarget.includes(itemStack);
    }

    @Override
    public @NotNull Component displayName(int i) {
        return MessageUtils.color("&7" + getName() + " " +
                ((i != 1 || this.maxLevel == 1) ? "" : RomanUtils.toRoman(i)));
    }

    @Override
    public float getDamageIncrease(int i, @NotNull EntityCategory entityCategory) {
        return 0;
    }

    @Override
    public @NotNull Set<EquipmentSlot> getActiveSlots() {
        return Sets.newHashSet();
    }

    @Override
    public @NotNull String translationKey() {
        return "enchant" + name;
    }
}
