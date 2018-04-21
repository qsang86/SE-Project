package DAO;

import entity.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Interacts directly with the database to run SQL queries that are related to users
 *
 * @author Daryln
 */
public class UserDAO {
    
    /**
     * Retrieves a user with the given emailID and password
     *
     * @param emailId the part of the email before the '@'
     * @param password user password
     * @return {@link User}
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static User retrieveUser(String emailId, String password) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException{
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet result = null;
        
        try{
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("select * from users where password=? and email like ?");
            stmt.setString(1, password);
            stmt.setString(2, emailId + "@" + "%" +"smu.edu.sg");
            result = stmt.executeQuery();
            
            if (result.next()){
                if (emailId.equals("admin")){
                    User admin = new User(result.getString(1),result.getString(2),result.getString(4), password, 'M',true);
                    return admin;
                }
                else{
                String sGender = result.getString(5);
                char gender = sGender.charAt(0);
                User student= new User(result.getString(1),result.getString(2),result.getString(4),password,gender,false);
                
                return student;
                } 
            } else {
                return null;
            }
        }
        finally{
            ConnectionManager.close(conn, stmt, result);
        }
    }
    
    
    
    // add specified user into database 

    /**
     * Inserts validated demographics.csv data into the database
     *
     * @param userList list of validated demographics.csv records to be added into the database
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void addUser(ArrayList<String[]> userList) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int counter = 0;
        int totalRows = userList.size();
        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement("insert into users(macaddress,name,password,email,gender) values (?,?,?,?,?)");
            for (String[] line: userList) {
                String macAddress = line[0];
                String name = line[1];
                String password = line[2];
                String email = line[3];
                String gender = line[4];
                
                pstmt.setString(1,macAddress);
                pstmt.setString(2,name);
                pstmt.setString(3,password);
                pstmt.setString(4,email);
                pstmt.setString(5,gender);
                pstmt.addBatch();
                
                counter++;
                totalRows--;
                if (counter==1000 || totalRows<1000) {
                    counter = 0;
                    pstmt.executeBatch();
                    conn.commit();
                }
            }
        } finally {
            ConnectionManager.close(conn, pstmt, null);
        }
    }
    
    /**
     * Deletes all user data in the database
     *
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void deleteUserData() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("truncate users");
            pstmt.executeUpdate();
        } finally {
            ConnectionManager.close(conn, pstmt, null);
        }

    }
    
    /**
     * Adds the admin user data into the database
     *
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void addAdmin() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("insert into users(macaddress,name,password,email,gender) values ('123456789a10bcde1fg1h1i2131j4k1lmn516171','admin','SETeam4','admin@sis.smu.edu.sg','M')");
            pstmt.executeUpdate();
        } finally {
            ConnectionManager.close(conn, pstmt, null);
        }
    }
    
    /**
     * Retrieves all users within the 15minute window of the specified timestamp
     *
     * @param dateTime TimeStamp of type String (yyyy-MM-dd HH:mm:ss)
     * @return ArrayList&lt;{@link User}&gt; list of users within the 15minutes window of the specified timestamp
     * @throws SQLException
     */
    public static ArrayList<User> retrieveAllUsers(String dateTime) throws SQLException{
        ArrayList<User> users = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet result = null;
        try{
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("SELECT u.macaddress, u.name, u.email, u.gender from users u\n" + 
                    "inner join \n" +
                    "(SELECT macaddress FROM `user_locations` WHERE time_stamp >= (? - interval '15' minute) AND time_stamp <= ? group by macaddress) as temp \n" +
                    "ON temp.macaddress = u.macaddress");
            stmt.setString(1, dateTime);
            stmt.setString(2, dateTime);
            result = stmt.executeQuery();
            
            while (result.next()){

                String sGender = result.getString(4);
                char gender = sGender.charAt(0);
                User students= new User(result.getString(1),result.getString(2),result.getString(3),gender);
                
                users.add(students);
               
            } 
            
            return users;
        }
        finally{
            ConnectionManager.close(conn, stmt, result);
        }    
    }
    
    /**
     * Retrieves emails of user according to the specified macaddress
     *
     * @param macaddress
     * @return email String
     * @throws SQLException
     */
    public static String retrieveEmail(String macaddress) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String email = null;
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select email from users where macaddress=?");
            pstmt.setString(1, macaddress);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                email = rs.getString(1);
            }
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        
        return email;
    }
}
