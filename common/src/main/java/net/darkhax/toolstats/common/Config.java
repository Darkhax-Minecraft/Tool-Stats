package net.darkhax.toolstats.common;

import net.darkhax.pricklemc.common.api.annotations.Value;

public class Config {

    @Value(comment = "Should enchantability be displayed in tooltips?")
    public boolean showEnchantability = true;

    @Value(comment = "Should repair cost be shown in tooltips?")
    public boolean showRepairCost = true;

    @Value(comment = "Should mining efficiency be shown in tooltips?")
    public boolean showEfficiency = true;

    @Value(comment = "Should durability be displayed in tooltips?")
    public boolean showDurability = true;

    @Value(comment = "Should durability be displayed in tooltips when the item is fully repaired?")
    public boolean alwaysShowDurability = false;
}