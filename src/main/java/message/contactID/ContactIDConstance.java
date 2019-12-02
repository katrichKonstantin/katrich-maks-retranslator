package message.contactID;

public interface ContactIDConstance {

    char LINE_NUMBER = '0';
    char FIRST_CHAR = '5';
    String RECEIVER_NUMBER = "01";
    String SIGN_FORMAT = "18";
    String END_STRING = "\\x14";

    byte CMD_IOCTL_REPLY = (byte) 0x92;
    byte IO_NOTIFY_LOG = (byte) 0x0D;
    byte TYPE_MAKS_PRO = (byte) 0x9B;
    byte CMD_OK = (byte) 0x81 ;

    int IO_REPLY_LOG_POSITION = 2;
    int COUNT_LAST_COMMAND = 4;
    int UID_POSITION = 5;
    int ACCOUNT_POSITION = 0;
    int EVENT_POSITION = 2;
    int GROUP_POSITION = 4;
    int ZONE_POSITION = 5;

    int ACCOUNT_STR_POSITION = 7;
    int Q_STR_POSITION = 11;
    int EVENT_STR_POSITION = 12;
    int GROUP_STR_POSITION = 15;
    int ZONE_STR_POSITION = 17;
}
