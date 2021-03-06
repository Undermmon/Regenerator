/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.draksterau.Regenerator.Handlers;

import com.draksterau.Regenerator.RegeneratorPlugin;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author draks
 */
public final class RConfig extends RObject {

    // Configuration Version?
    public String configVersion = plugin.getDescription().getVersion();

    public RegeneratorPlugin getPlugin() {
        return plugin;
    }
    
    
    // Defines the Language.
    
    public String language = "ENGLISH";
    
    // Enables fake player detection.
    
    public boolean enableUnknownProtectionDetection = false;
    
    // Enables regeneration of chunks on next chunk load, if they arent protected.
    
    public boolean enableRegenerationNextChunkLoad = true;
    
    // UUID for fake player requests.
    
    public UUID fakePlayerUUID = UUID.randomUUID();
    
    // Should Regenerator cache chunks into the database when they are loaded?
    public boolean cacheChunksOnLoad = false;
    
    // Interval between task parses on worlds.
    public long parseInterval = 300;
    
    // How much of the interval in percent can be used for processing?
    public double percentIntervalRuntime = 0.5;
    
    // Maximum chunks per parse
    public double numChunksPerParse = 25;
    
    // Whether or not new worlds that are loaded should have manual regen enabled by default
    public boolean defaultManualRegen = false;
    
    // Whether or not new worlds that are loaded should have auto regen enabled by default
    public boolean defaultAutoRegen = false;
    
    // Minimum TPS for regenerator to continue running parses.
    public int minTpsRegen = 15;
    
    // Should Regenerator run without grief prevention plugins enabled?
    public boolean noGriefRun = false;
    
    // Distance at which players will prevent a chunk regenerating (too close)
    public int distanceNearbyMinimum = 16;
    
    // Set this to true to allow loaded chunks to regenerate.
    public boolean targetLoadedChunks = false;
    // Set this to true to allow unloaded chunks to regenerate.
    public boolean targetUnloadedChunks = true;
    
    // Should this plugin respect that chunks are in use by players.
    public boolean regenerateChunksInUseByPlayers = false;
    
    // Should this plugin clear the chunk of all entities? This includes dropped items, villagers, zombies and all!
    public boolean clearRegeneratedChunksOfEntities = false;
    
    // Should this plugin not regenerate near WarpDrive ships?
    public boolean warpDriveCompatibility = false;
    
    // List of entity types that should be excluded from regenerating.
    public List<String> excludeEntityTypesFromRegeneration = new ArrayList<String>();
    
    public RConfig(RegeneratorPlugin plugin) {
        super(plugin);
        this.loadData();
        this.validateConfig();
    }

