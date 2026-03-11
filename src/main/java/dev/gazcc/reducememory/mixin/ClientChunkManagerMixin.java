package dev.gazcc.reducememory.mixin;

import dev.gazcc.reducememory.ReduceMemoryMod;
import net.minecraft.client.world.ClientChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ClientChunkManager.class)
public class ClientChunkManagerMixin {
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientChunkManager$ClientChunkMap;<init>(I)V"), index = 0, require = 0)
    private static int reducememory$reduceChunkCacheRadius(int original) {
        long maxMem = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        if (maxMem < 512) return Math.max(2, original - 3);
        if (maxMem < 1024) return Math.max(3, original - 2);
        if (maxMem < 2048) return Math.max(4, original - 1);
        return original;
    }
}
