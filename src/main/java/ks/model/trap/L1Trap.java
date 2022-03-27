package ks.model.trap;

import ks.core.storage.TrapStorage;
import ks.model.L1World;
import ks.model.instance.L1TrapInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_EffectLocation;

public abstract class L1Trap {
    protected final int id;

    protected final int gfxId;

    protected final boolean isDetectionable;

    public L1Trap(TrapStorage storage) {
        id = storage.getInt("id");
        gfxId = storage.getInt("gfxId");
        isDetectionable = storage.getBoolean("isDetectionable");
    }

    public L1Trap(int id, int gfxId, boolean detectionable) {
        this.id = id;
        this.gfxId = gfxId;
        isDetectionable = detectionable;
    }

    public static L1Trap newNull() {
        return new L1NullTrap();
    }

    public int getId() {
        return id;
    }

    public int getGfxId() {
        return gfxId;
    }

    protected void sendEffect(L1TrapInstance trapObj) {
        if (getGfxId() == 0) {
            return;
        }

        for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(trapObj)) {
            pc.sendPackets(new S_EffectLocation(trapObj.getLocation(), getGfxId()));
        }
    }

    public abstract void onTrod(L1PcInstance from, L1TrapInstance trap);

    public void onDetection(L1TrapInstance trapObj) {
        if (isDetectionable) {
            sendEffect(trapObj);
        }
    }
}

