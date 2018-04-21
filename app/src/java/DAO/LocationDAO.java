package DAO;

import entity.Location;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Interacts directly with the database to run SQL queries that are related to locations 
 *
 * @author Daryln
 */
public class LocationDAO {

    /**
     * Adds validated location-lookup.csv records to the database
     *
     * @param locationList ArrayList&lt;String[]&gt; of valid location-lookup.csv records to be inserted into the database
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void addLocation(ArrayList<String[]> locationList) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int counter = 0;
        int totalRows = locationList.size();
        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement("insert into location(location_id,semantic_place) values (?,?)");
            for (String[] location : locationList) {
                String locationId = location[0];
                String semanticPlace = location[1];

                pstmt.setString(1, locationId);
                pstmt.setString(2, semanticPlace);
                pstmt.addBatch();

                counter++;
                totalRows--;
                if (counter == 1000 || totalRows < 1000) {
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
     * Deletes all location-lookup.csv data
     *
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void deleteLocationData() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("truncate location");
            pstmt.executeUpdate();
        } finally {
            ConnectionManager.close(conn, pstmt, null);
        }

    }

    /**
     * Validates if locationID exists in database
     *
     * @param locationId location-id field in location.csv file
     * @return boolean false if locationID does not exist, true if it exists
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public boolean checkValidUserLocationId(String locationId) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int realID = Integer.parseInt(locationId);
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select count(*) from location where location_id=?");
            pstmt.setInt(1, realID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                if (rs.getInt(1) == 1) {
                    return true;
                }
            }

        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }

        return false;
    }

    /**
     * Retrieves all valid location-ids
     *
     * @return ArrayList&lt;{@link String}&gt; an ArrayList of all the location-ids in the database
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static ArrayList<String> retrieveAllLocationIds() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        ArrayList<String> locationIdList = new ArrayList<>();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            conn = ConnectionManager.getConnection();
            String sql = "select location_id from location";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                locationIdList.add(rs.getString("location_id"));
            }

            return locationIdList;
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
    }

    /**
     * Retrieves all the timestamps of users currently at the specified semantic place
     *
     * @param dateTime TimeStamp of type String (yyyy-MM-dd HH:mm:ss)
     * @param semanticPlace String
     * @return ArrayList&lt;{@link Location}&gt; ArrayList of all the timestamps of users currently at specified semantic place
     * @throws SQLException
     */
    public static ArrayList<Location> getCurrentUserLocation(String dateTime, String semanticPlace) throws SQLException {
        ArrayList<Location> locationList = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            pstmt = conn.prepareStatement("select temp1.macaddress from\n"
                    + "	(select macaddress, max(time_stamp),maxLid from ( \n"
                    + "	select macaddress, time_stamp, location_id as maxLid\n"
                    + "	from user_locations\n"
                    + "	where time_stamp >= (? -  interval 15 minute) and time_stamp < ?\n"
                    + "	group by macaddress, time_stamp\n"
                    + "	order by macaddress, time_stamp desc) as t1\n"
                    + "	group by macaddress) as temp1\n"
                    + "where temp1.maxLid in (select location_id from location where semantic_place = ?)");

            pstmt.setString(1, dateTime);
            pstmt.setString(2, dateTime);
            pstmt.setString(3, semanticPlace);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Location location = new Location(semanticPlace, rs.getString(1));
                locationList.add(location);
            }
            return locationList;
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }

    }

    /**
     * Retrieves all the new locations of users at the next 15minute window who were previously at the specified semantic place
     *
     * @param dateTime TimeStamp of type String (yyyy-MM-dd HH:mm:ss)
     * @param semanticPlace String
     * @return LinkedHashMap&lt;String,LinkedHashMap&lt;String,String&gt;&gt; ArrayList of all the new locations of users
     * @throws SQLException
     */
    public static LinkedHashMap<String, LinkedHashMap<String, String>> getNext15minLocation(String dateTime, String semanticPlace) throws SQLException {

        LinkedHashMap<String, LinkedHashMap<String, String>> locationList = new LinkedHashMap<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            pstmt = conn.prepareStatement("select macaddress, semantic_place, time_stamp\n"
                    + "from user_locations ul\n"
                    + "inner join location l on ul.location_id = l.location_id\n"
                    + "where macaddress in (\n"
                    + "	select temp1.macaddress from\n"
                    + "		(select macaddress, max(time_stamp),maxLid from ( \n"
                    + "		select macaddress, time_stamp, location_id as maxLid\n"
                    + "		from user_locations\n"
                    + "		where time_stamp >= (? -  interval 15 minute) and time_stamp < ?\n"
                    + "		group by macaddress, time_stamp\n"
                    + "		order by macaddress, time_stamp desc) as t1\n"
                    + "		group by macaddress) as temp1\n"
                    + "	where temp1.maxLid in (select location_id from location where semantic_place = ?)\n"
                    + "and time_stamp < (? +  interval 15 minute) and time_stamp >= ?)\n"
                    + "order by macaddress, time_stamp desc;");
            pstmt.setString(1, dateTime);
            pstmt.setString(2, dateTime);
            pstmt.setString(3, semanticPlace);
            pstmt.setString(4, dateTime);
            pstmt.setString(5, dateTime);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                LinkedHashMap<String, String> timePlace = new LinkedHashMap<>();
                if (!locationList.keySet().isEmpty()) {
                    if (locationList.keySet().contains(rs.getString(1))) {
                        timePlace = locationList.get(rs.getString(1));
                    }
                }
                timePlace.put(rs.getString(3), rs.getString(2));
                locationList.put(rs.getString(1), timePlace);
            }
            return locationList;
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
    }

    /**
     * Returns all the semantic places in the database
     *
     * @return ArrayList&lt;{@link String}&gt; list of all the semantic places in the database
     * @throws SQLException
     */
    public static ArrayList<String> getAllSemanticPlace() throws SQLException {
        ArrayList<String> spList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            String sql = "select distinct semantic_place from location;";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                spList.add(rs.getString(1));
            }
            return spList;
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }

    }
}
