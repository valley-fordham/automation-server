package com.glenfordham.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Helper functions relating to Strings
 */
public class StringUtils {

    /**
     * Gets a String object from an InputStream and closes the InputStream
     *
     * @param inputStream the InputStream to extract the String from
     * @return a String containing the text from the InputStream
     * @throws IOException if an error occurs when reading the text
     */
    public static String getStringFromStream(InputStream inputStream) throws IOException {
        try (InputStreamReader isReader = new InputStreamReader(inputStream)) {
            BufferedReader reader = new BufferedReader(isReader);
            StringBuilder stringBuilder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                stringBuilder.append(str);
            }
            return stringBuilder.toString();
        }
    }

    private StringUtils() {}
}
