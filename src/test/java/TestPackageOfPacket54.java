import org.junit.Assert;
import org.junit.Test;
import message.exeption.BrokenPacketException;
import message.packet54.Packet;
import message.utils.Utility;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TestPackageOfPacket54 extends Assert {
    private Random random = new Random();

    @Test
    public void testPacketConstructUsnComm() throws BrokenPacketException {
        long usn = 257;
        Packet packet = new Packet((byte) random.nextInt(), (byte) random.nextInt(),
                (byte) 64, random.nextInt(Integer.MAX_VALUE), usn, "test".getBytes(), null);
        assertEquals(257, packet.getBody().getUsn());
        assert (Arrays.equals("test".getBytes(), packet.getBody().getbComm()));
    }

    @Test
    public void testPacketConstructComm() throws BrokenPacketException {
        Packet packet = new Packet((byte) random.nextInt(), (byte) random.nextInt(),
                (byte) 0, random.nextInt(Integer.MAX_VALUE), "test".getBytes());
        assertEquals(4, packet.getBody().getCommLen());
        assert (Arrays.equals("test".getBytes(), packet.getBody().getbComm()));
    }

    @Test
    public void testPacketConstructUsnCommCrypt() throws BrokenPacketException {
        long usn = 257;
        Packet packet = new Packet((byte) random.nextInt(), (byte) random.nextInt(),
                (byte) 192, random.nextInt(Integer.MAX_VALUE), usn, "test".getBytes(), null);
        assertEquals(257, packet.getBody().getUsn());
        assert (Arrays.equals("test".getBytes(), packet.getBody().getbComm()));
    }

    @Test
    public void testPacketConstructCommCrypt() throws BrokenPacketException {
        Packet packet = new Packet((byte) random.nextInt(), (byte) random.nextInt(),
                (byte) 128, (long) random.nextInt(Integer.MAX_VALUE) * 2 + 1, "test".getBytes());
        assertEquals(4, packet.getBody().getCommLen());
        assert (Arrays.equals("test".getBytes(), packet.getBody().getbComm()));
    }

    @Test
    public void testPacketConstructBytes() throws BrokenPacketException {
        Packet packet1 = new Packet((byte) random.nextInt(), (byte) random.nextInt(),
                (byte) 0, random.nextInt(Integer.MAX_VALUE), "test11".getBytes());
        Packet packet2 = new Packet(packet1.toBytes(null), null);
        Assert.assertEquals(packet1, packet2);
    }

    @Test
    public void testPacketConstructBytesUsn() throws BrokenPacketException {
        long usn = 12;
        Packet packet1 = new Packet((byte) random.nextInt(), (byte) random.nextInt(),
                (byte) 64, (long) Integer.MAX_VALUE * 2, usn, "test1".getBytes());
        Packet packet2 = new Packet(packet1.toBytes(null), null);
        Assert.assertEquals(packet1, packet2);
    }

    @Test
    public void testPacketConstructBytesCrypt() throws BrokenPacketException {
        Packet packet1 = new Packet((byte) random.nextInt(), (byte) random.nextInt(),
                (byte) 128, random.nextInt(Integer.MAX_VALUE), "test11".getBytes());
        Packet packet2 = new Packet(packet1.toBytes(null), null);
        Assert.assertEquals(packet1, packet2);
    }

    @Test
    public void testPacketConstructBytesUsnCrypt() throws BrokenPacketException {
        long usn = 256 * 256 + 1;
        Packet packet1 = new Packet((byte) random.nextInt(), (byte) random.nextInt(),
                (byte) 192, random.nextInt(Integer.MAX_VALUE), usn, "test1".getBytes(), null);
        Packet packet2 = new Packet(packet1.toBytes(null), null);
        Assert.assertEquals(packet1, packet2);
    }

    @Test
    public void testPacketListConstructBytes() throws BrokenPacketException {
        Packet packet1 = new Packet((byte) random.nextInt(), (byte) random.nextInt(),
                (byte) 0, random.nextInt(Integer.MAX_VALUE), "test11".getBytes());
        Packet packet2 = new Packet((byte) random.nextInt(), (byte) random.nextInt(),
                (byte) 0, random.nextInt(Integer.MAX_VALUE), "test11".getBytes());
        ByteBuffer buffer = ByteBuffer.allocate(packet1.length() + packet2.length());
        buffer.put(packet1.toBytes(null)).put(packet2.toBytes(null));

        List<Packet> listPacket = Packet.decodeMessages(buffer.array(), null);
        Assert.assertEquals(listPacket.get(0), packet1);
        Assert.assertEquals(listPacket.get(1), packet2);
    }

    @Test
    public void testPacketListConstructBytesUsn() throws BrokenPacketException {
        long usn = 12;
        Packet packet1 = new Packet((byte) random.nextInt(), (byte) random.nextInt(),
                (byte) 64, random.nextInt(Integer.MAX_VALUE), usn, "test11".getBytes());
        Packet packet2 = new Packet((byte) random.nextInt(), (byte) random.nextInt(),
                (byte) 64, random.nextInt(Integer.MAX_VALUE), usn, "test11".getBytes());
        ByteBuffer buffer = ByteBuffer.allocate(packet1.length() + packet2.length());
        buffer.put(packet1.toBytes(null)).put(packet2.toBytes(null));

        List<Packet> listPacket = Packet.decodeMessages(buffer.array(), null);
        Assert.assertEquals(listPacket.get(0), packet1);
        Assert.assertEquals(listPacket.get(1), packet2);
    }

    @Test
    public void testPacketListConstructBytesCrypt() throws BrokenPacketException {
        Packet packet1 = new Packet((byte) random.nextInt(), (byte) random.nextInt(),
                (byte) 128, random.nextInt(Integer.MAX_VALUE), "test11".getBytes());
        Packet packet2 = new Packet((byte) random.nextInt(), (byte) random.nextInt(),
                (byte) 128, random.nextInt(Integer.MAX_VALUE), "test11".getBytes());
        ByteBuffer buffer = ByteBuffer.allocate(packet1.length() + packet2.length());
        buffer.put(packet1.toBytes(null)).put(packet2.toBytes(null));

        List<Packet> listPacket = Packet.decodeMessages(buffer.array(), null);
        Assert.assertEquals(listPacket.get(0), packet1);
        Assert.assertEquals(listPacket.get(1), packet2);
    }

    @Test
    public void testPacketListConstructBytesUsnCrypt() throws BrokenPacketException {
        long usn = 256 * 256 + 1;

        Packet packet1 = new Packet((byte) random.nextInt(), (byte) random.nextInt(),
                (byte) 192, random.nextInt(Integer.MAX_VALUE), usn, "test11".getBytes());
        Packet packet2 = new Packet((byte) random.nextInt(), (byte) random.nextInt(),
                (byte) 192, random.nextInt(Integer.MAX_VALUE), usn, "test11".getBytes());
        ByteBuffer buffer = ByteBuffer.allocate(packet1.length() + packet2.length());
        buffer.put(packet1.toBytes(null)).put(packet2.toBytes(null));

        List<Packet> listPacket = Packet.decodeMessages(buffer.array(), null);
        Assert.assertEquals(listPacket.get(0), packet1);
        Assert.assertEquals(listPacket.get(1), packet2);
    }

    @Test
    public void testPacketConstructBytesEqualsKey() throws BrokenPacketException {
        byte[] key = new byte[]{(byte) random.nextInt(), (byte) random.nextInt(), (byte) random.nextInt(), (byte) random.nextInt()};
        Packet packet1 = new Packet((byte) random.nextInt(), (byte) random.nextInt(),
                (byte) 0, random.nextInt(Integer.MAX_VALUE), "test11".getBytes());
        Packet packet2 = new Packet(packet1.toBytes(key), key);
        Assert.assertEquals(packet1, packet2);
    }

    @Test(expected = BrokenPacketException.class)
    public void testPacketConstructBytesErrorKey() throws BrokenPacketException {
        byte[] key1 = new byte[]{0x01, 0x02, 0x03, 0x04};
        byte[] key2 = new byte[]{0x01, 0x03, 0x03, 0x04};
        Packet packet1 = new Packet((byte) random.nextInt(), (byte) random.nextInt(),
                (byte) 128, random.nextInt(Integer.MAX_VALUE), "test11".getBytes());
        Packet packet2 = new Packet(packet1.toBytes(key1), key2);
    }

    @Test
    public void testPacketLengthCrypt() throws BrokenPacketException {
        Packet packet = new Packet((byte) random.nextInt(), (byte) random.nextInt(),
                (byte) 128, random.nextInt(Integer.MAX_VALUE), "test".getBytes());
        Assert.assertEquals(packet.length(), packet.toBytes(null).length);
    }

    @Test
    public void testPacketLength() throws BrokenPacketException {
        Packet packet = new Packet((byte) random.nextInt(), (byte) random.nextInt(),
                (byte) 0, random.nextInt(Integer.MAX_VALUE), "test".getBytes());
        Assert.assertEquals (packet.length() , packet.toBytes(null).length);
    }

    @Test
    public void testPacketLengthUSNCrypt() throws BrokenPacketException {
        byte b = (byte) random.nextInt();
        byte[] usn = {b, b, b, b, b, b};
        Packet packet = new Packet((byte) random.nextInt(), (byte) random.nextInt(),
                (byte) 194, random.nextInt(Integer.MAX_VALUE), Utility.takeLongFromNBytes(usn, 0, 6, ByteOrder.LITTLE_ENDIAN), "test".getBytes());
        Assert.assertEquals (packet.length() , packet.toBytes(null).length);
    }

    @Test
    public void testPacketLengthUSN() throws BrokenPacketException {
        long usn = random.nextLong() & 0xffffff;
        Packet packet = new Packet((byte) random.nextInt(), (byte) random.nextInt(),
                (byte) 64, random.nextInt(Integer.MAX_VALUE), usn, "test".getBytes());
        Assert.assertEquals (packet.length() , packet.toBytes(null).length);
    }

    @Test
    public void testPacketNotEquals() throws BrokenPacketException {
        Packet packet1 = new Packet((byte) 0, (byte) 0, (byte) 0, 0, "test".getBytes());
        Packet packet2 = new Packet((byte) 1, (byte) 0, (byte) 0, 0, "test".getBytes());
        Packet packet3 = new Packet((byte) 0, (byte) 1, (byte) 0, 0, "test".getBytes());
        Packet packet4 = new Packet((byte) 0, (byte) 0, (byte) 1, 0, "test".getBytes());
        Packet packet5 = new Packet((byte) 0, (byte) 0, (byte) 0, 1, "test".getBytes());
        Packet packet6 = new Packet((byte) 0, (byte) 0, (byte) 0, 0, "test1".getBytes());

        Assert.assertNotEquals(packet1, packet2);
        Assert.assertNotEquals(packet1, packet3);
        Assert.assertNotEquals(packet1, packet4);
        Assert.assertNotEquals(packet1, packet5);
        Assert.assertNotEquals(packet1, packet6);
    }

    @Test
    public void testPacketEquals() throws BrokenPacketException {
        Packet packet1 = new Packet((byte) 0, (byte) 0, (byte) 0, 0, "test".getBytes());
        Packet packet2 = new Packet((byte) 0, (byte) 0, (byte) 0, 0, "test".getBytes());

        Assert.assertEquals(packet1, packet2);
    }

    @Test
    public void testPacketEqualsUsn() throws BrokenPacketException {
        long usn1 = 257, usn2 = 1;

        Packet packet1 = new Packet((byte) 0, (byte) 0, (byte) 192, 0, usn1, "test".getBytes(), null);
        Packet packet2 = new Packet((byte) 0, (byte) 0, (byte) 192, 0, usn1, "test".getBytes(), null);
        Packet packet3 = new Packet((byte) 0, (byte) 0, (byte) 192, 0, usn2, "test".getBytes(), null);

        Assert.assertEquals(packet1, packet2);
        Assert.assertNotEquals(packet1, packet3);
    }

    @Test(expected = BrokenPacketException.class)
    public void testPacketToCreateException() throws BrokenPacketException {
        Random random = new Random();
        long usn = 257;
        Packet packet1 = new Packet((byte) 1, (byte) 2, (byte) 64, 4, usn, "test".getBytes(), null); //Test package
        byte[] bytes = new byte[packet1.toBytes(null).length];
        byte randomValueOfByte;
        for (int i = 0; i < bytes.length; i++) {
            System.arraycopy(packet1.toBytes(null), 0, bytes, 0, bytes.length);
            randomValueOfByte = (byte) random.nextInt();

            while (bytes[i] == (bytes[i] = randomValueOfByte)) {
                randomValueOfByte = (byte) random.nextInt();
            }
            Packet packet2 = new Packet(bytes, null);
        }
    }

    @Test
    public void testPacketConstructBigId() throws BrokenPacketException {
        Packet packet1 = new Packet((byte) random.nextInt(), (byte) random.nextInt(),
                (byte) 128, 4294967295L, "1234".getBytes());
        Packet packet2 = new Packet(packet1.toBytes(null), null);
        Assert.assertEquals(packet1, packet2);
    }

    @Test
    public void testPacketId() {
        BigInteger number = new BigInteger("FFFFFFFF", 16);
        assertEquals(4294967295L, number.longValue());
    }

    @Test(expected = BrokenPacketException.class)
    public void testPacketConstructNoUSNException() throws BrokenPacketException {
        Packet packet = new Packet((byte) random.nextInt(), (byte) random.nextInt(), (byte) 64, 4, "test".getBytes());
    }

    @Test(expected = BrokenPacketException.class)
    public void testPacketConstructUSNException() throws BrokenPacketException {
        long usn = 257;
        Packet packet = new Packet((byte) random.nextInt(), (byte) random.nextInt(), (byte) 63,
                4, usn, "test".getBytes());
    }

    @Test(expected = BrokenPacketException.class)
    public void testPacketConstructNullCommException() throws BrokenPacketException {
        byte[] bCommand = null;
        Packet packet = new Packet((byte) random.nextInt(), (byte) random.nextInt(), (byte) 0, 4, bCommand);
    }

    @Test(expected = BrokenPacketException.class)
    public void testPacketConstructNullCommUSNException() throws BrokenPacketException {
        byte[] bCommand = null;
        long usn = 257;
        Packet packet = new Packet((byte) random.nextInt(), (byte) random.nextInt(), (byte) 0, 128, usn, bCommand);
    }

    @Test(expected = BrokenPacketException.class)
    public void testPacketConstructPktIdException() throws BrokenPacketException {
        byte[] bCommand = null;
        Packet packet = new Packet((byte) random.nextInt(), (byte) random.nextInt(), (byte) 0, -1, "test".getBytes());
    }

}
