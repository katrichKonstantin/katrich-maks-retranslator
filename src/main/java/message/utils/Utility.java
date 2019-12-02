package message.utils;

import message.exeption.FailTakeException;

import java.nio.ByteOrder;

public class Utility {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    /**
     * Take short from byte array in little ending order
     *
     * @param bytes array byte
     * @param start start position of short
     * @return short
     * @throws FailTakeException if start + 2 > byte length
     */
    public static short takeShort(byte[] bytes, int start) throws FailTakeException {
        return takeShort(bytes, start, ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Take long what comprises of n byte in little ending order
     *
     * @param bytes  byte array
     * @param start  start position of long
     * @param nBytes count of byte
     * @return long
     * @throws FailTakeException if start + n > bytes length
     */
    public static long takeLongFromNBytes(byte[] bytes, int start, int nBytes) throws FailTakeException {
        return takeLongFromNBytes(bytes, start, nBytes, ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * This method take Long from byte array in little ending order
     *
     * @param bytes byte array
     * @param start start position of long
     * @return long
     * @throws FailTakeException if start + 8 > byte length
     */
    public static long takeLong(byte[] bytes, int start) throws FailTakeException {
        return takeLong(bytes, start, ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * This method take Int from byte array in little ending order
     *
     * @param bytes byte array
     * @param start start position of int
     * @return int
     * @throws FailTakeException if start + 8 > byte length
     */
    public static int takeInt(byte[] bytes, int start) throws FailTakeException {
        return takeInt(bytes, start, ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * This method take short from 2 byte in array of byte
     *
     * @param bytes     byte array
     * @param start     start position of short
     * @param byteOrder order
     * @return short
     */
    public static short takeShort(byte[] bytes, int start, ByteOrder byteOrder) throws FailTakeException {
        if (bytes == null || start + Short.BYTES > bytes.length || start < 0) throw new FailTakeException();
        short newShort;
        if (byteOrder.equals(ByteOrder.LITTLE_ENDIAN)) {
            newShort = bytes[start + 1];
            newShort <<= Byte.SIZE;
            newShort |= (bytes[start] & 0xff);
            return newShort;
        }
        newShort = bytes[start];
        newShort <<= Byte.SIZE;
        newShort |= (bytes[start + 1] & 0xff);
        return newShort;
    }

    /**
     * Take long what comprises of n byte in little ending order
     *
     * @param bytes     byte array
     * @param start     start position of long
     * @param nBytes    count of byte
     * @param byteOrder order
     * @return long
     * @throws FailTakeException if start + n > bytes length
     */
    public static long takeLongFromNBytes(byte[] bytes, int start, int nBytes, ByteOrder byteOrder) throws FailTakeException {
        if (bytes == null || start + nBytes > bytes.length || start < 0) throw new FailTakeException();
        long newLong = 0;
        if (byteOrder.equals(ByteOrder.LITTLE_ENDIAN)) {
            //newLong = 0;
            for (int i = start + nBytes - 1; i >= start; i--) {
                newLong <<= Byte.SIZE;
                newLong |= (bytes[i] & 0xff);
            }
            return newLong;
        }
        start += Long.BYTES - nBytes;
        newLong = bytes[start++];
        for (int i = start; i < start + nBytes - 1; i++) {
            newLong <<= Byte.SIZE;
            newLong |= (bytes[i] & 0xff);
        }
        return newLong;
    }

    /**
     * This method take Long from byte array in little ending order
     *
     * @param bytes     byte array
     * @param start     start position of long
     * @param byteOrder order
     * @return long
     * @throws FailTakeException if start + 8 > bytes length
     */
    public static long takeLong(byte[] bytes, int start, ByteOrder byteOrder) throws FailTakeException {
        return takeLongFromNBytes(bytes, start, Long.BYTES, byteOrder);
    }

    /**
     * This method take Int from byte array in little ending order
     *
     * @param bytes     byte array
     * @param start     start position of int
     * @param byteOrder order
     * @return int
     * @throws FailTakeException if start + 8 > bytes length
     */
    public static int takeInt(byte[] bytes, int start, ByteOrder byteOrder) throws FailTakeException {
        if (bytes == null || start + Integer.BYTES > bytes.length || start < 0) throw new FailTakeException();
        int newInt;
        if (byteOrder.equals(ByteOrder.LITTLE_ENDIAN)) {
            newInt = bytes[start + Integer.BYTES - 1];
            for (int i = start + Integer.BYTES - 2; i >= start; i--) {
                newInt <<= Byte.SIZE;
                newInt |= (bytes[i] & 0xff);
            }
            return newInt;
        }
        newInt = bytes[start++];
        for (int i = start; i < start + Integer.BYTES - 1; i++) {
            newInt <<= Byte.SIZE;
            newInt |= (bytes[i] & 0xff);
        }
        return newInt;
    }

    /**
     * Put n byte of long to byte array
     *
     * @param enterLong long
     * @param bytes     byte array
     * @param start     start position
     * @param nBytes    number of byte
     * @param byteOrder order
     * @throws FailTakeException if start + n > byte length
     */
    public static void putLongNBytes(long enterLong, byte[] bytes, int start, int nBytes, ByteOrder byteOrder) throws FailTakeException {
        if (bytes == null || start + nBytes > bytes.length || start < 0) throw new FailTakeException();
        if (byteOrder.equals(ByteOrder.LITTLE_ENDIAN)) {
            bytes[start++] = (byte) enterLong;
            for (int i = start; i < start + nBytes - 1; i++) {
                enterLong >>= Byte.SIZE;
                bytes[i] = (byte) enterLong;
            }
            return;
        }
        bytes[start + Long.BYTES - 1] = (byte) enterLong;
        for (int i = start + Long.BYTES - 2; i >= start + (Long.BYTES - nBytes); i--) {
            enterLong >>= Byte.SIZE;
            bytes[i] = (byte) enterLong;
        }
    }

    /**
     * This method take byte and convert to String
     *
     * @param bytes     byte array
     * @param start     start position
     * @param length    length of byte to convert
     * @param byteOrder order
     * @return String format of hex byte
     */
    public static String takeHexString(byte[] bytes, int start, int length, ByteOrder byteOrder) throws FailTakeException {
        if (start + length > bytes.length) throw new FailTakeException("TakeGes String: start + length > bytes.length");
        char[] hexChars = new char[length * 2];
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            for (int j = start, s = 0; j < length + start; j++) {
                int v = bytes[j] & 0xFF;
                hexChars[s * 2] = HEX_ARRAY[v >>> 4];
                hexChars[s++ * 2 + 1] = HEX_ARRAY[v & 0x0F];
            }
            return new String(hexChars);
        }
        for (int j = length + start - 1, s = 0; j >= start; j--) {
            int v = bytes[j] & 0xFF;
            hexChars[s * 2] = HEX_ARRAY[v >>> 4];
            hexChars[s++ * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte putByteFromHexString(String str, int start) {
        return (byte) ((Character.digit(str.charAt(start), 16) << 4)
                + Character.digit(str.charAt(start + 1), 16));
    }
}
