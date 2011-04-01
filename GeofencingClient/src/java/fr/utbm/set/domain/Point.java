/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.utbm.set.domain;

/**
 *
 * @author WAFAA AIT CHEIK BIHI
 */
public class Point {

    private double lat;
    private double lon;

    public Point() {
    }
    
    public Point(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * Get the value of longitude
     *
     * @return the value of longitude
     */
    public double getLon() {
        return lon;
    }

    /**
     * Set the value of longitude
     *
     * @param lon new value of longitude
     */
    public void setLon(double lon) {
        this.lon = lon;
    }

    /**
     * Get the value of latitude
     *
     * @return the value of latitude
     */
    public double getLat() {
        return lat;
    }

    /**
     * Set the value of latitude
     *
     * @param lat new value of latitude
     */
    public void setLat(double lat) {
        this.lat = lat;
    }


}
