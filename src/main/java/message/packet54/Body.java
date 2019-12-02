package message.packet54;

import message.exeption.BrokenPacketException;
import message.exeption.FailInputArgumentsException;
import message.exeption.FailTakeException;
import message.utils.CRC16;
import message.utils.Crypt;
import message.utils.Utility;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import static message.utils.Utility.takeShort;

/**
 * Body of package
 */
public class Body {
    private static final Random random = new Random();
    private short commLen;
    private long usn;
    private byte usnLen = 0, bFlag;
    private byte[] bComm, bModel = null;

    /**
     * Create Body with enter and Command AMD USN
     *
     * @param usn     unique customer identifier
     * @param command unique package identifier
     */
    public Body(long usn, byte[] command, byte bFlag) throws FailInputArgumentsException {
        //Check bFlag to USN byte.
        if ((bFlag & PacketConstants.FLAG_USN) == 0) throw new FailInputArgumentsException("Bit of USN in bFlag is zero when we use USN.");
        //Check USN byte.
        if (usn == 0) throw new FailInputArgumentsException("USN is zero when we mast use it.");
        this.usn = usn;
        usnLen = PacketConstants.USN_LENGTH;
        this.bComm = command;
        this.bFlag = bFlag;
        commLen = (short) command.length;
    }

    /**
     * Create Body with enter Command AND WITHOUT USN
     *
     * @param command unique package identifier
     */
    public Body(byte[] command, byte bFlag) throws FailInputArgumentsException {
        if (command == null) throw new FailInputArgumentsException("Command is null!");
        //Check bFlag to USN byte. Prohibits use by other constructor.
        if ((bFlag & PacketConstants.FLAG_USN) != 0) throw new FailInputArgumentsException("Bit of USN in bFlag is zero when we use USN.");
        this.bComm = command;
        this.bFlag = bFlag;
        commLen = (short) command.length;
    }

    /**
     * Transform bytes array to the Body
     *
     * @param bytes enter bytes array
     */
    public Body(byte[] bytes, byte bFlag, byte[] key) throws BrokenPacketException {
        this.bFlag = bFlag;
        boolean isUsn = (bFlag & PacketConstants.FLAG_USN) != 0; //Is USN flag
        byte[] bodyBytes = Arrays.copyOfRange(bytes, PacketConstants.SIZE_OF_HEADER, bytes.length - Short.BYTES);

        if ((bFlag & PacketConstants.FLAG_CRYPT) != 0) { // If packet is crypt
            bodyBytes = Crypt.decode(new Crypt.Key(key), bodyBytes);
            bodyBytes = deleteTrash(bodyBytes);
        }

        bModel = bodyBytes;
        if (isUsn) { // If we have USn in message
            usnLen = (bodyBytes[PacketConstants.POSITION_USN_LENGTH]);
            if (usnLen != 6) throw new FailTakeException("Illegal key");
            usn = Utility.takeLongFromNBytes(bodyBytes, PacketConstants.POSITION_USN_LENGTH + 1, usnLen, ByteOrder.LITTLE_ENDIAN);
            if (usn == 0) throw new FailTakeException("USN is null!");
        }

        short lenOfCommandMessage = takeShort(bodyBytes, 0); //Take length of command part

        try {
            if (takeShort(bodyBytes, PacketConstants.POSITION_USN_LENGTH + (isUsn ? 1 : 0) + usnLen + lenOfCommandMessage) !=
                    CRC16.getCRC(bodyBytes, (isUsn ? 1 : 0) + usnLen + Short.BYTES, lenOfCommandMessage)) {
                bModel = null;
                throw new BrokenPacketException("Second CRC is incorrect!");
            }
            //If crc is normal
            commLen = lenOfCommandMessage;
            bComm = Arrays.copyOfRange(bodyBytes, usnLen + (isUsn ? 1 : 0) + Short.BYTES, usnLen + (isUsn ? 1 : 0) + Short.BYTES + lenOfCommandMessage);
        } catch (ArrayIndexOutOfBoundsException e) { // If lenOfCommand part is less illegal
            throw new BrokenPacketException("Incorrect key crypt. Or package is broken. e: " + e.getMessage());
        }
    }

