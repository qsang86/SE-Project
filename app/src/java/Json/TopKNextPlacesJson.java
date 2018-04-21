/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Json;

import Controller.TopKNextPlacesController;
import DAO.LocationDAO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import entity.Location;
import entity.TimeStampValidator;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Retrieve top k next places in JSON format
 *
 * @author Daryln
 */
@WebServlet(name = "TopKNextPlacesJson", urlPatterns = {"/json/top-k-next-places"})
public class TopKNextPlacesJson extends HttpServlet {

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
        final String SHARED_SECRET = "seteamrocketfour";
        
        try (PrintWriter out = response.getWriter()) {
       
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject jsonOutput = new JsonObject();
            JsonArray successMessages = new JsonArray();
            JsonArray error = new JsonArray();

            String webToken = request.getParameter("token");
            String k = request.getParameter("k");
            String dateTime = request.getParameter("date");
            String origin = request.getParameter("origin");
            int kNum = 3;

            if (dateTime == null || dateTime.isEmpty()) {
                error.add("blank date");
            }

            if (origin == null || origin.isEmpty()) {
                error.add("blank origin");
            }
            
            
            if (webToken == null) {
                error.add("missing token");
            }
            
            
            if (webToken != null) {
                //check token
                try {
                    if (JWTUtility.verify(webToken, SHARED_SECRET) == null) {
                        error.add("invalid token");
                    }
                } catch (JWTException e) {
                    error.add("invalid token");
                }
            }
            
            
            if (error.size() > 0) {
                jsonOutput.addProperty("status", "error");
                jsonOutput.add("messages", error);
                String output = gson.toJson(jsonOutput);
                out.println(output);
                return;
            }
            
            //check date
            dateTime = TimeStampValidator.validateJsonTimeStamp(dateTime);
            if (dateTime == null) {
                error.add("invalid date");
            }
            
            
            //check k
            if (k == null || k.isEmpty()) {
                kNum = 3;
            } else {
                try {
                    kNum = Integer.parseInt(k);
                    if (kNum < 1 || kNum > 10) {
                        error.add("invalid k");
                    }
                } catch (NumberFormatException e) {
                    error.add("invalid k");
                }
            }

            
            ArrayList<String> semanticPlaces = new ArrayList<>();
            try {
                semanticPlaces = LocationDAO.getAllSemanticPlace();
                if (!semanticPlaces.contains(origin)){
                    error.add("invalid origin");
                }
            } catch (SQLException ex) {
                error.add("invalid origin");
            }
            
            if (error.size() > 0) {
                jsonOutput.addProperty("status", "error");
                jsonOutput.add("messages", error);
                String output = gson.toJson(jsonOutput);
                out.println(output);
                return;
            }
            
            ArrayList<Location> chosenList = TopKNextPlacesController.getLocations(dateTime, origin);
            TreeMap<Integer, ArrayList<String>> finalList = TopKNextPlacesController.getCompareList(dateTime, origin);
                        
            Integer[] mapKeys = finalList.keySet().toArray(new Integer[finalList.size()]);//put all the keyset into array
            Integer numNextUsers = 0;
                    
            for (int i=0; i < mapKeys.length; i++) {
                Integer count = mapKeys[i];
                if(i < kNum){
                    JsonObject successOutput = new JsonObject();
                    successOutput.addProperty("rank", (i+1));
                    ArrayList<String> rowList = finalList.get(count);//get the value from the key
                    String outLocation = rowList.get(0);
                    numNextUsers += count;
                    for (int row = 1; row < rowList.size(); row++) { //check if there is more than one semantic place
                        numNextUsers+=count;
                        outLocation += "," + rowList.get(row);
                    }
                    successOutput.addProperty("semantic-place", outLocation);
                    successOutput.addProperty("count", count);
                    successMessages.add(successOutput);
                }
            }
            
            jsonOutput.addProperty("status","success");
            jsonOutput.addProperty("total-users", chosenList.size());
            jsonOutput.addProperty("total-next-place-users", numNextUsers);
            jsonOutput.add("results",successMessages);
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
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

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
