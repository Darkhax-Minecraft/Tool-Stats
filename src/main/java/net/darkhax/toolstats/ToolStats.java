package net.darkhax.toolstats;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.EnchantmentScreen;
import net.minecraft.client.gui.screen.inventory.AnvilScreen;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
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

@Mod("toolstats")
public class ToolStats {
    
    private final Configuration config = new Configuration();
    private final DecimalFormat format = new DecimalFormat("0.##");
    
    public ToolStats() {
        
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of( () -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        
        if (FMLEnvironment.dist == Dist.CLIENT) {
            
            ModLoadingContext.get().registerConfig(Type.CLIENT, this.config.getSpec());
            MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onItemTooltip);
        }
    }
    
    private void onItemTooltip (ItemTooltipEvent event) {
        
        final List<ITextComponent> additions = new ArrayList<>();
        
        final ItemStack stack = event.getItemStack();
        
        if (stack.getItem() instanceof TieredItem) {
            
            final TieredItem item = (TieredItem) stack.getItem();
            final IItemTier tier = item.getTier();
            
            if (this.config.shouldShowHarvestLevel()) {
                
                additions.add(new TranslationTextComponent("tooltip.toolstats.harvestlevel", tier.getHarvestLevel()).mergeStyle(TextFormatting.DARK_GREEN));
            }
            
            if (this.config.shouldShowEfficiency()) {
                
                additions.add(new TranslationTextComponent("tooltip.toolstats.efficiency", this.format.format(tier.getEfficiency())).mergeStyle(TextFormatting.DARK_GREEN));
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
    
    private static int getInsertOffset (boolean advanced, int tooltipSize, ItemStack stack) {
        
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