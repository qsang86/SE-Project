/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.util.ArrayList;

/**
 * A class to contain details of the error message such as:
 * <br> Name of the CSV file (demographics.csv, location.csv, location-lookup.csv)
 * <br> Row number of the error in the CSV file
 * <br> Types of errors within one error message
 *
 * @author Keng Yew
 */
public class BootstrapError implements Comparable<BootstrapError> {
    private String fileName;
    private int line;
    private ArrayList<String> errorMsg;
    
    /**
     *
     * @param fileName Name of CSV file (demographics.csv, location.csv, location-lookup.csv)
     * @param line Row number of the error in the CSV file
     * @param errorMsg Types of errors within one error message
     */
    public BootstrapError(String fileName, int line, ArrayList<String> errorMsg) {
        this.fileName = fileName;
        this.line = line;
        this.errorMsg = errorMsg;
    }
    
    /**
     * Returns the CSV file name
     *
     * @return the CSV file name
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * Returns the row number of the error in the CSV file
     *
     * @return Row number of the error in the CSV file
     */
    public int getLine() {
        return line;
    }
    
    /**
     * Returns the types of errors within on error message
     *
     * @return ArrayList&lt;String&gt; Types of errors within one error message
     */
    public ArrayList<String> getErrorMsg() {
        return errorMsg;
    }
    
    /**
     * 
     * @return String which outputs "Error" + {@link #getFileName() getFileName} + line + errorMsg
     */
    @Override
    public String toString() {
        return "Error: " + getFileName() + line + errorMsg;
    }
    
    @Override
    public int compareTo(BootstrapError anotherError) {
        return line - anotherError.line;
    }
}
