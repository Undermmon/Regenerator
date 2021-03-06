package com.draksterau.Regenerator;

//import com.draksterau.Regenerator.commands.RegeneratorCommand;
import com.draksterau.Regenerator.listeners.eventListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;
import org.bukkit.event.Listener;
import com.draksterau.Regenerator.integration.Integration;
import com.draksterau.Regenerator.tasks.lagTask;
import com.draksterau.Regenerator.tasks.regenTask;
import com.draksterau.Regenerator.Handlers.RConfig;
import com.draksterau.Regenerator.Handlers.RLang;
import com.draksterau.Regenerator.Handlers.RUtils;
import com.draksterau.Regenerator.Handlers.RWorld;
import com.draksterau.Regenerator.commands.RegeneratorCommand;
import org.bukkit.entity.Player;

public class RegeneratorPlugin extends JavaPlugin implements Listener {
    
    // Load the RUtils module on enable.
    public RUtils utils;
        
    // Config gets loaded here in onEnable()
    public RConfig config;
    
    public RLang lang;

    public List<List<String>> availableIntergrations = new ArrayList<List<String>>();
    
    public List<Integration> loadedIntegrations = new ArrayList<Integration>();
    
    public List<RWorld> loadedWorlds = new ArrayList<RWorld>();
    
    public Player fakePlayer = null;
    
    @Override
    public void onEnable () {
        // Loads the language file.
        lang = new RLang(this);
        // Load the RUtils module.
        utils = new RUtils(this);
        // Config gets loaded here in onEnable()
        config = new RConfig(this);
        
        utils.throwMessage("info", String.format(lang.getForKey("messages.pluginLoading"), config.configVersion));
        utils.initAvailableIntegrations();
        utils.loadIntegrations();
        if (this.isEnabled()) {
            if (getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
                if (Bukkit.getPluginManager().getPlugin("WorldEdit").getDescription().getVersion().startsWith("7")) {
                    utils.throwMessage("info", "Detected Dependency: WorldEdit v" + Bukkit.getPluginManager().getPlugin("WorldEdit").getDescription().getVersion());
                } else {
                    utils.throwMessage("severe", "Detected Dependency: WorldEdit v" + Bukkit.getPluginManager().getPlugin("WorldEdit").getDescription().getVersion() + ", v7 is required!");
                    this.disablePlugin();
                }
            } else {
                    utils.throwMessage("severe", "Regenerator v3.4.0+ requires WorldEdit 7 or greater to perform regeneration due to limitations in the Spigot/Bukkit API after MC v1.13. Please install WorldEdit!");
                    this.disablePlugin();
            }
            utils.throwMessage("info", String.format(lang.getForKey("messages.pluginStarting"), config.configVersion));
            if (config.enableUnknownProtectionDetection) utils.throwMessage("info", "Experimental Feature: UnknownProtectionDetection is active! Chunks will be treated as protected if any protection plugin disallows Regenerator breaking blocks in chunks!");
            if (loadedIntegrations.isEmpty()) {
                if (config.noGriefRun) {
                    utils.throwMessage("warning", "No supported grief protection plugins found. No land will be protected from regeneration via external plugins!");
                } else {
                    utils.throwMessage("warning", "No supported grief protection plugins found. You must acknowledge that you must configure the plugin properly or risk losing chunks.");
                    utils.throwMessage("info", "Regenerator supports the following plugins:");
                    utils.iterateIntegrations();
                    utils.throwMessage("severe", "You must set 'noGriefRun' to true in config before Regenerator will load without integrations.");
                    if (!config.enableUnknownProtectionDetection) utils.throwMessage("severe", "You may set 'enableUnknownProtectionDetection' to true if you have a grief prevention plugin that is not supported. This works by having Regenerator try and break blocks as an unknown player within a chunk to see if a protection plugin prevents this.");
                }
            }
            if (this.isEnabled()) {
                utils.loadWorlds();
                
                // This registers all event listeners.
                try {
                    getServer().getPluginManager().registerEvents(new eventListener(this), this);
                    utils.throwMessage("info", "Successfully registered Event Listeners!");
                } catch (Exception e) {
                    utils.throwMessage("severe", "Failed to start event listeners. Please report this (and the below error) to the developer!");
                    e.printStackTrace();
                    this.disablePlugin();
                }
                // This registers a repeating task to measure 1 tick, so we can accurately  get TPS.
                try {
                    new lagTask().runTaskTimer(this, 100L, 1L);
                    utils.throwMessage("info", "Successfully registered TPS Monitor!");
                } catch (Exception e) {
                    utils.throwMessage("severe", "Failed to start TPS monitor. Please report this (and the below error) to the developer!");
                    e.printStackTrace();
                    this.disablePlugin();
                }
                // This registers the regeneration task.
                try {
                    new regenTask(this).runTaskTimerAsynchronously(this,1200, config.parseInterval * 20);
                    utils.throwMessage("info", "Successfully registered Regeneration Task!");
                } catch (Exception e) {
                    utils.throwMessage("severe", "Failed to start regeneration task. Please report this (and the below error) to the developer!");
                    e.printStackTrace();
                    this.disablePlugin();
                }
                utils.throwMessage("info", String.format(this.lang.getForKey("messages.parseSchedule"), "30", String.valueOf(config.parseInterval)));
            }
        }
    }
    
    public void disablePlugin() {
     Bukkit.getServer().getPluginManager().disablePlugin(this);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        RegeneratorCommand RegeneratorCommand = new RegeneratorCommand(this, sender, cmd, label, args);
        return RegeneratorCommand.doCommand();
    }

}
