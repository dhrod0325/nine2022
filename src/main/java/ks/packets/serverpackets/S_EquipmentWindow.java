package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;


public class S_EquipmentWindow extends ServerBasePacket {
    public static final byte EQUIPMENT_INDEX_HEML = 1;
    public static final byte EQUIPMENT_INDEX_ARMOR = 2;
    public static final byte EQUIPMENT_INDEX_T = 3;
    public static final byte EQUIPMENT_INDEX_CLOAK = 4;
    public static final byte EQUIPMENT_INDEX_BOOTS = 5;
    public static final byte EQUIPMENT_INDEX_GLOVE = 6;
    public static final byte EQUIPMENT_INDEX_SHIELD = 7;
    public static final byte EQUIPMENT_INDEX_WEAPON = 8;
    public static final byte EQUIPMENT_INDEX_NECKLACE = 10;
    public static final byte EQUIPMENT_INDEX_BELT = 11;
    public static final byte EQUIPMENT_INDEX_EARRING = 12;
    public static final byte EQUIPMENT_INDEX_RING1 = 18;

    public static final byte EQUIPMENT_INDEX_RUNE1 = 22;

    public static final int TYPE_EQUIP_ON_LOGIN = 0x41;
    public static final int TYPE_EQUIP_ACTION = 0x42;

    public S_EquipmentWindow(int itemObjId, int index, boolean isEq, int type) {
        writeC(L1Opcodes.S_OPCODE_RETURNEDSTAT);
        writeC(type);
        writeD(itemObjId);
        writeC(index);
        writeC(isEq ? 1 : 0);
    }

}
