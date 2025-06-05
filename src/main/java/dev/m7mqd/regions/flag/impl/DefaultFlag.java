package dev.m7mqd.regions.flag.impl;

import dev.m7mqd.regions.flag.Flag;

public enum DefaultFlag implements Flag {
    BLOCK_BREAK,
    BLOCK_PLACE,
    INTERACT,
    ENTITY_DAMAGE;

    @Override
    public Key toKey() {
        return Flag.Key.fromString(this.name().toLowerCase());
    }
}
