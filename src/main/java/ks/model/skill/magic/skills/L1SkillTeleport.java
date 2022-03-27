package ks.model.skill.magic.skills;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Character;
import ks.model.L1Location;
import ks.model.L1Teleport;
import ks.model.L1World;
import ks.model.bookMark.L1BookMark;
import ks.model.map.L1Map;
import ks.model.map.L1WorldMap;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_ChatPacket;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.L1TeleportUtils;
import ks.util.common.random.RandomUtils;

import java.util.List;

import static ks.constants.L1SkillId.MASS_TELEPORT;

public class L1SkillTeleport extends L1SkillAdapter {
    public L1SkillTeleport(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character cha = request.getTargetCharacter();

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1BookMark bookMark = pc.getBookMark().findByLocation(request.getTargetX(), request.getTargetY(), request.getTargetId());

            if (bookMark != null) {
                if (pc.isEscapable()) {
                    int newX = bookMark.getLocX();
                    int newY = bookMark.getLocY();
                    short mapId = bookMark.getMapId();
                    L1Map map = L1WorldMap.getInstance().getMap(mapId);

                    if (skillId == MASS_TELEPORT) {
                        List<L1PcInstance> clanMember = L1World.getInstance().getVisiblePlayer(pc);

                        for (L1PcInstance member : clanMember) {
                            if (pc.getLocation().getTileLineDistance(member.getLocation()) >= 3) {
                                continue;
                            }
                            if (pc.getMapId() != member.getMapId()) {
                                continue;
                            }
                            if (member.getClanId() != pc.getClanId()) {
                                continue;
                            }
                            if (member.getClanId() == 0) {
                                continue;
                            }

                            member.sendPackets(new S_SystemMessage("혈맹원 '" + pc.getName() + "' 이(가) 매스 텔레포트를 사용 하였습니다."));

                            int newX2 = newX + RandomUtils.nextInt(3) + 1;
                            int newY2 = newY + RandomUtils.nextInt(3) + 1;

                            if (map.isInMap(newX2, newY2) && map.isPassable(newX2, newY2)) {
                                L1Teleport.teleport(member, newX2, newY2, mapId, member.getHeading(), true);
                            } else {
                                L1Teleport.teleport(member, newX, newY, mapId, member.getHeading(), true);
                            }
                        }
                    }

                    if (pc.getInventory().checkItem(20288)) {
                        L1Teleport.teleport(pc, newX, newY, mapId, pc.getHeading(), true);
                    } else {
                        int newX2 = newX + RandomUtils.nextInt(15);
                        int newY2 = newY + RandomUtils.nextInt(15);

                        if (map.isInMap(newX2, newY2) && map.isPassable(newX2, newY2)) {
                            L1Teleport.teleport(pc, newX2, newY2, mapId, pc.getHeading(), true);
                        } else {
                            L1Teleport.teleport(pc, newX, newY, mapId, pc.getHeading(), true);
                        }
                    }
                } else {
                    L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), false);
                    pc.sendPackets(new S_ChatPacket(pc, "아무일도 일어나지 않았습니다.", L1Opcodes.S_OPCODE_MSG, 20));
                }
            } else {
                boolean isTeleportAble = pc.isTeleportAble();

                if (isTeleportAble || L1TeleportUtils.omanAmuletTeleportAble(pc)) {
                    L1Location newLocation = pc.getLocation().randomLocation(200, true);
                    int newX = newLocation.getX();
                    int newY = newLocation.getY();
                    short mapId = (short) newLocation.getMapId();

                    if (skillId == MASS_TELEPORT) { // 매스 텔레포트
                        List<L1PcInstance> clanMember = L1World.getInstance().getVisiblePlayer(pc);
                        for (L1PcInstance member : clanMember) {
                            if (pc.getLocation().getTileLineDistance(member.getLocation()) >= 3) {
                                continue;
                            }
                            if (pc.getMapId() != member.getMapId()) {
                                continue;
                            }
                            if (member.getClanId() != pc.getClanId()) {
                                continue;
                            }
                            if (member.getClanId() == 0) {
                                continue;
                            }

                            member.sendPackets(new S_SystemMessage("혈맹원 '" + pc.getName() + "' 이(가) 매스 텔레포트를 사용 하였습니다."));

                            int newX2;
                            int newY2;
                            short mapId2 = (short) newLocation.getMapId();

                            int rndX, rndY;
                            int ckbb = RandomUtils.nextInt(2);

                            if (ckbb == 1) {
                                rndX = RandomUtils.nextInt(4) * -1;
                            } else {
                                rndX = RandomUtils.nextInt(4);
                            }

                            if (ckbb == 1) {
                                rndY = RandomUtils.nextInt(4) * -1;
                            } else {
                                rndY = RandomUtils.nextInt(4);
                            }

                            newX2 = newX + rndX;
                            newY2 = newY + rndY;
                            L1Map map = L1WorldMap.getInstance().getMap(mapId);

                            if (map.isInMap(newX2, newY2) && map.isPassable(newX2, newY2)) {
                                L1Teleport.teleport(member, newX2, newY2, mapId2, 5, true);
                            } else {
                                L1Teleport.teleport(member, newX, newY, mapId, 5, true);
                            }
                        }
                    }
                    L1Teleport.teleport(pc, newX, newY, mapId, 5, true);
                } else {
                    pc.sendPackets(new S_ChatPacket(pc, "이곳에서는 무작위 텔레포트를 사용할 수 없습니다.", L1Opcodes.S_OPCODE_MSG, 20));
                    L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), false);
                }
            }
        }
    }
}
