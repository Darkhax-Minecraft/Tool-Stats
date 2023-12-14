package net.darkhax.toolstats;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.EnchantmentScreen;
import net.minecraft.client.gui.screen.inventory.AnvilScreen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TieredItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Mod("toolstats")
public class ToolStats {

    private final Configuration config = new Configuration();
    private final DecimalFormat format = new DecimalFormat("0.##");

    public ToolStats() {

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        if (FMLEnvironment.dist == Dist.CLIENT) {

            ModLoadingContext.get().registerConfig(Type.CLIENT, this.config.getSpec());
            MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onItemTooltip);
        }
    }

    private void onItemTooltip(ItemTooltipEvent event) {

        final List<ITextComponent> additions = new ArrayList<>();

        final ItemStack stack = event.getItemStack();

        if (stack.getItem() instanceof TieredItem) {

            final TieredItem item = (TieredItem) stack.getItem();
            final IItemTier tier = item.getTier();

            if (this.config.shouldShowHarvestLevel()) {

                additions.add(new TranslationTextComponent("tooltip.toolstats.harvestlevel", tier.getHarvestLevel()).mergeStyle(TextFormatting.DARK_GREEN));
            }

            if (this.config.shouldShowEfficiency()) {

                additions.add(new TranslationTextComponent("tooltip.toolstats.efficiency", this.format.format(getDestroySpeed(stack))).mergeStyle(TextFormatting.DARK_GREEN));
            }
        }

        if (this.config.shouldShowEnchantability() && (this.config.shouldShowEnchantabilityAlways() || Minecraft.getInstance().currentScreen instanceof EnchantmentScreen)) {

            final int enchantability = stack.getItemEnchantability();

            if (enchantability > 0) {

                additions.add(new TranslationTextComponent("tooltip.toolstats.enchantability", enchantability).mergeStyle(TextFormatting.DARK_GREEN));
            }
        }

        if (this.config.shouldShowRepairCost() && Minecraft.getInstance().currentScreen instanceof AnvilScreen) {

            final int repairCost = stack.getRepairCost();

            if (repairCost > 0) {

                additions.add(new TranslationTextComponent("tooltip.toolstats.repaircost", repairCost).mergeStyle(TextFormatting.DARK_GREEN));
            }
        }

        if (this.config.shouldShowDurability() && stack.isDamageable()) {

            additions.add(new TranslationTextComponent("item.durability", stack.getMaxDamage() - stack.getDamage(), stack.getMaxDamage()));
        }

        event.getToolTip().addAll(getInsertOffset(event.getFlags().isAdvanced(), event.getToolTip().size(), stack), additions);
    }

    private float getDestroySpeed(ItemStack stack) {

        float destroySpeed = getDestroySpeed(stack, stack.getItem());

        // Account for the efficiency enchantment using similar code to Mojang.
        if (config.factorEfficiency() && destroySpeed > 1.0F) {

            final int efficiencyLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);

            if (efficiencyLevel > 0) {

                destroySpeed += (float)(efficiencyLevel * efficiencyLevel + 1);
            }
        }

        return destroySpeed;
    }

    private static float getDestroySpeed(ItemStack stack, Item item) {

        if (item instanceof PickaxeItem) {
            return item.getDestroySpeed(stack, Blocks.COBBLESTONE.getDefaultState());
        }

        if (item instanceof AxeItem) {
            return item.getDestroySpeed(stack, Blocks.OAK_PLANKS.getDefaultState());
        }

        if (item instanceof ShovelItem) {
            return item.getDestroySpeed(stack, Blocks.DIRT.getDefaultState());
        }

        if (item instanceof HoeItem) {
            return item.getDestroySpeed(stack, Blocks.MUSHROOM_STEM.getDefaultState());
        }

        if (item instanceof SwordItem) {
            return item.getDestroySpeed(stack, Blocks.COBWEB.getDefaultState());
        }

        if (item instanceof ShearsItem) {
            return item.getDestroySpeed(stack, Blocks.OAK_LEAVES.getDefaultState());
        }

        if (item instanceof TieredItem) {
            return ((TieredItem) item).getTier().getEfficiency();
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
}