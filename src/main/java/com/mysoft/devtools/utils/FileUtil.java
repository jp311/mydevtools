package com.mysoft.devtools.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hezd 2023/5/3
 */
public class FileUtil {
    public static String readAllText(String path) throws IOException {
        FileInputStream inputStream = new FileInputStream(path);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(System.lineSeparator());
        }
        reader.close();
        return stringBuilder.toString();
    }

    public static void writeAllText(String fileName, String content) throws IOException {
        FileWriter fileWriter = new FileWriter(fileName, StandardCharsets.UTF_8);
        fileWriter.write(content);
        fileWriter.close();
    }

    public static List<File> getAllFiles(String path) {
        List<File> fileList = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) {
            return fileList;
        }
        if (file.isFile()) {
            fileList.add(file);
        } else {
            File[] files = file.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    if (subFile.isDirectory()) {
                        fileList.addAll(getAllFiles(subFile.getAbsolutePath()));
                    } else {
                        fileList.add(subFile);
                    }
                }
            }
        }
        return fileList;
    }

    public static boolean isExist(String file) {
        return new File(file).exists();
    }

    public static String combine(String... paths) {
        return String.join(File.separator, paths);
    }

    public static String getParent(String path) {
        return new File(path).getParent();
    }

    public static String readResourceContent(String relationPath) {
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(relationPath);
        if (inputStream == null) {
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            inputStream.close();
            reader.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
