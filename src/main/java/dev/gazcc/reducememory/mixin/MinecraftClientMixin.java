package dev.gazcc.reducememory.mixin;

import dev.gazcc.reducememory.ReduceMemoryMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.Minecraft")
public class MinecraftClientMixin {
    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)V",
            at = @At("TAIL"), require = 0)
    private void onDisconnect(CallbackInfo ci) {
        if (ReduceMemoryMod.getConfig() != null &&
            ReduceMemoryMod.getConfig().gcOnDisconnect) {
            System.gc();
            System.gc();
        }
    }
}
