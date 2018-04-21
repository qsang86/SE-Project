/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import Controller.TopKNextPlacesController;
import DAO.LocationDAO;
import entity.*;
import java.util.TreeMap;
import java.util.ArrayList;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;

/**
 * Returns the Top k Next places 
 *
 * @author Daryln
 */
@WebServlet("/TopKNextPlacesServlet")
public class TopKNextPlacesServlet extends HttpServlet {

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

        try {
            //validation
            String dateTime = request.getParameter("date");
            dateTime = TimeStampValidator.validateJsonTimeStamp(dateTime);
            
            if (dateTime == null) {
                request.setAttribute("errorMsg", "Invalid Date");
                RequestDispatcher view = request.getRequestDispatcher("topknextplaces.jsp");
                view.forward(request, response);
                return;
            }
            
            String kValue = request.getParameter("k");
            int kvalue;
            try {
                kvalue = Integer.parseInt(kValue);
            } catch (Exception e) {
                request.setAttribute("errorMsg", "invalid k");
                RequestDispatcher view = request.getRequestDispatcher("topknextplaces.jsp");
                view.forward(request, response);
                return;
            }
            
            String semanticPlace = request.getParameter("origin");
            
            ArrayList<String> semanticPlaces = new ArrayList<>();
            
            //check if location exist (for json)
            semanticPlaces = LocationDAO.getAllSemanticPlace();
            if (!semanticPlaces.contains(semanticPlace)){
                request.setAttribute("errorMsg", "invalid origin");
                RequestDispatcher view = request.getRequestDispatcher("topknextplaces.jsp");
                view.forward(request, response);
                return;
            }
            
            
            ArrayList<Location> chosenList = TopKNextPlacesController.getLocations(dateTime, semanticPlace);
            
            TreeMap<Integer, ArrayList<String>> finalList = TopKNextPlacesController.getCompareList(dateTime, semanticPlace);

            if (finalList.size() > 0) {

                int numNextUsers = 0;
                for (int num : finalList.keySet()){
                    for (String place : finalList.get(num)){
                        numNextUsers += num;
                    }
                }
                request.setAttribute("topknextplaceresult", finalList);
                request.setAttribute("k", kvalue);
                request.setAttribute("previousUsers", chosenList.size());
                request.setAttribute("nextUsers", numNextUsers);
                String backSelection = "<script>document.getElementById(\"dateTimeId\").value = \""
                    + dateTime.replace(" ", "T") + "\"\n"
                    + "document.getElementById(\"semanticId\").value = \""
                    + semanticPlace + "\"\n"
                    + "document.getElementById(\"kId\").value = \"" 
                    + kValue + "\"</script>";
                request.setAttribute("returnSelection", backSelection);
                RequestDispatcher view = request.getRequestDispatcher("topknextplaces.jsp");
                view.forward(request, response);
            } else {
                request.setAttribute("noresult", "no result");
                request.setAttribute("k", kvalue);
                RequestDispatcher view = request.getRequestDispatcher("topknextplaces.jsp");
                view.forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("errorMsg", "Invalid input");
            RequestDispatcher view = request.getRequestDispatcher("topknextplaces.jsp");
            view.forward(request, response);
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
