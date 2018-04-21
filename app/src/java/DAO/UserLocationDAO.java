/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import entity.Companion;
import entity.Location;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *Interacts directly with the database to run SQL queries that are related to user locations
 * 
 * @author Keng Yew
 */
public class UserLocationDAO {
    
    /**
     * Inserts validated records of location.csv through a temp file into the database
     * 
     * @param pathName path of temp file created which contains validated records of location.csv that are to be added into the database
     * @throws IOException
     * @throws SQLException
     */
    public static void addUserLocationByFile(String pathName) throws IOException, SQLException {
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        File file = null;
        String sql = "load data local infile '" + pathName +"' into table user_locations "
                    + " fields terminated by ','"
                    + " enclosed by '\"'"
                    + " lines terminated by '\\n'";

        try {
            file = new File(pathName);
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.executeQuery();
        } finally {
            if (file!=null) {
                file.delete();
            }
            ConnectionManager.close(conn,pstmt,null);
        }

    }
    
    /**
     * Adding foreign keys
     *
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void addForeignKeys() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("alter table user_locations add constraint user_locations_fk foreign key (location_id) references location (location_id)");
            pstmt.executeUpdate();
        } finally {
            ConnectionManager.close(conn, pstmt, null);
        }

    }
    
    /**
     * Remove foreign keys
     *
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void removeForeignKeys() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("alter table user_locations drop foreign key user_locations_fk");
            pstmt.executeUpdate();
        } finally {
            ConnectionManager.close(conn, pstmt, null);
        }

    }
    
    /**
     * Delete all user location data in the database
     *
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void deleteUserLocationData() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("truncate user_locations");
            pstmt.executeUpdate();
        } finally {
            ConnectionManager.close(conn, pstmt, null);
        }

    }
    
    /**
     * Retrieves all records from user_location table in database (location.csv data)
     *
     * @return HashMap&lt;String,String&gt;
     * <br> key - timestamp + macaddress
     * <br> value - location-id
     * @throws SQLException
     */
    public static HashMap<String,String> retrieveAll() throws SQLException {
        HashMap<String,String> userLocationMap = new HashMap<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM user_locations");
            rs = stmt.executeQuery();
            while (rs.next()) {
                userLocationMap.put(rs.getString(1)+rs.getString(2), rs.getString(3));
            }
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        
        return userLocationMap;
    }

