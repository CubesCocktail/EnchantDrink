package com.github.zamponimarco.enchantdrink.manager;

import com.github.zamponimarco.cubescocktail.libs.model.ModelManager;
import com.github.zamponimarco.cubescocktail.libs.model.math.IntRange;
import com.github.zamponimarco.enchantdrink.EnchantDrink;
import com.github.zamponimarco.enchantdrink.enchant.Enchant;
import com.github.zamponimarco.enchantdrink.enchant.EnchantmentWrapper;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Getter
public class EnchantManager extends ModelManager<Enchant> {

    private List<Enchant> enchants;

    public EnchantManager(Class<Enchant> classObject, String databaseType, JavaPlugin plugin) {
        super(classObject, databaseType, plugin, ImmutableMap.of("name", "enchant",
                "fileSupplier", (Supplier<File>) () -> {
                    String fileName = "enchant.yml";
                    File dataFile = new File(EnchantDrink.getInstance().getDataFolder(), fileName);
                    if (!dataFile.exists()) {
                        EnchantDrink.getInstance().saveResource(fileName);
                    }
                    return dataFile;
                }));
        this.enchants = fetchModels();
    }

    public Enchant getByName(String name) {
        return enchants.stream().filter(enchant -> enchant.getName().equals(name)).findFirst().orElse(null);
    }

    public Enchant getById(UUID id) {
        return enchants.stream().filter(enchant -> enchant.getId().equals(id)).findFirst().orElse(null);
    }

    public Map<EnchantmentWrapper, Integer> getCustomEnchantmentWrappers(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) return new HashMap<>();
        PersistentDataContainer persistentDataContainer = itemStack.getItemMeta().getPersistentDataContainer();
        return enchants.stream().filter(enchant -> persistentDataContainer.has(enchant.getEnchantmentOptions().getKey(),
                PersistentDataType.INTEGER)).collect(Collectors.toMap(Enchant::getEnchantmentOptions,
                enchant -> persistentDataContainer.getOrDefault(enchant.getEnchantmentOptions().getKey(),
                        PersistentDataType.INTEGER, 0)));
    }

    public Set<Enchant> getCustomEnchants(ItemStack itemStack) {
        return getCustomEnchantmentWrappers(itemStack).keySet().stream().map(enchantmentWrapper -> EnchantDrink.getInstance().
                getEnchantManager().getById(enchantmentWrapper.getId())).collect(Collectors.toSet());
    }

    public void updateEnchants(ItemStack item) {
        Map<EnchantmentWrapper, Integer> currentEnchants = getCustomEnchantmentWrappers(item);
        enchants.forEach(enchant -> removeCustomEnchant(item, enchant.getEnchantmentOptions()));
        currentEnchants.forEach((enchant, level) -> addCustomEnchant(item, enchant, level, true));
    }

    public boolean addCustomEnchant(ItemStack item, EnchantmentWrapper enchant, int level, boolean force) {
        if (!force && !enchant.canEnchantItem(item)) return false;

        removeCustomEnchant(item, enchant);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        meta.getPersistentDataContainer().set(enchant.getKey(), PersistentDataType.INTEGER, level);
        item.setItemMeta(meta);
        addEnchantLore(item, enchant, level);

        return true;
    }

    public void removeCustomEnchant(ItemStack item, EnchantmentWrapper enchant) {

        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().remove(enchant.getKey());
        removeEnchantLore(item, enchant);
        item.setItemMeta(meta);
    }

    private void addEnchantLore(ItemStack item, EnchantmentWrapper enchant, int level) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        List<Component> lore = meta.lore();
        if (lore == null) lore = new ArrayList<>();

        Component text = enchant.displayName(level);

        removeEnchantLore(item, enchant);
        lore.add(0, text);

        meta.lore(lore);
        item.setItemMeta(meta);
    }


    private void removeEnchantLore(ItemStack item, EnchantmentWrapper id) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        List<Component> lore = meta.lore();
        if (lore == null) return;

        meta.lore(lore.stream().filter(component -> PlainComponentSerializer.plain().serialize(component).
                contains(id.getName())).collect(Collectors.toList()));
        item.setItemMeta(meta);
    }

    public Map<EnchantmentWrapper, Integer> fetchRandomEnchants(ItemStack itemStack) {
        List<EnchantmentWrapper> enchantments = getEnchants().stream().map(Enchant::getEnchantmentOptions).
                filter(enchantmentWrapper -> enchantmentWrapper.canEnchantItem(itemStack)).collect(Collectors.toList());
        Map<EnchantmentWrapper, Integer> enchants = new HashMap<>();
        int enchRoll = new Random().nextInt(2);

        if (enchantments.size() > 0) {
            for (int count = 0; count < enchRoll; count++) {
                EnchantmentWrapper enchant = enchantments.get(new Random().nextInt(enchantments.size()));
                if (enchant == null) continue;

                IntRange levelRange = new IntRange(enchant.getStartLevel(), enchant.getMaxLevel());

                int level = new Random().nextInt(levelRange.getDifference()) + levelRange.getMin();
                enchants.put(enchant, level);
            }
        }
        return enchants;
    }

    public boolean isNaturallyEnchantable(ItemStack item) {
        return item.getType().equals(Material.ENCHANTED_BOOK) || EnchantmentTarget.BREAKABLE.includes(item);
    }

}
