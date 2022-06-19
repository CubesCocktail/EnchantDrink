package com.github.zamponimarco.enchantdrink.listener;

import com.github.zamponimarco.cubescocktail.CubesCocktail;
import com.github.zamponimarco.enchantdrink.EnchantDrink;
import com.github.zamponimarco.enchantdrink.enchant.EnchantmentWrapper;
import com.github.zamponimarco.enchantdrink.manager.EnchantManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.*;

import java.util.Map;

public class EnchantListener implements Listener {

    private EnchantManager manager;

    public EnchantListener() {
        this.manager = EnchantDrink.getInstance().getEnchantManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEnchantUpdateAnvil(PrepareAnvilEvent e) {
        AnvilInventory inventory = e.getInventory();

        ItemStack first = inventory.getItem(0);
        ItemStack second = inventory.getItem(1);
        ItemStack result = e.getResult();

        // Check if source item is an enchantable single item.
        if (first == null || first.getAmount() > 1) return;

        // For repair/rename, only re-add item enchants.
        if (second == null && result != null && result.getType() == first.getType()) {
            ItemStack finalResult = new ItemStack(result);
            manager.getCustomEnchantmentWrappers(first).forEach((enchant, level) ->
                    manager.addCustomEnchant(finalResult, enchant, level, true));
            e.setResult(finalResult);
            return;
        }

        // Check if the second item is an enchantable single item.
        if (second == null || second.getAmount() > 1) return;

        // Prevent operation if first item is book while the second one is another item.
        if (first.getType() == Material.ENCHANTED_BOOK && second.getType() != first.getType()) return;

        // Fine result item in case if it's nulled somehow.
        if (result == null || result.getType() == Material.AIR) {
            result = new ItemStack(first);
        }

        Map<EnchantmentWrapper, Integer> enchAdd = manager.getCustomEnchantmentWrappers(first);
        int repairCost = inventory.getRepairCost();

        // If the second item is an enchanted book or the same item type, then
        // we can merge our enchantments.
        if (second.getType() == Material.ENCHANTED_BOOK || second.getType() == first.getType()) {
            manager.getCustomEnchantmentWrappers(second).forEach((enchant, level) -> enchAdd.merge(enchant, level,
                    (oldLvl, newLvl) -> (oldLvl.equals(newLvl)) ? (oldLvl + 1) : (Math.max(oldLvl, newLvl))));
        }

        if (!first.equals(result)) {
            manager.updateEnchants(result);
            e.setResult(result);

            inventory.setRepairCost(repairCost);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEnchantUpdateGrindstone(InventoryClickEvent e) {
        Inventory inventory = e.getInventory();
        if (inventory.getType() != InventoryType.GRINDSTONE) return;

        Bukkit.getScheduler().runTask(CubesCocktail.getInstance(), () -> {
            ItemStack result = inventory.getItem(2);
            if (result == null) return;

            manager.updateEnchants(result);
        });
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEnchantPopulateEnchantingTable(final EnchantItemEvent e) {
        ItemStack target = e.getItem();

        Map<EnchantmentWrapper, Integer> toAdd = manager.fetchRandomEnchants(target);

        CubesCocktail.getInstance().getServer().getScheduler().runTask(CubesCocktail.getInstance(), () -> {
            ItemStack result = e.getInventory().getItem(0);
            if (result == null) return;

            toAdd.forEach(((enchantment, level) -> manager.addCustomEnchant(target, enchantment, level, true)));
            e.getInventory().setItem(0, result);
        });
    }

}
