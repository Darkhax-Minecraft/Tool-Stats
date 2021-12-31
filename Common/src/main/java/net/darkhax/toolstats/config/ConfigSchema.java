package net.darkhax.toolstats.config;

import net.darkhax.toolstats.Constants;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpyglassItem;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigSchema {

    public boolean showEnchantability = true;
    public boolean alwaysShowEnchantability = false;

    public boolean showRepairCost = true;
    public boolean alwaysShowRepairCost = false;

    public boolean showHarvestLevel = true;
    public boolean showEfficiency = true;

    public boolean showDurability = true;
    public boolean alwaysShowDurability = false;

    public static ConfigSchema load(File configFile) {

        final ConfigSchema defaultConfig = new ConfigSchema();

        // Attempt to load existing config file
        if (configFile.exists()) {

            try (FileReader reader = new FileReader(configFile)) {

                return Constants.GSON.fromJson(reader, ConfigSchema.class);
            }

            catch (IOException e) {

                Constants.LOG.error("Could not read config file {}. Defaults will be used.", configFile.getAbsolutePath());
                Constants.LOG.catching(e);
            }
        }

        else {

            Constants.LOG.info("Creating a new config file at {}.", configFile.getAbsolutePath());
            configFile.getParentFile().mkdirs();
        }

        try (FileWriter writer = new FileWriter(configFile)) {

            Constants.GSON.toJson(defaultConfig, writer);
            Constants.LOG.info("Default configuration file generated at {}.", configFile.getAbsolutePath());
        }

        catch (IOException e) {

            Constants.LOG.error("Could not write config file '{}'!", configFile.getAbsolutePath());
            Constants.LOG.catching(e);
        }


        return defaultConfig;
    }
}