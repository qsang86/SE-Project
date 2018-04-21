/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DAO.UserLocationDAO;
import entity.Companion;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.joda.time.Duration;
import org.joda.time.Interval;

/**
 * Manages the TopKCompanions functionality 
 *
 * @author Daryln
 */
public class CompanionController {

    /**
     *
     * @param k ranking ranging from 1 to 10 (default k value is 3)
     * @param dateTime TimeStamp of type String (yyyy-MM-dd HH:mm:ss)
     * @param macaddress macaddress of user
     * @return HashMap&lt;Integer, ArrayList&lt;Companion&gt;&gt; rankingOfCompanion
     * @throws SQLException
     */
    public static HashMap<Integer,ArrayList<Companion>> controlCompanions(int k, String dateTime, String macaddress) throws SQLException {
        
        //check if user has only 1 record in location.csv based on dateTime
        Companion specifiedUser = UserLocationDAO.getSingleRowSpecifiedUser(dateTime, macaddress);
        
        //if specifiedUser is null, get specifiedUser from userDAO based on dateTime
        if (specifiedUser == null) {
            specifiedUser = UserLocationDAO.getSpecifiedUser(dateTime, macaddress);
        }
        
        //calculate and update totalDuration
        specifiedUser.refreshDuration();
        
        //get all the locations which are stored in the keys
        Set<String> locationSet = specifiedUser.getMap().keySet();
        
        ArrayList<String> locationList = new ArrayList<>();
        locationList.addAll(locationSet);
        
        //look for other users which are at the same room with specified user at that dateTime
        ArrayList<String[]> rawData = UserLocationDAO.getOtherUsers(dateTime, macaddress);
        //arrange these data based on their duration of stay in that location
        ArrayList<Companion> companionList = convertRawData(rawData, locationList, dateTime);
        //find single row users
        ArrayList<Companion> singleRowCompanionList = UserLocationDAO.getOtherSingleRowUsers(dateTime, macaddress, locationList);
        companionList.addAll(singleRowCompanionList);
        
        //arrange them based on user and companions
        ArrayList<Companion> correctCompanionList = manageCompanions(specifiedUser, companionList);
       
        Collections.sort(correctCompanionList);
        
        
        //rank the list of companions
        HashMap<Integer,ArrayList<Companion>> rankingOfCompanion = new HashMap<>();
        int number = 1;
        int fixedDuration = correctCompanionList.get(0).getTotalDuration();
        ArrayList<Companion> companionsWithinRank = new ArrayList<>();
        for (int i=0; i<correctCompanionList.size(); i++) {
            Companion person = correctCompanionList.get(i);
            int duration = person.getTotalDuration();
            if (duration == fixedDuration) {
                companionsWithinRank.add(person);
            } else {
                rankingOfCompanion.put(number, (ArrayList<Companion>)companionsWithinRank.clone());
                number++;
                companionsWithinRank.clear();
                fixedDuration = duration;
                companionsWithinRank.add(person);
            }
            
            if (i == correctCompanionList.size()-1) {
                rankingOfCompanion.put(number, (ArrayList<Companion>)companionsWithinRank.clone());
            }
        }
        
        if (k < rankingOfCompanion.size()) {
            Iterator<Integer> iterInteger = rankingOfCompanion.keySet().iterator();
            while (iterInteger.hasNext()) {
                int num = iterInteger.next();
                if (num > k) {
                    iterInteger.remove();
                }
            }
        }
        
        
        return rankingOfCompanion;
    }
    
    /**
     * 
     * @param specifiedUser user specified by given macaddress
     * @param companionList ArrayList of Companions 
     * @return ArrayList&lt;Companion&gt;
     */
    private static ArrayList<Companion> manageCompanions(Companion specifiedUser, ArrayList<Companion> companionList) {
        HashMap<String,ArrayList<Interval>> specifiedLocationIntervals = specifiedUser.getPeriodOfEachLocation();
        ArrayList<Companion> correctCompanionList = new ArrayList<>();
        
        for (Companion companion : companionList) {
            HashMap<String,ArrayList<Interval>> companionLocationIntervals = companion.getPeriodOfEachLocation();
            for (Map.Entry<String,ArrayList<Interval>> entry : companionLocationIntervals.entrySet()) {
                int totalTimePerLocation = 0;
                String companionLocation = entry.getKey();
                ArrayList<Interval> companionIntervals = entry.getValue();
                ArrayList<Interval> specifiedUserIntervals = specifiedLocationIntervals.get(companionLocation);
                
                for (Interval companionInterval : companionIntervals) {
                    for (Interval specifiedInterval : specifiedUserIntervals) {
                        Interval overlap = companionInterval.overlap(specifiedInterval);
                        if (overlap != null) {
                                Duration intervalDuration = overlap.toDuration();
                                int intervalTime = (int)(intervalDuration.getStandardSeconds());
                                totalTimePerLocation += intervalTime;
                        }
                    }
                }
                companion.setDuration(companionLocation, totalTimePerLocation);
            }
            
            companion.refreshDuration();
            if (companion.getTotalDuration() > 0) {
                correctCompanionList.add(companion);
            }
        }
        
        return correctCompanionList;
    }
    
