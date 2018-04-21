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
import java.util.*;

/**
 * Controls validation and inserting of data of location-lookup.csv
 *
 * @author Keng Yew
 * 
 */
public class LocationController {

    /**
     *
     * @param strList  ArrayList of String[] containing all the data in demographics.csv
     * @return {@link BootstrapStat}
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static BootstrapStat controlLocation(ArrayList<String[]> strList) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (strList==null || strList.isEmpty()) {
            return null;
        }
        
        BootstrapStat locationStat = new BootstrapStat();
        locationStat.setFileName("location-lookup.csv");
        HashMap<String,Integer> headerMap = assignHeaders(strList.get(0));
        
        int semanticPlacePos = headerMap.get("semantic-place");
        int locationIdPos = headerMap.get("location-id");
        
        for (int i = 1; i<strList.size(); i++) {
            String[] strRecord = strList.get(i);

            String semanticPlace = strRecord[semanticPlacePos];
            String locationId = strRecord[locationIdPos].trim();

            ArrayList<String> errorMessages = new ArrayList<>();
            ArrayList<String> blankFieldMessages = Validator.checkBlankLocation(locationId, semanticPlace);
            
            if (blankFieldMessages.isEmpty()) {
                errorMessages = Validator.validateLocation(locationId, semanticPlace);
            } else {
                Collections.sort(blankFieldMessages, new BlankFieldComparator(headerMap));
                errorMessages.addAll(blankFieldMessages);
            }
            
            
            if (errorMessages.isEmpty()) {
                locationStat.addRows(new String[] {locationId, semanticPlace});
            } else {
                locationStat.addError(new BootstrapError("location-lookup.csv", i+1, errorMessages));
            }
        }
        
        locationStat.refreshNumRecords();
        
        return locationStat;
    }
    
    
    /**
     * 
     * @param headers headers of the fields of location-lookup.csv
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
