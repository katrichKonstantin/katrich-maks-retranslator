import message.contactID.ContactID;
import message.exeption.BrokenPacketException;
import message.exeption.FailInputArgumentsException;
import message.packet54.Packet;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class TestContactId {
    private byte[] bytes1 = {-110, -101, 0, 13, 1, -32, 5, 1, 0, 64, 80, 52, 2, 1, 0, 1};
    private byte[] bytes2 = {-110, -101, 0, 13, 1, -32, 5, 1, 0, 64, 80, 20, 34, 3, 0, 3};
    private byte[] bytes3 = {-110, -101, 0, 13, 0, 64, 80, 52, 2, 1, 0, 1};
    private long usn = (long) Integer.MAX_VALUE * 2;

    @Test
    public void testFormConstructTypeOne() throws BrokenPacketException {
        Packet packet = new Packet((byte) 1, (byte) 1, (byte) 0, 80, bytes1);
        ContactID contactID = new ContactID(packet);
        Assert.assertEquals(contactID.toContactIDForm(), "5010 184050R40201001\\x14");
        Assert.assertArrayEquals(contactID.getLastCommand(), new byte[]{-32, 5, 1, 0});
    }

    @Test
    public void testFormConstructTypeOneCrypt() throws BrokenPacketException {
        Packet packet = new Packet((byte) 1, (byte) 1, (byte) 128, 80, bytes1);
        ContactID contactID = new ContactID(packet);
        Assert.assertEquals(contactID.toContactIDForm(), "5010 184050R40201001\\x14");
        Assert.assertArrayEquals(contactID.getLastCommand(), new byte[]{-32, 5, 1, 0});
    }

    @Test
    public void testFormConstructUSN() throws BrokenPacketException {
        Packet packet = new Packet((byte) 1, (byte) 1, (byte) 64, 80, usn, bytes1);
        ContactID contactID = new ContactID(packet);
        Assert.assertEquals(contactID.toContactIDForm(), "5010 184050R40201001\\x14");
        Assert.assertArrayEquals(contactID.getLastCommand(), new byte[]{-32, 5, 1, 0});
    }

    @Test
    public void testFormConstructUSNCrypt() throws BrokenPacketException {
        Packet packet = new Packet((byte) 1, (byte) 1, (byte) 192, 80, usn, bytes1);
        ContactID contactID = new ContactID(packet);
        Assert.assertEquals(contactID.toContactIDForm(), "5010 184050R40201001\\x14");
        Assert.assertArrayEquals(contactID.getLastCommand(), new byte[]{-32, 5, 1, 0});
    }

    @Test
    public void testFormConstructTypeTwo() throws BrokenPacketException {
        Packet packet = new Packet((byte) 1, (byte) 1, (byte) 0, 80, bytes2);
        ContactID contactID = new ContactID(packet);
        Assert.assertEquals(contactID.toContactIDForm(), "5010 184050E42203003\\x14");
        Assert.assertArrayEquals(contactID.getLastCommand(), new byte[]{-32, 5, 1, 0});
    }

    @Test
    public void testFormConstructCryptTypeTwo() throws BrokenPacketException {
        Packet packet = new Packet((byte) 1, (byte) 1, (byte) 128, 80, bytes2);
        ContactID contactID = new ContactID(packet);
        Assert.assertEquals(contactID.toContactIDForm(), "5010 184050E42203003\\x14");
        Assert.assertArrayEquals(contactID.getLastCommand(), new byte[]{-32, 5, 1, 0});
    }

    @Test
    public void testFormConstructWithoutINO() throws BrokenPacketException { // Without IO_NOTIFY_LOG
        Packet packet = new Packet((byte) 1, (byte) 1, (byte) 0, 80, bytes3);
        ContactID contactID = new ContactID(packet);
        Assert.assertEquals(contactID.toContactIDForm(), "5010 184050R40201001\\x14");
        Assert.assertNull(contactID.getLastCommand());
    }

    @Test
    public void testFormConstructWithoutINOCrypt() throws BrokenPacketException { // Without IO_NOTIFY_LOG
        Packet packet = new Packet((byte) 1, (byte) 1, (byte) 128, 80, bytes3);
        ContactID contactID = new ContactID(packet);
        Assert.assertEquals(contactID.toContactIDForm(), "5010 184050R40201001\\x14");
        Assert.assertNull(contactID.getLastCommand());
    }

    @Test
    public void testSendHardBit(){
        Assert.assertEquals(ContactID.hardBit(), "1001@\\x14");
    }

    @Test
    public void testEquals() throws BrokenPacketException {
        Packet packet1 = new Packet((byte) 1, (byte) 1, (byte) 0, 80, bytes1);
        ContactID contactID1 = new ContactID(packet1);
        ContactID contactID2 = new ContactID(contactID1.toContactIDForm(), contactID1.getLastCommand());
        Assert.assertEquals(contactID1, contactID2);
    }

    @Test
    public void testNotEquals() throws BrokenPacketException {
        Packet packet1 = new Packet((byte) 1, (byte) 1, (byte) 0, 80, bytes1);
        ContactID contactID1 = new ContactID(packet1);
        Packet packet2 = new Packet((byte) 1, (byte) 1, (byte) 0, 80, bytes2);
        ContactID contactID2 = new ContactID(packet2);
        Assert.assertNotEquals(contactID1, contactID2);
    }
}
