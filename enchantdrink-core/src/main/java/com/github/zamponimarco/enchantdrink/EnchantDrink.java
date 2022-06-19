package com.github.zamponimarco.enchantdrink;

import com.github.zamponimarco.cubescocktail.CubesCocktail;
import com.github.zamponimarco.cubescocktail.addon.Addon;
import com.github.zamponimarco.cubescocktail.libs.command.PluginCommandExecutor;
import com.github.zamponimarco.enchantdrink.command.EnchantsListCommand;
import com.github.zamponimarco.enchantdrink.enchant.Enchant;
import com.github.zamponimarco.enchantdrink.enchant.EnchantmentWrapper;
import com.github.zamponimarco.enchantdrink.listener.EnchantListener;
import com.github.zamponimarco.enchantdrink.listener.TimerListener;
import com.github.zamponimarco.enchantdrink.manager.EnchantManager;
import com.github.zamponimarco.enchantdrink.skill.Skill;
import com.github.zamponimarco.enchantdrink.skill.SkillKey;
import com.github.zamponimarco.enchantdrink.skill.TimedSkill;
import com.github.zamponimarco.enchantdrink.skill.TriggeredSkill;
import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

@Getter
public class EnchantDrink extends Addon {

    private static EnchantDrink instance;

    static {
        ConfigurationSerialization.registerClass(Enchant.class);
        ConfigurationSerialization.registerClass(EnchantmentWrapper.class);
        ConfigurationSerialization.registerClass(SkillKey.class);
        ConfigurationSerialization.registerClass(TimedSkill.class);
        ConfigurationSerialization.registerClass(TriggeredSkill.class);
    }

    private EnchantManager enchantManager;

    public static EnchantDrink getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        setUpConfig();
        setUpLibrary();
        setUpData();
        setUpCommands();
        CubesCocktail.getInstance().getServer().getPluginManager().registerEvents(new EnchantListener(),
                CubesCocktail.getInstance());
        CubesCocktail.getInstance().getServer().getPluginManager().registerEvents(new TimerListener(),
                CubesCocktail.getInstance());
    }

    @Override
    public void renameFunction(String oldName, String name) {
    }


    private void setUpConfig() {
    }

    private void setUpLibrary() {
    }

    private void setUpData() {
        this.enchantManager = new EnchantManager(Enchant.class, "comp", CubesCocktail.getInstance());
    }

    private void setUpCommands() {
        PluginCommandExecutor ex = new PluginCommandExecutor("list", new EnchantsListCommand());
        CubesCocktail.getInstance().getCommandExecutor().registerCommand("enchant", ex);
    }
}
