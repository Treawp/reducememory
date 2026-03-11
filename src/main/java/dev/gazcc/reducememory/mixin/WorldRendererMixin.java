package dev.gazcc.reducememory.mixin;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true, require = 0)
    private static int reducememory$limitRenderDistance(int renderDistance) {
        long maxMem = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        if (maxMem < 512) return Math.min(renderDistance, 4);
        if (maxMem < 1024) return Math.min(renderDistance, 6);
        if (maxMem < 2048) return Math.min(renderDistance, 8);
        return renderDistance;
    }
}
