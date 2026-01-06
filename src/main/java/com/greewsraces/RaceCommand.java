package com.greewsraces;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class RaceCommand {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, 
                               CommandRegistryAccess registryAccess, 
                               CommandManager.RegistrationEnvironment environment) {
        
        dispatcher.register(CommandManager.literal("race")
            .then(CommandManager.literal("menu")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("player", EntityArgumentType.player())
                    .executes(RaceCommand::openMenuForPlayer)))
        );
    }
    
    private static int openMenuForPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
        
        ServerPlayNetworking.send(target, new RaceSyncPayload(""));
        return 1;
    }
}