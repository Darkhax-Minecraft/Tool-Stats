package net.darkhax.toolstats;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.TierSortingRegistry;
import net.neoforged.neoforgespi.Environment;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

@Mod(Constants.MOD_ID)
public class ToolStatsNeoForge {

    private final Map<Tier, Integer> tierCache = new WeakHashMap<>();
    private final Map<Integer, Tier> vanillaTierLevels = Map.of(0, Tiers.WOOD, 1, Tiers.STONE, 2, Tiers.IRON, 3, Tiers.DIAMOND, 4, Tiers.NETHERITE);

    public ToolStatsNeoForge() {

        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> IExtensionPoint.DisplayTest.IGNORESERVERONLY, (a, b) -> true));

        if (Environment.get().getDist() == Dist.CLIENT) {


            final ToolStatsCommon toolStats = new ToolStatsCommon(FMLPaths.CONFIGDIR.get(), ItemStack::getEnchantmentValue, this::getTierLevel);
        }
    }

    public int getTierLevel(Tier tier) {

        if (!tierCache.containsKey(tier)) {

            // If tier is sorted but has not been cached revalidate the cache.
            if (TierSortingRegistry.isTierSorted(tier)) {

                tierCache.clear();

                int tierLevel = 0;

                for (Tier currentTier : TierSortingRegistry.getSortedTiers()) {

                    final ResourceLocation id = TierSortingRegistry.getName(currentTier);
                    final boolean isVanilla = id != null && "minecraft".equals(id.getNamespace());

                    // Tier is not the same as the previous one.
                    // TODO Previous versions checked if the custom Forge tag was empty. Tags don't work like this anymore.
                    if ((isVanilla || (currentTier.getTag() != null && !isTagEmpty(currentTier.getTag())))) {

                        tierLevel++;
                    }

                    tierCache.put(currentTier, tierLevel);
                }
            }

            // Unregistered tiers get matched with their vanilla counterparts.
            else if (vanillaTierLevels.containsKey(tier.getLevel())) {

                // Remap from vanilla tier levels to Forge's sorted tier levels.
                tierCache.put(tier, getTierLevel(vanillaTierLevels.get(tier.getLevel())));
            }
        }

        return tierCache.computeIfAbsent(tier, t -> -1);
    }

    private static boolean isTagEmpty(TagKey<Block> key) {

        final Optional<HolderSet.Named<Block>> holders = BuiltInRegistries.BLOCK.getTag(key);
        return holders == null || holders.isEmpty() || holders.get().size() <= 0;
    }
}