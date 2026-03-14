package dev.gazcc.reducememory;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;

public class MemoryHudRenderer {

    public static void register() {
        HudRenderCallback.EVENT.register(MemoryHudRenderer::render);
    }

    private static void render(GuiGraphics ctx, DeltaTracker dt) {
        ModConfig cfg = ReduceMemoryMod.getConfig();
        if (cfg == null || !cfg.showMemoryHud) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) return;

        Runtime r = Runtime.getRuntime();
        long max = r.maxMemory() / (1024 * 1024);
        long used = (r.totalMemory() - r.freeMemory()) / (1024 * 1024);
        int mem = (int)((double) used / max * 100);
        int fps = mc.getFps();

        int x = cfg.hudX;
        int y = cfg.hudY;

        int mCol = mem > 85 ? 0xFFFF4444 : mem > 65 ? 0xFFFFAA00 : 0xFF55FF55;
        int fCol = fps < 15 ? 0xFFFF4444 : fps < 30 ? 0xFFFFAA00 : 0xFF55FF55;

        ctx.fill(x, y, x + 65, y + 46, 0xBB000000);
        ctx.drawString(mc.font, "M:" + mem + "%", x + 3, y + 4, mCol, true);
        ctx.drawString(mc.font, "F:" + fps, x + 3, y + 14, fCol, true);
        ctx.drawString(mc.font, "C:N/A", x + 3, y + 24, 0xFFAAAAAA, true);
        ctx.drawString(mc.font, "G:N/A", x + 3, y + 34, 0xFFAAAAAA, true);
    }
}
