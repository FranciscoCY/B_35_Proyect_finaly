package nttdata.grupouno.com.operations.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class Util {

    private Util(){

    }

    public static String dateToString(Date date){
        DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
        return formatter.format(date);
    }

    public static Date stringToDate(String dateString) {
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy.MM.dd");
        if (dateString == null){
            return null;
        }
        try {
            return formatter.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date addDay(Date date, int amount){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR,amount);
        return calendar.getTime();
    }

    public static String getMonth(Date date){
        DateFormat formatter = new SimpleDateFormat("MM");
        return formatter.format(date);
    }

    public static String getYear(Date date){
        DateFormat formatter = new SimpleDateFormat("yyyy");
        return formatter.format(date);
    }

    /**
     * Generate cart number
     * @return
     */
    public static String generateCartNumber() {
        SecureRandom random = new SecureRandom();
        return "4152000".concat(String.valueOf(random.nextInt(999999999)));
    }

    /**
     * Encript AES for Card Number Tarjet
     * @param pan
     * @param idClient
     * @return
     */
    public static String encriptAES(String pan, String idClient){
        try {
            IvParameterSpec iv = new IvParameterSpec(idClient.substring(0, 16).getBytes(StandardCharsets.UTF_8));
            SecretKeySpec secretKeySpec = new SecretKeySpec(idClient.substring(0, 16).getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);

            byte[] encripted = cipher.doFinal(pan.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encripted);
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * Desencript AES
     * @param panEncript
     * @param idClient
     * @return
     */
    public static String desencriptAES(String panEncript, String idClient) {
        try {
            IvParameterSpec iv = new IvParameterSpec(idClient.substring(0, 16).getBytes(StandardCharsets.UTF_8));
            SecretKeySpec secretKeySpec = new SecretKeySpec(idClient.substring(0, 16).getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);

            byte[] encripted = cipher.doFinal(Base64.getDecoder().decode(panEncript));
            return new String(encripted);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Generate Hash
     * @param text
     * @return
     */
    public static String generateHash(String text) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA3-256");
            digest.reset();
            digest.update(text.getBytes(StandardCharsets.UTF_8));
            return String.format("%064x", new BigInteger(1, digest.digest()));

        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String dateTimeToString(Date date){
        DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss");
        return formatter.format(date);
    }

    public static Date stringToDateTime(String dateString) {
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy.MM.dd HH.mm.ss");
        if (dateString == null){
            return null;
        }
        try {
            return formatter.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }
}
