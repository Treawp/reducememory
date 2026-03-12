package dev.gazcc.reducememory;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReduceMemoryMod implements ClientModInitializer {

    public static final String MOD_ID = "reducememory";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static ModConfig config;
    private int tickCounter = 0;
    private int fpsLimitCounter = 0;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        LOGGER.info("[ReduceMemory] v2.0.0 Active.");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // === AUTO GC ===
            if (config.enableAutoGC) {
                tickCounter++;
                int intervalTicks = config.gcIntervalSeconds * 20;
                if (tickCounter >= intervalTicks) {
                    tickCounter = 0;
                    Runtime r = Runtime.getRuntime();
                    long max = r.maxMemory();
                    long used = r.totalMemory() - r.freeMemory();
                    double ratio = (double) used / max;
                    double threshold = config.gcThresholdPercent / 100.0;
                    if (ratio >= threshold) {
                        long usedMB = used / (1024 * 1024);
                        long maxMB = max / (1024 * 1024);
                        LOGGER.info("[ReduceMemory] GC triggered - {}MB/{}MB ({}%)",
                            usedMB, maxMB, (int)(ratio * 100));
                        System.gc();
                    }
                }
            }

            // === REDUCE CPU LOAD - limit FPS saat game di background ===
            if (config.reduceCpuLoad && client.isWindowFocused() == false) {
                fpsLimitCounter++;
                if (fpsLimitCounter >= 20) {
                    fpsLimitCounter = 0;
                    try {
                        // Sleep 50ms setiap 20 tick saat unfocused = kurangi CPU usage
                        Thread.sleep(50);
                    } catch (InterruptedException ignored) {}
                }
            } else {
                fpsLimitCounter = 0;
            }

            // === REDUCE GPU LOAD - paksa limit FPS saat background ===
            if (config.reduceGpuLoad && client.isWindowFocused() == false) {
                if (client.options != null) {
                    // Simpan FPS limit asli dan paksa rendah saat background
                    client.options.maxFps().setValue(config.maxFpsOnBackground);
                }
            }
        });
    }

    public static ModConfig getConfig() {
        return config;
    }
                    }
