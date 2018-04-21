/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Json;

import Controller.GroupDetectionController;
import DAO.UserDAO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import entity.AutoGroupMemberComparator;
import entity.Companion;
import entity.Group;
import entity.GroupComparator;
import entity.TimeStampValidator;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Prints output of Automatic Group Detection functionality in JSON format
 *
 * @author Daryln
 */
@WebServlet(name = "AutoGroupDetectionJson", urlPatterns = {"/json/group_detect"})
public class AutoGroupDetectionJson extends HttpServlet {

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
            JsonArray error = new JsonArray();
            
            String dateTime = request.getParameter("date");
            String webToken = request.getParameter("token");
            
            if (dateTime == null || dateTime.isEmpty()) {
                error.add("blank date");
            }

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
            
            dateTime = TimeStampValidator.validateJsonTimeStamp(dateTime);

            if (dateTime == null) {
                error.add("invalid date");
            }
            
            
            if (error.size() > 0) {
                jsonOutput.addProperty("status", "error");
                jsonOutput.add("messages", error);
                String output = gson.toJson(jsonOutput);
                out.println(output);
                return;
            }
            
            
            try {
                ArrayList<Companion> unfilteredPeople = GroupDetectionController.getPeople(dateTime);
                ArrayList<Group> groups = GroupDetectionController.firstRound(unfilteredPeople);
                ArrayList<Group> additionalGroups = GroupDetectionController.secondRound(unfilteredPeople);
                groups.addAll(additionalGroups);
                ArrayList<Group> filteredGroups = GroupDetectionController.filterGroups(groups);
                for (Group group : filteredGroups) {
                    ArrayList<Companion> people = group.getGroup();
                    Collections.sort(people, new AutoGroupMemberComparator());
                }
                
                Collections.sort(filteredGroups, new GroupComparator());
                int totalNumUsers = UserDAO.retrieveAllUsers(dateTime).size();
                int totalGroups = filteredGroups.size();
                JsonArray groupsDetails = new JsonArray();
                for (Group group : filteredGroups) {
                    ArrayList<Companion> groupMembers = group.getGroup();
                    Collections.sort(groupMembers, new AutoGroupMemberComparator());
                    JsonArray members = new JsonArray();
                    JsonArray locations = new JsonArray();
                    JsonObject singleGroupDetails = new JsonObject();
                    
                    for (Companion companion : groupMembers) {
                        JsonObject memberDetails = new JsonObject();
                        String email = companion.getEmail();
                        if (email == null) {
                            email = "";
                        }
                        String macaddress = companion.getMacAddress();
                        memberDetails.addProperty("email", email);
                        memberDetails.addProperty("mac-address", macaddress);
                        members.add(memberDetails);
                    }
                    
                    HashMap<String,Integer> locationAndTime = group.getLocationAndTime();
                    TreeMap<String,Integer> orderedLocationAndTime = new TreeMap<>(locationAndTime);
                    for (Map.Entry<String,Integer> entry : orderedLocationAndTime.entrySet()) {
                        JsonObject location = new JsonObject();
                        String locationId = entry.getKey();
                        int time = entry.getValue();
                        
                        location.addProperty("location", locationId);
                        location.addProperty("time-spent", time);
                        locations.add(location);
                    }
                    
                    singleGroupDetails.addProperty("size", group.size());
                    singleGroupDetails.addProperty("total-time-spent", group.getTotalTime());
                    singleGroupDetails.add("members", members);
                    singleGroupDetails.add("locations", locations);
                    groupsDetails.add(singleGroupDetails);
                }
                
                
                jsonOutput.addProperty("status", "success");
                jsonOutput.addProperty("total-users", totalNumUsers);
                jsonOutput.addProperty("total-groups", totalGroups);
                jsonOutput.add("groups", groupsDetails);
                out.println(gson.toJson(jsonOutput));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
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
