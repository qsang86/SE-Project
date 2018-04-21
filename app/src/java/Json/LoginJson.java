/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Json;

import DAO.UserDAO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import entity.User;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Prints success status of Login functionality in JSON format
 *
 * @author Yong Qing
 */

@WebServlet(name = "LoginJson", urlPatterns = {"/json/authenticate"})


public class LoginJson extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //response.setContentType("text/html;charset=UTF-8");
        //create shared secret
        final String SHARED_SECRET = "seteamrocketfour";
        
        try (PrintWriter out = response.getWriter()) {
            // create JsonObject
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject jsonOutput = new JsonObject();
            JsonArray error = new JsonArray();
            
            
            /* TODO output your page here. You may use following sample code. */
            String email = request.getParameter("username");
            String pwd = request.getParameter("password");
            
            //add validation for blank username and password
            if (email == null || email.isEmpty()) {
                error.add("blank username");
            }
            
            if (pwd == null || pwd.isEmpty()) {
                error.add("blank password");
            }
            
            //print out error messages if blank 
            if (error.size() > 0) {
                jsonOutput.addProperty("status","error");
                jsonOutput.add("messages", error);
                String output = gson.toJson(jsonOutput);
                out.println(output);
                return;
            }
            
            User user = null;

            try {
                user = UserDAO.retrieveUser(email, pwd);
                if (user != null) {
                   String webToken = JWTUtility.sign(SHARED_SECRET, email);
                   jsonOutput.addProperty("status","success");
                   jsonOutput.addProperty("token", webToken);
                   String output = gson.toJson(jsonOutput);
                   out.println(output);
                   return;
                } else {
                    error.add("invalid username/password");
                }
            } catch (Exception e) {
               error.add("invalid username/password");
            }
            
            jsonOutput.addProperty("status","error");
            jsonOutput.add("messages", error);
            String output = gson.toJson(jsonOutput);
            out.println(output);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */


    /**
     * Handles the HTTP <code>POST</code> method.
     *
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
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
