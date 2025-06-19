package dev.m7mqd.regions.model;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class RegionService {
    protected final Map<String, Region> regions = new ConcurrentHashMap<>();


    public abstract void load();
    public abstract void persist();
    public abstract void open();
    public abstract void close();

    public void addRegion(Region region) {
        if (region == null || region.getName() == null)
            throw new IllegalArgumentException("Region or region name cannot be null");
        regions.put(region.getName(), region);
    }

    public boolean removeRegion(String name) {
        return regions.remove(name) != null;
    }

    @Nullable
    public Region getRegion(String name) {
        return regions.get(name);
    }
    @Nullable
    public Region getRegion(Location location) {
        for (Region region : regions.values()) {
            if (region.contains(location)) return region;
        }
        return null;
    }
    public void updateName(Region region, String name){
        this.regions.remove(region.getName());
        this.regions.put(name, region);
        region.setName(name);
    }
    public Map<String, Region> getRegions() {
        return Collections.unmodifiableMap(regions);
    }
}
