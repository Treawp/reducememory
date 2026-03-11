ReduceMemoryMod.java
package dev.gazcc.reducememory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReduceMemoryMod implements ClientModInitializer {

    public static final String MOD_ID = "reducememory";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final int GC_INTERVAL_TICKS = 300;
    private static final double GC_THRESHOLD = 0.80;
    private int tickCounter = 0;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[ReduceMemory] Mod initialized. Memory optimization active.");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            tickCounter++;
            if (tickCounter >= GC_INTERVAL_TICKS) {
                tickCounter = 0;
                Runtime runtime = Runtime.getRuntime();
                long maxMemory = runtime.maxMemory();
                long usedMemory = runtime.totalMemory() - runtime.freeMemory();
                double usageRatio = (double) usedMemory / maxMemory;
                if (usageRatio >= GC_THRESHOLD) {
                    LOGGER.info("[ReduceMemory] Memory {}% - triggering GC", (int)(usageRatio * 100));
                    System.gc();
                }
            }
        });
    }

    public static double getDynamicEntityDistanceMultiplier() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        double usageRatio = (double) usedMemory / maxMemory;
        if (usageRatio > 0.85) return 0.35;
        if (usageRatio > 0.70) return 0.55;
        if (usageRatio > 0.55) return 0.75;
        return 1.0;
    }
                  }
