/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.util.Comparator;

/**
 * Orders the sequence of members within a group
 *
 * @author Keng Yew
 */
public class AutoGroupMemberComparator implements Comparator<Companion> {

    @Override
    public int compare(Companion o1, Companion o2) {

        String email = o1.getEmail();
        String otherEmail = o2.getEmail();

        if (email == null && otherEmail != null) {
            return 1;
        } else if (email != null && otherEmail == null) {
            return -1;
        }

        return o1.getMacAddress().compareTo(o2.getMacAddress());
    }
}
