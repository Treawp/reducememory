package dev.gazcc.reducememory;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "reducememory")
public class ModConfig implements ConfigData {

    @Comment("Aktifkan auto GC")
    public boolean enableAutoGC = true;

    @Comment("Interval GC dalam detik (1-60)")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 60)
    public int gcIntervalSeconds = 10;

    @Comment("Threshold memory untuk trigger GC (50-95%)")
    @ConfigEntry.BoundedDiscrete(min = 50, max = 95)
    public int gcThresholdPercent = 80;

    @Comment("Aktifkan GC saat disconnect world")
    public boolean gcOnDisconnect = true;

    @Comment("Tampilkan info memory di chat saat GC")
    public boolean showGCNotification = false;
}
