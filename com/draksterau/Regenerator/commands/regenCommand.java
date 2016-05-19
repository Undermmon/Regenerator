/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.draksterau.Regenerator.commands;

import com.draksterau.Regenerator.Handlers.RChunk;
import com.draksterau.Regenerator.Handlers.RWorld;
import com.draksterau.Regenerator.integration.Integration;
import com.draksterau.Regenerator.tasks.ChunkTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

/**
 *
 * @author draks
 */
public class regenCommand {
    
    RegeneratorCommand command;
    
    public regenCommand(RegeneratorCommand RegeneratorCommand) {
        this.command = RegeneratorCommand;
    }
    
    public void doCommand() {
        if (command.plugin.utils.getSenderPlayer(command.sender) == null) {
            command.sender.sendMessage(ChatColor.RED + "This command can only be performed while in-game.");
        } else {
            if (command.plugin.utils.isLagOK()) {
                Chunk rootChunk = command.plugin.utils.getSenderPlayer(command.sender).getLocation().getChunk();
                RChunk rChunk = new RChunk(command.plugin, rootChunk.getX(), rootChunk.getZ(), rootChunk.getWorld().getName());
                if (command.plugin.utils.canManuallyRegen(command.plugin.utils.getSenderPlayer(command.sender), rootChunk)) {
                    Bukkit.getServer().getScheduler().runTask(command.plugin, new ChunkTask(rChunk, true));
                    rChunk.resetActivity();
                    Player player = command.plugin.utils.getSenderPlayer(command.sender);
                    Integration integration = command.plugin.utils.getIntegrationForChunk(player.getLocation().getChunk());
                    if (integration != null && integration.isChunkClaimed(rootChunk)) {                    
                        player.sendMessage(command.plugin.utils.getFancyName() + integration.getPlayerRegenReason(player, rootChunk));  
                    } else {
                        player.sendMessage(command.plugin.utils.getFancyName() + ChatColor.GREEN + "The unclaimed area around you has been regenerated.");
                    }
                } else {
                    Chunk senderChunk = command.plugin.utils.getSenderChunk(command.sender);
                    Player player = command.plugin.utils.getSenderPlayer(command.sender);
                    if (!rChunk.canManualRegen()) {
                        player.sendMessage(command.plugin.utils.getFancyName() + ChatColor.RED + "Failed to perform manual regeneration as the world you are on has it disabled.");
                    } else {
                        if (command.plugin.utils.getCountIntegration(player.getLocation().getChunk()) == 1) {
                            player.sendMessage(command.plugin.utils.getFancyName() + command.plugin.utils.getIntegrationForChunk(senderChunk).getPlayerRegenReason(player, rootChunk));
                            player.sendMessage(command.plugin.utils.getFancyName() + "This requires the permission node: " + command.plugin.utils.getIntegrationForChunk(senderChunk).getPermissionRequiredToRegen(player, rootChunk));
                        } else {
                            if (command.plugin.utils.getCountIntegration(player.getLocation().getChunk()) > 1) {
                                player.sendMessage(command.plugin.utils.getFancyName() + ChatColor.RED + "This chunk is claimed by more than one grief prevention plugin. It can only be regenerated by OPS or those with the regenerator.regen.override permission node.");
                            } else {
                                player.sendMessage(command.plugin.utils.getFancyName() + ChatColor.RED + "This chunk is unclaimed and requires the regenerator.regen.unclaimed permission node to regenerate.");
                            }
                        }
                    }


                }
            } else {
                command.plugin.utils.getSenderPlayer(command.sender).sendMessage(command.plugin.utils.getFancyName() + ChatColor.RED + "Regeneration capabilities have been suspended - TPS has dropped below what is set in configuration.");
            }

        }
    }
}
