package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1ItemInstance;

public class S_IdentifyDesc extends ServerBasePacket {
    public S_IdentifyDesc(L1ItemInstance item) {
        buildPacket(item);
    }

    private void buildPacket(L1ItemInstance item) {
        writeC(L1Opcodes.S_OPCODE_IDENTIFYDESC);

        if (item.getItem().getItemDescId() > 0) {
            writeH(item.getItem().getItemDescId());
        } else {
            writeH(1);
        }

        StringBuilder name = new StringBuilder();

        if (item.getBless() == 0) {
            name.append("$227 ");
        } else if (item.getBless() == 2) {
            name.append("$228 "); // 저주해졌다
        }

        name.append(item.getItem().getNameId());

        if (item.getItem().getType2() == 1) { // weapon
            String magicDmg = "";

            if (item.getPc() != null) {
                int dmg = item.getDmgByMagic() + item.getHolyDmgByMagic();
                if (dmg > 0) {
                    magicDmg = "(+" + dmg + ")";
                }
            }

            writeH(134);
            writeC(3);
            writeS(name.toString());
            writeS(item.getItem().getDmgSmall() + "+" + item.getEnchantLevel() + magicDmg);
            writeS(item.getItem().getDmgLarge() + "+" + item.getEnchantLevel() + magicDmg);
        } else if (item.getItem().getType2() == 2) {
            if (item.getItem().getItemId() == 20383) { // 기마용 헤룸
                writeH(137); // \f1%0：사용 가능 회수%1［무게%2］
                writeC(3);
                writeS(name.toString());
                writeS(String.valueOf(item.getChargeCount()));
            } else {
                if (item.getEnchantLevel() >= 0) {
                    writeH(135); // \f1%0：방어력%1 방어도구
                    writeC(2);
                    writeS(name.toString());
                    if (item.getAcByMagic() > 0) {
                        writeS(Math.abs(item.getItem().getAc()) + "+" + item.getEnchantLevel() + "(" + item.getAcByMagic() + ")");
                    } else {
                        writeS(Math.abs(item.getItem().getAc()) + "+" + item.getEnchantLevel());
                    }
                } else if (item.getEnchantLevel() < 0) {
                    writeH(135);
                    writeC(2);
                    writeS(name.toString());
                    if (item.getAcByMagic() > 0) {
                        writeS(Math.abs(item.getItem().getAc()) + "" + item.getEnchantLevel() + "(" + item.getAcByMagic() + ")");
                    } else {
                        writeS(Math.abs(item.getItem().getAc()) + "" + item.getEnchantLevel());
                    }
                }
            }

        } else if (item.getItem().getType2() == 0) { // etcitem
            if (item.getItem().getType() == 1) { // wand
                writeH(137); // \f1%0：사용 가능 회수%1［무게%2］
                writeC(3);
                writeS(name.toString());
                writeS(String.valueOf(item.getChargeCount()));
            } else if (item.getItem().getType() == 2) {
                writeH(138);
                writeC(2);
                name.append(": $231 "); // 나머지의 연료
                name.append(item.getRemainingTime());
                writeS(name.toString());
            } else if (item.getItem().getType() == 7) { // food
                writeH(136); // \f1%0：만복도%1［무게%2］
                writeC(3);
                writeS(name.toString());
                writeS(String.valueOf(item.getItem().getFoodVolume()));
            } else {
                writeH(138); // \f1%0：［무게%1］
                writeC(2);
                writeS(name.toString());
            }

            writeS(String.valueOf(item.getWeight()));
        } else if (item.getItem().getType2() == 3) { // 레이스 티켓
            writeH(138); // \f1%0：［무게%1］
            writeC(2);
            writeS(name.toString());
        }
    }
}
