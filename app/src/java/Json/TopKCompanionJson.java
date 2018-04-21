package Json;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Controller.CompanionController;
import DAO.UserDAO;
import DAO.UserLocationDAO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entity.Companion;
import entity.TimeStampValidator;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Prints Top K Companions in JSON format
 *
 * @author Daryln
 */
@WebServlet(name = "TopKCompanionJson", urlPatterns = {"/json/top-k-companions"})
public class TopKCompanionJson extends HttpServlet {

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
            String k = request.getParameter("k");
            int kNum = 3;
            String webToken = request.getParameter("token");
            String macaddress = request.getParameter("mac-address");
            
            if (dateTime == null || dateTime.isEmpty()) {
                error.add("blank date");
            }

            if (macaddress == null || macaddress.isEmpty()) {
                error.add("blank mac-address");
            }
            
            //check validity of token
            if (webToken == null) {
                error.add("missing token");
            } else {
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
            
            
            
            //check validity of date
            dateTime = TimeStampValidator.validateJsonTimeStamp(dateTime);
            
            if (dateTime == null) {
                error.add("invalid date");
            }
            
            
            //check validity of k
            if (k == null || k.isEmpty()) {
                kNum = 3;
            } else {
                try {
                    kNum = Integer.parseInt(k);
                    if (kNum > 10 || kNum < 1) {
                        error.add("invalid k");
                    }
                } catch (NumberFormatException e) {
                    error.add("invalid k");
                }
            }
            
            
            //check validity of mac-address
            try {
                Companion isValid = UserLocationDAO.getSpecifiedUser(dateTime, macaddress);
                if (isValid == null) {
                    isValid = UserLocationDAO.getSingleRowSpecifiedUser(dateTime, macaddress);
                    if (isValid == null) {
                        error.add("invalid mac-address");
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            
            
            if (error.size() > 0) {
                jsonOutput.addProperty("status", "error");
                jsonOutput.add("messages", error);
                String output = gson.toJson(jsonOutput);
                out.println(output);
                return;
            }
            
            
            try {
                HashMap<Integer,ArrayList<Companion>> rankingOfCompanion = CompanionController.controlCompanions(kNum, dateTime, macaddress);
                for (int i=1; i<=kNum; i++) {
                    ArrayList<Companion> companionsWithinRank = rankingOfCompanion.get(i);
                    JsonObject successOutput = new JsonObject();
                    successOutput.addProperty("rank", i);
                    int seconds = 0;

                    for (Companion companion : companionsWithinRank) {
                        String email = companion.getEmail();
                        if (email == null) {
                            email = "";
                        }
                        String companionMacAddress = companion.getMacAddress();
                        seconds = companion.getTotalDuration();
                        successOutput.addProperty("companion",email);
                        successOutput.addProperty("mac-address", companionMacAddress);
                    }

                    successOutput.addProperty("time-together",seconds);
                    successMessages.add(successOutput);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            
            
            jsonOutput.addProperty("status","success");
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
