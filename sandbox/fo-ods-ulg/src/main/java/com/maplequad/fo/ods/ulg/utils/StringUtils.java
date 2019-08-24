package com.maplequad.fo.ods.ulg.utils;

import java.util.stream.IntStream;

/***
 * StringUtils - all tricky and repeating string manipulations at one place
 *
 * @author Madhav Mindhe
 * @since :   23/07/2017
 */
public class StringUtils {

    private static StringUtils stringUtils;

    /***
     * Added a private constructor to hide access to implicit public one
     *
     * */
    private StringUtils() {

    }

    /**
     * This method provides threadsafe access to the singleton instance of StringUtils
     *
     * @return instance of StringUtils
     */
    public static synchronized StringUtils getInstance() {
        if (null == stringUtils) {
            stringUtils = new StringUtils();
        }
        return stringUtils;
    }

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
}