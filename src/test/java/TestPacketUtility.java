import org.junit.Assert;
import org.junit.Test;
import message.exeption.FailTakeException;
import message.utils.Utility;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

public class TestPacketUtility {
    private static final Random random = new Random();

    @Test
    public void testTakeShortBigEnd() throws FailTakeException {
        byte[] bytes = {0x00, 0x01};
        ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
        buffer.order(ByteOrder.BIG_ENDIAN);
        short randShort = (short) random.nextInt();
        buffer.putShort(randShort);
        short tShort = Utility.takeShort(buffer.array(), 0, ByteOrder.BIG_ENDIAN);
        short tShortOne = Utility.takeShort(bytes, 0, ByteOrder.BIG_ENDIAN);
        Assert.assertEquals(tShort, randShort);
        Assert.assertEquals(tShortOne, 1);
    }

    @Test
    public void testTakeShortLitEnd() throws FailTakeException {
        byte[] bytes = {0x01, 0x00};
        ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        short randShort = (short) random.nextInt();
        buffer.putShort(randShort);
        short tShort = Utility.takeShort(buffer.array(), 0, ByteOrder.LITTLE_ENDIAN);
        short tShortOne = Utility.takeShort(bytes, 0, ByteOrder.LITTLE_ENDIAN);
        Assert.assertEquals(tShort, randShort);
        Assert.assertEquals(tShortOne, 1);
    }

    @Test
    public void testTakeIntBigEnd() throws FailTakeException {
        byte[] bytes = {0x00, 0x00, 0x00, 0x01};
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.order(ByteOrder.BIG_ENDIAN);
        int randInt = random.nextInt();
        buffer.putInt(randInt);
        int tInt = Utility.takeInt(buffer.array(), 0, ByteOrder.BIG_ENDIAN);
        int tIntOne = Utility.takeInt(bytes, 0, ByteOrder.BIG_ENDIAN);
        Assert.assertEquals(tInt, randInt);
        Assert.assertEquals(tIntOne, 1);
    }

    @Test
    public void testTakeIntLitEnd() throws FailTakeException {
        byte[] bytes = {0x01, 0x00, 0x00, 0x00};
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int randInt = random.nextInt();
        buffer.putInt(randInt);
        int tInt = Utility.takeInt(buffer.array(), 0, ByteOrder.LITTLE_ENDIAN);
        int tIntOne = Utility.takeInt(bytes, 0, ByteOrder.LITTLE_ENDIAN);
        Assert.assertEquals(tInt, randInt);
        Assert.assertEquals(tIntOne, 1);
    }