    /**
     * This method delete trash bytes from command part
     *
     * @param value command part
     * @return command part without trash
     */
    private static byte[] deleteTrash(byte[] value) {
        byte[] result = new byte[value.length - (value.length / 8)];
        for (int i = 0, s = 0; i < value.length; i++) {
            if (((i + 1) % 8) == 0 && i != 0) continue;
            result[s++] = value[i];
        }
        return result;
    }

    /**
     * This method encoding command part. Add trash and encoding
     *
     * @param value command part
     * @param isUSN flag is USn in packet
     * @param key   in what key we mast encrypt bytes
     * @return encoding command part
     */
    private static byte[] encodeBody(byte[] value, boolean isUSN, byte[] key) {
        int resLength = value.length + (value.length / 8);
        if (resLength % 8 != 0) resLength = 8 - (resLength % 8) + resLength;
        byte[] result = new byte[resLength];
        for (int i = 0, s = 0; i < resLength; i++) { // Adding trash
            if (((i + 1) % 8 == 0 && i != 0) || s == value.length)
                result[i] = (byte) random.nextInt();
            else result[i] = value[s++];
        }
        result = Crypt.encode(new Crypt.Key(key), result);

        if (isUSN) { //If we have USN in packet
            int countRandBytes = 0;
            countRandBytes = random.nextInt(8);
            byte[] newResult = new byte[resLength + countRandBytes];
            System.arraycopy(result, 0, newResult, 0, result.length);
            for (int i = resLength; i < resLength + countRandBytes; i++) //Adding trash to end bytes
                newResult[i] = (byte) random.nextInt();
            result = newResult;
        }
        return result;
    }

    /**
     * Transform Body to bytes array
     *
     * @return byte[]
     */
    public byte[] toBytes(byte[] key, byte bFlag) throws FailTakeException {
        if (bModel != null) return bModel;

        this.bFlag = bFlag;

        boolean isUSN = (bFlag & PacketConstants.FLAG_USN) != 0;
        if (isUSN) usnLen = PacketConstants.USN_LENGTH;

        int clearLength = Short.BYTES * 2 + commLen + (isUSN ? 1 + usnLen : 0);

        ByteBuffer buff = ByteBuffer.allocate(clearLength).order(ByteOrder.LITTLE_ENDIAN); // Create buffer with little ending order
        buff.putShort(commLen);//Add length of command part
        if (isUSN) { //Add USN and USN length
            buff.put(usnLen);
            byte[] arr = new byte[usnLen];
            Utility.putLongNBytes(usn, arr, 0, 6, ByteOrder.LITTLE_ENDIAN);
            buff.put(arr);
        }
        buff.put(bComm); //Add command part
        buff.putShort(CRC16.getCRC(buff.array(), Short.BYTES + usnLen + (isUSN ? 1 : 0), bComm.length)); //Add crc

        if ((bFlag & PacketConstants.FLAG_CRYPT) != 0) // If we want to encrypt command bytes
            bModel = encodeBody(buff.array(), !isUSN, key);
        else bModel = buff.array();
        return bModel;
    }

    /**
     * Return length of Body
     *
     * @return length
     */
    public short length() {
        try {
            bModel = toBytes(null, bFlag);
        } catch (FailTakeException e) {
            e.printStackTrace();
            return 0;
        }
        return (short) bModel.length;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("\nDefault S (ua) key\n");
        if (usnLen != 0) {
            str.append("USN  ");
            str.append(Long.toHexString(usn));
        }
        str.append("\n");
        for (byte b : bComm)
            str.append(String.format("%02X ", b));
        return str.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Body)) return false;
        Body body = (Body) o;
        return commLen == body.commLen &&
                usnLen == body.usnLen &&
                usn == body.usn &&
                Arrays.equals(bComm, body.bComm);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(commLen, usnLen);
        result = 31 * result + (int) usn;
        result = 31 * result + Arrays.hashCode(bComm);
        return result;
    }

    public short getCommLen() {
        return commLen;
    }

    public long getUsn() {
        return usn;
    }

    public byte[] getbComm() {
        return bComm;
    }
}