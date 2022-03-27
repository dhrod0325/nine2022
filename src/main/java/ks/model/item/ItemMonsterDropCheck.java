package ks.model.item;

import ks.model.L1Character;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.attack.utils.L1WeaponUtils;
import ks.model.instance.L1DoorInstance;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1MonsterInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_DropInfo;
import ks.packets.serverpackets.S_DropInfo2;
import ks.packets.serverpackets.S_NpcChatPacket;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemMonsterDropCheck extends L1ItemInstance {
    private final Logger logger = LogManager.getLogger();

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        try {
            if (cha instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) cha;
                int objectId = packet.readD();

                L1Object findObject = L1World.getInstance().findObject(objectId);

                if (findObject instanceof L1MonsterInstance) {
                    L1MonsterInstance mon = (L1MonsterInstance) findObject;

                    int id;

                    if (mon.getTemplate().getTransformId() == 45590) {
                        id = transRiferMonId(mon.getMapId());
                    } else {
                        id = mon.getNpcId();
                    }

                    if (getItemId() == 6000042) {
                        pc.sendPackets(new S_DropInfo(id));
                    } else if (getItemId() == 6000100) {
                        pc.sendPackets(new S_DropInfo2(mon));
                    }

                    String msg = "취약:" + L1WeaponUtils.getWeakAttrString(mon.getTemplate().getWeakAttr());

                    pc.sendPackets(new S_NpcChatPacket(mon, msg, 0));

                } else {
                    pc.sendPackets(new S_SystemMessage("몬스터를 선택해주세요"));
                }

                if (pc.isGm()) {
                    int npcId = 0;

                    if (findObject instanceof L1NpcInstance) {
                        npcId = ((L1NpcInstance) findObject).getNpcId();
                    }

                    StringBuilder msg = new StringBuilder("OBJECT ID : " + objectId + ",NPCID : " + npcId).append("\n");

                    if (findObject instanceof L1MonsterInstance) {
                        L1MonsterInstance mon = ((L1MonsterInstance) findObject);

                        msg.append("MAX HP : ").append(mon.getMaxHp()).append(", OPTION HP : ").append(mon.getOptionHp()).append("\n");
                        msg.append("CURRENT HP : ").append(mon.getCurrentHp()).append("\n");
                        msg.append("x : ").append(mon.getX()).append(",y : ").append(mon.getY()).append("\n");
                        if (mon.getSpawn() != null) {
                            msg.append("spawnId : ").append(mon.getSpawn().getId()).append(",targetDoor : ").append(mon.getSpawn().getDoorId());
                        }

                    }

                    if (findObject instanceof L1DoorInstance) {
                        msg.append(",doorId : ").append(((L1DoorInstance) findObject).getDoorId());
                    }

                    pc.sendPackets(new S_SystemMessage(msg.toString()));
                }
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    private int transRiferMonId(int mapId) {
        switch (mapId) {
            case 110:
                return 45380;
            case 120:
                return 45409;
            case 130:
                return 45471;
            case 140:
                return 45455;
            case 150:
                return 45496;
            case 160:
                return 45524;
            case 170:
                return 45528;
            case 180:
                return 45540;
            case 190:
                return 45589;
            case 200:
                return 45620;
        }

        return 0;
    }
}
