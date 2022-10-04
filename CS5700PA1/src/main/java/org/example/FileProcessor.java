package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileProcessor {

  /**
   * Read the file content as a list of strings
   *
   * @param filePath absolute path of the file as a string
   * @return a list of strings where each string indicate a line in the file
   */
  public static List<String> readFile(String filePath) {
    List<String> lines = null;
    try {
      lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return lines;
  }

  /**
   * Write the protocol response to the file path. If the file does not exist, it will create this
   * file, otherwise, it will overwrite.
   *
   * @param filePath absolute path for the new response file
   * @param response the protocol response object containing all the evaluated results
   */
  public static void writeFile(String filePath, ProtocolResponse response) {
    String responseString = response.toString();
    try (FileWriter fWriter = new FileWriter(filePath)) {
      fWriter.write(responseString);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
