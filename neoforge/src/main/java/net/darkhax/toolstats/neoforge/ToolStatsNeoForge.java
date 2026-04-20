package net.darkhax.toolstats.neoforge;

import net.darkhax.toolstats.common.ToolStatsCommon;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddAttributeTooltipsEvent;

@Mod(ToolStatsCommon.MOD_ID)
public class ToolStatsNeoForge {

    public ToolStatsNeoForge() {
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            ToolStatsCommon.LOG.debug("Initializing ToolStats");
            NeoForge.EVENT_BUS.addListener(AddAttributeTooltipsEvent.class, e -> ToolStatsCommon.displayTooltipInfo(e.getStack(), e::addTooltipLines));
        }
    }
}