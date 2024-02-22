package com.jackdaw;

import com.jackdaw.auxiliary.configuration.SettingManager;
import com.jackdaw.teleport.Teleport;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class EssentialServerFeature implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("essential-server-feature");

    @Override
    public void onInitialize() {

        String runDir = System.getProperty("user.dir");
        Path configPath = Paths.get(runDir, "config", "essential-server-feature");
        if (!Files.exists(configPath)) {
            try {
                Files.createDirectories(configPath);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }

        SettingManager setting = getSettingManager(configPath);

        if (setting == null) {
            LOGGER.error("Main: Can't load config file.");
            return;
        }
        if (setting.isFastGameModeSettingEnabled()) {
            LOGGER.info("EssentialServerFeature Loaded.");
            CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("s")
                    .executes(context -> {
                        final ServerPlayerEntity player = context.getSource().getPlayer();
                        if (player != null) {
                            player.changeGameMode(GameMode.SURVIVAL);
                            LOGGER.info("Set {} gamemode to Survival.", player.getName());
                        }
                        return 1;
                    })));
            CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("c")
                    .executes(context -> {
                        final ServerPlayerEntity player = context.getSource().getPlayer();
                        if (player != null) {
                            player.changeGameMode(GameMode.SPECTATOR);
                            LOGGER.info("Set {} gamemode to Spectator.", player.getName());
                        }
                        return 1;
                    })));
        }
        if (setting.isTeleportEnabled()) {
            LOGGER.info("Teleport Loaded.");
            Teleport teleport = new Teleport(LOGGER, configPath.toFile());

            SuggestionProvider<ServerCommandSource> suggestionProvider = (context, builder) -> {
                File workingDirectory = new File(configPath.toFile(), "points");
                File[] points = workingDirectory.listFiles();
                if (points != null) {
                    for (File point: points) {
                        Optional<String> option = Arrays.stream(point.getName().split("\\.")).findFirst();
                        option.ifPresent(builder::suggest);
                    }
                }
                return builder.buildFuture();
            };

            CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                    dispatcher.register(literal("tel")
                            .then(literal("set")
                                    .then(argument("name", StringArgumentType.greedyString())
                                            .executes(context -> {
                                                // record the position
                                                teleport.setPoint(context);
                                                return 1;
                                            }))
                                    .executes(context -> {
                                        // introduction message
                                        teleport.introductionMessage(context);
                                        return 1;
                                    }))
                            .then(literal("to")
                                    .then(argument("name", StringArgumentType.greedyString())
                                            .suggests(suggestionProvider)
                                            .executes(context -> {
                                                // teleport the player to the point
                                                teleport.teleport(context);
                                                return 1;
                                            }))
                                    .executes(context -> {
                                        // introduction message
                                        teleport.introductionMessage(context);
                                        return 1;
                                    }))
                            .then(literal("del")
                                    .then(argument("name", StringArgumentType.greedyString())
                                            .suggests(suggestionProvider)
                                            .executes(context -> {
                                                // delete the point
                                                teleport.delPoint(context);
                                                return 1;
                                            }))
                                    .executes(context -> {
                                        // introduction message
                                        teleport.introductionMessage(context);
                                        return 1;
                                    }))
                            .executes(context -> {
                                // introduction message
                                teleport.introductionMessage(context);
                                return 1;
                            })));
        }
        if (setting.isGiveOPEnabled()) {
            LOGGER.info("GiveOP Loaded.");
            CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("god")
                    .executes(context -> {
                        final ServerPlayerEntity player = context.getSource().getPlayer();
                        if (player != null) {
                            // give this player an op
                            MinecraftServer server =  context.getSource().getServer();
                            ParseResults<ServerCommandSource> results = server.getCommandManager().getDispatcher().parse("op " + player.getName().getString(), server.getCommandSource());
                            server.getCommandManager().execute(results, "op " + player.getName().getString());
                        }
                        return 1;
                    })));
            if (setting.isTempEnabled()){
                ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
                    ServerPlayerEntity player = handler.player;
                    // 检查玩家是否有op权限并取消
                    if (player.hasPermissionLevel(4)) {
                        ParseResults<ServerCommandSource> results = server.getCommandManager().getDispatcher().parse("deop " + player.getName().getString(), server.getCommandSource());
                        server.getCommandManager().execute(results, "deop " + player.getName().getString());
                    }
                });
            }
        }
    }

    @Nullable
    private SettingManager getSettingManager(@NotNull Path dataDirectory) {
        SettingManager setting;
        try {
            setting = new SettingManager(dataDirectory.toFile(), LOGGER);
        } catch (IOException ioException) {
            System.out.println(ioException.getMessage());
            return null;
        }
        return setting;
    }

}