    /**
     * 
     * @param rawData ArrayList of String[] retrieved from UserLocationDAO
     * @param locationList locations that the specifiedUser was in
     * @param dateTime TimeStamp of type String (yyyy-MM-dd HH:mm:ss)
     * @return ArrayList&lt;Companion&gt;
     */
    private static ArrayList<Companion> convertRawData(ArrayList<String[]> rawData, ArrayList<String> locationList, String dateTime) {
        ArrayList<Companion> companionList = new ArrayList<>();
        Companion companion = null;
        String currentMacAddress = null;
        String currentLocation = null;
        int currentDuration = 0;
        Timestamp chosenTime = Timestamp.valueOf(dateTime);
        Timestamp currentStartTime = null;
        Timestamp currentEndTime = null;
        Timestamp tempCurrentEndTime = null;
        
        //loop through all raw data to find companions in the exact location within a specified time 
        for (int i=0; i<rawData.size(); i++) {
            String[] row = rawData.get(i);
            
            //assign the each values from string array into a variable
            String macaddress = row[0];
            String location = row[1];
            Timestamp startTime = Timestamp.valueOf(row[3]);
            Timestamp endTime = Timestamp.valueOf(row[4]);
            int duration = Integer.parseInt(row[5]);
            
            // at rawdata[0] set current macaddress, current start time and current end time
            if (i == 0) {
                companion = new Companion(macaddress);
                currentMacAddress = macaddress;
                currentLocation = "";
                currentStartTime = startTime;
                currentEndTime = endTime;
            }        
            
            // if macaddress matches
            if (currentMacAddress.equals(macaddress)) {
                if (currentDuration > 300) {
                    tempCurrentEndTime = new Timestamp(currentStartTime.getTime() + 5*60*1000);
                }
                
                
                if (!currentLocation.isEmpty()) {
                    if (tempCurrentEndTime != null) {
                        companion.setPeriod(currentLocation, currentStartTime, tempCurrentEndTime);
                        tempCurrentEndTime = null;
                    } else {
                        companion.setPeriod(currentLocation, currentStartTime, currentEndTime);
                    }
                }
                
                //if location is found inside location visited by specified user
                if (locationList.contains(location)) {
                    currentLocation = location;
                    currentDuration = duration;
                    currentStartTime = startTime;
                    currentEndTime = endTime;
                } else {
                    currentLocation = "";
                    currentStartTime = startTime;
                    currentEndTime = endTime;
                }
            } else {
                if (currentDuration > 300) {
                    tempCurrentEndTime = new Timestamp(currentStartTime.getTime() + 5*60*1000);
                }

                if (!currentLocation.isEmpty()) {
                    if (tempCurrentEndTime != null) {
                        companion.setPeriod(currentLocation, currentStartTime, tempCurrentEndTime);
                        tempCurrentEndTime = null;
                    } else {
                        companion.setPeriod(currentLocation, currentStartTime, currentEndTime);
                    }
                }

                String[] prevRow = rawData.get(i-1);
                String lastLocation = prevRow[2];

                if (locationList.contains(lastLocation)) {
                    int lastDuration = (int)((chosenTime.getTime() - currentEndTime.getTime())/1000);
                    if (lastDuration > 300) {
                        tempCurrentEndTime = new Timestamp(currentEndTime.getTime() + 5*60*1000);
                    }

                    if (tempCurrentEndTime != null) {
                        companion.setPeriod(lastLocation, currentEndTime, tempCurrentEndTime);
                        tempCurrentEndTime = null;
                    } else {
                        companion.setPeriod(lastLocation, currentEndTime, chosenTime);
                    }
                }
                
                //if record != null
                if (companion.hasRecords()) {
                    companion.setEmail(prevRow[6]);
                    companionList.add(companion);
                }

                companion = new Companion(macaddress);
                currentMacAddress = macaddress;
                if (locationList.contains(location)) {
                    currentLocation = location;
                    currentDuration = duration;
                    currentStartTime = startTime;
                    currentEndTime = endTime;
                } else {
                    currentLocation = "";
                    currentStartTime = startTime;
                    currentEndTime = endTime;
                }
            }

            //check last row in datasize
            if (i == (rawData.size()-1)) {
                if (!currentLocation.isEmpty()) {
                    if (currentDuration > 300) {
                        tempCurrentEndTime = new Timestamp(currentStartTime.getTime() + 5*60*1000);
                    }

                    if (tempCurrentEndTime != null) {
                        companion.setPeriod(currentLocation, currentStartTime, tempCurrentEndTime);
                        tempCurrentEndTime = null;
                    } else {
                        companion.setPeriod(currentLocation, currentStartTime, currentEndTime);
                    }
                }
                
                String ultiLastLocation = row[2];
                if (locationList.contains(ultiLastLocation)) {
                    int lastDuration = (int)((chosenTime.getTime() - currentEndTime.getTime())/1000);
                    if (lastDuration > 300) {
                        tempCurrentEndTime = new Timestamp(currentEndTime.getTime() + 5*60*1000);
                    }


                    if (tempCurrentEndTime != null) {
                        companion.setPeriod(ultiLastLocation, currentEndTime, tempCurrentEndTime);
                        tempCurrentEndTime = null;
                    } else {
                        companion.setPeriod(ultiLastLocation, currentEndTime, chosenTime);
                    }
                }

                if (companion.hasRecords()) {
                    companion.setEmail(row[6]);
                    companionList.add(companion);
                }
            }
        }
        
        
        return companionList;
    }
}
