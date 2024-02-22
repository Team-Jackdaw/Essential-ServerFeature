package com.jackdaw.auxiliary.pointInfo;

public class PointInfo {
    private final String name;
    private int[] position;

    private String world;


    /**
     * A point information class. To initialize the class, you need the parameters below.
     * @param name The point name.
     * @param position The position of the point.
     */
    public PointInfo(String name, int[] position, String world) {
        this.name = name;
        this.position = position;
        this.world = world;
    }

    /**
     * Get the point name from the data file.
     *
     * @return the String, i.e. name of point.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the point position on X axes.
     * @return the int, i.e. position on X axes.
     */
    public int getX() {
        return this.position[0];
    }

    /**
     * Get the point position on X axes.
     * @return the int, i.e. position on X axes.
     */
    public int getY() {
        return this.position[1];
    }

    /**
     * Get the point position on X axes.
     * @return the int, i.e. position on X axes.
     */
    public int getZ() {
        return this.position[2];
    }

    /**
     * Get the world of this point.
     * @return the String, i.e. world.
     */
    public String getWorld() {
        return this.world;
    }

    /**
     * Set the server name from the data file.
     *
     * @param position The position of the point.
     */
    public void setPosition(int[] position, String world) {
        this.position = position;
        this.world = world;
    }
}
