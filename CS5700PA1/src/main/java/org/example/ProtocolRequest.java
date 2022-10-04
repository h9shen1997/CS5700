package org.example;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ProtocolRequest implements IProtocol {

  private final short numOfExpression;
  private final short[] lengthArr;
  private final String[] expressionArr;

  public ProtocolRequest(short numOfExpression, short[] lengthArr, String[] expressionArr) {
    this.numOfExpression = numOfExpression;
    this.lengthArr = lengthArr;
    this.expressionArr = expressionArr;
  }

  /**
   * Construct a protocol request from a list of byte. The byte is in big-endian order.
   *
   * @param byteList a list of byte in big endian order
   * @return the constructed protocol request
   */
  public static ProtocolRequest convertFromByteList(List<Byte> byteList) {
    // Get the number of expression in big-endian order
    short numOfExpression = (short) (((byteList.get(0) & 0xff) << BYTE_SHIFT) | (byteList.get(1)
        & 0xff));

    // Initialize length and expression array
    short[] lengthArr = new short[numOfExpression];
    String[] expressionArr = new String[numOfExpression];

    // Process the byte list based on the length of each expression
    int expressionIndex = 0;

    // Index starts right after the number of expression, which is a two byte number.
    int index = OFFSET;
    while (index < byteList.size() && expressionIndex < numOfExpression) {
      // Get the length of expression in big-endian order
      short length = (short) (((byteList.get(index) & 0xff) << BYTE_SHIFT) | (
          byteList.get(index + 1)
              & 0xff));
      byte[] expByteArr = new byte[length];

      // Get the expression string which is encoded using UTF8
      int j = 0;
      for (int i = index + OFFSET; i < index + OFFSET + length; i++) {
        expByteArr[j++] = byteList.get(i);
      }
      String expression = new String(expByteArr, StandardCharsets.UTF_8);
      lengthArr[expressionIndex] = length;
      expressionArr[expressionIndex] = expression;

      // Calculate the starting index of the next expression length and increment expression count
      index += (OFFSET + length);
      expressionIndex++;
    }
    return new ProtocolRequest(numOfExpression, lengthArr, expressionArr);
  }

  /**
   * Evaluate the expression string
   *
   * @param expression expression string, which only contains subtraction and addition operations
   * @return the evaluated result
   */
  public static int evaluateExpression(String expression) {
    int number = 0;
    // Save all the numbers and sign first
    List<Integer> allNumbers = new ArrayList<>();
    List<Character> signs = new ArrayList<>();
    for (char c : expression.toCharArray()) {
      if (Character.isDigit(c)) {
        number = number * TEN_MULTIPLIER + (c - '0');
      } else {
        allNumbers.add(number);
        number = 0;
        signs.add(c);
      }
    }
    if (number != 0) {
      allNumbers.add(number);
    }

    // Set the result to the first number and then subtract or add the next number based on the sign
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

  /**
   * Convert the protocol request to a byte array where the number of expressions and length of
   * expression are both in big-endian order. The actual expression is encoed using UTF8
   *
   * @return the byte array
   */
  public byte[] convertToByteArr() {
    List<Byte> byteArr = new ArrayList<>();
    byteArr.add((byte) ((this.numOfExpression >> BYTE_SHIFT) & 0xff));
    byteArr.add((byte) (this.numOfExpression & 0xff));
    for (int i = 0; i < this.numOfExpression; i++) {
      byteArr.add((byte) ((this.lengthArr[i] >> BYTE_SHIFT) & 0xff));
      byteArr.add((byte) (this.lengthArr[i] & 0xff));
      byte[] curExpression = this.expressionArr[i].getBytes(StandardCharsets.UTF_8);
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
