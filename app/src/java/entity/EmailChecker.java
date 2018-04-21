/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.util.*;

/**
 * 
 * Validates the email field in demographics.csv
 *
 * @author Keng Yew
 */
public class EmailChecker {

    /**
     *
     * @param email of user
     * @return true if email is valid, false if not
     */
    public static boolean validateEmail(String email) {

        ArrayList<String> schools = new ArrayList<>();

        schools.add("sis");
        schools.add("business");
        schools.add("socsc");
        schools.add("accountancy");
        schools.add("law");
        schools.add("economics");

        String checkEmail = email.toLowerCase();

        //check if "smu.edu.sg" exist
        int findLastEmail = checkEmail.indexOf(".smu.edu.sg");
        if (findLastEmail == -1) {
            return true;
        }

        //check if "@" exist
        int findAt = checkEmail.indexOf("@");
        if (findAt == -1) {
            return true;
        }

        //check if school exist
        String findSchool = checkEmail.substring(findAt + 1, findLastEmail);

        if (!schools.contains(findSchool)) {
            return true;
        }

        //check year
        String nameAndYear = checkEmail.substring(0, findAt);

        int dotBeforeYear = nameAndYear.lastIndexOf(".");

        String year = checkEmail.substring(dotBeforeYear + 1, findAt);
        try {
            int iYear = Integer.parseInt(year);

            if (!(iYear >= 2013 && iYear <= 2017)) {
                return true;
            }
        } catch (NumberFormatException e) {
            return true;
        }
        
        
        //check name
        String name = checkEmail.substring(0, dotBeforeYear);
        int counter = 0;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);

            if (!((c >= 'a' && c <= 'z') || c == '.' || (c >= '0' && c <= '9'))) {
                return true;
            }

            if (c == '.') {
                counter += 1;
            }

            if (counter > 1) {
                return true;
            }

        }

        return false;
    }
}
