package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;

public class S_Emblem extends ServerBasePacket {
    public S_Emblem(int emblemId) {
        try {
            String emblemFile = String.valueOf(emblemId);
            File file = new File("data/emblem/" + emblemFile);

            if (file.exists()) {
                writeC(L1Opcodes.S_OPCODE_EMBLEM);
                writeD(emblemId);
                writeByte(IOUtils.toByteArray(new FileInputStream(file)));
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }
}
