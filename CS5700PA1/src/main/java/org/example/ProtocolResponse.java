package org.example;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ProtocolResponse {

  private static final int ANSWER_OFFSET = 2;
  short numOfAnswers;
  short[] lengthArr;
  String[] answerArr;

  public ProtocolResponse(short numOfAnswers, short[] lengthArr, String[] answerArr) {
    this.numOfAnswers = numOfAnswers;
    this.lengthArr = lengthArr;
    this.answerArr = answerArr;
  }

  public static ProtocolResponse convertFromByteList(List<Byte> byteList) {
    short numOfAnswer = (short) (((byteList.get(0) & 0xff) << 8) | (byteList.get(1) & 0xff));
    short[] lengthArr = new short[numOfAnswer];
    String[] answerArr = new String[numOfAnswer];
    int answerIndex = 0;
    int index = ANSWER_OFFSET;
    while (index < byteList.size() && answerIndex < numOfAnswer) {
      short length = (short) (((byteList.get(index) & 0xff) << 8) | (byteList.get(index + 1)
          & 0xff));
      byte[] ansByteArr = new byte[length];
      int j = 0;
      for (int i = index + ANSWER_OFFSET; i < index + ANSWER_OFFSET + length; i++) {
        ansByteArr[j++] = byteList.get(i);
      }
      String answer = new String(ansByteArr, StandardCharsets.UTF_8);
      lengthArr[answerIndex] = length;
      answerArr[answerIndex] = answer;
      index += (ANSWER_OFFSET + length);
      answerIndex++;
    }
    return new ProtocolResponse(numOfAnswer, lengthArr, answerArr);
  }

  public byte[] convertToByteArr() {
    List<Byte> byteArr = new ArrayList<>();
    byteArr.add((byte) ((numOfAnswers >> 8) & 0xff));
    byteArr.add((byte) (numOfAnswers & 0xff));
    for (int i = 0; i < numOfAnswers; i++) {
      byteArr.add((byte) ((lengthArr[i] >> 8) & 0xff));
      byteArr.add((byte) (lengthArr[i] & 0xff));
      byte[] curAnswer = answerArr[i].getBytes(StandardCharsets.UTF_8);
      for (byte b : curAnswer) {
        byteArr.add(b);
      }
    }
    byte[] result = new byte[byteArr.size()];
    for (int i = 0; i < result.length; i++) {
      result[i] = byteArr.get(i);
    }
    return result;
  }

  public void printResponse() {
    System.out.println(this);
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.numOfAnswers).append("\n");
    for (short i = 0; i < this.numOfAnswers; i++) {
      sb.append(this.lengthArr[i]).append("\n");
      sb.append(this.answerArr[i]).append("\n");
    }
    return sb.toString();
  }
}
