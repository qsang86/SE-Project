package entity;

import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Validates fields of CSV files
 *
 * @author HP
 */
public class Validator {
    //return error messages in arraylist format

    /**
     * Check for blank fields in demographics.csv
     *
     * @param macAddress macaddress filed in demographics.csv
     * @param name name field in demographics.csv
     * @param email email field in demographics.csv
     * @param password password field in demographics.csv
     * @param gender gender field in demographics.csv
     * @return list of error messages, empty list if no errors
     */
    public static ArrayList<String> checkBlankDemographics(String macAddress, String name, String email, String password, String gender) {
        
        ArrayList<String> errors = new ArrayList<>();
        
        if (macAddress==null || macAddress.length()==0) {
            errors.add("blank mac address");
        }
        
        if (name==null || name.length()==0) {
            errors.add("blank name");
        }
        
        if (email==null || email.length()==0) {
            errors.add("blank email");
        }
        
        if (password==null || password.length()==0) {
            errors.add("blank password");
        }
        
        if (gender==null || gender.length()==0) {
            errors.add("blank gender");
        }
        
        return errors;
    }
    
    /**
     * Check for invalid fields in demographics.csv
     *
     * @param macAddress macaddress filed in demographics.csv
     * @param name name field in demographics.csv
     * @param email email field in demographics.csv
     * @param password password field in demographics.csv
     * @param gender gender field in demographics.csv
     * @return list of error messages, empty list if no errors
     * 
     */
    public static ArrayList<String> validateUser(String macAddress, String name, String email, String password, String gender){
        
        ArrayList<String> errors = new ArrayList<>();
        
        //validate all data
        
        boolean hasError = EmailChecker.validateEmail(email);
        if (hasError) {
            errors.add("invalid email");
        }
        
        if (gender.length()>1) {
            errors.add("invalid gender");
        } else {
            char charGender = gender.charAt(0);
            if (!(charGender=='M' || charGender=='F' || charGender=='m' || charGender=='f')) {
                errors.add("invalid gender");
            }
        }
        
        
        if (!(macAddress.length() == 40 && macAddress.matches("-?[0-9a-fA-F]+"))) { // using regex
            errors.add("invalid mac address");
        } 

        String checkPass = password.replaceAll("\\s+", "").trim();
        if (password.length()<8 || !(checkPass.equals(password))) {
            errors.add("invalid password");
        }
        
    
        
        return errors;
    } 
    
    /**
     * Checks for blanks fields in location.csv
     *
     * @param timeStamp timestamp field in location.csv
     * @param macAddress macaddress field in location.csv
     * @param locationID location-id field in location.csv
     * @return list of error messages, empty list if no errors
     */
    public static ArrayList<String> checkBlankUserLocation(String timeStamp, String macAddress, String locationID) {
        ArrayList<String> errors = new ArrayList<>();
        //connection location database -> LocationDAO
        
        if (timeStamp==null || timeStamp.length()==0) {
            errors.add("blank timestamp");
        }
        
        if (macAddress==null || macAddress.length()==0) {
            errors.add("blank mac address");
        }
        
        if (locationID==null || locationID.length()==0) {
            errors.add("blank location");
        }
        
        
        return errors;
    }
    
    /**
     * Checks for invalid fields in location.csv
     *
     * @param timeStamp timestamp field in location.csv
     * @param macAddress macaddress field in location.csv
     * @param locationID location-id field in location.csv
     * @return list of error messages, empty list if no errors
     */
    public static ArrayList<String> validateUserLocation(String timeStamp, String macAddress, String locationID){
        
        ArrayList<String> errors = new ArrayList<>();
        //connection location database -> LocationDAO
        
        
        if (!(macAddress.length() == 40 && macAddress.matches("-?[0-9a-fA-F]+"))) { // using regex
            errors.add("invalid mac address");
        } 
        
        //check if timestamp valid
        boolean isCorrectTimeStamp = TimeStampValidator.validateTimeStamp(timeStamp);
        if (!isCorrectTimeStamp) {
            errors.add("invalid timestamp");
        }
        
        return errors;
    }
    
    /**
     * Checks for blank fields in location-lookup.csv
     *
     * @param locationId location-id field in location-lookup.csv
     * @param semanticPlace semantic-place field in location-lookup.csv
     * @return list of error messages, empty list if no errors
     */
    public static ArrayList<String> checkBlankLocation(String locationId, String semanticPlace) {
        ArrayList<String> errors = new ArrayList<>();
        
        if (locationId==null || locationId.length()==0) {
            errors.add("blank location id");
        }
        
        if (semanticPlace==null || semanticPlace.length()==0) {
            errors.add("blank semantic place");
        }
        
        return errors;
    }
    
    /**
     * Checks for invalid fields in location-lookup.csv
     *
     * @param locationId location-id field in location-lookup.csv
     * @param semanticPlace semantic-place field in location-lookup.csv
     * @return list of error messages, empty list if no errors
     */
    public static ArrayList<String> validateLocation(String locationId, String semanticPlace){
        
        ArrayList<String> errors = new ArrayList<>();
        
        
        try {
            int tempLocationId = Integer.parseInt(locationId);
            if (tempLocationId < 0) {
                errors.add("invalid location id");
            }
        } catch (NumberFormatException e) {
            errors.add("invalid location id");
        }
    
        
        
        String foundation = semanticPlace.substring(0,7);
        String levelNumber = semanticPlace.substring(7,8);
        if (!(foundation.equals("SMUSISL") || foundation.equals("SMUSISB"))) {
            errors.add("invalid semantic place");
        } else {
            try {
                int lvlNumber = Integer.parseInt(levelNumber);
                if (lvlNumber>5 || lvlNumber<1) {
                    errors.add("invalid semantic place");
                } 
            } catch (NumberFormatException e) {
                errors.add("invalid semantic place");
            }
        }

        return errors;
    }
    
}