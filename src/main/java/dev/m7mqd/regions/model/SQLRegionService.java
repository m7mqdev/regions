package dev.m7mqd.regions.model;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.m7mqd.regions.flag.Flag;
import dev.m7mqd.regions.flag.FlagService;
import dev.m7mqd.regions.flag.FlagState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;

public class SQLRegionService extends RegionService {
    public static final String PROPERTIES_FILE = "database.properties";
    private HikariDataSource dataSource;
    private final Plugin plugin;
    private final FlagService flagService;

    public SQLRegionService(final Plugin plugin, FlagService flagService) {
        this.plugin = plugin;
        this.flagService = flagService;
    }

    @Override
    public void open() {
        Path propertiesPath = plugin.getDataFolder().toPath().resolve(PROPERTIES_FILE);
        if (!Files.exists(plugin.getDataFolder().toPath())) plugin.getDataFolder().mkdirs();
        if (!Files.exists(propertiesPath)) plugin.saveResource(PROPERTIES_FILE, false);

        try {
            HikariConfig config = new HikariConfig(propertiesPath.toString());
            this.dataSource = new HikariDataSource(config);
            createDatabaseSchema();
        } catch (Exception e) {
            throw new RuntimeException("Failed to open SQLRegionService", e);
        }
    }

    @Override
    public void close() {
        persist();
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    private void createDatabaseSchema() {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.addBatch(Queries.CREATE_REGIONS_TABLE);
            stmt.addBatch(Queries.CREATE_FLAGS_TABLE);
            stmt.addBatch(Queries.CREATE_WHITELIST_TABLE);
            stmt.executeBatch();
        } catch (Exception e) {
            throw new RuntimeException("Error creating DB schema", e);
        }
    }

    @Override
    public void load() {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement selectRegions = conn.prepareStatement(Queries.SELECT_ALL_REGIONS);
                 PreparedStatement selectFlags = conn.prepareStatement(Queries.SELECT_FLAGS_BY_REGION);
                 PreparedStatement selectWhitelist = conn.prepareStatement(Queries.SELECT_WHITELIST_BY_REGION)) {

                try (ResultSet rs = selectRegions.executeQuery()) {
                    while (rs.next()) {
                        String name = rs.getString("name");
                        String world = rs.getString("world");
                        Location min = new Location(Bukkit.getWorld(world), rs.getDouble("min_x"), rs.getDouble("min_y"), rs.getDouble("min_z"));
                        Location max = new Location(Bukkit.getWorld(world), rs.getDouble("max_x"), rs.getDouble("max_y"), rs.getDouble("max_z"));

                        Set<UUID> whitelisted = new HashSet<>();
                        selectWhitelist.setString(1, name);
                        try (ResultSet wl = selectWhitelist.executeQuery()) {
                            while (wl.next()) whitelisted.add(UUID.fromString(wl.getString("uuid")));
                        }

                        Map<Flag, FlagState> flags = new HashMap<>();
                        selectFlags.setString(1, name);
                        try (ResultSet fl = selectFlags.executeQuery()) {
                            while (fl.next()) {
                                flags.put(flagService.resolve(Flag.Key.fromString(fl.getString("flag"))), FlagState.valueOf(fl.getString("state")));
                            }
                        }

                        Region region = new Region(name, whitelisted, flags, min, max);
                        regions.put(name, region);
                    }
                }

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load regions", e);
        }
    }

    @Override
    public void persist() {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement insertRegion = conn.prepareStatement(Queries.INSERT_REGION);
                 PreparedStatement insertFlag = conn.prepareStatement(Queries.INSERT_FLAG);
                 PreparedStatement insertWhitelist = conn.prepareStatement(Queries.INSERT_WHITELIST);
                 Statement truncate = conn.createStatement()) {

                truncate.execute("DELETE FROM whitelisted_players");
                truncate.execute("DELETE FROM flags");
                truncate.execute("DELETE FROM regions");

                for (Region region : regions.values()) {
                    Location min = region.getMin();
                    Location max = region.getMax();

                    if (!min.getWorld().getName().equals(max.getWorld().getName())) {
                        throw new IllegalStateException("min and max locations must be in the same world");
                    }

                    insertRegion.setString(1, region.getName());
                    insertRegion.setString(2, min.getWorld().getName());
                    insertRegion.setDouble(3, min.getX());
                    insertRegion.setDouble(4, min.getY());
                    insertRegion.setDouble(5, min.getZ());
                    insertRegion.setDouble(6, max.getX());
                    insertRegion.setDouble(7, max.getY());
                    insertRegion.setDouble(8, max.getZ());
                    insertRegion.addBatch();

                    for (Map.Entry<Flag, FlagState> entry : region.getFlags().entrySet()) {
                        insertFlag.setString(1, region.getName());
                        insertFlag.setString(2, entry.getKey().toKey().toString());
                        insertFlag.setString(3, entry.getValue().name());
                        insertFlag.addBatch();
                    }

                    for (UUID uuid : region.getWhitelisted()) {
                        insertWhitelist.setString(1, region.getName());
                        insertWhitelist.setString(2, uuid.toString());
                        insertWhitelist.addBatch();
                    }
                }
                insertRegion.executeBatch();
                insertFlag.executeBatch();
                insertWhitelist.executeBatch();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to persist regions", e);
        }
    }
}
