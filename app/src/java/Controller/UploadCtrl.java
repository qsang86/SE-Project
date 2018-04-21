/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DAO.*;
import au.com.bytecode.opencsv.CSVReader;
import entity.BootstrapStat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.http.Part;

/**
 * Interacts directly with the BootstrapServlet class and manages the following controllers:
 * <br> {@link DemographicsController}
 * <br> {@link LocationController}
 * <br> {@link UserLocationController}
 *
 * @author Daryln
 */
public class UploadCtrl {
    
    /**
     * Removes existing records in database and inserts the data of the given file into the database
     *
     * @param file the zip file containing the 3 csv files
     * @return ArrayList&lt;{@link BootstrapStat}&gt;
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IOException
     */
    public static ArrayList<BootstrapStat> uploadFiles(Part file) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException, IOException{
        if(file == null){
            return null;
        }
        
        //arraylist to take in correct and wrong records in csv
        ArrayList<BootstrapStat> result = new ArrayList<>();
        
        //open zip file
        InputStream inputStream = file.getInputStream();
        ZipEntry entry = null;
        ZipInputStream zis = new ZipInputStream(inputStream);
        
        //hashmap to contain the records of the csv files
        HashMap<String, ArrayList<String[]>> zipEntries = new HashMap<>();
        
        //read csv files and add into zipEntries
        while((entry = zis.getNextEntry()) != null){
            InputStreamReader inputStreamReader = new InputStreamReader(zis, "UTF-8");
            BufferedReader bfreader = new BufferedReader(inputStreamReader);
            CSVReader reader = new CSVReader(bfreader, ',');
            ArrayList<String[]> strList = (ArrayList<String[]>) reader.readAll();
            zipEntries.put(entry.getName(), strList);
        }
        inputStream.close();
        zis.close();
        
        //validation for zipEntries
        if(zipEntries.isEmpty()){
            return null;
        }
        
        if (!(zipEntries.containsKey("demographics.csv") && zipEntries.containsKey("location-lookup.csv") && zipEntries.containsKey("location.csv") && zipEntries.size()==3)) {
            return null;
        }
        
        //remove foreign keys
        UserLocationDAO.removeForeignKeys();
        
        //delete data
        UserDAO.deleteUserData();
        UserDAO.addAdmin();
        LocationDAO.deleteLocationData();
        UserLocationDAO.deleteUserLocationData();
        
        //Validating and storing demographics.csv
        BootstrapStat demoStat = DemographicsController.controlDemographics(zipEntries.get("demographics.csv"));
        UserDAO.addUser(demoStat.getCorrectRows());
        
        //Validating and storing location-lookup.csv
        BootstrapStat locationStat = LocationController.controlLocation(zipEntries.get("location-lookup.csv"));
        LocationDAO.addLocation(locationStat.getCorrectRows());
        
        //Validating and storing location.csv
        BootstrapStat userLocationStat = UserLocationController.controlUserLocation(zipEntries.get("location.csv"));
        String pathOfTempFile = UserLocationController.getPathOfTempFile(userLocationStat.getCorrectRows());
        UserLocationDAO.addUserLocationByFile(pathOfTempFile);
        
        //add foreign keys
        UserLocationDAO.addForeignKeys();
        
        //add into result to be returned
        result.add(demoStat);
        result.add(locationStat);
        result.add(userLocationStat);
        
        
        return result;
    }

    /**
     * Uploads files to be added onto the existing records in the database
     * 
     * @param file a zip file containing a maximum of two files: demographics.csv/location.csv
     * @return ArrayList&lt;{@link BootstrapStat}&gt;
     * @throws IOException
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static ArrayList<BootstrapStat> uploadAdditionalFiles(Part file) throws IOException, SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        InputStream inputStream = file.getInputStream();
        
        ArrayList<BootstrapStat> result = new ArrayList<>();
        ZipEntry entry = null;
        //unzip
        ZipInputStream zis = new ZipInputStream(inputStream);
        HashMap<String, ArrayList<String[]>> zipEntries = new HashMap<>();
        
        //read csv files and add into zipEntries
        while ((entry = zis.getNextEntry()) != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(zis, "UTF-8");
            BufferedReader bfreader = new BufferedReader(inputStreamReader);
            CSVReader reader = new CSVReader(bfreader, ',');
            ArrayList<String[]> strList = (ArrayList<String[]>) reader.readAll();
            zipEntries.put(entry.getName(), strList);
        }
        
        inputStream.close();
        zis.close();
        
        // if nothing was added --> error. return null value 
        if (zipEntries.isEmpty()) {
            return null;
        }
        
        // loop through each value in zipEntries to check that file is either demographics.csv or location.csv
        // if not --> error. return null value
        Iterator<String> iterKey = zipEntries.keySet().iterator();
        while (iterKey.hasNext()) {
            String key = iterKey.next();
            if (!(key.equals("demographics.csv") || key.equals("location.csv"))) {
                return null;
            }
        }
        
        
        BootstrapStat demoStat = null;
        BootstrapStat userLocationStat = null;
        
        
        if (zipEntries.get("demographics.csv") != null) {
            
            // validation
            demoStat = DemographicsController.controlDemographics(zipEntries.get("demographics.csv"));
            
            // add new users into database
            UserDAO.addUser(demoStat.getCorrectRows());
        }
        
        
        if (zipEntries.get("location.csv") != null) {
            
            // validation
            userLocationStat = UserLocationController.controlUserLocation(zipEntries.get("location.csv"));
            // check for duplicates
            UserLocationController.controlAdditionalUserLocation(userLocationStat);
            String pathOfTempFile = UserLocationController.getPathOfTempFile(userLocationStat.getCorrectRows());
            if (pathOfTempFile != null) {
                UserLocationDAO.addUserLocationByFile(pathOfTempFile);
            }
        }
        
        
        if (demoStat != null) {
            result.add(demoStat);
        }
        
        
        if (userLocationStat != null) {
            result.add(userLocationStat);
        }
        
        return result;
    }
}
