package com.jackdaw.auxiliary.pointInfo;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

/**
 * A serializer used to read or write the point information from the files.
 *
 * <p>Read or Write the data file with point information, each file just record one point information.</p>
 */
public final class PointInfoManager {
    private final Logger logger;
    private final File theFile;
    private final PointInfo pointInfo;

    public PointInfoManager(File workingDirectory, Logger logger, @NotNull String pointName) {
        this.logger = logger;
        int[] defaultPos = {0, 0, 0};
        String defaultWorld = "world";
        this.pointInfo = new PointInfo(pointName, defaultPos, defaultWorld);
        this.theFile = new File(workingDirectory, pointName + ".yml");
        readOrInitialize();
    }

    // read or initialize the user data file
    private void readOrInitialize() {
        if (!theFile.exists()) {
            return;
        }
        try {
            HashMap pointData = YamlUtils.readFile(theFile);
            int[] position = new int[3];
            position[0] = (int) pointData.get("x");
            position[1] = (int) pointData.get("y");
            position[2] = (int) pointData.get("z");
            String world = (String) pointData.get("world");
            this.pointInfo.setPosition(position, world);
        } catch (FileNotFoundException e) {
            logger.error("Teleport: Can't open the point data file.");
        }
    }


    // Confirm and write the file as the PointInfo class record.
    private void writeFile() {
        try {
            if (!theFile.exists()) {
                if (!theFile.createNewFile()) {
                    return;
                }
            }
            HashMap userData = new HashMap();
            userData.put("name", this.pointInfo.getName());
            userData.put("x", this.pointInfo.getX());
            userData.put("y", this.pointInfo.getY());
            userData.put("z", this.pointInfo.getZ());
            userData.put("world", this.pointInfo.getWorld());
            YamlUtils.writeFile(theFile, userData);
        } catch (IOException e) {
            this.logger.error("Teleport: Can't write the point data file.");
        }
    }

    /**
     * Get Point information from data file.
     *
     * @return Point information.
     */
    public PointInfo getPointInfo() {
        return pointInfo;
    }

    /**
     * Set point's record position.
     * @param pos position.
     */
    public void setPos(@NotNull BlockPos pos, @NotNull ServerWorld serverWorld) {
        int[] position = {pos.getX(), pos.getY(), pos.getZ()};
        String world = serverWorld.getRegistryKey().getValue().toString();
        this.pointInfo.setPosition(position, world);
        writeFile();
    }

    /**
     * Delete the point.
     */
    public void deletePoint() {
        if (!theFile.exists()) {
            return;
        }
        if (!theFile.delete()) {
            this.logger.error("Teleport: Can't delete the point data file.");
        }
    }
}
