package org.ferpin.bloques.util;

import java.io.BufferedReader;
import java.io.IOException;

public class Files {
    private Files(){}

    public static String readEverythingFromFile(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(path))) {
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
        }
        return sb.toString();
    }
}
