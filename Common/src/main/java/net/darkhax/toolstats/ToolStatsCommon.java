package net.darkhax.toolstats;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.darkhax.toolstats.config.ConfigSchema;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ToolStatsCommon {

    private final ConfigSchema config;
    private final Function<ItemStack, Integer> enchantabilityResolver;
    private final Function<Tier, Integer> harvestLevelResolver;

    private final Map<Tier, Component> digSpeedCache = new HashMap<>();
    private final Int2ObjectMap<Component> enchantabilityCache = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<Component> repairCostCache = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<Component> harvestLevelCache = new Int2ObjectOpenHashMap<>();

    public ToolStatsCommon(Path configDir, Function<ItemStack, Integer> enchantabilityResolver, Function<Tier, Integer> harvestLevelResolver) {

        this.config = ConfigSchema.load(configDir.resolve(Constants.MOD_ID + ".json").toFile());
        this.enchantabilityResolver = enchantabilityResolver;
        this.harvestLevelResolver = harvestLevelResolver;
    }

    public void displayTooltipInfo(ItemStack stack, TooltipFlag context, List<Component> tooltip) {

        final List<Component> additions = new ArrayList<>();

        if (stack.getItem() instanceof TieredItem tieredItem) {

            if (this.config.showHarvestLevel) {

                additions.add(this.harvestLevelCache.computeIfAbsent(harvestLevelResolver.apply(tieredItem.getTier()), lvl -> new TranslatableComponent("tooltip.toolstats.harvestlevel", lvl).withStyle(ChatFormatting.DARK_GREEN)));
            }

            if (this.config.showEfficiency) {

                additions.add(digSpeedCache.computeIfAbsent(tieredItem.getTier(), tier -> new TranslatableComponent("tooltip.toolstats.efficiency", Constants.DECIMAL_FORMAT.format(tier.getSpeed())).withStyle(ChatFormatting.DARK_GREEN)));
            }
        }

        if (this.config.showEnchantability && (this.config.alwaysShowEnchantability || Minecraft.getInstance().screen instanceof EnchantmentScreen)) {

            final int enchantability = this.enchantabilityResolver.apply(stack);

            if (enchantability > 0) {

                additions.add(this.enchantabilityCache.computeIfAbsent(enchantability, enchLvl -> new TranslatableComponent("tooltip.toolstats.enchantability", enchLvl).withStyle(ChatFormatting.DARK_GREEN)));
            }
        }

        if (this.config.showRepairCost && (this.config.alwaysShowRepairCost || Minecraft.getInstance().screen instanceof AnvilScreen)) {

            final int repairCost = stack.getBaseRepairCost();

            if (repairCost > 0) {

                additions.add(this.repairCostCache.computeIfAbsent(repairCost, cost -> new TranslatableComponent("tooltip.toolstats.repaircost", cost).withStyle(ChatFormatting.DARK_GREEN)));
            }
        }

        if (this.config.showDurability && stack.isDamageableItem()) {

            if (this.config.alwaysShowDurability || stack.isDamaged()) {

                additions.add(new TranslatableComponent("item.durability", stack.getMaxDamage() - stack.getDamageValue(), stack.getMaxDamage()));
            }
        }

        tooltip.addAll(getInsertOffset(context.isAdvanced(), tooltip.size(), stack), additions);
    }

    private static int getInsertOffset(boolean advanced, int tooltipSize, ItemStack stack) {

        int offset = 0;

        if (advanced) {

            // item id
            offset++;

            // tag count
            if (stack.hasTag()) {

                offset++;
            }

            // durability
            if (stack.isDamaged()) {

                offset++;
            }
        }

        return Math.max(0, tooltipSize - offset);
    }
}