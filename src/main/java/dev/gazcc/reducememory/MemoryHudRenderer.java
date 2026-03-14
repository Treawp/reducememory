package dev.gazcc.reducememory;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class MemoryHudRenderer {

    private static boolean dragging = false;
    private static int dragOffsetX = 0;
    private static int dragOffsetY = 0;
    private static final int HUD_WIDTH = 120;
    private static final int HUD_HEIGHT = 32;

    public static void register() {
        HudRenderCallback.EVENT.register(MemoryHudRenderer::render);
    }

    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        ModConfig config = ReduceMemoryMod.getConfig();
        if (config == null || !config.showMemoryHud) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.hudHidden) return;

        Runtime r = Runtime.getRuntime();
        long max = r.maxMemory() / (1024 * 1024);
        long used = (r.totalMemory() - r.freeMemory()) / (1024 * 1024);
        double ratio = (double) used / max;

        int x = config.hudX;
        int y = config.hudY;

        // Background
        context.fill(x, y, x + HUD_WIDTH, y + HUD_HEIGHT, 0xAA000000);

        // Title
        context.drawTextWithShadow(
            client.textRenderer,
            "§b[RM] Memory",
            x + 4, y + 4, 0xFFFFFF
        );

        // MB text
        int color = ratio > 0.85 ? 0xFF4444 : ratio > 0.65 ? 0xFFAA00 : 0x55FF55;
        context.drawTextWithShadow(
            client.textRenderer,
            used + "MB / " + max + "MB",
            x + 4, y + 14, color
        );

        // Memory bar
        if (config.showMemoryBar) {
            int barY = y + HUD_HEIGHT - 5;
            int barWidth = HUD_WIDTH - 8;
            // Bar background
            context.fill(x + 4, barY, x + 4 + barWidth, barY + 3, 0xFF333333);
            // Bar fill
            int fillWidth = (int)(barWidth * ratio);
            int barColor = ratio > 0.85 ? 0xFFFF4444 : ratio > 0.65 ? 0xFFFFAA00 : 0xFF55FF55;
            context.fill(x + 4, barY, x + 4 + fillWidth, barY + 3, barColor);
        }
    }

    public static boolean onMouseClick(double mouseX, double mouseY, int button) {
        ModConfig config = ReduceMemoryMod.getConfig();
        if (config == null || !config.showMemoryHud) return false;

        int x = config.hudX;
        int y = config.hudY;

        if (mouseX >= x && mouseX <= x + HUD_WIDTH &&
            mouseY >= y && mouseY <= y + HUD_HEIGHT && button == 0) {
            dragging = true;
            dragOffsetX = (int) mouseX - x;
            dragOffsetY = (int) mouseY - y;
            return true;
        }
        return false;
    }

    public static void onMouseRelease(int button) {
        if (button == 0) dragging = false;
    }

    public static void onMouseDrag(double mouseX, double mouseY) {
        if (!dragging) return;
        ModConfig config = ReduceMemoryMod.getConfig();
        if (config == null) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int screenW = client.getWindow().getScaledWidth();
        int screenH = client.getWindow().getScaledHeight();

        config.hudX = (int) Math.max(0, Math.min(mouseX - dragOffsetX, screenW - HUD_WIDTH));
        config.hudY = (int) Math.max(0, Math.min(mouseY - dragOffsetY, screenH - HUD_HEIGHT));

        // Save posisi
        AutoConfig.getConfigHolder(ModConfig.class).save();
    }
          }
