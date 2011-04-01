/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.utbm.set.domain;

/**
 *
 * @author WAFAA AIT CHEIK BIHI
 */
public class Vehicle {

    private double height = 0;
    private double weight = 0;
    private boolean isTDG = false;
    private int speedLimit = 0;
    private TypeVehicle typeVehicle;

    public Vehicle(){};

    public Vehicle(double height, double weight, boolean isTDG, int speedLimit, TypeVehicle typeVehicle){
        this.height = height;
        this.weight = weight;
        this.isTDG = isTDG;
        this.speedLimit = speedLimit;
        this.typeVehicle = typeVehicle;
    }
    
    /**
     * Get the value of typeVehicle
     *
     * @return the value of typeVehicle
     */
    public TypeVehicle getTypeVehicle() {
        return typeVehicle;
    }

    /**
     * Set the value of typeVehicle
     *
     * @param typeVehicle new value of typeVehicle
     */
    public void setTypeVehicle(TypeVehicle typeVehicle) {
        this.typeVehicle = typeVehicle;
    }

    /**
     * Get the value of speedLimit
     *
     * @return the value of speedLimit
     */
    public int getSpeedLimit() {
        return speedLimit;
    }

    /**
     * Set the value of speedLimit
     *
     * @param speedLimit new value of speedLimit
     */
    public void setSpeedLimit(int speedLimit) {
        this.speedLimit = speedLimit;
    }


    /**
     * Get the value of isTDG
     *
     * @return the value of isTDG
     */
    public boolean isIsTDG() {
        return isTDG;
    }

    /**
     * Set the value of isTDG
     *
     * @param isTDG new value of isTDG
     */
    public void setIsTDG(boolean isTDG) {
        this.isTDG = isTDG;
    }


    /**
     * Get the value of weight
     *
     * @return the value of weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Set the value of weight
     *
     * @param weight new value of weight
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }


    /**
     * Get the value of height
     *
     * @return the value of height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Set the value of height
     *
     * @param height new value of height
     */
    public void setHeight(double height) {
        this.height = height;
    }

}
