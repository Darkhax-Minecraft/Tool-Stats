package net.darkhax.toolstats.fabric;

import net.darkhax.toolstats.common.ToolStatsCommon;
import net.fabricmc.api.ClientModInitializer;

public class ToolStatsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ToolStatsCommon.LOG.debug("Initializing ToolStats");
    }
}