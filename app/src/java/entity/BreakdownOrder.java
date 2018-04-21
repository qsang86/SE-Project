/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Contains the following attributes:
 * <br>
 * <br> first - TreeMap&lt;String,ArrayList&lt;User&gt;&gt; the breakdown of the first option in the order
 * <br> second - TreeMap&lt;String,TreeMap&lt;String,ArrayList&lt;User&gt;&gt;&gt; the breakdown of the second option in the order
 * <br> third - TreeMap&lt;String,TreeMap&lt;String,ArrayList&lt;User&gt;&gt;&gt; the breakdown of the third option in the order
 *
 * @author Keng Yew
 */
public class BreakdownOrder {
    private TreeMap<String, ArrayList<User>> first;
    private TreeMap<String, TreeMap<String, ArrayList<User>>> second;
    private TreeMap<String, TreeMap<String, ArrayList<User>>> third;
    
    /**
     * <br> first - TreeMap&lt;String,ArrayList&lt;User&gt;&gt;
     * <br> key - First option name. Eg. If first option in order is school, then "sis", "economics", "accountancy" etc
     * <br> value - ArrayList of users according to each option name
     * <br>
     * 
     * 
     * <br> second - TreeMap&lt;String,TreeMap&lt;String,ArrayList&lt;User&gt;&gt;&gt; the breakdown of the second option in the order
     * <br> key - First option name. Eg. If first option in order is school, then "sis", "economics", "accountancy" etc
     * <br> value - second option breakdown (TreeMap)
     * <br> inner TreeMap
     * <br> key - second option name, Eg. if second option in order is gender, then "M" or "F"
     * <br> value - ArrayList of users according to each option name
     * <br>
     * 
     * 
     * <br> third - TreeMap&lt;String,TreeMap&lt;String,ArrayList&lt;User&gt;&gt;&gt;
     * <br> key - first option name + second option name
     * <br> value - third option breakdown (TreeMap)
     * <br> inner TreeMap
     * <br> key - third option name, eg. if third option in order is gender, then "M" or "F"
     * <br> value - ArrayList of users according to each option name
     * 
     */
    public BreakdownOrder(){
        first = new TreeMap<>();
        second = new TreeMap<>();
        third = new TreeMap<>();
    }
    
    /**
     *
     * @return The first option breakdown
     */
    public TreeMap<String, ArrayList<User>> getFirst(){
        return first;
    }
    
    /**
     *
     * @return The second option breakdown
     */
    public TreeMap<String, TreeMap<String, ArrayList<User>>> getSecond(){
        return second;
    }
    
    /**
     *
     * @return The third option breakdown
     */
    public TreeMap<String, TreeMap<String, ArrayList<User>>> getThird(){
        return third;
    }
    
    /**
     * Refer to {@link #BreakdownOrder() BreakdownOrder} for more details
     *
     * @param firstOption first option breakdown
     */
    public void setFirst(TreeMap<String, ArrayList<User>> firstOption){
        //setting the first breakdown into a treemap
        //key --> option name, eg. if first option in order is school, then "sis", "economics", "accountancy" etc
        //value --> ArrayList of users according to each option
        first = (TreeMap<String,ArrayList<User>>)firstOption.clone();
    }
    
    /**
     * Refer to {@link #BreakdownOrder() BreakdownOrder} for more details
     *
     * @param optionName first option name
     * @param secondOption second option breakdown
     */
    public void setSecond(String optionName, TreeMap<String, ArrayList<User>> secondOption){
        /*outer TreeMap:
        key --> first option name, eg. if first option in order is school, then "sis", "economics", "accountancy" etc
        value --> second option breakdown (TreeMap)
        
        inner TreeMap
        key --> second option name, eg. if second option in order is gender, then "M" or "F"
        value --> ArrayList of users according to each option
        */
        second.put(optionName, secondOption);
    }
    
    /**
     * Refer to {@link #BreakdownOrder() BreakdownOrder} for more details
     *
     * @param firstOptionName first option name
     * @param secondOptionName second option name
     * @param thirdOption third option breakdown
     */
    public void setThird(String firstOptionName, String secondOptionName, TreeMap<String, ArrayList<User>> thirdOption){
        /*outer TreeMap:
        key --> first option name + second option name
        value --> third option breakdown (TreeMap)
        
        inner TreeMap
        key --> third option name, eg. if third option in order is gender, then "M" or "F"
        value --> ArrayList of users according to each option
        */
        third.put(firstOptionName+secondOptionName, thirdOption);
    }
}
