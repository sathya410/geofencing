
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;


import fr.utbm.dao.GeofenceDAO;
import fr.utbm.set.domain.Geofence;
import fr.utbm.set.domain.Point;
import fr.utbm.set.domain.TypeVehicle;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

 /**
 *
 * @author Administrateur
 */
public class addGeofence extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */

    //Les Attributs
    String titre;
    String couleur;
    String infos;
    String VMax;
    String PMax;
    String listTypeVehicle;
    String HautMAX;
    String isTDG;
    String idGeo;


    String[] listeLat=new String[100];
    String[] listeLng=new String[100];

    String[] schedule = new String[10];
    String scheduleType;

    //On crée un nouveau Document JDOM basé sur la racine que l'on vient de créer
    Document document = null;// new com.sun.xml.internal.txw2.Document();


    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String lat = request.getParameter("lat");
        String lng = request.getParameter("lng");


        listeLat=lat.split(":");
        listeLng=lng.split(":");
        List<Point> listPoints = new ArrayList<Point>();
        // Getting point list
        for (int i=0; i<listeLat.length; i++){
            System.out.println("LAT avant:"+listeLat[i]);
            System.out.println("LON avant:"+listeLng[i]);
            System.out.println("**************************************");
            if(listeLat[i] != null && listeLng[i] != null){
                listeLat[i] = listeLat[i].trim();
                listeLng[i] = listeLng[i].trim();
                System.out.println("LAT apres:"+listeLat[i]);
            System.out.println("LON apre:"+listeLng[i]);
            System.out.println("**************************************");
                if(listeLat[i].compareTo("") != 0 && listeLng[i].compareTo("") != 0){
                    Point p = new Point( Double.parseDouble(listeLat[i]), Double.parseDouble(listeLng[i]));
                    listPoints.add(p);
                    System.out.println("point ajouté !");
                }
            }
        }
        
        titre   = request.getParameter("titre");
        couleur = request.getParameter("couleur");
        infos   = request.getParameter("infos");
        VMax    = request.getParameter("VMax");
        PMax    = request.getParameter("PMax");
        listTypeVehicle   = request.getParameter("TypeV");
        String[] split = listTypeVehicle.split(",");
        List<TypeVehicle> listTypeV = new ArrayList<TypeVehicle>();

        //Get a list of vehicle
        for(int i=0; i<split.length; i++){
            switch (Integer.parseInt(split[i])) {
                case 1: listTypeV.add(TypeVehicle.CAR);
                        break;
                case 2: listTypeV.add(TypeVehicle.TRUCK);
                        break;
                case 3: listTypeV.add(TypeVehicle.TWOWHEEL);
                        break;
            }
        }
        HautMAX = request.getParameter("HMax");
        isTDG   = request.getParameter("isTDG");
        idGeo    = request.getParameter("geo");
        scheduleType = request.getParameter("scheduleType");


        //Process informations following the schedule type.
        if(scheduleType.equals("1")){
            schedule = new String[2];
            schedule[1] = request.getParameter("day");
        }
        else if(scheduleType.equals("2")){
            schedule = new String[5];
            schedule[1] = request.getParameter("dayBegin");
            schedule[2] = request.getParameter("hourBegin");
            schedule[3] = request.getParameter("dayEnd");
            schedule[4] = request.getParameter("hourEnd");
        }
        else if(scheduleType.equals("3")){
            schedule = new String[4];
            schedule[1] = request.getParameter("day");
            schedule[2] = request.getParameter("hourBegin");
            schedule[3] = request.getParameter("hourEnd");
        }
        else if(scheduleType.equals("4")){
            String days = request.getParameter("days");
            String daysSplit[] = days.split(",");
            System.out.println(daysSplit.length);
            for(int i = 0; i<daysSplit.length; i++){
                schedule[i+1] = daysSplit[i];
            }
            schedule[8] = request.getParameter("hourBegin");
            schedule[9] = request.getParameter("hourEnd");

        }

        schedule[0] = request.getParameter("scheduleType");

        Geofence geofence = new Geofence(titre, listTypeV, Integer.parseInt(VMax) , Double.parseDouble(PMax), Double.parseDouble(HautMAX), listPoints, Boolean.valueOf(isTDG), couleur);

        //If the geofence has an Id ( means that it has already been created ), so make and update, else, make an insert.
        try{
            GeofenceDAO dao = new GeofenceDAO();
            if(!idGeo.equals("-1")){
               dao.updateGeofence(geofence, idGeo);
               dao.updateSchedule(idGeo, schedule);
            }
         else{
                dao.addGeofence(geofence);
                dao.insertSchedule(schedule);
         }
            
            
        } catch (Exception e){
            e.printStackTrace();
        }

        response.sendRedirect("index.jsp");

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    public void transformerXml(Document document, PrintWriter fichier) {
    	try {
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(fichier);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer serializer = tf.newTransformer();
            serializer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
            serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"users.dtd");
            serializer.setOutputProperty(OutputKeys.INDENT,"yes");
            serializer.transform(domSource, streamResult);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void GenerateXML(PrintWriter out)
    {
          // PrintWriter from a Servlet
            //PrintWriter out2 = response.getWriter();

        try {

// Create XML DOM document (Memory consuming).
            Document xmldoc = null;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();
            Element e = null;
            Node n = null;
// Document.
            xmldoc = impl.createDocument(null, "USERS", null);
// Root element.
            Element root = xmldoc.getDocumentElement();
            String[] id = {"PWD122", "MX787", "A4Q45"};
            String[] type = {"customer", "manager", "employee"};
            String[] desc = {"Tim@Home", "Jack&Moud", "John Doe"};
            for (int i = 0; i < id.length; i++) {
                // Child i.
                e = xmldoc.createElementNS(null, "USER");
                e.setAttributeNS(null, "ID", id[i]);
                e.setAttributeNS(null, "TYPE", type[i]);
                n = xmldoc.createTextNode(desc[i]);
                e.appendChild(n);
                root.appendChild(e);
            }

            transformerXml(xmldoc, out);

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(addGeofence.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
