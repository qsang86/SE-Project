/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DAO.UserLocationDAO;
import entity.Group;
import entity.Companion;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.joda.time.Duration;
import org.joda.time.Interval;

/**
 * Manages the Automatic Group Detection functionality
 *
 * @author Daryln
 * 
 */
public class GroupDetectionController {

    /**
     *
     * @param dateTime TimeStamp of type String (yyyy-MM-dd HH:mm:ss)
     * @return ArrayList&lt;Companion&gt;
     * @throws SQLException
     */
    public static ArrayList<Companion> getPeople(String dateTime) throws SQLException {
        ArrayList<String[]> rawData = UserLocationDAO.getGroup(dateTime);
        ArrayList<Companion> personList = convertRawData(rawData, dateTime);
        
        return personList;
    }
    
    /**
     * 
     * @param personList ArrayList of Companion
     * @return ArrayList&lt;Group&gt;
     * @throws SQLException 
     */
    public static ArrayList<Group> firstRound(ArrayList<Companion> personList) throws SQLException {
        ArrayList<Group> groups = new ArrayList<>();

        for (Companion person : personList) {
            Group group = new Group();
            group.addPerson(person);
            HashMap<String, ArrayList<Interval>> periodOfEachLocation = person.getPeriodOfEachLocation();

            for (Companion otherPerson : personList) {
                if (person.equals(otherPerson)) {
                    continue;
                }
                HashMap<String, ArrayList<Interval>> otherPeriodOfEachLocation = otherPerson.getPeriodOfEachLocation();

                if (group.size() == 1) {
                    int totalDuration = 0;
                    HashMap<String, ArrayList<Interval>> commonLocationPeriods = new HashMap<>();
                    for (Map.Entry<String, ArrayList<Interval>> entry : periodOfEachLocation.entrySet()) {
                        String location = entry.getKey();
                        ArrayList<Interval> intervals = entry.getValue();
                        if (otherPeriodOfEachLocation.get(location) != null) {
                            ArrayList<Interval> otherIntervals = otherPeriodOfEachLocation.get(location);
                            for (Interval interval : intervals) {
                                for (Interval otherInterval : otherIntervals) {
                                    Interval overlapInterval = interval.overlap(otherInterval);
                                    if (overlapInterval != null) {
                                        int intervalTime = (int) (overlapInterval.toDuration().getStandardSeconds());
                                        if (commonLocationPeriods.get(location) != null) {
                                            //changes here
                                            ArrayList<Interval> intervalsAtLocation = commonLocationPeriods.get(location);
                                            intervalsAtLocation.add(overlapInterval);
                                            totalDuration += intervalTime;
                                        } else {
                                            totalDuration += intervalTime;
                                            ArrayList<Interval> locationInterval = new ArrayList<>();
                                            locationInterval.add(overlapInterval);
                                            commonLocationPeriods.put(location, locationInterval);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (totalDuration >= 720) {
                        group.addPerson(otherPerson);
                        group.setCommonLocationAndPeriod(commonLocationPeriods);
                    }
                } else {
                    boolean isValid = group.compare(otherPerson);
                    if (isValid) {
                        group.addPerson(otherPerson);
                    }
                }
            }

            if (group.size() > 1) {
                groups.add(group);
            }
        }

        return groups;
    }
    
    /**
     * 
     * @param personList ArrayList of Companion
     * @return ArrayList&lt;Group&gt;
     */
    public static ArrayList<Group> secondRound(ArrayList<Companion> personList) {
        ArrayList<Group> groups = new ArrayList<>();

        for (int i=0; i<personList.size(); i++) {
            Group group = new Group();
            Companion person = personList.get(i);
            group.addPerson(person);
            HashMap<String, ArrayList<Interval>> periodOfEachLocation = person.getPeriodOfEachLocation();

            for (int j=i+1; j<personList.size(); j++) {
                Companion otherPerson = null;
                try {
                    otherPerson = personList.get(j);
                } catch (IndexOutOfBoundsException e) {
                    break;
                }
                
                if (person.equals(otherPerson)) {
                    continue;
                }
                HashMap<String, ArrayList<Interval>> otherPeriodOfEachLocation = otherPerson.getPeriodOfEachLocation();

                if (group.size() == 1) {
                    int totalDuration = 0;
                    HashMap<String, ArrayList<Interval>> commonLocationPeriods = new HashMap<>();
                    for (Map.Entry<String, ArrayList<Interval>> entry : periodOfEachLocation.entrySet()) {
                        String location = entry.getKey();
                        ArrayList<Interval> intervals = entry.getValue();
                        if (otherPeriodOfEachLocation.get(location) != null) {
                            ArrayList<Interval> otherIntervals = otherPeriodOfEachLocation.get(location);
                            for (Interval interval : intervals) {
                                for (Interval otherInterval : otherIntervals) {
                                    Interval overlapInterval = interval.overlap(otherInterval);
                                    if (overlapInterval != null) {
                                        int intervalTime = (int) (overlapInterval.toDuration().getStandardSeconds());
                                        if (commonLocationPeriods.get(location) != null) {
                                            //changes here
                                            ArrayList<Interval> intervalsAtLocation = commonLocationPeriods.get(location);
                                            intervalsAtLocation.add(overlapInterval);
                                            totalDuration += intervalTime;
                                        } else {
                                            totalDuration += intervalTime;
                                            ArrayList<Interval> locationInterval = new ArrayList<>();
                                            locationInterval.add(overlapInterval);
                                            commonLocationPeriods.put(location, locationInterval);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (totalDuration >= 720) {
                        group.addPerson(otherPerson);
                        group.setCommonLocationAndPeriod(commonLocationPeriods);
                    }
                } else {
                    boolean isValid = group.compare(otherPerson);
                    if (isValid) {
                        group.addPerson(otherPerson);
                    }
                }
            }

            if (group.size() > 1) {
                groups.add(group);
            }
        }

        return groups;
    }

    /**
     * 
     * @param unfilteredGroups ArrayList&lt;Group&gt; possibly containing subgroups and duplicate groups
     * @return ArrayList&lt;Companion&gt; that has been filtered
     */
    public static ArrayList<Group> filterGroups(ArrayList<Group> unfilteredGroups) {
        ArrayList<Group> filteredGroups = new ArrayList<>();
        for (Group unfilteredGroup : unfilteredGroups) {
            boolean isValid = true;
            HashMap<String, Integer> unfilteredLocationAndTime = unfilteredGroup.getLocationAndTime();
            ArrayList<Companion> unfilteredGroupPeople = unfilteredGroup.getGroup();
            for (Group filteredGroup : filteredGroups) {
                HashMap<String, Integer> filteredLocationAndTime = filteredGroup.getLocationAndTime();
                if (filteredLocationAndTime.size() >= unfilteredLocationAndTime.size() && filteredGroup.size() >= unfilteredGroup.size()) {
                    int countDuplicateLocation = 0;
                    int countGroupPeople = 0;
                    for (Map.Entry<String, Integer> entry : filteredLocationAndTime.entrySet()) {
                        String filteredLocation = entry.getKey();
                        int filteredDuration = entry.getValue();
                        if (unfilteredLocationAndTime.containsKey(filteredLocation)) {
                            int unfilteredDuration = unfilteredLocationAndTime.get(filteredLocation);
                            if (unfilteredDuration == filteredDuration) {
                                countDuplicateLocation++;
                            }
                        }
                    }

                    for (Companion filteredCompanion : filteredGroup.getGroup()) {
                        if (unfilteredGroupPeople.contains(filteredCompanion)) {
                            countGroupPeople++;
                        }
                    }

                    if ((countGroupPeople == filteredGroup.size() || countGroupPeople == unfilteredGroup.size()) && (countDuplicateLocation == filteredLocationAndTime.size() || countDuplicateLocation == unfilteredLocationAndTime.size())) {
                        isValid = false;
                        break;
                    }
                }
            }

            if (isValid) {
                filteredGroups.add(unfilteredGroup);
            }
        }

        return filteredGroups;
    }

    /**
     * 
     * @param rawData ArrayList&lt;String[]&gt; retrieved from UserLocationDAO
     * @param dateTime TimeStamp of type String (yyyy-MM-dd HH:mm:ss)
     * @return ArrayList&lt;Companion&gt;
     */
    public static ArrayList<Companion> convertRawData(ArrayList<String[]> rawData, String dateTime) {
        ArrayList<Companion> personList = new ArrayList<>();
        Companion person = null;
        String currentMacAddress = null;
        String currentLocation = null;
        int currentDuration = 0;
        Timestamp currentStartTime = null;
        Timestamp currentEndTime = null;
        Timestamp tempCurrentEndTime = null;
        Timestamp chosenTime = Timestamp.valueOf(dateTime);
        int totalDuration = 0;

        for (int i = 0; i < rawData.size(); i++) {
            String[] row = rawData.get(i);
            String macaddress = row[0];
            String location = row[1];
            Timestamp startTime = Timestamp.valueOf(row[3]);
            Timestamp endTime = Timestamp.valueOf(row[4]);
            int duration = Integer.parseInt(row[5]);

            if (i == 0) {
                person = new Companion(macaddress);
                currentMacAddress = macaddress;
                currentLocation = location;
                currentDuration = duration;
                currentStartTime = startTime;
                currentEndTime = endTime;
            }

            if (currentMacAddress.equals(macaddress)) {
                if (currentDuration > 300) {
                    currentDuration = 300;
                    tempCurrentEndTime = new Timestamp(currentStartTime.getTime() + 5 * 60 * 1000);
                }

                if (i > 0) {
                    totalDuration += currentDuration;
                    if (tempCurrentEndTime != null) {
                        person.setPeriod(currentLocation, currentStartTime, tempCurrentEndTime);
                        tempCurrentEndTime = null;
                    } else {
                        person.setPeriod(currentLocation, currentStartTime, currentEndTime);
                    }
                }

                currentLocation = location;
                currentDuration = duration;
                currentStartTime = startTime;
                currentEndTime = endTime;
            } else {
                if (currentDuration > 300) {
                    currentDuration = 300;
                    tempCurrentEndTime = new Timestamp(currentStartTime.getTime() + 5 * 60 * 1000);
                }

                //changes here
                totalDuration += currentDuration;
                if (tempCurrentEndTime != null) {
                    person.setPeriod(currentLocation, currentStartTime, tempCurrentEndTime);
                    tempCurrentEndTime = null;
                } else {
                    person.setPeriod(currentLocation, currentStartTime, currentEndTime);
                }

                String[] prevRow = rawData.get(i - 1);
                String lastLocation = prevRow[2];

                int lastDuration = (int) ((chosenTime.getTime() - currentEndTime.getTime()) / 1000);
                if (lastDuration > 300) {
                    lastDuration = 300;
                    tempCurrentEndTime = new Timestamp(currentEndTime.getTime() + 5 * 60 * 1000);
                }

                totalDuration += lastDuration;
                if (tempCurrentEndTime != null) {
                    person.setPeriod(lastLocation, currentEndTime, tempCurrentEndTime);
                    tempCurrentEndTime = null;
                } else {
                    person.setPeriod(lastLocation, currentEndTime, chosenTime);
                }

                if (totalDuration >= 720) {
                    person.setEmail(prevRow[6]);
                    personList.add(person);
                }

                person = new Companion(macaddress);
                currentMacAddress = macaddress;
                currentLocation = location;
                currentDuration = duration;
                currentStartTime = startTime;
                currentEndTime = endTime;

                if (i != (rawData.size() - 1)) {
                    totalDuration = 0;
                }
            }

            if (i == (rawData.size() - 1)) {
                if (currentDuration > 300) {
                    currentDuration = 300;
                    tempCurrentEndTime = new Timestamp(currentStartTime.getTime() + 5 * 60 * 1000);
                }

                totalDuration += currentDuration;
                if (tempCurrentEndTime != null) {
                    person.setPeriod(currentLocation, currentStartTime, tempCurrentEndTime);
                    tempCurrentEndTime = null;
                } else {
                    person.setPeriod(currentLocation, currentStartTime, currentEndTime);
                }

                String ultiLastLocation = row[2];
                int lastDuration = (int) ((chosenTime.getTime() - currentEndTime.getTime()) / 1000);
                if (lastDuration > 300) {
                    lastDuration = 300;
                    tempCurrentEndTime = new Timestamp(currentEndTime.getTime() + 5 * 60 * 1000);
                }

                totalDuration += lastDuration;
                person.setDuration(ultiLastLocation, lastDuration);
                if (tempCurrentEndTime != null) {
                    person.setPeriod(ultiLastLocation, currentEndTime, tempCurrentEndTime);
                    tempCurrentEndTime = null;
                } else {
                    person.setPeriod(ultiLastLocation, currentEndTime, chosenTime);
                }

                if (totalDuration >= 720) {
                    person.setEmail(row[6]);
                    personList.add(person);
                }
            }
        }

        return personList;
    }
}
