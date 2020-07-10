package com.glenfordham.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StringUtils {

    public static String getStringFromStream(InputStream inputStream) throws IOException {
        // TODO: check if it's better to close input stream here too
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
