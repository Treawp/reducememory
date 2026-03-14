package dev.gazcc.reducememory;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "reducememory")
public class ModConfig implements ConfigData {

    public boolean enableAutoGC = true;

    @ConfigEntry.BoundedDiscrete(min = 1, max = 60)
    public int gcIntervalSeconds = 10;

    @ConfigEntry.BoundedDiscrete(min = 50, max = 95)
    public int gcThresholdPercent = 80;

    public boolean gcOnDisconnect = true;
    public boolean showGCNotification = false;
    public boolean reduceCpuLoad = true;
    public boolean reduceGpuLoad = true;

    @ConfigEntry.BoundedDiscrete(min = 1, max = 60)
    public int maxFpsOnBackground = 10;

    public boolean entityCulling = true;
    public boolean improveChunkLoading = true;

    @ConfigEntry.BoundedDiscrete(min = 2, max = 16)
    public int maxChunkUpdatesPerTick = 4;

    public boolean reduceParticles = true;
    public boolean gcOnLowMemory = true;

    @ConfigEntry.BoundedDiscrete(min = 10, max = 90)
    public int lowMemoryThreshold = 85;

    public boolean showMemoryHud = true;
    public boolean showMemoryBar = true;
    public boolean chunkUnloadBoost = true;

    @ConfigEntry.BoundedDiscrete(min = 50, max = 95)
    public int chunkUnloadThreshold = 75;

    public MemoryPreset memoryPreset = MemoryPreset.MEDIUM;

    // HUD position
    public int hudX = 2;
    public int hudY = 2;

    public enum MemoryPreset {
        LOW, MEDIUM, HIGH
    }

    public void applyPreset() {
        switch (memoryPreset) {
            case LOW:
                gcIntervalSeconds = 5;
                gcThresholdPercent = 60;
                lowMemoryThreshold = 70;
                chunkUnloadThreshold = 60;
                maxChunkUpdatesPerTick = 2;
                reduceCpuLoad = true;
                reduceGpuLoad = true;
                chunkUnloadBoost = true;
                break;
            case HIGH:
                gcIntervalSeconds = 30;
                gcThresholdPercent = 90;
                lowMemoryThreshold = 90;
                chunkUnloadThreshold = 88;
                maxChunkUpdatesPerTick = 8;
                reduceCpuLoad = false;
                reduceGpuLoad = false;
                chunkUnloadBoost = false;
                break;
            default:
                gcIntervalSeconds = 10;
                gcThresholdPercent = 80;
                lowMemoryThreshold = 85;
                chunkUnloadThreshold = 75;
                maxChunkUpdatesPerTick = 4;
                reduceCpuLoad = true;
                reduceGpuLoad = true;
                chunkUnloadBoost = true;
                break;
        }
    }
    }
