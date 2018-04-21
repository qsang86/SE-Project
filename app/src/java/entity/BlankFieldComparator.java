/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.util.Comparator;
import java.util.HashMap;

/**
 * Orders the sequence of the output of the different blank fields in the error messages
 *
 * @author Daryln
 */
public class BlankFieldComparator implements Comparator<String> {
    private final HashMap<String,Integer> order;
    
    /**
     * Creates a new BlankFieldComparator which orders the sequence of the output of the different blank fields
     *
     * @param order order of the header fields in the csv files
     */
    public BlankFieldComparator(HashMap<String,Integer> order) {
        this.order = order;
    }
    
    @Override
    public int compare(String error1, String error2) {
        String comparingError1 = error1.substring(6);
        String comparingError2 = error2.substring(6);
        
        if (comparingError1.equals("mac address")) {
            comparingError1 = "mac-address";
        }
        
        if (comparingError2.equals("mac address")) {
            comparingError2 = "mac-address";
        }
        
        int order1 = order.get(comparingError1);
        int order2 = order.get(comparingError2);
        
        return order1 - order2;
    }
}
