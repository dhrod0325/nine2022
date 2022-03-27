package ks.packets.clientpackets;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.app.event.L1SelectCharacterEvent;
import ks.constants.L1PacketBoxType;
import ks.core.auth.AuthorizationUtils;
import ks.core.datatables.exp.ExpTable;
import ks.core.network.L1Client;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.*;
import ks.util.L1CommonUtils;
import org.apache.commons.lang3.StringUtils;

public class C_SelectCharacter extends ClientBasePacket {
    public C_SelectCharacter(byte[] data, L1Client client) {
        super(data);

        try {
            if (data == null) {
                return;
            }

            if (client == null) {
                return;
            }

            String charName = readS();

            L1PcInstance pc = L1PcInstance.load(charName);

            if (pc == null) {
                client.disconnectNow();
                return;
            }

            String accountName = client.getAccountName();

            if (!StringUtils.isEmpty(accountName) && !pc.getAccountName().equals(accountName)) {
                client.disconnectNow();
                return;
            }

            if (AuthorizationUtils.getInstance().isAlreadyLoginAccount(accountName)) {
                return;
            }

            logger.info("[캐릭접속] - 케릭명: {} / 계정:{} / IP: {}", charName, accountName, client.getHostname());

            client.setActiveChar(pc);

            pc.setClient(client);

            init(pc);

            pc.save();

            LineageAppContext.getCtx().publishEvent(new L1SelectCharacterEvent(pc));

        } catch (Exception e) {
            logger.error(e);
            logger.error("오류", e);
        }
    }

    public static void init(L1PcInstance pc) {
        pc.setWorld(true);
        pc.setOnlineStatus(1);

        pc.clearSkillMastery();
        pc.updateOnlineStatus();
        pc.sendPackets(new S_Unknown1(pc));

        pc.loadItems();
        pc.loadBookMarks();
        pc.loadSkills();
        pc.getBackCheck();

        L1World.getInstance().storeObject(pc);
        L1World.getInstance().addVisibleObject(pc);

        pc.sendPackets(new S_MapID(pc.getMapId(), pc.getMap().isUnderwater()));
        pc.sendPackets(new S_PacketBox(L1PacketBoxType.DODGE, 0x0000));
        pc.sendPackets(new S_OwnCharStatus(pc));
        pc.sendPackets(new S_ReturnedStat(pc, 4));
        pc.sendPackets(new S_OwnCharAttrDef(pc));
        pc.sendPackets(new S_OwnCharPack(pc));

        pc.sendPackets(new S_SPMR(pc));
        pc.sendPackets(new S_PacketBox(L1PacketBoxType.UPDATE_DG, pc.getDg()));
        pc.sendPackets(new S_Weather(L1World.getInstance().getWeather()));

        pc.sendCastleMaster();
        pc.sendVisualEffect();
        pc.getLight().turnOnOffLight();
        pc.statUpWithBuff();

        pc.initClan();
        pc.dieCheck();
        pc.bonusStatCheck();
        pc.sendPackets(new S_OwnCharStatus2(pc));

        if (pc.getReturnStat() != 0) {
            pc.returnStats();
        }

        if (CodeConfig.CHARACTER_CONFIG_IN_SERVER_SIDE) {
            pc.sendPackets(new S_CharacterConfig(pc.getId()));
        }

        pc.searchSummon();
        pc.sendPackets(new S_OwnCharStatus(pc));
        pc.checkCloneItem();

        pc.checkStatus();
        pc.checkLevelDown();
        pc.changgo();
        pc.loadExcludes();
        pc.searchArmor();
        pc.searchWeapon();
        pc.baphoSystem();
        pc.sendPackets(new S_PacketBox(L1PacketBoxType.KARMA, pc.getKarma()));

        if (pc.getHeading() < 0 || pc.getHeading() > 7) {
            pc.setHeading(0);
        }

        pc.checkMail();

        if (pc.getExp() == ExpTable.getInstance().getStartExp()) {
            //pc.sendPackets(new S_ShowCCHtml(pc.getId(), "cc_welcome"));
        }

        if (L1CommonUtils.isStandByServer()) {
            L1CommonUtils.sendStandByMsg(pc);
        }

        pc.selectCharacter();
    }
}
