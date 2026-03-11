package dev.gazcc.reducememory.mixin;

import dev.gazcc.reducememory.ReduceMemoryMod;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)V",
            at = @At("TAIL"), require = 0)
    private void onDisconnect(CallbackInfo ci) {
        ReduceMemoryMod.LOGGER.info("[ReduceMemory] Disconnect - forcing GC");
        System.gc();
        System.gc();
    }

    @Inject(method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V",
            at = @At("TAIL"), require = 0)
    private void onClearLevel(CallbackInfo ci) {
        System.gc();
    }
}
