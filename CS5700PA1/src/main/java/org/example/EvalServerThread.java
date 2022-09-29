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
      while (in.read(buffer, 0, MAX_BYTE_BUFFER_SIZE) != -1) {
        if (buffer[0] == (byte) ('*')) {
          break;
        }
        for (byte b : buffer) {
          requestByteArrList.add(b);
        }
      }
      ProtocolRequest request = ProtocolRequest.convertFromByteList(requestByteArrList);
      short numOfAnswer = request.getNumOfExpression();
      String[] expressionArr = request.getExpressionArr();
      short[] lengthArr = new short[numOfAnswer];
      String[] answerArr = new String[numOfAnswer];
      for (int i = 0; i < expressionArr.length; i++) {
        int answer = ProtocolRequest.evaluateExpression(expressionArr[i]);
        String answerStr = String.valueOf(answer);
        byte[] answerStrByteArr = answerStr.getBytes(StandardCharsets.UTF_8);
        lengthArr[i] = (short) answerStrByteArr.length;
        answerArr[i] = answerStr;
      }
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
      buffer[0] = (byte) ('*');
      out.write(buffer, 0, 1);
      response.printResponse();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
