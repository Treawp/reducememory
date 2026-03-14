package dev.gazcc.reducememory;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ReduceMemoryConfigScreen extends Screen {

    private final Screen parent;
    private int activeTab = 0;

    public ReduceMemoryConfigScreen(Screen parent) {
        super(Component.literal("ReduceMemory Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        int cx = width / 2 - 100;

        this.addRenderableWidget(Button.builder(
            Component.literal("Memory"),
            btn -> { activeTab = 0; clearWidgets(); init(); }
        ).bounds(10, 20, 65, 20).build());

        this.addRenderableWidget(Button.builder(
            Component.literal("CPU"),
            btn -> { activeTab = 1; clearWidgets(); init(); }
        ).bounds(80, 20, 65, 20).build());

        this.addRenderableWidget(Button.builder(
            Component.literal("GPU"),
            btn -> { activeTab = 2; clearWidgets(); init(); }
        ).bounds(150, 20, 65, 20).build());

        ModConfig cfg = ReduceMemoryMod.getConfig();

        if (activeTab == 0) {
            addBtn("Auto GC: " + on(cfg.enableAutoGC), cx, 55, btn -> {
                cfg.enableAutoGC = !cfg.enableAutoGC; save();
                btn.setMessage(Component.literal("Auto GC: " + on(cfg.enableAutoGC)));
            });
            addBtn("GC on Disconnect: " + on(cfg.gcOnDisconnect), cx, 80, btn -> {
                cfg.gcOnDisconnect = !cfg.gcOnDisconnect; save();
                btn.setMessage(Component.literal("GC on Disconnect: " + on(cfg.gcOnDisconnect)));
            });
            addBtn("GC on Low Memory: " + on(cfg.gcOnLowMemory), cx, 105, btn -> {
                cfg.gcOnLowMemory = !cfg.gcOnLowMemory; save();
                btn.setMessage(Component.literal("GC on Low Memory: " + on(cfg.gcOnLowMemory)));
            });
            addBtn("Memory HUD: " + on(cfg.showMemoryHud), cx, 130, btn -> {
                cfg.showMemoryHud = !cfg.showMemoryHud; save();
                btn.setMessage(Component.literal("Memory HUD: " + on(cfg.showMemoryHud)));
            });
            addBtn("Memory Bar: " + on(cfg.showMemoryBar), cx, 155, btn -> {
                cfg.showMemoryBar = !cfg.showMemoryBar; save();
                btn.setMessage(Component.literal("Memory Bar: " + on(cfg.showMemoryBar)));
            });
            addBtn("Chunk Unload: " + on(cfg.chunkUnloadBoost), cx, 180, btn -> {
                cfg.chunkUnloadBoost = !cfg.chunkUnloadBoost; save();
                btn.setMessage(Component.literal("Chunk Unload: " + on(cfg.chunkUnloadBoost)));
            });
            addBtn("Preset: " + cfg.memoryPreset.name(), cx, 205, btn -> {
                ModConfig.MemoryPreset[] vals = ModConfig.MemoryPreset.values();
                cfg.memoryPreset = vals[(cfg.memoryPreset.ordinal() + 1) % vals.length];
                cfg.applyPreset(); save();
                btn.setMessage(Component.literal("Preset: " + cfg.memoryPreset.name()));
            });
        } else if (activeTab == 1) {
            addBtn("Reduce CPU: " + on(cfg.reduceCpuLoad), cx, 55, btn -> {
                cfg.reduceCpuLoad = !cfg.reduceCpuLoad; save();
                btn.setMessage(Component.literal("Reduce CPU: " + on(cfg.reduceCpuLoad)));
            });
            addBtn("Chunk Loading: " + on(cfg.improveChunkLoading), cx, 80, btn -> {
                cfg.improveChunkLoading = !cfg.improveChunkLoading; save();
                btn.setMessage(Component.literal("Chunk Loading: " + on(cfg.improveChunkLoading)));
            });
            addBtn("Entity Culling: " + on(cfg.entityCulling), cx, 105, btn -> {
                cfg.entityCulling = !cfg.entityCulling; save();
                btn.setMessage(Component.literal("Entity Culling: " + on(cfg.entityCulling)));
            });
            addBtn("Reduce Particles: " + on(cfg.reduceParticles), cx, 130, btn -> {
                cfg.reduceParticles = !cfg.reduceParticles; save();
                btn.setMessage(Component.literal("Reduce Particles: " + on(cfg.reduceParticles)));
            });
        } else {
            addBtn("Reduce GPU: " + on(cfg.reduceGpuLoad), cx, 55, btn -> {
                cfg.reduceGpuLoad = !cfg.reduceGpuLoad; save();
                btn.setMessage(Component.literal("Reduce GPU: " + on(cfg.reduceGpuLoad)));
            });
            addBtn("Max BG FPS: " + cfg.maxFpsOnBackground, cx, 80, btn -> {
                cfg.maxFpsOnBackground = cfg.maxFpsOnBackground >= 60 ? 1 : cfg.maxFpsOnBackground + 5;
                save();
                btn.setMessage(Component.literal("Max BG FPS: " + cfg.maxFpsOnBackground));
            });
        }

        this.addRenderableWidget(Button.builder(
            Component.literal("Back"),
            btn -> this.minecraft.setScreen(parent)
        ).bounds(width / 2 - 50, height - 30, 100, 20).build());
    }

    private void addBtn(String label, int x, int y, Button.OnPress action) {
        this.addRenderableWidget(Button.builder(
            Component.literal(label), action
        ).bounds(x, y, 200, 20).build());
    }

    private String on(boolean val) {
        return val ? "ON" : "OFF";
    }

    private void save() {
        AutoConfig.getConfigHolder(ModConfig.class).save();
    }

    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredString(this.font, "ReduceMemory Settings", this.width / 2, 5, 0xFFFFFF);
        String[] descs = {
            "Memory: GC settings and HUD",
            "CPU: Load reduction and chunks",
            "GPU: Frame rate settings"
        };
        ctx.drawString(this.font, descs[activeTab], 10, height - 45, 0xAAAAAA);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
                 }
