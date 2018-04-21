/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DAO.LocationDAO;
import DAO.UserLocationDAO;
import au.com.bytecode.opencsv.CSVWriter;
import entity.BlankFieldComparator;
import entity.BootstrapError;
import entity.BootstrapStat;
import entity.TimeStampValidator;
import entity.Validator;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Controls the validation and inserting of data of location.csv
 *
 * @author Daryln
 * 
 */
public class UserLocationController {

    //check for duplicate entries within csv

    /**
     *
     * @param strList ArrayList of String[] containing all the data in demographics.csv
     * @return {@link BootstrapStat}
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static BootstrapStat controlUserLocation(ArrayList<String[]> strList) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {

        if (strList == null || strList.isEmpty()) {
            return null;
        }

        //get all correct location ids from database
        ArrayList<String> locationIds = LocationDAO.retrieveAllLocationIds();

        BootstrapStat userLocationStat = new BootstrapStat();
        userLocationStat.setFileName("location.csv");
        HashMap<String,Integer> headerMap = assignHeaders(strList.get(0));
        
        int macPos = headerMap.get("mac-address");
        int timeStampPos = headerMap.get("timestamp");
        int locationIdPos = headerMap.get("location-id");

        //unique rows of timestamp + macaddress
        HashMap<String, Integer> checkDuplicateRows = new HashMap<>();

        //remove header row
        strList.remove(0);

        //count backwards
        int count = strList.size() + 1;
        Collections.reverse(strList);

        for (String[] strRecord : strList) {
            String macAddress = strRecord[macPos].trim();
            String timeStamp = strRecord[timeStampPos];
            String locationId = strRecord[locationIdPos].trim();
            //row number
            int rowNum = count;
            
            ArrayList<String> errorMessages = new ArrayList<>();
            ArrayList<String> blankFieldMessages = Validator.checkBlankUserLocation(timeStamp, macAddress, locationId);
            if (blankFieldMessages.isEmpty()) {
                //check if locationid matches database locationids
                if (!(locationIds.contains(locationId))) {
                    errorMessages.add("invalid location");
                }
                errorMessages.addAll(Validator.validateUserLocation(timeStamp, macAddress, locationId));
            } else {
                Collections.sort(blankFieldMessages, new BlankFieldComparator(headerMap));
                errorMessages.addAll(blankFieldMessages);
            }

            //check for duplicate rows
            if (!(errorMessages.contains("invalid timestamp") || errorMessages.contains("invalid mac address"))) {
                if (checkDuplicateRows.get(timeStamp + macAddress) != null) {
                    errorMessages.add("duplicate row");
                    Collections.sort(errorMessages);
                }
            }

            if (errorMessages.isEmpty()) {
                //input unique timestamp + macaddress
                checkDuplicateRows.put(timeStamp + macAddress, rowNum);
                //add in correct rows
                userLocationStat.addRows(new String[]{timeStamp, macAddress, locationId});
            } else {
                //add in wrong rows
                userLocationStat.addError(new BootstrapError("location.csv", rowNum, errorMessages));
            }

            count--;
        }
        
        userLocationStat.duplicateKeysAndRowNum(checkDuplicateRows);
        Collections.sort(userLocationStat.getErrors());
        userLocationStat.refreshNumRecords();

        return userLocationStat;
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

    //check for duplicate entries in csv against the database (upload addtional file)
    /**
     * 
     * @param userLocationStat check for duplicate entries in csv against the database (upload addtional file function)
     * @throws SQLException 
     */
    public static void controlAdditionalUserLocation(BootstrapStat userLocationStat) throws SQLException {
        ArrayList<String[]> additionalUserLocationRows = userLocationStat.getCorrectRows();
        HashMap<String, String> databaseUserLocations = UserLocationDAO.retrieveAll();
        HashMap<String, Integer> locationCSVFileRows = userLocationStat.getRowNum();

        

        for (int i = 0; i < additionalUserLocationRows.size(); i++) {
            String[] row = additionalUserLocationRows.get(i);
            String timeStamp = row[0];
            String macAddress = row[1];
            String csvLocationID = row[2];

            if (databaseUserLocations.get(timeStamp + ".0" + macAddress) != null) {
                String locationID = databaseUserLocations.get(timeStamp+".0"+ macAddress);
                if (csvLocationID.equals(locationID)) {
                    
                    // if row already exists, delete 
                    userLocationStat.removeRow(i);
                    int rowNum = locationCSVFileRows.get(timeStamp+macAddress);
                    ArrayList<String> errorMsg = new ArrayList<>();
                    errorMsg.add("duplicate row");
                    userLocationStat.addError(new BootstrapError("location.csv", rowNum, errorMsg));
                    i--;
                }
            }
        }
        
        Collections.sort(userLocationStat.getErrors());
        userLocationStat.refreshNumRecords();
    }
    
    /**
     * Create a temp file to load all the validated records into. Then initiate loadDataInFile for faster processing time
     * 
     * @param userLocationList ArrayList&lt;String[]&gt; containing validated records of location.csv
     * @return String pathOfTempFile
     * @throws IOException 
     */
    public static String getPathOfTempFile(ArrayList<String[]> userLocationList) throws IOException {
        File tempFile = null;
        CSVWriter writer = null;
        String fileName = "locationData";
        
        if (userLocationList.isEmpty()) {
            return null;
        }
        
        
        
        try {
            tempFile = File.createTempFile(fileName, ".csv");
            String pathName = tempFile.getAbsolutePath();
            writer = new CSVWriter(new FileWriter(pathName));
            writer.writeAll(userLocationList);
            pathName = pathName.replace("\\", "/");
            return pathName;
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
