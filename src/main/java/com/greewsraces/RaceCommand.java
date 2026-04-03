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
                .executes(RaceCommand::openRaceMenuSelf)
                .then(CommandManager.argument("player", EntityArgumentType.player())
                    .executes(RaceCommand::openRaceMenuTarget)))
            .then(CommandManager.literal("language")
                .executes(RaceCommand::openLanguageSelf))
        );
    }

    private static int openRaceMenuSelf(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
            return 0;
        }
        ServerPlayNetworking.send(player, new OpenGuiPayload(OpenGuiPayload.KIND_RACE));
        return 1;
    }

    private static int openRaceMenuTarget(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
        ServerPlayNetworking.send(target, new OpenGuiPayload(OpenGuiPayload.KIND_RACE));
        return 1;
    }

    private static int openLanguageSelf(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
            return 0;
        }
        ServerPlayNetworking.send(player, new OpenGuiPayload(OpenGuiPayload.KIND_LANGUAGE));
        return 1;
    }
}
