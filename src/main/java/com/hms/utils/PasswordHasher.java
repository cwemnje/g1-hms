package com.hms.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHasher {

    // Private constructor — utility class, no instantiation needed
    private PasswordHasher() {}

    // Generate a random salt
    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // Hash a password with a salt
    public static String hashPassword(String plainPassword) {
        try {
            String salt = generateSalt();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashedBytes = md.digest(plainPassword.getBytes());
            String hashedPassword = Base64.getEncoder().encodeToString(hashedBytes);

            // Store as salt:hash so we can verify later
            return salt + ":" + hashedPassword;

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Hashing error: " + e.getMessage());
            return null;
        }
    }

    // Verify a plain password against a stored hash
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        try {
            // Split the stored value back into salt and hash
            String[] parts = storedHash.split(":");
            String salt = parts[0];
            String originalHash = parts[1];

            // Rehash the input password with the same salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashedBytes = md.digest(plainPassword.getBytes());
            String newHash = Base64.getEncoder().encodeToString(hashedBytes);

            // Compare
            return newHash.equals(originalHash);

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Verification error: " + e.getMessage());
            return false;
        }
    }
}