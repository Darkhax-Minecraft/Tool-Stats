package net.darkhax.toolstats.common;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.darkhax.pricklemc.common.api.config.ConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.util.function.Consumer;

public class ToolStatsCommon {

    public static final String MOD_ID = "toolstats";
    public static final String MOD_NAME = "Tool Stats";
    public static final Logger LOG = LogManager.getLogger(MOD_NAME);
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private static final Config CONFIG = ConfigManager.load(MOD_ID, new Config());

    public static void displayTooltipInfo(ItemStack stack, Consumer<Component> tooltip) {
        if (CONFIG.showEfficiency && !stack.is(ItemTags.SWORDS)) {
            final float speed = getDestroySpeed(stack);
            if (speed > 0f) {
                tooltip.accept(Component.translatable("tooltip.toolstats.efficiency", DECIMAL_FORMAT.format(speed)).withStyle(ChatFormatting.DARK_GREEN));
            }
        }
        if (CONFIG.showEnchantability && stack.get(DataComponents.ENCHANTABLE) instanceof Enchantable(int value) && value > 0) {
            tooltip.accept(Component.translatable("tooltip.toolstats.enchantability", value).withStyle(ChatFormatting.DARK_GREEN));
        }
        if (CONFIG.showRepairCost && stack.get(DataComponents.REPAIR_COST) instanceof Integer repairCost && repairCost > 0) {
            tooltip.accept(Component.translatable("tooltip.toolstats.repaircost", repairCost).withStyle(ChatFormatting.DARK_GREEN));
        }
        if (CONFIG.showDurability && stack.isDamageableItem() && (CONFIG.alwaysShowDurability || stack.isDamaged())) {
            tooltip.accept(Component.translatable("item.durability", stack.getMaxDamage() - stack.getDamageValue(), stack.getMaxDamage()).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    private static float getDestroySpeed(ItemStack stack) {
        float destroySpeed = getBaseDestroySpeed(stack);
        if (destroySpeed > 1.0F) {
            final int efficiencyLevel = getEnchantmentLevel(stack, Enchantments.EFFICIENCY);
            if (efficiencyLevel > 0) {
                destroySpeed += (float) (efficiencyLevel * efficiencyLevel + 1);
            }
        }
        return destroySpeed;
    }

    public static int getEnchantmentLevel(ItemStack stack, ResourceKey<Enchantment> enchantment) {
        final ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
        if (enchantments != null) {
            for (Object2IntMap.Entry<Holder<Enchantment>> enchant : enchantments.entrySet()) {
                if (enchant.getKey().is(enchantment)) {
                    return enchant.getIntValue();
                }
            }
        }
        return 0;
    }

    private static float getBaseDestroySpeed(ItemStack stack) {
        if (stack.is(ItemTags.PICKAXES)) {
            return stack.getDestroySpeed(Blocks.COBBLESTONE.defaultBlockState());
        }
        if (stack.is(ItemTags.AXES)) {
            return stack.getDestroySpeed(Blocks.OAK_PLANKS.defaultBlockState());
        }
        if (stack.is(ItemTags.SHOVELS)) {
            return stack.getDestroySpeed(Blocks.DIRT.defaultBlockState());
        }
        if (stack.is(ItemTags.HOES)) {
            return stack.getDestroySpeed(Blocks.DRIED_KELP_BLOCK.defaultBlockState());
        }
        if (stack.is(ItemTags.SWORDS)) {
            return stack.getDestroySpeed(Blocks.SUNFLOWER.defaultBlockState());
        }
        if (stack.getItem() instanceof ShearsItem shears) {
            return shears.getDestroySpeed(stack, Blocks.OAK_LEAVES.defaultBlockState());
        }
        return 0f;
    }
}