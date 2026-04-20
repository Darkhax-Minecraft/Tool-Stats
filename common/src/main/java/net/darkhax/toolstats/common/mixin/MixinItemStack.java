package net.darkhax.toolstats.common.mixin;

import net.darkhax.toolstats.common.ToolStatsCommon;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class MixinItemStack {

    @Inject(method = "addAttributeTooltips", at = @At("RETURN"))
    private void addAttributeTooltips(Consumer<Component> consumer, TooltipDisplay display, @Nullable Player player, CallbackInfo ci) {
        ToolStatsCommon.displayTooltipInfo((ItemStack) (Object) this, consumer);
    }
}
