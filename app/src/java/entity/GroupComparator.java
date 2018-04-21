/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Orders the sequence within a list of Group objects
 *
 * @author Keng Yew
 */
public class GroupComparator implements Comparator<Group> {

    @Override
    public int compare(Group o1, Group o2) {
        int compareSize = (o1.size() - o2.size()) * (-1);
        if (compareSize != 0) {
            return compareSize;
        }

        int compareTime = (o1.getTotalTime() - o2.getTotalTime()) * (-1);

        if (compareTime != 0) {
            return compareTime;
        }

        ArrayList<Companion> o1List = o1.getGroup();
        ArrayList<Companion> o2List = o2.getGroup();

        Collections.sort(o1List, new AutoGroupMacAddressComparator());
        Collections.sort(o2List, new AutoGroupMacAddressComparator());

        for (int i = 0; i < o1List.size(); i++) {
            Companion o1Companion = o1List.get(i);
            Companion o2Companion = o2List.get(i);

            int compareMac = o1Companion.getMacAddress().compareTo(o2Companion.getMacAddress());
            if (compareMac != 0) {
                return compareMac;
            }
        }

        return 0;
    }
}
