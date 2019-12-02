package message.packet54;

import message.contactID.ContactID;
import message.exeption.BrokenPacketException;
import message.exeption.FailInputArgumentsException;
import message.exeption.FailTakeException;
import message.utils.CRC16;
import message.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLogger;

import java.io.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


public class Packet {
    private byte bDst, bSrc, bFlag;
    private long bPktId;
    private Body bBody;
    private short wLen;
    private byte[] bModel;

    private static final Logger logger = LoggerFactory.getLogger(Packet.class);

    public Packet(byte bDst, byte bSrc, long bPktId, long usn, byte[] command, byte[] key) throws BrokenPacketException {
        this(bDst, bSrc,  PacketConstants.FLAG_USN_CRYPT, bPktId, usn, command);
        bModel = toBytes(key);
    }

    public Packet(byte bDst, byte bSrc, long bPktId, long usn, byte[] command) throws BrokenPacketException {
        this(bDst, bSrc,  PacketConstants.FLAG_USN, bPktId, usn, command);
    }

    /**
     * Create package with enter USN and Command
     *
     * @param bDst    message recipient address
     * @param bSrc    data sender address
     * @param bFlag   encryption flag
     * @param bPktId  unique package identifier
     * @param usn     unique customer identifier
     * @param command unique package identifier
     * @param key     key of crypt
     */
    public Packet(byte bDst, byte bSrc, byte bFlag, long bPktId, long usn, byte[] command, byte[] key) throws BrokenPacketException {
        this(bDst, bSrc, bFlag, bPktId, usn, command);
        bModel = toBytes(key);
    }

    /**
     * Create package with enter USN and Command
     *
     * @param bDst    message recipient address
     * @param bSrc    data sender address
     * @param bFlag   encryption flag
     * @param bPktId  unique package identifier
     * @param usn     unique customer identifier
     * @param command unique package identifier
     */
    public Packet(byte bDst, byte bSrc, byte bFlag, long bPktId, long usn, byte[] command) throws BrokenPacketException {
        this(bDst, bSrc, bFlag, bPktId);
        if (command == null || usn < 0) throw new FailInputArgumentsException("Command is null or usn < 0.");
        this.bBody = new Body(usn, command, bFlag);
        this.wLen = bBody.length();
        logger.info("Create packet with usn and command.");
    }

    /**
     * Create package with enter Command
     *
     * @param bDst    message recipient address
     * @param bSrc    data sender address
     * @param bFlag   encryption flag
     * @param bPktId  unique package identifier
     * @param command unique package identifier
     */
    public Packet(byte bDst, byte bSrc, byte bFlag, long bPktId, byte[] command) throws BrokenPacketException {
        this(bDst, bSrc, bFlag, bPktId);
        if (command == null) throw new FailInputArgumentsException("Command is null.");
        this.bBody = new Body(command, bFlag);
        this.wLen = bBody.length();
    }

    /**
     * Create package with enter bytes
     *
     * @param bytes - enter bytes
     */
    public Packet(byte[] bytes, byte[] key) throws BrokenPacketException {
        //Check bMagi, CRC1 and CRC3
        if (bytes[PacketConstants.POSITION_MAGIC] != PacketConstants.BYTE_MAGIC)
            throw new BrokenPacketException("Packet don`t start at bMagic!");
        if (Utility.takeShort(bytes, PacketConstants.FIRST_CRC_DISTANCE) != CRC16.getCRC(bytes, 0, PacketConstants.FIRST_CRC_DISTANCE))
            throw new BrokenPacketException("First CRC incorrect!");
        if (Utility.takeShort(bytes, bytes.length - Short.BYTES, ByteOrder.LITTLE_ENDIAN) != CRC16.getCRC(bytes, PacketConstants.SIZE_OF_HEADER, bytes.length - Short.BYTES - PacketConstants.SIZE_OF_HEADER))
            throw new BrokenPacketException("Third CRC incorrect!");

        bFlag = bytes[PacketConstants.POSITION_FLAG];
        bBody = new Body(bytes, bFlag, key);

        wLen = Utility.takeShort(bytes, PacketConstants.POSITION_LENGTH);
        bPktId = Utility.takeLongFromNBytes(bytes, PacketConstants.POSITION_PKTID, Integer.BYTES);
        bDst = bytes[PacketConstants.POSITION_DST];
        bSrc = bytes[PacketConstants.POSITION_SRC];
        bModel = bytes;
        logger.debug("Create packet");
    }

    /**
     * Create standard package
     *
     * @param bDst   message recipient address
     * @param bSrc   data sender address
     * @param bFlag  encryption flag
     * @param bPktId unique package identifier
     */
    private Packet(byte bDst, byte bSrc, byte bFlag, long bPktId) throws FailInputArgumentsException {
        if (bPktId < 0) throw new FailInputArgumentsException("bPktId is < 0.");
        this.bDst = bDst;
        this.bSrc = bSrc;
        this.bFlag = bFlag;
        this.bPktId = bPktId;
        logger.debug("Create package. Info: bDst: " + bDst + ", bSrc: " + bSrc + ", bFlag: " + bFlag + ", bPktId: " + bPktId);
    }

