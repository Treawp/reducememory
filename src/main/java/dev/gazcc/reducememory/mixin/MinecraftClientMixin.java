package dev.gazcc.reducememory.mixin;

import dev.gazcc.reducememory.ReduceMemoryMod;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;Z)V",
            at = @At("TAIL"), require = 0)
    private void onDisconnect(CallbackInfo ci) {
        if (ReduceMemoryMod.getConfig().gcOnDisconnect) {
            ReduceMemoryMod.LOGGER.info("[ReduceMemory] Disconnect GC triggered");
            System.gc();
            System.gc();
        }
    }
}
