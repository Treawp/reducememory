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
    private int tickCounter = 0;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        LOGGER.info("[ReduceMemory] v2.0.0 Active. RAM: {}MB",
            Runtime.getRuntime().maxMemory() / (1024 * 1024));

        // GC di background thread - TIDAK freeze game
        gcScheduler.scheduleAtFixedRate(() -> {
            if (!config.enableAutoGC) return;
            Runtime r = Runtime.getRuntime();
            long max = r.maxMemory();
            long used = r.totalMemory() - r.freeMemory();
            double ratio = (double) used / max;
            double threshold = config.gcThresholdPercent / 100.0;

            if (ratio >= threshold && gcRunning.compareAndSet(false, true)) {
                try {
                    long usedMB = used / (1024 * 1024);
                    long maxMB = max / (1024 * 1024);
                    LOGGER.info("[ReduceMemory] GC start - {}MB/{}MB ({}%)",
                        usedMB, maxMB, (int)(ratio * 100));
                    System.gc();
                    Thread.sleep(500); // cooldown biar gak spam GC
                    long afterMB = (r.totalMemory() - r.freeMemory()) / (1024 * 1024);
                    LOGGER.info("[ReduceMemory] GC done - {}MB -> {}MB", usedMB, afterMB);
                } catch (InterruptedException ignored) {
                } finally {
                    gcRunning.set(false);
                }
            }
        }, 5, config.gcIntervalSeconds, TimeUnit.SECONDS);

        // Low memory emergency GC
        gcScheduler.scheduleAtFixedRate(() -> {
            if (!config.gcOnLowMemory) return;
            Runtime r = Runtime.getRuntime();
            double ratio = (double)(r.totalMemory() - r.freeMemory()) / r.maxMemory() * 100;
            if (ratio >= config.lowMemoryThreshold && gcRunning.compareAndSet(false, true)) {
                try {
                    LOGGER.info("[ReduceMemory] Emergency GC! {}%", (int)ratio);
                    System.gc();
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                } finally {
                    gcRunning.set(false);
                }
            }
        }, 10, 5, TimeUnit.SECONDS);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Tick counter buat logging doang, GC udah di background
            tickCounter++;
            if (tickCounter >= 200) {
                tickCounter = 0;
                if (config.reduceCpuLoad) {
                    try { Thread.sleep(2); } catch (InterruptedException ignored) {}
                }
            }
        });
    }

    public static ModConfig getConfig() {
        return config;
    }
                                                              }
