/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.joda.time.Duration;
import org.joda.time.Interval;

/**
 * 
 * A class which contains the groupings of all {@link Companion} classes
 * 
 * <br> commonLocationInterval - common intervals spent by all companions in the group
 * <br> grouping - list of all companions in the group
 * <br> minTimePerLocation - minimum time spent by all companions in the group
 * <br> totalTime - total time spent by the group
 *
 * @author Daryln
 */
public class Group {
    private HashMap<String,ArrayList<Interval>> commonLocationInterval;
    private ArrayList<Companion> grouping;
    private HashMap<String,Integer> minTimePerLocation;
    private int totalTime;
    
    /**
     * totalTime = 0;
     *
     */
    public Group() {
        commonLocationInterval = new HashMap<>();
        grouping = new ArrayList<>();
        minTimePerLocation = new HashMap<>();
        totalTime = 0;
    }
    
    /**
     *
     * @param commonLocationAndPeriod common intervals spent by 2 {@link Companion} in the group
     */
    public void setCommonLocationAndPeriod(HashMap<String,ArrayList<Interval>> commonLocationAndPeriod) {
        commonLocationInterval = (HashMap<String,ArrayList<Interval>>)commonLocationAndPeriod.clone();
        for (Map.Entry<String,ArrayList<Interval>> entry : commonLocationInterval.entrySet()) {
            int durationPerLocation = 0;
            String location = entry.getKey();
            ArrayList<Interval> intervals = entry.getValue();
            for (Interval interval : intervals) {
                Duration intervalDuration = interval.toDuration();
                int intervalTime = (int)(intervalDuration.getStandardSeconds());
                durationPerLocation += intervalTime;
            }
            
            minTimePerLocation.put(location, durationPerLocation);
        }
    }
    
    /**
     * Add another companion to the group
     *
     * @param person {@link Companion}
     */
    public void addPerson(Companion person) {
        grouping.add(person);
    }
    
    /**
     *
     * @return total time spent by the group
     */
    public int getTotalTime() {
        int tempTime = 0;
        Iterator<Integer> iterInt = minTimePerLocation.values().iterator();
        while (iterInt.hasNext()) {
            tempTime += iterInt.next();
        }
        
        totalTime = tempTime;
        return totalTime;
    }
    
    /**
     *
     * @return list of all companions in the group
     */
    public ArrayList<Companion> getGroup() {
        return grouping;
    }
    
    /**
     *
     * @return common time spent by all companions in the group
     */
    public HashMap<String,Integer> getLocationAndTime() {
        return minTimePerLocation;
    }
    
    /**
     *
     * @return size of the group
     */
    public int size() {
        return grouping.size();
    }
    
    /**
     * Determines if otherPerson is a valid member of the group
     *
     * @param otherPerson another Companion 
     * @return true if otherPerson is a valid member of the group, false if otherwise
     */
    public boolean compare(Companion otherPerson) {
        int totalDuration = 0;
        ArrayList<String> checkDuplicateIntervals = new ArrayList<>();
        HashMap<String,ArrayList<Interval>> settingMinimumTime = new HashMap<>();
        HashMap<String,ArrayList<Interval>> otherPeriodOfEachLocation = otherPerson.getPeriodOfEachLocation();
        for (Map.Entry<String,ArrayList<Interval>> entry : commonLocationInterval.entrySet()) {
            String commonLocation = entry.getKey();
            ArrayList<Interval> commonIntervals = entry.getValue();
            if (otherPeriodOfEachLocation.get(commonLocation) == null) {
                return false;
            }
            
            
            ArrayList<Interval> otherPersonIntervals = otherPeriodOfEachLocation.get(commonLocation);
            
            for (Interval interval : commonIntervals) {
                for (Interval otherInterval : otherPersonIntervals) {
                    Interval checkOverlap = otherInterval.overlap(interval);
                    if (checkOverlap != null) {
                        if (!checkDuplicateIntervals.contains(checkOverlap.toString())) {
                            checkDuplicateIntervals.add(checkOverlap.toString());
                            Duration intervalDuration = checkOverlap.toDuration();
                            int intervalTime = (int)(intervalDuration.getStandardSeconds());
                            totalDuration += intervalTime;
                            
                            
                            
                            
                            if (settingMinimumTime.get(commonLocation) != null) {
                                ArrayList<Interval> intervalsAtLocation = settingMinimumTime.get(commonLocation);
                                intervalsAtLocation.add(checkOverlap);
                            } else {
                                ArrayList<Interval> locationInterval = new ArrayList<>();
                                locationInterval.add(checkOverlap);
                                settingMinimumTime.put(commonLocation, locationInterval);
                            }
                        }
                    }
                }
            }
        }
        
        if (totalDuration < 720) {
            return false;
        }
        
        
        setCommonLocationAndPeriod(settingMinimumTime);
        return true;
    }
}
