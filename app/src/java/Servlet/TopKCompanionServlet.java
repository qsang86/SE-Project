/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import Controller.CompanionController;
import entity.Companion;
import entity.TimeStampValidator;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Daryln
 */
@WebServlet(name = "TopKCompanionServlet", urlPatterns = {"/TopKCompanionServlet"})
public class TopKCompanionServlet extends HttpServlet {

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
            String dateTime = request.getParameter("dateTime");
            dateTime = TimeStampValidator.validateJsonTimeStamp(dateTime);
            
            if (dateTime == null) {
                request.setAttribute("error", "Invalid Date");
                RequestDispatcher view = request.getRequestDispatcher("topkcompanions.jsp");
                view.forward(request, response);
                return;
            }
            String macaddress = request.getParameter("macaddress");
            String k = request.getParameter("kCompanions");
            int kNum = Integer.parseInt(k);

            try {
                //get hashmap of companions sorted by rank
                HashMap<Integer,ArrayList<Companion>> companionMap = CompanionController.controlCompanions(kNum, dateTime, macaddress);
                request.setAttribute("topkcompanions", companionMap);
                RequestDispatcher view = request.getRequestDispatcher("topkcompanions.jsp");
                view.forward(request, response);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        } catch (Exception e) {
            request.setAttribute("error", "Wrong input");
            RequestDispatcher view = request.getRequestDispatcher("topkcompanions.jsp");
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
