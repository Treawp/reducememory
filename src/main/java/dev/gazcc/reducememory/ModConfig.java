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

    // Anti lag features
    public boolean improveChunkLoading = true;

    @ConfigEntry.BoundedDiscrete(min = 2, max = 16)
    public int maxChunkUpdatesPerTick = 4;

    public boolean reduceParticles = true;

    public boolean gcOnLowMemory = true;

    @ConfigEntry.BoundedDiscrete(min = 10, max = 90)
    public int lowMemoryThreshold = 85;
}