    /**
     * Method transform package to array of bytes
     *
     * @return result
     */
    public byte[] toBytes(byte[] key) throws FailTakeException {
        //if (bModel != null) return bModel;
        byte[] bodyBytes = bBody.toBytes(key, bFlag);
        wLen = (short) bodyBytes.length;
        ByteBuffer buff = ByteBuffer.allocate(this.length());
        buff.order(ByteOrder.LITTLE_ENDIAN);

        buff.put(PacketConstants.BYTE_MAGIC);
        buff.put(bDst);
        buff.put(bSrc);
        buff.put(bFlag);

        //1: Create one more array (If don`t use ByteBuffer we can use this)
        byte[] arr = new byte[Integer.BYTES];
        Utility.putLongNBytes(bPktId, arr, 0, Integer.BYTES, ByteOrder.LITTLE_ENDIAN);
        buff.put(arr);

        //2: Don`t create array but something strange
        //buff.putInt((int) (bPktId));

        buff.putShort(wLen);

        buff.putShort(CRC16.getCRC(buff.array(), 0, PacketConstants.FIRST_CRC_DISTANCE));

        buff.put(bodyBytes);

        buff.putShort(CRC16.getCRC(bodyBytes));
        bModel = buff.array();
        return bModel;
    }

    public byte[] toBytes() throws FailTakeException {
        if (bModel != null) return bModel;
        return toBytes(null);
    }

    /**
     * This method crate list of packet from big byte array
     *
     * @param bytes byte array
     * @param key   key of encrypt
     * @return list of packet
     * @throws BrokenPacketException if something going wrong with transformation
     */
    public static List<Packet> decodeMessages(byte[] bytes, byte[] key) throws BrokenPacketException {
        if (bytes == null || bytes.length == 0) return null;
        final List<Packet> messages = new LinkedList<>();
        int endPacket;
        while (true) {
            if (bytes[PacketConstants.POSITION_MAGIC] != PacketConstants.BYTE_MAGIC)
                throw new BrokenPacketException("Byte with messages is broken!");
            short len = Utility.takeShort(bytes, PacketConstants.POSITION_LENGTH);
            // length = header + Body length + short CRC
            endPacket = PacketConstants.SIZE_OF_HEADER + len + 2;
            if (endPacket > bytes.length) throw new BrokenPacketException("Byte with messages is broken!");
            messages.add(new Packet(Arrays.copyOfRange(bytes, 0, endPacket), key));
            if (endPacket == bytes.length) break; //byte array is ended
            bytes = Arrays.copyOfRange(bytes, endPacket, bytes.length);

        }
        return messages;
    }

    /**
     * This method return length of Packet
     *
     * @return int length
     */
    public int length() {
        return bBody.length() + 14;
    }

    @Override
    public String toString() {
        try {
            return "\n==============================\n" +
                    "\nDst: " + Integer.toHexString(bDst) +
                    "\nSrc: " + Integer.toHexString(bSrc) +
                    "\nFlag: " + Integer.toHexString(bFlag & 0xff) +
                    "\nPktId: " + Long.toHexString(bPktId) +
                    "\nLen: " + wLen +
                    "\n" + bBody.toString() +
                    "\n " + new ContactID(this);
        } catch (FailInputArgumentsException e) {
            System.out.println("Sorry, can`t transform Contact Id(((");
        }
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Packet)) return false;
        Packet packet = (Packet) o;
        return bDst == packet.bDst &&
                bSrc == packet.bSrc &&
                bFlag == packet.bFlag &&
                bPktId == packet.bPktId &&
                Objects.equals(bBody, packet.bBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bDst, bSrc, bFlag, bPktId, wLen, bBody);
    }

    public static void main(String[] args) throws IOException {
        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "ERROR");

        int[] key = new int[]{0x0E79CB6E, 0xECFDEF38, 0x9C4C2F55, 0xA43809EF, 0xBFBBC2C9, 0x9F9FEC32, 0x3C4CC859, 0x03C8C02D};
        ByteBuffer bKey = ByteBuffer.allocate(key.length * Integer.BYTES);
        bKey.order(ByteOrder.LITTLE_ENDIAN);
        for (int i : key)
            bKey.putInt(i);

        BufferedReader buff = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Projects\\test_server\\src\\main\\java\\message\\tetsterMessage\\captured-packets.txt")));
        while (buff.ready()) {
            String[] str = buff.readLine().split(" ");
            byte[] bytes = new byte[str.length];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) Integer.parseInt(str[i], 16);
            }
            System.out.println(Packet.decodeMessages(bytes, bKey.array()));
        }
    }

    public byte getDst() {
        return bDst;
    }

    public byte getSrc() {
        return bSrc;
    }

    public byte getFlag() {
        return bFlag;
    }

    public long getPktId() {
        return bPktId;
    }

    public Body getBody() {
        return bBody;
    }

}
