/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import Controller.HeatmapCtrl;
import entity.Location;
import entity.LocationComparator;
import entity.TimeStampValidator;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.Collections;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * returns the heatmap at the specified timestamp
 *
 * @author Daryln
 */
@WebServlet(name = "HeatmapServlet", urlPatterns = {"/HeatmapServlet"})
public class HeatmapServlet extends HttpServlet {

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
        try{
            String dateTime = request.getParameter("dateTime");
            String floorLevel = request.getParameter("floor");
            
            // for valid inputs only
            if (dateTime != null && floorLevel != null) {
                dateTime = TimeStampValidator.validateJsonTimeStamp(dateTime);
                
                // retrieve heatmap from control
                ArrayList<Location> heatmap = HeatmapCtrl.getNormHeatmap(dateTime);
                
                // to store filtered locations for heatmap
                ArrayList<Location> specificHeatmap = new ArrayList<>();
               
                
                int floorNum = Integer.parseInt(floorLevel);
                
                // filter and get locations on the selected floor 
                for(Location location : heatmap){
                    if(location.getFloor() == floorNum){
                        specificHeatmap.add(location);
                    }
                }
                
                // sort locations in heatmap by semantic place
                Collections.sort(specificHeatmap, new LocationComparator());
                
                request.setAttribute("dateTime", dateTime);
                request.setAttribute("floorLevel", floorNum);
                request.setAttribute("specificHeatmap", specificHeatmap);
                RequestDispatcher view = request.getRequestDispatcher("heatmap.jsp");
                view.forward(request, response);
            }
            else{
                request.setAttribute("specificHeatmap", "Invalid Input");
                RequestDispatcher view = request.getRequestDispatcher("heatmap.jsp");
                view.forward(request, response);
            }
        } catch (Exception ex){
            response.sendRedirect("heatmap.jsp");
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
        doGet(request, response);
//        processRequest(request, response);
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
