package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class EvalServerThread implements Runnable {

  private static final int MAX_BYTE_BUFFER_SIZE = 16;
  private final Socket socket;

  public EvalServerThread(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {
    try {
      OutputStream out = socket.getOutputStream();
      InputStream in = socket.getInputStream();
      byte[] buffer = new byte[MAX_BYTE_BUFFER_SIZE];
      List<Byte> requestByteArrList = new ArrayList<>();

      // Start reading the stream from the input stream and keep reading until the EOF character * is sent
      while (in.read(buffer, 0, MAX_BYTE_BUFFER_SIZE) != -1) {
        if (buffer[0] == (byte) ('*')) {
          break;
        }
        for (byte b : buffer) {
          requestByteArrList.add(b);
        }
      }

      // Construct the client protocol request from the byte list
      ProtocolRequest request = ProtocolRequest.convertFromByteList(requestByteArrList);

      // Get the expression information from the request
      short numOfAnswer = request.getNumOfExpression();
      String[] expressionArr = request.getExpressionArr();
      short[] lengthArr = new short[numOfAnswer];

      // Initialize the answer array
      String[] answerArr = new String[numOfAnswer];

      // Evaluate the expression and get the answer and its length of byte
      for (int i = 0; i < expressionArr.length; i++) {
        int answer = ProtocolRequest.evaluateExpression(expressionArr[i]);
        String answerStr = String.valueOf(answer);
        byte[] answerStrByteArr = answerStr.getBytes(StandardCharsets.UTF_8);
        lengthArr[i] = (short) answerStrByteArr.length;
        answerArr[i] = answerStr;
      }

      // Construct the response to send back to the client
      ProtocolResponse response = new ProtocolResponse(numOfAnswer, lengthArr, answerArr);
      byte[] responseByteArr = response.convertToByteArr();
      int index = 0;
      while (index < responseByteArr.length) {
        System.arraycopy(responseByteArr, index, buffer, 0,
            (index + MAX_BYTE_BUFFER_SIZE) >= responseByteArr.length ? responseByteArr.length
                - index : MAX_BYTE_BUFFER_SIZE);
        index += MAX_BYTE_BUFFER_SIZE;
        out.write(buffer, 0, MAX_BYTE_BUFFER_SIZE);
      }

      // Send the EOF character * back to inform the client the stream is completed
      buffer[0] = (byte) ('*');
      out.write(buffer, 0, 1);

      // This line is a debug output to validate the response the server send to the client.
      // response.printResponse();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
