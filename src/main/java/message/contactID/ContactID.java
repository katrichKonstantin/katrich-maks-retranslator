package message.contactID;

import message.exeption.BrokenPacketException;
import message.exeption.FailInputArgumentsException;
import message.exeption.IllegalContactIdException;
import message.packet54.*;
import message.utils.Utility;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;

public class ContactID {
    private byte[] bCommand, lastCommand;
    private String model, event, zone, account, Q, group;
    private int uid;

    /**
     * This constructor creat object from bytes array from Body of Packet
     * @param packet entering packet
     * @throws FailInputArgumentsException first byte is Irregular
     */
    public ContactID(Packet packet) throws FailInputArgumentsException {
        int startMainMessages = 1;
        this.bCommand = packet.getBody().getbComm();

        if (bCommand[0] == ContactIDConstance.CMD_OK) { // Maybe useless because if first byte is CMD_OK we don`t use ContactID form
            model = hardBit();
            return;
        }

        if (bCommand[startMainMessages] == ContactIDConstance.TYPE_MAKS_PRO) {
            if (bCommand[startMainMessages + 1] == 1) {
                //Something is TYPE_MAKS_PRO is 1
                startMainMessages += 4; //Something like this
            } else if (bCommand[startMainMessages + 1] == 0) {
                //Something is TYPE_MAKS_PRO is 0. Maybe ignore
            }
            startMainMessages += 2;
        }

        if (bCommand[startMainMessages] == ContactIDConstance.IO_NOTIFY_LOG) {
            startMainMessages += 2;
            if (bCommand[startMainMessages - 1] == 1) { //If we have 01 of IO_NOTIFY_LOG
                lastCommand = new byte[ContactIDConstance.COUNT_LAST_COMMAND];
                for (int i = 0, s = startMainMessages; i < lastCommand.length; i++)
                    lastCommand[i] = bCommand[s++];
                uid = Utility.takeInt(bCommand, startMainMessages, ByteOrder.BIG_ENDIAN);
                startMainMessages += 4;
            } else if (bCommand[startMainMessages - 1] == 0) { //If we have 00 of IO_NOTIFY_LOG
                //Ignore
            }
        }

        account = Utility.takeHexString(bCommand, startMainMessages + ContactIDConstance.ACCOUNT_POSITION, 2, ByteOrder.BIG_ENDIAN);
        event = Utility.takeHexString(bCommand, startMainMessages + ContactIDConstance.EVENT_POSITION, 2, ByteOrder.BIG_ENDIAN);
        Q = String.valueOf(event.charAt(0));
        event = event.substring(1, 4);
        group = Utility.takeHexString(bCommand, startMainMessages + ContactIDConstance.GROUP_POSITION, 1, ByteOrder.BIG_ENDIAN);
        zone = Utility.takeHexString(bCommand, startMainMessages + ContactIDConstance.ZONE_POSITION, 2, ByteOrder.BIG_ENDIAN);
        zone = zone.substring(1, 4);
        //if(bCommand[0] != ContactIDConstance.commandByte)
    }

    /**
     * This constructor create object from ContactId form
     * @param strContactId ContactId form string
     * @param lastCommand array of last command (length 4)
     * @throws BrokenPacketException Irregular Q
     */
    public ContactID(String strContactId, byte[] lastCommand) throws BrokenPacketException {
        this(strContactId);
        this.lastCommand = lastCommand;
        this.uid = Utility.takeInt(lastCommand,0,ByteOrder.BIG_ENDIAN);
    }

    /**
     * This constructor create object from ContactId form
     * @param strContactId ContactId form string
     * @throws BrokenPacketException Irregular Q
     */
    public ContactID(String strContactId) throws BrokenPacketException {
        account = strContactId.substring(ContactIDConstance.ACCOUNT_STR_POSITION, ContactIDConstance.Q_STR_POSITION);

        switch (String.valueOf(strContactId.charAt(ContactIDConstance.Q_STR_POSITION))) {
            case "E":
                Q = "1";
                break;
            case "P":
                Q = "2";
                break;
            case "R":
                Q = "3";
                break;
            default:
                throw new BrokenPacketException();
        }
        event = strContactId.substring(ContactIDConstance.EVENT_STR_POSITION, ContactIDConstance.GROUP_STR_POSITION);
        group = strContactId.substring(ContactIDConstance.GROUP_STR_POSITION, ContactIDConstance.ZONE_STR_POSITION);
        zone = strContactId.substring(ContactIDConstance.ZONE_STR_POSITION, ContactIDConstance.ZONE_STR_POSITION + 3);
    }

