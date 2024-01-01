import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    // Place your database connection details here
    private static final String DB_URL = "jdbc:mysql://localhost:3306/NotownMusicalStore";
    private static final String USER = "root";
    private static final String PASSWORD = "Prashanth@2000";

    public static ResultSet searchRecords(String query) throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static void updateRecord(String query) throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }
}