    @Test
    public void testTakeLongBigEnd() throws FailTakeException {
        byte[] bytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01};
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.order(ByteOrder.BIG_ENDIAN);
        long randLong = random.nextLong();
        buffer.putLong(randLong);
        long tLong = Utility.takeLong(buffer.array(), 0, ByteOrder.BIG_ENDIAN);
        long tLongOne = Utility.takeLong(bytes, 0, ByteOrder.BIG_ENDIAN);
        Assert.assertEquals(tLong, randLong);
        Assert.assertEquals(tLongOne, 1);
    }

    @Test
    public void testTakeLongLitEnd() throws FailTakeException {
        byte[] bytes = {0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        long randLong = random.nextLong();
        buffer.putLong(randLong);
        long tLong = Utility.takeLong(buffer.array(), 0, ByteOrder.LITTLE_ENDIAN);
        long tLongOne = Utility.takeLong(bytes, 0, ByteOrder.LITTLE_ENDIAN);
        Assert.assertEquals(tLong, randLong);
        Assert.assertEquals(tLongOne, 1);
    }

    @Test
    public void testTakeLongFourBigEnd() throws FailTakeException {
        byte[] bytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01};
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.order(ByteOrder.BIG_ENDIAN);
        long randLong = random.nextInt(Integer.MAX_VALUE) * 2 + 1;
        buffer.putLong(randLong);
        long tLong = Utility.takeLongFromNBytes(buffer.array(), 0, Integer.BYTES, ByteOrder.BIG_ENDIAN);
        long tLongOne = Utility.takeLongFromNBytes(bytes, 0, Integer.BYTES, ByteOrder.BIG_ENDIAN);
        Assert.assertEquals(tLong, randLong);
        Assert.assertEquals(tLongOne, 1);
    }

    @Test
    public void testTakeLongFourLitEnd() throws FailTakeException {
        byte[] bytes = {0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        long randLong = (long) random.nextInt(Integer.MAX_VALUE) * 2 + 1;
        buffer.putLong(randLong);
        long tLong = Utility.takeLongFromNBytes(buffer.array(), 0, Integer.BYTES, ByteOrder.LITTLE_ENDIAN);
        long tLongOne = Utility.takeLongFromNBytes(bytes, 0, Integer.BYTES, ByteOrder.LITTLE_ENDIAN);
        Assert.assertEquals(tLong, randLong);
        Assert.assertEquals(tLongOne, 1);
    }

    @Test(expected = FailTakeException.class)
    public void testTakeShortNull() throws FailTakeException {
        byte[] bytes = null;
        short tShort = Utility.takeShort(bytes, 0);
        Assert.fail();
    }

    @Test(expected = FailTakeException.class)
    public void testTakeIntNull() throws FailTakeException {
        byte[] bytes = null;
        int tInt = Utility.takeInt(bytes, 0);
        Assert.fail();
    }

    @Test(expected = FailTakeException.class)
    public void testTakeLongNull() throws FailTakeException {
        byte[] bytes = null;
        long tLong = Utility.takeLong(bytes, 0);
        Assert.fail();
    }

    @Test(expected = FailTakeException.class)
    public void testTakeLongFourNull() throws FailTakeException {
        byte[] bytes = null;
        long tLong = Utility.takeLongFromNBytes(bytes, 0, Integer.BYTES);
        Assert.fail();
    }

    @Test(expected = FailTakeException.class)
    public void testTakeShortErrStart() throws FailTakeException {
        byte[] bytes = {0x01};
        short tShort = Utility.takeShort(bytes, 0);
        Assert.fail();
    }

    @Test(expected = FailTakeException.class)
    public void testTakeIntErrStart() throws FailTakeException {
        byte[] bytes = {0x01, 0x01, 0x01};
        int tInt = Utility.takeInt(bytes, 0);
        Assert.fail();
    }

    @Test(expected = FailTakeException.class)
    public void testTakeLongErrStart() throws FailTakeException {
        byte[] bytes = {0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
        long tLong = Utility.takeLong(bytes, 0);
        Assert.fail();
    }

    @Test(expected = FailTakeException.class)
    public void testTakeLongFourErrStart() throws FailTakeException {
        byte[] bytes = {0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
        long tLong = Utility.takeLongFromNBytes(bytes, 1, 7);
        Assert.fail();
    }

    @Test
    public void testPutLongLitEnd() throws FailTakeException {
        ByteBuffer buff = ByteBuffer.allocate(8);
        buff.order(ByteOrder.LITTLE_ENDIAN);
        byte[] arr = new byte[8];
        long randLong = random.nextLong();
        Utility.putLongNBytes(randLong, arr, 0, 8, ByteOrder.LITTLE_ENDIAN);
        buff.put(arr);
        buff.clear();
        Assert.assertEquals(buff.getLong(), randLong);
    }

    @Test
    public void testPutLongBigEnd() throws FailTakeException {
        ByteBuffer buff = ByteBuffer.allocate(8);
        buff.order(ByteOrder.BIG_ENDIAN);
        byte[] arr = new byte[8];
        long randLong = random.nextLong();
        Utility.putLongNBytes(randLong, arr, 0, 8, ByteOrder.BIG_ENDIAN);
        buff.put(arr);
        buff.clear();
        Assert.assertEquals(buff.getLong(), randLong);
    }

    @Test
    public void testPutLongSixLitEnd() throws FailTakeException {
        ByteBuffer buff = ByteBuffer.allocate(8);
        buff.order(ByteOrder.LITTLE_ENDIAN);
        byte[] arr = new byte[8];
        long randLong = (random.nextLong() & 0xFFFFFF);
        Utility.putLongNBytes(randLong, arr, 0, 6, ByteOrder.LITTLE_ENDIAN);
        buff.put(arr);
        buff.clear();
        Assert.assertEquals(buff.getLong(), randLong);
    }

    @Test
    public void testPutLongSixBigEnd() throws FailTakeException {
        ByteBuffer buff = ByteBuffer.allocate(8);
        buff.order(ByteOrder.BIG_ENDIAN);
        byte[] arr = new byte[8];
        long randLong = (random.nextLong() & 0xffffff);
        Utility.putLongNBytes(randLong, arr, 0, 6, ByteOrder.BIG_ENDIAN);
        buff.put(arr);
        buff.clear();
        Assert.assertEquals(buff.getLong(), randLong);
    }

    @Test
    public void testTakeHexStringLitEnd() throws FailTakeException {
        byte[] bytes = {0x34, 0x43, 0x54};
        String res = Utility.takeHexString(bytes, 0, 3, ByteOrder.LITTLE_ENDIAN);
        Assert.assertEquals(res, "544334");
    }

    @Test
    public void testTakeHexStringBigEnd() throws FailTakeException {
        byte[] bytes = {0x34, 0x43, 0x54};
        String res = Utility.takeHexString(bytes, 0, 3, ByteOrder.BIG_ENDIAN);
        Assert.assertEquals(res, "344354");
    }

    @Test(expected = FailTakeException.class)
    public void testTakeHexStringLitEndException() throws FailTakeException {
        byte[] bytes = {0x34, 0x43, 0x54};
        String res = Utility.takeHexString(bytes, 1, 3, ByteOrder.LITTLE_ENDIAN);
        Assert.assertEquals(res, "544334");
    }

    @Test(expected = FailTakeException.class)
    public void testTakeHexStringBigEndException() throws FailTakeException {
        byte[] bytes = {0x34, 0x43, 0x54};
        String res = Utility.takeHexString(bytes, 1, 3, ByteOrder.BIG_ENDIAN);
        Assert.assertEquals(res, "344354");
    }
}
