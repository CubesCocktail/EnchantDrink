package com.github.zamponimarco.enchantdrink.enchant;

import com.github.zamponimarco.cubescocktail.libs.annotation.Serializable;
import com.github.zamponimarco.cubescocktail.libs.model.NamedModel;
import com.github.zamponimarco.cubescocktail.libs.util.ItemUtils;
import com.github.zamponimarco.cubescocktail.libs.util.MessageUtils;
import com.github.zamponimarco.enchantdrink.EnchantDrink;
import com.github.zamponimarco.enchantdrink.skill.Skill;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class Enchant extends NamedModel implements Cloneable {

    private static final String CONDITIONS_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjNhYzM0MTg1YmNlZTYyNmRkOWJjOTY2YmU2NDk4NDM4ZmJmYTc1NDFjODYyYWM3MTZmZmVmOWZkMTg1In19fQ==";

    protected static int enchantCounter = 1;

    @Serializable(stringValue = true)
    private UUID id;
    @Serializable(headTexture = CONDITIONS_HEAD, description = "gui.condition.or.conditions")
    private EnchantmentWrapper enchantmentOptions;
    @Serializable(headTexture = CONDITIONS_HEAD, description = "gui.condition.or.conditions")
    private List<Skill> skills;

    public Enchant() {
        this(UUID.randomUUID(), nextAvailableName());
    }

    public Enchant(UUID id, String name) {
        this(id, name, new EnchantmentWrapper(name, id), Lists.newArrayList());
    }

    public Enchant(UUID id, String name, EnchantmentWrapper enchantmentOptions, List<Skill> skills) {
        super(name);
        this.id = id;
        this.enchantmentOptions = enchantmentOptions;
        this.skills = skills;
    }

    public Enchant(Map<String, Object> map) {
        super(map);
        this.id = UUID.fromString((String) map.get("id"));
        this.enchantmentOptions = (EnchantmentWrapper) map.get("enchantmentOptions");
        this.skills = (List<Skill>) map.get("skills");
    }

    public Skill getSkillById(UUID id) {
        return skills.stream().filter(skill -> skill.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    protected boolean isAlreadyPresent(String s) {
        return false;
    }

    protected static String nextAvailableName() {
        String name;
        do {
            name = "enchant" + enchantCounter;
            enchantCounter++;
        } while (EnchantDrink.getInstance().getEnchantManager().getByName(name) != null);
        return name;
    }

    @Override
    public ItemStack getGUIItem() {
        return ItemUtils.getNamedItem(new ItemStack(Material.BOOK), MessageUtils.color("&6" + name), Lists.newArrayList());
    }

    @Override
    public void onModify(Field field) {
        if (field.getName().equals("name")) {
            enchantmentOptions.setName(name);
        }
    }
}
