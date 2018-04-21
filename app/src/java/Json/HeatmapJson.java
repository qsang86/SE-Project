/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Json;

import Controller.HeatmapCtrl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import entity.Location;
import entity.LocationComparator;
import entity.TimeStampValidator;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Prints heatmap functionality in JSON format
 *
 * @author Yong Qing
 */

@WebServlet(name = "HeatmapJson", urlPatterns = {"/json/heatmap"})
public class HeatmapJson extends HttpServlet {

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
            /* TODO output your page here. You may use following sample code. */

            // create JsonObject
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject jsonOutput = new JsonObject();
            JsonArray successMessages = new JsonArray();
            JsonArray error = new JsonArray();

            String dateTime = request.getParameter("date");
            String floorLevel = request.getParameter("floor");

            String webToken = request.getParameter("token");

            if (dateTime == null || dateTime.isEmpty()) {
                error.add("blank date");
            }

            if (floorLevel == null || floorLevel.isEmpty()) {
                error.add("blank floor");
            }
            
            if (webToken == null) {
                error.add("missing token");
            }
            
            if (webToken != null) {
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

            
            dateTime = TimeStampValidator.validateJsonTimeStamp(dateTime);
            
            if (dateTime == null) {
                error.add("invalid date");
            }
            
            try {
                int level = Integer.parseInt(floorLevel);
                if (level < 0 || level > 5) {
                    error.add("invalid floor");
                }
            } catch (NumberFormatException e) {
                error.add("invalid floor");
            }
            
            if (error.size() > 0) {
                jsonOutput.addProperty("status", "error");
                jsonOutput.add("messages", error);
                String output = gson.toJson(jsonOutput);
                out.println(output);
                return;
            }
            
            
            
            ArrayList<Location> heatmap = HeatmapCtrl.getNormHeatmap(dateTime);
            ArrayList<Location> specificHeatmap = new ArrayList<>();

            int floorNum = Integer.parseInt(floorLevel);

            for (Location location : heatmap) {
                if (location.getFloor() == floorNum) {
                    specificHeatmap.add(location);
                }
            }
            
            Collections.sort(specificHeatmap, new LocationComparator());
            
            for (Location location : specificHeatmap) {
                //print out no result if there are no people at the floor

                //print out the density and the number of people

                String semanticPlace = location.getSemanticPlace();
                int numPeople = location.getNumberOfPeople();
                int crowdDensity = location.getDensity(numPeople);
                JsonObject successOutput = new JsonObject();

                successOutput.addProperty("semantic-place",semanticPlace);
                successOutput.addProperty("num-people",numPeople);
                successOutput.addProperty("crowd-density",crowdDensity);

                successMessages.add(successOutput);
            }
            
            jsonOutput.addProperty("status","success");
            jsonOutput.add("heatmap",successMessages);
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
        // processRequest(request, response);
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
