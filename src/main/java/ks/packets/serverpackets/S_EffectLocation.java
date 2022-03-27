package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Location;
import ks.model.types.Point;

public class S_EffectLocation extends ServerBasePacket {
    public S_EffectLocation(Point pt, int gfxId) {
        this(pt.getX(), pt.getY(), gfxId);
    }

    public S_EffectLocation(L1Location loc, int gfxId) {
        this(loc.getX(), loc.getY(), gfxId);
    }

    public S_EffectLocation(int x, int y, int gfxId) {
        writeC(L1Opcodes.S_OPCODE_EFFECTLOCATION);
        writeH(x);
        writeH(y);
        writeH(gfxId);
        writeC(0);
    }
}
