package auth;

import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthService {

    public static boolean login(String username, String password) {

        try {

            Connection conn = DBConnection.connect();

            String query =
                    "SELECT * FROM users WHERE username = ? AND password = ?";

            PreparedStatement pst =
                    conn.prepareStatement(query);

            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {

                System.out.println("Login Successful!");
                System.out.println("Welcome: "
                        + rs.getString("full_name"));

                System.out.println("Role: "
                        + rs.getString("role"));

                return true;

            } else {

                System.out.println("Invalid Username or Password!");
                return false;
            }

        } catch (Exception e) {

            System.out.println("Login Error!");
            e.printStackTrace();

            return false;
        }
    }
}