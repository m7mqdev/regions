package dev.m7mqd.regions.model;

import dev.m7mqd.regions.flag.Flag;
import dev.m7mqd.regions.flag.FlagState;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListSet;


public class Region {
    private String name;
    private Location min;
    private Location max;
    private final Map<UUID, Boolean> whitelisted;
    private final Map<Flag, FlagState> flags;

    public Region(String name, Set<UUID> whitelisted, Map<Flag, FlagState> flags, Location min, Location max) {
        this.name = name;
        this.whitelisted = new ConcurrentHashMap<>(whitelisted.size());
        whitelisted.forEach((player) -> Region.this.whitelisted.put(player, true));
        this.flags = new ConcurrentHashMap<>(flags);
        this.min = min;
        this.max = max;
    }

    public String getName() {
        return name;
    }

    public Region setMin(Location min) {
        this.min = min;
        return this;
    }

    public Region setMax(Location max) {
        this.max = max;
        return this;
    }

    public Location getMin() {
        return min;
    }

    public Location getMax() {
        return max;
    }

    public boolean contains(Location location) {
        if (min == null || max == null || location == null || !min.getWorld().equals(location.getWorld())) {
            return false;
        }
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return x >= Math.min(min.getX(), max.getX()) && x <= Math.max(min.getX(), max.getX()) &&
                y >= Math.min(min.getY(), max.getY()) && y <= Math.max(min.getY(), max.getY()) &&
                z >= Math.min(min.getZ(), max.getZ()) && z <= Math.max(min.getZ(), max.getZ());
    }

    @Nullable
    public FlagState getFlagState(Flag flag) {
        return this.flags.get(flag);
    }

    public void setFlagState(Flag flag, FlagState state) {
        this.flags.put(flag, state);
    }

    public void removeFlagState(Flag flag) {
        this.flags.remove(flag);
    }

    public Set<UUID> getWhitelisted() {
        return Collections.unmodifiableSet(whitelisted.keySet());
    }

    public Map<Flag, FlagState> getFlags() {
        return Collections.unmodifiableMap(flags);
    }

    public void addWhitelisted(UUID uuid) {
        this.whitelisted.put(uuid, true);
    }

    public void removeWhitelisted(UUID uuid) {
        this.whitelisted.remove(uuid);
    }

    protected void setName(String input) {
        this.name = input;
    }
}