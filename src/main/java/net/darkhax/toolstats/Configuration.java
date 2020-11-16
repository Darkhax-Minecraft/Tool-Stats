package net.darkhax.toolstats;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public class Configuration {
    
    private final ForgeConfigSpec spec;
    
    private final BooleanValue showEnchantability;
    private final BooleanValue showHarvestLevel;
    private final BooleanValue showEfficiency;
    private final BooleanValue showRepairCost;
    private final BooleanValue showDurability;
    
    public Configuration() {
        
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        
        // General Configs
        builder.comment("General settings for the mod.");
        builder.push("general");
        
        builder.comment("Should enchantability be shown?");
        this.showEnchantability = builder.define("show-enchantability", true);
        
        builder.comment("Should harvest level be shown?");
        this.showHarvestLevel = builder.define("show-harvest-level", true);
        
        builder.comment("Should mining efficiency be shown?");
        this.showEfficiency = builder.define("show-efficiency", true);
        
        builder.comment("Should repair cost be shown in the anvil GUI?");
        this.showRepairCost = builder.define("show-repair-cost", true);
        
        builder.comment("Should the durability be shown on the tool?");
        this.showDurability = builder.define("show-durability", true);
        
        builder.pop();
        this.spec = builder.build();
    }
    
    public ForgeConfigSpec getSpec () {
        
        return this.spec;
    }
    
    public boolean shouldShowEnchantability() {
    	
    	return this.showEnchantability.get();
    }
    
    public boolean shouldShowHarvestLevel() {
    	
    	return this.showHarvestLevel.get();
    }
    
    public boolean shouldShowEfficiency() {
    	
    	return this.showEfficiency.get();
    }
    
    public boolean shouldShowRepairCost() {
    	
    	return this.showRepairCost.get();
    }
    
    public boolean shouldShowDurability() {
    	
    	return this.showDurability.get();
    }
}