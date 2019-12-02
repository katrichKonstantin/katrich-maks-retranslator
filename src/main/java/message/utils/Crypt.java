package message.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Random;

public class Crypt {
    private static final byte[][] substitutionTable = new byte[][]
            {
                    {0x0D, 0x02, 0x08, 0x04, 0x06, 0x0F, 0x0B, 0x01, 0x0A, 0x09, 0x03, 0x0E, 0x05, 0x00, 0x0C, 0x07},
                    {0x04, 0x0B, 0x02, 0x0E, 0x0F, 0x00, 0x08, 0x0D, 0x03, 0x0C, 0x09, 0x07, 0x05, 0x0A, 0x06, 0x01},
                    {0x0C, 0x01, 0x0A, 0x0F, 0x09, 0x02, 0x06, 0x08, 0x00, 0x0D, 0x03, 0x04, 0x0E, 0x07, 0x05, 0x0B},
                    {0x02, 0x0C, 0x04, 0x01, 0x07, 0x0A, 0x0B, 0x06, 0x08, 0x05, 0x03, 0x0F, 0x0D, 0x00, 0x0E, 0x09},
                    {0x07, 0x0D, 0x0E, 0x03, 0x00, 0x06, 0x09, 0x0A, 0x01, 0x02, 0x08, 0x05, 0x0B, 0x0C, 0x04, 0x0F},
                    {0x0A, 0x00, 0x09, 0x0E, 0x06, 0x03, 0x0F, 0x05, 0x01, 0x0D, 0x0C, 0x07, 0x0B, 0x04, 0x02, 0x08},
                    {0x0F, 0x01, 0x08, 0x0E, 0x06, 0x0B, 0x03, 0x04, 0x09, 0x07, 0x02, 0x0D, 0x0C, 0x00, 0x05, 0x0A},
                    {0x0E, 0x04, 0x0D, 0x01, 0x02, 0x0F, 0x0B, 0x08, 0x03, 0x0A, 0x06, 0x0C, 0x05, 0x09, 0x00, 0x07}
            };
    private static final byte[] encodingKeyIndexes = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7, 7, 6, 5, 4, 3, 2, 1, 0};
    private static final byte[] decodingKeyIndexes = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 7, 6, 5, 4, 3, 2, 1, 0, 7, 6, 5, 4, 3, 2, 1, 0, 7, 6, 5, 4, 3, 2, 1, 0};
    private static final int PASS_COUNT = 32;
    private static final int BLOCK_SIZE = Long.SIZE / Byte.SIZE;

    private static int substitute(long x) {
        int result = 0;
        for (int i = 0; i < 8; i++) {
            result |= ((int) (substitutionTable[i][(byte) ((x & 0x0f) & 0xff)]) & 0xffffffffL) << (4 * i);
            x >>= 4;
        }
        return result;
    }

    private static long low(long x) {
        return x & 0x00000000ffffffffL;
    }

    private static long high(long x) {
        return (x >> 32) & 0x00000000ffffffffL;
    }

    private static long updateLow(long x, long low) {
        return (x & 0xffffffff00000000L) | (low & 0x00000000ffffffffL);
    }

    private static long updateHigh(long x, long high) {
        return (x & 0x00000000ffffffffL) | ((high << 32) & 0xffffffff00000000L);
    }

    private static long transformBlock(long input, int[] key, byte[] indexes) {
        long temp;
        long result = input;
        for (int i = 0; i < PASS_COUNT; i++) {
            temp = low(result);
            result = updateLow(result, Integer.rotateLeft(substitute(low(result) + key[indexes[i]]), 11) ^ high(result));
            result = updateHigh(result, temp);
        }
        temp = high(result);
        result = updateHigh(result, low(result));
        result = updateLow(result, temp);
        return result;
    }


    public static byte[] decode(Key key, byte[] value) {
        LongBuffer input = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN).asLongBuffer();
        ByteBuffer result = ByteBuffer.allocate(value.length).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < input.capacity(); i++)
            result.putLong(transformBlock(input.get(i), key.value, decodingKeyIndexes));
        return result.array();
    }


    public static byte[] encode(Key key, byte[] value) {
        long[] input = new long[(value.length + BLOCK_SIZE - 1) / BLOCK_SIZE];
        Random random = new Random();
        for (int i = 0, s = 0; i < input.length; i++) {
            input[i] |= (long) (s < value.length ? value[s++] & 0xff : random.nextInt(256));
            input[i] |= (long) (s < value.length ? value[s++] & 0xff : random.nextInt(256)) << 8;
            input[i] |= (long) (s < value.length ? value[s++] & 0xff : random.nextInt(256)) << 16;
            input[i] |= (long) (s < value.length ? value[s++] & 0xff : random.nextInt(256)) << 24;
            input[i] |= (long) (s < value.length ? value[s++] & 0xff : random.nextInt(256)) << 32;
            input[i] |= (long) (s < value.length ? value[s++] & 0xff : random.nextInt(256)) << 40;
            input[i] |= (long) (s < value.length ? value[s++] & 0xff : random.nextInt(256)) << 48;
            input[i] |= (long) (s < value.length ? value[s++] & 0xff : random.nextInt(256)) << 56;
        }
        ByteBuffer result = ByteBuffer.allocate(input.length * BLOCK_SIZE).order(ByteOrder.LITTLE_ENDIAN);
        for (long anInput : input)
            result.putLong(transformBlock(anInput, key.value, encodingKeyIndexes));
        return result.array();
    }

    public static class Key {
        private static final int KEY_SIZE = 4 * 8;
        private final int[] value;

        public Key(byte[] key) {
            ByteBuffer defaultKey = ByteBuffer.allocate(KEY_SIZE).order(ByteOrder.LITTLE_ENDIAN);
            defaultKey.putInt(0x12EEEDB0);
            defaultKey.putInt(0x13702766);
            defaultKey.putInt(0x95614B0B);
            defaultKey.putInt(0x3007E881);
            defaultKey.putInt(0x0F0F11F6);
            defaultKey.putInt(0x3E643810);
            defaultKey.putInt(0xF5DAA965);
            defaultKey.putInt(0xD10ECD15);
            if (key != null) {
                if (key.length > KEY_SIZE)
                    throw new IllegalArgumentException();
                for (int i = 0; i < key.length; i++)
                    defaultKey.put(i, key[i]);
            }
            defaultKey.position(0);
            IntBuffer ib = defaultKey.asIntBuffer();
            value = new int[ib.capacity()];
            ib.get(value);
        }
    }
}
