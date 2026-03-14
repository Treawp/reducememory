package dev.gazcc.reducememory;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ReduceMemoryConfigScreen extends Screen {

    private final Screen parent;
    private int activeTab = 0; // 0=Memory, 1=CPU, 2=GPU
    private static final String[] TABS = {"Memory", "CPU", "GPU"};

    public ReduceMemoryConfigScreen(Screen parent) {
        super(Component.literal("ReduceMemory Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        // Tab buttons
        for (int i = 0; i < TABS.length; i++) {
            final int idx = i;
            this.addRenderableWidget(Button.builder(
                Component.literal(TABS[i]),
                btn -> { activeTab = idx; rebuildWidgets(); }
            ).bounds(10 + i * 70, 20, 65, 20).build());
        }

        ModConfig cfg = ReduceMemoryMod.getConfig();

        if (activeTab == 0) {
            // Memory tab
            addToggleButton("Auto GC: " + (cfg.enableAutoGC ? "ON" : "OFF"), 50, btn -> {
                cfg.enableAutoGC = !cfg.enableAutoGC;
                save(); rebuildWidgets();
            });
            addToggleButton("GC on Disconnect: " + (cfg.gcOnDisconnect ? "ON" : "OFF"), 80, btn -> {
                cfg.gcOnDisconnect = !cfg.gcOnDisconnect;
                save(); rebuildWidgets();
            });
            addToggleButton("GC on Low Memory: " + (cfg.gcOnLowMemory ? "ON" : "OFF"), 110, btn -> {
                cfg.gcOnLowMemory = !cfg.gcOnLowMemory;
                save(); rebuildWidgets();
            });
            addToggleButton("Show Memory HUD: " + (cfg.showMemoryHud ? "ON" : "OFF"), 140, btn -> {
                cfg.showMemoryHud = !cfg.showMemoryHud;
                save(); rebuildWidgets();
            });
            addToggleButton("Show Memory Bar: " + (cfg.showMemoryBar ? "ON" : "OFF"), 170, btn -> {
                cfg.showMemoryBar = !cfg.showMemoryBar;
                save(); rebuildWidgets();
            });
            addToggleButton("Chunk Unload Boost: " + (cfg.chunkUnloadBoost ? "ON" : "OFF"), 200, btn -> {
                cfg.chunkUnloadBoost = !cfg.chunkUnloadBoost;
                save(); rebuildWidgets();
            });
            addToggleButton("Preset: " + cfg.memoryPreset.name(), 230, btn -> {
                ModConfig.MemoryPreset[] vals = ModConfig.MemoryPreset.values();
                int next = (cfg.memoryPreset.ordinal() + 1) % vals.length;
                cfg.memoryPreset = vals[next];
                cfg.applyPreset();
                save(); rebuildWidgets();
            });
        } else if (activeTab == 1) {
            // CPU tab
            addToggleButton("Reduce CPU Load: " + (cfg.reduceCpuLoad ? "ON" : "OFF"), 50, btn -> {
                cfg.reduceCpuLoad = !cfg.reduceCpuLoad;
                save(); rebuildWidgets();
            });
            addToggleButton("Improve Chunk Loading: " + (cfg.improveChunkLoading ? "ON" : "OFF"), 80, btn -> {
                cfg.improveChunkLoading = !cfg.improveChunkLoading;
                save(); rebuildWidgets();
            });
            addToggleButton("Entity Culling: " + (cfg.entityCulling ? "ON" : "OFF"), 110, btn -> {
                cfg.entityCulling = !cfg.entityCulling;
                save(); rebuildWidgets();
            });
            addToggleButton("Reduce Particles: " + (cfg.reduceParticles ? "ON" : "OFF"), 140, btn -> {
                cfg.reduceParticles = !cfg.reduceParticles;
                save(); rebuildWidgets();
            });
        } else {
            // GPU tab
            addToggleButton("Reduce GPU Load: " + (cfg.reduceGpuLoad ? "ON" : "OFF"), 50, btn -> {
                cfg.reduceGpuLoad = !cfg.reduceGpuLoad;
                save(); rebuildWidgets();
            });
            addToggleButton("Max BG FPS: " + cfg.maxFpsOnBackground, 80, btn -> {
                cfg.maxFpsOnBackground = cfg.maxFpsOnBackground >= 60 ? 1 : cfg.maxFpsOnBackground + 5;
                save(); rebuildWidgets();
            });
        }

        // Back button
        this.addRenderableWidget(Button.builder(
            Component.literal("Back"),
            btn -> this.minecraft.setScreen(parent)
        ).bounds(width / 2 - 50, height - 30, 100, 20).build());
    }

    private void addToggleButton(String label, int y, Button.OnPress action) {
        this.addRenderableWidget(Button.builder(
            Component.literal(label), action
        ).bounds(width / 2 - 100, y, 200, 20).build());
    }

    private void save() {
        AutoConfig.getConfigHolder(ModConfig.class).save();
    }

    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        this.renderBackground(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredString(this.font, "ReduceMemory Settings", this.width / 2, 5, 0xFFFFFF);
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
                                     }
