package net.darkhax.toolstats;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.Tier;

public class ToolStatsFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        final ToolStatsCommon toolStats = new ToolStatsCommon(FabricLoader.getInstance().getConfigDir(), stack -> stack.getItem().getEnchantmentValue(), ToolStatsFabric::resolveHarvestTier);
        ItemTooltipCallback.EVENT.register(toolStats::displayTooltipInfo);
    }

    private static int resolveHarvestTier(Tier tier) {

        return tier.getLevel();
    }
}