    /**
     * This method transform this object to bCommand form
     * @return byte array
     */
    public byte[] toBytesCommand() {
        ByteBuffer buff = ByteBuffer.allocate(1 + (uid == 0 ? 2 : 6) + 7).order(ByteOrder.BIG_ENDIAN);
        buff.put(ContactIDConstance.CMD_IOCTL_REPLY); //Flag of CMD_IOCTL_REPLY command

        buff.put(ContactIDConstance.IO_NOTIFY_LOG); //Check of UID (storage of last command)
        if (uid != 0) {
            buff.put((byte) 0x01); //Flag "Have UID"
            buff.put(lastCommand);
        } else
            buff.put((byte) 0x00); //Flag "Don`t have UID"
        buff.put(Utility.putByteFromHexString(account, 0));
        buff.put(Utility.putByteFromHexString(account, 2));
        buff.put(Utility.putByteFromHexString(Q + event.charAt(0), 0));
        buff.put(Utility.putByteFromHexString(event, 1));
        buff.put(Utility.putByteFromHexString(group, 0));
        buff.put(Utility.putByteFromHexString("0" + zone, 0));
        buff.put(Utility.putByteFromHexString(zone, 1));
        return buff.array();
    }

    /**
     * This method transform this object to ContactID form
     *
     * @return ContactId form String
     * @throws BrokenPacketException Irregular Q
     */
    public String toContactIDForm() throws BrokenPacketException {
        if (model != null) return model;
        StringBuilder str = new StringBuilder();
        str.append(ContactIDConstance.FIRST_CHAR)
                .append(ContactIDConstance.RECEIVER_NUMBER)
                .append(ContactIDConstance.LINE_NUMBER)
                .append(' ')
                .append(ContactIDConstance.SIGN_FORMAT)
                .append(account);

        switch (Q) {
            case "1":
                str.append('E');
                break;
            case "2":
                str.append('P');
                break;
            case "3":
                str.append('R');
                break;
            default:
                throw new BrokenPacketException();
        }
        str.append(event)
                .append(group)
                .append(zone)
                .append(ContactIDConstance.END_STRING);
        model = str.toString();
        return model;
    }

    /**
     * This method return hardBit
     * @return hardBit
     */
    public static String hardBit() {
        return "1001@\\x14";
    }

    public byte[] getLastCommand() {
        return lastCommand;
    }

    public String getEvent() {
        return event;
    }

    public String getZone() {
        return zone;
    }

    public String getAccount() {
        return account;
    }

    public String getQ() {
        return Q;
    }

    public String getGroup() {
        return group;
    }

    @Override
    public String toString() {
        try {
            toContactIDForm();
        } catch (BrokenPacketException e) {
            e.printStackTrace();
        }
        return " Last command " + Arrays.toString(lastCommand) +
                " account " + account +
                " event " + Q + "" + event +
                " zone " + zone +
                " group " + group +
                " \n send: " + model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactID)) return false;
        ContactID contactID = (ContactID) o;
        return uid == contactID.uid &&
                event.equals(contactID.event) &&
                zone.equals(contactID.zone) &&
                account.equals(contactID.account) &&
                group.equals(contactID.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, zone, account, group, uid);
    }

    public static void main(String[] args) throws BrokenPacketException {
        String[] str = {"92", "9B", "00", "0D", "01", "E0", "05", "01", "00", "40", "50", "34", "02", "01", "00", "01"};
        byte[] bytes = new byte[str.length];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(str[i], 16);
        }
        Packet packet = new Packet((byte) 1, (byte) 1, (byte) 0, 80, bytes);
        ContactID contactID = new ContactID(packet);
        System.out.println(contactID.toContactIDForm());

        ContactID contactID1 = new ContactID(contactID.toContactIDForm());
        System.out.println(contactID1.toContactIDForm());
        System.out.println(contactID);

        for (byte b : contactID.toBytesCommand()) {
            System.out.print((String.format("%02x ", b)));
        }
        //System.out.println(Arrays.toString(contactID1.toBytesCommand()));
    }
}
