package net.darkhax.toolstats;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.Tier;

public class ToolStatsFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        new ToolStatsCommon(FabricLoader.getInstance().getConfigDir(), stack -> stack.getItem().getEnchantmentValue(), Tier::getLevel);
    }
}