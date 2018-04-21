/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

/**
 * 
 * <br> numOfPeople - number of users in the particular location(semantic place)
 * <br> semanticPlace - location name 
 * <br> macaddress - macaddress of user in that location 
 * <br> timestamp - interval time spent by user in that location 
 *
 * @author Daryln
 */
public class Location implements Comparable<Location>{
    private int numOfPeople;
    private String semanticPlace;
    private String macAddress;
    private String timeStamp;
    
    /**
     *
     * @param semanticPlace location name
     * @param numOfPeople number of users at that location
     */
    public Location(String semanticPlace, int numOfPeople){
        this.numOfPeople = numOfPeople;
        this.semanticPlace = semanticPlace;
    }
    
    /**
     *
     * @param semanticPlace location name
     * @param macAddress macaddress of user
     */
    public Location(String semanticPlace, String macAddress){
        this.semanticPlace = semanticPlace;
        this.macAddress = macAddress;
    }
    
    /**
     *
     * @param macAddress macaddress of user
     * @param semanticPlace location name
     * @param timeStamp interval time spent by user
     */
    public Location(String macAddress, String semanticPlace, String timeStamp){
        this.macAddress = macAddress;
        this.semanticPlace = semanticPlace;
        this.timeStamp = timeStamp;
    }
    
    /**
     *
     * @return number of users at this location
     */
    public int getNumberOfPeople(){
        return numOfPeople;
    }
    
    /**
     *
     * @return name of location
     */
    public String getSemanticPlace(){
        return semanticPlace;
    }
    
    /**
     *
     * @return macaddress of this user
     */
    public String getMacAddress(){
        return macAddress;
    }
    
    /**
     *
     * @return interval time spent by the user at this location
     */
    public String getTimeStamp(){
        return timeStamp;
    }
    
    public int compareTo(Location anotherLocation){
        return (numOfPeople - anotherLocation.numOfPeople)*(-1);
    }
    
    /**
     *
     * @return floor number of the location
     */
    public int getFloor(){
        int floor = 0;
        String basement = semanticPlace.substring(6,7);
        if(basement.equals("B")){
            return 0;
        }
        
        String floorNum = semanticPlace.substring(7,8);
        floor = Integer.parseInt(floorNum);
        
        return floor;
        
    }
    
    /**
     * Returns heatmap density of location (0 being empty, 6 being the most crowded)
     *
     * @param num density level
     * @return Heatmap Density of location
     */
    public static int getDensity(int num){
        if(num == 0){
                return 0;
        }
        if(num >= 1 && num <= 2){
                return 1;
        }
        if(num >= 3 && num <= 5){
                return 2;
        }
        if(num >= 6 && num <= 10){
                return 3;
        }
        if(num >= 11 && num <= 20){
                return 4;
        }
        if(num >= 20 && num <= 30){
                return 5;
        }

        return 6;
    }
    
}
