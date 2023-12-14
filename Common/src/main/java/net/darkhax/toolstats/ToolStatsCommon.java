package net.darkhax.toolstats;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.darkhax.bookshelf.api.Services;
import net.darkhax.toolstats.config.ConfigSchema;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ToolStatsCommon {

    private final TagKey<Item> TAG_IGNORE = itemTag("ignored");
    private final TagKey<Item> TAG_IGNORE_HARVEST_LEVEL = itemTag("ignore_harvest_level");
    private final TagKey<Item> TAG_IGNORE_DIG_SPEED = itemTag("ignore_dig_speed");
    private final TagKey<Item> TAG_IGNORE_ENCHANTABILITY = itemTag("ignore_enchantability");
    private final TagKey<Item> TAG_IGNORE_REPAIR_COST = itemTag("ignore_repair_cost");
    private final TagKey<Item> TAG_IGNORE_DURABILITY = itemTag("ignore_durability");

    private final ConfigSchema config;
    private final Function<ItemStack, Integer> enchantabilityResolver;
    private final Function<Tier, Integer> harvestLevelResolver;

    private final Int2ObjectMap<Component> enchantabilityCache = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<Component> repairCostCache = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<Component> harvestLevelCache = new Int2ObjectOpenHashMap<>();

    public ToolStatsCommon(Path configDir, Function<ItemStack, Integer> enchantabilityResolver, Function<Tier, Integer> harvestLevelResolver) {

        this.config = ConfigSchema.load(configDir.resolve(Constants.MOD_ID + ".json").toFile());
        this.enchantabilityResolver = enchantabilityResolver;
        this.harvestLevelResolver = harvestLevelResolver;

        Services.EVENTS.addItemTooltipListener(this::displayTooltipInfo);
    }

    private void displayTooltipInfo(ItemStack stack, List<Component> tooltip, TooltipFlag context) {

        final Item item = stack.getItem();

        if (!stack.is(TAG_IGNORE)) {

            final List<Component> additions = new ArrayList<>();

            if (stack.getItem() instanceof TieredItem tieredItem) {

                if (!stack.is(TAG_IGNORE_HARVEST_LEVEL) && this.config.showHarvestLevel) {

                    additions.add(this.harvestLevelCache.computeIfAbsent(harvestLevelResolver.apply(tieredItem.getTier()), lvl -> new TranslatableComponent("tooltip.toolstats.harvestlevel", lvl).withStyle(ChatFormatting.DARK_GREEN)));
                }

                if (!stack.is(TAG_IGNORE_DIG_SPEED) && this.config.showEfficiency) {

                    float speed = getDestroySpeed(stack);

                    if (speed > 0f) {
                        additions.add(new TranslatableComponent("tooltip.toolstats.efficiency", Constants.DECIMAL_FORMAT.format(speed)).withStyle(ChatFormatting.DARK_GREEN));
                    }
                }
            }

            if (!stack.is(TAG_IGNORE_ENCHANTABILITY) && this.config.showEnchantability && (this.config.alwaysShowEnchantability || Minecraft.getInstance().screen instanceof EnchantmentScreen)) {

                final int enchantability = this.enchantabilityResolver.apply(stack);

                if (enchantability > 0) {

                    additions.add(this.enchantabilityCache.computeIfAbsent(enchantability, enchLvl -> new TranslatableComponent("tooltip.toolstats.enchantability", enchLvl).withStyle(ChatFormatting.DARK_GREEN)));
                }
            }

            if (!stack.is(TAG_IGNORE_REPAIR_COST) && this.config.showRepairCost && (this.config.alwaysShowRepairCost || Minecraft.getInstance().screen instanceof AnvilScreen)) {

                final int repairCost = stack.getBaseRepairCost();

                if (repairCost > 0) {

                    additions.add(this.repairCostCache.computeIfAbsent(repairCost, cost -> new TranslatableComponent("tooltip.toolstats.repaircost", cost).withStyle(ChatFormatting.DARK_GREEN)));
                }
            }

            if (!context.isAdvanced() && !stack.is(TAG_IGNORE_DURABILITY) && this.config.showDurability && stack.isDamageableItem()) {

                if (this.config.alwaysShowDurability || stack.isDamaged()) {

                    additions.add(new TranslatableComponent("item.durability", stack.getMaxDamage() - stack.getDamageValue(), stack.getMaxDamage()));
                }
            }

            if (!additions.isEmpty()) {

                tooltip.addAll(getInsertOffset(context.isAdvanced(), tooltip.size(), stack), additions);
            }
        }
    }

    private float getDestroySpeed(ItemStack stack) {

        float destroySpeed = getDestroySpeed(stack, stack.getItem());

        // Account for the efficiency enchantment using similar code to Mojang.
        if (config.factorEfficiencyEnchantment && destroySpeed > 1.0F) {

            final int efficiencyLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, stack);

            if (efficiencyLevel > 0) {

                destroySpeed += (float)(efficiencyLevel * efficiencyLevel + 1);
            }
        }

        return destroySpeed;
    }

    private static float getDestroySpeed(ItemStack stack, Item item) {

        if (item instanceof PickaxeItem pickaxe) {
            return pickaxe.getDestroySpeed(stack, Blocks.COBBLESTONE.defaultBlockState());
        }

        if (item instanceof AxeItem axe) {
            return axe.getDestroySpeed(stack, Blocks.OAK_PLANKS.defaultBlockState());
        }

        if (item instanceof ShovelItem shovel) {
            return shovel.getDestroySpeed(stack, Blocks.DIRT.defaultBlockState());
        }

        if (item instanceof HoeItem hoe) {
            return hoe.getDestroySpeed(stack, Blocks.MUSHROOM_STEM.defaultBlockState());
        }

        if (item instanceof SwordItem sword) {
            return sword.getDestroySpeed(stack, Blocks.COBWEB.defaultBlockState());
        }

        if (item instanceof ShearsItem shears) {
            return shears.getDestroySpeed(stack, Blocks.OAK_LEAVES.defaultBlockState());
        }

        if (item instanceof TieredItem tieredItem) {
            return tieredItem.getTier().getSpeed();
        }

        return 0f;
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

    private static TagKey<Item> itemTag(String key) {

        return Services.TAGS.itemTag(new ResourceLocation(Constants.MOD_ID, key));
    }
}