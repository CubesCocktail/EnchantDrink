package com.github.zamponimarco.enchantdrink.command;

import com.github.zamponimarco.cubescocktail.CubesCocktail;
import com.github.zamponimarco.cubescocktail.libs.command.AbstractCommand;
import com.github.zamponimarco.cubescocktail.libs.gui.model.ModelCollectionInventoryHolder;
import com.github.zamponimarco.cubescocktail.libs.model.ModelPath;
import com.github.zamponimarco.enchantdrink.EnchantDrink;
import com.github.zamponimarco.enchantdrink.enchant.Enchant;
import lombok.SneakyThrows;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class EnchantsListCommand extends AbstractCommand {

    @SneakyThrows
    @Override
    protected void execute(String[] arguments, CommandSender sender) {
        Player p = (Player) sender;
        p.openInventory(new ModelCollectionInventoryHolder<Enchant>(CubesCocktail.getInstance(), null,
                new ModelPath<>(EnchantDrink.getInstance().getEnchantManager(), null), EnchantDrink.getInstance().
                getEnchantManager().getClass().getDeclaredField("enchants"), 1, o -> true).getInventory());
    }

    @Override
    protected boolean isOnlyPlayer() {
        return true;
    }

    @Override
    protected Permission getPermission() {
        return new Permission("cubescocktail.enchant.list");
    }

}
