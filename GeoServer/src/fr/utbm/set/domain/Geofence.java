package fr.utbm.set.domain;

import fr.utbm.set.tml.TMLVehicleType;
import java.awt.Polygon;
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
    private List<TMLVehicleType> typeVehicles;
     /**
     * speedLimit : The defined speed limit in the geofence
     */
    private int idgeofence;
    private int speedLimit;
    private double weightMax;
    private double heightMax;
    //private Double banDegrees;
    private List<Point> points;
    private boolean isTDG;
    //the polygon corresponding to the geofence
    private Polygon polygon;

    public Geofence() {
        this.name = "";
        this.typeVehicles = new ArrayList<TMLVehicleType>();
        this.speedLimit = 0;
        this.weightMax = 0.0;
        this.heightMax = 0.0;
        this.points = new ArrayList<Point>();
        this.isTDG = false;
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

    public Geofence(String name, List<TMLVehicleType> typeVehicles, int speedLimit, double weightMax, double heightMax, List<Point> points, boolean isTDG)
    {
        this.name = name;
        this.typeVehicles = typeVehicles;
        this.speedLimit = speedLimit;
        this.weightMax = weightMax;
        this.heightMax = heightMax;
        this.points = points;
        this.isTDG = isTDG;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }
    
    public void setIdgeofence(int idgeofence) {
        this.idgeofence = idgeofence;
    }

    public int getIdgeofence() {
        return idgeofence;
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

    public List<TMLVehicleType> getTypeVehicles() {
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

    public void setTypeVehicles(List<TMLVehicleType> typeVehicles) {
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

    @Override
    public String toString(){
        return this.name + "%%" + this.typeVehicles + "%%" + this.speedLimit + "%%" +
        this.weightMax + "%%" + this.heightMax + "%%" + this.points + "%%" + this.isTDG + "%%";
    }
}
