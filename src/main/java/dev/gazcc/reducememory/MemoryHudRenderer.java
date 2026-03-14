package dev.gazcc.reducememory;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;

public class MemoryHudRenderer {

    private static final int W = 62;
    private static final int H = 48;

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
        int memPct = (int)((double) used / max * 100);
        int fps = mc.getFps();

        int x = cfg.hudX;
        int y = cfg.hudY;

        ctx.fill(x, y, x + W, y + H, 0xBB000000);

        int mCol = memPct > 85 ? 0xFF4444 : memPct > 65 ? 0xFFAA00 : 0x55FF55;
        int fCol = fps < 15 ? 0xFF4444 : fps < 30 ? 0xFFAA00 : 0x55FF55;

        ctx.drawString(mc.font, "M:" + memPct + "%", x + 3, y + 4, mCol);
        ctx.drawString(mc.font, "F:" + fps, x + 3, y + 14, fCol);
        ctx.drawString(mc.font, "C:N/A", x + 3, y + 24, 0xAAAAAA);
        ctx.drawString(mc.font, "G:N/A", x + 3, y + 34, 0xAAAAAA);
    }
    }
