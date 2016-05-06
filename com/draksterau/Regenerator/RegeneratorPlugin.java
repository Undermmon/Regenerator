package com.draksterau.Regenerator;

//import com.draksterau.Regenerator.commands.RegeneratorCommand;
import com.draksterau.Regenerator.listeners.eventListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;
import org.bukkit.event.Listener;
import com.draksterau.Regenerator.integration.Integration;
import com.draksterau.Regenerator.tasks.lagTask;
import com.draksterau.Regenerator.tasks.regenTask;
import com.draksterau.Regenerator.Handlers.RChunk;
import com.draksterau.Regenerator.Handlers.RConfig;
import com.draksterau.Regenerator.Handlers.RUtils;
import com.draksterau.Regenerator.Handlers.RWorld;
import com.draksterau.Regenerator.commands.RegeneratorCommand;

public class RegeneratorPlugin extends JavaPlugin implements Listener {
    
    // Config gets loaded here in onEnable()
    public RConfig config = new RConfig(this);
    
    // Load the RUtils module.
    public RUtils utils = new RUtils(this);
        
    public Logger log = Logger.getLogger("Minecraft");

    public List<List<String>> availableIntergrations = new ArrayList<List<String>>();
    
    public List<Integration> loadedIntegrations = new ArrayList<Integration>();
    
    public List<RWorld> loadedWorlds = new ArrayList<RWorld>();
    
    public List<RChunk> loadedChunks = new ArrayList<RChunk>();

    public boolean isPaused = false;
    
    @Override
    public void onEnable () {
        utils.throwMessage("info", "Loaded Regenerator!");
        utils.initAvailableIntegrations();
        utils.loadIntegrations();
        utils.loadWorlds();
        if (this.isEnabled()) {
            utils.throwMessage("info", "Starting Regenerator v" + config.configVersion);
            if (loadedIntegrations.isEmpty()) {
                if (config.noGriefRun) {
                    utils.throwMessage("warning", "No supported grief protection plugins found. No land will be protected from regeneration via external plugins!");
                } else {
                    utils.throwMessage("severe", "No supported grief protection plugins found. You must set 'noGriefRun' to true in config before Regenerator will load. This is accepting you need to configure things properly OR YOU WILL LOSE CHUNKS!");
                }
            }
            if (this.isEnabled()) {
                // This registers all event listeners.
                getServer().getPluginManager().registerEvents(new eventListener(this), this);
                // This registers a repeating task to measure 1 tick, so we can accurately  get TPS.
                getServer().getScheduler().runTaskTimer(this, new lagTask(), 100L, 1L);
                // This registers the regeneration task.
                getServer().getScheduler().runTaskTimerAsynchronously(this, new regenTask(this), config.parseInterval * 20, config.parseInterval * 20);
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
