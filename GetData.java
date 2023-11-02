import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeSet;
import java.util.Vector;

import org.json.JSONObject;
import org.json.JSONArray;

public class GetData {

    static String prefix = "project3.";

    // You must use the following variable as the JDBC connection
    Connection oracleConnection = null;

    // You must refer to the following variables for the corresponding
    // tables in your database
    String userTableName = null;
    String friendsTableName = null;
    String cityTableName = null;
    String currentCityTableName = null;
    String hometownCityTableName = null;

    // DO NOT modify this constructor
    public GetData(String u, Connection c) {
        super();
        String dataType = u;
        oracleConnection = c;
        userTableName = prefix + dataType + "_USERS";
        friendsTableName = prefix + dataType + "_FRIENDS";
        cityTableName = prefix + dataType + "_CITIES";
        currentCityTableName = prefix + dataType + "_USER_CURRENT_CITIES";
        hometownCityTableName = prefix + dataType + "_USER_HOMETOWN_CITIES";
    }

    @SuppressWarnings("unchecked")
    public JSONArray toJSON() throws SQLException {

        // This is the data structure to store all users' information
        JSONArray users_info = new JSONArray();

        try (Statement stmt = oracleConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rst = stmt.executeQuery(
                "SELECT User_ID, First_Name, Last_Name, Gender, Year_of_Birth, Month_of_Birth, Day_of_Birth " +
                "FROM " + userTableName
            );

            Statement stmt2 = oracleConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            while (rst.next()) {
                JSONObject user = new JSONObject();
                user.put("user_id", rst.getInt(1));
                user.put("first_name", rst.getString(2));
                user.put("last_name", rst.getString(3));
                user.put("gender", rst.getString(4));
                user.put("YOB", rst.getInt(5));
                user.put("MOB", rst.getInt(6));
                user.put("DOB", rst.getInt(7));

                String queryCurrent = "SELECT CT.City_Name, CT.State_Name, CT.Country_Name " +
    				"FROM " + currentCityTableName + " C, " + cityTableName + " CT " +
    				"WHERE CT.City_ID = C.Current_City_ID AND C.User_ID = " + rst.getInt(1);

                String queryHometown = "SELECT C.City_Name, C.State_Name, C.Country_Name " +
                    "FROM " + hometownCityTableName + " H, " + cityTableName + " C " +
                    "WHERE C.City_ID = H.Hometown_City_ID AND H.User_ID = " + rst.getInt(1);

                ResultSet rst2 = stmt2.executeQuery(queryCurrent);
                JSONObject current = new JSONObject();
                if (!rst2.next()) {
                    user.put("current", current);
                }
                else {
                    current.put("city", rst2.getString(1));
                    current.put("state", rst2.getString(2));
                    current.put("country", rst2.getString(3));
                    user.put("current", current);
                }

                ResultSet rst3 = stmt2.executeQuery(queryHometown);
                JSONObject hometown = new JSONObject();
                if (!rst3.next()) {
                    user.put("hometown", hometown);
                }
                else {
                    hometown.put("city", rst3.getString(1));
                    hometown.put("state", rst3.getString(2));
                    hometown.put("country", rst3.getString(3));
                    user.put("hometown", hometown);
                }

                String queryFriends = "SELECT F.USER2_ID " +
    				"FROM " + friendsTableName + " F " +
    				"WHERE F.USER1_ID = " + rst.getInt(1);

                JSONArray friends = new JSONArray();
                ResultSet rst4 = stmt2.executeQuery(queryFriends);
                while (rst4.next()){
                    friends.put(rst4.getInt(1));
                }
                user.put("friends", friends);

                users_info.put(user);
                rst2.close();
                rst3.close();
                rst4.close();
            }

            rst.close();
            stmt2.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return users_info;
    }

    // This outputs to a file "output.json"
    // DO NOT MODIFY this function
    public void writeJSON(JSONArray users_info) {
        try {
            FileWriter file = new FileWriter(System.getProperty("user.dir") + "/output.json");
            file.write(users_info.toString());
            file.flush();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
