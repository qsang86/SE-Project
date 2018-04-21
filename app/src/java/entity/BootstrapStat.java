/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class which contains validated data from the CSV files
 * <br>
 * <br> correctRows - ArrayList&lt;String[]&gt; of validated records in the CSV file
 * <br> bootstrapErrors - ArrayList&lt;{@link BootstrapError}&gt; of errors in the CSV file
 * <br> fileName - Name of CSV file
 * <br> numRecords - number of correct records in the CSV file
 * <br> rowNum - HashMap&lt;String,Integer&gt; for UserLocationController, rowNum is a hashmap for containing the timestamp + macaddress and the row number of that particular row to check for duplicates
 *
 * @author HP
 */
public class BootstrapStat {
    private final ArrayList<String[]> correctRows;
    private final ArrayList<BootstrapError> bootstrapErrors;
    private String fileName; 
    private int numRecords;
    private HashMap<String,Integer> rowNum;
    
    /**
     * <br> correctRows - ArrayList&lt;String[]&gt; of validated records in the CSV file
     * <br> bootstrapErrors - ArrayList&lt;{@link BootstrapError}&gt; of errors in the CSV file
     * <br> fileName - Name of CSV file
     * <br> numRecords - number of correct records in the CSV file
     * <br> rowNum - HashMap&lt;String,Integer&gt; for UserLocationController
     * <br> key - timestamp + macaddress
     * <br> value - row number of CSV file
     *
     */
    public BootstrapStat() {
        bootstrapErrors = new ArrayList<>();
        correctRows = new ArrayList<>();
        fileName = "";
        numRecords = 0;
        rowNum = new HashMap<>();
    }
    
    /**
     *
     * @param name CSV file name
     */
    public void setFileName(String name) {
        fileName = name;
    }
    
    /**
     *
     * @param row String Array of validated fields of the CSV file
     */
    public void addRows(String[] row) {
        correctRows.add(row);
    }
    
    /**
     * Adding errors in the CSV file
     *
     * @param error {@link BootstrapError} of the CSV file
     */
    public void addError(BootstrapError error) {
        bootstrapErrors.add(error);
    }
    
    /**
     *
     * @return ArrayList&lt;String[]&gt; the validated data to be inserted into the database
     */
    public ArrayList<String[]> getCorrectRows() {
        return correctRows;
    }
    
    /**
     *
     * @return The errors in the CSV file
     */
    public ArrayList<BootstrapError> getErrors() {
        return bootstrapErrors;
    }
    
    /**
     * Refresh the size of the number of correct records in the CSV file
     *
     */
    public void refreshNumRecords() {
        numRecords = correctRows.size();
    }
    
    /**
     *
     * @return The number of correct records that are to be inserted into the database
     */
    public int getNumRecords() {
        return numRecords;
    }
    
    /**
     *
     * @return The CSV file name
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * Clears all validated data stored
     *
     */
    public void clear() {
        correctRows.clear();
    }
    
    /**
     * Removes a record in correctRows (usually due to duplicate rows that are already existing in the database)
     *
     * @param rowNum remove a record in the validated data
     */
    public void removeRow(int rowNum) {
        correctRows.remove(rowNum);
    }
    
    /**
     *
     * @param map Assigns a hashmap to rowNum
     */
    public void duplicateKeysAndRowNum(HashMap<String, Integer> map) {
        rowNum = (HashMap<String,Integer>)map.clone();
    }
    
    /**
     *
     * @return rowNum
     */
    public HashMap<String,Integer> getRowNum() {
        return rowNum;
    }
}
