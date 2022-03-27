package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1ItemInstance;

public class S_AddItem extends ServerBasePacket {
    public S_AddItem() {
    }

    public S_AddItem(L1ItemInstance item) {
        writeC(L1Opcodes.S_OPCODE_ADDITEM);
        build(item);
    }

    public void build(L1ItemInstance item) {
        if (L1Opcodes.SERVER_VERSION == 3.1) {
            build31(item);
        } else if (L1Opcodes.SERVER_VERSION == 3.8) {
            build38(item);
        }
    }

    public void build31(L1ItemInstance item) {
        writeD(item.getId());
        writeC(item.getItem().getUseType());
        writeC(0);

        writeH(item.getGfxId());
        writeC(item.getBless());
        writeD(item.getCount());
        writeC(item.isIdentified() ? 1 : 0);

        viewName(item);
    }

    public void build38(L1ItemInstance item) {
        writeD(item.getId());
        writeH(item.getItem().getMagicCatalystType());

        int type = item.getItem().getUseType();
        if (type < 0) {
            type = 0;
        }
        writeC(type);
        int count = item.getChargeCount();
        if (count < 0) {
            count = 0;
        }

        writeC(count);

        writeH(item.getGfxId());
        writeC(item.getBless());
        writeD(item.getCount());

        int bit = 0;
        if (!item.getItem().isTradeAble()) bit += 2;//교환 불가능
        if (!item.getItem().isDeleteAble()) bit += 4;//삭제 불가능
        if (item.getItem().getSafeEnchant() < 0) bit += 8;//인챈불가능
        if (item.getBless() >= 128) bit = 46;

        if (item.isIdentified()) bit += 1;//확인

        writeC(bit);

        viewName(item);

        writeC(10);
        writeD(0);
        writeD(0);
        writeH(0);
    }

    private void viewName(L1ItemInstance item) {
        writeS(item.getViewName());

        if (!item.isIdentified()) {
            // 미감정의 경우 스테이터스를 보낼 필요는 없다
            writeC(0);
        } else {
            byte[] status = item.getStatusBytes();
            writeC(status.length);
            for (byte b : status) {
                writeC(b);
            }
        }
    }

}
