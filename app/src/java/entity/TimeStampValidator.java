/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Validates timestamp (yyyy-MM-dd HH:mm:ss)
 *
 * @author Daryln
 */
public class TimeStampValidator {
    
    /**
     * Validates timestamp in the location.csv file
     *
     * @param timeStamp date and time specified
     * @return true if timestamp is of format (yyyy-MM-dd HH:mm:ss), false if otherwise
     */
    public static boolean validateTimeStamp(String timeStamp) {
        try {
            LocalDateTime ldt = LocalDateTime.parse(timeStamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    /**
     * Validates timestamp specified in User Interface or through JSON web service
     *
     * @param timeStamp date and time specified
     * @return timestamp of type String with format of(yyyy-MM-dd HH:mm:ss) if specified timestamp is of format (yyyy-MM-ddTHH:mm:ss), null if otherwise
     */
    public static String validateJsonTimeStamp(String timeStamp) {
        try {
            timeStamp = timeStamp.replaceAll("t", " ");
            LocalDateTime ldt = LocalDateTime.parse(timeStamp);
            String correctTime = ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            int year = Integer.parseInt(correctTime.substring(0, 4));
            if (year > 2017 || year < 2000) {
                return null;
            }
            
            return correctTime;
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
