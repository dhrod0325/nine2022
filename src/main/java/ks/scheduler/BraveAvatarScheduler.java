package ks.scheduler;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.constants.L1PacketBoxType;
import ks.constants.L1SkillId;
import ks.core.datatables.SkillsTable;
import ks.model.L1Character;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_OwnCharAttrDef;
import ks.packets.serverpackets.S_OwnCharStatus;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_SPMR;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component
public class BraveAvatarScheduler {
    public static final int BRAVE_AVATAR_DISTANCE = 15;

    private final List<L1PcInstance> crownList = new CopyOnWriteArrayList<>();

    private final Map<Integer, List<L1PcInstance>> buffMemberMap = new HashMap<>();

    public static BraveAvatarScheduler getInstance() {
        return LineageAppContext.getBean(BraveAvatarScheduler.class);
    }

    @Scheduled(fixedDelay = 1000)
    public void run() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        if (!CodeConfig.USE_BRAVE_AVATAR) {
            return;
        }

        repeatBraveAvatar();
    }

    public void addCrown(L1PcInstance pc) {
        if (crownList.contains(pc)) {
            return;
        }

        if (SkillsTable.getInstance().spellCheck(pc.getId(), L1SkillId.BRAVE_AVATAR)) {
            crownList.add(pc);
            buffMemberMap.put(pc.getId(), new CopyOnWriteArrayList<>());
        }
    }

    public void removeCrown(L1PcInstance crown) {
        if (crown == null || !crownList.contains(crown)) {
            return;
        }

        clearBuffMembers(buffMemberMap.get(crown.getId()));

        crownList.remove(crown);
        buffMemberMap.remove(crown.getId());
    }

    public void clearBuffMembers(List<L1PcInstance> members) {
        if (members == null)
            return;

        members.forEach(pc -> {
            removeBuff(pc);
            members.remove(pc);
        });
    }

    private void repeatBraveAvatar() {
        crownList.stream()
                .filter(Objects::nonNull)
                .forEach(crown -> {
                    List<L1PcInstance> members = buffMemberMap.get(crown.getId());

                    if (!crown.isInParty()) {
                        clearBuffMembers(members);
                        doBuff(crown, 2);
                        return;
                    }

                    if (crown.isDead()) {
                        clearBuffMembers(members);
                        return;
                    }

                    int oldMemberSize = members.size();

                    List<L1PcInstance> visibleMembers = getVisibleMembers(crown);

                    visibleMembers.stream().filter(pc -> !members.contains(pc)).forEach(members::add);

                    members.forEach(pc -> {
                        if (!visibleMembers.contains(pc)) {
                            removeBuff(pc);
                            members.remove(pc);
                        }
                    });

                    int newMemberSize = members.size();

                    if (oldMemberSize != newMemberSize) {
                        members.forEach(pc -> {
                            removeBuff(pc);
                            doBuff(pc, newMemberSize);
                        });
                    }
                });
    }

    public void doBuff(L1PcInstance pc, int partyCount) {
        if (partyCount == 2) {
            if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_BRAVE_AVATAR_1ST)) {
                pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_BRAVE_AVATAR_1ST, 0);
                startBuff(pc);
            }
        } else if (partyCount >= 3 && partyCount <= 4) {
            if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_BRAVE_AVATAR_2ND)) {
                pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_BRAVE_AVATAR_2ND, 0);
                startBuff(pc);
            }
        } else if (partyCount >= 5 && partyCount <= 8) {
            if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_BRAVE_AVATAR_3RD)) {
                pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_BRAVE_AVATAR_3RD, 0);

                startBuff(pc);
            }
        }
    }

    public void startBuff(L1PcInstance pc) {
        boolean changed = false;

        if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_BRAVE_AVATAR_1ST)) {
            pc.sendPackets(new S_PacketBox(L1PacketBoxType.UNLIMITED_ICON1, 477, true));

            statUp3(pc, 1);
            changed = true;
        } else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_BRAVE_AVATAR_2ND)) {
            pc.sendPackets(new S_PacketBox(L1PacketBoxType.UNLIMITED_ICON1, 478, true));

            statUp2(pc, 1);
            changed = true;
        } else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_BRAVE_AVATAR_3RD)) {
            pc.sendPackets(new S_PacketBox(L1PacketBoxType.UNLIMITED_ICON1, 479, true));

            statUp1(pc, 1);
            changed = true;
        }

        if (changed) {
            pc.sendPackets(new S_SPMR(pc));
            pc.sendPackets(new S_OwnCharStatus(pc));
            pc.sendPackets(new S_OwnCharAttrDef(pc));
        }
    }

    public void removeBuff(L1PcInstance pc) {
        boolean changed = false;

        if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_BRAVE_AVATAR_1ST)) {
            pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.STATUS_BRAVE_AVATAR_1ST);
            pc.sendPackets(new S_PacketBox(L1PacketBoxType.UNLIMITED_ICON1, 477, false));
            statUp3(pc, -1);
            changed = true;
        }

        if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_BRAVE_AVATAR_2ND)) {
            pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.STATUS_BRAVE_AVATAR_2ND);
            pc.sendPackets(new S_PacketBox(L1PacketBoxType.UNLIMITED_ICON1, 478, false));
            statUp2(pc, -1);
            changed = true;
        }

        if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_BRAVE_AVATAR_3RD)) {
            pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.STATUS_BRAVE_AVATAR_3RD);
            pc.sendPackets(new S_PacketBox(L1PacketBoxType.UNLIMITED_ICON1, 479, false));
            statUp1(pc, -1);
            changed = true;
        }

        if (changed) {
            pc.sendPackets(new S_SPMR(pc));
            pc.sendPackets(new S_OwnCharStatus(pc));
            pc.sendPackets(new S_OwnCharAttrDef(pc));
        }
    }

    private List<L1PcInstance> getVisibleMembers(L1PcInstance crown) {
        List<L1PcInstance> visiblePartyMembers = L1World.getInstance()
                .getVisiblePlayer(crown, BRAVE_AVATAR_DISTANCE)
                .stream()
                .filter(crown.getParty()::isMember)
                .collect(Collectors.toList());

        visiblePartyMembers.add(crown);

        return visiblePartyMembers;
    }

    private void statUp3(L1Character targetCharacter, int type) {
        targetCharacter.getResistance().addMr(8 * type);
        targetCharacter.getAbility().addAddedStr(type);
        targetCharacter.getAbility().addAddedDex(type);
        targetCharacter.getAbility().addAddedInt(type);
    }

    private void statUp2(L1Character targetCharacter, int type) {
        targetCharacter.getResistance().addMr(9 * type);
        targetCharacter.getResistance().addStun(2 * type);
        targetCharacter.getAbility().addAddedStr(type);
        targetCharacter.getAbility().addAddedDex(type);
        targetCharacter.getAbility().addAddedInt(type);
    }

    private void statUp1(L1Character targetCharacter, int type) {
        targetCharacter.getResistance().addMr(10 * type);
        targetCharacter.getResistance().addStun(3 * type);
        targetCharacter.getAbility().addAddedStr(type);
        targetCharacter.getAbility().addAddedDex(type);
        targetCharacter.getAbility().addAddedInt(type);
    }

}
