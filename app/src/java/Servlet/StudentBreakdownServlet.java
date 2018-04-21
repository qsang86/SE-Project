/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import Controller.BreakdownController;
import entity.BreakdownOrder;
import DAO.UserDAO;
import entity.TimeStampValidator;
import entity.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * returns the school,year and/or gender breakdown
 *
 * @author Daryln
 */
@WebServlet(name = "StudentBreakdownServlet", urlPatterns = {"/StudentBreakdownServlet"})
public class StudentBreakdownServlet extends HttpServlet {

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
            String orderStr = request.getParameter("order");
            //check if timestamp is valid
            String checkDate = TimeStampValidator.validateJsonTimeStamp(dateTime);
            
            if (checkDate == null) {
                request.setAttribute("ErrMsg", "invalid date");
                RequestDispatcher view = request.getRequestDispatcher("studentbreakdown.jsp");
                view.forward(request, response);
                return;
            }
            
            //the order is a concatenated string delimited by ","
            String[] sortLs = orderStr.split(",");
            ArrayList<String> sorting = new ArrayList<>(Arrays.asList(sortLs));
            
            boolean checkOrder = true;
            
            if (sorting.size()>3 || sorting.isEmpty()) {
                checkOrder = false;
            }
            
            int countSchool = 0;
            int countGender = 0;
            int countYear = 0;
            
            for (String option : sorting) {
                if (!(option.equals("school") || option.equals("gender") || option.equals("year"))) {
                    checkOrder = false;
                    break;
                }
                
                if (option.equals("school")) {
                    countSchool++;
                } else if (option.equals("gender")) {
                    countGender++;
                } else {
                    countYear++;
                }
            }
            
            //check if user entered an option more than once in the order
            //eg: invalid order --> school,school,gender
            if (countSchool>1 || countYear>1 || countGender>1) {
                checkOrder = false;
            }
            
            if (!checkOrder) {
                request.setAttribute("ErrMsg", "invalid order");
                RequestDispatcher view = request.getRequestDispatcher("studentbreakdown.jsp");
                view.forward(request, response);
                return;
            }
            
            //direct to controller to get values
            int countOptions = 1;   //setting which option to be broken down (gender, year or school)
            ArrayList<User> userList = BreakdownController.getUserList(dateTime);

