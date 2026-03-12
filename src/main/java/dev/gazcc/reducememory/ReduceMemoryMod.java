package dev.gazcc.reducememory;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReduceMemoryMod implements ClientModInitializer {

    public static final String MOD_ID = "reducememory";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static ModConfig config;
    private int tickCounter = 0;
    private int gcCounter = 0;
    private int bgCounter = 0;
    private int chunkCounter = 0;
    private int originalFps = -1;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        LOGGER.info("[ReduceMemory] v2.0.0 Active.");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            // === AUTO GC ===
            if (config.enableAutoGC) {
                tickCounter++;
                if (tickCounter >= config.gcIntervalSeconds * 20) {
                    tickCounter = 0;
                    Runtime r = Runtime.getRuntime();
                    double ratio = (double)(r.totalMemory() - r.freeMemory()) / r.maxMemory();
                    if (ratio >= config.gcThresholdPercent / 100.0) {
                        LOGGER.info("[ReduceMemory] GC - {}%", (int)(ratio * 100));
                        System.gc();
                    }
                }
            }

            // === GC ON LOW MEMORY ===
            if (config.gcOnLowMemory) {
                gcCounter++;
                if (gcCounter >= 100) {
                    gcCounter = 0;
                    Runtime r = Runtime.getRuntime();
                    double ratio = (double)(r.totalMemory() - r.freeMemory()) / r.maxMemory() * 100;
                    if (ratio >= config.lowMemoryThreshold) {
                        System.gc();
                        System.gc();
                    }
                }
            }

            // === REDUCE CPU LOAD ===
            if (config.reduceCpuLoad) {
                bgCounter++;
                if (bgCounter >= 5) {
                    bgCounter = 0;
                    try { Thread.sleep(10); } catch (InterruptedException ignored) {}
                }
            }

            // === REDUCE GPU LOAD - limit FPS saat background ===
            if (config.reduceGpuLoad && client.options != null) {
                boolean focused = client.isWindowFocused();
                if (!focused) {
                    if (originalFps == -1) {
                        originalFps = client.options.getMaxFps();
                    }
                    if (client.options.getMaxFps() > config.maxFpsOnBackground) {
                        client.options.maxFps.setValue(config.maxFpsOnBackground);
                    }
                } else {
                    if (originalFps != -1) {
                        client.options.maxFps.setValue(originalFps);
                        originalFps = -1;
                    }
                }
            }

            // === IMPROVE CHUNK LOADING ===
            if (config.improveChunkLoading) {
                chunkCounter++;
                if (chunkCounter >= config.maxChunkUpdatesPerTick) {
                    chunkCounter = 0;
                    Runtime r = Runtime.getRuntime();
                    if ((double)(r.totalMemory() - r.freeMemory()) / r.maxMemory() > 0.75) {
                        System.gc();
                    }
                }
            }

        });
    }

    public static ModConfig getConfig() {
        return config;
    }
                            }
