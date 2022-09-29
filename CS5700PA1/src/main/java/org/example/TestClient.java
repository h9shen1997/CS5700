package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TestClient {

  private static final int EXPECTED_ARG = 4;
  private static final int MAX_BYTE_BUFFER_SIZE = 16;

  public static void main(String[] args) {
    if (args.length != EXPECTED_ARG) {
      System.err.println("Usage: provide hostname, port number, input and output file paths");
      System.exit(1);
    }

    String hostname = args[0];
    int portNumber = Integer.parseInt(args[1]);
    String inputFilePath = args[2];
    String outputFilePath = args[3];
    List<String> lines = FileProcessor.readFile(inputFilePath);
    if (lines == null) {
      System.err.println("Failed to read the input file");
      System.exit(1);
    }

    TestClient client = new TestClient();

    ProtocolRequest request = client.constructRequest(lines);
    byte[] requestByteArr = request.convertToByteArr(); // big endian order

    byte[] buffer = new byte[MAX_BYTE_BUFFER_SIZE];

    List<Byte> responseByteArrList = new ArrayList<>();

    try (Socket socket = new Socket(hostname, portNumber)) {
      OutputStream out = socket.getOutputStream();
      InputStream in = socket.getInputStream();
      int index = 0;
      while (index < requestByteArr.length) {
        System.arraycopy(requestByteArr, index, buffer, 0,
            (index + MAX_BYTE_BUFFER_SIZE) >= requestByteArr.length ? requestByteArr.length - index
                : MAX_BYTE_BUFFER_SIZE);
        index += MAX_BYTE_BUFFER_SIZE;
        out.write(buffer, 0, MAX_BYTE_BUFFER_SIZE);
      }
      buffer[0] = (byte) ('*');
      out.write(buffer, 0, 1);
      while (in.read(buffer, 0, MAX_BYTE_BUFFER_SIZE) != -1) {
        if (buffer[0] == (byte) ('*')) {
          break;
        }
        for (byte b : buffer) {
          responseByteArrList.add(b);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    ProtocolResponse response = ProtocolResponse.convertFromByteList(responseByteArrList);
    FileProcessor.writeFile(outputFilePath, response);
  }

  private ProtocolRequest constructRequest(List<String> lines) {
    short numOfExpression = Short.parseShort(lines.get(0));
    short[] lengthArr = new short[numOfExpression];
    String[] expressionArr = new String[numOfExpression];
    int index = 0;
    for (int i = 1; i < lines.size(); i += 2) {
      short lengthOfExpression = Short.parseShort(lines.get(i));
      String expression = lines.get(i + 1);
      lengthArr[index] = lengthOfExpression;
      expressionArr[index] = expression;
      index++;
    }
    return new ProtocolRequest(numOfExpression, lengthArr, expressionArr);
  }

}
