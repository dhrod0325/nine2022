package ks.scheduler;

import ks.app.LineageAppContext;
import ks.constants.L1PacketBoxType;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ItemStatus;
import ks.packets.serverpackets.S_OwnCharStatus;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.L1CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ItemTimerScheduler {
    private static final Logger logger = LogManager.getLogger();

    private final List<L1ItemInstance> ownerList = new CopyOnWriteArrayList<>();
    private final List<L1ItemInstance> enchantList = new CopyOnWriteArrayList<>();
    private final List<L1ItemInstance> equipList = new CopyOnWriteArrayList<>();

    public static ItemTimerScheduler getInstance() {
        return LineageAppContext.getBean(ItemTimerScheduler.class);
    }

    @Scheduled(fixedDelay = 1000)
    public void run() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        ownerCheck();

        enchantCheck();

        equipCheck();
    }

    private void equipCheck() {
        equipList.stream()
                .filter(Objects::nonNull)
                .forEach(item -> {
                    try {
                        L1PcInstance pc = item.getEquipPc();

                        if (pc == null) {
                            removeEquip(item);
                            return;
                        }

                        item.getLastStatus().updateRemainingTime();

                        if ((item.getRemainingTime() - 1) > 0) {
                            if (pc.getOnlineStatus() == 0) {
                                item.stopEquipmentTimer();
                            }

                            item.setRemainingTime(item.getRemainingTime() - 1);
                            pc.sendPackets(new S_ItemStatus(item));
                        } else {
                            pc.sendPackets(item.getLogName() + "의 사용시간이 만료 되어 소멸 되었습니다.");
                            pc.getInventory().removeItem(item, 1);
                            item.cancelEquipmentTimer();
                        }
                    } catch (Exception e) {
                        logger.error(e);
                    }
                });
    }

    private void enchantCheck() {
        enchantList.stream()
                .filter(Objects::nonNull)
                .forEach(item -> {
                    try {
                        L1PcInstance pc = item.getPc();

                        if (item.getEnchantTime() <= System.currentTimeMillis() || pc == null || pc.getOnlineStatus() == 0) {
                            item.setAcByMagic(0);
                            item.setDmgByMagic(0);
                            item.setHolyDmgByMagic(0);
                            item.setHitByMagic(0);

                            if (pc != null) {
                                if (pc.getInventory().checkItem(item.getItem().getItemId())) {
                                    if (item.isEquipped()) {
                                        if (L1CommonUtils.isArmor(item)) {
                                            pc.getAC().addAc(3);
                                            pc.sendPackets(new S_OwnCharStatus(pc));
                                        }

                                        if (item.getSkillIcon() > 0) {
                                            pc.sendPackets(new S_PacketBox(L1PacketBoxType.UNLIMITED_ICON1, item.getSkillIcon(), false));
                                            item.setSkillIcon(0);
                                        }
                                    }
                                }

                                pc.sendPackets(new S_ServerMessage(308, item.getLogName()));
                            }

                            item.setMagicRunning(false);
                            item.setPc(null);

                            removeEnchant(item);
                        }
                    } catch (Exception e) {
                        logger.error(e);
                    }
                });
    }

    private void ownerCheck() {
        ownerList.stream().filter(Objects::nonNull).forEach(item -> {
            try {
                if (item.getItemOwner() == null) {
                    removeOwner(item);
                    return;
                }

                if (item.getOwnerTime() <= System.currentTimeMillis()) {
                    removeOwner(item);
                }
            } catch (Exception e) {
                logger.error(e);
            }
        });
    }

    public void addOwner(L1ItemInstance item) {
        if (!ownerList.contains(item)) {
            ownerList.add(item);
        }
    }

    public void removeOwner(L1ItemInstance item) {
        item.setItemOwner(null);
        item.setOwnerTime(0);

        ownerList.remove(item);
    }

    public void addEnchant(L1ItemInstance item) {
        if (!enchantList.contains(item))
            enchantList.add(item);
    }

    public void removeEnchant(L1ItemInstance item) {
        enchantList.remove(item);
    }

    public void addEquip(L1ItemInstance item) {
        if (!equipList.contains(item)) {
            equipList.add(item);
        }
    }

    public void removeEquip(L1ItemInstance item) {
        equipList.remove(item);
    }
}
