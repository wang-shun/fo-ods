package com.maplequad.fo.ods.tradecore.utils;

import com.google.cloud.Timestamp;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import java.util.stream.IntStream;

/***
 * StringUtils - all tricky and repeating string manipulations at one place
 *
 * @author Madhav Mindhe
 * @since :   23/07/2017
 */
public class StringUtils {

    private static final SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat dd_MMM_YYYY = new SimpleDateFormat("dd-MMM-yyyy");

    private static final int MILLIS_IN_SECOND = 1000;
    private static final int NANOS_IN_MILLI_SECOND = 1000000;
    private static final String DATE_SEPARATOR = "-";
    private static final String COMMA = ",";

    /**
     * This method takes an array of Strings as input and returns a concatenated String
     * that is made up of all the Strings from the input array.
     *
     * @param input
     * @return
     */
    public static String concat(String... input) {
        StringBuilder concatStr = new StringBuilder();
        if (input != null) {
            IntStream.range(0, input.length).forEach(i -> concatStr.append(input[i]));
        }
        return concatStr.toString();
    }

    /**
     * This method takes an array of integers as input and returns a concatenated String
     * that is made up of all the integers separated with commas from the input array.
     *
     * @param input
     * @return
     */
    public static String concat(int... input) {
        StringBuilder concatStr = new StringBuilder();
        if (input != null) {
            IntStream.range(0, input.length).forEach(i -> concatStr.append(input[i]).append(COMMA));
        }
        return concatStr.toString();
    }

    /**
     * This method takes an array of floats as input and returns a concatenated String
     * that is made up of all the floats separated with commas from the input array.
     *
     * @param input
     * @return
     */
    public static String concat(float... input) {
        StringBuilder concatStr = new StringBuilder();
        if (input != null) {
            IntStream.range(0, input.length).forEach(i -> concatStr.append(input[i]).append(COMMA));
        }
        return concatStr.toString();
    }

    /**
     * This method creates a unique sequence id.
     *
     * @return unique long sequence id
     */
    public static long generateLong() {
        final UUID uid = UUID.randomUUID();
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uid.getLeastSignificantBits());
        buffer.putLong(uid.getMostSignificantBits());
        final BigInteger bi = new BigInteger(buffer.array());
        return bi.longValue() & Long.MAX_VALUE;
    }

    /**
     * This method creates a unique sequence id.
     *
     * @return unique String sequence id
     */
    public static String generateString() {
        final UUID uid = UUID.randomUUID();
        return uid.toString();
    }


    /**
     * This method returns year, month and day from the date
     *
     * @param date
     * @return
     */
    public static int[] getYMD(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();
        return new int[]{year, month, day};
    }

    /**
     * This method converts com.google.cloud.Date into java.util.Date
     *
     * @param date in the form of com.google.cloud.Date
     * @return equivalent date in the form of java.util.Date
     */
    public static Date getDate(com.google.cloud.Date date) {
        Date uDate;
        try {
            String strDate = concat(String.valueOf(date.getYear()), DATE_SEPARATOR, String.valueOf(date.getMonth()),
                    DATE_SEPARATOR, String.valueOf(date.getDayOfMonth()));
            uDate = YYYY_MM_DD.parse(strDate);
        } catch (ParseException pe) {
            throw new RuntimeException("Invalid Date Specified as => " + date + pe);
        }
        return uDate;
    }


    /**
     * This method is used to get DateTimeStamp in java.util.Date format from com.google.cloud.Timestamp
     *
     * @param timestamp
     * @return timestamp as an instance of java.util.Date
     */
    public static Date getDate(Timestamp timestamp) {
        return new Date((timestamp.getSeconds() * MILLIS_IN_SECOND) + (timestamp.getNanos() / NANOS_IN_MILLI_SECOND));
    }

    /**
     * This methods is used to get the String representation of provided date in dd-MMM-YYYY format
     *
     * @param date
     * @return String representation of provided date in dd-MMM-YYYY format
     */
    public static String format_dd_MMM_YYYY(Date date) {
        return dd_MMM_YYYY.format(date);
    }
}