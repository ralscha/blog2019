package ch.rasc.keyvalue;

public class VariableLengthInt {

  public static byte[] zigZagEncoding(int value) {
    return encodeUInt32((value << 1) ^ (value >> 31));
  }

  public static int zigZagDecoding(byte[] input) {
    int result = decodeUInt32(input);
    return (result >>> 1) ^ -(result & 1);
  }

  public static byte[] encodeUInt32(int inputValue) {
    int value = inputValue;
    byte[] buffer = new byte[5];
    int position = 0;

    while (true) {
      // ~0x7F = 0xffffff80
      if ((value & 0b11111111111111111111111110000000) == 0) {
        buffer[position++] = (byte) value;
        break;
      }

      buffer[position++] = (byte) ((value & 0b1111111) | 0b10000000);
      value >>>= 7;
    }

    byte[] dest = new byte[position];
    System.arraycopy(buffer, 0, dest, 0, position);
    return dest;
  }

  public static int decodeUInt32(byte[] input) {
    int result = 0;
    int shift = 0;
    for (int ix = 0; ix < input.length; ix++) {
      byte b = input[ix];
      result |= (b & 0b1111111) << shift;
      shift += 7;
      if ((b & 0b10000000) == 0) {
        return result;
      }
    }
    return result;
  }

  // HELPERS

  public static void printBinaryString(byte b) {
    String line1 = "┌───┬───┬───┬───┬───┬───┬───┬───┐";
    String line2 = "│ 7 │ 6 │ 5 │ 4 │ 3 │ 2 │ 1 │ 0 │";
    String line3 = "├───┼───┼───┼───┼───┼───┼───┼───┤";
    String line5 = "└───┴───┴───┴───┴───┴───┴───┴───┘";

    String binString = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
    StringBuilder sb = new StringBuilder(33);
    for (char c : binString.toCharArray()) {
      sb.append("│ ").append(c).append(" ");
    }
    sb.append("│ (decimal ").append(b).append(")");
    String line4 = sb.toString();

    System.out.println(line1);
    System.out.println(line2);
    System.out.println(line3);
    System.out.println(line4);
    System.out.println(line5);
  }

  public static void operation(int a, int b, int result, String operation,
      int numberOfBits, int groupSize) {
    String astr = intToBinaryString(a, numberOfBits, groupSize);
    String line1 = String.format("%" + (operation.length() + 2 + astr.length()) + "s",
        astr);
    String line2 = String.format(" %s %s", operation,
        intToBinaryString(b, numberOfBits, groupSize));
    String line3 = String.format(" %s %s", "=",
        intToBinaryString(result, numberOfBits, groupSize));

    System.out.println(line1);
    System.out.println(line2);
    System.out.println(line3);
  }

  public static String intToBinaryString(int number, int numberOfBits, int groupSize) {
    StringBuilder result = new StringBuilder();

    for (int i = numberOfBits - 1; i >= 0; i--) {
      int mask = 1 << i;
      result.append((number & mask) != 0 ? "1" : "0");

      if (i % groupSize == 0) {
        result.append(" ");
      }
    }
    result.replace(result.length() - 1, result.length(), "");

    return result.toString();
  }

}
