/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DAO.LocationDAO;
import entity.Location;
import static java.lang.System.out;
import java.util.ArrayList;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.TreeMap;

/**
 * Manages the Top K Next Places functionality
 * 
 * @author Daryln
 */
public class TopKNextPlacesController {

    /**
     * Gets current time spent of all macaddresses at the specified timestamp and semantic place
     * 
     * @param dateTime TimeStamp of type String (yyyy-MM-dd HH:mm:ss)
     * @param semanticPlace
     * @return ArrayList&lt;Location&gt;
     */
    public static ArrayList<Location> getLocations(String dateTime, String semanticPlace) {

        try {
            //get all locations within 15mins timeframe
            ArrayList<Location> locations = LocationDAO.getCurrentUserLocation(dateTime, semanticPlace);
            return locations;
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Returns the next places that the macaddresses in {@link #getLocations(java.lang.String, java.lang.String) getLocations} visit 15min after the specified timestamp
     *
     * @param dateTime TimeStamp of type String (yyyy-MM-dd HH:mm:ss)
     * @param semanticPlace 
     * @return TreeMap&lt;Integer, ArrayList&lt;String&gt;&gt;
     */
    public static TreeMap<Integer,ArrayList<String>> getCompareList(String dateTime, String semanticPlace) {
        try {

            LinkedHashMap<String, LinkedHashMap<String, String>> locations = LocationDAO.getNext15minLocation(dateTime, semanticPlace);
            LinkedHashMap<String, Integer> rtnList = new LinkedHashMap<>();

            if (locations.size() > 0) {
                for (String macAdd : locations.keySet()) {// loop through all the keys (macaddress)

                    LinkedHashMap<String, String> uLocations = locations.get(macAdd); //get the value of the key

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date stayedDateTime = df.parse(dateTime); //initialize and parse the dateTime(input)
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(stayedDateTime);
                    cal.add(Calendar.SECOND, 900); //add 900 seconds ( <=15minutes) for the future location
                    stayedDateTime = df.parse(df.format(cal.getTime()));
                    boolean notFound = true;
                    String curLoc = "";
                    String[] keyArr = uLocations.keySet().toArray(new String[0]);
                    int curPos = 0;
                    for (String dateTimeRow : uLocations.keySet()) { // loop through the values(ArrayList) of the key
                        if (notFound) {
                            curLoc = uLocations.get(dateTimeRow);
                            Date rDateTime = df.parse(dateTimeRow);//parse the location row dateTime
                            double difference = stayedDateTime.getTime() - rDateTime.getTime(); //find the difference of previous and row
                            difference = difference / (60.0 * 1000.0); //convert to minutes
                            Integer currentNumber = 0; //initailize the number for the count
                            if (difference >= 5.0) { //if valid stayed for more than 5 minutes
                                System.out.println(macAdd + "; " + uLocations.get(dateTimeRow) + stayedDateTime + " and " + rDateTime + "difference " + difference);
                                if (rtnList.keySet().size() > 0) { //if keySet(semanticplace) is not empty
                                    if (rtnList.keySet().contains(uLocations.get(dateTimeRow))) { //if semantic place key found
                                        currentNumber = rtnList.get(uLocations.get(dateTimeRow)); //retrieve the values(count) of the key
                                    }
                                }
                                rtnList.put(uLocations.get(dateTimeRow), ++currentNumber);//map and add the number of value by 1
                                notFound = false;
                            }
                            if (curPos+1 <keyArr.length && !uLocations.get(keyArr[curPos+1]).equals(curLoc)) {
                                stayedDateTime = rDateTime;//assign the next one for checking
                            }
                        }
                        curPos++;
                    }
                }
            }
            TreeMap<Integer, ArrayList<String>> finalList = new TreeMap(Collections.reverseOrder()); //reverse the count order

            if (rtnList.size() > 0) {
                for (String loc : rtnList.keySet()) {
                    Integer num = rtnList.get(loc);

                    ArrayList<String> locList = new ArrayList<>(); //initialize for the semantic places list
                    if (!finalList.keySet().isEmpty()) { //keyset is not empty
                        if (finalList.keySet().contains(num)) { //keyset contain the key(count)
                            locList = finalList.get(num);//set to the location list
                        }
                    }
                    locList.add(loc); //add to the location list
                    finalList.put(num, locList);//map to the finalList
                }
            }
            return finalList; // return back to servlet
        } catch (SQLException e) {
            return null;
        } catch (ParseException ex) {
            return null;
        }
    }

}
