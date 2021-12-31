package net.darkhax.toolstats;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Tier;

public record TierInfo(Component harvestLevel, Component digSpeed) {

    public TierInfo(Tier tier) {

        this(new TranslatableComponent("tooltip.toolstats.harvestlevel", tier.getLevel()).withStyle(ChatFormatting.DARK_GREEN), new TranslatableComponent("tooltip.toolstats.efficiency", Constants.DECIMAL_FORMAT.format(tier.getSpeed())).withStyle(ChatFormatting.DARK_GREEN));
    }
}
