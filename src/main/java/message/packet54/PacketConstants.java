package message.packet54;

interface PacketConstants {
    int USN_LENGTH = 6;
    byte BYTE_MAGIC = 0x54; //First byte in message

    byte FLAG_USN = 0x40;
    byte FLAG_CRYPT = (byte) 0x80;
    byte FLAG_USN_CRYPT = (byte) 0xC0;

    int SIZE_OF_HEADER = 12;
    int FIRST_CRC_DISTANCE = 10;

    int POSITION_MAGIC = 0;
    int POSITION_DST = 1;
    int POSITION_SRC = 2;
    int POSITION_FLAG = 3;
    int POSITION_PKTID = 4;
    int POSITION_LENGTH = 8;
    int POSITION_USN_LENGTH = 2;
}
