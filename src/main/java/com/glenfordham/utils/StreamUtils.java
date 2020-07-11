package com.glenfordham.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Helper functions relating to Strings
 */
public class StreamUtils {

    /**
     * Gets a String object from an InputStream and closes the InputStream
     *
     * @param inputStream the InputStream to extract the String from
     * @return a String containing the text from the InputStream
     * @throws IOException if an error occurs when reading the text
     */
    public static String getString(InputStream inputStream) throws IOException {
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

    /**
     * Writes text to an OutputStream and flushes the text immediately. Closing is left for the caller
     *
     * @param text the message to write to the OutputStream
     * @param outputStream the OutputStream to be written to
     * @throws IOException if an error occurs when writing the text
     */
    public static void writeString(String text, OutputStream outputStream) throws IOException {
        outputStream.write(text.getBytes());
        outputStream.flush();
    }

    private StreamUtils() {}
}