    /**
     * Retrieves the locations and number of users at each respective semantic place
     *
     * @param dateTime TimeStamp of type String (yyyy-MM-dd HH:mm:ss)
     * @return ArrayList&lt;{@link Location}&gt; list of the locations within the specified timestamp
     * @throws SQLException
     */
    public static ArrayList<Location> getHeatmap(String dateTime) throws SQLException {
        ArrayList<Location> heatmap = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
  
        try {
			conn = ConnectionManager.getConnection();
                        // retrieve semantic place and total sum of macaddress at each semantic place for all floors within specified datetime
			pstmt = conn.prepareStatement("select semantic_place, sum(totalmacaddress) from location l left outer join \n" +
                    "(select location_id, count(the_macaddress) as totalmacaddress from\n" +
                    "(select temp.macaddress as the_macaddress, location_id from \n" +
                    "(select macaddress,max(time_stamp) as maximum_time from user_locations where time_stamp>=(? - interval 15 minute) and time_stamp <(?) group by macaddress) as temp, user_locations ul\n" +
                    "where temp.macaddress=ul.macaddress and maximum_time=ul.time_stamp) as temp2 group by location_id) as temp3 on l.location_id=temp3.location_id group by semantic_place order by sum(totalmacaddress) desc;");
            
            pstmt.setString(1, dateTime);
            pstmt.setString(2, dateTime);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                heatmap.add(new Location(rs.getString(1), rs.getInt(2)));
            }
        } 
		finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        
        return heatmap;
    }
    
    //top k companion

    /**
     * Retrieves user which only has one record in the entire user_locations table (location.csv)
     *
     * @param dateTime TimeStamp of type String (yyyy-MM-dd HH:mm:ss)
     * @param macaddress
     * @return {@link Companion}
     * @throws SQLException
     */
    public static Companion getSingleRowSpecifiedUser(String dateTime, String macaddress) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Companion specifiedUser = new Companion(macaddress);
        Timestamp tempCurrentEndTime = null;
        Timestamp specifiedEndTime = Timestamp.valueOf(dateTime);
        
        try {
            conn = ConnectionManager.getConnection();
            //get single record user
            pstmt = conn.prepareStatement("select temp.location_id,temp.time_stamp,temp.duration,email from users right outer join "
                    + "(select macaddress,location_id, time_stamp, timestampdiff(second,time_stamp,?) as duration from " 
                    + "(select macaddress,count(location_id) as num, location_id, time_stamp from user_locations where time_stamp>=(? - interval 15 minute) " 
                    + "and time_stamp <? and macaddress=? group by macaddress) as t1 where num=1) as temp on temp.macaddress=users.macaddress;");
            
            pstmt.setString(1, dateTime);
            pstmt.setString(2, dateTime);
            pstmt.setString(3, dateTime);
            pstmt.setString(4, macaddress);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String location = rs.getString(1);
                Timestamp startTime = rs.getTimestamp(2);
                int duration = rs.getInt(3);
                String email = rs.getString(4);
                
                if (duration > 300) {
                    duration = 300;
                    tempCurrentEndTime = new Timestamp(startTime.getTime() + 5*60*1000);
                }
                
                specifiedUser.setDuration(location, duration);
                specifiedUser.setEmail(email);
                if (tempCurrentEndTime != null) {
                    specifiedUser.setPeriod(location, startTime, tempCurrentEndTime);
                    tempCurrentEndTime = null;
                } else {
                    specifiedUser.setPeriod(location, startTime, specifiedEndTime);
                }
            }
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        
        if (!specifiedUser.hasRecords()) {
            return null;
        }
        
        
        return specifiedUser;
    }
    
    /**
     * Retrieves user with multiple records in the user_locations table (location.csv)
     *
     * @param dateTime TimeStamp of type String (yyyy-MM-dd HH:mm:ss)
     * @param macaddress
     * @return {@link Companion}
     * @throws SQLException
     */
    public static Companion getSpecifiedUser(String dateTime, String macaddress) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Companion specifiedUser = new Companion(macaddress);
        int currentDuration = 0;
        String currentLocation = "";
        Timestamp currentStartTime = null;
        Timestamp currentEndTime = null;
        Timestamp tempCurrentEndTime = null;
        
        try {
            conn = ConnectionManager.getConnection();
            // get the first room occurance, second room ocurance, start time, end time, durationm and email
            pstmt = conn.prepareStatement("select temp.firstroom,temp.secondroom,temp.start_time,temp.end_time,temp.duration,email from users right outer join "
                    + "(select t1.macaddress,t1.location_id as firstroom,t2.location_id as secondroom,start_time,end_time,timestampdiff(second,start_time,end_time) as duration from " 
                    + "(select macaddress, location_id,time_stamp as start_time from user_locations where  time_stamp>=(? - interval 15 minute) and time_stamp <? group by macaddress,location_id,time_stamp) as t1, " 
                    + "(select macaddress, location_id,time_stamp as end_time from user_locations where " 
                    + "time_stamp>=(? - interval 15 minute) and time_stamp <? group by macaddress,location_id,time_stamp) as t2  " 
                    + "where start_time<end_time and t1.macaddress=t2.macaddress and t1.macaddress=? group by t1.macaddress,start_time order by t1.macaddress,start_time) "
                    + "as temp on users.macaddress=temp.macaddress;");
            
            
            pstmt.setString(1, dateTime);
            pstmt.setString(2, dateTime);
            pstmt.setString(3, dateTime);
            pstmt.setString(4, dateTime);
            pstmt.setString(5, macaddress);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String location = rs.getString(1);
                int duration = rs.getInt(5);
                Timestamp startTime = rs.getTimestamp(3);
                Timestamp endTime = rs.getTimestamp(4);
                
                
                
                
                if (currentDuration > 300) {
                    currentDuration = 300;
                    tempCurrentEndTime = new Timestamp(currentStartTime.getTime() + 5*60*1000);
                }
                
                if (!rs.isFirst()) {
                    if (specifiedUser.getDuration(currentLocation) != 0) {
                        int previousTiming = specifiedUser.getDuration(currentLocation);
                        currentDuration += previousTiming;
                        specifiedUser.setDuration(currentLocation, currentDuration);
                        if (tempCurrentEndTime != null) {
                            specifiedUser.setPeriod(currentLocation, currentStartTime, tempCurrentEndTime);
                            tempCurrentEndTime = null;
                        } else {
                            specifiedUser.setPeriod(currentLocation, currentStartTime, currentEndTime);
                        }
                    } else {
                        specifiedUser.setDuration(currentLocation, currentDuration);
                        if (tempCurrentEndTime != null) {
                            specifiedUser.setPeriod(currentLocation, currentStartTime, tempCurrentEndTime);
                            tempCurrentEndTime = null;
                        } else {
                            specifiedUser.setPeriod(currentLocation, currentStartTime, currentEndTime);
                        }
                    }
                }
                
                currentLocation = location;
                currentStartTime = startTime;
                currentEndTime = endTime;
                currentDuration = duration;
                
                
                if (rs.isLast()) {
                    specifiedUser.setEmail(rs.getString(6));
                    
                    if (currentDuration > 300) {
                        currentDuration = 300;
                        tempCurrentEndTime = new Timestamp(currentStartTime.getTime() + 5*60*1000);
                    }
                    
                    if (specifiedUser.getDuration(currentLocation) != 0) {
                        int previousTiming = specifiedUser.getDuration(currentLocation);
                        currentDuration += previousTiming;
                        specifiedUser.setDuration(currentLocation, currentDuration);
                        if (tempCurrentEndTime != null) {
                            specifiedUser.setPeriod(currentLocation, currentStartTime, tempCurrentEndTime);
                            tempCurrentEndTime = null;
                        } else {
                            specifiedUser.setPeriod(currentLocation, currentStartTime, currentEndTime);
                        }
                    } else {
                        specifiedUser.setDuration(currentLocation, currentDuration);
                        if (tempCurrentEndTime != null) {
                            specifiedUser.setPeriod(currentLocation, currentStartTime, tempCurrentEndTime);
                            tempCurrentEndTime = null;
                        } else {
                            specifiedUser.setPeriod(currentLocation, currentStartTime, currentEndTime);
                        }
                    }
                    
                    String lastLocation = rs.getString(2);
                    Timestamp chosenTime = Timestamp.valueOf(dateTime);
                    int lastDuration = (int)((chosenTime.getTime() - currentEndTime.getTime())/1000);
                    if (lastDuration > 300) {
                        lastDuration = 300;
                        tempCurrentEndTime = new Timestamp(currentEndTime.getTime() + 5*60*1000);
                    }
                    
                    
                    if (specifiedUser.getDuration(lastLocation) != 0) {
                        int previousTiming = specifiedUser.getDuration(lastLocation);
                        lastDuration += previousTiming;
                        specifiedUser.setDuration(lastLocation, lastDuration);
                        if (tempCurrentEndTime != null) {
                            specifiedUser.setPeriod(lastLocation, currentEndTime, tempCurrentEndTime);
                            tempCurrentEndTime = null;
                        } else {
                            specifiedUser.setPeriod(lastLocation, currentEndTime, chosenTime);
                        }
                    } else {
                        specifiedUser.setDuration(lastLocation, lastDuration);
                        if (tempCurrentEndTime != null) {
                            specifiedUser.setPeriod(lastLocation, currentEndTime, tempCurrentEndTime);
                            tempCurrentEndTime = null;
                        } else {
                            specifiedUser.setPeriod(lastLocation, currentEndTime, chosenTime);
                        }
                    }
                }
            }
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        
        return specifiedUser;
    }
    
    /**
     * Retrieves other users with only one record in the user_locations table (location.csv)
     *
     * @param dateTime TimeStamp of type String (yyyy-MM-dd HH:mm:ss)
     * @param specifiedMacAddress macaddress of specified user
     * @param locationList list of locations visited by specified user
     * @return ArrayList&lt;{@link Companion}&gt; list of other users
     * @throws SQLException
     */
    public static ArrayList<Companion> getOtherSingleRowUsers(String dateTime, String specifiedMacAddress, ArrayList<String> locationList) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<Companion> companionList = new ArrayList<>();
        Timestamp tempCurrentEndTime = null;
        Timestamp specifiedEndTime = Timestamp.valueOf(dateTime);
        
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select temp.macaddress,temp.location_id,temp.time_stamp,temp.duration,email from users right outer join"
                    + "(select macaddress,location_id, time_stamp, timestampdiff(second,time_stamp,?) as duration from " 
                    + "(select macaddress,count(location_id) as num, location_id, time_stamp from user_locations where time_stamp>=(? - interval 15 minute) " 
                    + "and time_stamp <? and macaddress<>? group by macaddress) as t1 where num=1) as temp on users.macaddress=temp.macaddress;");
            
            pstmt.setString(1, dateTime);
            pstmt.setString(2, dateTime);
            pstmt.setString(3, dateTime);
            pstmt.setString(4, specifiedMacAddress);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String macaddress = rs.getString(1);
                String location = rs.getString(2);
                Timestamp startTime = rs.getTimestamp(3);
                int duration = rs.getInt(4);
                
                if (duration > 300) {
                    duration = 300;
                    tempCurrentEndTime = new Timestamp(startTime.getTime() + 5*60*1000);
                }
                
                if (locationList.contains(location)) {
                    Companion companion = new Companion(macaddress);
                    companion.setDuration(location, duration);
                    if (tempCurrentEndTime != null) {
                        companion.setPeriod(location, startTime, tempCurrentEndTime);
                        tempCurrentEndTime = null;
                    } else {
                        companion.setPeriod(location, startTime, specifiedEndTime);
                    }
                    companion.setEmail(rs.getString(5));
                    companionList.add(companion);
                }
            }
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        
        return companionList;
    }
    
    /**
     * Retrieves list of other users with multiple records in the user_locations table (location.csv)
     *
     * @param dateTime TimeStamp of type String (yyyy-MM-dd HH:mm:ss)
     * @param specifiedMacAddress macaddress of the specified user
     * @return ArrayList&lt;String[]&gt; list of other users 
     * @throws SQLException
     */
    public static ArrayList<String[]> getOtherUsers(String dateTime, String specifiedMacAddress) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<String[]> rawData = new ArrayList<>();
        
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select temp.macaddress,temp.firstroom,temp.secondroom,temp.start_time,temp.end_time,temp.duration,email from users right outer join "
                    + "(select t1.macaddress,t1.location_id as firstroom,t2.location_id as secondroom,start_time,end_time,timestampdiff(second,start_time,end_time) as duration from " 
                    + "(select macaddress, location_id,time_stamp as start_time from user_locations where  time_stamp>=(? - interval 15 minute) and time_stamp <? group by macaddress,location_id,time_stamp) as t1, " 
                    + "(select macaddress, location_id,time_stamp as end_time from user_locations where " 
                    + "time_stamp>=(? - interval 15 minute) and time_stamp <? group by macaddress,location_id,time_stamp) as t2  " 
                    + "where start_time<end_time and t1.macaddress=t2.macaddress and t1.macaddress<>? group by macaddress,start_time order by macaddress,start_time) "
                    + "as temp on users.macaddress=temp.macaddress;");
            
            
            pstmt.setString(1, dateTime);
            pstmt.setString(2, dateTime);
            pstmt.setString(3, dateTime);
            pstmt.setString(4, dateTime);
            pstmt.setString(5, specifiedMacAddress);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                rawData.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)});
            }
            
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
     
        return rawData;
    }
    
    /**
     * Retrieves all data users within the specified timestamp from user_locations table
     *
     * @param dateTime TimeStamp of type String (yyyy-MM-dd HH:mm:ss)
     * @return ArrayList&lt;String[]&gt; list of all user data with multiple records in the user_locations table 
     * @throws SQLException
     */
    public static ArrayList<String[]> getGroup(String dateTime) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<String[]> rawData = new ArrayList<>();
        
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select temp.macaddress,temp.firstroom,temp.secondroom,temp.start_time,temp.end_time,temp.duration,email from users right outer join "
                    + "(select t1.macaddress,t1.location_id as firstroom,t2.location_id as secondroom,start_time,end_time,timestampdiff(second,start_time,end_time) as duration from " 
                    + "(select macaddress, location_id,time_stamp as start_time from user_locations where  time_stamp>=(? - interval 15 minute) and time_stamp <? group by macaddress,location_id,time_stamp) as t1, " 
                    + "(select macaddress, location_id,time_stamp as end_time from user_locations where " 
                    + "time_stamp>=(? - interval 15 minute) and time_stamp <? group by macaddress,location_id,time_stamp) as t2  " 
                    + "where start_time<end_time and t1.macaddress=t2.macaddress group by macaddress,start_time order by macaddress,start_time) as temp on users.macaddress=temp.macaddress;");
            
            
            pstmt.setString(1, dateTime);
            pstmt.setString(2, dateTime);
            pstmt.setString(3, dateTime);
            pstmt.setString(4, dateTime);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                rawData.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)});
            }
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        
        return rawData;
    }
}

