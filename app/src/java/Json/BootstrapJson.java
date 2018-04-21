/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Json;

import Controller.UploadCtrl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entity.BootstrapError;
import entity.BootstrapStat;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Bootstraps application and prints output in JSON format
 *
 * @author Daryln
 */
@WebServlet(name = "BootstrapJson", urlPatterns = {"/json/bootstrap"})
@MultipartConfig
public class BootstrapJson extends HttpServlet {

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
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject jsonOutput = new JsonObject();
            JsonArray correctNumForFiles = new JsonArray();
            JsonArray error = new JsonArray();
            
            
            String webToken = request.getParameter("token");
            
            
            if (webToken != null) {
                try {
                    String username = JWTUtility.verify(webToken, SHARED_SECRET);
                    if (username == null) {
                        error.add("invalid token");
                    }
                } catch (Exception e) {
                    error.add("invalid token");
                }
            }
            
            
            Part filePart = request.getPart("bootstrap-file");
            
            if (filePart == null) {
                error.add("missing file");
            }
            
            if (webToken == null) {
                error.add("missing token");
            }
            
            
            if (error.size() > 0) {
                jsonOutput.addProperty("status", "error");
                jsonOutput.add("messages", error);
                String output = gson.toJson(jsonOutput);
                out.println(output);
                return;
            }
            
            
            try {
                ArrayList<BootstrapStat> results = UploadCtrl.uploadFiles(filePart);
                if (results != null) {
                    ArrayList<BootstrapError> allErrors = new ArrayList<>();
                    for (BootstrapStat stat : results) {
                        JsonObject fileRecords = new JsonObject();
                        fileRecords.addProperty(stat.getFileName(), stat.getNumRecords());
                        correctNumForFiles.add(fileRecords);
                        allErrors.addAll(stat.getErrors());
                    }
                    
                    
                    if (allErrors.isEmpty()) {
                        jsonOutput.addProperty("status", "success");
                        jsonOutput.add("num-record-loaded", correctNumForFiles);
                        String output = gson.toJson(jsonOutput);
                        out.println(output);
                        return;
                    }
                    
                    for (BootstrapError uploadError : allErrors) {
                        JsonObject jsonError = new JsonObject();
                        
                        ArrayList<String> errorMsg = uploadError.getErrorMsg();
                        if (errorMsg.size() > 1) {
                            Collections.sort(errorMsg);
                        }
                        
                        JsonArray messageArr = new JsonArray();
                        for (String line : errorMsg) {
                            messageArr.add(line);
                        }
                        
                        jsonError.addProperty("file", uploadError.getFileName());
                        jsonError.addProperty("line", uploadError.getLine());
                        jsonError.add("messages", messageArr);
                        
                        error.add(jsonError);
                    }
                    
                    jsonOutput.addProperty("status", "error");
                    jsonOutput.add("num-record-loaded", correctNumForFiles);
                    jsonOutput.add("error", error);
                    String output = gson.toJson(jsonOutput);
                    out.println(output);
                } else {
                    error.add("invalid file");
                    jsonOutput.addProperty("status", "error");
                    jsonOutput.add("messages", error);
                    String output = gson.toJson(jsonOutput);
                    out.println(output);
                    return;
                }
            } catch (Exception e) {
                out.println(e.getMessage());
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
