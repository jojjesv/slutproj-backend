/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import se.johan.foodi.admin.RecipeBean;

/**
 *
 * @author johan
 */
public class FileUtils {

    private static final char[] DEFAULT_FILE_CHAR_SET
            = "abcdefghijklmopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVXYZ0123456789"
                    .toCharArray();

    /**
     * Generates a unique file name target for an uploaded file.
     *
     * @param extension File extension with leading dot
     */
    public static String generateFileIdentifier(Path parentDir) {
        StringBuilder builder = new StringBuilder();
        String basePathStr = parentDir.toString() + "/";

        Path path = Paths.get(basePathStr);

        int strLength = 32;
        char[] strCharSet = DEFAULT_FILE_CHAR_SET;

        do {

            builder.setLength(0);
            for (int i = 0; i < strLength; i++) {
                builder.append(
                        strCharSet[(int) Math.floor(Math.random() * strCharSet.length)]
                );
            }

        } while (Files.exists(Paths.get(basePathStr, builder.toString())));

        return builder.toString();
    }
}
