/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

/**
 * <br> macAddress - macaddress of user
 * <br> name - name of user
 * <br> email - email of user
 * <br> password - password of user
 * <br> gender - gender of user
 * <br> admin - true if user is an admin, false if otherwise
 *
 * @author Soon Wei
 */
public class User {
    private String macAddress;
    private String name;
    private String email;
    private String password;
    private char gender;
    private boolean admin;
    
    /**
     *
     * @param macAddress macaddress of user
     * @param name name of user
     * @param email email of user
     * @param password password of user
     * @param gender gender of user
     * @param admin true if user is admin, false if otherwise
     */
    public User(String macAddress, String name, String email, String password, char gender, boolean admin){
        this.macAddress = macAddress;
        this.name = name;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.admin = admin;
    }
    
    /**
     * Initialize a default student user
     *
     * @param macAddress macaddress of user
     * @param name name of user
     * @param email email of user
     * @param gender gender of user
     * 
     * admin = false;
     */
    public User(String macAddress, String name, String email, char gender) {
        this.macAddress = macAddress;
        this.name = name;
        this.email = email;
        this.password = null;
        this.gender = gender;
        this.admin = false;
    }
    
    /**
     *
     * @return macaddress of user
     */
    public String getMacAddress(){
        return macAddress;
    }
    
    /**
     *
     * @return name of user
     */
    public String getName(){
        return name;
    }
    
    /**
     *
     * @return email of user
     */
    public String getEmail(){
        return email;
    }
    
    /**
     *
     * @return password of user
     */
    public String getPassword(){
        return password;
    }
    
    /**
     *
     * @return gender of user
     */
    public char getGender(){
        return gender;
    }
    
    /**
     *
     * @return true if user is an admin, false if otherwise
     */
    public boolean isAdmin(){
        if (admin == true) {
            return true;
        }
        
        return false;
    }
    
    /**
     *
     * @return year of enrollment of student
     */
    public int getYear(){
        String year = "";
        int firstIndex = email.indexOf('@');
        
        year = email.substring(firstIndex-4,firstIndex);
        
        int iYear = Integer.parseInt(year);
        return iYear;
           
    }
    
    /**
     *
     * @return school of student
     */
    public String getSchool(){
        String school = "";
        int firstIndex = email.indexOf('@');
        int secondIndex = email.indexOf(".", firstIndex);
        school = email.substring(firstIndex +1,secondIndex);

        return school;
           
    }
    
    
}
