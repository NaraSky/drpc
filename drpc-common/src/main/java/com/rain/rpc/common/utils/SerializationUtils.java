package com.rain.rpc.common.utils;

import java.util.stream.IntStream;

public class SerializationUtils {

    private static final String PADDING_STRING = "0";

    /**
     * The maximum length of serialization type is 16
     */
    public static final int MAX_SERIALIZATION_TYPE_COUNT = 16;

    /**
     * Pad the string with zeros to make it 16 characters long
     *
     * @param str the original string
     * @return the padded string
     */
    public static String paddingString(String str) {
        str = transNullToEmpty(str);
        if (str.length() >= MAX_SERIALIZATION_TYPE_COUNT) return str;
        int paddingCount = MAX_SERIALIZATION_TYPE_COUNT - str.length();
        StringBuilder paddingString = new StringBuilder(str);
        IntStream.range(0, paddingCount).forEach((i) -> {
            paddingString.append(PADDING_STRING);
        });
        return paddingString.toString();
    }

    /**
     * Remove zeros from the string
     *
     * @param str the original string
     * @return the string after removing zeros
     */
    public static String subString(String str) {
        str = transNullToEmpty(str);
        return str.replace(PADDING_STRING, "");
    }

    /**
     * Convert null to empty string
     *
     * @param str the string to convert
     * @return empty string if the input is null, otherwise the original string
     */
    public static String transNullToEmpty(String str) {
        return str == null ? "" : str;
    }
}