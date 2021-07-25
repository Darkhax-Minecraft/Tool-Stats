package net.darkhax.toolstats;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class ToolStats implements ClientModInitializer {
	
	private final DecimalFormat format = new DecimalFormat("0.##");
	
	@Override
	public void onInitializeClient() {
		
		ItemTooltipCallback.EVENT.register(this::onTooltipDisplayed);
	}
	
    private void onTooltipDisplayed (ItemStack stack, TooltipContext context, List<Text> lines) {
        
        final List<Text> additions = new ArrayList<>();
        
        if (stack.getItem() instanceof ToolItem) {
            
            final ToolItem item = (ToolItem) stack.getItem();
            final ToolMaterial tier = item.getMaterial();
            
            additions.add(new TranslatableText("tooltip.toolstats.harvestlevel", tier.getMiningLevel()).formatted(Formatting.DARK_GREEN));
            additions.add(new TranslatableText("tooltip.toolstats.efficiency", this.format.format(tier.getMiningSpeedMultiplier())).formatted(Formatting.DARK_GREEN));
            
            if (MinecraftClient.getInstance().currentScreen instanceof EnchantmentScreen) {
                
                final int enchantability = tier.getEnchantability();
                
                if (enchantability > 0) {
                    
                    additions.add(new TranslatableText("tooltip.toolstats.enchantability", enchantability).formatted(Formatting.DARK_GREEN));
                }
            }
        }
        
        if (MinecraftClient.getInstance().currentScreen instanceof AnvilScreen) {
            
            final int repairCost = stack.getRepairCost();
            
            if (repairCost > 0) {
                
                additions.add(new TranslatableText("tooltip.toolstats.repaircost", repairCost).formatted(Formatting.DARK_GREEN));
            }
        }
        
        if (stack.isDamageable()) {
            
            additions.add(new TranslatableText("item.durability", stack.getMaxDamage() - stack.getDamage(), stack.getMaxDamage()));
        }
        
        lines.addAll(getInsertOffset(context.isAdvanced(), lines.size(), stack), additions);
    }
    
    private static int getInsertOffset (boolean advanced, int tooltipSize, ItemStack stack) {
        
        int offset = 0;
        
        if (advanced) {
            
            // item id
            offset++;
            
            // tag count
            if (stack.hasNbt()) {
                
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