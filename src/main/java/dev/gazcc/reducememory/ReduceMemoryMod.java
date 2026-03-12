package dev.gazcc.reducememory;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReduceMemoryMod implements ClientModInitializer {

    public static final String MOD_ID = "reducememory";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static ModConfig config;
    private int tickCounter = 0;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        LOGGER.info("[ReduceMemory] v2.0.0 Active.");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!config.enableAutoGC) return;

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
                    LOGGER.info("[ReduceMemory] GC triggered - Used: {}MB / {}MB ({}%)",
                        usedMB, maxMB, (int)(ratio * 100));
                    System.gc();

                    if (config.showGCNotification) {
                        ClientPlayerEntity player = client.player;
                        if (player != null) {
                            player.sendMessage(
                                net.minecraft.text.Text.literal(
                                    "§a[ReduceMemory] §fGC selesai - Memory: §e" + usedMB + "MB§f/§c" + maxMB + "MB"
                                ), true
                            );
                        }
                    }
                }
            }
        });
    }

    public static ModConfig getConfig() {
        return config;
    }
                    }
