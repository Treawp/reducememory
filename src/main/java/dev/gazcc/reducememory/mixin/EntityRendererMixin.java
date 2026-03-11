package dev.gazcc.reducememory.mixin;

import dev.gazcc.reducememory.ReduceMemoryMod;
import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyReturnValue;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @ModifyReturnValue(method = "getRenderDistance", at = @At("RETURN"), require = 0)
    private float reducememory$reduceEntityRenderDistance(float original) {
        return (float)(original * ReduceMemoryMod.getDynamicEntityDistanceMultiplier());
    }
}
