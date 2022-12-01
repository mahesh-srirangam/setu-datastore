/**
 * ****************************************************************************
 *
 * Copyright (c) 2022, FarEye and/or its affiliates. All rights
 * reserved.
 * ___________________________________________________________________________________
 *
 *
 * NOTICE: All information contained herein is, and remains the property of
 * FaEye and its suppliers,if any. The intellectual and technical concepts
 * contained herein are proprietary to FarEye. and its suppliers and
 * may be covered by us and Foreign Patents, patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from FarEye
 */
package com.fareyeconnect.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import io.quarkus.logging.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

/**
 *
 * @author Baldeep Singh Kwatra
 * @since 16-Oct-2022, 8:13:32 PM
 */
public class CryptoUtil {

    /**
     * Encrypts the plain text with the given password by using algorithm
     * AES/CBC/PKCS5Padding
     *
     * @param plainText
     * @param password
     * @return
     */
    public static String encrypt(String plainText, String password) {
        return encrypt(plainText, password, "AES/CBC/PKCS5Padding");
    }

    /**
     * Encrypts the plain text with the given password & algorithm
     *
     * @param plainText
     * @param password
     * @param algorithm
     * @return
     */
    public static String encrypt(String plainText, String password, String algorithm) {
        try {

            byte[] salt = generateSalt();
            SecretKey secret = generateKey(password, salt);
            IvParameterSpec ivSpec = generateIv();

            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secret, ivSpec);
            byte[] encryptedText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // concatenate salt + iv + ciphertext
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(salt);
            outputStream.write(ivSpec.getIV());
            outputStream.write(encryptedText);

            // properly encode the complete ciphertext
            return Base64.getEncoder()
                    .encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            Log.error("Unable to encrypt {}", e);
        }
        return null;
    }

    /**
     * Decrypts cipherText with the given password by using algorithm
     * AES/CBC/PKCS5Padding
     *
     * @param cipherText
     * @param password
     * @return
     */
    public static String decrypt(String cipherText, String password) {
        return decrypt(cipherText, password, "AES/CBC/PKCS5Padding");
    }

    /**
     * Decrypts the cipherText with given password & algorithm
     *
     * @param cipherText
     * @param password
     * @param algorithm
     * @return
     */
    public static String decrypt(String cipherText, String password, String algorithm) {
        try {
            byte[] ciphertextBytes = Base64.getDecoder().decode(cipherText);
            if (ciphertextBytes.length < 48) {
                return null;
            }
            byte[] salt = Arrays.copyOfRange(ciphertextBytes, 0, 16);
            byte[] iv = Arrays.copyOfRange(ciphertextBytes, 16, 32);
            byte[] ct = Arrays.copyOfRange(ciphertextBytes, 32, ciphertextBytes.length);

            SecretKey secret = generateKey(password, salt);
            Cipher cipher = Cipher.getInstance(algorithm);

            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
            byte[] plaintext = cipher.doFinal(ct);

            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.error("Unable to decrypt {}", e);
        }
        return null;
    }

    /**
     * Generates a key with given password & salt
     *
     * @param password
     * @param salt
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static SecretKey generateKey(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    /**
     * To be used when salt is string in key generation process
     *
     * @param password
     * @param salt
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static SecretKey generateKey(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        return generateKey(password, salt.getBytes());

    }

    /**
     * Generates a random IV
     *
     * @return
     */
    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    /**
     * Generates a random Salt
     *
     * @return
     */
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    void encryptFile(String content, String fileName, String password, String algorithm)
            throws InvalidKeyException, IOException {
        try {
            byte[] salt = generateSalt();
            SecretKey secret = generateKey(password, salt);
            IvParameterSpec ivSpec = generateIv();

            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secret, ivSpec);

            try (FileOutputStream fileOut = new FileOutputStream(fileName);
                    CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher)) {
                fileOut.write(salt);
                fileOut.write(ivSpec.getIV());
                cipherOut.write(content.getBytes());
            }
        } catch (Exception e) {
            Log.error("Error encryptFile{}", e);
        }

    }

    String decryptFile(String fileName, String password, String algorithm)
            throws InvalidAlgorithmParameterException, InvalidKeyException, IOException {
        try {
            String content;
            try (FileInputStream fileIn = new FileInputStream(fileName)) {
                // read salt
                byte[] salt = new byte[16];
                int saltBytesRead = fileIn.read(salt);
                if (saltBytesRead < 16) {
                    Log.errorf("Invalid Salt length %e", saltBytesRead);
                    return null;
                }
                // read iv
                byte[] fileIv = new byte[16];
                int ivBytesRead = fileIn.read(fileIv);
                if (ivBytesRead < 16) {
                    Log.errorf("Invalid IV length %e", ivBytesRead);
                    return null;
                }

                SecretKey secret = generateKey(password, salt);

                Cipher cipher = Cipher.getInstance(algorithm);
                cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(fileIv));

                try (CipherInputStream cipherIn = new CipherInputStream(fileIn, cipher);
                        InputStreamReader inputReader = new InputStreamReader(cipherIn);
                        BufferedReader reader = new BufferedReader(inputReader)) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    content = sb.toString();
                }
            }
            return content;
        } catch (Exception e) {
            Log.error("Decrypting file {}", e);
            return null;
        }
    }
}