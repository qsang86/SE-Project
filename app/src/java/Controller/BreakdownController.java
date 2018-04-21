/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DAO.UserDAO;
import entity.TimeStampValidator;
import entity.User;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

/**
 * Manages the breakdown of the order specified within the 15 minute time period
 *
 * @author Keng Yew
 */
public class BreakdownController {

    /**
     * 
     * @param dateTime
     * TimeStamp of type String (yyyy-MM-dd HH:mm:ss)
     * @return ArrayList&lt;User&gt; users
     */
    public static ArrayList<User> getUserList(String dateTime){
        try{
            String correctDate = TimeStampValidator.validateJsonTimeStamp(dateTime);
            //get all the users in demographics.csv within the specified timestamp
            ArrayList<User> users = UserDAO.retrieveAllUsers(correctDate);

            return users;
        }
        catch(SQLException e){
            return null;
        }
        
    }
    
    /**
     *
     * @param userList ArrayList of User to be broken down by year
     * @return TreeMap&lt;String, ArrayList&lt;User&gt;&gt; yearBreakdownMap
     */
    public static TreeMap<String,ArrayList<User>> getYearBreakdown(ArrayList<User> userList) {
        //from the given ArrayList of users --> break down into the different years
        TreeMap<String,ArrayList<User>> yearBreakdownMap = new TreeMap<>();
        ArrayList<User> year2013 = new ArrayList<>();
        ArrayList<User> year2014 = new ArrayList<>();
        ArrayList<User> year2015 = new ArrayList<>();
        ArrayList<User> year2016 = new ArrayList<>();
        ArrayList<User> year2017 = new ArrayList<>();
        
        
        for (User user : userList) {
            int year = user.getYear();
            switch (year) {
                case 2013:
                    year2013.add(user);
                    break;
                case 2014:
                    year2014.add(user);
                    break;
                case 2015:
                    year2015.add(user);
                    break;
                case 2016:
                    year2016.add(user);
                    break;
                default:
                    year2017.add(user);
                    break;
            }
        }
        
        
        yearBreakdownMap.put("2013", year2013);
        yearBreakdownMap.put("2014", year2014);
        yearBreakdownMap.put("2015", year2015);
        yearBreakdownMap.put("2016", year2016);
        yearBreakdownMap.put("2017", year2017);
        
        return yearBreakdownMap;
    }
    
    /**
     *
     * @param userList ArrayList of User to be broken down by school
     * @return TreeMap&lt;String, ArrayList&lt;User&gt;&gt; schoolBreakdownMap
     */
    public static TreeMap<String,ArrayList<User>> getSchoolBreakdown(ArrayList<User> userList) {
        //from the given ArrayList of users --> break down into the different schools
        TreeMap<String,ArrayList<User>> schoolBreakdownMap = new TreeMap<>();
        ArrayList<User> sis = new ArrayList<>();
        ArrayList<User> business = new ArrayList<>();
        ArrayList<User> accountancy = new ArrayList<>();
        ArrayList<User> econs = new ArrayList<>();
        ArrayList<User> socsc = new ArrayList<>();
        ArrayList<User> law = new ArrayList<>();
        
        for (User user : userList) {
            String school = user.getSchool();
            switch (school) {
                case "sis":
                    sis.add(user);
                    break;
                case "business":
                    business.add(user);
                    break;
                case "accountancy":
                    accountancy.add(user);
                    break;
                case "law":
                    law.add(user);
                    break;
                case "socsc":
                    socsc.add(user);
                    break;
                case "economics":
                    econs.add(user);
                    break;
                default:
                    break;
            }
        }
        
        
        schoolBreakdownMap.put("sis", sis);
        schoolBreakdownMap.put("socsc", socsc);
        schoolBreakdownMap.put("business", business);
        schoolBreakdownMap.put("accountancy", accountancy);
        schoolBreakdownMap.put("law", law);
        schoolBreakdownMap.put("economics", econs);
        
        return schoolBreakdownMap;
    }
    
    /**
     *
     * @param userList ArrayList of User to be broken down by gender
     * @return TreeMap&lt;String, ArrayList&lt;User&gt;&gt; genderBreakdownMap
     */
    public static TreeMap<String,ArrayList<User>> getGenderBreakdown(ArrayList<User> userList) {
        //from the given ArrayList of users --> break down into the different genders
        TreeMap<String,ArrayList<User>> genderBreakdownMap = new TreeMap<>(Collections.reverseOrder());
        ArrayList<User> male = new ArrayList<>();
        ArrayList<User> female = new ArrayList<>();
        
        for (User user : userList) {
            char gender = user.getGender();
            if (gender=='M' || gender=='m') {
                male.add(user);
            } else {
                female.add(user);
            }
        }
        
        
        genderBreakdownMap.put("M", male);
        genderBreakdownMap.put("F", female);
        
        return genderBreakdownMap;
    }

}
