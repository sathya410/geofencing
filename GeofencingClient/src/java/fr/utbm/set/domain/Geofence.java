/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.utbm.set.domain;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author WAFAA AIT CHEIK BIHI
 */
public class Geofence {
    /**
     * The attributes of Geofence Class
     */

    /**
     * name : The name or the description of the geofence
     */
    private String name;
    /**
     * typeVehicles : the vehicle type prohibited in the geofence
     */
    private List<TypeVehicle> typeVehicles;
     /**
     * speedLimit : The defined speed limit in the geofence
     */
    private int speedLimit;
    private double weightMax;
    private double heightMax;
    //private Double banDegrees;
    private List<Point> points;
    private boolean isTDG;
    private String color;


    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public Geofence() {
        this.name = "";
        this.typeVehicles = new ArrayList<TypeVehicle>();
        this.speedLimit = 0;
        this.weightMax = 0.0;
        this.heightMax = 0.0;
        this.points = new ArrayList<Point>();
        this.isTDG = false;
        this.color = new String("FF0000");
    }

    /**
     * Constructeur de Geofence
     * @param name of the geofence
     * @param typeVehicles list of vehicles types
     * @param speedLimit
     * @param weightMax
     * @param heightMax
     * @param points characteristics of the geofence
     * @param isTDG area allow dangerous transportation goods
     */

    public Geofence(String name, List<TypeVehicle> typeVehicles, int speedLimit, double weightMax, double heightMax, List<Point> points, boolean isTDG, String color) {
        this.name = name;
        this.typeVehicles = typeVehicles;
        this.speedLimit = speedLimit;
        this.weightMax = weightMax;
        this.heightMax = heightMax;
        this.points = points;
        this.isTDG = isTDG;
        this.color = color;
    }

    public double getHeightMax() {
        return heightMax;
    }

    public String getName() {
        return name;
    }

    public List<Point> getPoints() {
        return points;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    public List<TypeVehicle> getTypeVehicles() {
        return typeVehicles;
    }

    public double getWeightMax() {
        return weightMax;
    }

    public void setHeightMax(double heightMax) {
        this.heightMax = heightMax;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public void setSpeedLimit(int speedLimit) {
        this.speedLimit = speedLimit;
    }

    public void setTypeVehicles(List<TypeVehicle> typeVehicles) {
        this.typeVehicles = typeVehicles;
    }

    public void setWeightMax(double weightMax) {
        this.weightMax = weightMax;
    }

    public boolean isIsTDG() {
        return isTDG;
    }

    public void setIsTDG(boolean isTDG) {
        this.isTDG = isTDG;
    }
}
