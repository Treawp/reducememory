package dev.gazcc.reducememory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReduceMemoryMod implements ClientModInitializer {
    public static final String MOD_ID = "reducememory";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final int GC_INTERVAL = 300;
    private static final double GC_THRESHOLD = 0.80;
    private int tick = 0;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[ReduceMemory] Active.");
        ClientTickEvents.END_CLIENT_TICK.register(c -> {
            if (++tick >= GC_INTERVAL) {
                tick = 0;
                Runtime r = Runtime.getRuntime();
                double ratio = (double)(r.totalMemory() - r.freeMemory()) / r.maxMemory();
                if (ratio >= GC_THRESHOLD) System.gc();
            }
        });
    }

    public static double getEntityMultiplier() {
        Runtime r = Runtime.getRuntime();
        double ratio = (double)(r.totalMemory() - r.freeMemory()) / r.maxMemory();
        if (ratio > 0.85) return 0.35;
        if (ratio > 0.70) return 0.55;
        if (ratio > 0.55) return 0.75;
        return 1.0;
    }
            }
