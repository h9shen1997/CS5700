package org.example;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ProtocolRequest {

  private static final int EXPRESSION_OFFSET = 2;
  private final short numOfExpression;
  private final short[] lengthArr;
  private final String[] expressionArr;

  public ProtocolRequest(short numOfExpression, short[] lengthArr, String[] expressionArr) {
    this.numOfExpression = numOfExpression;
    this.lengthArr = lengthArr;
    this.expressionArr = expressionArr;
  }

  public static ProtocolRequest convertFromByteList(List<Byte> byteList) {
    short numOfExpression = (short) (((byteList.get(0) & 0xff) << 8) | (byteList.get(1) & 0xff));
    short[] lengthArr = new short[numOfExpression];
    String[] expressionArr = new String[numOfExpression];
    int expressionIndex = 0;
    int index = EXPRESSION_OFFSET;
    while (index < byteList.size() && expressionIndex < numOfExpression) {
      short length = (short) (((byteList.get(index) & 0xff) << 8) | (byteList.get(index + 1)
          & 0xff));
      byte[] expByteArr = new byte[length];
      int j = 0;
      for (int i = index + EXPRESSION_OFFSET; i < index + EXPRESSION_OFFSET + length; i++) {
        expByteArr[j++] = byteList.get(i);
      }
      String expression = new String(expByteArr, StandardCharsets.UTF_8);
      lengthArr[expressionIndex] = length;
      expressionArr[expressionIndex] = expression;
      index += (EXPRESSION_OFFSET + length);
      expressionIndex++;
    }
    return new ProtocolRequest(numOfExpression, lengthArr, expressionArr);
  }

  public static int evaluateExpression(String expression) {
    System.out.println("The expression is " + expression);
    int number = 0;
    List<Integer> allNumbers = new ArrayList<>();
    List<Character> signs = new ArrayList<>();
    for (char c : expression.toCharArray()) {
      if (Character.isDigit(c)) {
        number = number * 10 + (c - '0');
      } else {
        allNumbers.add(number);
        number = 0;
        signs.add(c);
      }
    }
    if (number != 0) {
      allNumbers.add(number);
    }
    int result = allNumbers.get(0);
    for (int i = 1; i < allNumbers.size(); i++) {
      if (signs.get(i - 1) == '+') {
        result += allNumbers.get(i);
      } else {
        result -= allNumbers.get(i);
      }
    }
    return result;
  }

  public short getNumOfExpression() {
    return numOfExpression;
  }

  public short[] getLengthArr() {
    return lengthArr;
  }

  public String[] getExpressionArr() {
    return expressionArr;
  }

  public byte[] convertToByteArr() {
    List<Byte> byteArr = new ArrayList<>();
    byteArr.add((byte) ((numOfExpression >> 8) & 0xff));
    byteArr.add((byte) (numOfExpression & 0xff));
    for (int i = 0; i < numOfExpression; i++) {
      byteArr.add((byte) ((lengthArr[i] >> 8) & 0xff));
      byteArr.add((byte) (lengthArr[i] & 0xff));
      byte[] curExpression = expressionArr[i].getBytes(StandardCharsets.UTF_8);
      for (byte b : curExpression) {
        byteArr.add(b);
      }
    }
    byte[] result = new byte[byteArr.size()];
    for (int i = 0; i < result.length; i++) {
      result[i] = byteArr.get(i);
    }
    return result;
  }

  public void printRequest() {
    System.out.println(this.numOfExpression);
    for (short i = 0; i < this.numOfExpression; i++) {
      System.out.println(this.lengthArr[i]);
      System.out.println(this.expressionArr[i]);
    }
  }
}
