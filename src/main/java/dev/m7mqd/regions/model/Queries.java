package dev.m7mqd.regions.model;

public class Queries {

    public static final String CREATE_REGIONS_TABLE = """
        CREATE TABLE IF NOT EXISTS regions (
            name VARCHAR(64) PRIMARY KEY,
            world VARCHAR(64),
            min_x DOUBLE,
            min_y DOUBLE,
            min_z DOUBLE,
            max_x DOUBLE,
            max_y DOUBLE,
            max_z DOUBLE
        );
    """;

    public static final String CREATE_FLAGS_TABLE = """
        CREATE TABLE IF NOT EXISTS flags (
            region_name VARCHAR(64),
            flag VARCHAR(64),
            state VARCHAR(64),
            PRIMARY KEY (region_name, flag),
            FOREIGN KEY (region_name) REFERENCES regions(name) ON DELETE CASCADE
        );
    """;

    public static final String CREATE_WHITELIST_TABLE = """
        CREATE TABLE IF NOT EXISTS whitelisted_players (
            region_name VARCHAR(64),
            uuid VARCHAR(36),
            PRIMARY KEY (region_name, uuid),
            FOREIGN KEY (region_name) REFERENCES regions(name) ON DELETE CASCADE
        );
    """;

    public static final String INSERT_REGION = """
        INSERT INTO regions (
            name, world,
            min_x, min_y, min_z,
            max_x, max_y, max_z
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?);
    """;

    public static final String INSERT_FLAG = """
        INSERT INTO flags (
            region_name, flag, state
        ) VALUES (?, ?, ?);
    """;

    public static final String INSERT_WHITELIST = """
        INSERT INTO whitelisted_players (
            region_name, uuid
        ) VALUES (?, ?);
    """;

    public static final String SELECT_ALL_REGIONS = """
        SELECT * FROM regions;
    """;

    public static final String SELECT_FLAGS_BY_REGION = """
        SELECT * FROM flags WHERE region_name = ?;
    """;

    public static final String SELECT_WHITELIST_BY_REGION = """
        SELECT * FROM whitelisted_players WHERE region_name = ?;
    """;

    private Queries(){
        throw new IllegalStateException("Utility class construction is forbidden.");
    }
}
