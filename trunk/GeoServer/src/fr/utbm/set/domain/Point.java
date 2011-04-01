package fr.utbm.set.domain;

/**
 *
 * @author WAFAA AIT CHEIK BIHI
 */
public class Point {
    double lat;
    double lon;
    public static final double r = 6378;// earth radius in Km.

    public Point(){
    }

    public Point(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getX(){
        return r*Math.cos(lon)*Math.cos(lat);
    }

    public double getY(){
        return r*Math.cos(lon)*Math.sin(lat);
    }

    public double getZ(){
        return r*Math.sin(lon);
    }

    public int getXInt(){
        return (int) (r*Math.cos(lon)*Math.cos(lat)) * 100000;
    }

    public int getYInt(){
        return (int) (r*Math.cos(lon)*Math.sin(lat)) * 100000;
    }

    public int getZInt(){
        return (int) (r*Math.sin(lon)) * 100000;
    }

    @Override
    public String toString(){
        return lat + ";" + lon + "\n";
    }

    public Point fromString(String s){
        String[] latlon = s.split(";");
        return new Point(Double.parseDouble(latlon[0].trim()), Double.parseDouble(latlon[1].trim()));
    }
    
}