    public void validate() {
        if (minTpsRegen > 20 || minTpsRegen < 1) minTpsRegen = 15;
    }
    @Override
    void loadData() {
        // Attempt to load the config file.
        if (configFile == null) configFile = new File(plugin.getDataFolder() + "/global.yml");
        // Attempt to read the config in the config file.
        config = YamlConfiguration.loadConfiguration(configFile);
        // If the config file is null (due to the config file being invalid or not there) create a new one.
        if (configFile == null) configFile = new File(plugin.getDataFolder() + "/global.yml");
        // If the file doesnt exist, populate it from the template.
        if (!configFile.exists()) {
            config = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("global.yml")));
            saveData();
        }
        if (!config.isSet("configVersion")) {
            this.plugin.utils.throwMessage("new",String.format(this.plugin.lang.getForKey("messages.addingNewConfig"), "configVersion", this.configFile.getName()));
        } else {
            if (!config.getString("configVersion").equals(this.plugin.getDescription().getVersion())) {
                updateConfig();
            } else {
                this.configVersion = config.getString("configVersion");
            }
        }
        this.defaultManualRegen = config.getBoolean("defaultManualRegen");
        this.defaultAutoRegen = config.getBoolean("defaultAutoRegen");
        this.noGriefRun = config.getBoolean("noGriefRun");
        this.minTpsRegen = config.getInt("minTpsRegen");
        this.parseInterval = config.getInt("parseInterval");
        this.numChunksPerParse =config.getInt("numChunksPerParse");
        this.percentIntervalRuntime = config.getDouble("percentIntervalRuntime");
        if (!config.isSet("cacheChunksOnLoad")) {
            this.plugin.utils.throwMessage("new",String.format(this.plugin.lang.getForKey("messages.addingNewConfig"), "cacheChunksOnLoad", this.configFile.getName()));
        } else {
            this.cacheChunksOnLoad = config.getBoolean("cacheChunksOnLoad");
        }
        if (!config.isSet("distanceNearbyMinimum")) {
            this.plugin.utils.throwMessage("new",String.format(this.plugin.lang.getForKey("messages.addingNewConfig"), "distanceNearbyMinimum", this.configFile.getName()));
        } else {
            this.distanceNearbyMinimum = config.getInt("distanceNearbyMinimum");
        }
        if (!config.isSet("targetLoadedChunks")) {
            this.plugin.utils.throwMessage("new",String.format(this.plugin.lang.getForKey("messages.addingNewConfig"), "targetLoadedChunks", this.configFile.getName()));
        } else {
            this.targetLoadedChunks = config.getBoolean("targetLoadedChunks");
        }
        if (!config.isSet("fakePlayerUUID")) {
            this.plugin.utils.throwMessage("new",String.format(this.plugin.lang.getForKey("messages.addingNewConfig"), "fakePlayerUUID", this.configFile.getName()));
        } else {
            this.fakePlayerUUID = UUID.fromString(config.getString("fakePlayerUUID"));
        }
        if (!config.isSet("targetUnloadedChunks")) {
            this.plugin.utils.throwMessage("new",String.format(this.plugin.lang.getForKey("messages.addingNewConfig"), "targetUnloadedChunks", this.configFile.getName()));
        } else {
            this.targetUnloadedChunks = config.getBoolean("targetUnloadedChunks");
        }
        if (!config.isSet("regenerateChunksInUseByPlayers")) {
            this.plugin.utils.throwMessage("new",String.format(this.plugin.lang.getForKey("messages.addingNewConfig"), "regenerateChunksInUseByPlayers", this.configFile.getName()));
        } else {
            this.regenerateChunksInUseByPlayers = config.getBoolean("regenerateChunksInUseByPlayers");
        }
        if (!config.isSet("clearRegeneratedChunksOfEntities")) {
            this.plugin.utils.throwMessage("new",String.format(this.plugin.lang.getForKey("messages.addingNewConfig"), "clearRegeneratedChunksOfEntities", this.configFile.getName()));
        } else {
            this.clearRegeneratedChunksOfEntities = config.getBoolean("clearRegeneratedChunksOfEntities");
        }
        if (!config.isSet("enableUnknownProtectionDetection")) {
            this.plugin.utils.throwMessage("new",String.format(this.plugin.lang.getForKey("messages.addingNewConfig"), "enableUnknownProtectionDetection", this.configFile.getName()));
        } else {
            this.enableUnknownProtectionDetection = config.getBoolean("enableUnknownProtectionDetection");
        }
        if (!config.isSet("excludeEntityTypesFromRegeneration")) {
            this.plugin.utils.throwMessage("new",String.format(this.plugin.lang.getForKey("messages.addingNewConfig"), "excludeEntityTypesFromRegeneration", this.configFile.getName()));
        } else {
            this.excludeEntityTypesFromRegeneration = config.getStringList("excludeEntityTypesFromRegeneration");
        }
        if (!config.isSet("warpDriveCompatibility")) {
            this.plugin.utils.throwMessage("new",String.format(this.plugin.lang.getForKey("messages.addingNewConfig"), "warpDriveCompatibility", this.configFile.getName()));
        } else {
            this.warpDriveCompatibility = config.getBoolean("warpDriveCompatibility");
        }
        if (!config.isSet("enableRegenerationNextChunkLoad")) {
            this.plugin.utils.throwMessage("new",String.format(this.plugin.lang.getForKey("messages.addingNewConfig"), "enableRegenerationNextChunkLoad", this.configFile.getName()));
        } else {
            this.enableRegenerationNextChunkLoad = config.getBoolean("enableRegenerationNextChunkLoad");
        }
    }

    public void updateConfig() {
        this.plugin.utils.throwMessage("info",this.plugin.lang.getForKey("messages.oldConfigFileUpdating"));
        // For now, we just update the config version.
        this.configVersion = this.plugin.getDescription().getVersion();
    }
    
    public void validateConfig() {
        this.plugin.utils.throwMessage("info", this.plugin.lang.getForKey("messages.validatingConfig"));
        if (this.distanceNearbyMinimum < 16) {
            this.plugin.utils.throwMessage("warning", String.format(this.plugin.lang.getForKey("messages.distanceNearbyTooClose"), "16"));
            this.distanceNearbyMinimum = 16;
        }
        if (this.distanceNearbyMinimum > 256) {
            this.plugin.utils.throwMessage("warning", String.format(this.plugin.lang.getForKey("messages.distanceNearbyTooFar"), "256"));
            this.distanceNearbyMinimum = 256;
        }
        
        if (this.parseInterval < 60) {
            this.plugin.utils.throwMessage("warning", String.format(this.plugin.lang.getForKey("messages.parseIntervalInsufficient"), "60"));
            this.parseInterval = 60;
        }
        if (!this.targetLoadedChunks && !this.targetUnloadedChunks) {
            this.plugin.utils.throwMessage("warning", String.format(this.plugin.lang.getForKey("messages.mustTargetSomething")));
            this.targetUnloadedChunks = true;
        }
        if (this.parseInterval > 3600) {
            this.plugin.utils.throwMessage("warning", String.format(this.plugin.lang.getForKey("messages.parseIntervalNotFrequent"), "3600"));
            this.parseInterval = 3600;
        }
        if (this.minTpsRegen > 20 || this.minTpsRegen < 1) {
            this.plugin.utils.throwMessage("warning", String.format(this.plugin.lang.getForKey("messages.tpsInvalid"), "15"));
            this.minTpsRegen = 15;
        }
        if (this.percentIntervalRuntime > 1.0 || this.percentIntervalRuntime < 0.1) {
            this.plugin.utils.throwMessage("warning", String.format(this.plugin.lang.getForKey("messages.runtimeInvalid"), "0.5"));
            this.percentIntervalRuntime = 0.5;
        }
        if (this.numChunksPerParse > (((this.parseInterval * 10)) * this.percentIntervalRuntime) || this.numChunksPerParse < 1) {
            this.plugin.utils.throwMessage("warning", String.format(this.plugin.lang.getForKey("messages.numChunksPerParseInvalid"), String.valueOf((((this.parseInterval * 10)) * this.percentIntervalRuntime)/2)));
            this.numChunksPerParse = (((this.parseInterval * 5)) * this.percentIntervalRuntime);
        }
        if (!config.isSet("language")) {
            this.plugin.utils.throwMessage("new",String.format(this.plugin.lang.getForKey("messages.addingNewConfig"), "language", this.configFile.getName()));
        } else {
            if (new File(plugin.getDataFolder() + "/lang/" + config.getString("language") + ".yml").exists()) {
                this.plugin.utils.throwMessage("info", "Loading language: " + config.getString("language"));
                this.language = config.getString("language");
            } else {
                if (config.getString("language").equals("ENGLISH")) {
                    this.plugin.utils.throwMessage("severe", "Loading default language, ENGLISH failed. No valid language is set & the default language of ENGLISH has been deleted, so Regenerator cannot load.");
                    this.plugin.disablePlugin();
                    return;
                }
                this.plugin.utils.throwMessage("warning", "Loading language: " + config.getString("language") + " failed, there is no YML file called " + plugin.getDataFolder() + "/lang/" + config.getString("language") + ".yml" + "!");
                this.plugin.utils.throwMessage("warning", "Contribute a messages.yml for : " + config.getString("language") + " on github (http://github.com/draksterau/regenerator/) to have it included in a future release!");
                this.language = "ENGLISH";
            }
        }
        this.saveData();
        this.loadData();
    }
    @Override
    void saveData() {
        config.set("defaultManualRegen", this.defaultManualRegen);
        config.set("defaultAutoRegen", this.defaultAutoRegen);
        config.set("noGriefRun", this.noGriefRun);
        config.set("minTpsRegen", this.minTpsRegen);
        config.set("configVersion", this.configVersion);
        config.set("numChunksPerParse", this.numChunksPerParse);
        config.set("parseInterval", this.parseInterval);
        config.set("percentIntervalRuntime", this.percentIntervalRuntime);
        config.set("distanceNearbyMinimum", this.distanceNearbyMinimum);
        config.set("targetLoadedChunks", this.targetLoadedChunks);
        config.set("targetUnloadedChunks", this.targetUnloadedChunks);
        config.set("regenerateChunksInUseByPlayers", this.regenerateChunksInUseByPlayers);
        config.set("clearRegeneratedChunksOfEntities", this.clearRegeneratedChunksOfEntities);
        config.set("excludeEntityTypesFromRegeneration", this.excludeEntityTypesFromRegeneration);
        config.set("warpDriveCompatibility", this.warpDriveCompatibility);
        config.set("cacheChunksOnLoad", this.cacheChunksOnLoad);
        config.set("fakePlayerUUID", this.fakePlayerUUID.toString());
        config.set("enableUnknownProtectionDetection", this.enableUnknownProtectionDetection);
        config.set("enableRegenerationNextChunkLoad", this.enableRegenerationNextChunkLoad);
        
        try {
            config.save(configFile);
        } catch (IOException ex) {
            plugin.utils.throwMessage("severe",String.format(plugin.lang.getForKey("messages.cantSaveGlobalConfig"), configFile.getAbsolutePath(), ex.getMessage()));
        }    
    }
    
}
