package dev.gazcc.reducememory;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ReduceMemoryConfigScreen extends Screen {

    private final Screen parent;
    private int activeTab = 0;
    private static final String[] TABS = {"General", "CPU", "GPU"};

    public ReduceMemoryConfigScreen(Screen parent) {
        super(Component.literal("ReduceMemory"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        for (int i = 0; i < TABS.length; i++) {
            final int idx = i;
            this.addRenderableWidget(Button.builder(
                Component.literal(TABS[i]),
                btn -> { activeTab = idx; clearWidgets(); init(); }
            ).bounds(i * 80, 0, 79, 20).build());
        }

        ModConfig cfg = ReduceMemoryMod.getConfig();
        int cx = width / 2 - 100;
        int sy = 30;

        if (activeTab == 0) {
            addBtn("Auto GC: " + on(cfg.enableAutoGC), cx, sy, btn -> {
                cfg.enableAutoGC = !cfg.enableAutoGC; save();
                btn.setMessage(Component.literal("Auto GC: " + on(cfg.enableAutoGC)));
            });
            addBtn("GC on Disconnect: " + on(cfg.gcOnDisconnect), cx, sy+22, btn -> {
                cfg.gcOnDisconnect = !cfg.gcOnDisconnect; save();
                btn.setMessage(Component.literal("GC on Disconnect: " + on(cfg.gcOnDisconnect)));
            });
            addBtn("GC on Low Memory: " + on(cfg.gcOnLowMemory), cx, sy+44, btn -> {
                cfg.gcOnLowMemory = !cfg.gcOnLowMemory; save();
                btn.setMessage(Component.literal("GC on Low Memory: " + on(cfg.gcOnLowMemory)));
            });
            addBtn("Memory HUD: " + on(cfg.showMemoryHud), cx, sy+66, btn -> {
                cfg.showMemoryHud = !cfg.showMemoryHud; save();
                btn.setMessage(Component.literal("Memory HUD: " + on(cfg.showMemoryHud)));
            });
            addBtn("Chunk Unload Boost: " + on(cfg.chunkUnloadBoost), cx, sy+88, btn -> {
                cfg.chunkUnloadBoost = !cfg.chunkUnloadBoost; save();
                btn.setMessage(Component.literal("Chunk Unload Boost: " + on(cfg.chunkUnloadBoost)));
            });
            addBtn("Preset: " + cfg.memoryPreset.name(), cx, sy+110, btn -> {
                ModConfig.MemoryPreset[] vals = ModConfig.MemoryPreset.values();
                cfg.memoryPreset = vals[(cfg.memoryPreset.ordinal() + 1) % vals.length];
                cfg.applyPreset(); save();
                btn.setMessage(Component.literal("Preset: " + cfg.memoryPreset.name()));
            });
        } else if (activeTab == 1) {
            addBtn("Reduce CPU Load: " + on(cfg.reduceCpuLoad), cx, sy, btn -> {
                cfg.reduceCpuLoad = !cfg.reduceCpuLoad; save();
                btn.setMessage(Component.literal("Reduce CPU Load: " + on(cfg.reduceCpuLoad)));
            });
            addBtn("Improve Chunk Loading: " + on(cfg.improveChunkLoading), cx, sy+22, btn -> {
                cfg.improveChunkLoading = !cfg.improveChunkLoading; save();
                btn.setMessage(Component.literal("Improve Chunk Loading: " + on(cfg.improveChunkLoading)));
            });
            addBtn("Entity Culling: " + on(cfg.entityCulling), cx, sy+44, btn -> {
                cfg.entityCulling = !cfg.entityCulling; save();
                btn.setMessage(Component.literal("Entity Culling: " + on(cfg.entityCulling)));
            });
            addBtn("Reduce Particles: " + on(cfg.reduceParticles), cx, sy+66, btn -> {
                cfg.reduceParticles = !cfg.reduceParticles; save();
                btn.setMessage(Component.literal("Reduce Particles: " + on(cfg.reduceParticles)));
            });
        } else {
            addBtn("Reduce GPU Load: " + on(cfg.reduceGpuLoad), cx, sy, btn -> {
                cfg.reduceGpuLoad = !cfg.reduceGpuLoad; save();
                btn.setMessage(Component.literal("Reduce GPU Load: " + on(cfg.reduceGpuLoad)));
            });
            addBtn("Max BG FPS: " + cfg.maxFpsOnBackground, cx, sy+22, btn -> {
                cfg.maxFpsOnBackground = cfg.maxFpsOnBackground >= 60 ? 1 : cfg.maxFpsOnBackground + 5;
                save();
                btn.setMessage(Component.literal("Max BG FPS: " + cfg.maxFpsOnBackground));
            });
        }

        this.addRenderableWidget(Button.builder(
            Component.literal("Cancel"),
            btn -> this.minecraft.setScreen(parent)
        ).bounds(width / 2 - 102, height - 28, 100, 20).build());

        this.addRenderableWidget(Button.builder(
            Component.literal("Save & Quit"),
            btn -> { save(); this.minecraft.setScreen(parent); }
        ).bounds(width / 2 + 2, height - 28, 100, 20).build());
    }

    private void addBtn(String label, int x, int y, Button.OnPress action) {
        this.addRenderableWidget(Button.builder(
            Component.literal(label), action
        ).bounds(x, y, 200, 20).build());
    }

    private String on(boolean val) {
        return val ? "Yes" : "No";
    }

    private void save() {
        AutoConfig.getConfigHolder(ModConfig.class).save();
    }

    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredString(this.font, "ReduceMemory Options", this.width / 2, 22, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
                                                 }
