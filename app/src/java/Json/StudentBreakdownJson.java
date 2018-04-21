/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Json;

import Controller.BreakdownController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import entity.BreakdownOrder;
import entity.TimeStampValidator;
import entity.User;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Prints breakdown of order in JSON format
 *
 * @author Keng Yew
 */
@WebServlet(name = "StudentBreakdownJson", urlPatterns = {"/json/basic-loc-report"})
public class StudentBreakdownJson extends HttpServlet {

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
            JsonArray secondArray = new JsonArray();
            JsonArray thirdArray = new JsonArray();
            JsonObject firstOutput = new JsonObject();
            JsonObject secondOutput = new JsonObject();
            JsonObject thirdOutput = new JsonObject();

            JsonArray error = new JsonArray();

            String dateTime = request.getParameter("date");
            String orderStr = request.getParameter("order");

            String webToken = request.getParameter("token");

            if (dateTime == null || dateTime.isEmpty()) {
                error.add("blank date");
            }

            if (orderStr == null || orderStr.isEmpty()) {
                error.add("missing order");
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
            
            String checkDate = TimeStampValidator.validateJsonTimeStamp(dateTime);

            if (checkDate == null) {
                error.add("invalid date");
            }

            String[] sortLs = orderStr.split(",");
            ArrayList<String> sorting = new ArrayList<>(Arrays.asList(sortLs));

            boolean checkOrder = true;

            if (sorting.size() > 3 || sorting.isEmpty()) {
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

            if (countSchool > 1 || countYear > 1 || countGender > 1) {
                checkOrder = false;
            }

            if (!checkOrder) {
                error.add("invalid order");
            }


            
            if (error.size() > 0) {
                jsonOutput.addProperty("status", "error");
                jsonOutput.add("messages", error);
                String output = gson.toJson(jsonOutput);
                out.println(output);
                return;
            }
            
            
            //direct to controller to get values
            int countOptions = 1;
            ArrayList<User> userList = BreakdownController.getUserList(dateTime);

            BreakdownOrder breakdownOrder = new BreakdownOrder();
            for (String option : sorting) {

                if (option.equalsIgnoreCase("school")) { //school
                    //breakdown by school
                    if (countOptions == 1) {
                        TreeMap<String, ArrayList<User>> schoolBreakdown = BreakdownController.getSchoolBreakdown(userList);
                        breakdownOrder.setFirst(schoolBreakdown);
                    } else if (countOptions == 2) {
                        TreeMap<String, ArrayList<User>> first = breakdownOrder.getFirst();
                        for (Map.Entry<String, ArrayList<User>> entry : first.entrySet()) {
                            String firstOptionName = entry.getKey();
                            TreeMap<String, ArrayList<User>> schoolBreakdown = BreakdownController.getSchoolBreakdown(entry.getValue());
                            breakdownOrder.setSecond(firstOptionName, schoolBreakdown);
                        }
                    } else { //countOptions==3
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
                    if (countOptions == 1) {
                        TreeMap<String, ArrayList<User>> genderBreakdown = BreakdownController.getGenderBreakdown(userList);
                        breakdownOrder.setFirst(genderBreakdown);
                    } else if (countOptions == 2) {
                        TreeMap<String, ArrayList<User>> first = breakdownOrder.getFirst();
                        //TreeMap<String,TreeMap<String,ArrayList<User>>> second = breakdownOrder.getSecond();
                        for (Map.Entry<String, ArrayList<User>> entry : first.entrySet()) {
                            String firstOptionName = entry.getKey();
                            TreeMap<String, ArrayList<User>> genderBreakdown = BreakdownController.getGenderBreakdown(entry.getValue());
                            breakdownOrder.setSecond(firstOptionName, genderBreakdown);
                        }
                    } else { //countOptions==3
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
                    if (countOptions == 1) {
                        TreeMap<String, ArrayList<User>> yearBreakdown = BreakdownController.getYearBreakdown(userList);
                        breakdownOrder.setFirst(yearBreakdown);
                    } else if (countOptions == 2) {
                        TreeMap<String, ArrayList<User>> first = breakdownOrder.getFirst();
                        //TreeMap<String,TreeMap<String,ArrayList<User>>> second = breakdownOrder.getSecond();
                        for (Map.Entry<String, ArrayList<User>> entry : first.entrySet()) {
                            String firstOptionName = entry.getKey();
                            TreeMap<String, ArrayList<User>> yearBreakdown = BreakdownController.getYearBreakdown(entry.getValue());
                            breakdownOrder.setSecond(firstOptionName, yearBreakdown);
                        }
                    } else { //countOptions==3
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

//================================================================================================   

            TreeMap<String, ArrayList<User>> firstBreakdown = breakdownOrder.getFirst();
            TreeMap<String, TreeMap<String, ArrayList<User>>> secondBreakdown = breakdownOrder.getSecond();
            TreeMap<String, TreeMap<String, ArrayList<User>>> thirdBreakdown = breakdownOrder.getThird();
            
            
            for (Map.Entry<String, ArrayList<User>> entry : firstBreakdown.entrySet()) {
                String firstOptionName = entry.getKey();

                int firstBreakdownSize = entry.getValue().size();
                
                try {
                    int optionIfNum = Integer.parseInt(firstOptionName);
                    firstOutput.addProperty(sorting.get(0), optionIfNum);
                } catch (NumberFormatException e) {
                    firstOutput.addProperty(sorting.get(0), firstOptionName);
                }
                
                firstOutput.addProperty("count", firstBreakdownSize);
                
                TreeMap<String, ArrayList<User>> secondOption = secondBreakdown.get(firstOptionName);
                if (secondOption != null) {
                    
                    for (Map.Entry<String, ArrayList<User>> secondEntry : secondOption.entrySet()) {
                        String secondOptionName = secondEntry.getKey();

                        int secondBreakdownSize = secondEntry.getValue().size();

                        try {
                            int optionIfNum = Integer.parseInt(secondOptionName);
                            secondOutput.addProperty(sorting.get(1), optionIfNum);
                        } catch (NumberFormatException e) {
                            secondOutput.addProperty(sorting.get(1), secondOptionName);
                        }
                        
                        secondOutput.addProperty("count", secondBreakdownSize);

                        TreeMap<String, ArrayList<User>> thirdOption = thirdBreakdown.get(firstOptionName + secondOptionName);
                        if (thirdOption != null) {
                            
                            for (Map.Entry<String, ArrayList<User>> thirdEntry : thirdOption.entrySet()) {
                                String thirdOptionName = thirdEntry.getKey();

                                int thirdBreakdownSize = thirdEntry.getValue().size();

                                try {
                                    int optionIfNum = Integer.parseInt(thirdOptionName);
                                    thirdOutput.addProperty(sorting.get(2), optionIfNum);
                                } catch (NumberFormatException e) {
                                    thirdOutput.addProperty(sorting.get(2), thirdOptionName);
                                }
                                
                                thirdOutput.addProperty("count", thirdBreakdownSize);

                                thirdArray.add(thirdOutput);
                                thirdOutput = new JsonObject();
                            }
                        }

                        if (thirdArray.size() > 0) {
                            secondOutput.add("breakdown", thirdArray);
                        }

                        secondArray.add(secondOutput);
                        secondOutput = new JsonObject();
                        thirdArray = new JsonArray();
                    }
                }

                if (secondArray.size() > 0) {
                    firstOutput.add("breakdown", secondArray);
                }

                successMessages.add(firstOutput);
                secondArray = new JsonArray();
                firstOutput = new JsonObject();
            }

            jsonOutput.addProperty("status", "success");
            jsonOutput.add("breakdown", successMessages);
            String output = gson.toJson(jsonOutput);
            out.println(output);
//======================================================================================================
            
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
