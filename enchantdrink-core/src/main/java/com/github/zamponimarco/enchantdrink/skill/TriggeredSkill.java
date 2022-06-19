package com.github.zamponimarco.enchantdrink.skill;

import com.github.zamponimarco.cubescocktail.CubesCocktail;
import com.github.zamponimarco.cubescocktail.action.args.ActionArgument;
import com.github.zamponimarco.cubescocktail.action.args.ActionArgumentKey;
import com.github.zamponimarco.cubescocktail.action.group.ActionGroup;
import com.github.zamponimarco.cubescocktail.annotation.CasterOnlyPlayer;
import com.github.zamponimarco.cubescocktail.annotation.PossibleSources;
import com.github.zamponimarco.cubescocktail.annotation.PossibleTargets;
import com.github.zamponimarco.cubescocktail.cooldown.CooldownOptions;
import com.github.zamponimarco.cubescocktail.cooldown.Cooldownable;
import com.github.zamponimarco.cubescocktail.libs.annotation.Enumerable;
import com.github.zamponimarco.cubescocktail.libs.annotation.Serializable;
import com.github.zamponimarco.cubescocktail.libs.model.ModelPath;
import com.github.zamponimarco.cubescocktail.slot.Slot;
import com.github.zamponimarco.cubescocktail.source.Source;
import com.github.zamponimarco.cubescocktail.trgt.ItemTarget;
import com.github.zamponimarco.cubescocktail.trgt.Target;
import com.github.zamponimarco.cubescocktail.trigger.LeftClickTrigger;
import com.github.zamponimarco.cubescocktail.trigger.Trigger;
import com.github.zamponimarco.cubescocktail.trigger.TriggerListener;
import com.github.zamponimarco.cubescocktail.util.Utils;
import com.github.zamponimarco.enchantdrink.EnchantDrink;
import com.github.zamponimarco.enchantdrink.enchant.Enchant;
import com.github.zamponimarco.enchantdrink.enchant.EnchantmentWrapper;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Enumerable.Child
@CasterOnlyPlayer
@PossibleTargets("getPossibleTargets")
@PossibleSources("getPossibleSources")
@Enumerable.Displayable(name = "&6&lTriggered Skill", description = "gui.item.skill.triggered.description", headTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY1Mjg2ZTNlNmZhMDBlNGE2MGJiODk2NzViOWFhNzVkNmM5Y2RkMWVjODQwZDFiY2MyOTZiNzFjOTJmOWU0MyJ9fX0")
public class TriggeredSkill extends Skill implements TriggerListener, Cooldownable {

    private static final String TRIGGER_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY1Mjg2ZTNlNmZhMDBlNGE2MGJiODk2NzViOWFhNzVkNmM5Y2RkMWVjODQwZDFiY2MyOTZiNzFjOTJmOWU0MyJ9fX0=";
    private static final String COOLDOWN_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmZlOGNmZjc1ZjdkNDMzMjYwYWYxZWNiMmY3NzNiNGJjMzgxZDk1MWRlNGUyZWI2NjE0MjM3NzlhNTkwZTcyYiJ9fX0=";

    @Serializable(headTexture = COOLDOWN_HEAD, description = "gui.item.skill.triggered.cooldownOptions")
    @Serializable.Number(minValue = 1, scale = 1)
    protected CooldownOptions cooldownOptions;

    @Serializable(headTexture = TRIGGER_HEAD, description = "gui.item.skill.triggered.trigger")
    protected Trigger trigger;

    public TriggeredSkill(ModelPath<Enchant> path) {
        super(path);
        this.cooldownOptions = new CooldownOptions();
        this.trigger = new LeftClickTrigger();
        trigger.registerListener(this);
        registerKeyed();
    }

    public TriggeredSkill(UUID itemId, UUID id, List<Slot> allowedSlots, List<ActionGroup> groups,
                          CooldownOptions cooldownOptions, Trigger trigger) {
        super(itemId, id, allowedSlots, groups);
        this.cooldownOptions = cooldownOptions;
        this.trigger = trigger;
        trigger.registerListener(this);
        registerKeyed();
    }

    public TriggeredSkill(Map<String, Object> map) {
        super(map);
        this.cooldownOptions = (CooldownOptions) map.getOrDefault("cooldownOptions", new CooldownOptions());
        this.trigger = (Trigger) map.getOrDefault("trigger", new LeftClickTrigger());
        trigger.registerListener(this);
        registerKeyed();
    }

    public Collection<Class<? extends Target>> getPossibleTargets() {
        Set<Class<? extends Target>> targets = new HashSet<>(trigger.getPossibleTargets());
        targets.add(ItemTarget.class);
        return targets;
    }

    public Collection<Class<? extends Source>> getPossibleSources() {
        return new HashSet<>(trigger.getPossibleSources());
    }

    @Override
    public void onTrigger(ActionArgument args) {
        LivingEntity caster = args.getArgument(ActionArgumentKey.CASTER);
        ItemStack itemStack = args.getArgument(ActionArgumentKey.ITEM);

        if (itemStack != null) {
            executeTriggers(args, caster);
        } else {
            List<ItemStack> items = Utils.getEntityItems(caster);
            IntStream.range(0, items.size()).filter(i -> Objects.nonNull(items.get(i))).forEach(i -> {
                ItemStack equipItem = items.get(i);
                Map<EnchantmentWrapper, Integer> enchants = EnchantDrink.getInstance().getEnchantManager().
                        getCustomEnchantmentWrappers(equipItem);
                if (!enchants.isEmpty() && enchants.containsKey(EnchantDrink.getInstance().getEnchantManager().
                        getById(enchantId).getEnchantmentOptions()) && getAllowedSlots().contains(Slot.slots.get(i))) {
                    args.setArgument(ActionArgumentKey.ITEM, equipItem);
                    executeTriggers(args, caster);
                    args.setArgument(ActionArgumentKey.ITEM, null);
                }
            });
        }
    }

    private void executeTriggers(ActionArgument args, LivingEntity caster) {
        if (cooldownOptions.getCooldown() > 0) {
            if (CubesCocktail.getInstance().getCooldownManager().getCooldown(caster, getKey()) > 0) {
                if (caster instanceof Player) {
                    cooldownOptions.getBar().switchCooldownContext((Player) caster, getKey(),
                            cooldownOptions.getCooldown());
                }
                return;
            } else {
                CubesCocktail.getInstance().getCooldownManager().addCooldown(caster, getKey(), cooldownOptions.getCooldown(),
                        getCooldownOptions().getBar());
            }
        }
        executeActions(args);
    }

    @Override
    public ItemStack getGUIItem() {
        return trigger.getGUIItem();
    }

    @Override
    public void onRemoval() {
        trigger.unregisterListener(this);
        unregisterKeyed();
    }

    @Override
    public Skill clone() {
        return new TriggeredSkill(enchantId, id, allowedSlots.stream().map(Slot::clone).collect(Collectors.toList()),
                groups.stream().map(ActionGroup::clone).collect(Collectors.toList()), cooldownOptions.clone(), trigger.clone());
    }

    @Override
    public String getName() {
        return trigger.getName();
    }
}
