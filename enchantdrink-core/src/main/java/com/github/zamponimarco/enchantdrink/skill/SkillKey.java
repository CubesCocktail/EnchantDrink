package com.github.zamponimarco.enchantdrink.skill;

import com.github.zamponimarco.cubescocktail.key.Key;
import com.github.zamponimarco.cubescocktail.libs.annotation.Serializable;
import com.github.zamponimarco.cubescocktail.libs.util.ItemUtils;
import com.github.zamponimarco.cubescocktail.libs.util.MessageUtils;
import com.github.zamponimarco.enchantdrink.EnchantDrink;
import com.github.zamponimarco.enchantdrink.enchant.Enchant;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class SkillKey implements Key {

    @Serializable(stringValue = true)
    private UUID enchantId;
    @Serializable(stringValue = true)
    private UUID skillId;

    public SkillKey(Map<String, Object> map) {
        this.enchantId = UUID.fromString((String) map.getOrDefault("itemId", null));
        this.skillId = UUID.fromString((String) map.getOrDefault("skillId", null));
    }

    @Override
    public ItemStack getGUIItem() {
        Enchant enchant = EnchantDrink.getInstance().getEnchantManager().getById(enchantId);
        if (enchant == null) {
            return null;
        }
        Skill skill = enchant.getSkillById(skillId);
        if (skill == null) {
            return null;
        }
        return ItemUtils.getNamedItem(
                enchant.getGUIItem(),
                MessageUtils.color("&6&lEnchant: &c" + enchant.getName()),
                Lists.newArrayList(
                        MessageUtils.color("&6&lSkill: &c" + skill.getName())
                )
        );
    }

    @Override
    public Key clone() {
        return new SkillKey(enchantId, skillId);
    }

    @Override
    public String getName() {
        Enchant enchant = EnchantDrink.getInstance().getEnchantManager().getById(enchantId);
        if (enchant == null) {
            return "";
        }
        Skill skill = enchant.getSkillById(skillId);
        if (skill == null) {
            return "";
        }

        return String.format("%s/%s", enchant.getName(), skill.getName());
    }
}