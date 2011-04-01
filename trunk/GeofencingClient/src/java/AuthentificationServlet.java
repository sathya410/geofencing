/*
 * Servlet utilisée pour la connexion et la déconnection de l'utilisateur
 */


import fr.utbm.dao.ConnexionProperties;
import fr.utbm.set.util.Database;
import java.io.IOException;
//import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Renez
 */
public class AuthentificationServlet extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    /*Connexion et déconnexion de l'utilisateur*/
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException, SQLException {
        response.setContentType("text/html");
        java.io.PrintWriter out = response.getWriter();
        //check si l'utilisateur veut se déconnecter
        if(request.getParameter("deco")!=null)
        {
            HttpSession session=request.getSession();
            session.setAttribute("log", false);
            response.sendRedirect("login.jsp");
        }
        //tentative de connexion
        else
        {

            String login= request.getParameter("login");
            String pswd= request.getParameter("pswd");
            Database dtb = new Database(Database.MYSQL);
            dtb.openDatabase(ConnexionProperties.DB_URL, ConnexionProperties.DB_USR, ConnexionProperties.DB_PWD);
            Connection db = dtb.getConnection();
            System.out.println(db.isClosed());
            Statement isql = db.createStatement();
            String query = new String();
            query= "select count(*) from user where login=\'"+
                    login+"\' and password=\'"+pswd+"\';";
            //System.out.println(query);
            ResultSet rs = isql.executeQuery(query);
            int res=0;
            if(rs.next())
                res=rs.getInt(1);
            //check si l'utilisateur existe dans la base et se connect si oui
            if(res>0){
                HttpSession session=request.getSession();
                session.setAttribute("log", true);
                response.sendRedirect("index.jsp");
            }
            //si l'utilisateur n'existe pas dans la base l'utilisateur et reconduit sur la page de login
            else{
                response.sendRedirect("login.jsp");
            }
        }
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
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(AuthentificationServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(AuthentificationServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
