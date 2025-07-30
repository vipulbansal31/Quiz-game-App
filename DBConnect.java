package connection;

import java.sql.*;

public class DBConnect {
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            return DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:XE",
                    "quiz", "game"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
