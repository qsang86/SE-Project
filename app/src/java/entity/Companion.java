/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.joda.time.Interval;

/**
 * 
 * A class for storing intervals spent by a companion at each location
 * 
 * <br> durationOfEachLocation - Time spent at each location. eg at location A, companion is there for 47 seconds
 * <br> macaddress - macaddress of the companion
 * <br> email - email of the companion
 * <br> totalDuration - total duration spent by the companion during the 15minute time window
 * <br> periodOfEachLocation - intervals of time spent at each location. eg at location A, companion is there from 10:00:01 to 10:00:48
 *
 * @author Daryln
 */
public class Companion implements Comparable<Companion> {
    private HashMap<String,Integer> durationOfEachLocation;
    private String macaddress;
    private String email;
    private int totalDuration;
    private HashMap<String,ArrayList<Interval>> periodOfEachLocation;
    
    /**
     * 
     * email = "";
     * totalDuration = 0;
     * @param macaddress macaddress of the companion
     */
    public Companion(String macaddress) {
        durationOfEachLocation = new HashMap<>();
        periodOfEachLocation = new HashMap<>();
        this.macaddress = macaddress;
        email = "";
        totalDuration = 0;
    }
    
    /**
     *
     * @param locationId location-id of location
     * @return Time spent at particular location
     */
    public int getDuration(String locationId) {
        Integer tempDuration = durationOfEachLocation.get(locationId);
        if (tempDuration == null) {
            return 0;
        } else {
            return tempDuration;
        }
    }
    
    /**
     *
     * @param locationId location-id of location
     * @param duration time spent at particular location
     */
    public void setDuration(String locationId, int duration) {
        durationOfEachLocation.put(locationId, duration);
    }
    
    /**
     *
     * @return Macaddress of companion
     */
    public String getMacAddress() {
        return macaddress;
    }
    
    /**
     * refresh totalDuration of the companion by summing up all the time spent by companion at each location
     *
     */
    public void refreshDuration() {
        Collection<Integer> duration = durationOfEachLocation.values();
        Iterator<Integer> durationIter = duration.iterator();
        while (durationIter.hasNext()) {
            totalDuration += durationIter.next();
        }
    }
    
    /**
     *
     * @return total duration spent by companion in the 15minute window
     */
    public int getTotalDuration() {
        return totalDuration;
    }
    
    /**
     *
     * @return email of companion
     */
    public String getEmail() {
        return email;
    }
    
    /**
     *
     * @return HashMap of time spent at each location
     */
    public HashMap<String,Integer> getMap() {
        return durationOfEachLocation;
    }
    
    /**
     *
     * @return false if companion has 0 records of interval time spent at any location, true if companion has at least 1 record
     */
    public boolean hasRecords() {
        if (periodOfEachLocation.isEmpty()) {
            return false;
        }
        
        return true;
    }
    
    /**
     *
     * @param email email of companion
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public int compareTo(Companion otherCompanion) {
        return (getTotalDuration() - otherCompanion.getTotalDuration())*(-1);
    }
    
    /**
     *
     * @param locationId location-id of location
     * @param startTime start time of interval of time spent at given location-id
     * @param endTime end time of interval of time spent at given location-id
     */
    public void setPeriod(String locationId, Timestamp startTime, Timestamp endTime) {
        //changes here
        Interval interval = new Interval(startTime.getTime(),endTime.getTime());
        if (periodOfEachLocation.get(locationId) != null) {
            ArrayList<Interval> intervals = periodOfEachLocation.get(locationId);
            intervals.add(interval);
        } else {
            ArrayList<Interval> intervals = new ArrayList<>();
            intervals.add(interval);
            periodOfEachLocation.put(locationId, intervals);
        }
    }
    
    /**
     *
     * @param anotherPerson another companion
     * @return true if anotherPerson is the same companion, false if not
     */
    public boolean equals(Companion anotherPerson) {
        String currentMac = getMacAddress();
        String otherMac = anotherPerson.getMacAddress();
        return currentMac.equals(otherMac);
    }
    
    /**
     *
     * @return HashMap of interval of time spent at all locations
     */
    public HashMap<String,ArrayList<Interval>> getPeriodOfEachLocation() {
        return periodOfEachLocation;
    }
}
