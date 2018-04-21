/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import entity.BlankFieldComparator;
import entity.BootstrapError;
import entity.BootstrapStat;
import entity.Validator;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Controls validation and inserting of data of demographics.csv
 *
 * @author Daryln
 * 
 */
public class DemographicsController {
    
    /**
     *
     * @param strList ArrayList of String[] containing all the data in demographics.csv
     * @return {@link BootstrapStat}
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static BootstrapStat controlDemographics(ArrayList<String[]> strList) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        
        if (strList==null || strList.isEmpty()) {
            return null;
        }

        BootstrapStat demoStat = new BootstrapStat();
        demoStat.setFileName("demographics.csv");
        HashMap<String,Integer> headerMap = assignHeaders(strList.get(0));
        
        // get position of headers 
        int macPos = headerMap.get("mac-address");
        int namePos = headerMap.get("name");
        int passwordPos = headerMap.get("password");
        int emailPos = headerMap.get("email");
        int genderPos = headerMap.get("gender");
        
        // loop through demographic records to check whether they are erranous or not 
        for (int i = 1; i<strList.size(); i++) {
            String[] strRecord = strList.get(i);

            String macAddress = strRecord[macPos].trim();
            String name = strRecord[namePos].trim();
            String password = strRecord[passwordPos].trim();
            String email = strRecord[emailPos].trim();
            String strGender = strRecord[genderPos].trim();

            
            ArrayList<String> errorMessages = new ArrayList<>();
            // check for blank data
            ArrayList<String> blankFieldMessages = Validator.checkBlankDemographics(macAddress, name, email, password, strGender);
            if (blankFieldMessages.isEmpty()) {
                // if no error, check whether user is valid 
                errorMessages = Validator.validateUser(macAddress, name, email, password, strGender);
            } else {
                // if there are errors, sort and add to errorMessages
                Collections.sort(blankFieldMessages, new BlankFieldComparator(headerMap));
                errorMessages.addAll(blankFieldMessages);
            }
            
            // add into demoStat to return
            if (errorMessages.isEmpty()) {
                demoStat.addRows(new String[] {macAddress, name, password, email, strGender});
            } else {
                demoStat.addError(new BootstrapError("demographics.csv", i+1, errorMessages));
            }
        }
        
        demoStat.refreshNumRecords();
        
        return demoStat;
    }
    
    
    
    /**
     * 
     * @param headers headers of the fields of demographics.csv
     * @return HashMap&lt;String,Integer&gt;
     * <br> Key - Header name
     * <br> Value - Position of header
     */
    private static HashMap<String,Integer> assignHeaders(String[] headers) {
        HashMap<String, Integer> headerMap = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            headerMap.put(headers[i].trim(), i);
        }
        
        return headerMap; 
    }
}
