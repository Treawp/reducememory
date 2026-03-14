package dev.gazcc.reducememory;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReduceMemoryMod implements ClientModInitializer {

    public static final String MOD_ID = "reducememory";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static ModConfig config;

    private final ScheduledExecutorService gcScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "ReduceMemory-GC");
        t.setDaemon(true);
        t.setPriority(Thread.MIN_PRIORITY);
        return t;
    });

    private final AtomicBoolean gcRunning = new AtomicBoolean(false);

    @Override
    public void onInitializeClient() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        config.applyPreset();
        LOGGER.info("[ReduceMemory] v2.0.0 Active. RAM: {}MB preset: {}",
            Runtime.getRuntime().maxMemory() / (1024 * 1024),
            config.memoryPreset);

        // Auto GC
        gcScheduler.scheduleAtFixedRate(() -> {
            if (!config.enableAutoGC) return;
            triggerGCIfNeeded(config.gcThresholdPercent / 100.0, false);
        }, 5, config.gcIntervalSeconds, TimeUnit.SECONDS);

        // Emergency GC
        gcScheduler.scheduleAtFixedRate(() -> {
            if (!config.gcOnLowMemory) return;
            triggerGCIfNeeded(config.lowMemoryThreshold / 100.0, true);
        }, 10, 5, TimeUnit.SECONDS);

        // Chunk Unload Boost
        gcScheduler.scheduleAtFixedRate(() -> {
            if (!config.chunkUnloadBoost) return;
            Runtime r = Runtime.getRuntime();
            double ratio = (double)(r.totalMemory() - r.freeMemory()) / r.maxMemory() * 100;
            if (ratio >= config.chunkUnloadThreshold) {
                LOGGER.info("[ReduceMemory] Chunk unload boost triggered {}%", (int)ratio);
                System.gc();
            }
        }, 15, 8, TimeUnit.SECONDS);

        // Main tick - ZERO blocking
        ClientTickEvents.END_CLIENT_TICK.register(client -> {});
    }

    private void triggerGCIfNeeded(double threshold, boolean emergency) {
        Runtime r = Runtime.getRuntime();
        long max = r.maxMemory();
        long used = r.totalMemory() - r.freeMemory();
        double ratio = (double) used / max;

        if (ratio >= threshold && gcRunning.compareAndSet(false, true)) {
            try {
                long usedMB = used / (1024 * 1024);
                long maxMB = max / (1024 * 1024);
                LOGGER.info("[ReduceMemory] {} GC - {}MB/{}MB ({}%)",
                    emergency ? "Emergency" : "Auto",
                    usedMB, maxMB, (int)(ratio * 100));
                System.gc();
                Thread.sleep(500);
                long afterMB = (r.totalMemory() - r.freeMemory()) / (1024 * 1024);
                LOGGER.info("[ReduceMemory] GC done - {}MB -> {}MB", usedMB, afterMB);
            } catch (InterruptedException ignored) {
            } finally {
                gcRunning.set(false);
            }
        }
    }

    public static ModConfig getConfig() {
        return config;
    }
                }
