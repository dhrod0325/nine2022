package ks.packets.clientpackets;

import ks.app.config.prop.CodeConfig;
import ks.core.network.L1Client;
import ks.model.L1Clan;
import ks.model.L1ClanJoin;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Message_YN;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.L1FaceUtils;
import ks.util.common.SqlUtils;

import java.util.List;
import java.util.Map;

public class C_JoinClan extends ClientBasePacket {
    public static final int CLAN_RANK_SUB_PRINCE = 3;
    public static final int CLAN_RANK_GUARDIAN = 9;

    public C_JoinClan(byte[] data, L1Client client) {
        super(data);

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        L1PcInstance target = L1FaceUtils.faceToFace(pc);

        if (target != null) {
            joinClan(pc, target);
        }
    }

    private void joinClan(L1PcInstance player, L1PcInstance target) {
        int clanId = target.getClanId();
        String clanName = target.getClanName();

        if (clanId == 0) {
            player.sendPackets(new S_ServerMessage(90, target.getName()));
            return;
        }

        L1Clan clan = L1World.getInstance().getClan(clanName);

        if (clan == null) {
            return;
        }

        if (!target.isCrown() && !(target.getClanRank() == CLAN_RANK_GUARDIAN) && !(target.getClanRank() == CLAN_RANK_SUB_PRINCE)) {
            player.sendPackets(new S_SystemMessage(target.getName() + "는 왕자나 공주 수호기사가 아닙니다."));
            return;
        }

        if (player.getClanId() == 0 && !player.isGm()) {
            boolean Checking = clanIdSearch1(player.getClient().getAccountName(), target.getClanId());

            if (Checking) {
                player.sendPackets(new S_SystemMessage("당신의 계정내 한캐릭은 이미 다른 혈맹에 가입중입니다. "));
                player.sendPackets(new S_SystemMessage("한 계정내 캐릭들은 같은 혈맹만 가입 가능합니다. "));
                target.sendPackets(new S_SystemMessage("상대방의 계정에 캐릭터가 다른 혈맹에 가입중입니다."));
                return;
            }
        }

        List<L1PcInstance> clanMember = clan.getOnlineClanMember();

        if (clanMember.size() >= CodeConfig.CLAN_MEMBER_MAX_COUNT) {
            player.sendPackets(new S_SystemMessage("가입된 인원이 많기때문에 가입을 할수 없습니다."));
            return;
        }

        if (player.getClanId() != 0) {
            if (player.isCrown()) {
                String player_clan_name = player.getClanName();
                L1Clan player_clan = L1World.getInstance().getClan(player_clan_name);

                if (player_clan == null) {
                    return;
                }

                if (player.getId() != player_clan.getLeaderId()) {
                    player.sendPackets(new S_ServerMessage(89));

                    return;
                }

                if (player_clan.getCastleId() != 0 || player_clan.getHouseId() != 0) {
                    player.sendPackets(new S_ServerMessage(665));
                    return;
                }
            } else {
                player.sendPackets(new S_ServerMessage(89));
                return;
            }
        }

        target.setTempID(player.getId());

        if (target.getStateMap().isAutoClan()) {
            L1ClanJoin.getInstance().clanJoin(target, player);
        } else {
            target.sendPackets(new S_Message_YN(97, player.getName()));
        }
    }

    private boolean clanIdSearch1(String accountName, int clanId) {
        int count = 0;

        List<Map<String, Object>> list = SqlUtils.queryForList("SELECT ClanID FROM characters WHERE account_name = ?", accountName);

        for (Map<String, Object> o : list) {
            int cId = Integer.parseInt(o.get("ClanID") + "");

            if (cId != 0 && cId != clanId) {
                count++;
                break;
            }
        }

        return count > 0;
    }
}
