/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.util.Comparator;

/**
 * Orders the sequence of macaddresses within group
 *
 * @author Keng Yew
 */
public class AutoGroupMacAddressComparator implements Comparator<Companion>{
    @Override
    public int compare(Companion o1, Companion o2) {
        
        return o1.getMacAddress().compareTo(o2.getMacAddress());
        
    }
}
