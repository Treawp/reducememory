package dev.gazcc.reducememory;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;

public class MemoryHudRenderer {

    private static boolean dragging = false;
    private static int dragOffsetX = 0;
    private static int dragOffsetY = 0;
    private static final int W = 80;
    private static final int H = 52;
    private static final OperatingSystemMXBean osBean =
        (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

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
        int cpu = (int)(osBean.getCpuLoad() * 100);
        if (cpu < 0) cpu = 0;

        int x = cfg.hudX;
        int y = cfg.hudY;

        // Background
        ctx.fill(x, y, x + W, y + H, 0xBB000000);

        // M: memory%
        int mCol = memPct > 85 ? 0xFF4444 : memPct > 65 ? 0xFFAA00 : 0x55FF55;
        ctx.drawString(mc.font, "\u00a7fM:\u00a7" + (memPct > 85 ? "c" : memPct > 65 ? "6" : "a") + memPct + "%", x + 4, y + 4, mCol);

        // F: fps
        int fCol = fps < 15 ? 0xFF4444 : fps < 30 ? 0xFFAA00 : 0x55FF55;
        ctx.drawString(mc.font, "\u00a7fF:\u00a7" + (fps < 15 ? "c" : fps < 30 ? "6" : "a") + fps, x + 4, y + 14, fCol);

        // C: cpu%
        int cCol = cpu > 85 ? 0xFF4444 : cpu > 60 ? 0xFFAA00 : 0x55FF55;
        ctx.drawString(mc.font, "\u00a7fC:\u00a7" + (cpu > 85 ? "c" : cpu > 60 ? "6" : "a") + cpu + "%", x + 4, y + 24, cCol);

        // G: gpu (N/A karena API ga ada)
        ctx.drawString(mc.font, "\u00a7fG:\u00a7aN/A", x + 4, y + 34, 0xAAAAAA);

        // Bar memory
        if (cfg.showMemoryBar) {
            int by = y + H - 5;
            int bw = W - 8;
            ctx.fill(x + 4, by, x + 4 + bw, by + 3, 0xFF333333);
            int fw = (int)(bw * memPct / 100.0);
            int bc = memPct > 85 ? 0xFFFF4444 : memPct > 65 ? 0xFFFFAA00 : 0xFF55FF55;
            ctx.fill(x + 4, by, x + 4 + fw, by + 3, bc);
        }
    }

    public static boolean onMouseClick(double mx, double my, int btn) {
        ModConfig cfg = ReduceMemoryMod.getConfig();
        if (cfg == null || !cfg.showMemoryHud) return false;
        if (mx >= cfg.hudX && mx <= cfg.hudX + W &&
            my >= cfg.hudY && my <= cfg.hudY + H && btn == 0) {
            dragging = true;
            dragOffsetX = (int) mx - cfg.hudX;
            dragOffsetY = (int) my - cfg.hudY;
            return true;
        }
        return false;
    }

    public static void onMouseRelease(int btn) {
        if (btn == 0) dragging = false;
    }

    public static void onMouseDrag(double mx, double my) {
        if (!dragging) return;
        ModConfig cfg = ReduceMemoryMod.getConfig();
        if (cfg == null) return;
        Minecraft mc = Minecraft.getInstance();
        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();
        cfg.hudX = (int) Math.max(0, Math.min(mx - dragOffsetX, sw - W));
        cfg.hudY = (int) Math.max(0, Math.min(my - dragOffsetY, sh - H));
        AutoConfig.getConfigHolder(ModConfig.class).save();
    }
    }
