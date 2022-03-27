package ks.system.petMatch;

import ks.app.LineageAppContext;
import ks.constants.L1ItemId;
import ks.constants.L1PacketBoxType;
import ks.constants.L1SkillId;
import ks.core.datatables.item.ItemTable;
import ks.core.datatables.npc.NpcTable;
import ks.core.datatables.pet.PetTable;
import ks.model.*;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1PetInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.L1SkillUse;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SkillSound;
import ks.util.common.random.RandomUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class PetMatch {
    public static PetMatch getInstance() {
        return LineageAppContext.getBean(PetMatch.class);
    }

    public static final int MAX_PET_MATCH = 1;
    private static final short[] PET_MATCH_MAP_ID = {5125, 5131, 5132, 5133, 5134};

    public static final int STATUS_NONE = 0;
    public static final int STATUS_READY1 = 1;
    public static final int STATUS_READY2 = 2;
    public static final int STATUS_PLAYING = 3;

    private final String[] pc1Name = new String[MAX_PET_MATCH];
    private final String[] pc2Name = new String[MAX_PET_MATCH];

    private final L1PetInstance[] pet1 = new L1PetInstance[MAX_PET_MATCH];
    private final L1PetInstance[] pet2 = new L1PetInstance[MAX_PET_MATCH];

    public synchronized boolean enterPetMatch(L1PcInstance pc, int amuletId) {
        int petMatchNo = decidePetMatchNo();

        if (petMatchNo == -1) {
            return false;
        }

        L1PetInstance pet = withdrawPet(pc, amuletId, petMatchNo);

        if (pet != null) {
            L1Teleport.teleport(pc, 32799, 32868, PET_MATCH_MAP_ID[petMatchNo], 0, true);
            L1SkillUse skillUse = new L1SkillUse(pc, L1SkillId.CANCELLATION, pet.getId(), pet.getX(), pet.getY(), 0, L1SkillUse.TYPE_LOGIN);
            skillUse.run();

            PetMatchReadyTimer timer = new PetMatchReadyTimer(this, petMatchNo, pc, pet);
            timer.begin();
        }

        return true;
    }

    private L1PetInstance withdrawPet(L1PcInstance pc, int amuletId, int petMatchNo) {
        L1Pet pt = PetTable.getInstance().getTemplate(amuletId);

        if (pt == null)
            return null;

        L1Npc npcTemp = NpcTable.getInstance().getTemplate(pt.getNpcId());
        L1PetInstance pet = new L1PetInstance(npcTemp, pc, pt);
        pet.setPetCost(6);

        int status = getPetMatchStatus(petMatchNo);

        if (status == STATUS_NONE) {
            LineageAppContext.commonTaskScheduler().schedule(() -> L1Teleport.npcTeleport(pet, 32799, 32865, PET_MATCH_MAP_ID[petMatchNo], 0, true), Instant.now().plusMillis(100));
        } else if (status == STATUS_READY1) {
            LineageAppContext.commonTaskScheduler().schedule(() -> L1Teleport.npcTeleport(pet, 32799, 32861, PET_MATCH_MAP_ID[petMatchNo], 4, true), Instant.now().plusMillis(100));
        }

        return pet;
    }

    private int decidePetMatchNo() {
        for (int i = 0; i < MAX_PET_MATCH; i++) {
            int status = getPetMatchStatus(i);

            if (status == STATUS_READY1 || status == STATUS_READY2) {
                return i;
            }
        }

        for (int i = 0; i < MAX_PET_MATCH; i++) {
            int status = getPetMatchStatus(i);

            if (status == STATUS_NONE) {
                return i;
            }
        }

        return -1;
    }

    public synchronized int getPetMatchStatus(int petMatchNo) {
        L1PcInstance pc1 = null;
        L1PcInstance pc2 = null;

        if (pc1Name[petMatchNo] != null) {
            pc1 = L1World.getInstance().getPlayer(pc1Name[petMatchNo]);
        }

        if (pc2Name[petMatchNo] != null) {
            pc2 = L1World.getInstance().getPlayer(pc2Name[petMatchNo]);
        }

        if (pc1 == null && pc2 == null)
            return STATUS_NONE;

        if (pc1 == null) {
            if (pc2.getMapId() == PET_MATCH_MAP_ID[petMatchNo]) {
                return STATUS_READY2;
            } else {
                pc2Name[petMatchNo] = null;
                pet2[petMatchNo] = null;

                return STATUS_NONE;
            }
        }

        if (pc2 == null) {
            if (pc1.getMapId() == PET_MATCH_MAP_ID[petMatchNo]) {
                return STATUS_READY1;
            } else {
                pc1Name[petMatchNo] = null;
                pet1[petMatchNo] = null;
                return STATUS_NONE;
            }
        }

        // PC가 시합장에 2명 있는 경우
        if (pc1.getMapId() == PET_MATCH_MAP_ID[petMatchNo] && pc2.getMapId() == PET_MATCH_MAP_ID[petMatchNo]) {
            return STATUS_PLAYING;
        }

        // PC가 시합장에 1명 있는 경우
        if (pc1.getMapId() == PET_MATCH_MAP_ID[petMatchNo]) {
            pc2Name[petMatchNo] = null;
            pet2[petMatchNo] = null;
            return STATUS_READY1;
        }

        if (pc2.getMapId() == PET_MATCH_MAP_ID[petMatchNo]) {
            pc1Name[petMatchNo] = null;
            pet1[petMatchNo] = null;
            return STATUS_READY2;
        }

        return STATUS_NONE;
    }

    public void endPetMatch(int petMatchNo, int winNo) {
        sleep(4000);

        L1PcInstance pc1 = L1World.getInstance().getPlayer(pc1Name[petMatchNo]);
        L1PcInstance pc2 = L1World.getInstance().getPlayer(pc2Name[petMatchNo]);

        if (winNo == 1) {
            giveMedal(pc1, petMatchNo, true);
            giveMedal(pc2, petMatchNo, false);
        } else if (winNo == 2) {
            giveMedal(pc1, petMatchNo, false);
            giveMedal(pc2, petMatchNo, true);
        } else if (winNo == 3) {
            giveMedal(pc1, petMatchNo, false);
            giveMedal(pc2, petMatchNo, false);
        }

        pc1.sendPackets(new S_PacketBox(L1PacketBoxType.MINIGAME_TIME_CLEAR));
        pc2.sendPackets(new S_PacketBox(L1PacketBoxType.MINIGAME_TIME_CLEAR));

        quitPetMatch(petMatchNo);
    }

    private void giveMedal(L1PcInstance pc, int petMatchNo, boolean isWin) {
        if (pc == null) {
            return;
        }

        if (pc.getMapId() != PET_MATCH_MAP_ID[petMatchNo]) {
            return;
        }

        if (isWin) {
            pc.sendPackets(new S_ServerMessage(1166, pc.getName()));  // %0%s펫 매치로 승리를 거두었습니다.
            L1ItemInstance item = ItemTable.getInstance().createItem(41309);
            int count = 3;

            if (item != null) {
                if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
                    item.setCount(count);
                    pc.getInventory().storeItem(item);
                    if (RandomUtils.nextInt(10) <= 2) {
                        pc.getInventory().storeItem(L1ItemId.PETMATCH_WINNER_PIECE, 1);
                    }
                    pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                }
            }
        } else {
            L1ItemInstance item = ItemTable.getInstance().createItem(41309);
            int count = 1;

            if (item != null) {
                if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
                    item.setCount(count);
                    pc.getInventory().storeItem(item);
                    pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                }
            }
        }
    }

    private void quitPetMatch(int petMatchNo) {
        quitPetMatch(petMatchNo, pc1Name, pet1);
        quitPetMatch(petMatchNo, pc2Name, pet2);
    }

    private void quitPetMatch(int petMatchNo, String[] pcNames, L1PetInstance[] pets) {
        L1PcInstance pc = L1World.getInstance().getPlayer(pcNames[petMatchNo]);

        if (pc != null && pc.getMapId() == PET_MATCH_MAP_ID[petMatchNo]) {
            for (Object object : pc.getPetList().values()) {
                if (object instanceof L1PetInstance) {
                    L1PetInstance pet = (L1PetInstance) object;
                    pet.dropItem();
                    pc.getPetList().remove(pet.getId());
                    pet.deleteMe();
                }
            }

            L1Teleport.teleport(pc, 32630, 32744, (short) 4, 4, true);
        }

        pcNames[petMatchNo] = null;
        pets[petMatchNo] = null;
    }

    public int setPetMatchPc(int petMatchNo, L1PcInstance pc, L1PetInstance pet) {
        int status = getPetMatchStatus(petMatchNo);

        switch (status) {
            case STATUS_NONE:
                pc1Name[petMatchNo] = pc.getName();
                pet1[petMatchNo] = pet;
                return STATUS_READY1;
            case STATUS_READY1:
                pc2Name[petMatchNo] = pc.getName();
                pet2[petMatchNo] = pet;
                return STATUS_PLAYING;
            case STATUS_READY2:
                pc1Name[petMatchNo] = pc.getName();
                pet1[petMatchNo] = pet;
                return STATUS_PLAYING;
            default:
                return STATUS_NONE;
        }
    }

    public void sleep(long mill) {
        try {
            Thread.sleep(mill);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startPetMatch(int petMatchNo) {
        sendMessage("펫 매치가 시작됩니다", petMatchNo);

        sleep(3000);

        int a = 3204 + RandomUtils.nextInt(6);
        int b = 3204 + RandomUtils.nextInt(6);

        Broadcaster.broadcastPacket(pet1[petMatchNo], new S_SkillSound(pet1[petMatchNo].getId(), a));
        Broadcaster.broadcastPacket(pet2[petMatchNo], new S_SkillSound(pet2[petMatchNo].getId(), b));

        sleep(4000);

        if (a > b) {
            pet1[petMatchNo].useHastePotion(500);
            pet2[petMatchNo].setSleepTime(4000);
        } else if (b > a) {
            pet2[petMatchNo].useHastePotion(500);
            pet1[petMatchNo].setSleepTime(4000);
        }

        L1PcInstance pc1 = L1World.getInstance().getPlayer(pc1Name[petMatchNo]);
        L1PcInstance pc2 = L1World.getInstance().getPlayer(pc2Name[petMatchNo]);

        sleep(2000);

        pet1[petMatchNo].setCurrentPetStatus(1);
        pet1[petMatchNo].setTarget(pet2[petMatchNo]);

        pet2[petMatchNo].setCurrentPetStatus(1);
        pet2[petMatchNo].setTarget(pet1[petMatchNo]);

        pc1.sendPackets(new S_PacketBox(L1PacketBoxType.MINIGAME_TIME, 300));
        pc2.sendPackets(new S_PacketBox(L1PacketBoxType.MINIGAME_TIME, 300));

        PetMatchTimer timer = new PetMatchTimer(this, pet1[petMatchNo], pet2[petMatchNo], petMatchNo);
        timer.begin();
    }

    private void sendMessage(String msg, int petMatchNo) {
        L1PcInstance pc1 = L1World.getInstance().getPlayer(pc1Name[petMatchNo]);
        L1PcInstance pc2 = L1World.getInstance().getPlayer(pc2Name[petMatchNo]);

        pc1.sendPackets(msg);
        pc1.sendGreenMessage(msg);
        pc2.sendPackets(msg);
        pc2.sendGreenMessage(msg);
    }
}
