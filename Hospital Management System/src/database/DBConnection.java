package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/hospital_db";

    private static final String USER = "postgres";

    private static final String PASSWORD = "Sniper4eva";

    public static Connection connect() {

        try {
            
            Class.forName("org.postgresql.Driver");

            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            System.out.println("Database Connected Successfully!");

            return conn;

        } catch (Exception e) {

            System.out.println("Connection Failed!");

            e.printStackTrace();

            return null;
        }
    }
}