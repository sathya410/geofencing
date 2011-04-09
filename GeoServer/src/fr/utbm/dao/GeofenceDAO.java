package fr.utbm.dao;

import fr.utbm.set.domain.Geofence;
import fr.utbm.set.domain.Point;
import fr.utbm.set.domain.Vehicle;
import fr.utbm.set.tml.TMLVehicleType;
import fr.utbm.set.util.Database;
import java.awt.Polygon;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author WAFAA AIT CHEIK BIHI 
 */
public class GeofenceDAO {
    private Database db;


    public GeofenceDAO() {
        db = new Database(2);
        db.openDatabase(ConnexionProperties.DB_URL, ConnexionProperties.DB_USR, ConnexionProperties.DB_PWD);
    }

    public boolean addEvent(int idvehicle, int idgeofencing, String event, Date date)
    {
        try {
            java.util.Date dt = new java.util.Date();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = sdf.format(dt);

            int rsResult = db.insert(new String[]{"idvehicle", "idgeofence", "event", "date"}, new String[]{Integer.toString(idvehicle), Integer.toString(idgeofencing), event, currentTime}, "event");
            if (rsResult == 0)
                return false;
            else
                return true;
        } catch (SQLException ex) {
            Logger.getLogger(GeofenceDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean addGeofence (Geofence geofence){
        String[] values = new String[]{geofence.getName(), 
                                       Integer.toString(geofence.getSpeedLimit()),
                                       Double.toString(geofence.getWeightMax()),
                                       Double.toString(geofence.getHeightMax()),
                                       Integer.toString((geofence.isIsTDG()) ? 1 : 0)};

        String[] fields = new String[] {"geofenceName", "speedLimit", "weight", "height", "isTDG"};
        try{
            if (db.insert( fields, values, "geofence") == 0)
                return false;
            ResultSet rs = db.executeQuery("SELECT MAX(geofenceID) FROM geofence");
            String lastID = null;
            while (rs.next()){
                lastID = rs.getString(1);
            }

            if (lastID != null){
                List<Point> listPoints = geofence.getPoints();
                for(Point p : listPoints){
                    int rsResult = db.insert(new String[] {"lat", "lon", "geofenceID"},
                            new String[] {Double.toString(p.getLat()), Double.toString(p.getLon()), lastID},
                            "points");
                    if (rsResult == 0)
                        return false;
                }

                List<TMLVehicleType> listV = geofence.getTypeVehicles() != null ? geofence.getTypeVehicles() : new ArrayList<TMLVehicleType>();
                if( listV != null && listV.size() > 0){
                    for(TMLVehicleType tv : listV){
                        System.out.println("TV = "+tv);
                        ResultSet row = db.executeQuery("SELECT vehicleTypeID FROM vehicletype WHERE type='"+tv+"'");
                        String vehicleTypeID = null;
                        while (row.next()){
                            vehicleTypeID = row.getString(1);
                        }
                        int rsResult = db.insert(new String[] {"geofenceID", "vehicleTypeID"},
                            new String[] {lastID, vehicleTypeID},
                            "geofencevehicletypeassoc");
                        if (rsResult == 0)
                            return false;
                    }
        
                }
            }
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Get the characteristics of geofence based on geofence id
     * @param idGeofence
     * @return Geofence
     */
    public Geofence selectGeofence(int idGeofence){
        Geofence geofence = new Geofence();
        ResultSet rs, rsVehic, ps;
        List<Point> points = new ArrayList<Point>();
        try {
            rs = db.executeQuery("SELECT * FROM geofence WHERE geofenceID =" +idGeofence);
            while(rs.next()){
                geofence.setIdgeofence(idGeofence);
                geofence.setHeightMax(rs.getDouble("height"));
                geofence.setIsTDG(rs.getBoolean("isTDG"));
                geofence.setName(rs.getString("geofenceName"));
                geofence.setSpeedLimit(rs.getInt("speedLimit"));
                geofence.setWeightMax(rs.getDouble("weight"));
            }

            rsVehic = db.executeQuery("SELECT * FROM geofencevehicletypeassoc WHERE geofenceID =" +idGeofence);

            while (rsVehic.next()){
                List<TMLVehicleType> typeVehicles  = geofence.getTypeVehicles();
                String listTypeVehicle  = rsVehic.getString("vehicleTypeID");

                   switch (Integer.parseInt(listTypeVehicle)) {
                    case 1: typeVehicles.add(TMLVehicleType.CAR);
                            break;
                    case 2: typeVehicles.add(TMLVehicleType.TRUCK);
                            break;
                    case 3: typeVehicles.add(TMLVehicleType.TWO_WHEEL);
                            break;
                    }
            }

            Polygon polyGTmp = new Polygon();
            polyGTmp.reset();

            ps = db.executeQuery("SELECT lat, lon FROM points WHERE geofenceID =" +idGeofence);
            while(ps.next()){
                Point aux = new Point(ps.getDouble("lat"), ps.getDouble("lon"));
                points.add(aux);
                polyGTmp.addPoint(aux.getXInt(), aux.getYInt());
            }

            geofence.setPoints(points);
            geofence.setPolygon(polyGTmp);

        }catch (Exception e){
            e.printStackTrace();
        }
        return geofence;
    }

     /**
     * Get all the known geofences
     * @return ArrayList<Geofence>
     */
    public ArrayList<Geofence> selectAllGeofences(){
        ArrayList<Geofence> res = new ArrayList<Geofence>();
        ResultSet rs;
        try {
            rs = db.executeQuery("SELECT geofenceID FROM geofence ORDER BY geofenceID");
            while(rs.next()){
                res.add(selectGeofence(rs.getInt("geofenceID")));
            }
        }
        catch (Exception e){
            //e.printStackTrace();
            System.err.println("Error;Error while getting the list of geofences .");
        }
        return res;
    }

    /**
     * Returns a String containing all the geofences' caracteristics
     * Each geofence is separated by the caracter #
     * @return String
     */
    public String getAllGeofencesString(){
        ArrayList<Geofence> geof = selectAllGeofences();
        String res = "";
        
        for (int i=0; i < geof.size(); i++){
            res += geof.get(i).toString() + "#";
        }

        if (res.equals(""))
            res = "Error# Error while getting the geofences.";
        return res;
    }

    /**
     * list ids of geofences based on vehicle characteristics
     * @param vehicle
     * @return list of Integer
     */
    public List<Integer> selectGeofences(Vehicle vehicle){
        ResultSet rs;
        List<Integer> listInt = new ArrayList<Integer>();
        String query = null;
        query = "SELECT g.geofenceID" +
                " FROM geofence g";
         query += " INNER JOIN geofencevehicletypeassoc a ON g.geofenceID = a.geofenceID" +
                    " INNER JOIN vehicletype v ON v.vehicleTypeID = a.vehicleTypeID  " +
                    " WHERE v.type = "+vehicle.getTypeVehicle();
        if(vehicle.getHeight() != 0)
            query += " AND "+vehicle.getHeight()+" < g.height";
        if(vehicle.getWeight() != 0)
            query += " AND "+vehicle.getWeight()+" < g.weight";
        if(vehicle.isIsTDG())
            query += " AND "+vehicle.isIsTDG()+" = g.isTDG";
        
        try {
            rs = db.executeQuery(query);
            while(rs.next()){
                listInt.add(rs.getInt("geofenceID"));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
       return listInt;
    }
    
    public void updateGeofence(Geofence geofence){
        
    }

    public void updateGeofences(Geofence[] geofences){
        
    }

    public void deleteGeofence(){
        
    }

}