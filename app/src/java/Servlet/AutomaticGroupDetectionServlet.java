/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import Controller.GroupDetectionController;
import DAO.UserDAO;
import entity.AutoGroupMemberComparator;
import entity.Companion;
import entity.Group;
import entity.GroupComparator;
import entity.TimeStampValidator;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
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
@WebServlet(name = "AutomaticGroupDetectionServlet", urlPatterns = {"/AutomaticGroupDetectionServlet"})
public class AutomaticGroupDetectionServlet extends HttpServlet {

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
            String dateTime = request.getParameter("dateTime");
            dateTime = TimeStampValidator.validateJsonTimeStamp(dateTime);

            try {
                ArrayList<Companion> unfilteredPeople = GroupDetectionController.getPeople(dateTime);   //get all users in location.csv who spent at least 12min within the specified timestamp
                ArrayList<Group> groups = GroupDetectionController.firstRound(unfilteredPeople);    //iterate all users twice to form groups
                ArrayList<Group> additionalGroups = GroupDetectionController.secondRound(unfilteredPeople); // iterate one user to the next user for any other separate pairings
                groups.addAll(additionalGroups);
                Collections.sort(groups, new GroupComparator());
                ArrayList<Group> filteredGroups = GroupDetectionController.filterGroups(groups); //clear all duplicate groups or subgroups
                
                Collections.sort(filteredGroups, new GroupComparator());
                ArrayList<User> users = UserDAO.retrieveAllUsers(dateTime); //get total number of users in SIS building
                int totalNumUsers = users.size();
                request.setAttribute("numUsers", totalNumUsers);
                request.setAttribute("groupdetect", filteredGroups);
                RequestDispatcher view = request.getRequestDispatcher("groupdetection.jsp");
                view.forward(request, response);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        } catch (Exception e) {
            request.setAttribute("error", "Wrong input");
            RequestDispatcher view = request.getRequestDispatcher("groupdetection.jsp");
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
