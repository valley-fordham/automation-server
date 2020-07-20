package com.glenfordham.utils;

import com.glenfordham.webserver.Application;
import org.apache.commons.io.FileUtils;

import java.io.*;

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

    /**
     * Takes an InputStream and turns it into a File (makes a temporary file)
     *
     * @param inputStream the InputStream to turn into a File object
     * @return a File built from the InputStream
     * @throws IOException if an error occurs when building the File
     */
    public static File getFile(InputStream inputStream) throws IOException {
        File file = File.createTempFile(Application.class.getPackageName(), null);
        file.deleteOnExit();
        FileUtils.copyInputStreamToFile(inputStream, file);
        return file;
    }

    private StreamUtils() {
    }
}
