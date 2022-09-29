package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileProcessor {

  public static List<String> readFile(String filePath) {
    List<String> lines = null;
    try {
      lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return lines;
  }

  public static void writeFile(String filePath, ProtocolResponse response) {
    String responseString = response.toString();
    try (FileWriter fWriter = new FileWriter(filePath)) {
      fWriter.write(responseString);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
