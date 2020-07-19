package net.darkhax.toolstats;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.EnchantmentScreen;
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
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;

@Mod("toolstats")
public class ToolStats {
    
    private final Configuration config = new Configuration();
    private final DecimalFormat format = new DecimalFormat("0.##");
    
    public ToolStats() {
        
        ModLoadingContext.get().registerConfig(Type.CLIENT, this.config.getSpec());        
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onItemTooltip));
    }
    
    private void onItemTooltip(ItemTooltipEvent event) {
    	
    	final List<ITextComponent> additions = new ArrayList<>();
    	
    	final ItemStack stack = event.getItemStack();
    	
    	if (stack.getItem() instanceof TieredItem) {
    		
    		final TieredItem item = ((TieredItem) stack.getItem());
    		final IItemTier tier = item.getTier();
    		
    		if (config.shouldShowHarvestLevel()) {
    			
    			additions.add(new TranslationTextComponent("tooltip.toolstats.harvestlevel", tier.getHarvestLevel()).applyTextStyle(TextFormatting.DARK_GREEN));
    		}
    		
    		if (config.shouldShowEfficiency()) {
    			
    			additions.add(new TranslationTextComponent("tooltip.toolstats.efficiency", format.format(tier.getEfficiency())).applyTextStyle(TextFormatting.DARK_GREEN));
    		}
    	}
    	
    	if (config.shouldShowEnchantability() && Minecraft.getInstance().currentScreen instanceof EnchantmentScreen) {
    		
    		final int enchantability = stack.getItemEnchantability();
    		
    		if (enchantability > 0) {
    			
    			additions.add(new TranslationTextComponent("tooltip.toolstats.enchantability", enchantability).applyTextStyle(TextFormatting.DARK_GREEN));
    		}
    	}
    	
    	final int offset = Math.max(0, event.getFlags().isAdvanced() ? event.getToolTip().size() - (stack.hasTag() ? 2 : 1) : event.getToolTip().size());
    	event.getToolTip().addAll(offset, additions);
    }
}