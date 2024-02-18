package com.company.lib.project;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class TestUtils {

    public static File getFile(String fileName) {

        // The class loader that loaded the class
        ClassLoader classLoader = TestUtils.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        String file = resource.getFile();

        // the stream holding the file content
        if (file == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return new File(file);
        }

    }
}
