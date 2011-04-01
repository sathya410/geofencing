/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.utbm.dao;

import fr.utbm.set.domain.Geofence;
import fr.utbm.set.domain.Point;
import fr.utbm.set.domain.TypeVehicle;
import fr.utbm.set.domain.Vehicle;
import fr.utbm.set.util.Database;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public boolean addGeofence (Geofence geofence){
        String[] values = new String[]{geofence.getName(), 
                                       Integer.toString(geofence.getSpeedLimit()),
                                       Double.toString(geofence.getWeightMax()),
                                       Double.toString(geofence.getHeightMax()),
                                       Integer.toString((geofence.isIsTDG()) ? 1 : 0),
                                        geofence.getColor()};

        String[] fields = new String[] {"geofenceName", "speedLimit", "weight", "height", "isTDG", "color"};
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

                List<TypeVehicle> listV = geofence.getTypeVehicles() != null ? geofence.getTypeVehicles() : new ArrayList<TypeVehicle>();
                if( listV != null && listV.size() > 0){
                    for(TypeVehicle tv : listV){
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
        ResultSet rs, ps, vs;
        List<Point> points = new ArrayList<Point>();
        List<TypeVehicle> listTypeV = new ArrayList<TypeVehicle>();
        try {
            rs = db.executeQuery("SELECT * FROM geofence WHERE geofenceID =" +idGeofence);
            while(rs.next()){
                geofence.setHeightMax(rs.getDouble("height"));
                geofence.setIsTDG(rs.getBoolean("isTDG"));
                geofence.setName(rs.getString("geofenceName"));
                geofence.setSpeedLimit(rs.getInt("speedLimit"));
                geofence.setWeightMax(rs.getDouble("weight"));
                geofence.setColor(rs.getString("color"));
            }

            ps = db.executeQuery("SELECT lat, lon FROM points WHERE geofenceID =" +idGeofence);
            while(ps.next()){
                points.add(new Point(ps.getDouble("lat"), ps.getDouble("lon")));
            }

            vs = db.executeQuery("SELECT vehicleTypeID FROM geofencevehicletypeassoc WHERE geofenceID =" +idGeofence);
            while(vs.next()){
                switch (vs.getInt("vehicleTypeID")) {
                    case 1: listTypeV.add(TypeVehicle.CAR);
                            break;
                    case 2: listTypeV.add(TypeVehicle.TRUCK);
                            break;
                    case 3: listTypeV.add(TypeVehicle.TWOWHEEL);
                            break;
                }
            }

            geofence.setPoints(points);
            geofence.setTypeVehicles(listTypeV);

        }catch (Exception e){
            e.printStackTrace();
        }
        return geofence;
    }
    /**
     * list ids of geofences related to vehicle characteristics
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
    
    /**
     * Update the characteristics of geofence based on geofence id
     * @param idGeo, id of the geofence
     * @param geofence, the new characteristics
     */
    public void updateGeofence(Geofence geofence, String idGeo) throws SQLException{
        //db.executeQuery("DELETE * FROM points WHERE geofenceID ="+ idGeo);
        db.removeRow(new String[] {"geofenceID"}, new String[] {idGeo}, "points");
        String[] values = new String[]{geofence.getName(),
                                       Integer.toString(geofence.getSpeedLimit()),
                                       Double.toString(geofence.getWeightMax()),
                                       Double.toString(geofence.getHeightMax()),
                                       Integer.toString((geofence.isIsTDG()) ? 1 : 0),
                                        geofence.getColor()};
        List<Point> listPoints = geofence.getPoints();
                for(Point p : listPoints){
                    db.insert(new String[] {"lat", "lon", "geofenceID"},
                            new String[] {Double.toString(p.getLat()), Double.toString(p.getLon()), idGeo},
                            "points");
        }


        db.removeRow(new String[] {"geofenceID"}, new String[] {idGeo}, "geofencevehicletypeassoc");

        List<TypeVehicle> listV = geofence.getTypeVehicles() != null ? geofence.getTypeVehicles() : new ArrayList<TypeVehicle>();
        if( listV != null && listV.size() > 0){
            for(TypeVehicle tv : listV){
                System.out.println("TV = "+tv);
                ResultSet row = db.executeQuery("SELECT vehicleTypeID FROM vehicletype WHERE type='"+tv+"'");
                String vehicleTypeID = null;
                while (row.next()){
                    vehicleTypeID = row.getString(1);
                }
                int rsResult = db.insert(new String[] {"geofenceID", "vehicleTypeID"},
                    new String[] {idGeo, vehicleTypeID},
                    "geofencevehicletypeassoc");
                if (rsResult == 0)
                    System.out.println("/!\\Update vehicle ");
            }

        }
      
        String[] fields = new String[] {"geofenceName", "speedLimit", "weight", "height", "isTDG", "color"};


       
       for(int k = 0; k < fields.length; k++){
           System.out.println(fields[k]);
       }
       
       db.updateRow("geofenceID",idGeo,fields,  values,"geofence") ;


    }
    

    public boolean insertSchedule(String[] schedule )throws SQLException{
        try{
            int rsResult = 1;
            String[] fields;
            String scheduleType = new String(schedule[0]);
            

        
            ResultSet rs = db.executeQuery("SELECT MAX(geofenceID) FROM geofence");
            String lastID = null;
            while (rs.next()){
                lastID = rs.getString(1);
            }


            if (lastID != null){
                if(scheduleType.equals("1")){
                    fields = new String[] {"geofenceID","dateBegin"};
                }
                else if(scheduleType.equals("2")){
                    fields = new String[] {"geofenceID","dateBegin", "hourBegin", "dateEnd",  "hourEnd"};
                }
                else if(scheduleType.equals("3")){
                    fields = new String[] {"geofenceID","dateBegin", "hourBegin",  "hourEnd"};
                }
                else if(scheduleType.equals("4")){
                    fields = new String[] {"geofenceID","monday", "tuesday",  "wednesday", "thursday", "friday", "saturday", "sunday", "hourBegin", "hourEnd"};
                }
                else if(scheduleType.equals("0")){
                    fields = new String[1];
                }
                else{
                    fields = new String[1];
                    return false;
                }
                schedule[0] = lastID;
                
                db.updateRow("geofenceID", lastID ,new String[] {"scheduleID"},  new String[] {scheduleType},"geofence");
                if(scheduleType.equals("4")){
                    rsResult = db.insert( fields, schedule, "scheduleperiodic");
                    
                }
                else if(scheduleType.equals("0")){
                
                }
                else{
                    rsResult = db.insert( fields, schedule,  "scheduledate");
                }

                if (rsResult == 0)
                        return false;
            }
            
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    
    public boolean updateSchedule(String geofenceID, String[] schedule )throws SQLException{
        try{
            int rsResult = 1;
            String[] fields;
            String scheduleType = new String(schedule[0]);


            if(scheduleType.equals("1")){
                fields = new String[] {"geofenceID","dateBegin"};
            }
            else if(scheduleType.equals("2")){
                fields = new String[] {"geofenceID","dateBegin", "hourBegin", "dateEnd",  "hourEnd"};
            }
            else if(scheduleType.equals("3")){
                fields = new String[] {"geofenceID","dateBegin", "hourBegin",  "hourEnd"};
            }
            else if(scheduleType.equals("4")){
                fields = new String[] {"geofenceID","monday", "tuesday",  "wednesday", "thursday", "friday", "saturday", "sunday", "hourBegin", "hourEnd"};
            }
            else if(scheduleType.equals("0")){
                fields = new String[1];
            }
            else{
                fields = new String[1];
                return false;
            }
            schedule[0] = geofenceID;

            db.updateRow("geofenceID", geofenceID ,new String[] {"scheduleID"},  new String[] {scheduleType},"geofence");
            db.removeRow(new String[] {"geofenceID"}, new String[] {geofenceID}, "scheduleperiodic");
            db.removeRow(new String[] {"geofenceID"}, new String[] {geofenceID}, "scheduledate");
            
            if(scheduleType.equals("4")){
                rsResult = db.insert( fields, schedule, "scheduleperiodic");

            }
            else if(scheduleType.equals("0")){

            }
            else{
                rsResult = db.insert( fields, schedule,  "scheduledate");
            }

            if (rsResult == 0)
                    return false;

            return true;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
     

    public void updateGeofences(Geofence[] geofences){
        
    }

    public void deleteGeofence(String geofenceID){
        db.removeRow(new String[] {"geofenceID"}, new String[] {geofenceID}, "scheduledate");
        db.removeRow(new String[] {"geofenceID"}, new String[] {geofenceID}, "scheduleperiodic");
        db.removeRow(new String[] {"geofenceID"}, new String[] {geofenceID}, "geofencevehicletypeassoc");
        db.removeRow(new String[] {"geofenceID"}, new String[] {geofenceID}, "geofence");
    }
    

    public String[] getSchedule(int geofenceID){

        String[] res = new String[1];
        ResultSet rs, ps;
        int scheduleType = -1;
        try {
            rs = db.executeQuery("SELECT scheduleID FROM geofence WHERE geofenceID =" +geofenceID);
            while(rs.next()){
                scheduleType = (rs.getInt("scheduleID"));

            }

            if(scheduleType == 4){
                ps = db.executeQuery("SELECT * FROM scheduleperiodic WHERE geofenceID =" +geofenceID);
                while(ps.next()){
                    res = new String[10];
                    res[1] = ps.getString("monday");
                    res[2] = ps.getString("tuesday");
                    res[3] = ps.getString("wednesday");
                    res[4] = ps.getString("thursday");
                    res[5] = ps.getString("friday");
                    res[6] = ps.getString("saturday");
                    res[7] = ps.getString("sunday");
                    res[8] = ps.getString("hourBegin").substring(0, 5);
                    res[9] = ps.getString("hourEnd").substring(0, 5);
                }
                
            }else if(scheduleType == 0){
                res = new String[1];

            }else if(scheduleType != -1){
                ps = db.executeQuery("SELECT * FROM scheduledate WHERE geofenceID =" +geofenceID);
                while(ps.next()){
                    if(scheduleType == 1){
                        res = new String[2];
                        res[1] = ps.getString("dateBegin");
                    }
                    else if(scheduleType == 2){
                        res = new String[5];
                        res[1] = ps.getString("dateBegin");
                        res[2] = ps.getString("hourBegin").substring(0, 5);
                        res[3] = ps.getString("dateEnd");
                        res[4] = ps.getString("hourEnd").substring(0, 5);

                    }
                    else if(scheduleType == 3){
                        res = new String[4];
                        res[1] = ps.getString("dateBegin");
                        res[2] = ps.getString("hourBegin").substring(0, 5);
                        res[3] = ps.getString("hourEnd").substring(0, 5);
                    }
                }
                
            }
            if(res.length != 1 || scheduleType == 0 ){
                res[0] = Integer.toString(scheduleType);
            }else{
                res[0] = "-1";
            }
            

        }catch (SQLException e){
            e.printStackTrace();
        }

        return res;
    }

}