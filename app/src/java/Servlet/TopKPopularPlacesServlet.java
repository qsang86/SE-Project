/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import Controller.HeatmapCtrl;
import entity.Location;
import entity.TimeStampValidator;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Returns the Top K Popular places
 *
 * @author Daryln
 */
public class TopKPopularPlacesServlet extends HttpServlet {

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
            String dateTime = (String)request.getParameter("dateTime");
            int topNumber = Integer.valueOf(request.getParameter("kPopular"));
            
            // return "Invalid Input" for invalid values of datetime and rank number 
            if (dateTime == null || dateTime.isEmpty() || topNumber>10 || topNumber<1) {
                request.setAttribute("TopKPopularResult", "Invalid Input");
                RequestDispatcher view = request.getRequestDispatcher("topkpopularplaces.jsp");
                view.forward(request, response);
                return;
            }
            String correctDate = TimeStampValidator.validateJsonTimeStamp(dateTime);
            
            // get heatmap (num of people in each location in the building at given datetime)
            ArrayList<Location> locationList = HeatmapCtrl.getNormHeatmap(correctDate);
            
            // retrieve top k popular entries 
            HashMap<Integer,ArrayList<Location>> rankingOfLocation = HeatmapCtrl.rankingOfTopKPop(locationList, topNumber);
            request.setAttribute("dateTime", dateTime);
            request.setAttribute("kNum", topNumber);
            request.setAttribute("TopKPopularResult", rankingOfLocation);
            RequestDispatcher view = request.getRequestDispatcher("topkpopularplaces.jsp");
            view.forward(request, response);
        } catch (Exception ex){
            response.sendRedirect("topkpopularplaces.jsp");
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
