package com.jackdaw.teleport;

import com.jackdaw.auxiliary.pointInfo.PointInfo;
import com.jackdaw.auxiliary.pointInfo.PointInfoManager;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;


public class Teleport {
    private final Logger logger;

    private final File workingDirectory;

    public Teleport(Logger logger, File workingDirectory){
        this.logger = logger;
        this.workingDirectory = new File(workingDirectory, "points");
        checkFolder();
    }

    public void introductionMessage(@NotNull CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            player.sendMessage(Text.of("""
                    Teleport plugin command:
                    /tel set <name> - set a point
                    /tel del <name> - delete a point
                    /tel to <name> - teleport to a point"""));
        }
    }

    public void setPoint(@NotNull CommandContext<ServerCommandSource> context) {
        String name = context.getArgument("name", String.class);
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            PointInfoManager aPoint = new PointInfoManager(this.workingDirectory, this.logger, name);
            aPoint.setPos(player.getBlockPos(), player.getWorld());
            player.sendMessage(Text.of("Set point **.".replace("**", name)));
        } else {
            logger.error("Not a player sent the teleport command.");
        }
    }

    public void teleport(@NotNull CommandContext<ServerCommandSource> context) {
        String name = context.getArgument("name", String.class);
        final ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            PointInfoManager aPoint = new PointInfoManager(this.workingDirectory, this.logger, name);
            PointInfo pointInfo = aPoint.getPointInfo();
            for (ServerWorld world: context.getSource().getServer().getWorlds()) {
                if (world.getRegistryKey().getValue().toString().equals(pointInfo.getWorld())) {
                    player.teleport(world, pointInfo.getX(), pointInfo.getY(), pointInfo.getZ(), 0, 0);
                    return;
                }}
            player.sendMessage(Text.of("No point match the argument."));
        }
    }


    public void delPoint(@NotNull CommandContext<ServerCommandSource> context) {
        String name = context.getArgument("name", String.class);
        PointInfoManager aPoint = new PointInfoManager(this.workingDirectory, this.logger, name);
        aPoint.deletePoint();
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            player.sendMessage(Text.of("Deleted point **.".replace("**", name)));
        }
    }

    private void checkFolder(){
        if(!workingDirectory.exists()){
            if(!workingDirectory.mkdir()){
                logger.error("Teleport: Can't make a new folder.");
            }
        }
    }
}
