/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.util.*;

/**
 * Orders the sequence of Location objects in an ArrayList in alphabetical order
 *
 * @author Daryln
 */
public class LocationComparator implements Comparator<Location> {
    
    @Override
    public int compare(Location o1, Location o2) {
        return (o1.getSemanticPlace().compareTo(o2.getSemanticPlace()));
    }
}

