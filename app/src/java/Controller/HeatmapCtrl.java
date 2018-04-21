package Controller;

import DAO.UserLocationDAO;
import entity.Location;
import java.util.HashMap;
import java.util.*;
import java.sql.SQLException;

/**
 * Manages the Heatmap functionality
 * 
 * @author Daryln
 * 
 */
public class HeatmapCtrl{

    /**
     *
     * @param dateTime TimeStamp of type String (yyyy-MM-dd HH:mm:ss)
     * @return ArrayList&lt;Location&gt;
     */
    public static ArrayList<Location> getNormHeatmap(String dateTime) {
        ArrayList<Location> heatmap = null;
        try{
            // retrieve heatmap for all levels at that datetime from UserLocationDAO
            heatmap = UserLocationDAO.getHeatmap(dateTime);	
        } catch (SQLException e) {
            return null;
        }
        return heatmap;
    }
    
    /**
     * 
     * @param locationList ArrayList of Location to be sorted by rank
     * @param k range of ranking from 1 to 10 (Default is 3)
     * @return HashMap&lt;Integer, ArrayList&lt;Location&gt;&gt;
     */
    public static HashMap<Integer,ArrayList<Location>> rankingOfTopKPop(ArrayList<Location> locationList, int k) {
        Collections.sort(locationList);
        HashMap<Integer,ArrayList<Location>> rankingOfLocation = new HashMap<>();
        int number = 1;
        int fixedNum = locationList.get(0).getNumberOfPeople();
        ArrayList<Location> locationWithinRank = new ArrayList<>();
        
        // loop through each location in given list
        for (int i=0; i<locationList.size(); i++) {
            Location location = locationList.get(i);
            int numPeople = location.getNumberOfPeople();
            
            // to check for same ranking
            if (numPeople == fixedNum) {
                locationWithinRank.add(location);
            } else {
                // to add locations according to their ranking 
                
                rankingOfLocation.put(number, (ArrayList<Location>)locationWithinRank.clone());
                // go to next ranking
                number++;
                locationWithinRank.clear();
                fixedNum = numPeople;
                locationWithinRank.add(location);
            }
            
            // last entry
            if (i == locationList.size()-1) {
                rankingOfLocation.put(number, (ArrayList<Location>)locationWithinRank.clone());
            }
        }
        
        // if number of entries exceeds ranking 
        if (k < rankingOfLocation.size()) {
            Iterator<Integer> iterInteger = rankingOfLocation.keySet().iterator();
            while (iterInteger.hasNext()) {
                int num = iterInteger.next();
                if (num > k) {
                    // check the number, if it's larger than k value, remove. 
                    iterInteger.remove();
                }
            }
        }
        
        return rankingOfLocation;
    }
}