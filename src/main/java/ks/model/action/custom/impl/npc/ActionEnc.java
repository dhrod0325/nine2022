package ks.model.action.custom.impl.npc;

import ks.constants.L1ItemId;
import ks.constants.L1SkillId;
import ks.model.L1Clan;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.L1SkillUse;
import ks.packets.serverpackets.S_ServerMessage;

import static ks.constants.L1SkillId.BLESSED_ARMOR;

public class ActionEnc extends L1AbstractNpcAction {
    public ActionEnc(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (action.equalsIgnoreCase("encw")) {
            L1Clan clan = L1World.getInstance().getClan(pc.getClanName());

            if (npcId == 70508) {
                if (pc.getInventory().checkItem(L1ItemId.ADENA, 100)) {
                    pc.getInventory().consumeItem(L1ItemId.ADENA, 100);
                } else {
                    pc.sendPackets(new S_ServerMessage(189)); //아데나가 충분치 않습니다.
                    return;
                }
            } else if (npcId == 70547) {    // 켄트성
                if (clan != null && clan.getCastleId() != 1) return;
            } else if (npcId == 70816) {    // 오크성
                if (clan != null && clan.getCastleId() != 2) return;
            } else if (npcId == 70777) { // 윈다우드
                if (clan != null && clan.getCastleId() != 3) return;
            } else if (npcId == 70599) { // 기란
                if (clan != null && clan.getCastleId() != 4) return;
            } else if (npcId == 70861) { // 하이네
                if (clan != null && clan.getCastleId() != 5) return;
            } else if (npcId == 70655) { // 난성
                if (clan != null && clan.getCastleId() != 6) return;
            } else if (npcId == 70686) { // 아덴
                if (clan != null && clan.getCastleId() != 7) return;
            }
            if (pc.getWeapon() == null) {
                pc.sendPackets(new S_ServerMessage(79));
            } else {
                for (L1ItemInstance item : pc.getInventory().getItems()) {
                    if (pc.getWeapon().equals(item)) {
                        new L1SkillUse(pc, L1SkillId.ENCHANT_WEAPON, item.getId(), 0, 0, 0, L1SkillUse.TYPE_SPELL_SC).run();
                        break;
                    }
                }
            }
        } else if (action.equalsIgnoreCase("enca")) {
            L1Clan clan = L1World.getInstance().getClan(pc.getClanName());

            if (npcId == 70509) {
                if (pc.getInventory().checkItem(L1ItemId.ADENA, 100)) {
                    pc.getInventory().consumeItem(L1ItemId.ADENA, 100);
                } else {
                    pc.sendPackets(new S_ServerMessage(189)); //아데나가 충분치 않습니다.
                    return;
                }
            } else if (npcId == 70550) {    // 켄트성
                if (clan != null && clan.getCastleId() != 1) return;
            } else if (npcId == 70820) {    // 오크성
                if (clan != null && clan.getCastleId() != 2) return;
            } else if (npcId == 70780) { // 윈다우드
                if (clan != null && clan.getCastleId() != 3) return;
            } else if (npcId == 70601) { // 기란
                if (clan != null && clan.getCastleId() != 4) return;
            } else if (npcId == 70865) { // 하이네
                if (clan != null && clan.getCastleId() != 5) return;
            } else if (npcId == 70657) { // 난성
                if (clan != null && clan.getCastleId() != 6) return;
            } else if (npcId == 70692) { // 아덴
                if (clan != null && clan.getCastleId() != 7) return;
            }

            L1ItemInstance item = pc.getInventory().getItemEquipped(2, 2);

            if (item != null) {
                new L1SkillUse(pc, BLESSED_ARMOR, item.getId(), 0, 0, 0, L1SkillUse.TYPE_SPELL_SC).run();
            } else {
                pc.sendPackets(new S_ServerMessage(79));
            }
        }
    }
}
