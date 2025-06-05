package dev.m7mqd.regions.flag;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FlagService {
    private final Supplier<Flag> SUPPLIER = () -> null;

    private final Map<Flag.Key, Supplier<Flag>> flags = new ConcurrentHashMap<>();



    public void addResolver(Flag.Key key, Supplier<Flag> flagSupplier){
        this.flags.put(key, flagSupplier);
    }
    @Nullable
    public Flag resolve(Flag.Key key){
        return this.flags.getOrDefault(key, SUPPLIER).get();
    }

    public Collection<Flag> flags() {
        return this.flags.values().stream().map(Supplier::get).collect(Collectors.toSet());
    }
}
