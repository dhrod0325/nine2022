package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1PetInstance;
import ks.model.instance.L1SummonInstance;

public class S_PetMenuPacket extends ServerBasePacket {
    public S_PetMenuPacket(L1NpcInstance npc, int exppercet) {
        buildpacket(npc, exppercet);
    }

    private void buildpacket(L1NpcInstance npc, int exppercet) {
        writeC(L1Opcodes.S_OPCODE_SHOWHTML);

        if (npc instanceof L1PetInstance) { // 펫
            L1PetInstance pet = (L1PetInstance) npc;
            writeD(pet.getId());
            writeS("anicom");
            writeC(0x00);
            writeH(10);
            switch (pet.getCurrentPetStatus()) {
                case 1:
                    writeS("$469"); // 공격 태세
                    break;
                case 2:
                    writeS("$470"); // 방어 태세
                    break;
                case 5:
                    writeS("$472"); // 경계
                    break;
                case 8:
                    writeS("$613"); // 수집
                    break;
                default:
                    writeS("$471"); // 휴게
                    break;
            }
            writeS(Integer.toString(pet.getCurrentHp())); // 현재의 HP
            writeS(Integer.toString(pet.getMaxHp())); // 최대 HP
            writeS(Integer.toString(pet.getCurrentMp())); // 현재의 MP
            writeS(Integer.toString(pet.getMaxMp())); // 최대 MP
            writeS(Integer.toString(pet.getLevel())); // 레벨
            writeS(pet.getName()); // 펫의 이름을 표시
            switch (pet.getFood()) {
                case 0:
                    writeS("$612");// 아주배부름
                    break;
                case 1:
                    writeS("$611");// 배부름
                    break;
                case 2:
                    writeS("$610");// 보통
                    break;
                case 3:
                    writeS("$609");// 약간 배고픔
                    break;
                default:
                    writeS("$608");
                    break;
            }
            writeS(Integer.toString(exppercet)); // 경험치
            writeS(Integer.toString(pet.getLawful())); // 아라이먼트
        } else if (npc instanceof L1SummonInstance) { // 사몬몬스타
            L1SummonInstance summon = (L1SummonInstance) npc;
            writeD(summon.getId());
            writeS("moncom");
            writeC(0x00);
            writeH(6); // 건네주는 인수 캐릭터의 수의 모양
            switch (summon.getCurrentPetStatus()) {
                case 1:
                    writeS("$469"); // 공격 태세
                    break;
                case 2:
                    writeS("$470"); // 방어 태세
                    break;
                case 5:
                    writeS("$472"); // 경계
                    break;
                default:
                    writeS("$471"); // 휴게
                    break;
            }
            writeS(Integer.toString(summon.getCurrentHp())); // 현재의 HP
            writeS(Integer.toString(summon.getMaxHp())); // 최대 HP
            writeS(Integer.toString(summon.getCurrentMp())); // 현재의 MP
            writeS(Integer.toString(summon.getMaxMp())); // 최대 MP
            writeS(Integer.toString(summon.getLevel())); // 레벨
        }
    }
}