            //BreakdownOrder is a class to hold all the different breakdowns
            BreakdownOrder breakdownOrder = new BreakdownOrder();
            for (String option : sorting) {

                if (option.equalsIgnoreCase("school")) { //school
                    //breakdown by school
                    if (countOptions == 1) {    //if school is the first option
                        TreeMap<String, ArrayList<User>> schoolBreakdown = BreakdownController.getSchoolBreakdown(userList);
                        breakdownOrder.setFirst(schoolBreakdown);
                    } else if (countOptions == 2) {     //if school is the second option
                        TreeMap<String, ArrayList<User>> first = breakdownOrder.getFirst();
                        for (Map.Entry<String, ArrayList<User>> entry : first.entrySet()) {
                            String firstOptionName = entry.getKey();
                            TreeMap<String, ArrayList<User>> schoolBreakdown = BreakdownController.getSchoolBreakdown(entry.getValue());
                            breakdownOrder.setSecond(firstOptionName, schoolBreakdown);
                        }
                    } else { //countOptions==3 --> if school is the third option
                        TreeMap<String, TreeMap<String, ArrayList<User>>> second = breakdownOrder.getSecond();
                        for (Map.Entry<String, TreeMap<String, ArrayList<User>>> entry : second.entrySet()) {
                            String firstOptionName = entry.getKey();
                            TreeMap<String, ArrayList<User>> secondOptions = entry.getValue();
                            for (Map.Entry<String, ArrayList<User>> secondEntry : secondOptions.entrySet()) {
                                String secondOptionName = secondEntry.getKey();
                                TreeMap<String, ArrayList<User>> schoolBreakdown = BreakdownController.getSchoolBreakdown(secondEntry.getValue());
                                breakdownOrder.setThird(firstOptionName, secondOptionName, schoolBreakdown);
                            }
                        }
                    }
                } else if (option.equalsIgnoreCase("gender")) { //gender
                    //breakdown by gender
                    if (countOptions == 1) {    // if gender is the first option
                        TreeMap<String, ArrayList<User>> genderBreakdown = BreakdownController.getGenderBreakdown(userList);
                        breakdownOrder.setFirst(genderBreakdown);
                    } else if (countOptions == 2) { //if gender is the second option
                        TreeMap<String, ArrayList<User>> first = breakdownOrder.getFirst();
                        //TreeMap<String,TreeMap<String,ArrayList<User>>> second = breakdownOrder.getSecond();
                        for (Map.Entry<String, ArrayList<User>> entry : first.entrySet()) {
                            String firstOptionName = entry.getKey();
                            TreeMap<String, ArrayList<User>> genderBreakdown = BreakdownController.getGenderBreakdown(entry.getValue());
                            breakdownOrder.setSecond(firstOptionName, genderBreakdown);
                        }
                    } else { //countOptions==3 --> if gender is the third option
                        TreeMap<String, TreeMap<String, ArrayList<User>>> second = breakdownOrder.getSecond();
                        for (Map.Entry<String, TreeMap<String, ArrayList<User>>> entry : second.entrySet()) {
                            String firstOptionName = entry.getKey();
                            TreeMap<String, ArrayList<User>> secondOptions = entry.getValue();
                            for (Map.Entry<String, ArrayList<User>> secondEntry : secondOptions.entrySet()) {
                                String secondOptionName = secondEntry.getKey();
                                TreeMap<String, ArrayList<User>> genderBreakdown = BreakdownController.getGenderBreakdown(secondEntry.getValue());
                                breakdownOrder.setThird(firstOptionName, secondOptionName, genderBreakdown);
                            }
                        }
                    }
                } else if (option.equalsIgnoreCase("year")) { //year
                    //breakdown by year
                    if (countOptions == 1) {    // if year is the first option
                        TreeMap<String, ArrayList<User>> yearBreakdown = BreakdownController.getYearBreakdown(userList);
                        breakdownOrder.setFirst(yearBreakdown);
                    } else if (countOptions == 2) { // if year is the second option
                        TreeMap<String, ArrayList<User>> first = breakdownOrder.getFirst();
                        //TreeMap<String,TreeMap<String,ArrayList<User>>> second = breakdownOrder.getSecond();
                        for (Map.Entry<String, ArrayList<User>> entry : first.entrySet()) {
                            String firstOptionName = entry.getKey();
                            TreeMap<String, ArrayList<User>> yearBreakdown = BreakdownController.getYearBreakdown(entry.getValue());
                            breakdownOrder.setSecond(firstOptionName, yearBreakdown);
                        }
                    } else { //countOptions==3 --> if year is the third option
                        TreeMap<String, TreeMap<String, ArrayList<User>>> second = breakdownOrder.getSecond();
                        for (Map.Entry<String, TreeMap<String, ArrayList<User>>> entry : second.entrySet()) {
                            String firstOptionName = entry.getKey();
                            TreeMap<String, ArrayList<User>> secondOptions = entry.getValue();
                            for (Map.Entry<String, ArrayList<User>> secondEntry : secondOptions.entrySet()) {
                                String secondOptionName = secondEntry.getKey();
                                TreeMap<String, ArrayList<User>> yearBreakdown = BreakdownController.getYearBreakdown(secondEntry.getValue());
                                breakdownOrder.setThird(firstOptionName, secondOptionName, yearBreakdown);
                            }
                        }
                    }
                }

                countOptions++;
            }

            int userSize = userList.size();
//                    
            request.setAttribute("orderStr", orderStr);
            request.setAttribute("sorting", sorting);
            request.setAttribute("dateTime", dateTime);
            request.setAttribute("userSize", userSize);
            request.setAttribute("breakdown", breakdownOrder);
            RequestDispatcher view = request.getRequestDispatcher("studentbreakdown.jsp");
            view.forward(request, response);

        } catch (Exception ex) {

//            if (dateTime == null || sorting.isEmpty() || countSchool>1 || countGender>1 || countYear>1){
            request.setAttribute("ErrMsg", "invalid input");
            RequestDispatcher view = request.getRequestDispatcher("studentbreakdown.jsp");
            view.forward(request, response);
        }//return;
        //discuss about datetime validation later
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

