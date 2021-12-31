package net.darkhax.toolstats;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.Environment;
import net.minecraftforge.network.NetworkConstants;

import java.util.function.Consumer;

@Mod(Constants.MOD_ID)
public class ToolStatsForge {

    public ToolStatsForge() {

        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        if (Environment.get().getDist() == Dist.CLIENT) {

            final ToolStatsCommon toolStats = new ToolStatsCommon(FMLPaths.CONFIGDIR.get(), ItemStack::getItemEnchantability);
            MinecraftForge.EVENT_BUS.addListener((Consumer<ItemTooltipEvent>) event -> toolStats.displayTooltipInfo(event.getItemStack(), event.getFlags(), event.getToolTip()));
        }
    }
}