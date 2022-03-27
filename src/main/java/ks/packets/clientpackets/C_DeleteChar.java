package ks.packets.clientpackets;

import ks.app.config.prop.CodeConfig;
import ks.core.datatables.pc.CharacterTable;
import ks.core.network.L1Client;
import ks.model.L1Clan;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_DeleteCharOK;
import ks.packets.serverpackets.S_Notice;
import ks.system.userShop.L1UserShopCreateHandler;
import ks.system.userShop.L1UserShopHandleMessenger;

public class C_DeleteChar extends ClientBasePacket {
    public C_DeleteChar(byte[] decrypt, L1Client client) {
        super(decrypt);

        String name = readS();

        if (client == null || name == null)
            return;

        try {
            L1PcInstance pc = CharacterTable.getInstance().restoreCharacter(name);

            if (pc == null) {
                client.sendPacket(new S_Notice("잘못된 요청입니다."));
                client.disconnect();
                return;
            }

            if (pc.getLevel() < CodeConfig.DELETE_MIN_LEVEL) {
                client.sendPacket(new S_Notice("캐릭 삭제 최소 레벨은 " + CodeConfig.DELETE_MIN_LEVEL + "입니다"));
                return;
            }

            if (CodeConfig.DELETE_CHARACTER_AFTER_7DAYS) {
                client.sendPacket(new S_Notice("캐릭터 삭제가 불가능합니다. 운영자에게 문의 바랍니다."));
                return;
            }


            L1UserShopHandleMessenger shop = L1UserShopCreateHandler.getInstance().find(pc);

            if (shop != null && shop.getShopInstance() != null) {
                shop.getShopInstance().closeShop();
            }

            L1Clan clan = L1World.getInstance().getClan(pc.getClanName());

            if (clan != null) {
                clan.removeClanMember(name);
            }

            CharacterTable.getInstance().deleteCharacter(client.getAccountName(), name);

            client.sendPacket(new S_DeleteCharOK(S_DeleteCharOK.DELETE_CHAR_NOW));
        } catch (Exception e) {
            logger.error(e);
            client.disconnect();
        }
    }
}
